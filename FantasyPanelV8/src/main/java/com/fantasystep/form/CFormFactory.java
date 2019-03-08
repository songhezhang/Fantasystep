package com.fantasystep.form;


import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.UINodeUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.container.AbstractCContainer.FormMode;
import com.fantasystep.container.multiple.NodeTableCContainer;
import com.fantasystep.container.multiple.account.AccountTableCContainer;
import com.fantasystep.container.multiple.relation.MembershipCContainer;
import com.fantasystep.container.multiple.relation.MultiTableMembersCContainer;
import com.fantasystep.container.multiple.relation.NodeMembersReadOnlyCContainer;
import com.fantasystep.container.multiple.relation.ResourceCContainer;
import com.fantasystep.container.multiple.relation.ResourceReadOnlyCContainer;
import com.fantasystep.container.single.NodeDisplayCContainer;
import com.fantasystep.container.single.NodeDisplayCContainer.Mode;
import com.fantasystep.container.single.SingleNodeCContainer;
import com.fantasystep.domain.AbstractAccount;
import com.fantasystep.domain.AbstractGroup;
import com.fantasystep.domain.Group;
import com.fantasystep.domain.MongoDynamicDomain;
import com.fantasystep.domain.Node;
import com.fantasystep.domain.Organization;
import com.fantasystep.domain.Permission;
import com.fantasystep.domain.Resource;
import com.fantasystep.domain.Table;
import com.fantasystep.domain.User;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.helper.Status;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.persistence.exception.PermissionDeniedException;
import com.fantasystep.persistence.exception.PersistenceException;
import com.fantasystep.persistence.exception.UnauthorizedException;
import com.fantasystep.util.CNodeUtil;
import com.fantasystep.utils.NodeClassUtil;
import com.fantasystep.utils.NodeUtil;
import com.fantasystep.utils.PermissionUtil.AllTruePermissionDescriptor;
import com.fantasystep.utils.PermissionUtil.PermissionDescriptor;

public class CFormFactory
{
	public static final Comparator<String>								comparator				= new Comparator<String>()
																								{
																									@Override
																									public int compare( String o1, String o2 )
																									{
																										return 1;
																									}
																								};

	private static Map<Class<? extends Node>,Map<String,List<Node>>>	nodeInputTemplatesMap	= new HashMap<Class<? extends Node>,Map<String,List<Node>>>();

	private static Map<Class<? extends Node>,Class<?>[]>				nodeWizardTemplatesMap	= new HashMap<Class<? extends Node>,Class<?>[]>();

	private static Map<Class<? extends Node>,Map<Class<?>,String>>		tabLayoutMap			= new HashMap<Class<? extends Node>,Map<Class<?>,String>>();;
	
	static
	{
		initWizardTemplatesMap();
		initTabTemplatesMap();
		// initNodeInputTemplatesMap();
	}

	public static Map<String,List<Node>> getNodeInputTemplateMap( Class<? extends Node> clazz )
	{
		return nodeInputTemplatesMap.get( clazz );
	}

