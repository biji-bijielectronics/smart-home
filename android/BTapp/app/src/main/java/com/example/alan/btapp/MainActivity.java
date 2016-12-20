package com.example.alan.btapp;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import static com.example.alan.btapp.DataFragment.currents;
import static com.example.alan.btapp.DataFragment.dates;
import static com.example.alan.btapp.DataFragment.volts;
import static com.example.alan.btapp.StartActivity.database;
import static com.example.alan.btapp.StartActivity.isDone;
import static com.example.alan.btapp.StartActivity.mConnectedThread;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private ProgressDialog mProgressDialog;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectedThread.write("#D~");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new MainFragment(), "Main");
        viewPagerAdapter.addFragments(new LightFragment(), "Light");
        viewPagerAdapter.addFragments(new DataFragment(), "Data");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                try {
                    if (mProgressDialog == null) {
                        mProgressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", "Lading data...");
                        mProgressDialog.setCancelable(false);
                    }

                    if (!mProgressDialog.isShowing()) {
                        mProgressDialog.show();
                    }

                } catch (IllegalArgumentException ie) {
                    ie.printStackTrace();
                } catch (RuntimeException re) {
                    re.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                while (!isDone) {}

                cursor = database.query(DBHelper.TABLE_ENERGY, null, null, null, null, null, null);


                if (cursor.moveToFirst()) {
                    dates = new ArrayList<>();
                    volts = new ArrayList<>();
                    currents = new ArrayList<>();
                    int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
                    int currentIndex = cursor.getColumnIndex(DBHelper.KEY_CURRENT);
                    int voltageIndex = cursor.getColumnIndex(DBHelper.KEY_VOLTAGE);
                    do {
                        dates.add(cursor.getString(dateIndex));
                        volts.add(cursor.getString(voltageIndex));
                        currents.add(cursor.getString(currentIndex));
                    } while (cursor.moveToNext());

                } else
                    Log.d("mLog", "0 rows");
                //database.delete(DBHelper.TABLE_ENERGY, null, null);
                cursor.close();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    if (mProgressDialog != null) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                    }
                } catch (IllegalArgumentException ie) {
                    ie.printStackTrace();

                } catch (RuntimeException re) {
                    re.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnectedThread.cancel();
    }
}
