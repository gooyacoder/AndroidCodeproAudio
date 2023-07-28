package com.ahm.codepro_audio_streaming_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
// import android.media.MediaPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.ILibVLC;
import org.videolan.libvlc.interfaces.IMedia;

import android.media.MediaRecorder;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    static final int add = R.id.add;
    static final int remove = R.id.remove;

    private Button btn;
    private Button stations_btn;
    private boolean isPlaying;
    private boolean isRecording;
    private MediaPlayer mediaPlayer;
    private ArrayList<String> stations;
    private ArrayList<String> stationsNames;
    private int index;
    private Equalizer equalizer;
    private SeekBar volumeSeekbar;
    private SeekBar eq_1, eq_2, eq_3, eq_4, eq_5;
    private int num_1, num_2, num_3, num_4, num_5;
    private TextView stationView;;
    private LinearLayout seekbarLayout;
    private Timer timer;
    private String songTitle, songArtist;
    private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private MediaRecorder recorder;

    private static final int REQUEST_CODE_PERMISSIONS = 2;

    private String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateStationsList();
        btn = findViewById(R.id.audioStreamBtn);
        stations_btn = findViewById(R.id.stationsBtn);
        isPlaying = false;
        equalizer = new Equalizer(1, 1);
        stationView = findViewById(R.id.station);
        volumeSeekbar = findViewById(R.id.volume_seekbar);
        SetupVolumeSeekbar();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        seekbarLayout = findViewById(R.id.seekbarLayout);
        ViewGroup.LayoutParams params = seekbarLayout.getLayoutParams();
        LinearLayout imgLayout = findViewById(R.id.image_layout);
        if (height == 480 && width == 320) {
            btn.setPadding(0, 0, 0, 0);
            params.width = 300;
            seekbarLayout.setLayoutParams(params);

        } else {
            params.width = 350 * (int) ((float) volumeSeekbar.getContext()
                    .getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            seekbarLayout.setLayoutParams(params);
        }
        eq_1 = findViewById(R.id.eq_1);
        eq_2 = findViewById(R.id.eq_2);
        eq_3 = findViewById(R.id.eq_3);
        eq_4 = findViewById(R.id.eq_4);
        eq_5 = findViewById(R.id.eq_5);

        recorder = new MediaRecorder();
        isRecording = false;

        if (allPermissionsGranted()) {
            // Permissions granted, proceed with the app
            // startApp();
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        SetupPlayButton();
        SetupEqualizerSeekbars();
        RestoreEqualizerSeekbars();
        SetupStationsButton();
        CreateRecordingsFolder();
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void CreateRecordingsFolder() {
        File folder = new File("/sdcard/AudioRecordings");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private void GetPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);

        } else {

            // startRecording();

        }

        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not yet granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // Permission is already granted, proceed with writing to external storage
            // writeToFileOnExternalStorage();
        }

    }

    private void Record() throws IOException {

        if(!isRecording){
            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(128000);
            recorder.setAudioSamplingRate(44100);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH_mm");
            String currentTime = dateFormat.format(new Date());
            recorder.setOutputFile("/sdcard/AudioRecordings/" + stationsNames.get(index) + "_" + currentTime + ".mp3");
            recorder.prepare();
            recorder.start();
            Button btn = findViewById(R.id.recordBtn);
            btn.setText("Stop Rec");
            isRecording = true;
        }
        else{
            recorder.stop();
            Button btn = findViewById(R.id.recordBtn);
            btn.setText("Record");
            isRecording = false;
        }

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                // Permissions granted, proceed with the app
//                // startApp();
//            } else {
//                // Permissions denied
//                // Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
//                // finish();
//            }
//        }
//    }
    private void RestoreEqualizerSeekbars() {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int mProgress = mSharedPrefs.getInt("band_0", 50);
        eq_1.setProgress(mProgress);
        mProgress = mSharedPrefs.getInt("band_1", 50);
        eq_2.setProgress(mProgress);
        mProgress = mSharedPrefs.getInt("band_2", 50);
        eq_3.setProgress(mProgress);
        mProgress = mSharedPrefs.getInt("band_3", 50);
        eq_4.setProgress(mProgress);
        mProgress = mSharedPrefs.getInt("band_4", 50);
        eq_5.setProgress(mProgress);
    }

    private void SetupVolumeSeekbar() {
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null) {
//                    mediaPlayer.setVolume((float) i / 100.0f,
//                            (float) i / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void SetupStationsButton() {
        stations_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectActivity();
            }
        });
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

        /* Handle item selection
        switch (item.getItemId()) {

            case add:
                showAddActivity();
                return true;

            case remove:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } */

        if(item.getItemId() == add){
            showAddActivity();
            return true;
        }
        else if(item.getItemId() == remove){
            this.finish();
            return true;
        }
        else{
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

    private void UpdateStationsList() {
        Station_Database_Handler data_handler = new Station_Database_Handler(
                this, null, null, 1);
        stations = data_handler.loadStations();
        TextView count_view = findViewById(R.id.count_view);
        if (stations.size() == 0) {
            AddStations();
        }
        count_view.setText("There are " + stations.size() + " stations to choose from.");
    }

    private void AddStations() {
        Station_Database_Handler data_handler = new Station_Database_Handler(
                this, null, null, 1);
        data_handler.addInitials();
    }

    private void UpdateStationsNamesList() {
        Station_Database_Handler data_handler = new Station_Database_Handler(
                this, null, null, 1);
        stationsNames = data_handler.loadStationsNames();

    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateStationsList();
        UpdateStationsNamesList();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        index = prefs.getInt("index", 0);
        if (stationsNames.size() > 0) {
            if(index == stationsNames.size()){
                index--;
            }
            stationView.setText(stationsNames.get(index));
        }


    }


    //  Equalizer SeekBar onSeekbarChanged eventHandlers

    private void SetupEqualizerSeekbars() {
        eq_1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (equalizer != null) {
                    short band = 0;
                    short value = (short) (-1500 + (i * 30));
                    try{
                        equalizer.setBandLevel(band, value);
                        int mProgress = i;
                        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                        mEditor.putInt("band_0", mProgress).commit();
                    }
                    catch(Exception exception){

                    }


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        eq_2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (equalizer != null) {
                    short band = 1;
                    short value = (short) (-1500 + (i * 30));
                    try{
                        equalizer.setBandLevel(band, value);
                        int mProgress = i;
                        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                        mEditor.putInt("band_1", mProgress).commit();
                    }
                    catch(Exception exception){

                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        eq_3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (equalizer != null) {
                    short band = 2;
                    short value = (short) (-1500 + (i * 30));
                    try{
                        equalizer.setBandLevel(band, value);
                        int mProgress = i;
                        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                        mEditor.putInt("band_2", mProgress).commit();
                    }
                    catch(Exception exception){

                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        eq_4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (equalizer != null) {
                    short band = 3;
                    short value = (short) (-1500 + (i * 30));
                    try{
                        equalizer.setBandLevel(band, value);
                        int mProgress = i;
                        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                        mEditor.putInt("band_3", mProgress).commit();
                    }
                    catch(Exception exception){

                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        eq_5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (equalizer != null) {
                    short band = 4;
                    short value = (short) (-1500 + (i * 30));
                    try{
                        equalizer.setBandLevel(band, value);
                        int mProgress = i;
                        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                        mEditor.putInt("band_4", mProgress).commit();
                    }
                    catch(Exception exception){

                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void SetupPlayButton() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stations.size() > 0) {
                    if (!isPlaying) {
                        LibVLC libVLC = new LibVLC(getApplicationContext());
                        mediaPlayer = new MediaPlayer(libVLC);
                        Media media = new Media(libVLC, Uri.parse(stations.get(index)));
                        mediaPlayer.setMedia(media);
                        mediaPlayer.play();
                        //mediaPlayer.setVolume(0.5f, 0.5f);
                        volumeSeekbar.setProgress(50);
                        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        //equalizer = new Equalizer(1, mediaPlayer.getAudioSessionId());
                        equalizer.setEnabled(true);
                        InitializeEqualizer();
                        btn.setText("Stop");
                        SharedPreferences prefs =
                                PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext());
                        index = prefs.getInt("index", 0);

//                        try {
//
//
//                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                @Override
//                                public void onPrepared(final MediaPlayer mp) {
//                                    mp.start();
//                                }
//                            });
//                            mediaPlayer.setDataSource(stations.get(index));
//                            mediaPlayer.prepareAsync();
//                        } catch (IOException e) {
//                            Toast.makeText(getApplicationContext(), e.getMessage(),
//                                    Toast.LENGTH_LONG).show();
//                        }
                        getMeta();
                        isPlaying = true;

                    } else {
                        btn.setText("Play");

                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            equalizer.release();
                            equalizer = null;
                            timer.cancel();
                            timer = null;
                        }

                        isPlaying = false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Station database is empty!", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void InitializeEqualizer() {
        if (equalizer != null) {
            short band = 0;
            num_1 = eq_1.getProgress();
            short value = (short) (-1500 + (num_1 * 30));
            equalizer.setBandLevel(band, value);

            band = 1;
            num_2 = eq_2.getProgress();
            value = (short) (-1500 + (num_2 * 30));
            equalizer.setBandLevel(band, value);

            band = 2;
            num_3 = eq_3.getProgress();
            value = (short) (-1500 + (num_3 * 30));
            equalizer.setBandLevel(band, value);

            band = 3;
            num_4 = eq_4.getProgress();
            value = (short) (-1500 + (num_3 * 30));
            equalizer.setBandLevel(band, value);

            band = 4;
            num_5 = eq_5.getProgress();
            value = (short) (-1500 + (num_4 * 30));
            equalizer.setBandLevel(band, value);

        }
    }

    private void getMeta() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    IcyStreamMeta icy = new IcyStreamMeta();
                    icy.setStreamUrl(new URL(stations.get(index)));
                    if (!icy.isError()) {
                        final String title = icy.getTitle();
                        final String artist = icy.getArtist();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (artist != null) {
                                    songArtist = artist;
                                }
                                if (title != null) {
                                    songTitle = title;
                                }
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    public void btn_reset_clicked(View view) {
        eq_1.setProgress(50);
        eq_2.setProgress(50);
        eq_3.setProgress(50);
        eq_4.setProgress(50);
        eq_5.setProgress(50);
    }

    public void radio_info_btn_clicked(View view) {
        Intent intent = new Intent(this, RadioInfoActivity.class);
        intent.putExtra("songTitle", songTitle);
        intent.putExtra("songArtist", songArtist);
        startActivity(intent);
    }

    public void btn_record_clicked(View view) throws IOException {
        Record();
    }
}