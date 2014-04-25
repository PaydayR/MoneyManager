package com.paydayr.moneymanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.paydayr.moneymanager.activities.MainActivity;

public class NotificationService {
	
	@SuppressWarnings("deprecation")
	public static void sendNotification(Context context, String tickerText, String title, String content, int notificationID){
        // Create an object of Notification manager 
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = android.R.drawable.stat_sys_warning; // icon from resources
        long when = System.currentTimeMillis();         // notification time

        MainActivity.LAST_ABA=2;
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        // the next two lines initialize the Notification, using the configurations above
        Notification notification = new Notification(icon, tickerText, when);
        notification.setLatestEventInfo(context, title, content, contentIntent);


        mNotificationManager.notify(notificationID, notification);		
	}
}
