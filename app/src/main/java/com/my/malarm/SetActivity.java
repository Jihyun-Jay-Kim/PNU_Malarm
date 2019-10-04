package com.my.malarm;
import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by kjh on 2018. 4. 20..
 */

public class SetActivity extends Activity implements OnTimeChangedListener, OnDateChangedListener {
    private ListView listView;
    public static ArrayList<MusicDto> list;
    static final int alarmSetId = 123;

    private MediaPlayer mediaPlayer;
    private TextView title;
    private ImageView album,previous,play,pause,next;
    private SeekBar seekBar;
    boolean isPlaying = true;
    private ContentResolver res;
    private MusicActivity.ProgressUpdate progressUpdate;
    private int position;
    public static MediaPlayer m;
    public static boolean alarming;

    static int changeScreen = 0;
    /*
     * 알람관련 맴버 변수
     */ // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //일자 설정 클래스
    private DatePicker mDate;
    //시작 설정 클래스
    private TimePicker mTime;

    private int myH;
    private int myM;

    Intent shakeIntent;

    private long db_id = -1;

    private ToggleButton _toggleSun, _toggleMon, _toggleTue, _toggleWed, _toggleThu, _toggleFri, _toggleSat, _toggleVib;

    private int mVibrate = 0;

    private TextView textView1, textView2, textView3;

    private int mAlarmHour = 12;
    private int mAlarmMinute = 0;
    /*
     * 통지 관련 맴버 변수
     */
    private NotificationManager mNotification;

    private int whichMusic;
    private int mLv1; // 없음 있을 때, 0:없음, 1:흔들기, 2:말하기, 3:문제풀기
    private int mLv2; // 없음 없을 때, 0:흔들기, 1:말하기, 2:문제풀기
    private int mLv3;

    private String kLv1; // 각 스텝 해제 키
    private String kLv2;
    private String kLv3;

    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    public static String def_shk = "5";
    public static String def_voi = "종료";
    public static String def_sol = "0";
    public static int Vol_firVal = 1;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whichMusic = 0;
        mLv1 = -1;
        mLv2 = -1;
        mLv3 = -1;

        kLv1 = new String("");
        kLv2 = new String("");
        kLv3 = new String("");

