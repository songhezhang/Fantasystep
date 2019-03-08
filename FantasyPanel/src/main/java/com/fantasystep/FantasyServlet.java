package com.fantasystep;

import javax.servlet.annotation.WebServlet;

import com.fantasystep.panel.CApplication;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@WebServlet(value = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false, ui = CApplication.class, widgetset = "com.fantasystep.WidgetSet")
public class FantasyServlet extends VaadinServlet {
	private static final long serialVersionUID = -293653874043407811L;
}
