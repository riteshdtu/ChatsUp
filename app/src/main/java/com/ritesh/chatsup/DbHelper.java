package com.ritesh.chatsup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ritesh on 3/27/2015.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chatsup.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users (_id integer primary key autoincrement, contact text unique, name text, status integer default 0, msg_count integer default 0);");
        db.execSQL("create table all_msgs (_id integer primary key autoincrement, contact text, msg text, time long,received integer, status integer default 0);");
        db.execSQL("create table pending_msgs (_id integer primary key autoincrement, contact text, msg text);");//, all_msgs_id integer, FOREIGN KEY (all_msgs_id) REFERENCES all_msgs(_id));");
//        db.execSQL("insert into all_msgs values(1,'9871412029','hello aashisha',CURRENT_TIMESTAMP,0,0);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS pending_msgs");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS all_msgs");
        onCreate(sqLiteDatabase);
    }

}
