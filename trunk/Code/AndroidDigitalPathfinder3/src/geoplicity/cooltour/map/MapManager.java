/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geoplicity.cooltour.map;

import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;

import geoplicity.cooltour.gps.LocationManager;
import geoplicity.cooltour.tour.Step;
import geoplicity.cooltour.util.Constants;
import geoplicity.cooltour.way.WayManager;
import geoplicity.cooltour.map.WayPt;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

/**
 * This class manages the map as a square layer of four tiles. The layer has
 * four possible states, A, B, C, D. The in-memory bitmap is managed by paging in
 * tiles dynamically. The tiles are maintained as a "layer" of one, two or four
 * tiles.
 *
 */
public class MapManager extends ImageView{
	
	private static MapManager instance = null;

	//singleton 
	public static MapManager getInstance()
	{
		return instance;
	}	

	protected static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.RGB_565;     //Bitmap configuration

	protected Bitmap mapImage = null;

	protected int mapWidth = 0;
	protected int mapHeight = 0;

	protected WayPt curPosition = null;

	//Upper left corner of the view (screen)
	protected int viewX = 0;
	protected int viewY = 0;

	//Screen (and hence, the view) size
	private int screenW = -1;
	private int screenH = -1;

	//Tiled layer configurations
	public enum Config{ START, A, B, C, D};

	//Current configuration, initialized to START
	protected Config curConfig = Config.START;

	//Tile layer organized by index:
	// 0 1
	// 2 3
	protected Tile[] layer = new Tile[4];

	//Indices in the tile layer
	protected final int UPPER_LEFT = 0;
	protected final int UPPER_RIGHT = 1;
	private final int LOWER_LEFT = 2;
	protected final int LOWER_RIGHT = 3;

	//World coordinates of the tile layer.
	protected int layerX = 0;
	protected int layerY = 0;

	// Visitor's indicator on the map
	protected Locator locator = null;

	protected WayManager wayManager = null;

	/**
	 * Constructor
	 * @param w Width of the screen
	 * @param h Height of the screen
	 */
	public MapManager(Context context, int w, int h){          
		super(context);
		Logger.log(Logger.DEBUG, "constructor, screen w = " + w + " screen h = " + h);
		this.screenW = w;
		this.screenH = h;

		locator = new Locator(context, this);
		wayManager = new WayManager();
		instance = this;
		this.initMap();

	}

	/**
	 * Update the map
	 */
	public void update(){
		Logger.log(Logger.DEBUG, "update()");
		WayPt pos = LocationManager.GetInstance().GetCurPosition();

		//If the position is null it means the lon, lat is not within range
		//If the boundaries are not ok, it means the x,y is off the map --
		//which should not be possible.
		if(pos != null)
			curPosition = pos;

		//Update the view with the user at the center
		this.updateView();

		//Save the old col, row of the upper left tile since it is possible
		//for the state to be the same but the tiles need to be refreshed
		//especially if the view is the same size or larger than a tile.
		int oldRow = layer[UPPER_LEFT].row;
		int oldCol = layer[UPPER_LEFT].col;

		//Update the tile info
		this.updateTiles();

		//Get the new tile configuration
		try {
			Config newConfig = this.getConfig();
			if(newConfig != curConfig ||
					oldRow != layer[UPPER_LEFT].row || oldCol != layer[UPPER_LEFT].col){
				this.pageInTiles(newConfig);
				curConfig = newConfig;
			}
		} catch (Exception e) {
			Logger.log(Logger.TRAP, "An error occurred while updating map: " + e.toString());
		}

		//Get the tiled layer's upper left corner
		layerX = layer[UPPER_LEFT].col * Tile.width - viewX;
		layerY = layer[UPPER_LEFT].row * Tile.height - viewY;

		//Move the locator
		locator.update(curPosition.getX(), curPosition.getY());

		//Update the way manager
		wayManager.update();
	}

	/**
	 * Returns true if the landmark is visible on the display.
	 */
	public boolean inView(int x, int y) {
		if (x < viewX || x > (viewX + screenW))
			return false;

		if (y < viewY || y > (viewY + screenH))
			return false;

		return true;
	}

