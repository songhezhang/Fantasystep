package com.fantasystep.form;

import com.fantasystep.component.common.PopUpModel;

public interface WizardHelper {
	abstract PopUpModel getCurrentStep();

	abstract public void wizardBack();

	abstract public void wizardCancel();

	abstract public void wizardNext();

	abstract public void wizardSave();
}
