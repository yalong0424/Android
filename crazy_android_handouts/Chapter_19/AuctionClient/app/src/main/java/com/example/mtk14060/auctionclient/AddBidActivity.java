package com.example.mtk14060.auctionclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.mtk14060.app.base.SingleFragmentActivity;

public class AddBidActivity extends SingleFragmentActivity {
    private static final String ITEM_ID_KEY = "itemId";

    @Override
    protected Fragment createFragment() {
        int itemId = getIntent().getIntExtra(ITEM_ID_KEY, -1);
        return AddBidFragment.newInstance(itemId);
    }

    public static Intent getAddBidIntent(Context context, int itemId)
    {
        Intent intent = new Intent(context, AddBidActivity.class);
        intent.putExtra(ITEM_ID_KEY, itemId);
        return intent;
    }

    public static int parseItemId(Bundle bundle)
    {
        int itemId = -1;
        if (bundle != null)
        {
            itemId = bundle.getInt(ITEM_ID_KEY);
        }
        return itemId;
    }
}
