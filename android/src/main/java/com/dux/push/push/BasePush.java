package com.dux.push.push;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.dux.push.IPush;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public abstract class BasePush implements IPush {
    public Context context;

    public static String TAG = "rnduxpush";

    protected String appId;
    protected String appKey;

    public BasePush(Context context) {
        this.context = context;
    }

    @Override
    public void init(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
        this.init();
    }

    protected void httpPost(String url, Map<String, String> map) {
        OkHttpClient client = new OkHttpClient();

        map.put("sign", "qwqwqwqwqwqw");
        Gson gson = new Gson();
        String json = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .header("appkey", appId)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.d("response:", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
