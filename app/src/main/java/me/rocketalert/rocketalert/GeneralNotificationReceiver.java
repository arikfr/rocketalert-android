package me.rocketalert.rocketalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class GeneralNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "me.rocketalert.GeneralNotificationReceiver";

    public GeneralNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
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

            if (!json.has("titleText")) {
                Log.w(TAG, "Notification has no title. Skipping.");
                return;
            }

            Notification notification = buildNotification(context, json);

            showNotification(context, notification);
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }

    private Notification buildNotification(Context context, JSONObject json) throws JSONException {
        String title = json.getString("titleText");

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(title);
        builder.setSound(notificationSound);

        if (json.has("contentText")) {
            builder.setContentText(json.getString("contentText"));
        }

        if (json.has("tickerText")) {
            builder.setTicker(json.getString("tickerText"));
        }

        if (json.has("actionUri")) {
            String actionUri = json.getString("actionUri");
            Log.d(TAG, "Setting actionUri: " + actionUri);
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(actionUri));

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
            builder.setContentIntent(contentIntent);
        }

        builder.setAutoCancel(true);

        return builder.build();
    }

    private void showNotification(Context context, Notification notification) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }
}
