package com.qiyukf.desk.chart.view;

/**
 * 默认都为空实现，派生类可只实现自己关注的事件
 * Created by zhoujianghua on 2016/10/4.
 */
public class ChartGestureListener {

    public boolean onDown(float x, float y) {
        return true;
    }

    public void onShowPress(float x, float y) {
    }

    public boolean onSingleTap(float x, float y) {
        return false;
    }

    public void onLongPress(float x, float y) {
    }

    public boolean onScroll(float distanceX, float distanceY) {
        return false;
    }

    public boolean onFling(float velocityX, float velocityY) {
        return false;
    }

    public boolean onDoubleTap(float x, float y) {
        return false;
    }

    public boolean onScaleBegin(float focusX, float focusY) {
        return false;
    }

    public boolean onScale(float factorX, float factorY) {
        return false;
    }

    public void onTouchEnd() {
    }
}
