package com.example.mtk14060.auctionclient;

import android.support.v4.app.Fragment;

import com.example.mtk14060.app.base.SingleFragmentActivity;

public class AddKindActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AddKindFragment();
    }
}
