package com.ahm.codepro_audio_streaming_app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.MediaPlayer.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
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
import java.io.FileOutputStream;
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
    private String fileName;

    private LibVLC libVLC_rec;
    private org.videolan.libvlc.MediaPlayer mediaPlayer_rec;

    private static final int REQUEST_CODE_PERMISSIONS = 2;

    private String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateStationsList();
        btn = findViewById(R.id.audioStreamBtn);
        stations_btn = findViewById(R.id.stationsBtn);
        isPlaying = false;
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

        isRecording = false;

        if (allPermissionsGranted()) {
            // Permissions granted, proceed with the app
            // startApp();
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Permission is granted, proceed with writing to external storage
            } else {
                // Permission is not granted, request for permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }


        SetupPlayButton();
        SetupEqualizerSeekbars();
        RestoreEqualizerSeekbars();
        SetupStationsButton();
        //CreateRecordingsFolder();
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

//    private void CreateRecordingsFolder() {
//        File folder = new File("/sdcard/AudioRecordings");
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//    }

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



    }

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
                    mediaPlayer.setVolume(i);
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
                    int band = 0;
                    float value = (float) (-20 + (i * 0.4));
                    try{
                        equalizer.setAmp(band, value);
                        mediaPlayer.setEqualizer(equalizer);
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
                    int band = 2;
                    float value = (float) (-20 + (i * 0.4));
                    try{
                        equalizer.setAmp(band, value);
                        mediaPlayer.setEqualizer(equalizer);
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
                    int band = 4;
                    float value = (float) (-20 + (i * 0.4));
                    try{
                        equalizer.setAmp(band, value);
                        mediaPlayer.setEqualizer(equalizer);
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
                    int band = 6;
                    float value = (float) (-20 + (i * 0.4));
                    try{
                        equalizer.setAmp(band, value);
                        mediaPlayer.setEqualizer(equalizer);
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
                    int band = 8;
                    float value = (float) (-20 + (i * 0.4));
                    try{
                        equalizer.setAmp(band, value);
                        mediaPlayer.setEqualizer(equalizer);
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
                        equalizer = MediaPlayer.Equalizer.create();
                        mediaPlayer.setEqualizer(equalizer);
                        mediaPlayer.play();
                        mediaPlayer.setVolume(50);
                        volumeSeekbar.setProgress(50);
                        InitializeEqualizer();
                        btn.setText("Stop");
                        SharedPreferences prefs =
                                PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext());
                        index = prefs.getInt("index", 0);
                        getMeta();
                        isPlaying = true;

                    } else {
                        btn.setText("Play");

                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
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
            float value = (float) (-20 + (num_1 * 0.4));
            equalizer.setAmp(band, value);

            band = 2;
            num_2 = eq_2.getProgress();
            value = (float) (-20 + (num_1 * 0.4));
            equalizer.setAmp(band, value);

            band = 4;
            num_3 = eq_3.getProgress();
            value = (float) (-20 + (num_1 * 0.4));
            equalizer.setAmp(band, value);

            band = 6;
            num_4 = eq_4.getProgress();
            value = (float) (-20 + (num_1 * 0.4));
            equalizer.setAmp(band, value);

            band = 8;
            num_5 = eq_5.getProgress();
            value = (float) (-20 + (num_1 * 0.4));
            equalizer.setAmp(band, value);
            mediaPlayer.setEqualizer(equalizer);


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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if(!isRecording){
                libVLC_rec = new LibVLC(this);
                mediaPlayer_rec = new MediaPlayer(libVLC_rec);
                Media media = new Media(libVLC_rec, Uri.parse(stations.get(index)));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd-HH.m.s");
                String currentTime = dateFormat.format(new Date());
                fileName = stationsNames.get(index) + " " + currentTime + ".mp3";
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), fileName);
                String options = ":sout=#transcode{acodec=mp3,ab=128}:std{access=file,mux=raw,dst=" + file + "}";
                media.addOption(options);
                mediaPlayer_rec.setMedia(media);
                mediaPlayer_rec.play();
                Button btn = findViewById(R.id.recordBtn);
                btn.setText("Stop Rec");
                isRecording = true;
            }
            else{
                mediaPlayer_rec.stop();
                Button btn = findViewById(R.id.recordBtn);
                btn.setText("Record");
                isRecording = false;
            }


        }
        else{
            legacyCode();
        }
    }

    private void legacyCode() {
        if(!isRecording){
            libVLC_rec = new LibVLC(this);
            mediaPlayer_rec = new MediaPlayer(libVLC_rec);
            Media media = new Media(libVLC_rec, Uri.parse(stations.get(index)));
            createFileName();
            File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            String outputFilePath = outputDir.getAbsolutePath() + File.separator + fileName;
            String options = ":sout=#transcode{acodec=mp3,ab=128}:std{access=file,mux=raw,dst=" + outputFilePath + "}";
            media.addOption(options);
            mediaPlayer_rec.setMedia(media);
            mediaPlayer_rec.play();
            Button btn = findViewById(R.id.recordBtn);
            btn.setText("Stop Rec");
            isRecording = true;
        }
        else{
            mediaPlayer_rec.stop();
            Button btn = findViewById(R.id.recordBtn);
            btn.setText("Record");
            isRecording = false;
        }
    }

    private void createFileName(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        String currentTime = dateFormat.format(new Date());
        fileName = stationsNames.get(index) + " " + currentTime + ".mp3";
    }
}