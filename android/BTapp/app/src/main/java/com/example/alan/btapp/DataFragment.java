package com.example.alan.btapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import static com.example.alan.btapp.StartActivity.mConnectedThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {

    private LineChart lineChart;

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        //mainLayout = (RelativeLayout) view.findViewById(R.id.);

        lineChart = (LineChart) view.findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(2, 10));
        entries.add(new Entry(3, 23));
        entries.add(new Entry(4, 54));
        entries.add(new Entry(5, 32));
        entries.add(new Entry(6, 13));
        entries.add(new Entry(7, 1));

        LineDataSet dataSet = new LineDataSet(entries, "Energy"); // add entries to dataset
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
    }
}
