package com.example.mtk14060.auctionclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.mtk14060.app.base.SingleFragmentActivity;

public class ManageItemActivity extends SingleFragmentActivity implements Callbacks
{
    @Override
    protected Fragment createFragment() {
        return new ManageItemFragment();
    }

    @Override
    public void onItemSelected(Integer id, Bundle bundle) {
        // 当用户单击添加按钮时，将会启动AddItem Activity
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }
}
