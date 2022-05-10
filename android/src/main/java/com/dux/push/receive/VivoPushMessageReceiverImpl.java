package com.dux.push.receive;

import android.content.Context;
import android.util.Log;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.model.UnvarnishedMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class VivoPushMessageReceiverImpl extends OpenClientPushMessageReceiver {
    @Override
    public void onReceiveRegId(Context context, String s) {
        Log.d("rnduxpush", " onReceiveRegId= " + s);
    }

    @Override
    public void onTransmissionMessage(Context context, UnvarnishedMessage unvarnishedMessage) {
        super.onTransmissionMessage(context, unvarnishedMessage);
        Log.d("rnduxpush", "收到透传通知 onTransmissionMessage= " + unvarnishedMessage.getMessage());
    }

    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage unvarnishedMessage) {
        super.onNotificationMessageClicked(context, unvarnishedMessage);
        Log.d("rnduxpush", "收到通知点击回调 onTransmissionMessage= " + unvarnishedMessage.toString());
    }
}
