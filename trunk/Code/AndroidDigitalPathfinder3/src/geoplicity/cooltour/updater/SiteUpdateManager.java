package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;

import java.util.HashMap;
import java.util.Map;

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
	private SiteUpdateManager() {
		updates = new HashMap<String,SiteUpdateThread>();
		sites = new  HashMap<String,SiteData>();
	}
	public static SiteUpdateManager getInstance() {
		if (ref == null) {
			return new SiteUpdateManager();
		}
		else {
			return ref;
		}
	}
	/**
	 * 
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

}
