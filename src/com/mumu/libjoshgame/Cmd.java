/*
 * Copyright (C) 2017 The Josh Tool Project
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

package com.mumu.libjoshgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cmd {
    private final String TAG = "Cmd";
    private static String mAdbDefaultIP = "127.0.0.1";
    private static int mMaxDevices = 3;
    private String[] mAdbDevicePort = {"62001", "62025", "62026"};
    private boolean[] mDeviceAvailable = {false, false, false};

    public Cmd() {
        tryConnect();
    }

    public String runCommand(String cmd) {
        return runCommand(cmd, 2);
    }

    public String runCommand(String cmd, int idx) {
        String fullCmd = "-s " + mAdbDefaultIP + ":" + mAdbDevicePort[idx] + " shell " + cmd;
        String result = "";

        try {
            result = runAdbCommandInternal(fullCmd);
        } catch (Exception e) {
            Log.e(TAG, "Run adb command failed.");
        }
        return result;
    }

    private void tryConnect() {
        for(int i = 0; i < mMaxDevices; i++) {
            String cmd = "connect " + mAdbDefaultIP + ":" + mAdbDevicePort[i];
            try {
                runAdbCommandInternal(cmd);
            } catch (Exception e) {
                Log.w(TAG, "Adb connect failed: " + cmd);
            }
        }
    }

    public int getAdbDevices() {
        String result = "";

        try {
            result = runAdbCommandInternal("devices");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "adb devices: " + result);
        return 1;

    }

    private String runAdbCommandInternal(String cmd) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec("adb " + cmd);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(process.getErrorStream()));

        // read the output from the command
        String s, result = "";
        while ((s = stdInput.readLine()) != null) {
            result += s;
        }

        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            Log.w(TAG, "Fail to execute command " + cmd + ": " + s);
        }

        return result;
    }
}
