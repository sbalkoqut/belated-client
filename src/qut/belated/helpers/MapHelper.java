package qut.belated.helpers;

import java.util.ArrayList;

import qut.belated.Directions;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapHelper {

	public static LatLngBounds computeBoundsToShowAll(Iterable<Directions> directions)
	{
		LatLngBounds mapBounds = null;
		for(Directions item : directions)
		{
			mapBounds = combine(mapBounds, item.getBounds());
		}
		return mapBounds;
		
	}
	
	public static LatLngBounds combine(LatLngBounds first, LatLngBounds second)
	{
		if (first == null)
			return second;
		if (second == null)
			return first;
		
		LatLngBounds combined = first.including(second.northeast);
		combined = combined.including(second.southwest);
		return combined;
	}
	

    public static PolylineOptions createPolylineFor(Directions directions, int color)
    {
    	ArrayList<LatLng> points = directions.getPoints();
    	PolylineOptions polyLine = new PolylineOptions().width(3).color(color);

    	for(int i = 0 ; i < points.size(); i++) {          
    		polyLine.add(points.get(i));
    	}
    	return polyLine;
    }
}
