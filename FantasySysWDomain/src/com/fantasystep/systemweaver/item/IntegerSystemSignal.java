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

@DomainClass(validParents = { BusConnection.class, SignalMapping.class }, label = "LABEL_INTEGER_SYSTEM_SIGNAL", icon = "system-signal.ico")
public class IntegerSystemSignal extends SysWDomain {

	private static final long serialVersionUID = -52150989171683707L;
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_INITIAL_VALUE")
	private Integer initialValue;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_MIN")
	private Float min;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_MAX")
	private Float max;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_OFFSET")
	private Float offset;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_SCALING_FACTOR")
	private Float scalingFactor;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_UNIT")
	private String unit;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	@FantasyView(controlType = ControlType.CHECKBOX, label = "LABEL_COUNTER")
	private Boolean counter;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	@FantasyView(controlType = ControlType.CHECKBOX, label = "LABEL_CHECKSUM")
	private Boolean checksum;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = SDBType.class, valueOptions = SDBType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_SDB_TYPE")
	private SDBType sDBType;

	public Integer getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(Integer initialValue) {
		this.initialValue = initialValue;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public SDBType getSDBType() {
		return sDBType;
	}

	public void setSDBType(SDBType sDBType) {
		this.sDBType = sDBType;
	}

	public Boolean getCounter() {
		return counter;
	}

	public void setCounter(Boolean counter) {
		this.counter = counter;
	}

	public Boolean getChecksum() {
		return checksum;
	}

	public void setChecksum(Boolean checksum) {
		this.checksum = checksum;
	}
}
