package geoplicity.cooltour.way;

import java.util.ArrayList;
import java.util.List;

    public class Landmark
    {
        
    	protected String m_Key;
    	
    	protected int m_X;
    	
    	protected int m_Y;
    	
    	protected String m_Descr;
    	
    	protected List<Watch> m_Watches;
    	
    	protected Boolean m_Fired;
    	
    	protected List<String> m_Videos;
    	
    	protected List<String> m_SlideShows;
    	
    	public Landmark()
        {
            m_Fired = false;

            m_Watches = new ArrayList<Watch>();

            m_Videos = new ArrayList<String>();

            m_SlideShows = new ArrayList<String>();
        }

        public String getKey()
        {
            return m_Key;
        }
        
        public void setKey(String value)
        {
            m_Key = value;
        }

        public int getX()
        {
            return m_X;
        }
        
        public void setX(int value)
        {
            m_X = value;
        }

        public int getY()
        {
            return m_Y;
        }
        
        public void setY(int value)
        {
            m_Y = value;
        }

        public String getDescr()
        {
            return m_Descr;
        }
        
        public void setDescr(String value)
        {
            m_Descr = value;
        }

        public List<Watch> getWatches()
        {
            return m_Watches;
        }
        
        public void setWatches(List<Watch> value)
        {
            m_Watches = value;
        }

        public Boolean getFired()
        {
            return m_Fired;
        }
        
        public void setFired(Boolean value)
        {
            m_Fired = value;
        }

        public List<String> getVideos()
        {
            return m_Videos;
        }
        
        public void setVideos(List<String> value)
        {
            m_Videos = value;
        }

        public List<String> getSlideShows()
        {
            return m_SlideShows;
        }
        
        public void setSlideShow(List<String> value)
        {
            m_SlideShows = value;
        }
    }
