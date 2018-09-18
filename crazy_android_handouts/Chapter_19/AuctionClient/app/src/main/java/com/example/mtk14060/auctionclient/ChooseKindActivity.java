package com.example.mtk14060.auctionclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.mtk14060.app.base.SingleFragmentActivity;

public class ChooseKindActivity extends SingleFragmentActivity implements Callbacks
{
    @Override
    protected Fragment createFragment() {
        return new ChooseKindFragment();
    }

    @Override
    public void onItemSelected(Integer id, Bundle bundle) {
        Intent intent = ChooseItemActivity.getChooseItemIntent(this,
                ChooseItemActivity.parseKindId(bundle));
        startActivity(intent);
    }
}
