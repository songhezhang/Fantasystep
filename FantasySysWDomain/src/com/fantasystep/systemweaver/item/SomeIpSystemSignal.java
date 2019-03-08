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
import com.fantasystep.systemweaver.itemenum.MessageType;

@DomainClass(validParents = { Frame.class, BusConnection.class, SignalMapping.class }, label = "LABEL_SOME_IP_SYSTEM_SIGNAL", icon = "system-signal.ico")
public class SomeIpSystemSignal extends SysWDomain {

	private static final long serialVersionUID = -3170366554936988447L;

	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_MESSAGE_ID")
	private Integer messageID;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_SOME_IP_INTERFACE_VERSION")
	private Integer sOMEIPInterfaceVersion;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.DECIMAL)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_SCALING_FACTOR")
	private Float scalingFactor;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.DECIMAL, label = "LABEL_OFFSET")
	private Integer offset;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = MessageType.class, valueOptions = MessageType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_MESSAGE_TYPE")
	private MessageType messageType;

	public Integer getMessageID() {
		return messageID;
	}

	public void setMessageID(Integer messageID) {
		this.messageID = messageID;
	}

	public Integer getSOMEIPInterfaceVersion() {
		return sOMEIPInterfaceVersion;
	}

	public void setSOMEIPInterfaceVersion(Integer sOMEIPInterfaceVersion) {
		this.sOMEIPInterfaceVersion = sOMEIPInterfaceVersion;
	}

	public Float getScalingFactor() {
		return scalingFactor;
	}

	public void setScalingFactor(Float scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
}
