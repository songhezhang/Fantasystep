package com.fantasystep.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fantasystep.domain.Node;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FantasyStep
{
	Storage storage() default Storage.MYSQL;

	Class<?> listType() default void.class;

	@SuppressWarnings("rawtypes")
	Class<? extends Enum> enumType() default Enum.class;
	
	Class<? extends Node> relationType() default Node.class;

	Class<?>[] mapType() default {};
	
	boolean unique() default false;
	
	String alias() default "";
	
	boolean encrypted() default false;
	
	SerializationType serializationType() default SerializationType.USE_FIELD_TYPE;
	
	int serializationMaximumLength() default 1000;
	
	boolean sharedKey() default false;
	
	boolean required() default false;
	
	String defaultValue() default "";
	
	String foreignKey() default "";
	
	Class<? extends ValueOptions> valueOptions() default ValueOptions.class;
	
	StorageModelType storageModel() default StorageModelType.LIST;
	
	String storageName() default "";

	boolean propertyField() default true;
}