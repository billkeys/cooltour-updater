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


        }
        else if (extras.get(Constants.INTENT_EXTRA_SITE_RUNNING_UPDATE) != null) {
        	SiteUpdateManager mgr = SiteUpdateManager.getInstance();
        	//mgr.getUpdate();
        }
        
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
	public SiteUpdateData getSiteUpdateData(SiteData site) {
		if (site == null)
			return null;
		String siteUpdateProperties = Constants.UPDATE_SERVER+site.getName()+"/"+site.getVersion()+"/"+site.getName()+Constants.UPDATE_FILE_EXT; 
		SiteUpdateData su = null;
		try {
			su = new SiteUpdateData(siteUpdateProperties);
		} catch (IOException e) {
			Log.v(Constants.LOG_TAG, "Failed to get site update data", e);
		}
		return su;
	}
	public void startUpdate(View v) {
		Log.v(Constants.LOG_TAG, "starting update for "+mUpdate.getName());
		SiteUpdateManager mgr = SiteUpdateManager.getInstance();
		SiteUpdateThread updateThread = mgr.getUpdate(mUpdate);
		updateThread.run();
	    //Intent intent = new Intent(this, com.example.app.ChooseYourBoxer.class);
	    //startActivityForResult(intent, CHOOSE_FIGHTER);
		
	}
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
}
