package android.csulb.edu.fitnessapp;

/**
 * Created by metehan on 11/25/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class TrackDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TrackDb";
    public static final String TABLE_NAME = "tracks";
    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String TRANSPORTATION = "transportation";
    public static final String DISTANCE = "distance";
    public static final String CALORIES = "calories";
    public static final String COORDINATES = "coordinates";
    public static final String TIME = "time";

    private HashMap hp;

    public TrackDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table tracks " +
                        "(id integer primary key, date text, coordinates text, " +
                        "transportation text, distance text, calories text, time text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS tracks");
        onCreate(db);
    }

    public boolean insertTrack(String date, String coordinates, String transportation,
                               String distance, String calories, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("coordinates", coordinates);
        contentValues.put("transportation", transportation);
        contentValues.put("distance", distance);
        contentValues.put("calories", calories);
        contentValues.put("time", time);
        db.insert("tracks", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tracks where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateTrack(Integer id, String date, String coordinates, String transportation,
                               String distance, String calories, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("coordinates", coordinates);
        contentValues.put("transportation", transportation);
        contentValues.put("distance", distance);
        contentValues.put("calories", calories);
        contentValues.put("time", time);
        db.update("tracks", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean deleteTrack(String rowId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(TABLE_NAME, ID + "=" + rowId, null) > 0;
    }

    public TreeMap<String, Integer> getAllTracks() {
        ArrayList<String> array_list = new ArrayList<String>();

        TreeMap<String, Integer> allTracks = new TreeMap<>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tracks", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            allTracks.put(res.getString(res.getColumnIndex(DATE)) + " #" +
                    (res.getInt(res.getColumnIndex(ID))), (res.getInt(res.getColumnIndex(ID))));
            res.moveToNext();
        }
        return allTracks;
    }
}

