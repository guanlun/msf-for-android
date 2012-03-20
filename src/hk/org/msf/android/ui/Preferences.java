package hk.org.msf.android.ui;
 
import hk.org.msf.android.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
 
public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.preferences);
    }
    
    /**
     * After setting the preferences, restart the application because the language settings
     * might have been changed, get the data in a difference language:
     */
    @Override
    public void onBackPressed() {
    	this.finish();
    	Intent intent = new Intent(Preferences.this, SplashScreen.class);
    	startActivity(intent);
    }
    
}
