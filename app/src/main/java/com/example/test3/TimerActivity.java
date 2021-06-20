<<<<<<< HEAD
package com.example.test3;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.SystemClock;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.test3.A;

public class TimerActivity extends AppCompatActivity {

    private static final int ARG_DISTANCE = 100;
    private static final String TIME_STUDY = "time_study";
    private static final String TIME_BED = "time_bed";
    private static final String TIME_OUT = "time_out";
    private static final String START_STUDY = "start_study";
    private static final String START_BED = "start_bed";
    private static final String START_OUT = "start_out";
    private static final String STATUS_LATEST = "status_latest";

    private TextView mStatus;
    private TextView mStudy;
    private TextView mBed;
    private TextView mOut;

    private int distance;
    private int[] pressure;
    private int[] weight;

    private int new_status;
    private int old_status;

    private Handler handler;
    private Runnable runnable;
    private final int delay = 1000;

    public Long time_study=0L;
    public Long time_bed=0L;
    public Long time_out=0L;
    public Long start_study;
    public Long start_bed;
    public Long start_out;
    public Long current;
    public Long past = 0L;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStatus = (TextView) findViewById(R.id.section_label_status);
        mStudy = (TextView) findViewById(R.id.timer_study);
        mBed = (TextView) findViewById(R.id.timer_bed);
        mOut = (TextView) findViewById(R.id.timer_out);

        // Restore stored values
        SharedPreferences sf = getSharedPreferences("sFile2", MODE_PRIVATE);
        // check the value of the key. If there is no value, return 0
        old_status = sf.getInt("STATUS_LATEST", 0);

