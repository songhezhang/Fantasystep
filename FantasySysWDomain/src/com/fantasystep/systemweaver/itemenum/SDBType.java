package com.fantasystep.systemweaver.itemenum;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.annotation.ValueOptions;

public enum SDBType implements ValueOptions {
	UINT1("uint1"), 
	UINT2("uint2"), 
	UINT3("uint3"), 
	UINT4("uint4"), 
	UINT5("uint5"), 
	UINT6("uint6"), 
	UINT7("uint7"), 
	UINT8("uint8"), 
	UINT9("uint9"), 
	UINT10("uint10"), 
	UINT11("uint11"), 
	UINT12("uint12"), 
	UINT13("uint13"), 
	UINT14("uint14"), 
	UINT15("uint15"), 
	UINT16("uint16"), 
	UINT17("uint17"), 
	UINT18("uint18"), 
	UINT19("uint19"), 
	UINT20("uint20"), 
	UINT21("uint21"), 
	UINT22("uint22"), 
	UINT23("uint23"), 
	UINT24("uint24"), 
	UINT25("uint25"), 
	UINT26("uint26"), 
	UINT27("uint27"), 
	UINT28("uint28"), 
	UINT29("uint29"), 
	UINT30("uint30"), 
	UINT31("uint31"), 
	UINT32("uint32"), 
	UINT33("uint33"), 
	UINT34("uint34"), 
	UINT35("uint35"), 
	UINT36("uint36"), 
	UINT37("uint37"), 
	UINT38("uint38"), 
	UINT39("uint39"), 
	UINT40("uint40"), 
	UINT41("uint41"), 
	UINT42("uint42"), 
	UINT43("uint43"), 
	UINT44("uint44"), 
	UINT45("uint45"), 
	UINT46("uint46"), 
	UINT47("uint47"), 
	UINT48("uint48"), 
	UINT49("uint49"), 
	UINT50("uint50"), 
	UINT51("uint51"), 
	UINT52("uint52"), 
	UINT53("uint53"), 
	UINT54("uint54"), 
	UINT55("uint55"), 
	UINT56("uint56"), 
	UINT57("uint57"), 
	UINT58("uint58"), 
	UINT59("uint59"), 
	UINT60("uint60"), 
	UINT61("uint61"), 
	UINT62("uint62"), 
	UINT63("uint63"), 
	UINT64("uint64"), 
	INT1("int1"), 
	INT2("int2"), 
	INT3("int3"), 
	INT4("int4"), 
	INT5("int5"), 
	INT6("int6"), 
	INT7("int7"), 
	INT8("int8"), 
	INT9("int9"), 
	INT10("int10"), 
	INT11("int11"), 
	INT12("int12"), 
	INT13("int13"), 
	INT14("int14"), 
	INT15("int15"), 
	INT16("int16"), 
	INT17("int17"), 
	INT18("int18"), 
	INT19("int19"), 
	INT20("int20"), 
	INT21("int21"), 
	INT22("int22"), 
	INT23("int23"), 
	INT24("int24"), 
	INT25("int25"), 
	INT26("int26"), 
	INT27("int27"), 
	INT28("int28"), 
	INT29("int29"), 
	INT30("int30"), 
	INT31("int31"), 
	INT32("int32"), 
	INT33("int33"), 
	INT34("int34"), 
	INT35("int35"), 
	INT36("int36"), 
	INT37("int37"), 
	INT38("int38"), 
	INT39("int39"), 
	INT40("int40"), 
	INT41("int41"), 
	INT42("int42"), 
	INT43("int43"), 
	INT44("int44"), 
	INT45("int45"), 
	INT46("int46"), 
	INT47("int47"), 
	INT48("int48"), 
	INT49("int49"), 
	INT50("int50"), 
	INT51("int51"), 
	INT52("int52"), 
	INT53("int53"), 
	INT54("int54"), 
	INT55("int55"), 
	INT56("int56"), 
	INT57("int57"), 
	INT58("int58"), 
	INT59("int59"), 
	INT60("int60"), 
	INT61("int61"), 
	INT62("int62"), 
	INT63("int63"), 
	INT64("int64");
	private String label;

	private SDBType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	private List<ValueOptionEntry> entries;

	public List<ValueOptionEntry> getValues() {
		if (entries == null) {
			entries = new ArrayList<ValueOptionEntry>();
			for (final SDBType g : SDBType.values()) {
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
