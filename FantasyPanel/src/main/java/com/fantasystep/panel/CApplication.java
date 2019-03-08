package com.fantasystep.panel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.annotation.ValueOptions;
import com.fantasystep.component.panel.AbstractCApplication;
import com.fantasystep.component.utils.EnvironmentUtil;
import com.fantasystep.domain.Entity;
import com.fantasystep.domain.EntityGroup;
import com.fantasystep.domain.Node;
import com.fantasystep.persistence.TreeManagerSubclassHolder;
import com.fantasystep.utils.JCompiler;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;

@PreserveOnRefresh
@Push(value = PushMode.MANUAL, transport = Transport.STREAMING)
@Title("FantasyStep")
@JavaScript("https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js")
@Theme("mytheme")
public class CApplication extends AbstractCApplication {
	
	private static final long serialVersionUID = 2780076547913870823L;

	private static Logger logger = LoggerFactory.getLogger(CApplication.class);
	
	private EventHandler eventHandler = null;
	private SessionHandler sessionHandler = null;
	private UIHandler uiHandler = null;
	private TreeManagerSubclassHolder treeManager = null;
	private UUID sessionKey = null;
	private boolean isDeveloperMode = false;
	private final static String DEBUG_MODE = "mode";

	public EventHandler getEventHandler() {
		if (eventHandler == null)
			eventHandler = new EventHandler();
		return eventHandler;
	}

	public SessionHandler getSessionHandler() {
		if (sessionHandler == null)
			sessionHandler = new SessionHandler(this);
		return sessionHandler;
	}

	public UIHandler getUiHandler() {
		if (uiHandler == null)
			uiHandler = new UIHandler();
		return uiHandler;
	}

	@Override
	protected void init(com.vaadin.server.VaadinRequest request) {
		super.init(request);
		VaadinSession.getCurrent().getSession().setMaxInactiveInterval(-1);
		String sessionKey = request.getParameter(EnvironmentUtil.SESSION_KEY);
		if (sessionKey != null)
			this.sessionKey = UUID.fromString(sessionKey);

		if (request.getParameter(DEBUG_MODE) != null)
			this.isDeveloperMode = (request.getParameter(DEBUG_MODE) != null && request
					.getParameter(DEBUG_MODE).equals("developer"));
	};

	@Override
	protected String getLocalizationId() {
		return "fantasypanel";
	}

	@Override
	protected void initSession() {
		if (sessionKey != null)
			getSessionHandler().handlerRequest(sessionKey);
		else
			getUiHandler().buildLoginLayout();
		getSessionHandler().initEventListener();
		initConverter();
	}

	private void initConverter() {
		class UUIDConverter implements Converter<String, UUID> {
			private static final long serialVersionUID = -5225142774712746095L;

			@Override
			public String convertToPresentation(UUID value,
					Class<? extends String> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				if(value == null)
					return null;
				else return value.toString();
			}

			@Override
			public Class<UUID> getModelType() {
				return UUID.class;
			}

			@Override
			public Class<String> getPresentationType() {
				return String.class;
			}

			@Override
			public UUID convertToModel(String value,
					Class<? extends UUID> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				if(value == null || value.isEmpty())
					return UUID.randomUUID();
				else return UUID.fromString(value);
			}
		}
		@SuppressWarnings("rawtypes")
		class EnumConverter implements Converter<String, Enum> {
			private static final long serialVersionUID = -7819591695561219416L;

			@Override
			public String convertToPresentation(Enum value,
					Class<? extends String> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				return value == null ? null : value.toString();
			}

			@Override
			public Class<Enum> getModelType() {
				return Enum.class;
			}

			@Override
			public Class<String> getPresentationType() {
				return String.class;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Enum convertToModel(String value,
					Class<? extends Enum> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				if(ValueOptions.class.isAssignableFrom(targetType)) {
					for(ValueOptionEntry entry : ((ValueOptions)targetType.getEnumConstants()[0]).getValues()) {
						if(entry.getValue().toString().equals(value))
							return (Enum)entry.getValue();
					}
				}
				if(value == null)
					return null;
				else return Enum.valueOf(targetType, value);
			}
		}
		
		class MyConverterFactory extends DefaultConverterFactory {
			private static final long serialVersionUID = 8417113314826736235L;

			@SuppressWarnings("unchecked")
			@Override
			public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(
					Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
				if (String.class == presentationType && UUID.class == modelType)
					return (Converter<PRESENTATION, MODEL>) new UUIDConverter();
				if (String.class == presentationType && Enum.class.isAssignableFrom(modelType))
					return (Converter<PRESENTATION, MODEL>) new EnumConverter();
				return super.createConverter(presentationType, modelType);
			}
		}

		VaadinSession.getCurrent()
				.setConverterFactory(new MyConverterFactory());
	}
	
	@Override
	protected void closeSession() {
		getSessionHandler().closeEventListener();
		sessionHandler = null;
		uiHandler = null;
		eventHandler = null;
	}

	public boolean isDeveloperMode() {
		return isDeveloperMode;
	}

	public TreeManagerSubclassHolder getTreeManager() {
		if (treeManager == null) {
			logger.info("Treeeeee : " + sessionKey);
			if (sessionKey != null)
				TreeHandler.getDescriptor()
						.setSessionKey(sessionKey.toString());
			treeManager = TreeHandler.getDescriptor().getProxy();
		}
		return treeManager;
	}
	
	@SuppressWarnings("unchecked")
	public void refreshDynamicClassesMap() {
		Map<String, Class<? extends Node>> map = new HashMap<String, Class<? extends Node>>();
		
		Map<String, String> codes = new HashMap<String, String>();
		Node root = TreeHandler.getRootNode();
		for(Node node : NodeUtil.getChildren(root, root))
			if(node instanceof EntityGroup)
				for(Node child : node.getChildren()) {
					if(child instanceof Entity) {
						Entity entity = (Entity) child;
						codes.put(entity.getFullName(), entity.getSourceCode());
					}
				}
		for(Entry<String, String> entry : codes.entrySet()) {
			Class<?> clazz = JCompiler.getInstance().registerClass(entry.getKey(), codes);
			DomainClass dc = clazz.getAnnotation(DomainClass.class);
			if(dc == null)
				continue;
			map.put(entry.getKey(), (Class<? extends Node>)clazz);
		}
		NodeClassUtil.setupDynamicEntityClass(map);
	}
}
