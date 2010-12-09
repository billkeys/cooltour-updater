package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.sites.SiteListAdapter;
import geoplicity.cooltour.sites.Sites;
import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.geoplicity.mobile.util.Property;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
/**
 * The main activity for the updater piece.
 * This will retrieve all sites hosted on the updater
 * server, then merge this list with what sites are
 * installed locally.  The user is then shown a
 * comprehensive list of sites, with an indication of
 * which sites are new (i.e. not installed), downlevel and
 * up-to-date.
 * 
 * @author Brendon Drew (b.j.drew@gmail.com)
 *
 */
public class SiteList extends ListActivity {
	/**
	 * Menu item for launching "MaunUI" Activity
	 */
	private static final int MAIN_UI_ID = Menu.FIRST; 
	/**
	 * Menu item for launching "About Geoplicity" Activity
	 */
	private static final int ABOUT_ID = Menu.FIRST + 1;   
    public static ArrayList<SiteData> mSiteList;
	private SiteListAdapter<SiteData> mSiteListAdapter;
	protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Log.d(Constants.LOG_TAG, "SiteList onCreate()");

        //TODO This code to check for the app root should be move into MAINUI
        Property.loadProperties(Constants.DEFAULT_APP_PROPERTIES);
        if (Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR) == null) {
        	Property.setProperty(Constants.PROPERTY_APP_ROOT_DIR, Constants.DEFAULT_APP_ROOT_DIR);
        }
        Log.d(Constants.LOG_TAG,Property.dump());
        //refreshList();
//        ArrayList<SiteData> sites = getSites();
//        if (sites.size() > 0) {
//            mSiteListAdapter = new SiteListAdapter<SiteData>(this, R.layout.site_list_row, sites);
//            setListAdapter(mSiteListAdapter);
//        }
//        else {
//            AlertDialog.Builder diag =  new AlertDialog.Builder(this);
//        	diag.setMessage("Cannot get site list from server!");
//        	diag.setPositiveButton("Try Again", null);
//        	diag.setNegativeButton("Cancel", null);
//        	diag.show();
//        }
    }
	/**
	 * Get the full list of sites.
	 * @return
	 */
    public ArrayList<SiteData> getSites() {
    	Log.d(Constants.LOG_TAG, "SiteList getSites()");
    	mSiteList = new ArrayList<SiteData>(); 
    	Sites siteList = new Sites();
    	try {
    		
    		siteList.load(new FileInputStream(Constants.SDCARD_ROOT+
    				Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR)+
    				"/"+Constants.DEFAULT_SITE_PROPERTIES));
    		
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Failed to get local site list.", e);
			//Log.d(Constants.LOG_TAG,"app root:")+
		}
   		Log.d(Constants.LOG_TAG, siteList.toString());
    	try{
    		//Sites remoteSites = ;
    		siteList.merge(new Sites(Constants.UPDATE_SERVER+Constants.UPDATE_SITES_FILE));
			//mSiteList = remoteSites.getSites();
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Failed to get sites list from "+Constants.UPDATE_SERVER, e);
		}
		mSiteList.addAll(siteList.getSites());
        Log.v(Constants.LOG_TAG, "returing "+mSiteList.size()+" sites");
    	return mSiteList;
    }
    /**
     * 
     */
    public void refreshList() {
    	if (mSiteList == null) {
    		getSites();
    	}
    	else {
    		for (int i = 0 ; i< mSiteList.size(); i++) {
    			if (SiteUpdateManager.getInstance().containsUpdate(mSiteList.get(i).getName())) {
    				mSiteList.set(i, 
    						SiteUpdateManager.getInstance().getUpdateThread(
    								mSiteList.get(i).getName()).getUpdateData());
    				//site = SiteUpdateManager.getInstance().getUpdateThread(site.getName()).getUpdateData();
    			}
    		}
    	}
    }
    /**
     * 
     */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		SiteData s = mSiteList.get(position);
		Log.v(Constants.LOG_TAG, "position "+position+" selected, "+s.toString());
		Intent i = new Intent(Constants.INTENT_ACTION_LAUNCH_SITE_UPDATE);

		i.putExtra(Constants.INTENT_EXTRA_SITE_UPDATE, position); // key/value pair, where key needs current package prefix.
		startActivity(i); 
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(Constants.LOG_TAG, "SiteList onPause()");
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d(Constants.LOG_TAG, "SiteList onRestart()");
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(Constants.LOG_TAG, "SiteList onResume()");
		refreshList();
      
      if (mSiteList == null || mSiteList.isEmpty()) {
          AlertDialog.Builder diag =  new AlertDialog.Builder(this);
        	diag.setMessage("Cannot get site list from server!");
        	diag.setPositiveButton("Try Again", null);
        	diag.setNegativeButton("Cancel", null);
        	diag.show();
      }
      else {
    	  mSiteListAdapter = new SiteListAdapter<SiteData>(this, R.layout.site_list_row, mSiteList);
          setListAdapter(mSiteListAdapter);

      }
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
}
