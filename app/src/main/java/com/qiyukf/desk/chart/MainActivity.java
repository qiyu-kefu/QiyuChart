package com.qiyukf.desk.chart;

import android.graphics.Color;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.qiyukf.desk.chart.animation.Interpolators;
import com.qiyukf.desk.chart.charts.grid.GridData;
import com.qiyukf.desk.chart.charts.grid.bar.BarChart;
import com.qiyukf.desk.chart.charts.grid.bar.BarConfiguration;
import com.qiyukf.desk.chart.charts.grid.line.LineChart;
import com.qiyukf.desk.chart.charts.grid.line.LineConfiguration;
import com.qiyukf.desk.chart.charts.pie.PieChart;
import com.qiyukf.desk.chart.charts.pie.PieConfiguration;
import com.qiyukf.desk.chart.charts.pie.PieData;
import com.qiyukf.desk.chart.view.ChartTextureView;
import com.qiyukf.desk.chart.view.ChartView;
import com.qiyukf.desk.utils.sys.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ScrollView rootView;

    private LinearLayout chartContainer;

    private ChartTextureView chartTextureView;

    private ChartView normalChartView;

    private LineChart mutableLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenUtils.init(this);

        setContentView(R.layout.activity_main);

        rootView = (ScrollView) findViewById(R.id.root_scroll_view);

        chartContainer = (LinearLayout) findViewById(R.id.charts_container);

        normalChartView = (ChartView) findViewById(R.id.normal_chart_view);

        setupChartView();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mutableLineChart.addData(randomData("Jan"));
                mutableLineChart.addData(randomData("Feb"));
                mutableLineChart.addData(randomData("Mar"));
                mutableLineChart.addData(randomData("Apr"));
                mutableLineChart.notifyDataChanged(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TextureView 在某些手机上screen lock unlock之后，可能会变空白，这里重绘一下
        chartTextureView.render(null);
    }

    private void setupChartView() {
        chartTextureView = new ChartTextureView(this);
        chartTextureView.addChart(pieChart(new RectF(50, 50, 800, 500)));
        chartTextureView.addChart(barChart(new RectF(50, 550, ScreenUtils.getScreenWidth() - 200, 1000)));
        chartContainer.addView(chartTextureView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        normalChartView.addChart(lineChart(new RectF(50, 50, ScreenUtils.getScreenWidth() - 200, 500)));
        normalChartView.addChart(singleLineChart(new RectF(50, 550, ScreenUtils.getScreenWidth() - 200, 1000)));
    }

    private Chart pieChart(RectF bound) {
        PieChart pieChart = new PieChart();
        pieChart.addData(new PieData(20, 0xFF916de3, "赵"));
        pieChart.addData(new PieData(25, 0xFF87d3ac, "钱"));
        pieChart.addData(new PieData(10, 0xFFa9abad, "孙"));
        pieChart.addData(new PieData(30, 0xFF5092e1, "李"));

        pieChart.setBounds(bound);

        pieChart.setConfiguration(PieConfiguration.create()
                .setRingRatio(0.5f)
                .setPercentageColor(Color.WHITE)
                .setInnerBorder(true)
                .setBorderColor(Color.WHITE)
                .setBorderWidth(ScreenUtils.dp2px(2))
                .setShowPercentage(true));

        return pieChart;
    }

    private Chart barChart(RectF bound) {
        BarChart barChart = new BarChart();
        barChart.addData(randomData("Mon"));
        barChart.addData(randomData("Tue"));
        barChart.addData(randomData("Wed"));
        barChart.addData(randomData("Thu"));
        barChart.addData(randomData("Fri"));
        barChart.addData(randomData("Sat"));
        barChart.addData(randomData("Sun"));

        barChart.setConfiguration(BarConfiguration.create()
                .setTextSize(ScreenUtils.dp2px(12))
                .setTextColor(Color.GRAY)
                .setShowDesc(true)
                .setShowInAnimation(false)
                .setInAnimationInterpolator(Interpolators.LINEAR)
                .setInAnimationDuration(900));
        barChart.setBounds(bound);

        barChart.setScrollView(rootView);
        return barChart;
    }

    private Chart lineChart(RectF bound) {
        List<GridData> dataList = new ArrayList<>();
        dataList.add(randomData("Mon"));
        dataList.add(randomData("Tue"));
        dataList.add(randomData("Wed"));
        dataList.add(randomData("Thu"));
        dataList.add(randomData("Fri"));
        dataList.add(randomData("Sat"));
        dataList.add(randomData("Sun"));

        mutableLineChart = new LineChart();
        mutableLineChart.setDataList(dataList);
        mutableLineChart.setConfiguration(LineConfiguration.create()
                .setTextSize(ScreenUtils.dp2px(12))
                .setTextColor(Color.BLACK)
                .setShowShadow(true)
                .setShowInAnimation(true)
                .setInAnimationInterpolator(Interpolators.LINEAR)
                .setInAnimationDuration(900));
        mutableLineChart.setBounds(bound);

        mutableLineChart.setScrollView(rootView);
        return mutableLineChart;
    }

    private Chart singleLineChart(RectF bound) {
        List<GridData> dataList = new ArrayList<>();
        dataList.add(randomDataSingle("Mon"));
        dataList.add(randomDataSingle("Tue"));
        dataList.add(randomDataSingle("Wed"));
        dataList.add(randomDataSingle("Thu"));
        dataList.add(randomDataSingle("Fri"));
        dataList.add(randomDataSingle("Sat"));
        dataList.add(randomDataSingle("Sun"));

        LineChart lineChart = new LineChart();
        lineChart.setDataList(dataList);
        lineChart.setConfiguration(LineConfiguration.create()
                .setTextSize(ScreenUtils.dp2px(12))
                .setTextColor(Color.BLACK)
                .setShowShadow(true)
                .setShowDesc(false)
                .setShowInAnimation(true)
                .setInAnimationInterpolator(Interpolators.ACC_DEC)
                .setInAnimationDuration(900));
        lineChart.setBounds(bound);

        lineChart.setScrollView(rootView);
        return lineChart;
    }

    private GridData randomData(String title) {
        GridData.Entry[] entries = new GridData.Entry[4];
        final String[] descs = new String[]{"赵", "钱", "孙", "李"};
        final int[] colors = new int[]{0xFF916de3, 0xFF87d3ac, 0xFFa9abad, 0xFF5092e1};
        for (int i = 0; i < 4; i++) {
            entries[i] = new GridData.Entry(colors[i], descs[i], new Random().nextInt(101));
        }
        return new GridData(title, "tips标题", entries);
    }

    private GridData randomDataSingle(String title) {
        GridData.Entry[] entries = new GridData.Entry[1];
        entries[0] = new GridData.Entry(0xFF916de3, "赵", new Random().nextInt(101));
        return new GridData(title, "tips标题", entries);
    }
}
