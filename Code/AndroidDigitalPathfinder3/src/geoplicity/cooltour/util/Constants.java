/**
 * Copyright (c) 2010 Contributors, http://geoplicity.org/
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

/**
 * This class defines some constants used throughout the application.
 * 
 */
public class Constants {
	// tag used for logging
	public static final String LOG_TAG = "CoolTour";
	public static final String PROPERTY_APP_ROOT_DIR = "app.root.dir";
	public static final String PROPERTY_UPDATE_URL = "update.url";
	// intent actions
	public static final String INTENT_ACTION_MAIN_UI = "geoplicity.cooltour.MAIN_UI";
	public static final String INTENT_ACTION_BEGIN_TOUR = "geoplicity.cooltour.BEGIN_TOUR";
	public static final String INTENT_ACTION_UPDATE_LOCATION = "geoplicity.cooltour.UPDATE_LOCATION";
	public static final String INTENT_ACTION_LAUNCH_SITE_UPDATER = "geoplicity.cooltour.LAUNCH_SITE_UPDATER";
	public static final String INTENT_ACTION_LAUNCH_SITE_UPDATE = "geoplicity.cooltour.LAUNCH_SITE_UPDATE";

	// intent extras
	public static final String INTENT_EXTRA_SITE_UPDATE = "geoplicity.cooltour.SITE_UPDATE";
	public static final String INTENT_EXTRA_SITE_UPDATE_NAME = "geoplicity.cooltour.SITE_UPDATE_NAME";
	public static final String INTENT_EXTRA_SITE_RUNNING_UPDATE = "geoplicity.cooltour.SITE_RUNNING_UPDATE";

	// default property files
	public static final String DEFAULT_APP_PROPERTIES = "app-props.txt";
	public static final String DEFAULT_SITE_PROPERTIES = "site-props.txt";
	public static final String DEFAULT_GEO_PROPERTIES = "/geo-props.txt";
	public static final String DEFAULT_MAP_PROPERTIES = "/map.props";
	public static final String DEFAULT_APP_ROOT_DIR = "/Geoplicity";
	public static final String DEFAULT_WAY_PROPERTIES = "stway-5-props.txt";
	public static final String DEFAULT_TRI_PROPERTIES = "sttri-4-props.txt";

	// testing property
	public static final int TEST_INTERVAL = 120; // how many seconds the test
													// case will run, 0 means
													// indefinite
	public static final String TEST_FILE = "/sdcard/test/log-2010-5-28.txt";

	// Default site to display to user
	public static final String DEFAULT_SITE = "Staatsburgh";

	// Updater Constants
	public static final String SDCARD_ROOT = "/sdcard";
	// Note: In order to use a web server on the same system in which the
	// emulator is running, you have to use your workstation's IP instead of
	// "localhost".
	public static final String UPDATE_SERVER = "http://192.168.1.102/";
	// public static final String UPDATE_SERVER =
	// "http://reachforthestarsmcc.com/sdodge/files/sites/";
	public static final String UPDATE_FILE_EXT = ".txt";
	public static final String UPDATE_SITES_FILE = "sites" + UPDATE_FILE_EXT;
	public static final String UPDATE_TEMP_DIR = "tmp/";
}
