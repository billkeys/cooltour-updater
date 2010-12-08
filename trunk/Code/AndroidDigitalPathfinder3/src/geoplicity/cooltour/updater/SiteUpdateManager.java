package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.util.Constants;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
/**
 * Singleton class to track threads
 * @author Brendon Drew (b.j.drew@gmail.com)
 *
 */
public class SiteUpdateManager {
	static SiteUpdateManager ref;
	Map<String,SiteData> sites;
	Map<String,SiteUpdateThread> updates;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * Private Constructor
	 */
	private SiteUpdateManager() {
		updates = new HashMap<String,SiteUpdateThread>();
		sites = new  HashMap<String,SiteData>();
	}
	/**
	 * Returns the one instance of this class.
	 * @return
	 */
	public static SiteUpdateManager getInstance() {
		if (ref == null) {
			Log.d(Constants.LOG_TAG, "new SiteUpdateManager instance");
			ref = new SiteUpdateManager();
		}
		return ref;
	}
	/**
	 * Get the thread
	 * @param upd
	 * @return
	 */
	public SiteUpdateThread getUpdateThread(SiteUpdateData upd) {
		if (updates.containsKey(upd.getName())) {
			return updates.get(upd.getName());
		}
		else {
			SiteUpdateThread uThread = new SiteUpdateThread(upd);
			return uThread;
		}
	}
	public SiteUpdateThread getUpdateThread(String siteName) {
		if (updates.containsKey(siteName)) {
			return updates.get(siteName);
		}
		else {
			return null;
		}
	}
	/**
	 * Start or 
	 * @param upd
	 */
	public void startUpdate(SiteUpdateData upd) {
		SiteUpdateThread uThread = new SiteUpdateThread(upd);
		updates.put(upd.getName(), uThread);
		uThread.start();
		Log.v(Constants.LOG_TAG, "started update for "+upd.getName());
		Log.d(Constants.LOG_TAG, "updates: "+updates.size());
	}
	public void pauseUpdate(SiteUpdateData upd) {
		SiteUpdateThread uThread = getUpdateThread(upd);
		uThread.suspend();
//		updates.put(upd.getName(), uThread);
//		uThread.start();
		Log.v(Constants.LOG_TAG, "paused update for "+upd.getName());
	}
	/**
	 * Check
	 * @param siteName
	 * @return
	 */
	public boolean containsUpdate(String siteName) {
		Log.d(Constants.LOG_TAG, "updates: "+updates.size());
		boolean exists = false;
		if (updates.containsKey(siteName))
			exists = true;
		Log.d(Constants.LOG_TAG, "Update "+siteName+" exists in SiteUpdateManager:"+exists);
		return exists;
	}

}
