package com.example.friedegg.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.friedegg.R;
import com.example.friedegg.base.BaseActivity;
import com.example.friedegg.utils.ShareUtils;
import com.example.friedegg.utils.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoDetailActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.imgBtn_back)
    ImageButton imgBtn_back;
    @BindView(R.id.imgBtn_forward)
    ImageButton imgBtn_forward;
    @BindView(R.id.imgBtn_control)
    ImageButton imgBtn_control;

    private String url;

    private boolean isLoadFinish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.loading);
        mToolbar.setNavigationIcon(R.drawable.ic_actionbar_back);

        imgBtn_back.setOnClickListener(this);
        imgBtn_forward.setOnClickListener(this);
        imgBtn_control.setOnClickListener(this);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {

                        if (newProgress == 100) {
                            progress.setVisibility(View.GONE);
                        } else {
                            progress.setProgress(newProgress);
                            progress.setVisibility(View.VISIBLE);
                        }

                        super.onProgressChanged(view, newProgress);
                    }
                }

        );
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                imgBtn_control.setImageResource(R.drawable.ic_action_refresh);
                isLoadFinish = true;
                mToolbar.setTitle(view.getTitle());
            }
        });


    }

    @Override
    protected void initData() {
        url = getIntent().getStringExtra("url");
        webview.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtn_back:
                if (webview.canGoBack()) {
                    webview.goBack();
                }
                break;
            case R.id.imgBtn_forward:
                if (webview.canGoForward()) {
                    webview.goForward();
                }
                break;
            case R.id.imgBtn_control:

                if (isLoadFinish) {
                    webview.reload();
                    isLoadFinish = false;
                } else {
                    webview.stopLoading();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webview != null) {
            webview.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webview != null) {
            webview.onPause();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_more:
                new AlertDialog.Builder(this)
                        .setItems(R.array.video_more, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    //分享
                                    case 0:
                                        ShareUtils.shareText(VideoDetailActivity.this, mToolbar.getTitle() + " " + url);
                                        break;
                                    //复制
                                    case 1:
                                        TextUtil.copy(VideoDetailActivity.this, url);
                                        break;
                                    //浏览器打开
                                    case 2:
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                        break;
                                }
                            }
                        }).create().show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
