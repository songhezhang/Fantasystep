package com.fantasystep.container.multiple.relation;


import java.util.List;

import com.fantasystep.component.panel.ConcreteNodeEvent;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.component.utils.UINodeUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.domain.Group;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.TreeHandler;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MultiTableMembersCContainer extends AbstractCContainer
{
	private static final long serialVersionUID = 8313523954351116480L;
	
	private NodeMembersReadOnlyCContainer	membersContainer;			// members of targetNode
	private MembershipReadOnlyCContainer	membershipContainer;		// joined membership by targetNode
	private Node							target;
	private Node							root;

	public MultiTableMembersCContainer( Action action, FormMode mode )
	{
		super( action, mode );

		this.target = TreeHandler.getTargetNodeByApplication();
		this.root = TreeHandler.getRootNodeByApplication();

		initDisplay();
		setMargin( false );

		removeComponent( getHeader() );// special case
	}

	@Override
	public void buttonClick( ClickEvent event )
	{

	}

	public NodeMembersReadOnlyCContainer getMembersContainer()
	{
		if( membersContainer == null )
		{
			List<Node> list = NodeUtil.getMembers( (MemberHolder) this.target, this.root );
			membersContainer = new NodeMembersReadOnlyCContainer( list, LocalizationHandler.get( LabelUtil.LABEL_MEMBERS ) );
		
			//To syncronize data for different applications, we are setting up root/target again.
			membersContainer.setRoot( this.root );
			membersContainer.setTarget( this.target );

			membersContainer.setMargin( false );
		}
		return membersContainer;
	}

	@SuppressWarnings("unchecked")
	public MembershipReadOnlyCContainer getMembershipContainer()
	{
		if( membershipContainer == null )
		{
			List<Node> list = UINodeUtil.getValidatedMemberships( TreeHandler.getTargetNodeByApplication(), TreeHandler.getRootNodeByApplication(),
					Group.class );

			membershipContainer = new MembershipReadOnlyCContainer( list, LocalizationHandler.get( LabelUtil.LABEL_MEMBERSHIPS ) );
			membershipContainer.setMargin( false );
		}
		return membershipContainer;
	}

	@Override
	protected void initDisplay()
	{
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent( getMembershipContainer() );
		vl.addComponent( new Label( "<br />", ContentMode.HTML ) );// spacer between two containers

		if( TreeHandler.getTargetNode() instanceof MemberHolder ) // only on Group/org case
			vl.addComponent( getMembersContainer() );

		addBodyComponent( LayoutUtil.addScrollablePanel( vl, true ), Alignment.TOP_LEFT );
	}

	@Override
	protected void initForm()
	{
	}

	@Override
	public void notify( NodeEvent event )
	{
		this.root = ( (ConcreteNodeEvent) event ).getRootNode();
		this.target = ( (ConcreteNodeEvent) event ).getTargetNode();

		this.membersContainer = null;
		this.membershipContainer = null;
		getBodyContainer().removeAllComponents();
		initDisplay();
	}
	@Override
	public Class<? extends Node> getNodeClass() {
		return this.target.getClass();
	}
}
