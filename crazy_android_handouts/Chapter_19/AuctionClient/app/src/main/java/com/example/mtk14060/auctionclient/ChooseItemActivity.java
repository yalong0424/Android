package com.example.mtk14060.auctionclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.mtk14060.app.base.SingleFragmentActivity;

public class ChooseItemActivity extends SingleFragmentActivity implements Callbacks
{
    private static final String KIND_ID_KEY = "kindId";

    @Override
    protected Fragment createFragment() {
        long kindId = getIntent().getLongExtra(KIND_ID_KEY, -1);
        return ChooseItemFragment.newInstance(kindId);
    }

    @Override
    public void onItemSelected(Integer id, Bundle bundle) {
        Intent intent = AddBidActivity.getAddBidIntent(this,
                AddBidActivity.parseItemId(bundle));
        startActivity(intent);
    }

    public static Intent getChooseItemIntent(Context context, long kindId)
    {
        Intent intent = new Intent(context, ChooseItemActivity.class);
        intent.putExtra(KIND_ID_KEY, kindId);
        return intent;
    }

    public static long parseKindId(Bundle bundle)
    {
        long kindId = -1;
        if (bundle != null)
        {
            kindId = bundle.getLong(KIND_ID_KEY);
        }
        return kindId;
    }
}
