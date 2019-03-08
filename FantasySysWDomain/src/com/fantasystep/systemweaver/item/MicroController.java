package com.fantasystep.systemweaver.item;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.systemweaver.SysWDomain;
import com.fantasystep.systemweaver.annotation.SystemWeaver;
import com.fantasystep.systemweaver.itemenum.OperatingSystem;

@DomainClass(validParents = { EcuSw.class }, label = "LABEL_MICRO_CONTROLLER", icon = "microcontroller.ico")
public class MicroController extends SysWDomain {

	private static final long serialVersionUID = -8038543309642613564L;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = OperatingSystem.class, valueOptions = OperatingSystem.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_OPERATING_SYSTEM")
	private OperatingSystem operatingSystem;

	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(OperatingSystem operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
}
