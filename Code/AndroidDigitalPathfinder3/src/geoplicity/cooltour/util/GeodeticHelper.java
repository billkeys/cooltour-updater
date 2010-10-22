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
package geoplicity.cooltour.util;

import geoplicity.cooltour.map.WayPt;



    public class GeodeticHelper
    {
         /**
         * Average walking speed in meters / min assuming 6.5 km/h.
         * See http://en.wikipedia.org/wiki/Walking
         */
        public final double WALK_SPEED = 5 * 1000.0 / 60;

            /** Square root of 3 */
            private final static double SQRT3 = 1.732050807568877294;

        /// <summary>
        /// Square root of 2 */
        /// </summary>
        private final static double SQRT2 = 1.1414213562;

            /** Mean geodetic radius in meters */
            private final static double MEAN_RADIUS = 6371.0;

        /// <summary>
        /// "Offshoot" from a point
        /// </summary>
        private final static int OFFSHOOT = 25;

        /// <summary>
        /// Default affine transform parameters
        /// </summary>
        private static  double m_A = -9.304113802413918E-6;
        private static  double m_B = 0.0;
        private static  double m_C = 73.93858977316418;
        private static  double m_D = 0.0;
        private static  double m_E = -6.507936507939126E-6;
        private static  double m_F = 41.7282873015873;

        public static double getA()
        {
            return m_A; 
        }
        
        public static void setA(double value)
        {
            m_A = value; 
        }
        
        public static double getB()
        {
            return m_B; 
        }
        
        public static void setB(double value)
        {
            m_B = value; 
        }

        public static double getC()
        {
            return m_C; 
        }
        
        public static void setC(double value)
        {
            m_C = value; 
        }

        public static double getD()
        {
            return m_D; 
        }
        
        public static void setD(double value)
        {
            m_D = value; 
        }

        public static double getE()
        {
            return m_E; 
        }
        
        public static void setE(double value)
        {
            m_E = value; 
        }

        public static double getF()
        {
            return m_F; 
        }
        
        public static void setF(double value)
        {
            m_F = value; 
        }

        public static double GetLon(int x)
        {
            return x * m_A + m_C;
        }

        public static double GetLat(int y)
        {
            return y * m_E + m_F;
        }


        /// <summary>
        /// Gets the distance in meters between two points.
        /// </summary>
        /// <param name="p1"></param>
        /// <param name="p2"></param>
        /// <returns></returns>
        public static double getDistance(WayPt p1, WayPt p2) {
            return haversine(p1.getLon(),p1.getLat(),p2.getLon(),p2.getLat());
        }

            /**
             * Gets the havesine distance in meters.
             * @param lat2 Secondary latitude
             * @param lon2 Secondary longitude
             * @param lat1 Primary latitude
             * @param lon1 primary longitude
             * @return Distance in meters.
             */
            private static double haversine(double lon1,double lat1,double lon2, double lat2) {
                    lat1 *= Math.PI / 180.0;
                    lon1 *= Math.PI / 180.0;

                    lat2 *= Math.PI / 180.0;
                    lon2 *= Math.PI / 180.0;

                    double dlat = lat2 - lat1;
                    double dlon = lon2 - lon1;

                    double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
                                            Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon/2) * Math.sin(dlon/2);

                    double c = 2.0 * atan2(Math.sqrt(a),Math.sqrt(1-a));

                    double d = MEAN_RADIUS * c * 1000.0;

                    return d;
            }

            /**
             * Gets the arctan
             * @param x Value
             * @return Radians
             */
            private static double atan(double x)
            {
                    Boolean signChange=false;
                    Boolean invert=false;
                    int sp=0;
                    double x2, a;
                    // check up the sign change
                    if(x < 0.0)
                    {
                            x=-x;
                            signChange=true;
                    }
                    // check up the invertation
                    if(x>1.0)
                    {
                            x=1/x;
                            invert=true;
                    }
                    // process shrinking the domain until x<PI/12
                    while(x>Math.PI/12)
                    {
                            sp++;
                            a=x+SQRT3;
                            a=1/a;
                            x=x*SQRT3;
                            x=x-1;
                            x=x*a;
                    }
                    // calculation core
                    x2=x*x;
                    a=x2+1.4087812;
                    a=0.55913709/a;
                    a=a+0.60310579;
                    a=a-(x2*0.05160454);
                    a=a*x;
                    // process until sp=0
                    while(sp>0)
                    {
                            a=a+Math.PI/6;
                            sp--;
                    }
                    // invertation took place
                    if(invert) a=Math.PI/2-a;
                    // sign change took place
                    if(signChange) a=-a;
                    //
                    return a;
            }

            /**
             * Gets the arctan
             * @param y
             * @param x
             * @return Radians
             */
            protected static double atan2(double y, double x)
            {
                    // if x=y=0
                    if(y==0.0 && x==0.0)
                            return 0.0;
                    // if x>0 atan(y/x)
                    if(x>0.0)
                            return atan(y/x);
                    // if x<0 sign(y)*(pi - atan(|y/x|))
                    if(x<0.0)
                    {
                            if(y<0.0)
                                    return -(Math.PI-atan(y/x));
                            else
                                    return Math.PI-atan(-y/x);
                    }
                    // if x=0 y!=0 sign(y)*pi/2
                    if(y<0.0)
                            return -Math.PI/2.0;
                    else
                            return Math.PI/2.0;
            }

        /// <summary>
        /// Gets the bearing from p1 to p2
        /// </summary>
        /// <param name="p1"></param>
        /// <param name="p2"></param>
        /// <returns></returns>
        public static double getBearing(WayPt p1, WayPt p2)
        {
            double lat1 = p1.getLat() * Math.PI / 180.0;
            double lon1 = p1.getLon() * Math.PI / 180.0;

            double lat2 = p2.getLat() * Math.PI / 180.0;
            double lon2 = p2.getLon() * Math.PI / 180.0;

            return getBearing(lon1, lat1, lon2, lat2);
        }

        /// <summary>
        /// Gets the "true north" bearing between two points in radians. 
        /// </summary>
        /// <param name="p1"></param>
        /// <param name="p2"></param>
        /// <returns></returns>
            public static double getBearing(double lon1, double lat1, double lon2, double lat2) {
                    double dlon = lon2 - lon1;

                    double y = Math.sin(dlon) * Math.cos(lat2);

                    double x = Math.cos(lat1) * Math.sin(lat2) -
                                       Math.sin(lat1) * Math.cos(lat2)* Math.cos(dlon);

                    double thetaRad = atan2(y, x);

            return thetaRad;
        }

        /// <summary>
        /// Compute (lon, lat) -> (x,y)
        /// </summary>
        /// <param name="p">Primary point with (x,y) and (lon,lat) known</param>
        /// <param name="q">Query point with (lon,lat) known and (x,y) unknown</param>
        /// <param name="pixelsPerMeter">Pixels per meter</param>
        /// <returns>Query with x,y updated</returns>
        public static WayPt Project(WayPt p, WayPt q, double pixelsPerMeter) {
            // Convert to pixels the distance to the query 
            double d = GeodeticHelper.getDistance(p, q);

            double u = pixelsPerMeter * d;

            // Get the angle of attack between primary and query points
            double theta = GeodeticHelper.getBearing(p, q);

            // Estimate the x, y of the target using the distance (in pixels)
            // and the attack angle
            q.setX((int) (p.getX() - u * Math.sin(theta) + 0.5));

            q.setY((int) (p.getY() - u * Math.cos(theta) + 0.5));

            return q;
        }

        /// <summary>
        /// Compute (x, y) -> (lon, lat)
        /// </summary>
        /// <param name="p0"></param>
        /// <param name="p1"></param>
        /// <param name="p3"></param>
        /// <returns></returns>
        public static WayPt projectReverse(WayPt p0, WayPt p1, WayPt q)
        {
            double x0 = p0.getX();
            double y0 = p0.getY();

            double x1 = p1.getX();
            double y1 = p1.getY();

            double dx = x0 - x1;
            double dy = y0 - y1;

            double lon0 = p0.getLon();
            double lat0 = p0.getLat();

            double lon1 = p1.getLon();
            double lat1 = p1.getLat();

            double dlon = lon0 - lon1;
            double dlat = lat0 - lat1;

            double degPerPixLon = dlon / dx;
            double degPerPixLat = dlat / dy;

            double dxq = p0.getX() - q.getX();
            double dyq = p0.getY() - q.getY();

            double lonq = lon0 - degPerPixLon * dxq;
            double latq = lat0 - degPerPixLat * dyq;

            WayPt result = new WayPt(lonq, latq, q.getX(), q.getY());

            return result;
        }

        /// <summary>
        /// Converts meters to pixels at a given point.
        /// </summary>
        /// <param name="meters"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public static int metersToPixels(double meters, int x, int y)
        {
            double lon0 = GetLon(x);
            double lat0 = GetLat(y);

            double lon1 = GetLon(x + OFFSHOOT);
            double lat1 = GetLat(y + OFFSHOOT);

            double dist = haversine(lon0, lat0, lon1, lat1);

            int pixels = (int) (SQRT2 * OFFSHOOT / dist * meters + 0.5);

            return pixels;
        }
    }
//
 
    
    
    
    