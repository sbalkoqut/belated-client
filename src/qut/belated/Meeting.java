package qut.belated;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Meeting {
	public Meeting(JSONObject o) throws JSONException
	{
		subject = o.getString("subject");
		description = o.getString("description");
		location = o.getString("location");
		latitude = o.getDouble("latitude");
		longitude = o.getDouble("longitude");
		String startDateString = o.getString("start");
		String endDateString = o.getString("end");
		try {
			start= ISO8601DateParser.parse(startDateString);
			end = ISO8601DateParser.parse(endDateString);
		} catch (ParseException e) {
			throw new JSONException("Start or end date not ISO8601 encoded.");
		}
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
	
	
}
