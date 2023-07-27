package com.ahm.codepro_audio_streaming_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class AddActivity extends AppCompatActivity {

    EditText url_text;
    EditText title_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        title_text = findViewById(R.id.title_text);
        url_text = findViewById(R.id.url_text);
    }

    public void btn_add_clicked(View view) {
        if(title_text.getText().toString().length() > 0 &&
            url_text.getText().toString().length() > 0)
        {

            String station_url = url_text.getText().toString();
            String station_title = title_text.getText().toString();
            Station_Database_Handler db = new Station_Database_Handler(
                    this, null, null, 1);
            long result;
            result = db.addStation(station_url, station_title);


            if(result == -1){
                Toast.makeText(this, "adding station failed!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("size", db.loadStations().size());
            editor.commit();
            db.close();
            this.finish();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Station title and url")
                    .setMessage("Please enter the station title and url.")
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();

        }

    }

}

