package com.example.friedegg.base;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by 123 on 2016/5/23.
 * BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity implements ConstantString{
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mContext = this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Abstract Method In Activity
    ///////////////////////////////////////////////////////////////////////////

    protected abstract void initView();

    protected abstract void initData();

    ///////////////////////////////////////////////////////////////////////////
    // Common Operation
    ///////////////////////////////////////////////////////////////////////////

    public void replaceFragment(int id_content, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id_content, fragment);
        transaction.commit();
    }
}
