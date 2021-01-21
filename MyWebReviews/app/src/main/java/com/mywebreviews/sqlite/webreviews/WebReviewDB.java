package com.mywebreviews.sqlite.webreviews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mywebreviews.WebReview;

public class WebReviewDB {
    private static final int VERSION_BDD = 1;
    private static final String BDD_NAME = "web_reviews.db";

    private static final String WEB_REVIEWS_TABLE = "web_reviews";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_DOMAIN = "domain";
    private static final int NUM_COL_DOMAIN = 1;
    private static final String COL_SCORE = "score";
    private static final int NUM_COL_SCORE = 2;
    private static final String COL_NOTES = "notes";
    private static final int NUM_COL_NOTES = 3;

    private SQLiteDatabase bdd;

    private SQLiteManager SQLiteDB;

    public WebReviewDB(Context context){
        SQLiteDB = new SQLiteManager(context, BDD_NAME, null, VERSION_BDD);
    }

    public void open(){
        bdd = SQLiteDB.getWritableDatabase();
    }

    public void close(){
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insert(WebReview webReview){
        ContentValues values = new ContentValues();
        values.put(COL_DOMAIN, webReview.getDomain());
        values.put(COL_SCORE, webReview.getScore());
        values.put(COL_NOTES, webReview.getNotes());
        return bdd.insert(WEB_REVIEWS_TABLE, null, values);
    }

    public int update(int id, WebReview webReview){
        ContentValues values = new ContentValues();
        values.put(COL_DOMAIN, webReview.getDomain());
        values.put(COL_SCORE, webReview.getScore());
        values.put(COL_NOTES, webReview.getNotes());
        return bdd.update(WEB_REVIEWS_TABLE, values, COL_ID + " = " +id, null);
    }

    public int removeWithID(int id){
        return bdd.delete(WEB_REVIEWS_TABLE, COL_ID + " = " +id, null);
    }

    public WebReview getByDomain(String domain){
        Cursor c = bdd.query(WEB_REVIEWS_TABLE, new String[] {COL_ID, COL_DOMAIN, COL_SCORE, COL_NOTES},
                COL_DOMAIN + " LIKE \"" + domain +"\"",
                null, null, null, null
        );
        return cursorToObject(c);
    }

    private WebReview cursorToObject(Cursor c){
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();

        WebReview webReview = new WebReview();

        webReview.setDBId(c.getInt(NUM_COL_ID));
        webReview.setDomain(c.getString(NUM_COL_DOMAIN));
        webReview.setScore(c.getInt(NUM_COL_SCORE));
        webReview.setNotes(c.getString(NUM_COL_NOTES));
        c.close();

        return webReview;
    }
}