        switch(old_status) {
            case 0:
                start_study = sf.getLong("START_STUDY", SystemClock.elapsedRealtime());
                time_study = sf.getLong("TIME_STUDY", 0L);
            case 1:
                start_bed = sf.getLong("START_BED", SystemClock.elapsedRealtime());
                time_bed = sf.getLong("TIME_BED", 0L);
            default:
                start_out = sf.getLong("START_OUT", SystemClock.elapsedRealtime());
                time_out = sf.getLong("TIME_OUT", 0L);
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                recvData();
                showData();
                handler.postDelayed(runnable, delay);
            }
        };
        runnable.run();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void recvData() {
        /*final Chronometer chronometer_study = (Chronometer) findViewById(R.id.chronometer_study);
        final Chronometer chronometer_bed = (Chronometer) findViewById(R.id.chronometer_bed);
        final Chronometer chronometer_out = (Chronometer) findViewById(R.id.chronometer_out);*/
        /*ArrayList<Integer> values = new ArrayList<>(Arrays.asList(127, 0, 0, 0, 1, 250, 100));
        distance = values.get(0);
        pressure = new int[]{values.get(1), values.get(2), values.get(3), values.get(4)};
        weight = new int[]{values.get(5), values.get(6)};*/
        List<int[]> values = A.getInstance().getN(1);
        Log.i("msgtimer_d", Arrays.toString(values.get(0)));
        distance = values.get(0)[0];
        pressure = new int[]{values.get(0)[1], values.get(0)[2], values.get(0)[3], values.get(0)[4]};
        //weight = new int[]{values.get(0)[5], values.get(0)[6]};
        new_status = 2; // out
        Log.i("msgtimer", String.valueOf(new_status));
        for (int i = 0; i < pressure.length; i++) {
            if (pressure[i] != 0) {
                new_status = 0; // studying
                break;
            }
        }
        Log.i("msgtimer2", String.valueOf(new_status));
        Log.i("msgtimerdistance", String.valueOf(distance));
        if (new_status != 0 && distance < ARG_DISTANCE) { //{ && new_status!=0) {
            new_status = 1; // bed
        }
        Log.i("msgtimer3", String.valueOf(new_status));

        switch (new_status)
        {
            case 0:
                time_study += delay;
                break;
            case 1:
                time_bed += delay;
                break;
            case 2:
                time_out += delay;
                break;
        }
        past = SystemClock.elapsedRealtime();
    }

    private void showData() {
        int time = (int) (time_study / 1000);
        int hour = time / (60 * 60);
        int min = time % (60 * 60) / 60;
        int sec = time % 60;
        mStudy.setText(hour + "시간 " + min + "분 " + sec + "초");
        time = (int) (time_bed / 1000);
        hour = time / (60 * 60);
        min = time % (60 * 60) / 60;
        sec = time % 60;
        mBed.setText(hour + "시간 " + min + "분 " + sec + "초");
        time = (int) (time_out / 1000);
        hour = time / (60 * 60);
        min = time % (60 * 60) / 60;
        sec = time % 60;
        mOut.setText(hour + "시간 " + min + "분 " + sec + "초");
        switch (new_status) {
            case 0:
                mStatus.setText("Status: Studying");
                break;
            case 1:
                mStatus.setText("Status: Staying in Bed");
                break;
            default:
                mStatus.setText("Status: Outside of Room");
                break;
        }
    }

    @Override
    protected void onStop() { // before TimerActivity is terminated
        super.onStop();

        // set SharedPreferences with sFile(file name) and default mode
        SharedPreferences sf = getSharedPreferences("sFile2", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        current = SystemClock.elapsedRealtime();
        Long time = current-past;
        editor.putInt("STATUS_LATEST", new_status);
        switch(new_status) {
            case 0:
                time_study += time;
                editor.putLong("START_STUDY", start_study);
                editor.putLong("TIME_STUDY", time_study);
                break;
            case 1:
                time_bed += time;
                editor.putLong("START_BED", start_bed);
                editor.putLong("TIME_BED", time_bed);
                break;
            default:
                time_out += time;
                editor.putLong("START_OUT", start_out);
                editor.putLong("TIME_OUT", time_out);
                break;
        }

        editor.commit();
        handler.removeCallbacks(runnable);
    }

=======
package com.example.test3;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.SystemClock;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.test3.A;

public class TimerActivity extends AppCompatActivity {

    private static final int ARG_DISTANCE = 100;
    private static final String TIME_STUDY = "time_study";
    private static final String TIME_BED = "time_bed";
    private static final String TIME_OUT = "time_out";
    private static final String START_STUDY = "start_study";
    private static final String START_BED = "start_bed";
    private static final String START_OUT = "start_out";
    private static final String STATUS_LATEST = "status_latest";

    private TextView mStatus;
    private TextView mStudy;
    private TextView mBed;
    private TextView mOut;

    private int distance;
    private int[] pressure;
    private int[] weight;

    private int new_status;
    private int old_status;

    private Handler handler;
    private Runnable runnable;
    private final int delay = 1000;

    public Long time_study=0L;
    public Long time_bed=0L;
    public Long time_out=0L;
    public Long start_study;
    public Long start_bed;
    public Long start_out;
    public Long current;
    public Long past = 0L;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStatus = (TextView) findViewById(R.id.section_label_status);
        mStudy = (TextView) findViewById(R.id.timer_study);
        mBed = (TextView) findViewById(R.id.timer_bed);
        mOut = (TextView) findViewById(R.id.timer_out);

        // Restore stored values
        SharedPreferences sf = getSharedPreferences("sFile2", MODE_PRIVATE);
        // check the value of the key. If there is no value, return 0
        old_status = sf.getInt("STATUS_LATEST", 0);

        switch(old_status) {
            case 0:
                start_study = sf.getLong("START_STUDY", SystemClock.elapsedRealtime());
                time_study = sf.getLong("TIME_STUDY", 0L);
            case 1:
                start_bed = sf.getLong("START_BED", SystemClock.elapsedRealtime());
                time_bed = sf.getLong("TIME_BED", 0L);
            default:
                start_out = sf.getLong("START_OUT", SystemClock.elapsedRealtime());
                time_out = sf.getLong("TIME_OUT", 0L);
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                recvData();
                showData();
                handler.postDelayed(runnable, delay);
            }
        };
        runnable.run();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void recvData() {
        /*final Chronometer chronometer_study = (Chronometer) findViewById(R.id.chronometer_study);
        final Chronometer chronometer_bed = (Chronometer) findViewById(R.id.chronometer_bed);
        final Chronometer chronometer_out = (Chronometer) findViewById(R.id.chronometer_out);*/
        /*ArrayList<Integer> values = new ArrayList<>(Arrays.asList(127, 0, 0, 0, 1, 250, 100));
        distance = values.get(0);
        pressure = new int[]{values.get(1), values.get(2), values.get(3), values.get(4)};
        weight = new int[]{values.get(5), values.get(6)};*/
        List<int[]> values = A.getInstance().getN(1);
        Log.i("msgtimer_d", Arrays.toString(values.get(0)));
        distance = values.get(0)[0];
        pressure = new int[]{values.get(0)[1], values.get(0)[2], values.get(0)[3], values.get(0)[4]};
        //weight = new int[]{values.get(0)[5], values.get(0)[6]};
        new_status = 2; // out
        Log.i("msgtimer", String.valueOf(new_status));
        for (int i = 0; i < pressure.length; i++) {
            if (pressure[i] != 0) {
                new_status = 0; // studying
                break;
            }
        }
        Log.i("msgtimer2", String.valueOf(new_status));
        Log.i("msgtimerdistance", String.valueOf(distance));
        if (new_status != 0 && distance < ARG_DISTANCE) { //{ && new_status!=0) {
            new_status = 1; // bed
        }
        Log.i("msgtimer3", String.valueOf(new_status));

        switch (new_status)
        {
            case 0:
                time_study += delay;
                break;
            case 1:
                time_bed += delay;
                break;
            case 2:
                time_out += delay;
                break;
        }
        past = SystemClock.elapsedRealtime();
    }

    private void showData() {
        int time = (int) (time_study / 1000);
        int hour = time / (60 * 60);
        int min = time % (60 * 60) / 60;
        int sec = time % 60;
        mStudy.setText(hour + "시간 " + min + "분 " + sec + "초");
        time = (int) (time_bed / 1000);
        hour = time / (60 * 60);
        min = time % (60 * 60) / 60;
        sec = time % 60;
        mBed.setText(hour + "시간 " + min + "분 " + sec + "초");
        time = (int) (time_out / 1000);
        hour = time / (60 * 60);
        min = time % (60 * 60) / 60;
        sec = time % 60;
        mOut.setText(hour + "시간 " + min + "분 " + sec + "초");
        switch (new_status) {
            case 0:
                mStatus.setText("Status: Studying");
                break;
            case 1:
                mStatus.setText("Status: Staying in Bed");
                break;
            default:
                mStatus.setText("Status: Outside of Room");
                break;
        }
    }

    @Override
    protected void onStop() { // before TimerActivity is terminated
        super.onStop();

        // set SharedPreferences with sFile(file name) and default mode
        SharedPreferences sf = getSharedPreferences("sFile2", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        current = SystemClock.elapsedRealtime();
        Long time = current-past;
        editor.putInt("STATUS_LATEST", new_status);
        switch(new_status) {
            case 0:
                time_study += time;
                editor.putLong("START_STUDY", start_study);
                editor.putLong("TIME_STUDY", time_study);
                break;
            case 1:
                time_bed += time;
                editor.putLong("START_BED", start_bed);
                editor.putLong("TIME_BED", time_bed);
                break;
            default:
                time_out += time;
                editor.putLong("START_OUT", start_out);
                editor.putLong("TIME_OUT", time_out);
                break;
        }

        editor.commit();
        handler.removeCallbacks(runnable);
    }

>>>>>>> 32196bc (test)
}