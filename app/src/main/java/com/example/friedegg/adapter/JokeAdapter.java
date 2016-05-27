package com.example.friedegg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.friedegg.R;
import com.example.friedegg.activity.CommentListActivity;
import com.example.friedegg.callback.LoadFinishCallBack;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.modul.CommentNumber;
import com.example.friedegg.modul.Joke;
import com.example.friedegg.net.Request4CommentCounts;
import com.example.friedegg.net.Request4Joke;
import com.example.friedegg.net.RequestManager;
import com.example.friedegg.utils.NetWorkUtil;
import com.example.friedegg.utils.String2TimeUtil;

import java.util.ArrayList;

/**
 * Created by CRZ on 2016/5/25 17:13.
 * JokeAdapter
 */
public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JokeViewHolder> {
    private int page;
    private int lastPosition = -1;
    private ArrayList<Joke> mJokes;
    private Activity mActivity;
    private LoadResultCallBack mLoadResultCallBack;
    private LoadFinishCallBack mLoadFinisCallBack;

    public JokeAdapter(Activity activity, LoadFinishCallBack loadFinishCallBack, LoadResultCallBack loadResultCallBack) {
        super();
        mActivity = activity;
        mLoadFinisCallBack = loadFinishCallBack;
        mLoadResultCallBack = loadResultCallBack;
        mJokes = new ArrayList<>();
    }

    protected void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_bottom_in);
            viewToAnimate.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    //当view画出屏幕是调用
    public void onViewDetachedFromWindow(JokeViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.card.clearAnimation();
    }

    @Override
    public JokeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_joke, parent, false);
        return new JokeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(JokeViewHolder holder, int position) {
        final Joke joke = mJokes.get(position);
        holder.tv_content.setText(joke.getComment_content());
        holder.tv_author.setText(joke.getComment_author());
        holder.tv_time.setText(String2TimeUtil.dateString2GoodExperienceFormat(joke.getComment_date()));
        holder.tv_like.setText(joke.getVote_positive());
        holder.tv_comment_count.setText(joke.getComment_counts());
        holder.tv_unlike.setText(joke.getVote_negative());
        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.ll_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CommentListActivity.class);
                intent.putExtra("thread_key", "comment-" + joke.getComment_ID());
                mActivity.startActivity(intent);
            }
        });
        setAnimation(holder.card, position);
    }

    @Override
    public int getItemCount() {
        return mJokes.size();
    }

    public void loadFirst() {
        page = 1;
        loadDataByNetworkType();
    }
    
    public void loadNextPage() {
        page++;
        loadDataByNetworkType();
    }

    private void loadDataByNetworkType() {
        if (NetWorkUtil.isNetWorkConnected(mActivity)){
            loadData();
        }else{
            loadCache();
        }
    }

    private void loadCache() {
    }

    private void loadData() {
        RequestManager.addRequest(new Request4Joke(Joke.getRequestUrl(page), new Response.Listener<ArrayList<Joke>>() {
            @Override
            public void onResponse(ArrayList<Joke> jokes) {
                getCommentCounts(jokes);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mLoadFinisCallBack.loadFinish(null);
            }
        }),mActivity);
        
    }

    private void getCommentCounts(final ArrayList<Joke> jokes) {
        StringBuilder builder = new StringBuilder();
        for (Joke joke:jokes){
            builder.append("comment-").append(joke.getComment_ID()).append(",");
        }
        String url = builder.toString();
        if (url.endsWith(",")){
            url = url.substring(0,url.length()-1);
        }
        RequestManager.addRequest(new Request4CommentCounts(CommentNumber.getCommentCountsURL(url), new Response.Listener<ArrayList<CommentNumber>>() {
            @Override
            public void onResponse(ArrayList<CommentNumber> commentNumbers) {
                for (int i = 0;i<jokes.size();i++){
                    jokes.get(i).setComment_counts(commentNumbers.get(i).getComments()+"");
                }
                if (page == 1){
                    mJokes.clear();
                }
                mJokes.addAll(jokes);
                notifyDataSetChanged();

                mLoadFinisCallBack.loadFinish(null);
                mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, volleyError.getMessage());
                mLoadFinisCallBack.loadFinish(null);
            }
        }),mActivity);
    }

    public class JokeViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_author;
        private TextView tv_time;
        private TextView tv_content;
        private TextView tv_like;
        private TextView tv_unlike;
        private TextView tv_comment_count;

        private ImageView img_share;
        private CardView card;
        private LinearLayout ll_comment;

        public JokeViewHolder(View contentView) {
            super(contentView);
            tv_author = (TextView) contentView.findViewById(R.id.tv_author);
            tv_content = (TextView) contentView.findViewById(R.id.tv_content);
            tv_time = (TextView) contentView.findViewById(R.id.tv_time);
            tv_like = (TextView) contentView.findViewById(R.id.tv_like);
            tv_unlike = (TextView) contentView.findViewById(R.id.tv_unlike);
            tv_comment_count = (TextView) contentView.findViewById(R.id.tv_comment_count);

            img_share = (ImageView) contentView.findViewById(R.id.img_share);
            card = (CardView) contentView.findViewById(R.id.card);
            ll_comment = (LinearLayout) contentView.findViewById(R.id.ll_comment);
        }
    }
}
