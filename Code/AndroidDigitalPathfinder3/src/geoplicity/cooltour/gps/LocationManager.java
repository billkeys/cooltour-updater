/*
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
 */

package geoplicity.cooltour.gps;

import java.util.ArrayList;
import java.util.List;

import org.geoplicity.cakewalk.api.Location;
import org.geoplicity.cakewalk.api.LocationDevice;
import org.geoplicity.cakewalk.api.LocationListener;

import geoplicity.cooltour.map.WayPt;
import geoplicity.cooltour.util.GeodeticHelper;
import geoplicity.cooltour.way.Landmark;
import geoplicity.cooltour.way.Watch;

public class LocationManager implements LocationListener {
	private double m_Lat = 0;

	private double m_Lon = 0;

	private TriMesh m_TriMesh = null;

	private Triangle m_CurTriangle = null;

	private WayPt m_CurPos = null;

	private double m_LonOld = 0.0;

	private double m_LatOld = 0.0;

	private double m_Bearing = 0.0;

	private int m_Count = 0;

	private List<ILandmarkListener> m_Listeners = new ArrayList<ILandmarkListener>();
	private List<Landmark> m_Landmarks = new ArrayList<Landmark>();

	// / <summary>
	// / Filter levels interpreted as seconds.
	// / </summary>
	@SuppressWarnings("unused")
	private final int FILTER_NONE = 1;
	private final static int FILTER_LO = 3;
	@SuppressWarnings("unused")
	private final int FILTER_MED = 15;
	@SuppressWarnings("unused")
	private final int FILTER_HI = 30;

	// / <summary>
	// / Bearing filter
	// / </summary>
	private static int m_BearFilter = FILTER_LO;

	// / <summary>
	// / Bearing "filter" buffer
	// / </summary>
	//private Location[] m_LocBuf = new Location[m_BearFilter];

	// / <summary>
	// / Longitude, latitude filter lag in seconds
	// / </summary>
	private static int m_LocFilter = FILTER_LO;

	// / <summary>
	// / Current bearing from "true" north
	// / </summary>

	public double getBearing() {
		return m_Bearing;
	}

	// / <summary>
	// / EMA filter the larger the value the longer the lag
	// / </summary>

	public static int getLocationFilter() {
		return m_LocFilter;
	}

	public static void setLocationFilter(int value) {
		m_LocFilter = value;
	}

	private static LocationManager m_LocationManager = null;

	// / <summary>
	// / Instance property to fetch a location manager
	// / </summary>
	/*
	 * public static LocationManager getInstance() { if (m_LocationManager ==
	 * null) m_LocationManager = new LocationManager();
	 * 
	 * return m_LocationManager;
	 * 
	 * }
	 */
	// / <summary>
	// / Constructor
	// / </summary>
	private LocationManager() {
		InitMesh();

		InitDevice();

	//	for (int i = 0; i < m_LocBuf.length; i++)
	//		m_LocBuf[i] = new Location();

	}

	// / <summary>
	// / Initializes the triangle mesh and loads its ref points.
	// / </summary>
	private void InitMesh() {
		m_TriMesh = new TriMesh();

		m_TriMesh.LoadRefPoints();

		m_TriMesh.LoadTris();
	}

	// / <summary>
	// / Initializes and starts the devices.
	// / </summary>
	private void InitDevice() {
		try {
		LocationDevice device = LocationDevice.getInstance();

		device.setLocationListener(this);

		device.start();
		}
		catch(Exception e) {
			// TODO: put in logger here
			
		}
	}

	// / <summary>
	// / Get an instance of the location manager.
	// / </summary>
	// / <returns></returns>
	public static LocationManager GetInstance() {
		if (m_LocationManager == null)
			m_LocationManager = new LocationManager();

		return m_LocationManager;
	}

	// / <summary>
	// / Process location updates.
	// / </summary>
	// / <param name="location"></param>

	
	public void updated(Location location) {
		// TODO Auto-generated method stub
		Filter(location);

		synchronized (this) {
			m_CurPos = GetPosition(m_Lon, m_Lat);
		}

		HandleWatches();
	}
	

