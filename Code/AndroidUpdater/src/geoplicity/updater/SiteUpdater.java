package geoplicity.updater;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class SiteUpdater extends ListActivity {
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        ArrayList<SiteData> sites = getSites();
        ArrayAdapter adapter = new SiteListAdapter(this, 0, sites);
        setListAdapter(adapter);

        
    }
    /**
     * 
     * @return
     */
    public ArrayList<SiteData> getSites() {
    	ArrayList<SiteData> sites = new ArrayList<SiteData>();
    	//TODO Actually get the site list from the server (or a local properties file)
    	SiteData site = new SiteData();
    	site.setName("Olana");
    	site.setVersion("Version");
    	sites.add(site);
    	return sites;
    }
}