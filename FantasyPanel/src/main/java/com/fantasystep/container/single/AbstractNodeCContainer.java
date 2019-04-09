package com.fantasystep.container.single;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.annotation.ValueOptions;
import com.fantasystep.component.field.common.CalendarCField;
import com.fantasystep.component.field.common.ComboboxCField;
import com.fantasystep.component.field.common.FileUploaderCField;
import com.fantasystep.component.field.common.OptionGroupCField;
import com.fantasystep.component.field.common.PasswordCField;
import com.fantasystep.component.field.common.RadioButtonCField;
import com.fantasystep.component.field.common.TextAreaCField;
import com.fantasystep.component.field.common.TextFieldCField;
import com.fantasystep.component.field.custom.DateOfBirthCField;
import com.fantasystep.component.field.custom.DocumentedCField;
import com.fantasystep.component.field.custom.DynamicNodeRelationshipCField;
import com.fantasystep.component.field.custom.JsonTextAreaCField;
import com.fantasystep.component.field.custom.LabelCField;
import com.fantasystep.component.field.custom.NodeMembersCField;
import com.fantasystep.component.field.custom.NodeTreeComboboxCField;
import com.fantasystep.component.field.custom.TristateCField;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.domain.DynamicDomain;
import com.fantasystep.domain.Entity;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.helper.PropertyGroups;
import com.fantasystep.helper.Validation;
import com.fantasystep.helper.VolatileFile;
import com.fantasystep.panel.CApplication;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.persistence.PersistenceInterceptor;
import com.fantasystep.persistence.exception.PermissionDeniedException;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.exception.UnauthorizedException;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

@SuppressWarnings("deprecation")
public abstract class AbstractNodeCContainer extends AbstractCContainer implements ClickListener
{
	private static final long serialVersionUID = 6524908604383053876L;
	
	protected Form								form;
	private Map<String,PropertyGroups>			groupLabelProperties	= null;
	protected List<com.fantasystep.component.panel.Listener>	listeners				= new ArrayList<com.fantasystep.component.panel.Listener>();
	protected Node								node;

	protected List<String>						uiFields				= null;
	private String								uniqueField				= null;

	public AbstractNodeCContainer( Node node, NodeEvent.Action action, FormMode mode )
	{
		super( action, mode );
		this.node = node;
		initUiFieldsAndRank();

		addHeaderTitle( String.format( "%s %s", getCaptionPrefix(), LabelUtil.getDomainLabel( node.getClass() ).toLowerCase() ), CSSUtil.BLACK_FEATURED_TITLE );
	}

