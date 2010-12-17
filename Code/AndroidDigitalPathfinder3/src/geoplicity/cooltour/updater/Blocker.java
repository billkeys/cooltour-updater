package geoplicity.cooltour.updater;

import geoplicity.cooltour.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import android.util.Log;

/**
 * 
 * @author Skyler Dodge (skylarkdodge@gmail.com)
 * 
 */

public class Blocker {

	/**
	 * This method runs on the server side to break a specified zip file into
	 * blocks by filling a byte buffer and creating a new file once the
	 * specified block size is reached and then store them in the same location
	 * as the original file
	 * 
	 * @param name
	 *            The name of the file to be blocked
	 * 
	 * @param blockSize
	 *            The pre-calculated optimal block size
	 * 
	 * @param location
	 *            The location that the file is located (i.e., the server)
	 */

	public static void block(String name, String blockSize, String location) {
		try {
			FileInputStream fis = new FileInputStream(location + name + ".zip");
			int size = Integer.parseInt(blockSize);
			byte buffer[] = new byte[size];

			int i = 1;
			while (true) {
				int bytesToTransfer = fis.read(buffer, 0, size);
				if (bytesToTransfer == -1)
					break;

				String filename = location + name + i;
				FileOutputStream fos = new FileOutputStream(filename);
				fos.write(buffer, 0, bytesToTransfer);
				fos.flush();
				fos.close();

				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method runs on the client side (i.e. on the device) and is
	 * responsible for reconstituting the blocks into the original file by
	 * copying all the bytes in the order of the block numbers into one file
	 * 
	 * @param name
	 *            The name of the file to be reconstituted
	 * @param numberOfBlocks
	 *            The number of total blocks that make up the original file
	 * @param target
	 *            The target directory that the blocks are located and the file
	 *            will be unblocked to
	 */

	public static void unblock(String name, int numberOfBlocks, String target) {
		try {
			FileOutputStream fos = new FileOutputStream(target);
			for (int i = 1; i <= numberOfBlocks; i++) {
				File f = new File(name + i);
				long sizeOfFile = f.length();
				FileInputStream fis = new FileInputStream(f);
				byte buffer[] = new byte[(int) sizeOfFile];
				fis.read(buffer);
				fos.write(buffer);
				fos.flush();
				Log.v(Constants.LOG_TAG, "Unblocker: Block " + i + " done");
			}
			Log.v(Constants.LOG_TAG, "File fully reassembled");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The following methods are to be used both on the server and client side. 
	 * Before the file on the server is blocked, it must be assigned a checksum 
	 * to verify that it has been correctly unblocked after download.  Therefore, 
	 * the getMD5Checksum method must be called on both the original file on the 
	 * server as well as the file on the device before it is unpacked.
	 */

	/**
	 * This method creates a byte array checksum
	 * 
	 * @param filename
	 *            The name of the file to create a checksum with
	 * @return Returns a byte array representation of the checksum
	 * @throws Exception
	 */

	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	/**
	 * This method takes a file and converts a byte array checksum to string
	 * representation for verification after the unblock method is done
	 * reconstituting the file
	 * 
	 * @param filename
	 *            The name of the file to pass to createChecksum
	 * @return Returns a string representation of the checksum
	 * @throws Exception
	 */

	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}
