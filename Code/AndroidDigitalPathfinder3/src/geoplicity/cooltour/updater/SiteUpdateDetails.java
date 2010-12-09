package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;

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
	//Menu item for launching "MaunUI" Activity
	private static final int MAIN_UI_ID = Menu.FIRST; 
	//Menu item for launching "About Geoplicity" Activity
	private static final int ABOUT_ID = Menu.FIRST + 1;     
	static SiteUpdateData mUpdate;
	static SiteUpdateThread mUpdateThread;
	private viewThread vt;
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
		SiteUpdateData su = null;
		if (SiteUpdateManager.getInstance().containsUpdate(site.getName())) {
			su = SiteUpdateManager.getInstance().getUpdateThread(site.getName()).getUpdateData();
		}
		else {
			String siteUpdateProperties = Constants.UPDATE_SERVER+
			site.getName()+"/"+site.getVersion()+"/"+site.getName()+Constants.UPDATE_FILE_EXT; 

			try {
				su = new SiteUpdateData(siteUpdateProperties);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "Failed to get site update data", e);
			}
		}
		return su;
	}
	/**
	 * Invoked when pressing the start
	 * @param v
	 */
	public void startUpdate(View v) {
		SiteUpdateManager.getInstance().startUpdate(mUpdate);
		startViewUpdater();
	}
	/**
	 * Invoked when presses the pause button
	 * @param v
	 */
	public void pauseUpdate(View v) {
		Log.v(Constants.LOG_TAG," pausing update "+mUpdate.getName()+"");
		SiteUpdateThread upd = SiteUpdateManager.getInstance().getUpdateThread(mUpdate);
		upd.interrupt();
	}
	/**
	 * 
	 * @param v
	 */
	public void cancelUpdate(View v) {
		Log.v(Constants.LOG_TAG," cancelling update "+mUpdate.getName()+"");
		SiteUpdateThread upd = SiteUpdateManager.getInstance().getUpdateThread(mUpdate);
		upd.interrupt();
		//TODO cleanup temp files.
	}

	private void startViewUpdater() {
		vt = new viewThread();
		vt.start();

	}
	private void updateView () {
		Log.v(Constants.LOG_TAG," "+mUpdate.toString());
		if (mUpdate.isUpdateComplete()) {
            setContentView(R.layout.site_update_complete);

            TextView name = (TextView) findViewById(R.id.site_update_details_name);
            name.setText(mUpdate.getName());
            TextView statusText = (TextView) findViewById(R.id.site_update_status);
        	statusText.setText("Comlpeted!");
        	vt.stop();
		}
		else if (mUpdate.isUpdateInProgress()) {
            setContentView(R.layout.site_update_in_progress_details);

            TextView name = (TextView) findViewById(R.id.site_update_details_name);
            name.setText(mUpdate.getName());

            TextView statusText = (TextView) findViewById(R.id.site_update_status);
        	statusText.setText("In Progress");
        	
            TextView blocks = (TextView) findViewById(R.id.site_update_block_count);
            blocks.setText(Integer.toString(mUpdate.getCurrentBlock()));
        }
        else {
            setContentView(R.layout.site_update_details);
            TextView name = (TextView) findViewById(R.id.site_update_details_name);
            name.setText(mUpdate.getName());
            TextView version = (TextView) findViewById(R.id.site_update_details_version);
            version.setText(mUpdate.getVersion());
            TextView blocks = (TextView) findViewById(R.id.site_update_details_blocks);
            blocks.setText(mUpdate.getBlockCount()+"");
            TextView size = (TextView) findViewById(R.id.site_update_details_size);
            size.setText(mUpdate.getFileSize()+"");
            Button b = (Button) findViewById(R.id.site_update_details_button);
            if (mUpdate.hasUpdateStarted()) {
            	b.setText(R.string.resume_update);
            }
            else {
            	b.setText(R.string.start_update);
            }
           	
           	if (vt != null) {
           		vt.stop();
           	}
        }
        onContentChanged();

	}
	/**
	 * Thread for updating this activities view.
	 * @author Brendon Drew (b.j.drew@gmail.com)
	 *
	 */
	protected class viewThread extends Thread {
		public viewThread() {
		}
		@Override
		public void run() {
			//Only 
			while (mUpdate.isUpdateInProgress()) {
				try{
					sleep(500);
					handler.sendEmptyMessage(0);
				}
				catch(InterruptedException e) {
					//Log.v(TAG,"Thread Insomnia");
				}
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
    	// TODO Auto-generated method stub
    	//return super.onMenuItemSelected(featureId, item);
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
        	Log.v(Constants.LOG_TAG, extras.get(Constants.INTENT_EXTRA_SITE_UPDATE).toString());
        	//selectedSite = (SiteData) extras.get(Constants.INTENT_EXTRA_SITE_UPDATE);
        	Integer selectedSiteIndex = (Integer) extras.get(Constants.INTENT_EXTRA_SITE_UPDATE);
        	selectedSite = SiteList.mSiteList.get(selectedSiteIndex);
            mUpdate = getSiteUpdateData(selectedSite);

            if (mUpdate != null) {
            	Log.v(Constants.LOG_TAG, mUpdate.toString());
        		startViewUpdater();   	
            	if (mUpdate.isUpdateInProgress()) {
            		//setContentView(R.layout.site_update_in_progress_details);
            		startViewUpdater();
            	}
            	else {
            		updateView();
            	}

            }
            else {    
                AlertDialog.Builder diag =  new AlertDialog.Builder(this);
            	diag.setMessage("Failed to get site data");
            	diag.setPositiveButton("Try Again", null);
            	diag.setNegativeButton("Cancel", null);
            	diag.show();
            }
        }
        else if (extras != null && extras.get(Constants.INTENT_EXTRA_SITE_RUNNING_UPDATE) != null) {
        	Log.v(Constants.LOG_TAG, mUpdate.toString());
        }
    }
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
}
