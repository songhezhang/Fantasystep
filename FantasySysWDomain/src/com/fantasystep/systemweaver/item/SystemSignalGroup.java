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

@DomainClass(validParents = { BusConnection.class, SignalMapping.class }, label = "LABEL_SYSTEM_SIGNAL_GROUP", icon = "system-signal.ico")
public class SystemSignalGroup extends SysWDomain {

	private static final long serialVersionUID = -7642876029295314181L;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_DIMENSION")
	private Integer dimension;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_END_TO_END_ID")
	private Integer endToEndID;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	@FantasyView(controlType = ControlType.CHECKBOX, label = "LABEL_PROTECT_END_TO_END")
	private Boolean protectEndToEnd;

	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}

	public Integer getEndToEndID() {
		return endToEndID;
	}

	public void setEndToEndID(Integer endToEndID) {
		this.endToEndID = endToEndID;
	}

	public Boolean getProtectEndToEnd() {
		return protectEndToEnd;
	}

	public void setProtectEndToEnd(Boolean protectEndToEnd) {
		this.protectEndToEnd = protectEndToEnd;
	}
}
