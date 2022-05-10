package com.dux.push.push;

import android.content.Context;
import android.util.Log;
import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.dux.push.PushHelper;

import java.util.HashMap;
import java.util.Map;

public class Oppo extends BasePush implements ICallBackResultService {
    public Oppo(Context context) {
        super(context);
    }

    @Override
    public void init() {
        try {
            HeytapPushManager.init(context, true);
            HeytapPushManager.register(context, String.valueOf(PushHelper.getConfigValue(context, "com.oppo.push.app_key")), String.valueOf(PushHelper.getConfigValue(context, "com.oppo.push.app_secret")), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String setAlias(String alias) {

        Map<String, String> map = new HashMap<>();
        map.put("alias", alias);
        map.put("token", HeytapPushManager.getRegisterID());
        httpPost("http://sendapi.service.duxravel.com/api/alias", map);
        return null;
    }

    @Override
    public String unsetAlias(String alias) {
        Map<String, String> map = new HashMap<>();
        map.put("alias", alias);
        httpPost("http://sendapi.service.duxravel.com/api/alias/unset", map);
        return null;
    }

    @Override
    public String setTag(String tag) {
        Map<String, String> map = new HashMap<>();
        map.put("tag", tag);
        map.put("token", HeytapPushManager.getRegisterID());
        httpPost("http://sendapi.service.duxravel.com/api/tag", map);
        return null;
    }

    @Override
    public String unsetTag(String tag) {
        Map<String, String> map = new HashMap<>();
        map.put("tag", tag);
        map.put("token", HeytapPushManager.getRegisterID());
        httpPost("http://sendapi.service.duxravel.com/api/tag/unset", map);
        return null;
    }

    @Override
    public void onRegister(int i, String s) {
        Log.d(TAG, "注册成功" + s);
    }

    @Override
    public void onUnRegister(int i) {

    }

    @Override
    public void onSetPushTime(int i, String s) {

    }

    @Override
    public void onGetPushStatus(int i, int i1) {

    }

    @Override
    public void onGetNotificationStatus(int i, int i1) {

    }

    @Override
    public void onError(int i, String s) {

    }
}
