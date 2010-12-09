package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;
import geoplicity.cooltour.util.Utilities;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
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
 * This activity will act as a controller for managing a site update and 
 * will be invoked by a user selecting a site from the site list.
 * Specific site actions will be controlled from this activity, and the 
 * available actions will depend on the sites state.
 * <br>
 * 1)The site is up-to-date - Display site info (i.e. version)
 * 2)The site is new or downlevel - Display an Install/Update button to begin the update/ 
 * to start, cancel, pause and update.
 * 3)Update in progress - Display a progress bar, and a cancel button
 * 4)Update in progress (paused) - Display a progress bar, and a resume button
 * 5)Update Complete - Display update info, i.e. total time.
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
	static SiteUpdateData sUpdate;
	static SiteUpdateThread sUpdateThread;
	private SiteUpdaterService mUpdaterService;
	private ViewThread mViewThread;
	/**
	 * 
	 */
	protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "SiteUpdateDetails onCreate()");
    }
	
	
	/**
	 * Get the site update data.  If the thread hasn't started, 
	 * retrieve the data from the server otherwise get it from 
	 * the thread.
	 * @param site
	 * @return
	 */
	public SiteUpdateData getSiteUpdateData(SiteData site) {
		Log.d(Constants.LOG_TAG, "SiteUpdateDetails getSiteUpdateData()");
		if (site == null)
			return null;
		Log.d(Constants.LOG_TAG, "SiteUpdateDetails getSiteUpdateData() incoming site:"+site.toString());
		SiteUpdateData su = null;
		if (SiteUpdateManager.getInstance(this).containsUpdate(site.getName())) {
			su = SiteUpdateManager.getInstance(this).getUpdateThread(site.getName()).getUpdateData();
		}
		else {
			String siteUpdateProperties = Constants.UPDATE_SERVER+
			site.getName()+"/"+site.getVersion()+"/"+site.getName()+Constants.UPDATE_FILE_EXT; 

			try {
				su = new SiteUpdateData(siteUpdateProperties, site);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "Failed to get site update data", e);
			}
		}
		Log.d(Constants.LOG_TAG, "SiteUpdateDetails getSiteUpdateData() returning:"+su.toString());
		return su;
	}
	/**
	 * Invoked when pressing the start
	 * @param v
	 */
	public void toggleRun(View v) {
		SiteUpdateManager mgr = SiteUpdateManager.getInstance();
		SiteUpdateThread uThread = mgr.getUpdateThread(sUpdate, this);

		if (mUpdaterService != null) {
			Log.d(Constants.LOG_TAG, "we have the service!");
		}
		else 
			Log.d(Constants.LOG_TAG, "no service!");
		
		if (!uThread.isAlive()) {
			Log.v(Constants.LOG_TAG," starting update "+sUpdate.getName()+"");
			mgr.startUpdate(sUpdate, this);
		}
		else {
			Log.v(Constants.LOG_TAG," pausing update "+sUpdate.getName()+"");
			uThread.interrupt();
		}
		startViewUpdater();
	}
	/**
	 * 
	 * @param v
	 */
	public void cancelUpdate(View v) {
		Log.v(Constants.LOG_TAG," cancelling update "+sUpdate.getName()+"");
		SiteUpdateThread upd = SiteUpdateManager.getInstance().getUpdateThread(sUpdate, this);
		
		upd.setCancel(true);
		upd.interrupt();
	}
	/**
	 * 
	 */
	private void startViewUpdater() {
		mViewThread = new ViewThread();
		mViewThread.start();

	}
	/**
	 * Updates the current view based on the state of the update
	 */
	private void updateView () {
		setContentView(R.layout.site_update_details);
		/**
		 * Get the Views
		 */
        TextView name = (TextView) findViewById(R.id.site_update_details_name);
        TextView version = (TextView) findViewById(R.id.site_update_details_version);
        TextView blocks = (TextView) findViewById(R.id.site_update_details_blocks);
        TextView progress = (TextView) findViewById(R.id.site_update_progress);
        TextView size = (TextView) findViewById(R.id.site_update_details_size);
        TextView statusText = (TextView) findViewById(R.id.site_update_status);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.update_progress_bar);
        Button toggleRunButton = (Button) findViewById(R.id.site_update_toggle_button);
        Button cancelButton = (Button) findViewById(R.id.site_update_cancel_button);

        name.setText(sUpdate.getName().replaceAll("_", " "));
        version.setText(sUpdate.getVersion());
        blocks.setText(sUpdate.getBlockCount()+"");
        progressBar.setVisibility(View.GONE);
        size.setText(Utilities.parseBytesToHumanString(sUpdate.getFileSize()));
        statusText.setVisibility(View.VISIBLE);
        statusText.setText(sUpdate.getStatusMessage());
        progress.setText(Integer.toString(sUpdate.getCurrentBlock())+" of "+sUpdate.getBlockCount());
        progress.setVisibility(View.VISIBLE);
        
		if (sUpdate.isUpdateComplete()) {
        	toggleRunButton.setVisibility(View.GONE);
        	cancelButton.setVisibility(View.GONE);
		}
		else if (sUpdate.isUpdateInProgress()) {
			toggleRunButton.setText(R.string.pause_update);
        	toggleRunButton.setVisibility(View.VISIBLE);
        	cancelButton.setVisibility(View.VISIBLE);
        	int level = 0;
        	if (sUpdate.getCurrentBlock() > 1) {
        		double p =(( (double) sUpdate.getCurrentBlock()-1.0)/ (double) sUpdate.getBlockCount())*100.0;
        		level = (int)p;
        	}
            progressBar.setProgress(level);
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            if (!sUpdate.isUpdateAvailable() && !sUpdate.isNewSite()) {
            	toggleRunButton.setVisibility(View.GONE);
            	progress.setVisibility(View.GONE);
            }
            else if (sUpdate.hasUpdateStarted()) {
            	toggleRunButton.setText(R.string.resume_update);
            	progressBar.setVisibility(View.VISIBLE);
            }
            else if (sUpdate.hasError()) {
            	toggleRunButton.setText(R.string.retry_update);
            }
            else {
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
	 * @author Brendon Drew (b.j.drew@gmail.com)
	 *
	 */
	protected class ViewThread extends Thread {
		@Override
		public void run() {
			try{ 
				while (sUpdate.isUpdateInProgress()) {
					handler.sendEmptyMessage(0);
					sleep(500);
				}
				//Update once more after we exit the loop
				handler.sendEmptyMessage(0);
			}
			catch(InterruptedException e) { 
				Log.d(Constants.LOG_TAG,"viewThread interuppted");
			}
			Log.d(Constants.LOG_TAG,"viewThread terminating");
		}
	}
	/**
	 * 
	 */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
    		updateView();
        }
    };
    /**
     * 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MAIN_UI_ID, 0, R.string.menu_home);
        menu.add(0, ABOUT_ID, 0, R.string.menu_about);
        return true;
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.v(Constants.LOG_TAG, "menu item selected:"+featureId);
    	switch (featureId) {
    		case MAIN_UI_ID:
    			Intent i = new Intent(Constants.INTENT_ACTION_MAIN_UI);
    			startActivity(i); 
    		break;
    		case ABOUT_ID:
    			//TODO Implement
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
	@Override
	protected void onResume() {
		Log.d(Constants.LOG_TAG, "SiteUpdateDetails onResume()");
		// TODO Auto-generated method stub
		super.onResume();
        SiteData selectedSite = null;
        Intent i = getIntent();
        Bundle extras = i.getExtras();
    	
    	if (extras != null && extras.get(Constants.INTENT_EXTRA_SITE_UPDATE) != null) {
    		Log.v(Constants.LOG_TAG, "extra="+Constants.INTENT_EXTRA_SITE_UPDATE);
    		Integer selectedSiteIndex = (Integer) extras.get(Constants.INTENT_EXTRA_SITE_UPDATE);
        	selectedSite = SiteList.mSiteList.get(selectedSiteIndex);
            sUpdate = getSiteUpdateData(selectedSite);
    	}
    	else if (extras != null && extras.get(Constants.INTENT_EXTRA_SITE_UPDATE_NAME) != null) {
    		Log.v(Constants.LOG_TAG, "extra="+Constants.INTENT_EXTRA_SITE_UPDATE_NAME);
    		String siteName = (String)extras.get(Constants.INTENT_EXTRA_SITE_UPDATE_NAME);
            sUpdate = SiteUpdateManager.getInstance().getUpdateThread(siteName).getUpdateData();
    	}
    	else {
    		Log.e(Constants.LOG_TAG, "No reference to an update!");
    	}

        if (sUpdate != null) {
            //Log.d(Constants.LOG_TAG, "details for site:"+mUpdate.toString());
    		startViewUpdater();   	
        	if (sUpdate.isUpdateInProgress()) {
        		//setContentView(R.layout.site_update_in_progress_details);
        		startViewUpdater();
        	}
        	else {
        		updateView();
        	}

        }
        else {    
            AlertDialog.Builder diag =  new AlertDialog.Builder(this);
        	diag.setMessage("Failed to get site data!");
        	diag.setPositiveButton("Try Again", null);
        	diag.setNegativeButton("Cancel", null);
        	diag.show();
        }
    }
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

//	private ServiceConnection mConnection = new ServiceConnection() {
//	    public void onServiceConnected(ComponentName className, IBinder service) {
//	        // This is called when the connection with the service has been
//	        // established, giving us the service object we can use to
//	        // interact with the service.  Because we have bound to a explicit
//	        // service that we know is running in our own process, we can
//	        // cast its IBinder to a concrete class and directly access it.
//	        mUpdaterService = ((SiteUpdaterService.LocalBinder)service).getService();
//
//	        if (mUpdaterService != null) {
//	        	Log.d(Constants.LOG_TAG,"we have a service!");
//	        }
//	        // Tell the user about this for our demo.
////	        Toast.makeText(mBoundService, "Service Connected",
////	                Toast.LENGTH_SHORT).show();
//	    }
//
//	    public void onServiceDisconnected(ComponentName className) {
//	        // This is called when the connection with the service has been
//	        // unexpectedly disconnected -- that is, its process crashed.
//	        // Because it is running in our same process, we should never
//	        // see this happen.
//	        mUpdaterService = null;
////	        Toast.makeText(Binding.this, "Serivce Disconnected",
////	                Toast.LENGTH_SHORT).show();
//	    }
//	};
//	private boolean mIsBound;
//
//	void doBindService() {
//	    // Establish a connection with the service.  We use an explicit
//	    // class name because we want a specific service implementation that
//	    // we know will be running in our own process (and thus won't be
//	    // supporting component replacement by other applications).
//	    bindService(new Intent(this,
//	    		SiteUpdaterService.class), mConnection, Context.BIND_AUTO_CREATE);
//	    mIsBound = true;
//	}
//
//	void doUnbindService() {
//	    if (mIsBound) {
//	        // Detach our existing connection.
//	        unbindService(mConnection);
//	        mIsBound = false;
//	    }
//	}
//
//	@Override
//	protected void onDestroy() {
//	    super.onDestroy();
//	    doUnbindService();
//	}
	
}
