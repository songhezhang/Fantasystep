package com.fantasystep.component.menu;

import java.util.List;

import com.fantasystep.domain.Node;

public interface MenuHelper
{
	abstract public List<Node> getSelectedNodes();

	abstract public boolean isHideMenuItem();

	abstract public void triggerHideMenuItem();

}