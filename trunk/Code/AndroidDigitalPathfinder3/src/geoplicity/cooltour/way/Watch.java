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

/**
	This class represents a watch "trigger"
 */

public class Watch
{

	protected Boolean m_enabled;

	protected int m_x;

	protected int m_y;

	protected int m_radius;

	protected Landmark m_landmark;

	protected String m_label;


	/**
	 *  Constructor
	 */
	
	public Watch()
	{
		m_enabled = true;
	}
	/**
	 * 	Indicates whether this watch can fire
	 */
	public Boolean getEnabled()
	{
		return m_enabled;
	}

	public void setEnabled(Boolean value)
	{
		m_enabled = value;
	}
	/**
	 * X coordinate of watch
	 * @return
	 */
	public int getX()
	{
		return m_x;
	}

	public void setX(int value)
	{
		m_x = value;
	}
	
	/**
	 * Y coordinate of watch
	 * @return
	 */
	public int getY()
	{
		return m_y;
	}

	public void setY(int value)
	{
		m_y = value;
	}
	/**
	 * This is the radius from the central point in pixels.
	 * In the property file it's stored as meters but we convert
	 * it to pixels.)
	 */
	
	public int getRadius()
	{
		return m_radius;
	}

	public void setRadius(int value)
	{
		m_radius = value;
	}

	/**
	 *  Landmark to which this watch is linked
	 * @return
	 */
	public Landmark getLandmark()
	{
		return m_landmark;
	}

	public void setLandmark(Landmark value)
	{
		m_landmark = value;
	}

	/**
	 *  Watch label
	 * @return
	 */
	public String getLabel()
	{
		return m_label;
	}

	public void setLabel(String value)
	{
		m_label = value;
	}
}
