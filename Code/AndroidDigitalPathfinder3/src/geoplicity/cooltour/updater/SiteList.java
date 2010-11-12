package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.sites.SiteListAdapter;
import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;

import java.util.ArrayList;

import org.geoplicity.mobile.util.Logger;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private ArrayList<SiteData> mSiteList;
	private SiteListAdapter mSiteListAdapter;
	protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Logger.log(Logger.DEBUG, "SiteList onCreate()");
        Log.v(Constants.LOG_TAG, "SiteList onCreate()");
        //setContentView(R.layout.site_list);
        ArrayList<SiteData> sites = getSites();
        mSiteListAdapter = new SiteListAdapter(this, R.layout.site_list_row, sites);
        setListAdapter(mSiteListAdapter);
    }
    public ArrayList<SiteData> getSites() {
    	mSiteList = new ArrayList<SiteData>();
    	//TODO Implement
    	//Dummy Data
    	SiteData site = new SiteData();
    	site.setName("Olana");
    	site.setVersion("1.2");
    	mSiteList.add(site);
    	site = new SiteData();
    	site.setName("Staatsburg");
    	site.setVersion("2.0");
    	mSiteList.add(site);
    	//Logger.log(Logger.DEBUG, "returing "+mSiteList.size()+" sites");
        Log.v(Constants.LOG_TAG, "returing "+mSiteList.size()+" sites");
    	return mSiteList;
    }
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Logger.log(Logger.DEBUG, "position "+position+" selected");
		Intent myIntent = new Intent(Constants.INTENT_ACTION_LAUNCH_SITE_UPDATE);
//		myIntent.setClassName("geoplicity.cooltour.updater", "geoplicity.cooltour.updater.SiteUpdateDetails");
		myIntent.putExtra(Constants.INTENT_EXTRA_SITE_UPDATE, mSiteList.get(position)); // key/value pair, where key needs current package prefix.
		startActivity(myIntent); 
	}
}
