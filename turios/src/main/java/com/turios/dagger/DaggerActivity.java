/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.turios.dagger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewConfiguration;

import com.parse.BuildConfig;
import com.parse.ParseAnalytics;
import com.turios.dagger.modules.ActivityModule;

import dagger.ObjectGraph;

/**
 * Base activity which sets up a per-activity object graph and performs
 * injection.
 */
public abstract class DaggerActivity extends FragmentActivity {
	private ObjectGraph activityGraph;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");

			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			// presumably, not relevant
		}

		// Create the activity graph by .plus-ing our modules onto the
		// application graph.
		DaggerApplication application = (DaggerApplication) getApplication();
		activityGraph = application.getApplicationGraph().plus(
				getModules().toArray());

		// Inject ourselves so subclasses will have dependencies fulfilled when
		// this method returns.
		activityGraph.inject(this);

		if (!BuildConfig.DEBUG)
			ParseAnalytics.trackAppOpened(getIntent());

	}

	@Override protected void onDestroy() {
		// Eagerly clear the reference to the activity graph to allow it to be
		// garbage collected as
		// soon as possible.
		activityGraph = null;

		super.onDestroy();
	}

	/**
	 * A list of modules to use for the individual activity graph. Subclasses
	 * can override this method to provide additional modules provided they call
	 * and include the modules returned by calling {@code super.getModules()}.
	 */
	protected List<Object> getModules() {
		return Arrays.<Object> asList(new ActivityModule(this));
	}

	/** Inject the supplied {@code object} using the activity-specific graph. */
	public void inject(Object object) {
		activityGraph.inject(object);
	}
}
