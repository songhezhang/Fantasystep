package com.fantasystep.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.helper.GenderEnum;
import com.fantasystep.helper.PropertyGroups;
import com.fantasystep.helper.Validation;

@DomainClass(validParents = {Group.class, Organization.class}, label = "LABEL_USER", icon = "user_icon.png")
public class User extends Node {
	private static final long serialVersionUID = 2360426979442011197L;

	public static final String CURRENT_USER_PROPERTY = "current_user";
	
	@FantasyStep(required = true, storage = Storage.LDAP)
	private Set<String> objectClass = new HashSet<String>();
	
	@FantasyStep(required = true, storage = Storage.LDAP, storageName = "cn")
	@FantasyView(controlType = ControlType.TEXTBOX, order = 2, label = "LABEL_FIRSTNAME")
	private String firstName;
	
	@FantasyStep(required = true, storage = Storage.MYSQL)
	@FantasyView(controlType = ControlType.TEXTBOX, order = 3, label = "LABEL_LASTNAME")
	private String lastName;
	
	@FantasyStep(required = true, storage = Storage.MYSQL, enumType = GenderEnum.class, valueOptions = GenderEnum.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, order = 6, label = "LABEL_GENDER")
	private GenderEnum gender;
	
	@FantasyStep(required = true, storage = Storage.LDAP, storageName = "sn")
	@FantasyView(controlType = ControlType.TEXTBOX, order = 1, group = PropertyGroups.BASE_PROPERTY, label = "LABEL_USERNAME")
	private String username;
	
	@FantasyStep(required = true, storage = Storage.LDAP, storageName = "userPassword", encrypted = true)
	@FantasyView(controlType = ControlType.PASSWORD, order = 4, label = "LABEL_PASSWORD")
	private String password;
	
	@FantasyStep(required = true, storage = Storage.MYSQL, serializationType = SerializationType.DATE)
	@FantasyView(controlType = ControlType.DATEOFBIRTH, order = 8, group = PropertyGroups.ADVANCED_PROPERTY, label = "LABEL_BIRTHDAY")
	private Date birthday;
	
	@FantasyStep(required = true, storage = Storage.MYSQL, serializationMaximumLength = 40, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TREEDROPDOWN, order = 5, label = "LABEL_ADMIN_NODE")
	private UUID adminNode;
	
	@FantasyStep(required = true, storage = Storage.LDAP, storageName = "fantasystepEmail", serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, validate = Validation.EMAIL, order = 9, label = "LABEL_EMAIL")
	private String email;
	
	@FantasyStep(storage = Storage.MYSQL, serializationMaximumLength = 16, serializationType = SerializationType.BOOLEAN)
	@FantasyView(controlType = ControlType.CHECKBOX, order = 7, label = "LABEL_ACTIVATED")
	private Boolean activated;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public GenderEnum getGender() {
		return gender;
	}
	public void setGender(GenderEnum gender) {
		this.gender = gender;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public Boolean getActivated() {
		return activated;
	}
	public void setActivated(Boolean activated) {
		this.activated = activated;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public UUID getAdminNode() {
		return adminNode;
	}
	public void setAdminNode(UUID adminNode) {
		this.adminNode = adminNode;
	}
	public List<Permission> getPermissions(Class<? extends Node> clazz) {
		List<Permission> list = new ArrayList<Permission>();
		if(getChildren() != null)
		for(Node node : getChildren())
			if(node instanceof Permission)
				list.add((Permission)node);
		return list;
	}
	
	public User() {
		getObjectClass().addAll(Arrays.asList(new String[]{"top", "person", "fantasystepNode"}));
	}
	public Set<String> getObjectClass() {
		return objectClass;
	}
	public void setObjectClass(Set<String> objectClass) {
		this.objectClass = objectClass;
	}
	
	@Override
	public String getLabel() {
		return String.format("%s %s", firstName, lastName);
	}
}
