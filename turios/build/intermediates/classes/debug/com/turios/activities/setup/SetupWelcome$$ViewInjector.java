// Generated code from Butter Knife. Do not modify!
package com.turios.activities.setup;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SetupWelcome$$ViewInjector {
  public static void inject(Finder finder, final com.turios.activities.setup.SetupWelcome target, Object source) {
    View view;
    view = finder.findById(source, 2131427394);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427394' for field 'unitname' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.unitname = (android.widget.EditText) view;
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
  }

  public static void reset(com.turios.activities.setup.SetupWelcome target) {
    target.unitname = null;
  }
}
