package com.qiyukf.desk.chart.charts.pie;

import com.qiyukf.desk.chart.ChartData;

/**
 * Created by zhoujianghua on 2016/10/2.
 */

public class PieData implements ChartData {

    private float proportion;

    private int color;

    private String title;

    public PieData(float proportion, int color, String title) {
        this.proportion = proportion;
        this.color = color;
        this.title = title;
    }

    public float getProportion() {
        return proportion;
    }

    public int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }
}
