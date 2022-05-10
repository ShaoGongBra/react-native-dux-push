package com.dux.push;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

public class PushHelper {

    public static WritableMap parsePushMessage(String message) {

        if (message == null) {
            return null;
        }
        try {

            WritableMap param = Arguments.createMap();
//            param.putString("title", miPushMessage.getTitle());
//            param.putString("description", miPushMessage.getDescription());
//            param.putString("content", miPushMessage.getContent());
            return param;
        } catch (Exception e) {

            return null;
        }

    }


    public static Object getConfigValue(Context context, String key) throws Exception {
        if (context == null) {
            return "";
        }
        if (TextUtils.isEmpty(key)) {
            return "";
        }

        ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

        if (appInfo != null && appInfo.metaData != null && appInfo.metaData.containsKey(key)) {

            return String.valueOf(appInfo.metaData.get(key));
        } else {
            return "";
        }
    }
}
