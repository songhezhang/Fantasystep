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
import com.fantasystep.systemweaver.itemenum.FloatType;

@DomainClass(validParents = { DiagnosticEvent.class, DataIdentifier.class, DataInMemory.class, DesignSignalGroup.class }, label = "LABEL_FLOAT_DESIGN_SIGNAL", icon = "float-design-signal.ico")
public class FloatDesignSignal extends SysWDomain {

	private static final long serialVersionUID = -7889504908509933856L;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_DIMENSION")
	private Integer dimension;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.DECIMAL)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_INITIAL_VALUE")
	private Float initialValue;

	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = FloatType.class, valueOptions = FloatType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_FLOAT_TYPE")
	private FloatType floatType;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.DECIMAL)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_MAX")
	private Float max;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.DECIMAL)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_MIN")
	private Float min;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.DECIMAL)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_OFFSET")
	private Float offset;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.DECIMAL)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_SCALING_FACTOR")
	private Float scalingFactor;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	@FantasyView(controlType = ControlType.CHECKBOX, label = "LABEL_PROTECT_END_TO_END")
	private Boolean protectEndToEnd;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_UNIT")
	private String unit;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	@FantasyView(controlType = ControlType.CHECKBOX, label = "LABEL_HOSTLOG")
	private Boolean hostLog;

	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}

	public Float getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(Float initialValue) {
		this.initialValue = initialValue;
	}

	public FloatType getFloatType() {
		return floatType;
	}

	public void setFloatType(FloatType floatType) {
		this.floatType = floatType;
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Boolean getHostLog() {
		return hostLog;
	}

	public void setHostLog(Boolean hostLog) {
		this.hostLog = hostLog;
	}

	public Float getOffset() {
		return offset;
	}

	public void setOffset(Float offset) {
		this.offset = offset;
	}

	public Float getScalingFactor() {
		return scalingFactor;
	}

	public void setScalingFactor(Float scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

	public Boolean getProtectEndToEnd() {
		return protectEndToEnd;
	}

	public void setProtectEndToEnd(Boolean protectEndToEnd) {
		this.protectEndToEnd = protectEndToEnd;
	}
}
