package com.example.friedegg.adapter;

import android.app.Activity;
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

import com.example.friedegg.R;
import com.example.friedegg.cache.MyVideoCache;
import com.example.friedegg.callback.LoadFinishCallBack;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.modul.CommentNumber;
import com.example.friedegg.modul.Video;
import com.example.friedegg.net.JSONParser;
import com.example.friedegg.okhttp.CommentCountsParser;
import com.example.friedegg.okhttp.OkHttpCallback;
import com.example.friedegg.okhttp.OkHttpProxy;
import com.example.friedegg.okhttp.VideoParser;
import com.example.friedegg.utils.NetWorkUtil;

import java.util.ArrayList;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private int page;
    private ArrayList<Video> mVideos;
    private int lastPosition = -1;
    private Activity mActivity;
    private LoadResultCallBack mLoadResultCallBack;
    private LoadFinishCallBack mLoadFinisCallBack;

    public VideoAdapter(Activity activity, LoadResultCallBack loadResultCallBack, LoadFinishCallBack loadFinisCallBack) {
        mActivity = activity;
        mLoadFinisCallBack = loadFinisCallBack;
        mLoadResultCallBack = loadResultCallBack;
        mVideos = new ArrayList<>();
    }

    private void setAnimation(View viewToAnimation, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimation.getContext(), R.anim.item_bottom_in);
            viewToAnimation.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(VideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.card.clearAnimation();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mVideos.size();
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
        if (NetWorkUtil.isNetWorkConnected(mActivity)) {
            loadData();
        } else {
            loadCache();
        }
    }

    private void loadData() {
        OkHttpProxy.get(Video.getUrlVideos(page), mActivity, new OkHttpCallback<ArrayList<Video>>(new VideoParser()) {
            @Override
            public void onSuccess(int code, ArrayList<Video> videos) {
                getCommentCounts(videos);
            }

            @Override
            public void onFailure(int code, String msg) {
                mLoadFinisCallBack.loadFinish(null);
            }
        });
    }

    private void loadCache() {

       /* mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
        mLoadFinisCallBack.loadFinish(null);
        MyVideoCache videoCacheUtil = MyVideoCache.getInstance(mActivity);
        if (page == 1) {
            mVideos.clear();
            ShowToast.Short(ConstantString.LOAD_NO_NETWORK);
        }
        mVideos.addAll(videoCacheUtil.getCacheByPage(page));
        notifyDataSetChanged();*/

    }

    //获取评论数量
    private void getCommentCounts(final ArrayList<Video> videos) {

        StringBuilder sb = new StringBuilder();
        for (Video video : videos) {
            sb.append("comment-" + video.getComment_ID() + ",");
        }
        OkHttpProxy.get(CommentNumber.getCommentCountsURL(sb.toString()), mActivity, new OkHttpCallback<ArrayList<CommentNumber>>(new CommentCountsParser()) {

            @Override
            public void onSuccess(int code, ArrayList<CommentNumber> commentNumbers) {
                mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                mLoadFinisCallBack.loadFinish(null);

                for (int i = 0; i < videos.size(); i++) {
                    videos.get(i).setComment_count(commentNumbers.get(i).getComments() + "");
                }

                if (page == 1) {
                    mVideos.clear();
                    MyVideoCache.getInstance(mActivity).clearAllCache();
                }

                mVideos.addAll(videos);
                notifyDataSetChanged();
                MyVideoCache.getInstance(mActivity).addResultCache(JSONParser.toString
                        (videos), page);
                //防止加载不到一页的情况
                if (mVideos.size() < 10) {
                    loadNextPage();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mLoadFinisCallBack.loadFinish(null);
                mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, msg);
            }
        });

    }
    static class VideoViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private TextView tv_like;
        private TextView tv_unlike;
        private TextView tv_comment_count;
        private TextView tv_un_support_des;
        private TextView tv_support_des;
        private ImageView img_share;
        private ImageView img;

        private LinearLayout ll_comment;
        private CardView card;

        public VideoViewHolder(View contentView) {
            super(contentView);

            img = (ImageView) contentView.findViewById(R.id.img);
            tv_title = (TextView) contentView.findViewById(R.id.tv_title);
            tv_like = (TextView) contentView.findViewById(R.id.tv_like);
            tv_unlike = (TextView) contentView.findViewById(R.id.tv_unlike);

            tv_comment_count = (TextView) contentView.findViewById(R.id.tv_comment_count);
            tv_un_support_des = (TextView) contentView.findViewById(R.id.tv_unsupport_des);
            tv_support_des = (TextView) contentView.findViewById(R.id.tv_support_des);

            img_share = (ImageView) contentView.findViewById(R.id.img_share);
            ll_comment = (LinearLayout) contentView.findViewById(R.id.ll_comment);
            card = (CardView) contentView.findViewById(R.id.card);
        }
    }
}
