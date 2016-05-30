package com.example.friedegg.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.friedegg.R;

/**
 * Created by CRZ on 2016/5/30 15:07.
 * ShareUtils
 */
public class ShareUtils {
    public static void shareText(Activity activity, String shareText) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                shareText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, activity.getResources().getString(R
                .string.app_name)));
    }

}
