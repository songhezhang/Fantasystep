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

@DomainClass(validParents = { ControlRoutine.class }, label = "LABEL_ROUTINE_OPERATION", icon = "control-routine.ico")
public class RoutineOperation extends SysWDomain {

	private static final long serialVersionUID = -1861272517766963670L;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_REQUEST_SIZE")
	private Integer requestSize;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_RESPONSE_SIZE")
	private Integer responseSize;

	public Integer getRequestSize() {
		return requestSize;
	}

	public void setRequestSize(Integer requestSize) {
		this.requestSize = requestSize;
	}

	public Integer getResponseSize() {
		return responseSize;
	}

	public void setResponseSize(Integer responseSize) {
		this.responseSize = responseSize;
	}
}
