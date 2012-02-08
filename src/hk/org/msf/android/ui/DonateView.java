package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DonateView extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.donate);
        
        Button donateButton = (Button) this.findViewById(R.id.donate_button);
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
}
