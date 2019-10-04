package com.my.malarm;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmOn extends Activity {

    public TextView mCurTimetextView;
    public Timer mTimer;

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on);
        Button btn1 = (Button)findViewById(R.id.solve_btn);
        mCurTimetextView = (TextView)findViewById(R.id.CurrentTimeTextView);
        MainTimerTask timerTask = new MainTimerTask();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 500, 1000);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), Test.class);
                startActivity(in);
                finish();
            }
        });


    }
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            Date rightNow = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
            String dateString = formatter.format(rightNow);
            mCurTimetextView.setText(dateString);
        }
    };
    @Override
    protected void onDestroy()
    {
        mTimer.cancel();
        super.onDestroy();
    }
    @Override
    protected void onPause()
    {
        mTimer.cancel();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        MainTimerTask timerTask = new MainTimerTask();
        mTimer.schedule(timerTask, 500, 3000);
        super.onResume();
    }

    class MainTimerTask extends TimerTask {
        public void run(){
            mHandler.post(mUpdateTimeTask);
        }
    }

}