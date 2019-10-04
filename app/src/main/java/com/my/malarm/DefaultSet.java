package com.my.malarm;

import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import static com.my.malarm.AlarmReceiver.mNotifi;
import static com.my.malarm.SetActivity.def_shk;
import static com.my.malarm.SetActivity.def_sol;
import static com.my.malarm.SetActivity.def_voi;

/**
 * Created by kjh on 2018. 6. 10..
 */

public class DefaultSet extends PreferenceActivity implements OnPreferenceChangeListener {

    private PreferenceScreen mScreen;
    private EditTextPreference mVoice_key;
    private EditTextPreference mShake_count;
    private EditTextPreference mSolve_level;
    private ListPreference mVibe_sel;
    private EditTextPreference mNotification;
    private SeekBarPreference mVolume_bar;

    public static int vibe_freq;
    public static int volume_size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.def_set);

        mScreen = getPreferenceScreen();

        mVoice_key = (EditTextPreference)mScreen.findPreference("voice_key");
        mShake_count = (EditTextPreference)mScreen.findPreference("shake_count");
        mSolve_level = (EditTextPreference)mScreen.findPreference("solve_lev");

        mVibe_sel = (ListPreference)mScreen.findPreference("vibeSelect");
        mNotification = (EditTextPreference)mScreen.findPreference("notification");
        mVolume_bar = (SeekBarPreference) mScreen.findPreference("volumnBar");

        mVoice_key.setOnPreferenceChangeListener(this);
        mShake_count.setOnPreferenceChangeListener(this);
        mSolve_level.setOnPreferenceChangeListener(this);
        mVibe_sel.setOnPreferenceChangeListener(this);
        mNotification.setOnPreferenceChangeListener(this);
        //mVolume_bar.setOnPreferenceChangeListener(this);

    }

    @Override
    public void onResume(){
        super.onResume();
        updateSummary();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = (String) newValue;
        if(preference == mVoice_key){
            mVoice_key.setSummary(value);
            def_voi = value;
        }else if(preference == mShake_count){
            mShake_count.setSummary(value);
            def_shk = value;
        }else if(preference == mSolve_level){
            mSolve_level.setSummary(value);
            def_sol = value;
        }else if(preference == mNotification){
            mNotification.setSummary(value);
            mNotifi = value;
        }else if(preference == mVibe_sel){
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            mVibe_sel.setSummary(index >= 0 ? listPreference.getEntries()[index]
                    : null);
            vibe_freq = index;
        }else if(preference == mVolume_bar){
            SeekBarPreference seekBarPreference = (SeekBarPreference)preference;
            //mVolume_bar.setSummary(mVolume_bar.getProgress());
        }
        return true;
    }
    private void updateSummary(){
//액티비티 실행 할 때 저장되어있는 summary값을 set
//안 하면 안 뜸
        mVoice_key.setSummary(mVoice_key.getText());
        mShake_count.setSummary(mShake_count.getText());
        mSolve_level.setSummary(mSolve_level.getText());
        mNotification.setSummary(mNotification.getText());

        mVibe_sel.setSummary(mVibe_sel.getEntry());
        //mVolume_bar.setSummary(mVolume_bar.getProgress());
    }

}
