package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper

{
    private Context mcontext;
    public static final String TABLE_NAME="book";
    public static final String TITLE="title";
    public static final String ID="id";
    public static final String PHOTO="photo";
    public static final String CONTENT="content";
    public static final String TIME="time";


    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mcontext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +TITLE+" TEXT NOT NULL, "+CONTENT+" TEXT, "+TIME+" TEXT NOT NULL, "+PHOTO+" TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);

    }
}
