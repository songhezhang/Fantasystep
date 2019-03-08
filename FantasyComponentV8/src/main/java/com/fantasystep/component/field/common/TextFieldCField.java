package com.fantasystep.component.field.common;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractCField;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.ui.TextField;

public class TextFieldCField extends AbstractCField
{
	public TextFieldCField( FieldAttributeAccessor fieldAttributes )
	{
		super( fieldAttributes );
	}

	@Override
	public void initField()
	{
		field = new TextField();
		field.addValueChangeListener( new Property.ValueChangeListener()
		{
			private static final long serialVersionUID = -6596268323085037127L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				if( ( (TextField) field ).getNullRepresentation().equals( "null" ) )
					( (TextField) field ).setNullRepresentation( "" );
				else
					validateField();
			}
		} );
	}
}
