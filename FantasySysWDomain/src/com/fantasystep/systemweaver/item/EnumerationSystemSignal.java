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
import com.fantasystep.systemweaver.itemenum.SDBType;

@DomainClass(validParents = { BusConnection.class, SignalMapping.class }, label = "LABEL_ENUMERATION_SYSTEM_SIGNAL", icon = "system-signal.ico")
public class EnumerationSystemSignal extends SysWDomain {

	private static final long serialVersionUID = -2893643295725265927L;
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.DECIMAL)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_INITIAL_VALUE")
	private String initialValue;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTAREA, label = "LABEL_DESCRIPTION_ATTRIBUTE")
	private String descriptionAttribute;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = SDBType.class, valueOptions = SDBType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_SDB_TYPE")
	private SDBType sDBType;

	public String getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	public String getDescriptionAttribute() {
		return descriptionAttribute;
	}

	public void setDescriptionAttribute(String descriptionAttribute) {
		this.descriptionAttribute = descriptionAttribute;
	}

	public SDBType getSDBType() {
		return sDBType;
	}

	public void setSDBType(SDBType sDBType) {
		this.sDBType = sDBType;
	}
}
