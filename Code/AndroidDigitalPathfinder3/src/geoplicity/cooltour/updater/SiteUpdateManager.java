package geoplicity.cooltour.updater;

import java.util.HashMap;
import java.util.Map;

public class SiteUpdateManager {
	static SiteUpdateManager ref;
	Map<String,SiteUpdateThread> updates;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	private SiteUpdateManager() {
		updates = new HashMap<String,SiteUpdateThread>();
	}
	public static SiteUpdateManager getInstance() {
		if (ref == null) {
			return new SiteUpdateManager();
		}
		else {
			return ref;
		}
	}
	public SiteUpdateThread getUpdate(SiteUpdateData upd) {
		if (updates.containsKey(upd.getName())) {
			return updates.get(upd.getName());
		}
		else {
			SiteUpdateThread uThread = new SiteUpdateThread(upd);
			return uThread;
		}
	}

}
