package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.sites.SiteListAdapter;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ArrayList<SiteData> sites = getSites();
        ArrayAdapter adapter = new SiteListAdapter(this, 0, sites);
        setListAdapter(adapter);
    }
    public ArrayList<SiteData> getSites() {
    	ArrayList<SiteData> sites = new ArrayList<SiteData>();
//    	SiteData site = new SiteData();
//    	site.setName("Olana");
//    	site.setVersion("Version");
//    	sites.add(site);
    	return sites;
    }
}
