package com.dux.push.push;

import android.content.Context;
import android.util.Log;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.util.VivoPushException;

public class Vivo extends BasePush implements IPushActionListener {
    public Vivo(Context context) {
        super(context);
    }

    @Override
    public void init() {
        try {
            PushClient.getInstance(context).initialize();
            PushClient.getInstance(context).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int i) {
                    Log.d(TAG, "onStateChanged2:" + i);
                    if (i == 0) {
                        Log.d(TAG, PushClient.getInstance(context).getRegId());
                        PushClient.getInstance(context).setTopic("all-member", this);
                    }
                }
            });

        } catch (VivoPushException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String setAlias(String alias) {
        PushClient.getInstance(context).bindAlias(alias, this);
        return null;
    }

    @Override
    public String unsetAlias(String alias) {
        PushClient.getInstance(context).unBindAlias(alias, this);
        return null;
    }

    @Override
    public String setTag(String tag) {
        PushClient.getInstance(context).setTopic(tag, this);
        return null;
    }

    @Override
    public String unsetTag(String tag) {
        PushClient.getInstance(context).delTopic(tag, this);
        return null;
    }

    @Override
    public void onStateChanged(int i) {
        Log.d(TAG, "onStateChanged:" + i);
    }
}
