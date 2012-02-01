package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.utils.MySettings;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutUs extends Activity {
    
    private LinearLayout charterLayout;
    private TextView charterTitleText;
    
    private Button extendCharterButton;
    private Button donationButton;
    private Button workWithUsButton;
    private Button gotoWebsiteButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        
        ImageView aboutUsImage = (ImageView)findViewById(R.id.about_us_image);
        
        MySettings settings = MySettings.getMySettings();
        String langPref = settings.getLangPref();
        
        if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
            aboutUsImage.setImageResource(R.drawable.logo_cn);
        } else {
            aboutUsImage.setImageResource(R.drawable.logo_hk);
        }
        
        charterLayout = (LinearLayout)findViewById(R.id.charter_layout);
        charterTitleText = (TextView)findViewById(R.id.chater_title);
        
        extendCharterButton = (Button)findViewById(R.id.charter_extend_button);
        donationButton = (Button)findViewById(R.id.donation_button);
        workWithUsButton = (Button)findViewById(R.id.work_with_us_button);
        gotoWebsiteButton = (Button)findViewById(R.id.goto_website_button);
        
        extendCharterButton.getBackground().setAlpha(50);
        donationButton.getBackground().setAlpha(50);
        workWithUsButton.getBackground().setAlpha(50);
        gotoWebsiteButton.getBackground().setAlpha(50);
        
        final String charterTitleExtended = getApplicationContext().getResources().getString(R.string.charter_content0);
        final String charterContentText1 = getApplicationContext().getResources().getString(R.string.charter_content1);
        final String charterContentText2 = getApplicationContext().getResources().getString(R.string.charter_content2);
        final String charterContentText3 = getApplicationContext().getResources().getString(R.string.charter_content3);
        final String charterContentText4 = getApplicationContext().getResources().getString(R.string.charter_content4);
        
        extendCharterButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                charterTitleText.setText(charterTitleExtended);
                
                TextView charterContent1 = new TextView(getApplicationContext());
                TextView charterContent2 = new TextView(getApplicationContext());
                TextView charterContent3 = new TextView(getApplicationContext());
                TextView charterContent4 = new TextView(getApplicationContext());
                
                charterContent1.setText(charterContentText1);
                charterContent2.setText(charterContentText2);
                charterContent3.setText(charterContentText3);
                charterContent4.setText(charterContentText4);
                
                charterContent1.setTextSize(16f);
                charterContent2.setTextSize(16f);
                charterContent3.setTextSize(16f);
                charterContent4.setTextSize(16f);
                
                charterContent1.setTextColor(Color.rgb(50, 50, 50));
                charterContent2.setTextColor(Color.rgb(50, 50, 50));
                charterContent3.setTextColor(Color.rgb(50, 50, 50));
                charterContent4.setTextColor(Color.rgb(50, 50, 50));
                
                charterContent1.setPadding(30, 10, 30, 10);
                charterContent2.setPadding(30, 10, 30, 10);
                charterContent3.setPadding(30, 10, 30, 10);
                charterContent4.setPadding(30, 10, 30, 10);
                
                charterLayout.removeAllViews();
                charterLayout.addView(charterTitleText);
                charterLayout.addView(charterContent1);
                charterLayout.addView(charterContent2);
                charterLayout.addView(charterContent3);
                charterLayout.addView(charterContent4);
            }
        });
        
        donationButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String donateUrl = getApplicationContext().getResources()
                        .getString(R.string.donate_url);
                Intent intent = new Intent(AboutUs.this, WebViewDisplay.class);
                intent.putExtra("url", donateUrl);
                startActivity(intent);
            }
        });
        
        workWithUsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String workWithUsUrl = getApplicationContext().getResources()
                        .getString(R.string.work_with_us_url);
                Intent intent = new Intent(AboutUs.this, WebViewDisplay.class);
                intent.putExtra("url", workWithUsUrl);
                startActivity(intent);
            }
        });
        
        gotoWebsiteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String HomePageUrl = getApplicationContext().getResources()
                        .getString(R.string.homepage_url);
                Intent intent = new Intent(AboutUs.this, WebViewDisplay.class);
                intent.putExtra("url", HomePageUrl);
                startActivity(intent);
            }
        });
    }
}
