package com.example.friedegg.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.friedegg.R;
import com.example.friedegg.activity.CommentListActivity;
import com.example.friedegg.activity.ImageDetailActivity;
import com.example.friedegg.base.BaseActivity;
import com.example.friedegg.base.ConstantString;
import com.example.friedegg.cache.MyPictureCache;
import com.example.friedegg.callback.LoadFinishCallBack;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.modul.CommentNumber;
import com.example.friedegg.modul.Picture;
import com.example.friedegg.net.JSONParser;
import com.example.friedegg.net.Request4CommentCounts;
import com.example.friedegg.net.Request4Picture;
import com.example.friedegg.net.RequestManager;
import com.example.friedegg.utils.FileUtil;
import com.example.friedegg.utils.NetWorkUtil;
import com.example.friedegg.utils.ShareUtils;
import com.example.friedegg.utils.ShowToast;
import com.example.friedegg.utils.String2TimeUtil;
import com.example.friedegg.utils.TextUtil;
import com.example.friedegg.view.ShowMaxImageView;
import com.example.friedegg.view.imageloader.ImageLoaderProxy;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by 润泽 on 2016/6/4.
 * PictureAdapter
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {
    private int page;
    private int lastPosition = -1;
    private ArrayList<Picture> pictures;
    private LoadFinishCallBack mLoadFinisCallBack;
    private LoadResultCallBack mLoadResultCallBack;
    private Activity mActivity;
    private boolean isWifiConnected;
    private Picture.PictureType mType;
    private LoadFinishCallBack mSaveFileCallBack;

    public PictureAdapter(Activity activity, LoadResultCallBack loadResultCallBack, LoadFinishCallBack loadFinisCallBack, Picture.PictureType type) {
        mActivity = activity;
        mType = type;
        mLoadFinisCallBack = loadFinisCallBack;
        mLoadResultCallBack = loadResultCallBack;
        pictures = new ArrayList<>();
        isWifiConnected = NetWorkUtil.isWifiConnected(mActivity);
    }

    private void setAnimation(View viewToAnimation, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimation.getContext(), R.anim.item_bottom_in);
            viewToAnimation.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(PictureViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.card.clearAnimation();
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pic, parent, false);
        return new PictureViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PictureViewHolder holder, int position) {
        final  Picture picture = pictures.get(position);
        String picUrl = picture.getPics()[0];

        if (picUrl.endsWith(".gif")){
            holder.img_gif.setVisibility(View.VISIBLE);
            //非WIFI网络情况下，GIF图只加载缩略图，详情页才加载真实图片
            if (!isWifiConnected){
                picUrl =picUrl.replace("mw600", "small").replace("mw1200", "small").replace("large", "small");
            }
        }else {
            holder.img_gif.setVisibility(View.GONE);
        }

        holder.progress.setProgress(0);
        holder.progress.setVisibility(View.VISIBLE);

        ImageLoaderProxy.displayImageList(picUrl, holder.img, R.drawable.ic_loading_large, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                holder.progress.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                holder.progress.setProgress((int) (current * 100f / total));
            }
        });
        if (TextUtil.isNull(picture.getText_content().trim())) {
            holder.tv_content.setVisibility(View.GONE);
        } else {
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.tv_content.setText(picture.getText_content().trim());
        }

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ImageDetailActivity.class);

                intent.putExtra(BaseActivity.DATA_IMAGE_AUTHOR, picture.getComment_author());
                intent.putExtra(BaseActivity.DATA_IMAGE_URL, picture.getPics());
                intent.putExtra(BaseActivity.DATA_IMAGE_ID, picture.getComment_ID());
                intent.putExtra(BaseActivity.DATA_THREAD_KEY, "comment-" + picture.getComment_ID());

                if (picture.getPics()[0].endsWith(".gif")) {
                    intent.putExtra(BaseActivity.DATA_IS_NEED_WEBVIEW, true);
                }

                mActivity.startActivity(intent);
            }
        });

        holder.tv_author.setText(picture.getComment_author());
        holder.tv_time.setText(String2TimeUtil.dateString2GoodExperienceFormat(picture.getComment_date()));
        holder.tv_like.setText(picture.getVote_positive());
        holder.tv_comment_count.setText(picture.getComment_counts());
        holder.tv_unlike.setText(picture.getVote_negative());
        //用于恢复默认的文字
        holder.tv_like.setTypeface(Typeface.DEFAULT);
        holder.tv_like.setTextColor(ContextCompat.getColor(mActivity,R.color.secondary_text_default_material_light));
        holder.tv_support_des.setTextColor(ContextCompat.getColor(mActivity,R.color.secondary_text_default_material_light));
        holder.tv_unlike.setTypeface(Typeface.DEFAULT);
        holder.tv_unlike.setTextColor(ContextCompat.getColor(mActivity,R.color.secondary_text_default_material_light));
        holder.tv_un_support_des.setTextColor(ContextCompat.getColor(mActivity,R.color.secondary_text_default_material_light));

        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(mActivity).setItems(R.array.joke_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                ShareUtils.shareText(mActivity, picture.getPics()[0]);
                                break;
                            case 1:
                                FileUtil.savePicture(mActivity,picture.getPics()[0],mSaveFileCallBack);
                                break;
                        }
                    }
                }).show();
            }
        });
        holder.ll_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CommentListActivity.class);
                intent.putExtra(BaseActivity.DATA_THREAD_KEY, "comment-" + picture.getComment_ID());
                mActivity.startActivity(intent);
            }
        });

        setAnimation(holder.card, position);

    }

    @Override
    public int getItemCount() {
        return pictures.size();
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

    private void loadCache() {
        mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
        mLoadFinisCallBack.loadFinish(null);
        MyPictureCache pictureCacheUtil = MyPictureCache.getInstance(mActivity);
        if (page == 1) {
            pictures.clear();
            ShowToast.Short(ConstantString.LOAD_NO_NETWORK);
        }
        pictures.addAll(pictureCacheUtil.getCacheByPage(page));
        notifyDataSetChanged();
    }

    private void loadData() {
        RequestManager.addRequest(new Request4Picture(Picture.getRequestUrl(mType, page), new Response.Listener<ArrayList<Picture>>() {
            @Override
            public void onResponse(ArrayList<Picture> pictures) {
                getCommentCounts(pictures);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, volleyError.getMessage());
                mLoadFinisCallBack.loadFinish(null);
            }
        }), mActivity);
    }

    private void getCommentCounts(final ArrayList<Picture> pictures) {
        StringBuffer sb = new StringBuffer();
        for (Picture picture : pictures) {
            sb.append("comment-" + picture.getComment_ID() + ",");
        }

        RequestManager.addRequest(new Request4CommentCounts(CommentNumber.getCommentCountsURL(sb.toString()), new Response.Listener<ArrayList<CommentNumber>>() {
            @Override
            public void onResponse(ArrayList<CommentNumber> commentNumbers) {
                mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                mLoadFinisCallBack.loadFinish(null);
                for (int i = 0; i < pictures.size(); i++) {
                    pictures.get(i).setComment_counts(commentNumbers.get(i).getComments() + "");
                }
                if (page == 1) {
                    PictureAdapter.this.pictures.clear();
                    MyPictureCache.getInstance(mActivity).clearAllCache();
                }
                PictureAdapter.this.pictures.addAll(pictures);
                notifyDataSetChanged();
                //加载完毕后缓存
                MyPictureCache.getInstance(mActivity).addResultCache(JSONParser.toString
                        (pictures), page);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ShowToast.Short(ConstantString.LOAD_FAILED);
                mLoadFinisCallBack.loadFinish(null);
                mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, volleyError.getMessage());
            }
        }), mActivity);
    }

    public void setSaveFileCallBack(LoadFinishCallBack mSaveFileCallBack) {
        this.mSaveFileCallBack = mSaveFileCallBack;
    }

    public void setIsWifi(boolean isWifiConnected) {
        this.isWifiConnected = isWifiConnected;
    }

    public static class PictureViewHolder extends RecyclerView.ViewHolder {
        TextView tv_author;
        TextView tv_time;
        TextView tv_content;
        TextView tv_like;
        TextView tv_unlike;
        TextView tv_comment_count;
        TextView tv_un_support_des;
        TextView tv_support_des;
        ImageView img_share;
        ImageView img_gif;
        ShowMaxImageView img;

        LinearLayout ll_comment;
        ProgressBar progress;
        CardView card;
        public PictureViewHolder(View itemView) {
            super(itemView);
            tv_author = (TextView) itemView.findViewById(R.id.tv_author);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_like = (TextView) itemView.findViewById(R.id.tv_like);
            tv_unlike = (TextView) itemView.findViewById(R.id.tv_unlike);
            tv_comment_count = (TextView) itemView.findViewById(R.id.tv_comment_count);

            tv_un_support_des = (TextView) itemView.findViewById(R.id.tv_unsupport_des);
            tv_support_des = (TextView) itemView.findViewById(R.id.tv_support_des);
            img_share = (ImageView) itemView.findViewById(R.id.img_share);
            card = (CardView) itemView.findViewById(R.id.card);
            ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
            img_gif = (ImageView) itemView.findViewById(R.id.img_gif);
            img = (ShowMaxImageView) itemView.findViewById(R.id.img);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }
}
