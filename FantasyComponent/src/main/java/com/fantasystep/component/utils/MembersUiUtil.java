package com.fantasystep.component.utils;

import com.fantasystep.component.panel.LocalizationHandler;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MembersUiUtil
{

	private final static String	AVAILABLE_BOX		= "<div class='available-box'></div>";
	private final static String	INVALID_BOX			= "<div class='invalid-box'></div>";
	private final static String	NOT_AVAILABLE_BOX	= "<div class='not-vailable-box'></div>";

	public static Component getHorizontalLagend()
	{
		HorizontalLayout hz = new HorizontalLayout();
		hz.setSpacing( true );
		hz.addComponent( getLagendRow( AVAILABLE_BOX, LabelUtil.LABEL_AVAILABLE ) );
		hz.addComponent( new Label( "&nbsp;&nbsp;", ContentMode.HTML ) );
		hz.addComponent( getLagendRow( NOT_AVAILABLE_BOX, LabelUtil.LABEL_NOT_AVAILABLE ) );
		hz.addComponent( new Label( "&nbsp;&nbsp;", ContentMode.HTML ) );
		hz.addComponent( getLagendRow( INVALID_BOX, LabelUtil.LABEL_INVALID ) );

		return hz;
	}

	private static Component getLagendRow( String box, String title )
	{
		HorizontalLayout hz = new HorizontalLayout();
		hz.setSpacing( true );
		hz.addComponent( new Label( box, ContentMode.HTML ) );
		hz.addComponent( new Label( LocalizationHandler.get( title ) ) );

		return hz;
	}

	public static Component getVerticalLagend()
	{
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent( getLagendRow( AVAILABLE_BOX, LabelUtil.LABEL_AVAILABLE ) );
		vl.addComponent( getLagendRow( NOT_AVAILABLE_BOX, LabelUtil.LABEL_NOT_AVAILABLE ) );
		vl.addComponent( getLagendRow( INVALID_BOX, LabelUtil.LABEL_INVALID ) );

		return vl;
	}
}
