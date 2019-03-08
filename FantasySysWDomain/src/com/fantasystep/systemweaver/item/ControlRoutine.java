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

@DomainClass(validParents = { LogicComponent.class }, label = "LABEL_CONTROL_ROUTINE", icon = "control-routine.ico")
public class ControlRoutine extends SysWDomain {

	private static final long serialVersionUID = -6991948591054366785L;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.ALPHANUMERIC, label = "LABEL_CONTROL_ROUTINE_ID")
	private String controlRoutineID;

	public String getControlRoutineID() {
		return controlRoutineID;
	}

	public void setControlRoutineID(String controlRoutineID) {
		this.controlRoutineID = controlRoutineID;
	}
}
