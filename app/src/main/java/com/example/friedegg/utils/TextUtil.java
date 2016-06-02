package com.example.friedegg.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.example.friedegg.base.ConstantString;

/**
 * Created by CRZ on 2016/5/30 15:16.
 * TextUtil
 */
public class TextUtil {
    public static void copy(Activity activity, String copyText) {
        ClipboardManager clip = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setPrimaryClip(ClipData.newPlainText(null, copyText));
        ShowToast.Short(ConstantString.COPY_SUCCESS);
    }
    /**
     * 判断是否为null、空字符串或者是"null"
     *
     * @param str
     * @return
     */
    public static boolean isNull(CharSequence... str) {

        for (CharSequence cha : str) {
            if (cha == null || cha.length() == 0 || cha.equals("null")) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }
}
