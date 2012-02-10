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
		updater.startUpdate();
	}
}