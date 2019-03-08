package com.fantasystep.component.field.common;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;

public class ComboboxCField extends AbstractMultiSelectCField
{
	public ComboboxCField( FieldAttributeAccessor fieldAttributes )
	{
		super( fieldAttributes );
	}

	@Override
	public void initField()
	{
		field = new ComboBox();
		setValueToMultiItems( (ComboBox) field );
		( (ComboBox) field ).setTextInputAllowed( true );
		field.addValueChangeListener( new Property.ValueChangeListener()
		{
			private static final long serialVersionUID = -9132784426183465347L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				validateField();
			}
		} );
	}
}
