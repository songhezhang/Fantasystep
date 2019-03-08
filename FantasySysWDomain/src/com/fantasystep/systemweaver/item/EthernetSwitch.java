package com.fantasystep.systemweaver.item;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.systemweaver.SysWDomain;

@DomainClass(validParents = { EcuSw.class }, label = "LABEL_ETHERNET_SWITCH", icon = "ethernet-switch.ico")
public class EthernetSwitch extends SysWDomain {

	private static final long serialVersionUID = 7572623999989925205L;

}
