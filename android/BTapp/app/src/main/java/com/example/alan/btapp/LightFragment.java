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
    CheckBox checkBox;

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
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("1");
                } else if (!checkBox.isChecked()) {
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("0");
                }
            }
        });
    }

}
