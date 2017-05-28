package com.turios.activities.setup;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.turios.R;
import com.turios.dagger.DaggerActivity;
import com.turios.modules.extend.AccessplansModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetupPDFViewer extends DaggerActivity {

	private static final String TAG = "SetupPDFViewer";

	@BindView(R.id.setup_list_pdfviewers) LinearLayout listpdfviewers;
	@BindView(R.id.setup_install_pdfviewer) LinearLayout installpdfviewer;

	@BindView(R.id.button_continue) Button continue_button;
	@BindView(R.id.setup_installed_pdfviewers) TextView pdfviewers;

	@BindView(R.id.setup_pdfinstall_market) Button market;

	@Inject ActionBar actionbar;
	@Inject AccessplansModule accessplansModule;

	@OnClick(R.id.button_continue) public void onContinue(Button button) {
		startActivity(WizardHelper.getNextScreen());
		SetupPDFViewer.this.finish();
	}

	@OnClick(R.id.button_back) public void onPrevious(Button button) {
		startActivity(WizardHelper.getPreviousScreen());
		SetupPDFViewer.this.finish();
	}

	@OnClick(R.id.setup_pdfinstall_market) public void marketInstall(
			Button button) {
		Intent marketIntent = new Intent(Intent.ACTION_VIEW);
		marketIntent.setData(Uri.parse("market://details?id=com.adobe.reader"));
		startActivity(marketIntent);
	}

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_pdfviewer);

		ButterKnife.bind(this);

		actionbar.setTitle("PDF");

		this.setFinishOnTouchOutside(false);

		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override protected void onResume() {

		if (!accessplansModule.canDisplayPdf()) {
			pdfviewers.setTextColor(Color.RED);
			pdfviewers.setText("Ingen fundet");
			continue_button.setEnabled(false);
		} else {
			StringBuffer b = new StringBuffer();
			for (String info : accessplansModule.getPdfViewersInstalled()) {
				b.append(info + "\n");
				if (info.toLowerCase(Locale.US).contains("adobe")) {
					installpdfviewer.setVisibility(View.GONE);
				}
			}
			pdfviewers.setText(b.toString());
			continue_button.setEnabled(true);
		}
		super.onResume();
	}

	@Override public void onBackPressed() {
		// do nothing
	}

	private class InstallAdobeReader extends AsyncTask<Void, Void, String> {

		@Override protected String doInBackground(Void... params) {
			String filename = "adobereader.apk";

			HttpURLConnection c;
			try {
				URL url = new URL("http://turios.dk/" + filename);
				c = (HttpURLConnection) url.openConnection();
				c.setRequestMethod("GET");
				c.setDoOutput(true);
				c.connect();
			} catch (IOException e1) {
				return e1.getMessage();
			}

			File myFilesDir = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/Download");

			File adobeFile = new File(myFilesDir, filename);

			if (adobeFile.exists()) {
				adobeFile.delete();
			}

			if ((myFilesDir.mkdirs() || myFilesDir.isDirectory())) {
				try {
					InputStream is = c.getInputStream();
					FileOutputStream fos = new FileOutputStream(myFilesDir
							+ "/" + filename);

					byte[] buffer = new byte[1024];
					int len1 = 0;
					while ((len1 = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len1);
					}
					fos.close();
					is.close();

				} catch (Exception e) {
					return e.getMessage();
				}

				if (adobeFile.exists()) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(adobeFile),
							"application/vnd.android.package-archive");
					startActivity(intent);
					return getApplicationContext().getString(R.string.file_successfully_downloaded);
				} else {
					return getApplicationContext().getString(R.string.could_not_locate_file);
				}
			} else {
				return getApplicationContext().getString(R.string.could_not_create_or_access_downloadfolder);
			}
		}

		@Override protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
					.show();
			super.onPostExecute(result);
		}
	}
}
