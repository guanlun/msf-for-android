package hk.org.msf.android.utils;

import hk.org.msf.android.R;
import hk.org.msf.android.data.RSSDatabase;
import hk.org.msf.android.data.RSSDatabaseHelper;
import hk.org.msf.android.data.RSSEntry;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

/**
 * This class is used to parse the html pages on Youku, since it does not provide RSS feeds.
 */
public class YoukuHTMLParser {
	/**
	 * Used to break the connection limit that Youku has for java:
	 */
	public static final String fakeEnv = "Mozilla 5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.11) ";
	
	/**
	 * Used to match the useful info:
	 */
	private static final String pattern1 = "<li class=\"videoImg\"><a href=\"(.*)\" " +
			"target=\"_blank\"><img src=\"(.*)\" alt=\"(.*)\" title";
	private static final String pattern2 = "<li><span class=\"f2nd\">发布:</span> <span class=\"num\">(.*)</span>";
	
	/**
	 * Parse the Youku HTML file to get the feeds
	 * @param context the context used for getting the resources
	 */
	public static void parseHTML(Context context) {

		RSSDatabase db = RSSDatabase.getDatabaseInstance(null);
		String site = context.getResources().getString(R.string.youku_site);
		
		try {
			URL u = new URL(site);
			URLConnection urlConn = u.openConnection();
			urlConn.setRequestProperty("User-Agent", fakeEnv);
			
			InputStream is = urlConn.getInputStream();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
			
			String line = new String();
			
			/**
			 * Since the main part is not in the same line as the date, use two patterns to match it:
			 */
			Pattern re1 = Pattern.compile(pattern1);
			Pattern re2 = Pattern.compile(pattern2);
			
			String url = null;
			String image = null;
			String title = null;
			String date = null;
			
			while((line = toUTF8(dis.readLine())) != null) {
		          Matcher m1 = re1.matcher(line);
		          Matcher m2 = re2.matcher(line);
		          if(m1.find()) {
					url = m1.group(1);
					image = m1.group(2);
					title = m1.group(3);
	        }
          
          /**
           * After finding the date, insert the whole entry to the database:
           */
          if(m2.find()) {
            date = m2.group(1);
            db.insertRSSEntry(new RSSEntry(RSSDatabaseHelper.VIDEO, parseVideoTitle(title), url, 
                date, url, "", image));
          }
        }

      } catch (Exception e) {}
	}
	
	/**
	 * Convert the encoding type to UTF-8
	 * @param str string in other form of encoding
	 * @return string converted to encoding type UTF-8
	 */
    public static String toUTF8(String str) throws Exception {
      return new String(str.getBytes("ISO-8859-1"),"UTF-8");
    }
    
    /**
     * Some of the video titles contains "&quot;", which represents the quotation mark,
     * convert this to the quotation mark
     * @param the title of the video
     * @return the parsed title with "&quot;" replaced by quotation mark
     */
	private static String parseVideoTitle(String title) {
		return title.replace("&quot;", "\"");
	}
}
