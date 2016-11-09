package com.qiyukf.desk.chart.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.qiyukf.desk.chart.Chart;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理了点击和缩放手势
 * Created by zhoujianghua on 2016/10/3.
 */
public class ChartGestureDetector implements View.OnTouchListener {

    private Context context;

    private List<Chart> chartList = new ArrayList<>();

    private GestureDetector gestureDetector;

    private ScaleGestureDetector scaleGestureDetector;

    public ChartGestureDetector(Context context) {
        this.context = context;
    }

    public void addChart(Chart chart) {
        if (chart.isGestureEnabled() && gestureDetector == null) {
            gestureDetector = new GestureDetector(context, gestureListener());
        }

        if (chart.isScaleEnabled() && scaleGestureDetector == null) {
            scaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener());
        }

        chartList.add(chart);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (scaleGestureDetector != null) {
            scaleGestureDetector.onTouchEvent(event);
            if (scaleGestureDetector.isInProgress()) {
                return true;
            }
        }

        if (gestureDetector != null) {
            return gestureDetector.onTouchEvent(event);
        }

        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            for (Chart chart : chartList) {
                if (chart.getGestureListener() != null) {
                    chart.getGestureListener().onTouchEnd();
                }
            }
        }

        return false;
    }

    private GestureDetector.OnGestureListener gestureListener() {
        return new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                for (Chart chart : chartList) {
                    if (chart.getGestureListener() != null && pointInChart(e, chart)) {
                        float x = e.getX() - chart.getBound().left;
                        float y = e.getY() - chart.getBound().top;
                        return chart.getGestureListener().onDown(x, y);
                    }
                }
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                for (Chart chart : chartList) {
                    if (chart.getGestureListener() != null && pointInChart(e, chart)) {
                        float x = e.getX() - chart.getBound().left;
                        float y = e.getY() - chart.getBound().top;
                        chart.getGestureListener().onShowPress(x, y);
                        break;
                    }
                }
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                for (Chart chart : chartList) {
                    if (chart.getGestureListener() != null && pointInChart(e, chart)) {
                        float x = e.getX() - chart.getBound().left;
                        float y = e.getY() - chart.getBound().top;
                        return chart.getGestureListener().onSingleTap(x, y);
                    }
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                for (Chart chart : chartList) {
                    if (chart.getGestureListener() != null && pointInChart(e, chart)) {
                        float x = e.getX() - chart.getBound().left;
                        float y = e.getY() - chart.getBound().top;
                        chart.getGestureListener().onLongPress(x, y);
                        break;
                    }
                }
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                for (Chart chart : chartList) {
                    if (chart.getGestureListener() != null && pointInChart(e1, chart)) {
                        return chart.getGestureListener().onScroll(distanceX, distanceY);
                    }
                }
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                for (Chart chart : chartList) {
                    if (chart.getGestureListener() != null && pointInChart(e1, chart)) {
                        return chart.getGestureListener().onFling(velocityX, velocityY);
                    }
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                for (Chart chart : chartList) {
                    if (chart.getGestureListener() != null && pointInChart(e, chart)) {
                        float x = e.getX() - chart.getBound().left;
                        float y = e.getY() - chart.getBound().top;
                        return chart.getGestureListener().onDoubleTap(x, y);
                    }
                }
                return false;
            }
        };
    }

    private ScaleGestureDetector.OnScaleGestureListener scaleGestureListener() {
        return new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factorX = detector.getPreviousSpanX() > 0 ? detector.getCurrentSpanX() / detector.getPreviousSpanX() : 1;
                float factorY = detector.getPreviousSpanY() > 0 ? detector.getCurrentSpanY() / detector.getPreviousSpanY() : 1;
                for (Chart chart : chartList) {
                    if (chart.getGestureListener() != null && pointInChart(detector, chart)) {
                        return chart.getGestureListener().onScale(factorX, factorY);
                    }
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                for (Chart chart : chartList) {
                    if (chart.getGestureListener() != null && pointInChart(detector, chart)) {
                        float focusX = detector.getFocusX() - chart.getBound().left;
                        float focusY = detector.getFocusY() - chart.getBound().top;
                        return chart.getGestureListener().onScaleBegin(focusX, focusY);
                    }
                }
                return false;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        };
    }

    private boolean pointInChart(MotionEvent event, Chart chart) {
        return chart.getBound().contains(event.getX(), event.getY());
    }

    private boolean pointInChart(ScaleGestureDetector detector, Chart chart) {
        return chart.getBound().contains(detector.getFocusX(), detector.getFocusY());
    }
}
