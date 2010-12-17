/**
 * Copyright (c) 2010 Contributors, http://geoplicity.org/
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
package geoplicity.cooltour.sites;


import java.io.IOException;
/**
 * Wrapper around the properties file that contains site data.
 * 
 * @author Brendon Drew (bjdrew@gmail.com)
 *
 */
public class SiteData extends RemoteProperties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String KEY_NAME = "name";
	public static final String KEY_VERSION = "version";
	public static final String KEY_NEW_VERSION = "new_version";
	public static final String KEY_UPADATE_URL = "update_url";
	protected boolean mUpdateAvailable;
	protected boolean mUpdateStarted;
	protected boolean mUpdateComplete;
	protected boolean mNewSite;
	public SiteData() {
		super();
	}
	public SiteData(String locationUrl) throws IOException {
		super(locationUrl);
	}
	public String getName() {
		return getProperty(KEY_NAME);
	}
	public void setName(String name) {
		setProperty(KEY_NAME, name);
	}
	public String getUpdateUrl() {
		return getProperty(KEY_UPADATE_URL);
	}
	public void SetUpdateUrl(String url) {
		setProperty(KEY_UPADATE_URL, url);
	}
	public String getVersion() {
		return getProperty(KEY_VERSION);
	}
	public void setVersion(String version) {
		setProperty(KEY_VERSION, version);
	}
	public String getNewVersion() {
		return getProperty(KEY_NEW_VERSION);
	}
	public void setNewVersion(String version) {
		setProperty(KEY_NEW_VERSION, version);
	}
	public boolean isUpdateAvailable() {
		return mUpdateAvailable;
	}
	public void setUpdateAvailable(boolean updateAvailable) {
		this.mUpdateAvailable = updateAvailable;
	}
	public boolean isNewSite() {
		return mNewSite;
	}
	public void setNewSite(boolean newSite) {
		this.mNewSite = newSite;
	}
	public boolean hasUpdateStarted() {
		return mUpdateStarted;
	}
	public void setUpdateStarted(boolean updateStarted) {
		this.mUpdateStarted = updateStarted;
	}
	public boolean isUpdateComplete() {
		return mUpdateComplete;
	}
	public void setUpdateComplete(boolean updateComplete) {
		this.mUpdateComplete = updateComplete;
	}
	public String toString() {
		return "[ SiteData :: "+super.toString() +
		", updateAvailable="+mUpdateAvailable+", " +
		"newSite="+mNewSite+"," +
		"updateStarted="+mUpdateStarted+"," +
		"updateComplete="+mUpdateComplete+" ]";
	}
}
