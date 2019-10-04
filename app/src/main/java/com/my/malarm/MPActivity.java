package com.my.malarm;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static com.my.malarm.AlarmReceiver.alarming;
import static com.my.malarm.AlarmReceiver.alarming_lv1;
import static com.my.malarm.AlarmReceiver.alarming_lv2;
import static com.my.malarm.AlarmReceiver.alarming_lv3;
import static com.my.malarm.AlarmReceiver.kLv1;
import static com.my.malarm.AlarmReceiver.kLv2;
import static com.my.malarm.AlarmReceiver.kLv3;
import static com.my.malarm.AlarmReceiver.m;
import static com.my.malarm.AlarmReceiver.mLv1;
import static com.my.malarm.AlarmReceiver.mLv2;
import static com.my.malarm.AlarmReceiver.mLv3;
import static com.my.malarm.AlarmReceiver.mediaPlayer;
import static com.my.malarm.AlarmReceiver.shake_count;
import static com.my.malarm.AlarmReceiver.sol_difficulty;
import static com.my.malarm.AlarmReceiver.vibe;
import static com.my.malarm.AlarmReceiver.vibrating;
import static com.my.malarm.AlarmReceiver.voice_Key;

/**
 * Created by kjh on 2018. 5. 25..
 */

public class MPActivity extends Activity {
    EditText et;
    private String oper;
    private int result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView txt = new TextView(this);

        txt.setText("\n");
        txt.setTextSize(18);
        layout.addView(txt);
        Random randomGenerator = new Random();
        int randomInteger_1 = 0;
        int randomInteger_2 = 0;

        if(sol_difficulty.equals("0")){
            randomInteger_1 = randomGenerator.nextInt(10) +1; //0단계를 위한 랜덤 생성
            randomInteger_2 = randomGenerator.nextInt(10) +1; //0단계를 위한 랜덤 생성
            oper = " * ";
            result = randomInteger_1 * randomInteger_2;
        }
        else if(sol_difficulty.equals("1")){
            randomInteger_1 = randomGenerator.nextInt(10) +1; //1단계를 위한 랜덤 생성
            randomInteger_2 = randomGenerator.nextInt(10) +1; //1단계를 위한 랜덤 생성

            randomInteger_1 *= randomGenerator.nextInt(20) +10; //1단계를 위한 랜덤 큰 값으로
            randomInteger_2 *= randomGenerator.nextInt(20) +10; //1단계를 위한 랜덤 큰 값으로

            oper = " + ";
            result = randomInteger_1 + randomInteger_2;
        }
        else{
            randomInteger_1 = randomGenerator.nextInt(20) +11; //2단계를 위한 랜덤 생성
            randomInteger_1 += randomGenerator.nextInt(10);
            randomInteger_2 = randomGenerator.nextInt(10) +1;  //2단계를 위한 랜덤 생성
            oper = " * ";
            result = randomInteger_1 * randomInteger_2;
        }

        final TextView prob_view = new TextView(this);
        String mproblem = randomInteger_1 + oper + randomInteger_2;
        prob_view.setText(mproblem);
        prob_view.setTextSize(18);
        layout.addView(prob_view);

        et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL
                |InputType.TYPE_NUMBER_FLAG_SIGNED);
        layout.addView(et);
        Button input = new Button(this);
        input.setText("문제 풀기 (난이도 : " + sol_difficulty + ")");
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result==Integer.parseInt(et.getText().toString())){

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
                        Intent in = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(in);
                    }
                    else{
                        showNextActivity();
                    }

                    finish();
                }
                else{
                    Toast.makeText(MPActivity.this, "오답.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        layout.addView(input);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
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
    @Override
    public void onBackPressed() {

    }
}
