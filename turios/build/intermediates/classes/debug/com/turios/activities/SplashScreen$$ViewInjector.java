// Generated code from Butter Knife. Do not modify!
package com.turios.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SplashScreen$$ViewInjector {
  public static void inject(Finder finder, final com.turios.activities.SplashScreen target, Object source) {
    View view;
    view = finder.findById(source, 2131427344);
    target.progressBar = (android.widget.ProgressBar) view;
    view = finder.findById(source, 2131427342);
    target.logo = (android.widget.ImageView) view;
  }

  public static void reset(com.turios.activities.SplashScreen target) {
    target.progressBar = null;
    target.logo = null;
  }
}
