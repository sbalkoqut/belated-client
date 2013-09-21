package qut.belated;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@SuppressLint("Wakelock") // We intend to releasing it in BackgroundLocationService.
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("AlarmReceiver", "Received a periodic alarm.");
		
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			
		Intent locationChangedNotificationIntent = new Intent(context, BackgroundLocationService.class);
		PendingIntent locationChangedIntent = PendingIntent.getService(context, 0, locationChangedNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
		locationManager.removeUpdates(locationChangedIntent);
		
		if (meetingSoon())
		{
			//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10 * 1000, locationChangedIntent);
			//NETWORK_PROVIDER GPS_PROVIDER
			locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationChangedIntent);
			
			if (MainActivity.inForeground)
			{
				Toast.makeText(context, "Attempting to determine location.", Toast.LENGTH_SHORT).show();
			}
			
			if (BackgroundLocationService.wakeLock == null)
			{
				Log.v("AlarmReceiver", "Getting a wake lock for 60 seconds.");
				PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock lock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationChangedReceiver");
				lock.acquire(60 * 1000);
				BackgroundLocationService.wakeLock = lock;
			}
		}
	}
	
	private boolean meetingSoon()
	{
		return true;
	}
}
