package com.example.mtk14060.app.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

public abstract class SingleFragmentActivity extends AppCompatActivity
{
    private static final int ROOT_CONTAINER_ID = 0x90001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        setContentView(linearLayout);
        linearLayout.setId(ROOT_CONTAINER_ID);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(ROOT_CONTAINER_ID, createFragment())
                .commit();
    }

    protected abstract Fragment createFragment();
}
