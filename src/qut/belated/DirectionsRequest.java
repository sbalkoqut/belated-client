package qut.belated;

import android.location.Location;

public class DirectionsRequest {

	public DirectionsRequest(Location origin, Meeting destination, PhysicalTravelMode travelMode)
	{
		this.travelMode = travelMode;
		originLatitude = origin.getLatitude();
		originLongitude = origin.getLongitude();
		destinationLatitude = destination.getLatitude();
		destinationLongitude = destination.getLongitude();
	}

	PhysicalTravelMode travelMode;
	
	public PhysicalTravelMode getTravelMode() {
		return travelMode;
	}

	double originLatitude;
	
	public double getOriginLatitude() {
		return originLatitude;
	}

	double originLongitude;
	
	public double getOriginLongitude() {
		return originLongitude;
	}

	double destinationLatitude;
	
	public double getDestinationLatitude() {
		return destinationLatitude;
	}
	
	double destinationLongitude;
	
	public double getDestinationLongitude() {
		return destinationLongitude;
	}
}
