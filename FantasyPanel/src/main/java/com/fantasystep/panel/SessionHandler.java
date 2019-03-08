package com.fantasystep.panel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.component.panel.ConcreteNodeEvent;
import com.fantasystep.domain.DynamicDomain;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.IconHolder;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.VolatileFile;
import com.fantasystep.persistence.exception.InvalidSessionException;
import com.fantasystep.persistence.exception.PermissionDeniedException;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.exception.UnauthorizedException;
import com.fantasystep.util.CNodeUtil;
import com.fantasystep.utils.Environment;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.Option;
import com.fantasystep.utils.Option.InvalidOptionFormatException;
import com.fantasystep.utils.Option.StringOption;
import com.fantasystep.utils.Option.URLOption;
import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;

public class SessionHandler {
	public static final String LANGUAGE = "language";

	private static Logger logger = LoggerFactory
			.getLogger(SessionHandler.class);

	static {
		try {
			Option.setConfigFileName("/etc/fantasystep/ui-sessionhandler.conf");

			URLOption ICONSERVER_ADDRESS = new URLOption("iconserver.address",
					"http://localhost:8080/fantasyiconserver/IconServer", true,
					"IconServer address");
			StringOption VOLATILE_FILE_STORAGE_LOCATION = new StringOption(
					"file.storage.location", "/tmp", true,
					"Storage location for VolatileFile");

			Option.load();

			Environment.addProperty(VolatileFile.STORAGE_LOCATION_PROPERTY,
					VOLATILE_FILE_STORAGE_LOCATION.value());
			Environment.addProperty(IconHolder.ICONSERVER_PROPERTY,
					ICONSERVER_ADDRESS.value().toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidOptionFormatException e) {
			e.printStackTrace();
		}
	}

	private CApplication application;

	private EventListener eventListener;

	public SessionHandler(CApplication application) {
		this.application = application;
	}

	public void closeEventListener() {
		if (eventListener != null) {
			eventListener.destroy();
			eventListener = null;
		}
		logger.info("Bye Bye");
	}

	public void handlerRequest(UUID sessionKey) {
		if (sessionKey != null) {
			try {
				logger.info(String.format("Login with session key: %s ",
						sessionKey));
				TreeHandler.get().authenticateWithSessionKey(sessionKey);
				TreeHandler.updateTreeFromStorage();
				application.getUiHandler().getLogout().setVisible(false);
				application.getUiHandler().initStyleWindow();
				application.getUiHandler().showMainLayout();
			} catch (InvalidSessionException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract class EventListener implements MessageListener {
		private TopicSession eventSession;
		private Topic eventTopic;
		private TopicConnection topicConnection;

		public EventListener() {
			try {

				InitialContext jndi = new InitialContext();
				TopicConnectionFactory factory = (TopicConnectionFactory) jndi
						.lookup("java:app/EventTopicConnectionFactory");

				setTopicConnection(factory.createTopicConnection());
				setEventSession(getTopicConnection().createTopicSession(false,
						Session.AUTO_ACKNOWLEDGE));
				setEventTopic((Topic) jndi.lookup("java:app/EventTopic"));

				TopicSubscriber subscriber = getEventSession()
						.createSubscriber(getEventTopic());
				subscriber.setMessageListener(this);

				getTopicConnection().start();

			} catch (NamingException e) {
				e.printStackTrace();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

		public void destroy() {
			try {
				getTopicConnection().close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

		private TopicSession getEventSession() {
			return eventSession;
		}

		private Topic getEventTopic() {
			return eventTopic;
		}

		private TopicConnection getTopicConnection() {
			return topicConnection;
		}

		public abstract void onEvent(NodeEvent nodeEvent);

		@Override
		public void onMessage(Message message) {
			try {
				if (message instanceof ObjectMessage) {
					ObjectMessage eventMessage = (ObjectMessage) message;
					onEvent((NodeEvent) eventMessage.getObject());
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

		private void setEventSession(TopicSession eventSession) {
			this.eventSession = eventSession;
		}

		private void setEventTopic(Topic eventTopic) {
			this.eventTopic = eventTopic;
		}

		private void setTopicConnection(TopicConnection topicConnection) {
			this.topicConnection = topicConnection;
		}

	}

	public void initEventListener() {
		if (this.eventListener == null) {
			this.eventListener = new EventListener() {
				@Override
				public void onEvent(final NodeEvent nodeEvent) {
					logger.info(String.format("Got an event: %s, %s",
							nodeEvent.getAction(), nodeEvent.getNode()));
					logger.info(String.format("Application id: %s, %s",
							SessionHandler.this.application.hashCode(),
							SessionHandler.this.application.isDeveloperMode()));

					if (VaadinSession.getCurrent() == null || VaadinSession.getCurrent().getSession() == null)
						return;
					synchronized (SessionHandler.this.application) {
						try {
							SessionHandler.this.application
									.access(new Runnable() {
										@Override
										public void run() {
											Node newNode = nodeEvent.getNode();
											if (newNode.getClass().equals(
													Node.class)
													&& newNode
															.getSerializationNode() != null)
												newNode = NodeClassUtil
														.getDeserializationNode(newNode);

											Node root = (Node) VaadinSession
													.getCurrent()
													.getSession()
													.getAttribute(
															Node.ROOT_NODE_PROPERTY);

											Node targetNode = (Node) VaadinSession
													.getCurrent()
													.getSession()
													.getAttribute(
															Node.TARGET_NODE_PROPERTY);
											System.out.println(newNode.getClass() + "++++++++++++++++++++++++++++");
											if (DynamicDomain.class
													.isAssignableFrom(newNode
															.getClass()))
												try {
													targetNode = TreeHandler
															.get()
															.getFullNodeByID(
																	targetNode
																			.getId());
													List<Node> list = new ArrayList<Node>();
													for (Node n : targetNode
															.getChildren())
														if (n.getClass()
																.equals(Node.class)) {
															Node nn = NodeClassUtil
																	.getDeserializationNode(n);
															if (nn.getClass()
																	.equals(newNode
																			.getClass()))
																list.add(nn);
														}
													targetNode
															.setChildren(list);
												} catch (UnauthorizedException e) {
													e.printStackTrace();
												} catch (PersistenceException e) {
													e.printStackTrace();
												} catch (PermissionDeniedException e) {
													e.printStackTrace();
												}
											else {
												CNodeUtil.updateTree(root,
														newNode,
														nodeEvent.getAction());
												targetNode = NodeUtil.getNode(
														targetNode.getId(),
														root);
											}
											if (targetNode == null)
												SessionHandler.this.application
														.getEventHandler()
														.notify(new ConcreteNodeEvent(
																nodeEvent,
																root,
																NodeUtil.getNode(
																		newNode.getParentId(),
																		root)));
											else
												SessionHandler.this.application
														.getEventHandler()
														.notify(new ConcreteNodeEvent(
																nodeEvent,
																root,
																targetNode));
											if (CApplication.class.isAnnotationPresent(Push.class) && 
													CApplication.class.getAnnotation(Push.class).value() != PushMode.DISABLED)
												SessionHandler.this.application.push();
										}
									});
						} catch (IllegalStateException e) {
						}
					}
				}
			};
		}
	}
}
