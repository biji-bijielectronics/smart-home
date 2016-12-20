package com.example.alan.btapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alan on 09/12/2016.
 */

class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "energyDB";
    static final String TABLE_ENERGY = "energy";
    static final String KEY_ID = "_id";
    static final String KEY_DATE = "date";
    static final String KEY_CURRENT = "current";
    static final String KEY_VOLTAGE = "voltage";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ENERGY + "(" + KEY_ID
                + " integer primary key," + KEY_DATE + " text," + KEY_CURRENT + " text," + KEY_VOLTAGE + " text" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ENERGY);

        onCreate(db);

    }
}
