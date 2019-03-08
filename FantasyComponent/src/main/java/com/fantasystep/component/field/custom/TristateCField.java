package com.fantasystep.component.field.custom;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractCField;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.component.utils.CSSUtil;
import com.fantasystep.component.utils.LabelUtil;
import com.fantasystep.helper.Status;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class TristateCField extends AbstractCField {
	public TristateCField(FieldAttributeAccessor fieldAttributes) {
		super(fieldAttributes);
	}

	@Override
	public void initField() {
		field = new TristateField(this.fieldAttributes.getLabel());
	}
}

class TristateField extends AbstractCustomField {
	private static final long serialVersionUID = 712198603951424982L;
	private CheckBox falseCheckBox = new CheckBox();
	private Label lbl = new Label();
	private CheckBox trueCheckBox = new CheckBox();

	public TristateField(String caption) {
		super(caption, Status.class);
		trueCheckBox.setImmediate(true);
		trueCheckBox.setDescription(LocalizationHandler
				.get(LabelUtil.LABEL_GRANT));
		trueCheckBox.addValueChangeListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 169467790764661980L;

			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				falseCheckBox.setValue(false);
				getField().setValue(getValue());
			}
		});

		falseCheckBox.setImmediate(true);
		falseCheckBox.setDescription(LocalizationHandler
				.get(LabelUtil.LABEL_DENY));
		falseCheckBox
				.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 5640366865033835326L;

					@Override
					public void valueChange(
							com.vaadin.data.Property.ValueChangeEvent event) {
						trueCheckBox.setValue(false);
						getField().setValue(getValue());
					}
				});

		this.addListener(new ValueChangeListener() {

			private static final long serialVersionUID = 8159221059961072202L;

			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				setValue(getField().getValue());
				setToolTip((Status) getField().getValue());
			}

		});

		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(trueCheckBox);
		layout.addComponent(falseCheckBox);
		layout.addComponent(lbl);
		setCompositionRoot(layout);
	}

	@Override
	public Object getValue() {
		if ((Boolean) trueCheckBox.getValue())
			return Status.TRUE;

		if ((Boolean) falseCheckBox.getValue())
			return Status.FALSE;

		return Status.NONE;
	}

	private void setToolTip(Status status) {
		switch (status) {
		case TRUE:
			lbl.setValue(LocalizationHandler.get(LabelUtil.LABEL_GRANT));
			lbl.setStyleName(CSSUtil.GREEN_ITALIC_TEXT);
			break;
		case FALSE:
			lbl.setValue(LocalizationHandler.get(LabelUtil.LABEL_DENY));
			lbl.setStyleName(CSSUtil.YELLOW_ITALIC_TEXT);
			break;
		case NONE:
			lbl.setValue(LocalizationHandler.get(LabelUtil.LABEL_NONE));
			lbl.setStyleName(CSSUtil.LIGHT_GRAY_ITALIC_TEXT);
			break;

		}
	}

	@Override
	public void setValue(Object value) throws ReadOnlyException,
			ConversionException {
		getField().setValue(value);
		Status status = (Status) value;
		trueCheckBox.setValue(status == Status.TRUE);
		falseCheckBox.setValue(status == Status.FALSE);
	}
}
