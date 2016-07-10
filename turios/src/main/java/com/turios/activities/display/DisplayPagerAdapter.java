package com.turios.activities.display;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.turios.R;
import com.turios.activities.fragments.DisplayFragment;
import com.turios.modules.core.DisplayCoreModule;

	public class DisplayPagerAdapter extends FragmentStatePagerAdapter  {
	
		private static final String TAG = "DisplayPagerAdapter";
	
		SparseArray<DisplayFragment> registeredFragments = new SparseArray<DisplayFragment>();
	
		private final Context context;
		private final DisplayCoreModule display;
		private final FragmentManager fm;
		
		private boolean isAddOrRemoving;
	
		public DisplayPagerAdapter(Context context, FragmentManager fm,
				DisplayCoreModule display) {
			super(fm);
			this.context = context;
			this.display = display;
			this.fm = fm;
	
			Log.d(TAG, "pages " + display.getPagesCount());
		}
	
		
		public void notifySizeChangingDataSetChange() {
			isAddOrRemoving = true;
			notifyDataSetChanged();
			isAddOrRemoving = false;
		}
		
		@Override
		public int getCount() {
			int count = (display != null && display.getPagesCount() > 0) ? display
					.getPagesCount() : 1;
			return count;
		}
	
		@Override
		public int getItemPosition(Object object) {
			DisplayFragment frag = (DisplayFragment) object;
			if (!display.containsPageId(frag.getPageId())) {
				// this will update the 'no information' page with id -1
				return POSITION_NONE;
			}
			if (isAddOrRemoving) {
				// recreate all for simplicity
				return POSITION_NONE;
			}
			return POSITION_UNCHANGED;
		}
	
		@Override
		public Fragment getItem(int position) {
			Log.d(TAG, "getItem " + position);
			return DisplayFragment.newInstance(position);
		}
	
		@Override
		public CharSequence getPageTitle(int position) {
			if (display != null && display.getPagesCount() > 0) {
				return context.getString(R.string.page) + " " + (position + 1);
			} else {
				return super.getPageTitle(position);
			}
		}
	
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.d(TAG, "instantiateItem " + position);
			DisplayFragment fragment = (DisplayFragment) super.instantiateItem(
					container, position);
			registeredFragments.put(position, fragment);
			return fragment;
		}
	
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.d(TAG, "destroyItem " + position);
			registeredFragments.remove(position);
			super.destroyItem(container, position, object);
		}
	
		public Fragment getRegisteredFragment(int position) {
			return registeredFragments.get(position);
		}
	
		public SparseArray<DisplayFragment> getRegisteredFragments() {
			return registeredFragments;
		}
	
	}
