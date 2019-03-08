package com.fantasystep.component.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.domain.AbstractGroup;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.data.util.BeanItem;

@SuppressWarnings("unchecked")
public class UINodeUtil
{
	// it will return all memberships including non validated nodes
	public static List<Node> getAllMemberships( Node target, Node root,  Class<? extends AbstractGroup>... filterType )
	{
		List<Node> list = new ArrayList<Node>();

		list.addAll( getPersonalMemberships( target, root, filterType ) );

		Node parent = NodeUtil.getNode( target.getParentId(), root );
		if( parent != null )
			list.addAll( getAllMemberships( parent, root, filterType ) );

		return list;
	}

	// Note: It will return the Node, which is registered inside MemberHolder, it can be target node or the parent of
	// targetNode
	public static Node getInheritedFrom( MemberHolder m, Node target, Node root )
	{

		if( m.getMembers() != null && m.getMembers().contains( target.getId() ) )
			return target;
		else
		{
			Node parent = NodeUtil.getNode( target.getParentId(), root );
			if( parent != null )
				return getInheritedFrom( m, parent, root );
		}

		return null;
	}

	public static Map<MemberHolder,Node> getInheritedNodesMap( List<Node> members, Node target, Node root )
	{
		Map<MemberHolder,Node> map = new HashMap<MemberHolder,Node>();
		for( Node m : members )
			// ignore child nodes by using first parameter
			if( !target.getId().equals( m.getParentId() ) && m instanceof MemberHolder && !( (MemberHolder) m ).getMembers().contains( target.getId() ) )
				map.put( (MemberHolder) m, getInheritedFrom( (MemberHolder) m, target, root ) );

		return map;
	}

	public static List<Node> getNonWindowsMemberships( Node target, Node root, Class<? extends AbstractGroup>... filterType )
	{
		List<Node> list = new ArrayList<Node>();
		for( Node n : getPersonalMemberships( target, root, filterType ) )
			if( !hasWindowAccount( n ) )
				list.add( n );

		Node parent = NodeUtil.getNode( target.getParentId(), root );
		if( parent != null )
			list.addAll( getNonWindowsMemberships( parent, root, filterType ) );

		return list;
	}

	// Need to refactor this method,
	public static List<Node> getParentsAndChildren( Node target, Node root )
	{
		List<Node> nodes = new ArrayList<Node>();
		nodes.add( target );
		nodes.addAll(NodeUtil.getParents( target, root ) );
		nodes.addAll(NodeUtil.getChildren( target, root ) );
		return nodes;
	}

	public static List<Node> getPersonalMemberships( Node target, Node root, Class<? extends AbstractGroup>... filterType )
	{
		List<MemberHolder> memberships = NodeUtil.getMemberships( target, root );
		return NodeUtil.filterNodes( Arrays.asList( memberships.toArray( new Node[] {} ) ), filterType );
	}

	// Note : need to refactor this method
	public static String getPrimaryFieldLabel( Node n )
	{
		BeanItem<Node> item = new BeanItem<Node>( n );
		for( Object pid : item.getItemPropertyIds() )
		{
			FieldAttributeAccessor attr = AnnotationsParser.getAttributes( ( ( n ) ).getClass(), pid.toString() );

			if( attr != null && attr.getIsSiblingUnique() )
				if( (Boolean) item.getItemProperty( pid ).getValue() )
					return LocalizationHandler.get( attr.getLabel() );
		}
		return "";
	}

	public static Node getPrimaryNode( Node parent, Class<? extends Node> clzz )
	{
		for( Node n : parent.getChildren( clzz ) )
			if( isPrimarySiblingValue( n ) )
				return n;

		return null;
	}

	public static List<Node> getPrimaryNodes( Node parent )
	{
		List<Node> list = new ArrayList<Node>();
		for( Node n : parent.getChildren() )
			if( isPrimarySiblingValue( n ) )
				list.add( n );

		return list;
	}

	public static List<Node> getValidatedMemberships( Node target, Node root, Class<? extends AbstractGroup>... filterType )
	{
		List<Node> list = new ArrayList<Node>();
		if( hasWindowAccount( target ) )
			list.addAll( getAllMemberships( target, root, filterType ) );
		else
			list.addAll( getNonWindowsMemberships( target, root, filterType ) );
		return list;
	}

	public static boolean hasInheritedNodes( MemberHolder m, Node target, Node root )
	{
		if( getInheritedFrom( m, target, root ) == null )
			return false;

		if( !getInheritedFrom( m, target, root ).getId().equals( target.getId() ) )
			return true;

		return false;
	}

	public static boolean hasPrimaryAnnotation( Class<? extends Node> clazz )
	{
		for( Field f : AnnotationsParser.getUIControlField( clazz ) )
		{
			f.setAccessible( true );
			if( AnnotationsParser.getAttributes( clazz, f.getName()).getIsSiblingUnique() )
				return true;
		}

		return false;
	}

	public static boolean hasWindowAccount( Node target )
	{
//		List<Class<? extends AbstractAccount>> accounts = AccountUtil.getAccountTypes( target );
//		for( Class<? extends AbstractAccount> acc : accounts )
//			if( AbstractWindowsAccount.class.isAssignableFrom( acc ) )
//				return true;

		return false;
	}

//	public static boolean hasWindowsMemberships( Node target, Node root, Class<? extends AbstractGroup>... filterType )
//	{
//		if( !UINodeUtil.hasWindowAccount( target ) )
//			return false;
//
//		for( Node n : UINodeUtil.getPersonalMemberships( target, root, filterType ) )
//			if( UINodeUtil.hasWindowAccount( n ) )
//				return true;
//
//		return false;
//	}

	public static boolean isPrimarySiblingValue( Node n )
	{
		BeanItem<Node> item = new BeanItem<Node>( n );
		for( Object pid : item.getItemPropertyIds() )
		{
			FieldAttributeAccessor attr = AnnotationsParser.getAttributes( ( ( n ) ).getClass(), pid.toString());
			if( attr != null && attr.getIsSiblingUnique() )
				if( (Boolean) item.getItemProperty( pid ).getValue() )
					return true;
		}

		return false;
	}
}
