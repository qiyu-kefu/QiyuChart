package com.qiyukf.desk.chart;

import android.animation.TimeInterpolator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

import com.qiyukf.desk.chart.animation.TimingAnimation;
import com.qiyukf.desk.chart.view.ChartGestureListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujianghua on 2016/10/1.
 */

public abstract class Chart<D extends ChartData, C extends ChartConfiguration> {

    protected RectF bound = new RectF();

    protected ChartContainer chartView;

    // data
    protected List<D> dataList = new ArrayList<>();

    // configuration
    protected C configuration;

    // gesture listener
    private boolean gestureEnabled = false;
    private boolean scaleEnabled = false;
    protected ChartGestureListener gestureListener;

    // 第一次显示的动画属性
    private TimingAnimation inAnimation;
    private float inFraction = 1.0f;

    public void addData(D data) {
        dataList.add(data);
    }

    public void setDataList(List<D> dataList) {
        if (dataList == null) {
            throw new NullPointerException("data list can not be null");
        }
        this.dataList = dataList;
    }

    /**
     * {@link ChartContainer#addChart(Chart)}前调用
     */
    public void setConfiguration(C configuration) {
        this.configuration = configuration;
    }

    public void setBounds(RectF bound) {
        if (bound == null) {
            throw new NullPointerException("null bounds of chart");
        }
        this.bound = bound;

        if (chartView != null && chartView.isAttached()) {
            onConfiguration();

            chartView.requestLayout();
        }
    }

    public Bitmap toBitmap() {
        Bitmap bitmap = Bitmap.createBitmap((int) bound.width(), (int) bound.height(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        onDraw(canvas);
        return bitmap;
    }

    public RectF getBound() {
        return bound;
    }

    public float getWidth() {
        return bound.width();
    }

    public float getHeight() {
        return bound.height();
    }

    public void attach(ChartContainer view) {
        this.chartView = view;
    }

    public boolean isGestureEnabled() {
        return gestureEnabled && configuration.isTouchable();
    }

    public boolean isScaleEnabled() {
        return scaleEnabled;
    }

    public ChartGestureListener getGestureListener() {
        return gestureListener;
    }

    public void onViewAttachedToWindow() {
        if (!isVisible()) {
            return;
        }

        onConfiguration();

        if (configuration.isShowInAnimation()) {
            scheduleInAnimation();
        } else {
            invalidate();
        }
    }

    public void notifyDataChanged(boolean animationIn) {
        onConfiguration();

        if (animationIn && configuration.isShowInAnimation()) {
            scheduleInAnimation();
        } else {
            invalidate();
        }
    }

    protected void invalidate() {
        if (isVisible()) {
            chartView.render(this);
        }
    }

    public void draw(Canvas canvas) {
        if (!isVisible()) {
            return;
        }

        int saveCount = canvas.save();
        canvas.translate(bound.left, bound.top);
        onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private void scheduleInAnimation() {
        if (inAnimation != null && inAnimation.isRunning()) {
            inAnimation.cancel();
        }

        if (!isVisible()) {
            return;
        }

        inFraction = 0;

        TimeInterpolator interpolator = configuration.getInAnimationInterpolator();
        long duration = configuration.getInAnimationDuration();

        inAnimation = new TimingAnimation();
        inAnimation.setInterpolator(interpolator)
                .setDuration(duration)
                .setListener(new InAnimationListener())
                .start();
    }

    private class InAnimationListener implements TimingAnimation.AnimationListener {
        @Override
        public void onValueUpdate(TimingAnimation animation, float fraction) {
            inFraction = fraction;
            invalidate();
        }
    }

    protected void enableGesture(boolean gesture, boolean scale) {
        this.gestureEnabled = gesture;
        this.scaleEnabled = scale;
    }

    protected boolean isVisible() {
        return chartView != null && chartView.isAttached() && !bound.isEmpty() && !dataList.isEmpty();
    }

    protected float getInFraction() {
        return inFraction;
    }

    protected abstract void onConfiguration();

    protected abstract void onDraw(Canvas canvas);
}
