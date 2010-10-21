package geoplicity.updater;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SiteListAdapter<T> extends ArrayAdapter<T> {

        private ArrayList<SiteData> items;
        private Context c;

        public SiteListAdapter(Context context, int textViewResourceId, ArrayList<SiteData> items) {
                super(context, textViewResourceId);
                this.items = items;
                this.c = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                //View v = convertView;
                LinearLayout rowLayout = null;
                if (convertView == null) {
                    rowLayout = (LinearLayout)LayoutInflater.from(c).inflate
                    (R.layout.site_list_row, parent, false);
                    //TextView tv = (TextView)rowLayout.findViewById(R.id.txtName);
//                	LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    v = vi.inflate(R.layout.site_list_row, null);
                }
                SiteData o = items.get(position);
                if (o != null) {
                        TextView tt = (TextView) rowLayout.findViewById(R.id.site_name);
                        TextView bt = (TextView) rowLayout.findViewById(R.id.site_status);
                        if (tt != null) {
                              tt.setText("Name: "+o.getName());                            }
                        if(bt != null){
                              bt.setText("Version: "+ o.getVersion());
                        }
                }
                return rowLayout;
        }
}
