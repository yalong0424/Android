package com.mediatek.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.CrimeDbSchema.CrimeBaseHelper;
import database.CrimeDbSchema.CrimeCursorWrapper;
import database.CrimeDbSchema.CrimeDBSchema.CrimeTable;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    //private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

        //mCrimes = new ArrayList<>();
        /* test data
        for (int i = 0; i < 100; ++i){
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }
        */
    }

    public List<Crime> getCrimes() {
        //return mCrimes;
        //return new ArrayList<>();
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursorWrapper = queryCrimes(null, null);

        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                crimes.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        }
        finally {
            cursorWrapper.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
        /*
        for(Crime crime: mCrimes) {
            if (crime.getId().equals(id)){
                return crime;
            }
        }
        return null;
        */
        //return null;
        CrimeCursorWrapper cursorWrapper = queryCrimes(CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});

        try {
            if (cursorWrapper.getCount() == 0) {
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        }
        finally {
            cursorWrapper.close();
        }
    }

    public int getIndex(UUID id) {
        /*
        for (int i = 0; i < mCrimes.size(); ++i) {
            if (mCrimes.get(i).getId().equals(id))
            {
                return i;
            }
        }
        return -1;
        */
        //return -1;
        List<Crime> crimes = getCrimes();
        for (int i = 0; i < crimes.size(); ++i) {
            if (crimes.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public void delCrime(Crime crime) {
        //return mCrimes.remove(crime);
        //return false;
        String uuidStr = crime.getId().toString();
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[]{uuidStr});
    }

    public void addCrime(Crime crime) {
        //mCrimes.add(crime);
        ContentValues record = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, record);
    }

    public void updateCrime(Crime crime) {
        String uuidStr = crime.getId().toString();
        ContentValues record = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, record, CrimeTable.Cols.UUID + " = ?", new String[]{uuidStr});
    }

    //private Cursor queryCrimes(String whereClause, String[] whereArgs) {
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, //Columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        //return cursor;
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

        return values;
    }

    public static CrimeLab get(Context context){
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
}
