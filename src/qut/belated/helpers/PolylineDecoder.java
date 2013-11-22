package qut.belated.helpers;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class PolylineDecoder {

	final static int CHUNK_BIT_LENGTH = 5;
	final static int CHUNK_CONTINUE_BIT_POWER_OF_TWO = CHUNK_BIT_LENGTH;
	
	static int longitudeInE5;
	static int latitudeInE5;
	static int charIndex;
	static String encodedPolyline;
	static ArrayList<LatLng> route;
	
	static BitBuffer bitBuffer = new BitBuffer(CHUNK_BIT_LENGTH);
	
	final static double E5 = 100000;
	
	public static ArrayList<LatLng> decodePolyline(String polyline) {
		setEncodedPolyline(polyline);
        clearWaypoints();
        readWaypoints();
        return route;
    }
	
	private static void clearWaypoints()
	{
		route = new ArrayList<LatLng>();
		latitudeInE5 = 0;
		longitudeInE5 = 0;
	}
	
	private static void readWaypoints()
	{
        int length = encodedPolyline.length();
        while (charIndex < length) {
        	readWaypoint();
        }
	}
	
	private static void readWaypoint()
	{
		readWaypointDelta();
		setWaypoint();
	}
	
	private static void readWaypointDelta()
	{
		latitudeInE5 += readDeltaInE5();
		longitudeInE5 += readDeltaInE5();
	}
		
	private static void setWaypoint()
	{
		double lat = ((double)latitudeInE5) / E5;
    	double lng = ((double)longitudeInE5) / E5;
    		
    	route.add(new LatLng(lat, lng));
	}
	
	private static int readDeltaInE5()
	{
    	fillBuffer();
    	performSignCorrection();
    	return bitBuffer.getValue();
	}
	
	private static void fillBuffer()
	{
    	bitBuffer.clear();
    	int chunk;
    	do {
    		chunk = readChunk();
    		bitBuffer.prependWord(chunk);
    	} while (isContinueBitSet(chunk));
	}
	
	private static boolean isContinueBitSet(int chunk)
	{
		return BitBuffer.testBit(chunk, CHUNK_CONTINUE_BIT_POWER_OF_TWO);
	}
	
	private static void performSignCorrection()
	{
    	boolean signBit = bitBuffer.takeBit();
    	if (signBit)
    		performTwosCompliment();
	}
	
	private static void performTwosCompliment()
	{
		bitBuffer.invertBits();
	}
			
	private static void setEncodedPolyline(String polyline)
	{
		encodedPolyline = polyline;
        charIndex = 0;
	}
	
	private static char readEncodedCharacter()
	{
		return encodedPolyline.charAt(charIndex++);
	}
	
	private static int readChunk()
	{
		return readEncodedCharacter() - 63;
	}
	
}
