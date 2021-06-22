package com.example.chironsolutions;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {


    public static final String USER_TABLE = "User_Table";
    public static final String COLUMN_PPG = "PPG";
    public static final String COLUMN_ECG = "ECG";
    public static final String COLUMN_DBP = "DBP";
    public static final String COLUMN_SBP = "SBP";
    public static final String COLUMN_DATE = "date";

    public DatabaseHandler(@Nullable Context context) {
        super(context, "UserDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "Create Table " + USER_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PPG + " DOUBLE, " + COLUMN_ECG + " DOUBLE, " + COLUMN_DBP + " DOUBLE, " + COLUMN_SBP + " DOUBLE, " + COLUMN_DATE + " LONG)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(UserDataModel userDataModel){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PPG, userDataModel.getPPG());
        cv.put(COLUMN_ECG, userDataModel.getECG());
        cv.put(COLUMN_DBP, userDataModel.getDBP());
        cv.put(COLUMN_SBP, userDataModel.getSBP());
        cv.put(COLUMN_DATE, userDataModel.getDate());

        long insert = db.insert(USER_TABLE, null, cv);

        if(insert == -1){
            return false;
        }
        else{
            return true;
        }
    }
}
