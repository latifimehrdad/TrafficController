package com.ngra.trafficcontroller.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.DialogChartMeasureBinding;
import com.ngra.trafficcontroller.models.ModelChartMeasureDistance;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogChartMeasure extends DialogFragment {


    private Context context;
    private ArrayList<ModelChartMeasureDistance> arrayList;


    @BindView(R.id.DialogIgnor)
    Button DialogIgnor;

    @BindView(R.id.chart)
    BarChart chart;


    public DialogChartMeasure(Context context, ArrayList<ModelChartMeasureDistance> arrayList) {//__ Start DialogChartMeasure
        this.context = context;
        this.arrayList = arrayList;
    }//_____________________________________________________________________________________________ End DialogChartMeasure


    public Dialog onCreateDialog(Bundle savedInstanceState) {//_____________________________________ Start onCreateDialog
        View view = null;
        DialogChartMeasureBinding binding = DataBindingUtil
                .inflate(LayoutInflater
                                .from(this.context),
                        R.layout.dialog_chart_measure,
                        null,
                        false);
        binding.setMeasure("");
        view = binding.getRoot();
        ButterKnife.bind(this, view);
        DialogIgnor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DialogChartMeasure.this.dismiss();
            }
        });
        ConfigChart();

        return new AlertDialog.Builder(context).setView(view).create();
    }//_____________________________________________________________________________________________ End onCreateDialog


    private void ConfigChart() {//__________________________________________________________________ Start ConfigChart
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(60);
        chart.setPinchZoom(false);
        chart.getLegend().setEnabled(true);
        chart.setDrawGridBackground(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(270);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(10);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String title = arrayList.get((int) value % arrayList.size()).getDate();
                return title;
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        //leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        //rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        setData();
    }//_____________________________________________________________________________________________ End ConfigChart


    private void setData() {//______________________________________________________________________ Start setData

        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            values.add(new BarEntry(i, arrayList.get(i).getMeasure()));
        }

        BarDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {

            set1 = new BarDataSet(values, getResources().getString(R.string.InTenDays));

            set1.setDrawIcons(false);

            List<Integer> colors = new ArrayList<>();
            colors.add(context.getResources().getColor(R.color.colorAccent));
            colors.add(context.getResources().getColor(R.color.colorPrimary));

            set1.setColors(colors);


            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.5f);

            chart.setData(data);
        }
    }//_____________________________________________________________________________________________ End setData


}
