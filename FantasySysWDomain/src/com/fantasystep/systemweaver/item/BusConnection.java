package com.fantasystep.systemweaver.item;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.systemweaver.SysWDomain;

@DomainClass(validParents = { MicroControllerCoreVirtualMachine.class }, label = "LABEL_BUS_CONNECTION", icon = "bus-connection.ico")
public class BusConnection extends SysWDomain {

	private static final long serialVersionUID = 1645460655698566384L;

}
