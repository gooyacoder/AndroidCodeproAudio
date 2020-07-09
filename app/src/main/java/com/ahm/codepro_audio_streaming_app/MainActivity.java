package com.ahm.codepro_audio_streaming_app;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.media.audiofx.*;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Button btn;
    private boolean isPlaying;
    private MediaPlayer mediaPlayer;
    private ArrayList<String> stations;
    private ArrayList<String> stationsNames;
    private int index;
    private short preset_index;
    private Equalizer equalizer;
    private SeekBar volumeSeekbar;
    private TextView stationView;
    private TextView equalizerView;
    private String[] equalizer_presets = {"Flat", "Pop", "Rock", "Classical",
            "Jazz", "Headphone", "News", "Dance", "Full Bass", "Full Treble"};
    private LinearLayout seekbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateStationsList();
        btn = findViewById(R.id.audioStreamBtn);
        isPlaying = false;
        equalizer = new Equalizer(1, 1);
        stationView = findViewById(R.id.station);
        equalizerView = findViewById(R.id.equalizer);
        volumeSeekbar = findViewById(R.id.volume_seekbar);
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer != null){
                    mediaPlayer.setVolume((float)i / 100.0f,
                            (float)i / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(stations.size() > 0){
                            if (!isPlaying) {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setVolume(0.5f, 0.5f);
                            volumeSeekbar.setProgress(50);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            equalizer = new Equalizer(1, mediaPlayer.getAudioSessionId());
                            equalizer.setEnabled(true);
                            btn.setText("Stop");
                            SharedPreferences prefs =
                                    PreferenceManager
                                            .getDefaultSharedPreferences(getApplicationContext());
                            index = prefs.getInt("index", 0);
                            preset_index = (short)prefs.getInt("eq_index", 0);
                            try{

                                UpdateEqualizer(preset_index);
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(final MediaPlayer mp) {
                                        mp.start();
                                    }
                                });
                                mediaPlayer.setDataSource(stations.get(index));
                                mediaPlayer.prepareAsync();
                            }catch(IOException e){
                                Toast.makeText(getApplicationContext(),e.getMessage(),
                                        Toast.LENGTH_LONG ).show();
                            }

                            isPlaying = true;

                        } else {
                            btn.setText("Play");

                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                mediaPlayer = null;
                                equalizer.release();
                                equalizer = null;
                            }

                            isPlaying = false;
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Station database is empty!", Toast.LENGTH_LONG).show();
                    }
                }

        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        seekbarLayout = findViewById(R.id.seekbarLayout);
        ViewGroup.LayoutParams params = seekbarLayout.getLayoutParams();

        if(height == 480 && width == 320){
            btn.setPadding(0,0,0,0);
            btn.setTextSize(15);
            params.width = 300;
            seekbarLayout.setLayoutParams(params);
        }else{
            params.width = 350 * (int)((float)volumeSeekbar.getContext()
                    .getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            seekbarLayout.setLayoutParams(params);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.audio_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.select:
                showSelectActivity();
                return true;
            case R.id.add:
                showAddActivity();
                return true;
            case R.id.eq:
                showEqualizerActivity();
                return true;
            case R.id.remove:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showEqualizerActivity() {
        Intent i = new Intent(this, EqualizerActivityEdited.class);
        startActivity(i);
    }

    private void showAddActivity() {
        Intent i = new Intent(this, AddActivity.class);
        startActivity(i);
    }

    private void showSelectActivity() {
        Intent i = new Intent(this, SelectActivity.class);
        startActivity(i);
    }

    private void UpdateStationsList(){
        Station_Database_Handler data_handler = new Station_Database_Handler(
                this, null, null, 1);
        stations = data_handler.loadStations();
        TextView count_view = findViewById(R.id.count_view);
        count_view.setText("There are " + stations.size() + " stations to choose from.");
    }

    private void UpdateStationsNamesList(){
        Station_Database_Handler data_handler = new Station_Database_Handler(
                this, null, null, 1);
        stationsNames = data_handler.loadStationsNames();

    }

    @Override
    protected void onResume(){
        super.onResume();
        UpdateStationsList();
        UpdateStationsNamesList();
/*        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            isPlaying = false;
            equalizer = new Equalizer(1, mediaPlayer.getAudioSessionId());
            equalizer.setEnabled(true);
        }*/
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preset_index = (short)prefs.getInt("eq_index", 0);
        equalizerView.setText(equalizer_presets[preset_index]);
        index = prefs.getInt("index", 0);
        if(stationsNames.size() > 0){
            stationView.setText(stationsNames.get(index));
        }
        if(mediaPlayer != null){
            UpdateEqualizer(preset_index);
        }

    }

    private void UpdateEqualizer(int setting_index) {
        switch(setting_index){
            case 0:
                SetEqualizerBands_001();
            return;
            case 1:
                SetEqualizerBands_002();
                return;
            case 2:
                SetEqualizerBands_003();
                return;
            case 3:
                SetEqualizerBands_004();
                return;
            case 4:
                SetEqualizerBands_005();
                return;
            case 5:
                SetEqualizerBands_006();
                return;
            case 6:
                SetEqualizerBands_007();
                return;
            case 7:
                SetEqualizerBands_008();
                return;
            case 8:
                SetEqualizerBands_009();
                return;
            case 9:
                SetEqualizerBands_010();
                return;
            default:
                return;

        }
    }

    //  Flat
    private void SetEqualizerBands_001() {
        short band = 0;
        equalizer.setBandLevel(band, (short)0);
        equalizer.setBandLevel((short)(band+1), (short)0);
        equalizer.setBandLevel((short)(band+2), (short)0);
        equalizer.setBandLevel((short)(band+3), (short)0);
        equalizer.setBandLevel((short)(band+4), (short)0);

    }

    //  Pop
    private void SetEqualizerBands_002() {
        short band = 0;
        equalizer.setBandLevel(band, (short)150);
        equalizer.setBandLevel((short)(band+1), (short)250);
        equalizer.setBandLevel((short)(band+2), (short)500);
        equalizer.setBandLevel((short)(band+3), (short)250);
        equalizer.setBandLevel((short)(band+4), (short)150);

    }

    //  Rock
    private void SetEqualizerBands_003() {
        short band = 0;
        equalizer.setBandLevel(band, (short)800);
        equalizer.setBandLevel((short)(band+1), (short)-1000);
        equalizer.setBandLevel((short)(band+2), (short)0);
        equalizer.setBandLevel((short)(band+3), (short)0);
        equalizer.setBandLevel((short)(band+4), (short)1000);

    }

    //  Classcial
    private void SetEqualizerBands_004() {
        short band = 0;
        equalizer.setBandLevel(band, (short)500);
        equalizer.setBandLevel((short)(band+1), (short)500);
        equalizer.setBandLevel((short)(band+2), (short)500);
        equalizer.setBandLevel((short)(band+3), (short)-500);
        equalizer.setBandLevel((short)(band+4), (short)-500);

    }

    //  Jazz
    private void SetEqualizerBands_005() {
        short band = 0;
        equalizer.setBandLevel(band, (short)600);
        equalizer.setBandLevel((short)(band+1), (short)200);
        equalizer.setBandLevel((short)(band+2), (short)400);
        equalizer.setBandLevel((short)(band+3), (short)200);
        equalizer.setBandLevel((short)(band+4), (short)300);

    }

    //  Headphone
    private void SetEqualizerBands_006() {
        short band = 0;
        equalizer.setBandLevel(band, (short)700);
        equalizer.setBandLevel((short)(band+1), (short)-500);
        equalizer.setBandLevel((short)(band+2), (short)-500);
        equalizer.setBandLevel((short)(band+3), (short)-500);
        equalizer.setBandLevel((short)(band+4), (short)1000);

    }

    //  News
    private void SetEqualizerBands_007() {
        short band = 0;
        equalizer.setBandLevel(band, (short)0);
        equalizer.setBandLevel((short)(band+1), (short)700);
        equalizer.setBandLevel((short)(band+2), (short)700);
        equalizer.setBandLevel((short)(band+3), (short)250);
        equalizer.setBandLevel((short)(band+4), (short)250);
    }

    //  Dance
    private void SetEqualizerBands_008() {
        short band = 0;
        equalizer.setBandLevel(band, (short)300);
        equalizer.setBandLevel((short)(band+1), (short)700);
        equalizer.setBandLevel((short)(band+2), (short)-300);
        equalizer.setBandLevel((short)(band+3), (short)0);
        equalizer.setBandLevel((short)(band+4), (short)800);
    }

    //  Full Bass
    private void SetEqualizerBands_009() {
        short band = 0;
        equalizer.setBandLevel(band, (short)800);
        equalizer.setBandLevel((short)(band+1), (short)800);
        equalizer.setBandLevel((short)(band+2), (short)0);
        equalizer.setBandLevel((short)(band+3), (short)-800);
        equalizer.setBandLevel((short)(band+4), (short)-1000);
    }

    //  Full Treble
    private void SetEqualizerBands_010() {
        short band = 0;
        equalizer.setBandLevel(band, (short)-1000);
        equalizer.setBandLevel((short)(band+1), (short)-500);
        equalizer.setBandLevel((short)(band+2), (short)0);
        equalizer.setBandLevel((short)(band+3), (short)500);
        equalizer.setBandLevel((short)(band+4), (short)1000);
    }


}