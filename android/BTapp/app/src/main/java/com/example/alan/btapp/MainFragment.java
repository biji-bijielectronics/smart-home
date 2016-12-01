package com.example.alan.btapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import static com.example.alan.btapp.StartActivity.mConnectedThread;
import static com.example.alan.btapp.StartActivity.voltage;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private AwesomeSpeedometer awesomeSpeedometer;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        awesomeSpeedometer = (AwesomeSpeedometer) view.findViewById(R.id.speedView);

        while (true) {
            try {

                mConnectedThread.write("#R~");
                mConnectedThread.sleep(1000);
                awesomeSpeedometer.speedTo(voltage); //voltage * current
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        awesomeSpeedometer.invalidate();
//        awesomeSpeedometer.speedTo(20);
//        if (awesomeSpeedometer.getSpeed() < 25) {
//            awesomeSpeedometer.setSpeedometerColor(Color.BLUE);
//            awesomeSpeedometer.speedTo(50);
//        } else if (awesomeSpeedometer.getSpeed() > 25 && awesomeSpeedometer.getSpeed() < 60) {
//            awesomeSpeedometer.setSpeedometerColor(Color.GREEN);
//            awesomeSpeedometer.speedTo(80);
//        } else if (awesomeSpeedometer.getSpeed() > 60 && awesomeSpeedometer.getSpeed() < 100) {
//            awesomeSpeedometer.setSpeedometerColor(Color.RED);
//            awesomeSpeedometer.speedTo(0);
//        }
    }
}
