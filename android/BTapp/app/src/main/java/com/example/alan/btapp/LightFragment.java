package com.example.alan.btapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import static com.example.alan.btapp.StartActivity.mConnectedThread;

public class LightFragment extends Fragment {
    ToggleButton toggle, toggle2, toggle3, toggle4, toggle5;
    SeekBar brightness1, brightness2, brightness3, brightness4, brightness5;

    public LightFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_light, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        brightness1 = (SeekBar) view.findViewById(R.id.seekBar);
        brightness2 = (SeekBar) view.findViewById(R.id.seekBar2);
        brightness3 = (SeekBar) view.findViewById(R.id.seekBar3);
        brightness4 = (SeekBar) view.findViewById(R.id.seekBar4);
        brightness5 = (SeekBar) view.findViewById(R.id.seekBar5);

        brightness1.setMax(255);
        brightness2.setMax(255);
        brightness3.setMax(255);
        brightness4.setMax(255);
        brightness5.setMax(255);

        toggle = (ToggleButton) view.findViewById(R.id.toggleBtn);
        toggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //
                if (toggle.isChecked()) {
                    mConnectedThread.write("#L1+255~");    // Send "1" via Bluetooth
                } else {
                    mConnectedThread.write("#L1+0~");    // Send "0" via Bluetooth
                }
            }
        });
        toggle2 = (ToggleButton) view.findViewById(R.id.toggleBtn2);
        toggle2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //
                if (toggle2.isChecked()) {
                    mConnectedThread.write("#L2+255~");    // Send "1" via Bluetooth
                } else {
                    mConnectedThread.write("#L2+0~");    // Send "0" via Bluetooth
                }
            }
        });
        toggle3 = (ToggleButton) view.findViewById(R.id.toggleBtn3);
        toggle3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //
                if (toggle3.isChecked()) {
                    mConnectedThread.write("#L3+255~");    // Send "1" via Bluetooth
                } else {
                    mConnectedThread.write("#L3+0~");    // Send "0" via Bluetooth
                }
            }
        });
        toggle4 = (ToggleButton) view.findViewById(R.id.toggleBtn4);
        toggle4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //
                if (toggle4.isChecked()) {
                    mConnectedThread.write("#L4+255~");    // Send "1" via Bluetooth
                } else {
                    mConnectedThread.write("#L4+0~");    // Send "0" via Bluetooth
                }
            }
        });
        toggle5 = (ToggleButton) view.findViewById(R.id.toggleBtn5);
        toggle5.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //
                if (toggle5.isChecked()) {
                    mConnectedThread.write("#L5+255~");    // Send "1" via Bluetooth
                } else {
                    mConnectedThread.write("#L5+0~");    // Send "0" via Bluetooth
                }
            }
        });


        brightness1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentVal = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                send2Bluetooth(1, currentVal);


                Toast.makeText(getContext(), "Light 1 : " + currentVal, Toast.LENGTH_SHORT).show();
            }
        });
        brightness2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar2, int i, boolean b) {
                currentVal = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar2) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar2) {

                send2Bluetooth(2, currentVal);
            }
        });

        brightness3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar3, int i, boolean b) {
                currentVal = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar3) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar3) {
                send2Bluetooth(3, currentVal);
            }
        });
        brightness4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar4, int i, boolean b) {
                currentVal = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar4) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar4) {
                send2Bluetooth(4, currentVal);
            }
        });
        brightness5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentVal = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar5, int i, boolean b) {
                currentVal = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar5) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar5) {
                send2Bluetooth(5, currentVal);
            }
        });
    }


    void send2Bluetooth(int led, int brightness) {
        //make sure there is a paired device
        String cmd = "#L" + Integer.toString(led) + "+" + Integer.toString(brightness) + "~";

        mConnectedThread.write(cmd);    // Send "0" via Bluetooth
    }

}
