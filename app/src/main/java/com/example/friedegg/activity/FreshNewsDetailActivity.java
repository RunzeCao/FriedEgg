package com.example.friedegg.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.friedegg.R;
import com.example.friedegg.base.BaseActivity;
import com.example.friedegg.fragment.FreshNewsDetailFragment;
import com.example.friedegg.modul.FreshNews;
import com.example.friedegg.utils.LogUtils;

import java.util.ArrayList;

public class FreshNewsDetailActivity extends BaseActivity {

    private static final String TAG = FreshNewsDetailActivity.class.getSimpleName();
    private ViewPager viewPager;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG,"onCreate");
        setContentView(R.layout.activity_fresh_news_detail);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.vp);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        assert mToolbar != null;
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_actionbar_back);
    }

    @Override
    protected void initData() {
        ArrayList<FreshNews> freshNewses = (ArrayList<FreshNews>) getIntent().getSerializableExtra(DATA_FRESH_NEWS);
        int position = getIntent().getIntExtra(DATA_POSITION, 0);
        viewPager.setAdapter(new FreshNewsDetailAdapter(getSupportFragmentManager(), freshNewses));
        viewPager.setCurrentItem(position);
    }

    private class FreshNewsDetailAdapter extends FragmentPagerAdapter {
        private ArrayList<FreshNews> freshNewses;

        public FreshNewsDetailAdapter(FragmentManager fm, ArrayList<FreshNews> freshNewses) {
            super(fm);
            this.freshNewses = freshNewses;
        }

        @Override
        public Fragment getItem(int position) {
            return FreshNewsDetailFragment.getInstance(freshNewses.get(position));
        }

        @Override
        public int getCount() {
            return freshNewses.size();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
