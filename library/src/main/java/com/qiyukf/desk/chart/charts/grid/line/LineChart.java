package com.qiyukf.desk.chart.charts.grid.line;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.qiyukf.desk.chart.charts.grid.GridChart;
import com.qiyukf.desk.chart.charts.grid.GridData;
import com.qiyukf.desk.utils.sys.ScreenUtils;

/**
 * Created by hzwangchenyan on 2016/10/8.
 */
public class LineChart extends GridChart<LineConfiguration> {
    private Paint linePaint = new Paint();
    private Paint tapLinePaint = new Paint();
    private Paint pointPaint = new Paint();
    private Paint clearPaint = new Paint();
    private Paint shadowPaint = new Paint();

    private Path path = new Path();

    private RectF tipsRect = new RectF();
    private int tapPosition = -1;

    public LineChart() {
        super();

        setConfiguration(LineConfiguration.DEFAULT);
        setupPaints();
    }

    @Override
    protected void setupPaints() {
        super.setupPaints();

        // 曲线画笔
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(configuration.getLineWidth());

        // 点击选中线画笔
        tapLinePaint.setAntiAlias(true);
        tapLinePaint.setColor(0xFF979797);
        tapLinePaint.setStyle(Paint.Style.STROKE);
        tapLinePaint.setStrokeWidth(ScreenUtils.dp2px(1));

        // 圆点画笔
        pointPaint.setAntiAlias(true);

        // 擦除画笔
        clearPaint.setAntiAlias(true);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // 阴影画笔
        shadowPaint.setAntiAlias(true);
    }

    @Override
    protected void onConfiguration() {
        super.onConfiguration();

        if (!isVisible()) {
            return;
        }

        tapPosition = -1;
    }

    @Override
    protected void drawDesc(Canvas canvas) {
        float pointRadius = ScreenUtils.dp2px(3.5f);
        float spacing1 = ScreenUtils.dp2px(5);
        float spacing2 = ScreenUtils.dp2px(15);
        float descMaxLength = (pointRadius * 2 + spacing1) * dataList.get(0).getEntries().length +
                spacing2 * (dataList.get(0).getEntries().length - 1);
        for (GridData.Entry entry : dataList.get(0).getEntries()) {
            descMaxLength += textPaint.measureText(entry.getDesc());
        }

        float descStartX = (getWidth() - horizontalOffset - descMaxLength) / 2 + horizontalOffset;
        float descMidY = getHeight() - getTextHeight() / 2;

        textPaint.setTextAlign(Paint.Align.LEFT);
        for (GridData.Entry entry : dataList.get(0).getEntries()) {
            pointPaint.setColor(entry.getDescColor());
            textPaint.setColor(entry.getDescColor());
            canvas.drawCircle(descStartX + pointRadius, descMidY, pointRadius, pointPaint);

            descStartX += pointRadius * 2 + spacing1;
            canvas.drawText(entry.getDesc(), descStartX, getHeight() - getTextOffsetY(), textPaint);
            descStartX += textPaint.measureText(entry.getDesc()) + spacing2;
        }
    }

    @Override
    protected void drawContent(Canvas canvas) {
        for (int index = 0; index < dataList.get(0).getEntries().length; ++index) {
            drawLine(canvas, index);
        }
    }

    private void drawLine(Canvas canvas, int index) {
        // 绘制曲线
        float currentX, currentY, nextX, nextY;
        path.reset();
        for (int i = firstRenderItem; i < lastRenderItem; i++) {
            currentX = getItemWidth() * i;
            currentY = getChartBottom() - (dataList.get(i).getEntries()[index].getValue() * getItemHeightRatio());
            nextX = getItemWidth() * (i + 1);
            nextY = getChartBottom() - (dataList.get(i + 1).getEntries()[index].getValue() * getItemHeightRatio());

            if (i == firstRenderItem) {
                path.moveTo(currentX, currentY);
            }
            path.lineTo(nextX, nextY);
        }
        linePaint.setColor(dataList.get(0).getEntries()[index].getLineColor());
        canvas.drawPath(path, linePaint);

        // 绘制阴影
        if (configuration.isShowShadow() && dataList.size() > 1) {
            int color = dataList.get(0).getEntries()[index].getLineColor();
            int alphaColor = Color.argb((int) (Color.alpha(color) * 0.3), Color.red(color), Color.green(color), Color.blue(color));
            shadowPaint.setColor(alphaColor);
            float lastRenderX = getItemWidth() * lastRenderItem;
            path.lineTo(lastRenderX, getChartBottom());
            path.lineTo(getItemWidth() * firstRenderItem, getChartBottom());
            path.lineTo(getItemWidth() * firstRenderItem, getChartBottom() - (dataList.get(firstRenderItem).getEntries()[0].getValue() * getItemHeightRatio()));
            canvas.drawPath(path, shadowPaint);
        }
    }

    @Override
    protected void onScrollBegin(float distanceX, float distanceY) {
        tapPosition = -1;
    }

    @Override
    protected boolean onSingleTapImpl(float x, float y) {
        PointF point = new PointF(x - horizontalOffset, y);
        if (point.x < 0 || point.y > getChartBottom()) {
            return false;
        }
        RectF rect = new RectF();
        for (int i = 0; i < dataList.size(); i++) {
            float measuredX = getItemWidth() * i + translateX;
            if (measuredX < 0 || (int) measuredX > (int) (getWidth() - horizontalOffset)) {
                // 超出屏幕不关心
                continue;
            }
            if (dataList.get(i).getEntries()[0].getValue() < 0) {
                // 不合法
                continue;
            }
            float radius = defaultItemWidth * 0.5f;
            rect.left = measuredX - radius;
            rect.top = 0;
            rect.right = measuredX + radius;
            rect.bottom = getChartBottom();
            if (rect.contains(point.x, point.y)) {
                tapPosition = (tapPosition == i) ? -1 : i;
                invalidate();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void calculateRenderRange() {
        // 计算绘制起止点，超出屏幕区域不绘制
        firstRenderItem = 0;
        lastRenderItem = dataList.size() - 1;
        for (int i = 0; i < dataList.size() - 1; i++) {
            float nextX = getItemWidth() * (i + 1);

            if (nextX + translateX > 0) {
                firstRenderItem = i;
                break;
            }
        }

        for (int i = firstRenderItem + 1; i < dataList.size(); i++) {
            float x = getItemWidth() * i;

            if (x + translateX >= getChartWidth()) {
                lastRenderItem = i;
                break;
            }
        }

        for (int i = firstRenderItem; i <= lastRenderItem; i++) {
            if (dataList.get(i).getEntries()[0].getValue() < 0) {
                lastRenderItem = i - 1;
            }
        }
    }
}
