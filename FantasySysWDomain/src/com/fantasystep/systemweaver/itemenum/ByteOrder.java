package com.fantasystep.systemweaver.itemenum;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.annotation.ValueOptions;

public enum ByteOrder implements ValueOptions {
	MSBFIRST("MSBFirst"), MSBLAST("MSBLast"), OPAQUE("opaque");

	private String label;

	private ByteOrder(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	private List<ValueOptionEntry> entries;

	public List<ValueOptionEntry> getValues() {
		if (entries == null) {
			entries = new ArrayList<ValueOptionEntry>();
			for (final ByteOrder g : ByteOrder.values()) {
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
