package geoplicity.cooltour.map;

/**
 * This class implements a tile
 * @author jessieyu
 *
 */

/**
 * This class implements a tile. 
 */
public class Tile {
	
	public static String dir;		//base tile directory
	public static String prefix;	//tile prefix name
	public static String type;		//tile image type, e.g., "jpg"
	public static int width;		//tile width
	public static int height;		//tile height
	
	public int row;			//row this tile corresponds to
	public int col;			//column this tile corresponds to

	public boolean equals(Tile tile){
		return (tile.col == this.col) && (tile.row == this.row);
	}

}
