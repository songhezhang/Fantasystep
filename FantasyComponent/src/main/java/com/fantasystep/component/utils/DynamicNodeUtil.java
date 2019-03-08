package com.fantasystep.component.utils;

import java.util.List;

import com.fantasystep.domain.Node;
import com.fantasystep.utils.NodeUtil;

public class DynamicNodeUtil {
	public static List<Class<? extends Node>> getValidChildren(Class<? extends Node> class1) {
		List<Class<? extends Node>> list = NodeUtil.getValidChildren(class1);
		
		return list;
	}
}
