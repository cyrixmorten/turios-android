package com.turios.modules.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.turios.dagger.quialifiers.ForApplication;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PathsCoreModule {

	private static final String TAG = "PathsCoreModule";

	private final Context context;

	@Inject public PathsCoreModule(@ForApplication Context context) {
		this.context = context;

		if (hasExternalStorage()) {

			String external_sd = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			setPathToSD(external_sd);
			createDirs();
		}
	}

	public boolean hasExternalStorage() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	private void setPathToSD(String pathToSD) {

		PATH_TURIOS_HOME = pathToSD + "/Turios";

		PATH_LOCAL_PICKLIST = PATH_TURIOS_HOME + "/" + PATH_DROPBOX_PICKLIST;
		PATH_LOCAL_ACCESSPLANS = PATH_TURIOS_HOME + "/"
				+ PATH_DROPBOX_ACCESSPLANS;
		PATH_LOCAL_HYDRANTS = PATH_TURIOS_HOME + "/" + PATH_DROPBOX_HYDRANTS;
		PATH_LOCAL_HYDRANTS_ICONS = PATH_TURIOS_HOME + "/"
				+ PATH_DROPBOX_HYDRANTS_ICONS;

		FILEPATH_LOCAL_DATA = pathToSD + "/.tdata";
		FILEPATH_LOCAL_PICKLIST = PATH_LOCAL_PICKLIST + "/picklist.txt";
		FILEPATH_LOCAL_HYDRANTS = PATH_LOCAL_HYDRANTS + "/hydrants";
		FILEPATH_LOCAL_HYDRANTS_ICONSMAP = PATH_LOCAL_HYDRANTS
				+ "/icons/iconsmap.txt";
		FILEPATH_LOCAL_PREFERENCES = PATH_TURIOS_HOME + "/.preferences";
	}

	public String PATH_TURIOS_HOME;

	public final String PATH_DROPBOX_PICKLIST = "Picklist";
	public final String PATH_DROPBOX_ACCESSPLANS = "Accessplans";
	public final String PATH_DROPBOX_HYDRANTS = "Hydrants";
	public final String PATH_DROPBOX_HYDRANTS_ICONS = "Hydrants/icons";

	public final String FILEPATH_DROPBOX_PICKLIST = PATH_DROPBOX_PICKLIST
			+ "/picklist.txt";
	public final String FILEPATH_DROPBOX_ACCESSPLANEXAMPLE = PATH_DROPBOX_ACCESSPLANS
			+ "/TKP.pdf";
	// public final String FILEPATH_DROPBOX_PREFERENCES = "/.preferences";
	public final String FILEPATH_DROPBOX_HYDRANTS = PATH_DROPBOX_HYDRANTS
			+ "/hydrants";
	public final String FILEPATH_DROPBOX_HYDRANTS_ICONSMAP = "Hydrants/icons/iconsmap.txt";

	public String PATH_LOCAL_PICKLIST;
	public String PATH_LOCAL_ACCESSPLANS;
	public String PATH_LOCAL_HYDRANTS;
	public String PATH_LOCAL_HYDRANTS_ICONS;

	public String FILEPATH_LOCAL_DATA;
	public String FILEPATH_LOCAL_PICKLIST;
	public String FILEPATH_LOCAL_HYDRANTS;
	public String FILEPATH_LOCAL_PREFERENCES;
	public String FILEPATH_LOCAL_HYDRANTS_ICONSMAP;

	private void createDirs() {

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {

			File picklistDir = new File(PATH_LOCAL_PICKLIST);
			File accessplansDir = new File(PATH_LOCAL_ACCESSPLANS);
			File hydrantsDir = new File(PATH_LOCAL_HYDRANTS);

			picklistDir.mkdirs();
			accessplansDir.mkdirs();
			hydrantsDir.mkdirs();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(new File("file://" + PATH_TURIOS_HOME)); //out is your output file
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);
            } else {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
                        .parse("file://" + PATH_TURIOS_HOME)));
            }

		}

	}

}
