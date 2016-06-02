package com.example.friedegg.net;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.friedegg.callback.LoadFinishCallBack;
import com.example.friedegg.modul.Commentator;
import com.example.friedegg.utils.TextUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by CRZ on 2016/6/2 15:44.
 * Request4CommentList
 */
public class Request4CommentList extends Request<ArrayList<Commentator>> {
    private Response.Listener<ArrayList<Commentator>> listener;
    private LoadFinishCallBack callBack;

    public Request4CommentList(String url, Response.Listener<ArrayList<Commentator>> listener, Response.ErrorListener errorListener, LoadFinishCallBack loadFinishCallBack) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
        this.callBack = loadFinishCallBack;
    }

    @Override
    protected Response<ArrayList<Commentator>> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonStr= new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            JSONObject jsonObject = new JSONObject(jsonStr);
            String allThreadId = jsonObject.getString("response").replace("[", "").replace("]", "").replace("\"", "");
            String[] threadIds = allThreadId.split("\\,");

            callBack.loadFinish(jsonObject.optJSONObject("thread").optString("thread_id"));

            if (TextUtils.isEmpty(threadIds[0])){
                return Response.success(new ArrayList<Commentator>(), HttpHeaderParser.parseCacheHeaders(networkResponse));
            }else{
                //然后根据thread_id再去获得对应的评论和作者信息
                JSONObject parentPostsJson = jsonObject.getJSONObject("parentPosts");
                //找出热门评论
                String hotPosts = jsonObject.getString("hotPosts").replace("[", "").replace("]", "").replace("\"", "");
                String[] allHotPosts = hotPosts.split("\\,");
                List<String> allHotPostsArray = Arrays.asList(allHotPosts);
                ArrayList<Commentator> commentators = new ArrayList<>();

                for (String threadId:threadIds){
                    Commentator  commentator = new Commentator();
                    JSONObject threadObject = parentPostsJson.getJSONObject(threadId);
                    if (allHotPostsArray.contains(threadId)){
                        commentator.setTag(Commentator.TAG_HOT);
                    }else{
                        commentator.setTag(Commentator.TAG_NORMAL);
                    }
                    commentator.setPost_id(threadObject.optString("post_id"));
                    commentator.setParent_id(threadObject.optString("parent_id"));
                    String parentsString = threadObject.optString("parents").replace("[", "").replace("]", "").replace("\"", "");
                    String[] parents = parentsString.split("\\,");
                    commentator.setParents(parents);
                    //如果第一个数据为空，则只有一层
                    if (TextUtil.isNull(parents[0])) {
                        commentator.setFloorNum(1);
                    } else {
                        commentator.setFloorNum(parents.length + 1);
                    }
                    commentator.setMessage(threadObject.optString("message"));
                    commentator.setCreated_at(threadObject.optString("created_at"));
                    JSONObject authorObject = threadObject.optJSONObject("author");
                    commentator.setName(authorObject.optString("name"));
                    commentator.setAvatar_url(authorObject.optString("avatar_url"));
                    commentator.setType(Commentator.TYPE_NORMAL);
                    commentators.add(commentator);
                }
                return Response.success(commentators, HttpHeaderParser.parseCacheHeaders(networkResponse));

            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(ArrayList<Commentator> respose) {
        listener.onResponse(respose);
    }
}
