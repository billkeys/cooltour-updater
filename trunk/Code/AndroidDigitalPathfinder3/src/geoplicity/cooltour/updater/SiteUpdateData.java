package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;

import java.io.IOException;

/**
 * Wrapper around the properties file that contains site update data.
 * 
 * @author Brendon Drew (bjdrew@gmail.com)
 *
 */
public class SiteUpdateData extends SiteData {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String KEY_BLOCK_SIZE = "block_size";
	public static final String KEY_BLOCK_COUNT = "num_blocks";
	public static final String KEY_CHECKSUM = "checksum";
	public static final String KEY_FILE_FORMAT = "file_format";
	public static final String KEY_FILE_SIZE = "file_size";
	int currentBlock = 1;
	int currentMode = 0;
	boolean updateInProgress;

	boolean hasError;
	String statusMessage;
	public SiteUpdateData() {
		super();
	}
	public SiteUpdateData(String urlString, SiteData site) throws IOException {
		super(urlString);
		this.updateAvailable = site.isUpdateAvailable();
		this.updateStarted = site.hasUpdateStarted();
		this.newSite = site.isNewSite();
	}
	public SiteUpdateData(SiteData site) {
		super();
		setName(site.getName());
		setVersion(site.getVersion());
	}
	public long getBlockSize() {
		return new Long(getProperty(KEY_BLOCK_SIZE));
	}
	public void setBlockSize(long blockSize) {
		setProperty(KEY_BLOCK_SIZE, ""+blockSize);
	}
	public int getBlockCount() {
		return new Integer(getProperty(KEY_BLOCK_COUNT));
	}
	public void setBlockCount(int blocks) {
		setProperty(KEY_BLOCK_COUNT, ""+blocks);
	}
	public String getFileFormat() {
		return getProperty(KEY_FILE_FORMAT);
	}
	public void setFileFormat(String fileFormat) {
		setProperty(KEY_FILE_FORMAT, fileFormat);
	}
	public long getFileSize() {
		return Long.parseLong(getProperty(KEY_FILE_SIZE));
	}
	public void setFileSize(long fileSize) {
		setProperty(KEY_FILE_SIZE, fileSize+"");
	}
	public boolean isUpdateInProgress() {
		return updateInProgress;
	}
	public void setUpdateInProgress(boolean updateStarted) {
		this.updateInProgress = updateStarted;
	}
	public int getCurrentBlock() {
		return currentBlock;
	}
	public void setCurrentBlock(int blocksDownloaded) {
		this.currentBlock = blocksDownloaded;
	}
	public void incrementCurrentBlock() {
		this.currentBlock++;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String resultMessage) {
		this.statusMessage = resultMessage;
	}
	public int getCurrentMode() {
		return currentMode;
	}
	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
	}
	public boolean hasError() {
		return hasError;
	}
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
	public void reset() {
		currentBlock = 1;
		currentMode = 0;
		updateInProgress = false;
		updateStarted = false;
		
	}
	public String toString() {
		return "[ SiteUpdateData :: "+super.toString() +
		", currentBlock="+currentBlock+", " +
		"currentMode="+currentMode+"," +
		"updateInProgress="+updateInProgress+", " +
		"hasError="+hasError+", " +
		"statusMessage="+statusMessage+" ]";
	}
}
