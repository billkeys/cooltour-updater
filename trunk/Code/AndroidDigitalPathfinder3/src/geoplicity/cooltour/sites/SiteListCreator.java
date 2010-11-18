package geoplicity.cooltour.sites;

import geoplicity.cooltour.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;

public class SiteListCreator extends Properties {
	
	static final long serialVersionUID = 1L;
	//private static final String STORAGE_DEVICE = "/sdcard/";
	
	
	@SuppressWarnings("unchecked")
	public List<String> getSiteChoices() throws NoSitePropsException{
		List<String> sites = new Vector<String>();
		File sitesFile = new File(Constants.GEOPLICITY_SDCARD_ROOT+Constants.DEFAULT_SITE_PROPERTIES);
		try {
			FileInputStream fis = new FileInputStream(sitesFile);
			load(fis);
			fis.close();
			Set siteList = keySet();
			Iterator<String> siteListIterator =  (Iterator<String>) siteList.iterator();
			while(siteListIterator.hasNext()){
				String site = siteListIterator.next();
				if (site.contains("_")){
					site = site.replaceAll("_", " ");
					sites.add(site);
				}
				else
					sites.add(site);
			}
			return sites;
		}
		catch(FileNotFoundException noSiteProps){
			Logger.log(Logger.TRAP, "Unable to read file site-props.txt using path "+
					sitesFile+" "+noSiteProps.toString());
			throw new NoSitePropsException("Unable to read file site-props.txt");
		}
		catch(IOException badFileInputStream){
			Logger.log(Logger.TRAP, "Unable to open input stream to file site-props.txt "+
					"using path "+sitesFile+" "+badFileInputStream.toString());
			throw new NoSitePropsException("Unable to open input stream to site-props.txt");
		}
	}   

}
