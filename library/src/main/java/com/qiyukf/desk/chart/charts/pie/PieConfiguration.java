package com.qiyukf.desk.chart.charts.pie;

import android.graphics.Color;

import com.qiyukf.desk.chart.ChartConfiguration;
import com.qiyukf.desk.utils.sys.ScreenUtils;

/**
 * Created by zhoujianghua on 2016/10/10.
 */

public class PieConfiguration extends ChartConfiguration<PieConfiguration> {

    public static final PieConfiguration DEFAULT = create();

    public static final int DESC_POS_LEFT = 1;
    public static final int DESC_POS_RIGHT = 2; // 这种方式可以显示更多
    public static final int DESC_POS_MIDDLE = 0;

    // attributes
    private boolean innerBorder = false;
    private int borderWidth = 0;
    private int borderColor = 0;

    private float ringRatio = 0;

    private float highlightScale = 0.1f;

    private boolean showPercentage = true;
    private int percentageColor = Color.GRAY;
    private int percentageTextSize = ScreenUtils.dp2px(10);

    private int descPosition = DESC_POS_RIGHT;
    private int descTextColor = Color.GRAY;
    private int descTextSize = ScreenUtils.dp2px(12);

    private PieConfiguration() {

    }

    public static PieConfiguration create() {
        return new PieConfiguration();
    }

    public PieConfiguration setInnerBorder(boolean innerBorder) {
        this.innerBorder = innerBorder;
        return this;
    }

    public PieConfiguration setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public PieConfiguration setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public PieConfiguration setRingRatio(float ringRatio) {
        this.ringRatio = ringRatio;
        return this;
    }

    public PieConfiguration setHighlightScale(float highlightScale) {
        this.highlightScale = highlightScale;
        return this;
    }

    public PieConfiguration setShowPercentage(boolean showPercentage) {
        this.showPercentage = showPercentage;
        return this;
    }

    public PieConfiguration setPercentageColor(int percentageColor) {
        this.percentageColor = percentageColor;
        return this;
    }

    public PieConfiguration setPercentageTextSize(int percentageTextSize) {
        this.percentageTextSize = percentageTextSize;
        return this;
    }

    public PieConfiguration setDescPosition(int descPosition) {
        this.descPosition = descPosition;
        return this;
    }

    public PieConfiguration setDescTextColor(int descTextColor) {
        this.descTextColor = descTextColor;
        return this;
    }

    public PieConfiguration setDescTextSize(int descTextSize) {
        this.descTextSize = descTextSize;
        return this;
    }

    public boolean innerBorder() {
        return innerBorder;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public float getRingRatio() {
        return ringRatio;
    }

    public float getHighlightScale() {
        return highlightScale;
    }

    public boolean showPercentage() {
        return showPercentage;
    }

    public int getPercentageColor() {
        return percentageColor;
    }

    public int getPercentageTextSize() {
        return percentageTextSize;
    }

    public int getDescPosition() {
        return descPosition;
    }

    public int getDescTextColor() {
        return descTextColor;
    }

    public int getDescTextSize() {
        return descTextSize;
    }
}
