package com.fantasystep.component.field.custom;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractCField;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.LabelUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

public class DateOfBirthCField extends AbstractCField
{
	public DateOfBirthCField( FieldAttributeAccessor fieldAttributes )
	{
		super( fieldAttributes );
	}

	@Override
	public void initField()
	{
		field = new DateOfBirthField( this.fieldAttributes.getLabel() );
	}
}

class DateOfBirthField extends AbstractCustomField
{
	private enum Month
	{
		January( "LABEL_JANUARY", 31 ), February( "LABEL_FEBRUARY", 28 ), March( "LABEL_MARCH", 31 ), April( "LABEL_APRIL", 30 ), May( "LABEL_MAY", 31 ), June( "LABEL_JUNE", 30 ), July( "LABEL_JULY",
				31 ), August( "LABEL_AUGUST", 31 ), September( "LABEL_SEPTEMBER", 30 ), October( "LABEL_OCTOBER", 31 ), November( "LABEL_NOVEMBER", 30 ), December( "LABEL_DECEMBER", 31 );

		private int		days;
		private String	label;

		private Month( String label, int days )
		{
			this.label = label;
			this.days = days;
		}

		private int getDaysLength()
		{
			return days;
		}

		private String getLabel()
		{
			return label;
		}
	}

	private static final long	serialVersionUID	= 1L;
	private GregorianCalendar	cal					= new GregorianCalendar();
	private NativeSelect		daysBox				= new NativeSelect();
	private NativeSelect		monthsBox			= new NativeSelect();
	private NativeSelect		yearsBox			= new NativeSelect();

	public DateOfBirthField( String caption )
	{
		super( caption, Date.class );
		buildYearsCombo();
		buildMonthsCombo();
		buildDaysCombo( 31 );

		monthsBox.addValueChangeListener( new ValueChangeListener()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event )
			{
				if( isValidMonth() )
				{
					resetDays();
					getField().setValue( getSelectedDate() );
				}
			}

		} );

		yearsBox.addValueChangeListener( new ValueChangeListener()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event )
			{
				if( isValidMonth() ) // is month selected then need to reset the days
				{
					resetDays();
					getField().setValue( getSelectedDate() );
				}
			}
		} );

		daysBox.addValueChangeListener( new ValueChangeListener()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void valueChange( com.vaadin.data.Property.ValueChangeEvent event )
			{
				if( daysBox.getValue() != null && !daysBox.getValue().equals( LocalizationHandler.get( LabelUtil.LABEL_DAY ) ) )
					if( isValid() )
						getField().setValue( getSelectedDate() );
			}
		} );

		monthsBox.setImmediate( true );
		yearsBox.setImmediate( true );
		daysBox.setImmediate( true );

		monthsBox.setNullSelectionAllowed( false );
		yearsBox.setNullSelectionAllowed( false );
		daysBox.setNullSelectionAllowed( false );

		monthsBox.setWidth( "80px" );
		yearsBox.setWidth( "60px" );
		daysBox.setWidth( "58px" );

		HorizontalLayout hz = new HorizontalLayout();
		hz.addComponent( yearsBox );
		hz.addComponent( new Label( "&nbsp;", ContentMode.HTML ) );
		hz.addComponent( monthsBox );
		hz.addComponent( new Label( "&nbsp;", ContentMode.HTML ) );
		hz.addComponent( daysBox );
		setCompositionRoot( hz );
	}

	private void buildDaysCombo( int daysCount )
	{
		Integer prevSelVal = ( daysBox.getValue() instanceof Integer ) ? Integer.parseInt( daysBox.getValue().toString() ) : null;

		daysBox.removeAllItems();
		daysBox.addItem( LocalizationHandler.get( LabelUtil.LABEL_DAY ) );

		for( int d = 1; d <= daysCount; d++ )
			daysBox.addItem( d );

		if( prevSelVal != null )
			daysBox.setValue( ( prevSelVal > daysCount ) ? daysCount : prevSelVal ); // select last
		// index
		else
		{
			Collection<?> itemIds = daysBox.getItemIds();
			daysBox.setValue( itemIds.iterator().next() );
		}
	}

	private void buildMonthsCombo()
	{
		monthsBox.addItem( LocalizationHandler.get( LabelUtil.LABEL_MONTH ) );
		for( Month m : Month.values() )
		{
			Item it = monthsBox.addItem( m );
			monthsBox.setItemCaption( it, LocalizationHandler.get( m.getLabel() ) );
		}

		Collection<?> itemIds = monthsBox.getItemIds();
		monthsBox.setValue( itemIds.iterator().next() );
	}

	private void buildYearsCombo()
	{
		yearsBox.addItem( LocalizationHandler.get( LabelUtil.LABEL_YEAR ) );
		for( int y = cal.get( Calendar.YEAR ); y >= 1901; y-- )
			yearsBox.addItem( y );

		Collection<?> itemIds = yearsBox.getItemIds();
		yearsBox.setValue( itemIds.iterator().next() );
	}

	private Date getSelectedDate()
	{
		if( isValidMonth() && isValidYear() && isValidDay() )
		{
			List<Month> months = Arrays.asList( Month.values() );
			cal.set( Integer.parseInt( yearsBox.getValue().toString() ), months.indexOf( monthsBox.getValue() ), Integer.parseInt( daysBox.getValue().toString() ) );
			return cal.getTime();
		} else return null;
	}

	@Override
	public Object getValue()
	{
		return getField().getValue();
	}

	@Override
	public boolean isValid()
	{
		if( isValidMonth() && isValidYear() && isValidDay() )
			return true;

		// if not required, and all fields are not selected
		if( !isRequired() && !isValidMonth() && !isValidYear() && !isValidDay() )
			return true;

		else return false;
	}

	private boolean isValidDay()
	{
		return !( daysBox.getValue() == null || daysBox.getValue().equals( LocalizationHandler.get( LabelUtil.LABEL_DAY ) ) );
	}

	private boolean isValidMonth()
	{
		return !monthsBox.getValue().equals( LocalizationHandler.get( LabelUtil.LABEL_MONTH ) );
	}

	private boolean isValidYear()
	{
		return !yearsBox.getValue().equals( LocalizationHandler.get( LabelUtil.LABEL_YEAR ) );
	}

	private void resetDays()
	{
		if( !isValidMonth() )
			return; // we don't need to reset the days

		Month m = (Month) monthsBox.getValue();

		if( isValidYear() )
		{
			if( cal.isLeapYear( Integer.parseInt( yearsBox.getValue().toString() ) ) && m.equals( Month.February ) )
			{
				buildDaysCombo( 29 );
				return;
			}
		}

		buildDaysCombo( m.getDaysLength() );
	}

	@Override
	public void setValue( Object newValue ) throws ReadOnlyException, ConversionException
	{
		if( newValue instanceof Date )
		{
			Date date = (Date) newValue;
			yearsBox.setValue( Integer.parseInt( new SimpleDateFormat( "yyyy" ).format( date ) ) );
			monthsBox.setValue( Month.values()[Integer.parseInt( new SimpleDateFormat( "M" ).format( date ) ) - 1] );
			daysBox.setValue( Integer.parseInt( new SimpleDateFormat( "d" ).format( date ) ) );

			getField().setValue( date );
		}
	}
}
