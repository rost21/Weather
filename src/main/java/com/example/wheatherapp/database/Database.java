package com.example.wheatherapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class Database extends SQLiteAssetHelper {
    private static final String DATABASE_NAMES = "sqlitedata";
    private static final int DATABASE_VERSION = 3;

    public Database(Context c) {
        super(c, DATABASE_NAMES, null, DATABASE_VERSION);
    }
}
