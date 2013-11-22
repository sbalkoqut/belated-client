package qut.belated;

import qut.belated.helpers.AlarmHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsActivity extends Activity {


	EditText emailField;
	EditText urlField;
	LinearLayout mainLayout;
	BelatedPreferences preferences;

	@Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		preferences = new BelatedPreferences(this);
		Switch backgroundServiceSwitch = (Switch) findViewById(R.id.switchBackgroundService);
        backgroundServiceSwitch.setChecked(preferences.isServiceEnabled());
        backgroundServiceSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
			@Override
			public void onCheckedChanged(CompoundButton btn, boolean checked) {
				preferences.setServiceEnabled(checked);
			
				AlarmHelper.setAlarms(SettingsActivity.this, checked);
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
				}
			}
        });
        
        mainLayout = (LinearLayout) findViewById(R.id.settingsLayout);
        mainLayout.setOnTouchListener(new OnTouchListener()
        {
			@Override
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_UP)
				{
					InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
				}
				return false;
			}
        });
	}
	
	@Override
	public void onPause() {
		mainLayout.requestFocus();
    	super.onPause();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
