package com.qiyukf.desk.chart.charts.grid.bar;

import com.qiyukf.desk.chart.charts.grid.GridConfiguration;

/**
 * Created by hzwangchenyan on 2016/10/10.
 */
public class BarConfiguration extends GridConfiguration<BarConfiguration> {
    public static final BarConfiguration DEFAULT = create();

    private BarConfiguration() {
    }

    public static BarConfiguration create() {
        return new BarConfiguration();
    }
}
