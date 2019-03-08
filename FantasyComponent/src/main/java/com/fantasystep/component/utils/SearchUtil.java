package com.fantasystep.component.utils;

import com.fantasystep.component.common.Searchable;
import com.fantasystep.component.panel.LocalizationHandler;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class SearchUtil
{

	private static VerticalLayout getHeader( String title, Searchable source )
	{
		VerticalLayout marginLayout = new VerticalLayout();

		Label lbl = new Label( title, ContentMode.HTML );

		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeUndefined();
		hl.setHeight( "36px" );

		Component search = getSearchBox( source );
		hl.setWidth( "95%" );

		hl.addComponent( lbl );
		hl.addComponent( search );
		// Expand Ratio
		marginLayout.addComponent( hl );
		hl.setExpandRatio( lbl, 1f );
		hl.setExpandRatio( search, 2f );
		hl.setComponentAlignment( search, Alignment.TOP_RIGHT );

		return wrappedHeaderLayout( marginLayout );
	}

	@SuppressWarnings("serial")
	public static Component getSearchBox( final Searchable source )
	{
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setStyleName( CSSUtil.SEARCH_INPUT_BG );
		hl.setWidth( "70%" );
		hl.setHeight( "18px" );

		final TextField textField = new TextField();
		textField.setWidth( "100%" );
		textField.setValue( LocalizationHandler.get( LabelUtil.LABEL_SEARCH ) );
		textField.setStyleName( CSSUtil.SEARCH_LIGHT_GRAY_NOMAL );

		Button button = new Button( "", new ClickListener()
		{
			@Override
			public void buttonClick( ClickEvent event )
			{
				source.removeContainerFilter();

				if( textField.getStyleName().equals( CSSUtil.SEARCH_LIGHT_GRAY_NOMAL ) )
				{
					textField.setCursorPosition( 0 );
					textField.setValue( "" );

				}

				source.applyContainerFilter( textField.getValue().toString().trim() );
			}
		} );

		textField.addFocusListener( new com.vaadin.event.FieldEvents.FocusListener()
		{
			@Override
			public void focus( FocusEvent event )
			{
				if( textField.getStyleName().equals( CSSUtil.SEARCH_LIGHT_GRAY_NOMAL ) )
				{
					textField.removeStyleName( CSSUtil.SEARCH_LIGHT_GRAY_NOMAL );
					textField.setValue( "" );
				}
			}

		} );

		textField.addBlurListener( new BlurListener()
		{
			@Override
			public void blur( BlurEvent event )
			{
				if( textField.getValue().toString().trim().equals( "" ) )
				{
					textField.addStyleName( CSSUtil.SEARCH_LIGHT_GRAY_NOMAL );
					textField.setValue( LocalizationHandler.get( LabelUtil.LABEL_SEARCH ) );
					source.removeContainerFilter();
				}
			}
		} );

		IconUtil.wrapImage( button, IconUtil.ICON_SEARCH );
		button.setWidth( "30px" );
		button.setClickShortcut( KeyCode.ENTER );

		hl.addComponent( textField );
		hl.addComponent( button );
		// hl.setSpacing( true );
		hl.setExpandRatio( textField, 3f );
		hl.setExpandRatio( button, 1f );
		hl.setComponentAlignment( button, Alignment.TOP_RIGHT );

		return hl;
	}

	public static Component getSimpleSearch( Label label, Searchable wrappedSource )
	{
		HorizontalLayout hz = new HorizontalLayout();
		hz.setStyleName( "simple-panel" );
		hz.setSpacing( true );
		if( label != null )
			hz.addComponent( label );

		hz.addComponent( getSearchBox( wrappedSource ) );

		return hz;
	}

	private static Component getWrappedComponent( Searchable source, Integer width )
	{
		VerticalLayout wrapped = new VerticalLayout();
		wrapped.setStyleName( "box" );
		( (Component) source ).setSizeUndefined();

		// Note: width in percentage will create a problem

		if( width != null ) // remove scroll space like 15
			( (Component) source ).setWidth( (width - 15) + "px" );

		wrapped.addComponent( ( (Component) source ) );

		return wrapped;
	}

	private static VerticalLayout wrappedHeaderLayout( Component component )
	{
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setStyleName( "header" );
		wrapper.addComponent( component );

		return wrapper;
	}

	public static Component wrapTitleHeaderWithSearch( Searchable source, String title )
	{
		return wrapTitleHeaderWithSearch( source, title, null, null );
	}

	// returns search box with complete wrapper
	public static Component wrapTitleHeaderWithSearch( Searchable source, String title, Integer height, Integer width )
	{

		VerticalLayout outer = new VerticalLayout();
		outer.setStyleName( "container" );

		Component wrappedComponent = getWrappedComponent( source, width );

		outer.addComponent( getHeader( title, source ) );
		outer.addComponent( wrappedComponent );

		if( height != null )
			wrappedComponent.setHeight( height + "px" );

		if( source instanceof Table )
			( (Component) source ).setStyleName( CSSUtil.CUSTOMIZED_TABLE_STYLE );

		if( width != null )
			outer.setWidth( width + "px" );

		return outer;
	}
}
