package com.princedragons.geoexpert;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoreDatabase extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "GeoExpertOfflineList";

    // Cache table name
    private static final String TABLE_NAME = "firstList";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_SCORE = "score";

    public ScoreDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
                + KEY_SCORE + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    /*public void setList(ArrayList<Score> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            addItem(list.get(i));
        }
    }*/

    // Adding new item
    public void addScore(Score item)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, item.date);
        values.put(KEY_SCORE, item.score);

        // Inserting Row
        long res = db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Items
    public ArrayList<Score> getAllScores()
    {
        ArrayList<Score> contactList = new ArrayList<Score>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + KEY_SCORE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Score score = new Score();
                score.date = cursor.getString(1);
                score.score = cursor.getInt(2);
                // Adding contact to list
                contactList.add(score);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single item
    /*public int updateContact(Score item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getText());
        values.put(KEY_CHECKED, item.getChecked());

        String cond = KEY_NAME + " = '" + String.valueOf(item.getText()) + "'";

        // updating row
        int rval = db.update(TABLE_NAME, values, cond , null );
        return ( rval );
    }

    // Deleting single item
    public void deleteContact(Score item) {
        SQLiteDatabase db = this.getWritableDatabase();
        String cond = KEY_DATE + " = '" + String.valueOf(item.date) + "'";
        db.delete(TABLE_NAME, cond,  null);
        db.close();
    }*/

    public void removeAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
}