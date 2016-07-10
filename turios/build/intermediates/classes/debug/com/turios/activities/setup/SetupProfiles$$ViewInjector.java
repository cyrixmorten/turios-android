// Generated code from Butter Knife. Do not modify!
package com.turios.activities.setup;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SetupProfiles$$ViewInjector {
  public static void inject(Finder finder, final com.turios.activities.setup.SetupProfiles target, Object source) {
    View view;
    view = finder.findById(source, 2131427388);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427388' for field 'content' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.content = (android.widget.LinearLayout) view;
    view = finder.findById(source, 2131427381);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427381' for field 'loading' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.loading = (android.widget.ProgressBar) view;
    view = finder.findById(source, 2131427389);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427389' for field 'noProfileInfo' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.noProfileInfo = (android.widget.TextView) view;
    view = finder.findById(source, 2131427379);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427379' for method 'onContinue' was not found. If this view is optional add '@Optional' annotation.");
    }
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.onContinue((android.widget.Button) p0);
        }
      });
    view = finder.findById(source, 2131427378);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427378' for method 'onPrevious' was not found. If this view is optional add '@Optional' annotation.");
    }
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.onPrevious((android.widget.Button) p0);
        }
      });
  }

  public static void reset(com.turios.activities.setup.SetupProfiles target) {
    target.content = null;
    target.loading = null;
    target.noProfileInfo = null;
  }
}
