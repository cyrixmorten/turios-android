package com.turios.modules.extend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.turios.R;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.ModulePreferences;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.PathsCoreModule;
import com.turios.modules.preferences.AccessplansModulePreferences;
import com.turios.persistence.Preferences;
import com.turios.util.Constants;

@Singleton
public class AccessplansModule extends StandardModule {

	private final AccessplansModulePreferences modulePreferences;



	public AccessplansModulePreferences getPreferences() {
		return modulePreferences;
	}

	private static final String TAG = "AccessplansModule";

	private final PathsCoreModule paths;

	@Inject public AccessplansModule(@ForApplication Context context,
			Preferences preferences, ExpirationCoreModule expiration,
			ParseCoreModule parse, PathsCoreModule paths) {
		super(context, preferences, expiration, parse, MODULES.ACCESSPLANS);

		Log.i(TAG, "CREATING MODULE " + TAG);

		this.paths = paths;
		this.modulePreferences = new AccessplansModulePreferences(preferences);
	}

	public static final String MIME_TYPE_PDF = "application/pdf";

	public boolean isAccessplan(String name) {
		File file = new File(paths.PATH_LOCAL_ACCESSPLANS + "/" + name
				+ Constants.EXTENSION_PDF);
		return file.exists();
	}

	public void viewAccessplan(String name) {
		File file = new File(paths.PATH_LOCAL_ACCESSPLANS + "/" + name
				+ Constants.EXTENSION_PDF);
		Log.d(TAG, "viewAccessplan " + Uri.fromFile(file));
		try {
			Intent intent = new Intent();
			intent.setPackage("com.adobe.reader");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), MIME_TYPE_PDF);
			context.startActivity(intent);
		} catch (ActivityNotFoundException activityNotFoundException) {
			Log.d(TAG, "com.adobe.reader not found");
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), MIME_TYPE_PDF);
			context.startActivity(intent);
		}
	}

	public void showDownloadPdfviewerDialog() {
		// No application to view, ask to download one
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.no_pdfviewer_installed));
		builder.setMessage(context
				.getString(R.string.confirm_download_from_google_play));
		builder.setPositiveButton(context.getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog,
							int which) {
						Intent marketIntent = new Intent(Intent.ACTION_VIEW);
						marketIntent.setData(Uri
								.parse("market://details?id=com.adobe.reader"));
						context.startActivity(marketIntent);
					}
				});
		builder.setNegativeButton(context.getString(android.R.string.no), null);
		builder.create().show();
	}

	/**
	 * Check if the supplied context can render PDF files via some installed
	 * application that reacts to a intent with the pdf mime type and viewing
	 * action.
	 * 
	 * @param context
	 * @return
	 */
	public boolean canDisplayPdf() {

		if (getPdfViewersInstalled().size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<String> getPdfViewersInstalled() {
		List<String> viewers = new ArrayList<String>();
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(MIME_TYPE_PDF);
		for (ResolveInfo ri : packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY)) {
			viewers.add(ri.loadLabel(packageManager).toString());
		}
		return viewers;
	}

}
