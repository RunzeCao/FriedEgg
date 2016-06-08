package com.example.friedegg.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.friedegg.modul.Picture;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 润泽 on 2016/6/5.
 * 无聊图数据请求器
 */
public class Request4Picture extends Request<ArrayList<Picture>> {
    private Response.Listener<ArrayList<Picture>> listener;

    public Request4Picture(String url, Response.Listener<ArrayList<Picture>> listener, Response.ErrorListener errorlistener) {
        super(Method.GET, url, errorlistener);
        this.listener = listener;
    }

    @Override
    protected Response<ArrayList<Picture>> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonStr = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            jsonStr = new JSONObject(jsonStr).getJSONArray("comments").toString();

            ArrayList<Picture> pictures = (ArrayList<Picture>) JSONParser.toObject(jsonStr,new TypeToken<ArrayList<Picture>>(){}.getType());
            return Response.success(pictures,HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(ArrayList<Picture> pictures) {
            listener.onResponse(pictures);
    }
}
