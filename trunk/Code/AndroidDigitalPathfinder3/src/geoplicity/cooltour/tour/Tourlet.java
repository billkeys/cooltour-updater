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

import org.geoplicity.mobile.util.Property;

public class Tourlet {
	public ArrayList<Step> steps=new ArrayList<Step>();
	public ArrayList<String> landmarkKeys=new ArrayList<String>();
	public ArrayList<AudioTrack> AudioTracks=new ArrayList<AudioTrack>();
	private Tourlet segue;
	public ArrayList<Step> getStep() {
		return steps;
	}
	public void setStep(ArrayList<Step> steps) {
		this.steps = steps;
	}
	public ArrayList<String> getLandmarkKeys() {
		return landmarkKeys;
	}
	public void setLandmarkKeys(ArrayList<String> landmarkKeys) {
		this.landmarkKeys = landmarkKeys;
	}
	public ArrayList<AudioTrack> getAudioTracks() {
		return AudioTracks;
	}
	public void setAudioTracks(ArrayList<AudioTrack> audioTracks) {
		AudioTracks = audioTracks;
	}
	public Tourlet getSegue() {
		return segue;
	}
	public void setSegue(Tourlet segue) {
		this.segue = segue;
	}
	public Tourlet(String name)
	{
		//Parse the map.net.<tourlet> property
		
		steps=new ArrayList<Step>();
		
		// map.net.T-2582 tour 0 1 2 3 4 5 6 7
		
		String key="map.net."+name;
		
		String[] values=Property.getProperty(key).split(" ");
		
		//Load the steps
		Step lastStep=null;
		
		for(int i = 1; i < values.length; i++)
		{
			String tag=values[i];
			
			// map.net.tour.T-2582.0 1271 950 0 0

			String ky="map.net.tour."+name+"."+tag;
			String[] vals=Property.takeProperty(ky).split(" ");

			int x=Integer.parseInt(vals[0]);
			int y=Integer.parseInt(vals[1]);
			
			Step step=new Step(x, y);
			steps.add(step);
			
			 // Link last step forward to this step and link
            // this step backward to the previous step

			if(lastStep!=null)
			{
				lastStep.setNext(step);
				step.setPrevious(lastStep);
			}
			lastStep=step;
			
		}
		
		// Load the landmarks. The landmarks are not stored
        // here but only referenced here. The landmarks
        // are maintained by the way manager.
		
		landmarkKeys=new ArrayList<String>();
		
		// map.net.tour.T-2582.landmark.0 map.net.lois.L-2496.A
		for(int i = 0; ;i++)
		{
			String ky="map.net.tour." + name + ".landmark." + i;
			
			String val=Property.takeProperty(ky);
			
			if(val==null)
			{
				break;
			}
			landmarkKeys.add(val);
		}
	}
}
