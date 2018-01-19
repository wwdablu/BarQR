package com.soumya.wwdablu.barqr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.soumya.wwdablu.barqr.historyfragment.HistoryPojo;
import com.soumya.wwdablu.barqr.historyfragment.ImmutableHistoryPojo;

import java.util.LinkedList;

public class HistoryHelper {

    private static HistoryHelper instance;
    private BarQrDatabase database;

    private HistoryHelper(Context context) {

        database = new BarQrDatabase(context);
    }

    public static HistoryHelper getInstance(Context context) {

        if(null == instance) {
            instance = new HistoryHelper(context);
        }

        return instance;
    }

    public void addHistoryData(HistoryPojo historyPojo) {

        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(BarQrContract.History.TYPE, historyPojo.rawScanType());
        insertValues.put(BarQrContract.History.DATA, historyPojo.rawScanData());
        sqLiteDatabase.insert(BarQrContract.History.TABLE, null, insertValues);
        sqLiteDatabase.close();
    }

    public LinkedList<HistoryPojo> getHistoryData() {

        LinkedList<HistoryPojo> historyList = new LinkedList<>();

        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(BarQrContract.History.TABLE,
                null, null, null, null, null, null);
        if(null != cursor && cursor.moveToFirst()) {

            do {

                HistoryPojo historyPojo = ImmutableHistoryPojo.builder()
                        .rawScanType(cursor.getString(cursor.getColumnIndex(BarQrContract.History.TYPE)))
                        .rawScanData(cursor.getString(cursor.getColumnIndex(BarQrContract.History.DATA)))
                        .build();

                historyList.add(historyPojo);

            } while (cursor.moveToNext());

            cursor.close();
        }

        sqLiteDatabase.close();

        return historyList;
    }

    public void clearAllHistory() {

        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        sqLiteDatabase.delete(BarQrContract.History.TABLE, null, null);
        sqLiteDatabase.close();
    }
}
