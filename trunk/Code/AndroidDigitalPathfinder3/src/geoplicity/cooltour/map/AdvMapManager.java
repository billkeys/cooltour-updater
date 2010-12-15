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
	protected void initMap() {
		Logger.log(Logger.TRACE, "initMap()");

		// load the tile properties
		String rootDir = Property.getProperty("app.root.dir");
		String mapProperties = rootDir + "/"
				+ Property.getProperty("current.site")
				+ Property.getProperty("map.props");

		Property.loadProperties(mapProperties);
		Tile.dir = Property.storageDevice + rootDir + "/"
				+ Property.getProperty("current.site") + "/images/";

		Tile.prefix = Property.takeProperty("map.tile.prefix");
		Tile.type = Property.takeProperty("map.tile.type");
		Tile.width = Integer.parseInt(Property.takeProperty("map.tile.width"));
		Tile.height = Integer
				.parseInt(Property.takeProperty("map.tile.height"));

		mapWidth = Integer.parseInt(Property.takeProperty("map.image.width"));
		mapHeight = Integer.parseInt(Property.takeProperty("map.image.height"));

		// Position the map center over the default location
		double lon = Double.parseDouble(Property
				.takeProperty("map.default.lon"));
		double lat = Double.parseDouble(Property
				.takeProperty("map.default.lat"));

		// Position the user at the default location
		curPosition = LocationManager.GetInstance().GetPosition(lon, lat);

		// Move cursor to default location
		locator.update(curPosition.getX(), curPosition.getY());

		updateView();
		updateTiles();

		try {
			Config newConfig = getConfig();
			Logger.log(Logger.TRACE, "new config is " + newConfig);

			if (newConfig != curConfig) {
				pageInTiles(newConfig);
				curConfig = newConfig;

			}
		} catch (Exception e) {
			Logger.log(Logger.TRAP,
					"An error occurred while initializing map: " + e.toString());
		}

		// Get the tiled layer's upper left corner and render the layer
		layerX = layer[UPPER_LEFT].col * Tile.width - viewX;
		layerY = layer[UPPER_LEFT].row * Tile.height - viewY;
	}

	/**
	 * Update the map
	 */
	public void update() {
		Logger.log(Logger.DEBUG, "update()");
		WayPt pos = LocationManager.GetInstance().GetCurPosition();

		// If the position is null it means the lon, lat is not within range
		// If the boundaries are not ok, it means the x,y is off the map --
		// which should not be possible.
		if (pos != null)
			curPosition = pos;

		// Update the view with the user at the center
		this.updateView();

		// Save the old col, row of the upper left tile since it is possible
		// for the state to be the same but the tiles need to be refreshed
		// especially if the view is the same size or larger than a tile.
		int oldRow = layer[UPPER_LEFT].row;
		int oldCol = layer[UPPER_LEFT].col;

		// Update the tile info
		this.updateTiles();

		// Get the new tile configuration
		try {
			Config newConfig = this.getConfig();
			if (newConfig != curConfig || oldRow != layer[UPPER_LEFT].row
					|| oldCol != layer[UPPER_LEFT].col) {
				this.pageInTiles(newConfig);
				curConfig = newConfig;
			} else {
				this.pageInTiles(newConfig);
			}
		} catch (Exception e) {
			Logger.log(Logger.TRAP, "An error occurred while updating map: "
					+ e.toString());
		}

		// Get the tiled layer's upper left corner
		layerX = layer[UPPER_LEFT].col * Tile.width - viewX;
		layerY = layer[UPPER_LEFT].row * Tile.height - viewY;

		// Move the locator
		locator.update(curPosition.getX(), curPosition.getY());

		// Update the way manager
		wayManager.update();
	}

	/**
	 * Page in the A configuration: 
	 * 10
	 * 00
	 */
	protected void pageA() {
		mapImage = Bitmap.createBitmap(Tile.width, Tile.height, BITMAP_CONFIG);
		Canvas canvas = new Canvas(mapImage);

		int tileX = this.getViewX() / Tile.width;
		int tileY = this.getViewY() / Tile.height;

		int x = this.getViewX() - tileX * Tile.width;
		int y = this.getViewY() - tileY * Tile.height;

		int tileWidth = (tileX + 1) * Tile.width - this.getViewX();
		int tileHeight = (tileY + 1) * Tile.height - this.getViewY();

		// Upper left tile
		Bitmap bitmap = BitmapFactory.decodeFile(getTileName(0));
		bitmap = Bitmap.createBitmap(bitmap, x, y, tileWidth, tileHeight);

		canvas.drawBitmap(bitmap, x, y, null);
	}

	/**
	 * Page in the B configuration:
	 * 11
	 * 00
	 */
	protected void pageB() {
		mapImage = Bitmap.createBitmap(Tile.width * 2, Tile.height,
				BITMAP_CONFIG);
		Canvas canvas = new Canvas(mapImage);

		int tileX = this.getViewX() / Tile.width;
		int tileY = this.getViewY() / Tile.height;

		int x = this.getViewX() - tileX * Tile.width;
		int y = this.getViewY() - tileY * Tile.height;

		int tileWidth = (tileX + 1) * Tile.width - this.getViewX();
		int tileHeight = (tileY + 1) * Tile.height - this.getViewY();

		// Upper left tile
		Bitmap bitmap = BitmapFactory.decodeFile(getTileName(0));
		bitmap = Bitmap.createBitmap(bitmap, x, y, tileWidth, tileHeight);

		canvas.drawBitmap(bitmap, x, y, null);

		// Upper right tile
		int widthRight = Tile.width - tileWidth;
		bitmap = BitmapFactory.decodeFile(getTileName(1));
		bitmap = Bitmap.createBitmap(bitmap, 0, y, widthRight, tileHeight);

		canvas.drawBitmap(bitmap, x + tileWidth, y, null);
	}

	/**
	 * Page in the C configuration:
	 * 10 
	 * 10
	 */
	protected void pageC() {
		mapImage = Bitmap.createBitmap(Tile.width * 2, Tile.height * 2,
				BITMAP_CONFIG);
		Canvas canvas = new Canvas(mapImage);

		int tileX = this.getViewX() / Tile.width;
		int tileY = this.getViewY() / Tile.height;

		int x = this.getViewX() - tileX * Tile.width;
		int y = this.getViewY() - tileY * Tile.height;

		int tileWidth = (tileX + 1) * Tile.width - this.getViewX();
		int tileHeight = (tileY + 1) * Tile.height - this.getViewY();

		// Upper left tile
		Bitmap bitmap = BitmapFactory.decodeFile(getTileName(0));
		bitmap = Bitmap.createBitmap(bitmap, x, y, tileWidth, tileHeight);

		canvas.drawBitmap(bitmap, x, y, null);

		// Lower left tile
		int heightLower = Tile.height - tileHeight;
		bitmap = BitmapFactory.decodeFile(getTileName(2));
		bitmap = Bitmap.createBitmap(bitmap, x, 0, tileWidth, heightLower);

		canvas.drawBitmap(bitmap, x, y + tileHeight, null);
	}

	/**
	 * Page in the D configuration:
	 * 11 
	 * 11
	 */
	protected void pageD() {
		mapImage = Bitmap.createBitmap(Tile.width *2, Tile.height *2,
				BITMAP_CONFIG);
		Canvas canvas = new Canvas(mapImage);

		int tileX = this.getViewX() / Tile.width;
		int titleY = this.getViewY() / Tile.height;

		int x = this.getViewX() - tileX * Tile.width;
		int y = this.getViewY() - titleY * Tile.height;

		int tileWidth = (tileX + 1) * Tile.width - this.getViewX();
		int tileHeight = (titleY + 1) * Tile.height - this.getViewY();

		// Upper left tile
		Bitmap bitmap = BitmapFactory.decodeFile(getTileName(0));
		bitmap = Bitmap.createBitmap(bitmap, x, y, tileWidth, tileHeight);

		canvas.drawBitmap(bitmap, x, y, null);

		// Upper right tile
		int widthRight = Tile.width - tileWidth;
		bitmap = BitmapFactory.decodeFile(getTileName(1));
		bitmap = Bitmap.createBitmap(bitmap, 0, y, widthRight, tileHeight);

		canvas.drawBitmap(bitmap, x + tileWidth, y, null);

		// Lower left tile
		int heightLower = Tile.height - tileHeight;
		bitmap = BitmapFactory.decodeFile(getTileName(2));
		bitmap = Bitmap.createBitmap(bitmap, x, 0, tileWidth, heightLower);

		canvas.drawBitmap(bitmap, x, y + tileHeight, null);

		// Lower right tile
		bitmap = BitmapFactory.decodeFile(getTileName(3));
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, widthRight, heightLower);

		canvas.drawBitmap(bitmap, x + tileWidth, y + tileHeight, null);
	}
}