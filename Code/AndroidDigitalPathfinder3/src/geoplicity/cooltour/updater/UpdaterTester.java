package geoplicity.cooltour.updater;
public class UpdaterTester {

	/**
	 * args[0] is the location of the file on the server (eg. http://reachforthestarsmcc.com/sdodge/files/sites/)
	 * args[1] is the name of the file (eg. Olana)
	 * args[2] is the block size (eg. 5000000)
	 * args[3] is the number of blocks (eg. 6)
	 * args[4] is the base location to store on device (eg. C:\Test\sdcard\geoplicity\)
	 */
	public static void main(String[] args) {
		//Blocker.block("Olana", "5000000", "C:\\Test\\Other\\Attempt2\\");
		//Downloader.download("http://reachforthestarsmcc.com/sdodge/files/sites/", "Olana", "6", "C:\\Test\\sdcard\\geoplicity\\");
		//Unblocker.unblock("Olana", "6", "C:\\Test\\sdcard\\geoplicity\\");
		//ZipUnpacker.unpack("C:\\Test\\sdcard\\geoplicity\\", "Olana");
		
		//Blocker.block(args[1], args[2], "server");
		//Downloader.download(args[0], args[1], args[3], args[4]);
		//Unblocker.unblock(args[1], args[3], args[4]);
		//ZipUnpacker.unpack(args[4], args[1]);
		//CleanUp.deleteTemp(args[4]+args[1]+"\\temp");
	}
	public static void testThread() {
		SiteUpdateData update = new SiteUpdateData();
		SiteUpdateThread updateThread = new SiteUpdateThread(update);
	}

}
