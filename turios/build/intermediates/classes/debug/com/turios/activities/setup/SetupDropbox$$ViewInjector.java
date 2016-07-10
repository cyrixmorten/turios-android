// Generated code from Butter Knife. Do not modify!
package com.turios.activities.setup;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SetupDropbox$$ViewInjector {
  public static void inject(Finder finder, final com.turios.activities.setup.SetupDropbox target, Object source) {
    View view;
    view = finder.findById(source, 2131427382);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427382' for field 'progress' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.progress = (android.widget.ProgressBar) view;
    view = finder.findById(source, 2131427383);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427383' for field 'info' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.info = (android.widget.TextView) view;
    view = finder.findById(source, 2131427380);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427380' for field 'connect_button' and method 'onConnect' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.connect_button = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.onConnect((android.widget.Button) p0);
        }
      });
    view = finder.findById(source, 2131427379);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427379' for field 'continue_button' and method 'onContinue' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.continue_button = (android.widget.Button) view;
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
      throw new IllegalStateException("Required view with id '2131427378' for field 'back_button' and method 'onPrevious' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.back_button = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.onPrevious((android.widget.Button) p0);
        }
      });
    view = finder.findById(source, 2131427381);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427381' for field 'loading' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.loading = (android.widget.ProgressBar) view;
  }

  public static void reset(com.turios.activities.setup.SetupDropbox target) {
    target.progress = null;
    target.info = null;
    target.connect_button = null;
    target.continue_button = null;
    target.back_button = null;
    target.loading = null;
  }
}
