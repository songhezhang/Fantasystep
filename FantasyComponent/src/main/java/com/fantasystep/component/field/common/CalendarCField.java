package com.fantasystep.component.field.common;

import java.util.Date;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractCField;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.PopupDateField;

public class CalendarCField extends AbstractCField
{
	public CalendarCField( FieldAttributeAccessor fieldAttributes )
	{
		super( fieldAttributes );
	}

	@Override
	public void initField()
	{
		field = new PopupDateField();
		final PopupDateField datetime = (PopupDateField) field;
		datetime.setResolution( Resolution.DAY );
		datetime.setDateFormat( "yyyy-MM-dd" );
		datetime.setValue( new Date() );
		datetime.addValueChangeListener( new Property.ValueChangeListener()
		{
			private static final long serialVersionUID = -2834800728544615700L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				if( event.getProperty().getValue() == null )
					datetime.setValue( new Date() );
				else
					validateField();
			}
		} );
		datetime.setImmediate( true );
	}
}
