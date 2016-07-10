//package com.turios.activities.fragments.task;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.Activity;
//import android.content.DialogInterface;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//
//import com.actionbarsherlock.app.SherlockDialogFragment;
//import com.androidmapsextensions.GoogleMap;
//import com.androidmapsextensions.MarkerOptions;
//import com.turios.R;
//
///**
// * This Fragment manages a single background task and retains itself across
// * configuration changes.
// */
//public class LoadHydrantsToMapTaskFragment extends SherlockDialogFragment {
//
//	public static final String TAG = LoadHydrantsToMapTaskFragment.class
//			.getSimpleName();
//
//	public interface LoadHydrantsToMapTaskCallback {
//		void onPreExecute(int maxProgress);
//
//		void onProgressUpdate(int progress);
//
//		void onCancelled();
//
//		void onPostExecute();
//	}
//
//	private LoadHydrantsToMapTask mTask;
//	private ProgressBar mProgressBar;
//
//	private List<MarkerOptions> mHydrants;
//
//	private GoogleMap map;
//
//	public static LoadHydrantsToMapTaskFragment newInstance(
//			List<MarkerOptions> hydrants, GoogleMap map) {
//		LoadHydrantsToMapTaskFragment taskFragment = new LoadHydrantsToMapTaskFragment();
//		taskFragment.mHydrants = hydrants;
//		taskFragment.map = map;
//
//		return taskFragment;
//	}
//
//	@Override public void onAttach(Activity activity) {
//		super.onAttach(activity);
//
//	}
//
//	@Override public View onCreateView(LayoutInflater inflater,
//			ViewGroup container, Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.dialog_progress_task, container);
//		mProgressBar = (ProgressBar) view.findViewById(R.id.progress_downloading);
//		mProgressBar.setProgress(0);
//		mProgressBar.setMax(mHydrants.size());
//
//		getDialog().setTitle(getActivity().getString(R.string.adding_hydrants));
//		// This dialog can't be canceled by pressing the back key.
//		getDialog().setCancelable(false);
//		getDialog().setCanceledOnTouchOutside(false);
//
//		return view;
//	}
//
//	/**
//	 * This method will only be called once when the retained Fragment is first
//	 * created.
//	 */
//	@Override public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setStyle(SherlockDialogFragment.STYLE_NORMAL, R.style.TuriosDialog);
//
//		// Retain this fragment across configuration changes.
//		// setRetainInstance(true);
//
//		mTask = new LoadHydrantsToMapTask(mHydrants);
//		mTask.setCallback(new LoadHydrantsToMapTaskCallback() {
//
//			@Override public void onPreExecute(int maxProgress) {
//			}
//
//			@Override public void onProgressUpdate(int progress) {
//				// mProgressBar.setProgress(progress);
//			}
//
//			@Override public void onPostExecute() {
//				if (isResumed())
//					dismiss();
//
//				mTask = null;
//
//			}
//
//			@Override public void onCancelled() {
//				if (isResumed())
//					dismiss();
//
//				mTask = null;
//			}
//		});
//
//		mTask.execute();
//	}
//
//	@Override public void onResume() {
//		super.onResume();
//
//		// This is a little hacky, but we will see if the task has finished
//		// while we weren't
//		// in this activity, and then we can dismiss ourselves.
//		if (mTask == null)
//			dismiss();
//	}
//
//	@Override public void onDetach() {
//		super.onDetach();
//	}
//
//	// This is to work around what is apparently a bug. If you don't have it
//	// here the dialog will be dismissed on rotation, so tell it not to dismiss.
//	@Override public void onDestroyView() {
//		if (getDialog() != null && getRetainInstance())
//			getDialog().setDismissMessage(null);
//		super.onDestroyView();
//	}
//
//	// Also when we are dismissed we need to cancel the task.
//	@Override public void onDismiss(DialogInterface dialog) {
//		super.onDismiss(dialog);
//		// If true, the thread is interrupted immediately, which may do bad
//		// things.
//		// If false, it guarantees a result is never returned (onPostExecute()
//		// isn't called)
//		// but you have to repeatedly call isCancelled() in your
//		// doInBackground()
//		// function to check if it should exit. For some tasks that might not be
//		// feasible.
//		if (mTask != null)
//			mTask.cancel(false);
//
//	}
//
//	private class LoadHydrantsToMapTask extends
//			AsyncTask<Void, Integer, List<MarkerOptions>> {
//		// Before running code in separate thread
//		List<MarkerOptions> mHydrants;
//		LoadHydrantsToMapTaskCallback mLoadHydrantsToMapTaskCallback;
//
//		public LoadHydrantsToMapTask(List<MarkerOptions> hydrants) {
//			this.mHydrants = hydrants;
//		}
//
//		public void setCallback(
//				LoadHydrantsToMapTaskCallback loadHydrantsToMapTaskCallback) {
//			this.mLoadHydrantsToMapTaskCallback = loadHydrantsToMapTaskCallback;
//		}
//
//		@Override protected void onPreExecute() {
//
//			if (mLoadHydrantsToMapTaskCallback != null) {
//				mLoadHydrantsToMapTaskCallback.onPreExecute(mHydrants.size());
//			}
//
//		}
//
//		// The code to be executed in a background thread.
//		@Override protected List<MarkerOptions> doInBackground(Void... arg) {
//			List<MarkerOptions> markers = new ArrayList<MarkerOptions>();
//
//			for (MarkerOptions marker : mHydrants) {
//
//				map.addMarker(marker);
//
//				publishProgress(markers.size());
//			}
//			return markers;
//		}
//
//		// Update the progress
//		@Override protected void onProgressUpdate(Integer... values) {
//			if (mLoadHydrantsToMapTaskCallback != null) {
//				mLoadHydrantsToMapTaskCallback.onProgressUpdate(values[0]);
//			}
//		}
//
//		@Override protected void onCancelled() {
//			if (mLoadHydrantsToMapTaskCallback != null) {
//				mLoadHydrantsToMapTaskCallback.onCancelled();
//			}
//		}
//
//		// after executing the code in the thread
//		@Override protected void onPostExecute(List<MarkerOptions> markers) {
//
//			if (mLoadHydrantsToMapTaskCallback != null) {
//				mLoadHydrantsToMapTaskCallback.onPostExecute();
//			}
//
//		}
//
//	}
//
// }
