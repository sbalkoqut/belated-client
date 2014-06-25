package qut.belated;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import qut.belated.helpers.HttpHelper;
import android.os.AsyncTask;
import android.util.Log;

public class SendTravelPlanTask extends AsyncTask<TravelPlan, Void, Void> {

	HttpClient client;
	String serviceAddress;
	BelatedPreferences preferences;
	TravelPlan plan;
	
	public SendTravelPlanTask()
	{
		this.preferences = new BelatedPreferences(MainActivity.instance);
	}
	
	protected void onPostExecute(Void completed)
	{
	}
	
	@Override
	protected Void doInBackground(TravelPlan... args) {
		this.plan = args[0];
		sendTravelPlan();
		return null;
	}
	
	
	private void sendTravelPlan()
	{
		try
		{
			initialiseHttpClient();
			findServiceAddress();
			performHttpPost();
		} 
		catch (IllegalArgumentException e) {
			Log.e("SetTravelPlanTask", "Service Address Invalid.");
		}
		catch (ClientProtocolException e) {
			Log.e("SetTravelPlanTask", "Client protocol exception on HTTP Post.");
		} 
		catch (IOException e) {
			Log.e("SetTravelPlanTask", "IO exception on HTTP Post. " + e.getMessage());
		}	
	}
	
	private void initialiseHttpClient()
	{
		client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 7000); 
	}
	
	private void findServiceAddress()
	{
		serviceAddress = HttpHelper.getTravelPlanUrl(preferences.getServiceIP(), plan.getMeetingId());
	}
	
	private void performHttpPost() throws ClientProtocolException, IOException
	{
		HttpPost request = new HttpPost(serviceAddress);
		
		UrlEncodedFormEntity postData = plan.toHttpFormPost(preferences.getEmail());
		request.setEntity(postData);

		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		Log.v("SetTravelPlanTask", "Travel plan sent, status code: " + statusCode);
	}
}
