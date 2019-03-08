package com.fantasystep.systemweaver.itemenum;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.annotation.ValueOptions;

public enum ASIL implements ValueOptions {
	QM("QM"), A("A"), B("B"), C("C"), D("D"), NOTAPPLICABLE("Not Applicable"),
	QMA("QM(A)"), QMB("QM(B)"), QMC("QM(C)"), QMD("QM(D)"), AB("A(B)"), AC("A(C)"), AD("A(D)"), BC("B(C)"), BD("B(D)"), CD("C(D)");

	private String label;

	private ASIL(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	private List<ValueOptionEntry> entries;

	public List<ValueOptionEntry> getValues() {
		if (entries == null) {
			entries = new ArrayList<ValueOptionEntry>();
			for (final ASIL g : ASIL.values()) {
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
