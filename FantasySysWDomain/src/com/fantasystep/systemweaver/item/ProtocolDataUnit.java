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

@DomainClass(validParents = { Frame.class }, label = "LABEL_PROTOCOL_DATA_UNIT", icon = "protocol-data-unit.ico")
public class ProtocolDataUnit extends SysWDomain {

	private static final long serialVersionUID = 7310369332864769662L;

	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = Triggering.class, valueOptions = Triggering.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_TRIGGERING")
	private Triggering triggering;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_CYCLE_TIME")
	private Integer cycleTimems;

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
