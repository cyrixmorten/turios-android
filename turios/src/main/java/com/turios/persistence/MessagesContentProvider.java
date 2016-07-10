/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.turios.persistence;

import java.util.HashMap;
import java.util.Locale;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;


public class MessagesContentProvider extends ContentProvider {

	private DBAdapter dbHelper;
	private static HashMap<String, String> MESSAGES_PROJECTION_MAP;
	private static final String TABLE_NAME = "messages";
	private static final String AUTHORITY = "com.turios.persistence.messagescontentprovider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase(Locale.US));
	public static final Uri UNIQUEID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase(Locale.US) + "/uniqueid");
	public static final Uri MESSAGE_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase(Locale.US) + "/message");
	public static final Uri TIMESTAMP_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase(Locale.US)
					+ "/timestamp");

	public static final String DEFAULT_SORT_ORDER = "_id ASC";

	private static final UriMatcher URL_MATCHER;

	private static final int MESSAGES = 1;
	private static final int MESSAGES__ID = 2;
	private static final int MESSAGES_UNIQUEID = 3;
	private static final int MESSAGES_MESSAGE = 4;
	private static final int MESSAGES_TIMESTAMP = 5;

	// Content values keys (using column names)
	public static final String _ID = "_id";
	public static final String UNIQUEID = "uniqueid";
	public static final String MESSAGE = "message";
	public static final String TIMESTAMP = "timestamp";

	public boolean onCreate() {
		dbHelper = new DBAdapter(getContext(), true);
		return (dbHelper == null) ? false : true;
	}

	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteDatabase mDB = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (URL_MATCHER.match(url)) {
		case MESSAGES:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(MESSAGES_PROJECTION_MAP);
			break;
		case MESSAGES__ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;
		case MESSAGES_UNIQUEID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("uniqueid='" + url.getPathSegments().get(2) + "'");
			break;
		case MESSAGES_MESSAGE:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("message='" + url.getPathSegments().get(2) + "'");
			break;
		case MESSAGES_TIMESTAMP:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("timestamp='" + url.getPathSegments().get(2) + "'");
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		String orderBy = "";
		if (TextUtils.isEmpty(sort)) {
			orderBy = DEFAULT_SORT_ORDER;
		} else {
			orderBy = sort;
		}
		Cursor c = qb.query(mDB, projection, selection, selectionArgs, null,
				null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	public String getType(Uri url) {
		switch (URL_MATCHER.match(url)) {
		case MESSAGES:
			return "vnd.android.cursor.dir/vnd.cyrix.turios.database.messages";
		case MESSAGES__ID:
			return "vnd.android.cursor.item/vnd.cyrix.turios.database.messages";
		case MESSAGES_UNIQUEID:
			return "vnd.android.cursor.item/vnd.cyrix.turios.database.messages";
		case MESSAGES_MESSAGE:
			return "vnd.android.cursor.item/vnd.cyrix.turios.database.messages";
		case MESSAGES_TIMESTAMP:
			return "vnd.android.cursor.item/vnd.cyrix.turios.database.messages";

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}

	public Uri insert(Uri url, ContentValues initialValues) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		long rowID;
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		if (URL_MATCHER.match(url) != MESSAGES) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		rowID = mDB.insert("messages", "messages", values);
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + url);
	}

	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		int count;
		String segment = "";
		switch (URL_MATCHER.match(url)) {
		case MESSAGES:
			count = mDB.delete(TABLE_NAME, where, whereArgs);
			break;
		case MESSAGES__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.delete(TABLE_NAME,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MESSAGES_UNIQUEID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"uniqueid="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MESSAGES_MESSAGE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"message="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MESSAGES_TIMESTAMP:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"timestamp="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		int count;
		String segment = "";
		switch (URL_MATCHER.match(url)) {
		case MESSAGES:
			count = mDB.update(TABLE_NAME, values, where, whereArgs);
			break;
		case MESSAGES__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.update(TABLE_NAME, values,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MESSAGES_UNIQUEID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"uniqueid="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MESSAGES_MESSAGE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"message="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MESSAGES_TIMESTAMP:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"timestamp="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	static {
		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(Locale.US), MESSAGES);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(Locale.US) + "/#",
				MESSAGES__ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(Locale.US) + "/uniqueid"
				+ "/*", MESSAGES_UNIQUEID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(Locale.US) + "/message"
				+ "/*", MESSAGES_MESSAGE);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(Locale.US) + "/timestamp"
				+ "/*", MESSAGES_TIMESTAMP);

		MESSAGES_PROJECTION_MAP = new HashMap<String, String>();
		MESSAGES_PROJECTION_MAP.put(_ID, "_id");
		MESSAGES_PROJECTION_MAP.put(UNIQUEID, "uniqueid");
		MESSAGES_PROJECTION_MAP.put(MESSAGE, "message");
		MESSAGES_PROJECTION_MAP.put(TIMESTAMP, "timestamp");

	}
}
