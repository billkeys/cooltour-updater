package geoplicity.cooltour.updater;
import java.io.*;

public class Blocker {

	/**
	 * args[0] is the name of the file (eg. Olana)
	 * args[1] is the size of the blocks (eg. 5000000)
	 * args[2] is the location of the file (eg. C:\Test\server\sites\);
	 */

	public static void block(String name, String blockSize, String location) {
		try {
			FileInputStream fis = new FileInputStream(location + name + ".zip");
			int size = Integer.parseInt(blockSize);
			byte buffer[] = new byte[size];

			int i = 1;
			while (true) {
				int bytesToTransfer = fis.read(buffer, 0, size);
				if (bytesToTransfer == -1) //if none left
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
	 * args[0] is the name of the file (eg. Olana)
	 * args[1] is the number of blocks (eg. 6)
	 * args[2] is the location of the file (eg. C:\Test\sdcard\geoplicity\);
	 */
	
	public static void unblock(String name, int numberOfBlocks, String target){
		try {
			//int numberOfBlocks = Integer.parseInt(amount);
			FileOutputStream fos = new FileOutputStream(target);
			for(int i = 1;i<=numberOfBlocks;i++){
				File f = new File(name+i);
				long sizeOfFile = f.length();
				FileInputStream fis = new FileInputStream(f);
				byte buffer[] = new byte[(int)sizeOfFile];
				fis.read(buffer);
				fos.write(buffer);
				fos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
