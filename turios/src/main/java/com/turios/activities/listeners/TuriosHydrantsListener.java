//package com.turios.activities.listeners;
//
//import java.util.List;
//
//import javax.inject.Inject;
//
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//
//import com.androidmapsextensions.GoogleMap;
//import com.google.analytics.tracking.android.Log;
//import com.turios.activities.fragments.task.LoadHydrantsToMapTaskFragment;
//import com.turios.modules.data.HydrantHolder;
//import com.turios.modules.extend.HydrantsModule.HydrantsModuleCallback;
//
//public class TuriosHydrantsListener implements HydrantsModuleCallback {
//
//	private final FragmentManager fm;
//
//	@Inject public TuriosHydrantsListener(FragmentManager fm) {
//		this.fm = fm;
//	}
//
//	@Override public void loadHydrantsToMap(List<HydrantHolder> hydrants,
//			GoogleMap map) {
//
//		LoadHydrantsToMapTaskFragment loadHydrantsFragment;
//
//		if (fm != null) {
//			FragmentTransaction ft = fm.beginTransaction();
//			loadHydrantsFragment = (LoadHydrantsToMapTaskFragment) fm
//					.findFragmentByTag(LoadHydrantsToMapTaskFragment.TAG);
//			if (loadHydrantsFragment != null) {
//				Log.i("Attatching LoadHydrantsToMapTaskFragment");
//				ft.attach(loadHydrantsFragment);
//			} else {
//				loadHydrantsFragment = LoadHydrantsToMapTaskFragment
//						.newInstance(hydrants, map);
//				Log.i("Adding new LoadHydrantsToMapTaskFragment");
//				ft.add(loadHydrantsFragment, LoadHydrantsToMapTaskFragment.TAG);
//			}
//			ft.commit();
//		}
//	}
// }
