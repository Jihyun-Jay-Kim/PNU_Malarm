package com.my.malarm;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


import static com.my.malarm.AlarmReceiver.alarming_lv1;
import static com.my.malarm.AlarmReceiver.alarming_lv2;
import static com.my.malarm.AlarmReceiver.alarming_lv3;
import static com.my.malarm.AlarmReceiver.kLv1;
import static com.my.malarm.AlarmReceiver.kLv2;
import static com.my.malarm.AlarmReceiver.kLv3;
import static com.my.malarm.AlarmReceiver.m;
import static com.my.malarm.AlarmReceiver.alarming;
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
 * Created by kjh on 2018. 5. 21..
 */

public class VoiceActivity extends Activity {

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 5);
            toast("순순히 권한을 넘기지 않으면, 음성 인식 기능을 사용할 수 없다!");
        }
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView txt = new TextView(this);
        txt.setText("\n");
        txt.setTextSize(18);
        layout.addView(txt);
        Button input = new Button(this);

        input.setText("음성 입력 : " + voice_Key);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.pause();
                inputVoice(txt);
            }
        });
        layout.addView(input);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREAN);
            }
        });

    }

    public void inputVoice(final TextView txt) {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            final SpeechRecognizer stt = SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    toast("음성 입력 시작...");
                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                }

                @Override
                public void onEndOfSpeech() {
                    toast("음성 입력 종료");
                }

                @Override
                public void onError(int error) {

                    toast("오류 발생 : " + error);
                    mediaPlayer.start();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> result = (ArrayList<String>) results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    txt.append("[나] "+result.get(0)+"\n");
                    replyAnswer(result.get(0), txt);
                    stt.destroy();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            stt.startListening(intent);
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void replyAnswer(String input, TextView txt){
        try{
            /*
            if(input.equals("안녕")){
                txt.append("[짭스티] 누구세요?\n");
                tts.speak("누구세요?", TextToSpeech.QUEUE_FLUSH, null);
            }
            else if(input.equals("너는 누구니")){
                txt.append("[짭스티] 나는 짭스티라고 해.\n");
                tts.speak("나는 짭스티라고 해.", TextToSpeech.QUEUE_FLUSH, null);
            }
            */
            if(input.equals(voice_Key)){
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
                    mediaPlayer.start();
                }
                finish();
            }
            else {
                txt.append("[Malarm] 일어나서 얘기해\n");
                tts.speak(input, TextToSpeech.QUEUE_FLUSH, null);
                tts.speak("일어나서 얘기해.", TextToSpeech.QUEUE_FLUSH, null);
                int delaytime = 0;
                delaytime = 0;
                Thread.sleep(4000);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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