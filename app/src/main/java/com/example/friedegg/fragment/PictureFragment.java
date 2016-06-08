package com.example.friedegg.fragment;

import android.media.MediaScannerConnection;
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
import com.example.friedegg.adapter.PictureAdapter;
import com.example.friedegg.base.BaseFragment;
import com.example.friedegg.base.ConstantString;
import com.example.friedegg.callback.LoadFinishCallBack;
import com.example.friedegg.callback.LoadMoreListener;
import com.example.friedegg.callback.LoadResultCallBack;
import com.example.friedegg.modul.NetWorkEvent;
import com.example.friedegg.modul.Picture;
import com.example.friedegg.utils.FEMediaScannerConnectionClient;
import com.example.friedegg.utils.NetWorkUtil;
import com.example.friedegg.utils.ShowToast;
import com.example.friedegg.view.AutoLoadRecyclerView;
import com.example.friedegg.view.imageloader.ImageLoaderProxy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * Created by 123 on 2016/5/24.
 * PictureFragment
 */
public class PictureFragment extends BaseFragment implements LoadResultCallBack,LoadFinishCallBack{


    private AutoLoadRecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar loading;

    private PictureAdapter mAdapter;
    //用于记录是否是首次进入
    private boolean isFirstChange;
    //记录最后一次提示显示时间，防止多次提示
    private long lastShowTime;
    protected Picture.PictureType mType;
    private MediaScannerConnection connection;

    public PictureFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        isFirstChange = true;
        mType = Picture.PictureType.BoringPicture;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                mAdapter.loadNextPage();
            }
        });
        mRecyclerView.setOnPauseListenerParams(false, true);

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

        mAdapter = new PictureAdapter(getActivity(),this,mRecyclerView,mType);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setSaveFileCallBack(this);
        mAdapter.loadFirst();
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除内存缓存，避免由于内存缓存造成的图片显示不完整
        ImageLoaderProxy.getImageLoader().clearMemoryCache();
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
        loading.setVisibility(View.GONE);
        ShowToast.Short(ConstantString.LOAD_FAILED);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void loadFinish(Object obj) {
        Bundle bundle = (Bundle) obj;
        boolean isSmallPic = bundle.getBoolean(DATA_IS_SIAMLL_PIC);
        String filePath = bundle.getString(DATA_FILE_PATH);
        File newFile = new File(filePath);
        FEMediaScannerConnectionClient connectionClient = new FEMediaScannerConnectionClient(isSmallPic,newFile);
        connection = new MediaScannerConnection(getActivity(),connectionClient);
        connectionClient.setMediaScannerConnection(connection);
        connection.connect();
    }
    @Subscribe
    public void onEventMainThread(NetWorkEvent event) {

        if (event.getType() == NetWorkEvent.AVAILABLE) {
            if (NetWorkUtil.isWifiConnected(getActivity())) {
                mAdapter.setIsWifi(true);
                if (!isFirstChange && (System.currentTimeMillis() - lastShowTime) > 3000) {
                    ShowToast.Short("已切换为WIFI模式，自动加载GIF图片");
                    lastShowTime = System.currentTimeMillis();
                }
            } else {
                mAdapter.setIsWifi(false);
                if (!isFirstChange && (System.currentTimeMillis() - lastShowTime) > 3000) {
                    ShowToast.Short("已切换为省流量模式，只加载GIF缩略图");
                    lastShowTime = System.currentTimeMillis();
                }
            }
            isFirstChange = false;
        }
    }
}
