package qut.belated;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import qut.belated.helpers.ISO8601Date;

public class TravelPlan {
	public TravelPlan(String meetingId, PhysicalTravelMode mode, Date eta)
	{
		setMeetingId(meetingId);
		setPlan(mode, eta);
	}
	
	public TravelPlan(String meetingId, AlternateTravelMode mode)
	{
		setMeetingId(meetingId);
		setPlan(mode);
	}
	
	String meetingId;
	public String getMeetingId() {
		return meetingId;
	}
	
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	
	String mode;
	public String getMode() {
		return mode;
	}

	Date eta;
	public Date getEta() {
		return eta;
	}
	
	public void setPlan(PhysicalTravelMode mode, Date eta)
	{
		this.mode = mode.toString().toLowerCase(Locale.US);
		this.eta = eta;
	}
	
	public void setPlan(AlternateTravelMode mode)
	{
		this.mode = mode.toString().toLowerCase(Locale.US);
		this.eta = null;
	}	
		
	public UrlEncodedFormEntity toHttpFormPost(String email) throws UnsupportedEncodingException {
		ArrayList<BasicNameValuePair> formValues = new ArrayList<BasicNameValuePair>();
		
		formValues.add(new BasicNameValuePair("email", email));
		
		if (eta != null)
		{
			String serialisedEta = ISO8601Date.toString(eta);
			formValues.add(new BasicNameValuePair("eta", serialisedEta));
		}
		
		formValues.add(new BasicNameValuePair("mode", mode.toLowerCase(Locale.US)));
		
		UrlEncodedFormEntity post = new UrlEncodedFormEntity(formValues);
		post.setContentType("application/x-www-form-urlencoded");
		return post;
	}

	public void send()
	{
    	new SendTravelPlanTask().execute(this);
	}
}
