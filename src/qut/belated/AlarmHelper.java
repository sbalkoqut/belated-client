package qut.belated;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmHelper {
	public static void setAlarmState(Context context, boolean enableAlarms)
	{
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		alarmManager.cancel(alarmIntent);
		if (enableAlarms)
		{
			Log.v("AlarmHelper", "Alarms are scheduled.");
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, alarmIntent);					
		}
		else
		{
			Log.v("AlarmHelper", "Any existing alarms are no longer scheduled.");
		}
	}
}
