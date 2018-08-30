package com.example.divided.mathrush;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ScoresDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "scores.db";
    private static final String TABLE_EASY_NAME = "easy_scores";
    private static final String TABLE_MEDIUM_NAME = "medium_scores";
    private static final String TABLE_HARD_NAME = "hard_scores";
    private static final String COL2 = "NAME";
    private static final String COL3 = "SCORE";
    private static final String COL4 = "ROUND";

    ScoresDatabaseHelper(Context context) {
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

        if (difficultyLevel == 0) {
            result = db.insert(TABLE_EASY_NAME, null, contentValues);
        } else if (difficultyLevel == 1) {
            result = db.insert(TABLE_MEDIUM_NAME, null, contentValues);
        } else {
            result = db.insert(TABLE_HARD_NAME, null, contentValues);
        }
        return result != -1;
    }

    public void clearRanking(int difficultyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (difficultyLevel == 0) {
            db.execSQL("delete from " + TABLE_EASY_NAME);
        } else if (difficultyLevel == 1) {
            db.execSQL("delete from " + TABLE_MEDIUM_NAME);
        } else {
            db.execSQL("delete from " + TABLE_HARD_NAME);
        }
    }

    public int getRankingPlace(ScoreInformation scoreInformation, int difficultyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (difficultyLevel == 0) {
            Cursor res = db.rawQuery("SELECT * FROM " + TABLE_EASY_NAME + " WHERE " + COL3 + " >" + scoreInformation.getScore() + " ORDER BY " + COL3 + " DESC", null);
            if (res != null) {
                int place = res.getCount() + 1;
                res.close();
                return place;
            }
        } else if (difficultyLevel == 1) {
            Cursor res = db.rawQuery("SELECT * FROM " + TABLE_MEDIUM_NAME + " WHERE " + COL3 + " >" + scoreInformation.getScore() + " ORDER BY " + COL3 + " DESC", null);
            if (res != null) {
                int place = res.getCount() + 1;
                res.close();
                return place;
            }
        } else {
            Cursor res = db.rawQuery("SELECT * FROM " + TABLE_HARD_NAME + " WHERE " + COL3 + " >" + scoreInformation.getScore() + " ORDER BY " + COL3 + " DESC", null);
            if (res != null) {
                int place = res.getCount() + 1;
                res.close();
                return place;
            }
        }
        return 0; // means error
    }

    public ArrayList<ScoreInformation>[] loadScoresFromDatabase() {
        ArrayList<ScoreInformation>[] scoreLists = (ArrayList<ScoreInformation>[]) new ArrayList[3];

        for (int i = 0; i < 3; i++) {
            scoreLists[i] = new ArrayList<>();
            Cursor res = this.getScores(i);
            if (res.getCount() != 0) {
                while (res.moveToNext()) {
                    final String name = res.getString(1);
                    final int score = res.getInt(2);
                    final int round = res.getInt(3);
                    ScoreInformation newScore = new ScoreInformation(name, round, score);
                    scoreLists[i].add(newScore);
                }
            }
        }
        return scoreLists;
    }

    private Cursor getScores(int difficultyLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (difficultyLevel == 0) {
            return db.rawQuery("SELECT * FROM " + TABLE_EASY_NAME + " ORDER BY " + COL3 + " DESC", null);
        } else if (difficultyLevel == 1) {
            return db.rawQuery("SELECT * FROM " + TABLE_MEDIUM_NAME + " ORDER BY " + COL3 + " DESC", null);
        } else {
            return db.rawQuery("SELECT * FROM " + TABLE_HARD_NAME + " ORDER BY " + COL3 + " DESC", null);
        }
    }
}
