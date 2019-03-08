package com.fantasystep.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fantasystep.helper.Cardinality;
import com.fantasystep.helper.PropertyGroups;
import com.fantasystep.helper.Validation;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FantasyView {
	
	int order() default 0;
	
	String label() default "";
	
	ControlType controlType() default ControlType.LABEL;
	
	Validation validate() default Validation.NONE;
	
	String customValidation() default "";
	
	String validationErrorMessage() default "";
	
	boolean isSiblingUnique() default false;
	
	String groupLabel() default "";
	
	Cardinality cardinality() default Cardinality.SINGLE;
	
	boolean specialDisplay() default false;
	
	PropertyGroups group() default PropertyGroups.BASE_PROPERTY;
	
	String[] dynamicInfo() default {};

	String jsonTemplate() default "";
}
