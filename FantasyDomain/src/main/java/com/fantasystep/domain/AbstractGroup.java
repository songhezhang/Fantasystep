package com.fantasystep.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.helper.MemberHolder;

@DomainClass(label = "LABEL_ANY_GROUP", icon = "file-roller.png")
public abstract class AbstractGroup extends Node implements MemberHolder {
	
	private static final long serialVersionUID = -8570442454588527315L;

	@FantasyStep
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABLE_NAME")
	private String name;
	
	@FantasyStep(listType = UUID.class)
	@FantasyView(controlType = ControlType.MEMBERLIST, label = "LABEL_MEMBERSHIP")
	private List<UUID> members = new ArrayList<UUID>();

	public boolean allows(Node targetNodeByApplication) {
		return true;
	}

	public void removeMember(Node node) {
		this.members.remove(node.getId());
	}

	public void addMember(Node node) {
		this.members.add(node.getId());
	}

	public List<UUID> getMembers() {
		return this.members;
	}
	
	public void setMembers(List<UUID> members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getLabel() {
		return this.name == null ? super.getLabel() : this.name;
	}
}