	/**
	 * Initialize the map
	 */
	protected void initMap(){
		Logger.log(Logger.TRACE, "initMap()");

		//load the tile properties
		String rootDir = Property.getProperty("app.root.dir");
		String mapProperties = rootDir + "/" + Property.getProperty("current.site")+
		Property.getProperty("map.props");

		Property.loadProperties(mapProperties);
		Tile.dir = Property.storageDevice + rootDir + "/" +
		Property.getProperty("current.site")+ "/images/";


		Tile.prefix = Property.takeProperty("map.tile.prefix");
		Tile.type = Property.takeProperty("map.tile.type");
		Tile.width = Integer.parseInt(Property.takeProperty("map.tile.width"));
		Tile.height = Integer.parseInt(Property.takeProperty("map.tile.height"));

		mapWidth = Integer.parseInt(Property.takeProperty("map.image.width"));
		mapHeight = Integer.parseInt(Property.takeProperty("map.image.height"));

		//Position the map center over the default location
		double lon = Double.parseDouble(Property.takeProperty("map.default.lon"));
		double lat = Double.parseDouble(Property.takeProperty("map.default.lat"));

		//Position the user at the default location
		curPosition = LocationManager.GetInstance().GetPosition(lon, lat);

		//Move cursor to default location
		locator.update(curPosition.getX(), curPosition.getY());

		updateView();
		updateTiles();

		try{
			Config newConfig = getConfig();
			Logger.log(Logger.TRACE, "new config is " + newConfig);

			if(newConfig != curConfig){
				pageInTiles(newConfig);
				curConfig = newConfig;

			}
		}catch(Exception e){
			Logger.log(Logger.TRAP, "An error occurred while initializing map: " + e.toString());
		}

		//Get the tiled layer's upper left corner and render the layer
		layerX = layer[UPPER_LEFT].col * Tile.width - viewX;
		layerY = layer[UPPER_LEFT].row * Tile.height - viewY;
	}

	/**
	 * Updates the view based on the user's position. This "drags" the
	 * view relative to the user's location unless the user is near
	 * the edge, in which case the user keeps moving but the view doesn't.
	 */
	protected void updateView(){
		Logger.log(Logger.DEBUG, "updateView()");
		int oldViewX = viewX;
		int oldViewY = viewY;

		viewX = curPosition.getX() - screenW / 2;
		viewY = curPosition.getY() - screenH / 2;

		//Check for boundary conditions
		if(viewX < 0 || viewX + screenW > mapWidth){
			viewX = oldViewX;
		}

		if(viewY < 0 || viewY + screenH > mapHeight){
			viewY = oldViewY;
		}

		Logger.log(Logger.DEBUG, "view x = " + viewX + " view y = " + viewY);
	}

	/**
	 * Update the layer of four tiles.
	 */
	protected void updateTiles(){  
		Logger.log(Logger.DEBUG, "updateTiles()");
		int worldx = curPosition.getX();
		int worldy = curPosition.getY();

		layer[UPPER_LEFT] = translateToTile(worldx-screenW/2, worldy-screenH/2);
		layer[UPPER_RIGHT] = translateToTile(worldx + screenW / 2, worldy - screenH / 2);
		layer[LOWER_LEFT] = translateToTile(worldx-screenW/2, worldy+screenH/2);
		layer[LOWER_RIGHT] = translateToTile(worldx+screenW/2, worldy+screenH/2);
	}

	/**
	 * Translate world coordinates to tile coordinates
	 * @param worldx X coordinate in the world
	 * @param worldy Y coordinate in the world
	 * @return
	 */
	private Tile translateToTile(int worldx, int worldy){
		Tile tile = new Tile();

		tile.col = worldx / Tile.width;
		tile.row = worldy / Tile.height;

		return tile;
	}

	/**
	 * Get the current screen configuration (A, B, C, or D)
	 * @return The current screen configuration
	 * @throws Exception if the configuration is invalid
	 */
	protected Config getConfig() throws Exception{
		Logger.log(Logger.DEBUG, "getConfig()");
		if( layer[UPPER_LEFT].equals(layer[LOWER_LEFT]) &&
				layer[LOWER_LEFT].equals(layer[UPPER_RIGHT]) &&
				layer[LOWER_LEFT].equals(layer[UPPER_RIGHT]) &&
				layer[LOWER_LEFT].equals(layer[LOWER_RIGHT])
		){
			return Config.A;
		}

		if( !layer[UPPER_LEFT].equals(layer[UPPER_RIGHT]) &&
				!layer[UPPER_RIGHT].equals(layer[LOWER_RIGHT]) &&
				!layer[LOWER_RIGHT].equals(layer[LOWER_LEFT])
		){
			return Config.D;
		}

		if(layer[UPPER_LEFT].col+1 == layer[UPPER_RIGHT].col){
			return Config.B;
		}

		if(layer[UPPER_LEFT].row+1 == layer[LOWER_LEFT].row){
			return Config.C;
		}

		throw (new Exception("Invalid state"));

	}

