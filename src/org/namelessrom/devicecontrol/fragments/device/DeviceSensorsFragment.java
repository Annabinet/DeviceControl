/*
 *  Copyright (C) 2012 The CyanogenMod Project
 *  Modifications Copyright (C) 2013-2014 Alexander "Evisceration" Martinz
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

package org.namelessrom.devicecontrol.fragments.device;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.namelessrom.devicecontrol.R;
import org.namelessrom.devicecontrol.utils.threads.WriteAndForget;
import org.namelessrom.devicecontrol.utils.Utils;
import org.namelessrom.devicecontrol.utils.constants.DeviceConstants;
import org.namelessrom.devicecontrol.utils.constants.FileConstants;

public class DeviceSensorsFragment extends PreferenceFragment
        implements DeviceConstants, FileConstants {

    //==============================================================================================
    // Fields
    //==============================================================================================
    private static final boolean sHasGyro = Utils.fileExists(FILE_USE_GYRO_CALIB);
    private CheckBoxPreference mGyroUse;
    private Preference mGyroCalibrate;

    //==============================================================================================
    // Overridden Methods
    //==============================================================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.device_sensors_preferences);

        if (!sHasGyro) {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_SENSORS));
        } else {
            mGyroUse = (CheckBoxPreference) findPreference(KEY_USE_GYRO_CALIBRATION);
            mGyroUse.setChecked(Utils.readOneLine(FILE_USE_GYRO_CALIB).equals("1"));
            mGyroCalibrate = findPreference(KEY_CALIBRATE_GYRO);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        if (preference == mGyroUse) {
            new WriteAndForget(FILE_USE_GYRO_CALIB, (mGyroUse.isChecked() ? "1" : "0")).run();
        } else if (preference == mGyroCalibrate) {
            new WriteAndForget(FILE_USE_GYRO_CALIB, "0")
                    .append(FILE_USE_GYRO_CALIB, "1").run();
            Utils.showDialog(getActivity(),
                    "Calibration done", "The gyroscope has been successfully calibrated!");
        }
        return true;
    }

    //==============================================================================================
    // Methods
    //==============================================================================================

    public static boolean isSupported() {
        return (sHasGyro);
    }
}
