package me.rocketalert.rocketalert;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Alert extends Activity {
    private static final String TAG = "me.rocketalert.Alert";
    private ListView mSubscriptionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        mSubscriptionsList = (ListView) findViewById(R.id.listSubscriptions);
        mSubscriptionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openSettings();
            }
        });

        if (BuildConfig.DEBUG) {
            enableStrictMode();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Set<String> subscriptions = PushService.getSubscriptions(this);
        List<String> subscriptionsArray = new ArrayList<String>();

        if (subscriptions.contains("all-android")) {
            subscriptionsArray.add(getString(R.string.all_areas));
        }

        for (String areaKey : subscriptions) {
            String areaName = Area.getAreaName(areaKey);
            if (areaName != null) {
                subscriptionsArray.add(areaName);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subscriptionsArray);
        mSubscriptionsList.setAdapter(adapter);
    }

    private void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                        // alternatively .detectAll() for all detectable problems
                .penaltyLog()
//                .penaltyDeath()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                        // alternatively .detectAll() for all detectable problems
                .penaltyLog()
//                .penaltyDeath()
                .build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_feedback) {
            sendFeedback();
        } else if (id == R.id.action_settings) {
            openSettings();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "android-feedback@rocketalert.me", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");

        String body = "";

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = "Version: " + pInfo.versionName;
            String osVersion = "Android version: " + Build.VERSION.RELEASE;
            String model = String.format("Device model: %s (%s)", Build.MANUFACTURER, Build.MODEL);
            String installationId = "Installation id: " +
                    ParseInstallation.getCurrentInstallation().getInstallationId();

            body = String.format("\n\n%s\n%s\n%s\n%s", version, osVersion, model, installationId);
        } catch(Exception ex) {
            Crashlytics.logException(ex);
        }

        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "Choose an Email client:"));
    }
}
