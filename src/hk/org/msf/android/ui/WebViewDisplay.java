package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewDisplay extends Activity {

	private WebViewDisplay self;
	private WebView webView;
	private ProgressDialog progress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		webView = new WebView(this);

		this.setContentView(webView);
		
		self = this;
		
		showLoadingMessage();
		new Thread(new LoadPage()).start();
	}
	
	private void showLoadingMessage() {
		progress = ProgressDialog.show(this, "", self.getResources().getString(R.string.loading), true);
		progress.setCancelable(true);
	}
	
	private class LoadPage implements Runnable {
		
		@Override
		public void run() {
			
			Intent intent = self.getIntent();
			String url = null;
			if (intent !=null && intent.getExtras()!=null) {
				url = intent.getExtras().getString("url");
			}
			
			WebSettings settings = webView.getSettings();
			settings.setSupportZoom(true);
      settings.setJavaScriptEnabled(true);
      settings.setBuiltInZoomControls(true);
      settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
      settings.setAllowFileAccess(true);
      settings.setPluginsEnabled(true);
        
      webView.setWebViewClient(new WebViewClient() {
        @Override
         public boolean shouldOverrideUrlLoading(WebView view, String url) {
           view.loadUrl(url);
           return true;
         }
      });
      
      webView.setWebViewClient(new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
          progress.dismiss();
        }
      });
      
      webView.loadUrl(url);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		webView.destroy();
	}

	@Override
	public void onBackPressed() {
		if(webView.canGoBack()) {
			webView.goBack();
		} else {
			this.finish();
		}
	}
}
