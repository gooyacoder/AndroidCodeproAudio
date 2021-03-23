package com.ahm.codepro_audio_streaming_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private Button btn;
    private Button stations_btn;
    private boolean isPlaying;
    private MediaPlayer mediaPlayer;
    private ArrayList<String> stations;
    private ArrayList<String> stationsNames;
    private int index;
    private Equalizer equalizer;
    private SeekBar volumeSeekbar;
    private SeekBar eq_1, eq_2, eq_3, eq_4, eq_5;
    private int num_1, num_2, num_3, num_4, num_5;
    private TextView stationView;
    private String[] equalizer_presets = {"Flat", "Pop", "Rock", "Classical",
            "Jazz", "Headphone", "News", "Dance", "Full Bass", "Full Treble"};
    private LinearLayout seekbarLayout;


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

        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume((float) i / 100.0f,
                            (float) i / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        seekbarLayout = findViewById(R.id.seekbarLayout);
        ViewGroup.LayoutParams params = seekbarLayout.getLayoutParams();

        LinearLayout imgLayout = findViewById(R.id.image_layout);

        if (height == 480 && width == 320) {
            btn.setPadding(0, 0, 0, 0);
            //btn.setTextSize(15);
            params.width = 300;
            seekbarLayout.setLayoutParams(params);

        } else {
            params.width = 350 * (int) ((float) volumeSeekbar.getContext()
                    .getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            seekbarLayout.setLayoutParams(params);
        }

        eq_1 = findViewById(R.id.eq_1);
        //eq_1.setMax(3000);
        eq_2 = findViewById(R.id.eq_2);
        //eq_2.setMax(3000);
        eq_3 = findViewById(R.id.eq_3);
        //eq_3.setMax(3000);
        eq_4 = findViewById(R.id.eq_4);
        //eq_4.setMax(3000);
        eq_5 = findViewById(R.id.eq_5);
        //eq_5.setMax(3000);

        SetupEqualizerSeekbars();
        SetupPlayButton();
        SetupStationsButton();


//        short[] levels = equalizer.getBandLevelRange();
//        Toast.makeText(getApplicationContext(), levels[0] + " " + levels[1], Toast.LENGTH_LONG).show();

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
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.add:
                showAddActivity();
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
                    equalizer.setBandLevel(band, value);

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
                    equalizer.setBandLevel(band, value);

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
                    equalizer.setBandLevel(band, value);

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
                    equalizer.setBandLevel(band, value);

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
                    equalizer.setBandLevel(band, value);

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
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setVolume(0.5f, 0.5f);
                        volumeSeekbar.setProgress(50);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        equalizer = new Equalizer(1, mediaPlayer.getAudioSessionId());
                        equalizer.setEnabled(true);
                        InitializeEqualizer();
                        btn.setText("Stop");
                        SharedPreferences prefs =
                                PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext());
                        index = prefs.getInt("index", 0);

                        try {


                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(final MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                            mediaPlayer.setDataSource(stations.get(index));
                            mediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    IcyStreamMeta icy = new IcyStreamMeta();
                    icy.setStreamUrl(new URL(stations.get(index)));
                    if (!icy.isError()) {
                        final String title = icy.getTitle();
                        final TextView song_title = findViewById(R.id.song_title);
                        final String artist = icy.getArtist();
                        final TextView song_artist = findViewById(R.id.song_artist);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (artist != null) {
                                    song_artist.setText(artist);
                                }
                                if (title != null) {
                                    song_title.setText(title);
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

}