package geoplicity.updater;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class SiteList extends ListActivity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ArrayList<SiteData> sites = getSites();
        ArrayAdapter adapter = new SiteListAdapter(this, 0, sites);
        setListAdapter(adapter);
    }
    public ArrayList<SiteData> getSites() {
    	ArrayList<SiteData> sites = new ArrayList<SiteData>();
    	SiteData site = new SiteData();
    	site.setName("Olana");
    	site.setVersion("Version");
    	sites.add(site);
    	return sites;
    }
}
