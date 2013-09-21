package qut.belated;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetDirectionsTask extends AsyncTask<String, Void, Directions> {

	MainActivity activity;
	String mode;
	
	public GetDirectionsTask(MainActivity activity, String mode)
	{
		this.activity = activity;
		this.mode = mode;
	}
	
	protected void onPostExecute(Directions directions)
	{
		activity.showDirections(mode, directions);
	}
	
	@Override
	protected Directions doInBackground(String... args) {
		return getDirections(args[0], args[1], args[2], args[3]);
	}
	
	private Directions getDirections(String startLatitude, String startLongitude, String endLatitude, String endLongitude)
	{
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000); 
		
		HttpContext context = new BasicHttpContext();
		HttpGet httpGet = null;
		String address = "http://maps.googleapis.com/maps/api/directions/json?" 
                + "origin=" + startLatitude + "," + startLongitude  
                + "&destination=" + endLatitude + "," + endLongitude 
                + "&mode=" + mode 
                + "&sensor=false&units=metric";
		if (mode == "transit")
		{
			long timestampInOneMinute = (System.currentTimeMillis() / 1000) + 60;
			address += "&departure_time=" + Long.toString(timestampInOneMinute);
		}
		try
		{
			httpGet = new HttpGet(address);
		}
		catch (IllegalArgumentException e)
		{
			Log.e("GetDirectionsTask", "Service URL Invalid.");
			//showToast("Problem whilst getting appointments (Service IP invalid).");
			return null;
		}
		
		try
		{
			HttpResponse response = client.execute(httpGet, context);
			int statusCode = response.getStatusLine().getStatusCode();
			Log.v("GetMeetingsTask", "Directions retreived, status code: " + statusCode);
			
			HttpEntity entity = response.getEntity();
			
			if (entity != null)
			{
				InputStream inStream = entity.getContent();
				String jsonString = Utils.readString(inStream);
				return processDirectionsJson(jsonString, mode);
			}
		} 
		catch (ClientProtocolException e) {
			Log.e("GetDirectionsTask", "Client protocol exception on HTTP Get.");
			//showToast("Problem whilst sending location (ClientProtocolException).");
		} 
		catch (IOException e) {
			Log.e("GetDirectionsTask", "IO exception on HTTP Get. " + e.getMessage());
			//showToast("Problem whilst sending location (IOException).");
		}
		return null;
	}

	private Directions processDirectionsJson(String jsonString, String mode)
	{
		try
		{
			JSONObject directionsObject = new JSONObject(jsonString);
			Directions directions = new Directions(directionsObject, mode);
			return directions;
		}
		catch (JSONException e)
		{
			Log.e("GetDirectionsTask", "Couldn't parse JSON: " + e.getMessage());
			return null;
		}
	}
}
