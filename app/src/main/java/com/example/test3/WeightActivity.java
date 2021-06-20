package com.example.test3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import com.example.test3.A;

public class WeightActivity extends AppCompatActivity {

    private static final String START_DEV1 = "start_dev1";
    private static final String START_DEV2 = "start_dev2";
    private static final String END_DEV1 = "end_dev1";
    private static final String END_DEV2 = "end_dev2";
    private static final String TIME_DEV1 = "time_dev1";
    private static final String TIME_DEV2 = "time_dev2";

    private TextView mDev1;
    private TextView mDev2;
    private TextView mDev1_time;
    private TextView mDev2_time;
    public int status_dev1;
    //public int status_dev2;

    private int[] weight;

    private Handler handler;
    private Runnable runnable;
    private final int delay = 1000;

    public Long start_dev1;
    public Long start_dev2;
    public Long time_dev1;
    //public Long time_dev2;
    public Long current;
    public Long past;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        mDev1 = (TextView) findViewById(R.id.weight_dev1_status);
        //mDev2 = (TextView) findViewById(R.id.weight_dev2_status);
        mDev1_time = (TextView) findViewById(R.id.weight_dev1_time);
        //mDev2_time = (TextView) findViewById(R.id.weight_dev2_time);

        restoreData();

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

    protected void restoreData() {
        // Restore stored values
        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        // check the value of the key. If there is no value, return 0
        time_dev1 = sf.getLong("TIME_DEV1", 0);
        //time_dev2 = sf.getLong("TIME_DEV2", 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void recvData() {
        past = SystemClock.elapsedRealtime();
        List<int[]> values = A.getInstance().getN(1);
        //weight = new int[]{values.get(0)[5], values.get(0)[6]};
        status_dev1 = values.get(0)[5];
        //status_dev2 = values.get(0)[5];
        if (status_dev1 == 0) {
            time_dev1 += delay;
        }
    }

    protected void showData() {
        Log.i("msgdev", String.valueOf(status_dev1));
        if (status_dev1 == 1) {
            mDev1.setText("사용 X");

        } else if (status_dev1 == 0) {
            mDev1.setText("사용 O");
        }

        /*if (status_dev2 == 1) {
            mDev2.setText("사용 O");
        } else if (status_dev2 == 0) {
            mDev2.setText("사용 X");
        }*/
        int time = (int) (time_dev1 / 1000);
        int hour = time / (60 * 60);
        int min = time % (60 * 60) / 60;
        int sec = time % 60;
        mDev1_time.setText(hour + "시간 " + min + "분 " + sec + "초");
        /*time = (int) (time_dev2 / 1000);
        hour = time / (60 * 60);
        min = time % (60 * 60) / 60;
        sec = time % 60;
        mDev2_time.setText(hour + "시간 " + min + "분 " + sec + "초");*/
    }
    @Override
    protected void onStop() { // before TimerActivity is terminated
        super.onStop();

        // set SharedPreferences with sFile(file name) and default mode
        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        current = SystemClock.elapsedRealtime();
        Long time = current-past;

        if(status_dev1==0) {
            time_dev1 += time;
            editor.putLong("TIME_DEV1", time_dev1);
        }

        /*if(status_dev2==1) {
            time_dev2 += time;
            editor.putLong("TIME_DEV2", time_dev2);
        }*/

        editor.commit();
        handler.removeCallbacks(runnable);
    }
}