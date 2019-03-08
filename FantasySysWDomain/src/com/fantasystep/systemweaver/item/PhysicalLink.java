package com.fantasystep.systemweaver.item;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.helper.Validation;
import com.fantasystep.systemweaver.SysWDomain;
import com.fantasystep.systemweaver.annotation.SystemWeaver;

@DomainClass(validParents = { EthernetSwitch.class, BusController.class }, label = "LABEL_PHYSICAL_LINK", icon = "physical-link.ico")
public class PhysicalLink extends SysWDomain {

	private static final long serialVersionUID = 1759087087431551146L;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_PHYSICAL_PORT_NUMBER")
	private Integer physicalPortNumber;

	public Integer getPhysicalPortNumber() {
		return physicalPortNumber;
	}

	public void setPhysicalPortNumber(Integer physicalPortNumber) {
		this.physicalPortNumber = physicalPortNumber;
	}
}
