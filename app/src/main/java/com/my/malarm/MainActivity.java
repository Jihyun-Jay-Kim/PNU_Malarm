package com.my.malarm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class MainActivity extends Activity {
    private DBAdapter db;
    private ListView list;
    private MyCursorAdapter adapter;
    private Cursor currentCursor;


    private static int colID;
    private static int colONOFF;
    private static int colHOUR;
    private static int colMINUTE;
    private static int colDAY;
    private static int colSONG;
    private static int colLV1;
    private static int colLV2;
    private static int colLV3;
    private static int colVIB;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent LoadingIntent = new Intent(this, LoadingActivity.class);
        startActivity(LoadingIntent);

        db = new DBAdapter(this);
        db.open();

        currentCursor = db.fetchAllAlarm();

        list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(itemClickListener);
        list.setOnTouchListener(TouchListener);
        String[] from = new String[]{DBAdapter.ALARM_HOUR, DBAdapter.ALARM_MINUTE};
        int[] to = new int[]{R.id.list_time, R.id.list_date};

        adapter = new MyCursorAdapter(list.getContext(), R.layout.list_item, currentCursor, from, to);
        list.setAdapter(adapter);

        colID = currentCursor.getColumnIndex("_id");
        colONOFF = currentCursor.getColumnIndex(DBAdapter.ALARM_ON);
        colDAY = currentCursor.getColumnIndex(DBAdapter.ALARM_APDAY);
        colHOUR = currentCursor.getColumnIndex(DBAdapter.ALARM_HOUR);
        colMINUTE = currentCursor.getColumnIndex(DBAdapter.ALARM_MINUTE);
        colSONG = currentCursor.getColumnIndex(DBAdapter.ALARM_SONG);
        colLV1 = currentCursor.getColumnIndex(DBAdapter.ALARM_LEVEL_1);
        colLV2 = currentCursor.getColumnIndex(DBAdapter.ALARM_LEVEL_2);
        colLV3 = currentCursor.getColumnIndex(DBAdapter.ALARM_LEVEL_3);
        colVIB = currentCursor.getColumnIndex(DBAdapter.ALARM_VIBRATE);

        Button btn1 = (Button) findViewById(R.id.ib3);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), SetActivity.class);
                startActivity(in);
                //finish();
            }
        });

        Button btn2 = (Button) findViewById(R.id.def_set);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), DefaultSet.class);
                startActivity(in);
                //finish();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

        currentCursor = db.fetchAllAlarm();
        adapter.notifyDataSetChanged();

        //list.setAdapter(adapter);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////
    //for column action - 터치된 위치를 알아냄
    ///////////////////////////////////////////////////////////////////////////
    private static int QuickMenuEvent = 0;
    private static float CheckedColumn_x = 0;

    AdapterView.OnTouchListener TouchListener = new AdapterView.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // 여기서 view 는 ListItem 이 아닌  리스트 자체임
            CheckedColumn_x = event.getX();
            //CheckedColumn_y = event.getY();
            QuickMenuEvent = event.getAction();

            return false;
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////
    //
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> list, View view, int position, long id) {
            currentCursor.moveToPosition(position);

            ImageView icon_view = (ImageView)view.findViewById(R.id.list_onoff);
            ImageView delete_view = (ImageView)view.findViewById(R.id.alarm_del);

            if (icon_view.getLeft() < CheckedColumn_x && CheckedColumn_x < icon_view.getRight()) {
                long db_id = currentCursor.getLong(colID);
                int on = currentCursor.getInt(colONOFF);

                //
                if (on == 0) on = 1;
                else on = 0;
                //
                db.modifyAlarmOn(db_id, on);
                currentCursor = db.fetchAllAlarm();
                adapter.notifyDataSetChanged();
                //
                //	calendar = Calendar.getInstance();
                if(on == 1){
                    icon_view.setImageResource(R.drawable.on_icon);
                    Toast.makeText(getBaseContext(), "알람이 설정 되었습니다. ",
                            Toast.LENGTH_SHORT).show();
                } else {
                    icon_view.setImageResource(R.drawable.off_icon);
                    Toast.makeText(MainActivity.this, "알람이 해제됐습니다.", Toast.LENGTH_SHORT).show();
                }

            } else if (delete_view.getLeft() < CheckedColumn_x && CheckedColumn_x < delete_view.getRight()) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long db_id = currentCursor.getLong(colID);
                                        db.delAlarm("" + db_id);
                                        currentCursor = db.fetchAllAlarm();
                                        adapter.notifyDataSetChanged();
                                        Utility.cancelAlarm(MainActivity.this);
                                        Utility.startFirstAlarm(MainActivity.this);
                                    }
                                })
                        .setNegativeButton("아니요", null)
                        .show();
            }
            else{
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("수정하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long db_id = currentCursor.getLong(colID);

                                        // 최고 우선 순위 알람에만 적용됨 수정할 부분
                                        Intent intent = new Intent(MainActivity.this, SetActivity.class);
                                        intent.putExtra("id", db_id);
                                        intent.putExtra("day", currentCursor.getInt(colDAY));
                                        intent.putExtra("hour", currentCursor.getInt(colHOUR));
                                        intent.putExtra("min", currentCursor.getInt(colMINUTE));
                                        intent.putExtra("song", currentCursor.getInt(colSONG));
                                        intent.putExtra("lv1", currentCursor.getInt(colLV1));
                                        intent.putExtra("lv2", currentCursor.getInt(colLV2));
                                        intent.putExtra("lv3", currentCursor.getInt(colLV3));
                                        intent.putExtra("vib", currentCursor.getInt(colVIB));

                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("아니요", null)
                        .show();
            }

        }
    };

    ////////////////////////////////////////////////////////////////////////////////////
    //list adapter	
    ////////////////////////////////////////////////////////////////////////////////////
    private class MyCursorAdapter extends SimpleCursorAdapter {
        Context my_context;
        private int mRowLayout;

        MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to, 0);
            my_context = context;
            mRowLayout = layout;
        }

        @Override
        public int getCount() {
            return currentCursor.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            currentCursor.moveToPosition(position); ///////////////
            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) my_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(mRowLayout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.list_onoff);
                viewHolder.time = (TextView) convertView.findViewById(R.id.list_time);
                viewHolder.day = (TextView) convertView.findViewById(R.id.list_date);
                viewHolder.lv1 = (TextView) convertView.findViewById(R.id.list_lv1);
                viewHolder.lv2 = (TextView) convertView.findViewById(R.id.list_lv2);
                viewHolder.lv3 = (TextView) convertView.findViewById(R.id.list_lv3);

                //
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //
            viewHolder.time.setText(getTimeString(currentCursor.getInt(colHOUR), currentCursor.getInt(colMINUTE)));


            int day = currentCursor.getInt(colDAY);
            String strDay = "";
            if ((day & 0x01) == 0x01) {
                strDay = "일";
            }
            if ((day & 0x02) == 0x02) {
                strDay += "월";
            }
            if ((day & 0x04) == 0x04) {
                strDay += "화";
            }
            if ((day & 0x08) == 0x08) {
                strDay += "수";
            }
            if ((day & 0x10) == 0x10) {
                strDay += "목";
            }
            if ((day & 0x20) == 0x20) {
                strDay += "금";
            }
            if ((day & 0x40) == 0x40) {
                strDay += "토";
            }
            //
            viewHolder.day.setText(strDay);
            //
            int tLv1 = currentCursor.getInt(colLV1);
            int tLv2 = currentCursor.getInt(colLV2);
            int tLv3 = currentCursor.getInt(colLV3);
            viewHolder.lv1.setText("");
            viewHolder.lv2.setText("");
            viewHolder.lv3.setText("");

            if(tLv1==0){
                viewHolder.lv1.setText("흔들기");
            }
            else if(tLv1==1){
                viewHolder.lv1.setText("음성인식");
            }
            else if(tLv1==2){
                viewHolder.lv1.setText("문제풀기");
            }

            if(tLv2==0){
                viewHolder.lv2.setText("없음");
            }
            else if(tLv2==1){
                viewHolder.lv2.setText("흔들기");
            }
            else if(tLv2==2){
                viewHolder.lv2.setText("음성인식");
            }
            else if(tLv2==3){
                viewHolder.lv2.setText("문제풀기");
            }

            if(tLv3==0){
                viewHolder.lv3.setText("없음");
            }
            else if(tLv3==1){
                viewHolder.lv3.setText("흔들기");
            }
            else if(tLv3==2){
                viewHolder.lv3.setText("음성인식");
            }
            else if(tLv3==3){
                viewHolder.lv3.setText("문제풀기");
            }

            int on = currentCursor.getInt(colONOFF);

            if (on == 1) viewHolder.icon.setImageResource(R.drawable.on_icon);
            else viewHolder.icon.setImageResource(R.drawable.off_icon);
            //
            return convertView;
        }

        private class ViewHolder {
            ImageView icon;
            TextView time;
            TextView day;
            TextView lv1;
            TextView lv2;
            TextView lv3;
        };


    }

    public String getTimeString(int h, int m) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        SimpleDateFormat dayformat = new SimpleDateFormat("HH:mm");
        dayformat.setCalendar(cal);
        Date date = cal.getTime();
        return dayformat.format(date);
    }
    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


}