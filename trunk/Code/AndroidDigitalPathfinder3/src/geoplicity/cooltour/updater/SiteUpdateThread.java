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
 * A thread that includes all logic to perform a site update.
 * @author Brendon Drew (bjdrew@gmail.com) and Skyler Dodge
 *         (skylarkdodge@gmail.com)
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
	 * The mode of operation. The modes represent each step of the update
	 * process. By default the thread starts at 0, though you can specify which
	 * step of the process in which the thread will start by setting the mode
	 * before calling start()
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
		mTmpDir = Constants.SDCARD_ROOT
				+ Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR) + "/"
				+ Constants.UPDATE_TEMP_DIR + mUpdateData.getName() + "/";
	}

	@Override
	public void run() {
		Log.v(Constants.LOG_TAG, "start run, mode=" + mMode);
		// Log.d(Constants.LOG_TAG,Property.dump());
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
				mUpdateData.setCurrentMode(MODE_RESUME);
				downloadBlocks(mTmpDir);
			case MODE_REASSEMBLE:
				mUpdateData.setCurrentMode(MODE_REASSEMBLE);
				mUpdateData.setStatusMessage("Reassembling Archive...");
				reassemble(mTmpDir);
				verify(mTmpDir, mUpdateData.getName(),
						mUpdateData.getFileFormat());
			case MODE_UNPACK:
				mUpdateData.setCurrentMode(MODE_UNPACK);
				mUpdateData.setStatusMessage("Extracting Archive...");
				unpack(mTmpDir, mUpdateData.getName());
				deleteBlocks(mTmpDir);
				copyDirectory(new File(mTmpDir + mUpdateData.getName()),
						new File(Constants.SDCARD_ROOT
								+ Constants.DEFAULT_APP_ROOT_DIR + "/"
								+ mUpdateData.getName()));
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
			} else {
				mUpdateData.setStatusMessage("Paused");
			}
			Log.v(Constants.LOG_TAG, e.getMessage());
		} catch (IOException e) {
			mUpdateData.setStatusMessage("Failed");
			mUpdateData.setHasError(true);
			Log.v(Constants.LOG_TAG, e.getMessage());
		} catch (Exception e) {
			Log.v(Constants.LOG_TAG, e.getMessage());
		}
		mUpdateData.setUpdateInProgress(false);
		notifyUser();

	}

	/**
	 * This method downloads the blocks of the specified file on the server to
	 * the temp directory
	 * 
	 * @param tmpDir
	 *            This is where the files will be downloaded to
	 * @throws InterruptedException
	 * @throws IOException
	 */

	private void downloadBlocks(String tmpDir) throws InterruptedException,
			IOException {
		File dir = new File(tmpDir);
		if (!dir.exists()) {
			if (dir.mkdirs()) {
				Log.v(Constants.LOG_TAG, dir + "created");
			} else {
				Log.e(Constants.LOG_TAG, "Failed to create " + dir);
				throw new IOException("Failed to create");
			}
		}
		for (int i = mUpdateData.getCurrentBlock(); i <= mUpdateData
				.getBlockCount(); i++) {
			String blockName = mUpdateData.getName() + i;
			mUpdateData.setStatusMessage("Downloading file " + i + " of "
					+ mUpdateData.getBlockCount());
			downloadBlock(Constants.UPDATE_SERVER + mUpdateData.getName() + "/"
					+ mUpdateData.getVersion() + "/" + blockName, tmpDir
					+ blockName);
			Log.v(Constants.LOG_TAG, "Files done downloading");
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("Thread Interrupted");
			}
			mUpdateData.incrementCurrentBlock();
		}

	}

	/**
	 * This method downloads each individual block, based on the url of the
	 * block location, and downloads it to the specified directory, which is the
	 * temp directory as specified above. This also helps in recovery because we
	 * can start at any particular block if we need to re-download.
	 * 
	 * @param urlString
	 *            The URL of each individual block
	 * @param saveTo
	 *            This is where the block will be downloaded to
	 * @throws IOException
	 * @throws InterruptedException
	 */

	private void downloadBlock(String urlString, String saveTo)
			throws IOException, InterruptedException {
		Log.v(Constants.LOG_TAG, "fetching " + urlString);
		// sleep(3000);
		URL url = new URL(urlString);
		BufferedInputStream in = new BufferedInputStream(url.openStream());

		FileOutputStream fos = new FileOutputStream(saveTo);
		BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
		byte[] data = new byte[1024];
		int x = 0;
		while ((x = in.read(data, 0, 1024)) >= 0) {
			bout.write(data, 0, x);
			// Check for an interrupt
			if (Thread.currentThread().isInterrupted()) {
				bout.close();
				in.close();
				throw new InterruptedException("Thread Interrupted");
			}

		}
		bout.close();
		in.close();
	}

	/**
	 * This method calls unblock method of Blocker.java
	 * 
	 * @param tmpDir
	 */

	private void reassemble(String tmpDir) {
		Blocker.unblock(tmpDir + mUpdateData.getName(),
				mUpdateData.getBlockCount(), tmpDir + mUpdateData.getName()
						+ ".zip");
	}
	
	/**
	 * This method calls the getMD5Checksum method of Blocker.java and compares 
	 * the checksum retrieved from the site data to the calculated checksum of
	 * the fully reconstituted file
	 * 
	 * @param mTmpDir
	 * @param name
	 * @param fileFormat
	 * @throws Exception
	 */

	private void verify(String mTmpDir, String name, String fileFormat)
			throws Exception {
		String validChecksum = "false";
		if (mUpdateData.getChecksum().equals(
				Blocker.getMD5Checksum(mTmpDir + name + "." + fileFormat))) {
			validChecksum = "true";
		}
		Log.v(Constants.LOG_TAG, "validChecksum: " + validChecksum);

	}

	/**
	 * This method unpacks the contents of the reconstituted file
	 * 
	 * @param location
	 *            Location of the file to be unpacked
	 * @param name
	 *            Name of the file to be unpacked
	 * @throws IOException
	 */

	private void unpack(String location, String name) throws IOException {
		ZipInputStream zis = null;

		FileInputStream fis = new FileInputStream(location + name + "."
				+ mUpdateData.getFileFormat());
		zis = new ZipInputStream(fis);

		ZipEntry ze;
		File mainDir = new File(location + name + "/");
		mainDir.mkdirs();

		while ((ze = zis.getNextEntry()) != null) {
			if (ze.isDirectory()) {
				File dir = new File(location + ze.getName());
				dir.mkdirs();
				Log.v(Constants.LOG_TAG, dir.toString() + " created");
			} else {
				File f = new File(location + ze.getName());
				f.createNewFile();
				Log.v(Constants.LOG_TAG, f.toString() + " created");
				OutputStream out = new FileOutputStream(f);
				int sz = 0;
				byte[] buf = new byte[1024];
				int n;
				while ((n = zis.read(buf, 0, 1024)) > -1) {
					sz += n;
					out.write(buf, 0, n);
				}
			}
		}
		Log.v(Constants.LOG_TAG, "Done unpacking");
	}

	/**
	 * This method deletes the blocks once the file is reconstituted correctly.
	 * There is no need to keep them around once we have verification of a
	 * stable file and it has been unpacked to the temp directory.
	 * 
	 * @param mTmpDir
	 *            Location of the blocks
	 */

	private void deleteBlocks(String mTmpDir) {
		for (int i = 1; i <= mUpdateData.getBlockCount(); i++) {
			File f = new File(mTmpDir + mUpdateData.getName() + i);
			f.delete();
			Log.v(Constants.LOG_TAG, mUpdateData.getName() + " block " + i
					+ " deleted");
		}
		Log.v(Constants.LOG_TAG, mUpdateData.getName()
				+ "'s blocks deleted from temp folder");
	}

	/**
	 * This method copies the unpacked files from the temp directory to the main
	 * directory on the sdcard to be used by the Digital Pathfinder app
	 * 
	 * @param sourceLocation
	 *            Where the original files are
	 * @param targetLocation
	 *            Where they are going to be moved to
	 * @throws IOException
	 */

	private void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {

			FileInputStream in = new FileInputStream(sourceLocation);
			FileOutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	/**
	 * This method deletes the temp directory for the recently finished
	 * successfully downloaded site to clear anything remaining and make room
	 * 
	 * @param location
	 *            Location of the temp directory of the recent site
	 * @return
	 */

	private boolean deleteTemp(String location) {
		File path = new File(location);
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteTemp(files[i].toString());
				} else {
					Log.v(Constants.LOG_TAG, "Deleting " + files[i].toString());
					files[i].delete();
				}
			}
		}
		Log.v(Constants.LOG_TAG, "Deleting " + path.toString());
		return (path.delete());
	}

	/**
	 * Update the properties file listing the installed sites
	 * 
	 * @return
	 */
	private boolean updateSiteProps() {
		File sites = new File("/sdcard/Geoplicity/"
				+ Constants.DEFAULT_SITE_PROPERTIES);
		if (!sites.exists()) {
			try {
				if (!sites.createNewFile()) {
					Log.e(Constants.LOG_TAG, "Failed to create " + sites);
					return false;
				} else {
					Log.v(Constants.LOG_TAG, "Created " + sites);
				}
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "Failed to create" + sites, e);
				return false;
			}
		}
		try {
			Properties sitesProps = new Properties();
			sitesProps.load(new FileInputStream(sites));
			sitesProps.setProperty(mUpdateData.getName(),
					mUpdateData.getVersion());
			sitesProps.save(new FileOutputStream(sites), "Updated "
					+ mUpdateData.getName());
			Log.v(Constants.LOG_TAG, "updated " + sites);
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Failed to save" + sites, e);
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
	 * Post a notification about the updates progress.
	 * @param complete
	 */
	private void notifyUser() {
		if (mContext == null) {
			Log.e(Constants.LOG_TAG, "null context, cannot notify for site "
					+ mUpdateData.getName());
			return;
		}
		CharSequence contentTitle;
		CharSequence contentText = null;
		int flags = 0;
		if (mUpdateData.isUpdateComplete()) {
			if (mUpdateData.isNewSite()) {
				contentTitle = "Site Install Complete!";
				contentText = "Successfully Installed Site "
						+ mUpdateData.getName();
			} else {
				contentTitle = "Site Update Complete!";
				contentText = "Successfully Updates Site "
						+ mUpdateData.getName();
			}
		} else if (mUpdateData.isUpdateInProgress()) {
			contentTitle = "Site Download in Progress";
			contentText = "Currently downloading " + mUpdateData.getName();
			flags = Notification.FLAG_ONGOING_EVENT;
		} else if (mUpdateData.hasError()) {
			contentTitle = "Update Failed";
			contentText = "Update for Site " + mUpdateData.getName()
					+ " failed.";
		} else if (mUpdateData.hasUpdateStarted()) {
			contentTitle = "Update Paused";
			contentText = "Update for Site " + mUpdateData.getName()
					+ " has been paused.";
			flags = Notification.FLAG_ONGOING_EVENT;
		} else {
			// No notification
			return;
		}

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(ns);

		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, contentText, when);
		notification.flags = flags;
		// notification.defaults |= Notification.DEFAULT_SOUND;
		// notification.defaults |= Notification.DEFAULT_VIBRATE;

		Context context = mContext.getApplicationContext();

		Intent notificationIntent = new Intent(
				Constants.INTENT_ACTION_LAUNCH_SITE_UPDATE);
		notificationIntent.putExtra(Constants.INTENT_EXTRA_SITE_UPDATE_NAME,
				mUpdateData.getName());

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
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
