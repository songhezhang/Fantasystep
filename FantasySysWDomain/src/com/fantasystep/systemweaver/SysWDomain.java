package com.fantasystep.systemweaver;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.domain.Node;
import com.fantasystep.systemweaver.annotation.SystemWeaver;
import com.fantasystep.systemweaver.annotation.SystemWeaverDomain;
import com.fantasystep.systemweaver.itemenum.VccModelMapping.VCC_PART_TYPE;

@SystemWeaverDomain
@DomainClass(label = "LABEL_SYSW_DOMAIN", icon = "file-roller.png")
public abstract class SysWDomain extends Node {
	
	private static final long serialVersionUID = 387796940786683851L;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_HANDLE_ID")
	private String handleId;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_NAME")
	private String name;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_VERSION")
	private String version;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_DESCRIPTION")
	private String description;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = VCC_PART_TYPE.class, valueOptions = VCC_PART_TYPE.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_PART_TYPE")
	private VCC_PART_TYPE partType = VCC_PART_TYPE.NOT_SET;

	public String getHandleId() {
		return handleId;
	}

	public void setHandleId(String handleId) {
		this.handleId = handleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public VCC_PART_TYPE getPartType() {
		return partType;
	}

	public void setPartType(VCC_PART_TYPE partType) {
		this.partType = partType;
	}

	@Override
	public String getLabel() {
		return name;
	}
}
