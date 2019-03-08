package com.fantasystep.panel;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.component.panel.Listener;
import com.fantasystep.helper.NodeEvent;

public class EventHandler
{
	/*
	 * public EventHandler() { }
	 * 
	 * private class ListenerFilter { private Action action; private Class<? extends Node> type; private Node parent;
	 * 
	 * private boolean compare( Object o1, Object o2 ) { if( o1 == null ) return true;
	 * 
	 * return o1.equals( o2 );
	 * 
	 * }
	 * 
	 * @Override public boolean equals( Object obj ) { if( this == obj ) return true; if( obj == null ) return false;
	 * if( getClass() != obj.getClass() ) return false; ListenerFilter other = (ListenerFilter) obj; if(
	 * !getOuterType().equals( other.getOuterType() ) ) return false; if( action != other.action ) return false; if(
	 * parent == null ) { if( other.parent != null ) return false; } else if( !parent.equals( other.parent ) ) return
	 * false; if( type == null ) { if( other.type != null ) return false; } else if( !type.equals( other.type ) ) return
	 * false; return true; }
	 * 
	 * public Action getAction() { return action; }
	 * 
	 * private EventHandler getOuterType() { return EventHandler.this; }
	 * 
	 * public Node getParent() { return parent; }
	 * 
	 * public Class<? extends Node> getType() { return type; }
	 * 
	 * @Override public int hashCode() { final int prime = 31; int result = 1; result = prime * result +
	 * getOuterType().hashCode(); result = prime * result + ( ( action == null ) ? 0 : action.hashCode() ); result =
	 * prime * result + ( ( parent == null ) ? 0 : parent.hashCode() ); result = prime * result + ( ( type == null ) ? 0
	 * : type.hashCode() ); return result; }
	 * 
	 * @SuppressWarnings("unused") public boolean matches( NodeEvent event ) { // TODO improve this method... Add
	 * support for child classes and more return compare( getAction(), event.getAction() ) && compare(
	 * getParent().getParentId(), event.getNode().getParentId() ) && getType().equals( event.getNode().getType() ); }
	 * 
	 * private void setAction( Action action ) { this.action = action; }
	 * 
	 * private void setParent( Node parent ) { this.parent = parent; }
	 * 
	 * private void setType( Class<? extends Node> type ) { this.type = type; } }
	 * 
	 * private Map<ListenerFilter,List<Listener>> listeners = new HashMap<EventHandler.ListenerFilter,List<Listener>>();
	 * 
	 * public void addListener( Listener listener ) { addListener( listener, null, null, null ); }
	 * 
	 * public void addListener( Listener listener, Action action ) { addListener( listener, action, null, null ); }
	 * 
	 * public void addListener( Listener listener, Action action, Class<? extends Node> type ) { addListener( listener,
	 * action, type, null ); }
	 * 
	 * public void addListener( Listener listener, Action action, Class<? extends Node> type, Node parent ) {
	 * ListenerFilter filter = new ListenerFilter(); filter.setAction( action ); filter.setType( type );
	 * filter.setParent( parent );
	 * 
	 * if( !getListeners().containsKey( filter ) ) getListeners().put( filter, new ArrayList<Listener>() );
	 * 
	 * getListeners().get( filter ).add( listener );
	 * 
	 * }
	 * 
	 * public void addListener( Listener listener, Class<? extends Node> type ) { addListener( listener, null, type,
	 * null ); }
	 * 
	 * public void addListener( Listener listener, Class<? extends Node> type, Node parent ) { addListener( listener,
	 * null, type, null ); }
	 * 
	 * public void addListener( Listener listener, Node parent ) { addListener( listener, null, null, parent ); }
	 * 
	 * public void removeListener( Listener listener ) { for( List<Listener> listenerList : getListeners().values() )
	 * listenerList.remove( listener ); }
	 * 
	 * private Map<ListenerFilter,List<Listener>> getListeners() { return listeners; }
	 * 
	 * public void notify( NodeEvent event ) { for( Entry<ListenerFilter,List<Listener>> entry :
	 * getListeners().entrySet() ) { ListenerFilter filter = entry.getKey(); List<Listener> listeners =
	 * entry.getValue(); if( filter.matches( event ) ) for( Listener listener : listeners ) listener.notify( event ); }
	 * }
	 */

	private List<Listener>	myListeners	= new ArrayList<Listener>();

	public synchronized void addListener( Listener listener )
	{
		myListeners.add( listener );
	}

	public synchronized void notify( NodeEvent event )
	{
		for( Listener listener : myListeners )
			listener.notify( event );
	}

	public synchronized void removeListener( Listener listener )
	{
		myListeners.remove( listener );
	}
}
