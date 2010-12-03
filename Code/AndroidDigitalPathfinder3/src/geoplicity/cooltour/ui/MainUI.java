/**
 * @author - Abuelsaad and Goldsmith
 * 
 */

package geoplicity.cooltour.ui;

import geoplicity.cooltour.sites.NoSitePropsException;
import geoplicity.cooltour.sites.NoWayPropsException;
import geoplicity.cooltour.sites.SiteListCreator;
import geoplicity.cooltour.sites.TourListCreator;
import geoplicity.cooltour.util.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainUI extends Activity {	
	
	//Menu item for launching "Updater" Activity
	private static final int UPDATE_ID = Menu.FIRST; 
	//Menu item for launching "About Geoplicity" Activity
	private static final int ABOUT_ID = Menu.FIRST + 1;     
	
	//Button for "Begin Tour"
	private Button m_BeginButton;	
	//Button for "Quit Tour"
	private Button m_QuitButton;    
	
	//Spinner for Site Selection
	private Spinner m_SiteSelectionList;
	//Adapter for connecting to Site Selection Spinner
	private ArrayAdapter<CharSequence> m_SiteSelectionAdapter;
	
	//Spinner for Tour Selection
	private Spinner m_TourSelectionList;
	//Adapter for connecting to Tour Selection Spinner
	private ArrayAdapter<CharSequence> m_TourSelectionAdapter;
	//HashMap containing key and value pairs for list of tours and tour details
	private HashMap<String, String> m_TourList;
	
	//CheckBox for Start Tour at Beginning
	private CheckBox m_StartAtBegin;
	
	//Current Root Directory
	private String m_RootDir;
	//Current Site Selection
	private String m_SelectedSite;
	//Current Tour Selection
	private String m_TourSelection;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the Layout View to main.xml
        setContentView(R.layout.main);	
        //Bind to elements in the Layout
        bindToLayout();
        loadApplicationProperties();
    }
    
    
    /**
     * Method for handling button clicks on the "Quit Tour" Button
     */
    private OnClickListener m_QuitTourListener = new OnClickListener() {
        public void onClick(View v) {
          System.exit(0);
        }
    };
    
    /**
     * Method for handling button clicks on the "Begin Tour" Button
     * 
     * Currently has the responsibility of invoking the next Activity 
     *  (MapActivity)
     */
    private OnClickListener m_BeginTourListener = new OnClickListener() {
    	public void onClick(View v) {
    		Logger.log(Logger.INPUT,"begin button clicked");
    		Logger.log(Logger.TRACE,"sending intent " + Constants.INTENT_ACTION_BEGIN_TOUR);
    		loadSiteSpecificProperties();
        	Intent intent = new Intent(Constants.INTENT_ACTION_BEGIN_TOUR);
        	startActivity(intent);
    	}
    };
    
    /**
     * Binds the following local reference variables to the XML Layout
     *       Spinner m_SiteSelectionList --> R.id.SiteSelect
     *       Spinner m_TourSelectionList --> R.id.TourSelect
     *       Button m_BeginButton        --> R.id.begin_button
     *       Button m_QuitButton         --> R.id.quit_button
     *       
     * Creates the following local reference variables for updating text on 
     * the display.
     *       ArrayAdapter m_SiteSelectionAdapter --> Used to update Site List
     *       ArrayAdapter m_TourSelectionAdapter --> Used to update Tour List
     */
    private void bindToLayout(){
    	//Configure Site Select Spinner
    	m_SiteSelectionList = (Spinner) findViewById(R.id.SiteSelect);
        m_SiteSelectionAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        m_SiteSelectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_SiteSelectionList.setAdapter(m_SiteSelectionAdapter);
        m_SiteSelectionList.setOnItemSelectedListener(new SiteListSelectedListener());
        
        //Configure Tour Select Spinner
        m_TourSelectionList = (Spinner) findViewById(R.id.TourSelect);
        m_TourSelectionAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        m_TourSelectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_TourSelectionList.setAdapter(m_TourSelectionAdapter);
        m_TourSelectionList.setOnItemSelectedListener(new TourListSelectedListener());
        
        //Begin Tour Button
        m_BeginButton = (Button)findViewById(R.id.begin_button); 
        m_BeginButton.setOnClickListener(m_BeginTourListener);
        
        //Quit Tour Button
        m_QuitButton = (Button)findViewById(R.id.quit_button);
        m_QuitButton.setOnClickListener(m_QuitTourListener);
        
        //Start Tour Beginning Check Box
        m_StartAtBegin = (CheckBox)findViewById(R.id.StartTourBegin);
    }
    
    /**
     * Used for handling the Menu Button
     * Displays two Options:
     *    1. Launching the Updater Activity
     *    2. Launching an Activity describing the Geoplicity project
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, UPDATE_ID, 0, R.string.menu_updater);
        menu.add(0, ABOUT_ID, 0, R.string.menu_about);
        return true;
    }
    
    /**
     * Used for displaying the currently downloaded Site Plugins
     * Retrieves available site plugins (as a List<String>)
     *  from geoplicity.cooltour.sites.SiteListCreator()
     * 
     */
	private void displaySiteSpecificProperties(){
    	SiteListCreator siteCreator = new SiteListCreator();
    	try {
    		List<String> siteList = siteCreator.getSiteChoices();
    		Iterator<String> siteListIterator = siteList.iterator();
    		while (siteListIterator.hasNext()){
    			m_SiteSelectionAdapter.add(siteListIterator.next());
    		}
    	}
    	catch (NoSitePropsException loadDefault){
    		//TODO:
    		//Just display single site Staatsburg.
    	}
    }
    
    /**
     * Load the application properties (app-props.txt)
     * 
     *      app.root.dir        --> /Geoplicity
     *      
     * Invokes method (displaySiteSpecificProperties) to display available
     *  site choices to user.
     */
    private void loadApplicationProperties(){   
    	Property.loadProperties(Constants.DEFAULT_APP_PROPERTIES);
    	m_RootDir = Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR);   
        displaySiteSpecificProperties();
    }
    
    /**
     * Sets site specific properties
     * 
     *      current.site          --> user selected site
     *      tour.sequence.enabled --> sets true if tour should start from beginning
     * 
     * Loads the site specific properties 
     *                               <Typical Location>
     *    - GEO Properties      --> /Geoplicity/site_selected/geo-props.txt
     *    - MAP Properties      --> /Geoplicity/site_selected/configs/map-props.txt
     *    - WAY Properties      --> /Geoplicity/site_selected/configs/way-props.txt
     *    - TRI Properties      --> /Geoplicity/site_selected/configs/tri-props.txt
     *    - GPS Properties      --> /Geoplicity/site_selected/configs/nmea.txt
     */
    private void loadSiteSpecificProperties() {
    	if (m_StartAtBegin.isChecked())
    		Property.setProperty("tour.sequence.enabled", "true");
    	else
    		Property.setProperty("tour.sequence.enabled", "false");
    	Property.setProperty("tour.current", m_TourSelection);
    	Property.loadProperties(m_RootDir + "/" + m_SelectedSite + Constants.DEFAULT_GEO_PROPERTIES);
    	Log.v("LOAD SITE PROPERTIES", m_RootDir + "/" + m_SelectedSite + Constants.DEFAULT_GEO_PROPERTIES);
    	Logger.init();	
    	Logger.log(Logger.DEBUG, "Site root directory is: "+m_RootDir + "/" + m_SelectedSite);
    	Property.setProperty("current.site", m_SelectedSite);
    	Property.loadProperties(m_RootDir + "/" + m_SelectedSite + Property.getProperty("map.props"));
    	Property.loadProperties(m_RootDir + "/" + m_SelectedSite + Property.getProperty("way.props"));
    	Property.loadProperties(m_RootDir + "/" + m_SelectedSite + Property.getProperty("tri.props"));
    	Property.loadProperties(m_RootDir + "/" + m_SelectedSite + Property.getProperty("gps.sim.file"));
    }
    
    
    /**
     * Button click handler used for grabbing selected tour length from user.
     * 
     * Displays tour length statistics in the following format
     * 
     *        The tour selected is <name of tour>
     *        Dist: <distance> mi / <number of stops> stops
     *
     */
    public class TourListSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
        	m_TourSelection = parent.getItemAtPosition(pos).toString();
        	if(m_TourSelection != null)
        	{
        		Toast.makeText(parent.getContext(), "The tour selected is " +
        			m_TourSelection+"\n" + m_TourList.get(m_TourSelection), Toast.LENGTH_LONG).show();
        	}
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
    
    /**
     * Button click handler used for grabbing selected tour from user.
     * 
     * Modifies class attribute (m_SelectedSite) to contain the tour selected
     *  by the user.
     *
     */
    public class SiteListSelectedListener implements OnItemSelectedListener {
    	
    	public void onItemSelected(AdapterView<?> siteListSpinner,
            View view, int pos, long id){
    		m_TourSelectionAdapter.clear();
    		m_SelectedSite = siteListSpinner.getItemAtPosition(pos).toString();
    		TourListCreator tourCreator = new TourListCreator(m_SelectedSite);
    		try {
    			m_TourList = tourCreator.getTourChoices();
    			Set<String> tourListKeys = m_TourList.keySet();
    			Iterator<String> tourListIterator = tourListKeys.iterator();
    			while(tourListIterator.hasNext()) {
    				m_TourSelectionAdapter.add(tourListIterator.next());
    			}
        	}
        	catch (NoWayPropsException loadDefault){
        		//TODO:
        		//Just display single site Staatsburg.
        	}
    		
    		
    	}
    	
    	public void onNothingSelected(AdapterView parent){}
    }
}