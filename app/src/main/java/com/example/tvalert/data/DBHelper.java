package com.example.tvalert.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tvalert.data.TVContract.SeriesEntry;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alerts.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + SeriesEntry.TABLE_NAME + "("
                + SeriesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SeriesEntry.COLUMN_SERIES_NAME + " TEXT NOT NULL, "
                + SeriesEntry.COLUMN_SERIES_ID + " INT NOT NULL UNIQUE, "
                + SeriesEntry.COLUMN_SEASON + " INT, "
                + SeriesEntry.COLUMN_LAST_EPISODE_DATE + " DATE, "
                + SeriesEntry.COLUMN_COLUMN_LAST_EPISODE_NUMBER + " INT, "
                + SeriesEntry.COLUMN_LAST_EPISODE_SEEN + " BIT DEFAULT 0);";
        Log.v("dbHelper", SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}


