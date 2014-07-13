package me.rocketalert.rocketalert;

import android.content.SharedPreferences;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Application extends android.app.Application {
    String TAG = "me.rocketalert.Application";

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);

        Log.i(TAG, "Registering with Parse");

        Properties config  = new Properties();

        try {
            InputStream configFile = getAssets().open("config.properties");
            config.load(configFile);
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

        String applicationId = config.getProperty("parseApplicationId");
        String clientKey = config.getProperty("parseClientKey");

        try {
            Parse.initialize(getApplicationContext(), applicationId, clientKey);

            PushService.setDefaultPushCallback(getApplicationContext(), Alert.class);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Boolean enableAllAreas = prefs.getBoolean("enableAllAreas", true);

            if (enableAllAreas) {
                PushService.subscribe(getApplicationContext(), "all-android", Alert.class);
            }

            if (BuildConfig.DEBUG) {
                PushService.subscribe(getApplicationContext(), "test-android", Alert.class);
            }

            ParseInstallation.getCurrentInstallation().saveInBackground();
        } catch (NetworkOnMainThreadException e) {
            Crashlytics.logException(e);
        }

        Log.i(TAG, "Registration done.");
    }
}
