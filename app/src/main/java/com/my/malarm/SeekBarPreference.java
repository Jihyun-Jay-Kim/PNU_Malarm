package com.my.malarm;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.my.malarm.DefaultSet.volume_size;
import static com.my.malarm.SetActivity.Vol_firVal;

/**
 * Created by kjh on 2018. 6. 23..
 */

public class SeekBarPreference extends DialogPreference {
    private Context context;
    private SeekBar volumeLevel;
    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;/*
        volumeLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Log.d("prog?","("+progress+")");
            }
        });
*/

    }
    protected void onPrepareDialogBuilder(Builder builder) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setMinimumWidth(400);
        layout.setPadding(20, 20, 20, 20);
        volumeLevel = new SeekBar(context);
        volumeLevel.setMax(15);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            volumeLevel.setMin(1);
        }

        volumeLevel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        volumeLevel.setProgress(Vol_firVal);
        layout.addView(volumeLevel);

        builder.setView(layout);
        super.onPrepareDialogBuilder(builder);
    }
    protected void onDialogClosed(boolean positiveResult) {
        Toast.makeText(context, "음량(0~15) : "+volumeLevel.getProgress(), Toast.LENGTH_SHORT).show();
        if(positiveResult){
            persistString(volumeLevel.getProgress()+"");
            Log.d("inSeekBar",volumeLevel.getProgress()+"");
        }
        persistString(volumeLevel.getProgress()+"");
        Vol_firVal = volumeLevel.getProgress();
        volume_size = Vol_firVal;
    }

    public int getProgress(){
        return volumeLevel.getProgress();
    }
}