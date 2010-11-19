package geoplicity.cooltour.sites;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.ui.R;

import java.util.ArrayList;

import org.geoplicity.mobile.util.Logger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * List Adapter for SiteData objects
 * @author Brendon Drew (b.j.drew@gmail.com)
 *
 * @param <T>
 */
public class SiteListAdapter<T> extends ArrayAdapter<SiteData> {

        private ArrayList<SiteData> items;
        private Context c;

        public SiteListAdapter(Context context, int textViewResourceId, ArrayList<SiteData> items) {
                super(context, textViewResourceId, items);
                this.items = items;
                this.c = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	//Logger.log(Logger.DEBUG, "SiteListAdapter getView()");
        	View v = convertView;
                if (v == null) {
                	LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.site_list_row, null);
                }
                SiteData site = items.get(position);
                if (site != null) {
                        TextView tt = (TextView) v.findViewById(R.id.site_name);
                        TextView bt = (TextView) v.findViewById(R.id.site_status);
                        if (tt != null) {
                              tt.setText(site.getName());                            }
                        if(bt != null){
                              bt.setText("Version: "+ site.getVersion());
                        }
                }
                return v;
        }
}
