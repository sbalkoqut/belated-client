package qut.belated.helpers;

import qut.belated.BackgroundLocationService;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class LocationHelper {
	
	private static String locationProvider;
	private static Criteria getLocationCriteria()
	{
		Criteria criteria = new Criteria();
		
		criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setSpeedRequired(false);
		
		return criteria;
	}
	
	private static String getLocationProvider(LocationManager manager)
	{
		Criteria locationCriteria = getLocationCriteria();
		locationProvider = manager.getBestProvider(locationCriteria, false);
		return locationProvider;
	}
	
	private static LocationManager getLocationManager(Context context)
	{
		return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	private static PendingIntent getBackgroundServiceIntent(Context context)
	{
		Intent backgroundLocationServiceIntent = new Intent(context, BackgroundLocationService.class);
		PendingIntent locationServicePendingIntent = PendingIntent.getService(context, 0, backgroundLocationServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return locationServicePendingIntent;
	}
	
	public static void requestBackgroundLocationUpdate(Context context)
	{
		LocationManager managerInstance = getLocationManager(context);
		String locationProvider = getLocationProvider(managerInstance);
		PendingIntent onLocationChanged = getBackgroundServiceIntent(context);
		
		managerInstance.requestSingleUpdate(locationProvider, onLocationChanged);
				
		BackgroundLocationService.acquireWakeLock(context);
	}
	
	public static String getActiveLocationProvider()
	{
		return locationProvider;
	}
	
	public static Location getLastLocation(Context context)
	{
		LocationManager managerInstance = getLocationManager(context);
		String provider = getLocationProvider(managerInstance);
		return managerInstance.getLastKnownLocation(provider);
	}
	
}
