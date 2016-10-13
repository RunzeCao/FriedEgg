package com.example.friedegg.okhttp;


import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpProxy {

    private static OkHttpClient mHttpClient;

    public static void init() {
        synchronized (OkHttpProxy.class) {
            if (mHttpClient == null) {
                mHttpClient = new OkHttpClient();
            }
        }
    }

    public static OkHttpClient getInstance() {
        if (mHttpClient == null)
            init();
        return mHttpClient;
    }

    public static Call get(String url, OkHttpCallback responseCallback) {
        return get(url, null, responseCallback);
    }

    public static Call get(String url, Object tag, OkHttpCallback responseCallback) {
        Request.Builder builder = new Request.Builder().url(url);
        if (tag == null) {
            builder.tag(tag);
        }
        Request request = builder.build();
        Call call = getInstance().newCall(request);
        call.enqueue(responseCallback);
        return call;
    }

    public static Call post(String url, Map<String, String> params, OkHttpCallback responseCallback) {
        return post(url, params, null, responseCallback);
    }

    private static Call post(String url, Map<String, String> params, Object tag, OkHttpCallback responseCallback) {
        Request.Builder builder = new Request.Builder().url(url);
        if (tag != null) {
            builder.tag(tag);
        }
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                bodyBuilder.add(key, params.get(key));
            }
        }
        RequestBody body = bodyBuilder.build();
        builder.post(body);
        Request request = builder.build();
        Call call = getInstance().newCall(request);
        call.enqueue(responseCallback);
        return call;
    }

    public static void cancel(Object tag) {
        for (Call call : mHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

}
