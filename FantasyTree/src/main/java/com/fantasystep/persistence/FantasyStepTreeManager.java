package com.fantasystep.persistence;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import com.fantasystep.domain.DynamicDomain;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.Privilage;
import com.fantasystep.domain.Resource;
import com.fantasystep.domain.User;
import com.fantasystep.exception.ValidationFailedException;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.persistence.exception.IdNotFoundException;
import com.fantasystep.persistence.exception.InvalidCredentialsException;
import com.fantasystep.persistence.exception.InvalidSessionException;
import com.fantasystep.persistence.exception.PermissionDeniedException;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.exception.RequiredFieldMissingException;
import com.fantasystep.persistence.exception.UnauthorizedException;
import com.fantasystep.persistence.exception.UniqueViolateException;
import com.fantasystep.persistence.session.SessionManager;
import com.fantasystep.utils.ConstantUtil;
import com.fantasystep.utils.JSON2NodeUtil;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.PasswordGenerator;
import com.fantasystep.utils.PermissionUtil;
import com.fantasystep.utils.PermissionUtil.PermissionDescriptor;

@WebService(endpointInterface="com.fantasystep.persistence.TreeManagerSubclassHolder")
@XmlAccessorType(XmlAccessType.FIELD)
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class FantasyStepTreeManager implements TreeManagerSubclassHolder
{
	private TopicSession		eventSession;
	private Topic				eventTopic;
	
//	@EJB(mappedName="ejb/pmEjb")
	@EJB
	private PersistenceProxy	persistenceManager;
	private TopicPublisher		publisher;

	@EJB
	private SessionManager		sessionManager;
	private TopicConnection		topicConnection;
	@javax.annotation.Resource
	private WebServiceContext	wsContext;
	
	private static Logger logger = LoggerFactory.getLogger(FantasyStepTreeManager.class);

	public FantasyStepTreeManager()
	{
		try
		{
			InitialContext context = new InitialContext();

			TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup("java:app/EventTopicConnectionFactory");
			setTopicConnection( factory.createTopicConnection() );
			setEventSession( getTopicConnection().createTopicSession( false, Session.AUTO_ACKNOWLEDGE ) );
			setEventTopic( (Topic) context.lookup( "java:app/EventTopic" ) );
			setPublisher( getEventSession().createPublisher( getEventTopic() ) );

			getTopicConnection().start();

		} catch( NamingException e )
		{
			e.printStackTrace();
		} catch( JMSException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean activate( UUID id ) throws PersistenceException
	{
		logger.info("PersistenceProxy " + persistenceManager);
		return getPersistenceManager().activate( id );
	}

	@Override
	public UUID authenticate( String identity, String password ) throws InvalidCredentialsException
	{
		getPersistenceManager().authenticate( identity, password );
		User user = getPersistenceManager().getUserByIdentity( identity );
		// setUserId( user.getId() );

		HttpServletRequest req = (HttpServletRequest) getMessageContext().get( MessageContext.SERVLET_REQUEST );
		logger.info( String.format( "%s is authenticating from IP: %s", user.getLabel(), req.getRemoteAddr() ) );

		UUID sessionId = getSessionManager().createSession( user );
		return sessionId;
	}

	@Override
	public User authenticateWithSessionKey( UUID sessionKey ) throws InvalidSessionException
	{
		User user = getSessionManager().getUser( sessionKey );
		// setUserId( user.getId() );
		return user;
	}

	private void authorize() throws UnauthorizedException
	{
		if( isOpenHouseMode() )
			return;
		if( getUserId() == null )
			throw new UnauthorizedException();
	}

	private void authorize( Privilage privilage, Node node ) throws UnauthorizedException, PermissionDeniedException
	{
		if( isOpenHouseMode() )
			return;
		authorize();

		PermissionDescriptor permissionDescriptor = PermissionUtil.getPermissionDescriptor( getUser(), node, getPersistenceManager().getNodeCache().get( getPersistenceManager().getRootId() ) );

		boolean autorized = false;
		switch( privilage )
		{
			case DELETE:
				autorized = permissionDescriptor.hasDeletePermission();
				break;
			case BROWSE:
				autorized = permissionDescriptor.hasBrowsePermission();
				break;
			case DESTROY:
				autorized = permissionDescriptor.hasDestroyPermission();
				break;
			case INSERT:
				autorized = permissionDescriptor.hasInsertPermission();
				break;
			case SELECT:
				autorized = permissionDescriptor.hasSelectPermission();
				break;
			case UPDATE:
				autorized = permissionDescriptor.hasUpdatePermission();
				break;
		}
		if( !autorized )
			throw new PermissionDeniedException( String.format( "User %s has no permission to %s %s", getUser().getLabel(), privilage, node.getLabel() ) );
	}

	@Override
	public void deleteTree( Node node ) throws UnauthorizedException, PermissionDeniedException
	{
		authorize( Privilage.DELETE, node );
		getPersistenceManager().deleteTree( node, UUID.randomUUID(), getUser() );
		queueEvent( new NodeEvent( Action.DELETE, node ) );
	}

	@Override
	public void destroySession( UUID sessionKey ) throws InvalidSessionException
	{
		getSessionManager().destroySession( sessionKey );
	}

	@Override
	public void destroyTree( Node node ) throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		authorize( Privilage.DESTROY, node );
		getPersistenceManager().destroyTree( node, UUID.randomUUID(), getUser() );
		queueEvent( new NodeEvent( Action.DESTROY, node ) );
	}

	@Override
	public void destroyUsersOtherSessions( UUID sessionKey ) throws InvalidSessionException
	{
		getSessionManager().destroyUsersOtherSessions( sessionKey );
	}

	/**
	 * Default case : occur every day at midnight in the default time zone
	 * 
	 * */
	@Schedule(persistent = false)
	public void doWork()
	{
		logger.info( "Start to recollect sessions ... " );
		logger.info( "Current time is : " + new Date() );
		getSessionManager().cleanUp();
		logger.info( "Sessions recollection done." );
	}

	@Override
	public List<Resource> getAssignedResources( Node node ) throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		return getPersistenceManager().getAssignedResources( node );
	}

	@Override
	public List<Resource> getAssignedResourcesByClass( Node node, Class<? extends Resource> clazz ) throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		try
		{
			authorize( Privilage.SELECT, node );
		} catch( Exception e )
		{
			logger.info( "AUTHORIZE error" );
			e.printStackTrace();
		}

		try
		{
			return getPersistenceManager().getAssignedResourcesByClass( node, clazz );
		} catch( Exception e )
		{
			logger.info( "GET_ASSIGNED_RESOURCES_BY_CLASS error" );
			e.printStackTrace();
		}

		return null;
	}

	private TopicSession getEventSession()
	{
		return eventSession;
	}

	private Topic getEventTopic()
	{
		return eventTopic;
	}

	@Override
	public Node getFullNodeByID( UUID uuid ) throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		Node nodeInCache = getPersistenceManager().getNodeCache().get( uuid );
		if( uuid == null || nodeInCache == null )
			throw new IdNotFoundException( String.format( "Could not find node with id: %s", uuid ) );

		authorize( Privilage.SELECT, nodeInCache );

		return getPersistenceManager().getFullNodeByID( uuid );
	}

	private MessageContext getMessageContext()
	{
		return wsContext.getMessageContext();
	}

	@Override
	public Node getNodeByID( UUID uuid ) throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		if( uuid == null )
			throw new IdNotFoundException( String.format( "Could not find node with id: %s", uuid ) );

		Node root = getPersistenceManager().getNodeCache().get( uuid );
		authorize( Privilage.SELECT, root );

		return getPersistenceManager().getNodeByID( uuid );
	}

	@Override
	public Node getNodeByNode( Node rootNode ) throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		authorize( Privilage.SELECT, rootNode );
		return getPersistenceManager().getNodeByNode( rootNode );
	}

	@Override
	public List<? extends Node> getNodesByAttribute( String attributeName, Object value ) throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		authorize();
		for( Node n : getPersistenceManager().getNodeCache().values() )
			authorize( Privilage.SELECT, n );

		return getPersistenceManager().getNodesByAttribute( attributeName, value );
	}

	private PersistenceProxy getPersistenceManager()
	{
		return persistenceManager;
	}

	private TopicPublisher getPublisher()
	{
		return publisher;
	}

	@Override
	public List<Resource> getResources() throws UnauthorizedException, PersistenceException
	{
		return getPersistenceManager().getResources();
	}

	@Override
	public List<Resource> getResourcesByClass( Class<? extends Resource> clazz ) throws UnauthorizedException, PersistenceException
	{
		authorize();
		return getPersistenceManager().getResourcesByClass( clazz );
	}

	private HttpSession getSession() {
		HttpSession session = ((javax.servlet.http.HttpServletRequest) getMessageContext()
				.get(MessageContext.SERVLET_REQUEST)).getSession();
		return session;
	}

	private SessionManager getSessionManager()
	{
		return sessionManager;
	}

	private TopicConnection getTopicConnection()
	{
		return topicConnection;
	}

	@Override
	public User getUser() throws UnauthorizedException
	{
		if( getSession() == null )
			throw new UnauthorizedException();
		UUID userId = getUserId();
		logger.info( "Current User: " + userId );
		if( userId == null )
			throw new UnauthorizedException();

		return getPersistenceManager().getUser( userId );
	}

	private User getUserByPhoneNumber( String targetNumber ) throws InvalidCredentialsException
	{
//		List<PhoneSubscription> subscriptions = getPersistenceManager().getNodesByClass( PhoneSubscription.class );
//		for( PhoneSubscription p : subscriptions )
//			if( normalizePhoneNumber( p.getNumber() ).equals( targetNumber ) )
//			{
//				try
//				{
//					UUID userId = getPersistenceManager().getNodeByID( p.getParentId() ).getParentId();
//					Node user = getPersistenceManager().getNodeByID( userId );
//
//					if( !( user instanceof User ) )
//						throw new InvalidCredentialsException( "No user found" );
//
//					return (User) user;
//
//				} catch( PersistenceException e )
//				{
//					e.printStackTrace();
//				}
//			}

		throw new InvalidCredentialsException( "No user found" );

	}

	@SuppressWarnings("unchecked")
	private UUID getUserId()
	{
		Map<String,Object> requestHeaders = (Map<String,Object>) wsContext.getMessageContext().get( MessageContext.HTTP_REQUEST_HEADERS );
		if( requestHeaders.containsKey( ConstantUtil.SESSION_KEY ) )
			try
			{
				logger.info(ConstantUtil.SESSION_KEY  + " " + ( (List<Object>) requestHeaders.get( ConstantUtil.SESSION_KEY ) ).get( 0 ).toString());
				return getSessionManager().getUser( UUID.fromString( ( (List<Object>) requestHeaders.get( ConstantUtil.SESSION_KEY ) ).get( 0 ).toString() ) ).getId();
			} catch( InvalidSessionException e )
			{
				e.printStackTrace();
			}
		return (UUID) getSession().getAttribute( "userId" );
	}

	@Override
	public List<Resource> getUserResources() throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		return getPersistenceManager().getUserResources( getUserId() );
	}

	@Override
	public List<Resource> getUserResourcesByClass( Class<? extends Resource> clazz ) throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		List<Resource> resources = getPersistenceManager().getUserResourcesByClass( getUserId(), clazz );
		logger.info( "Got resources: " );
		for( Resource res : resources )
			logger.info( String.format( "* %s", res.getLabel() ) );
		return resources;
	}

	@Override
	public Node getUserTree() throws UnauthorizedException, PersistenceException, PermissionDeniedException
	{
		return getPersistenceManager().getUserTree( getUserId() );
	}

	@Override
	public void insertTree( List<? extends Node> nodeList ) throws UniqueViolateException, RequiredFieldMissingException, UnauthorizedException, PersistenceException, ValidationFailedException,
			PermissionDeniedException
	{
		for( Node node : nodeList )
		{
			authorize( Privilage.INSERT, node );
		}

		getPersistenceManager().insertTree( nodeList, UUID.randomUUID(), getUser() );
		for( Node node : nodeList )
			queueEvent( new NodeEvent( Action.INSERT, node ) );
	}

	@Override
	public void insertTreeNode( Node node ) throws RequiredFieldMissingException, PersistenceException, ValidationFailedException, UnauthorizedException, PermissionDeniedException, UniqueViolateException
	{
		authorize( Privilage.INSERT, node );
		getPersistenceManager().insertTreeNode( node, UUID.randomUUID(), getUser() );
		queueEvent( new NodeEvent( Action.INSERT, node ) );
	}

	@Override
	public boolean isEmailAvailable( String identity )
	{
		return getPersistenceManager().getUserByIdentity( identity ) == null;
	}

	private boolean isOpenHouseMode()
	{
		return PersistenceProxy.isOpenHouseMode();
	}

	@Override
	public boolean isSessionValid( UUID sessionKey )
	{
		return getSessionManager().isSessionValid( sessionKey );
	};

	@Override
	public void modifyTree( List<? extends Node> nodes ) throws RequiredFieldMissingException, UnauthorizedException, PersistenceException, PermissionDeniedException, ValidationFailedException, UniqueViolateException
	{
		for( Node node : nodes )
			modifyTreeNode( node );
	}

	@Override
	public void modifyTreeNode( Node node ) throws RequiredFieldMissingException, UnauthorizedException, PersistenceException, PermissionDeniedException, ValidationFailedException, UniqueViolateException
	{
		authorize( Privilage.UPDATE, node );
//		Node oldNode = getPersistenceManager().getNodeByID( node.getId() );
//		// Don't modify nodes that aren't changed.
//		if( NodeUtil.equals( oldNode, node ) )
//			return;
		getPersistenceManager().modifyTreeNode( node, UUID.randomUUID(), getUser() );
		queueEvent( new NodeEvent( Action.UPDATE, node ) );
	}

	private String normalizePhoneNumber( String number )
	{
		number = number.replaceAll( "[^0-9+]", "" );
		if( !number.startsWith( "+46" ) )
			number = "+46".concat( number.substring( 1 ) );
		return number;
	}

	@PreDestroy
	public void preDestroy()
	{
		try
		{
			getTopicConnection().close();
			getSession().invalidate();
		} catch( JMSException e )
		{
			e.printStackTrace();
		}
	}

	private void queueEvent( NodeEvent event )
	{
		if( event.getNode() instanceof DynamicDomain || (event.getNode().getClass().equals(Node.class) && event.getNode().getSerializationNode() != null) )
			return;
		try
		{
			ObjectMessage eventMessage = getEventSession().createObjectMessage();
			eventMessage.setObject( event );
			getPublisher().publish( eventMessage, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, 1800000 );
		} catch( JMSException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void resetPassword( String identifier ) throws InvalidCredentialsException
	{
		String targetNumber = normalizePhoneNumber( identifier );
		User user = getUserByPhoneNumber( targetNumber );
		String password = resetPassword( user );
		logger.info(password);
		// SMSSender.sendMessage( targetNumber, String.format( "Your new password: %s", password ) );
	}

	private String resetPassword( User user )
	{
		try
		{
			String newPassword = PasswordGenerator.getRandomPassword();
			user.setPassword( newPassword );
			getPersistenceManager().modifyTreeNode( user, UUID.randomUUID(), getUser() );
			return newPassword;

		} catch( UniqueViolateException e )
		{
			e.printStackTrace();
		} catch( RequiredFieldMissingException e )
		{
			e.printStackTrace();
		} catch( ValidationFailedException e )
		{
			e.printStackTrace();
		} catch( PersistenceException e )
		{
			e.printStackTrace();
		} catch( UnauthorizedException e )
		{
			e.printStackTrace();
		}
		return null;
	}

	private void setEventSession( TopicSession eventSession )
	{
		this.eventSession = eventSession;
	}

	private void setEventTopic( Topic eventTopic )
	{
		this.eventTopic = eventTopic;
	}

	private void setPublisher( TopicPublisher publisher )
	{
		this.publisher = publisher;
	}

	private void setTopicConnection( TopicConnection topicConnection )
	{
		this.topicConnection = topicConnection;
	}

	@Override
	public void unDeleteTree( Node node ) throws UnauthorizedException, PermissionDeniedException
	{
		authorize( Privilage.DELETE, node );
		getPersistenceManager().unDeleteTree( node, UUID.randomUUID(), getUser() );
		queueEvent( new NodeEvent( Action.UNDELETE, node ) );
	}

	@Override
	public boolean validateCredentials( String identifier, String password )
	{
		try
		{
			getPersistenceManager().authenticate( identifier, password );
		} catch( InvalidCredentialsException e )
		{
			return false;
		}
		return true;
	}

	@Override
	public void insertJSONTreeNode(String classString, String nodeString) throws UniqueViolateException, RequiredFieldMissingException,
					UnauthorizedException, PersistenceException, ValidationFailedException, PermissionDeniedException {
		this.insertTreeNode(JSON2NodeUtil.json2Node(nodeString, NodeClassUtil.getDynamicEntityClassByFullName(classString)));
	}

//	@Override
//	public Application getDefaultApplication( ApplicationType applicationType ) throws UnauthorizedException
//	{
//		throw new NotImplementedException();
//	}
}
