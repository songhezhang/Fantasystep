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

@DomainClass(validParents = { DiagnosticEvent.class, DataIdentifier.class, DataInMemory.class, DesignSignalGroup.class }, label = "LABEL_ENUMERATION_DESIGN_SIGNAL", icon = "enumeration-design-signal.ico")
public class EnumerationDesignSignal extends SysWDomain {

	private static final long serialVersionUID = -1198758043867433946L;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_DIMENSION")
	private Integer dimension;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_INITIAL_VALUE")
	private String initialValue;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	@FantasyView(controlType = ControlType.CHECKBOX, label = "LABEL_HOSTLOG")
	private Boolean hostLog;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTAREA, validate = Validation.ALPHANUMERIC, label = "LABEL_DESCRIPTION_ATTRIBUTE")
	private String descriptionAttribute;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = SDBType.class, valueOptions = SDBType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_SDB_TYPE")
	private SDBType sDBType;

	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}

	public String getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	public Boolean getHostLog() {
		return hostLog;
	}

	public void setHostLog(Boolean hostLog) {
		this.hostLog = hostLog;
	}

	public String getDescriptionAttribute() {
		return descriptionAttribute;
	}

	public void setDescriptionAttribute(String descriptionAttribute) {
		this.descriptionAttribute = descriptionAttribute;
	}

	public SDBType getsDBType() {
		return sDBType;
	}

	public void setsDBType(SDBType sDBType) {
		this.sDBType = sDBType;
	}
}
