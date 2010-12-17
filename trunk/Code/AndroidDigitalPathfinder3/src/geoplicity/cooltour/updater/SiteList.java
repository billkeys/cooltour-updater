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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * The main activity for the updater. This will retrieve all sites hosted on the
 * updater server, then merge this list with what sites are installed locally.
 * The user is then shown a comprehensive list of sites, with an indication of
 * which sites are new (i.e. not installed), downlevel and up-to-date.
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
	/**
	 * The list of sites
	 */
	public static ArrayList<SiteData> mSiteList;
	/**
	 * The list adapter for generating the ListView
	 */
	private SiteListAdapter<SiteData> mSiteListAdapter;

	/**
	 * @param savedInstanceState
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(Constants.LOG_TAG, "SiteList onCreate()");

		// TODO This code to check for the app root should be move into MAINUI
		Property.loadProperties(Constants.DEFAULT_APP_PROPERTIES);
		if (Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR) == null) {
			Property.setProperty(Constants.PROPERTY_APP_ROOT_DIR,
					Constants.DEFAULT_APP_ROOT_DIR);
		}
		// Log.d(Constants.LOG_TAG,Property.dump());
	}

	/**
	 * Get the full list of sites, both local and remote.
	 * @return
	 */
	public ArrayList<SiteData> getSites() {
		Log.d(Constants.LOG_TAG, "SiteList getSites()");
		mSiteList = new ArrayList<SiteData>();
		Sites siteList = new Sites();
		//First get the local site list.
		try {
			siteList.load(new FileInputStream(Constants.SDCARD_ROOT
					+ Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR)
					+ "/" + Constants.DEFAULT_SITE_PROPERTIES));

		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Failed to get local site list.", e);
			// Log.d(Constants.LOG_TAG,"app root:")+
		}
		Log.d(Constants.LOG_TAG, siteList.toString());
		//Now merge the local list with the remote site list
		try {
			// Sites remoteSites = ;
			siteList.merge(new Sites(Constants.UPDATE_SERVER
					+ Constants.UPDATE_SITES_FILE));
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Failed to get sites list from "
					+ Constants.UPDATE_SERVER, e);
		}
		mSiteList.addAll(siteList.getSites());
		Log.v(Constants.LOG_TAG, "returing " + mSiteList.size() + " sites");
		return mSiteList;
	}

	/**
	 * Updates the site list with progress from any thread that may be running.
	 */
	public void refreshList() {
		if (mSiteList == null) {
			getSites();
		} else {
			for (int i = 0; i < mSiteList.size(); i++) {
				if (SiteUpdateManager.getInstance().containsUpdate(
						mSiteList.get(i).getName())) {
					Log.d(Constants.LOG_TAG, "refreshing entry "
							+ mSiteList.get(i).getName());
					mSiteList.set(i, SiteUpdateManager.getInstance()
							.getUpdateThread(mSiteList.get(i).getName())
							.getUpdateData());
				}
			}
		}
	}

	/**
	 * Called when an item is the list is selected.  In this case we
	 * want to launch the SiteUpdateDetails activity.  We add the site
	 * index to the intent so that SiteUpdateDetails can retrieve the
	 * site data.
	 * 
	 * @param l
	 * @param v
	 * @param position
	 * @param id
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		SiteData s = mSiteList.get(position);
		Log.v(Constants.LOG_TAG,
				"position " + position + " selected, " + s.toString());
		Intent i = new Intent(Constants.INTENT_ACTION_LAUNCH_SITE_UPDATE);
		i.putExtra(Constants.INTENT_EXTRA_SITE_UPDATE_INDEX, position);
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
	/**
	 * Always called when starting or restart an activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(Constants.LOG_TAG, "SiteList onResume()");
		refreshList();
		if (mSiteList == null || mSiteList.isEmpty()) {
			alertUserOnFail();
		} else {
			mSiteListAdapter = new SiteListAdapter<SiteData>(this,
					R.layout.site_list_row, mSiteList);
			setListAdapter(mSiteListAdapter);
		}
	}
	/**
	 * Posts a dialog alerting the user that the site list
	 * could not be generated. 
	 */
	private void alertUserOnFail() {
		AlertDialog.Builder diag = new AlertDialog.Builder(this);
		diag.setMessage("Failed to connect to server!");
		//rety will attempt to reload this activity
		diag.setPositiveButton(getString(R.string.retry_update),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(
								Constants.INTENT_ACTION_LAUNCH_SITE_UPDATER);
						startActivity(i);
					}
				});
		//Cancel should return the user to the MainUI
		diag.setNegativeButton(getString(R.string.cancel_update),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(
								Constants.INTENT_ACTION_MAIN_UI);
						startActivity(i);
					}
				});
		diag.show();	
	}
	/**
	 * Called when the user presses the menu button
	 * @param menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MAIN_UI_ID, 0, R.string.menu_home);
		menu.add(0, ABOUT_ID, 0, R.string.menu_about);
		return true;
	}

	/**
	 * Called when the user selects a menu item.
	 * @param featureId
	 * @param item
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.v(Constants.LOG_TAG, "menu item selected:" + featureId);
		switch (featureId) {
		case MAIN_UI_ID:
			Intent i = new Intent(Constants.INTENT_ACTION_MAIN_UI);
			startActivity(i);
			break;
		case ABOUT_ID:
			// TODO Implement
			break;
		default:
		}
		return true;
	}
}
