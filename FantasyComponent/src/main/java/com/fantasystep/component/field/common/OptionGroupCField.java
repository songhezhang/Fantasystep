package com.fantasystep.component.field.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.component.field.custom.AbstractCustomField;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

class CustomizedOptionGroup extends OptionGroup
{

	private static final long	serialVersionUID	= 1L;

	private boolean				isCommiting;
	private Class<?>			type;

	public CustomizedOptionGroup( Class<?> type )
	{
		super();
		this.type = type;
	}

	@Override
	public void commit() throws SourceException, InvalidValueException
	{
		isCommiting = true;
		try
		{
			super.commit();
		} finally
		{
			isCommiting = false;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getValue()
	{
		final Object retValue = super.getValue();

		if( isCommiting && type != null && List.class.isAssignableFrom( type ) )
		{
			if( retValue == null )
				return new ArrayList( 0 );
			if( retValue instanceof Set )
				return Collections.unmodifiableList( new ArrayList( (Set) retValue ) );
			else if( retValue instanceof Collection )
				return new ArrayList( (Collection) retValue );
			else
			{
				final List l = new ArrayList( 1 );
				if( items.containsId( retValue ) )
					l.add( retValue );
				return l;
			}
		}

		return retValue;
	}
}

class ExtCustomizedOptionGroupField extends AbstractCustomField
{
	private static final long	serialVersionUID	= 6939645285962353454L;

	private CheckBox			reverseCheckBox		= new CheckBox();
	private CheckBox			allCheckBox			= new CheckBox();

	public ExtCustomizedOptionGroupField( OptionGroup field, final List<ValueOptionEntry> entries )
	{
		super( field.getCaption(), field.getType() );
		setField( field );
		getField().setCaption( null );

		allCheckBox.setImmediate( true );
		allCheckBox.setDescription( LocalizationHandler.get( LabelUtil.LABEL_CHECK_ALL ) );
		allCheckBox.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -871147162043940930L;

			@Override
            public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
            	reverseCheckBox.setValue( false );
				if( (Boolean) allCheckBox.getValue() )
				{
					getField().setValue( entries );
					allCheckBox.setDescription( LocalizationHandler.get( LabelUtil.LABEL_CHECK_NONE ) );
				} else
				{
					getField().setValue( null );
					allCheckBox.setDescription( LocalizationHandler.get( LabelUtil.LABEL_CHECK_ALL ) );
				}
            }
        });
		

		reverseCheckBox.setImmediate( true );
		reverseCheckBox.setDescription( LocalizationHandler.get( LabelUtil.LABEL_CHECK_REVERSE ) );
		reverseCheckBox.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -1039441876358191694L;

			@SuppressWarnings("rawtypes")
			@Override
            public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				allCheckBox.setValue( false );
				allCheckBox.setDescription( LocalizationHandler.get( LabelUtil.LABEL_CHECK_ALL ) );

				for( Object obj : entries )
				{
					if( ( (Collection) getField().getValue() ).contains( obj ) )
						( (OptionGroup) getField() ).unselect( obj );
					else ( (OptionGroup) getField() ).select( obj );
				}
            }
        });

		VerticalLayout vlayout = new VerticalLayout();
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent( allCheckBox );
		layout.addComponent( reverseCheckBox );
		vlayout.addComponent( layout );
		vlayout.addComponent( getField() );
		setCompositionRoot( vlayout );
	}

	@Override
	public Object getValue()
	{
		return getField().getValue();
	}

	@Override
	public void setValue( Object newValue ) throws ReadOnlyException, ConversionException
	{
		getField().setValue( newValue );
	}

	@Override
	public void setBuffered(boolean buffered) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBuffered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAllValidators() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeValueChangeListener(ValueChangeListener listener) {
		// TODO Auto-generated method stub
		
	}
}

public class OptionGroupCField extends AbstractMultiSelectCField
{

	public OptionGroupCField( FieldAttributeAccessor fieldAttributes )
	{
		super( fieldAttributes );
	}

	@Override
	public void initField()
	{
		CustomizedOptionGroup tmp = new CustomizedOptionGroup( Enum.class );
		setValueToMultiItems( tmp );
		tmp.addValueChangeListener( new Property.ValueChangeListener()
		{
			private static final long serialVersionUID = -3543051909216779572L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				// validateField();
			}
		} );

		if( getMultiItems().getValues().size() >= 5 )
			field = new ExtCustomizedOptionGroupField( tmp, getMultiItems().getValues() );
		else field = tmp;
	}
}