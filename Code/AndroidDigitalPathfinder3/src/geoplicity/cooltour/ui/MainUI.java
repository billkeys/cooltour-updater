package geoplicity.cooltour.ui;

import geoplicity.cooltour.util.Constants;

import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * The initial Activity (UI screen)
 *
 */
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
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the Layout View to main.xml
        setContentView(R.layout.main);	
        //Bind to elements in the Layout
        bindToLayout();
        
        loadProperties();
        m_SiteSelectionAdapter.add("Staatsburg");
        m_SiteSelectionAdapter.add("Olana");
        m_SiteSelectionAdapter.add("Locust Grove");
        m_SiteSelectionAdapter.add("Springside");
        
        m_TourSelectionAdapter.add("Landscape");
        m_TourSelectionAdapter.add("Full Tour");         
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
        
        //Configure Tour Select Spinner
        m_TourSelectionList = (Spinner) findViewById(R.id.TourSelect);
        m_TourSelectionAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        m_TourSelectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_TourSelectionList.setAdapter(m_TourSelectionAdapter);
        m_TourSelectionList.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
        //Begin Tour Button
        m_BeginButton = (Button)findViewById(R.id.begin_button); 
        m_BeginButton.setOnClickListener(m_BeginTourListener);
        
        //Quit Tour Button
        m_QuitButton = (Button)findViewById(R.id.quit_button);
        m_QuitButton.setOnClickListener(m_QuitTourListener);
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
     * Load the application properties
     */
    private void loadProperties(){    	
    	//Get the app properties
    	//Property.loadAppProperties();
    	Property.loadProperties(Constants.DEFAULT_APP_PROPERTIES);
    	
    	//Get the root dir property (loaded as an app property)
    	String rootDir = Property.getProperty("app.root.dir");    	

    	//Load main geo properties
    	Property.loadProperties(rootDir + Constants.DEFAULT_GEO_PROPERTIES);
    	
    	//Initialize the logger, which must be done after loading the geo property file
    	Logger.init();	
    	Logger.log(Logger.DEBUG, "root directory is " + rootDir);
    	
    	//Load the map properties
    	Property.loadProperties(rootDir + Constants.DEFAULT_MAP_PROPERTIES); 
    	
    	
    	//Load the way file which comes from the mapineer browser	TODO
    	Property.loadProperties(Constants.DEFAULT_WAY_PROPERTIES);
    	
    	
    	//Load the optimal triangle file which comes from the projection tool	TODO
    	Property.loadProperties(Constants.DEFAULT_TRI_PROPERTIES);
    	
    	  
    }
    
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
          Toast.makeText(parent.getContext(), "The tour selected is " +
              parent.getItemAtPosition(pos).toString()+"\n Dist: 1.06 mi / 10 stops", Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
}