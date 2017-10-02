package com.soumya.wwdablu.barqr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class BarQrDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "barqr";
    private static final int DB_VERSION = 1;

    public BarQrDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(
            "CREATE TABLE " + BarQrContract.History.TABLE + " (" +
                BarQrContract.History._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BarQrContract.History.TYPE + " TEXT, " +
                BarQrContract.History.DATA + " TEXT " +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
