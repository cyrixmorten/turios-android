// Generated code from Butter Knife. Do not modify!
package com.turios.activities.setup;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SetupPDFViewer$$ViewInjector {
  public static void inject(Finder finder, final com.turios.activities.setup.SetupPDFViewer target, Object source) {
    View view;
    view = finder.findById(source, 2131427384);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427384' for field 'listpdfviewers' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.listpdfviewers = (android.widget.LinearLayout) view;
    view = finder.findById(source, 2131427386);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427386' for field 'installpdfviewer' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.installpdfviewer = (android.widget.LinearLayout) view;
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
    view = finder.findById(source, 2131427385);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427385' for field 'pdfviewers' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.pdfviewers = (android.widget.TextView) view;
    view = finder.findById(source, 2131427387);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427387' for field 'market' and method 'marketInstall' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.market = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.marketInstall((android.widget.Button) p0);
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

  public static void reset(com.turios.activities.setup.SetupPDFViewer target) {
    target.listpdfviewers = null;
    target.installpdfviewer = null;
    target.continue_button = null;
    target.pdfviewers = null;
    target.market = null;
  }
}
