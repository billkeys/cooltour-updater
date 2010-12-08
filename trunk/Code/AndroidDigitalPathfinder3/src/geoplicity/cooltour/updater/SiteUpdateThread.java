package geoplicity.cooltour.updater;

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

import android.util.Log;

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
	int mode;
	/**
	 * Set false to 
	 */
	boolean destroy;
	SiteUpdateData updateData; 
	/**
	 * 
	 * @param update
	 */
	public SiteUpdateThread(SiteUpdateData update) {
		updateData = update;
		mode = updateData.getCurrentMode();
	}
	@Override
	public void run() {
		Log.v(Constants.LOG_TAG, "start run, mode="+mode);
		updateData.setUpdateStarted(true);
		updateData.setUpdateInProgress(true);
		try {
			switch (mode) {
				case MODE_START:
					updateData.setCurrentMode(MODE_START);
					downloadBlocks(Constants.SDCARD_ROOT+"/Geoplicity/"+Constants.UPDATE_TEMP_DIR);
				case MODE_RESUME:
					//TODO Implement 
					updateData.setCurrentMode(MODE_RESUME);
					Log.v(Constants.LOG_TAG,"Current block:"+Integer.toString(updateData.getCurrentBlock()));
				case MODE_REASSEMBLE:
					updateData.setCurrentMode(MODE_REASSEMBLE);
					reassemble(Constants.SDCARD_ROOT+"/Geoplicity/"+Constants.UPDATE_TEMP_DIR);
				case MODE_UNPACK:
					updateData.setCurrentMode(MODE_UNPACK);
					unpack(Constants.SDCARD_ROOT+"/Geoplicity/"+Constants.UPDATE_TEMP_DIR, updateData.getName());
				case MODE_CLEANUP:
					updateData.setCurrentMode(MODE_CLEANUP);
					deleteTemp(Constants.SDCARD_ROOT+"/Geoplicity/"+Constants.UPDATE_TEMP_DIR);
				case MODE_FINISH:
					updateData.setCurrentMode(MODE_FINISH);
					updateSiteProps();
				default:
			}	
			updateData.setUpdateComplete(true);
		} catch (InterruptedException e) {
			Log.v(Constants.LOG_TAG, e.getMessage());
		}
		updateData.setUpdateInProgress(false);

	}
	private void downloadBlocks(String tmpDir) throws InterruptedException {
		File dir = new File(tmpDir);
		if (!dir.exists()) {
			if (dir.mkdirs()) {
				Log.v(Constants.LOG_TAG, dir+ "created");
			}
			else {
				Log.e(Constants.LOG_TAG, "Failed to create "+dir);
				return;
			}
		}
		for(int i=updateData.getCurrentBlock();i<=updateData.getBlockCount();i++){
			String blockName = updateData.getName()+i;
			downloadBlock(Constants.UPDATE_SERVER+updateData.getName()+"/"+updateData.getVersion()+"/"+blockName, tmpDir+blockName);
			Log.v(Constants.LOG_TAG,"Files done downloading");
		    if (Thread.currentThread().isInterrupted()) {
		        throw new InterruptedException("Thread Interrupted");
		      }
		}

	}
	private void downloadBlock(String urlString, String saveTo){
		try {
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
			}
			bout.close();
			in.close();
			updateData.incrementCurrentBlock();
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void reassemble(String tmpDir) {
		//Blocker b = new Blocker();
		Blocker.unblock(tmpDir+updateData.getName(), updateData.getBlockCount(), tmpDir+updateData.getName()+".zip");
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
			
			File mainDir = new File(Constants.SDCARD_ROOT+"/Geoplicity/"+name+"/");
			mainDir.mkdirs();

			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					File dir = new File(Constants.SDCARD_ROOT + "/Geoplicity/" + name + "/" + ze.getName());
					dir.mkdirs();
					Log.v(Constants.LOG_TAG,dir.toString()+" created");
				} else {
					File f = new File(Constants.SDCARD_ROOT + "/Geoplicity/" + name + "/" + ze.getName());
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
			sitesProps.setProperty(updateData.getName(), updateData.getVersion());
			sitesProps.save(new FileOutputStream(sites), "Updated "+updateData.getName());
			Log.v(Constants.LOG_TAG,"updated "+sites);
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Failed to save"+sites, e);
			return false;
		}
		
		return true;

	}
	public SiteUpdateData getUpdateData() {
		return updateData;
	}
	public void setUpdateData(SiteUpdateData updateData) {
		this.updateData = updateData;
	}
	/**
	 * 
	 */
	public void pause() {
		//TODO Implement
	}
	/**
	 * 
	 */
	public void cancel() {
		//TODO Implement
	}
}
