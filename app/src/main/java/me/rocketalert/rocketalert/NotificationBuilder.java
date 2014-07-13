package me.rocketalert.rocketalert;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class NotificationBuilder {
    public static Notification getNotification(Context context, long timestamp, String areaName,
                                               String locations, int timeToCover) {
        // TODO: proper timestamp
        // countdown? http://developer.android.com/training/notify-user/display-progress.html
        // TODO: localization
        String title = String.format("%s (%d שניות)", areaName, timeToCover);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String tickerText = String.format("%s: %s", areaName, locations);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(locations)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(locations))
                        .setTicker(tickerText)
                        .setPriority(Notification.PRIORITY_MAX)

                        .setSound(alarmSound)
                        .setLights(Color.RED, 3000, 3000)
                        .setAutoCancel(true)
                        .setWhen(timestamp);

        if (prefs.getBoolean("enableVibrator", true)) {
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }

        return builder.build();
    }
}
