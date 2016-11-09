package com.qiyukf.desk.chart.charts.pie;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.qiyukf.desk.chart.Chart;
import com.qiyukf.desk.chart.animation.Interpolators;
import com.qiyukf.desk.chart.animation.TimingAnimation;
import com.qiyukf.desk.chart.view.ChartGestureListener;
import com.qiyukf.desk.utils.sys.ScreenUtils;

import java.util.List;
import java.util.Locale;

/**
 * Created by zhoujianghua on 2016/10/1.
 */

public class PieChart extends Chart<PieData, PieConfiguration> {

    private float total = 0;

    private PointF origin;
    private float radius;

    // highlight
    private int highlight;
    private float highlightFraction;

    // animation
    private TimingAnimation currentAnimation;

    // avoid allocation while drawing
    private Paint shapePaint;
    private Paint textPaint;
    private RectF square;
    private Path path;

    private Paint.FontMetrics descMetrics;
    private float percentageTextHeight;

    public PieChart() {
        setConfiguration(PieConfiguration.DEFAULT);

        // setup paint
        shapePaint = new Paint();
        shapePaint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);

        square = new RectF();
        path = new Path();

        highlight = -1;

        // gesture
        setupGesture();
    }

    @Override
    public void addData(PieData data) {
        super.addData(data);
        total += data.getProportion();
    }

    @Override
    public void setDataList(List<PieData> dataList) {
        super.setDataList(dataList);
        total = 0;
        for (PieData data : dataList) {
            total += data.getProportion();
        }
    }

    public void highlight(int index) {
        if (index < 0 || index >= dataList.size()) {
            return;
        }

        if (currentAnimation != null && currentAnimation.isRunning()) {
            // 还有动画在做, 直接让他完成
            currentAnimation.complete();
        }
        if (index == highlight) {
            reset();
        } else {
            highlight = index;
            highlightFraction = 0;

            startHighlightAnimation();
        }
    }

    public void reset() {
        highlight = -1;
        highlightFraction = 0;
        invalidate();
    }

    @Override
    protected void onConfiguration() {
        total = 0;
        for (PieData data : dataList) {
            total += data.getProportion();
        }
        textPaint.setTextSize(configuration.getPercentageTextSize());
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        percentageTextHeight = fontMetrics.descent - fontMetrics.ascent;

        textPaint.setTextSize(configuration.getDescTextSize());
        descMetrics = textPaint.getFontMetrics();

        float maxRadius = Math.min(bound.width(), bound.height()) / 2;
        float hlScale = 1 + configuration.getHighlightScale();
        radius = maxRadius / hlScale;
        if (configuration.showPercentage()) {
            // 还有可能会显示在圆饼外面， 后面再想想怎么处理
        }

        origin = new PointF();
        if (configuration.getDescPosition() == PieConfiguration.DESC_POS_LEFT) {
            float maxDescWidth = getDescMaxWidth();
            float x = Math.min(maxDescWidth + maxRadius, bound.width() - maxRadius);
            origin.set(x, maxRadius);
        } else if(configuration.getDescPosition() == PieConfiguration.DESC_POS_RIGHT){
            origin.set(maxRadius, maxRadius);
        } else {
            origin.set(bound.width()/2, bound.height()/2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (total <= 0 || dataList.size() == 0) {
            return;
        }

        float fraction = getInFraction();

        drawDecs(canvas);

        float proportion = 0;
        for (int index = 0; index < dataList.size(); ++index) {
            canvas.save();
            drawData(canvas, index, proportion);
            canvas.restore();
            PieData data = dataList.get(index);
            proportion += (data.getProportion() / total * fraction);
        }

        if (configuration.innerBorder()) {
            proportion = 0;
            for (int index = 0; index < dataList.size(); ++index) {
                drawInnerBorder(canvas, proportion);
                proportion += (dataList.get(index).getProportion() / total * fraction);
            }
        }
    }

    private void drawDecs(Canvas canvas) {
        if(configuration.getDescPosition() != PieConfiguration.DESC_POS_MIDDLE) {
            float descTextHeight = descMetrics.descent - descMetrics.ascent;
            float rectWidth = descTextHeight - 10;
            float rectTop = descMetrics.ascent + 5;
            float textBottom = descTextHeight;
            canvas.save();
            if (configuration.getDescPosition() == PieConfiguration.DESC_POS_LEFT) {
                canvas.translate(0, Math.min(origin.y / 4, origin.y - descTextHeight * dataList.size() / 2));
            } else {
                canvas.translate(radius * 2.4f, origin.y - descTextHeight * dataList.size() / 2);
            }

            shapePaint.setStyle(Paint.Style.FILL);
            textPaint.setTextSize(configuration.getDescTextSize());
            textPaint.setColor(configuration.getDescTextColor());
            square.set(0, rectTop, rectWidth, rectTop + rectWidth);
            for (PieData data : dataList) {
                shapePaint.setColor(data.getColor());
                square.offset(0, descTextHeight);
                canvas.drawRect(square, shapePaint);
                String text = String.format("%s  %s个(%s%%)", data.getTitle(), (int) data.getProportion(), (int) (data.getProportion() * 100 / total));
                canvas.drawText(text, descTextHeight, textBottom, textPaint);
                textBottom += descTextHeight;
            }
            canvas.restore();
        }
    }

    private void drawData(Canvas canvas, int index, float proportion) {
        canvas.translate(origin.x, origin.y);

        PieData data = dataList.get(index);

        double startRadians = proportion * 2 * Math.PI;
        float startX = (float) (radius * Math.sin(startRadians));
        float startY = (float) (-radius * Math.cos(startRadians));

        float sweep = data.getProportion() / total * getInFraction();

        double endRadians = (proportion + sweep) * 2 * Math.PI;
        float endX = (float) (radius * Math.sin(endRadians));
        float endY = (float) (-radius * Math.cos(endRadians));

        if (index == highlight) {
            float offset = highlightFraction * radius * configuration.getHighlightScale();
            float offsetX = (float) (offset * Math.sin((startRadians + endRadians) / 2));
            float offsetY = (float) (-offset * Math.cos((startRadians + endRadians) / 2));
            canvas.translate(offsetX, offsetY);
        }

        square.set(-radius, -radius, radius, radius);

        float startAngle = proportion * 360 - 90;
        float sweepAngle = sweep * 360;

        // fill
        shapePaint.setStyle(Paint.Style.FILL);
        shapePaint.setColor(data.getColor());

        float ringRatio = configuration.getRingRatio();
        path.reset();
        path.moveTo(startX * ringRatio, startY * ringRatio);
        path.lineTo(startX, startY);
        if (sweepAngle == 360) {
            // 360 == 0，分两次画
            path.arcTo(square, startAngle, sweepAngle / 2);
            path.arcTo(square, startAngle + sweepAngle / 2, sweepAngle / 2);
        } else {
            path.arcTo(square, startAngle, sweepAngle);
        }
        path.lineTo(endX * ringRatio, endY * ringRatio);
        if (ringRatio > 0) {
            square.inset(radius * ringRatio, radius * ringRatio);
            if (sweepAngle == 360) {
                // 360 == 0，分两次画
                path.arcTo(square, startAngle + sweepAngle, -sweepAngle / 2);
                path.arcTo(square, startAngle + sweepAngle / 2, -sweepAngle / 2);
            } else {
                path.arcTo(square, startAngle + sweepAngle, -sweepAngle);
            }
        }

        canvas.drawPath(path, shapePaint);

        if (getInFraction() < 1 && configuration.showPercentage()) { // 还在动画中，不画
            return;
        }
        //draw ratio text
        if(configuration.getDescPosition() != PieConfiguration.DESC_POS_MIDDLE) {
            float textX = 0;
            float textY = 0;

            textPaint.setColor(configuration.getPercentageColor());
            textPaint.setTextSize(configuration.getPercentageTextSize());
            String ratio = String.format(Locale.CHINA, "%.1f%%", data.getProportion() / total * 100);
            float textWidth = textPaint.measureText(ratio);
            if (textInCircle(radius, (float) (endRadians - startRadians), textWidth)) {
                float pos = radius * (1 + configuration.getRingRatio()) / 2;
                textX += pos * Math.sin((startRadians + endRadians) / 2);
                textY += -pos * Math.cos((startRadians + endRadians) / 2);
            } else {
                textX += (radius + textWidth / 2) * Math.sin((startRadians + endRadians) / 2);
                textY += -(radius + percentageTextHeight) * Math.cos((startRadians + endRadians) / 2);
            }
            textX -= textWidth / 2;
            textY += percentageTextHeight / 2;
            canvas.drawText(ratio, textX, textY, textPaint);
        }

    }

    private void drawInnerBorder(Canvas canvas, float proportion) {
        canvas.save();
        canvas.translate(origin.x, origin.y);

        shapePaint.setStyle(Paint.Style.STROKE);
        shapePaint.setStrokeWidth(configuration.getBorderWidth());
        shapePaint.setColor(configuration.getBorderColor());

        double radians = proportion * 2 * Math.PI;
        float innerRadius = radius * (1 - configuration.getRingRatio());
        float startX = (float) (innerRadius * Math.sin(radians));
        float startY = (float) (-innerRadius * Math.cos(radians));
        float endX = (float) (radius * Math.sin(radians));
        float endY = (float) (-radius * Math.cos(radians));

        // 先清除再画
        shapePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawLine(startX, startY, endX, endY, shapePaint);

        shapePaint.setXfermode(null);
        canvas.drawLine(startX, startY, endX, endY, shapePaint);

        canvas.restore();
    }

    private boolean textInCircle(float radius, float angle, float textWidth) {
        if (angle > Math.PI) {
            return true;
        }
        double availableSpace = radius * Math.tan(angle / 2);
        double textNeedSpace = Math.sqrt(textWidth * textWidth + Math.pow(percentageTextHeight, 2));
        return Math.abs(availableSpace) > textNeedSpace;
    }

    private float getDescMaxWidth() {
        textPaint.setTextSize(configuration.getDescTextSize());
        float maxWidth = 0;
        for (PieData data : dataList) {
            maxWidth = Math.max(textPaint.measureText(data.getTitle()), maxWidth);
        }
        float rectWidth = descMetrics.descent - descMetrics.ascent;
        return maxWidth + rectWidth + ScreenUtils.dp2px(5);
    }

    private int pointToIndex(PointF point) {
        float distanceX = point.x - origin.x;
        float distanceY = origin.y - point.y; // y坐标改为向上增长，方便计算

        int index = degreeIndex(distanceX, distanceY);
        return inBoundOfIndex(index, distanceX, distanceY);
    }

    private int degreeIndex(float distanceX, float distanceY) {
        // 先计算落在哪一格中
        double degree = Math.toDegrees(Math.atan2(distanceY, distanceX));
        // 将degree调整到从y轴往上，顺时针方向的角度上
        degree = 90 - degree;
        if (degree < 0) {
            degree += 360;
        }

        float mark = (float) (degree / 360 * total);

        int index = -1;
        float accumulation = 0;
        for (int i = 0; i < dataList.size(); ++i) {
            accumulation += dataList.get(i).getProportion();
            if (accumulation > mark) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int inBoundOfIndex(int index, float distanceX, float distanceY) {
        // 再看看是不是在圆范围内
        if (index != -1) {
            float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
            if (index == highlight) {
                distance = distance / (1 + configuration.getHighlightScale());
            }
            if (distance < radius && distance >= radius * configuration.getRingRatio()) {
                return index;
            }
        }
        return -1;
    }

    private void startHighlightAnimation() {
        currentAnimation = new TimingAnimation();
        currentAnimation.setDuration(300)
                .setInterpolator(Interpolators.OVERSHOOT)
                .setListener(new TimingAnimation.AnimationListener() {
                    @Override
                    public void onValueUpdate(TimingAnimation animation, float fraction) {
                        highlightFraction = fraction;
                        invalidate();
                    }
                });
        currentAnimation.start();
    }

    private void setupGesture() {
        gestureListener = new ChartGestureListener() {
            @Override
            public boolean onSingleTap(float x, float y) {
                if (!isFullCircle()) {
                    int index = pointToIndex(new PointF(x, y));
                    if (index != -1) {
                        highlight(index);
                        return true;
                    }
                }
                return false;
            }
        };

        enableGesture(true, false);
    }

    private boolean isFullCircle() {
        for (PieData pieData : dataList) {
            if (pieData.getProportion() == total) {
                return true;
            }
        }
        return false;
    }
}
