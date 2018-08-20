package com.gafasdesol.hawkersfans.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gafasdesol.hawkersfans.activities.SplashScreen;
import com.gafasdesol.hawkersfans.app.MyAppPrefsManager;
import com.gafasdesol.hawkersfans.utils.NotificationHelper;
import com.gafasdesol.hawkersfans.utils.NotificationScheduler;


/**
 * AlarmReceiver receives the Broadcast Intent
 */

public class AlarmReceiver extends BroadcastReceiver {
    
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                
                NotificationScheduler.setReminder(context, AlarmReceiver.class);
            }
        }
        
        
        Intent notificationIntent = new Intent(context, SplashScreen.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(context);
        
        
        //Trigger the notification
        NotificationHelper.showNewNotification
                (
                        context,
                        notificationIntent,
                        myAppPrefsManager.getLocalNotificationsTitle(),
                        myAppPrefsManager.getLocalNotificationsDescription()
                );
        
    }
    
}

