package database.CrimeDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.mediatek.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidStr = getString(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.SOLVED));

        Crime crime = new Crime(UUID.fromString(uuidStr));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);

        return crime;
    }
}
