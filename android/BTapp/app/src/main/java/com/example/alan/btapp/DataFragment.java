package com.example.alan.btapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static com.example.alan.btapp.MainFragment.current;
import static com.example.alan.btapp.MainFragment.voltage;
import static com.example.alan.btapp.StartActivity.mConnectedThread;
import static com.example.alan.btapp.StartActivity.mHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {
    public static String[] graphData;

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;

    private ScheduledExecutorService exec;
    private Button button;
    private LineChart lineChart;
    private List<Entry> entriesV = new ArrayList<Entry>();
    private List<Entry> entriesC = new ArrayList<Entry>();
    private LineDataSet voltage;
    private LineDataSet current;
    private List<ILineDataSet> dataSets;
    private LineData data;
    private List<String> date;
    private String[] arr;
    private Handler handler;

    private StringBuilder recDataString = new StringBuilder();

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
        lineChart = (LineChart) view.findViewById(R.id.chart);
        button = (Button) view.findViewById(R.id.syncButton);

        date = new ArrayList<String>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mConnectedThread.write("#C~");
                for (int i = 0; i < graphData.length - 1; i++) {
                    String[] dataSet = graphData[i].split("\\+");
                    entriesV.add(new Entry(i, Float.parseFloat(dataSet[2])));
                    entriesC.add(new Entry(i, Float.parseFloat(dataSet[1])));
                }

                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        });
        if (graphData != null) {
            for (int i = 0; i < graphData.length; i++) {
                String[] arr = graphData[i].split("\\+");
                arr[0] = arr[0].replaceAll("[\\n\\r]", "");
                arr[0] = arr[0].trim();
                if (arr[0].equals("~")) break;
                Date d = new Date(Long.parseLong(arr[0]) * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dates = sdf.format(d);

                date.add(dates);
            }
        }



        arr = date.toArray(new String[date.size()]);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(arr));

        entriesV.add(new Entry(0, 0));
        entriesC.add(new Entry(0, 0));

        voltage = new LineDataSet(entriesV, "Voltage"); // add entries to dataset
        voltage.setAxisDependency(YAxis.AxisDependency.LEFT);
        voltage.setColor(Color.RED);
        voltage.setCircleColor(Color.RED);
        voltage.setCircleRadius(1f);
        voltage.setLineWidth(2f);

        current = new LineDataSet(entriesC, "Current"); // add entries to dataset
        current.setAxisDependency(YAxis.AxisDependency.LEFT);
        current.setColor(Color.YELLOW);
        current.setCircleColor(Color.YELLOW);
        current.setCircleRadius(1f);
        current.setLineWidth(2f);

        dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(voltage);
        dataSets.add(current);

        data = new

                LineData(dataSets);

        lineChart.setData(data);
        lineChart.invalidate();
    }

    private class xyz extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please Wait...");
            this.dialog.show();
            // put your code which preload with processDialog
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            // put your code here
            mConnectedThread.write("#D~");
            while (mHandler.hasMessages(2)) {
                if (!mHandler.hasMessages(2)) {




                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
    }

}
