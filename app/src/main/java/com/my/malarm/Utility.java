package com.my.malarm;
/**
 * Created by kjh on 2018. 5. 17..
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.Calendar;
import java.util.Locale;

public class Utility {
    static final int alarmSetId = 123;

    // 
    // 현재 언어 설정이 한국어인지 판단
    //
    public static boolean useKoreanLanguage(Context context) {
        Locale lc = context.getResources().getConfiguration().locale;
        String language = lc.getLanguage();

        if (language.equals("ko")) { // 일본어 : ja,  영어 : en
            return true;
        } else {
            return false;
        }
    }

    // DB 에 있는 첫번째 알람을 설정
    public  static void startFirstAlarm(Context context) {
        int apday;
        int onoff;
        int day;
        int hour;
        int min;
        int vib;
        int song;
        int lv1;
        int lv2;
        int lv3;
        String k_lv1;
        String k_lv2;
        String k_lv3;

        Calendar calendar = Calendar.getInstance();
        int c_day = calendar.get(Calendar.DAY_OF_WEEK);
        int c_hour = calendar.get(Calendar.HOUR_OF_DAY);
        int c_min = calendar.get(Calendar.MINUTE);
        //minimum  가장 가가운 id를 찾는 변수
        int m_day = 100;
        int m_hour = 100;
        int m_min = 100;
        int m_vib = 0;
        int m_song = 0;
        int m_lv1 = 0;
        int m_lv2 = 0;
        int m_lv3 = 0;
        String mk_lv1 = new String("5");
        String mk_lv2 = new String("종료");
        String mk_lv3 = new String("0");
        //
        long m_id;
        // 오늘의 년-월-일
        // 시간이 경과한 알람을 DB에서 제거
        DBAdapter db = new DBAdapter(context);
        if (db == null) return;

        db.open();
        Cursor c = db.fetchAllAlarm();

        if (c.moveToFirst()) {	// 첫번째로 이동
            do {
                onoff =  c.getInt(c.getColumnIndex(DBAdapter.ALARM_ON));
                if (onoff == 1) {
                    day =  c.getInt(c.getColumnIndex(DBAdapter.ALARM_APDAY));
                    hour = c.getInt(c.getColumnIndex(DBAdapter.ALARM_HOUR));
                    min = c.getInt(c.getColumnIndex(DBAdapter.ALARM_MINUTE));
                    vib = c.getInt(c.getColumnIndex(DBAdapter.ALARM_VIBRATE));
                    lv1 = c.getInt(c.getColumnIndex(DBAdapter.ALARM_LEVEL_1));
                    lv2 = c.getInt(c.getColumnIndex(DBAdapter.ALARM_LEVEL_2));
                    lv3 = c.getInt(c.getColumnIndex(DBAdapter.ALARM_LEVEL_3));
                    song = c.getInt(c.getColumnIndex(DBAdapter.ALARM_SONG));
                    k_lv1 = c.getString(c.getColumnIndex(DBAdapter.ALARM_LEV_1_KEY));
                    k_lv2 = c.getString(c.getColumnIndex(DBAdapter.ALARM_LEV_2_KEY));
                    k_lv3 = c.getString(c.getColumnIndex(DBAdapter.ALARM_LEV_3_KEY));
                    //
                    for (int i = 0; i < 7; i++) {
                        if ((day & 0x01) == 0x01) {
                            apday = i+1;
                            if ((apday < c_day)
                                    || ((apday == c_day) && (hour < c_hour))
                                    || ((apday == c_day) && (hour == c_hour) && (min <= c_min))){
                                apday += 7;
                            }

                            if (m_day > apday){
                                m_day = apday;
                                m_hour = hour;
                                m_min = min;
                                m_id = c.getLong(c.getColumnIndex("_id"));
                                m_vib = vib;
                                m_song = song;
                                m_lv1 = lv1;
                                m_lv2 = lv2;
                                m_lv3 = lv3;
                                mk_lv1 = k_lv1;
                                mk_lv2 = k_lv2;
                                mk_lv3 = k_lv3;
                            } else if ((m_day == apday ) && (m_hour > hour)){
                                m_hour = hour;
                                m_min = min;
                                m_id = c.getLong(c.getColumnIndex("_id"));
                                m_vib = vib;
                                m_song = song;
                                m_lv1 = lv1;
                                m_lv2 = lv2;
                                m_lv3 = lv3;
                                mk_lv1 = k_lv1;
                                mk_lv2 = k_lv2;
                                mk_lv3 = k_lv3;
                            } else if ((m_day == apday) && (m_hour == hour) && (m_min > min)){
                                m_min = min;
                                m_id = c.getLong(c.getColumnIndex("_id"));
                                m_vib = vib;
                                m_song = song;
                                m_lv1 = lv1;
                                m_lv2 = lv2;
                                m_lv3 = lv3;
                                mk_lv1 = k_lv1;
                                mk_lv2 = k_lv2;
                                mk_lv3 = k_lv3;
                            }
                        }
                        day = day >> 1;
                    }
                }
            } while (c.moveToNext());
        }
        if( m_day != 100){
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("song", m_song);
            intent.putExtra("lv1", m_lv1);
            intent.putExtra("lv2", m_lv2);
            intent.putExtra("lv3", m_lv3);
            intent.putExtra("vibrate", m_vib);

            intent.putExtra("klv1", mk_lv1);
            intent.putExtra("klv2", mk_lv2);
            intent.putExtra("klv3", mk_lv3);
            //PendingIntent sender = PendingIntent.getBroadcast(afternoonAlarm.this, 0, intent, 0);
            calendar.add(Calendar.DAY_OF_MONTH, m_day - c_day);
            calendar.set(Calendar.HOUR_OF_DAY, m_hour);
            calendar.set(Calendar.MINUTE, m_min);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            PendingIntent sender = PendingIntent.getActivity(context, alarmSetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager mManager = null;
            mManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 0, sender);
   
       	/* Toast.makeText(context, "알람 설정 시간" + calendar.get(Calendar.YEAR)+ "년 "
			+ (calendar.get(Calendar.MONTH)+1) + "월 "
			+ calendar.get(Calendar.DAY_OF_MONTH) + "일 "
			//+ calendar.get(Calendar.DAY_OF_WEEK)  + "요일 "
			+ calendar.get(Calendar.HOUR_OF_DAY) + "시 "
			+ calendar.get(Calendar.MINUTE)+ "분 ",
			7000).show();
			*/
        }

        db.close();
    }

    //알람 해제
    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getActivity(context, alarmSetId, intent, PendingIntent.FLAG_CANCEL_CURRENT );

        // And cancel the alarm.
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
        //Toast.makeText(context, "알람이 해제됐습니다.", Toast.LENGTH_SHORT).show();
    }

}