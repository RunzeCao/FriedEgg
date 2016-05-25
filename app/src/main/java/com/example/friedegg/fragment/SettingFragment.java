package com.example.friedegg.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.example.friedegg.R;
import com.example.friedegg.utils.AppInfoUtil;
import com.example.friedegg.utils.ShowToast;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment {
    public static final String CLEAR_CACHE = "clear_cache";
    public static final String ABOUT_APP = "about_app";
    public static final String APP_VERSION = "app_version";
    public static final String ENABLE_SISTER = "enable_sister";
    public static final String ENABLE_FRESH_BIG = "enable_fresh_big";

    private Preference clearCache;
    private Preference aboutApp;
    private Preference appVersion;
    private CheckBoxPreference enableSister;
    private CheckBoxPreference enableBig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        clearCache = findPreference(CLEAR_CACHE);
        aboutApp = findPreference(ABOUT_APP);
        appVersion = findPreference(APP_VERSION);
        enableSister = (CheckBoxPreference) findPreference(ENABLE_SISTER);
        enableBig = (CheckBoxPreference) findPreference(ENABLE_FRESH_BIG);

        appVersion.setTitle(AppInfoUtil.getVersionName(getActivity()));
        enableSister.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ShowToast.Short(((Boolean) newValue) ? "已解锁隐藏属性->妹子图" : "已关闭隐藏属性->妹子图");
                return true;
            }
        });
        enableBig.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ShowToast.Short(((Boolean) newValue) ? "已开启大图模式" : "已关闭大图模式");
                return true;
            }
        });
        aboutApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("煎蛋开源版").setMessage("一只Android小白的日常练习，感谢github，感谢凯子哥").setNegativeButton("GITHUB", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RunzeCao/FriedEgg")));
                        dialog.dismiss();
                    }
                }).show();
                return true;
            }
        });
    }


}
