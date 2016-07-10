package com.turios.activities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.turios.BuildConfig;
import com.turios.R;
import com.turios.dagger.DaggerPreferenceActivity;
import com.turios.modules.core.ParseCoreModule;
import com.turios.settings.global.GeneralSettings;
import com.turios.util.Constants;

public class SettingsActivity extends DaggerPreferenceActivity {

	final static String TAG = "SettingsActivity";

	@Inject ParseCoreModule parse;

	@Override protected void onStart() {
		super.onStart();
		if (!BuildConfig.DEBUG) {
			EasyTracker.getInstance(this).activityStart(this);
		} else {
			GoogleAnalytics googleAnalytics = GoogleAnalytics
					.getInstance(getApplicationContext());
			googleAnalytics.setAppOptOut(true);
		}

	}

	@Override public void onBackPressed() {
		// just in case changes that affect screen has been made
		super.onBackPressed();
	}

	@Override protected void onStop() {
		super.onStop();
		if (!BuildConfig.DEBUG) {
			EasyTracker.getInstance(this).activityStop(this);
		}
	}

	@SuppressWarnings("unchecked") @Override protected void onCreate(
			Bundle savedInstanceState) {

		// as suggest by
		// http://stackoverflow.com/questions/15551673/android-headers-categories-in-preferenceactivity-with-preferencefragment
		if (onIsMultiPane())
			getIntent().putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
					GeneralSettings.class.getName());

		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// ActionBar actionBar = getActionBar();
		// actionBar.setTitle("SettingsActivity");
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState != null) {
			/*
			 * the headers must be restored before the super call in order to be
			 * ready for the call to setListAdapter()
			 */
			if (savedInstanceState.containsKey("headers")) {
				setHeaders((ArrayList<Header>) savedInstanceState
						.getSerializable("headers"));
			}
		}

		// actionBar.setTitle(R.string.prefs_settings);
		// actionBar.setDisplayHomeAsUpEnabled(true);

	}

	protected void onDestroy() {
		parse.getParseProfile().saveCurrentProfile(
				new GetCallback<ParseObject>() {

					@Override public void done(ParseObject object,
							ParseException e) {
						if (e == null) {
							Log.i(TAG, "Profile successfully saved!");
							parse.pushRefreshToProfile();
							sendBroadcast(new Intent(
									Constants.INTENT_REFRESH_MODULES));
							Toast.makeText(getApplicationContext(), getString(R.string.settings_changes_is_saving), Toast.LENGTH_SHORT).show();
						} else {
							Log.e(TAG, e.getMessage(), e);
						}
					}
				});
		super.onDestroy();
	};

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override protected void onResume() {
		super.onResume();

		// http://stackoverflow.com/questions/15551673/android-headers-categories-in-preferenceactivity-with-preferencefragment
		// Select the displayed fragment in the headers (when using a tablet) :
		// This should be done by Android, it is a bug fix
		if (getHeaders() != null) {

			final String displayedFragment = getIntent().getStringExtra(
					EXTRA_SHOW_FRAGMENT);
			if (displayedFragment != null) {
				for (final Header header : getHeaders()) {
					if (displayedFragment.equals(header.fragment)) {
						switchToHeader(header);
						break;
					}
				}
			}
		}

	}

	/**
	 * Populate the activity with the top-level headers.
	 */
	@Override public void onBuildHeaders(List<Header> target) {
		// we have to save the headers as the API call getHeaders() is hidden.
		setHeaders(target);
		loadHeadersFromResource(R.xml.settings_headers, target);
	}

	private List<Header> headers;

	private void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	private List<Header> getHeaders() {
		return headers;
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("headers",
				(ArrayList<PreferenceActivity.Header>) headers);
		super.onSaveInstanceState(outState);
	}

	@Override public void setListAdapter(ListAdapter adapter) {
		if (adapter == null) {
			super.setListAdapter(null);
		} else {
			super.setListAdapter(new HeaderAdapter(this, getHeaders()));
		}
	}

	private static class HeaderAdapter extends ArrayAdapter<Header> {
		static final int HEADER_TYPE_CATEGORY = 0;
		static final int HEADER_TYPE_NORMAL = 1;
		private static final int HEADER_TYPE_COUNT = HEADER_TYPE_NORMAL + 1;

		private static class HeaderViewHolder {
			ImageView icon;
			TextView title;
			TextView summary;
		}

		private LayoutInflater mInflater;

		static int getHeaderType(Header header) {
			if (header.fragment == null && header.intent == null) {
				return HEADER_TYPE_CATEGORY;
			} else {
				return HEADER_TYPE_NORMAL;
			}
		}

		@Override public int getItemViewType(int position) {
			Header header = getItem(position);
			return getHeaderType(header);
		}

		@Override public boolean areAllItemsEnabled() {
			return false; // because of categories
		}

		@Override public boolean isEnabled(int position) {
			return getItemViewType(position) != HEADER_TYPE_CATEGORY;
		}

		@Override public int getViewTypeCount() {
			return HEADER_TYPE_COUNT;
		}

		@Override public boolean hasStableIds() {
			return true;
		}

		public HeaderAdapter(Context context, List<Header> objects) {
			super(context, 0, objects);

			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override public View getView(int position, View convertView,
				ViewGroup parent) {
			HeaderViewHolder holder;
			Header header = getItem(position);
			int headerType = getHeaderType(header);
			View view = null;

			if (convertView == null) {
				holder = new HeaderViewHolder();
				switch (headerType) {
				case HEADER_TYPE_CATEGORY:
					view = new TextView(getContext(), null,
							android.R.attr.listSeparatorTextViewStyle);
					holder.title = (TextView) view;
					break;

				case HEADER_TYPE_NORMAL:
					view = mInflater.inflate(R.layout.preference_header_item,
							parent, false);
					holder.icon = (ImageView) view.findViewById(R.id.icon);
					holder.title = (TextView) view
							.findViewById(android.R.id.title);
					holder.summary = (TextView) view
							.findViewById(android.R.id.summary);
					break;
				}
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (HeaderViewHolder) view.getTag();
			}

			// All view fields must be updated every time, because the view may
			// be recycled
			switch (headerType) {
			case HEADER_TYPE_CATEGORY:
				holder.title.setText(header.getTitle(getContext()
						.getResources()));
				break;
			case HEADER_TYPE_NORMAL:
				holder.icon.setImageResource(header.iconRes);
				holder.title.setText(header.getTitle(getContext()
						.getResources()));
				CharSequence summary = header.getSummary(getContext()
						.getResources());
				if (!TextUtils.isEmpty(summary)) {
					holder.summary.setVisibility(View.VISIBLE);
					holder.summary.setText(summary);
				} else {
					holder.summary.setVisibility(View.GONE);
				}
				break;
			}

			return view;
		}

	}

}
