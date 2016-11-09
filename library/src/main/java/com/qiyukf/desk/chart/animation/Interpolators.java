package com.qiyukf.desk.chart.animation;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by zhoujianghua on 2016/10/3.
 * 均为默认配置，如需修改参数，要自己分配
 */

public class Interpolators {

    public static TimeInterpolator LINEAR = new LinearInterpolator();

    public static TimeInterpolator DECELERATE = new DecelerateInterpolator();

    public static TimeInterpolator OVERSHOOT = new OvershootInterpolator();

    public static TimeInterpolator ACC_DEC = new AccelerateDecelerateInterpolator();

}
