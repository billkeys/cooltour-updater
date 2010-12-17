package geoplicity.cooltour.sites;

import geoplicity.cooltour.util.Constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import android.util.Log;

/**
 * A Properties class that can be built from a URL to a properties file
 * 
 * @author Brendon Drew bjdrew@gmail.com
 * 
 */
public class RemoteProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The URL to the remote properties
	 */
	private String mUrl;

	/**
	 * 
	 * @param locationUrl
	 * @throws IOException
	 */
	public RemoteProperties(String locationUrl) throws IOException {
		mUrl = locationUrl;
		Log.v(Constants.LOG_TAG, "fetching " + locationUrl);
		URL urlObj = new URL(locationUrl);
		URLConnection urlConn = urlObj.openConnection();
		urlConn.setDoInput(true);
		urlConn.setUseCaches(false);
		DataInputStream dis = new DataInputStream(urlConn.getInputStream());
		load(dis);
		Log.d(Constants.LOG_TAG, "" + toString());

	}

	public RemoteProperties() {
		// TODO Auto-generated constructor stub
	}

	public String getUrl() {
		return mUrl;
	}
}
