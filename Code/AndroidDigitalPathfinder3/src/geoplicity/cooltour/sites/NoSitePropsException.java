package geoplicity.cooltour.sites;

/**
 * Exception thrown if the site properties file cannot be found on the SD card
 */
public class NoSitePropsException extends Exception {
	
	public NoSitePropsException(String exceptionMessage){
		super(exceptionMessage);
	}
}
