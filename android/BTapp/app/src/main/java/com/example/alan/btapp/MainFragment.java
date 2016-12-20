package com.example.alan.btapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.yongjhih.mismeter.MisMeter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.alan.btapp.StartActivity.mConnectedThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    public static Float voltage = 0.0f;
    public static Float current = 0.0f;

    private ScheduledExecutorService exec;

    private RoundCornerProgressBar progress1;
    private MisMeter meter;

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
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
//        awesomeSpeedometer = (AwesomeSpeedometer) view.findViewById(R.id.speedView);
//        awesomeSpeedometer.setWithTremble(false);

        //TODO - receive data about battery level and show it in progress bar

        progress1 = (RoundCornerProgressBar) view.findViewById(R.id.roundCornerProgressBar);

        meter = (MisMeter) view.findViewById(R.id.meter);

        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                System.out.println(voltage * current);
                mConnectedThread.write("#R~");

                float slope = (float) (1.0 * (100) / (12.7 - 11.6));
                final float output = (float) (0 + slope * (voltage - 11.6));
                System.out.println(output);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        meter.setProgress((voltage * current) / 100);
                        progress1.setProgress(output);
                    }
                });
            }
        }, 0, 1, TimeUnit.SECONDS);

    }

    @Override
    public void onPause() {
        super.onPause();
        exec.shutdown();
    }
}
