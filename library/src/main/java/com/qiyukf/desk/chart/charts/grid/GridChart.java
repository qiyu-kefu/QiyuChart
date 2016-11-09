package com.qiyukf.desk.chart.charts.grid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.text.TextPaint;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.qiyukf.desk.chart.Chart;
import com.qiyukf.desk.chart.animation.TimingAnimation;
import com.qiyukf.desk.chart.view.ChartGestureListener;
import com.qiyukf.desk.utils.sys.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzwangchenyan on 2016/10/9.
 */
public abstract class GridChart<C extends GridConfiguration> extends Chart<GridData, C> {
    private static final int TEXT_MARGIN = ScreenUtils.dp2px(5);
    private static final int ROW_COUNT = 5;
    private static final int MAX_SCALE = 3;
    private static final float FLING_MIN_VELOCITY_X = 200;

    private static final int TOUCHABLE_MAX_POINT = 12;
    private static final int UNTOUCHABLE_MAX_POINT = 5;

    private Paint solidLinePaint = new Paint();
    private Paint dashLinePaint = new Paint();
    protected TextPaint textPaint = new TextPaint();
    protected Paint.FontMetrics fontMetrics;
    private Path linePath = new Path();

    protected int firstRenderItem;
    protected int lastRenderItem;

    private List<Integer> renderPointList = new ArrayList<>();

    protected float defaultItemWidth;
    private float itemHeight;
    private int gridHeight;
    protected float horizontalOffset;

    protected float translateX = 0;
    private float scaleFocusX;
    private float scaleValue = 1;

    private TimingAnimation doubleTapAnimator;
    private TimingAnimation flingAnimator;
    private Scroller flingScroller;

    private ViewGroup scrollView;

    public GridChart() {
        setupGestureListener();
        enableGesture(true, true);
    }

    public void setScrollView(ViewGroup scrollView) {
        this.scrollView = scrollView;
    }

    protected void setupPaints() {
        // 文字画笔
        textPaint.setAntiAlias(true);
        textPaint.setColor(configuration.getTextColor());
        textPaint.setTextSize(configuration.getTextSize());
        fontMetrics = textPaint.getFontMetrics();

        // 虚线画笔
        dashLinePaint.reset();
        dashLinePaint.setColor(configuration.getGridLineColor());
        dashLinePaint.setStyle(Paint.Style.STROKE);
        dashLinePaint.setStrokeWidth(1);
        // 设置虚线的间隔和点的长度
        float dot = ScreenUtils.dp2px(1);
        PathEffect effects = new DashPathEffect(new float[]{dot, dot}, 1);
        dashLinePaint.setPathEffect(effects);

        // 实线画笔
        solidLinePaint.setColor(configuration.getGridLineColor());
        solidLinePaint.setStyle(Paint.Style.STROKE);
        solidLinePaint.setStrokeWidth(ScreenUtils.dp2px(1));
    }

    @Override
    protected void onConfiguration() {
        if (!isVisible()) {
            return;
        }

        translateX = 0;
        scaleValue = 1;

        setupPaints();
        calculateGridHeight();

        itemHeight = (getChartBottom() - getTextHeight()) / ROW_COUNT;
        horizontalOffset = textPaint.measureText(String.valueOf(gridHeight * 5)) + TEXT_MARGIN;
        if (configuration.isTouchable()) {
            float pointCount = (dataList.size() >= TOUCHABLE_MAX_POINT) ? (TOUCHABLE_MAX_POINT - 1) : (dataList.size() - 1);
            defaultItemWidth = getChartWidth() / pointCount;
        } else {
            defaultItemWidth = getChartWidth() / (dataList.size() - 1);
        }

        calculateRenderRange();
    }

    private void calculateGridHeight() {
        float max = 0;
        for (GridData data : dataList) {
            max = Math.max(max, data.getMaxValue());
        }

        gridHeight = (int) Math.ceil(max / 5);
        gridHeight = (gridHeight == 0) ? 1 : gridHeight;

        if (gridHeight > 5) {
            gridHeight = (int) Math.ceil((float) gridHeight / 5) * 5;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isVisible()) {
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            return;
        }

        drawGridText(canvas);
        drawGridLine(canvas);

        if (configuration.isShowDesc()) {
            drawDesc(canvas);
        }

        canvas.save();
        canvas.translate(horizontalOffset, 0);
        canvas.clipRect(0, 0, getChartWidth() * getInFraction(), getHeight());
        canvas.translate(translateX, 0);
        drawContent(canvas);
        canvas.restore();
    }

