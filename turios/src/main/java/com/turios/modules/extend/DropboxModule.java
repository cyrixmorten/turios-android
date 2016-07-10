package com.turios.modules.extend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileStatus;
import com.dropbox.sync.android.DbxFileStatus.PendingOperation;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.SyncStatusListener;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.dropbox.sync.android.DbxSyncStatus;
import com.turios.R;
import com.turios.dagger.quialifiers.ForApplication;
import com.turios.modules.ModulePreferences;
import com.turios.modules.core.ExpirationCoreModule;
import com.turios.modules.core.ParseCoreModule;
import com.turios.modules.core.PathsCoreModule;
import com.turios.modules.preferences.DropboxModulePreferences;
import com.turios.modules.utils.AssetFiles;
import com.turios.modules.utils.DropboxWriter;
import com.turios.persistence.Preferences;
import com.turios.util.Constants;

@Singleton
public class DropboxModule extends StandardModule {

	private final DropboxModulePreferences modulePreferences;



	public DropboxModulePreferences getPreferences() {
		return modulePreferences;
	}

	private static final String TAG = "DropboxModule";

	public interface DropboxCallback {
		public void syncStarted(int totalfiles);

		public void syncEnded();

		public void syncDownloaded(int filecount);

		public void syncCanceled(String cause);
	}

	private DropboxCallback dropboxCallback;

	public void setDropboxListener(DropboxCallback dropboxCallback) {
		this.dropboxCallback = dropboxCallback;
	}

	public static final String SYNC_FILES_COUNT_TOTAL = "extra_dropbox_files_total";
	public static final String SYNC_FILES_COUNT_CURRENT = "extra_dropbox_files_current";

	private static DbxFileSystem dbxFs;
	private int sync_items_expected = 0;
	private int sync_items_count = 0;

	private boolean createdFolderOrFile = false;

	private final PathsCoreModule pathsCoreModule;

	@Inject public DropboxModule(@ForApplication Context context,
			Preferences preferences, ExpirationCoreModule expiration,
			ParseCoreModule parse, PathsCoreModule paths) {
		super(context, preferences, expiration, parse, MODULES.DROPBOX);

		Log.i(TAG, "CREATING MODULE " + TAG);

		this.pathsCoreModule = paths;
		this.modulePreferences = new DropboxModulePreferences(preferences);
	}

