package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.utils.MySettings;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MSFView extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.msf_view);
		
        ImageView msfViewImage = (ImageView)findViewById(R.id.msf_view_image);
        
        MySettings settings = MySettings.getMySettings();
        String langPref = settings.getLangPref();
        
        if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
            msfViewImage.setImageResource(R.drawable.logo_cn);
        } else {
            msfViewImage.setImageResource(R.drawable.logo_hk);
        }
        
        Button aboutUsButton = (Button) this.findViewById(R.id.msf_view_about_us);
        aboutUsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MSFView.this, AboutUsView.class);
				startActivity(intent);
			}
        });
        
        Button donateButton = (Button) this.findViewById(R.id.msf_view_donate);
        donateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MSFView.this, DonateView.class);
				startActivity(intent);
			}
        });
        
        Button workWithUsButton = (Button) this.findViewById(R.id.msf_view_work_with_us);
        workWithUsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MSFView.this, WorkWithUsView.class);
				startActivity(intent);
			}
        });
        
        Button websiteButton = (Button) this.findViewById(R.id.msf_view_website);
        websiteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String HomePageUrl = getApplicationContext().getResources()
                        .getString(R.string.homepage_url);
                Intent intent = new Intent(MSFView.this, WebViewDisplay.class);
                intent.putExtra("url", HomePageUrl);
                startActivity(intent);	
			}
        });
	}
}
