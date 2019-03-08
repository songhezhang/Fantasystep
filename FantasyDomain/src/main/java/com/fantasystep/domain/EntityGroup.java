package com.fantasystep.domain;

import com.fantasystep.annotation.DomainClass;

@DomainClass(validParents = Group.class, label = "LABEL_ENTITY_GROUP", icon = "file-manager.png")
public class EntityGroup extends Group {
	private static final long serialVersionUID = 3993832192597448674L;
}
