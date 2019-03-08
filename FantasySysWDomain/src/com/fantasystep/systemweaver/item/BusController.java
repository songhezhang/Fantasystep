package com.fantasystep.systemweaver.item;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.systemweaver.SysWDomain;
import com.fantasystep.systemweaver.annotation.SystemWeaver;
import com.fantasystep.systemweaver.itemenum.BusType;

@DomainClass(validParents = { Socket.class, BusConnection.class }, label = "LABEL_BUS_CONTROLLER", icon = "bus-controller.ico")
public class BusController extends SysWDomain {

	private static final long serialVersionUID = 4257839760741161632L;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = BusType.class, valueOptions = BusType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_BUS_TYPE")
	private BusType busType;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, customValidation = "((\\.|^)(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]?|0$)){4}", label = "LABEL_DEFAULT_GATEWAY")
	private String defaultGateway;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, customValidation = "((\\.|^)(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]?|0$)){4}", label = "LABEL_IP_ADDRESS")
	private String iPAddress;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, customValidation = "((\\.|^)(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]?|0$)){4}", label = "LABEL_NETMASK")
	private String netmask;
	
	public BusType getBusType() {
		return busType;
	}

	public void setBusType(BusType busType) {
		this.busType = busType;
	}

	public String getDefaultGateway() {
		return defaultGateway;
	}

	public void setDefaultGateway(String defaultGateway) {
		this.defaultGateway = defaultGateway;
	}

	public String getIPAddress() {
		return iPAddress;
	}

	public void setIPAddress(String iPAddress) {
		this.iPAddress = iPAddress;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}
}
