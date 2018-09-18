package com.example.mtk14060.auctionclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.mtk14060.app.base.SingleFragmentActivity;

//仅仅用于包装、显示ManageKindFragment
public class ManageKindActivity extends SingleFragmentActivity implements Callbacks
{
    @Override
    protected Fragment createFragment() {
        return new ManageKindFragment();
    }

    @Override
    public void onItemSelected(Integer id, Bundle bundle) {
        // 当用户单击ManageKindFragment中的添加按钮时，启动AddKind Activity
        Intent intent = new Intent(this, AddKindActivity.class);
        startActivity(intent);
    }
}
