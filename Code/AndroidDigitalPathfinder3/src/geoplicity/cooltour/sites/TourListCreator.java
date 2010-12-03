package geoplicity.cooltour.sites;

import geoplicity.cooltour.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;


/**
 * The TourListCreator class generates the appropriate tour choices that the
 * user can select for a site. Once a site has been selected by the user from
 * the main screen, a call to this class is made to generate the corresponding 
 * tour choices using the site's way properties file. The tour choices are 
 * retrieved from the properties file and stored in the HashMap m_TourList which
 * is returned to the MainUI activity and used to populate the tour selection
 * drop down menu on the main screen.
 * @author Deyaa Abuelsaad
 *
 */
public class TourListCreator extends Properties {

	private static final long serialVersionUID = 1L;
	private static final String STORAGE_DEVICE = "/sdcard/";
	
	/* String value containing the currently selected site */
	private String m_SelectedSite = "";
	
	/* HashMap used to store the tour choices as the key and details about of 
	 * the tour as the value */
	private HashMap<String, String> m_TourList = new HashMap<String, String>();

	
	/**
	 * Overloaded constructor passed the site the user selected as a 
	 * parameter. The site is set once the user has selected a tour site from
	 * the site selection drop down menu on the main screen.
	 * @param m_SelectedSite - Site currently selected by the user
	 */
	public TourListCreator(String m_SelectedSite)
	{
		this.m_SelectedSite = m_SelectedSite;
	}
	
	
	/**
	 * Method used for generating this class's HashMap (m_TourList). The
	 * currently selected site (m_SelectedSite) is used to locate and load 
	 * properties from the site's corresponding way properties file on the SD 
	 * card. The map.pkg.* properties (eg. map.pkg.1, map.pkg.2) are passed into 
	 * addToTourList() method which then parses the properties and adds them to
	 * the HashMap.
	 * @return HashMap containing key and value pairs of the tour type and the
	 * details of the tour, respectively.
	 * @throws NoWayPropsException if the way props file was not found on the
	 * SD card
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> getTourChoices() throws NoWayPropsException{
		String rootDir = Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR);   
		try {
			FileInputStream fis = new FileInputStream(new File(STORAGE_DEVICE+
					rootDir + "/" + this.m_SelectedSite + "/configs/"+ 
					Constants.DEFAULT_WAY_PROPERTIES));
			
			Logger.log(Logger.TRAP,"Loading way properties from " + STORAGE_DEVICE+
					rootDir + "/" + m_SelectedSite + "/configs/"+ 
					Constants.DEFAULT_WAY_PROPERTIES);
			
			load(fis);
			fis.close();
			
			//Iterate over the keyset of the loaded properties and only get the
			//properties that begin with "map.pkg." 
			for(int i = 0; i < keySet().size(); i++)
			{
				String property = "map.pkg." + i;
				String tourProp = this.getProperty(property);
				if(tourProp != null)
				{
					addToTourList(tourProp);
				}
				else 
					break; //This condition is only reached when all the 
						   //map.pk.* properties have been loaded from the 
						   //properties file. Break out of the loop at this point.
			}
			return this.m_TourList;
		}
		catch(FileNotFoundException noWayProps){
			Logger.log(Logger.TRAP, "Unable to read file stway-5-props.txt using" +
					" path "+ STORAGE_DEVICE+rootDir + "/" + this.m_SelectedSite
					+ "/configs/"+Constants.DEFAULT_WAY_PROPERTIES
					+noWayProps.toString());
			throw new NoWayPropsException("Unable to read file stway-5-props.txt");
		}
		catch(IOException badFileInputStream){
			Logger.log(Logger.TRAP, "Unable to open input stream to file stway-5-props.txt "+
					"using path "+ STORAGE_DEVICE+rootDir + "/" + this.m_SelectedSite
					+ "/configs/"+Constants.DEFAULT_WAY_PROPERTIES
					+badFileInputStream.toString());
			throw new NoWayPropsException("Unable to open input stream to stway-5-props.txt");
		}
	}
	
	/**
	 * Parses the tour property by using a StringTokenizer to break it up into
	 * tokens. Only the first, fourth and fifth tokens will be stored in the
	 * HashMap since these tokens contain the tour type, the distance of the
	 * tour and the number of stops in the tour, respectively. The tour type
	 * is stored in the HashMap as the key and the distance and number of stops
	 * are stored as the value.
	 * @param property - tour property that requires parsing
	 */
	private void addToTourList(String property)
	{
		StringTokenizer st = new StringTokenizer(property, " ");
		/* Set the first value in the string to the tour type */
		String tourType = st.nextToken();
		/* Skip the next two values */
		st.nextToken();
		st.nextToken();
		/* Set the fourth and fifth values in the string to the distance and number of stops */
		String distance = st.nextToken();
		
		/* Truncate the distance to make it readable to the user */
		try { distance = String.format(" %.2f", Double.parseDouble(distance)); }
		catch(NumberFormatException distanceNotDouble){
			Logger.log(Logger.TRAP, "Distance provided was not double value");
		}
		String stops = st.nextToken();
		String tourDetails = "Dist: " + distance + " ft / " + stops + " stops";
		this.m_TourList.put(tourType, tourDetails);
	}
}