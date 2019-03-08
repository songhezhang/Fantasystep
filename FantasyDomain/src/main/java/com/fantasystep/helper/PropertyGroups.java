package com.fantasystep.helper;

public enum PropertyGroups {
	BASE_PROPERTY("base_property"), ADVANCED_PROPERTY("advanced_property");
	private String label;

	PropertyGroups(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
