package com.my.malarm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static com.my.malarm.AlarmReceiver.alarming;
import static com.my.malarm.AlarmReceiver.alarming_lv1;
import static com.my.malarm.AlarmReceiver.alarming_lv2;
import static com.my.malarm.AlarmReceiver.alarming_lv3;
import static com.my.malarm.AlarmReceiver.m;
import static com.my.malarm.AlarmReceiver.shake_count;

/**
 * Created by kjh on 2018. 5. 25..
 */

public class ShakeActivity extends Activity {
    Intent shakeIntent;

    public static Activity contextSA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        contextSA = (ShakeActivity)ShakeActivity.this;


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView txt = new TextView(this);
        txt.setText("\n");
        txt.setTextSize(18);
        layout.addView(txt);
        Button input = new Button(this);
        shakeIntent = new Intent(this, ShakeService.class);
        input.setText("흔들어 끄기 (" + shake_count+"번)");
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShakeActivity.this, "let's shake.",
                        Toast.LENGTH_SHORT).show();
                startService(shakeIntent);
            }
        });
        layout.addView(input);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);

    }
    @Override
    public void onBackPressed() {

    }
}
