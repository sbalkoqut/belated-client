package qut.belated;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import qut.belated.helpers.ISO8601Date;

import android.net.Uri;

public class Meeting {
	
	private Meeting()
	{
		
	}
	
	private static Meeting parsedMeeting;
	private static JSONObject jsonToParse;
	private static String myEmail;
	
	public static Meeting FromJson(JSONObject json, BelatedPreferences preferences)throws JSONException
	{
		parsedMeeting = new Meeting();
		jsonToParse = json;
		myEmail = preferences.getEmail();
		
		addGeneralProperties();
		addStartAndEndDates();
		addConferenceUrl();
		addAttendees();
		return parsedMeeting;
	}
	
	private static void addGeneralProperties() throws JSONException
	{
		parsedMeeting.id = jsonToParse.getString("_id");
		parsedMeeting.subject = jsonToParse.getString("subject");
		parsedMeeting.description = jsonToParse.getString("description");
		parsedMeeting.location = jsonToParse.getString("location");
		parsedMeeting.latitude = jsonToParse.getDouble("latitude");
		parsedMeeting.longitude = jsonToParse.getDouble("longitude");
	}
	
	private static void addStartAndEndDates() throws JSONException
	{
		String start = jsonToParse.getString("start");
		String end = jsonToParse.getString("end");
		
		try {
			parsedMeeting.start= ISO8601Date.parse(start);
			parsedMeeting.end = ISO8601Date.parse(end);
		} catch (ParseException e) {
			throw new JSONException("Start or end date not ISO8601 encoded.");
		}
	}
	
	private static void addAttendees() throws JSONException
	{
		parsedMeeting.myAttendee = null;
		parsedMeeting.otherAttendees = new ArrayList<Attendee>();
		
		Attendee organiser = Attendee.FromJson(jsonToParse.getJSONObject("organiser"));
		addAttendee(organiser);
		
		JSONArray jsonAttendees = jsonToParse.getJSONArray("attendees");
		for (int i = 0; i < jsonAttendees.length(); i++)
		{
			Attendee attendee = Attendee.FromJson(jsonAttendees.getJSONObject(i));
			addAttendee(attendee);
		}
		if (parsedMeeting.myAttendee == null)
			throw new JSONException("The meeting does not contain the current user.");
	}
	
	private static void addAttendee(Attendee attendee) throws JSONException
	{
		if (!attendee.isDeleted())
		{
			if (isAttendeeMe(attendee))
				parsedMeeting.myAttendee = attendee;
			else
				parsedMeeting.otherAttendees.add(attendee);
		}
	}
	
	private static boolean isAttendeeMe(Attendee attendee) throws JSONException
	{
		String email = attendee.getEmail();
		return email.equalsIgnoreCase(myEmail);
	}
	
	public static String getNameOf(JSONObject attendee) throws JSONException
	{
		return attendee.getString("name");
	}
	
	private static void addConferenceUrl() throws JSONException
	{
		if (jsonToParse.has("conferenceURL"))
		{
			String conferenceUrl = jsonToParse.getString("conferenceURL");
			parsedMeeting.conferenceURL = createUriFromJson(conferenceUrl);
		}
		else
			parsedMeeting.conferenceURL = null;
	}
	
	private static Uri createUriFromJson(String jsonValue)
	{
		if (!jsonValue.equalsIgnoreCase("null"))
			return Uri.parse(jsonValue);
		else
			return null;
	}
	
	String id;
	public String getId(){
		return id;
	}
	
	String subject;
	public String getSubject(){
		return subject;
	}
	
	String description;
	public String getDescription(){
		return description;
	}
	
	String location;
	public String getLocation(){
		return location;
	}

	double latitude;
	public double getLatitude(){
		return latitude;
	}

	double longitude;
	public double getLongitude(){
		return longitude;
	}
	
	Date start;
	public Date getStart(){
		return start;
	}
	
	Date end;
	public Date getEnd(){
		return end;
	}
	
	Uri conferenceURL;
	public Uri getConferenceURL() {
		return conferenceURL;
	}
	
	public boolean hasConferenceURL() {
		return conferenceURL != null;
	}
	
	List<Attendee> otherAttendees;
	public List<Attendee> getOtherAttendees()
	{
		return otherAttendees;
	}
	
	Attendee myAttendee;
	public Attendee getMyAttendee()
	{
		return myAttendee;
	}
}
