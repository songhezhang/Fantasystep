package com.fantasystep.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.Storage;
import com.fantasystep.helper.PropertyGroups;

@DomainClass(label = "LABEL_APPLICATION", icon = "stock_proxy.png")
@XmlRootElement(namespace="http://persistence.fantasystep.com/domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class Application extends Node {
	
	private static final long serialVersionUID = 5075547788330180116L;
	@FantasyStep(storage = Storage.MONGO)
	@FantasyView(controlType = ControlType.TEXTBOX, order = 1, group = PropertyGroups.BASE_PROPERTY, label = "LABEL_NAME")
	private String name = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		return this.name;
	}
}
