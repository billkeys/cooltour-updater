package geoplicity.cooltour.util;

/**
 * This class defines some constants used throughout the application.
 *
 */
public class Constants {
	//tag used for logging
	public static final String LOG_TAG = "CoolTour";
	public static final String PROPERTY_APP_ROOT_DIR = "app.root.dir";
	//intent actions
	public static final String INTENT_ACTION_MAIN_UI = "geoplicity.cooltour.MAIN_UI";
	public static final String INTENT_ACTION_BEGIN_TOUR = "geoplicity.cooltour.BEGIN_TOUR";
	public static final String INTENT_ACTION_UPDATE_LOCATION = "geoplicity.cooltour.UPDATE_LOCATION";
	public static final String INTENT_ACTION_LAUNCH_SITE_UPDATER = "geoplicity.cooltour.LAUNCH_SITE_UPDATER";
	public static final String INTENT_ACTION_LAUNCH_SITE_UPDATE = "geoplicity.cooltour.LAUNCH_SITE_UPDATE";
	
	//intent extras
	public static final String INTENT_EXTRA_SITE_UPDATE = "geoplicity.cooltour.SITE_UPDATE";
	public static final String INTENT_EXTRA_SITE_RUNNING_UPDATE = "geoplicity.cooltour.SITE_RUNNING_UPDATE";
	
	//default property files
	public static final String DEFAULT_APP_PROPERTIES = "app-props.txt";
	public static final String DEFAULT_SITE_PROPERTIES = "site-props.txt";
	public static final String DEFAULT_GEO_PROPERTIES = "/geo-props.txt";
	public static final String DEFAULT_MAP_PROPERTIES = "/map.props";
	
	public static final String DEFAULT_WAY_PROPERTIES = "stway-5-props.txt";
	public static final String DEFAULT_TRI_PROPERTIES = "sttri-4-props.txt";
	
	//testing property
	public static final int TEST_INTERVAL = 120;	//how many seconds the test case will run, 0 means indefinite
	public static final String TEST_FILE = "/sdcard/test/log-2010-5-28.txt";
	
	//Updater Constats
	public static final String SDCARD_ROOT = "/sdcard";
	public static final String UPDATE_SERVER = "http://brendondrew.com/geoplicity/sites/";
	public static final String UPDATE_FILE_EXT = ".txt";
	public static final String UPDATE_SITES_FILE = "sites"+UPDATE_FILE_EXT;
	public static final String UPDATE_TEMP_DIR = ".tmp/";
}
