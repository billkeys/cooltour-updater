package geoplicity.cooltour.sites;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import geoplicity.cooltour.util.Constants;
import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;
import android.util.Log;

public class TourListCreator extends Properties {

	private static final long serialVersionUID = 1L;
	private static final String STORAGE_DEVICE = "/sdcard/";
	private String m_SelectedSite = "";
	private HashMap<String, String> m_TourList = new HashMap<String, String>();

	public TourListCreator(String m_SelectedSite)
	{
		this.m_SelectedSite = m_SelectedSite;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, String> getTourChoices() throws NoSitePropsException{
		String rootDir = Property.getProperty("app.root.dir");   
		try {
			FileInputStream fis = new FileInputStream(new File(STORAGE_DEVICE+
					rootDir + "/" + m_SelectedSite + "/configs/"+ 
					Constants.DEFAULT_WAY_PROPERTIES));
			
			Log.v("TourListCreator", "Loading way properties from " + STORAGE_DEVICE+
					rootDir + "/" + m_SelectedSite + "/configs/"+ 
					Constants.DEFAULT_WAY_PROPERTIES);
			
			load(fis);
			fis.close();
			
			for(int i = 0; i < keySet().size(); i++)
			{
				String property = "map.pkg." + i;
				String tourProp = this.getProperty(property);
				if(tourProp != null)
				{
					addToTourList(tourProp);
				}
				else //This condition is reached when all the map.pk.* properties have been loaded from the properties file. Break out of the loop at this point.
					break;
			}
			return this.m_TourList;
		}
		catch(FileNotFoundException noSiteProps){
			Logger.log(Logger.TRAP, "Unable to read file site-props.txt using path "+
					STORAGE_DEVICE+"site-props.txt\n"+noSiteProps.toString());
			throw new NoSitePropsException("Unable to read file site-props.txt");
		}
		catch(IOException badFileInputStream){
			Logger.log(Logger.TRAP, "Unable to open input stream to file site-props.txt "+
					"using path "+STORAGE_DEVICE+"site-props.txt\n"+badFileInputStream.toString());
			throw new NoSitePropsException("Unable to open input stream to site-props.txt");
		}
	}
	
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
		String stops = st.nextToken();
		String tourDetails = "Dist: " + distance + " ft / " + stops + " stops";
		this.m_TourList.put(tourType, tourDetails);
	}
}