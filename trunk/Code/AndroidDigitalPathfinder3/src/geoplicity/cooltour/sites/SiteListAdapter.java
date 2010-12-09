package geoplicity.cooltour.sites;

import geoplicity.cooltour.ui.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
/**
 * List Adapter for SiteData objects
 * @author Brendon Drew (b.j.drew@gmail.com)
 *
 * @param <T>
 */
public class SiteListAdapter<T> extends ArrayAdapter<SiteData> {

        private ArrayList<SiteData> mItems;
        private Context mContext;

        public SiteListAdapter(Context context, int textViewResourceId, ArrayList<SiteData> items) {
                super(context, textViewResourceId, items);
                this.mItems = items;
                this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	//Logger.log(Logger.DEBUG, "SiteListAdapter getView()");
        	View v = convertView;
                if (v == null) {
                	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.site_list_row, null);
                }
                SiteData site = mItems.get(position);
                if (site != null) {
                        TextView siteName = (TextView) v.findViewById(R.id.site_name);
                        TextView status = (TextView) v.findViewById(R.id.site_status);
                        siteName.setText(site.getName().replaceAll("_", " "));
                        if (site.isUpdateComplete()) {
                        	status.setText(R.string.up_to_date);
                        }
                        if (site.hasUpdateStarted()) {
                        	status.setText(R.string.update_started);
                        }
                        else if (site.isUpdateAvailable()) {
                        	status.setText(R.string.update_available);	
                        }
                        else if (site.isNewSite()) {
                        	status.setText(R.string.new_site);	
                        }
                        else {
                        	status.setText(R.string.up_to_date);
                        }
                        
                }
                return v;
        }
}
