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

import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;

import android.util.Log;

public class TourPackage {
	public String name;
	public String longDescr;
	public String shortDescr;
	public ArrayList<Tourlet> tourlets= new ArrayList<Tourlet>();
	public ArrayList<String> landmarkSequences=new ArrayList<String>();
	public String landmarkLast;
	public double distance;
	public int count;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLongDescr() {
		return longDescr;
	}
	public void setLongDescr(String longDescr) {
		this.longDescr = longDescr;
	}
	public String getShortDescr() {
		return shortDescr;
	}
	public void setShortDescr(String shortDescr) {
		this.shortDescr = shortDescr;
	}
	public ArrayList<Tourlet> getTourlets() {
		return tourlets;
	}
	public void setTourlets(ArrayList<Tourlet> tourlets) {
		this.tourlets = tourlets;
	}
	public ArrayList<String> getLandmarkSequences() {
		return landmarkSequences;
	}
	public void setLandmarkSequences(ArrayList<String> landmarkSequences) {
		this.landmarkSequences = landmarkSequences;
	}
	public String getLandmarkLast() {
		return landmarkLast;
	}
	public void setLandmarkLast(String landmarkLast) {
		this.landmarkLast = landmarkLast;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public TourPackage(String features)
	{
		landmarkSequences = new ArrayList<String>();
		landmarkLast = "";
		tourlets = new ArrayList<Tourlet>();
		String[] values = features.split(" ");
		name = values[0].replace("%20"," ");
		longDescr = values[1].replace("%20", " ").replace("%0A", "\n");
		shortDescr = values[2].replace("%20", " ");
		//in meters
		distance = Double.parseDouble(values[3]);
		//stops
		count = Integer.parseInt(values[4]);
		Tourlet lastTourlet = null;
		for(int i = 5; i < values.length ;i++)
		{
			Tourlet tourlet = new Tourlet(values[i]);

			tourlets.add(tourlet);

			//Link the tourlets forward and backward
			if(lastTourlet!=null)
			{
				try
				{

					tourlet.steps.get(0).setPrevious(lastTourlet.steps.get((lastTourlet.steps.size()-1)));
					lastTourlet.steps.get((lastTourlet.steps.size()-1)).setNext(tourlet.steps.get(0));

				}catch (Exception e) {
					// TODO: handle exception
					Logger.log(4, "vastav"+e);

				}


			}
			lastTourlet = tourlet;
		}

		//Get the landmark sequence
		String key = "map.pkg." + name + ".seq";
		String value = Property.takeProperty(key);

		if(value != null)
		{
			landmarkSequences = new ArrayList<String>();
			String[] sequences = value.trim().split(" ");
			for(int j = 0;j < sequences.length ; j++)
			{
				landmarkSequences.add(sequences[j]);
			}
		}

		//Get the last Landmark
		key = "map.pkg." +name+ ".last";
		value = Property.takeProperty(key);

		if(value != null)
		{
			landmarkLast = value;
		}

	}
	public String toString() {
		return name;
	}
	public Boolean inPackage(String landkey)
	{
		for (Tourlet tourlet : tourlets) {
			if(tourlet.landmarkKeys.contains(landkey))
			{
				return true;
			}

		}
		return false;

	}

}
