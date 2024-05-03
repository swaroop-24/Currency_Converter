package com.example.currencyconverter;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        double usdToBaseRate = getIntent().getDoubleExtra("USD_TO_BASE_RATE", 0.0);
        double gbpToBaseRate = getIntent().getDoubleExtra("GBP_TO_BASE_RATE", 0.0);
        double eurToBaseRate = getIntent().getDoubleExtra("EUR_TO_BASE_RATE", 0.0);
        double audToBaseRate = getIntent().getDoubleExtra("AUD_TO_BASE_RATE", 0.0);

        BarChart barChart = findViewById(R.id.barChart);
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(true);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) usdToBaseRate));
        entries.add(new BarEntry(1, (float) gbpToBaseRate));
        entries.add(new BarEntry(2, (float) eurToBaseRate));
        entries.add(new BarEntry(3, (float) audToBaseRate));

        BarDataSet dataSet = new BarDataSet(entries, "Exchange Rates");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"USD", "GBP", "EUR", "AUD"}));



        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        barChart.invalidate();
    }
}
