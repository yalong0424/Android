package com.mediatek.nerdlauncher;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

//负责托管fragment的抽象activity类
public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment(); //由子类给出需要盖activity托管的fragment实例

    @LayoutRes
    protected int getLayoutResId() { //动态提供布局资源ID，可由子类覆写，从而让不同子类拥有不同布局
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_fragment);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
