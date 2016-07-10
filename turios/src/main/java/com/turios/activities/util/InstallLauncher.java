package com.turios.activities.util;

import java.io.File;

import javax.inject.Inject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class InstallLauncher {

	private final Activity activity;

	@Inject public InstallLauncher(Activity activity) {
		super();
		this.activity = activity;
	}

	public void launchInstaller(String apkPath) {
		launchInstaller(new File(apkPath));
	}

	public void launchInstaller(File apkFile) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkFile),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivityForResult(intent, 0);
	}

}
