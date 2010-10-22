package geoplicity.cooltour.sites;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
/**
 * Wrapper around the properties file that contains site data.
 * 
 * @author Brendon Drew (bjdrew@gmail.com)
 *
 */
public class SiteData extends Properties{
	//Properties props;
//	String name;
//	String updateUrl;
//	String version;
	public static final String KEY_NAME = "name";
	public static final String KEY_VERSION = "version";
	public static final String KEY_UPADATE_URL = "update_url";
	public SiteData() {
		super();
		//props = new Properties();
	}
	public SiteData(String locationUrl) throws IOException {
		//props = new Properties();
		URL url = new URL(locationUrl);
	    URLConnection urlConn = url.openConnection(); 
	    urlConn.setDoInput(true); 
	    urlConn.setUseCaches(false);
	    DataInputStream dis = new DataInputStream(urlConn.getInputStream()); 
	    load(dis);
		
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
}
