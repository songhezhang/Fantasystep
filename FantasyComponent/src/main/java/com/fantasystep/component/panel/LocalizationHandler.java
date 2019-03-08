package com.fantasystep.component.panel;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.localization.LocalizationUtil;
import com.fantasystep.localization.LocalizationUtil.Language;
import com.vaadin.server.VaadinSession;

public class LocalizationHandler {

	private static Logger logger = LoggerFactory
			.getLogger(LocalizationHandler.class);

	public static String get(String label) {
		try {
			return ((LocalizationUtil) VaadinSession.getCurrent().getAttribute(
					LocalizationUtil.LOCALIZATIONUTIL_PROPERTY))
					.getString(label);
		} catch (Exception e) {
			return label;
		}
	}

	public static Language getCurrentLanguage() {
		return ((LocalizationUtil) VaadinSession.getCurrent().getAttribute(
				LocalizationUtil.LOCALIZATIONUTIL_PROPERTY)).getLanguage();
	}

	public static Language getLanguage(String prefLanguage) {
		if (prefLanguage == null)
			return Language.SWEDISH;

		String language;
		StringTokenizer st = new StringTokenizer(prefLanguage, ",");

		while (st.hasMoreElements()) {
			language = st.nextToken().trim();
			return Language.fromString(parseLocale(language));
		}
		return Language.SWEDISH;
	}

	private static String parseLocale(String language) {
		StringTokenizer st = new StringTokenizer(language, "-");
		if (st.countTokens() == 2)
			return st.nextToken();
		else
			return language;
	}

	public static void setLocalization(Language language) {
		logger.info("Set language to : " + language);
		VaadinSession.getCurrent().setAttribute(
				LocalizationUtil.LOCALIZATIONUTIL_PROPERTY,
				new LocalizationUtil(language));
	}
}
