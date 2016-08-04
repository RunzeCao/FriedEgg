package com.example.friedegg.net;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.friedegg.modul.Comment4FreshNews;
import com.example.friedegg.utils.LogUtils;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;


public class Request4PushFreshComment extends Request<Boolean> {
    private Response.Listener<Boolean> listener;
    private HashMap<String, String> params;

    public Request4PushFreshComment(String url, Response.Listener<Boolean> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<Boolean> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String resultStr = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));

            JSONObject resultObj = new JSONObject(resultStr);
            String result = resultObj.optString("status");
            String error=resultObj.optString("error");
            if ( result.equals("ok")) {
                return Response.success(true, HttpHeaderParser.parseCacheHeaders(networkResponse));
            } else {
                return Response.error(new VolleyError("错误原因:" + error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new VolleyError(e));
        }
    }

    @Override
    protected void deliverResponse(Boolean aBoolean) {
        listener.onResponse(aBoolean);
    }

    @Override
    public HashMap<String, String> getParams() {
        return params;
    }


    /**
     * 包装请求参数
     *
     * @return
     */
    public static String getRequestURL(String post_id, String parent_id,String parent_name,
                                       String name,String email, String content) {
        content= MessageFormat.format("@<a href=\"#comment-{0}\">{1}</a>: {2}",parent_id,parent_name, content);
        return  getRequestURLNoParent(post_id,name,email,content);
    }

    /**
     * 包装无父评论的请求参数
     *
     * @return
     */
    public static String getRequestURLNoParent(String post_id,String name, String email,String content) {
        //方法1 转义
        //content= MessageFormat.format("%40%3Ca+href%3D%27%23comment-{0}%27%3E{1}%3C%2Fa%3E%3A+{2}",parent_id,parent_name, content);
        //方法2 URLEncoder（更优）
        try {
            name = URLEncoder.encode(name, "utf-8");
            content=URLEncoder.encode(content, "utf-8");
        }catch (Exception ex){
            LogUtils.d("URLEncoder error");
        }
        return   MessageFormat.format("{0}&post_id={1}&content={2}&email={3}&name={4}",
                Comment4FreshNews.URL_PUSH_COMMENT, post_id, content, email, name);
    }

}
