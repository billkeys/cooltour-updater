/**
 * Copyright (c) 2010 Contributors, http://geoplicity.org/
 * See CONTRIBUTORS.TXT for a full list of copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Geoplicity Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE DEVELOPERS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package geoplicity.cooltour.sites;

import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.updater.SiteUpdateData;
import geoplicity.cooltour.util.Constants;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * ArrayAdapter to bind SiteData objects
 * to a ListActivity
 * 
 * @author Brendon Drew (b.j.drew@gmail.com)
 * 
 * @param <T>
 */
public class SiteListAdapter<T> extends ArrayAdapter<SiteData> {

	private ArrayList<SiteData> mItems;
	private Context mContext;

	public SiteListAdapter(Context context, int textViewResourceId,
			ArrayList<SiteData> items) {
		super(context, textViewResourceId, items);
		this.mItems = items;
		this.mContext = context;
	}
	/**
	 * Returns the row's view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Logger.log(Logger.DEBUG, "SiteListAdapter getView()");
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.site_list_row, null);
		}
		SiteData site = mItems.get(position);
		Log.d(Constants.LOG_TAG, "SiteListAdapter.getView():" + site.toString());
		if (site != null) {

			TextView siteName = (TextView) v.findViewById(R.id.site_name);
			TextView status = (TextView) v.findViewById(R.id.site_status);
			siteName.setText(site.getName().replaceAll("_", " "));
			if (site instanceof SiteUpdateData) {
				SiteUpdateData uSite = (SiteUpdateData) site;
				status.setText(uSite.getStatusMessage());
			}
			else if (site.isUpdateAvailable()) {
				status.setText(R.string.update_available);
			} else if (site.isNewSite()) {
				status.setText(R.string.new_site);
			} else {
				status.setText(R.string.up_to_date);
			}
		}
		return v;
	}
}
