package geoplicity.cooltour.sites;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.geoplicity.mobile.util.Logger;

public class SiteListCreator extends Properties {
	
	private static final String STORAGE_DEVICE = "/sdcard/";
	private FileInputStream fis;
	
	public Set getSiteChoices() throws NoSitePropsException{
		try {
			FileInputStream fis = new FileInputStream(new File(STORAGE_DEVICE+"site-props.txt"));
			load(fis);
			return keySet();
		}
		catch(FileNotFoundException noSiteProps){
			Logger.log(Logger.TRAP, "Unable to read file site-props.txt using path "+
					STORAGE_DEVICE+"site-props.txt\n"+noSiteProps.toString());
			throw new NoSitePropsException("Unable to read file site-props.txt");
		}
		catch(IOException badFileInputStream){
			Logger.log(Logger.TRAP, "Unable to open input stream to file site-props.txt "+
					"using path "+STORAGE_DEVICE+"site-props.txt\n"+badFileInputStream.toString());
			throw new NoSitePropsException("Unable to open input stream to site-props.txt");
		}
		finally {
			try { fis.close();}
			catch(IOException badFileInputStream){
				Logger.log(Logger.TRAP, "Unable to open input stream to file site-props.txt "+
						"using path "+STORAGE_DEVICE+"site-props.txt\n"+badFileInputStream.toString());
				throw new NoSitePropsException("Unable to open input stream to site-props.txt");
			}
		}
	}

}
