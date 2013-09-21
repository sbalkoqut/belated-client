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
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

public class GetMeetingsTask extends AsyncTask<Void, Void, Meeting[]> {
	MainActivity activity;
	public GetMeetingsTask(MainActivity activity)
	{
		this.activity = activity;
	}
	
	@Override
	protected Meeting[] doInBackground(Void... args) {
		return getUpcomingMeetings(activity.preferences);
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
			activity.showMeeting(earliestMeeting, true);
		}
		else
			activity.showMeeting(null, results != null);
	}
	
	
	
	
	
	private Meeting[] getUpcomingMeetings(PreferenceHelper preferences)
	{
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000); 
		
		HttpContext context = new BasicHttpContext();
		HttpGet httpGet = null;
		String address = Utils.getMeetingsUrl(preferences.getServiceIP(), preferences.getEmail());
		try
		{
			httpGet = new HttpGet(address);
		}
		catch (IllegalArgumentException e)
		{
			Log.e("GetMeetingsTask", "Service URL Invalid.");
			return null;
		}
		
		try
		{
			HttpResponse response = client.execute(httpGet, context);
			int statusCode = response.getStatusLine().getStatusCode();
			Log.v("GetMeetingsTask", "Appointments retreived, status code: " + statusCode);
			
			HttpEntity entity = response.getEntity();
			
			if (entity != null)
			{
				InputStream inStream = entity.getContent();
				String jsonString = Utils.readString(inStream);
				return processMeetingJson(jsonString);
			}
		} 
		catch (ClientProtocolException e) {
			Log.e("GetMeetingsTask", "Client protocol exception on HTTP Get.");
		} 
		catch (IOException e) {
			Log.e("GetMeetingsTask", "IO exception on HTTP Get. " + e.getMessage());
		}
		return null;
	}
	
	
	
	private Meeting[] processMeetingJson(String jsonString)
	{
		try
		{
			JSONArray meetingArray = new JSONArray(jsonString);
			Meeting[] meetings = new Meeting[meetingArray.length()];
			for (int i = 0; i < meetings.length; i++)
			{
				meetings[i] = new Meeting(meetingArray.getJSONObject(i));
			}
			return meetings;
		}
		catch (JSONException e)
		{
			Log.e("GetMeetingsTask", "Couldn't parse JSON: " + e.getMessage());
			return null;
		}
	}

}
