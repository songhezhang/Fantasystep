package com.fantasystep.autosar;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.Storage;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.PropertyGroups;

@DomainClass(label = "LABEL_AUTOSAR_MODEL", icon = "dropline.png")
public class AutosarModel extends Node {

	private static final long serialVersionUID = -7531081644215153874L;
	
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
