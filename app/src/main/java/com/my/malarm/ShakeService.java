package com.my.malarm;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import static com.my.malarm.AlarmReceiver.alarming_lv1;
import static com.my.malarm.AlarmReceiver.alarming_lv2;
import static com.my.malarm.AlarmReceiver.alarming_lv3;
import static com.my.malarm.AlarmReceiver.m;
import static com.my.malarm.AlarmReceiver.alarming;
import static com.my.malarm.AlarmReceiver.mLv1;
import static com.my.malarm.AlarmReceiver.mLv2;
import static com.my.malarm.AlarmReceiver.mLv3;
import static com.my.malarm.AlarmReceiver.mediaPlayer;
import static com.my.malarm.AlarmReceiver.sol_difficulty;
import static com.my.malarm.AlarmReceiver.vibe;
import static com.my.malarm.AlarmReceiver.vibrating;
import static com.my.malarm.AlarmReceiver.voice_Key;
import static com.my.malarm.ShakeActivity.contextSA;
import static com.my.malarm.AlarmReceiver.shake_count;
import static com.my.malarm.AlarmReceiver.kLv1;
import static com.my.malarm.AlarmReceiver.kLv2;
import static com.my.malarm.AlarmReceiver.kLv3;

public class ShakeService extends Service implements SensorEventListener{
    private long lastTime;
    private float speed;
    private float x, y, z;
    private float lastX, lastY, lastZ;
    private int count;

    private static final int SHAKE_THRESHOLD = 70;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;


    public ShakeService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onStart(Intent intent, int startID) {
        super.onStart(intent, startID);



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        contextSA = (ShakeActivity)ShakeActivity.contextSA;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);

            if (gabOfTime > 500) {

                lastTime = currentTime;

                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];
                speed = Math.abs(x + y + z - lastX - lastY - lastZ) /
                        gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD && alarming) {
                    count++;
                    if (count > shake_count) {
                        Toast.makeText(ShakeService.this, "Finish!",
                                Toast.LENGTH_SHORT).show();
                        count = 0;
                        if(alarming_lv1){
                            alarming_lv1 = false;
                            if(!alarming_lv2){
                                alarming = false;
                            }
                        }
                        else if(alarming_lv2){
                            alarming_lv2 = false;
                            if(!alarming_lv3){
                                alarming = false;
                            }
                        }
                        else if(alarming_lv3){
                            alarming_lv3  = false;
                            alarming = false;
                        }


                        if(alarming == false){
                            if(vibrating){
                                vibe.cancel();
                            }
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            if(contextSA!=null){
                                contextSA.finish();
                            }
                            Intent in = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(in);
                        }
                        else{
                            showNextActivity();
                            if(contextSA!=null){
                                contextSA.finish();
                            }
                        }
                    }
                }
                lastX = event.values[SensorManager.DATA_X];
                lastY = event.values[SensorManager.DATA_Y];
                lastZ = event.values[SensorManager.DATA_Z];
            }
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 반드시 제 정의가 필요한 메서드

    }

    public void showNextActivity(){


        if(mLv1==0 && alarming_lv1){
            shake_count = Integer.parseInt(kLv1);
            Intent in = new Intent(getBaseContext(), ShakeActivity.class);
            startActivity(in);
        }

        else if(mLv1==1 && alarming_lv1){
            voice_Key = kLv1;
            Intent in = new Intent(getBaseContext(), VoiceActivity.class);
            startActivity(in);
        }
        else if(mLv1==2 && alarming_lv1){
            sol_difficulty = kLv1;
            Intent in = new Intent(getBaseContext(), MPActivity.class);
            startActivity(in);
        }
        else if(mLv2==1 && alarming_lv2){
            shake_count = Integer.parseInt(kLv2);
            Intent in = new Intent(getBaseContext(), ShakeActivity.class);
            startActivity(in);
        }
        else if(mLv2==2 && alarming_lv2){
            voice_Key = kLv2;
            Intent in = new Intent(getBaseContext(), VoiceActivity.class);
            startActivity(in);
        }
        else if(mLv2==3 && alarming_lv2){
            sol_difficulty = kLv2;
            Intent in = new Intent(getBaseContext(), MPActivity.class);
            startActivity(in);
        }
        else if(mLv3==1 && alarming_lv3){
            shake_count = Integer.parseInt(kLv3);
            Intent in = new Intent(getBaseContext(), ShakeActivity.class);
            startActivity(in);
        }
        else if(mLv3==2 && alarming_lv3){
            voice_Key = kLv3;
            Intent in = new Intent(getBaseContext(), VoiceActivity.class);
            startActivity(in);
        }
        else if(mLv3==3 && alarming_lv3){
            sol_difficulty = kLv3;
            Intent in = new Intent(getBaseContext(), MPActivity.class);
            startActivity(in);
        }
    }


}
