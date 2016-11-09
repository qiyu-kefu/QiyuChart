package com.qiyukf.desk.chart.charts.grid;

import com.qiyukf.desk.chart.ChartData;

import java.io.Serializable;

/**
 * Created by zhoujianghua on 2016/10/4.
 */
public class GridData implements ChartData {
    private final String title;
    private final String tipsTitle;

    private final Entry[] entries;

    public GridData(String title, String tipsTitle, Entry[] entries) {
        this.title = title;
        this.tipsTitle = tipsTitle;
        this.entries = entries;
    }

    public String getTitle() {
        return title;
    }

    public String getTipsTitle() {
        return tipsTitle;
    }

    public Entry[] getEntries() {
        return entries;
    }

    public float getMaxValue() {
        float max = 0;
        for (Entry entry : entries) {
            max = Math.max(max, entry.getValue());
        }
        return max;
    }

    public static class Entry implements Serializable {
        // mutable
        private float value;

        // immutable
        private final int lineColor;
        private final int descColor;
        private final String desc;

        public Entry(int color, String desc, float value) {
            this.lineColor = color;
            this.descColor = color;
            this.desc = desc;

            this.value = value;
        }

        public Entry(int lineColor, int descColor, String desc, float value) {
            this.lineColor = lineColor;
            this.descColor = descColor;
            this.desc = desc;

            this.value = value;
        }

        public int getLineColor() {
            return lineColor;
        }

        public int getDescColor() {
            return descColor;
        }

        public String getDesc() {
            return desc;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }
}
