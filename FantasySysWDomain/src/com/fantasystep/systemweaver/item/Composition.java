package com.fantasystep.systemweaver.item;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.systemweaver.SysWDomain;
import com.fantasystep.systemweaver.annotation.SystemWeaver;
import com.fantasystep.systemweaver.itemenum.ComponentType;

@DomainClass(validParents = { MicroControllerCoreVirtualMachine.class }, label = "LABEL_COMPOSITION", icon = "composition.ico")
public class Composition extends SysWDomain {

	private static final long serialVersionUID = 6874945419593743747L;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_CYCLE_TIME")
	private Integer cycleTime;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = ComponentType.class, valueOptions = ComponentType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_COMPONENT_TYPE")
	private ComponentType componentType;

	public Integer getCycleTime() {
		return cycleTime;
	}

	public void setCycleTime(Integer cycleTime) {
		this.cycleTime = cycleTime;
	}

	public ComponentType getComponentType() {
		return componentType;
	}

	public void setComponentType(ComponentType componentType) {
		this.componentType = componentType;
	}
}
