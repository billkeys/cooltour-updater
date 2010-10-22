package geoplicity.cooltour.ui;

import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;

import geoplicity.cooltour.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The initial Activity (UI screen)
 *
 */
public class MainUI extends Activity {	//TODO not finished
	
	private Button beginButton;	//The "begin tour" button

	
	/**
	 * Called when the activity is first created.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);	//use the main.xml layout
        
        loadProperties();
        beginButton = (Button)findViewById(R.id.begin_button);        
        beginButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Logger.log(Logger.INPUT,"begin button clicked");
				handleBeginTour();
			}
        }
        );
    }
    
    /**
     * The "Begin Tour" button was clicked, start the tour
     */
    private void handleBeginTour(){
    	Logger.log(Logger.TRACE,"sending intent " + Constants.INTENT_ACTION_BEGIN_TOUR);
    	Intent intent = new Intent(Constants.INTENT_ACTION_BEGIN_TOUR);
    	startActivity(intent);
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
}