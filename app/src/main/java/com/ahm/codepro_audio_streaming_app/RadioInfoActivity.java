package com.ahm.codepro_audio_streaming_app;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class RadioInfoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio_info);
        TextView songTitle = findViewById(R.id.song_title);
        TextView songArtist = findViewById(R.id.song_artist);
        songArtist.setText(getIntent().getStringExtra("songArtist"));
        songTitle.setText(getIntent().getStringExtra("songTitle"));

    }

    public void return_clicked(View view) {
        finish();
    }
}
