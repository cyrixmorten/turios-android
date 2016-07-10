package com.turios.modules.core;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.turios.dagger.quialifiers.ForApplication;
import com.turios.persistence.MessagesContentProvider;

@Singleton
public class DatabaseCoreModule {

	private static final String TAG = "DatabaseCoreModule";

	private final Context context;

	@Inject public DatabaseCoreModule(@ForApplication Context context) {
		this.context = context;
	}

	public long addPage(String uniqueid, String message) {
		long now = System.currentTimeMillis();
		ContentValues cv = new ContentValues();
		cv.put(MessagesContentProvider.UNIQUEID, uniqueid);
		cv.put(MessagesContentProvider.MESSAGE, message);
		cv.put(MessagesContentProvider.TIMESTAMP, now);

		Uri messageUri = context.getContentResolver().insert(
				MessagesContentProvider.CONTENT_URI, cv);

		return ContentUris.parseId(messageUri);
	}

	public int deletePageById(long id) {
		return context.getContentResolver().delete(
				MessagesContentProvider.CONTENT_URI,
				MessagesContentProvider._ID + "=?",
				new String[] { String.valueOf(id) });
	}

	public Cursor getAllPages() {
		return context.getContentResolver().query(
				MessagesContentProvider.CONTENT_URI, null, null, null,
				MessagesContentProvider.TIMESTAMP);
	}

	public int getAllPagesCount() {
		Cursor pages = context.getContentResolver().query(
				MessagesContentProvider.CONTENT_URI, null, null, null,
				MessagesContentProvider.TIMESTAMP);
		int count = pages.getCount();
		pages.close();
		return count;
	}

	public Cursor getPageByUniqueId(long uniqueid) {
		return context.getContentResolver().query(
				MessagesContentProvider.CONTENT_URI, null,
				MessagesContentProvider.UNIQUEID + " = ?",
				new String[] { String.valueOf(uniqueid) }, null);
	}

	public Cursor getPageById(long id) {
		return context.getContentResolver().query(
				MessagesContentProvider.CONTENT_URI, null,
				MessagesContentProvider._ID + " = ?",
				new String[] { String.valueOf(id) }, null);
	}

	public int updatePageById(ContentValues cv, long id) {
		return context.getContentResolver().update(
				MessagesContentProvider.CONTENT_URI, cv,
				MessagesContentProvider._ID + " = ?",
				new String[] { String.valueOf(id) });
	}

}
