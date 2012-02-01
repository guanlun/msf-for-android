package hk.org.msf.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RSSDatabaseHelper extends SQLiteOpenHelper {
	
	public final static String NEWS = "newsEntry";
	public final static String BLOG = "blogEntry";
	public final static String IMAGE = "imageEntry";
	public final static String VIDEO = "videoEntry";
	
	public final static String KEY_TABLE_NAME = "table_name";
	
	/**
	 * constant strings for the columns:
	 */
	public final static String KEY_ENTRY_TYPE = "entry_type";
	public final static String KEY_TITLE = "title";
	public final static String KEY_CONTENT = "content";
	public final static String KEY_DATE = "date";
	public final static String KEY_DATA_URL = "data_url";
	public final static String KEY_OTHER_INFO = "other_info";
	public final static String KEY_IMAGE = "image";
	
	/**
	 * 7 columns in all as is listed above
	 */
	public final static int NUM_OF_COLUMNS = 7;
	
	/**
	 * The SQL code for creating a new SQLite database
	 */
	public final static String sql = "CREATE  TABLE " + KEY_TABLE_NAME + 
			" (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
			KEY_ENTRY_TYPE + " VARCHAR, " +
			KEY_TITLE + " VARCHAR, " +
			KEY_CONTENT + " VARCHAR, " +
			KEY_DATE + " VARCHAR, " +
			KEY_DATA_URL + " VARCHAR, " +
			KEY_OTHER_INFO + " VARCHAR, " +
			KEY_IMAGE + " VARCHAR)";

	public RSSDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS stationgeocode");
        onCreate(db);
	}

}
