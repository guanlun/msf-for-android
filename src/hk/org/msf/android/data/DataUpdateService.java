package hk.org.msf.android.data;

import android.app.IntentService;
import android.content.Intent;

public class DataUpdateService extends IntentService {

	public DataUpdateService() {
		super("Data Updater");
	}
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		
		DataUpdater updater = new DataUpdater(this);
		
		try {
			if(DataUpdater.isOnline(getApplicationContext())) {
				updater.updateAll(); //start to update
			} else { //if the user is offline, display the splash screen for 2 seconds without updating
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
			}
			//SplashScreen.handler.sendEmptyMessage(0);
		} catch(Exception e) {
			
		}
	}
}
