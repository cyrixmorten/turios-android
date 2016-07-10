package com.turios.modules.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.dropbox.sync.android.DbxFile;

public class DropboxWriter {

	private static final String TAG = "DropboxWriter";
	
	public static boolean writeAssetBinary(Context context, String assetFile,
			DbxFile toFile) {

		String tempsdpath = Environment.getExternalStorageDirectory()
				.toString() + "/temp";

		AssetManager assetManager = context.getAssets();

		OutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(assetFile);
			outputStream = new FileOutputStream(tempsdpath + "/" + assetFile);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}

		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			if (outputStream != null) {

				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			File file = new File(tempsdpath, assetFile);
			toFile.writeFromExistingFile(file, false);
			return true;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			toFile.close();
		}
		return false;
	}
	
	public static boolean writeAssetText(Context context, String assetFile,
			DbxFile toFile) {

		AssetManager assetManager = context.getAssets();
		ByteArrayOutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(assetFile);
			outputStream = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}

		try {
			toFile.writeString(outputStream.toString());
			return true;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			toFile.close();
		}
		return false;
	}
}
