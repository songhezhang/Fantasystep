package com.fantasystep.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fantasystep.component.common.PopUpModel;
import com.fantasystep.component.layout.SimpleContainerLayout;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.component.utils.LayoutUtil;
import com.fantasystep.container.AbstractCContainer;
import com.fantasystep.container.multiple.AbstractMultiNodeCContainer;
import com.fantasystep.container.single.AbstractNodeCContainer;
import com.fantasystep.helper.MemberHolder;
import com.fantasystep.helper.NodeEvent.Action;
import com.fantasystep.panel.CApplication;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;

public abstract class AbstractWizardCForm extends AbstractCForm implements ClickListener, WizardHelper
{
	private static final long serialVersionUID = -3105326757515826029L;
	protected Button		back		= new Button( LocalizationHandler.get( LabelUtil.LABEL_BACK ), this );
	protected Button		cancel		= new Button( LocalizationHandler.get( LabelUtil.LABEL_CANCEL ), this );

	private List<String>	domainNames	= new ArrayList<String>();
	protected Button		edit		= new Button( LocalizationHandler.get( LabelUtil.LABEL_EDIT ), this );
	protected Button		next		= new Button( LocalizationHandler.get( LabelUtil.LABEL_NEXT ), this );

	private PopUpModel		popup		= new PopUpModel();

	protected Button		save		= new Button( LocalizationHandler.get( LabelUtil.LABEL_SAVE ), this );		;

	public AbstractWizardCForm( Map<String,AbstractCContainer> containersMap, Action action )
	{
		super( containersMap, action );
		for( AbstractCContainer c : containersMap.values() )
			domainNames.add( getDomainLabelFromNodeContainer( c ) );
	}

	private String getDomainLabelFromNodeContainer( AbstractCContainer container )
	{
		if( container instanceof AbstractNodeCContainer )
			return LabelUtil.getDomainLabel( ( (AbstractNodeCContainer) container ).getNode().getClass() );

		else if( container instanceof AbstractMultiNodeCContainer )
			return LabelUtil.getDomainLabel( ( (AbstractMultiNodeCContainer) container ).getNodeClass() );

		return null;
	}

	@Override
	public void buttonClick( ClickEvent event )
	{
		if( event.getButton() == next )
		{
			wizardNext();
		} else if( event.getButton() == back )
		{
			wizardBack();
		} else if( event.getButton() == save )
		{
			AbstractCContainer c = getCContainerByIndex( currentPage );
			if( c.validContainerForm() )
			{
				popup.close();
				( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
				wizardSave();
			} else Notification.show( LocalizationHandler.get( LabelUtil.LABEL_VALIDATE_ERROR ) );

		} else if( event.getButton() == cancel )
		{
			popup.close();
			( (CApplication) CApplication.getCurrent() ).getEventHandler().removeListener( this );
			wizardCancel();
		}
	}

	protected AbstractCContainer getCContainerByIndex( int index )
	{
		if( index > getContainersMap().size() || index < 0 )
			return null;
		else return new ArrayList<AbstractCContainer>( getContainersMap().values() ).get( index );
	}

	@Override
	public PopUpModel getCurrentStep()
	{
//		if(popup.isAttached())
//			popup.setContent(null);
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin( new MarginInfo(false, true, true, true ));
		if( currentPage + 1 == getContainersMap().size() )
		{
			this.next.setEnabled( false );
			this.back.setEnabled( true );
			this.save.setVisible( true );
		} else if( currentPage == 0 )
		{
			this.next.setEnabled( true );
			this.back.setEnabled( false );
			this.save.setVisible( false );
		} else
		{
			this.next.setEnabled( true );
			this.back.setEnabled( true );
			this.save.setVisible( false );
		}

		if( this.action == Action.UPDATE )
			this.save.setVisible( true );
		buttons.addComponent( cancel );
		buttons.addComponent( back );
		buttons.addComponent( next );
		buttons.addComponent( save );

		SimpleContainerLayout sl = new SimpleContainerLayout();

		Panel panel = LayoutUtil.addScrollablePanel( getCContainerByIndex( currentPage ), true );
		if( getCContainerByIndex( currentPage ) instanceof AbstractNodeCContainer && ( (AbstractNodeCContainer) getCContainerByIndex( currentPage ) ).getNode() instanceof MemberHolder )
			panel.getContent().setWidth( LayoutUtil.CONST_MEMBERS_SIZE + "px" ); // special case

		sl.addHeader( getWizardHeader(), Alignment.TOP_LEFT );
		sl.getHeader().setStyleName( CSSUtil.GRANDIENT_BOTTOM_LINE );
		sl.addBodyComponent( panel, Alignment.TOP_LEFT );
		sl.addFooter( buttons, Alignment.BOTTOM_CENTER );

		popup.setCaption( String.format( "%s %s", domainNames.get( 0 ), LocalizationHandler.get( LabelUtil.LABEL_WIZARD ) ) );
		popup.setContent( sl );
		popup.setWidth( LayoutUtil.PERCENTAGE_LARGE_SIZE + "%" );

		return this.popup;
	}

	private Component getWizardHeader()
	{
		HorizontalLayout hz = new HorizontalLayout();
		for( String s : domainNames )
		{
			if( domainNames.indexOf( s ) <= currentPage )
				hz.addComponent( new Label( CSSUtil.wrapStyle( String.format( "%s.&nbsp;%s", domainNames.indexOf( s ) + 1, s ), CSSUtil.YELLOW_NORMAL_HEAD ), ContentMode.HTML ) );
			else hz.addComponent( new Label( CSSUtil.wrapStyle( String.format( "%s.&nbsp;%s", domainNames.indexOf( s ) + 1, s ), CSSUtil.LIGHT_GRAY_NORMAL_HEAD ), ContentMode.HTML ) );
		}
		return hz;
	}
}
