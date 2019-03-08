package com.fantasystep.helper;

import java.util.ArrayList;
import java.util.List;

import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.annotation.ValueOptions;
import com.fantasystep.domain.Node;

public class NodeOptions implements ValueOptions {

	private List<Node> nodes = null;

	public NodeOptions(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<ValueOptionEntry> getValues() {
		List<ValueOptionEntry> list = new ArrayList<ValueOptionEntry>();
		for (final Node node : nodes) {
			list.add(new ValueOptionEntry() {

				public Object getValue() {
					return node.getId();
				}

				public String getLabel() {
					return node.getLabel();
				}
			});
		}
		return list;
	}
}
