package com.example.friedegg.callback;

/**
 * Created by CRZ on 2016/5/25 16:40.
 * LoadResultCallBack
 */
public interface LoadResultCallBack {

    int SUCCESS_OK = 1001;
    int SUCCESS_NONE = 1002;
    int ERROR_NET = 1003;

    void onSuccess(int result, Object object);

    void onError(int code, String msg);
}
