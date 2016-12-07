package com.example.alan.btapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import static com.example.alan.btapp.StartActivity.mConnectedThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class LightFragment extends Fragment {
    CheckBox light1, light2, light3, light4, light5;

    public LightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_light, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        light1 = (CheckBox) view.findViewById(R.id.checkBox);
        light2 = (CheckBox) view.findViewById(R.id.checkBox2);
        light3 = (CheckBox) view.findViewById(R.id.checkBox3);
        light4 = (CheckBox) view.findViewById(R.id.checkBox4);
        light5 = (CheckBox) view.findViewById(R.id.checkBox5);

        light1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (light1.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L1+255~");
                } else if (!light1.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L1+0~");
                }
            }
        });

        light2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (light2.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L2+255~");
                } else if (!light2.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L2+0~");
                }
            }
        });

        light3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (light3.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L3+255~");
                } else if (!light3.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L1+0~");
                }
            }
        });

        light4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (light4.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L4+255~");
                } else if (!light4.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L4+0~");
                }
            }
        });

        light5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (light5.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L5+255~");
                } else if (!light5.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("#L5+0~");
                }
            }
        });
    }

}
