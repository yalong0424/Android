package com.mediatek.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.CrimeDbSchema.CrimeBaseHelper;

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
        return new ArrayList<>();
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
        return null;
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
        return -1;
    }

    public boolean delCrime(Crime crime) {
        //return mCrimes.remove(crime);
        return false;
    }

    public void addCrime(Crime crime) {
        //mCrimes.add(crime);
    }

    public static CrimeLab get(Context context){
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
}