	// / <summary>
	// / Handles watches linked to landmarks. Assumes locations and
	// / radii are in pixels.
	// / </summary>
	private void HandleWatches() {
		if (m_Listeners.size() == 0 || m_Landmarks.size() == 0
				|| m_CurPos == null)
			return;

		for (Landmark landmark : m_Landmarks) {
			if (landmark.getFired())
				continue;

			for (Watch watch : landmark.getWatches()) {
				if (!watch.getEnabled())
					continue;

				int radius = watch.getRadius();

				double dx = m_CurPos.getX() - watch.getX();
				double dy = m_CurPos.getY() - watch.getY();

				double dist = Math.sqrt(dx * dx + dy * dy);

				if (dist > radius)
					continue;

				for (ILandmarkListener listener : m_Listeners)
					listener.Reached(watch);

				landmark.setFired(true);

				break;
			}
		}
	}

	// / <summary>
	// / Adds a landmark listener.
	// / </summary>
	// / <param name="listener"></param>
	public void AddListener(ILandmarkListener listener) {
		m_Listeners.add(listener);
	}

	// / <summary>
	// / Adds a landmark to listen for.
	// / </summary>
	// / <param name="landmark"></param>
	public void AddLandmark(Landmark landmark) {
		if (m_Landmarks.contains(landmark))
			return;

		m_Landmarks.add(landmark);
	}

	// / <summary>
	// / Adds a bunch of landmarks to listen for.
	// / </summary>
	// / <param name="landmarks"></param>
	public void AddLandmarks(List<Landmark> landmarks) {
		for (Landmark landmark : landmarks) {
			AddLandmark(landmark);
		}
	}

	// / <summary>
	// / Clears all the landmarks.
	// / </summary>
	public void Clear() {
		m_Landmarks.clear();
	}

	// / <summary>
	// / Get the current position.
	// / </summary>
	// / <returns></returns>
	public WayPt GetCurPosition() {
		synchronized (this) {
	//		if (LocationDevice.getSignal() != LocationDevice.STATE.SatelliteFixed)
	//		return null;

			return m_CurPos;
		}
	}

	// / <summary>
	// / Translate lon, lat to XY pixels.
	// / </summary>
	// / <param name="lon"></param>
	// / <param name="lat"></param>
	// / <returns></returns>
	public WayPt GetPosition(double lon, double lat) {
		// Create the query point
		WayPt query = new WayPt(lon, lat, -1, -1);

		// Locate the triangle
		WayPt start = null;

		if (m_CurTriangle != null)
			start = m_TriMesh.GetPoint(m_CurTriangle.GetVertices()[0]);

		m_CurTriangle = m_TriMesh.Locate(query, start);

		if (m_CurTriangle == null)
			return null;

		// Use triangle to translate query to XY
		String[] vertices = m_CurTriangle.GetVertices();

		// Vertex sequence assumes index 0 is the primary optimal point
		// computed by the map projection tool.
		WayPt p0 = m_TriMesh.GetPoint(vertices[0]);

		WayPt loc = GeodeticHelper.Project(p0, query, m_CurTriangle
				.getPixelsPerMeter());

		return loc;
	}

	// / <summary>
	// / Gets the triangle for the current position.
	// / </summary>
	// / <returns></returns>
	public Triangle GetTriangle() {
		return m_CurTriangle;
	}

	// / <summary>
	// / Applies filters to reduce signal noise.
	// / </summary>
	// / <param name="location"></param>
	private void Filter(Location location) {
		m_Count++;

		if (m_Count == 1) {
			m_Lon = m_LonOld = location.getLongitude();

			m_Lat = m_LatOld = location.getLatitude();

			return;
		}

		double k = 2.0 / (1 + m_LocFilter);

		m_Lon = location.getLongitude() * k + m_Lon * (1.0 - k);

		m_Lat = location.getLatitude() * k + m_Lat * (1.0 - k);

		if ((m_Count % m_BearFilter) == 0) {
			m_Bearing = GeodeticHelper.getBearing(m_LonOld, m_LatOld, m_Lon,
					m_Lat);

			m_LonOld = m_Lon;
			m_LatOld = m_Lat;
		}

	}
}
