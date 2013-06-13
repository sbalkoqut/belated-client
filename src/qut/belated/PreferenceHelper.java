package qut.belated;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferenceHelper {
	SharedPreferences preferences;
	Context context;
	
	public PreferenceHelper(Context context)
	{
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.context = context;
	}
	
	public boolean isServiceEnabled()
	{
		return preferences.getBoolean("serviceEnabled", true);
	}
	
	public void setServiceEnabled(boolean enabled)
	{
		if (enabled == isServiceEnabled())
			return;
		
		SharedPreferences.Editor preferenceEditor = preferences.edit();
		preferenceEditor.putBoolean("serviceEnabled", enabled);
		preferenceEditor.commit();
		

		Log.v("PreferenceHelper", "Service now " + Boolean.toString(enabled) + ".");
	}
	
	public String getEmail()
	{
		if (preferences.contains("email"))
		{
			return preferences.getString("email", null);
		}
		else
		{
			return getDefaultEmail();
		}
	}
	
	public void setEmail(String email)
	{
		if (email.equals(getEmail()))
			return;
		
		SharedPreferences.Editor preferenceEditor = preferences.edit();
		preferenceEditor.putString("email", email);
		preferenceEditor.commit();
		
		Log.v("PreferenceHelper", "User email changed to \"" + email + "\".");
	}
	
	private String getDefaultEmail()
	{
		Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
		if (accounts.length > 0)
			return accounts[0].name;
		else
			return null;
	}
	
	public String getServiceAddress()
	{
		return preferences.getString("serviceAddress", null);
	}
	
	public void setServiceAddress(String address)
	{
		if (address.equals(getServiceAddress()))
			return;
		
		SharedPreferences.Editor preferenceEditor = preferences.edit();
		preferenceEditor.putString("serviceAddress", address);
		preferenceEditor.commit();
		
		Log.v("PreferenceHelper", "Service address changed to \"" + address + "\".");
	}
}
