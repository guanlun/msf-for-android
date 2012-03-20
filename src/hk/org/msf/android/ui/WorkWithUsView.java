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

public class WorkWithUsView extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.work_with_us);
        
        ImageView workWithUsViewImage = (ImageView)findViewById(R.id.work_with_us_view_image);
        
        MySettings settings = MySettings.getMySettings();
        String langPref = settings.getLangPref();
        
        if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
        	workWithUsViewImage.setImageResource(R.drawable.logo_cn);
        } else {
        	workWithUsViewImage.setImageResource(R.drawable.logo_hk);
        }
        
        TextView workWithUsButton = (TextView) this.findViewById(R.id.work_with_us_button);
        workWithUsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String workWithUsUrl = getApplicationContext().getResources()
                        .getString(R.string.work_with_us_url);
                Intent intent = new Intent(WorkWithUsView.this, WebViewDisplay.class);
                intent.putExtra("url", workWithUsUrl);
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
