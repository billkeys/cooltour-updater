/*package geoplicity.cooltour.watch;

import org.geoplicity.cakewalk.api.LocationDevice;
import org.geoplicity.mobile.util.Property;

import geoplicity.cooltour.map.MapManager;
import geoplicity.cooltour.tour.TourManager;
import geoplicity.cooltour.way.Landmark;
import geoplicity.cooltour.way.Watch;
import geoplicity.cooltour.way.WayManager;

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
/*
public class WatchManager implements Runnable{

	public void run()
	{
		// If location device is a simulated one, pause it

		if(LocationDevice.getInstance()clone()))
			LocationDevice.getInstance().pause();

		// Loop through every watch on the queue until
		// we finish them
		while (true)
		{
			Watch watch = null;

			WayManager.getInstance().setM_trucking(true);

				watch = (Watch)WayManager.getInstance().m_queue.get(0);
			

			Landmark landmark = watch.getLandmark();

			// If we have some videos to play, stop the map updates (save some
					// cycles) since they won't be visible and wake up the visitor
			// with a gong
			if (landmark.getVideos().size() != 0)
			{
				MapManager.getInstance().pause = true;

				Boolean lastStop =
					TourManager.getInstance().currentTour.landmarkLast.equals(landmark.getKey());

				if (lastStop)
					EmCee.getInstance().GongThemLastTime();
				else
					EmCee.getInstance().GongThem();

				VideoPlayback(landmark);

				if (lastStop && Property.getProperty("survey.enabled") != null)
				{
					SurveyForm2 survey = new SurveyForm2();

					survey.showDialog();

					// Disable the survey if we're finishing by this means
					Property.takeProperty("survey.enabled");
				}
			}

		}
		// Remove the current watch from the queue
        // and if the queue is empty, we're done for now.
        // Otherwise, there's work left.
      
		// TODO: to affect "BACK" put the current watch on the "back" queue

            WayManager.getInstance().m_queue.remove(0);

            if (WayManager.getInstance().m_queue.size() == 0)
            {
            	WayManager.getInstance().setM_trucking(false);

                LocationDevice.getInstance().resume();

                break;
            }
        }

	}

	private void VideoPlayback(Landmark landmark) {
		// TODO Auto-generated method stub
		
	}
}
*/

package geoplicity.cooltour.way;

public class WatchManager implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}

