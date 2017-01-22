package com.turios.modules.extend;

import android.content.Context;
import android.util.Log;

import com.turios.modules.Module;
import com.turios.modules.ModuleLoadCallback;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.persistence.Preferences;
import com.turios.settings.modules.status.CheckStrategy;
import com.turios.settings.modules.status.CheckStrategyStandard;

public abstract class StandardModule implements Module {

	private static final String TAG = "StandardModule";

	protected ModuleLoadCallback moduleCallback;

	protected MODULES module;

	protected final Context context;
	protected final ExpirationCoreModule expirationCoreModule;
	protected final ParseCoreModule parseCoreModule;
	protected final Preferences preferences;

	public StandardModule(Context context, Preferences preferences,
			ExpirationCoreModule expiration, ParseCoreModule parse,
			MODULES module) {
		this.context = context;
		this.preferences = preferences;
		this.expirationCoreModule = expiration;
		this.parseCoreModule = parse;
		this.module = module;
	}

	public void setModuleLoadCallback(ModuleLoadCallback moduleCallback) {
		this.moduleCallback = moduleCallback;
	}

	public void loadStarted() {
		if (moduleCallback != null) {
			moduleCallback.startedLoadingModule(module.toString());
		}
	}

	public void loadEnded() {
		if (moduleCallback != null) {
			moduleCallback.doneLoadingModule(module.toString());
		}
	}

	@Override public void load() {
		loadEnded();
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StandardModule other = (StandardModule) obj;
		if (module != other.module)
			return false;
		return true;
	}

	@Override public String toString() {
		return module.toString();
	}

	@Override public CheckStrategy getCheckStrategy() {
		return new CheckStrategyStandard(context, this);
	}

	public void loadIfEnabled() {
		final long timeStarted = System.currentTimeMillis();

		if (isEnabled()) {

			load();

			long timeEnded = System.currentTimeMillis();
			Log.d(TAG, "Done loading: " + module + " in "
					+ (timeEnded - timeStarted) + "ms");
		} else {

			loadStarted();

			Log.w(TAG, "Module not enabled: " + module);

			loadEnded();
		}
	}

	public boolean isPaid() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

	public void setActivated(boolean activated) {
		preferences.setModuleAcitve(module, activated);
	}

	public boolean isActivated() {
		return isEnabled() && preferences.isModuleAcitve(module);
	}

}
