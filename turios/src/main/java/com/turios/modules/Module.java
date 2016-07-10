package com.turios.modules;

import com.turios.settings.modules.status.CheckStrategy;

public interface Module {

	public enum MODULES {
		BASIS, DROPBOX, PICKLISTS, ACCESSPLANS, GOOGLEMAPS, HYDRANTS, DIRECTIONS, BROWSER
	}

	public ModulePreferences getPreferences();

	public CheckStrategy getCheckStrategy();

	public boolean isPaid();

	/**
	 * @return is paid and not expired
	 */
	public boolean isEnabled();

	public void setActivated(boolean activated);

	public void setModuleLoadCallback(ModuleLoadCallback moduleCallback);

	public void loadIfEnabled();

	public void load();

	public void loadStarted();

	public void loadEnded();

}
