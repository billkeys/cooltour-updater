package geoplicity.cooltour.gps;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.geoplicity.mobile.util.Property;

import geoplicity.cooltour.map.WayPt;
import geoplicity.cooltour.util.GeodeticHelper;


    /// <summary>
    /// This class implements a Deluanay triangle mesh which covers the world. Given an
    /// arbitrary query, the mesh can locate the enclosing triangle, if it exists, using
    /// the "jump-and-walk" algorithm.
    /// </summary>
    public class TriMesh
    {

        private final double SAMPLE_FRACTION = 0.20;

        private Hashtable<String, WayPt> m_LabelPtMap = new Hashtable<String, WayPt>();

        private List<Triangle> m_Triangles = new ArrayList<Triangle>();

        private List<WayPt> m_VisitedPts = new ArrayList<WayPt>();

        private List<Triangle> m_VisitedTris = new ArrayList<Triangle>();

        /// <summary>
        /// Get a point from a label.
        /// </summary>
        /// <param name="label"></param>
        /// <returns></returns>
        public WayPt GetPoint(String label)
        {
            return (WayPt) m_LabelPtMap.get(label) ;
        }

        /// <summary>
        /// Load the reference points of the triangle.
        /// </summary>
        /// <param name="wayFile"></param>
        /// <param name="triFile"></param>
        public void LoadRefPoints()
        {
            // Load only the reference point networks
        	//String mapNets = System.getProperty("map.nets");
           // String[] nets = mapNets.split(" ", 0);
        	String[] nets = Property.getProperty("map.nets").split(" ");

            for (int n = 0; n < nets.length; n++)
            {
                // Get the references or ground control points
                String key = "map.net." + nets[n];

                String value = Property.getProperty(key);

                if (value == null)
                    continue;

                String[] nodes = value.split(" ", 0);

                if (!nodes[0].equals("refs"))
                {
                    continue;
                }

                // Recycle the property's memory
                Property.takeProperty(key);
               // System.getProperties().remove(key);
                

                // Load the triangle labelPtMap
                for (int i = 1; i < nodes.length; i++)
                {
                    String ky = "map.net.ref." + nets[n] + "." + nodes[i];
                    String[] vals = Property.takeProperty(ky).split(" ", 0);
                   // System.getProperties().remove(ky);

                    int x = Integer.parseInt(vals[0]);
                    int y = Integer.parseInt(vals[1]);
                    double lon = Double.parseDouble(vals[2]);
                    double lat = Double.parseDouble(vals[3]);

                    WayPt pt = new WayPt(nodes[i], lon, lat, x, y);

                    m_LabelPtMap.put(nodes[i], pt);
                }
            }

        }

        /// <summary>
        /// Load the optimal triangles.
        /// </summary>
        /// <param name="triFile"></param>
        public void LoadTris()
        {
            for (int num = 0; ; num++)
            {
                String key = "map.tri.x-y." + num;

                String value = Property.takeProperty(key);
                //System.getProperties().remove(key);
                

                if (value == null)
                {
                    break;
                }

                String[] vertices = value.split(" ", 0);

                Triangle tri = new Triangle(num, vertices);

                // Compute the pixel / meter ration which we'll need later
                WayPt p0 = GetPoint(vertices[0]);
                WayPt p1 = GetPoint(vertices[1]);

                double distMeters = GeodeticHelper.getDistance(p0, p1);

                double distPixels = Math.sqrt((p0.getX() - p1.getX()) * (p0.getX() - p1.getX()) + (p0.getY() - p1.getY()) * (p0.getY() - p1.getY()));

                double ratio = distPixels / distMeters;

                tri.setPixelsPerMeter(ratio);

                m_Triangles.add(tri);

                // Build up the navigation graph.
                AddGraph(vertices, tri);
            }
        }

        /// <summary>
        /// Adds vertices to triangle navigation graph.
        /// </summary>
        /// <param name="vertices"></param>
        /// <param name="tri"></param>
        private void AddGraph(String[] vertices, Triangle tri) {
            for (int j = 0; j < vertices.length; j++) {
                WayPt pt = (WayPt) m_LabelPtMap.get(vertices[j]);

                pt.addTriangle(tri);

                for (int k = 0; k < vertices.length; k++) {
                    if (j == k) {
                        continue;
                    }

                    WayPt neighbor =(WayPt)  m_LabelPtMap.get(vertices[k]);

                    pt.addNeighbor(neighbor.getLabel());
                }
            }
        }

        /**
         * Locates the triangle of the query point using the jump-and-walk algorithm.
         * @param query Query point
         * @param start Start point, if null, use nearest of randomly sampled points.
         * @return A triangle if found, null otherwise.
         */
        public Triangle Locate(WayPt query, WayPt start) {
            // Initialize the visited lists which will contain the vertices
            // and triangles we've examined
            this.m_VisitedPts.clear();
            this.m_VisitedTris.clear();

            WayPt nearest = null;

            // If there is no start point, then find nearest using random sampling
            if (start == null) {
                // Randomly sample some vertices
                List<WayPt> samples = SampleRandomly();

                // Get nearest to the query pt
                nearest = GetNearest(query, samples);
            }
            else
                nearest = start;

            while (true) {
                // Add this vertex to visitedPts list so we don't go in circles
                m_VisitedPts.add(nearest);

                // Get all triangles that the nearest one is part of
                List<Triangle> nearestTris = nearest.GetTriangles();

                // Check if any of these triangle contain the query point
                for (Triangle tri : nearestTris) {
                    if (m_VisitedTris.contains(tri))
                        continue;

                    if (tri.inside(query, m_LabelPtMap)) {
                        //System.out.println("Visited vertices: " + visitedPts.size() + " triangles: " + visitedTris.size());
                        return tri;
                    }

                    m_VisitedTris.add(tri);
                }

                // If we reach this point it means no triangle contains both
                // the nearest point and the query point. So get the neighbors
                // of the nearest and check those.
                List<WayPt> neighbors = ToPoints(nearest.GetNeighbors());

                // Get the neighbor nearest the query pt which moves us (hopefully)
                // toward the query
                nearest = GetNearest(query, neighbors);

                // If there are no more nearest points -- the query pt is NOT
                // within the triangulation
                if (nearest == null) {
                    break;
                }
            }

            //System.out.println("Visited vertices: " + visitedPts.size() + " triangles: " + visitedTris.size());

            return null;
        }

        /// <summary>
        /// Convert list of labels to list of points.
        /// </summary>
        /// <param name="labels"></param>
        /// <returns></returns>
        private List<WayPt> ToPoints(List<String> labels) {
            List<WayPt> pts = new ArrayList<WayPt>();

            for (int i = 0; i < labels.size(); i++) {
                pts.add((WayPt)(m_LabelPtMap.get(labels.get(i))));
            }

            return pts;
        }

        /// <summary>
        /// Get the nearest point to the query from a list of samples.
        /// </summary>
        /// <param name="query"></param>
        /// <param name="samples"></param>
        /// <returns></returns>
        private WayPt GetNearest(WayPt query, List<WayPt> samples) {
            // Find the nearest random sample
            double minDist = Double.MAX_VALUE;

            WayPt nearest = null;

            for (int i = 0; i < samples.size(); i++) {
                WayPt sample = samples.get(i);

                if (m_VisitedPts.contains(sample))
                    continue;

                double dist = GeodeticHelper.getDistance(query, sample);

                if (dist < minDist) {
                    minDist = dist;
                    nearest = sample;
                }
            }

            return nearest;
        }

        /// <summary>
        /// Randomly sample vertices.
        /// </summary>
        /// <returns></returns>
        private List<WayPt> SampleRandomly() {
            // Randomly sample vertices without replacement
            int nsamples = (int) (m_LabelPtMap.size() * SAMPLE_FRACTION);

            Random ran = new Random(0);

            List<WayPt> samples = new ArrayList<WayPt>();
            
            int sz = m_LabelPtMap.size();

            for (int i = 0; i < nsamples; i++) {
                while (true) {
                    int lottery = ran.nextInt(sz - 1) + 1;

                    WayPt sample = (WayPt) m_LabelPtMap.get(lottery + "");

                    if (samples.contains(sample)) {
                        continue;
                    }

                    samples.add(sample);

                    break;
                }
            }

            return samples;
        }
    }

