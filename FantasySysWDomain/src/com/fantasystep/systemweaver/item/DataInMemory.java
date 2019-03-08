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
import com.fantasystep.systemweaver.itemenum.BaseType;

@DomainClass(validParents = { LogicComponent.class }, label = "LABEL_DATA_IN_MEMORY", icon = "data-in-memory.ico")
public class DataInMemory extends SysWDomain {

	private static final long serialVersionUID = -3403013239930502612L;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_NUMBER_OF_ELEMENTS")
	private Integer numberOfElements;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_NUMBER_OF_DATASETS")
	private Integer numberOfDataSets;

	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = BaseType.class, valueOptions = BaseType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_BASE_TYPE")
	private BaseType baseType;

	public Integer getNumberOfElements() {
		return numberOfElements;
	}

	public void setNumberOfElements(Integer numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	public Integer getNumberOfDataSets() {
		return numberOfDataSets;
	}

	public void setNumberOfDataSets(Integer numberOfDataSets) {
		this.numberOfDataSets = numberOfDataSets;
	}

	public BaseType getBaseType() {
		return baseType;
	}

	public void setBaseType(BaseType baseType) {
		this.baseType = baseType;
	}
}
