package com.turios.modules.core;

import static com.turios.util.Constants.DELIMITER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.turios.persistence.MessagesContentProvider;
import com.turios.util.Constants;
import com.turios.util.Notifications;

@Singleton
public class DisplayCoreModule {

	private static final String TAG = "DisplayCoreModule";

	public interface DisplayCoreModuleCallback {
		public void pageAdded(int page, List<String> allEntries);

		public void pageUpdated(int page, List<String> allEntries);

		public void pageDeleted(int page);
	}

	private DisplayCoreModuleCallback displayCoreModuleCallback;

	private int currentPageSelected;
	private SortedMap<Integer, Long> mapPageId;
	// private SortedMap<Long, AddressHolder> mapPageidAddressHolder;

	private List<Long> pages_delay_delete;

	private final DatabaseCoreModule databaseCoreModule;
	private final LocationsCoreModule locationsCoreModule;
	private final ParseCoreModule parseCoreModule;
	private final Notifications notifications;

	@Inject public DisplayCoreModule(DatabaseCoreModule databaseCoreModule,
			LocationsCoreModule locationsCoreModule, Notifications notifications,
			ParseCoreModule parseCoreModule) {
		this.databaseCoreModule = databaseCoreModule;
		this.locationsCoreModule = locationsCoreModule;
		this.notifications = notifications;
		this.parseCoreModule = parseCoreModule;

		pages_delay_delete = new ArrayList<Long>();
		mapPageId = new TreeMap<Integer, Long>();
		// mapPageidAddressHolder = new TreeMap<Long, AddressHolder>();

		Cursor pages = databaseCoreModule.getAllPages();
		int index = 0;
		Log.d(TAG, "loading " + pages.getCount() + " pages");
		while (pages.moveToNext()) {
			long id = pages.getLong(pages
					.getColumnIndexOrThrow(MessagesContentProvider._ID));
			mapPageId.put(index, id);
			index++;
		}
		pages.close();
	}

	public void setDisplayListener(
			DisplayCoreModuleCallback displayCoreModuleCallback) {
		this.displayCoreModuleCallback = displayCoreModuleCallback;
	}
	
	public SortedMap<Integer, Long> getPagesToIdMap() {
		return mapPageId;
	}

	public boolean newMessage(String message) {
		String uniqueidString = message.substring(0,
				message.indexOf(Constants.DELIMITER)).trim();

		message = message.substring(message.indexOf(Constants.DELIMITER) + 1)
				.trim();
		message = message.replace("\n", "");

		notifications.displayNotification(message);
		
		long uniqueid = 0;
		try {

			uniqueid = Long.parseLong(uniqueidString);

		} catch (NumberFormatException e) {
			return false;
		}

		Cursor messageCursor = databaseCoreModule.getPageByUniqueId(uniqueid);

		boolean updateExistingPage = messageCursor.getCount() > 0;

		if (updateExistingPage) {

			// Log.d(TAG, "uniqueidString already existed in database");

			messageCursor.moveToNext();

			long id = messageCursor.getLong(messageCursor
					.getColumnIndexOrThrow(MessagesContentProvider._ID));
			String currentMessage = messageCursor.getString(messageCursor
					.getColumnIndexOrThrow(MessagesContentProvider.MESSAGE));
			messageCursor.close();

			ContentValues cv = new ContentValues();

			String fullMessage = currentMessage + DELIMITER + message;

			cv.put(MessagesContentProvider.MESSAGE, fullMessage);
			databaseCoreModule.updatePageById(cv, id);

			updatePage(id, message);

		} else {
			try {

				long id = databaseCoreModule.addPage(uniqueidString, message);

				addPage(id, message);
			} catch (NumberFormatException e) {
				return false;
			}
		}

		return true;
	}

