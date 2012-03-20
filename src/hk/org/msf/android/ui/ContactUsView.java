package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.utils.MySettings;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class ContactUsView extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.contact_us);
        
        ImageView aboutAppViewImage = (ImageView)findViewById(R.id.contact_us_view_image);
        
        MySettings settings = MySettings.getMySettings();
        String langPref = settings.getLangPref();
        
        if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
        	aboutAppViewImage.setImageResource(R.drawable.logo_cn);
        } else {
        	aboutAppViewImage.setImageResource(R.drawable.logo_hk);
        }
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
		this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
