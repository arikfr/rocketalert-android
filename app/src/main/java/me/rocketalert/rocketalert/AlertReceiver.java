package me.rocketalert.rocketalert;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;

public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "me.rocketalert.AlertReceiver";

    public AlertReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.i(TAG, "got action " + action + " on channel " + channel + " with:");
            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                Log.d(TAG, "..." + key + " => " + json.getString(key));
            }

            /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String areaKey = json.optString("areaKey", null);

            Boolean enableAllAreas = prefs.getBoolean("enableAllAreas", true);

            // Fail safe mechanism:
            if (!enableAllAreas && areaKey != null && !prefs.getBoolean(areaKey, true)) {
                Log.i(TAG, "Skipping notification of area key: " + areaKey);
                return;
            }*/

            long timestamp = json.getInt("timestamp");
            String areaName = json.getString("areaName");
            String locations = json.getString("locations");
            int timeToCover = json.getInt("timeToCover");

            showNotification(context, timestamp, areaName, locations, timeToCover);

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }

    private void showNotification(Context context, long timestamp, String areaName, String locations, int timeToCover) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long endTime = System.currentTimeMillis() + 180*1000;
        Intent intent = new Intent(context, NotificationCancelReceiver.class);
        intent.putExtra("timestamp", timestamp);
        intent.putExtra("areaName", areaName);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC, endTime, pi);

        Notification notification = NotificationBuilder.getNotification(context, timestamp*1000, areaName, locations, timeToCover);

        notificationManager.notify(areaName, 0, notification);
    }
}
