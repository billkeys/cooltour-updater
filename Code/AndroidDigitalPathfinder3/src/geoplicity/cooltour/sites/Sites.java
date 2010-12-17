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

import geoplicity.cooltour.util.Constants;

import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

/**
 * Wrapper around the properties file that contains site data.
 * 
 * @author Brendon Drew (bjdrew@gmail.com)
 * 
 */
public class Sites extends RemoteProperties {
	/**
	 * 
	 */
	ArrayList<SiteData> sites;
	private static final long serialVersionUID = 1L;

	public Sites(String url) throws IOException {
		super(url);
		buildSiteList();
	}

	public Sites() {
		buildSiteList();
	}

	private void buildSiteList() {
		sites = new ArrayList<SiteData>();
		for (Object keyObj : keySet()) {
			SiteData site = new SiteData();
			site.setName(keyObj.toString());
			site.setVersion(getProperty(site.getName()));
			sites.add(site);
		}
	}

	public ArrayList<SiteData> getSites() {
		return sites;
	}

	/**
	 * Return the SiteData for the given site.
	 * 
	 * @param name
	 * @return
	 */
	public SiteData getSite(String name) {
		for (SiteData site : sites) {
			if (site.getName().equals(name)) {
				return site;
			}
		}
		return new SiteData();
	}

	/**
	 * Merges two Sites instances
	 * 
	 * @param in
	 *            The given Sites object is considered to be the newer set of
	 *            sites, so any higher versions found in this object will be
	 *            flagged as possible updates.
	 */
	public void merge(Sites in) {
		Log.d(Constants.LOG_TAG, "merging Sites");
		buildSiteList();
		for (SiteData thatSite : in.getSites()) {
			if (containsKey(thatSite.getName())) {
				// Both Sites objects have an entry for the same record
				//
				SiteData thisSite = getSite(thatSite.getName());
				Log.d(Constants.LOG_TAG, thisSite.toString());
				Log.d(Constants.LOG_TAG, thatSite.toString());
				if (thisSite.getVersion().equals(thatSite.getVersion())) {
					// No Update
					// thisSite.s
				} else {
					Log.d(Constants.LOG_TAG,
							"update available for:" + thisSite.getName());
					thisSite.setUpdateAvailable(true);
					thisSite.setNewVersion(thatSite.getVersion());
				}
			} else {
				Log.d(Constants.LOG_TAG, "new site:" + thatSite.getName());
				// new site
				thatSite.setNewSite(true);
				thatSite.setNewVersion(thatSite.getVersion());
				sites.add(thatSite);
			}
		}

	}

}
