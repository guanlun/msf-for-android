package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.data.DataUpdateService;
import hk.org.msf.android.data.DataUpdater;
import hk.org.msf.android.utils.MySettings;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends Activity {
	
	private static SplashScreen self;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		self = this;
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		/**
		 * Get the reference to the settings object from the singleton
		 */
		MySettings settings = MySettings.getMySettings();
		settings.configurePrefs(getApplicationContext());
		String langPref = settings.getLangPref();
		
		/**
		 * set the application language according to the class MySettings:
		 */
		Configuration config = new Configuration();
		
		if(langPref.equals(MySettings.ENGLISH)) {
			config.locale = Locale.ENGLISH;
		} else if(langPref.equals(MySettings.TRADITIONAL_CHINESE)) {
			config.locale = Locale.TAIWAN;
		} else if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
			config.locale = Locale.CHINA;
		}
		
		this.getBaseContext().getResources().updateConfiguration(config, 
				getBaseContext().getResources().getDisplayMetrics());

		this.setContentView(R.layout.splash);
		ImageView iv = (ImageView)findViewById(R.id.splash_image);
		
		/**
		 * get application language(this is only used for the splash image):
		 */
		String lang = this.getResources().getConfiguration().locale.getDisplayName();
		
		/**
		 * set splash image according to the application language:
		 */
		if(lang.equals("中文 (中国)") || lang.equals("Chinese (China)") 
				|| lang.equals("中文 (中華人民共和國)")) {
			iv.setImageResource(R.drawable.logo_cn);
		} else {
			iv.setImageResource(R.drawable.logo_hk);
		}
		
		showFlashingLoadingMessage();
		
		/**
		 * start the service to update database
		 */
		Intent intent = new Intent(this, DataUpdateService.class);
		this.startService(intent);

		Message msg = new Message();
		msg.what = 0;

		int delay = 3000; // Delay Time in ms
		handler.sendMessageDelayed(msg, delay);
	}
	
	/**
	 * Display the loading message with animation:
	 */
	private void showFlashingLoadingMessage() {
		
		final TextView loadingMsg = (TextView)findViewById(R.id.loading_msg);
		
		if(DataUpdater.isOnline(getApplicationContext())) {
			final Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
			final Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
			
			fadeIn.setAnimationListener(new AnimationListenerAdapter() {
				@Override
				public void onAnimationEnd(Animation anim) {
					loadingMsg.startAnimation(fadeOut);
				}
			});
			
			fadeOut.setAnimationListener(new AnimationListenerAdapter() {
				@Override
				public void onAnimationEnd(Animation anim) {
					loadingMsg.startAnimation(fadeIn);
				}
			});
			
			loadingMsg.startAnimation(fadeIn);
		} else {
			loadingMsg.setVisibility(View.INVISIBLE);
		}
	}

	public static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent i = new Intent(SplashScreen.self, MainMenu.class);
			self.startActivity(i);
			self.finish();
			self.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	};
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		handler.removeMessages(0);
		Message msg = new Message();
		msg.what = 0;
		handler.sendMessage(msg);
	}
	
	/**
	 * This class is used as a syntactical sugar for AnimationListener
	 */
	class AnimationListenerAdapter implements AnimationListener {
		@Override
		public void onAnimationEnd(Animation arg0) {}
		@Override
		public void onAnimationRepeat(Animation arg0) {}
		@Override
		public void onAnimationStart(Animation arg0) {}
	}
}
