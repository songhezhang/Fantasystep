package com.fantasystep.component.common;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.LoginForm;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;

public abstract class Login extends LoginForm
{
	private static final long serialVersionUID = -238342190216155675L;

	
	@Override
	protected com.vaadin.ui.TextField createUsernameField() {
		// TODO Auto-generated method stub
		return super.createUsernameField();
	}
//	@Override
//    protected Component createContent(TextField userNameField, PasswordField passwordField, Button loginButton) {
//        VerticalLayout layout = new VerticalLayout();
//        layout.setSpacing(true);
//        layout.setMargin(true);
//
//        layout.addComponent(userNameField);
//        layout.addComponent(passwordField);
//        layout.addComponent(loginButton);
//        layout.setComponentAlignment(loginButton, Alignment.BOTTOM_LEFT);
//        
//        return layout;
//    }
}
