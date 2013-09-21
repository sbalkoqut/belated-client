package qut.belated;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Directions {
	public Directions(JSONObject o, String mode) throws JSONException {
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
		distanceValue = distance.getInt("value");
		
		JSONObject duration = leg.getJSONObject("duration");
		durationText = duration.getString("text");
		durationValue = duration.getInt("value");
		
		JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
		String pointData = overviewPolyline.getString("points");
		points = decodeRoute(pointData);
		
		
		this.mode = mode;
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
	
	private int distanceValue;
	public int getDistanceValue()
	{
		return distanceValue;
	}
	
	private String durationText;
	public String getDurationText()
	{
		return durationText;
	}
	
	private int durationValue;
	public int getDurationValue()
	{
		return durationValue;
	}
	
	private LatLngBounds bounds;
	public LatLngBounds getBounds()
	{
		return bounds;
	}
	
	private String mode;
	public String getMode()
	{
		return mode;
	}
	
	 private ArrayList<LatLng> decodeRoute(String encoded) {
	        ArrayList<LatLng> poly = new ArrayList<LatLng>();
	        int index = 0, len = encoded.length();
	        int lat = 0, lng = 0;
	        while (index < len) {
	            int b, shift = 0, result = 0;
	            do {
	                b = encoded.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lat += dlat;
	            shift = 0;
	            result = 0;
	            do {
	                b = encoded.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lng += dlng;

	            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
	            poly.add(position);
	        }
	        return poly;
	 }


	
//	private ArrayList<LatLng> decodeRoute(String s) {
//        ArrayList<LatLng> route = new ArrayList<LatLng>();
//        
//        int latitude = 0;
//        int longitude = 0;
//        boolean readingLatitude = true;
//
//        int index = 0;
//        int length = s.length();
//        while (index < length) {
//        	int result = 0;
//        	int count = 0;
//        	int chunk;
//        	do {
//        		chunk = s.charAt(index++) - 63;
//        		chunk = chunk << (5 * count);
//        		result = result | chunk;
//        		count++;
//        	} while ((chunk & 0x20) != 0);
//        	
//        	if (result % 2 == 1)
//        	{
//        		result = result >> 1;
//        		result = ~result;
//        	}
//        	else
//        		result = result >> 1;
//        	
//        	if (readingLatitude)
//        		latitude += result;
//        	else
//        	{
//        		longitude += result;
//        		
//        		double lat = ((double)latitude) / ((double)10000);
//        		double lng = ((double)longitude) / ((double)10000);
//        		
//        		LatLng position = new LatLng(lat, lng);
//        		route.add(position);
//        	}
//        	readingLatitude = !readingLatitude;
//        }
//        return route;
//    }
}
