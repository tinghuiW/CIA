/*
 * Copyright (c) 2017 CMPUT301F17T15. This project is distributed under the MIT license.
 */

package com.cmput301.cia.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cmput301.cia.R;
import com.cmput301.cia.models.Habit;
import com.cmput301.cia.models.Profile;
import com.cmput301.cia.utilities.ElasticSearchUtilities;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gsp on 2017/11/13.
 */

public class StatisticViewActivity extends AppCompatActivity {
    private Profile user;
    private TextView typeName;
    private TextView typeNumber;
    private TextView completeNumber;
    private TextView totalNumber;
    PieChart pieChart;
    int[] yData;
    String[] xData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_view);

        String UserID = getIntent().getStringExtra("userName");
        Map<String, String> values = new HashMap<>();
        values.put("name", UserID);
        user = ElasticSearchUtilities.getObject("profile", Profile.class, values);
        String type = getIntent().getStringExtra("type");
        typeName = (TextView) findViewById(R.id.Type_Name);
        typeName.setText(type);
        typeNumber = (TextView) findViewById(R.id.habitsNumber);
        typeNumber.setText(String.valueOf(user.getHabitsInCategory(type).size()));
        completeNumber = (TextView) findViewById(R.id.completeNumber);
        completeNumber.setText(String.valueOf(user.getHabitHistory().size()));
        totalNumber = (TextView) findViewById(R.id.TotalNumber);
        //Todo use total passed habits
        int total = 3*user.getHabitHistory().size();
        totalNumber.setText(String.valueOf(total));
        yData = new int[]{total - user.getHabitHistory().size(), user.getHabitHistory().size()};
        xData = new String[]{"Total","Complete"};

        pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleRadius(15f);

        addData(pieChart);

    }

    private void addData(PieChart chart) {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            yEntrys.add(new PieEntry(yData[i] , i));
            xEntrys.add(xData[i]);
        }
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Number habits");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueTextColor(Color.WHITE);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        pieDataSet.setColors(colors);
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}