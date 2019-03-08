package com.fantasystep.domain;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.Storage;
import com.fantasystep.helper.PropertyGroups;

@DomainClass(validParents = { EntityGroup.class }, label = "LABEL_ENTITY", icon = "gtk-file.png")
public class Entity extends Node {
	
	private static final long serialVersionUID = -7768632032073537363L;
	
	@FantasyStep(storage = Storage.MONGO)
	@FantasyView(controlType = ControlType.TEXTBOX, order = 1, group = PropertyGroups.BASE_PROPERTY, label = "LABEL_FULL_NAME")
	private String fullName = null;
	@FantasyStep(storage = Storage.MONGO)
	@FantasyView(controlType = ControlType.TEXTAREA, order = 3, group = PropertyGroups.ADVANCED_PROPERTY, label = "LABEL_SOURCE_CODE")
	private String sourceCode = null;
	@FantasyStep(storage = Storage.MONGO)
	@FantasyView(controlType = ControlType.CHECKBOX, order = 2, group = PropertyGroups.BASE_PROPERTY, label = "LABEL_IS_ABSTRACT")
	private Boolean isAbstract = false;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public Boolean getIsAbstract() {
		return isAbstract;
	}

	public void setIsAbstract(Boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	@Override
	public String getLabel() {
		if(this.fullName != null)
			return this.fullName.substring(this.fullName.lastIndexOf(".") + 1);
		else return null;
	}
}
