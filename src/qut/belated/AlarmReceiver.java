package qut.belated;

import java.util.Locale;

import qut.belated.helpers.LocationHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("AlarmReceiver", "Received a periodic alarm.");
		
		updateLocationIfRequired(context);
	}
	
	private void updateLocationIfRequired(Context context)
	{
		if (meetingSoon())
		{
			LocationHelper.requestBackgroundLocationUpdate(context);
			showLocationRequestedToast(context);	
		}
	}
	
	private boolean meetingSoon()
	{
		return true;
	}
	
	private static void showLocationRequestedToast(Context context)
	{
		MainActivity.showToast("Determining location via " + LocationHelper.getActiveLocationProvider().toUpperCase(Locale.US) + ".");
	}
}
