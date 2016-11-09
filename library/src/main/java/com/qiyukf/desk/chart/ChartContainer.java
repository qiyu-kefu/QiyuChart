package com.qiyukf.desk.chart;

import android.content.Context;

/**
 * Created by zhoujianghua on 2016/10/11.
 */

public interface ChartContainer {

    Context getContext();

    void render(Chart chart);

    boolean isAttached();

    void requestLayout();

    void addChart(Chart chart);

    Chart getChartAt(int index);
}
