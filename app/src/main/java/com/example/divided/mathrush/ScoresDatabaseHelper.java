package com.example.divided.mathrush;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoresDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "scores.db";
    public static final String TABLE_EASY_NAME = "easy_scores";
    public static final String TABLE_MEDIUM_NAME = "medium_scores";
    public static final String TABLE_HARD_NAME = "hard_scores";
    public static final String COL1 = "ID";
    public static final String COL2 = "NAME";
    public static final String COL3 = "SCORE";
    public static final String COL4 = "ROUND";


    public ScoresDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_EASY_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,SCORE INTEGER,ROUND INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_MEDIUM_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,SCORE INTEGER,ROUND INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_HARD_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,SCORE INTEGER,ROUND INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EASY_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIUM_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HARD_NAME);
        onCreate(db);
    }

    public boolean insertScore(ScoreInformation scoreInformation, int difficultyLevel) {
        long result;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, scoreInformation.getName());
        contentValues.put(COL3, scoreInformation.getScore());
        contentValues.put(COL4, scoreInformation.getRound());

        if (difficultyLevel == 1) {
            result = db.insert(TABLE_EASY_NAME, null, contentValues);
        } else if (difficultyLevel == 2) {
            result = db.insert(TABLE_MEDIUM_NAME, null, contentValues);
        } else {
            result = db.insert(TABLE_HARD_NAME, null, contentValues);
        }

        return result != -1;

    }

    public void cleareRanking(int difficultyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (difficultyLevel == 1) {
            db.execSQL("delete from " + TABLE_EASY_NAME);
        } else if (difficultyLevel == 2) {
            db.execSQL("delete from " + TABLE_MEDIUM_NAME);
        } else {
            db.execSQL("delete from " + TABLE_HARD_NAME);
        }
    }

    public int getRankingPlace(ScoreInformation scoreInformation, int difficultyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (difficultyLevel == 1) {
            Cursor res = db.rawQuery("SELECT * FROM " + TABLE_EASY_NAME + " WHERE " + COL3 + " >" + scoreInformation.getScore() + " ORDER BY " + COL3 + " DESC", null);
            if (res != null) {
                return res.getCount() + 1;
            }
        } else if (difficultyLevel == 2) {
            Cursor res = db.rawQuery("SELECT * FROM " + TABLE_MEDIUM_NAME + " WHERE " + COL3 + " >" + scoreInformation.getScore() + " ORDER BY " + COL3 + " DESC", null);
            if (res != null) {
                return res.getCount() + 1;
            }
        } else {
            Cursor res = db.rawQuery("SELECT * FROM " + TABLE_HARD_NAME + " WHERE " + COL3 + " >" + scoreInformation.getScore() + " ORDER BY " + COL3 + " DESC", null);
            if (res != null) {
                return res.getCount() + 1;
            }
        }
        return 0; // means error
    }

    public Cursor getScores(int difficultyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (difficultyLevel == 1) {
            Cursor res = db.rawQuery("SELECT * FROM " + TABLE_EASY_NAME + " ORDER BY " + COL3 + " DESC", null);
            return res;
        } else if (difficultyLevel == 2) {
            Cursor res = db.rawQuery("SELECT * FROM " + TABLE_MEDIUM_NAME + " ORDER BY " + COL3 + " DESC", null);
            return res;
        } else {
            Cursor res = db.rawQuery("SELECT * FROM " + TABLE_HARD_NAME + " ORDER BY " + COL3 + " DESC", null);
            return res;
        }
    }

}
