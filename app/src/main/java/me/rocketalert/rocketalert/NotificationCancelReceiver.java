package me.rocketalert.rocketalert;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationCancelReceiver extends BroadcastReceiver {
    public NotificationCancelReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String areaName = intent.getStringExtra("areaName");
        long timestamp = intent.getLongExtra("timestamp", 0);

        notificationManager.cancel(areaName, 0);
    }
}
