package qut.belated;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText emailField;
	EditText urlField;
	LinearLayout scrollLayout;
	PreferenceHelper preferences;
	TextView txtMeetingTitle;
	TextView txtMeetingDate;
	TextView txtMeetingLocation;
	TextView txtMeetingDescription;
	Button btnWalkingDirections;
	Button btnDrivingDirections;
	Button btnTransitDirections;
	Button btnOnlineConference;
	TextView txtWalkingDetail;
	TextView txtDrivingDetail;
	TextView txtTransitDetail;
	RelativeLayout layoutWalking;
	RelativeLayout layoutDriving;
	RelativeLayout layoutTransit;
	RelativeLayout layoutOnline;
	
	MapFragment googleMapFragment;
	GoogleMap googleMap;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
   
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = new PreferenceHelper((Context) MainActivity.this);
        
        Switch backgroundServiceSwitch = (Switch) findViewById(R.id.switchBackgroundService);
        backgroundServiceSwitch.setChecked(preferences.isServiceEnabled());
        backgroundServiceSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
			@Override
			public void onCheckedChanged(CompoundButton btn, boolean checked) {
				preferences.setServiceEnabled(checked);
			
				AlarmHelper.setAlarmState(MainActivity.this, checked);
			}
        });
        
        emailField = (EditText) findViewById(R.id.txtEmail);
        emailField.setText(preferences.getEmail(), TextView.BufferType.EDITABLE);
        emailField.setOnFocusChangeListener(new OnFocusChangeListener()
        {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus)
				{
					preferences.setEmail(emailField.getText().toString().trim());
					if (!gettingMeetings)
					{
				    	new GetMeetingsTask(MainActivity.this).execute();
					}
				}
			}
        });
        
        urlField = (EditText) findViewById(R.id.txtServiceAddress);
        urlField.setText(preferences.getServiceIP(), TextView.BufferType.EDITABLE);
        urlField.setOnFocusChangeListener(new OnFocusChangeListener()
        {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus)
				{
					preferences.setServiceIP(urlField.getText().toString().trim());
					if (!gettingMeetings)
					{
				    	new GetMeetingsTask(MainActivity.this).execute();
					}
				}
			}
        });
        
        scrollLayout = (LinearLayout) findViewById(R.id.scrollLayout);
        scrollLayout.setOnTouchListener(new OnTouchListener()
        {

			@Override
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_UP)
				{
					InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(scrollLayout.getWindowToken(), 0);
				}
				return false;
			}
        	
        });

        
        txtMeetingTitle = (TextView) findViewById(R.id.txtMeetingTitle);
        txtMeetingDate = (TextView) findViewById(R.id.txtMeetingDate);
        txtMeetingLocation = (TextView) findViewById(R.id.txtMeetingLocation);
        txtMeetingDescription = (TextView) findViewById(R.id.txtMeetingDescription);

        btnWalkingDirections = (Button) findViewById(R.id.btnWalking);
        btnWalkingDirections.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				startNavigationToMeeting("w");
			}
        });
        btnWalkingDirections.setEnabled(false);
        
        btnDrivingDirections = (Button) findViewById(R.id.btnDriving);
        btnDrivingDirections.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				startNavigationToMeeting("d");
			}
        });
        btnDrivingDirections.setEnabled(false);
        
        btnTransitDirections = (Button) findViewById(R.id.btnTransit);
        btnTransitDirections.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				startNavigationToMeeting("r");
			}
        });
        btnTransitDirections.setEnabled(false);
        
        btnOnlineConference = (Button) findViewById(R.id.btnOnline);
        btnOnlineConference.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startConference();
			}
		});
        
        txtWalkingDetail = (TextView) findViewById(R.id.txtWalkingDetail);
        txtDrivingDetail = (TextView) findViewById(R.id.txtDrivingDetail);
        txtTransitDetail = (TextView) findViewById(R.id.txtTransitDetail);
        
        layoutWalking = (RelativeLayout) findViewById(R.id.layoutWalking);
        layoutDriving = (RelativeLayout) findViewById(R.id.layoutDriving);
        layoutTransit = (RelativeLayout) findViewById(R.id.layoutTransit);
        layoutOnline = (RelativeLayout) findViewById(R.id.layoutOnline);
        
        googleMapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        googleMapFragment.getView().setVisibility(View.INVISIBLE);
		googleMap = googleMapFragment.getMap();
		
        inForeground = false;
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void startNavigationToMeeting(String mode)
    {
    	if (meeting == null)
    		return;
    	Location currentLocation = BackgroundLocationService.getLastLocation();
		
    	if (currentLocation == null)
    		return;
    	String origin = Double.toString(currentLocation.getLatitude()) + "," + Double.toString(currentLocation.getLongitude());
		String destination = Double.toString(meeting.getLatitude()) + "," + Double.toString(meeting.getLongitude());
		
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
		Uri.parse("http://maps.google.com/maps?saddr=" + origin + "&daddr=" + destination + "&dirflg=" + mode));
		
		//intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
		startActivity(intent);
    }
    
    private void startConference()
    {
    	if (meeting == null)
    		return;
    	if (!meeting.hasConferenceURL())
    		return;
    	
    	Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
    			meeting.getConferenceURL());
    	
    	startActivity(intent);
    }
    
    private boolean gettingMeetings;
    public static boolean inForeground;
    public static MainActivity instance;
    protected void onResume()
    {
    	super.onResume();
    	instance = this;
    	inForeground = true;
    	gettingMeetings = true;
    	new GetMeetingsTask(this).execute();
    }
    
    protected void onPause()
    {
    	inForeground = false;
    	instance = null;
    	super.onPause();
    }
    
    Meeting meeting;
    boolean gotDirections;
    protected void showMeeting(Meeting m, boolean serviceAvailable)
    {
    	if (!inForeground)
    		return;
    	meeting = m;
    	gotDirections = false;
		if (m != null)
		{
    		txtMeetingTitle.setText(m.getSubject());
    		
    		Date startDate = m.getStart();
    		Date endDate = m.getEnd();


			DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    		
    		String result;
    		if (Utils.sameDay(startDate, endDate))
			{
    			if (Utils.sameDay(startDate, new Date()))
    				result = "Today, ";
    			else
    				result = dateFormat.format(startDate);
    			result += timeFormat.format(startDate) + " - " + timeFormat.format(endDate);
			}
    		else
    		{
    			result = dateFormat.format(startDate) + ", " + timeFormat.format(startDate) 
    					+ " - " + dateFormat.format(endDate) + ", " + timeFormat.format(endDate);
    		}
    		txtMeetingDate.setText(result);
    		txtMeetingLocation.setText(m.getLocation());
    		txtMeetingDescription.setText(m.getDescription());
    		
    		googleMap.clear();
			layoutWalking.setVisibility(View.VISIBLE);
			layoutDriving.setVisibility(View.VISIBLE);
			layoutTransit.setVisibility(View.VISIBLE);
			
			if (m.hasConferenceURL())
			{
				layoutOnline.setVisibility(View.VISIBLE);
			}
			else
			{
				layoutOnline.setVisibility(View.GONE);
			}
			
			btnDrivingDirections.setEnabled(false);
			btnWalkingDirections.setEnabled(false);
			btnTransitDirections.setEnabled(false);
			txtDrivingDetail.setText("");
			txtWalkingDetail.setText("");
			txtTransitDetail.setText("");

			googleMapFragment.getView().setVisibility(View.VISIBLE);
			LatLng meetingPosition = new LatLng(meeting.getLatitude(), meeting.getLongitude());
			
			MarkerOptions marker = new MarkerOptions();
			marker.title(meeting.getLocation());
			marker.position(meetingPosition);
			marker.draggable(false);
			googleMap.addMarker(marker);
			
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(meetingPosition, 15);
			googleMap.moveCamera(update);
			
    		Location currentLocation = BackgroundLocationService.getLastLocation();
    		
    		if (currentLocation != null)
    		{
    			onLocationUpdated(currentLocation);
    		}
		}
		else
		{
			txtMeetingTitle.setText(serviceAvailable ? "No upcoming meetings." : "Service unavailable.");
			txtMeetingDate.setText("");
			txtMeetingLocation.setText("");
			txtMeetingDescription.setText("");
			layoutWalking.setVisibility(View.INVISIBLE);
			layoutDriving.setVisibility(View.INVISIBLE);
			layoutTransit.setVisibility(View.INVISIBLE);
			layoutOnline.setVisibility(View.GONE);
			googleMapFragment.getView().setVisibility(View.INVISIBLE);
			gettingMeetings = false;
		}
    	
    }
    
    int directionsReceivedCount = 0;
    LatLngBounds directionBounds;
    protected void onLocationUpdated(Location location)
    {
    	if (meeting != null && !gotDirections)
    	{
    		gotDirections = true;
    		directionsReceivedCount = 0;
    		directionBounds = null;
			Log.d("MainActivity", "Invoking for driving directions.");
			btnDrivingDirections.setEnabled(true);
			btnWalkingDirections.setEnabled(true);
			btnTransitDirections.setEnabled(true);
			new GetDirectionsTask(this, "walking").execute(
					Double.toString(location.getLatitude()),
					Double.toString(location.getLongitude()),
					Double.toString(meeting.getLatitude()),
					Double.toString(meeting.getLongitude()));
			new GetDirectionsTask(this, "driving").execute(
					Double.toString(location.getLatitude()),
					Double.toString(location.getLongitude()),
					Double.toString(meeting.getLatitude()),
					Double.toString(meeting.getLongitude()));
			new GetDirectionsTask(this, "transit").execute(
					Double.toString(location.getLatitude()),
					Double.toString(location.getLongitude()),
					Double.toString(meeting.getLatitude()),
					Double.toString(meeting.getLongitude()));
			
    	}
    }
    
    public static void LocationUpdated(Location newLocation)
    {
    	if (inForeground)
    	{
    		instance.onLocationUpdated(newLocation);
    	}
    }
    
    protected void showDirections(String mode, Directions d)
    {
    	if (!inForeground)
    		return;
    	directionsReceivedCount++;
    	if (mode == "walking")
    	{
    		if (d != null)
    		{
    			txtWalkingDetail.setText(d.getDurationText() + " / " + d.getDistanceText());
    			googleMap.addPolyline(getPolyline(d, Color.BLUE));
    		}
    		else
    		{
    			txtWalkingDetail.setText("Tap for directions.");
    		}
    	}
    	else if (mode == "driving")
    	{
    		if (d != null)
    		{
    			txtDrivingDetail.setText(d.getDurationText() + " / " + d.getDistanceText());
    			googleMap.addPolyline(getPolyline(d, 0xFFBB00BB));
    		}
    		else
    		{
    			txtDrivingDetail.setText("Tap for directions.");
    		}
    	}
    	else if (mode == "transit")
    	{
    		if (d != null)
    		{
    			txtTransitDetail.setText(d.getDurationText() + " / " + d.getDistanceText());
    			googleMap.addPolyline(getPolyline(d, Color.RED));
    		}
    		else
    		{
    			txtTransitDetail.setText("Not available from here.");
    		}
    	}
    	if (d != null)
    	{
    		if (directionBounds == null)
    			directionBounds = d.getBounds();
    		else
    			directionBounds = Utils.Combine(directionBounds, d.getBounds());
    	}
    	if (directionsReceivedCount == 3 && directionBounds != null)
    	{
    		CameraUpdate update = CameraUpdateFactory.newLatLngBounds(directionBounds, 30);
			googleMap.moveCamera(update);
			gettingMeetings = false;
    	}
    }
    
    private PolylineOptions getPolyline(Directions d, int color)
    {
    	ArrayList<LatLng> points = d.getPoints();
    	PolylineOptions polyLine = new PolylineOptions().width(3).color(color);

    	for(int i = 0 ; i < points.size(); i++) {          
    		polyLine.add(points.get(i));
    	}
    	return polyLine;
    }
}
