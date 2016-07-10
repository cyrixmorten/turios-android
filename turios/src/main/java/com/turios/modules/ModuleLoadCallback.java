package com.turios.modules;

public interface ModuleLoadCallback {

	public void startedLoadingModule(String module);
	public void doneLoadingModule(String module);
	
}
