package geoplicity.cooltour.sites;

/**
 * Exception thrown if the way properties file cannot be found on the SD card
 */
public class NoWayPropsException extends Exception {
	
	public NoWayPropsException(String exceptionMessage){
		super(exceptionMessage);
	}
}
