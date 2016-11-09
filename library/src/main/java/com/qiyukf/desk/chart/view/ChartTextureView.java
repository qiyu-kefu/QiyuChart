package com.qiyukf.desk.chart.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;

import com.qiyukf.desk.chart.Chart;
import com.qiyukf.desk.chart.ChartContainer;
import com.qiyukf.desk.chart.RenderHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用TextureView绘制，如果同时有大量图表，且一起做动画，照说这种空间效率会更高一点
 * Created by zhoujianghua on 2016/10/2.
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ChartTextureView extends TextureView implements ChartContainer, TextureView.SurfaceTextureListener {

    private Renderer renderer;

    private ChartGestureDetector detector;

    private List<Chart> charts = new ArrayList<>();

    public ChartTextureView(Context context) {
        super(context);
        init();
    }

    public ChartTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChartTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setSurfaceTextureListener(this);
        setOpaque(false);
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
    public boolean isAttached() {
        return renderer != null;
    }

    @Override
    public void render(Chart chart) {
        if (renderer != null) {
            // 由于硬件缓冲的原因，如果不全部重绘，会导致没有绘制的部分显示不正确的内容
            renderer.render();
        }
    }

    @Override
    public Chart getChartAt(int index) {
        if (index < 0 || index >= charts.size()) {
            return null;
        }
        return charts.get(index);
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
    public boolean onTouchEvent(MotionEvent event) {
        if (detector != null) {
            return detector.onTouch(this, event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        renderer = new Renderer();

        // 先画一个起头
        for (Chart chart : charts) {
            chart.onViewAttachedToWindow();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        for (Chart chart : charts) {
            chart.onViewAttachedToWindow();
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        renderer.destroy();
        renderer = null;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private class Renderer {

        private final static int STATUS_INIT = 0;
        private final static int STATUS_REQUEST = 1;
        private final static int STATUS_SCHEDULE = 2;
        private final static int STATUS_RENDER = 3;
        private AtomicInteger status = new AtomicInteger(STATUS_INIT);

        private Handler renderHandler;

        private Renderer() {
            renderHandler = RenderHandler.get();
        }

        private void destroy() {
            renderHandler.removeCallbacks(invalidateRunnable);
        }

        private void render() {
            if (!schedule(STATUS_INIT)) {
                status.compareAndSet(STATUS_RENDER, STATUS_REQUEST);
            }
        }

        private Runnable invalidateRunnable = new Runnable() {
            @Override
            public void run() {
                if (status.compareAndSet(STATUS_SCHEDULE, STATUS_RENDER)) {
                    drawChart();

                    if (status.get() == STATUS_REQUEST) {
                        schedule(STATUS_REQUEST);
                    } else {
                        status.set(STATUS_INIT);
                    }
                }
            }
        };

        private boolean schedule(int stateFrom) {
            if (status.compareAndSet(stateFrom, STATUS_SCHEDULE)) {
                renderHandler.post(invalidateRunnable);
                return true;
            }
            return false;
        }

        private void drawChart() {
            Canvas canvas = lockCanvas();
            try {
                // 清屏
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                for (Chart chart : charts) {
                    chart.draw(canvas);
                }
            } catch (Throwable e) {
                Log.i("Chart", "draw exception: " + e);
            } finally {
                unlockCanvasAndPost(canvas);
            }
        }
    }
}
