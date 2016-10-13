package com.example.friedegg.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.friedegg.R;
import com.example.friedegg.base.BaseFragment;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.view.AutoLoadRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoFragment extends BaseFragment implements LoadResultCallBack {

    @BindView(R.id.recycler_view)
    AutoLoadRecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.loading)
    ProgressBar loading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_load, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onSuccess(int result, Object object) {

    }

    @Override
    public void onError(int code, String msg) {

    }
}
