package com.dux.push;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.heytap.msp.push.HeytapPushManager;
import com.dux.push.push.BasePush;
import com.dux.push.push.Hms;
import com.dux.push.push.Oppo;
import com.dux.push.push.Vivo;
import com.dux.push.push.XM;
import com.vivo.push.PushClient;

public class RNDuxPushModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private BasePush push;

  public RNDuxPushModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNDuxPush";
  }

  @ReactMethod
  public void init(String appkey, String secret, ReadableMap readableMap) {
    try {
      String brand = SystemUtil.getDeviceBrand();
      if (brand.toUpperCase().startsWith("HUAWEI") && (readableMap == null || !readableMap.getBoolean("hms"))) {
        push = new Hms(reactContext);
      } else if ((brand.toUpperCase().startsWith("OPPO") || brand.toUpperCase().startsWith("一加") || brand.toUpperCase().startsWith("REALME")) && (readableMap == null || !readableMap.getBoolean("oppo"))) {
        push = new Oppo(reactContext);
      } else if (brand.toUpperCase().startsWith("VIVO") && (readableMap == null || !readableMap.getBoolean("vivo")) && PushClient.getInstance(reactContext).isSupport()) {
        push = new Vivo(reactContext);
      } else {
        // 其他走小米推送
        push = new XM(reactContext);
      }
      push.init(appkey, secret);

      HeytapPushManager.requestNotificationPermission();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void setAlias(String alias, final Promise promise) {
    try {
      String regId = push.setAlias(alias);
      if (!TextUtils.isEmpty(regId)) {
        if (promise != null) {
          promise.resolve(regId);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void unsetAlias(String alias, final Promise promise) {
    try {
      String regId = push.unsetAlias(alias);
      if (!TextUtils.isEmpty(regId)) {
        if (promise != null) {
          promise.resolve(regId);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void setTag(String tag, final Promise promise) {
    try {
      String regId = push.setTag(tag);
      if (!TextUtils.isEmpty(regId)) {
        if (promise != null) {
          promise.resolve(regId);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void unsetTag(String tag, final Promise promise) {
    try {
      String regId = push.unsetTag(tag);
      if (!TextUtils.isEmpty(regId)) {
        if (promise != null) {
          promise.resolve(regId);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void notify(String title, String body) {
    int id = reactContext.getResources().getIdentifier("ic_launcher", "mipmap", reactContext.getPackageName());
    PendingIntent pendingIntent;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
      pendingIntent = PendingIntent.getActivity(reactContext, 0, new Intent(),
        PendingIntent.FLAG_IMMUTABLE);
    } else {
      pendingIntent = PendingIntent.getActivity(reactContext, 0, new Intent(),
        PendingIntent.FLAG_ONE_SHOT);
    }
    final NotificationManager notificationManager = (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      int importance = NotificationManager.IMPORTANCE_HIGH;
      @SuppressLint("WrongConstant")
      NotificationChannel notificationChannel = new NotificationChannel("ID", "NAME", importance);
      notificationChannel.enableVibration(true);
      notificationChannel.setShowBadge(true);
      notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(notificationChannel);
      }
    }
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(reactContext, "ID");
    mBuilder.setSmallIcon(id);
    mBuilder.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
    mBuilder.setDefaults(Notification.DEFAULT_ALL);
    mBuilder.setAutoCancel(true);
    mBuilder.setContentTitle(title);
    mBuilder.setContentText(body);
    mBuilder.setContentIntent(pendingIntent);
    final Notification notify = mBuilder.build();

    //随机id 1000-2000
    final int notifyId = (int) (Math.random() * 1000 + 1000);
    if (notificationManager != null) {
      notificationManager.notify(notifyId, notify);
    }
  }

  @ReactMethod
  public void notificationsEnabled(Promise promise) {
    boolean areNotificationsEnabled = NotificationManagerCompat.from(getCurrentActivity().getApplication()).areNotificationsEnabled();
    promise.resolve(areNotificationsEnabled);
  }

  @ReactMethod
  public void goPushSetting() {
    Activity activity = getCurrentActivity();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // 这种方案适用于 API 26, 即8.0（含8.0）以上可以用
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
      intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
      intent.putExtra(Settings.EXTRA_CHANNEL_ID, activity.getApplicationInfo().uid);
      activity.startActivity(intent);
    } else {
      toPermissionSetting();
    }
  }


  /**
   * 跳转到权限设置
   */
  @ReactMethod
  public void toPermissionSetting() {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
      toSystemConfig();
    } else {
      try {
        toApplicationInfo();
      } catch (Exception e) {
        e.printStackTrace();
        toSystemConfig();
      }
    }
  }

  /**
   * 应用信息界面
   *
   */
  @ReactMethod
  public void toApplicationInfo() {
    Activity activity = getCurrentActivity();
    Intent localIntent = new Intent();
    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
    activity.startActivity(localIntent);
  }

  /**
   * 系统设置界面
   *
   */
  @ReactMethod
  public void toSystemConfig() {
    try {
      Intent intent = new Intent(Settings.ACTION_SETTINGS);
      getCurrentActivity().startActivity(intent);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
