package com.fantasystep.component.field.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
				System.out.println("getValue: " + textfield.getValue());
				if(Map.class.isAssignableFrom(getField().getType()))
					return new ObjectMapper().readValue(textfield.getValue(), LinkedHashMap.class);
				if(List.class.isAssignableFrom(getField().getType()))
					return new ObjectMapper().readValue(textfield.getValue(), ArrayList.class);
				if(Set.class.isAssignableFrom(getField().getType()))
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
				String template = fieldAttributes.getJsonTemplate() == null ? "{}" : fieldAttributes.getJsonTemplate(); 
				JSONObject json = new JSONObject(template);
				textfield.setValue(json.toString(4).replace("*", ""));
			} else if ((List.class.isAssignableFrom(newValue.getClass()) && ((List)newValue).isEmpty()) ||
					(Set.class.isAssignableFrom(newValue.getClass()) && ((Set)newValue).isEmpty())) {
				String template = fieldAttributes.getJsonTemplate() == null ? "[]" : fieldAttributes.getJsonTemplate();
				JSONArray json = new JSONArray(template);
				textfield.setValue(json.toString(4).replace("*", ""));
			} else textfield.setValue(JSON2NodeUtil.object2Json(newValue));
		}
	}
	
//	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
//		String a = "{\"home\":{\"left\":\"-1700px\",\"top\":\"-2100px\",\"rotate\":\"-30deg\",\"scale\":\"1.0\"},\"1-2-1\":{\"animation\":{\"left\":\"-100px\",\"top\":\"20px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":10},\"1-5-2\":{\"animation\":{\"left\":\"-3500px\",\"top\":\"-800px\",\"rotate\":\"90deg\",\"scale\":\"1.0\"},\"captionIndex\":16},\"1-9-1\":{\"animation\":{\"left\":\"-1400px\",\"top\":\"0px\",\"rotate\":\"-30deg\",\"scale\":\"1.0\"},\"captionIndex\":6},\"1-13-2\":{\"animation\":{\"left\":\"-3500px\",\"top\":\"-3500px\",\"rotate\":\"90deg\",\"scale\":\"1.0\"},\"captionIndex\":4},\"2-1-4\":{\"animation\":{\"left\":\"-900px\",\"top\":\"-3800px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":1},\"2-7-2\":{\"animation\":{\"left\":\"-2900px\",\"top\":\"-1500px\",\"rotate\":\"90deg\",\"scale\":\"1.0\"},\"captionIndex\":7},\"3-3-4\":{\"animation\":{\"left\":\"-1500px\",\"top\":\"-3150px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":9},\"3-9-2\":{\"animation\":{\"left\":\"-2200px\",\"top\":\"-2150px\",\"rotate\":\"90deg\",\"scale\":\"1.0\"},\"captionIndex\":8},\"3-13-1\":{\"animation\":{\"left\":\"-3700px\",\"top\":\"-1300px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":5},\"4-1-1\":{\"animation\":{\"left\":\"300px\",\"top\":\"-1950px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":3},\"4-5-4\":{\"animation\":{\"left\":\"-1700px\",\"top\":\"-2100px\",\"rotate\":\"-30deg\",\"scale\":\"1.0\"},\"captionIndex\":-1},\"4-11-2\":{\"animation\":{\"left\":\"-2100px\",\"top\":\"-2700px\",\"rotate\":\"60deg\",\"scale\":\"1.0\"},\"captionIndex\":14},\"4-13-4\":{\"animation\":{\"left\":\"-4050px\",\"top\":\"-1850px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":11},\"5-6-4\":{\"animation\":{\"left\":\"-2900px\",\"top\":\"-2110px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":2},\"6-1-4\":{\"animation\":{\"left\":\"-3500px\",\"top\":\"-3800px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":17},\"6-3-1\":{\"animation\":{\"left\":\"-450px\",\"top\":\"-3285px\",\"rotate\":\"0deg\",\"scale\":\"1.0\"},\"captionIndex\":13},\"6-7-4\":{\"animation\":{\"left\":\"-3500px\",\"top\":\"-1760px\",\"rotate\":\"-90deg\",\"scale\":\"1.0\"},\"captionIndex\":12},\"5-9-1\":{\"animation\":{\"left\":\"-2700px\",\"top\":\"-2350px\",\"rotate\":\"-30deg\",\"scale\":\"1.0\"},\"captionIndex\":15}}";
//		LinkedHashMap b = new ObjectMapper().readValue(a, LinkedHashMap.class);
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.writer(SerializationFeature.INDENT_OUTPUT);
//		
//		Layout layout = new Layout();
//		layout.setLinkJson(b);
//		Node node = NodeClassUtil.getSerializationNode(layout);
//
//		StringWriter sw = new StringWriter();
//		try {
//			objectMapper.writeValue(sw, b);
//			System.out.println(sw.getBuffer().toString());
//		} catch (JsonGenerationException e) {
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
////		System.out.println(b);
//	}
}
