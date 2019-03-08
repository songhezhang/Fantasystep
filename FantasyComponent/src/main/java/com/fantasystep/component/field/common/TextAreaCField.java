package com.fantasystep.component.field.common;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractCField;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.TextArea;

public class TextAreaCField extends AbstractCField {

	public TextAreaCField(FieldAttributeAccessor fieldAttributes) {
		super(fieldAttributes);
	}

	@Override
	public void initField() {
		field = new TextArea();
		field.addValueChangeListener( new Property.ValueChangeListener()
		{
			private static final long serialVersionUID = -6596268323085037127L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				if( ( (TextArea) field ).getNullRepresentation().equals( "null" ) )
					( (TextArea) field ).setNullRepresentation( "" );
				else
					validateField();
			}
		} );
		field.setWidth("700px");
		field.setHeight("420px");
//		( (TextArea) field ).setSizeFull();
	}
}
