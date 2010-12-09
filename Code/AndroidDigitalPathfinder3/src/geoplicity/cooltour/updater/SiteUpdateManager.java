package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.util.Constants;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
/**
 * Singleton class to track threads
 * @author Brendon Drew (b.j.drew@gmail.com)
 *
 */
public class SiteUpdateManager {
	static SiteUpdateManager sRef;
	Context mContext;
	Map<String,SiteData> mSites;
	Map<String,SiteUpdateThread> mUpdates;
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
		mUpdates = new HashMap<String,SiteUpdateThread>();
		mSites = new  HashMap<String,SiteData>();
	}
	public static SiteUpdateManager getInstance() {
		return getInstance(null);
	}
	/**
	 * Returns the one instance of this class.
	 * @return
	 */
	public static SiteUpdateManager getInstance(Context c) {
		if (sRef == null) {
			Log.d(Constants.LOG_TAG, "new SiteUpdateManager instance, context:"+c);
			sRef = new SiteUpdateManager(c);
		}
		if (c != null) {
			Log.d(Constants.LOG_TAG, "setting new context:"+c);
			sRef.mContext =c;
		}
		return sRef;
	}
	/**
	 * Get the thread
	 * @param upd
	 * @return
	 */
	public SiteUpdateThread getUpdateThread(SiteUpdateData upd, Context ctx) {
		if (mUpdates.containsKey(upd.getName()) && 
				mUpdates.get(upd.getName()).isAlive()) {
				return mUpdates.get(upd.getName());
		}
		else {
			Log.v(Constants.LOG_TAG, "creating new thread for "+upd.getName());
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
		}
		else {
			return null;
		}
	}
	/**
	 * Start or 
	 * @param upd
	 */
	public void startUpdate(SiteUpdateData upd, Context ctx) {
		SiteUpdateThread uThread = getUpdateThread(upd,ctx);
		mUpdates.put(upd.getName(), uThread);
		uThread.start();
		Log.v(Constants.LOG_TAG, "started update for "+upd.getName());
		Log.d(Constants.LOG_TAG, "updates: "+mUpdates.size());
	}
	/**
	 * Check
	 * @param siteName
	 * @return
	 */
	public boolean containsUpdate(String siteName) {
		Log.d(Constants.LOG_TAG, "updates: "+mUpdates.size());
		boolean exists = false;
		if (mUpdates.containsKey(siteName))
			exists = true;
		Log.d(Constants.LOG_TAG, "Update "+siteName+" exists in SiteUpdateManager:"+exists);
		return exists;
	}

}
