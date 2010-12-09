package geoplicity.cooltour.updater;

import geoplicity.cooltour.ui.R;
import geoplicity.cooltour.util.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.geoplicity.mobile.util.Property;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/**
 * 
 * @author Brendon Drew (bjdrew@gmail.com)
 *
 */
public class SiteUpdateThread extends Thread {
	public static final int MODE_START = 0; 
	public static final int MODE_RESUME = 1;
	public static final int MODE_REASSEMBLE = 2;
	public static final int MODE_UNPACK = 3;
	public static final int MODE_CLEANUP = 4;
	public static final int MODE_FINISH = 5;
	/**
	 * The mode of operation.  The modes represent each step of the update process.
	 * By default the thread starts at 0, though you can specify which step of
	 * the process in which the thread will start by setting the mode before
	 * calling start() 
	 */
	int mMode;
	/**
	 * Set false to 
	 */
	boolean mCancel;
	/**
	 * The context within which this thread runs
	 */
	Context mContext;
	/**
	 * Update status
	 */
	SiteUpdateData mUpdateData;
	/**
	 * Temp directory.
	 */
	private String mTmpDir;
	/**
	 * 
	 * @param update
	 */
	public SiteUpdateThread(SiteUpdateData update, Context c) {
		mUpdateData = update;
		mContext = c;
		mMode = mUpdateData.getCurrentMode();
		mTmpDir = Constants.SDCARD_ROOT+
		Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR)+"/"+
		Constants.UPDATE_TEMP_DIR+"/"+mUpdateData.getName()+"/";
	}
	@Override
	public void run() {
		Log.v(Constants.LOG_TAG, "start run, mode="+mMode);
		//Log.d(Constants.LOG_TAG,Property.dump());
		mUpdateData.setStatusMessage("Update Started");
		mUpdateData.setUpdateStarted(true);
		mUpdateData.setUpdateInProgress(true);
		notifyUser();
		try {
			switch (mMode) {
				case MODE_START:
					mUpdateData.setCurrentMode(MODE_START);
					downloadBlocks(mTmpDir);
				case MODE_RESUME:
					//TODO Implement 
					mUpdateData.setCurrentMode(MODE_RESUME);
					Log.v(Constants.LOG_TAG,"Current block:"+Integer.toString(mUpdateData.getCurrentBlock()));
				case MODE_REASSEMBLE:
					mUpdateData.setCurrentMode(MODE_REASSEMBLE);
					mUpdateData.setStatusMessage("Reassembling Archive...");
					reassemble(mTmpDir);
				case MODE_UNPACK:
					mUpdateData.setCurrentMode(MODE_UNPACK);
					mUpdateData.setStatusMessage("Extracting Archive...");
					unpack(mTmpDir, mUpdateData.getName());
				case MODE_CLEANUP:
					mUpdateData.setStatusMessage("Finishing Up...");
					mUpdateData.setCurrentMode(MODE_CLEANUP);
					deleteTemp(mTmpDir);
				case MODE_FINISH:
					mUpdateData.setCurrentMode(MODE_FINISH);
					updateSiteProps();
				default:
			}	
			mUpdateData.setUpdateComplete(true);
			mUpdateData.setStatusMessage("Update Complete!");
		} catch (InterruptedException e) {
			if (mCancel) {
				mUpdateData.setStatusMessage("Canceled");
				mUpdateData.reset();
				deleteTemp(mTmpDir);
			}
			else{
				mUpdateData.setStatusMessage("Paused");
			}
			
			Log.v(Constants.LOG_TAG, e.getMessage());
		} catch (IOException e) {
			mUpdateData.setStatusMessage("Failed");
			mUpdateData.setHasError(true);
			Log.v(Constants.LOG_TAG, e.getMessage());
		}
		mUpdateData.setUpdateInProgress(false);
		notifyUser();

	}
	private void downloadBlocks(String tmpDir) throws InterruptedException, IOException {
		File dir = new File(tmpDir);
		if (!dir.exists()) {
			if (dir.mkdirs()) {
				Log.v(Constants.LOG_TAG, dir+ "created");
			}
			else {
				Log.e(Constants.LOG_TAG, "Failed to create "+dir);
				throw new IOException("Failed to create");
			}
		}
		for(int i=mUpdateData.getCurrentBlock();i<=mUpdateData.getBlockCount();i++){
			String blockName = mUpdateData.getName()+i;
			mUpdateData.setStatusMessage("Downloading file "+i+" of "+mUpdateData.getBlockCount());
			downloadBlock(Constants.UPDATE_SERVER+mUpdateData.getName()+"/"+mUpdateData.getVersion()+"/"+blockName, tmpDir+blockName);
			Log.v(Constants.LOG_TAG,"Files done downloading");
		    if (Thread.currentThread().isInterrupted()) {
		        throw new InterruptedException("Thread Interrupted");
		    }
			mUpdateData.incrementCurrentBlock();
		}

	}
	private void downloadBlock(String urlString, String saveTo) throws IOException, InterruptedException{
//		try {
			//for(int i=1;i<=Integer.parseInt(amount);i++){
			Log.v(Constants.LOG_TAG, "fetching "+urlString);
			//sleep(3000);
			URL url = new URL(urlString);
			BufferedInputStream in = new BufferedInputStream(url.openStream());
			
			FileOutputStream fos = new FileOutputStream(saveTo);
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int x = 0;
			while ((x=in.read(data,0,1024))>=0){
				bout.write(data,0,x);
				//Check for an interrupt
			    if (Thread.currentThread().isInterrupted()) {
					bout.close();
					in.close();
			        throw new InterruptedException("Thread Interrupted");
			      }

			}
			bout.close();
			in.close();
			//}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	private void reassemble(String tmpDir) {
		//Blocker b = new Blocker();
		Blocker.unblock(tmpDir+mUpdateData.getName(), mUpdateData.getBlockCount(), tmpDir+mUpdateData.getName()+".zip");
	}
	/**
	 * 
	 * @param location
	 * @param name
	 */
	private void unpack(String location, String name){
		try {
			ZipInputStream zis = null;

			FileInputStream fis = new FileInputStream(location+name+".zip");
			zis = new ZipInputStream(fis);

			ZipEntry ze;
			
			File mainDir = new File(Constants.SDCARD_ROOT+
					Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR)+
					"/"+name+"/");
			mainDir.mkdirs();

			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					File dir = new File(Constants.SDCARD_ROOT + 
							Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR) + 
							"/" + ze.getName());
					dir.mkdirs();
					Log.v(Constants.LOG_TAG,dir.toString()+" created");
				} else {
					File f = new File(Constants.SDCARD_ROOT + 
							Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR)+"/"+ 
							ze.getName());
					f.createNewFile();
					Log.v(Constants.LOG_TAG,f.toString()+" created");
					OutputStream out = new FileOutputStream(f);
					int sz = 0;
					byte[] buf = new byte[1024];
					int n;
					while ((n = zis.read(buf, 0, 1024)) > -1) {
						sz += n;
						out.write(buf, 0, n);
					}
				}
				Log.v(Constants.LOG_TAG,"Done unpacking");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * args[0] is the location on device (eg. C:\Test\sdcard\geoplicity)
	 * args[1] is the name of the site(eg. Olana)
	 */

	private boolean deleteTemp(String location) {
		File path = new File(location);
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteTemp(files[i].toString());
				} else {
					Log.v(Constants.LOG_TAG,"Deleting "+files[i].toString());
					files[i].delete();
				}
			}
		}
		Log.v(Constants.LOG_TAG,"Deleting "+path.toString());
		return (path.delete());
	}
	/**
	 * Update the properties file listing the installed sites
	 * @return
	 */
	private boolean updateSiteProps() {
		File sites = new File("/sdcard/Geoplicity/"+Constants.DEFAULT_SITE_PROPERTIES);
		if (!sites.exists()) {
			try {
				if (!sites.createNewFile()) {
					Log.e(Constants.LOG_TAG,"Failed to create "+sites);
					return false;
				}
				else {
					Log.v(Constants.LOG_TAG,"Created "+sites);
				}
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "Failed to create"+sites, e);
				return false;
			}
		}
		try {
			Properties sitesProps = new Properties();
			sitesProps.load(new FileInputStream(sites));
			sitesProps.setProperty(mUpdateData.getName(), mUpdateData.getVersion());
			sitesProps.save(new FileOutputStream(sites), "Updated "+mUpdateData.getName());
			Log.v(Constants.LOG_TAG,"updated "+sites);
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Failed to save"+sites, e);
			return false;
		}
		
		return true;

	}
	public SiteUpdateData getUpdateData() {
		return mUpdateData;
	}
	public void setUpdateData(SiteUpdateData updateData) {
		this.mUpdateData = updateData;
	}
	/**
	 * 
	 * @param complete
	 */
	private void notifyUser() {
		if (mContext == null) {
			Log.e(Constants.LOG_TAG, "null context, cannot notify for site "+mUpdateData.getName());
			return;
		}
		CharSequence contentTitle;		
		CharSequence contentText = null;
		int flags = 0;
		if (mUpdateData.isUpdateComplete()) {
			if (mUpdateData.isNewSite()) {
				contentTitle = "Site Install Complete!";
				contentText = "Successfully Installed Site "+mUpdateData.getName();
			}
			else {
				contentTitle = "Site Update Complete!";
				contentText = "Successfully Updates Site "+mUpdateData.getName();
			}
		}
		else if (mUpdateData.isUpdateInProgress()) {
			contentTitle = "Site Download in Progress";
			contentText = "Currently downloading "+mUpdateData.getName();
			flags = Notification.FLAG_ONGOING_EVENT;
		}
		else if (mUpdateData.hasError()) {
			contentTitle = "Update Failed";
			contentText = "Update for Site "+mUpdateData.getName()+" failed.";
		}
		else if (mUpdateData.hasUpdateStarted()) {
			contentTitle = "Update Paused";
			contentText = "Update for Site "+mUpdateData.getName()+" has been paused.";
			flags = Notification.FLAG_ONGOING_EVENT;
		}
		else {
			//No notification
			return;
		}
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(ns);
		
		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, contentText, when);
		notification.flags = flags;
//		notification.defaults |= Notification.DEFAULT_SOUND;
//		notification.defaults |= Notification.DEFAULT_VIBRATE;
		
		Context context = mContext.getApplicationContext();

		Intent notificationIntent = new Intent(Constants.INTENT_ACTION_LAUNCH_SITE_UPDATE);
		notificationIntent.putExtra(Constants.INTENT_EXTRA_SITE_UPDATE_NAME, mUpdateData.getName());

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		mNotificationManager.notify(R.string.update_started, notification);
		Log.d(Constants.LOG_TAG, "sending notification");
	}
	public boolean isCancel() {
		return mCancel;
	}
	public void setCancel(boolean mCancel) {
		this.mCancel = mCancel;
	}
}
