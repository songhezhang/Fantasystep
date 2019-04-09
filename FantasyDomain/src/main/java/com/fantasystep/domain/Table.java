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

@DomainClass(label = "LABEL_TABLE", icon = "stock_proxy.png", validParents = { Resource.class, Application.class })
@XmlRootElement(namespace="http://persistence.fantasystep.com/domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class Table extends Node {

	private static final long serialVersionUID = 3222437709262908346L;
	
	@FantasyStep(storage = Storage.MONGO)
	@FantasyView(controlType = ControlType.TEXTBOX, order = 1, group = PropertyGroups.BASE_PROPERTY, label = "LABEL_TABLE_NAME")
	private String tableName;
	
	@FantasyStep(storage = Storage.MONGO)
	@FantasyView(controlType = ControlType.CHECKBOX, order = 2, group = PropertyGroups.BASE_PROPERTY, label = "LABEL_NEED_AUTHENTICATION")
	private Boolean needAuthentication = false;
	
	@Override
	public String getLabel() {
		return getClassFullName();
	}
	
	public String getClassFullName() {
		if(getTableName() != null && getTableName().contains("."))
			return getTableName().substring(getTableName().lastIndexOf(".") + 1);
		return getTableName();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Boolean getNeedAuthentication() {
		return needAuthentication;
	}

	public void setNeedAuthentication(Boolean needAuthentication) {
		this.needAuthentication = needAuthentication;
	}
}
