package com.my.malarm;
import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import static com.my.malarm.DefaultSet.vibe_freq;
import static com.my.malarm.DefaultSet.volume_size;

/**
 * Created by kjh on 2018. 4. 20..
 */

public class AlarmReceiver extends Activity {
    private ListView listView;
    public static ArrayList<MusicDto> list;
    private int YOURAPP_NOTIFICATION_ID;
    public static MediaPlayer m;
    public static boolean vibrating;
    public static boolean alarming;
    public static boolean alarming_lv1;
    public static boolean alarming_lv2;
    public static boolean alarming_lv3;
    public static int mLv1;
    public static int mLv2;
    public static int mLv3;
    public static String kLv1;
    public static String kLv2;
    public static String kLv3;
    public static String mNotifi = "Start Malarm!";
    public static int shake_count;
    public static String voice_Key;
    public static String sol_difficulty;
    public static MediaPlayer mediaPlayer;
    private TextView title;

    TextToSpeech tts;
    PowerManager.WakeLock wl = null;
    PowerManager pm;
    Scanner scan = new Scanner(System.in);
    public static Vibrator vibe = null;
    public static int count = 7;
    private int vib;

    public TextView mCurTimetextView;
    public Timer mTimer;
    Intent shakeIntent;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) { // 스크린이 켜져 있지 않으면 켠다
            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, "wake up");
            wl.acquire();
        }

        Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();

        showNotification(this, R.drawable.alarm1,
                "알람!!", "지금 이러고 있을 시간 없다.");

        setContentView(R.layout.activity_on);

        int song = getIntent().getIntExtra("song",1);
        vib  = getIntent().getIntExtra("vibrate",0);
        Log.d("song number",String.valueOf(song));

        if(song>0) {
            mediaPlayer = new MediaPlayer();
            getMusicList();
            playMusic(list.get(song));
        }else if(song==0){
            mediaPlayer = MediaPlayer.create(this , R.raw.soundtest );
            mediaPlayer.start();
        }

        //m.start();
        if (vib == 1) {	// 진동이 설정되어 있으면 ?
            vibrating = true;
            vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1000, 2000};        //  1초 대기 후 2초 진동
            if(vibe_freq==1){
                pattern = new long[]{700, 2000}; // 0.7초 대기 후 2초 진동
            }else if(vibe_freq==2){
                pattern = new long[]{400, 2000}; //0.4초 대기 후 2초 진동
            }
            vibe.vibrate(pattern, 0);                                 // 패턴을 지정하고, 계속 반복한다.
        }
        alarming = true;
        alarming_lv1 = true;
        alarming_lv2 = false;
        alarming_lv3 = false;

        if (wl != null) {
            wl.release();
            wl = null;
        }


        mLv1 = getIntent().getIntExtra("lv1",-1);
        mLv2 = getIntent().getIntExtra("lv2",-1);
        mLv3 = getIntent().getIntExtra("lv3",-1);

        kLv1 = new String(getIntent().getStringExtra("klv1"));
        kLv2 = new String(getIntent().getStringExtra("klv2"));
        kLv3 = new String(getIntent().getStringExtra("klv3"));

        /*
        Log.e("klv1",kLv1);
        Log.e("klv2",kLv2);
        Log.e("klv3",kLv3);
        */
        if(mLv2>0){
            alarming_lv2 = true;
            if(mLv3>0){
                alarming_lv3 = true;
            }
        }

        Button btn1 = (Button)findViewById(R.id.solve_btn);

        if(mLv1==0 && alarming_lv1){
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shake_count = Integer.parseInt(kLv1);
                    Intent in = new Intent(getBaseContext(), ShakeActivity.class);
                    startActivity(in);
                }
            });
        }
        else if(mLv1==1 && alarming_lv1){
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voice_Key = kLv1;
                    Intent in = new Intent(getBaseContext(), VoiceActivity.class);
                    startActivity(in);
                }
            });
        }
        else if(mLv1==2 && alarming_lv1){
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sol_difficulty = kLv1;
                    Intent in = new Intent(getBaseContext(), MPActivity.class);
                    startActivity(in);
                }
            });
        }
        else if(mLv2==1 && alarming_lv2){
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shake_count = Integer.parseInt(kLv2);
                    Intent in = new Intent(getBaseContext(), ShakeActivity.class);
                    startActivity(in);
                }
            });
        }
        else if(mLv2==2 && alarming_lv2){
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voice_Key = kLv3;
                    Intent in = new Intent(getBaseContext(), VoiceActivity.class);
                    startActivity(in);
                }
            });
        }
        else if(mLv2==3 && alarming_lv2){
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sol_difficulty = kLv2;
                    Intent in = new Intent(getBaseContext(), MPActivity.class);
                    startActivity(in);
                }
            });
        }
        else if(mLv3==1 && alarming_lv3){
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shake_count = Integer.parseInt(kLv3);
                    Intent in = new Intent(getBaseContext(), ShakeActivity.class);
                    startActivity(in);
                }
            });
        }
        else if(mLv3==2 && alarming_lv3){
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voice_Key = kLv3;
                    Intent in = new Intent(getBaseContext(), VoiceActivity.class);
                    startActivity(in);
                }
            });
        }
        else if(mLv3==3 && alarming_lv3){
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sol_difficulty = kLv3;
                    Intent in = new Intent(getBaseContext(), MPActivity.class);
                    startActivity(in);
                }
            });
        }
