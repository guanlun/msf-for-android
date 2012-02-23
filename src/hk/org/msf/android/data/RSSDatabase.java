package hk.org.msf.android.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RSSDatabase {

	private SQLiteDatabase sqliteDB;
	private final RSSDatabaseHelper mDbHelper;
	private static RSSDatabase instance;
	
	private ArrayList<RSSEntry> newsEntryList;
	private ArrayList<RSSEntry> blogEntryList;
	private ArrayList<RSSEntry> imageEntryList;
	private ArrayList<RSSEntry> videoEntryList;
	
	private boolean newsUpdateFinished = false;
	private boolean blogUpdateFinished = false;
	private boolean imageUpdateFinished = false;
	private boolean videoUpdateFinished = false;
	
	public final static String DB_NAME = "rssdatabase";
	
	private RSSDatabase(Context mContext) {
		
		mDbHelper = new RSSDatabaseHelper(mContext, DB_NAME, null, 1);
		sqliteDB = mDbHelper.getWritableDatabase();
	}
	
	/**
	 * Get a database instance to manipulate on it:
	 * @param Context used to build up the database
	 * @return an instance of the singleton database
	 */
	public static synchronized RSSDatabase getDatabaseInstance(Context mContext) {
		if(instance == null) {
			instance = new RSSDatabase(mContext);
		}
		return instance;
	}
	
	/**
	 * Insert an entry to the database
	 * @param an entry of one of the EntryTypes
	 */
	public void insertRSSEntry(RSSEntry entry) {
		/**
		 * parse the data because the format is not proper to be displayed on cell phones:
		 */
		ContentValues cv = new ContentValues();
		cv.put(RSSDatabaseHelper.KEY_ENTRY_TYPE, entry.entry_type);
		cv.put(RSSDatabaseHelper.KEY_TITLE, entry.title);
		cv.put(RSSDatabaseHelper.KEY_CONTENT, entry.content);
		cv.put(RSSDatabaseHelper.KEY_DATE, entry.date);
		cv.put(RSSDatabaseHelper.KEY_DATA_URL, entry.url);
		cv.put(RSSDatabaseHelper.KEY_OTHER_INFO, entry.otherInfo);
		cv.put(RSSDatabaseHelper.KEY_IMAGE, entry.image);
		
		sqliteDB.insert(RSSDatabaseHelper.KEY_TABLE_NAME, "", cv);
	}
	
	/**
	 * update all the ArrayLists that store the data
	 */
	public void refreshAllEntryLists() {
		refreshEntryList(RSSDatabaseHelper.NEWS);
		refreshEntryList(RSSDatabaseHelper.BLOG);
		refreshEntryList(RSSDatabaseHelper.IMAGE);
		refreshEntryList(RSSDatabaseHelper.VIDEO);
	}
	
	/**
	 * Update an specified ArrayList
	 * This method is called TWICE for each entry type in each start
	 * @param entryType
	 */
	public void refreshEntryList(String entryType) {
		if(entryType.equals(RSSDatabaseHelper.NEWS)) {
			newsEntryList = prepareRSSEntryList(RSSDatabaseHelper.NEWS);
			if (newsEntryList.size() == 0) {
				newsUpdateFinished = false;
			} else {
				newsUpdateFinished = true;
			}
		} else if(entryType.equals(RSSDatabaseHelper.BLOG)) {
			blogEntryList = prepareRSSEntryList(RSSDatabaseHelper.BLOG);
			if (blogEntryList.size() == 0) {
				blogUpdateFinished = false;
			} else {
				blogUpdateFinished = true;
			}
		} else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
			imageEntryList = prepareRSSEntryList(RSSDatabaseHelper.IMAGE);
			if (imageEntryList.size() == 0) {
				imageUpdateFinished = false;
			} else {
				imageUpdateFinished = true;
			}
		} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
			videoEntryList = prepareRSSEntryList(RSSDatabaseHelper.VIDEO);
			if (videoEntryList.size() == 0) {
				videoUpdateFinished = false;
			} else {
				videoUpdateFinished = true;
			}
		}
	}
	
	/**
	 * This function gets an ArrayList of specified entryType from the database
	 * @param a specified entryType used to select entries from the database
	 * @return an ArrayList containing all the entries of the specified type
	 */
	public ArrayList<RSSEntry> getRSSEntryList(String entryType) {
		if(entryType.equals(RSSDatabaseHelper.NEWS)) {
			return newsEntryList;
		} else if(entryType.equals(RSSDatabaseHelper.BLOG)) {
			return blogEntryList;
		} else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
			return imageEntryList;
		} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
			return videoEntryList;
		} else {
			return null;
		}
	}
	
	public boolean updateFinished(String entryType) {
		if(entryType.equals(RSSDatabaseHelper.NEWS)) {
			return newsUpdateFinished;
		} else if(entryType.equals(RSSDatabaseHelper.BLOG)) {
			return blogUpdateFinished;
		} else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
			return imageUpdateFinished;
		} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
			return videoUpdateFinished;
		} else {
			return false;
		}
	}
	
	/**
	 * Get the entryLists ready for retrieving, load the data from the database to the ArrayLists:
	 * This method is called TWICE for each entry type in each start
	 * @param the specified type of the entry
	 * @return the ArrayList that contains the data of the specified type
	 */
	private ArrayList<RSSEntry> prepareRSSEntryList(String entryType) {
		ArrayList<RSSEntry> entryList = new ArrayList<RSSEntry>();
		
		String selection = RSSDatabaseHelper.KEY_ENTRY_TYPE + " = ?";
		String [] selectionArgs = {entryType};
		
		Cursor c = sqliteDB.query(RSSDatabaseHelper.KEY_TABLE_NAME, null, 
				selection, selectionArgs, null, null, null);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			entryList.add(new RSSEntry(c.getString(1), c.getString(2), c.getString(3), 
					c.getString(4), c.getString(5), c.getString(6), c.getString(7)));
		}
		c.close();

		return entryList;
	}
	

	/**
	 * Clear a specified column of the database, i.e. clear all entries of a EntryType
	 * @param a specified entryType used to delete entries from the database
	 */
	public void clearDataColumn(String entryType) {
		sqliteDB.delete(RSSDatabaseHelper.KEY_TABLE_NAME, 
				RSSDatabaseHelper.KEY_ENTRY_TYPE + "='" + entryType + "'", null);
	}
	
	/**
	 * Clear all the information in the database
	 */
	public void clearEntireDatabase() {
        clearDataColumn(RSSDatabaseHelper.NEWS);
        clearDataColumn(RSSDatabaseHelper.BLOG);
        clearDataColumn(RSSDatabaseHelper.IMAGE);
        clearDataColumn(RSSDatabaseHelper.VIDEO);
	}
	
	/**
	 * Close the database
	 */
	public void close() {
		mDbHelper.close();
	}
}

