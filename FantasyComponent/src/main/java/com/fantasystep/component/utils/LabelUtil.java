package com.fantasystep.component.utils;

import com.fantasystep.annotation.DomainClass;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.domain.Node;
import com.vaadin.ui.Label;

public class LabelUtil
{

	public final static class LabelConstant
	{
		public final static String	PROPERTY	= "LABEL_PROPERTY_TAB";
	}

	public final static String	LABEL_ACTION_FAILED												= "LABEL_ACTION_FAILED";

	public final static String	LABEL_ADD														= "LABEL_ADD";
	public final static String	LABEL_ADD_ITEMS													= "LABEL_ADD_ITEMS";
	public final static String	LABEL_ALERT														= "LABEL_ALERT";

	public static final String	LABEL_AVAILABLE													= "LABEL_AVAILABLE";
	public final static String	LABEL_BACK														= "LABEL_BACK";
	public final static String	LABEL_CAN_NOT_DELETE_WINDOWS_ACCOUNT_OF_WINDOWS_MEMBER_ERROR	= "LABEL_CAN_NOT_DELETE_WINDOWS_ACCOUNT_OF_WINDOWS_MEMBER_ERROR";

	public final static String	LABEL_CANCEL													= "LABEL_CANCEL";

	public final static String	LABEL_CREATE_NEW												= "LABEL_CREATE_NEW";
	public final static String	LABEL_CURRENT_USER												= "LABEL_CURRENT_USER";
	public final static String	LABEL_DAY														= "LABEL_DAY";

	public final static String	LABEL_DELETE													= "LABEL_DELETE";
	public final static String	LABEL_DENY														= "LABEL_DENY";
	public final static String	LABEL_DESTROY													= "LABEL_DESTROY";
	public final static String	LABEL_EDIT														= "LABEL_EDIT";
	public final static String	LABEL_FAILED													= "LABEL_FAILED";
	public final static String	LABEL_FROM														= "LABEL_FROM";

	public final static String	LABEL_GRANT														= "LABEL_GRANT";
	public final static String	LABEL_HIDE														= "LABEL_HIDE";

	public final static String	LABEL_ICON														= "LABEL_ICON";
	public static final String	LABEL_INVALID													= "LABEL_INVALID";
	public final static String	LABEL_INVALID_MOVE												= "LABEL_INVALID_MOVE";

	public final static String	LABEL_ITEMS														= "LABEL_ITEMS";
	public final static String	LABEL_KLOUD_APPLICATION											= "LABEL_KLOUD_APPLICATION";
	public final static String	LABEL_LOGIN														= "LABEL_LOGIN";
	public final static String	LABEL_LOGOUT													= "LABEL_LOGOUT";

	public final static String	LABEL_MEMBERS													= "LABEL_MEMBERS";
	public final static String	LABEL_MEMBERSHIPS												= "LABEL_MEMBERSHIP";

	public final static String	LABEL_MONTH														= "LABEL_MONTH";
	public final static String	LABEL_NAME														= "LABEL_NAME";
	public final static String	LABEL_NEW														= "LABEL_NEW";
	public final static String	LABEL_NEXT														= "LABEL_NEXT";
	public final static String	LABEL_NO_DATA_FOUND												= "LABEL_NO_DATA_FOUND";
	public final static String	LABEL_NONE														= "LABEL_NONE";
	public static final String	LABEL_NOT_ALLOWED_TO_MOVE_INHERITED_NODES						= "LABEL_NOT_ALLOWED_TO_MOVE_INHERITED_NODES";
	public final static String	LABEL_NOT_AUTHORIZED											= "LABEL_NOT_AUTHORIZED";
	public final static String	LABEL_NOT_AUTHORIZED_TO_ACCESS_PAGE								= "LABEL_NOT_AUTHORIZED_TO_ACCESS_PAGE";
	public final static String	LABEL_NOT_AUTHORIZED_TREE_MANAGER_ACTION						= "LABEL_NOT_AUTHORIZED";

	public static final String	LABEL_NOT_AVAILABLE												= "LABEL_NOT_AVAILABLE";
	public final static String	LABEL_NOT_FOUND													= "LABEL_NOT_FOUND";
	public final static String	LABEL_PASSWORD													= "LABEL_PASSWORD";
	public final static String	LABEL_PATH														= "LABEL_PATH";
	public final static String	LABEL_PERMISSION_DENEID											= "LABEL_PERMISSION_DENIED";

	public final static String	LABEL_PERMISSION_ERROR											= "LABEL_PERMISSION_ERROR";
	public final static String	LABEL_PRIMARY_PROPERTIES										= "LABEL_PRIMARY_PROPERTIES";

	public final static String	LABEL_REMOVE_ITEMS												= "LABEL_REMOVE_ITEMS";
	public final static String	LABEL_REQUIRED													= "LABEL_REQUIRED";
	public final static String	LABEL_SAVE														= "LABEL_SAVE";
	public final static String	LABEL_SEARCH													= "LABEL_SEARCH";
	public final static String	LABEL_SOURCE													= "LABEL_SOURCE";

	public final static String	LABEL_TITLES													= "LABEL_TITLES";
	public final static String	LABEL_TYPE														= "LABEL_TYPE";
	public final static String	LABEL_UNDELETE													= "LABEL_UNDELETE";
	public final static String	LABEL_UNIQUE_VOILATE_ERROR										= "LABEL_UNIQUE_VOILATE_ERROR";
	public final static String	LABEL_UPDATE													= "LABEL_UPDATE";

	public final static String	LABEL_UPLOAD_FILE												= "LABEL_UPLOAD_FILE";
	public final static String	LABEL_USER_PASSWORD_INCORRECT									= "LABEL_USER_PASSWORD_INCORRECT";

	public final static String	LABEL_USERNAME													= "LABEL_USERNAME";
	public final static String	LABEL_VALIDATE_ERROR											= "LABEL_VALIDATE_ERROR";

	public final static String	LABEL_VALIDATE_FAILED											= "LABEL_VALIDATE_FAILED";

	public final static String	LABEL_VALIDATE_REQUIRED											= "LABEL_VALIDATE_REQUIRED";
	public final static String	LABEL_WIZARD													= "LABEL_WIZARD";
	public final static String	LABEL_YEAR														= "LABEL_YEAR";

	public final static String	LABEL_CHECK_ALL													= "LABEL_CHECK_ALL";
	public final static String	LABEL_CHECK_NONE												= "LABEL_CHECK_NONE";
	public final static String	LABEL_CHECK_REVERSE												= "LABEL_CHECK_REVERSE";

	public static String getDomainLabel( Class<? extends Node> domain )
	{
		try {
			return LocalizationHandler.get( domain.getAnnotation( DomainClass.class ).label() );
		} catch(Exception e) {
			return "None";
		}
	}

	// public static String getDomainLabelFromNodeContainer( AbstractCContainer container )
	// {
	// if( container instanceof AbstractNodeCContainer )
	// return getDomainLabel( ( (AbstractNodeCContainer) container ).getNode().getClass() );
	//
	// else if( container instanceof AbstractMultiNodeCContainer )
	// return getDomainLabel( ( (AbstractMultiNodeCContainer) container ).getNodeClass() );
	//
	// return null;
	// }

	public static Label getNoneDataLabel()
	{
		Label label = new Label( LocalizationHandler.get( LabelUtil.LABEL_NO_DATA_FOUND ) );
		label.setStyleName( CSSUtil.ERROR_ITALIC_TEXT );
		return label;
	}
}
