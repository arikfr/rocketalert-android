package me.rocketalert.rocketalert;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.BaseAdapter;

import com.parse.PushService;

import me.rocketalert.rocketalert.R;

import java.util.List;

public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "me.rocketalert.rocketalert.SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_areas);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_areas);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d(TAG, "Changed: " + s);

        String channel = null;

        if (s.startsWith("area_")) {
            channel = s;
        } else if ("enableAllAreas".equals(s)) {
            channel = "all-android";
        }

        if (channel != null) {
            Boolean subscribe  = sharedPreferences.getBoolean(s, false);

            Log.i(TAG, String.format("Updating subscription of %s to %s", channel, subscribe));

            if (subscribe) {
                PushService.subscribe(getApplicationContext(), channel, Alert.class);
            } else {
                PushService.unsubscribe(getApplicationContext(), channel);
            }
        }
    }
}
