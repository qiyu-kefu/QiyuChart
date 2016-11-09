package com.qiyukf.desk.chart.charts.grid;

import android.graphics.Color;

import com.qiyukf.desk.chart.ChartConfiguration;
import com.qiyukf.desk.utils.sys.ScreenUtils;

/**
 * Created by zhoujianghua on 2016/10/12.
 */
public abstract class GridConfiguration<T extends GridConfiguration> extends ChartConfiguration<T> {
    private int gridLineColor = 0xFFE0E0E0;

    private float textSize = ScreenUtils.dp2px(12);
    private int textColor = Color.GRAY;

    private boolean isShowDesc = false;

    private boolean isScalable = false;

    public T setGridLineColor(int gridLineColor) {
        this.gridLineColor = gridLineColor;
        return (T) this;
    }

    public T setShowDesc(boolean showDesc) {
        this.isShowDesc = showDesc;
        return (T) this;
    }

    public T setTextSize(float textSize) {
        this.textSize = textSize;
        return (T) this;
    }

    public T setTextColor(int textColor) {
        this.textColor = textColor;
        return (T) this;
    }

    public T setScalable(boolean scalable) {
        isScalable = scalable;
        return (T) this;
    }

    public int getGridLineColor() {
        return gridLineColor;
    }

    public boolean isShowDesc() {
        return isShowDesc;
    }

    public float getTextSize() {
        return textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public boolean isScalable() {
        return isScalable;
    }
}
