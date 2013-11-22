package qut.belated;

import org.json.JSONException;
import org.json.JSONObject;

public class Attendee
{
	private Attendee()
	{
		
	}
	
	public static Attendee FromJson(JSONObject json) throws JSONException
	{
		Attendee result = new Attendee();
		result.name = json.getString("name");
		result.email = json.getString("email");
		result.travelMode = json.getString("travelMode");
		result.deleted = json.has("deleted") && json.getBoolean("deleted");
		return result;
	}
	
	String name;
	public String getName()
	{
		return name;
	}
	
	String email;
	public String getEmail()
	{
		return email;
	}
	
	String travelMode;
	public String getTravelMode()
	{
		return travelMode;
	}
	
	public boolean testTravelMode(AlternateTravelMode mode)
	{
		return (travelMode.equalsIgnoreCase(mode.toString()));
	}
	
	public boolean testTravelMode(PhysicalTravelMode mode)
	{
		return (travelMode.equalsIgnoreCase(mode.toString()));
	}
	
	public void updateTravelPlan(TravelPlan plan)
	{
		travelMode = plan.getMode();
	}
	
	
	boolean deleted;
	public boolean isDeleted()
	{
		return deleted;
	}
	
}
