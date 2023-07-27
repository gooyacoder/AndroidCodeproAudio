package com.ahm.codepro_audio_streaming_app;

import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class EqualizerActivityEdited extends AppCompatActivity {

    //private Equalizer eq;
    private RadioButton r1;
    private RadioButton r2;
    private RadioButton r3;
    private RadioButton r4;
    private RadioButton r5;
    private RadioButton r6;
    private RadioButton r7;
    private RadioButton r8;
    private RadioButton r9;
    private RadioButton r10;
    private int preset_index;
    public final String Preferene = "Equalizer";
    private RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);

        //eq = new Equalizer(0, new MediaPlayer().getAudioSessionId());
        preset_index = 0;

        /*String[] presets = new String[10];
        for(short i = 0; i < 10; ++i){
            presets[i] = eq.getPresetName(i);
        }*/
        r1 = findViewById(R.id.radioButton1);
        r2 = findViewById(R.id.radioButton2);
        r3 = findViewById(R.id.radioButton3);
        r4 = findViewById(R.id.radioButton4);
        r5 = findViewById(R.id.radioButton5);
        r6 = findViewById(R.id.radioButton6);
        r7 = findViewById(R.id.radioButton7);
        r8 = findViewById(R.id.radioButton8);
        r9 = findViewById(R.id.radioButton9);
        r10 = findViewById(R.id.radioButton10);
        radioGroup = findViewById(R.id.radiogroup);

        r1.setText("Flat");
        r2.setText("Pop");
        r3.setText("Rock");
        r4.setText("Classical");
        r5.setText("Jazz");
        r6.setText("Headphone");
        r7.setText("News");
        r8.setText("Dance");
        r9.setText("Full Bass");
        r10.setText("Full Treble");

        getSettings();
        SelectSetting();

    }

    public void r1_clicked(View view) {
        preset_index = 0;
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        r6.setChecked(false);
        r7.setChecked(false);
        r8.setChecked(false);
        r9.setChecked(false);
        r10.setChecked(false);

    }

    public void r2_clicked(View view) {
        preset_index = 1;
        r1.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        r6.setChecked(false);
        r7.setChecked(false);
        r8.setChecked(false);
        r9.setChecked(false);
        r10.setChecked(false);
    }

    public void r3_clicked(View view) {
        preset_index = 2;
        r2.setChecked(false);
        r1.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        r6.setChecked(false);
        r7.setChecked(false);
        r8.setChecked(false);
        r9.setChecked(false);
        r10.setChecked(false);
    }

    public void r4_clicked(View view) {
        preset_index = 3;
        r2.setChecked(false);
        r3.setChecked(false);
        r1.setChecked(false);
        r5.setChecked(false);
        r6.setChecked(false);
        r7.setChecked(false);
        r8.setChecked(false);
        r9.setChecked(false);
        r10.setChecked(false);
    }

    public void r5_clicked(View view) {
        preset_index = 4;
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r1.setChecked(false);
        r6.setChecked(false);
        r7.setChecked(false);
        r8.setChecked(false);
        r9.setChecked(false);
        r10.setChecked(false);
    }

    public void r6_clicked(View view) {
        preset_index = 5;
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        r1.setChecked(false);
        r7.setChecked(false);
        r8.setChecked(false);
        r9.setChecked(false);
        r10.setChecked(false);
    }

    public void r7_clicked(View view) {
        preset_index = 6;
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        r6.setChecked(false);
        r1.setChecked(false);
        r8.setChecked(false);
        r9.setChecked(false);
        r10.setChecked(false);
    }

    public void r8_clicked(View view) {
        preset_index = 7;
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        r6.setChecked(false);
        r7.setChecked(false);
        r1.setChecked(false);
        r9.setChecked(false);
        r10.setChecked(false);
    }

    public void r9_clicked(View view) {
        preset_index = 8;
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        r6.setChecked(false);
        r7.setChecked(false);
        r8.setChecked(false);
        r1.setChecked(false);
        r10.setChecked(false);
    }

    public void r10_clicked(View view) {
        preset_index = 9;
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        r6.setChecked(false);
        r7.setChecked(false);
        r8.setChecked(false);
        r9.setChecked(false);
        r1.setChecked(false);
    }

    public void btn_eq_select_clicked(View view) {
        SharedPreferences prefs_001 =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs_001.edit();
        editor.putInt("eq_index", preset_index);
        editor.commit();
        this.finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        putSettings();
    }

    private void putSettings(){
        SharedPreferences.Editor preferences = getSharedPreferences(Preferene, 0).edit();
        preferences.putInt("selection", preset_index);
        preferences.commit();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        getSettings();
        SelectSetting();
    }

    @Override
    protected void onStart(){
        super.onStart();
        getSettings();
        SelectSetting();
    }

    private void getSettings(){
        SharedPreferences preferences = getSharedPreferences(Preferene, 0);
        preset_index = preferences.getInt("selection", 0);
    }

    private void SelectSetting() {
        switch(preset_index){
            case 0:
                radioGroup.check(R.id.radioButton1);
                break;
            case 1:
                radioGroup.check(R.id.radioButton2);
                break;
            case 2:
                radioGroup.check(R.id.radioButton3);
                break;
            case 3:
                radioGroup.check(R.id.radioButton4);
                break;
            case 4:
                radioGroup.check(R.id.radioButton5);
                break;
            case 5:
                radioGroup.check(R.id.radioButton6);
                break;
            case 6:
                radioGroup.check(R.id.radioButton7);
                break;
            case 7:
                radioGroup.check(R.id.radioButton8);
                break;
            case 8:
                radioGroup.check(R.id.radioButton9);
                break;
            case 9:
                radioGroup.check(R.id.radioButton10);
                break;
            default:
                radioGroup.check(R.id.radioButton1);
                break;
        }
    }
}

