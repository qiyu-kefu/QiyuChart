package com.qiyukf.desk.chart.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.qiyukf.desk.chart.Chart;
import com.qiyukf.desk.chart.ChartContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujianghua on 2016/10/11.
 */
public class ChartView extends View implements ChartContainer {

    private ChartGestureDetector detector;

    private List<Chart> charts = new ArrayList<>();

    private boolean attached = false;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float right = 0, bottom = 0;
        for (Chart chart : charts) {
            right = Math.max(right, chart.getBound().right);
            bottom = Math.max(bottom, chart.getBound().bottom);
        }
        setMeasuredDimension((int) right, (int) bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            for (Chart chart : charts) {
                chart.draw(canvas);
            }
        } catch (Throwable e) {
            Log.i("Chart", "draw exception: " + e);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attached = true;

        for (Chart chart : charts) {
            chart.onViewAttachedToWindow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attached = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (detector != null) {
            return detector.onTouch(this, event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void addChart(Chart chart) {
        if (charts.contains(chart)) {
            return;
        }
        if (chart != null) {
            charts.add(chart);

            chart.attach(this);

            if (detector == null) {
                detector = new ChartGestureDetector(getContext());
            }
            detector.addChart(chart);

            if (isAttached()) {
                requestLayout();
                chart.onViewAttachedToWindow();
            }
        }
    }

    @Override
    public void render(Chart chart) {
        if (chart == null) {
            postInvalidate();
        } else {
            RectF bound = chart.getBound();
            postInvalidate((int) bound.left, (int) bound.top, (int) bound.right, (int) bound.bottom);
        }
    }

    @Override
    public boolean isAttached() {
        return attached;
    }

    @Override
    public Chart getChartAt(int index) {
        if (index < 0 || index >= charts.size()) {
            return null;
        }
        return charts.get(index);
    }
}
