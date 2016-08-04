package com.example.friedegg.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.friedegg.R;
import com.example.friedegg.base.BaseActivity;
import com.example.friedegg.base.ConstantString;
import com.example.friedegg.modul.Commentator;
import com.example.friedegg.net.Request4PushComment;
import com.example.friedegg.net.Request4PushFreshComment;
import com.example.friedegg.utils.EditTextShakeHelper;
import com.example.friedegg.utils.SharedPreUtils;
import com.example.friedegg.utils.ShowToast;
import com.example.friedegg.utils.TextUtil;

import java.util.HashMap;


public class PushCommentActivity extends BaseActivity {

    private TextView tv_title;
    private EditText et_content;
    private Toolbar mToolbar;

    private String thread_id;
    private String parent_id;
    private String parent_name;
    private String author_name;
    private String author_email;
    private String message;

    private EditText et_name;
    private EditText et_email;

    private AlertDialog alertDialog;
    private View positiveAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_comment);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_content = (EditText) findViewById(R.id.et_content);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("回复");
        mToolbar.setNavigationIcon(R.drawable.ic_actionbar_back);
    }

    @Override
    protected void initData() {
        parent_name = getIntent().getStringExtra("parent_name");
        tv_title.setText(TextUtil.isNull(parent_name) ? "回复:" : "回复:" + parent_name);
        /*新鲜事中 文章id=当前的thread_id=接口参数中的post_id*/
        thread_id = getIntent().getStringExtra("thread_id");
        parent_id = getIntent().getStringExtra("parent_id");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_push_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_push:
                message = et_content.getText().toString();

                if (TextUtils.isEmpty(message)) {
                    ShowToast.Short(ConstantString.INPUT_TOO_SHORT);
                    new EditTextShakeHelper(this).shake(et_content);
                    return true;
                }

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.dialog_commentotar_info, null);
                et_name = (EditText) layout.findViewById(R.id.et_name);
                et_email = (EditText) layout.findViewById(R.id.et_email);
                et_name.setText(SharedPreUtils.getString(PushCommentActivity.this, "author_name"));
                et_email.setText(SharedPreUtils.getString(PushCommentActivity.this, "author_email"));
                alertDialog = new AlertDialog.Builder(this).setTitle("作为旅客留言").setView(layout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        author_name = et_name.getText().toString();
                        author_email = et_email.getText().toString();

                        SharedPreUtils.setString(PushCommentActivity.this, "author_name", author_name);
                        SharedPreUtils.setString(PushCommentActivity.this, "author_email", author_email);
                        //新鲜事
                        if (thread_id.length() == 5) {
                            String url;
                            if (!TextUtils.isEmpty(parent_id) && !TextUtils.isEmpty(parent_name)) {
                                url = Request4PushFreshComment.getRequestURL(thread_id, parent_id, parent_name, author_name, author_email, message);
                            } else {
                                url = Request4PushFreshComment.getRequestURLNoParent(thread_id, author_name, author_email, message);
                            }
                            //提交评论
                            executeRequest(new Request4PushFreshComment(url, new PushCommentListener(), new PushCommentErrorListener()));
                        }
                        //多说的评论post
                        HashMap<String, String> requestParams;
                        //回复别人 和首次评论
                        if (!TextUtil.isNull(parent_id)) {
                            requestParams = Request4PushComment.getRequestParams(thread_id, parent_id, author_name, author_email, message);
                        } else {
                            requestParams = Request4PushComment.getRequestParamsNoParent(thread_id, author_name, author_email, message);
                        }
                        executeRequest(new Request4PushComment(Commentator.URL_PUSH_COMMENT, requestParams, new PushCommentListener(), new PushCommentErrorListener()));

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private class PushCommentListener implements Response.Listener<Boolean> {
        @Override
        public void onResponse(Boolean aBoolean) {
            alertDialog.dismiss();
            if (aBoolean) {
                setResult(RESULT_OK);
                finish();
            } else {
                ShowToast.Short(ConstantString.COMMENT_FAILED);
            }
        }
    }

    private class PushCommentErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            ShowToast.Short(ConstantString.COMMENT_FAILED);
            alertDialog.dismiss();
        }
    }

    private class InputWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            positiveAction.setEnabled(TextUtil.isEmail(et_email.getText().toString().trim()
            ) && !TextUtil.isNull(et_name.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

}
