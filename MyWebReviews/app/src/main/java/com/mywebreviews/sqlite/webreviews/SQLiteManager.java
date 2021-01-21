package com.mywebreviews.sqlite.webreviews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteManager extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_REVIEWS = "CREATE TABLE web_reviews (id INTEGER PRIMARY " +
            "KEY AUTOINCREMENT, domain TEXT NOT NULL, score INTEGER NOT NULL, notes TEXT);";
    private static final String DROP_TABLE_REVIEW = "DROP TABLE web_review;";

    public SQLiteManager(Context context, String name, CursorFactory cursorFactory, int version){
        super(context, name, cursorFactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_REVIEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DROP_TABLE_REVIEW);
        onCreate(db);
    }
}
