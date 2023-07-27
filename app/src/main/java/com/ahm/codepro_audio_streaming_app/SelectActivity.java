package com.ahm.codepro_audio_streaming_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectActivity extends AppCompatActivity {

    ListView list;
    int index;
    Switch editSwitch;
    Button deleteButton;
    Button clearButton;
    Button selectButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

        Station_Database_Handler db = new Station_Database_Handler(getApplicationContext(),
                null, null, 1);
        ArrayList<String> list_of_stations = db.loadSelectionList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice, list_of_stations);
        list = findViewById(R.id.stations_listview);
        list.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                index = arg2;
            }
        });
        list.setAdapter(adapter);
        list.setAdapter(new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_single_choice, list_of_stations) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(getApplicationContext().getResources()
                        .getColor(R.color.List_fg));
                textView.setBackgroundColor(getApplicationContext().getResources()
                        .getColor(R.color.List_bg));
                textView.setTextSize(17);

                return textView;
            }
        });

        int size = db.loadStations().size();
        selectButton = findViewById(R.id.search_btn);
        editSwitch = findViewById(R.id.edit_switch);
        if(size > 0){
            selectButton.setEnabled(true);
            editSwitch.setEnabled(true);
        }else{
            selectButton.setEnabled(false);
            editSwitch.setEnabled(false);
        }


        clearButton = findViewById(R.id.clear_database);
        deleteButton = findViewById(R.id.delete);

        editSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    clearButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
                else{
                    clearButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

    }

    public void btn_select_clicked(View view) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("index", index);
        editor.commit();
        this.finish();
    }

    public void btn_delete_clicked(View view) {
        Station_Database_Handler db = new Station_Database_Handler(getApplicationContext()
                , null, null, 1);
        ArrayList<String> list = db.loadSelectionList();
        String item = list.get(index);
        db.deleteItem(item);
        db.close();
        this.finish();
    }

    public void btn_clear_clicked(View view) {
        Station_Database_Handler db = new Station_Database_Handler(getApplicationContext()
            , null, null, 1);
        db.clear();
        db.close();
        this.finish();
    }

}
