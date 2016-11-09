package com.qiyukf.desk.chart.charts.grid.bar;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.qiyukf.desk.chart.charts.grid.GridChart;
import com.qiyukf.desk.chart.charts.grid.GridData;
import com.qiyukf.desk.utils.sys.ScreenUtils;

/**
 * Created by zhoujianghua on 2016/10/4.
 */
public class BarChart extends GridChart<BarConfiguration> {
    private Paint barPaint = new Paint();

    public BarChart() {
        super();
        setConfiguration(BarConfiguration.DEFAULT);

        setupPaints();
    }

    @Override
    protected void setupPaints() {
        super.setupPaints();

        // 柱状图画笔
        barPaint.setAntiAlias(true);
    }

    @Override
    protected void drawDesc(Canvas canvas) {
        float blockLength = getTextHeight() / 2;
        float spacing1 = ScreenUtils.dp2px(5);
        float spacing2 = ScreenUtils.dp2px(15);

        float descMaxLength = (blockLength + spacing1) * dataList.get(0).getEntries().length +
                spacing2 * (dataList.get(0).getEntries().length - 1);
        for (GridData.Entry entry : dataList.get(0).getEntries()) {
            descMaxLength += textPaint.measureText(entry.getDesc());
        }

        float descStartX = (getWidth() - horizontalOffset - descMaxLength) / 2 + horizontalOffset;

        textPaint.setTextAlign(Paint.Align.LEFT);
        for (GridData.Entry entry : dataList.get(0).getEntries()) {
            barPaint.setColor(entry.getDescColor());
            textPaint.setColor(entry.getDescColor());
            canvas.drawRect(descStartX, getHeight() - (getTextHeight() + blockLength) / 2,
                    descStartX + blockLength, getHeight() - (getTextHeight() - blockLength) / 2, barPaint);

            descStartX += blockLength + spacing1;
            canvas.drawText(entry.getDesc(), descStartX, getHeight() - getTextOffsetY(), textPaint);
            descStartX += textPaint.measureText(entry.getDesc()) + spacing2;
        }
    }

    @Override
    protected void drawContent(Canvas canvas) {
        // 绘制柱状图
        for (int i = firstRenderItem; i <= lastRenderItem; i++) {
            GridData.Entry[] entries = dataList.get(i).getEntries();
            // 设定间距为柱宽的1/3
            float spacing = getItemWidth() / (entries.length * 4 + 1);
            float barWidth = spacing * 3;
            for (int j = 0; j < entries.length; j++) {
                barPaint.setColor(entries[j].getLineColor());
                float left = getItemWidth() * i + spacing + (barWidth + spacing) * j;
                float right = left + barWidth;
                float bottom = getChartBottom();
                float top = bottom - entries[j].getValue() * getItemHeightRatio() * getInFraction();
                canvas.drawRect(left, top, right, bottom, barPaint);
            }
        }
    }

    @Override
    protected void calculateRenderRange() {
        firstRenderItem = 0;
        lastRenderItem = dataList.size() - 1;
        for (int i = 0; i < dataList.size(); i++) {
            if (getItemWidth() * (i + 1) + translateX > 0) {
                firstRenderItem = i;
                break;
            }
        }

        for (int i = firstRenderItem; i < dataList.size(); i++) {
            if (getItemWidth() * (i + 1) + translateX >= getChartWidth()) {
                lastRenderItem = i;
                break;
            }
        }
    }
}
