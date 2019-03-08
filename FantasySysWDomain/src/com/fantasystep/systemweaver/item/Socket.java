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
import com.fantasystep.systemweaver.itemenum.ByteOrder;
import com.fantasystep.systemweaver.itemenum.Protocol;
import com.fantasystep.systemweaver.itemenum.RoutingScheme;
import com.fantasystep.systemweaver.itemenum.Serialization;

@DomainClass(validParents = { Vlan.class }, label = "LABEL_SOCKET", icon = "socket.ico")
public class Socket extends SysWDomain {

	private static final long serialVersionUID = -759817887787829855L;

	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = Protocol.class, valueOptions = Protocol.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_PROTOCAL")
	private Protocol protocol;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_SOURCE_PORT_NUMBER")
	private Integer sourcePortNumber;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_DESTINATION_PORT_NUMBER")
	private Integer destinationPortNumber;

	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = Serialization.class, valueOptions = Serialization.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_SERIALIZATION")
	private Serialization serialization;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_PRIORITY")
	private Integer priority;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.NUMERIC, label = "LABEL_ALIGNMENT")
	private Integer alignment;

	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = ByteOrder.class, valueOptions = ByteOrder.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_BYTE_ORDER")
	private ByteOrder byteOrder;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, customValidation = "((\\.|^)(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]?|0$)){4}", label = "LABEL_DESTINATION_IP_ADDRESS")
	private String destinationIPAddress;

	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, customValidation = "((\\.|^)(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]?|0$)){4}", label = "LABEL_DEFAULT_GATEWAY")
	private String defaultGateway;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = RoutingScheme.class, valueOptions = RoutingScheme.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_ROUTING_SCHEME")
	private RoutingScheme routingScheme;

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public Integer getSourcePortNumber() {
		return sourcePortNumber;
	}

	public void setSourcePortNumber(Integer sourcePortNumber) {
		this.sourcePortNumber = sourcePortNumber;
	}

	public Integer getDestinationPortNumber() {
		return destinationPortNumber;
	}

	public void setDestinationPortNumber(Integer destinationPortNumber) {
		this.destinationPortNumber = destinationPortNumber;
	}

	public Serialization getSerialization() {
		return serialization;
	}

	public void setSerialization(Serialization serialization) {
		this.serialization = serialization;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getAlignment() {
		return alignment;
	}

	public void setAlignment(Integer alignment) {
		this.alignment = alignment;
	}

	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	public String getDestinationIPAddress() {
		return destinationIPAddress;
	}

	public void setDestinationIPAddress(String destinationIPAddress) {
		this.destinationIPAddress = destinationIPAddress;
	}

	public RoutingScheme getRoutingScheme() {
		return routingScheme;
	}

	public void setRoutingScheme(RoutingScheme routingScheme) {
		this.routingScheme = routingScheme;
	}

	public String getDefaultGateway() {
		return defaultGateway;
	}

	public void setDefaultGateway(String defaultGateway) {
		this.defaultGateway = defaultGateway;
	}
}
