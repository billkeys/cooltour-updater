package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;
import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;

import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SiteUpdaterService extends Service {
    private NotificationManager mNM;
	static SiteUpdateManager ref;
	Map<String,SiteData> sites;
	Map<String,SiteUpdateThread> updates;
	
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	SiteUpdaterService getService() {
            return SiteUpdaterService.this;
        }
    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        notifyUser(true, new SiteUpdateData());
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        Log.d(Constants.LOG_TAG, "SiteUpdaterService Started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Constants.LOG_TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        //mNM.cancel(R.string.local_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, "Service Closed", Toast.LENGTH_SHORT).show();
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
			SiteUpdateThread uThread = new SiteUpdateThread(upd, this);
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
	private void notifyUser(boolean complete, SiteUpdateData updateData) {
		String ns = Context.NOTIFICATION_SERVICE;
		mNM = (NotificationManager) getSystemService(ns);
		
		int icon = R.drawable.icon;
		CharSequence tickerText = "Hello";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();

		CharSequence contentTitle;		
		CharSequence contentText;
		if (complete) {
			if (updateData.isNewSite()) {
				contentTitle = "Site Install Complete!";
				contentText = "Successfully Installed Site "+updateData.getName();
			}
			else {
				contentTitle = "Site Update Complete!";
				contentText = "Successfully Updates Site "+updateData.getName();
			}
		}
		else {
			contentTitle = "Site Download in Progress";
			contentText = "Currently downloading "+updateData.getName();

			notification.flags = Notification.FLAG_ONGOING_EVENT;
		}
		Intent notificationIntent = new Intent(Constants.INTENT_ACTION_LAUNCH_SITE_UPDATE);
		notificationIntent.putExtra(Constants.INTENT_EXTRA_SITE_UPDATE_NAME, updateData.getName()); // key/value pair, where key needs current package prefix.

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		mNM.notify(R.string.update_started, notification);
	}
}
