package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WorkWithUsView extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.work_with_us);
        
        Button workWithUsButton = (Button) this.findViewById(R.id.work_with_us_button);
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
}
