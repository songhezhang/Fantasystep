package com.fantasystep.container.multiple.account;


import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.container.multiple.AbstractMultiNodeCContainer;
import com.fantasystep.container.single.AbstractNodeCContainer;
import com.fantasystep.domain.AbstractAccount;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.utils.NodeClassUtil;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public abstract class AbstractAccountCContainer extends AbstractMultiNodeCContainer
{

	protected enum NodeActionType
	{
		ACTIVATE( "LABEL_ACTIVATE" ), DE_ACTIVATE( "LABEL_DE_ACTIVATE" );
		private String	label;

		private NodeActionType( String label )
		{
			setLabel( label );
		}

		public String getLabel()
		{
			return label;
		}

		private void setLabel( String label )
		{
			this.label = label;
		}
	}

	protected Map<Class<? extends AbstractAccount>,AbstractNodeCContainer>	accountMap	= new HashMap<Class<? extends AbstractAccount>,AbstractNodeCContainer>();

	protected Map<Class<? extends AbstractAccount>,String>					validAccountsMap;

	public AbstractAccountCContainer( NodeEvent.Action action, FormMode mode, Class<? extends Node> nodeClass )
	{
		super( action, nodeClass, mode );
		initAccountList();
		initForm();
		initDisplay();
	}

	@Override
	public void buttonClick( ClickEvent event )
	{
	}

	protected AbstractAccount getAccount( Class<? extends AbstractAccount> accountClazz )
	{
		for( Node node : nodeList )
		{
			AbstractAccount account = (AbstractAccount) node;
			if( account.getClass().equals( accountClazz ) )
				return account;
		}

		return null;
	};

	protected String getLabel( Class<? extends AbstractAccount> account )
	{
		return LocalizationHandler.get( validAccountsMap.get( account ) );
	}

	abstract protected Component getNodeComponent( Class<? extends AbstractAccount> account );

	abstract protected AbstractNodeCContainer getNodeContainer( Class<? extends AbstractAccount> clazz );

	@SuppressWarnings("unchecked")
	private Map<Class<? extends AbstractAccount>,String> getValidAccountsMap()
	{
		if( validAccountsMap == null )
		{
			validAccountsMap = new HashMap<Class<? extends AbstractAccount>,String>();
			for( Class<?> clzz : NodeClassUtil.getSubClassesInJVM(AbstractAccount.class) )
				if( !Modifier.isAbstract( clzz.getModifiers() ) )
					for( Class<?> parent : clzz.getAnnotation( DomainClass.class ).validParents() )
						if( parent.equals( nodeClass ) )
							validAccountsMap.put( (Class<? extends AbstractAccount>) clzz, clzz.getAnnotation( DomainClass.class ).label() );
		}
		return validAccountsMap;
	}

	private void initAccountList()
	{
		this.nodeList.clear();
		nodeList.addAll( TreeHandler.getTargetNodeByApplication().getChildren( AbstractAccount.class ) );
	}

	@Override
	protected void initDisplay()
	{
		( (VerticalLayout) getBodyContainer() ).removeAllComponents();
		VerticalLayout vl = new VerticalLayout();
		for( Class<? extends AbstractAccount> account : getValidAccountsMap().keySet() )
			vl.addComponent( getNodeComponent( account ) );

		addBodyComponent( vl, Alignment.TOP_LEFT );
	}

	@Override
	protected void initForm()
	{
		this.accountMap.clear();
		for( Class<? extends AbstractAccount> account : getValidAccountsMap().keySet() )
			accountMap.put( account, getNodeContainer( account ) );
	}

	@Override
	public void notify( NodeEvent event )
	{
		if( !( event.getNode() instanceof AbstractAccount ) )
			return;
		initAccountList();
		initForm();
		initDisplay();
	};

	protected void removeAccount( Class<? extends AbstractAccount> clazz )
	{

		int index = -1;
		for( Node node : nodeList )
		{
			AbstractAccount account = (AbstractAccount) node;
			if( account.getClass().equals( clazz ) )
				index = nodeList.indexOf( account );
		}
		if( index != -1 )
			nodeList.remove( index );
	}
}
