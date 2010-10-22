package geoplicity.cooltour.game;

import org.geoplicity.mobile.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

/**
 * This class implements the locator sprite (the animation over the locator).
 *
 */
public class Sprite{
	
	private Bitmap m_Image = null;	//sprite image
	
	//Width and height of each frame
	private int m_FrameWidth = 0;
	private int m_FrameHeight = 0;

	private int m_CurFrame = 0;
	private int m_TotalFrames = 0;

	private int[] m_OffXSeq = null;
	private int[] m_OffYSeq = null;

	//X, Y coordinates of the upper left corder of the sprite
	private int m_Xcoord = 0;
	private int m_Ycoord = 0;

	/**
	 * Constructor
	 * @param img Sprite image
	 * @param frameW width of the sprite
	 * @param frameH height of the sprite
	 */
	public Sprite(Context context, Bitmap img, int frameW, int frameH){
		Logger.log(Logger.DEBUG, "constructor");
		this.m_Image = img;
		this.m_FrameWidth = frameW;
		this.m_FrameHeight = frameH;
			
		initSequences();
	}
	
	/**
	 * Set the x coordinate of the sprite
	 * @param x The x coordinate of the sprite
	 */
	public void setX(int x){
		this.m_Xcoord = x;
	}
	
	/**
	 * Set the y coordinate of the sprite
	 * @param y The y coordinate of the sprite
	 */
	public void setY(int y){
		this.m_Ycoord = y;
	}
	
	/**
	 * Initialize the frame sequences.
	 */
	private void initSequences(){
		this.m_TotalFrames = (m_Image.getWidth() * m_Image.getHeight()) / (m_FrameWidth * m_FrameHeight);
		
		this.m_OffXSeq = new int[m_TotalFrames];
		this.m_OffYSeq = new int[m_TotalFrames];
		
		int seqno = 0;
		
        for (int row = 0; row < m_Image.getHeight() / m_FrameHeight; row++)
        {
            for (int col = 0; col < m_Image.getWidth() / m_FrameWidth; col++)
            {
                m_OffXSeq[seqno] = col * m_FrameWidth;

                m_OffYSeq[seqno] = row * m_FrameHeight;

                seqno++;
            }
        }
	}
	
	/**
	 * Move to the next frame.
	 */
	public void nextFrame(){
		m_CurFrame = (m_CurFrame + 1) % m_TotalFrames;
	}
	
	/**
	 * Move to the previous frame.
	 */
	public void prevFrame(){
		m_CurFrame--;
		if(m_CurFrame < 0)
            m_CurFrame = m_TotalFrames - 1;
	}
	
	/**
	 * Draw the sprite
	 * @param canvas The canvas on which the sprite is to be drawn.
	 */
	public void draw(Canvas canvas){
		
		int offX = m_OffXSeq[m_CurFrame];
		int offY = m_OffYSeq[m_CurFrame];

		Bitmap imgToDraw = Bitmap.createBitmap(m_Image, offX, offY, m_FrameWidth, m_FrameHeight);
		BitmapDrawable bDrawable = new BitmapDrawable(imgToDraw);
		bDrawable.setBounds(m_Xcoord, m_Ycoord, m_Xcoord + m_FrameWidth, m_Ycoord + m_FrameHeight);
		Logger.log(Logger.DEBUG, "drawing sprite at coord (" + m_Xcoord + "," + m_Ycoord + ")");
		bDrawable.draw(canvas);
	}
}
