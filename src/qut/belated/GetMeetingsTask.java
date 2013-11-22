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
import org.json.JSONArray;
import org.json.JSONException;

import qut.belated.helpers.HttpHelper;

import android.os.AsyncTask;
import android.util.Log;

public class GetMeetingsTask extends AsyncTask<Void, Void, Meeting[]> {
	MainActivity activity;
	BelatedPreferences preferences;
	String requestAddress;
	HttpClient client;
	HttpEntity httpGetResponse;
	Meeting[] result;
	
	public GetMeetingsTask()
	{
		this.activity = MainActivity.instance;
		preferences = new BelatedPreferences(activity);
	}
	
	@Override
	protected Meeting[] doInBackground(Void... args) {
		getUpcomingMeetings();
		return result;
	}
	
	private void getUpcomingMeetings()
	{
		try
		{
			initialiseHttpClient();
			findRequestAddress();
			performGetRequest();
			retreiveResult();
		} 
		catch (IllegalArgumentException e) {
			Log.e("GetMeetingsTask", "Service URL Invalid.");
		}
		catch (ClientProtocolException e) {
			Log.e("GetMeetingsTask", "Client protocol exception on HTTP Get.");
		} 
		catch (IOException e) {
			Log.e("GetMeetingsTask", "IO exception on HTTP Get. " + e.getMessage());
		}
	}
	
	private void initialiseHttpClient()
	{
		client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), HttpHelper.SHORT_TIMEOUT); 
	}
	
	private void findRequestAddress()
	{
		requestAddress = HttpHelper.getMeetingsUrl(preferences.getServiceIP(), preferences.getEmail());
	}

	private void performGetRequest() throws ClientProtocolException, IOException
	{
		HttpGet httpGet = new HttpGet(requestAddress);
		HttpResponse response = client.execute(httpGet);
		
		int statusCode = response.getStatusLine().getStatusCode();
		Log.v("GetMeetingsTask", "Meetings retreived, status code: " + statusCode);
		
		httpGetResponse = response.getEntity();
	}
	
	
	private void retreiveResult() throws IOException
	{
		clearResult();
		processGetResponse();	
	}
	
	private void clearResult()
	{
		result = new Meeting[0];
	}
	
	private void processGetResponse() throws IOException
	{
		if (httpGetResponse != null)
		{
			InputStream inStream = httpGetResponse.getContent();
			String jsonString = HttpHelper.readString(inStream);
			processMeetingJson(jsonString);
		}
	}
	
	private void processMeetingJson(String jsonString)
	{
		try
		{
			JSONArray meetingArray = new JSONArray(jsonString);
			Meeting[] meetings = new Meeting[meetingArray.length()];
			for (int i = 0; i < meetings.length; i++)
			{
				meetings[i] = Meeting.FromJson(meetingArray.getJSONObject(i), preferences);
			}
			result = meetings;
		}
		catch (JSONException e)
		{
			Log.e("GetMeetingsTask", "Couldn't parse JSON: " + e.getMessage());
		}
	}
	
	protected void onPostExecute(Meeting[] results)
	{
		if (results != null && results.length > 0)
		{
			Meeting earliestMeeting = results[0];
			Date earliestStart = results[0].getStart();
			for (int i = 1; i < results.length; i++)
			{
				Meeting thisMeeting = results[i];
				Date thisStart = thisMeeting.getStart();
				if (thisStart.before(earliestStart))
				{
					earliestStart = thisStart;
					earliestMeeting = thisMeeting;
				}
			}
			activity.onMeetingReceived(earliestMeeting, true);
		}
		else
			activity.onMeetingReceived(null, results != null);
	}
}
