package com.fantasystep.component.utils;

import com.vaadin.ui.Label;

public class CSSUtil
{

	public static final String	BLACK_FEATURED_TITLE				= "featured-title";
	public static final String	BLACK_NORMAL_NONE_DECORATION_LINK	= "link-button-no-text-decoration";

	public static final String	BLACK_NORMAL_UNDERLINE_LINK			= "link-button";
	public static final String	BLUE_ITALIC_TEXT					= "blue-italic-text";

	public static final String	BOTTOM_LINE							= "bottom-line";
	public static final String	BUTTONS_BLOCK						= "button-block";

	public static final String	CUSTOMIZED_TABLE_STYLE				= "customized-table";

	public static final String	DELETED_ITALIC_TABLE_ITEM			= "deleted";
	public static final String	DELETED_ITALIC_TREE_ITEM			= "deleted";

	public static final String	ERROR_ITALIC_TEXT					= "error-italic-text";
	public static final String	ERROR_NORMAL_TEXT					= "error-normal-text";

	public static final String	GRANDIENT_BOTTOM_LINE				= "featured-border";

	public static final String	GRAY_NORMAL_TITLE					= "strong-gray-title";
	public static final String	GRAY_STRONG_ITALIC_TEXT				= "strong-gray-italic-text";

	public static final String	GRAY_STRONG_NORMAL_TEXT				= "strong-gray-normal-text";
	public static final String	GREEN_BOLD_ITALIC_TABLE_ITEM		= "green-bold";
	public static final String	GREEN_ITALIC_TABLE_ITEM				= "green";

	public static final String	GREEN_ITALIC_TEXT					= "green-italic-text";
	public static final String	GREEN_NORMAL_TEXT					= "green-normal-text";

	public static final String	GREEN_NORMAL_UNDERLINE_LINK			= "link-button-green";

	public static final String	HIGHLIGHTED_ITALIC_TEXT				= "highlighted-italic-message";
	public static final String	INVALID_ITALIC_TABLE_ITEM			= "invalid";

	public static final String	INVALID_ITALIC_TREE_ITEM			= "invalid";
	public static final String	LIGHT_GRAY_ITALIC_TEXT				= "light-gray-italic-text";

	public static final String	LIGHT_GRAY_NORMAL_HEAD				= "head-light-gray-label";
	public static final String	NORMAL_ITALIC_TEXT					= "normal-italic-text";

	public static final String	NOT_AVAILABEL_ITALIC_TABLE_ITEM		= "not-available";
	public static final String	NOT_AVAILABLE_ITALIC_TREE_ITEM		= "not-available";

	public static final String	SEARCH_INPUT_BG						= "input-bg";
	public static final String	SEARCH_LIGHT_GRAY_NOMAL				= "light-gray";
	public static final String	SEARCH_PANEL						= "search-panel";

	public static final String	TRANSPARENT_PANEL					= "transparent-panel";
	public static final String	YELLOW_ITALIC_TEXT					= "yellow-italic-text";

	public static final String	YELLOW_NORMAL_HEAD					= "head-yellow-label";
	public static final String	YELLOW_NORMAL_TEXT					= "yellow-normal-text";
	public static final String	YELLOW_NORMAL_UNDERLINE_LINK		= "link-button-yellow";

	public static String wrapStyle( String label, String styleName )
	{
		return String.format( "<span class='%s'>%s</span>", styleName, label );
	}

	@SuppressWarnings("deprecation")
	public static Label wrapUnderlineLabel( String title, String styleName )
	{
		Label label = new Label( String.format( "<span class='%s'>%s<hr/></span>", styleName, title ), Label.CONTENT_XHTML );
		label.setWidth( "100%" );

		return label;
	}
}
