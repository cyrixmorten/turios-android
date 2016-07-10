package com.turios.activities.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.turios.BuildConfig;
import com.turios.R;
import com.turios.dagger.DaggerFragment;
import com.turios.dagger.quialifiers.ForActivity;
import com.turios.modules.extend.BrowserModule;
import com.turios.modules.preferences.BrowserModulePreferences;
import com.turios.util.Constants;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class BrowserFragment extends DaggerFragment {

    private static final String TAG = "BrowserFragment";

    public static int NAVIGATION_INDEX = 0;

    @Inject
    @ForActivity
    Context context;

    @Inject
    BrowserModule browserModule;

    private WebView webview;
    private Bundle webViewBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG) {
            Tracker tracker = GoogleAnalytics.getInstance(context).getTracker(
                    Constants.GATracker);

            tracker.send(MapBuilder.createAppView()
                    .set(Fields.SCREEN_NAME, "Browser View").build());
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        webview = (WebView) inflater.inflate(R.layout.fragment_browser,
                container, false);

        ButterKnife.inject(this, webview);

        return webview;
    }

    @Override
    public void onPause() {
        super.onPause();

        webViewBundle = new Bundle();
        webview.saveState(webViewBundle);
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        webview.setWebViewClient(new MyWebViewClient());

        // if (webViewBundle != null) {
        // Log.d(TAG, "Restoring webview");
        // webview.restoreState(webViewBundle);
        // } else {
        Log.d(TAG, "Creating webview");

        final BrowserModulePreferences browserPrefs = browserModule
                .getPreferences();

        webview.getSettings().setPluginState(PluginState.ON);
        webview.getSettings().setSupportZoom(browserPrefs.supportZoom());
        webview.getSettings().setBuiltInZoomControls(
                browserPrefs.builtInZoomControls());
        webview.getSettings().setDisplayZoomControls(
                browserPrefs.displayZoomControls());
        webview.getSettings().setJavaScriptEnabled(
                browserPrefs.javaScriptEnabled());
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(
                browserPrefs.javaScriptCanOpenWindowsAutomatically());
        webview.getSettings()
                .setAllowFileAccess(browserPrefs.allowFileAccess());

//		webview.getSettings().setDomStorageEnabled(
//				browserPrefs.domStorageEnabled());
        webview.getSettings().setDomStorageEnabled(true);


//        webview.loadUrl("javascript:window.onload = function(){" +
//                "document.getElementsByName('" + browserPrefs.inputName1() + "')[0].value = '" + browserPrefs.inputValue1() + "');" +
//                "document.forms[0].submit();" +
//                "};");


        webview.setWebViewClient(new WebViewClient() {


            public void onPageFinished(WebView view, String url) {
                if (browserPrefs.autoSubmitEnabled()) {
                    view.loadUrl("javascript: {"
                            + "var input1 = document.getElementsByName('" + browserPrefs.inputName1() + "')[0];"
                            + " if (input1) input1.value = '" + browserPrefs.inputValue1() + "';"
                            + " var input2 = document.getElementsByName('" + browserPrefs.inputName2() + "')[0];"
                            + " if (input2) input2.value = '" + browserPrefs.inputValue2() + "';"
                            + "var frms = document.forms;"
                            + "if (frms && frms.length > 0) frms[0].submit(); };");
                }
//					view.loadUrl("javascript:document.getElementsByName('"
//							+ browserPrefs.inputName1() + "')[0].value = '"
//							+ browserPrefs.inputValue1() + "'");
//					view.loadUrl("javascript:document.getElementsByName('"
//							+ browserPrefs.inputName2() + "')[0].value = '"
//							+ browserPrefs.inputValue2() + "'");
//					view.loadUrl("javascript:document.forms[0].submit()");
            }
        });


        webview.loadUrl(browserPrefs.url());
        // }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,
                                                String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}
