package com.fantasystep.component.field.custom;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractCField;
import com.fantasystep.component.utils.IconUtil;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class LabelCField extends AbstractCField {
	public LabelCField(FieldAttributeAccessor fieldAttributes) {
		super(fieldAttributes);
	}

	@Override
	public void initField() {
		field = new LabelField((this.fieldAttributes == null) ? null
				: this.fieldAttributes.getLabel());
	}
}

class LabelField extends AbstractCustomField {
	
	private static final long serialVersionUID = 709374797531226244L;
	
	private Label lblField = new Label();

	public LabelField(String caption) {
		super(caption, String.class);
		lblField.setImmediate(true);
		setCompositionRoot(lblField);
	}

	@Override
	public Object getValue() {
		return lblField.getValue();
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException,
			ConversionException {
		if (newValue != null && newValue.getClass().equals(Boolean.class)) {
			String imgSrc;
			if ((Boolean) newValue)
				imgSrc = IconUtil.getRawIcon(IconUtil.ICON_ACTIVATE,
						IconUtil.SMALL_ICON_SIZE);
			else
				imgSrc = IconUtil.getRawIcon(IconUtil.ICON_DEACTIVATE,
						IconUtil.SMALL_ICON_SIZE);
			lblField.setContentMode(ContentMode.HTML);
			lblField.setValue(imgSrc);
		} else {
			if(newValue == null)
				lblField.setValue("Missing Label.");
			else
				lblField.setValue(newValue.toString());
		}
	}
}