package com.turios.settings.modules.status;

import android.content.Context;
import android.preference.EditTextPreference;

public class EditTextPrefBuilder {

	private Context context;

	private String title;
	private String summary;
	private boolean enabled;

	public static EditTextPrefBuilder getBuilder(Context context) {
		return new EditTextPrefBuilder(context);
	}

	EditTextPreference ep = null;
	
	public EditTextPreference build() {
//		Handler handler = new Handler(Looper.getMainLooper());
//		handler.post(new Runnable() {
//			
//			@Override
//			public void run() {
				ep = new EditTextPreference(context);
				ep.setTitle(title);
				ep.setSummary(summary);
				ep.setEnabled(enabled);
//				
//			}
//		});
		
		return ep;

	}

	private EditTextPrefBuilder(Context context) {
		super();
		this.context = context;
		this.title = "";
		this.summary = "";
		this.enabled = false;
	}

	public EditTextPrefBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public EditTextPrefBuilder setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	public EditTextPrefBuilder setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

}
