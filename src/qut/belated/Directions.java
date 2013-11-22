package qut.belated;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import qut.belated.helpers.PolylineDecoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Directions {
	public Directions(JSONObject o, PhysicalTravelMode mode) throws JSONException {
		JSONArray routes = o.getJSONArray("routes");
		if (routes.length() == 0)
			throw new JSONException("Expected atleast one route.");
		JSONObject route = routes.getJSONObject(0);
		
		this.copyright = route.getString("copyrights");
		JSONObject bounds = route.getJSONObject("bounds");
		this.bounds = BoundsFromJson(bounds);
		
		JSONArray legs = route.getJSONArray("legs");
		if (legs.length() != 1)
			throw new JSONException("Expected one leg.");
		
		JSONObject leg = legs.getJSONObject(0);
		
		JSONObject distance = leg.getJSONObject("distance");
		distanceText = distance.getString("text");
		distanceInMeters = distance.getInt("value");
		
		JSONObject duration = leg.getJSONObject("duration");
		durationText = duration.getString("text");
		durationInSeconds = duration.getInt("value");
		
		JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
		String pointData = overviewPolyline.getString("points");
		points = PolylineDecoder.decodePolyline(pointData);
		
		this.mode = mode;
		this.found = true;
	}
	
	public Directions(PhysicalTravelMode mode)
	{
		this.copyright = "";
		this.bounds = null;
		this.distanceText = null;
		this.distanceInMeters = 0;
		this.durationText = null;
		this.durationInSeconds = 0;
		this.points = null;
		this.mode = mode;
		this.found = false;
	}
	
	boolean found;
	public boolean wereFound()
	{
		return found;
	}
	
	private static LatLngBounds BoundsFromJson(JSONObject bounds) throws JSONException
	{
		JSONObject northEastObject = bounds.getJSONObject("northeast");
		LatLng northEast = new LatLng(northEastObject.getDouble("lat"), northEastObject.getDouble("lng"));
		JSONObject southWestObject = bounds.getJSONObject("southwest");
		LatLng southWest = new LatLng(southWestObject.getDouble("lat"), southWestObject.getDouble("lng"));
		return new LatLngBounds(southWest, northEast);
	}
	
	private String copyright;
	public String getCopyright(){
		return copyright;
	}
	
	private ArrayList<LatLng> points;
	public ArrayList<LatLng> getPoints()
	{
		return points;
	}
	
	private String distanceText;
	public String getDistanceText()
	{
		return distanceText;
	}
	
	private int distanceInMeters;
	public int getDistanceInMeters()
	{
		return distanceInMeters;
	}
	
	private String durationText;
	public String getDurationText()
	{
		return durationText;
	}
	
	private int durationInSeconds;
	public int getDurationInSeconds()
	{
		return durationInSeconds;
	}
	
	public Date computeEstimatedTimeOfArrival()
	{
		Calendar estimatedTimeOfArrival = Calendar.getInstance();
		estimatedTimeOfArrival.setTime(new Date());
		estimatedTimeOfArrival.add(Calendar.SECOND, this.getDurationInSeconds());
		return estimatedTimeOfArrival.getTime();
	}
	
	private LatLngBounds bounds;
	public LatLngBounds getBounds()
	{
		return bounds;
	}
	
	private PhysicalTravelMode mode;
	public PhysicalTravelMode getMode()
	{
		return mode;
	}
	
	
}
