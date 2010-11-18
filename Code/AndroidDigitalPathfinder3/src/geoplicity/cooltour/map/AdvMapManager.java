package geoplicity.cooltour.map;

import geoplicity.cooltour.gps.LocationManager;

import org.geoplicity.mobile.util.Logger;
import org.geoplicity.mobile.util.Property;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class AdvMapManager extends MapManager {
        
         public AdvMapManager(Context context, int w, int h) {
                 super(context, w, h);
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
         	Tile.dir = Property.storageDevice + rootDir + "/" + Property.getProperty("current.site")+ "/images/";
         	
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
     			else {
     				this.pageInTiles(newConfig);
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
          * Page in the A configuration:
          * 10
          * 00
          */
         protected void pageA(){
         	mapImage = Bitmap.createBitmap(Tile.width, Tile.height, BITMAP_CONFIG);
         	Canvas canvas = new Canvas(mapImage);
         	
         	int tx = this.getViewX() / Tile.width;
     		int ty = this.getViewY() / Tile.height;

     		int x = this.getViewX() - tx * Tile.width;
     		int y = this.getViewY() - ty * Tile.height;

     		int w = (tx + 1) * Tile.width - this.getViewX();
     		int h = (ty + 1) * Tile.height - this.getViewY();
     		
         	// 1 0
     		// 0 0
     		java.lang.Runtime.getRuntime().gc();
     		Bitmap bitmap = BitmapFactory.decodeFile(getTileName(UPPER_LEFT));
     		bitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
     		
     		canvas.drawBitmap(bitmap, x, y, null);
         }
         
         /**
          * Page in the B configuration
          * 11
          * 00
          */
         protected void pageB(){
         	mapImage = Bitmap.createBitmap(Tile.width * 2, Tile.height, BITMAP_CONFIG);
         	Canvas canvas = new Canvas(mapImage);
         	
         	int tx = this.getViewX() / Tile.width;
     		int ty = this.getViewY() / Tile.height;

     		int x = this.getViewX() - tx * Tile.width;
     		int y = this.getViewY() - ty * Tile.height;

     		int w = (tx + 1) * Tile.width - this.getViewX();
     		int h = (ty + 1) * Tile.height - this.getViewY();
     		
         	// 1 0
     		// 0 0
     		java.lang.Runtime.getRuntime().gc();
     		Bitmap bitmap = BitmapFactory.decodeFile(getTileName(UPPER_LEFT));
     		bitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
     		
     		canvas.drawBitmap(bitmap, x, y, null);
     		
     		// 0 1
     		// 0 0
     		int w1 = Tile.width-w;
     		bitmap = BitmapFactory.decodeFile(getTileName(UPPER_RIGHT));
     		bitmap = Bitmap.createBitmap(bitmap, 0, y, w1, h);

     		canvas.drawBitmap(bitmap, x+w, y, null);
         }

         /**
          * Page in the C configuration
          * 10
          * 10
          */
         protected void pageC(){
         	mapImage = Bitmap.createBitmap(Tile.width*2, Tile.height * 2, BITMAP_CONFIG);
         	Canvas canvas = new Canvas(mapImage);
         	
         	int tx = this.getViewX() / Tile.width;
     		int ty = this.getViewY() / Tile.height;

     		int x = this.getViewX() - tx * Tile.width;
     		int y = this.getViewY() - ty * Tile.height;

     		int w = (tx + 1) * Tile.width - this.getViewX();
     		int h = (ty + 1) * Tile.height - this.getViewY();
     		
         	// 1 0
     		// 0 0
     		java.lang.Runtime.getRuntime().gc();
     		Bitmap bitmap = BitmapFactory.decodeFile(getTileName(UPPER_LEFT));
     		bitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
     		
     		canvas.drawBitmap(bitmap, x, y, null);
         	
         	// 0 0
     		// 1 0
     		int h1 = Tile.height-h;
     		bitmap = BitmapFactory.decodeFile(getTileName(LOWER_RIGHT));
     		bitmap = Bitmap.createBitmap(bitmap, x, 0, w, h1);

     		canvas.drawBitmap(bitmap, x, y+h, null);
     		System.out.println("TEST");
     		Log.v("initMap", "TEST");
         }
         
         /**
          * Page in the D configuration
          * 11
          * 11
          */
         protected void pageD() {
     		mapImage = Bitmap.createBitmap(Tile.width*2, Tile.height*2, BITMAP_CONFIG);
     		Canvas canvas = new Canvas(mapImage);

     		int tx = this.getViewX() / Tile.width;
     		int ty = this.getViewY() / Tile.height;

     		int x = this.getViewX() - tx * Tile.width;
     		int y = this.getViewY() - ty * Tile.height;

     		int w = (tx + 1) * Tile.width - this.getViewX();
     		int h = (ty + 1) * Tile.height - this.getViewY();
     		
     		// 1 0
     		// 0 0
     		java.lang.Runtime.getRuntime().gc();
     		Bitmap bitmap = BitmapFactory.decodeFile(getTileName(0));
     		bitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
     		
     		canvas.drawBitmap(bitmap, x, y, null);
     		
     		// 0 1
     		// 0 0
     		int w1 = Tile.width-w;
     		bitmap = BitmapFactory.decodeFile(getTileName(1));
     		bitmap = Bitmap.createBitmap(bitmap, 0, y, w1, h);

     		canvas.drawBitmap(bitmap, x+w, y, null);
     		
     		// 0 0
     		// 1 0
     		int h1 = Tile.height-h;
     		bitmap = BitmapFactory.decodeFile(getTileName(2));
     		bitmap = Bitmap.createBitmap(bitmap, x, 0, w, h1);

     		canvas.drawBitmap(bitmap, x, y+h, null);
     		
     		// 0 0
     		// 0 1
     		bitmap = BitmapFactory.decodeFile(getTileName(3));
     		bitmap = Bitmap.createBitmap(bitmap, 0, 0, w1, h1);

     		canvas.drawBitmap(bitmap, x+w,y+h, null);
     	}
}