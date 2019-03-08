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
import com.fantasystep.systemweaver.itemenum.IRV;

@DomainClass(validParents = { LogicComponent.class }, label = "LABEL_DATA_IDENTIFIER", icon = "data-identifier.ico")
public class DataIdentifier extends SysWDomain {

	private static final long serialVersionUID = 7702697951987516842L;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.ALPHANUMERIC, label = "LABEL_DID_NUMBER")
	private String dIDNumber;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_DID_SIZE")
	private Integer dIDSize;

	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = IRV.class, valueOptions = IRV.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_IRV")
	private IRV iRV;

	public String getDIDNumber() {
		return dIDNumber;
	}

	public void setDIDNumber(String dIDNumber) {
		this.dIDNumber = dIDNumber;
	}

	public Integer getDIDSize() {
		return dIDSize;
	}

	public void setDIDSize(Integer dIDSize) {
		this.dIDSize = dIDSize;
	}

	public IRV getIRV() {
		return iRV;
	}

	public void setIRV(IRV iRV) {
		this.iRV = iRV;
	}
}
