package qut.belated;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;

import qut.belated.helpers.HttpHelper;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class BackgroundLocationService extends IntentService {

	BelatedPreferences preferences;
	Handler uiThread;
	HttpClient client;
	String serviceAddress;
	ArrayList<BasicNameValuePair> locationPostData;
	
	public BackgroundLocationService() {
		super("Sends user position reports for Belated");
		preferences = null;
	}

	public void onCreate()
	{
		super.onCreate();
		uiThread = new Handler();
		preferences = new BelatedPreferences(this);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED))
		{
			Log.v("BackgroundLocationService", "Received a location update.");
			
			Location location = (Location)intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
			lastLocation = location;
			onLocationUpdated();
			sendLastLocation();
		}	
		else if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)
				|| intent.hasExtra(LocationManager.KEY_STATUS_CHANGED))
		{
			Log.v("BackgroundLocationService", "Received other location message.");
		}
		else
			Log.w("BackgroundLocationService", "Recieved unknown intent.");
	
		releaseWakeLock();
	}
	
	private void preparePostData()
	{
		locationPostData= new ArrayList<BasicNameValuePair>();
		locationPostData.add(new BasicNameValuePair("email", preferences.getEmail()));
		locationPostData.add(new BasicNameValuePair("lat", Double.toString(lastLocation.getLatitude())));
		locationPostData.add(new BasicNameValuePair("lng", Double.toString(lastLocation.getLongitude())));
	}
	
	private void sendLastLocation()
	{
		try 
		{
			initialiseHttpClient();
			findServiceAddress();
			preparePostData();
			performHttpPost();
		} 
		catch (IllegalArgumentException e)
		{
			Log.e("BackgroundLocationService", "Service URL Invalid.");
			showToast("Problem whilst sending location (Service IP invalid).");
		}
		catch (ClientProtocolException e) {
			Log.e("BackgroundLocationService", "Client protocol exception on HTTP Post.");
			showToast("Problem whilst sending location (ClientProtocolException).");
		} 
		catch (IOException e) {
			Log.e("BackgroundLocationService", "IO exception on HTTP Post. " + e.getMessage());
			showToast("Problem whilst sending location (IOException).");
		}
	}
	
	private void initialiseHttpClient()
	{
		client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000); 
	}
	
	private void findServiceAddress()
	{
		serviceAddress = HttpHelper.getLocationUrl(preferences.getServiceIP());
	}
	
	private void performHttpPost() throws ClientProtocolException, IOException
	{
		HttpPost httpPost = new HttpPost(serviceAddress);
		
		httpPost.setEntity(new UrlEncodedFormEntity(locationPostData, "UTF-8"));
	
		HttpResponse response = client.execute(httpPost);
		
		int statusCode = response.getStatusLine().getStatusCode();
		Log.v("BackgroundLocationService", "Location submitted, status code: " + statusCode);
		if (statusCode == 200)
			showToast("Location sent succesfully.");
		else
			showToast("Location send got status code " + statusCode);
	}
	
	static Location lastLocation;
	public static Location getLastReportedLocation()
	{
		return lastLocation;
	}
	
	static WakeLock wakeLock;
	private static void releaseWakeLock()
	{
		if (wakeLock != null)
		{
			if (wakeLock.isHeld())
				wakeLock.release();
			wakeLock = null;
			Log.v("BackgroundLocationService", "Wakelock released.");
		}
	}
	
	public static void acquireWakeLock(Context context)
	{
		if (wakeLock == null)
		{
			Log.v("BackgroundLocationService", "Getting a wake lock for 60 seconds.");
			
			PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock lock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationChangedReceiver");
			lock.acquire(60 * 1000);
			
			wakeLock = lock;
		}
	}
	
	private void onLocationUpdated()
	{
		final Location location = lastLocation;
		uiThread.post(new Runnable() {

			@Override
			public void run() {
				MainActivity.locationUpdated(location);
			}
		});
	}
	
	private void showToast(final String text)
	{
		uiThread.post(new Runnable() {

			@Override
			public void run() {
				MainActivity.showToast(text);
			}
		});
	}
}
