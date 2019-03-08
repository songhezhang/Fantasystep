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

@DomainClass(validParents = { LogicComponent.class }, label = "LABEL_DIAGNOSTIC_EVENT", icon = "diagnostic-event.ico")
public class DiagnosticEvent extends SysWDomain {

	private static final long serialVersionUID = 505011553251156129L;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.ALPHANUMERIC, label = "LABEL_DTC")
	private String dTC;

	public String getDTC() {
		return dTC;
	}

	public void setDTC(String dTC) {
		this.dTC = dTC;
	}
}
