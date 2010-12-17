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
import geoplicity.cooltour.util.Constants;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

/**
 * Singleton class to track updates
 * 
 * @author Brendon Drew (b.j.drew@gmail.com)
 * 
 */
public class SiteUpdateManager {
	static SiteUpdateManager sRef;
	Context mContext;
	Map<String, SiteData> mSites;
	Map<String, SiteUpdateThread> mUpdates;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Private Constructor
	 */
	private SiteUpdateManager(Context c) {
		mUpdates = new HashMap<String, SiteUpdateThread>();
		mSites = new HashMap<String, SiteData>();
	}

	public static SiteUpdateManager getInstance() {
		return getInstance(null);
	}

	/**
	 * Returns the one instance of this class.
	 * 
	 * @return
	 */
	public static SiteUpdateManager getInstance(Context c) {
		if (sRef == null) {
			Log.d(Constants.LOG_TAG, "new SiteUpdateManager instance, context:"
					+ c);
			sRef = new SiteUpdateManager(c);
		}
		if (c != null) {
			Log.d(Constants.LOG_TAG, "setting new context:" + c);
			sRef.mContext = c;
		}
		return sRef;
	}

	/**
	 * Get the thread
	 * 
	 * @param upd
	 * @return
	 */
	public SiteUpdateThread getUpdateThread(SiteUpdateData upd, Context ctx) {
		if (mUpdates.containsKey(upd.getName())
				&& mUpdates.get(upd.getName()).isAlive()) {
			return mUpdates.get(upd.getName());
		} else {
			Log.v(Constants.LOG_TAG, "creating new thread for " + upd.getName());
			SiteUpdateThread uThread = new SiteUpdateThread(upd, ctx);
			return uThread;
		}
	}

	/**
	 * 
	 * @param siteName
	 * @return
	 */
	public SiteUpdateThread getUpdateThread(String siteName) {
		if (mUpdates.containsKey(siteName)) {
			return mUpdates.get(siteName);
		} else {
			return null;
		}
	}

	/**
	 * Start or
	 * 
	 * @param upd
	 */
	public void startUpdate(SiteUpdateData upd, Context ctx) {
		SiteUpdateThread uThread = getUpdateThread(upd, ctx);
		mUpdates.put(upd.getName(), uThread);
		uThread.start();
		Log.v(Constants.LOG_TAG, "started update for " + upd.getName());
		Log.d(Constants.LOG_TAG, "updates: " + mUpdates.size());
	}

	/**
	 * Check
	 * 
	 * @param siteName
	 * @return
	 */
	public boolean containsUpdate(String siteName) {
		Log.d(Constants.LOG_TAG, "updates: " + mUpdates.size());
		boolean exists = false;
		if (mUpdates.containsKey(siteName))
			exists = true;
		Log.d(Constants.LOG_TAG, "Update " + siteName
				+ " exists in SiteUpdateManager:" + exists);
		return exists;
	}

}
