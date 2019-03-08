package com.fantasystep.component.layout;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class SimpleContainerLayout extends AbtractContainerLayout {
	private static final long serialVersionUID = -5077834523355060796L;
	
	private VerticalLayout body = new VerticalLayout();

	public SimpleContainerLayout() {
		super();
		bindLayout();
		body.setSizeFull();
		body.setSizeUndefined();
		body.setWidth("100%");
		body.setHeight("100%");
		body.setStyleName("scroll-panel");
	}

	public void addBodyComponent(Component component, Alignment alignment) {
		body.addComponent(component);
		body.setComponentAlignment(component, alignment);
	}

	@Override
	public AbstractComponentContainer getBodyContainer() {
		return body;
	}
}
