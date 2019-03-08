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
import com.fantasystep.systemweaver.itemenum.Triggering;

@DomainClass(validParents = { Socket.class }, label = "LABEL_FRAME", icon = "frame.ico")
public class Frame extends SysWDomain {

	private static final long serialVersionUID = -142088505035399272L;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_CYCLE_TIME")
	private Integer cycleTimems;

	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = Triggering.class, valueOptions = Triggering.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_TRIGGERING")
	private Triggering triggering;

	public Triggering getTriggering() {
		return triggering;
	}

	public void setTriggering(Triggering triggering) {
		this.triggering = triggering;
	}

	public Integer getCycleTimems() {
		return cycleTimems;
	}

	public void setCycleTimems(Integer cycleTimems) {
		this.cycleTimems = cycleTimems;
	}
	
}
