package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.sites.SiteListAdapter;
import geoplicity.cooltour.sites.Sites;
import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;

import java.io.IOException;
import java.util.ArrayList;

import org.geoplicity.mobile.util.Property;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
/**
 * The main activity for the updater piece.
 * This will retrieve all sites hosted on the updater
 * server, then merge this list with what sites are
 * installed locally.  The user is then shown a
 * comphrehensize list of sites, with an indication of
 * which sites are new (i.e. not installed), downlevel and
 * up-to-date.
 * 
 * @author Brendon Drew (b.j.drew@gmail.com)
 *
 */
public class SiteList extends ListActivity {
    public static ArrayList<SiteData> mSiteList;
	private SiteListAdapter<SiteData> mSiteListAdapter;
	protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v(Constants.LOG_TAG, "SiteList onCreate()");
        ArrayList<SiteData> sites = getSites();
        mSiteListAdapter = new SiteListAdapter(this, R.layout.site_list_row, sites);
        setListAdapter(mSiteListAdapter);
    }
    public ArrayList<SiteData> getSites() {
    	
    	try {
			Sites sites = new Sites(Property.getProperty(Constants.PROPERTY_UPDATE_URL)+Constants.UPDATE_SITES_FILE);
			mSiteList = sites.getSites();
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Failed to get sites list from "+Property.getProperty(Constants.PROPERTY_UPDATE_URL), e);
			mSiteList = new ArrayList<SiteData>();
		}
        Log.v(Constants.LOG_TAG, "returing "+mSiteList.size()+" sites");
    	return mSiteList;
    }
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
	}
}
