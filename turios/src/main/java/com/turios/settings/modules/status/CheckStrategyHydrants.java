package com.turios.settings.modules.status;

import java.util.List;

import android.content.Context;
import android.preference.Preference;

import com.turios.modules.extend.HydrantsModule;
import com.turios.modules.extend.StandardModule;

public class CheckStrategyHydrants extends CheckStrategyStandard {

	private HydrantsModule module;
	
	public CheckStrategyHydrants(Context context, HydrantsModule hydrantsModule) {
		super(context, hydrantsModule);
		
		// just because I dont want to cast the module every time
		this.module = hydrantsModule;
	}
	
	@Override
	public List<Preference> checkStatus() {
		List<Preference> status = super.checkStatus();
		// TODO Auto-generated method stub
		return status;
	}

	@Override
	public List<Preference> checkWarnings() {
		List<Preference> warnings = super.checkWarnings();
		// TODO Auto-generated method stub		
		return warnings;
	}
	
	@Override
	public List<Preference> checkFatalErrors() {
		List<Preference> errors = super.checkFatalErrors();
		// TODO Auto-generated method stub
		return errors;
	}
	
	@Override
	public boolean checks(StandardModule module) {
		return this.module.equals(module);
	}


}
