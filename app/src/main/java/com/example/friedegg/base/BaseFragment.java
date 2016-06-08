package com.example.friedegg.base;

import android.support.v4.app.Fragment;

import com.android.volley.Request;
import com.example.friedegg.net.RequestManager;
import com.example.friedegg.view.imageloader.ImageLoaderProxy;

/**
 * Created by 123 on 2016/5/24.
 *
 */
public class BaseFragment extends Fragment implements ConstantString{
    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestManager.cancelAll(this);
        ImageLoaderProxy.getImageLoader().clearMemoryCache();
    }

    protected void executeRequest(Request request) {
        RequestManager.addRequest(request, this);
    }
}
