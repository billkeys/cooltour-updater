package geoplicity.cooltour.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SiteUpdaterThread extends Thread {

	public SiteUpdaterThread(SiteUpdateDetails update) {
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}
	private void downloadBlock(String from, String name, String amount, String to){
		try {
			for(int i=1;i<=Integer.parseInt(amount);i++){
			URL url = new URL(from+name+i);
			BufferedInputStream in = new BufferedInputStream(url.openStream());
			File dir = new File(to+name+"\\temp\\");
			dir.mkdirs();
			FileOutputStream fos = new FileOutputStream(dir+"\\"+name+i);
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int x = 0;
			while ((x=in.read(data,0,1024))>=0){
				bout.write(data,0,x);
			}
			bout.close();
			in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param location
	 * @param name
	 */
	private void unpack(String location, String name){
		try {
			ZipInputStream zis = null;

			FileInputStream fis = new FileInputStream(location+name+"\\temp\\"+name+".zip");
			zis = new ZipInputStream(fis);

			ZipEntry ze;

			while ((ze = zis.getNextEntry()) != null) {
				if (ze.getName().endsWith("/")) {
					File dir = new File(location + name + "\\" + ze.getName());
					dir.mkdirs();
				} else {
					File f = new File(location + name + "\\" + ze.getName());
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
}
