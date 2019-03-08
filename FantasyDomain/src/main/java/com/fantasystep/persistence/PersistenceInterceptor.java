package com.fantasystep.persistence;

public interface PersistenceInterceptor {

	public abstract void initialize();

	public abstract void postDelete();

	public abstract void postDestroy();

	public abstract void postInsert();

	public abstract void postUnDelete();

	public abstract void postUpdate();

	public abstract void preDelete();

	public abstract void preDestroy();

	public abstract void preUnDelete();

	public abstract void preUpdate();

	public abstract void preInsert();

}
