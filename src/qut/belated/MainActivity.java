package qut.belated;

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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {

	EditText emailField;
	EditText urlField;
	RelativeLayout mainLayout;
	PreferenceHelper preferences;
	
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
				}
			}
        });
        
        urlField = (EditText) findViewById(R.id.txtServiceAddress);
        urlField.setText(preferences.getServiceAddress(), TextView.BufferType.EDITABLE);
        urlField.setOnFocusChangeListener(new OnFocusChangeListener()
        {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus)
				{
					preferences.setServiceAddress(urlField.getText().toString().trim());
				}
			}
        });
        
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
}
