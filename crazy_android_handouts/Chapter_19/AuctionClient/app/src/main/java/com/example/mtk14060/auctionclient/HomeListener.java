package com.example.mtk14060.auctionclient;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class HomeListener implements View.OnClickListener
{
    private Activity activity;

    public HomeListener(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, AuctionClientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }
}
