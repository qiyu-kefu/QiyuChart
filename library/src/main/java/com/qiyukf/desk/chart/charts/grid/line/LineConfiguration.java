package com.qiyukf.desk.chart.charts.grid.line;

import com.qiyukf.desk.chart.charts.grid.GridConfiguration;
import com.qiyukf.desk.utils.sys.ScreenUtils;

/**
 * Created by hzwangchenyan on 2016/10/10.
 */
public class LineConfiguration extends GridConfiguration<LineConfiguration> {
    public static final LineConfiguration DEFAULT = create();

    private boolean isShowShadow = false;

    private float lineWidth = ScreenUtils.dp2px(2);

    private LineConfiguration() {
    }

    public static LineConfiguration create() {
        return new LineConfiguration();
    }

    public LineConfiguration setShowShadow(boolean showShadow) {
        this.isShowShadow = showShadow;
        return this;
    }

    public LineConfiguration setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    public boolean isShowShadow() {
        return isShowShadow;
    }

    public float getLineWidth() {
        return lineWidth;
    }
}
