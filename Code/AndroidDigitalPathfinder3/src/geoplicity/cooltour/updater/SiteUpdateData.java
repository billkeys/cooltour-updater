package geoplicity.cooltour.updater;

import geoplicity.cooltour.sites.SiteData;

/**
 * Wrapper around the properties file that contains site update data.
 * 
 * @author Brendon Drew (bjdrew@gmail.com)
 *
 */
public class SiteUpdateData extends SiteData {
//	long blockSize;
//	int blocks;
//	String fileFormat;
//	long fileSize;
	public static final String KEY_BLOCK_SIZE = "block_size";
	public static final String KEY_BLOCK_COUNT = "block_count";
	public static final String KEY_CHECKSUM = "checksum";
	public static final String KEY_FILE_FORMAT = "file_format";
	public static final String KEY_FILE_SIZE = "file_size";

	public SiteUpdateData() {
		super();
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
		return new Long(getProperty(KEY_FILE_SIZE));
	}
	public void setFileSize(long fileSize) {
		setProperty(KEY_FILE_SIZE, fileSize+"");
	}
}
