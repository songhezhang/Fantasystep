package com.fantasystep.component.panel;

import java.io.IOException;

import com.fantasystep.localization.LocalizationUtil;
import com.fantasystep.localization.LocalizationUtil.Language;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public abstract class AbstractCApplication extends UI {

	private static final long serialVersionUID = -2682764252461477615L;

	public static final String LANGUAGE = "language";

	abstract protected void closeSession();

	abstract protected String getLocalizationId();

	@Override
	protected void init(VaadinRequest request) {
		String language = request.getParameter(LANGUAGE);

		Language preferedLanguage;
		if (language != null)
			preferedLanguage = Language.valueOf(language);
		else
			preferedLanguage = LocalizationHandler.getLanguage(request
					.getHeader("Accept-Language"));

		if (preferedLanguage != null) {
			try {
				LocalizationUtil.load(getLocalizationId(), preferedLanguage);
			} catch (IOException e) {
				e.printStackTrace();
			}
			LocalizationHandler.setLocalization(preferedLanguage);
		}
		initSession();
	}

	abstract protected void initSession();
	
	@Override
	public void close() {
		super.close();
		closeSession();
	}
}
