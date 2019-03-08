package com.fantasystep.component.field.common;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractCField;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.PasswordField;

public class PasswordCField extends AbstractCField
{

	public PasswordCField( FieldAttributeAccessor fieldAttributes )
	{
		super( fieldAttributes );
	}

	@Override
	public void initField()
	{
		field = new PasswordField();
		field.addValueChangeListener( new Property.ValueChangeListener()
		{
			private static final long serialVersionUID = 3725792505356687525L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				if( ( (PasswordField) field ).getNullRepresentation().equals( "null" ) )
					( (PasswordField) field ).setNullRepresentation( "" );
				else
					validateField();
			}
		} );
	}
}
