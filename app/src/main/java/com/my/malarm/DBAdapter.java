package com.my.malarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kjh on 2018. 5. 14..
 */

public class DBAdapter {
    private final Context context;
    static final String DB = "malarm";
    static final String TABLE_ALARM = "alarm";

    //////////////////////////////////////////////////////////////////
    //static final int DB_VERSION = 1;
    // 1 : 2018년 5월 14일
    static final int DB_VERSION = 4;
    // 4 : 2018년 6월 23일


    // alarm table
    public static final String ALARM_ON        = "onoff";     // ON / OFF
    public static final String ALARM_APDAY     = "apday";  // 적용일
    public static final String ALARM_HOUR      = "hour";   // 적용시
    public static final String ALARM_MINUTE    = "minute"; // 적용분
    public static final String ALARM_SONG      = "song";
    public static final String ALARM_LEVEL_1   = "level1";
    public static final String ALARM_LEVEL_2   = "level2";
    public static final String ALARM_LEVEL_3   = "level3";
    public static final String ALARM_VIBRATE   = "vibrate";// vibrate
    public static final String ALARM_LEV_1_KEY   = "klv1";
    public static final String ALARM_LEV_2_KEY   = "klv2";
    public static final String ALARM_LEV_3_KEY   = "klv3";

    ///////////////////////////////////////////////////////////////////
    static final String CREATE_ALARM = "create table " + TABLE_ALARM +
            " (_id integer primary key autoincrement, " +
            ALARM_ON + " integer, " +
            ALARM_APDAY + " integer, " +
            ALARM_HOUR + " integer, " +
            ALARM_MINUTE + " integer, " +
            ALARM_VIBRATE + " integer, " +
            ALARM_LEVEL_1 + " integer, " +
            ALARM_LEVEL_2 + " integer, " +
            ALARM_LEVEL_3 + " integer, " +
            ALARM_SONG + " integer);";


    static final String DROP = "drop table ";
    private SQLiteDatabase db;
    private NoteOpenHelper dbHelper;

    public DBAdapter(Context ctx) {
        context = ctx;
    }

    private static class NoteOpenHelper extends SQLiteOpenHelper {
        public NoteOpenHelper(Context c) {
            super(c, DB, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ALARM);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
            // TODO Auto-generated method stub
            if(oldversion!=DB_VERSION) {
                db.execSQL("ALTER TABLE alarm ADD COLUMN klv1 text");
                db.execSQL("ALTER TABLE alarm ADD COLUMN klv2 text");
                db.execSQL("ALTER TABLE alarm ADD COLUMN klv3 text");
            }
        }
    }

    public DBAdapter open() throws SQLException {
        dbHelper = new NoteOpenHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor fetchAllAlarm() {
        return db.query(TABLE_ALARM, null, null, null, null, null, ALARM_HOUR + " asc, " + ALARM_MINUTE + " asc");
    }

    public String addAlarm(int on, int day, int hour, int min, int vib, int lv1, int lv2, int lv3, int song, String klv1, String klv2, String klv3) {
        ContentValues values = new ContentValues();

        values.put(ALARM_ON, on);
        values.put(ALARM_APDAY, day);
        values.put(ALARM_HOUR, hour);
        values.put(ALARM_MINUTE, min);
        values.put(ALARM_SONG, song);
        values.put(ALARM_LEVEL_1, lv1);
        values.put(ALARM_LEVEL_2, lv2);
        values.put(ALARM_LEVEL_3, lv3);
        values.put(ALARM_VIBRATE, vib);
        values.put(ALARM_LEV_1_KEY, klv1);
        values.put(ALARM_LEV_2_KEY, klv2);
        values.put(ALARM_LEV_3_KEY, klv3);
        Log.d("KLV1",klv1);
        long id = db.insert(TABLE_ALARM, null, values);
        if (id < 0) {
            return "";
        }
        return Long.toString(id);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    public void delAlarm(String id) {
        db.delete(TABLE_ALARM, "_id = ?", new String[] {id});
    }

    public void modifyAlarm(long id, int on, int day, int hour, int min, int vib, int lv1, int lv2, int lv3, int song, String klv1, String klv2, String klv3) {
        ContentValues values = new ContentValues();
        values.put(ALARM_ON, on);
        values.put(ALARM_APDAY, day);
        values.put(ALARM_HOUR, hour);
        values.put(ALARM_MINUTE, min);
        values.put(ALARM_SONG, song);
        values.put(ALARM_LEVEL_1, lv1);
        values.put(ALARM_LEVEL_2, lv2);
        values.put(ALARM_LEVEL_3, lv3);
        values.put(ALARM_VIBRATE, vib);
        values.put(ALARM_LEV_1_KEY, klv1);
        values.put(ALARM_LEV_2_KEY, klv2);
        values.put(ALARM_LEV_3_KEY, klv3);


        db.update(TABLE_ALARM, values, "_id" + "='" + id + "'", null);
    }

    public void modifyAlarmOn(long id, int on) {
        ContentValues values = new ContentValues();
        values.put(ALARM_ON, on);

        db.update(TABLE_ALARM, values, "_id" + "='" + id + "'", null);
    }

}
