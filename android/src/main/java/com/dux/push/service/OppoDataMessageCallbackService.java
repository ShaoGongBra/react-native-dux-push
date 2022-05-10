package com.dux.push.service;

import android.content.Context;
import android.util.Log;
import com.heytap.msp.push.mode.DataMessage;
import com.heytap.msp.push.service.DataMessageCallbackService;
import com.dux.push.NotificationUtil;

public class OppoDataMessageCallbackService extends DataMessageCallbackService {
    @Override
    public void processMessage(Context context, DataMessage message) {
        super.processMessage(context, message);
        Log.d("rnduxpush", "processMessage  message" +message.toString());
        NotificationUtil.showNotification(context,message.getTitle(),message.getContent(), message.getNotifyID(), true);
    }
}
