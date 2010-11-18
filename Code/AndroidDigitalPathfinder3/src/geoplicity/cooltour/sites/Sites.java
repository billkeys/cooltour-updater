package geoplicity.cooltour.sites;

import java.io.IOException;
import java.util.ArrayList;
/**
 * Wrapper around the properties file that contains site data.
 * 
 * @author Brendon Drew (bjdrew@gmail.com)
 *
 */
public class Sites extends RemoteProperties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Sites(String url) throws IOException {
		super(url);
	}

	public ArrayList<SiteData> getSites() {
		ArrayList<SiteData> sites = new ArrayList<SiteData>();
		for (Object keyObj : keySet()) {
			SiteData site = new SiteData();
			site.setName(keyObj.toString());
			site.setVersion(getProperty(site.getName()));
			sites.add(site);
		}
		return sites;
	}

}