/*
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                if(alarming==true){
                    mp.start();
                }
                else{
                    mp.stop();
                    mp.release();
                }
            }
        });
*/
        mCurTimetextView = (TextView)findViewById(R.id.CurrentTimeTextView);
        AlarmReceiver.MainTimerTask timerTask = new AlarmReceiver.MainTimerTask();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 500, 1000);
        TextView textView = (TextView)findViewById(R.id.Textview);
        ImageView imageView =findViewById(R.id.weather);
        WeatherConnection weatherConnection = new WeatherConnection();
        AsyncTask<String, String, String> result = weatherConnection.execute("","");
        try{
            String msg = result.get();
            if(weatherConnection.text1.equals("맑음"))
                imageView.setBackgroundResource(R.drawable.sunny);
            else if(weatherConnection.text1.equals("구름조금"))
                imageView.setBackgroundResource(R.drawable.littlecloud);
            else if(weatherConnection.text1.equals("구름많음"))
                imageView.setBackgroundResource(R.drawable.manycloud);
            else if(weatherConnection.text1.equals("흐리고 비"))
                imageView.setBackgroundResource(R.drawable.rain);
            else if(weatherConnection.text1.equals("흐림"))
                imageView.setBackgroundResource(R.drawable.cloudy2);

            textView.setText(msg.toString());
        }catch (Exception e){}
    }
    public class WeatherConnection extends AsyncTask<String, String, String> {

        // 백그라운드에서 작업하게 한다
        public String text1 = null;
        @Override
        protected String doInBackground(String... params) {
            // Jsoup을 이용한 날씨데이터 Pasing하기
            try {
                String path = "https://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=08410108";
                Document document = Jsoup.connect(path).get();
                Elements elements = document.select("em");
                Elements elements1 = document.select("div.fl em strong");
                Element targetElement = elements.get(2);
                String text = targetElement.text();
                for(Element e: elements1)
                    text1 = e.text();
                return text;
            }catch(IOException i){}
            return null;
        }
    }
    private void showNotification(Context context, int statusBarIconID,
                                  String statusBarTextID, String detailedTextID) {
        Intent contentIntent = new Intent(context, BreakAlarm.class);
        PendingIntent theappIntent =
                PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        CharSequence from = "알람";
        CharSequence message = mNotifi;

        Notification notif = new Notification(statusBarIconID, null, System.currentTimeMillis());
        notif.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6");//ringURI;
        notif.flags = Notification.FLAG_INSISTENT;
        notif = new Notification.Builder(context)
                .setContentTitle(from)
                .setContentText(message)
                .setTicker(null)
                .setSmallIcon(statusBarIconID)
                .setContentIntent(theappIntent)
                .build();
        notif.ledARGB = Color.GREEN;
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
/*
        if(volume_size==0){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,3,AudioManager.FLAG_PLAY_SOUND);
            //mediaPlayer.setVolume(0.3f, 0.3f);
        }else if(volume_size==1){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,8,AudioManager.FLAG_PLAY_SOUND);
            //mediaPlayer.setVolume(0.5f, 0.5f);
        }else if(volume_size==2){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,14,AudioManager.FLAG_PLAY_SOUND);
            //mediaPlayer.setVolume(0.7f, 0.7f);
        }*/
        am.setStreamVolume(AudioManager.STREAM_MUSIC,volume_size,AudioManager.FLAG_PLAY_SOUND);

        nm.notify(1234, notif);

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
        AlarmReceiver.MainTimerTask timerTask = new AlarmReceiver.MainTimerTask();
        mTimer.schedule(timerTask, 500, 3000);
        super.onResume();
    }

    class MainTimerTask extends TimerTask {
        public void run(){
            mHandler.post(mUpdateTimeTask);
        }
    }
    public  void getMusicList(){
        list = new ArrayList<>();
        //가져오고 싶은 컬럼 명을 나열합니다. 음악의 아이디, 앰블럼 아이디, 제목, 아스티스트 정보를 가져옵니다.
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);

        while(cursor.moveToNext()){
            MusicDto musicDto = new MusicDto();
            musicDto.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            musicDto.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            musicDto.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            musicDto.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            list.add(musicDto);
            Log.d("musicDto getID",musicDto.getId());
            Log.d("musicDto getAlbumId",musicDto.getAlbumId());
            Log.d("musicDto getTitle",musicDto.getTitle());
            Log.d("musicDto getArtist",musicDto.getArtist());
        }

        cursor.close();

    }
    @Override
    public void onBackPressed() {
        //this.finish();
    }
    public void playMusic(MusicDto musicDto) {

        try {
            //seekBar.setProgress(0);
            //title.setText(musicDto.getArtist()+" - "+musicDto.getTitle());

            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+musicDto.getId());

            //mediaPlayer.reset();
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            //seekBar.setMax(mediaPlayer.getDuration());


            Log.d("musicDto", musicDto.getId());
            //Bitmap bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(musicDto.getAlbumId()),getApplication()));
            //album.setImageBitmap(bitmap);

        }
        catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
    }
    private static String getCoverArtPath(long albumId, Context context) {

        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }
}