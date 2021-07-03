package com.example.chironsolutions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        String createTableStatement = "CREATE TABLE " + USER_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PPG + " DOUBLE, " + COLUMN_ECG + " DOUBLE, " + COLUMN_DBP + " DOUBLE, " + COLUMN_SBP + " DOUBLE, " + COLUMN_DATE + " LONG)";

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

    public List<UserDataModel> getAll() {

        List<UserDataModel> returnList = new ArrayList<>();


        String queryString = "SELECT * FROM " + USER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){

            do {
                int recordID = cursor.getInt(0);
                double PPG = cursor.getDouble(1);
                double ECG = cursor.getDouble(2);
                double DBP = cursor.getDouble(3);
                double SBP = cursor.getDouble(4);
                long Date = cursor.getLong(5);

                UserDataModel newData = new UserDataModel(recordID, PPG,ECG, DBP, SBP, Date);
                returnList.add(newData);

            }while (cursor.moveToNext());
        }
        else{
            // fail, empty list
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public UserDataModel getLatest() {

        UserDataModel latestData = new UserDataModel();

        String queryString = "SELECT * FROM " + USER_TABLE + " ORDER BY " + COLUMN_DATE +" DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){

            int recordID = cursor.getInt(0);
            double PPG = cursor.getDouble(1);
            double ECG = cursor.getDouble(2);
            double DBP = cursor.getDouble(3);
            double SBP = cursor.getDouble(4);
            long Date = cursor.getLong(5);

            latestData = new UserDataModel(recordID, PPG,ECG, DBP, SBP, Date);

        }
        else{
            // fail, empty list
        }

        cursor.close();
        db.close();
        return latestData;
    }

    public void deleteAll(){

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DROP TABLE IF EXISTS " + USER_TABLE;
        db.execSQL(queryString);

        String createTableStatement = "CREATE TABLE " + USER_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PPG + " DOUBLE, " + COLUMN_ECG + " DOUBLE, " + COLUMN_DBP + " DOUBLE, " + COLUMN_SBP + " DOUBLE, " + COLUMN_DATE + " LONG)";
        db.execSQL(createTableStatement);
    }

}
