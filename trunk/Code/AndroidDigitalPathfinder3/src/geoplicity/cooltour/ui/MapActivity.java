package geoplicity.cooltour.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.geoplicity.mobile.util.Logger;

import geoplicity.cooltour.map.MapManager;
import geoplicity.cooltour.util.Constants;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * This is the Activity (screen UI) for the map view.
 *
 */
public class MapActivity extends Activity {
	
	private MapManager mapMgr;
	
	//The following variables are for testing only
	private LocationTimerTask m_locationTask;	
	private int m_runTime = 0;	//how long the test case has been running, starting from the first location update
	
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Logger.log(Logger.TRACE, "onCreate()");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mapMgr = new MapManager(this, display.getWidth(), display.getHeight());
		this.setContentView(mapMgr);
		
		if(m_locationTask == null){		//for testing only
			m_locationTask = new LocationTimerTask();
			Timer m_Timer = new Timer();
			m_Timer.schedule(m_locationTask, 1000, 1000);		//run every second
			Logger.log(Logger.DEBUG, "timer task scheduled");
		}
	}
	
	/**
	 * Called when the activity is resumed, usually due to an intent.
	 */
	@Override
	protected void onResume(){
		super.onResume();	
		Logger.log(Logger.TRACE, "onResume for " + this.getIntent().getAction());
	}
	
	/**
	 * This class implements a task to be run once every second to update the user's location.
	 * It is currently used for testing only.
	 *
	 */
	class LocationTimerTask extends TimerTask{
		@Override
		public void run(){
//			LocationManager.getInstance().updateLocation();
			mapMgr.update();
			mapMgr.postInvalidate();		
			
			if(++m_runTime == Constants.TEST_INTERVAL){
//				LocationManager.getInstance().closeReader();
				cancel();
			}
		}
	}

}
