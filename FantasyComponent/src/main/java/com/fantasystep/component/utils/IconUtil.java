package com.fantasystep.component.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

import com.fantasystep.annotation.AnnotationsParser;
import com.fantasystep.component.panel.LocalizationHandler;
import com.fantasystep.domain.Node;
import com.fantasystep.helper.IconHolder;
import com.fantasystep.helper.NodeType;
import com.fantasystep.utils.Environment;
import com.fantasystep.utils.NodeUtil;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.BaseTheme;

public class IconUtil {
	public static final String ICON_ACTIVATE = "gtk-ok.png";
	public static final String ICON_DEACTIVATE = "gtk-no.png";
	public static final String ICON_EDIT = "drivel-48.png";
	private final static String ICON_FOL = "icons";

	public static final String ICON_RT_BULLET = "gtk-jump-to-ltr.png";
	public static final String ICON_SEARCH = "search.png";

	private final static String IMAGES_FOL = "images";
	public final static int LARGE_ICON_SIZE = 32;
	public final static String LARGE_ICONS_PATH = String.format("%s/%s/",
			ICON_FOL, LARGE_ICON_SIZE);
	public final static int MEDIUM_ICON_SIZE = 24;
	public final static String MEDIUM_ICONS_PATH = String.format("%s/%s/",
			ICON_FOL, MEDIUM_ICON_SIZE);
	public final static int SMALL_ICON_SIZE = 16;
	public final static String SMALL_ICONS_PATH = String.format("%s/%s/",
			ICON_FOL, SMALL_ICON_SIZE);

	public static Label getIconLabel(Node node, int iconSize, boolean isCaption) {
		return getIconLabel(node, iconSize, isCaption, false);
	}

	public static Label getIconLabel(Node node, int iconSize,
			boolean isCaption, boolean isTooltip) {
		if (node != null) {
			String ImgStr = getIconSource(node, iconSize);

			if (isCaption)
				ImgStr = String.format("%s%s", ImgStr,
						LocalizationHandler.get(node.getLabel()));
			Label lbl = new Label(ImgStr, ContentMode.HTML);

			if (isTooltip)
				lbl.setDescription(LabelUtil.getDomainLabel(node.getClass()));
			return lbl;
		}
		return null;
	}

	public static Label getIconLabel(UUID id, Node root, int size) {
		return getIconLabel(NodeUtil.getNode(id, root), size, true, false);

	}

	public static Component getIconsCollection(Object value, int size) {
		HorizontalLayout hl = new HorizontalLayout();

		if (value instanceof Collection) {
			for (Object item : (Collection<?>) value) {
				String icon = IconUtil.getRawIcon(
						(Class<? extends Node>) ((NodeType) item).getValue(),
						size);
				Label lbl = new Label(icon, ContentMode.HTML);
				lbl.setDescription(LabelUtil
						.getDomainLabel((Class<? extends Node>) ((NodeType) item)
								.getValue()));
				hl.addComponent(new Label("&nbsp;&nbsp;", ContentMode.HTML));
				hl.addComponent(lbl);
			}

		}
		return hl;
	}

	private static String getIconSource(Node node, int iconSize) {
		String ImgStr;
		if (node instanceof IconHolder)
			ImgStr = getRawIcon(((IconHolder) node).getIcon(iconSize), iconSize);
		else
			ImgStr = getRawIcon(node.getClass(), iconSize);
		return ImgStr;
	}

