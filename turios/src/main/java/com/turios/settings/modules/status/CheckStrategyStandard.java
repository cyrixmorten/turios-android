package com.turios.settings.modules.status;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.preference.Preference;

import com.turios.R;
import com.turios.modules.extend.StandardModule;

public class CheckStrategyStandard implements CheckStrategy {
	
	protected Context context;
	protected StandardModule module;
	
	public CheckStrategyStandard(Context context, StandardModule module) {
		this.context = context;
		this.module = module;
	}

	@Override
	public List<Preference> checkStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Preference> checkWarnings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Preference> checkFatalErrors() {
		List<Preference> errorEditTextPrefs = new ArrayList<Preference>();
		// check if activated
//		if (!module.isActivated()) {
//			errorEditTextPrefs.add(EditTextPrefBuilder.getBuilder(context)
//					.setTitle("Ikke aktiveret").setSummary("Dette modul er ikke aktiveret").build());
//		}
		// check if enabled
		if (!module.isPaid()) {
			errorEditTextPrefs.add(EditTextPrefBuilder.getBuilder(context)
					.setTitle(context.getString(R.string.check_not_paid)).setSummary(context.getString(R.string.check_module_not_paid)).build());			
		}
		else if (!module.isActivated()) {
			errorEditTextPrefs.add(EditTextPrefBuilder.getBuilder(context)
					.setTitle(context.getString(R.string.check_not_enabled)).setSummary(context.getString(R.string.check_module_disabled)).build());				
		}
		return errorEditTextPrefs;
	}

	@Override
	public boolean checks(StandardModule module) {
		return this.module.equals(module);
	}


}
