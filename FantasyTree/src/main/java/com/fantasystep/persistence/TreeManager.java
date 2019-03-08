package com.fantasystep.persistence;

import java.util.List;
import java.util.UUID;

import javax.jws.WebMethod;
import javax.xml.namespace.QName;

import com.fantasystep.domain.Node;
import com.fantasystep.domain.Resource;
import com.fantasystep.domain.User;
import com.fantasystep.exception.ValidationFailedException;
import com.fantasystep.persistence.exception.InvalidCredentialsException;
import com.fantasystep.persistence.exception.InvalidSessionException;
import com.fantasystep.persistence.exception.PermissionDeniedException;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.exception.RequiredFieldMissingException;
import com.fantasystep.persistence.exception.UnauthorizedException;
import com.fantasystep.persistence.exception.UniqueViolateException;

public interface TreeManager
{
	public static final QName	SERVICE_QUALIFIED_NAME	= new QName( "http://persistence.fantasystep/", "TreeManagerService" );

	public static final String	TREEMANAGER_PROPERTY	= "TreeManager";

	@WebMethod(action = "activate")
	public abstract boolean activate( UUID id ) throws PersistenceException;

	@WebMethod(action = "authenticate")
	public abstract UUID authenticate( String identifier, String password ) throws InvalidCredentialsException;

	@WebMethod(action = "authenticateWithSessionKey")
	public abstract User authenticateWithSessionKey( UUID sessionKey ) throws InvalidSessionException;

	@WebMethod(action = "authenticateWithSessionKey")
	public abstract void deleteTree( Node rootNode ) throws UnauthorizedException, PermissionDeniedException;

	@WebMethod(action = "destroySession")
	public abstract void destroySession( UUID sessionKey ) throws InvalidSessionException;

	@WebMethod(action = "destroyTree")
	public abstract void destroyTree( Node rootNode ) throws UnauthorizedException, PersistenceException, PermissionDeniedException;

	@WebMethod(action = "destroyUsersOtherSessions")
	public abstract void destroyUsersOtherSessions( UUID sessionKey ) throws InvalidSessionException;

	/**
	 * Get resources assigned to specified node
	 * 
	 * @throws PermissionDeniedException
	 */
	@WebMethod(action = "getAssignedResources")
	public abstract List<Resource> getAssignedResources( Node node ) throws UnauthorizedException, PersistenceException, PermissionDeniedException;

	@WebMethod(action = "getAssignedResourcesByClass")
	public abstract List<Resource> getAssignedResourcesByClass( Node node, Class<? extends Resource> clazz ) throws UnauthorizedException, PersistenceException, PermissionDeniedException;

//	@WebMethod(action = "getDefaultApplication")
//	public abstract Application getDefaultApplication( ApplicationType applicationType ) throws UnauthorizedException;

	/**
	 * Get tree node from storage rather than cache by UUID including SubNodes.
	 * 
	 * @throws UnauthorizedException
	 * @throws PersistenceException
	 * @throws PermissionDeniedException
	 */
	@WebMethod(action = "getFullNodeByID")
	public abstract Node getFullNodeByID( UUID uuid ) throws UnauthorizedException, PersistenceException, PermissionDeniedException;

	/**
	 * Get node from cache by UUID.
	 * 
	 * @throws PermissionDeniedException
	 */
	@WebMethod(action = "getNodeByID")
	public abstract Node getNodeByID( UUID uuid ) throws UnauthorizedException, PersistenceException, PermissionDeniedException;

	/**
	 * Get ndoe from cache by node.
	 * 
	 * @throws PermissionDeniedException
	 */
	@WebMethod(action = "getNodeByNode")
	public abstract Node getNodeByNode( Node rootNode ) throws UnauthorizedException, PersistenceException, PermissionDeniedException;

	/**
	 * Get tree from cache by Attribute.
	 * 
	 * @throws UnauthorizedException
	 * @throws PersistenceException
	 * @throws PermissionDeniedException
	 */
	@WebMethod(action = "getNodesByAttribute")
	public abstract List<? extends Node> getNodesByAttribute( String attributeName, Object value ) throws UnauthorizedException, PersistenceException, PermissionDeniedException;

	/** Get all resources */
	@WebMethod(action = "getResources")
	public abstract List<Resource> getResources() throws UnauthorizedException, PersistenceException;

	/** Get resources of specified class */
	@WebMethod(action = "getResourcesByClass")
	public abstract List<Resource> getResourcesByClass( Class<? extends Resource> clazz ) throws UnauthorizedException, PersistenceException;

	@WebMethod(action = "getUser")
	public abstract User getUser() throws UnauthorizedException;

	@WebMethod(action = "getUserResources")
	public abstract List<Resource> getUserResources() throws UnauthorizedException, PersistenceException, PermissionDeniedException;

	@WebMethod(action = "getUserResourcesByClass")
	public abstract List<Resource> getUserResourcesByClass( Class<? extends Resource> clazz ) throws UnauthorizedException, PersistenceException, PermissionDeniedException;

	/**
	 * Get tree from cache.
	 * 
	 * @throws UnauthorizedException
	 * @throws PersistenceException
	 * @throws PermissionDeniedException
	 */
	@WebMethod(action = "getUserTree")
	public abstract Node getUserTree() throws UnauthorizedException, PersistenceException, PermissionDeniedException;

	@WebMethod(action = "insertTree")
	public abstract void insertTree( List<? extends Node> nodeList ) throws UniqueViolateException, RequiredFieldMissingException, UnauthorizedException, PersistenceException,
			ValidationFailedException, PermissionDeniedException;

	@WebMethod(action = "insertTreeNode")
	public abstract void insertTreeNode( Node node ) throws UniqueViolateException, RequiredFieldMissingException, UnauthorizedException, PersistenceException, ValidationFailedException,
			PermissionDeniedException;
	
	@WebMethod(action = "insertJSONTreeNode")
	public abstract void insertJSONTreeNode( String classString, String nodeString ) throws UniqueViolateException, RequiredFieldMissingException, UnauthorizedException, PersistenceException, ValidationFailedException,
			PermissionDeniedException;

	@WebMethod(action = "isEmailAvailable")
	public abstract boolean isEmailAvailable( String identity );

	@WebMethod(action = "isSessionValid")
	public abstract boolean isSessionValid( UUID sessionKey );

	@WebMethod(action = "modifyTree")
	public abstract void modifyTree( List<? extends Node> nodes ) throws UniqueViolateException, RequiredFieldMissingException, UnauthorizedException, PersistenceException, PermissionDeniedException,
			ValidationFailedException;

	@WebMethod(action = "modifyTreeNode")
	public abstract void modifyTreeNode( Node node ) throws UniqueViolateException, RequiredFieldMissingException, UnauthorizedException, PersistenceException, PermissionDeniedException,
			ValidationFailedException;

	@WebMethod(action = "resetPassword")
	public abstract void resetPassword( String identifier ) throws InvalidCredentialsException;

	@WebMethod(action = "unDeleteTree")
	public abstract void unDeleteTree( Node rootNode ) throws UnauthorizedException, PermissionDeniedException;

	@WebMethod(action = "validateCredentials")
	public abstract boolean validateCredentials( String identifier, String password );

}