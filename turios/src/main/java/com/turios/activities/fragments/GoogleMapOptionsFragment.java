package com.turios.activities.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import butterknife.ButterKnife;

import com.turios.R;
import com.turios.dagger.DaggerFragment;
import com.turios.modules.extend.GoogleMapsModule;
import com.turios.modules.extend.HydrantsModule;
import com.turios.modules.extend.HydrantsModule.HydrantOptionsChanged;
import com.turios.modules.extend.HydrantsModule.HydrantType;

public class GoogleMapOptionsFragment extends DaggerFragment implements
		HydrantOptionsChanged {

	private static final String TAG = GoogleMapOptionsFragment.class
			.getSimpleName();

	private List<ToggleButton> toggleButtons;
	@Inject HydrantsModule hydrants;
	@Inject GoogleMapsModule googlemap;

	// private View root;

	@Override public void onStart() {
		super.onStart();
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toggleButtons = new ArrayList<ToggleButton>();
	}

	@Override public void onResume() {
		super.onResume();
	}

	@Override public void onPause() {
		super.onPause();
	}

	@Override public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout.fragment_mapsoptions, container,
				false);

		ButterKnife.inject(this, root);

		return root;
	}

	@Override public void onViewStateRestored(Bundle savedInstanceState) {
		LinearLayout mapOptionsLayout = ButterKnife.findById(getView(),
				R.id.map_options);

		Set<HydrantType> hydrantTypes = hydrants.getHydrantTypes();

		// if (savedInstanceState == null) {
		for (HydrantType hydrantType : hydrantTypes) {
			boolean checked = hydrants.isHydrantTypeEnabled(hydrantType);
			ToggleButton tb = new ToggleButton(getActivity());
			tb.setButtonDrawable(android.R.color.white);
			tb.setTag(hydrantType);
			tb.setChecked(checked);
			tb.setText(hydrantType.getName());
			tb.setTextOn(hydrantType.getName());
			tb.setTextOff(hydrantType.getName());
			tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override public void onCheckedChanged(
						CompoundButton buttonView, boolean isChecked) {

					HydrantType type = (HydrantType) buttonView.getTag();
					if (isChecked) {
						hydrants.enableHydrantType(type);
					} else {
						hydrants.disableHydrantType(type);
					}
					googlemap.clearMap(true);
					hydrants.addEnabledHydrants();
				}
			});
			mapOptionsLayout.addView(tb);
			toggleButtons.add(tb);
		}
		;
		hydrants.setHydrantsOptionsChangedListener(this);

		super.onViewStateRestored(savedInstanceState);
	}

	@Override public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
		hydrants.setHydrantsOptionsChangedListener(null);
	}

	@Override public void enableAllHydrants() {
		for (ToggleButton tb : toggleButtons) {
			tb.setChecked(true);
		}
	}

	@Override public void disableAllHydrants() {
		Log.d(TAG, "disableAllHydrants " + toggleButtons.size());
		for (ToggleButton tb : toggleButtons) {
			tb.setChecked(false);
		}
	}

}
