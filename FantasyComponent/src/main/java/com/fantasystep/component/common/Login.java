package com.fantasystep.component.common;

import com.ejt.vaadin.loginform.LoginForm;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public abstract class Login extends LoginForm
{
	private static final long serialVersionUID = -238342190216155675L;

	@Override
    protected Component createContent(TextField userNameField, PasswordField passwordField, Button loginButton) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);

        layout.addComponent(userNameField);
        layout.addComponent(passwordField);
        layout.addComponent(loginButton);
        layout.setComponentAlignment(loginButton, Alignment.BOTTOM_LEFT);
        
        return layout;
    }
}
