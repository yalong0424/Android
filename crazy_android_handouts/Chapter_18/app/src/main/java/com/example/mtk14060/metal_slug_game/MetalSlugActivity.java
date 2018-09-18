package com.example.mtk14060.metal_slug_game;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static android.view.ViewGroup.LayoutParams.*;

public class MetalSlugActivity extends AppCompatActivity {

    private static FrameLayout mainLayout = null;
    private static FrameLayout.LayoutParams mainLP = null;
    public static Resources res = null;
    private static MetalSlugActivity mainActivity = null;

    private static int windowWidth;
    private static int windowHeight;

    private static GameView mainView = null;

    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取屏幕宽度和高度
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        windowHeight = metrics.heightPixels;
        windowWidth = metrics.widthPixels;

        res = getResources();

        setContentView(R.layout.activity_metal_slug);

        mainLayout = findViewById(R.id.mainLayout);

        mainView = new GameView(this.getApplicationContext(), GameView.STAGE_INIT);
        mainLP = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        mainLayout.addView(mainView, mainLP);

        //播放背景音乐
        player = MediaPlayer.create(this, R.raw.background);
        player.setLooping(true);
        player.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && !player.isPlaying()) {
            player.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }

    public static FrameLayout getMainLayout() {
        return mainLayout;
    }

    public static MetalSlugActivity getMainActivity() {
        return mainActivity;
    }
}
