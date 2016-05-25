package com.example.friedegg.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.friedegg.fragment.JokeFragment;
import com.example.friedegg.view.AutoLoadRecyclerView;

/**
 * Created by CRZ on 2016/5/25 16:06.
 * FreshNewsAdapter
 */
public class FreshNewsAdapter extends RecyclerView.Adapter<FreshNewsAdapter.ViewHolder> {

    public FreshNewsAdapter(FragmentActivity activity, AutoLoadRecyclerView mRecyclerView, JokeFragment jokeFragment, boolean isLargeMode) {
    }

    public void loadNextPage() {
    }

    public void loadFirst() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
