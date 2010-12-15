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
import java.util.List;

public class Landmark
{

	protected String m_key;

	protected int m_x;

	protected int m_y;

	protected String m_descr;

	protected List<Watch> m_watches;

	protected Boolean m_fired;

	protected List<String> m_videos;

	protected List<String> m_slideShows;

	public Landmark()
	{
		m_fired = false;

		m_watches = new ArrayList<Watch>();

		m_videos = new ArrayList<String>();

		m_slideShows = new ArrayList<String>();
	}

	public String getKey()
	{
		return m_key;
	}

	public void setKey(String value)
	{
		m_key = value;
	}

	public int getX()
	{
		return m_x;
	}

	public void setX(int value)
	{
		m_x = value;
	}

	public int getY()
	{
		return m_y;
	}

	public void setY(int value)
	{
		m_y = value;
	}

	public String getDescr()
	{
		return m_descr;
	}

	public void setDescr(String value)
	{
		m_descr = value;
	}

	public List<Watch> getWatches()
	{
		return m_watches;
	}

	public void setWatches(List<Watch> value)
	{
		m_watches = value;
	}

	public Boolean getFired()
	{
		return m_fired;
	}

	public void setFired(Boolean value)
	{
		m_fired = value;
	}

	public List<String> getVideos()
	{
		return m_videos;
	}

	public void setVideos(List<String> value)
	{
		m_videos = value;
	}

	public List<String> getSlideShows()
	{
		return m_slideShows;
	}

	public void setSlideShow(List<String> value)
	{
		m_slideShows = value;
	}
}
