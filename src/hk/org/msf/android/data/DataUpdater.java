package hk.org.msf.android.data;

import hk.org.msf.android.utils.AboutUsParser;
import hk.org.msf.android.utils.MySettings;
import hk.org.msf.android.utils.XMLParser;
import hk.org.msf.android.utils.YoukuHTMLParser;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DataUpdater {

	private Context mContext;
	
	private RSSDatabase db;
	
	private boolean online;
	
	/**Constructor
	 * @param Context
	 */
	public DataUpdater(Context context) {
		
        mContext = context;

        /**
         * initialize the database, get the entryLists ready with the data of the last time inserted to it;
         */
		db = RSSDatabase.getDatabaseInstance(mContext);
		db.refreshAllEntryLists();
		
		/**
		 * if online, now we can clear the database and put new data in:
		 * we don't need to worry about the data because they are now stored in the lists:
		 */
		online = isOnline(mContext);
		if(online) {
			db.clearEntireDatabase();
		}
	}

    /**
     * update all the date from the web, store them into the database:
     */
    public void updateAll() {
    	try {
    		AboutUsParser.updateAboutUs(mContext);
    		
    		updateData(RSSDatabaseHelper.NEWS);
    		db.refreshEntryList(RSSDatabaseHelper.NEWS);
            
	    	updateData(RSSDatabaseHelper.BLOG);
	    	db.refreshEntryList(RSSDatabaseHelper.BLOG);
            
    		updateData(RSSDatabaseHelper.IMAGE);
    		db.refreshEntryList(RSSDatabaseHelper.IMAGE);
            
    		updateData(RSSDatabaseHelper.VIDEO);
	    	db.refreshEntryList(RSSDatabaseHelper.VIDEO);
    	} catch(Exception e) {}
    }
		
    /**
     * update a specified type of data:
     */
    public void updateData(String entryType) {
    	try{
	    	if(entryType.equals(RSSDatabaseHelper.NEWS)) { //news
	    		XMLParser.parseRSSFeeds(mContext, RSSDatabaseHelper.NEWS);
	    		
	    	} else if(entryType.equals(RSSDatabaseHelper.BLOG)) { // blogs
	    		XMLParser.parseRSSFeeds(mContext, RSSDatabaseHelper.BLOG);
	    		
	    	} else if(entryType.equals(RSSDatabaseHelper.IMAGE)) { //images
	    		XMLParser.parseRSSFeeds(mContext, RSSDatabaseHelper.IMAGE);
	    		
	    	} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
	    		
	    		String videoResource = MySettings.getMySettings().getVideoSource();
	    		
	    		if(videoResource.equals(MySettings.YOUTUBE)) { //youtube video
	    			XMLParser.parseRSSFeeds(mContext, RSSDatabaseHelper.VIDEO);
	    		} else if(videoResource.equals(MySettings.YOUKU)) { //youku video
	    			YoukuHTMLParser.parseHTML(mContext);
	    		}
	    	}
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    /**
     * Checks whether the user has access to the Internet:
     * @param context the context used to obtain the parameters
     * @return true if online, false if not
     */
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return wifi.isConnectedOrConnecting() || mobile.isConnectedOrConnecting();
	}
}
