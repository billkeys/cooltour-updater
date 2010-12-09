/**
 * @author - Deyaa Abuelsaad and Sean Goldsmith
 * @Date   - December 8th 2010
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Main Activity for Digital Path Finder
 *  Displays site selection list from site-props.txt
 *  Displays tour selection list for each site
 *  Displays button for starting tour
 *  Displays button for quitting tour
 */
public class MainUI extends Activity {	
	
	//Menu item for launching "Updater" Activity
	private static final int UPDATE_ID = Menu.FIRST; 
	//Menu item for displaying day time screen
	private static final int DAY_TIME_ID = Menu.FIRST + 1;
	
	//Button for "Begin Tour"
	private Button mBeginButton;	
	//Button for "Quit Tour"
	private Button mQuitButton;    
	
	//Spinner for Site Selection
	private Spinner mSiteSelectionList;
	//Adapter for connecting to Site Selection Spinner
	private ArrayAdapter<CharSequence> mSiteSelectionAdapter;
	
	//Spinner for Tour Selection
	private Spinner mTourSelectionList;
	//Adapter for connecting to Tour Selection Spinner
	private ArrayAdapter<CharSequence> mTourSelectionAdapter;
	//HashMap containing key and value pairs for list of tours and tour details
	private HashMap<String, String> mTourList;
	
	//CheckBox for Start Tour at Beginning
	private CheckBox mStartAtBegin;
	
	//Current Root Directory
	private String mRootDir;
	//Current Site Selection
	private String mSelectedSite;
	//Current Tour Selection
	private String mTourSelection;
	
	
    /**
     * Entry method of Activity
     * 	Sets the display to use the layout {@link R.layout.main}
     *  Binds reference variables to XML layout using method bindToLayout()
     *  Loads the default application properties using loadApplicationProperties()
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the Layout View to main.xml
        setContentView(R.layout.main);	
        //Bind to elements in the Layout
        bindToLayout();
        loadApplicationProperties();
        Log.v(Constants.LOG_TAG, Property.dump());
    }
    
    
    /**
     * Method for handling button clicks on the "Quit Tour" Button
     */
    private OnClickListener mQuitTourListener = new OnClickListener() {
        public void onClick(View v) {
          System.exit(0);
        }
    };
    
