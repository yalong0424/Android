package com.example.mtk14060.auctionclient;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AuctionClientActivity extends AppCompatActivity implements Callbacks
{
    public static final String VIEW_FAIL_JSP_PAGE = "viewFail.jsp";
    private static final String VIEW_SUCC_JSP_PAGE = "viewSucc.jsp";

    //定义一个旗标，标识该应用示范支持大屏幕
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** 由于layout和layout-sw480dp目录下分别包含了同名的activity_main.xml界面布局文件，
          * 因此AuctionClientActivity会自动根据设备屏幕尺寸自动加载相应目录下的activity_main.xml界面布局文件。
          * 即，指定加载R.layout.activity_main对应的界面布局文件，但实际上该应用会根据屏幕分辨率家在不同的界面布局文件
          */
        setContentView(R.layout.activity_main);
        //如果加载的activity_main.xml中包含ID为auction_detail_container的组件，则加载的为大屏幕(即平板)界面
        if (findViewById(R.id.auction_detail_container) != null)
        {
            mTwoPane = true;
            ((AuctionListFragment)(getSupportFragmentManager().findFragmentById(R.id.auction_list)))
                    .setActivateOnItemClick(true);
        }
    }


    @Override
    public void onItemSelected(Integer id, Bundle bundle) {
        if (mTwoPane)
        {
            //如果程序加载的是layout目录下的activity_main.xml布局文件(手机)，则用户单击功能项时，系统将会启动相应的Activity来显示功能
            Fragment fragment = null;
            switch (id)
            {
                // 查看竞得物品
                case 0:
                    fragment = ViewItemFragment.newInstance(VIEW_SUCC_JSP_PAGE);
                    break;
                // 浏览流拍物品
                case 1:
                    fragment = ViewItemFragment.newInstance(VIEW_FAIL_JSP_PAGE);
                    break;
                // 管理物品种类
                case 2:
                    fragment = new ManageKindFragment();
                    break;
                // 管理物品
                case 3:
                    fragment = new ManageItemFragment();
                    break;
                // 浏览拍卖物品（选择物品种类）
                case 4:
                    fragment = new ChooseKindFragment();
                    break;
                // 查看自己的竞标
                case 5:
                    fragment = new ViewBidFragment();
                    break;
                case ManageItemFragment.ADD_ITEM:
                    fragment = new AddItemFragment();
                    break;
                case ManageKindFragment.ADD_KIND:
                    fragment = new AddKindFragment();
                    break;
                case ChooseKindFragment.CHOOSE_ITEM:
                    fragment = ChooseItemFragment.newInstance(ChooseItemActivity.parseKindId(bundle));
                    break;
                case ChooseItemFragment.ADD_BID:
                    fragment = AddBidFragment.newInstance(AddBidActivity.parseItemId(bundle));
                    break;
            }
            //使用fragment替换auction_detail_container容器当前显示的Fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.auction_detail_container, fragment)
                    .addToBackStack(null).commit();
        }
        else {
            //如果程序加载的是layout-sw480dp目录下的activity_main.xml布局文件(平板),则用户单击功能项时，
            // 系统将会使用FrameLayout加载相应功能的Fragment
            Intent intent = null;
            switch ((int)id)
            {
                // 查看竞得物品
                case 0:
                    //启动 ViewItemActivity
                    intent = ViewItemActivity.getViewItemInstance(this, VIEW_SUCC_JSP_PAGE);
                    startActivity(intent);
                    break;
                // 浏览流拍物品
                case 1:
                    //启动 ViewItemActivity
                    intent = ViewItemActivity.getViewItemInstance(this, VIEW_FAIL_JSP_PAGE);
                    startActivity(intent);
                    break;
                // 管理物品种类
                case 2:
                    // 启动 ManageKindActivity
                    intent = new Intent(this, ManageKindActivity.class);
                    startActivity(intent);
                    break;
                // 管理物品
                case 3:
                    // 启动 ManageItemActivity
                    intent = new Intent(this, ManageItemActivity.class);
                    startActivity(intent);
                    break;
                // 浏览拍卖物品（选择物品种类）
                case 4:
                    // 启动 ChooseKindActivity
                    intent = new Intent(this, ChooseKindActivity.class);
                    startActivity(intent);
                    break;
                // 查看自己的竞标
                case 5:
                    // 启动 ViewBidActivity
                    intent = new Intent(this, ViewBidActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
