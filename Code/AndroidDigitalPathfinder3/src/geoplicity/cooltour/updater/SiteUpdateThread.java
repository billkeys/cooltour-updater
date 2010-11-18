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
	int mode;
	int currentBlock = 1;
	SiteUpdateData updateData; 
	public SiteUpdateThread(SiteUpdateData update) {
		updateData = update;
		//currentBlock = updateData.get
	}
	@Override
	public void run() {
		Log.v(Constants.LOG_TAG, "start run, mode="+mode);
		switch (mode) {
			case MODE_START:
				downloadBlocks(Constants.UPDATE_TEMP_DIR);
			case MODE_RESUME:
				
			case MODE_REASSEMBLE:
				//reassemble(Constants.UPDATE_TEMP_DIR);
			case MODE_UNPACK:
				//unpack(Constants.UPDATE_TEMP_DIR, updateData.getName());
			case MODE_CLEANUP:
				//deleteTemp(Constants.UPDATE_TEMP_DIR);
			case MODE_FINISH:
				updateSiteProps();
			default:
			
		}
	}
	private void downloadBlocks(String tmpDir) {
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
		for(int i=currentBlock;i<=updateData.getBlockCount();i++){
			String blockName = updateData.getName()+i;
			downloadBlock(Constants.UPDATE_SERVER+updateData.getName()+"/"+updateData.getVersion()+"/"+blockName, tmpDir+blockName);
		}

	}
	private void downloadBlock(String urlString, String saveTo){
		try {
			//for(int i=1;i<=Integer.parseInt(amount);i++){
			Log.v(Constants.LOG_TAG, "fetching "+urlString);
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

			while ((ze = zis.getNextEntry()) != null) {
				if (ze.getName().endsWith("/")) {
					File dir = new File(Constants.SDCARD_ROOT + name + "/" + ze.getName());
					dir.mkdirs();
				} else {
					File f = new File(Constants.SDCARD_ROOT + name + "/" + ze.getName());
					f.createNewFile();
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
				System.out.println(files[i]);
				if (files[i].isDirectory()) {
					deleteTemp(files[i].toString());
				} else {
					files[i].delete();
				}
			}
		}
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
				Properties sitesProps = new Properties();
				sitesProps.load(new FileInputStream(sites));
				sitesProps.setProperty(updateData.getName(), updateData.getVersion());
				sitesProps.save(new FileOutputStream(sites), "Updated "+updateData.getName());
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "Failed to update"+sites, e);
				return false;
			}
		}
		return true;

	}
}