    /**
     * Method for handling button clicks on the "Begin Tour" Button
     * 
     * Currently has the responsibility of invoking the next Activity 
     *  {@link geoplicity.cooltour.ui.MapActivity}
     */
    private OnClickListener mBeginTourListener = new OnClickListener() {
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
     *       Spinner mSiteSelectionList --> R.id.SiteSelect
     *       Spinner mTourSelectionList --> R.id.TourSelect
     *       Button mBeginButton        --> R.id.begin_button
     *       Button mQuitButton         --> R.id.quit_button
     *       CheckBox mStartAtBegin     --> R.id.StartTourBegin
     *       
     * Creates the following local reference variables for updating text on 
     * the display.
     *       ArrayAdapter mSiteSelectionAdapter --> Used to update Site List
     *       ArrayAdapter mTourSelectionAdapter --> Used to update Tour List
     */
    private void bindToLayout(){
    	//Configure Site Select Spinner
    	mSiteSelectionList = (Spinner) findViewById(R.id.SiteSelect);
        mSiteSelectionAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        mSiteSelectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSiteSelectionList.setAdapter(mSiteSelectionAdapter);
        mSiteSelectionList.setOnItemSelectedListener(new SiteListSelectedListener());
        
        //Configure Tour Select Spinner
        mTourSelectionList = (Spinner) findViewById(R.id.TourSelect);
        mTourSelectionAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        mTourSelectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTourSelectionList.setAdapter(mTourSelectionAdapter);
        mTourSelectionList.setOnItemSelectedListener(new TourListSelectedListener());
        
        //Begin Tour Button
        mBeginButton = (Button)findViewById(R.id.begin_button); 
        mBeginButton.setOnClickListener(mBeginTourListener);
        
        //Quit Tour Button
        mQuitButton = (Button)findViewById(R.id.quit_button);
        mQuitButton.setOnClickListener(mQuitTourListener);
        
        //Start Tour Beginning Check Box
        mStartAtBegin = (CheckBox)findViewById(R.id.StartTourBegin);
    }
    
    /**
     * Used for displaying the Menu Button
     * Displays two Options:
     *    1. Launching the Updater Activity
     *    2. Launching an Activity describing the Geoplicity project
     *    3. Changing the display of the screen for the day time
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, UPDATE_ID, 0, R.string.menu_updater);
        menu.add(0, DAY_TIME_ID, 0, R.string.menu_daytime);
        return true;
    }
    
    /**
     * Used for handling the Menu Button
     *  If Menu option "Launch Updater" is selected:
     *     The activity {@link Constants.INTENT_ACTION_LAUNCH_SITE_UPDATE} is started
     *  If Menu option "Change Screen Daytime" is selected:
     *     A new layout is displayed {@link R.layout.daytime}
     *     The menu item is changed to display "Restore Defaults"
     */
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	  case UPDATE_ID:
    		  Intent updater = new Intent(Constants.INTENT_ACTION_LAUNCH_SITE_UPDATER);
    		  startActivity(updater);
    		  break;
    	  case DAY_TIME_ID:
    		  if (item.getTitle().equals("Change Screen Daytime")){		  
    			  setContentView(R.layout.daytime);
    		  	  item.setTitle("Restore Default");
    		  	  bindToLayout();
    		  	  loadApplicationProperties();
    		  }
    		  else {
    			  setContentView(R.layout.main);
    			  item.setTitle(R.string.menu_daytime);
    			  bindToLayout();
    			  loadApplicationProperties();
    		  }
    	}
		return super.onOptionsItemSelected(item);
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
    			mSiteSelectionAdapter.add(siteListIterator.next());
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
     * Sets class variable {@link mRootDir} to value contained in app.root.dir
     *      
     * Invokes method (displaySiteSpecificProperties) to display available
     *  site choices to user.
     */
    private void loadApplicationProperties(){   
    	Property.loadProperties(Constants.DEFAULT_APP_PROPERTIES);
    	mRootDir = Property.getProperty(Constants.PROPERTY_APP_ROOT_DIR);   
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
    	if (mStartAtBegin.isChecked())
    		Property.setProperty("tour.sequence.enabled", "true");
    	else
    		Property.setProperty("tour.sequence.enabled", "false");
    	Property.setProperty("tour.current", mTourSelection);
    	Property.loadProperties(mRootDir + "/" + mSelectedSite + Constants.DEFAULT_GEO_PROPERTIES);
    	Log.v("LOAD SITE PROPERTIES", mRootDir + "/" + mSelectedSite + Constants.DEFAULT_GEO_PROPERTIES);
    	Logger.init();	
    	Logger.log(Logger.DEBUG, "Site root directory is: "+mRootDir + "/" + mSelectedSite);
    	Property.setProperty("current.site", mSelectedSite);
    	Property.loadProperties(mRootDir + "/" + mSelectedSite + Property.getProperty("map.props"));
    	Property.loadProperties(mRootDir + "/" + mSelectedSite + Property.getProperty("way.props"));
    	Property.loadProperties(mRootDir + "/" + mSelectedSite + Property.getProperty("tri.props"));
    	Property.loadProperties(mRootDir + "/" + mSelectedSite + Property.getProperty("gps.sim.file"));
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
        	mTourSelection = parent.getItemAtPosition(pos).toString();
        	if(mTourSelection != null)
        	{
        		Toast.makeText(parent.getContext(), "The tour selected is " +
        			mTourSelection+"\n" + mTourList.get(mTourSelection), Toast.LENGTH_LONG).show();
        	}
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
    
    /**
     * Button click handler used for grabbing selected tour from user.
     * 
     * Modifies class attribute (mSelectedSite) to contain the tour selected
     *  by the user.
     *
     */
    public class SiteListSelectedListener implements OnItemSelectedListener {
    	
    	public void onItemSelected(AdapterView<?> siteListSpinner,
            View view, int pos, long id){
    		mTourSelectionAdapter.clear();
    		mSelectedSite = siteListSpinner.getItemAtPosition(pos).toString();
    		TourListCreator tourCreator = new TourListCreator(mSelectedSite);
    		try {
    			mTourList = tourCreator.getTourChoices();
    			Set<String> tourListKeys = mTourList.keySet();
    			Iterator<String> tourListIterator = tourListKeys.iterator();
    			while(tourListIterator.hasNext()) {
    				mTourSelectionAdapter.add(tourListIterator.next());
    			}
        	}
        	catch (NoWayPropsException loadDefault){
        		//TODO:
        		//Just display single site Staatsburg.
        	}	
    	}
    	public void onNothingSelected(AdapterView parent){}
    	
    }
//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//
//    	Intent i = new Intent(Constants.INTENT_ACTION_LAUNCH_SITE_UPDATER);
//    	startActivity(i);
//		return false;
//    }
}