    private void drawGridText(Canvas canvas) {
        // 绘制水平文字
        textPaint.setColor(configuration.getTextColor());
        textPaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i <= ROW_COUNT; i++) {
            String text = String.valueOf(gridHeight * i);
            canvas.drawText(text, horizontalOffset - TEXT_MARGIN, getChartBottom() - itemHeight * i +
                    getTextHeight() / 2 - getTextOffsetY(), textPaint);
        }

        canvas.save();
        canvas.translate(horizontalOffset, 0);
        canvas.clipRect(0, 0, getChartWidth() * getInFraction(), getHeight());
        canvas.translate(translateX, 0);

        // 绘制垂直文字
        calculateRenderPoints();
        textPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < renderPointList.size(); i++) {
            int index = renderPointList.get(i);
            String title = dataList.get(index).getTitle();
            float textWidth = textPaint.measureText(title);
            float drawX = getItemWidth() * index;
            if (drawX + translateX - textWidth / 2 < TEXT_MARGIN) {
                drawX = TEXT_MARGIN - translateX + textWidth / 2;
            } else if (drawX + translateX + textWidth / 2 > getChartWidth() - TEXT_MARGIN) {
                drawX = getChartWidth() - TEXT_MARGIN - translateX - textWidth / 2;
            }
            canvas.drawText(title, drawX, getChartBottom() + getBottomTextHeight() - getTextOffsetY(), textPaint);
        }

        canvas.restore();
    }

    private void drawGridLine(Canvas canvas) {
        linePath.reset();
        linePath.moveTo(horizontalOffset, getChartBottom() - itemHeight * 5);
        linePath.lineTo(horizontalOffset, getChartBottom());
        linePath.lineTo(getWidth(), getChartBottom());
        canvas.drawPath(linePath, solidLinePaint);

        linePath.reset();
        for (int i = 1; i <= ROW_COUNT; i++) {
            linePath.moveTo(horizontalOffset, getChartBottom() - itemHeight * i);
            linePath.lineTo(getWidth(), getChartBottom() - itemHeight * i);
        }
        canvas.drawPath(linePath, dashLinePaint);
    }

    private void calculateRenderPoints() {
        int firstVisiblePoint = 0;
        int lastVisiblePoint = dataList.size() - 1;
        for (int i = 0; i < dataList.size(); i++) {
            float currentX = getItemWidth() * i;
            if (currentX + translateX >= 0) {
                firstVisiblePoint = i;
                break;
            }
        }

        for (int i = firstVisiblePoint; i < dataList.size(); i++) {
            float nextX = getItemWidth() * (i + 1);
            if ((int) (nextX + translateX) > (int) getChartWidth()) {
                lastVisiblePoint = i;
                break;
            }
        }

        renderPointList.clear();
        int count = lastVisiblePoint - firstVisiblePoint + 1;
        int maxCount = configuration.isTouchable() ? TOUCHABLE_MAX_POINT : UNTOUCHABLE_MAX_POINT;
        if (count < maxCount) {
            for (int i = firstVisiblePoint; i <= lastVisiblePoint; i++) {
                renderPointList.add(i);
            }
        } else {
            float interval = (count - 1f) / (maxCount - 1);
            for (int i = 0; i < maxCount; i++) {
                int index = (int) Math.floor(firstVisiblePoint + interval * i);
                renderPointList.add(index);
            }
        }
    }

    protected float getTextHeight() {
        return fontMetrics.descent - fontMetrics.ascent;
    }

    private float getBottomTextHeight() {
        return getTextHeight() + TEXT_MARGIN;
    }

    private float getDescHeight() {
        if (configuration.isShowDesc()) {
            return getTextHeight() + ScreenUtils.dp2px(38);
        } else {
            return 0;
        }
    }

    protected float getChartBottom() {
        return getHeight() - getDescHeight() - getBottomTextHeight();
    }

    protected float getChartWidth() {
        return getWidth() - horizontalOffset;
    }

    private float getChartMeasuredWidth() {
        return getItemWidth() * (dataList.size() - 1);
    }

    protected float getItemWidth() {
        return defaultItemWidth * scaleValue;
    }

    protected float getItemHeightRatio() {
        return itemHeight / gridHeight;
    }

    protected float getTextOffsetY() {
        return fontMetrics.descent;
    }

    protected abstract void drawDesc(Canvas canvas);

    protected abstract void drawContent(Canvas canvas);

    protected abstract void calculateRenderRange();

    private void setupGestureListener() {
        gestureListener = new ChartGestureListener() {

            @Override
            public boolean onDown(float x, float y) {
                stopFling();
                return configuration.isTouchable();
            }

            @Override
            public boolean onScroll(float distanceX, float distanceY) {
                if (scrollView != null && Math.abs(distanceX) > Math.abs(distanceY)) {
                    requestDisallowInterceptTouchEvent(true);
                }

                onScrollBegin(distanceX, distanceY);

                // 平滑处理
                float offset = -distanceX * 0.5f;
                return updateTranslateX(translateX + offset);
            }

            @Override
            public boolean onFling(float velocityX, float velocityY) {
                return fling(velocityX);
            }

            /**
             * 全屏模式（默认缩放比例不为1）缩放还有点问题
             */
            @Override
            public boolean onScaleBegin(float focusX, float focusY) {
                if (!configuration.isScalable()) {
                    return false;
                }

                scaleFocusX = focusX - horizontalOffset;
                requestDisallowInterceptTouchEvent(true);
                return true;
            }

            @Override
            public boolean onScale(float factorX, float factorY) {
                if (!configuration.isScalable()) {
                    return false;
                }

                return scale(factorX);
            }

            @Override
            public boolean onDoubleTap(float x, float y) {
                if (!configuration.isScalable()) {
                    return false;
                }

                float newScaleFocusX = x - horizontalOffset;
                doubleTap(newScaleFocusX);
                return true;
            }

            @Override
            public boolean onSingleTap(float x, float y) {
                return onSingleTapImpl(x, y);
            }

            @Override
            public void onTouchEnd() {
                requestDisallowInterceptTouchEvent(false);
            }
        };
    }

    protected void onScrollBegin(float distanceX, float distanceY) {
    }

    protected boolean onSingleTapImpl(float x, float y) {
        return false;
    }

    private boolean scale(float factorX) {
        // 平滑处理
        factorX = 1 + (factorX - 1) * 0.5f;
        float newScaleValue = scaleValue * factorX;
        if (newScaleValue < 1) {
            newScaleValue = 1;
        } else if (newScaleValue > MAX_SCALE) {
            newScaleValue = MAX_SCALE;
        }

        return updateScale(newScaleValue);
    }

    private boolean fling(float velocityX) {
        if (Math.abs(velocityX) < FLING_MIN_VELOCITY_X) {
            return false;
        }

        flingScroller = new Scroller(chartView.getContext());
        flingScroller.fling((int) translateX, 0, (int) velocityX, 0, (int) (getChartWidth() - getChartMeasuredWidth()), 0, 0, 0);
        flingAnimator = new TimingAnimation();
        flingAnimator.setDuration(flingScroller.getDuration()) // 由Scroller去控制有没有滚动完
                .setListener(new TimingAnimation.AnimationListener() {
                    @Override
                    public void onValueUpdate(TimingAnimation animation, float fraction) {
                        if (flingScroller != null) {
                            if (flingScroller.computeScrollOffset()) {
                                updateTranslateX(flingScroller.getCurrX());
                            } else {
                                flingScroller = null;
                                flingAnimator = null;
                            }
                        }
                    }
                });
        flingAnimator.start();
        return true;
    }

    private void stopFling() {
        if (flingAnimator != null && flingAnimator.isRunning()) {
            flingAnimator.cancel();
        }
    }

    private void doubleTap(float newScaleFocusX) {
        if (doubleTapAnimator != null && doubleTapAnimator.isRunning()) {
            doubleTapAnimator.complete();
        }

        scaleFocusX = newScaleFocusX;

        doubleTapAnimator = new TimingAnimation(scaleValue, scaleValue == 1 ? 2 : 1);
        doubleTapAnimator.setDuration(300);
        doubleTapAnimator.setListener(new TimingAnimation.AnimationListener() {
            @Override
            public void onValueUpdate(TimingAnimation animation, float fraction) {
                updateScale(fraction);
            }
        });
        doubleTapAnimator.start();
    }

    private boolean updateScale(float newScaleValue) {
        if (newScaleValue == scaleValue) {
            return false;
        }

        float offset = (scaleValue - newScaleValue) * scaleFocusX;
        translateX += offset;

        if (translateX > 0) {
            translateX = 0;
        } else if (getChartMeasuredWidth() + translateX < getChartWidth()) {
            translateX = getChartWidth() - getChartMeasuredWidth();
        }

        scaleValue = newScaleValue;

        calculateRenderRange();

        invalidate();
        return true;
    }

    private boolean updateTranslateX(float newTranslateX) {
        if (newTranslateX > 0) {
            newTranslateX = 0;
        } else if (getChartMeasuredWidth() + newTranslateX < getChartWidth()) {
            newTranslateX = getChartWidth() - getChartMeasuredWidth();
        }

        if (newTranslateX != translateX) {
            translateX = newTranslateX;
            calculateRenderRange();
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    private void requestDisallowInterceptTouchEvent(boolean disallow) {
        if (scrollView != null) {
            scrollView.requestDisallowInterceptTouchEvent(disallow);
        }
    }
}
