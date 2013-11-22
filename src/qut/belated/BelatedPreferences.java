package qut.belated;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BelatedPreferences {
	SharedPreferences preferences;
	Context context;
	
	public BelatedPreferences(Context context)
	{
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.context = context;
	}
	
	public boolean isServiceEnabled()
	{
		return preferences.getBoolean("serviceEnabled", true);
	}
	
	public void setServiceEnabled(boolean enable)
	{
		if (enable == isServiceEnabled())
			return;
		
		SharedPreferences.Editor preferenceEditor = preferences.edit();
		preferenceEditor.putBoolean("serviceEnabled", enable);
		preferenceEditor.commit();
		

		Log.v("PreferenceHelper", "Service enablement now changed to " + Boolean.toString(enable) + ".");
	}
	
	public String getEmail()
	{
		if (preferences.contains("email"))
		{
			return preferences.getString("email", "");
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
			return "";
	}
	
	public String getServiceIP()
	{
		return preferences.getString("serviceIP", "54.252.45.18");
	}
	
	public void setServiceIP(String address)
	{
		if (address.equals(getServiceIP()))
			return;
		
		SharedPreferences.Editor preferenceEditor = preferences.edit();
		preferenceEditor.putString("serviceIP", address);
		preferenceEditor.commit();
		
		Log.v("PreferenceHelper", "Service IP changed to \"" + address + "\".");
	}
}
