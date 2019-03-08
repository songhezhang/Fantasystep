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
import com.fantasystep.systemweaver.itemenum.FixedPointType;

@DomainClass(validParents = { DiagnosticEvent.class, DataIdentifier.class, DataInMemory.class, DesignSignalGroup.class }, label = "LABEL_BOOLEAN_DESIGN_SIGNAL", icon = "boolean-design-signal.ico")
public class BooleanDesignSignal extends SysWDomain {

	private static final long serialVersionUID = -381261880404069108L;
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_DIMENSION")
	private Integer dimension;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_INITIAL_VALUE")
	private Integer initialValue;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = FixedPointType.class, valueOptions = FixedPointType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_FIXED_POINT_TYPE")
	private FixedPointType fixedPointType;

	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}

	public Integer getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(Integer initialValue) {
		this.initialValue = initialValue;
	}

	public FixedPointType getFixedPointType() {
		return fixedPointType;
	}

	public void setFixedPointType(FixedPointType fixedPointType) {
		this.fixedPointType = fixedPointType;
	}
}
