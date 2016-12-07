package com.example.alan.btapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        final MisMeter meter = (MisMeter) view.findViewById(R.id.meter);

        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("executed");
                    mConnectedThread.write("#R~");
                    meter.setProgress(voltage.floatValue() / 100);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        exec.shutdown();
    }
}
