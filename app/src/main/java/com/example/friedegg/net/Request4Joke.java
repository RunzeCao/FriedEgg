package com.example.friedegg.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.friedegg.modul.Joke;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CRZ on 2016/5/26 10:46.
 * Request4Joke
 */
public class Request4Joke extends Request<ArrayList<Joke>> {
    private Response.Listener<ArrayList<Joke>> mListener;

    public Request4Joke(String url, Response.Listener<ArrayList<Joke>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.mListener = listener;
    }

    @Override
    //服务器响应的数据进行解析，其中数据是以字节的形式存放在NetworkResponse的data变量中的，这里将数据取出然后组装成一个String，并传入Response的success()方法中即可。
    protected Response<ArrayList<Joke>> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonStr = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            jsonStr = new JSONObject(jsonStr).getJSONArray("comments").toString();
            //Json String 转换为 ArrayList<Joke>
            return Response.success((ArrayList<Joke>) JSONParser.toObject(jsonStr, new TypeToken<ArrayList<Joke>>() {}.getType()), HttpHeaderParser.parseCacheHeaders(networkResponse));
            //return Response.success((ArrayList<Joke>)new Gson().fromJson(jsonStr,new TypeToken<ArrayList<Joke>>(){}.getType()),HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }

    }

    @Override
    protected void deliverResponse(ArrayList<Joke> jokes) {
        mListener.onResponse(jokes);
    }
}
