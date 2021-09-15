/*
 *   Copyright 2017 woxingxiao
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.itxca.promptx.seekbar;

import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.TypedValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 *
 * Project Name : PromptX
 * Package Name : com.itxca.promptx.seekbar
 * Create Time  : 2021-09-11 22:11
 *
 * @author woxingxiao
 * @link https://github.com/woxingxiao/BubbleSeekBar
 **/
class BubbleUtils {

    private static final String KEY_MIUI_MANE = "ro.miui.ui.version.name";
    private static Properties sProperties = new Properties();
    private static Boolean miui;

    static boolean isMIUI() {
        if (miui != null) {
            return miui;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                sProperties.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            miui = sProperties.containsKey(KEY_MIUI_MANE);
        } else {
            Class<?> clazz;
            try {
                clazz = Class.forName("android.os.SystemProperties");
                Method getMethod = clazz.getDeclaredMethod("get", String.class);
                String name = (String) getMethod.invoke(null, KEY_MIUI_MANE);
                miui = !TextUtils.isEmpty(name);
            } catch (Exception e) {
                miui = false;
            }
        }

        return miui;
    }

    static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }
}