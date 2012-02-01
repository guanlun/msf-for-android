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
	private static final MySettings _instance = new MySettings();
    
	private String langPref;
	
	private MySettings() {
		// empty
	}
	
	/**
	 * Method to get a reference to the single instance (object) of the class that is created
	 * @return the setting instance
	 */
	public static synchronized MySettings getMySettings() {
		
		return _instance;
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
	
	/**
	 * Get the language preference
	 * @return the language preference string
	 */
	public String getLangPref() {
		return langPref;
	}
	
	/**
	 * Since YouTube is not available in mainland China, when the language preference is 
	 * simplified Chinese, set Youku as the video ource site, otherwise set YouTube as it.
	 * 
	 * In fact, we think this part should be changed because people using simplified Chinese
	 * may not be in mainland China. And most of them have no difficulty at all to read 
	 * traditional Chinese on the Youtube website. We did this way because this is the MSF's 
	 * regulation. If you are going to change the code please reconsider this problem.
	 * 
	 * @return the string representing the video source site
	 */
	public String getVideoSource() {
		if(langPref.equals(SIMPLIFIED_CHINESE)) {
			return YOUKU;
		} else {
			return YOUTUBE;
		}
	}
}
