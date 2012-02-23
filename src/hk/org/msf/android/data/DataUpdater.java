package hk.org.msf.android.data;

import hk.org.msf.android.utils.MySettings;
import hk.org.msf.android.utils.XMLParser;
import hk.org.msf.android.utils.YoukuHTMLParser;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class DataUpdater {

	private Context mContext;
	
	private RSSDatabase db;
	
	public DataUpdater(Context context) {
        mContext = context;
		db = RSSDatabase.getDatabaseInstance(mContext);
	}
	
	public void startUpdate() {
		db.refreshAllEntryLists(); // put data into the lists as backup
		if(isOnline(mContext)) {
			db.clearEntireDatabase();
			this.updateAll();
		}
	}

    /**
     * update all the date from the web, store them into the database:
     * This function is called ONCE
     */
    public void updateAll() {
    	try {
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
     * Parse XML got from websites and push data into the database
     * Called once for each entry type
     * @param entryType
     */
    public void updateData(String entryType) {
    	try{
    		if(entryType.equals(RSSDatabaseHelper.NEWS)) {
    			XMLParser.parseRSSFeeds(mContext, RSSDatabaseHelper.NEWS);
    		} else if(entryType.equals(RSSDatabaseHelper.BLOG)) {
    			XMLParser.parseRSSFeeds(mContext, RSSDatabaseHelper.BLOG);
    		} else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
    			XMLParser.parseRSSFeeds(mContext, RSSDatabaseHelper.IMAGE);
	    	} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
	    		String videoResource = MySettings.getMySettings().getVideoSource();
	    		if(videoResource.equals(MySettings.YOUTUBE)) { // youtube video
	    			XMLParser.parseRSSFeeds(mContext, RSSDatabaseHelper.VIDEO);
	    		} else if(videoResource.equals(MySettings.YOUKU)) { // youku video
	    			YoukuHTMLParser.parseHTML(mContext);
	    		}
	    	}
		} catch (Exception e) {}
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
