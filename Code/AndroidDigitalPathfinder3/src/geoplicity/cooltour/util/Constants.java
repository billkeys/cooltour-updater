package geoplicity.cooltour.util;

/**
 * This class defines some constants used throughout the application.
 *
 */
public class Constants {
	//tag used for logging
	public static final String LOG_TAG = "CoolTour";
	
	//intent actions
	public static final String INTENT_ACTION_BEGIN_TOUR = "geoplicity.cooltour.BEGIN_TOUR";
	public static final String INTENT_ACTION_UPDATE_LOCATION = "geoplicity.cooltour.UPDATE_LOCATION";
	public static final String INTENT_ACTION_LAUNCH_SITE_UPDATER = "geoplicity.cooltour.LAUNCH_SITE_UPDATER";
	public static final String INTENT_ACTION_LAUNCH_SITE_UPDATE = "geoplicity.cooltour.LAUNCH_SITE_UPDATE";
	
	//intent extras
	public static final String INTENT_EXTRA_SITE_UPDATE = "geoplicity.cooltour.SITE_UPDATE";
	
	//default property files
	public static final String DEFAULT_APP_PROPERTIES = "app-props.txt";
	public static final String DEFAULT_GEO_PROPERTIES = "/geo-props.txt";
	public static final String DEFAULT_MAP_PROPERTIES = "/map.props";
	
	public static final String DEFAULT_WAY_PROPERTIES = "stway-5-props.txt";
	public static final String DEFAULT_TRI_PROPERTIES = "sttri-4-props.txt";
	
	//testing property
	public static final int TEST_INTERVAL = 120;	//how many seconds the test case will run, 0 means indefinite
	public static final String TEST_FILE = "/sdcard/test/log-2010-5-28.txt";
}