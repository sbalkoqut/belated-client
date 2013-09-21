package qut.belated;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class BackgroundLocationService extends IntentService {

	PreferenceHelper preferences;
	Handler handler;
	
	public BackgroundLocationService() {
		super("Sends user position reports for Belated");
		preferences = null;
	}

	public void onCreate()
	{
		super.onCreate();
		handler = new Handler();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (preferences == null)
			preferences = new PreferenceHelper(this);
		if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED))
		{
			Log.v("BackgroundLocationService", "Received a location update.");
			
			Location location = (Location)intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
			lastLocation = location;
			onLocationUpdated(location);
			postLocation(location);
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
	
	private void postLocation(Location location)
	{
		ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("email", preferences.getEmail()));
		list.add(new BasicNameValuePair("lat", Double.toString(location.getLatitude())));
		list.add(new BasicNameValuePair("lng", Double.toString(location.getLongitude())));
		
		String address = Utils.getLocationUrl(preferences.getServiceIP());
		post(address, list);
	}
	
	private void post(String address, ArrayList<BasicNameValuePair> postParameters)
	{
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000); 
	
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(address);
		}
		catch (IllegalArgumentException e)
		{
			Log.e("BackgroundLocationService", "Service URL Invalid.");
			showToast("Problem whilst sending location (Service IP invalid).");
			return;
		}
		
		try {
			httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
			httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
		
			HttpResponse response = client.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			Log.v("BackgroundLocationService", "Location submitted, status code: " + statusCode);
			if (statusCode == 200)
				showToast("Location sent succesfully.");
			else
				showToast("Location send got status code " + statusCode);
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
	
	static Location lastLocation;
	public static Location getLastLocation()
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
	
	private void onLocationUpdated(final Location location)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				MainActivity.LocationUpdated(location);
			}
		});
	}
	
	private void showToast(final String text)
	{
		handler.post(new Runnable() {

			@Override
			public void run() {

				if (MainActivity.inForeground)
					Toast.makeText(BackgroundLocationService.this, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
