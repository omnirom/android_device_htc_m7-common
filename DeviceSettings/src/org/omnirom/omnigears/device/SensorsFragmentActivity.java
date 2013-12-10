/*
 * Copyright (C) 2013 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnirom.omnigears.device;

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.TwoStatePreference;
import android.util.Log;

import com.cyanogenmod.settings.device.R;

public class SensorsFragmentActivity extends PreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_ENABLED = "1";
    private static final String TAG = "DeviceSettings_Sensors";

    private static final String KEY_PROXIMITY_CALIBRATION = "proximity_calibration";
    private static final String FILE_PROXIMITY_KADC = "/sys/devices/virtual/optical_sensors/proximity/ps_kadc";
    public static final String KEY_POCKETDETECTION_METHOD = "pocketdetection_method";
    public static final String KEY_FLICK2WAKE_SWITCH = "flick2wake_switch";
    public static final String KEY_FLICK2SLEEP_SWITCH = "flick2sleep_switch";
    public static final String KEY_F2WSENSITIVITY_METHOD = "f2w_sensitivity_method";
    public static final String KEY_PICK2WAKE_SWITCH = "pick2wake_switch";
    public static final String KEY_F2STIMEOUT_METHOD = "f2s_time_out_method";

    private static boolean sPocketDetection;
    private static boolean sFlickPick;
    private ListPreference mPocketDetectionMethod;
    private TwoStatePreference mFlick2WakeSwitch;
    private TwoStatePreference mFlick2SleepSwitch;
    private ListPreference mF2WSensitivityMethod;
    private TwoStatePreference mPick2WakeSwitch;
    private ListPreference mF2STimeOutMethod;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        sPocketDetection = res.getBoolean(R.bool.has_pocketdetection);
        sFlickPick = res.getBoolean(R.bool.has_flick_option);

        addPreferencesFromResource(R.xml.sensors_preferences);

        final ListPreference proximityPref = (ListPreference)findPreference(KEY_PROXIMITY_CALIBRATION);
        proximityPref.setOnPreferenceChangeListener(this);

        if (sPocketDetection) {
            mPocketDetectionMethod = (ListPreference) findPreference(KEY_POCKETDETECTION_METHOD);
            mPocketDetectionMethod.setEnabled(PocketDetectionMethod.isSupported());
            mPocketDetectionMethod.setOnPreferenceChangeListener(new PocketDetectionMethod());
        }

        if (sFlickPick) {
            mFlick2WakeSwitch = (TwoStatePreference) findPreference(KEY_FLICK2WAKE_SWITCH);
            mFlick2WakeSwitch.setEnabled(Flick2WakeSwitch.isSupported());
            mFlick2WakeSwitch.setOnPreferenceChangeListener(new Flick2WakeSwitch());

            mFlick2SleepSwitch = (TwoStatePreference) findPreference(KEY_FLICK2SLEEP_SWITCH);
            mFlick2SleepSwitch.setEnabled(Flick2SleepSwitch.isSupported());
            mFlick2SleepSwitch.setOnPreferenceChangeListener(new Flick2SleepSwitch());

            mF2WSensitivityMethod = (ListPreference) findPreference(KEY_F2WSENSITIVITY_METHOD);
            mF2WSensitivityMethod.setEnabled(F2WSensitivityMethod.isSupported());
            mF2WSensitivityMethod.setOnPreferenceChangeListener(new F2WSensitivityMethod());

            mPick2WakeSwitch = (TwoStatePreference) findPreference(KEY_PICK2WAKE_SWITCH);
            mPick2WakeSwitch.setEnabled(Pick2WakeSwitch.isSupported());
            mPick2WakeSwitch.setOnPreferenceChangeListener(new Pick2WakeSwitch());

            mF2STimeOutMethod = (ListPreference) findPreference(KEY_F2STIMEOUT_METHOD);
            mF2STimeOutMethod.setEnabled(F2STimeOutMethod.isSupported());
            mF2STimeOutMethod.setOnPreferenceChangeListener(new F2STimeOutMethod());
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String boxValue;
        String key = preference.getKey();
        Log.w(TAG, "key: " + key);
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.e(TAG, "New proximity calibration value: " + (String)newValue);
        Utils.writeValue(FILE_PROXIMITY_KADC, (String)newValue);
        return true;
    }

    public static boolean isSupported(String FILE) {
        return Utils.fileExists(FILE);
    }

    public static void restore(Context context) {
        if (!isSupported(FILE_PROXIMITY_KADC)) {
            return;
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Utils.writeValue(FILE_PROXIMITY_KADC, sharedPrefs.getString(KEY_PROXIMITY_CALIBRATION, "0x0 0xFFFF3C2D"));
    }
}
