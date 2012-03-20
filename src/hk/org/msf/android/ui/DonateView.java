package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.utils.MySettings;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DonateView extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.donate);
        
        ImageView DonateViewImage = (ImageView)findViewById(R.id.donate_view_image);
        
        MySettings settings = MySettings.getMySettings();
        String langPref = settings.getLangPref();
        
        if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
        	DonateViewImage.setImageResource(R.drawable.logo_cn);
        } else {
        	DonateViewImage.setImageResource(R.drawable.logo_hk);
        }
        
        TextView donateButton = (TextView) this.findViewById(R.id.donate_button);
        donateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String donateUrl = getApplicationContext().getResources()
                        .getString(R.string.donate_url);
                Intent intent = new Intent(DonateView.this, WebViewDisplay.class);
                intent.putExtra("url", donateUrl);
                startActivity(intent);				
			}
        });
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
		this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
}
