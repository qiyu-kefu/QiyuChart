package com.qiyukf.desk.chart;

import android.animation.TimeInterpolator;

import com.qiyukf.desk.chart.animation.Interpolators;

/**
 * Created by hzwangchenyan on 2016/10/11.
 */
public abstract class ChartConfiguration<T extends ChartConfiguration> {
    private boolean showInAnimation = false;
    private TimeInterpolator inAnimationInterpolator = Interpolators.ACC_DEC;
    private long inAnimationDuration = 600;

    private boolean isTouchable = true;

    public T setShowInAnimation(boolean showInAnimation) {
        this.showInAnimation = showInAnimation;
        return (T) this;
    }

    public T setInAnimationInterpolator(TimeInterpolator inAnimationInterpolator) {
        this.inAnimationInterpolator = inAnimationInterpolator;
        return (T) this;
    }

    public T setInAnimationDuration(long inAnimationDuration) {
        this.inAnimationDuration = inAnimationDuration;
        return (T) this;
    }

    public boolean isShowInAnimation() {
        return showInAnimation;
    }

    public TimeInterpolator getInAnimationInterpolator() {
        return inAnimationInterpolator;
    }

    public long getInAnimationDuration() {
        return inAnimationDuration;
    }

    public T setTouchable(boolean touchable) {
        this.isTouchable = touchable;
        return (T) this;
    }

    public boolean isTouchable() {
        return isTouchable;
    }
}
