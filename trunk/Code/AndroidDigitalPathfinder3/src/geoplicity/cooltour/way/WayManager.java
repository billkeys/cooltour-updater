package geoplicity.cooltour.way;

/**
 *
 * Copyright (c) Contributors, http://geoplicity.org/
 * See CONTRIBUTORS.TXT for a full list of copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Geoplicity Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE DEVELOPERS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import geoplicity.cooltour.gps.ILandmarkListener;
import geoplicity.cooltour.gps.LocationManager;
import geoplicity.cooltour.map.MapManager;
import geoplicity.cooltour.tour.Step;
import geoplicity.cooltour.tour.TourManager;
import geoplicity.cooltour.tour.TourPackage;
import geoplicity.cooltour.tour.Tourlet;
import geoplicity.cooltour.util.GeodeticHelper;
import geoplicity.cooltour.util.VectorOps;

public class WayManager implements ILandmarkListener {	//TODO unfinished

	private static WayManager m_wayManager = null;

	//singleton 

	public static WayManager getInstance()
	{
		if(m_wayManager == null)
		{
			m_wayManager = new WayManager();
		}
		return m_wayManager;

	}

	private boolean m_trucking = false;
	public boolean isM_trucking() {
		return m_trucking;
	}

	public void setM_trucking(boolean m_trucking) {
		this.m_trucking = m_trucking;
	}

	public boolean isM_triggered() {
		return m_triggered;
	}

	public void setM_triggered(boolean m_triggered) {
		this.m_triggered = m_triggered;
	}

	private boolean m_triggered = false;
	public ArrayList<Watch> m_queue = new ArrayList<Watch>() ;
	private int m_sequence = 0;
	public Hashtable<String, Landmark> landmarkTable = new Hashtable<String, Landmark>();
	public ArrayList<Landmark> landmarks = new ArrayList<Landmark>();
	public Step firstInView;
	public Step lastInView;
	//private String m_Format;

	private Step start;
	private Step end;
	//m_linePenA and m_linePenB is used 
	//draw path on the map
	private Paint m_linePenA = new Paint();
	private Paint m_linePenB = new Paint();

	private final int POLY_HT = 20;
	private final int POLY_BASE = 15;

	ArrayList<Point> m_vertices = new ArrayList<Point>();

	//m_offstep m_onstep is for way pointers
	private Paint m_offStepBrush = new Paint();
	private Paint m_onStepBrush = new Paint();
	
	private Paint m_pointerBrush = new Paint();
	private Paint m_outlinePen = new Paint();
	//m_watches to draw watches on map
	private Paint m_watchPen = new Paint();

	//m_brush is used to write names on map
	private Paint m_brush = new Paint();

	private ArrayList<Step> m_wayPointers = new ArrayList<Step>();

	private int m_cycle = 0;
	private int m_wayCount = 0;

	private Bitmap m_terminal;

	@SuppressWarnings("unused")
	private Date m_startTime;


	public Step getFirstInView() {
		return firstInView;
	}

	public void setFirstInView(Step firstInView) {
		this.firstInView = firstInView;
	}

	public Step getLastInView() {
		return lastInView;
	}

	public void setLastInView(Step lastInView) {
		this.lastInView = lastInView;
	}

	/**
	 * Constructor
	 * 
	 */

	public WayManager() {

		landmarks=new ArrayList<Landmark>();
		landmarkTable=new Hashtable<String, Landmark>();
		LocationManager.GetInstance().AddListener(this);
		loadAffine();
		loadLandmarks();

		TourManager.getInstance().start();

		//This is for debugging ONLY since in practice
		//watches are registered with a tour.
		//LocationManager.GetInstance().AddLandmark(landmarks);

		m_linePenA.setColor(Color.argb(255, 255, 62, 150));
		m_linePenA.setStrokeWidth(9.0f);
		m_linePenB.setColor(Color.BLACK);
		m_linePenB.setStrokeWidth(4.0f);
		//violet blue(138,43,226)
		m_offStepBrush.setColor(Color.argb(255, 138, 43, 226));
		//white smoke(245,245,245)
		m_onStepBrush.setColor(Color.argb(255, 245, 245, 245));
		m_outlinePen.setColor(Color.BLACK);
		//yellow green(154,205,50)
		m_watchPen.setColor(Color.argb(255, 154, 205, 50));
		m_watchPen.setStrokeWidth(2.0f);

		m_pointerBrush = m_offStepBrush;

		m_brush.setColor(Color.BLACK);
		m_brush.setStyle(Style.FILL);
		m_brush.setTextSize(9);
		m_brush.setTypeface(Typeface.DEFAULT_BOLD);

		//Initialize the way pointers

		for(int i = 0; i < 20; i++ )
		{
			m_wayPointers.add(new Step(-1, -1, -1));
		}
		//Instantiate the Terminal Symbol

		String path = Property.storageDevice + Property.getProperty("app.root.dir") + "\\Staatsburgh\\images\\terminal1.png";
		m_terminal = BitmapFactory.decodeFile(path);

	}


	public synchronized void update(){

		/**start a new thread only if the work queue is currently empty
		which we assume means the watch manager is not running*/

		if ( !m_trucking  && m_triggered )
		{
			Thread thread = new Thread(new WatchManager());
			thread.start();
		}

		m_triggered = false;

	}

	public void render(Canvas g)
	{

		renderTour(g);
		renderLandmarks(g);

		if(m_cycle == 0)
		{
			m_startTime = new Date();
		}

		m_cycle++;

	}


	public void renderTour(Canvas g){

		TourPackage pkg = TourManager.getInstance().currentTour;

		if(pkg == null)
		{
			return;
		}

		/**start the first tourlet's step
		(The steps should all be double linked)*/

		Tourlet tourlet = pkg.tourlets.get(0);
		Step step = tourlet.steps.get(0);

		firstInView = null;
		lastInView = null;

		//Draw the main segments
		m_wayCount = 0;


		while(step != null)
		{
			if(MapManager.getInstance().inView(step.getX(), step.getY()))
			{
				if (firstInView == null)
				{
					firstInView = step;
				}
				lastInView = step;

				if (step.getNext() != null)
				{
					renderTourSegment(g, step, step.getNext());
					m_wayCount++;
				}
				else
				{
					renderTourTerminal(g, step);
				}
			}
			step = step.getNext();
		}

		//Draw the Leadiing Edge

		/*if (firstInView.getPrevious() != null)
		{
			renderTourSegment(g, firstInView.getPrevious(), firstInView);
		}*/

		//Draw the trailing edge

		if(lastInView != null && lastInView.getNext() != null)
		{
			renderTourSegment(g, lastInView, lastInView.getNext());
			m_wayCount++;
		}

		/*Render the way pointers
		Since these are the screen co-ordinates, there is no
		need to transform them to the rotated tile space.*/
		if(m_wayCount != 0)
		{
			int selected = m_cycle % m_wayCount ;
			int count = 0;

			for(int i=0; i < m_wayCount; i++)
			{
				Step way = m_wayPointers.get(i);

				if( selected == count)
				{
					//white smoke(245,245,245)
					m_onStepBrush.setColor(Color.argb(255, 245, 245, 245));
					m_onStepBrush.setStyle(Style.FILL_AND_STROKE);
					m_onStepBrush.setAntiAlias(true);
					m_pointerBrush.set(m_onStepBrush);
				}
				else
				{
					//violet blue(138,43,226)
					m_offStepBrush.setColor(Color.argb(255, 138, 43, 226));
					m_offStepBrush.setStyle(Style.FILL_AND_STROKE);
					m_offStepBrush.setAntiAlias(true);
					m_pointerBrush.set(m_offStepBrush);
				}
				renderPointer(g, way.getX(), way.getY(), way.getBearing());

				count++;
			}
		}
		//m_wayPointers.clear();
	}
	/**
	 * 
	 * @param g
	 * @param terminal
	 * 
	 * The method inserts image at the end of path or we can say at destination
	 */

	private void renderTourTerminal(Canvas g, Step terminal) {


		int w = m_terminal.getWidth();
		int h = m_terminal.getHeight();

		int xCenter = terminal.getX() - w / 2 - MapManager.getInstance().getViewX();
		int yCenter = terminal.getY() - h / 2 - MapManager.getInstance().getViewY();

		//Render it

		g.drawBitmap(m_terminal, xCenter, yCenter, null);

	}
	/**
	 * 
	 * @param g
	 * @param start
	 * @param end
	 * This method draw way on the map with two colors by taking 
	 * starting and ending point of line
	 */

	private void renderTourSegment(Canvas g, Step start, Step end) {


		this.start = MapManager.getInstance().transform(start);
		this.end = MapManager.getInstance().transform(end);

		int startX = this.start.getX() - MapManager.getInstance().getViewX();
		int startY = this.start.getY()- MapManager.getInstance().getViewY();

		int endX = this.end.getX() - MapManager.getInstance().getViewX();
		int endY = this.end.getY()- MapManager.getInstance().getViewY();

		g.drawLine(startX, startY, endX, endY, m_linePenA);
		g.drawLine(startX, startY, endX, endY, m_linePenB);

		double dx = startX - endX;
		double dy = startY - endY;

		double theta = Math.atan((dx/dy));

		/*I am not sure why this works but it does! and 
		probably because the Y increases toward the bottom
		of the screen we need to flip the pointer*/

		if (dy<0)
		{
			theta = theta - Math.PI;
		}

		//Compute the bearing since the pointer renderer expects bearing

		double bearing = 2.0 * Math.PI - theta;

		/*we have to render the pointers in a second pass because 
		line segment rendering will obscure the pointers.;
		renderPointer(g, startX, startY, 2.0 * Math.PI - theta);
		m_wayPointers.add(new Step(startX, startY, bearing));*/

		m_wayPointers.add(m_wayCount, new Step(startX, startY, bearing));
	}

	/**	Rendering the pointer it have Canvas(graphics),
	 * Screen x coordinate
	 * Screen y coordinate
	 * Bearing relative to the truth north in radians
	 * */

	private void renderPointer(Canvas g, int x, int y, double bearing) {


		// This is the "normalized" triangle locator we want to generate.
		//
		//              (x0, y0)
		//              *
		//             * *
		//            *   *
		//           *     *
		// (x2, y2) ********* (x1, y1)
		//
		// It's normalized in the sense that initially it's always upright
		// with the center at (0,0). Then, given theta, we rotate it locally
		// and translate to its world cordinate, (x,y).

		// If the position has changed, update the poly
		// First reset the poly at its initial position


		m_vertices = new ArrayList<Point>();
		Point point = new Point(0, -(POLY_HT) / 2);
		m_vertices.add(point);

		point = new Point(POLY_BASE/2, POLY_HT / 2);
		m_vertices.add(point);

		point = new Point(-(POLY_BASE)/2, POLY_HT / 2);
		m_vertices.add(point);

		VectorOps.rotate(m_vertices.get(0), bearing);
		VectorOps.rotate(m_vertices.get(1), bearing);
		VectorOps.rotate(m_vertices.get(2), bearing);

		/*Translate the poly  to its screen co-ordinates with
		offset (for the shadow).*/
		m_vertices.get(0).x += x;
		m_vertices.get(0).y += y;

		m_vertices.get(1).x += x;
		m_vertices.get(1).y += y;

		m_vertices.get(2).x += x;
		m_vertices.get(2).y += y;

		//Render the poly

		/*In the android to make shapes or to draw polygons we need to take the help of path
		here moveTo is the starting point and lineTo is the ending point.*/

		Path triangle = new Path();

		triangle.moveTo(m_vertices.get(0).x, m_vertices.get(0).y);
		triangle.lineTo(m_vertices.get(1).x, m_vertices.get(1).y);
		//triangle.moveTo(m_ver.get(1).x, m_ver.get(1).y);
		triangle.lineTo(m_vertices.get(2).x, m_vertices.get(2).y);
		//triangle.moveTo(m_ver.get(2).x, m_ver.get(2).y);
		//triangle.lineTo(m_ver.get(0).x, m_ver.get(0).y);

		//Style.FILL -fills the polygon and style.STROKE gives outline 
		m_pointerBrush.setStyle(Style.FILL_AND_STROKE);


		m_outlinePen.setColor(Color.BLACK);
		m_outlinePen.setStyle(Style.STROKE);

		g.drawPath(triangle, m_pointerBrush);


		Log.v("BrushColor",m_pointerBrush.getColor()+""+m_pointerBrush.getStyle());
		g.drawPath(triangle, m_outlinePen);

		triangle.close();

	}
	
	/**
	 * This method render the landmarks that are in the view
	 * @param g
	 */
	private void renderLandmarks(Canvas g) {


		//Render the Land marks that are in view
		for (Landmark landmark : landmarks) {

			/*Render the watches in view first -- note a landmark may or
			may not be in a view*/

			int x;
			int y;
			for (Watch watch : landmark.m_watches) {

				if (!watch.getEnabled() || !MapManager.getInstance().inView(watch.getX(), watch.getY()))
				{
					continue;
				}

				//convert to screen co-ordinates and center watch

				x = watch.getX() - MapManager.getInstance().getViewX() - watch.getRadius();
				y = watch.getY() - MapManager.getInstance().getViewY() - watch.getRadius();

				m_watchPen.setStyle(Style.STROKE);
				m_watchPen.setStrokeWidth(3.0f);
				//yellow green(154,205,50)
				m_watchPen.setColor(Color.argb(255, 154, 205, 50)); 
				g.drawCircle(x, y, watch.getRadius(), m_watchPen);

				m_watchPen.setStrokeWidth(1.0f);
				m_watchPen.setColor(Color.BLACK);
				g.drawCircle(x, y, watch.getRadius(), m_watchPen);

			}

			if (!MapManager.getInstance().inView(landmark.getX(), landmark.getY()))
			{
				continue;
			}

			String title = landmark.getDescr();

			//Convert landmark coordinate to screen coordinate

			x = landmark.getX() - MapManager.getInstance().getViewX();
			y = landmark.getY() - MapManager.getInstance().getViewY();

			Paint titleMeasure = new Paint();
			double w = titleMeasure.measureText(title);

			int x1 = (int) (x - (w/2));
			g.drawText(title, x1, y, m_brush);

		}

	}
	
	/**
	 * This method load landmarks and watches for the landmarks
	 */

	private void loadLandmarks() {


		//load all the lois networks

		String[] nets = Property.getProperty("map.nets").split(" ");

		for (int i=0; i<nets.length; i++)
		{
			String key = "map.net." + nets[i];
			String value = Property.takeProperty(key);

			if (value == null)
				continue;

			String[] nodes = value.split(" ");

			if (!nodes[0].equals("lois"))
			{
				continue;
			}

			//First, load all landmarks (and get watches in second pass) 
			for (int j=1; j < nodes.length; j++)
			{
				String label = nodes[j];

				if (Character.isLetter(label.charAt(0)))
				{

					// Get the primary landmark reference
					//map.net.lois.L-2829.E 513 888 73.93381676278355 41.72250825396825 Cannavino%20Library

					String landKey = "map.net.lois." + nets[i] + "." + label;
					String[] values = Property.takeProperty(landKey).split(" ");

					Landmark landmark = new Landmark();

					landmark.setKey(landKey);
					landmark.setX(Integer.parseInt(values[0]));
					landmark.setY(Integer.parseInt(values[1]));
					landmark.setDescr(values[4].replaceAll("%20", " "));

					this.addVideos(nets[i],label,landmark);

					landmarks.add(landmark);
					landmarkTable.put(landKey,landmark);
				}
			}
			//Now load the watches linked to landmarks on the tour

			for (int j = 1 ;j < nodes.length; j++)
			{
				String label = nodes[j];

				if(Character.isDigit(label.charAt(0)))
				{
					//map.net.lois.L-2829.1 631 714 73.93284581313131 41.72327457415254 N/A 75.0
					Watch watch = new Watch();

					String akey = "map.net.lois." + nets[i] + "." + label;
					String[] values = Property.takeProperty(akey).split(" ");

					watch.setX(Integer.parseInt(values[0]));
					watch.setY(Integer.parseInt(values[1]));

					// Get radius in meters and convert to pixels
					double radius = Double.parseDouble(values[5]);
					watch.setRadius(GeodeticHelper.metersToPixels(radius, watch.getX(), watch.getY()));

					watch.setLabel(label + "->" + nets[i]);

					// Get the watches extent which is necessarily a landmark
					String extent = "map.net.lois." + nets[i] + "." + label + ".extent";
					String extentName = Property.takeProperty(extent);

					if (extentName == null)
					{
						// This condition occurs if the watch has not
						// been assigned to a landmark
						continue;
					}

					// If the landmark to which this watch's extent points
					// to is not on the tour, then we won't use this watch
					if (!TourManager.getInstance().currentTour.inPackage(extentName))
						continue;

					// Point the watch to its landmark
					watch.m_landmark = landmarkTable.get(extentName);

					// Point the landmark to its watch
					watch.m_landmark.m_watches.add(watch);

				}
			}
		}

	}
	
	/**
	 * 
	 * @param net
	 * @param label
	 * @param landmark
	 * This method load all videos of the landmark
	 */

	private void addVideos(String net, String label, Landmark landmark) {

		// Add video mobisodes, if any
		// map.net.lois.L-2829.E.video.0 library24fps.3gp
		for (int k = 0; ; k++)
		{
			String key = "map.net.mobisode." + net + "." + label + "." + k;

			String value = Property.takeProperty(key);

			if (value == null)
				break;

			String[] values = value.split(" ");

			// If this is not video mobisode, then skip it
			if (!values[0].equals("Video"))
				continue;

			landmark.m_videos.add(values[1]);
		}


	}

	private void loadAffine() {

		// Load the affine parameters
		double a = Double.parseDouble(Property.takeProperty("map.net.affine.A"));
		GeodeticHelper.setA(a);

		double b = Double.parseDouble(Property.takeProperty("map.net.affine.B"));
		GeodeticHelper.setB(b);

		double c = Double.parseDouble(Property.takeProperty("map.net.affine.C"));
		GeodeticHelper.setC(c);

		double d = Double.parseDouble(Property.takeProperty("map.net.affine.D"));
		GeodeticHelper.setD(d);

		double e = Double.parseDouble(Property.takeProperty("map.net.affine.E"));
		GeodeticHelper.setE(e);

		double f = Double.parseDouble(Property.takeProperty("map.net.affine.F"));
		GeodeticHelper.setF(f);


	}

	@Override
	public synchronized void Reached(Watch watch) {

		if(!inSequence(watch))
		{
			return;
		}
		watch.m_enabled = false;
		m_queue.add(watch);
		m_triggered = true;
		Logger.log(Logger.INPUT,"WAY MANAGER reached"+watch.m_landmark.m_key);
	}

	private boolean inSequence(Watch watch) {

		//if Tour sequencing is not enabled ,
		//the visitor can start where ever they like
		if(Property.getProperty("tour.sequence.enabled") == null)
		{
			return true;
		}

		//otherwise they must run through the start up 
		//which is typically just one or two landmarks

		TourPackage currentTour = TourManager.getInstance().currentTour;

		//Get the key of Landmark asking for attention
		//and see if it matches the one in sequence,if there is a sequence

		String landmarkKey=watch.m_landmark.m_key;
		if(m_sequence >= currentTour.landmarkSequences.size())//doubt here
		{
			m_sequence++;
			return true;
		}

		String expectedKey = currentTour.landmarkSequences.get(m_sequence);

		if (!expectedKey.equals(landmarkKey))
			return false;

		m_sequence++;

		return true;

	}

}
