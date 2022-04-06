package com.sssoft.base.devices.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

public class DriverUtil {


    public static class LogLevel {
        public static final String Verbose = "v";
        public static final String Debug = "d";
        public static final String Info = "i";
        public static final String Warn = "w";
        public static final String Error = "e";
    }

    public static void Log(String level, String tag, String msg)
    {
        switch (level) {
            case "i":
                Log.i(tag, msg);
                break;
            case "d":
                Log.d(tag, msg);
                break;
            case "v":
                Log.v(tag, msg);
                break;
            case "e":
                Log.e(tag, msg);
                break;
            case "w":
                Log.w(tag, msg);
                break;
            default:
                Log.e(tag, msg);
        }
    }

    /** 判断APP包是否存在
     * @param context
     * @param packageName 包名
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName){
        if (TextUtils.isEmpty(packageName)) {

            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            Log.d("package info", info.toString()); // Timber 是我打印 log 用的工具，这里只是打印一下 log
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("package info", "checkApkExist:"+ e.toString()); // Timber 是我打印 log 用的工具，这里只是打印一下 log
            return false;
        }
    }
}