	private static void initTabTemplatesMap() {
		for( Class<? extends Node> clazz : NodeClassUtil.getNodeClassInJVM() )
		{
			if( clazz.getAnnotation( DomainClass.class ) != null && clazz.getAnnotation( DomainClass.class ).isPropertyNode() )
				continue;

			if( !Modifier.isAbstract( clazz.getModifiers() ) || Resource.class.isAssignableFrom( clazz ) )
			{
				tabLayoutMap.put( clazz, new TreeMap<Class<?>,String>( new Comparator<Class<?>>()
						{
					@Override
					public int compare( Class<?> o1, Class<?> o2 )
					{
						DomainClass dc1 = o1.getAnnotation( DomainClass.class );
						DomainClass dc2 = o2.getAnnotation( DomainClass.class );
						if(o1.equals(o2))
							return 0;
						else if( o2.equals( Node.class ) )
							return 1;
						else if( o1.equals( Node.class ) )
							return -1;
						else if( dc1.category().equals(dc2.category()) )
							return dc1.category().compareTo( dc2.category() );
						else
							return dc1.label().compareTo( dc2.label() );
					}
				}));
				tabLayoutMap.get( clazz ).put( Node.class, LabelUtil.LabelConstant.PROPERTY );
			}

			if( clazz.equals( User.class ) || clazz.equals( Group.class ) || clazz.equals( Organization.class ) || Resource.class.isAssignableFrom( clazz ) )
				tabLayoutMap.get( clazz ).put( AbstractGroup.class, AbstractGroup.class.getAnnotation( DomainClass.class ).label() );
			
			if( clazz.equals(Table.class))
				tabLayoutMap.get( clazz ).put( Table.class, Table.class.getAnnotation( DomainClass.class ).label() );
//			tabLayoutMap.get( clazz ).put( clazz, clazz.getAnnotation( DomainClass.class ).label() );
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, AbstractCContainer> getTabsFromTargetNode( Node root, Node node, Action action, Map<String,String> labelMap )
	{
		Map<String,AbstractCContainer> map = new TreeMap<String,AbstractCContainer>( comparator );
//		User user = TreeHandler.getUserNode();
		if(tabLayoutMap.get( node.getClass() ) != null) {
			for( Entry<Class<?>,String> tabMap : tabLayoutMap.get( node.getClass() ).entrySet() )
			{
	//			PermissionDescriptor descripter = new PermissionDescriptor();
				PermissionDescriptor descripter = new AllTruePermissionDescriptor();
	//			for( Permission p : user.getPermissions( (Class<? extends Node>) tabMap.getKey() ) )
	//				descripter.apply( p );
				if( !descripter.hasBrowsePermission() )
					continue;
	
				if( tabMap.getValue().equals( LabelUtil.LabelConstant.PROPERTY ) )
				{
					map.put( node.getClass().getSimpleName(), new NodeDisplayCContainer( node, Mode.WITH_UPDATE_BUTTON ) );
					labelMap.put( node.getClass().getSimpleName(), LocalizationHandler.get( tabMap.getValue() ) );
	
				} else if( node instanceof Resource )
				{
					List<Node> list = NodeUtil.getMembers( (MemberHolder) TreeHandler.getTargetNode(), TreeHandler.getRootNode() );
					map.put( AbstractGroup.class.getSimpleName(), new NodeMembersReadOnlyCContainer( list, LocalizationHandler.get( LabelUtil.LABEL_MEMBERS ) ) );
					labelMap.put( AbstractGroup.class.getSimpleName(), LocalizationHandler.get( tabMap.getValue() ) );
	
				} else if( tabMap.getKey().equals( AbstractGroup.class ) )
				{
					map.put( AbstractGroup.class.getSimpleName(), new MultiTableMembersCContainer( Action.UPDATE, FormMode.TAB ) );
					labelMap.put( AbstractGroup.class.getSimpleName(), LocalizationHandler.get( tabMap.getValue() ) );
	
				} else if( Resource.class.isAssignableFrom( tabMap.getKey() ) )
				{
					Class<? extends Resource> clazz = (Class<? extends Resource>) tabMap.getKey();
					List<Node> list = CNodeUtil.getAssignedResourcesByClass( node, (Class<? extends com.fantasystep.domain.Resource>) tabMap.getKey() );
	
					map.put( clazz.getSimpleName(), new ResourceReadOnlyCContainer( list, (Class<? extends Node>) tabMap.getKey() ) );
					labelMap.put( clazz.getSimpleName(), LocalizationHandler.get( tabMap.getValue() ) );
	
				} else if( tabMap.getKey().equals( AbstractAccount.class ) )
				{
					map.put( AbstractAccount.class.getSimpleName(), new AccountTableCContainer( null, action, AbstractAccount.class, FormMode.TAB, node ) );
					labelMap.put( AbstractAccount.class.getSimpleName(), LocalizationHandler.get( tabMap.getValue() ) );
	//			} else if( tabMap.getKey().equals( Phone.class ) || tabMap.getKey().equals( Computer.class ) )
	//			{
	//				List<Node> childs = node.getChildren( (Class<? extends Node>) tabMap.getKey() );
	//				map.put( tabMap.getKey().getSimpleName(), new NodeTableTreeCContainer( childs, (Class<? extends Node>) tabMap.getKey(), action, FormMode.TAB, node ) );
	//				labelMap.put( tabMap.getKey().getSimpleName(), LocalizationHandler.get( tabMap.getValue() ) );
				} else if( node instanceof Table) {
					Node fullNodeByID = node;
					try {
						fullNodeByID = TreeHandler.get().getFullNodeByID(node.getId());
					} catch (UnauthorizedException | PersistenceException
							| PermissionDeniedException e) {
						e.printStackTrace();
					}
					Class<? extends Node> clazz = NodeClassUtil.getDynamicEntityClassByFullName(((Table)node).getTableName());
					if(clazz == null)
						continue;
					List<Node> list = new ArrayList<Node>();
					for(Node n : fullNodeByID.getChildren())
						if(n.getClass().equals(Node.class)) {
							Node nn = NodeClassUtil.getDeserializationNode(n);
							if(nn.getClass().equals(clazz))
								list.add(nn);
						}
					map.put( node.getClass().getSimpleName(), new NodeTableCContainer( list, clazz, action, FormMode.TAB, node.getId() ) );
					labelMap.put( MongoDynamicDomain.class.getSimpleName(), LocalizationHandler.get(AnnotationsParser.getAttributes(node.getClass()).getLabel() ) );
				
				} else
				{
					Class<? extends Node> clazz = (Class<? extends Node>) tabMap.getKey();
					map.put( clazz.getSimpleName(), new NodeTableCContainer( node.getChildren( clazz ), clazz, action, FormMode.TAB, node.getId() ) );
					labelMap.put( clazz.getSimpleName(), LocalizationHandler.get( tabMap.getValue() ) );
				}
			}
		}
//		else if( MongoDynamicDomain.class.isAssignableFrom(node.getClass())) {
//			Node fullNodeByID = node;
//			try {
//				fullNodeByID = TreeHandler.get().getFullNodeByID(node.getId());
//			} catch (UnauthorizedException | PersistenceException
//					| PermissionDeniedException e) {
//				e.printStackTrace();
//			}
//			map.put( node.getClass().getSimpleName(), new NodeTableCContainer( fullNodeByID.getChildren( node.getClass() ), node.getClass(), action, FormMode.TAB, node.getId() ) );
//			labelMap.put( MongoDynamicDomain.class.getSimpleName(), LocalizationHandler.get(AnnotationsParser.getAttributes(node.getClass()).getLabel() ) );
//		} 
		else {
			map.put( node.getClass().getSimpleName(), new NodeDisplayCContainer( node, Mode.WITH_UPDATE_BUTTON ) );
			labelMap.put( node.getClass().getSimpleName(), LocalizationHandler.get(AnnotationsParser.getAttributes(node.getClass()).getLabel() ));
		}
		return map;
	}

	private static Class<?>[] getValidSteps( Node node )
	{
		return nodeWizardTemplatesMap.get( node.getClass() );
	}

	@SuppressWarnings("unchecked")
	public static Map<String,AbstractCContainer> getWizardPanelsFromTargetNode( Node root, Node node, Action action, Map<String,String> labelMap )
	{
		Map<String,AbstractCContainer> map = new TreeMap<String,AbstractCContainer>( comparator );

		for( Class<?> clazz : getValidSteps( node ) )
		{
			if( clazz.equals( Node.class ) )
			{
				map.put( node.getClass().getSimpleName(), new SingleNodeCContainer( node, action, FormMode.WIZARD, false ) );
				labelMap.put( node.getClass().getSimpleName(), LabelUtil.getDomainLabel( node.getClass() ) );
//			} else if( clazz.equals( MobilePhoneSubscription.class ) )
//			{
//				// Node newNode = NodeUtil.getNewNode( (Class<? extends Node>) clazz, node.getId() );
//				// map.put( clazz.getSimpleName(), new SingleNodeCContainer( newNode, action, FormMode.WIZARD, false )
//				// );
//				map.put( clazz.getSimpleName(), new NodeTableCContainer( node.getChildren( MobilePhoneSubscription.class ), MobilePhoneSubscription.class, action, FormMode.WIZARD, node.getId() ) );
//				labelMap.put( clazz.getSimpleName(), LabelUtil.getDomainLabel( (Class<? extends Node>) clazz ) );
			} else if( clazz.equals( AbstractGroup.class ) )
			{
				List<Node> list = null;
				if( action == Action.UPDATE )
					list = UINodeUtil.getValidatedMemberships( node, root, AbstractGroup.class );
				else list = new ArrayList<Node>();
				map.put( AbstractGroup.class.getSimpleName(), new MembershipCContainer( list, node, FormMode.WIZARD, false ) );
				labelMap.put( AbstractGroup.class.getSimpleName(), LabelUtil.getDomainLabel( AbstractGroup.class ) );
			} else if( Resource.class.isAssignableFrom( clazz ) )
			{
				List<Node> list = null;
				if( action == Action.UPDATE )
					list = CNodeUtil.getAssignedResourcesByClass( node, (Class<? extends com.fantasystep.domain.Resource>) clazz );
				else list = new ArrayList<Node>();
				map.put( clazz.getSimpleName(), new ResourceCContainer( list, node, (Class<? extends Resource>) clazz, FormMode.WIZARD, false ) );
				labelMap.put( clazz.getSimpleName(), LabelUtil.getDomainLabel( (Class<? extends Node>) clazz ) );
			} else if( clazz.equals( AbstractAccount.class ) )
			{
				map.put( AbstractAccount.class.getSimpleName(), new AccountTableCContainer( null, action, AbstractAccount.class, FormMode.WIZARD, node ) );
				labelMap.put( AbstractAccount.class.getSimpleName(), LabelUtil.getDomainLabel( AbstractAccount.class ) );
//			} else if( clazz.equals( Phone.class ) || clazz.equals( Computer.class ) )
//			{
//				List<Node> children = new ArrayList<Node>();
//				if( action == Action.UPDATE )
//					for( Node n : node.getChildren( (Class<? extends Node>) clazz ) )
//						children.addAll( NodeUtil.getChildren( n, n, true ) );
//				map.put( clazz.getSimpleName(), new NodeTableTreeCContainer( children, (Class<? extends Node>) clazz, action, FormMode.WIZARD, node ) );
//				labelMap.put( clazz.getSimpleName(), LabelUtil.getDomainLabel( (Class<? extends Node>) clazz ) );
			} else
			{
				map.put( clazz.getSimpleName(), new NodeTableCContainer( node.getChildren( (Class<? extends Node>) clazz ), (Class<? extends Node>) clazz, action, FormMode.WIZARD, node.getId() ) );
				labelMap.put( clazz.getSimpleName(), LabelUtil.getDomainLabel( (Class<? extends Node>) clazz ) );
			}
		}
		return map;
	}

	public static boolean hasNodeInputTemplate( Class<? extends Node> clazz )
	{
		return nodeInputTemplatesMap.containsKey( clazz );
	}

	@SuppressWarnings("unused")
	private static void initNodeInputTemplatesMap()
	{
		Map<String,List<Node>> map = new TreeMap<String,List<Node>>( comparator );
		List<Node> list1 = new ArrayList<Node>();
		List<Node> list2 = new ArrayList<Node>();
		List<Node> list3 = new ArrayList<Node>();
		Permission p1 = new Permission();
		Permission p2 = new Permission();
		Permission p3 = new Permission();
		Permission p4 = new Permission();
		p1.setTargetNodeId( TreeHandler.getRootNode().getId() );
		p2.setTargetNodeId( TreeHandler.getRootNode().getId() );
		p3.setTargetNodeId( TreeHandler.getRootNode().getId() );
		p4.setTargetNodeId( TreeHandler.getRootNode().getId() );
		p1.addTargetClass( Organization.class );
		p2.addTargetClass( User.class );
		p3.addTargetClass( AbstractAccount.class );
		p4.addTargetClass( Permission.class );
		p1.setBrowsePrivilage( Status.TRUE );
		p2.setInsertPrivilage( Status.TRUE );
		p3.setDeletePrivilage( Status.TRUE );
		p4.setDestroyPrivilage( Status.TRUE );
		list1.add( p1 );
		list1.add( p2 );
		list1.add( p3 );
		list1.add( p4 );

		list2.add( p1 );
		list2.add( p2 );
		list2.add( p3 );

		list3.add( p1 );
		list3.add( p2 );

		map.put( "LABEL_TEMPLATE1", list1 );
		map.put( "LABEL_TEMPLATE2", list2 );
		map.put( "LABEL_TEMPLATE3", list3 );

		nodeInputTemplatesMap.put( Permission.class, map );
	}

	private static void initWizardTemplatesMap()
	{
		nodeWizardTemplatesMap.put( User.class, new Class<?>[] { Node.class, Permission.class, AbstractGroup.class/*,  Location.class, 
		Phone.class  , Computer.class */} );
		nodeWizardTemplatesMap.put( Group.class, new Class<?>[] { Node.class, Permission.class/*,  Location.class} */});
//		nodeWizardTemplatesMap.put( MobilePhone.class, new Class<?>[] { Node.class, MobilePhoneSubscription.class } );
	}

	public static boolean isWizard( Class<? extends Node> clazz )
	{
		return nodeWizardTemplatesMap.containsKey( clazz );
	}
}
