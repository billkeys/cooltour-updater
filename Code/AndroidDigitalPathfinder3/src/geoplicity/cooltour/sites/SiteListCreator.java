/**
 * @author - Abuelsaad and Goldsmith
 * 
 */

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

/**
 * Class used to load properties from the site-props.txt file which contains 
 * the sites the user has downloaded on the device. The sites are stored in a 
 * sites List which is returned to the MainUI activity and used to populate the
 * site selection drop down menu on the main screen.
 *
 */
public class SiteListCreator extends Properties {
	
	static final long serialVersionUID = 1L;
	private static final String STORAGE_DEVICE = "/sdcard/";
	
	/**
	 * Method used to find and load the site-props.txt file. The key of each 
	 * property in the file contains the name of the site available on the
	 * device. An iterator is used to loop over the key set and check if the site
	 * name contains an underscore; if the condition is met the underscore is 
	 * removed from the name so that it can be displayed to the user on the main
	 * screen. The site names are then added to the sites List which is returned
	 * back to the MainUI activity. 
	 * @return List of sites created from the properties in site-props.txt
	 * @throws NoSitePropsException if site-props.txt cannot be found
	 */
	@SuppressWarnings("unchecked")
	public List<String> getSiteChoices() throws NoSitePropsException{
		
		List<String> sites = new Vector<String>();
		String rootDir = Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR);
		try {
			FileInputStream fis = new FileInputStream(new File(STORAGE_DEVICE+
					"/"+rootDir+"/"+Constants.DEFAULT_SITE_PROPERTIES));
			
			load(fis);
			fis.close();
			Set siteList = keySet();
			Iterator<String> siteListIterator =  (Iterator<String>) siteList.iterator();
			
			while(siteListIterator.hasNext()){
				String site = siteListIterator.next();
				if (site.contains("_")){
					site = site.replaceAll("_", " ");
				}
				if (site.equals(Constants.DEFAULT_SITE))
					sites.add(0, site);
				else
					sites.add(site);
			}
			return sites;
		} 
		catch(FileNotFoundException noSiteProps){
			Logger.log(Logger.TRAP, "Unable to read file "+ 
					Constants.DEFAULT_SITE_PROPERTIES +" using path "+
					STORAGE_DEVICE+Constants.DEFAULT_SITE_PROPERTIES+"\n"+
					noSiteProps.toString());
			
			throw new NoSitePropsException("Unable to read file "+
					Constants.DEFAULT_SITE_PROPERTIES);
		}
		catch(IOException badFileInputStream){
			Logger.log(Logger.TRAP, "Unable to open input stream to file "+ 
					Constants.DEFAULT_SITE_PROPERTIES +" using path "+
					STORAGE_DEVICE+Constants.DEFAULT_SITE_PROPERTIES+"\n"+
					badFileInputStream.toString());
			
			throw new NoSitePropsException("Unable to open input stream to "+
					Constants.DEFAULT_SITE_PROPERTIES);
		}
	}   

}
