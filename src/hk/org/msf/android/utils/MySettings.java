package hk.org.msf.android.utils;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MySettings {
	
	public static final String LANG_KEY = "langPref";
	public static final String VIDEO_KEY = "video_source";
	public static final String ENGLISH = "en";
	public static final String TRADITIONAL_CHINESE = "tc";
	public static final String SIMPLIFIED_CHINESE = "sc";
	public static final String YOUTUBE = "YouTube";
	public static final String YOUKU = "Youku";

    /**
     * Create one instance of the class
     */
	private static final MySettings instance = new MySettings();
    
	private String langPref;
	
	private MySettings() {
	}
	
	/**
	 * Method to get a reference to the single instance (object) of the class that is created
	 * @return the setting instance
	 */
	public static synchronized MySettings getMySettings() {
		return instance;
    }

	/**
	 * Initialize the user preferences from the saved SharedPreferences
	 * @param mContext the context used for the preference settings
	 */
	public void configurePrefs(Context mcontext) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
		
		Locale sysLocale = mcontext.getResources().getConfiguration().locale;
		
		if(sysLocale.equals(Locale.ENGLISH)) {
			langPref = prefs.getString(LANG_KEY, ENGLISH);
		} else if(sysLocale.equals(Locale.TAIWAN)) {
			langPref = prefs.getString(LANG_KEY, TRADITIONAL_CHINESE);
		} else if(sysLocale.equals(Locale.CHINA)) {
			langPref = prefs.getString(LANG_KEY, SIMPLIFIED_CHINESE);
		} else {
			langPref = prefs.getString(LANG_KEY, ENGLISH);
		}
		
		//set the preferences when the application is started for the first time:
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(LANG_KEY, langPref);
		//editor.putString(VIDEO_KEY, videoSource);
		
		editor.commit();
	}
	
	public String getLangPref() {
		return langPref;
	}
	
	public String getVideoSource() {
		if(langPref.equals(SIMPLIFIED_CHINESE)) {
			return YOUKU;
		} else {
			return YOUTUBE;
		}
	}
}
