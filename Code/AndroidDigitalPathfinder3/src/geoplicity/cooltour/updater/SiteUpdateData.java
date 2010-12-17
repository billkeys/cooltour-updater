/**
 * Copyright (c) Contributors, http://geoplicity.org/
 * See CONTRIBUTORS.TXT for a full list of copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Geoplicity Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE DEVELOPERS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
	/**
	 * The current block to download
	 */
	int mCurrentBlock = 1;
	/**
	 * The current step
	 */
	int mCurrentMode = 0;
	/**
	 * Set true to indicate that the update is in progress
	 * Note: this means that the thread has started once, 
	 * but not necessarily that the thread is currently 
	 * running.
	 */
	boolean mUpdateInProgress;
	/**
	 * Set true if an error had occurred.
	 */
	boolean mHasError;
	/**
	 * A string that represents the updates progress.
	 */
	String mStatusMessage;
	/**
	 * 
	 */
	public SiteUpdateData() {
		super();
	}
	/**
	 * Constructs the instance from a file hosted at some URL. 
	 * @param urlString
	 * @param site
	 * @throws IOException
	 */
	public SiteUpdateData(String urlString, SiteData site) throws IOException {
		super(urlString);
		this.mUpdateAvailable = site.isUpdateAvailable();
		this.mUpdateStarted = site.hasUpdateStarted();
		this.mNewSite = site.isNewSite();
	}
	/**
	 * 
	 * @param site
	 */
	public SiteUpdateData(SiteData site) {
		super();
		setName(site.getName());
		setVersion(site.getVersion());
	}
	public String getChecksum(){
		return new String(getProperty(KEY_CHECKSUM));
	}
	public void setChecksum(String checksum){
		setProperty(KEY_CHECKSUM, ""+checksum);
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
		return mUpdateInProgress;
	}
	public void setUpdateInProgress(boolean updateStarted) {
		this.mUpdateInProgress = updateStarted;
	}
	public int getCurrentBlock() {
		return mCurrentBlock;
	}
	public void setCurrentBlock(int blocksDownloaded) {
		this.mCurrentBlock = blocksDownloaded;
	}
	public void incrementCurrentBlock() {
		this.mCurrentBlock++;
	}
	public String getStatusMessage() {
		return mStatusMessage;
	}
	public void setStatusMessage(String resultMessage) {
		this.mStatusMessage = resultMessage;
	}
	public int getCurrentMode() {
		return mCurrentMode;
	}
	public void setCurrentMode(int currentMode) {
		this.mCurrentMode = currentMode;
	}
	public boolean hasError() {
		return mHasError;
	}
	public void setHasError(boolean hasError) {
		this.mHasError = hasError;
	}
	/**
	 * Clears update status
	 */
	public void reset() {
		mCurrentBlock = 1;
		mCurrentMode = 0;
		mUpdateInProgress = false;
		mUpdateStarted = false;
		
	}
	public String toString() {
		return "[ SiteUpdateData :: "+super.toString() +
		", currentBlock="+mCurrentBlock+", " +
		"currentMode="+mCurrentMode+"," +
		"updateInProgress="+mUpdateInProgress+", " +
		"hasError="+mHasError+", " +
		"statusMessage="+mStatusMessage+" ]";
	}
}
