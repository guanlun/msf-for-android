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

public class MSFView extends Activity {
	
	private Activity self;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		self = this;
		
		this.setContentView(R.layout.msf_view);
		
        ImageView msfViewImage = (ImageView)findViewById(R.id.msf_view_image);
        
        MySettings settings = MySettings.getMySettings();
        String langPref = settings.getLangPref();
        
        if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
            msfViewImage.setImageResource(R.drawable.logo_cn);
        } else {
            msfViewImage.setImageResource(R.drawable.logo_hk);
        }
        
        TextView aboutUsButton = (TextView) this.findViewById(R.id.msf_view_about_us);
        aboutUsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MSFView.this, AboutUsView.class);
				startActivity(intent);
				self.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
        });
        
        TextView donateButton = (TextView) this.findViewById(R.id.msf_view_donate);
        donateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MSFView.this, DonateView.class);
				startActivity(intent);
				self.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
        });
        
        TextView workWithUsButton = (TextView) this.findViewById(R.id.msf_view_work_with_us);
        workWithUsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MSFView.this, WorkWithUsView.class);
				startActivity(intent);
				self.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
        });
        
        TextView websiteButton = (TextView) this.findViewById(R.id.msf_view_contact_us);
        websiteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				String HomePageUrl = getApplicationContext().getResources()
                        .getString(R.string.homepage_url);
                Intent intent = new Intent(MSFView.this, WebViewDisplay.class);
                intent.putExtra("url", HomePageUrl);
                */
				Intent intent = new Intent(MSFView.this, ContactUsView.class);
                startActivity(intent);	
				self.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
        });
        
        TextView aboutAppButton = (TextView) this.findViewById(R.id.msf_view_about_app);
        aboutAppButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MSFView.this, AboutAppView.class);
				startActivity(intent);
				self.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
        });
	}
	
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
		this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
}
