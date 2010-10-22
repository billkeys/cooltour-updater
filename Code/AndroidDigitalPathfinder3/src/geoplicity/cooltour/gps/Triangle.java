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

import geoplicity.cooltour.map.WayPt;
import geoplicity.cooltour.util.VectorOps;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

    /// <summary>
    /// Triangle polygon defined by list of vertex string labels.
    /// </summary>
    public class Triangle
    {
        private List<String> m_Vertices = new ArrayList<String>();
        
        protected double m_PixelsPerMeter;
        
        protected int m_Num;

        /// <summary>
        /// Pixel per meter ratio for this triangle
        /// </summary>
        public double getPixelsPerMeter()
        {
        	return m_PixelsPerMeter;
        }
        
        public void setPixelsPerMeter(double value)
        {
        	m_PixelsPerMeter = value;
        }
        /// <summary>
        /// Number (or integer) label of this triangle
        /// </summary>
        public int getNum()
        {
            return m_Num;

        }
        
        public void setNum(int value)
        {
            m_Num = value;

        }

        /**
         * Constructor
         * @param num Triangle number
         * @param nodes Node labels
         */
        public Triangle(int num, String[] nodes)
        {
            this.m_Num = num;
            for (int i = 0; i < nodes.length; i++)
                m_Vertices.add(nodes[i]);
        }

        /// <summary>
        /// Determines if a point is inside the triangle using the barycentric technique.
        /// See http://www.blackpawn.com/texts/pointinpoly/default.html
        /// </summary>
        /// <param name="q">Query point</param>
        /// <param name="labelPtMap">Map from vertex label to point</param>
        /// <returns>True if the query is inside this triangle</returns>
        public Boolean inside(WayPt q, Hashtable<?, ?> labelPtMap)
        {
            //Compute vectors
            WayPt a = (WayPt) labelPtMap.get(m_Vertices.get(0));
            WayPt b = (WayPt) labelPtMap.get(m_Vertices.get(1));
            WayPt c = (WayPt) labelPtMap.get(m_Vertices.get(2));

            double[] v0 = new double[2];
            v0[0] = c.getLon() - a.getLon();
            v0[1] = c.getLat() - a.getLat();

            double[] v1 = new double[2];
            v1[0] = b.getLon() - a.getLon();
            v1[1] = b.getLat() - a.getLat();

            double[] v2 = new double[2];
            v2[0] = q.getLon() - a.getLon();
            v2[1] = q.getLat() - a.getLat();

            double dot00 = VectorOps.dotProduct(v0, v0);
            double dot01 = VectorOps.dotProduct(v0, v1);
            double dot02 = VectorOps.dotProduct(v0, v2);
            double dot11 = VectorOps.dotProduct(v1, v1);
            double dot12 = VectorOps.dotProduct(v1, v2);

            // Compute barycentric coordinates
            double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);

            double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
            double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

            // Check if point is inside triangle
            Boolean tf = (u > 0) && (v > 0) && (u + v < 1);

            return tf;
        }

        /// <summary>
        /// Gets triangle vertices (i.e., simplex) as an array.
        /// </summary>
        /// <returns>Vertices</returns>
        public String[] GetVertices()
        {
            String[] array = new String[m_Vertices.size()];

            for (int i = 0; i < m_Vertices.size(); i++)
                array[i] = m_Vertices.get(i);

            return array;
        }
    }
