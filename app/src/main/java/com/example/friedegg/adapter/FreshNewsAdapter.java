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
import com.example.friedegg.activity.FreshNewsDetailActivity;
import com.example.friedegg.base.ConstantString;
import com.example.friedegg.cache.MyFreshNewsCache;
import com.example.friedegg.callback.LoadFinishCallBack;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.modul.FreshNews;
import com.example.friedegg.net.JSONParser;
import com.example.friedegg.net.Request4FreshNews;
import com.example.friedegg.net.RequestManager;
import com.example.friedegg.utils.LogUtils;
import com.example.friedegg.utils.NetWorkUtil;
import com.example.friedegg.utils.ShareUtils;
import com.example.friedegg.utils.ShowToast;
import com.example.friedegg.view.imageloader.ImageLoaderProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;


public class FreshNewsAdapter extends RecyclerView.Adapter<FreshNewsAdapter.ViewHolder> {
    private static final String TAG = FreshNewsAdapter.class.getSimpleName();
    private int page;
    private int lastPosition = -1;
    private boolean isLargeMode;
    private Activity mActivity;
    private DisplayImageOptions options;
    private ArrayList<FreshNews> mFreshNews;
    private LoadFinishCallBack mLoadFinisCallBack;
    private LoadResultCallBack mLoadResultCallBack;

    public FreshNewsAdapter(Activity activity, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack, boolean isLargeMode) {
        this.mActivity = activity;
        this.isLargeMode = isLargeMode;
        this.mLoadFinisCallBack = loadFinisCallBack;
        this.mLoadResultCallBack = loadResultCallBack;
        mFreshNews = new ArrayList<>();

        int loadingResource = isLargeMode ? R.drawable.ic_loading_large : R.drawable.ic_loading_small;
        options = ImageLoaderProxy.getOptions4PictureList(loadingResource);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_bottom_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public void loadNextPage() {
        page++;
        loadDataByNetworkType();
    }

    public void loadFirst() {
        page = 1;
        loadDataByNetworkType();
    }

    private void loadDataByNetworkType() {
        if (NetWorkUtil.isNetWorkConnected(mActivity)) {
            RequestManager.addRequest(new Request4FreshNews(FreshNews.getUrlFreshNews(page),
                    new Response.Listener<ArrayList<FreshNews>>() {
                        @Override
                        public void onResponse(ArrayList<FreshNews> freshNewses) {
                            mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                            mLoadFinisCallBack.loadFinish(null);

                            if (page == 1) {
                                mFreshNews.clear();
                                MyFreshNewsCache.getInstance(mActivity).clearAllCache();
                            }

                            mFreshNews.addAll(freshNewses);
                            notifyDataSetChanged();

                            MyFreshNewsCache.getInstance(mActivity).addResultCache(JSONParser.toString(freshNewses),
                                    page);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, volleyError.getMessage());
                            mLoadFinisCallBack.loadFinish(null);
                        }
                    }), mActivity);
        }else{
            mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
            mLoadFinisCallBack.loadFinish(null);

            if (page == 1) {
                mFreshNews.clear();
                ShowToast.Short(ConstantString.LOAD_NO_NETWORK);
            }

            mFreshNews.addAll(MyFreshNewsCache.getInstance(mActivity).getCacheByPage(page));
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = isLargeMode ? R.layout.item_fresh_news : R.layout.item_fresh_news_small;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FreshNews freshNews = mFreshNews.get(position);
        ImageLoaderProxy.displayImage(freshNews.getCustomFields().getThumb_m(), holder.img, options);
        holder.tv_title.setText(freshNews.getTitle());
        holder.tv_info.setText(freshNews.getAuthor().getName() + "@" + freshNews.getTags().getTitle());
        holder.tv_views.setText("浏览" + freshNews.getCustomFields().getViews() + "次");

        if (isLargeMode) {
            holder.tv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareUtils.shareText(mActivity, freshNews.getTitle() + " " + freshNews.getUrl());
                }
            });

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDetailActivity(position);
                }
            });

            setAnimation(holder.card, position);
        } else {
            holder.ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDetailActivity(position);
                }
            });
            setAnimation(holder.ll_content, position);
        }
    }

    private void toDetailActivity(int position) {
        Intent intent = new Intent(mActivity, FreshNewsDetailActivity.class);
        intent.putExtra(ConstantString.DATA_FRESH_NEWS, mFreshNews);
        intent.putExtra(ConstantString.DATA_POSITION, position);
        LogUtils.d(TAG,position+"");
        mActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mFreshNews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_info;
        TextView tv_views;
        TextView tv_share;
        ImageView img;
        CardView card;
        LinearLayout ll_content;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);
            tv_views = (TextView) itemView.findViewById(R.id.tv_views);
            tv_share = (TextView) itemView.findViewById(R.id.tv_share);
            img = (ImageView) itemView.findViewById(R.id.img);
            card = (CardView) itemView.findViewById(R.id.card);
            ll_content = (LinearLayout) itemView.findViewById(R.id.ll_content);
        }
    }
}
