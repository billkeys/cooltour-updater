/**
 * Copyright (c) 2010 Contributors, http://geoplicity.org/
 * See CONTRIBUTORS.TXT for a full list of copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Geoplicity Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE DEVELOPERS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;
import geoplicity.cooltour.util.Utilities;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This activity retrieves and presents the site update details. It will be
 * invoked when a user selects a site from the site list. Specific site actions
 * will be handled by this activity, and the available actions will depend
 * on the update's state as follows: <br>
 * 1)The site is up-to-date - Display site info (i.e. version) <br>
 * 2)The site is new or downlevel - Display a 'start' button to begin 
 * the update.<br>
 * 3)Update in progress - Display a progress bar, and pause/cancel buttons <br>
 * 4)Update in progress (paused) - Display a progress bar, and a resume button <br>
 * 5)Update Complete - Display update info, i.e. total time. <br>
 * 6)Update Failed - Display error message, and a restart button
 * 
 * @author Brendon Drew (b.j.drew@gmail.com)
 * 
 */
public class SiteUpdateDetails extends Activity {
	/**
	 * Menu item for launching "MaunUI" Activity
	 */
	private static final int MAIN_UI_ID = Menu.FIRST;
	/**
	 * Menu item for launching "About Geoplicity" Activity
	 */
	private static final int ABOUT_ID = Menu.FIRST + 1;
	/**
	 * Details for the site being displayed
	 */
	static SiteUpdateData sUpdate;
	/**
	 * The index of the selected site from the SiteList Activity
	 */
	static int sSelectedSiteIndex;
	/**
	 * The thread
	 */
	static SiteUpdateThread sUpdateThread;
	/**
	 * The thread that updates this activity's view
	 */
	private ViewThread mViewThread;

