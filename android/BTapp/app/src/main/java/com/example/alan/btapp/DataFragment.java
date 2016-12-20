package com.example.alan.btapp;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.alan.btapp.StartActivity.database;
import static com.example.alan.btapp.StartActivity.isDone;
import static com.example.alan.btapp.StartActivity.mConnectedThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private LineChart lineChart;
    private List<Entry> entriesV;
    private List<Entry> entriesC;
    private List<Entry> entriesP;
    private LineDataSet voltage;
    private LineDataSet current;
    private LineDataSet power;
    private List<ILineDataSet> dataSets;
    private LineData data;
    private String[] arr;
    private Cursor cursor;
    private XAxis xAxis;
    private ProgressDialog mProgressDialog;

    public static ArrayList<String> dates;
    public static ArrayList<String> currents;
    public static ArrayList<String> volts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        lineChart = (LineChart) view.findViewById(R.id.chart);
        Button button = (Button) view.findViewById(R.id.syncButton);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        cursor = database.query(DBHelper.TABLE_ENERGY, null, null, null, null, null, null);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        try {
                            if (mProgressDialog == null) {
                                mProgressDialog = ProgressDialog.show(getContext(), "Please Wait", "Lading data...");
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
                        mConnectedThread.write("#D~");

                        while (!isDone) {}

                        cursor = database.query(DBHelper.TABLE_ENERGY, null, null, null, null, null, null);

                        if (cursor.moveToFirst()) {
                            dates = new ArrayList<>();
                            volts = new ArrayList<>();
                            currents = new ArrayList<>();
                            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
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

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

                // set the calendar to start of today
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);

                long today = c.getTimeInMillis();

                ArrayList<String> todayDates = new ArrayList<>();
                ArrayList<String> todayCurrents = new ArrayList<>();
                ArrayList<String> todayVolts = new ArrayList<>();
                ArrayList<Float> powerData = new ArrayList<>();
                List<String> date = new ArrayList<String>();

                for (int j = 0; j < dates.size(); j++) {
                    long gotTime = Long.parseLong(dates.get(j)) * 1000;
                    if (gotTime > today) {
                        todayDates.add(dates.get(j));
                        todayVolts.add(volts.get(j));
                        todayCurrents.add(currents.get(j));
                        powerData.add(Float.parseFloat(volts.get(j)) * Float.parseFloat(currents.get(j)));
                    }
                }

                for (int z = 0; z < todayDates.size(); z++) {
                    Date d = new Date(Long.parseLong(todayDates.get(z)) * 1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.ENGLISH);
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String dates = sdf.format(d);

                    date.add(dates);
                }

                arr = date.toArray(new String[date.size()]);

                xAxis = lineChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(arr));

                entriesC = new ArrayList<>();
                entriesV = new ArrayList<>();
                entriesP = new ArrayList<>();

                voltage = new LineDataSet(entriesV, "Voltage"); // add entries to dataset
                voltage.setAxisDependency(YAxis.AxisDependency.LEFT);
                voltage.setColor(Color.RED);
                voltage.setCircleColor(Color.RED);
                voltage.setCircleRadius(1.5f);
                voltage.setLineWidth(1f);

                current = new LineDataSet(entriesC, "Current"); // add entries to dataset
                current.setAxisDependency(YAxis.AxisDependency.LEFT);
                current.setColor(Color.YELLOW);
                current.setCircleColor(Color.YELLOW);
                current.setCircleRadius(1.5f);
                current.setLineWidth(1f);

                power = new LineDataSet(entriesP, "Power"); // add entries to dataset
                power.setAxisDependency(YAxis.AxisDependency.LEFT);
                power.setColor(Color.GREEN);
                power.setCircleColor(Color.GREEN);
                power.setCircleRadius(1.5f);
                power.setLineWidth(1f);

                for (int k = 0; k < todayVolts.size(); k++) {
                    voltage.addEntry(new Entry(k, Float.parseFloat(todayVolts.get(k))));
                    current.addEntry(new Entry(k, Float.parseFloat(todayCurrents.get(k))));
                    power.addEntry(new Entry(k, powerData.get(k)));
                }

                dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(voltage);
                dataSets.add(current);
                dataSets.add(power);

                data = new LineData(dataSets);

                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.setScaleYEnabled(false);
                lineChart.setData(data);
                lineChart.invalidate();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        cursor.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();

        if (item.equals("Today")) {
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

            // set the calendar to start of today
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            long today = c.getTimeInMillis();

            ArrayList<String> todayDates = new ArrayList<>();
            ArrayList<String> todayCurrents = new ArrayList<>();
            ArrayList<String> todayVolts = new ArrayList<>();
            ArrayList<Float> todayPower = new ArrayList<>();
            List<String> date = new ArrayList<String>();

            for (int j = 0; j < dates.size(); j++) {
                long gotTime = Long.parseLong(dates.get(j)) * 1000;
                if (gotTime > today) {
                    todayDates.add(dates.get(j));
                    todayVolts.add(volts.get(j));
                    todayCurrents.add(currents.get(j));
                    todayPower.add(Float.parseFloat(volts.get(j)) * Float.parseFloat(currents.get(j)));
                }
            }

            for (int z = 0; z < todayDates.size(); z++) {
                Date d = new Date(Long.parseLong(todayDates.get(z)) * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                String dates = sdf.format(d);

                date.add(dates);
            }

            arr = date.toArray(new String[date.size()]);

            xAxis = lineChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(arr));

            entriesC = new ArrayList<>();
            entriesV = new ArrayList<>();
            entriesP = new ArrayList<>();

            voltage = new LineDataSet(entriesV, "Voltage"); // add entries to dataset
            voltage.setAxisDependency(YAxis.AxisDependency.LEFT);
            voltage.setColor(Color.RED);
            voltage.setCircleColor(Color.RED);
            voltage.setCircleRadius(1.5f);
            voltage.setLineWidth(1f);

            current = new LineDataSet(entriesC, "Current"); // add entries to dataset
            current.setAxisDependency(YAxis.AxisDependency.LEFT);
            current.setColor(Color.YELLOW);
            current.setCircleColor(Color.YELLOW);
            current.setCircleRadius(1.5f);
            current.setLineWidth(1f);

            power = new LineDataSet(entriesP, "Power"); // add entries to dataset
            power.setAxisDependency(YAxis.AxisDependency.LEFT);
            power.setColor(Color.GREEN);
            power.setCircleColor(Color.GREEN);
            power.setCircleRadius(1.5f);
            power.setLineWidth(1f);

            for (int k = 0; k < todayVolts.size(); k++) {
                voltage.addEntry(new Entry(k, Float.parseFloat(todayVolts.get(k))));
                current.addEntry(new Entry(k, Float.parseFloat(todayCurrents.get(k))));
                power.addEntry(new Entry(k, todayPower.get(k)));
            }

            dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(voltage);
            dataSets.add(current);
            dataSets.add(power);

            data = new LineData(dataSets);

            lineChart.setDoubleTapToZoomEnabled(false);
            lineChart.setScaleYEnabled(false);
            lineChart.setData(data);
            lineChart.invalidate();


        } else if (item.equals("Yesterday")) {

            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

            // set the calendar to start of today
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            long today = c.getTimeInMillis();
//                System.out.println(today);

            Calendar t = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

            // set the calendar to start of today
            t.add(Calendar.DATE, -1);
            t.set(Calendar.HOUR_OF_DAY, 0);
            t.set(Calendar.MINUTE, 0);
            t.set(Calendar.SECOND, 0);
            t.set(Calendar.MILLISECOND, 0);

            long yesterday = t.getTimeInMillis();

            System.out.println(yesterday);
            System.out.println(today);

            ArrayList<String> yesterdayDates = new ArrayList<>();
            ArrayList<String> yesterdayCurrents = new ArrayList<>();
            ArrayList<String> yesterdayVolts = new ArrayList<>();
            ArrayList<Float> yesterdayPower = new ArrayList<>();
            List<String> date = new ArrayList<String>();

            for (int j = 0; j < dates.size(); j++) {
                long gotTime = Long.parseLong(dates.get(j)) * 1000;
                if (gotTime > yesterday && gotTime < today) {
                    yesterdayDates.add(dates.get(j));
                    yesterdayVolts.add(volts.get(j));
                    yesterdayCurrents.add(currents.get(j));
                    yesterdayPower.add(Float.parseFloat(volts.get(j)) * Float.parseFloat(currents.get(j)));
                }
            }

            if (yesterdayDates.size() < 1) {
                arr = date.toArray(new String[date.size()]);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(arr));

                entriesC = new ArrayList<>();
                entriesV = new ArrayList<>();

                entriesV.add(0, new Entry(0, 0));
                entriesC.add(0, new Entry(0, 0));

                voltage = new LineDataSet(entriesV, "Voltage"); // add entries to dataset
                voltage.setAxisDependency(YAxis.AxisDependency.LEFT);
                voltage.setColor(Color.RED);
                voltage.setCircleColor(Color.RED);
                voltage.setCircleRadius(1.5f);
                voltage.setLineWidth(1f);

                current = new LineDataSet(entriesC, "Current"); // add entries to dataset
                current.setAxisDependency(YAxis.AxisDependency.LEFT);
                current.setColor(Color.YELLOW);
                current.setCircleColor(Color.YELLOW);
                current.setCircleRadius(1.5f);
                current.setLineWidth(1f);

                dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(voltage);
                dataSets.add(current);

                data = new LineData(dataSets);

                lineChart.setData(data);
                lineChart.invalidate();
            } else {

                for (int z = 0; z < yesterdayDates.size(); z++) {
                    Date d = new Date(Long.parseLong(yesterdayDates.get(z)) * 1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.ENGLISH);
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String dates = sdf.format(d);

                    date.add(dates);
                }

                arr = date.toArray(new String[date.size()]);

                xAxis = lineChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(arr));

                entriesC = new ArrayList<>();
                entriesV = new ArrayList<>();
                entriesP = new ArrayList<>();

                voltage = new LineDataSet(entriesV, "Voltage"); // add entries to dataset
                voltage.setAxisDependency(YAxis.AxisDependency.LEFT);
                voltage.setColor(Color.RED);
                voltage.setCircleColor(Color.RED);
                voltage.setCircleRadius(1.5f);
                voltage.setLineWidth(1f);

                current = new LineDataSet(entriesC, "Current"); // add entries to dataset
                current.setAxisDependency(YAxis.AxisDependency.LEFT);
                current.setColor(Color.YELLOW);
                current.setCircleColor(Color.YELLOW);
                current.setCircleRadius(1.5f);
                current.setLineWidth(1f);

                power = new LineDataSet(entriesP, "Power"); // add entries to dataset
                power.setAxisDependency(YAxis.AxisDependency.LEFT);
                power.setColor(Color.GREEN);
                power.setCircleColor(Color.GREEN);
                power.setCircleRadius(1.5f);
                power.setLineWidth(1f);

                for (int k = 0; k < yesterdayVolts.size(); k++) {
                    voltage.addEntry(new Entry(k, Float.parseFloat(yesterdayVolts.get(k))));
                    current.addEntry(new Entry(k, Float.parseFloat(yesterdayCurrents.get(k))));
                    power.addEntry(new Entry(k, yesterdayPower.get(k)));
                }

                dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(voltage);
                dataSets.add(current);
                dataSets.add(power);

                data = new LineData(dataSets);

                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.setScaleYEnabled(false);
                lineChart.setData(data);
                lineChart.invalidate();
            }
        } else if (item.equals("Past week")) {

            ArrayList<Float> powerData = new ArrayList<>();

            List<String> date = new ArrayList<String>();

            cursor = database.query(DBHelper.TABLE_ENERGY, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                dates = new ArrayList<>();
                volts = new ArrayList<>();
                currents = new ArrayList<>();
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
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

            for (int k = 0; k < dates.size(); k++) {
                Date d = new Date(Long.parseLong(dates.get(k)) * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dates = sdf.format(d);

                date.add(dates);

                powerData.add(Float.parseFloat(volts.get(k)) * Float.parseFloat(currents.get(k)));
            }

            arr = date.toArray(new String[date.size()]);

            xAxis = lineChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(arr));

            entriesC = new ArrayList<>();
            entriesV = new ArrayList<>();
            entriesP = new ArrayList<>();

            voltage = new LineDataSet(entriesV, "Voltage"); // add entries to dataset
            voltage.setAxisDependency(YAxis.AxisDependency.LEFT);
            voltage.setColor(Color.RED);
            voltage.setCircleColor(Color.RED);
            voltage.setCircleRadius(1.5f);
            voltage.setLineWidth(1f);

            current = new LineDataSet(entriesC, "Current"); // add entries to dataset
            current.setAxisDependency(YAxis.AxisDependency.LEFT);
            current.setColor(Color.YELLOW);
            current.setCircleColor(Color.YELLOW);
            current.setCircleRadius(1.5f);
            current.setLineWidth(1f);

            power = new LineDataSet(entriesP, "Power"); // add entries to dataset
            power.setAxisDependency(YAxis.AxisDependency.LEFT);
            power.setColor(Color.GREEN);
            power.setCircleColor(Color.GREEN);
            power.setCircleRadius(1.5f);
            power.setLineWidth(1f);

            for (int k = 0; k < volts.size(); k++) {
                voltage.addEntry(new Entry(k, Float.parseFloat(volts.get(k))));
                current.addEntry(new Entry(k, Float.parseFloat(currents.get(k))));
                power.addEntry(new Entry(k, powerData.get(k)));
            }

            dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(voltage);
            dataSets.add(current);
            dataSets.add(power);

            data = new LineData(dataSets);

            lineChart.setDoubleTapToZoomEnabled(false);
            lineChart.setScaleYEnabled(false);
            lineChart.setData(data);
            lineChart.invalidate();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
