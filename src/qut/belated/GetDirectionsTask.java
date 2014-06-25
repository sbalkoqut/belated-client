package qut.belated;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import qut.belated.helpers.HttpHelper;

import android.os.AsyncTask;
import android.util.Log;

public class GetDirectionsTask extends AsyncTask<DirectionsRequest, Void, Directions> {

	MainActivity activity;
	
	DirectionsRequest request;
	String requestAddress;
	HttpClient client;
	HttpEntity httpGetResponse;
	Directions result;
	
	public GetDirectionsTask()
	{
		this.activity = MainActivity.instance;
	}
	
	@Override
	protected Directions doInBackground(DirectionsRequest... args) {
		if (args == null || args.length > 1)
			throw new IllegalArgumentException();
		
		request = args[0];
		
		findDirections();
		
		return result;
	}
	
	private void findDirections()
	{
		try
		{
			initialiseHttpClient();
			findRequestAddress();
			performGetRequest();
			retreiveResult();
		} 
		catch (IllegalArgumentException e)	{
			Log.e("GetDirectionsTask", "Service URL Invalid.");
		}
		catch (ClientProtocolException e) {
			Log.e("GetDirectionsTask", "Client protocol exception on HTTP Get.");
		} 
		catch (IOException e) {
			Log.e("GetDirectionsTask", "IO exception on HTTP Get. " + e.getMessage());
		}
	}
	
	private void initialiseHttpClient()
	{
		client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000); 
	}
	
	private void findRequestAddress()
	{
		PhysicalTravelMode travelMode = request.getTravelMode();
		
		String result = "http://maps.googleapis.com/maps/api/directions/json?" 
                + "origin=" + request.getOriginLatitude() + "," + request.getOriginLongitude()  
                + "&destination=" + request.getDestinationLatitude() + "," + request.getDestinationLongitude() 
                + "&mode=" + getAPITravelModeFlag(travelMode) 
                + "&sensor=false&units=metric";
		
		if (travelMode == PhysicalTravelMode.Transit)
		{
			long timestampOneMinuteFromNow = (System.currentTimeMillis() / 1000) + 60;
			result += "&departure_time=" + timestampOneMinuteFromNow;
		}
		requestAddress = result;
	}
	
	private String getAPITravelModeFlag(PhysicalTravelMode mode)
	{
		switch (mode)
		{
		case Car:
			return "driving";
		case Transit:
			return "transit";
		case Walk:
			return "walking";
		default:
			return null;
		}
	}
	
	private void performGetRequest() throws ClientProtocolException, IOException
	{
		HttpGet httpGetRequest = new HttpGet(requestAddress);
		HttpResponse response = client.execute(httpGetRequest);
		
		int statusCode = response.getStatusLine().getStatusCode();
		Log.v("GetDirectionsTask", "Directions retreived, status code: " + statusCode);
		
		httpGetResponse = response.getEntity();
	}
	
	private void retreiveResult() throws IOException
	{
		clearResult();
		processGetResponse();
	}

	private void clearResult()
	{
		result = new Directions(request.getTravelMode());
	}
	
	private void processGetResponse() throws IOException
	{
		if (httpGetResponse != null)
		{
			InputStream inStream = httpGetResponse.getContent();
			String jsonString = HttpHelper.readString(inStream);
			parseDirectionsJSON(jsonString);
		}
	}

	private void parseDirectionsJSON(String jsonString)
	{
		try
		{
			JSONObject directionsObject = new JSONObject(jsonString);
			result = new Directions(directionsObject, request.getTravelMode());
		}
		catch (JSONException e)
		{
			Log.e("GetDirectionsTask", "Couldn't parse JSON: " + e.getMessage());
		}
	}
	
	protected void onPostExecute(Directions directions)
	{
		activity.onDirectionsFound(directions);
	}
}
