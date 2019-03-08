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
import com.fantasystep.systemweaver.itemenum.ASIL;
import com.fantasystep.systemweaver.itemenum.FixedPointType;
import com.fantasystep.systemweaver.itemenum.SDBType;

@DomainClass(validParents = { DiagnosticEvent.class, DataIdentifier.class, DataInMemory.class, DesignSignalGroup.class }, label = "LABEL_INTEGER_DESIGN_SIGNAL", icon = "integer-design-signal.ico")
public class IntegerDesignSignal extends SysWDomain {

	private static final long serialVersionUID = 6119152838146370622L;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_DIMENSION")
	private Integer dimension;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_INITIAL_VALUE")
	private Long initialValue;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = FixedPointType.class, valueOptions = FixedPointType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_FIXED_POINT_TYPE")
	private FixedPointType fixedPointType;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_MAX")
	private Long max;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_MIN")
	private Long min;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_SCALING_FACTOR")
	private Float scalingFactor;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_OFFSET")
	private Float offset;
	
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
	@FantasyStep(storage = Storage.MONGO, enumType = ASIL.class, valueOptions = ASIL.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_ASIL")
	private ASIL aSIL;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	@FantasyView(controlType = ControlType.CHECKBOX, label = "LABEL_HOSTLOG")
	private Boolean hostLog;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	@FantasyView(controlType = ControlType.CHECKBOX, label = "LABEL_CHECK_UPDATE")
	private Boolean checkUpdate;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, enumType = SDBType.class, valueOptions = SDBType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_SDB_TYPE")
	private SDBType sDBType;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTAREA, label = "LABEL_DESCRIPTION_ATTRIBUTE")
	private String descriptionAttribute;
	
	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}

	public Long getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(Long initialValue) {
		this.initialValue = initialValue;
	}
	
	public FixedPointType getFixedPointType() {
		return fixedPointType;
	}

	public void setFixedPointType(FixedPointType fixedPointType) {
		this.fixedPointType = fixedPointType;
	}

	public Long getMax() {
		return max;
	}

	public void setMax(Long max) {
		this.max = max;
	}

	public Long getMin() {
		return min;
	}

	public void setMin(Long min) {
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

	public Boolean getCheckUpdate() {
		return checkUpdate;
	}

	public void setCheckUpdate(Boolean checkUpdate) {
		this.checkUpdate = checkUpdate;
	}

	public ASIL getaSIL() {
		return aSIL;
	}

	public void setaSIL(ASIL aSIL) {
		this.aSIL = aSIL;
	}

	public SDBType getsDBType() {
		return sDBType;
	}

	public void setsDBType(SDBType sDBType) {
		this.sDBType = sDBType;
	}

	public String getDescriptionAttribute() {
		return descriptionAttribute;
	}

	public void setDescriptionAttribute(String descriptionAttribute) {
		this.descriptionAttribute = descriptionAttribute;
	}
}
