package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;

import java.io.IOException;

import org.geoplicity.mobile.util.Property;

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
	protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v(Constants.LOG_TAG, "SiteUpdateDetails onCreate()");
        SiteData selectedSite = null;
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras.get(Constants.INTENT_EXTRA_SITE_UPDATE) != null) {
        	Log.v(Constants.LOG_TAG, extras.get(Constants.INTENT_EXTRA_SITE_UPDATE).toString());
        	//selectedSite = (SiteData) extras.get(Constants.INTENT_EXTRA_SITE_UPDATE);
        	Integer selectedSiteIndex = (Integer) extras.get(Constants.INTENT_EXTRA_SITE_UPDATE);
        	selectedSite = SiteList.mSiteList.get(selectedSiteIndex);
            mUpdate = getSiteUpdateData(selectedSite);

            if (mUpdate != null) {
            	Log.v(Constants.LOG_TAG, mUpdate.toString());
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
               	b.setText(R.string.start_update);
                

            }
            else {    
                AlertDialog.Builder diag =  new AlertDialog.Builder(this);
            	diag.setMessage("Failed to get site data");
            	diag.setPositiveButton("Try Again", null);
            	diag.setNegativeButton("Cancel", null);
            	diag.show();
            }
        }
        else if (extras.get(Constants.INTENT_EXTRA_SITE_RUNNING_UPDATE) != null) {
        	Log.v(Constants.LOG_TAG, mUpdate.toString());


        }
        


    }
	public SiteUpdateData getSiteUpdateData(SiteData site) {
		if (site == null)
			return null;
		String siteUpdateProperties = Constants.UPDATE_SERVER+
		site.getName()+"/"+site.getVersion()+"/"+site.getName()+Constants.UPDATE_FILE_EXT; 
		SiteUpdateData su = null;
		try {
			su = new SiteUpdateData(siteUpdateProperties);
		} catch (IOException e) {
			Log.v(Constants.LOG_TAG, "Failed to get site update data", e);
		}
		return su;
	}
	public void startUpdate(View v) {

		SiteUpdateManager mgr = SiteUpdateManager.getInstance();
		mUpdateThread = mgr.getUpdateThread(mUpdate);
		mUpdateThread.start();
		Log.v(Constants.LOG_TAG, "starteded update for "+mUpdate.getName());
		//watchUpdate();
		vt = new viewThread();
		vt.start();
		
	}
	private void updateView (SiteUpdateThread updtProc) {
		//Log.v(Constants.LOG_TAG," "+mUpdate.getName()+" updating view");
        setContentView(R.layout.site_update_in_progress_details);
        onContentChanged();
        TextView name = (TextView) findViewById(R.id.site_update_details_name);
        name.setText(mUpdate.getName());
        TextView statusText = (TextView) findViewById(R.id.site_update_status);
      
        if (updtProc.isAlive()) {
        	statusText.setText("In Progress");        	
        }
        else {
        	statusText.setText("Comlpeted!");
        }
        

	}
	protected class viewThread extends Thread {
		public viewThread() {
		}
		@Override
		public void run() {	
			while (true) {
				try{
					sleep(2000);
					handler.sendEmptyMessage(0);
				}
				catch(InterruptedException e) {
					//Log.v(TAG,"Thread Insomnia");
				}
			}
		}
	}
    private Handler handler = new Handler() {

        @Override

        public void handleMessage(Message msg) {
    		//Log.v(Constants.LOG_TAG,"handleMessage()->");
    		updateView(mUpdateThread);
    		//Log.v(Constants.LOG_TAG,"<-handleMessage()");
        }

    };
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
	private Handler mHandler = new Handler() {
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	            case 0:
	            //answer(msg.obj);
	            Log.d(Constants.LOG_TAG, "retry update for "+mUpdate.getName());
	            break;
	    
	            case 1:
	            // voicemail(msg.obj);
	            Log.d(Constants.LOG_TAG, "cancel update for "+mUpdate.getName());
	            break;
	    
	        }
	    }
	};
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		vt.stop();
	}
}
