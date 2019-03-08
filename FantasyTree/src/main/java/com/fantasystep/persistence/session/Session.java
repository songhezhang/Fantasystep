package com.fantasystep.persistence.session;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.fantasystep.domain.User;

public class Session implements Serializable {

	private static final long serialVersionUID = -7401674953785475123L;
	
	private Date createdAt;
	private UUID key;
	private User user;

	public Session(User user) {
		setUser(user);
		setCreatedAt(new Date());
		setKey(UUID.randomUUID());
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public UUID getKey() {
		return key;
	}

	public User getUser() {
		return user;
	}

	private void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	private void setKey(UUID key) {
		this.key = key;
	}

	private void setUser(User user) {
		this.user = user;
	}
}