	/**
	 * @param savedInstanceState
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(Constants.LOG_TAG, "SiteUpdateDetails onCreate()");
	}

	/**
	 * Get the site update data. If the thread hasn't started, retrieve the data
	 * from the server otherwise get it from the thread.
	 * 
	 * @param site
	 * @return
	 */
	public SiteUpdateData getSiteUpdateData(SiteData site) {
		Log.d(Constants.LOG_TAG, "SiteUpdateDetails getSiteUpdateData()");
		if (site == null)
			return null;
		Log.d(Constants.LOG_TAG,
				"SiteUpdateDetails getSiteUpdateData() incoming site:"
						+ site.toString());
		SiteUpdateData su = null;
		//Get it from the SiteUpdateManager if it has an instance
		if (SiteUpdateManager.getInstance(this).containsUpdate(site.getName())) {
			su = SiteUpdateManager.getInstance(this)
					.getUpdateThread(site.getName()).getUpdateData();
		} else {
			//Build the URL
			String siteUpdateProperties = Constants.UPDATE_SERVER
					+ site.getName() + "/" + site.getNewVersion() + "/"
					+ site.getName() + Constants.UPDATE_FILE_EXT;
			//Build the SiteUpdateData instance from the remote file.
			try {
				su = new SiteUpdateData(siteUpdateProperties, site);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "Failed to get site update data", e);
			}
		}
		return su;
	}

	/**
	 * Called when the user presses the start/pause button which doubles
	 * as a toggle between running and not running.
	 * 
	 * @param v
	 */
	public void toggleRun(View v) {
		SiteUpdateManager mgr = SiteUpdateManager.getInstance();
		//Get the thread.  This will create the thread instance if it
		//did not previously exist.
		SiteUpdateThread uThread = mgr.getUpdateThread(sUpdate, this);
		//Check state, and act accordingly.
		if (!uThread.isAlive()) {
			Log.v(Constants.LOG_TAG, " starting update " + sUpdate.getName()
					+ "");
			mgr.startUpdate(sUpdate, this);
		} else {
			Log.v(Constants.LOG_TAG, " pausing update " + sUpdate.getName()
					+ "");
			uThread.interrupt();
		}
		//Start the thread that monitors the thread and updates the view.
		startViewUpdater();
	}

	/**
	 * Called when the user presses the cancel button
	 * @param v
	 */
	public void cancelUpdate(View v) {
		Log.v(Constants.LOG_TAG, " cancelling update " + sUpdate.getName() + "");
		SiteUpdateThread upd = SiteUpdateManager.getInstance().getUpdateThread(
				sUpdate, this);
		//Set the cancel flag before interrupting so some cleanup can take place.
		upd.setCancel(true);
		upd.interrupt();
	}

	/**
	 * Start the thread that monitors the update.
	 */
	private void startViewUpdater() {
		mViewThread = new ViewThread();
		mViewThread.start();

	}

	/**
	 * Updates the current view based on the state of the update
	 * TODO cleanup by making the layout elements instance variables
	 */
	private void updateView() {
		setContentView(R.layout.site_update_details);
		
		// Get the layout elements
		TextView name = (TextView) findViewById(R.id.site_update_details_name);
		TextView version = (TextView) findViewById(R.id.site_update_details_version);
		TextView blocks = (TextView) findViewById(R.id.site_update_details_blocks);
		TextView progress = (TextView) findViewById(R.id.site_update_progress);
		TextView size = (TextView) findViewById(R.id.site_update_details_size);
		TextView statusText = (TextView) findViewById(R.id.site_update_status);
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.update_progress_bar);
		ProgressBar progressSpinner = (ProgressBar) findViewById(R.id.update_progress_spinner);
		Button toggleRunButton = (Button) findViewById(R.id.site_update_toggle_button);
		Button cancelButton = (Button) findViewById(R.id.site_update_cancel_button);
		
		// Set defaults
		name.setText(sUpdate.getName().replaceAll("_", " "));
		version.setText(sUpdate.getVersion());
		blocks.setText(sUpdate.getBlockCount() + "");
		progressBar.setVisibility(View.GONE);
		progressSpinner.setVisibility(View.GONE);
		size.setText(Utilities.parseBytesToHumanString(sUpdate.getFileSize()));
		statusText.setVisibility(View.VISIBLE);
		statusText.setText(sUpdate.getStatusMessage());
		progress.setVisibility(View.INVISIBLE);
		
		// Calculate the download percentage
		int downloadPercentage = 0;
		if (sUpdate.getCurrentBlock() > 1) {
			double p = (((double) sUpdate.getCurrentBlock() - 1.0) / (double) sUpdate
					.getBlockCount()) * 100.0;
			downloadPercentage = (int) p;
		}
		
		// Update elements based on the update's state
		if (sUpdate.isUpdateComplete()) {
			toggleRunButton.setVisibility(View.GONE);
			cancelButton.setVisibility(View.GONE);
		} else if (sUpdate.isUpdateInProgress()) {
			toggleRunButton.setText(R.string.pause_update);
			toggleRunButton.setVisibility(View.VISIBLE);
			cancelButton.setVisibility(View.VISIBLE);
			if (sUpdate.getCurrentMode() > SiteUpdateThread.MODE_RESUME) {
				progressSpinner.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
			} else {
				progressBar.setProgress(downloadPercentage);
				progressBar.setVisibility(View.VISIBLE);
			}
		} else {
			if (!sUpdate.isUpdateAvailable() && !sUpdate.isNewSite()) {
				toggleRunButton.setVisibility(View.GONE);
				progress.setVisibility(View.GONE);
			} else if (sUpdate.hasUpdateStarted()) {
				toggleRunButton.setText(R.string.resume_update);
				progressBar.setVisibility(View.VISIBLE);
				progressBar.setProgress(downloadPercentage);
			} else if (sUpdate.hasError()) {
				toggleRunButton.setText(R.string.retry_update);
			} else {
				statusText.setVisibility(View.GONE);
				progress.setVisibility(View.GONE);
				toggleRunButton.setText(R.string.start_update);
			}
			cancelButton.setVisibility(View.GONE);
			if (mViewThread != null) {
				mViewThread.interrupt();
			}
		}
		onContentChanged();
	}

	/**
	 * Thread for updating this activity's view.
	 * 
	 * @author Brendon Drew (b.j.drew@gmail.com)
	 * 
	 */
	protected class ViewThread extends Thread {
		int sLastMode = 0;

		@Override
		public void run() {
			try {
				while (sUpdate.isUpdateInProgress()) {
					// After the download finished, only update the view as we
					// step through each mode
					if (sUpdate.getCurrentMode() >= SiteUpdateThread.MODE_REASSEMBLE
							&& sUpdate.getCurrentMode() == sLastMode) {
						continue;
					}
					handler.sendEmptyMessage(0);
					sleep(500);
					sLastMode = sUpdate.getCurrentMode();
				}
				// Update once more after we exit the loop
				handler.sendEmptyMessage(0);
			} catch (InterruptedException e) {
				Log.d(Constants.LOG_TAG, "viewThread interuppted");
			}
			Log.d(Constants.LOG_TAG, "viewThread terminating");
		}
	}

	/**
	 * A simple handler to invoke the view update.
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			updateView();
		}
	};

	/**
     * Called when the user presses the menu button
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MAIN_UI_ID, 0, R.string.menu_home);
		menu.add(0, ABOUT_ID, 0, R.string.menu_about);
		return true;
	}
	/**
	 * Called when the user selects a menu item.
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.v(Constants.LOG_TAG, "menu item selected:" + featureId);
		switch (featureId) {
		case MAIN_UI_ID:
			Intent i = new Intent(Constants.INTENT_ACTION_MAIN_UI);
			startActivity(i);
			break;
		case ABOUT_ID:
			// TODO Implement
			break;
		default:
		}
		return true;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d(Constants.LOG_TAG, "SiteUpdateDetails onRestart()");
	}
	/**
	 * Always called when starting or restarting the activity.
	 * So this is where we put the logic to retrieve the site
	 * details
	 */
	@Override
	protected void onResume() {
		Log.d(Constants.LOG_TAG, "SiteUpdateDetails onResume()");
		// TODO Auto-generated method stub
		super.onResume();
		SiteData selectedSite = null;
		Intent i = getIntent();
		// The intent must include extras which will provide some reference to the site.
		Bundle extras = i.getExtras();
		//If an index was given, get the site at that location in the site list.
		if (extras != null
				&& extras.get(Constants.INTENT_EXTRA_SITE_UPDATE_INDEX) != null) {
			Log.v(Constants.LOG_TAG, "extra="
					+ Constants.INTENT_EXTRA_SITE_UPDATE_INDEX);
			sSelectedSiteIndex = (Integer) extras
					.get(Constants.INTENT_EXTRA_SITE_UPDATE_INDEX);
			selectedSite = SiteList.mSiteList.get(sSelectedSiteIndex);
			sUpdate = getSiteUpdateData(selectedSite);
		//If a name was given, retrieve the site data from the manager
		} else if (extras != null
				&& extras.get(Constants.INTENT_EXTRA_SITE_UPDATE_NAME) != null) {
			Log.v(Constants.LOG_TAG, "extra="
					+ Constants.INTENT_EXTRA_SITE_UPDATE_NAME);
			String siteName = (String) extras
					.get(Constants.INTENT_EXTRA_SITE_UPDATE_NAME);
			sUpdate = SiteUpdateManager.getInstance().getUpdateThread(siteName)
					.getUpdateData();
		} else {
			Log.e(Constants.LOG_TAG, "No reference to an update!");
		}
		//Ensure we have a valid object
		if (sUpdate != null) {
			Log.d(Constants.LOG_TAG,
					"SiteUpdateDetails displaying:" + sUpdate.toString());
			startViewUpdater();
			if (sUpdate.isUpdateInProgress()) {
				// if its running, start the view thread
				startViewUpdater();
			} else {
				//Otherwise simply display its state as it is now.
				updateView();
			}
		} else {
			alertUserOnFail();
		}
	}
	/**
	 * Posts a diaglog offering retry and cancel options.
	 */
	private void alertUserOnFail() {
		AlertDialog.Builder diag = new AlertDialog.Builder(this);
		diag.setMessage("Failed to get site data!");
		//Retry launches this activity again.
		diag.setPositiveButton(getString(R.string.retry_update),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(
								Constants.INTENT_ACTION_LAUNCH_SITE_UPDATE);
						i.putExtra(Constants.INTENT_EXTRA_SITE_UPDATE_INDEX,
								sSelectedSiteIndex);
						startActivity(i);
					}
				});
		//Cancel goes back to the list.
		diag.setNegativeButton(getString(R.string.cancel_update),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(
								Constants.INTENT_ACTION_LAUNCH_SITE_UPDATER);
						startActivity(i);
					}
				});
		diag.show();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	// private ServiceConnection mConnection = new ServiceConnection() {
	// public void onServiceConnected(ComponentName className, IBinder service)
	// {
	// // This is called when the connection with the service has been
	// // established, giving us the service object we can use to
	// // interact with the service. Because we have bound to a explicit
	// // service that we know is running in our own process, we can
	// // cast its IBinder to a concrete class and directly access it.
	// mUpdaterService = ((SiteUpdaterService.LocalBinder)service).getService();
	//
	// if (mUpdaterService != null) {
	// Log.d(Constants.LOG_TAG,"we have a service!");
	// }
	// // Tell the user about this for our demo.
	// // Toast.makeText(mBoundService, "Service Connected",
	// // Toast.LENGTH_SHORT).show();
	// }
	//
	// public void onServiceDisconnected(ComponentName className) {
	// // This is called when the connection with the service has been
	// // unexpectedly disconnected -- that is, its process crashed.
	// // Because it is running in our same process, we should never
	// // see this happen.
	// mUpdaterService = null;
	// // Toast.makeText(Binding.this, "Serivce Disconnected",
	// // Toast.LENGTH_SHORT).show();
	// }
	// };
	// private boolean mIsBound;
	//
	// void doBindService() {
	// // Establish a connection with the service. We use an explicit
	// // class name because we want a specific service implementation that
	// // we know will be running in our own process (and thus won't be
	// // supporting component replacement by other applications).
	// bindService(new Intent(this,
	// SiteUpdaterService.class), mConnection, Context.BIND_AUTO_CREATE);
	// mIsBound = true;
	// }
	//
	// void doUnbindService() {
	// if (mIsBound) {
	// // Detach our existing connection.
	// unbindService(mConnection);
	// mIsBound = false;
	// }
	// }
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// doUnbindService();
	// }

}
