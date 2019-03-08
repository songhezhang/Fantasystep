package com.fantasystep.component.field.common;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.annotation.ValueOptions;
import com.fantasystep.component.field.AbstractCField;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.helper.Cardinality;
import com.vaadin.ui.AbstractSelect;

public abstract class AbstractMultiSelectCField extends AbstractCField
{
	private ValueOptions	valueOptions	= null;

	public AbstractMultiSelectCField( FieldAttributeAccessor fieldAttributes )
	{
		super( fieldAttributes );
	}
	
	public AbstractMultiSelectCField( FieldAttributeAccessor fieldAttributes, ValueOptions valueOptions)
	{
		super( fieldAttributes );
		this.valueOptions = valueOptions;
	}

	protected ValueOptions getMultiItems()
	{
		if( valueOptions == null )
			try
			{
				if(fieldAttributes.getValueOptions().isEnum()) {
					if(fieldAttributes.getValueOptions().getEnumConstants().length > 0)
						valueOptions = fieldAttributes.getValueOptions().getEnumConstants()[0];
				} else valueOptions = fieldAttributes.getValueOptions().newInstance();
			} catch( InstantiationException e )
			{
				e.printStackTrace();
			} catch( IllegalAccessException e )
			{
				e.printStackTrace();
			}
		return valueOptions;
	}

	public void setValueToMultiItems( AbstractSelect multiItems )
	{
		if( fieldAttributes.getCardinality().equals( Cardinality.MULTI ) )
			multiItems.setMultiSelect( true );
		else multiItems.setMultiSelect( false );
		multiItems.setNewItemsAllowed( false );
		multiItems.setNullSelectionAllowed( true );
		for( ValueOptionEntry ob : getMultiItems().getValues() )
		{
			multiItems.addItem( ob.getValue() );
			multiItems.setItemCaption( ob.getValue(), LocalizationHandler.get( ob.getLabel() ) );
		}
		multiItems.setValue( fieldAttributes.getDefaultValue() );
	}
}
