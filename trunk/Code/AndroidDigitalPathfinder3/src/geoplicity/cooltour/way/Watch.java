package geoplicity.cooltour.way;

    /// <summary>
    /// This class represents a watch "trigger"
    /// </summary>
    public class Watch
    {
        
    	protected Boolean m_Enabled;
        
        protected int m_X;
        
        protected int m_Y;
        
        protected int m_Radius;
        
        protected Landmark m_Landmark;
        
        protected String m_Label;
    	
    	
    	/// <summary>
        /// Constructor
        /// </summary>
        public Watch()
        {
            m_Enabled = true;
        }
        
        /// <summary>
        /// Indicates whether this watch can fire
        /// </summary>
        public Boolean getEnabled()
        {
            return m_Enabled;
        }
        
        public void setEnabled(Boolean value)
        {
            m_Enabled = value;
        }

        /// <summary>
        /// X coordinate of watch
        /// </summary>
        public int getX()
        {
            return m_X;
        }
        
        public void setX(int value)
        {
            m_X = value;
        }

        /// <summary>
        /// Y coordinate of watch
        /// </summary>
        public int getY()
        {
            return m_Y;
        }
        
        public void setY(int value)
        {
            m_Y = value;
        }

        /// <summary>
        /// This is the radius from the central point in pixels.
        /// (In the property file it's stored as meters but we convert
        /// it to pixels.)
        /// </summary>
        public int getRadius()
        {
            return m_Radius;
        }
        
        public void setRadius(int value)
        {
            m_Radius = value;
        }

        /// <summary>
        /// Landmark to which this watch is linked
        /// </summary>
        public Landmark getLandmark()
        {
            return m_Landmark;
        }
        
        public void setLandmark(Landmark value)
        {
            m_Landmark = value;
        }

        /// <summary>
        /// Watch label
        /// </summary>
        public String getLabel()
        {
            return m_Label;
        }
        
        public void setLabel(String value)
        {
            m_Label = value;
        }
    }
