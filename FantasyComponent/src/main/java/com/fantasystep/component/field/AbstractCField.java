package com.fantasystep.component.field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.exception.FieldNotInitializeException;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.helper.Validation;
import com.fantasystep.utils.ValidationUtil;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;

public abstract class AbstractCField implements CField
{
	protected Field<?>						field;
	protected FieldAttributeAccessor	fieldAttributes;
	
	private static Logger logger = LoggerFactory.getLogger(AbstractCField.class);

	public AbstractCField( FieldAttributeAccessor fieldAttributes )
	{
		this.fieldAttributes = fieldAttributes;
	}

	@Override
	public Field<?> getField()
	{
		if( field == null )
		{
			initField();
			try
			{

				if( this.fieldAttributes != null )
				{
					if( field == null )
						throw new FieldNotInitializeException( String.format("\"%s\" 's inialization is incorrect", this.getClass().getSimpleName() ) );
					field.setCaption( LocalizationHandler.get( fieldAttributes.getLabel() ) );
					field.setRequired( fieldAttributes.getRequired() && field.isEnabled() );

					switch( fieldAttributes.getValidate() )
					{
						case EMAIL:
							( (AbstractComponent) field ).setComponentError( null );
							field.addValidator( new EmailValidator( LocalizationHandler.get( fieldAttributes.getValidationErrorMessage() ) ) );
							break;
						case ALPHA:
							break;
						case ALPHANUMERIC:
							( (AbstractComponent) field ).setComponentError( null );
							field.addValidator( new RegexpValidator( ValidationUtil.getValidateRegex( Validation.ALPHANUMERIC ), LocalizationHandler.get( fieldAttributes.getValidationErrorMessage() ) ) );
							break;
						case NUMERIC:
							( (AbstractComponent) field ).setComponentError( null );
							field.addValidator( new RegexpValidator( ValidationUtil.getValidateRegex( Validation.NUMERIC ), LocalizationHandler.get( fieldAttributes.getValidationErrorMessage() ) ) );
							break;
						case CUSTOM:
							if( fieldAttributes.getCustomValidation() != null && fieldAttributes.getValidationErrorMessage() != null )
								field.addValidator( new RegexpValidator( fieldAttributes.getCustomValidation(), LocalizationHandler.get( fieldAttributes.getValidationErrorMessage() ) ) );
							break;
						default:
							break;
					}
				}
				( (AbstractComponent) field ).setImmediate( true );
				field.setErrorHandler(new ErrorHandler() {
					private static final long serialVersionUID = 6027095776462772435L;

					@Override
					public void error(ErrorEvent event) {
						logger.error(event.getThrowable().getMessage());
					}
					
				});
			} catch( FieldNotInitializeException e )
			{
				e.printStackTrace();
				return new AbstractField<Object>()
				{
					private static final long	serialVersionUID	= -6179475193839420232L;

					@Override
					public Class<?> getType()
					{
						return null;
					}
				};
			}
		}
		return field;
	}

	protected void validateField()
	{
		if( getField().isValid() )
		{
			( (AbstractComponent) getField() ).setComponentError( null );
			// rollbackStyleName();
		} else
		{
			// if( fieldAttributes.getRequired() )
			// ( (AbstractComponent) getField() ).setComponentError( new UserError( LocalizationHandler.get(
			// LabelUtil.LABEL_REQUIRED ) ) );
			// if( fieldAttributes.getCustomValidation() != null )
			// ( (AbstractComponent) getField() ).setComponentError( new UserError(
			// fieldAttributes.getValidationErrorMessage() ) );
			// setErrorStyleName();
			if( fieldAttributes.getUnique() && fieldAttributes.getValidate() == Validation.EMAIL )
				new Notification( LocalizationHandler.get( LabelUtil.LABEL_UNIQUE_VOILATE_ERROR ) ).show(Page.getCurrent());
			else
				new Notification( LocalizationHandler.get( LabelUtil.LABEL_VALIDATE_ERROR ) ).show(Page.getCurrent());
		}
	}

	// private void setErrorStyleName()
	// {
	// if( fieldAttributes.getControlType() == ControlType.RADIO || fieldAttributes.getControlType() ==
	// ControlType.DROPDOWN || fieldAttributes.getControlType() == ControlType.CHECKBOX )
	// getField().setStyleName( "error-border-small" );
	//
	// else if( fieldAttributes.getControlType() == ControlType.TEXTBOX || fieldAttributes.getControlType() ==
	// ControlType.PASSWORD || fieldAttributes.getControlType() == ControlType.CALENDAR
	// || fieldAttributes.getControlType() == ControlType.FILEUPLOAD )
	// getField().setStyleName( "error-border" );
	// }
	//
	// private void rollbackStyleName()
	// {
	// if( fieldAttributes.getControlType() == ControlType.RADIO || fieldAttributes.getControlType() ==
	// ControlType.DROPDOWN || fieldAttributes.getControlType() == ControlType.CHECKBOX )
	// getField().setStyleName( "v-select-option" );
	//
	// else if( fieldAttributes.getControlType() == ControlType.TEXTBOX || fieldAttributes.getControlType() ==
	// ControlType.PASSWORD || fieldAttributes.getControlType() == ControlType.CALENDAR
	// || fieldAttributes.getControlType() == ControlType.FILEUPLOAD )
	// getField().setStyleName( "v-textfield" );
	// }
}
