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
	protected boolean updateAvailable;
	protected boolean updateStarted;
	protected boolean updateComplete;
	protected boolean newSite;
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
		return updateAvailable;
	}
	public void setUpdateAvailable(boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}
	public boolean isNewSite() {
		return newSite;
	}
	public void setNewSite(boolean newSite) {
		this.newSite = newSite;
	}
	public boolean hasUpdateStarted() {
		return updateStarted;
	}
	public void setUpdateStarted(boolean updateStarted) {
		this.updateStarted = updateStarted;
	}
	public boolean isUpdateComplete() {
		return updateComplete;
	}
	public void setUpdateComplete(boolean updateComplete) {
		this.updateComplete = updateComplete;
	}
	public String toString() {
		return "[ SiteData :: "+super.toString() +
		", updateAvailable="+updateAvailable+", " +
		"newSite="+newSite+"," +
		"updateStarted="+updateStarted+"," +
		"updateComplete="+updateComplete+" ]";
	}
}
