package com.fantasystep.systemweaver.item;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.systemweaver.SysWDomain;

@DomainClass(validParents = { BusConnection.class, SignalMapping.class }, label = "LABEL_BYTE_ARRAY_SYSTEM_SIGNAL", icon = "system-signal.ico")
public class ByteArraySystemSignal extends SysWDomain {

	private static final long serialVersionUID = -6060156816553094895L;

}
