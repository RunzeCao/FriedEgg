package com.example.friedegg.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.friedegg.R;
import com.example.friedegg.adapter.JokeAdapter;
import com.example.friedegg.base.BaseFragment;
import com.example.friedegg.base.ConstantString;
import com.example.friedegg.callback.LoadMoreListener;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.utils.ShowToast;
import com.example.friedegg.view.AutoLoadRecyclerView;

/**
 * Created by 123 on 2016/5/24.
 * 段子界面
 */
public class JokeFragment extends BaseFragment implements LoadResultCallBack {
    private AutoLoadRecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar loading;
    private JokeAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 必须在onCreate()方法中调用setHasOptionsMenu(true)方法来指示这个Fragment应该作为可选菜单的添加项
        // （否则，这个Fragment不接受对onCreateOptionsMenu()方法的调用）。
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_load, container, false);
        mRecyclerView = (AutoLoadRecyclerView) view.findViewById(R.id.auto_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        loading = (ProgressBar) view.findViewById(R.id.loading);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.loadFirst();
            }
        });
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                mAdapter.loadNextPage();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setOnPauseListenerParams(false, true);

        mAdapter = new JokeAdapter(getActivity(), mRecyclerView, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.loadFirst();
        loading.setVisibility(View.VISIBLE);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            mSwipeRefreshLayout.setRefreshing(true);
            mAdapter.loadFirst();
            return true;
        }
        return false;
    }

    @Override
    public void onSuccess(int result, Object object) {
        loading.setVisibility(View.GONE);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onError(int code, String msg) {
        loading.setVisibility(View.VISIBLE);
        ShowToast.Short(ConstantString.LOAD_FAILED);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