        //통지 매니저를 취득
        mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //알람 매니저를 취득
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);



        //시각 취득
        mCalendar = new GregorianCalendar();
        Log.i("HelloAlarmActivity",mCalendar.getTime().toString());
        //셋 버튼, 리셋버튼의 리스너를 등록
        setContentView(R.layout.activity_alarm);

        db_id = getIntent().getLongExtra("id",-1);

        shakeIntent = new Intent(this, ShakeService.class);
        _toggleSun = (ToggleButton) findViewById(R.id.toggle_sun);
        _toggleMon = (ToggleButton) findViewById(R.id.toggle_mon);
        _toggleTue = (ToggleButton) findViewById(R.id.toggle_tue);
        _toggleWed = (ToggleButton) findViewById(R.id.toggle_wed);
        _toggleThu = (ToggleButton) findViewById(R.id.toggle_thu);
        _toggleFri = (ToggleButton) findViewById(R.id.toggle_fri);
        _toggleSat = (ToggleButton) findViewById(R.id.toggle_sat);
        _toggleVib = (ToggleButton) findViewById(R.id.toggle_vib);

        textView1 = (TextView) findViewById(R.id.cur_lv1);
        textView2 = (TextView) findViewById(R.id.cur_lv2);
        textView3 = (TextView) findViewById(R.id.cur_lv3);

        textView1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mLv1==0){
                    showDialog(101);
                }
                else if(mLv1==1){
                    showDialog(102);
                }
                else if(mLv1==2){
                    showDialog(103);
                }

            }
        });

        textView2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mLv2==1){
                    showDialog(201);
                }
                else if(mLv2==2){
                    showDialog(202);
                }
                else if(mLv2==3){
                    showDialog(203);
                }

            }
        });

        textView3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mLv3==1){
                    showDialog(301);
                }
                else if(mLv3==2){
                    showDialog(302);
                }
                else if(mLv3==3){
                    showDialog(303);
                }

            }
        });


        Button b_set = (Button)findViewById(R.id.setalarm);
        b_set.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                setAlarm();
            }
        });

        Button b_reset = (Button)findViewById(R.id.reset);
        b_reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetAlarm();
            }
        });


        Button b_sel = (Button)findViewById(R.id.select);
        b_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });

        Button b_lv1 = (Button)findViewById(R.id.sel_lv1);
        b_lv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(2);

                if(mLv1==0){
                    textView1.setText("흔들기");
                }
                else if(mLv1==1){
                    textView1.setText("음성인식");
                }
                else if(mLv1==2){
                    textView1.setText("문제풀기");
                }
            }
        });

        Button b_lv2 = (Button)findViewById(R.id.sel_lv2);
        b_lv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLv1==-1){
                    showDialog(11);
                }
                else{
                    showDialog(3);

                    if(mLv2==0){
                        textView2.setText("없음");
                    }
                    else if(mLv2==1){
                        textView2.setText("흔들기");
                    }
                    else if(mLv2==2){
                        textView2.setText("음성인식");
                    }
                    else if(mLv2==3){
                        textView2.setText("문제풀기");
                    }

                }
            }
        });

        Button b_lv3 = (Button)findViewById(R.id.sel_lv3);
        b_lv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLv1==-1){
                    showDialog(11);
                }
                else if(mLv2==-1){
                    showDialog(21);
                }
                else if(mLv2==0){
                    showDialog(22);
                }
                else{
                    showDialog(4);

                    if(mLv3==0){
                        textView3.setText("없음");
                    }
                    else if(mLv3==1){
                        textView3.setText("흔들기");
                    }
                    else if(mLv3==2){
                        textView3.setText("음성인식");
                    }
                    else if(mLv3==3){
                        textView3.setText("문제풀기");
                    }
                }
            }
        });
        //일시 설정 클래스로 현재 시각을 설정
        mDate = (DatePicker)findViewById(R.id.date_picker);
        mDate.init (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),
                this);


        mTime = (TimePicker)findViewById(R.id.time_picker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            Log.d("Permission", "Permission denied.");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,  Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Log.d("Permission", "shouldShowRequestPermissionRationale.");
            } else {
                ActivityCompat.requestPermissions(this,       new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},  MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                Log.d("Permission", "else.");
            }
        }
        getMusicList();
    }

    //알람의 설정
    private void setAlarm() {
        int apday = 0, onVib = 0;

        if (_toggleSun.isChecked())  { apday |= 0x0001;}
        if (_toggleMon.isChecked())  { apday |= 0x0002;}
        if (_toggleTue.isChecked())  { apday |= 0x0004;}
        if (_toggleWed.isChecked())  { apday |= 0x0008;}
        if (_toggleThu.isChecked())  { apday |= 0x0010;}
        if (_toggleFri.isChecked())  { apday |= 0x0020;}
        if (_toggleSat.isChecked())  { apday |= 0x0040;}


        if (_toggleVib.isChecked())  { onVib = 1; }

        if (apday == 0)
        {
            Toast.makeText(SetActivity.this, "요일을 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mLv1 == -1)
        {
            Toast.makeText(SetActivity.this, "해제 단계를 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }


        myH = mTime.getCurrentHour();
        myM = mTime.getCurrentMinute();

        long setTimeL = mCalendar.getTimeInMillis();
        long nowTime = System.currentTimeMillis();

        long oneday = 24 * 60 * 60 * 1000;// 24시간

        while(setTimeL <= nowTime){
            setTimeL += oneday;
        }
/*
        if(nowTime >= setTimeL){
            Toast.makeText(SetActivity.this, "입력한 날짜는 현재 날짜보다 이전입니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        */
        Toast.makeText(SetActivity.this, "알람 설정.",
                Toast.LENGTH_SHORT).show();

        boolean[] week = { false, _toggleSun.isChecked(), _toggleMon.isChecked(), _toggleTue.isChecked(), _toggleWed.isChecked(),
                _toggleThu.isChecked(), _toggleFri.isChecked(), _toggleSat.isChecked() };

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("weekday", week);
        intent.putExtra("vibrate", onVib);
        PendingIntent sender = PendingIntent.getBroadcast(SetActivity.this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);


        DBAdapter db = new DBAdapter(SetActivity.this);
        db.open();

        if(db_id==-1){
            db.addAlarm(1, apday, myH, myM, onVib, mLv1, mLv2, mLv3, whichMusic,kLv1,kLv2,kLv3);
        }
        else{
            db.modifyAlarm(db_id,1, apday, myH, myM, onVib, mLv1, mLv2, mLv3, whichMusic,kLv1,kLv2,kLv3);
        }/*


        am.setRepeating(AlarmManager.RTC_WAKEUP, setTimeL, oneday, sender);
        Log.i("HelloAlarmActivity", mTime.toString());*/
        db.close();
        Utility.startFirstAlarm(this);
        this.finish();
    }

    //알람의 해제
    private void resetAlarm() {
        this.onBackPressed();
    }

    public static void cancelAlarm(Context ctx){
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getActivity(ctx, alarmSetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager)ctx.getSystemService(ctx.ALARM_SERVICE);
        am.cancel(sender);
    }

    //알람의 설정 시각에 발생하는 인텐트 작성
    private PendingIntent pendingIntent() {
        Intent i = new Intent(getApplicationContext(), SetActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        return pi;
    }

    //일자 설정 클래스의 상태변화 리스너
    public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set (year, monthOfYear, dayOfMonth, mTime.getCurrentHour(), mTime.getCurrentMinute());
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
    }

    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged (TimePicker view, int hourOfDay, int minute) {
        //mTime.setCurrentHour(hourOfDay);
        //mTime.setCurrentMinute(minute);
        //myH = hourOfDay;
        //myM = minute;

        mCalendar.set (mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), hourOfDay, minute, 0);
/*
              mCalendar.set(mCalendar.HOUR_OF_DAY, );
              mCalendar.set(mCalendar.MINUTE, minute);
              mCalendar.set(mCalendar.SECOND, 0);*/

        Log.i("HelloAlarmActivity",mCalendar.getTime().toString());
    }

    protected Dialog onCreateDialog(int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SetActivity.this);

        switch (id){
            case 1:
                final LinearLayout exLayout;
                LayoutInflater inflat =(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                exLayout = (LinearLayout)inflat.inflate(R.layout.mylist, null);

                LinearLayout.LayoutParams paramLayout = new LinearLayout.LayoutParams(

                    LinearLayout.LayoutParams.MATCH_PARENT,

                    LinearLayout.LayoutParams.MATCH_PARENT);

                addContentView(exLayout,paramLayout);


                //setContentView(R.layout.mylist);
                //getMusicList();
                builder.setTitle("노래 선택");

                ListView listView = (ListView)findViewById(R.id.list__view);


                MyAdapter adapter = new MyAdapter(this,list);


                listView.setAdapter(adapter);

                  builder.setView(listView);

                builder.setPositiveButton("Yes", null);

                builder.setNegativeButton("No", null);
                ((ViewGroup)listView.getParent()).removeView(listView);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    whichMusic = position;
                    Log.d("position",String.valueOf(position));
                    //list.get(position);
                    //playMusic(list.get(position));

                    //Intent intent = new Intent(SetActivity.this,MusicActivity.class);
                    //intent.putExtra("position",position);
                    //intent.putExtra("playlist",list);
                    //startActivity(intent);

                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Text 값 받아서 로그 남기기
                    String value = String.valueOf(whichMusic);

                    dialog.dismiss();     //닫기
                    // Event
                    if(changeScreen==0) {
                        changeScreen =1;

                        ((ViewGroup)exLayout.getParent()).removeView(exLayout);
                    }
                }
            });
            builder.setNegativeButton("No", null);

            break;
            case 2:
                builder.setTitle("스텝1 선택");

                builder.setItems(R.array.levelmenu_default, new DialogInterface.OnClickListener() {

                    // 리스트 목록 클릭 이벤트

                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        mLv1 = which;

                        if(mLv1==0){
                            textView1.setText("흔들기");
                            kLv1 = def_shk;
                        }
                        else if(mLv1==1){
                            textView1.setText("음성인식");
                            kLv1 = def_voi;
                        }
                        else if(mLv1==2){
                            textView1.setText("문제풀기");
                            kLv1 = def_sol;
                        }
                    }
                });
                //builder.setPositiveButton("Yes", null);

                builder.setNegativeButton("No", null);
                break;

            case 3:
                builder.setTitle("스텝2 선택");

                builder.setItems(R.array.levelmenu, new DialogInterface.OnClickListener() {

                    // 리스트 목록 클릭 이벤트

                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        mLv2 = which;

                        if(mLv2==0){
                            textView2.setText("없음");
                        }
                        else if(mLv2==1){
                            textView2.setText("흔들기");
                            kLv2 = def_shk;
                        }
                        else if(mLv2==2){
                            textView2.setText("음성인식");
                            kLv2 = def_voi;
                        }
                        else if(mLv2==3){
                            textView2.setText("문제풀기");
                            kLv2 = def_sol;
                        }
                    }

                });
                //builder.setPositiveButton("Yes", null);

                builder.setNegativeButton("No", null);
                break;
            case 4:
                builder.setTitle("스텝3 선택");

                builder.setItems(R.array.levelmenu, new DialogInterface.OnClickListener() {

                    // 리스트 목록 클릭 이벤트

                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        mLv3 = which;


                        if(mLv3==0){
                            textView3.setText("없음");
                        }
                        else if(mLv3==1){
                            textView3.setText("흔들기");
                            kLv3 = def_shk;
                        }
                        else if(mLv3==2){
                            textView3.setText("음성인식");
                            kLv3 = def_voi;
                        }
                        else if(mLv3==3){
                            textView3.setText("문제풀기");
                            kLv3 = def_sol;
                        }
                    }



                });
                //builder.setPositiveButton("Yes", null);

                builder.setNegativeButton("No", null);
                break;
            case 11:
                builder.setTitle("스텝1 먼저 선택하세요");
                builder.setNegativeButton("Cancel", null);
                break;
            case 21:
                builder.setTitle("스텝2 먼저 선택하세요");
                builder.setNegativeButton("Cancel", null);
                break;
            case 22:
                builder.setTitle("스텝2가 없습니다!");
                builder.setNegativeButton("Cancel", null);
                break;
            case 101:
            case 201:
            case 301:
                builder.setTitle("흔들기 횟수 설정");

                final EditText et1 = new EditText(SetActivity.this);
                final int myID_1 = id;
                et1.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL
                        |InputType.TYPE_NUMBER_FLAG_SIGNED);

                builder.setView(et1);
                // 확인 버튼 설정
                /*
                if(Integer.getInteger(et1.getText().toString())<5){
                    Toast.makeText(SetActivity.this, "5~20 입력하세요!",
                            Toast.LENGTH_SHORT).show();
                }
                */
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Text 값 받아서 로그 남기기
                        String value = et1.getText().toString();
                        if(myID_1==101){
                            kLv1 = new String(value);
                        }
                        else if(myID_1==201){
                            kLv2 = new String(value);
                        }
                        else if(myID_1==301){
                            kLv3 = new String(value);
                        }
                        // Event
                        if(value.length() == 0 || Integer.parseInt(value)<5 || Integer.parseInt(value)>50 ) {
                            Toast.makeText(SetActivity.this, "횟수 에러! 디폴트 설정합니다.",
                                    Toast.LENGTH_SHORT).show();
                            if(myID_1==101){
                                kLv1 = def_shk;
                            }
                            else if(myID_1==201){
                                kLv2 = def_shk;
                            }
                            else if(myID_1==301){
                                kLv3 = def_shk;
                            }    
                        }
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("No", null);
                break;
            case 102:
            case 202:
            case 302:
                builder.setTitle("음성 종료 설정");

                final EditText et2 = new EditText(SetActivity.this);
                final int myID_2 = id;

                builder.setView(et2);
                // 확인 버튼 설정
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Text 값 받아서 로그 남기기
                        String value = et2.getText().toString();
                        if(myID_2==102){
                            kLv1 = new String(value);
                        }
                        else if(myID_2==202){
                            kLv2 = new String(value);
                        }
                        else if(myID_2==302){
                            kLv3 = new String(value);
                        }
                        if(value.length() == 0 || value.length()<2 || value.length()>30 ) {
                            Toast.makeText(SetActivity.this, "종료어 에러! 디폴트 설정합니다.",
                                    Toast.LENGTH_SHORT).show();
                            if(myID_2==102){
                                kLv1 = def_voi;
                            }
                            else if(myID_2==202){
                                kLv2 = def_voi;
                            }
                            else if(myID_2==302){
                                kLv3 = def_voi;
                            }
                        }

                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
                builder.setNegativeButton("No", null);
                break;
            case 103:
            case 203:
            case 303:
                final int myID_3 = id;
                builder.setTitle("문제 난이도 설정");

                builder.setItems(R.array.solve_level, new DialogInterface.OnClickListener() {

                    // 리스트 목록 클릭 이벤트

                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        String value = String.valueOf(which);

                        if(myID_3==103){
                            kLv1 = new String(value);
                        }
                        else if(myID_3==203){
                            kLv2 = new String(value);
                        }
                        else if(myID_3==303){
                            kLv3 = new String(value);
                        }
                    }

                });
                builder.setNegativeButton("No", null);
                break;
        }

        return builder.create();

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
        this.finish();
    }
}