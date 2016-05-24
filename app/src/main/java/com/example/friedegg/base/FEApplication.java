package com.example.friedegg.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by 123 on 2016/5/24.
 * 自定义Application
 */
public class FEApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
