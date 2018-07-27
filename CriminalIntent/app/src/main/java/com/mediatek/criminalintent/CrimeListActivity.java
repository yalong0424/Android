package com.mediatek.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }

    @Override
    protected  int getLayoutResId() {
        //return R.layout.activity_twopane;
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        }
        else {
            Fragment detail_fragment = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, detail_fragment)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment crimeListFragment = (CrimeListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();
    }
}
