package com.example.test3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.widget.Chronometer;

import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TimerActivity extends AppCompatActivity {

    private static final int ARG_DISTANCE = 100;
    private TextView mStatus;
    private TextView mStudy;
    private TextView mBed;
    private TextView mOUt;
    private TextView time, recordView;
    private int distance;
    private int[] pressure;
    private int[] weight;

    private int status = 2;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    private int minute = 0;
    private String record = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStatus = (TextView) findViewById(R.id.section_label_status);
        mStudy = (TextView) findViewById(R.id.timer_study);
        mBed = (TextView) findViewById(R.id.timer_bed);
        mOUt = (TextView) findViewById(R.id.timer_out);

        if(savedInstanceState != null) {
            String old = savedInstanceState.getString("count");
            mStatus.setText("old" + old);
        } else {
            mStatus.setText("new" + DateFormat.getDateTimeInstance().format(new Date()));
        }
        showData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("count", DateFormat.getDateTimeInstance().format(new Date()));
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        String old = savedInstanceState.getString("count");
        mStatus.setText("old2" + old);
    }
    private void showData() {
        final Chronometer chronometer_study = (Chronometer) findViewById(R.id.chronometer_study);
        final Chronometer chronometer_bed = (Chronometer) findViewById(R.id.chronometer_bed);
        final Chronometer chronometer_out = (Chronometer) findViewById(R.id.chronometer_out);
        ArrayList<Integer> values = new ArrayList<>(Arrays.asList(127, 0, 0, 0, 0, 250, 100));
        // ArrayList<Integer> values = getN(1);
        distance = values.get(0);
        pressure = new int[]{values.get(1), values.get(2), values.get(3), values.get(4)};
        weight = new int[]{values.get(5), values.get(6)};

        for (int i = 0; i < pressure.length; i++) {
            if (pressure[i] != 0) {
                status = 0; // studying
                break;
            }
        }
        if (distance < ARG_DISTANCE) {
            status = 1; // bed
        }

        Intent intent = getIntent();
        String data = intent.getStringExtra("timer_study");
        if (status == 0) {
            mStatus.setText("Status: Studying");
            //timeThread = new Thread(new timeThread());
            //timeThread.start();
            chronometer_study.start();
            chronometer_bed.stop();
            chronometer_out.stop();
        } else if (status == 1){
            mStatus.setText("Status: Staying in Bed");
            //timeThread.interrupt();
            chronometer_study.stop();
            chronometer_bed.start();
            chronometer_out.stop();
        } else {
            mStatus.setText("Status: Outside of Room");
            /*timeThread = new Thread(new timeThread());
            timeThread.start();*/
            chronometer_study.stop();
            chronometer_bed.stop();
            chronometer_out.start();
        }
    }
    /*@SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mSec = msg.arg1 % 100;
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            int hour = (msg.arg1 / 100) / 360;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
            if (result.equals("00:01:15:00")) {
                Toast.makeText(MainActivity.this, "1분 15초가 지났습니다.", Toast.LENGTH_SHORT).show();
            }
            mTimeTextView.setText(result);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                mTimeTextView.setText("");
                                mTimeTextView.setText("00:00:00:00");
                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }*/

}
