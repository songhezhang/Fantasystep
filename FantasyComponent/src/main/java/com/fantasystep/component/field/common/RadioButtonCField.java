package com.fantasystep.component.field.common;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.OptionGroup;

public class RadioButtonCField extends AbstractMultiSelectCField
{
	public RadioButtonCField( FieldAttributeAccessor fieldAttributes )
	{
		super( fieldAttributes );
	}

	@Override
	public void initField()
	{
		field = new OptionGroup();
		OptionGroup radios = (OptionGroup) field;
		setValueToMultiItems( radios );
		radios.setMultiSelect( false );
		radios.setValue( fieldAttributes.getDefaultValue() );
		radios.setWidth( "150px" );
		radios.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = -8352886416666794867L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				validateField();
			}
		} );
	}
}
