package com.turios.settings.modules.status;

import java.util.List;

import com.turios.modules.extend.StandardModule;

import android.preference.Preference;

public interface CheckStrategy {

	public boolean checks(StandardModule module);
	public List<Preference> checkStatus();
	public List<Preference> checkWarnings();
	public List<Preference> checkFatalErrors();
}
