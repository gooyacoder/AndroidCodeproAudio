package com.ahm.codepro_audio_streaming_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Station_Database_Handler extends SQLiteOpenHelper {

    public Station_Database_Handler(Context context, String name,
                                    SQLiteDatabase.CursorFactory factory,
                                    int version){
        super(context, "myDatabase.db", factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String make_table = "create table stations_table(id int primary key, " +
                "station_url text not null, station_current_song text not null);";
        sqLiteDatabase.execSQL(make_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS  stations_table;");
        onCreate(sqLiteDatabase);
    }

    public ArrayList<String> loadStations(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from stations_table;",
                null);
        ArrayList<String> list = new ArrayList<String>();

        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String station_url = cursor.getString(1);
                list.add(station_url);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    public ArrayList<String> loadStationsNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from stations_table;",
                null);
        ArrayList<String> list = new ArrayList<String>();

        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String station_url = cursor.getString(2);
                list.add(station_url);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    public ArrayList<String> loadSelectionList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from stations_table;",
                null);
        ArrayList<String> list = new ArrayList<String>();

        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String current_song = cursor.getString(2);
                list.add(current_song);
                cursor.moveToNext();
            }
        }
        db.close();
        return list;
    }

    public long addStation(String url, String current_song){
        ContentValues values = new ContentValues();
        values.put("station_url", url);
        values.put("station_current_song", current_song);
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert("stations_table", null, values);
        db.close();
        return result;
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS  stations_table;");
        onCreate(db);
        db.close();
    }

    public void deleteItem(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from stations_table where" +
                " station_current_song = \"" + item + "\";");
        db.close();
    }

    public void addInitials() {
        addStation("http://gr.fluidstream.eu/gr1.mp3", "Giornale Radio");
        addStation("http://fm939.wnyc.org/wnycfm", "WNYC 93.9 FM");
        addStation("http://stream.srg-ssr.ch/m/rsp/mp3_128", "Radio Swiss Pop");
        addStation("http://stream.srg-ssr.ch/m/rsc_de/mp3_128", "Radio Swiss Classic");
        addStation("http://109.74.196.76:8060/stream", "Davide of MIMIC");
        addStation("http://uk7.internet-radio.com:8226/stream", "Box UK Radio");
        addStation("http://212.71.250.12:8040/stream", "Merge 104.8");
        addStation("http://uk6.internet-radio.com:8124/stream", "Party Vibe Music");
    }
}














































































