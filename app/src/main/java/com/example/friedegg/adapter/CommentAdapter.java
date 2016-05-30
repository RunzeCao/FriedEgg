package com.example.friedegg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.friedegg.R;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.modul.Comment4FreshNews;
import com.example.friedegg.modul.Commentator;

import java.util.ArrayList;

/**
 * Created by CRZ on 2016/5/30 16:00.
 * CommentAdapter
 * This Adapter is for Comment List, the comments for fresh news is special
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private ArrayList<Commentator> commentators;
    private ArrayList<Comment4FreshNews> commentators4FreshNews;

    private Activity mActivity;
    private String thread_key;
    private String thread_id;
    private LoadResultCallBack mLoadResultCallBack;
    private boolean isFromFreshNews;

    public CommentAdapter(Activity activity, String thread_key, boolean isFromFreshNews, LoadResultCallBack loadResultCallBack) {
        mActivity = activity;
        this.thread_key = thread_key;
        this.isFromFreshNews = isFromFreshNews;
        mLoadResultCallBack = loadResultCallBack;
        if (isFromFreshNews) {
            commentators4FreshNews = new ArrayList<>();
        } else {
            commentators = new ArrayList<>();
        }
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Commentator.TYPE_HOT:
            case Commentator.TYPE_NEW:
                return new CommentViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_comment_flag, parent, false));
            case Commentator.TYPE_NORMAL:
                return new CommentViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_comment, parent,false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        public CommentViewHolder(View itemView) {
            super(itemView);
        }
    }
}