	/**
	 * Page in the tile(s) corresponding to the current configuration
	 * @param config Tile configuration
	 * @throws Exception
	 */
	protected void pageInTiles(Config config) throws Exception{
		switch(config){
		case A: pageA(); break;
		case B: pageB(); break;
		case C: pageC(); break;
		case D: pageD(); break;
		default:
			throw new Exception("Invalid configuration " + config);
		}
	}

	/**
	 * Page in the A configuration:
	 * 10
	 * 00
	 */
	protected void pageA(){
		mapImage = BitmapFactory.decodeFile( getTileName(UPPER_LEFT) );
	}

	/**
	 * Page in the B configuration
	 * 11
	 * 00
	 */
	protected void pageB(){
		mapImage = Bitmap.createBitmap(Tile.width * 2, Tile.height, BITMAP_CONFIG);
		Canvas canvas = new Canvas(mapImage);

		Bitmap bitmapUpperLeft = BitmapFactory.decodeFile( getTileName(UPPER_LEFT) );
		canvas.drawBitmap(bitmapUpperLeft, 0, 0, null);

		Bitmap bitmapUpperRight = BitmapFactory.decodeFile( getTileName(UPPER_RIGHT) );
		canvas.drawBitmap(bitmapUpperRight, Tile.width, 0, null);
	}

	/**
	 * Page in the C configuration
	 * 10
	 * 10
	 */
	protected void pageC(){
		mapImage = Bitmap.createBitmap(Tile.width, Tile.height * 2, BITMAP_CONFIG);
		Canvas canvas = new Canvas(mapImage);

		Bitmap bitmapUpperLeft = BitmapFactory.decodeFile( getTileName(UPPER_LEFT) );
		canvas.drawBitmap(bitmapUpperLeft, 0, 0, null);

		Bitmap bitmapLowerRight = BitmapFactory.decodeFile( getTileName(LOWER_RIGHT) );
		canvas.drawBitmap(bitmapLowerRight, 0, Tile.height, null);
	}

	/**
	 * Page in the D configuration
	 * 11
	 * 11
	 */
	protected void pageD(){
		mapImage = Bitmap.createBitmap(Tile.width * 2, Tile.height * 2, BITMAP_CONFIG);
		Canvas canvas = new Canvas(mapImage);

		int num = 0;
		for (int row = 0; row < 2; row++){
			for (int col = 0; col < 2; col++){
				Bitmap bitmap = BitmapFactory.decodeFile( getTileName(num) );
				canvas.drawBitmap(bitmap, col*Tile.width, row*Tile.height, null);
				num++;
			}
		}
	}

	/**
	 * Get the filename of the tile
	 * @param pos Position number of the tile
	 * @return The filename of the specific tile
	 */
	protected String getTileName(int pos){      
		String tileName = Tile.dir + Tile.prefix + layer[pos].col + "_" + layer[pos].row + "." + Tile.type;
		Logger.log(Logger.DEBUG, "getTileName: tile name is " + tileName);
		return tileName;
	}

	/**
	 * Render the map
	 */
	@Override
	protected void onDraw(Canvas canvas){
		Logger.log(Logger.TRACE, "onDraw()");
		canvas.drawColor(Color.BLACK);  //initialize
		canvas.drawBitmap(mapImage, layerX, layerY, null);

		drawDebugInfo(canvas);

		wayManager.render(canvas);
		Logger.log(Logger.TRACE, "drawing locator");
		locator.draw(canvas);
	}

	/**
	 * Draw the current x,y coordinates on the screen
	 * @param canvas The canvas to be drawn on
	 */
	void drawDebugInfo(Canvas canvas){
		String debugInfo = "curPosX=" + curPosition.getX() + ", curPosY=" + curPosition.getY();
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setTextSize(12);
		canvas.drawText(debugInfo, 10, 20, paint);
	}

	/**
	 * Get the x coordinate of the view
	 * @return The x coordinate of the view
	 */
	public int getViewX(){
		return this.viewX;
	}
	/**
	 * Get the y coordinate of the view
	 * @return The y coordinate of the view
	 */
	public int getViewY(){
		return this.viewY;
	}

	/**
	 * Get the width of the view
	 * @return The width of the view
	 */
	public int getViewW(){
		return this.screenW;
	}
	/**
	 * Get the height of the view
	 * @return The height of the view
	 */
	public int getViewH(){
		return this.screenH;
	}
	/**
	 * 
	 * @param step
	 * @return
	 */
	public Step transform(Step step) {
		return step;
	}
}

