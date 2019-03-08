package com.fantasystep.component.field.custom;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractCField;
import com.fantasystep.component.utils.DocumentationUtil;
import com.fantasystep.component.utils.IconUtil;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

public class DocumentedCField extends AbstractCField
{

	private class DocumentedField extends AbstractCustomField
	{

		private static final long	serialVersionUID	= -8626095571168431670L;

		public DocumentedField( Field<Object> field, final String documentationReference )
		{
			super( field.getCaption(), field.getType() );
			setField( field );
			getField().setCaption( null );

			PopupView popup = new PopupView( new PopupView.Content()
			{

				private static final long	serialVersionUID	= -1796397484813429127L;

				@Override
				public String getMinimizedValueAsHTML()
				{
					return IconUtil.getRawIcon( "help.png", 16 );
				}

				@Override
				public Component getPopupComponent()
				{
					Label content = new Label( DocumentationUtil.getDocumentation( documentationReference ) );
					content.setContentMode( ContentMode.HTML );
					content.setWidth( "300px" );
					return content;
				}
			} );

			addListener( new ValueChangeListener()
			{
				private static final long	serialVersionUID	= 8159221059961072202L;

				@Override
				public void valueChange( com.vaadin.data.Property.ValueChangeEvent event )
				{
					setValue( getField().getValue() );
				}
			} );

			HorizontalLayout layout = new HorizontalLayout();
			layout.addComponent( getField() );
			layout.addComponent( popup );
			setCompositionRoot( layout );

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
	}

	private String						explanation;
	private Field<Object>				innerField;

	public DocumentedCField( FieldAttributeAccessor fieldAttributes, Field<Object> innerField, String explanation )
	{
		super( fieldAttributes );
		this.innerField = innerField;
		this.explanation = explanation;
	}

	@Override
	public void initField()
	{
		field = new DocumentedField( innerField, explanation );
	}

}
