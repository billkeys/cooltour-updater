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
	boolean updateStarted;
	boolean updateInProgress;
	boolean updateComplete;
	String resultMessage;
	public SiteUpdateData() {
		super();
	}
	public SiteUpdateData(String urlString) throws IOException {
		super(urlString);
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
	public boolean isUpdateComplete() {
		return updateComplete;
	}
	public void setUpdateComplete(boolean updateComplete) {
		this.updateComplete = updateComplete;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public boolean hasUpdateStarted() {
		return updateStarted;
	}
	public void setUpdateStarted(boolean updateStarted) {
		this.updateStarted = updateStarted;
	}
	public int getCurrentMode() {
		return currentMode;
	}
	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
	}

}
