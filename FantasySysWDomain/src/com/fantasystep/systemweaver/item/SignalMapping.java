package com.fantasystep.systemweaver.item;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.systemweaver.SysWDomain;

@DomainClass(validParents = { BusConnection.class }, label = "LABEL_SIGNAL_MAPPING", icon = "signal-mapping.ico")
public class SignalMapping extends SysWDomain {

	private static final long serialVersionUID = 5182238809273813912L;

}
