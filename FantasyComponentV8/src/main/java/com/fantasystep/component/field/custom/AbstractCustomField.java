package com.fantasystep.component.field.custom;

import java.util.Collection;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.CustomComponent;
import com.vaadin.v7.ui.Field;

@SuppressWarnings("rawtypes")
abstract public class AbstractCustomField extends CustomComponent implements Field
{
	private static final long	serialVersionUID	= -2454519961753841376L;
	private Field<Object>				field;

	@SuppressWarnings("unchecked")
	public AbstractCustomField( String caption, final Class<?> clazz )
	{
		setCaption( caption );
		field = new com.vaadin.v7.ui.AbstractField()
		{
			private static final long	serialVersionUID	= 2922573654420570071L;

			@Override
			public Class getType()
			{
				return clazz;
			}
		};
	}

	@Override
	public void addListener( ValueChangeListener listener )
	{
		field.addValueChangeListener( listener );
	}

	@Override
	public void addValidator( Validator validator )
	{
		field.addValidator( validator );
	}

	@Override
	public void commit() throws SourceException, InvalidValueException
	{
		field.commit();
	}

	@Override
	public void discard() throws SourceException
	{
		field.discard();
	}

	@Override
	public void focus()
	{
		field.focus();
	}

	protected Field<Object> getField()
	{
		return field;
	}

	@Override
	public Property getPropertyDataSource()
	{
		return field.getPropertyDataSource();
	}

	@Override
	public String getRequiredError()
	{
		return field.getRequiredError();
	}

	@Override
	public int getTabIndex()
	{
		return field.getTabIndex();
	}

	@Override
	public Class<?> getType()
	{
		return null;
	}

	@Override
	public Collection<Validator> getValidators()
	{
		return field.getValidators();
	}

	@Override
	public abstract Object getValue();

	@Override
	public boolean isInvalidAllowed()
	{
		return field.isInvalidAllowed();
	}

	@Override
	public boolean isInvalidCommitted()
	{
		return field.isInvalidCommitted();
	}

	@Override
	public boolean isModified()
	{
		return field.isModified();
	}

	@Override
	public boolean isRequired()
	{
		return field.isRequired();
	}

	@Override
	public boolean isValid()
	{
		return field.isValid();
	}

	@Override
	public void removeListener( ValueChangeListener listener )
	{
		field.removeValueChangeListener( listener );
	}

	@Override
	public void removeValidator( Validator validator )
	{
		field.removeValidator( validator );
	}

	public void setField( Field<Object> field )
	{
		this.field = field;
	}

	@Override
	public void setInvalidAllowed( boolean invalidValueAllowed ) throws UnsupportedOperationException
	{
		field.setInvalidAllowed( invalidValueAllowed );
	}

	@Override
	public void setInvalidCommitted( boolean isCommitted )
	{
		field.setInvalidCommitted( isCommitted );
	}

	@Override
	public void setPropertyDataSource( Property newDataSource )
	{
		field.setPropertyDataSource( newDataSource );
	}

	@Override
	public void setRequired( boolean required )
	{
		field.setRequired( required );
	}

	@Override
	public void setRequiredError( String requiredMessage )
	{
		field.setRequiredError( requiredMessage );
	}

	@Override
	public void setTabIndex( int tabIndex )
	{
		field.setTabIndex( tabIndex );
	}

	@Override
	public abstract void setValue( Object newValue ) throws ReadOnlyException, ConversionException;

	@Override
	public void validate() throws InvalidValueException
	{
		field.validate();
	}

	@Override
	public void valueChange( com.vaadin.v7.data.Property.ValueChangeEvent event )
	{
		field.valueChange( event );
	}

	@Override
	public void setBuffered(boolean buffered) {
		field.setBuffered(buffered);
	}

	@Override
	public boolean isBuffered() {
		return field.isBuffered();
	}

	@Override
	public void removeAllValidators() {
		field.removeAllValidators();
	}

	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		field.addValueChangeListener(listener);
	}

	@Override
	public void removeValueChangeListener(ValueChangeListener listener) {
		field.removeValueChangeListener(listener);
	}


	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private boolean readOnly = false;
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}
