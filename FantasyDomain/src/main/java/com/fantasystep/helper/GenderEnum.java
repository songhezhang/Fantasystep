package com.fantasystep.helper;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.annotation.ValueOptions;

public enum GenderEnum implements ValueOptions {
	MAN("LABEL_MAN"), WOMAN("LABEL_WOMAN"), NONE("LABEL_NONE"), UNKNOWN(
			"LABEL_UNKNOWN");

	private String label;

	private GenderEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	private List<ValueOptionEntry> entries;

	public List<ValueOptionEntry> getValues() {
		if (entries == null) {
			entries = new ArrayList<ValueOptionEntry>();
			for (final GenderEnum g : GenderEnum.values()) {
				entries.add(new ValueOptionEntry() {

					public Object getValue() {
						return g;
					}

					public String getLabel() {
						return g.getLabel();
					}
				});
			}
		}
		return entries;
	}
}