	@Override public void load() {

		super.loadStarted();

		DbxAccountManager mDbxAcctMgr = DbxAccountManager.getInstance(
				context.getApplicationContext(), Constants.DROPBOX_APPKEY,
				Constants.DROPBOX_SECRETKEY);
		try {
			if (mDbxAcctMgr.hasLinkedAccount()
					&& mDbxAcctMgr.getLinkedAccount().isLinked()) {
				dbxFs = DbxFileSystem
						.forAccount(mDbxAcctMgr.getLinkedAccount());
				createFilesAndFolders();
			} else if (!preferences.isFirstStartUp()) {

				// TODO create dialog asking if user wishes to connect to
				// Dropbox or disable Dropbox module

				// Intent intent = new Intent(context,
				// SetupDropbox.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.putExtra("settings", true);
				// context.startActivity(intent);
			}
		} catch (Unauthorized e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (InvalidPathException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (DbxException e) {
			Log.e(TAG, e.getMessage(), e);
		}

		super.loadEnded();

	}

	public DbxFileSystem getDbxFileSystem() {
		return dbxFs;
	}

	// public long getLast_dropbox_sync() {
	// return last_dropbox_sync;
	// }
	//
	// public void setLast_dropbox_sync(long last_dropbox_sync) {
	// this.last_dropbox_sync = last_dropbox_sync;
	// }

	/**
	 * @return if this is the last file that has been downloaded.
	 */
	public void fileDownloaded() {

		sync_items_count++;

		if (dropboxCallback != null) {

			dropboxCallback.syncDownloaded(sync_items_count
					);

			if (sync_items_expected == sync_items_count) {
				dropboxCallback.syncEnded();
			}
		}
	}

	public boolean isSynchronizing() {
		return sync_items_expected != sync_items_count;
	}

	private List<DbxFileInfo> listFiles(DbxPath folder,
			List<DbxFileInfo> filelist) {
		try {
			for (DbxFileInfo dbxInfo : dbxFs.listFolder(folder)) {
				// Log.d("listFiles", dbxInfo.path.getName());
				if (dbxInfo.isFolder) {
					// update Dropbox path
					DbxPath path = new DbxPath(folder, dbxInfo.path.getName());

					listFiles(path, filelist);
				} else {

					filelist.add(dbxInfo);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return filelist;
	}

	private String unfoldPath(DbxPath path, String unfoldedpath) {
		// are we at the root
		if (path.equals(DbxPath.ROOT)) {
			return unfoldedpath;
		}

		return unfoldPath(path.getParent(), path.getParent().getName() + "/"
				+ unfoldedpath);
	}

	public void synchronize(final DbxPath path,
			final DropboxFileDownloaderResultReceiver receiver) {

		if (dbxFs == null) {
			if (dropboxCallback != null)
				dropboxCallback.syncCanceled("No connection to Dropbox");
			return;
		}

		sync_items_count = 0;

		try {
			if (!dbxFs.hasSynced()) {
				dbxFs.addSyncStatusListener(new SyncStatusListener() {
					
					@Override
					public void onSyncStatusChange(DbxFileSystem sys) {	
						try {
							if (!sys.getSyncStatus().anyInProgress()) {
								new SyncTask(path, receiver).execute();
								dbxFs.removeSyncStatusListener(this);
							}
						} catch (DbxException e) {
							e.printStackTrace();
						}
					}
				});
			} else {
				new SyncTask(path, receiver).execute();
			}
		} catch (DbxException e) {
			e.printStackTrace();
		}

	}

	public void synchronize(final DbxPath path) {
		synchronize(path,
				new DropboxFileDownloaderResultReceiver(new Handler()));

	}

	private class SyncTask extends AsyncTask<Void, Void, List<Intent>> {
		List<DbxFileInfo> filesList = null;

		private String turiosHome;
		private DbxPath path;
		private DropboxFileDownloaderResultReceiver receiver;

		public SyncTask(DbxPath path,
				DropboxFileDownloaderResultReceiver receiver) {
			this.turiosHome = pathsCoreModule.PATH_TURIOS_HOME;
			this.path = path;
			this.receiver = receiver;
		}

		@Override protected void onPreExecute() {
			// setLast_dropbox_sync(System.currentTimeMillis());
			filesList = listFiles(path, new ArrayList<DbxFileInfo>());
			
		};

		List<String> filesOnDropbox = new ArrayList<String>();

		@Override protected List<Intent> doInBackground(Void... params) {
			List<Intent> intents = new ArrayList<Intent>();
			try {
				if (dbxFs.hasSynced()) {
					int count = 1;
					for (DbxFileInfo fileInfo : filesList) {

						// String folderpath =
						// fileInfo.path.getParent().getName();
						String folderpath = unfoldPath(
								fileInfo.path.getParent(), fileInfo.path
										.getParent().getName());
						String filename = fileInfo.path.getName();

						filesOnDropbox.add(folderpath + "/" + filename);

						// String fullpath = folderpath + "/" + filename;
						// Log.e(TAG, fullpath);

						Intent i = new Intent(context,
								DropboxFileDownloader.class);
						i.putExtra(Constants.EXTRA_RECEIVER, receiver);
						i.putExtra(Constants.EXTRA_HOME, turiosHome);
						i.putExtra(Constants.EXTRA_FOLDERPATH, folderpath);
						i.putExtra(Constants.EXTRA_FILENAME, filename);
						i.putExtra(SYNC_FILES_COUNT_TOTAL, filesList.size());
						i.putExtra(SYNC_FILES_COUNT_CURRENT, count);
						intents.add(i);

						count++;
					}

				} else {
					Toast.makeText(
							context,
							context.getString(R.string.dropbox_not_ready_try_again_later),
							Toast.LENGTH_SHORT).show();
				}
			} catch (DbxException e) {
			}
			return intents;
		}

		protected void onPostExecute(List<Intent> intents) {
			// send expected fileupdates count back to activity
			sync_items_expected = intents.size();
			
			if (dropboxCallback != null)
				dropboxCallback.syncStarted(sync_items_expected);

			// start downloading updates
			for (Intent i : intents) {
				context.startService(i);
			}

			// System.out.println("Files on Dropbox");
			// for (String file: filesOnDropbox) {
			// System.out.println(file);
			// }
			//
			// System.out.println("Run cleanUp");
			// and clean up files that does not exist in dropbox
			if (modulePreferences.deleteFilesDuringSync()) {
				File turiosRoot = new File(turiosHome);
				cleanUp(turiosRoot);
			}
		};

		int spc_count = -1;

		private void cleanUp(File aFile) {
			spc_count++;
			String spcs = "";
			for (int i = 0; i < spc_count; i++)
				spcs += " ";
			if (aFile.isFile()) {
				String absoluteFilePath = aFile.getAbsolutePath();
				String filePath = absoluteFilePath.replace(turiosHome, "");
				boolean delete = !filesOnDropbox.contains(filePath);
				if (delete) {
					aFile.delete();
					Toast.makeText(
							context,
							filePath + " "
									+ context.getString(R.string.deleted),
							Toast.LENGTH_SHORT).show();
				}
			} else if (aFile.isDirectory()) {
				System.out.println(spcs + "[DIR] " + aFile.getName());
				File[] listOfFiles = aFile.listFiles();
				if (listOfFiles != null) {
					for (int i = 0; i < listOfFiles.length; i++)
						cleanUp(listOfFiles[i]);
				} else {
					System.out.println(spcs + " [ACCESS DENIED]");
				}
			}
			spc_count--;
		}

	}

	public boolean createFilesAndFolders() throws InvalidPathException,
			DbxException {

		createdFolderOrFile = false;

		if (dbxFs != null) {

			setIfFalse(createFolderIfMissing(dbxFs, new DbxPath(DbxPath.ROOT,
					pathsCoreModule.PATH_DROPBOX_PICKLIST)));
			setIfFalse(createFolderIfMissing(dbxFs, new DbxPath(DbxPath.ROOT,
					pathsCoreModule.PATH_DROPBOX_ACCESSPLANS)));
			setIfFalse(createFolderIfMissing(dbxFs, new DbxPath(DbxPath.ROOT,
					pathsCoreModule.PATH_DROPBOX_HYDRANTS)));

			DbxPath picklist_path = new DbxPath(
					pathsCoreModule.FILEPATH_DROPBOX_PICKLIST);
			if (!fileExists(dbxFs, picklist_path)) {
				DbxFile picklist = dbxFs.create(picklist_path);
				boolean success = DropboxWriter.writeAssetText(context,
						AssetFiles.PICKLIST, picklist);
				setIfFalse(success);
			}

			DbxPath accessplan_path = new DbxPath(
					pathsCoreModule.FILEPATH_DROPBOX_ACCESSPLANEXAMPLE);
//			if (!fileExists(dbxFs, accessplan_path)) {
//				DbxFile accessplan = dbxFs.create(accessplan_path);
//				boolean success = DropboxWriter.writeAssetBinary(context,
//						AssetFiles.TKP, accessplan);
//				setIfFalse(success);
//			}
			return createdFolderOrFile;
		}
		return false;
	}

	private void setIfFalse(boolean value) {
		if (!createdFolderOrFile) {
			createdFolderOrFile = value;
		}
	}

	private boolean createFolderIfMissing(DbxFileSystem dbxFs, DbxPath folder)
			throws DbxException {
		if (!dbxFs.exists(folder)) {
			dbxFs.createFolder(folder);
			return true;
		}
		return false;
	}

	private boolean fileExists(DbxFileSystem dbxFs, DbxPath filepath) {
		try {
			DbxFile file = dbxFs.open(filepath);
			file.close();
			return true;
		} catch (DbxException e) {
			return false;
		}
	}

	public interface DropboxFileDownloaderReceiver {
		public void onDropboxFileDownloadReceiveResult(int resultCode,
				Bundle resultData);

	}

	public class DropboxFileDownloaderResultReceiver extends ResultReceiver {

		public static final int RESULTCODE_ERROR = -1;
		public static final int RESULTCODE_CREATED = 1;
		public static final int RESULTCODE_UPDATED = 2;
		public static final int RESULTCODE_UPTODATE = 3;

		private DropboxFileDownloaderReceiver mReceiver;

		public DropboxFileDownloaderResultReceiver(Handler handler) {
			super(handler);
		}

		public void setReceiver(DropboxFileDownloaderReceiver receiver) {
			mReceiver = receiver;
		}

		@Override protected void onReceiveResult(int resultCode,
				Bundle resultData) {

			fileDownloaded();

			if (mReceiver != null) {
				mReceiver.onDropboxFileDownloadReceiveResult(resultCode,
						resultData);
			}
		}

	}

	public static class DropboxFileDownloader extends IntentService {

		private final String TAG = DropboxFileDownloader.class.getName();

		public DropboxFileDownloader() {
			super("FileDownloader");
		}

		private ResultReceiver receiver;

		private Bundle bundle;

		@Override protected void onHandleIntent(Intent intent) {

			String turiosHome = intent.getStringExtra(Constants.EXTRA_HOME);
			String fileName = intent.getStringExtra(Constants.EXTRA_FILENAME);
			String folderPath = intent
					.getStringExtra(Constants.EXTRA_FOLDERPATH);

			bundle = intent.getExtras();

			receiver = intent.getParcelableExtra(Constants.EXTRA_RECEIVER);

			String fullpath = folderPath + "/" + fileName;

			DbxFile file;
			long fileModified = 0;
			long fileSize = 0;

			try {
				file = dbxFs.open(new DbxPath(fullpath));
				DbxFileStatus fileStatus = file.getNewerStatus();
				if (fileStatus != null && !fileStatus.isLatest) {
					if (!fileStatus.isCached) {
						Log.d(TAG, "File was not yet cached: " + fileName);

						while (file.getNewerStatus().pending == PendingOperation.DOWNLOAD) {
							Log.d(TAG, "Waiting for " + fileName
									+ " to be downloaded");
							Thread.sleep(1000);
						}

						Log.d(TAG, "File is now cached: " + fileStatus.isCached);

						file.update();
					} else {
						Log.d(TAG, "File was cached: " + fileName);
					}
				}
				fileModified = file.getInfo().modifiedTime.getTime();
				fileSize = file.getInfo().size;

			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				sendErrorMsg(e);
				return;
			}

			File stored_dir = new File(turiosHome + "/" + folderPath);
			if (!stored_dir.exists()) {
				stored_dir.mkdirs();
			}

			File stored_file = new File(turiosHome + "/" + folderPath, fileName);

			// File stored_file = getFileStreamPath(fileName);
			long local_modified = stored_file.lastModified();
			long local_size = stored_file.length();

			boolean should_sync = (fileModified > local_modified)
					|| fileSize != local_size;// && Math.abs(fileModified -
												// local_modified) >
												// TimeUnit.MILLISECONDS.convert(1,
												// TimeUnit.MINUTES);
			boolean fileexists = stored_file.exists();
			if (should_sync || !fileexists) {

				InputStream inputStream = null;
				FileOutputStream out = null;
				try {
					// read this file into InputStream
					inputStream = file.getReadStream();

					out = new FileOutputStream(stored_file);
					int read = 0;
					byte[] bytes = new byte[1024];

					int bytes_counter = 0;
					while ((read = inputStream.read(bytes)) != -1) {
						out.write(bytes, 0, read);
						bytes_counter++;
					}

					if (!fileexists) {
						bundle.putString(Constants.EXTRA_FILEPATH, fullpath);
						receiver.send(
								DropboxFileDownloaderResultReceiver.RESULTCODE_CREATED,
								bundle);
					} else {
						bundle.putString(Constants.EXTRA_FILEPATH, fullpath);
						receiver.send(
								DropboxFileDownloaderResultReceiver.RESULTCODE_UPDATED,
								bundle);
					}

				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
					sendErrorMsg(e);
				} finally {
					try {
						if (inputStream != null) {
							inputStream.close();
						}
						if (out != null) {
							out.flush();
							out.close();
						}

					} catch (IOException e) {
						Log.e(TAG, e.getMessage(), e);
						sendErrorMsg(e);
					}

				}
			} else {
				bundle.putString(Constants.EXTRA_FILEPATH, fullpath);
				receiver.send(
						DropboxFileDownloaderResultReceiver.RESULTCODE_UPTODATE,
						bundle);
			}

			file.close();
		}

		private void sendErrorMsg(Exception e) {
			bundle.putString(Constants.EXTRA_MESSAGE, e.getMessage());
			receiver.send(DropboxFileDownloaderResultReceiver.RESULTCODE_ERROR,
					bundle);
		}
	}
}
