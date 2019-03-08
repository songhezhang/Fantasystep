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

@DomainClass(validParents = { DiagnosticEvent.class, DataIdentifier.class, DataInMemory.class, BusConnection.class, SignalMapping.class }, label = "LABEL_BYTE_ARRAY_DESIGN_SIGNAL", icon = "byte-array-design-signal.ico")
public class ByteArrayDesignSignal extends SysWDomain {

	private static final long serialVersionUID = 3531915972393145373L;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_DIMENSION")
	private Integer dimension;
	
	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}

}
