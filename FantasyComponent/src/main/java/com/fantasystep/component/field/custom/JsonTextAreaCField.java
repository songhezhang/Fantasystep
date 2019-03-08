package com.fantasystep.component.field.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fantasystep.annotation.FieldAttributeAccessor;
import com.fantasystep.component.field.AbstractCField;
import com.fantasystep.utils.JSON2NodeUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.TextArea;

public class JsonTextAreaCField extends AbstractCField {

	private Class<?> type;
	public JsonTextAreaCField(FieldAttributeAccessor fieldAttributes, Class<?> type) {
		super(fieldAttributes);
		this.type = type;
	}

	@Override
	public void initField() {
		field = new JsonTextAreaField(this.fieldAttributes.getLabel(), type);
	}

	public class JsonTextAreaField extends AbstractCustomField {
		private static final long serialVersionUID = -3266913937790142950L;

		private TextArea textfield;
		public JsonTextAreaField(String caption, Class<?> type) {
			super(caption, type);
			setComponentLayout();
		}

		@Override
		public Object getValue() {
			try {
				if(getField().getType().equals(Map.class))
					return new ObjectMapper().readValue(textfield.getValue(), HashMap.class);
				if(getField().getType().equals(List.class))
					return new ObjectMapper().readValue(textfield.getValue(), ArrayList.class);
				if(getField().getType().equals(Set.class))
					return new ObjectMapper().readValue(textfield.getValue(), HashSet.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return textfield.getValue();
		}

		private void setComponentLayout() {
			textfield = new TextArea();
			textfield.addValueChangeListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = -6596268323085037127L;

				@Override
				public void valueChange(
						com.vaadin.data.Property.ValueChangeEvent event) {
					if (((TextArea) textfield).getNullRepresentation().equals(
							"null"))
						((TextArea) textfield).setNullRepresentation("");
					else
						validateField();
					getField().setValue(getValue());
				}
			});
			textfield.setWidth("700px");
			textfield.setHeight("220px");

			setCompositionRoot(textfield);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void setValue(Object newValue) throws ReadOnlyException,
				ConversionException {
			getField().setValue(newValue);
			if((Map.class.isAssignableFrom(newValue.getClass()) && ((Map)newValue).isEmpty()) ) {
				JSONObject json = new JSONObject(fieldAttributes.getJsonTemplate());
				textfield.setValue(json.toString(4).replace("*", ""));
			} else if ((List.class.isAssignableFrom(newValue.getClass()) && ((List)newValue).isEmpty()) ||
					(Set.class.isAssignableFrom(newValue.getClass()) && ((Set)newValue).isEmpty())) {
				JSONArray json = new JSONArray(fieldAttributes.getJsonTemplate());
				textfield.setValue(json.toString(4).replace("*", ""));
			} else textfield.setValue(JSON2NodeUtil.object2Json(newValue));
		}
	}
}