	public void addPage(long id, String receivedEntries) {

		int page = getPagesCount();

		addPage(page, id);

		locationsCoreModule.requestLocationUpdates();

		parseCoreModule.newTurnout(id, receivedEntries);

		if (displayCoreModuleCallback != null) {
			displayCoreModuleCallback.pageAdded(page,
					processEntriesToArray(receivedEntries));
		}
	}

	public void updatePage(long id, String newEntries) {

		int page = findPageById(id);

		parseCoreModule.updateTurnout(id, newEntries);

		if (displayCoreModuleCallback != null)
			displayCoreModuleCallback.pageUpdated(page,
					processEntriesToArray(newEntries));
	}

	public void deletePage(long id) {
		deletePage(findPageById(id));
	}

	public void deletePage(int page) {

//		Log.d(TAG, "before deletePage: " + getPagesCount());
		
		completePageDelete(page);

		if (displayCoreModuleCallback != null) {
			displayCoreModuleCallback.pageDeleted(page);
		}
//		Log.d(TAG, "after deletePage: " + getPagesCount());
	}

	private void completePageDelete(int page) {
		long id = getPageId(page);

		mapPageId.remove(page);
		removePageFromDeletionDelayList(id);

		decrementIndexMapFrom(page);

		databaseCoreModule.deletePageById(id);

	}

	public int getCurrentPageSelected() {
		return currentPageSelected;
	}

	public void setCurrentPageSelected(int currentPageSelected) {
		this.currentPageSelected = currentPageSelected;
	}

	public List<String> processEntriesToArray(String message) {
		String[] entries = message.split("\\" + Constants.DELIMITER, -1);
		return new ArrayList<String>(Arrays.asList(entries));
	}

	public void clearPages() {
		mapPageId.clear();
	}

	public boolean containsPageId(long pageId) {
		return mapPageId.containsValue(pageId);
	}

	public long getLastPageId() {
		Long id = mapPageId.get(mapPageId.size() - 1);
		return (id != null) ? id : -1;
	}

	public long getPageId(int page) {
		Long id = mapPageId.get(page);
		return (id != null) ? id : -1;
	}

	public void addPage(int pagenumber, long id) {
		mapPageId.put(pagenumber, id);
	}

	public int getPagesCount() {
		return mapPageId.size();
	}

	public int findPageById(long _id) {
		for (int key : mapPageId.keySet()) {
			if (mapPageId.get(key) == _id) {
				return key;
			}
		}
		return -1;
	}

	/**
	 * Decrement indexes to align these with the display e.g. having 4 pages;
	 * 1,2,3,4 and removing 2 would leave the indexMap keys 1,3,4 hence not in
	 * line with the display. This method turns these keys into 1,2,3.
	 * 
	 * @param Page
	 *            from where decrement shall begin
	 */
	private void decrementIndexMapFrom(int fromPage) {
		SortedMap<Integer, Long> dec_indexMap = new TreeMap<Integer, Long>();
		Set<Integer> indexKeys = mapPageId.keySet();
		for (Integer key : indexKeys) {
			int dec_index = key;
			long id = mapPageId.get(key);

			if (key >= fromPage) {
				dec_index = key - 1;
			}

			dec_indexMap.put(dec_index, id);
		}

		mapPageId = dec_indexMap;
	}


	public boolean isPageDeletionDelayed(long page_id) {
		return pages_delay_delete.contains(page_id);
	}

	public int getPageDeletionDelay(long page_id) {
		if (isPageDeletionDelayed(page_id)) {
			return 1000 * 60 * 60 * 24;
		}
		return 0;
	}

	public List<Long> getPages_delay_delete() {
		return pages_delay_delete;
	}

	public void setPages_delay_delete(List<Long> pages_delay_delete) {
		this.pages_delay_delete = pages_delay_delete;
	}

	public void addPageToDelayList(long page_id) {
		pages_delay_delete.add(page_id);
	}

	private void removePageFromDeletionDelayList(long page_id) {
		pages_delay_delete.remove(page_id);
	}

}