	private static URL getLocalIconUrl(String imgStr, String fol, Integer size) {
		try {
			if (size == null)
				return new URL(String.format("%s%s/%s", Environment
						.getProperty(EnvironmentUtil.APPLICATION_BASE_PATH),
						fol, imgStr));

			else
				return new URL(String.format("%s%s/%s/%s", Environment
						.getProperty(EnvironmentUtil.APPLICATION_BASE_PATH),
						fol, size, imgStr));

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String getRawIcon(Class<? extends Node> nodeClazz, int size) {
		if (AnnotationsParser.getAttributes(nodeClazz).getIcon() != null)
			return getRawIcon(
					getLocalIconUrl(AnnotationsParser.getAttributes(nodeClazz)
							.getIcon(), ICON_FOL, size), size);
		else
			return getRawIcon(
					getLocalIconUrl(String.format("%s.png", nodeClazz
							.getSimpleName().toLowerCase()), ICON_FOL, size),
					size);
	}

	public static String getRawIcon(String imgName, int size) {
		return getRawIcon(getLocalIconUrl(imgName, ICON_FOL, size), size);
	}

	private static String getRawIcon(URL url, int size) {
		return String.format("<img width=%s height=%s src=%s /> ", size, size,
				url);
	}

	public static String getRawImage(String imgName, int size) {
		return getRawIcon(getLocalIconUrl(imgName, IMAGES_FOL, null), size);
	}

	public static Component wrapIcon(Component component, Class<?> clazz,
			int iconSize) {
		String icon = null;
		if (AnnotationsParser.getAttributes(clazz).getIcon() != null)
			icon = AnnotationsParser.getAttributes(clazz).getIcon();
		else
			icon = String.format("%s.png", clazz.getSimpleName().toLowerCase());
		return wrapIcon(component, icon, iconSize, component.getCaption(),
				CSSUtil.BLACK_NORMAL_UNDERLINE_LINK);
	}

	public static Component wrapIcon(Component component, String icon,
			int iconSize, String caption) {
		return wrapIcon(component, icon, iconSize, caption,
				CSSUtil.BLACK_NORMAL_UNDERLINE_LINK);
	}

	public static Component wrapIcon(Component component, String icon,
			int iconSize, String caption, String styleName) {
		return wrapIcon(component, icon, ICON_FOL, iconSize, caption, styleName);
	}

	private static Component wrapIcon(Component component, String icon,
			String iconFol, Integer iconSize, String caption, String styleName) {
		if (component instanceof Button || component instanceof Label) {
			if (styleName != null)
				component.addStyleName(styleName);

			if (caption != null && caption != "")
				component.setCaption(caption);

			if (iconSize != null)
				component.setIcon(new ThemeResource(String.format("%s/%s/%s",
						iconFol, iconSize, icon)));
			else
				component.setIcon(new ThemeResource(String.format("%s/%s",
						iconFol, icon)));
			component.setStyleName(BaseTheme.BUTTON_LINK);
		}
		return component;
	}

	public static Component wrapImage(Component component, String image) {
		return wrapIcon(component, image, IMAGES_FOL, null, null, null);
	}

	public static Component wrapImage(String image, int size) {
		String str = getRawImage(image, size);
		return new Label(str, ContentMode.HTML);
	}

	public static Component wrapServerIcon(Component component, URL url,
			String caption) {
		if (url == null)
			new MalformedURLException(String.format("url is incorrect %s", url));

		component.setIcon(new ExternalResource(url));

		if (component instanceof Button)
			component.setStyleName(BaseTheme.BUTTON_LINK);

		return component;
	}

	public static Resource getMediumSizeIcon(Node node) {
		return getSizeIcon(node, MEDIUM_ICONS_PATH);
	}

	public static Resource getSmallSizeIcon(Node node) {
		return getSizeIcon(node, SMALL_ICONS_PATH);
	}

	public static Resource getLargeSizeIcon(Node node) {
		return getSizeIcon(node, LARGE_ICONS_PATH);
	}

	public static Resource getMediumSizeIcon(Class<? extends Node> clazz) {
		return getSizeIcon(clazz, MEDIUM_ICONS_PATH);
	}

	public static Resource getSmallSizeIcon(Class<? extends Node> clazz) {
		return getSizeIcon(clazz, SMALL_ICONS_PATH);
	}

	public static Resource getLargeSizeIcon(Class<? extends Node> clazz) {
		return getSizeIcon(clazz, LARGE_ICONS_PATH);
	}

	private static Resource getSizeIcon(Node node, String path) {
		if (node instanceof IconHolder)
			return new ExternalResource(
					((IconHolder) node).getIcon(IconUtil.MEDIUM_ICON_SIZE));
		else
			return getSizeIcon(node.getClass(), path);
	}

	private static ThemeResource getSizeIcon(Class<? extends Node> clazz,
			String path) {
		if (AnnotationsParser.getAttributes(clazz).getIcon() != null)
			return new ThemeResource(String.format("%s%s", path,
					AnnotationsParser.getAttributes(clazz).getIcon()));
		else
			return new ThemeResource(String.format("%s%s.png", path, clazz
					.getSimpleName().toLowerCase()));
	}
}
