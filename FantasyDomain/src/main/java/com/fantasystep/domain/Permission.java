package com.fantasystep.domain;

import java.util.List;
import java.util.UUID;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.helper.Status;

@DomainClass(isPropertyNode = true, label = "LABEL_PERMISSION")
public class Permission extends Node {

	private static final long serialVersionUID = -2288367503710886936L;
	@FantasyStep(required = true)
	@FantasyView(controlType = ControlType.TREEDROPDOWN, label = "LABEL_TARGET_NODE")
	private UUID targetNodeId;
	@FantasyStep(required = true, listType = Node.class)
//	@FantasyView(controlType = ControlType.DROPDOWN)
	private List<Class<? extends Node>> targetClasses;

	public UUID getTargetNodeId() {
		return targetNodeId;
	}

	public void setTargetNodeId(UUID targetNodeId) {
		this.targetNodeId = targetNodeId;
	}

	public List<Class<? extends Node>> getTargetClasses() {
		return targetClasses;
	}

	public void setTargetClasses(List<Class<? extends Node>> targetClasses) {
		this.targetClasses = targetClasses;
	}
	
	public void addTargetClass(Class<? extends Node> targetClass) {
		this.targetClasses.add(targetClass);
	}

	public void setBrowsePrivilage(Status true1) {
		
	}

	public void setInsertPrivilage(Status true1) {
		
	}

	public void setDeletePrivilage(Status true1) {
		
	}

	public void setDestroyPrivilage(Status true1) {
		
	}
}
