package geoplicity.cooltour.map;

import geoplicity.cooltour.gps.Triangle;

import java.util.ArrayList;
import java.util.List;

    /// <summary>
    /// This class is a container of x,y and lon,lat information.
    /// </summary>
    public class WayPt {
        /// <summary>
        /// Latitude
        /// </summary>
    	public final static String UNKNOWN_LABEL = "?";
    	
    	protected double lat;
    	
    	protected double lon;
    	
    	protected int x;
    	
    	protected int y;
    	
    	protected String label;
    	
    	protected WayPt extent;
    	
        public double getLat()
        {
        	return lat;
        }
        public void setLat(double value)
        {
        	lat = value;
        }

        /// <summary>
        /// Longitude
        /// </summary>

        public double getLon()
        {
        	return lon;
        }
        public void setLon(double value)
        {
        	lon = value;
        }

        /// <summary>
        /// X coordinate
        /// </summary>
        
        public int getX()
        {
        	return x;
        }
        public void setX(int value)
        {
        	x = value;
        }

        /// <summary>
        /// Y coordinate
        /// </summary>
       
        public int getY()
        {
        	return y;
        }
        public void setY(int value)
        {
        	y = value;
        }

        /// <summary>
        /// Point label name
        /// </summary>
       
        public String getLabel()
        {
        	return label;
        }
        public void setLabel(String value)
        {
        	label = value;
        }

        public WayPt getExtent()
        {
        	return extent;
        }
        public void setExtent(WayPt value)
        {
        	extent = value;
        }
        /// <summary>
        /// Neigboring points of this point. Since a point may be a member of
        /// one or more triangles, the number of neighbors could be as few as
        /// two and as many as n.
        /// </summary>
        private List<String> neighbors = new ArrayList<String>( );

        /// <summary>
        /// Triangles this point is part of
        /// </summary>
        private List<Triangle> triangles = new ArrayList<Triangle>( );

        /// <summary>
        /// Contructor
        /// </summary>
        /// <param name="label"></param>
        /// <param name="lon"></param>
        /// <param name="lat"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        public WayPt(String label, double lon, double lat, int x, int y) 
        {
        	this(lon,lat,x,y);
            this.label = label;
        }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="lon"></param>
        /// <param name="lat"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        public WayPt(double lon, double lat, int x, int y) {
            this.lat = lat;
            this.lon = lon;
            this.x = x;
            this.y = y;
            label = "?";
        }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="x"></param>
        /// <param name="y"></param>
        public WayPt(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public WayPt(double x, double y)
        {
            this.x = (int)x;
            this.y = (int)y;
        }

        /// <summary>
        /// Gets the neighbors
        /// </summary>
        /// <returns></returns>
        public List<String> GetNeighbors() {
            return neighbors;
        }

        /// <summary>
        /// Gets the membership triangles
        /// </summary>
        /// <returns></returns>
        public List<Triangle> GetTriangles() {
            return triangles;
        }

        /// <summary>
        /// Adds a triangle membership
        /// </summary>
        /// <param name="tri"></param>
        public void addTriangle(Triangle tri) {
            triangles.add(tri);
        }
        
        /// <summary>
        /// Adds a neighbor point
        /// </summary>
        /// <param name="neighbor"></param>
        public void addNeighbor(String neighbor) {
            if(neighbor.equals(label))
                return;

            for(String n : neighbors)
            {
                if(n.equals(neighbor))
                    return;
            }

            neighbors.add(neighbor);
        }

        /// <summary>
        /// Converts this point to a string.
        /// </summary>
        /// <returns></returns>
        
        public String toString() {
            return x+" "+y+" "+lon+" "+lat;
        }
    }
