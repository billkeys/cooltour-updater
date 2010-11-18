package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;

import org.geoplicity.mobile.util.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
	SiteUpdateData update;
	protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Logger.log(Logger.DEBUG, "SiteUpdateDetails onCreate()");
        SiteData selectedSite;
        if (savedInstanceState != null && savedInstanceState.get(Constants.INTENT_EXTRA_SITE_UPDATE) != null) {
        	selectedSite = (SiteData) savedInstanceState.get(Constants.INTENT_EXTRA_SITE_UPDATE);
        }
        else {
        	selectedSite = new SiteData();
        	selectedSite.setName("Unknown");
        }
        update = getSiteUpdateData(selectedSite);
        //setContentView(R.layout.site_update_details);
        setContentView(R.layout.site_update_details);
        
        TextView name = (TextView) findViewById(R.id.SiteName);
        name.setText(selectedSite.getName());
        TextView version = (TextView) findViewById(R.id.SiteUpdateVersion);
        version.setText(selectedSite.getVersion());
        //Button b = (Button) findViewById(R.id.site_update_start);
        //b.setText(R.string.start_update);

    }
	public SiteUpdateData getSiteUpdateData(SiteData site) {
		SiteUpdateData su = new SiteUpdateData();
		return su;
	}
	public void startUpdate() {
		Log.v(Constants.LOG_TAG, "starting update for "+update.getName());
	}
}
