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
 
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import geoplicity.cooltour.game.Sprite;
import geoplicity.cooltour.gps.LocationManager;
import geoplicity.cooltour.util.*;
import geoplicity.cooltour.ui.R;
 
/**
 * This class implement's the user icon (or locator) which gives the position
 * on the map. The location actually consists of two parts: the animated graphic
 * and the pointer which itself is made of two parts: the main polygon and
 * the shadow polygon.
 *
 */
public class Locator{
 
	//Polygon dimensions (i.e., the triangle size)
	private final int POLY_HT = 25;
	private final int POLY_BASE = 20;
    private final int POLY_SHADOW_OFFSET = 4;
    
    //Polygon main color and shadow color
    private final int PAINT_COLOR = Color.RED;
    private final int PAINT_SHADOW_COLOR = Color.BLACK;
 
    //Animated graphic width and height
    private final int SPRITE_WIDTH = 33;
    private final int SPRITE_HEIGHT = 33;
    private final int SPRITE_ID = R.drawable.visitor_anim3;
 
    // Animated graphic
    private Sprite sprite = null;
 
    // Pointer polygons which in our case are triangle shapes
    private Point[] vertices = null;
    private Point[] shadowVertices = null;
 
    // Current world position
    private int worldX = -1;
    private int worldY = -1;
 
    // Old world position
    private int oldWorldX = -1;
    private int oldWorldY = -1;
  
    //Paints for the polygon pointer
    private Paint paint = new Paint();
    private Paint paintShadow = new Paint();
    
    // Reference to the map manager to retrieve view info
    private MapManager mapMgr = null;
    
    /**
     * Constructor
     * @param context Context that gives global information about the application
     * @param mapmgr MapManager for which this locator is used 
     */
    public Locator(Context context, MapManager mapmgr){
    	Logger.log(Logger.DEBUG, "constructor");
    	
    	mapMgr = mapmgr;    	
    	
    	Bitmap img = BitmapFactory.decodeResource(context.getResources(), SPRITE_ID);
    	sprite = new Sprite(context, img, SPRITE_WIDTH, SPRITE_HEIGHT);
    	
    	//Initialize the arrays
    	vertices = new Point[3];
    	shadowVertices = new Point[3];
    	for(int i=0; i<vertices.length; i++){
    		vertices[i] = new Point();
    		shadowVertices[i] = new Point();
    	}
    	
    	paint.setColor(PAINT_COLOR);
    	paintShadow.setColor(PAINT_SHADOW_COLOR);
    }
    
    /**
     * Updates the current world coordinates
     * @param x The X coordinate in the world
     * @param y The Y coordinate in the world
     */
    public void update(int x, int y){
    	oldWorldX = worldX;
        oldWorldY = worldY;
 
        worldX = x;
        worldY = y;
        
        sprite.nextFrame();
    }
    
    /**
     * Draw the locator (the polygon).
     * @param canvas Canvas on which the locator is to be drawn.
     */
    public void draw(Canvas canvas){
    	Logger.log(Logger.DEBUG, "draw()");
    	
    	// Draw the shadow poly (it will be on the bottom)
    	double theta = LocationManager.GetInstance().getBearing() * 180.0 / Math.PI;
    	
    	int xo = 0;
        int yo = 0;
        
        if (theta <= 90.0 || theta >= 270.0){
            xo -= POLY_SHADOW_OFFSET;
            yo += POLY_SHADOW_OFFSET;
        }
        else{
            xo += POLY_SHADOW_OFFSET;
            yo -= POLY_SHADOW_OFFSET;
        }
        
        //Center sprite (on top) around it's screen coordinate
        int scrX = worldX - mapMgr.getViewX();
        int scrY = worldY - mapMgr.getViewY();
        
        sprite.setX(scrX - SPRITE_WIDTH / 2);
        sprite.setY(scrY - SPRITE_HEIGHT / 2);
        
        sprite.draw(canvas);
        
    }
    
    /**
     * Renders the poly, in this case a triangle. Much of this method was refactored from RFW.
     * @param canvas The canvas to draw on
     * @param vertices Vertices of the triangle
     * @param xoff X offset
     * @param yoff Y offset
     */
    private void drawPoly(Canvas canvas, Point[] vertices, int xoff, int yoff, Paint paint){    	    	
    	// This is the "normalized" triangle locator we want to generate.
        //
        //              (x0, y0)
        //              *
        //             * *
        //            *   *
        //           *     *
        // (x2, y2) ********* (x1, y1)
        //
        // It's normalized in the sense that initially it's always upright
        // with the center at (0,0). Then, given theta, we rotate it locally
        // and translate to its world coordinate, (x,y).
 
    	// Get screen coordinates (again!)
    	int scrX = worldX - mapMgr.getViewX();
    	int scrY = worldY - mapMgr.getViewY();
    	
    	Logger.log(Logger.DEBUG, "drawPoly()");
    	Logger.log(Logger.DEBUG, "screen coordinates: ("+scrX+","+scrY+")");
    	Logger.log(Logger.DEBUG, "current world coordinates: ("+worldX+","+worldY+")");
    	Logger.log(Logger.DEBUG, "old world coordinates: ("+oldWorldX+","+oldWorldY+")");
    	
    	// If the position has changed, update the poly
        if (oldWorldX != worldX || oldWorldY != worldY){
        	// First reset the poly at its initial position
            vertices[0].x = 0;
            vertices[0].y = -POLY_HT / 2;
 
            vertices[1].x = POLY_BASE / 2;
            vertices[1].y = POLY_HT / 2;
 
            vertices[2].x = -POLY_BASE / 2;
            vertices[2].y = POLY_HT / 2;
 
            // Get the bearing and rotate each vertex respectively
            double theta = LocationManager.GetInstance().getBearing();        
            
            VectorOps.rotate(vertices[0], theta);
            VectorOps.rotate(vertices[1], theta);
            VectorOps.rotate(vertices[2], theta);
            
            // Translate the poly to its screen coordinate with
            // offset (for the shadow).
            vertices[0].x += scrX + xoff;
            vertices[0].y += scrY + yoff;
 
            vertices[1].x += scrX + xoff;
            vertices[1].y += scrY + yoff;
 
            vertices[2].x += scrX + xoff;
            vertices[2].y += scrY + yoff;            
        }
 
    	//render the poly
        Path path = new Path();
        path.moveTo(vertices[0].x, vertices[0].y);
        path.lineTo(vertices[1].x, vertices[1].y);
        path.lineTo(vertices[2].x, vertices[2].y);
        
        canvas.drawPath(path, paint);
    }
 
}
 