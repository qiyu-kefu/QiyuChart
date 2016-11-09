package com.qiyukf.desk.utils.sys;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 使用前请一定记得初始化
 */
public class ScreenUtils {

    private static Context context;

    public static void init(Context context) {
        ScreenUtils.context = context.getApplicationContext();
    }

    public static int getScreenWidth() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int dp2px(float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    public static int px2dp(float pxValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }
}
