package com.example.alan.btapp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static com.example.alan.btapp.DataFragment.graphData;
import static com.example.alan.btapp.MainFragment.current;
import static com.example.alan.btapp.MainFragment.voltage;

public class StartActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3;

    public static Handler mHandler;
    public static ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter BA;

    private StringBuilder recDataString = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        BA = BluetoothAdapter.getDefaultAdapter();

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){

                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);               //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~"); // determine the end-of-line
                    if (endOfLineIndex > 0) {                        // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string

                        if (recDataString.charAt(0) == '#' && recDataString.charAt(1) == 'D') {
                            recDataString.deleteCharAt(0);
                            recDataString.deleteCharAt(0);
                            recDataString.substring(recDataString.length());
                            graphData = recDataString.toString().split("\\|");
                            for (String data : graphData) {
                                System.out.println(data);
                            }
                        }

                        if (recDataString.charAt(0) == '#' && recDataString.charAt(1) == 'R') //if it starts with # we know it is what we are looking for
                        {
                            recDataString.deleteCharAt(0);
                            recDataString.deleteCharAt(0);
                            String[] nums = recDataString.toString().split("\\+");
                            nums[1] = nums[1].substring(0, 4);
                            voltage = Float.parseFloat(nums[0]);
                            current = Float.parseFloat(nums[1]);
                            System.out.println(voltage + " . " + current);
                        }

                        if (recDataString.charAt(0) == '#' && recDataString.charAt(1) == 'C') {
                            Toast.makeText(getApplicationContext(), "Log files are cleared", Toast.LENGTH_SHORT).show();
                            System.out.println(recDataString.toString());
                        }
                        recDataString.delete(0, recDataString.length());            //clear all string data
                        dataInPrint = " ";
                    }
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1) {
                        Toast.makeText(getApplicationContext(), "Connected to Device: " + (String) (msg.obj), Toast.LENGTH_SHORT).show();
                        Intent mainMenu = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(mainMenu);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Connection failed",Toast.LENGTH_SHORT).show();
                }
            }
        };



        if (BA == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        // Enable Bluetooth
        if (!BA.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // ListView paired devices
        ListView lv = (ListView) findViewById(R.id.lv_paired);
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();
        for(BluetoothDevice btz : pairedDevices) list.add(btz.getName() + "\n" + btz.getAddress());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(mDeviceClickListener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView lv = (ListView) findViewById(R.id.lv_paired);
                ArrayList list = new ArrayList();
                Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
                for (BluetoothDevice btz : pairedDevices)
                    list.add(btz.getName() + "\n" + btz.getAddress());
                Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
                ArrayAdapter adapter = new ArrayAdapter(StartActivity.this, android.R.layout.simple_list_item_1, list);
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(mDeviceClickListener);
            }
        });
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!BA.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getApplicationContext(),"Connecting...",Toast.LENGTH_SHORT).show();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = BA.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }
}
