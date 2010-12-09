package geoplicity.cooltour.util;

public class Utilities {
	/**
	 * Convert bytes into a human readable string.
	 * e.g. 2000000 would yield '1.9MB'
	 * @param bytes
	 * @return
	 */
	public static String parseBytesToHumanString(double bytes) {
		if (bytes > 1000000) {
			bytes = bytes/1048576;
			bytes = round(bytes,2);
			return Double.toString(bytes)+"MB";
		}
		else if (bytes > 1000) {
			bytes = bytes/1024;
			bytes = round(bytes,2);
			return Double.toString(bytes)+"KB";
		} 
		else {
			return Double.toString(bytes)+"B";
		}
	}
	/**
	 * Rounds the given long to 
	 * @param Rval
	 * @param Rpl
	 * @return
	 */
	public static double round(double value, int places) {
		double p = Math.pow(10,places);
		value = value * p;
		double tmp = Math.round(value);
		return tmp/p;
	}
}
