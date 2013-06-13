package qut.belated;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context c, Intent bootIntent) {
		Log.v("BootReceiver", "Device booted.");
		
		PreferenceHelper preferences = new PreferenceHelper(c);
		
		AlarmHelper.setAlarmState(c, preferences.isServiceEnabled());
	}

}
