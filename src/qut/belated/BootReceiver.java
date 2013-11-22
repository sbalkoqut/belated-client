package qut.belated;

import qut.belated.helpers.AlarmHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent bootIntent) {
		Log.v("BootReceiver", "Device booted.");
		
		setupBackgroundLocationUpdates(context);
	}
	
	private void setupBackgroundLocationUpdates(Context context)
	{
		BelatedPreferences preferences = new BelatedPreferences(context);
		
		AlarmHelper.setAlarms(context, preferences.isServiceEnabled());
	}

}
