package com.example.friedegg.okhttp;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public abstract class OkHttpCallback<T> implements Callback {
    public static final int SUCCESS_OK = 200;
    public static final int ERROR_SERVER = 500;
    public static final int ERROR_CLIENT = 400;

    private static Handler handler = new Handler(Looper.getMainLooper());

    private OkHttpBaseParser<T> mParser;

    public OkHttpCallback(OkHttpBaseParser<T> mParser) {
        if (mParser == null) {
            throw new IllegalArgumentException("Parser can't be null");
        }
        this.mParser = mParser;
    }
    @Override
    public void onFailure(Call call, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailure(ERROR_SERVER, e.getMessage());
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        final T t = mParser.parse(response);
        final int code = mParser.code;

        if (code == SUCCESS_OK && t != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(code, t);
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onFailure(code, getErrorMsg(code));
                }
            });
        }
    }

    public abstract void onSuccess(int code, T t);

    public abstract void onFailure(int code, String msg);

    private static String getErrorMsg(int code) {

        switch (code) {
            case ERROR_CLIENT:
                return "客户端请求错误";
            case ERROR_SERVER:
                return "服务器错误";
            default:
                return "请求发生错误";
        }
    }


}
