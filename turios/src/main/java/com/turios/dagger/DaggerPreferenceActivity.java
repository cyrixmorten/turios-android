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

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.turios.dagger.modules.ActivityPreferenceModule;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Base activity which sets up a per-activity object graph and performs
 * injection.
 */
public abstract class DaggerPreferenceActivity extends PreferenceActivity {
	private ObjectGraph activityGraph;

	@Override protected void onCreate(Bundle savedInstanceState) {

		// call to super moved down in order to enable instantiation of header
		// in multipane mode
		super.onCreate(savedInstanceState);

		// Create the activity graph by .plus-ing our modules onto the
		// application graph.
		DaggerApplication application = (DaggerApplication) getApplication();
		activityGraph = application.getApplicationGraph().plus(
				getModules().toArray());

		// Inject ourselves so subclasses will have dependencies fulfilled when
		// this method returns.
		activityGraph.inject(this);

	}

	@Override protected void onDestroy() {
		// Eagerly clear the reference to the activity graph to allow it to be
		// garbage collected as
		// soon as possible.
		activityGraph = null;

		super.onDestroy();
	}

    @Override
    protected boolean isValidFragment (String fragmentName) {
        return true;
    }

	/**
	 * A list of modules to use for the individual activity graph. Subclasses
	 * can override this method to provide additional modules provided they call
	 * and include the modules returned by calling {@code super.getModules()}.
	 */
	protected List<Object> getModules() {
		return Arrays.<Object> asList(new ActivityPreferenceModule(this));
	}

	/** Inject the supplied {@code object} using the activity-specific graph. */
	public void inject(Object object) {
		activityGraph.inject(object);
	}
}
