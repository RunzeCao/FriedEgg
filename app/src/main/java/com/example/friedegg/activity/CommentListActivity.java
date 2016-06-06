package com.example.friedegg.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.friedegg.R;
import com.example.friedegg.adapter.CommentAdapter;
import com.example.friedegg.base.BaseActivity;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.utils.LogUtils;
import com.example.friedegg.utils.ShowToast;

/**
 * Created by CRZ on 2016/5/26 09:52.
 * 评论列表界面
 */
public class CommentListActivity extends BaseActivity implements LoadResultCallBack {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;

    private String thread_key;
    private String thread_id;
    private boolean isFromFreshNews;
    private CommentAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.loading);

        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("评论");
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isFromFreshNews) {
                    mAdapter.loadData4FreshNews();
                } else {
                    mAdapter.loadData();
                }
            }
        });


    }

    @Override
    protected void initData() {
        thread_key = getIntent().getStringExtra(DATA_THREAD_KEY);
        thread_id = getIntent().getStringExtra(DATA_THREAD_ID);
        isFromFreshNews = getIntent().getBooleanExtra(DATA_IS_FROM_FRESH_NEWS, false);
        if (isFromFreshNews) {
            mAdapter = new CommentAdapter(this, thread_id, isFromFreshNews, this);
            if (TextUtils.isEmpty(thread_id) || thread_id.equals("0")) {
                ShowToast.Short(FORBID_COMMENTS);
                finish();
            }
        } else {
            mAdapter = new CommentAdapter(this, thread_key, isFromFreshNews, this);
            if (TextUtils.isEmpty(thread_key) || thread_key.equals("0")) {
                ShowToast.Short(FORBID_COMMENTS);
                finish();
            }
        }
        mRecyclerView.setAdapter(mAdapter);
        if (isFromFreshNews) {
            mAdapter.loadData4FreshNews();
        } else {
            mAdapter.loadData();
        }
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment_list, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(this, PushCommentActivity.class);
                intent.putExtra(DATA_THREAD_ID, mAdapter.getThreadId());
                startActivityForResult(intent, 100);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(int result, Object object) {
        if (result == LoadResultCallBack.SUCCESS_NONE) {
            ShowToast.Short(NO_COMMENTS);
        }
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(int code, String msg) {
        mSwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);
        ShowToast.Short(LOAD_FAILED);
    }
}