	@Override
	public void buttonClick( ClickEvent event )
	{
		Button source = event.getButton();

		if ( source == save )
		{
			Throwable t = null;
			if(!getForm().isValid())
			{
				Notification.show( LocalizationHandler.get( LabelUtil.LABEL_VALIDATE_ERROR ), Type.WARNING_MESSAGE );
				return;
			}
			setReadOnly( false );
			getForm().commit();

			closeWindow();
			( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
			try
			{
				if( action == Action.INSERT ) {
					if(!DynamicDomain.class.isAssignableFrom(node.getClass()))
						TreeHandler.get().insertTreeNode( node );
					else
						TreeHandler.get().insertTreeNode( NodeClassUtil.getSerializationNode(node) );
				} else if( action == Action.UPDATE ) {
					if(!DynamicDomain.class.isAssignableFrom(node.getClass())) {
						NodeUtil.removeDynamicChildren(node);
						TreeHandler.get().modifyTreeNode( node );
					} else
						TreeHandler.get().modifyTreeNode( NodeClassUtil.getSerializationNode(node) );
				} 
				if(!DynamicDomain.class.isAssignableFrom(node.getClass())) {
					/** Here should be only one, but in case for some uncertain reason. */
					List<Node> list = getPreviousPrimaryNodes();
					for( Node n : list )
						TreeHandler.get().modifyTreeNode( n );
				}
				if(node instanceof Entity)
					((CApplication)UI.getCurrent()).refreshDynamicClassesMap();
			} catch( Exception e )
			{
				t = e;
				TreeHandler.handleTreeException( e );
			} finally
			{
				if( t != null )
					TreeHandler.updateTreeFromStorage();
			}
		} else if( source == edit )
		{
			setReadOnly( false );
		} else if( source == cancel )
		{
			closeWindow();
			( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Field createFieldWithPropertyId( Node node, Object propertyId )
	{
		Field field = null;
		FieldAttributeAccessor attributes = AnnotationsParser.getAttributes( node.getClass(), propertyId.toString());

		switch( attributes.getControlType() )
		{
			case TEXTBOX:
				field = new TextFieldCField( attributes ).getField();

				if( attributes.getUnique() && attributes.getValidate() == Validation.EMAIL )
				{
					if( action == Action.INSERT )
					{
						field.addValidator( new AbstractValidator( attributes.getValidationErrorMessage() )
						{
							private static final long serialVersionUID = 8161014948901930209L;

							@Override
							protected boolean isValidValue(Object value) {
								return TreeHandler.get().isEmailAvailable( value.toString() );
							}

							@Override
							public Class<?> getType() {
								return null;
							}
						} );
					} else if( action == Action.UPDATE )
					{
						final Object oldValue = NodeUtil.getAttribute( node, propertyId.toString() );
						field.addValidator( new AbstractValidator( attributes.getValidationErrorMessage() )
						{
							private static final long serialVersionUID = 450333025237409384L;

							@Override
							public boolean isValidValue( Object value )
							{
								if( value.equals( oldValue ) )
									return true;
								else return TreeHandler.get().isEmailAvailable( value.toString() );
							}
							
							@Override
							public Class<?> getType() {
								return null;
							}
						} );
					}
				}
				break;
			case TEXTAREA:
				field = new TextAreaCField( attributes ).getField();
				if(node instanceof Entity && propertyId.equals("sourceCode"))
					field.setHeight("800px");
				break;
			case PASSWORD:
				field = new PasswordCField( attributes ).getField();
				break;
			case CHECKBOX:
				if( attributes.getValueOptions() == ValueOptions.class )
					field = new CheckBox( LocalizationHandler.get( attributes.getLabel() ) );
				else field = new OptionGroupCField( attributes ).getField();
				break;
			case TRISTATE:
				if( attributes.getValueOptions() == ValueOptions.class )
					field = new CheckBox( LocalizationHandler.get( attributes.getLabel() ) );
				else field = new TristateCField( attributes ).getField();
				break;
			case DROPDOWN:
				field = new ComboboxCField( attributes ).getField();
				break;
			case TREEDROPDOWN:
				List<Node> tree = NodeUtil.getChildren(TreeHandler.getRootNodeByApplication(), TreeHandler.getRootNodeByApplication(), true);
				Collections.reverse( tree );
//				if( node.getClass().equals( User.class ) )
//					tree.add( node );
				field = new NodeTreeComboboxCField( attributes, tree ).getField();
				break;
			case RADIO:
				field = new RadioButtonCField( attributes ).getField();
				break;
			case CALENDAR:
				field = new CalendarCField( attributes ).getField();
				break;
			case DATEOFBIRTH:
				field = new DateOfBirthCField( attributes ).getField();
				BeanItem<Node> item = new BeanItem<Node>( node );
				field.setValue( item.getItemProperty( propertyId ).getValue() );
				break;
			case FILEUPLOAD:
				VolatileFile volatileFile = getVolatileFile( node );
				field = new FileUploaderCField( attributes, volatileFile ).getField();
				break;
			case MEMBERLIST:
				Node root = TreeHandler.getRootNodeByApplication();
				Node target = TreeHandler.getTargetNodeByApplication();
				List<Node> list = NodeUtil.getMembers( (MemberHolder) node, root );
				field = new NodeMembersCField( root, target, attributes, list ).getField();

				if( this.action == Action.INSERT )
					field.setEnabled( false );
				break;
			case DYNAMICRELATION:
				Class<? extends Node> type = NodeClassUtil.getDynamicEntityClassByFullName(attributes.getDynamicInfo()[0]);
				List<Node> allNodes = new ArrayList<Node>();
				try {
					Node fullNodeByID = TreeHandler.get().getFullNodeByID(UUID.fromString(attributes.getDynamicInfo()[1]));
					for(Node n : fullNodeByID.getChildren())
						if(n.getClass().equals(Node.class)) {
							Node nn = NodeClassUtil.getDeserializationNode(n);
							if(nn.getClass().equals(type))
								allNodes.add(nn);
						}
				} catch (UnauthorizedException e) {
					e.printStackTrace();
				} catch (PersistenceException e) {
					e.printStackTrace();
				} catch (PermissionDeniedException e) {
					e.printStackTrace();
				}
				field = new DynamicNodeRelationshipCField(attributes, allNodes, type).getField();
				item = new BeanItem<Node>( node );
				field.setValue( item.getItemProperty( propertyId ).getValue() );
				break;
			case JSON_TEXTAREA:
				item = new BeanItem<Node>( node );
				field = new JsonTextAreaCField( attributes, item.getItemProperty( propertyId ).getType() ).getField();
				field.setValue( item.getItemProperty( propertyId ).getValue() );
				break;
			case LABEL:
				field = new LabelCField( attributes ).getField();
				BeanItem<Node> bean = new BeanItem<Node>( node );
				Object value = bean.getItemProperty( propertyId ).getValue();
				field.setValue( value );
				break;
			default:
				break;
		}

		// if( attributes.getUnique() && action.equals( Action.UPDATE ) )
		// field.setEnabled( false );

		if( attributes.getIsSiblingUnique() )
			this.uniqueField = propertyId.toString();

		if( com.fantasystep.component.panel.Listener.class.isAssignableFrom( field.getClass() ) )
			this.listeners.add( (com.fantasystep.component.panel.Listener) field );

		java.lang.reflect.Field propertyField = NodeClassUtil.getField( node.getClass(), propertyId.toString() );
		if( AnnotationsParser.isDocumented( NodeClassUtil.getField( node.getClass(), propertyId.toString() ) ) )
			field = new DocumentedCField( attributes, field, String.format( "%s.%s", propertyField.getDeclaringClass().getCanonicalName(), propertyField.getName() ) ).getField();
		
		return field;

	}

	private String getCaptionPrefix()
	{
		return ( action == NodeEvent.Action.INSERT ? LocalizationHandler.get( LabelUtil.LABEL_CREATE_NEW ) : LocalizationHandler.get( LabelUtil.LABEL_UPDATE ) );
	}

	public final Form getForm()
	{
		if( form == null )
		{
			listeners.clear();
			form = new Form();
			initForm();
			initDisplay();
		}
		return form;
	}

	public Node getNode()
	{
		return node;
	}

	private List<Node> getPreviousPrimaryNodes()
	{
		return this.getPreviousPrimaryNodesByNodeList( NodeUtil.getNode( this.node.getParentId(), TreeHandler.getRootNode() ).getChildren( this.node.getClass() ) );
	}

	protected List<Node> getPreviousPrimaryNodesByNodeList( List<Node> nodeList )
	{
		List<Node> list = new ArrayList<Node>();
		if( uniqueField != null )
		{
			java.lang.reflect.Field field = NodeClassUtil.getField( this.node.getClass(), uniqueField );
			try
			{
				Boolean value = field.getBoolean( this.node );
				if( value )
				{
					for( Node child : nodeList )
					{
						if( field.getBoolean( child ) && !child.equals( this.node ) )
						{
							list.add( child );
							field.set( child, false );
						}
					}
				}
			} catch( IllegalArgumentException e )
			{
				e.printStackTrace();
			} catch( IllegalAccessException e )
			{
				e.printStackTrace();
			}
		}

		return list;
	}

	public VolatileFile getVolatileFile( Node node )
	{
		/** getVolatileFile from Ad */
		// if(node instanceof Ad)
		// {
		//
		// }

		return null;
	}

	public void handleVolatile()
	{
		Object obj = getVolatileFile( this.node );
		if( obj instanceof PersistenceInterceptor && action == Action.INSERT )
			( (PersistenceInterceptor) obj ).initialize();
	}

	private void initFooter()
	{
		if( withSaveAndCancelButtons )
		{
			HorizontalLayout footer = new HorizontalLayout();
			footer.setSpacing( true );
			footer.addComponent( save );
			footer.addComponent( cancel );
			footer.addComponent( edit );
			footer.setVisible( false );
			getForm().setFooter( footer );
		}
	}

	@Override
	protected void initForm()
	{
		if (withSaveAndCancelButtons) {
			getForm().setCaption(
					String.format("%s %s", getCaptionPrefix(),
							LabelUtil.getDomainLabel(getNode().getClass())));
			getHeader().setVisible(false);
		}
		getForm().setFormFieldFactory( new DefaultFieldFactory()
		{
			private static final long serialVersionUID = -2957515033455436745L;

			@Override
			public Field<?> createField( Item item, Object propertyId, Component uiContext )
			{
				if( AbstractNodeCContainer.this.isGroupLabelField( propertyId.toString() ) )
				{
					String title = LocalizationHandler.get( AbstractNodeCContainer.this.groupLabelProperties.get( propertyId ).getLabel() );
					getForm().getLayout().addComponent( new Label( CSSUtil.wrapStyle( title, CSSUtil.GRAY_NORMAL_TITLE ), ContentMode.HTML ) );
				}
				Field<?> field = createFieldWithPropertyId( node, propertyId );
				return field;
			}
		} );
		getForm().setSizeFull();
		
		this.initFooter();
		this.initItemDataSource( new BeanItem<Object>( getNode() ) );
	}

	protected void initItemDataSource( Item newDataSource )
	{
		getForm().setItemDataSource( newDataSource, uiFields );
		setReadOnly( false );
		getForm().getFooter().setVisible( true );
	}

	private void initUiFieldsAndRank()
	{
		groupLabelProperties = new HashMap<String,PropertyGroups>();
		Map<String,PropertyGroups> groupRank = new TreeMap<String,PropertyGroups>( new Comparator<String>()
		{
			@Override
			public int compare( String arg0, String arg1 )
			{
				int order0 = AnnotationsParser.getAttributes( node.getClass(), arg0 ).getOrder();
				int order1 = AnnotationsParser.getAttributes( node.getClass(), arg1 ).getOrder();
				if( order0 > order1 )
					return 1;
				else if( order0 < order1 )
					return -1;
				else return arg0.compareTo( arg1 );
			}
		} );
		for( String field : AnnotationsParser.getUIControlFieldNames( node.getClass() ) )
			if (this.action == NodeEvent.Action.INSERT && "id".equals(field))
				continue;
			else
				groupRank.put( field, AnnotationsParser.getAttributes( node.getClass(), field).getGroup() );
		
		uiFields = new ArrayList<String>(groupRank.keySet());

		Set<PropertyGroups> set = new HashSet<PropertyGroups>();
		for( Entry<String, PropertyGroups> entry : groupRank.entrySet() ) {
			if( !set.contains( entry.getValue() ) )
			{
				groupLabelProperties.put( entry.getKey(), entry.getValue() );
				set.add( entry.getValue() );
			}
		}
	}

	private boolean isGroupLabelField( String name )
	{
		if( this.groupLabelProperties.containsKey( name ) )
			return true;
		else return false;
	}

	@Override
	public void notify( NodeEvent event )
	{
		for( com.fantasystep.component.panel.Listener listener : this.listeners )
			listener.notify( event );
	}

	@Override
	public boolean validContainerForm()
	{
		if( form.isValid() )
		{
			form.commit();
			return true;
		} else return false;
	}
	@Override
	public Class<? extends Node> getNodeClass() {
		return node.getClass();
	}
}
