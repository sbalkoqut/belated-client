package qut.belated.helpers;

import qut.belated.AlarmReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmHelper {
	private static PendingIntent getAlarmReceiverIntent(Context context)
	{
		Intent notifyAlarmReceiver = new Intent(context, AlarmReceiver.class);
		PendingIntent pendingNotifyAlarmReceiver = PendingIntent.getBroadcast(context, 0, notifyAlarmReceiver, 0);
		return pendingNotifyAlarmReceiver;
	}
	
	private static AlarmManager getAlarmManager(Context context)
	{
		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
	
	public static void setAlarms(Context context, boolean enableAlarms)
	{
		AlarmManager manager = getAlarmManager(context);
		PendingIntent onAlarmIntent = getAlarmReceiverIntent(context);
				
		manager.cancel(onAlarmIntent);
		if (enableAlarms)
		{
			Log.v("AlarmHelper", "Alarms are scheduled.");
			manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, onAlarmIntent);					
		}
		else
		{
			Log.v("AlarmHelper", "Any existing alarms are no longer scheduled.");
		}
	}
}
