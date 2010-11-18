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
	//	long blockSize;
//	int blocks;
//	String fileFormat;
//	long fileSize;
	public static final String KEY_BLOCK_SIZE = "block_size";
	public static final String KEY_BLOCK_COUNT = "num_blocks";
	public static final String KEY_CHECKSUM = "checksum";
	public static final String KEY_FILE_FORMAT = "file_format";
	public static final String KEY_FILE_SIZE = "file_size";
	int blocksDownloaded = 0;
	public int getBlocksDownloaded() {
		return blocksDownloaded;
	}
	public void setBlocksDownloaded(int blocksDownloaded) {
		this.blocksDownloaded = blocksDownloaded;
	}
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
}
