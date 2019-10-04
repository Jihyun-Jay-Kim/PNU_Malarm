package com.my.malarm;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import static com.my.malarm.AlarmReceiver.mediaPlayer;

/**
 * Created by kjh on 2018. 4. 20..
 */

public class ShakeListener extends Activity implements SensorEventListener {
    private long lastTime;
    private float speed;
    private float x, y, z;
    private float lastX, lastY, lastZ;
    private int count;

    private static final int SHAKE_THRESHOLD = 100;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);

            if (gabOfTime > 100) {
                lastTime = currentTime;

                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];
                speed = Math.abs(x + y + z - lastX - lastY - lastZ) /
                        gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    // 이벤트 발생!!
                    count++;
                    if (count > 6) {
                        count = 0;
                        mediaPlayer.stop();

                        mediaPlayer.release();
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

}