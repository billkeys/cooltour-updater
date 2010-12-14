package geoplicity.cooltour.tour;

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
import java.util.Random;

import org.geoplicity.mobile.util.Property;


public class TourManager {

	//controls the voice
	String gender;
	private String[] m_gender = {"paul","kate"};
	private static TourManager instance = null;

	String[] m_criticalKeys = {"app.root.dir","music.enabled","mc.enabled","tour.sequence.enabled","survey.enabled"};

	//Tour Packages
	public ArrayList<TourPackage> tourPackages = new ArrayList<TourPackage>();
	public TourPackage currentTour;

	private TourManager()
	{
		tourPackages = new ArrayList<TourPackage>();
		loadTourPackages();
		Random random = new Random();
		gender = m_gender[random.nextInt(m_gender.length)];

	}

	//singleton instance
	public static TourManager getInstance()
	{
		if(instance == null)
			instance = new TourManager();
		return instance;

	}
	//Tells us we are ready to start the tour formally
	public void start()
	{
		//clean up the properties database
		Property.recycleBut(m_criticalKeys);
	}
	private void loadTourPackages() {
		// TODO Auto-generated method stub
		//map.pkg.0 Standard this %20 is %20

		for(int count = 0; ;count++)
		{
			String key = "map.pkg." + count;
			String features = Property.takeProperty(key);
			String temp = Property.getProperty("tour.current");
			if(features == null)
			{
				break;
			}

			TourPackage pkg = new TourPackage(features);
			String temp1[] = features.split(" ");
			if(temp.equalsIgnoreCase(temp1[0]))
			{
				currentTour = pkg;
			}
			tourPackages.add(pkg);
		}

	}

}
