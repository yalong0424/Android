package com.example.mtk14060.metal_slug_game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.mtk14060.metal_slug_game.comp.MonsterManager;
import com.example.mtk14060.metal_slug_game.comp.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    public static Player player = null;
    private Context mMainContext = null;

    private Paint mPaint = null;
    private Canvas mCanvas = null;

    //SurfaceHolder负责维护SurfaceView上绘制的内容
    private SurfaceHolder mSurfaceHolder = null;

    //游戏场景常量
    public static final int STAGE_NO_CHANGE = 0; //场景不改变常量
    public static final int STAGE_INIT      = 1; //初始化场景常量
    public static final int STAGE_LOGIN     = 2; //登录场景
    public static final int STAGE_GAME      = 3; //游戏场景
    public static final int STAGE_LOSE      = 4; //失败场景
    public static final int STAGE_QUIT      = 99; //退出场景
    public static final int STAGE_ERROR     = 255; //错误场景
    private int mStage = STAGE_NO_CHANGE; //记录该游戏目前处于何种场景

    //保存该游戏已经加载的所有场景的集合
    public static final List<Integer> mStageList = Collections.synchronizedList(new ArrayList<Integer>());

    public GameView(Context context, int firstStage) {
        super(context);

        mMainContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true); //设置抗锯齿
        setKeepScreenOn(true); //设置该组件会保持屏幕常亮，避免游戏过程中出现黑屏
        setFocusable(true); //设置焦点，处理相应事件
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        ViewManager.initScreen(MetalSlugActivity.getWindowWidth(), MetalSlugActivity.getWindowHeight());
        mStage = firstStage;
        player = new Player(mMainContext.getResources().getString(R.string.sun_wu_kong), Player.MAX_HP);
    }

    public Context getMainContext() {
        return mMainContext;
    }

    public void setMainContext(Context mainContext) {
        this.mMainContext = mainContext;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setPaint(Paint paint) {
        this.mPaint = paint;
    }

    public Canvas getCanvas() {
        return mCanvas;
    }

    public void setCanvas(Canvas canvas) {
        this.mCanvas = canvas;
    }

    public int getStage() {
        return mStage;
    }

    public void setStage(int stage) {
        this.mStage = stage;
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.mSurfaceHolder = surfaceHolder;
    }

    private static final int INIT = 1; //初始化 步骤
    private static final int LOGIC = 2; //逻辑 步骤
    private static final int CLEAN = 3; //清除 步骤
    private static final int PAINT = 4; //绘图 步骤

    //处理游戏的不同场景,该方法由游戏线程负责调度，该线程会不断执行doStage()方法
    public int doStage(int stage, int step)
    {
        int nextStage;
        switch (stage)
        {
            case STAGE_INIT:
                nextStage = doInit(step); //负责执行初始化
                break;
            case STAGE_LOGIN:
                nextStage = doLogin(step); //负责绘制游戏登录界面
                break;
            case STAGE_GAME:
                nextStage = doGame(step); //负责绘制游戏界面
                break;
            case STAGE_LOSE:
                nextStage = doLose(step); //负责绘制游戏失败界面
                break;
            default:
                nextStage = STAGE_ERROR;
                break;
        }
        return nextStage;
    }

    public void stageLogic()
    {
        int newStage = doStage(mStage, LOGIC);
        if (newStage != STAGE_NO_CHANGE && newStage != mStage)
        {
            doStage(mStage, CLEAN); //清除旧的场景
            mStage = newStage & 0xFF;
            doStage(mStage, INIT);
        }
        else if (mStageList.size() > 0)
        {
            newStage = STAGE_NO_CHANGE;
            synchronized (mStageList)
            {
                newStage = mStageList.get(0);
                mStageList.remove(0);
            }
            if (newStage == STAGE_NO_CHANGE)
            {
                return;
            }
            doStage(mStage, CLEAN); //清除旧的场景
            mStage = newStage & 0xFF;
            doStage(mStage, INIT);
        }
    }

    public int doInit(int step) {
        //初始化游戏图片
        ViewManager.loadResource();
        return STAGE_LOGIN; //跳转到登录界面
    }

    public Handler setViewHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            RelativeLayout layout = (RelativeLayout)msg.obj;
            if (layout != null) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
                );
                MetalSlugActivity.getMainLayout().addView(layout, params);
            }
        }
    };

    public Handler delViewHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            RelativeLayout layout = (RelativeLayout)msg.obj;
            if (layout != null)
            {
                MetalSlugActivity.getMainLayout().removeView(layout);
            }
        }
    };

    RelativeLayout gameLayout = null; //定义游戏界面
    private static final int ID_LEFT = 9000000;
    private static final int ID_FIRE = ID_LEFT + 1;

    public int doGame(int step)
    {
        switch (step)
        {
            case INIT:
                //初始化游戏界面
                if (gameLayout == null)
                {
                    gameLayout = new RelativeLayout(mMainContext);
                    //创建并添加向左移动的按钮
                    Button button = new Button(mMainContext);
                    button.setId(ID_LEFT);
                    button.setBackground(getResources().getDrawable(R.drawable.left));
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.setMargins((int)(ViewManager.scale * 20), 0, 0,
                            (int)(ViewManager.scale * 10));
                    gameLayout.addView(button, params);
                    button.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction())
                            {
                                case MotionEvent.ACTION_DOWN:
                                    player.setMove(Player.MOVE_LEFT);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    player.setMove(Player.MOVE_STAND);
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    break;
                            }
                            return false;
                        }
                    });
                    //添加向右的按钮
                    button = new Button(mMainContext);
                    button.setBackground(getResources().getDrawable(R.drawable.right));
                    params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.RIGHT_OF, ID_LEFT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.setMargins((int)(ViewManager.scale * 20), 0, 0,
                            (int)(ViewManager.scale * 10));
                    gameLayout.addView(button, params);
                    button.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction())
                            {
                                case MotionEvent.ACTION_DOWN:
                                    player.setMove(Player.MOVE_RIGHT);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    player.setMove(Player.MOVE_STAND);
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    break;
                            }
                            return false;
                        }
                    });
                    //添加射击按钮
                    button = new Button(mMainContext);
                    button.setId(ID_FIRE);
                    button.setBackground(getResources().getDrawable(R.drawable.fire));
                    params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.setMargins(0, 0, (int)(ViewManager.scale * 20),
                            (int)(ViewManager.scale * 10));
                    gameLayout.addView(button, params);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 当角色的leftShootTime为0时（上一枪发射结束），角色才能发射下一枪。
                            if (player.getLeftShootTime() <= 0)
                            {
                                player.addBullet();
                            }
                        }
                    });
                    //添加跳跃按钮
                    button = new Button(mMainContext);
                    button.setBackground(getResources().getDrawable(R.drawable.jump));
                    params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.LEFT_OF, ID_FIRE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.setMargins(0, 0, (int)(ViewManager.scale * 20),
                            (int)(ViewManager.scale * 10));
                    gameLayout.addView(button, params);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            player.setJump(true);
                        }
                    });
                    //将RelativeLayout容器添加到界面上
                    setViewHandler.sendMessage(setViewHandler.obtainMessage(0, gameLayout));
                }
                break;
            case LOGIC:
                // 随机生成怪物
                MonsterManager.generateMonster();
                // 检查碰撞
                MonsterManager.checkMonster();
                // 角色跳与移动
                player.logic();
                // 角色死亡
                if (player.isDie())
                {
                    mStageList.remove(STAGE_LOSE);
                }
                break;
            case CLEAN:
                //清除游戏界面
                if (gameLayout != null)
                {
                    delViewHandler.sendMessage(delViewHandler.obtainMessage(0, gameLayout));
                    gameLayout = null;
                }
                break;
            case PAINT:
                //绘制游戏元素
                ViewManager.clearScreen(mCanvas);
                ViewManager.drawGame(mCanvas);
                break;
        }
        return STAGE_NO_CHANGE;
    }

    private RelativeLayout loginView; //定义登录界面
    public int doLogin(int step) {
        switch (step)
        {
            case INIT:
                player.setHp(Player.MAX_HP); //初始化角色血量
                //初始化登录界面
                if (loginView == null)
                {
                    loginView = new RelativeLayout(mMainContext);
                    loginView.setBackgroundResource(R.drawable.game_back);
                    Button button = new Button(mMainContext);
                    button.setBackgroundResource(R.drawable.button_selector);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    loginView.addView(button, params);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mStageList.add(STAGE_GAME); //添加游戏场景
                        }
                    });
                    //通知Handler通知主界面加载loginView组件
                    setViewHandler.sendMessage(setViewHandler.obtainMessage(0, loginView));
                }
                break;
            case LOGIC:
                break;
            case CLEAN:
                //清楚登录界面
                if (loginView != null) {
                    //通过Handler通知主界面删除loginView组件
                    delViewHandler.sendMessage(delViewHandler.obtainMessage(0, loginView));
                    loginView = null;
                }
                break;
            case PAINT:
                break;
        }
        return STAGE_NO_CHANGE;
    }

    //定义游戏失败界面
    private RelativeLayout loseView;
    public int doLose(int step)
    {
        switch (step)
        {
            case INIT:
                //初始化失败界面
                if (loseView == null)
                {
                    loseView = new RelativeLayout(mMainContext);
                    loseView.setBackgroundResource(R.drawable.game_back);

                    Button button = new Button(mMainContext);
                    button.setBackgroundResource(R.drawable.again);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                      LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    loseView.addView(button, params);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //跳转到继续游戏界面
                            mStageList.add(STAGE_GAME);
                            //让角色生命值恢复到最大值
                            player.setHp(Player.MAX_HP);
                        }
                    });
                    setViewHandler.sendMessage(setViewHandler.obtainMessage(0, loseView));
                }
                break;
            case LOGIC:
                break;
            case CLEAN:
                if (loseView != null)
                {
                    delViewHandler.sendMessage(delViewHandler.obtainMessage(0, loseView));
                    loseView = null;
                }
                break;
            case PAINT:
                break;
        }
        return STAGE_NO_CHANGE;
    }

    //两次调度之间默认的暂停时间
    public static final int SLEEP_TIME = 40;
    //最小暂停时间
    public static final int MIN_SLEEP = 5;
    //定义负责让游戏界面上所有的角色和怪物“动”起来的线程
    class GameThread extends Thread
    {
        public SurfaceHolder surfaceHolder = null;
        public boolean needStop = false;
        public GameThread(SurfaceHolder holder)
        {
            this.surfaceHolder = holder;
        }

        @Override
        public void run() {
            long t1, t2;
            Looper.prepare();
            synchronized (surfaceHolder)
            {
                //游戏未退出,则每隔40毫秒就会重绘一次游戏界面，这样就会让整个游戏界面的角色、怪物“动”起来
                while (mStage != STAGE_QUIT && needStop == false)
                {
                    try {
                        //处理游戏的逻辑场景
                        stageLogic();
                        t1 = System.currentTimeMillis();
                        mCanvas = surfaceHolder.lockCanvas();
                        if (mCanvas != null)
                        {
                            //处理游戏场景
                            doStage(mStage, PAINT);
                        }
                        t2 = System.currentTimeMillis();
                        int paintTime = (int)(t2 - t1);
                        long millis = SLEEP_TIME - paintTime;
                        if (millis < MIN_SLEEP)
                        {
                            millis = MIN_SLEEP;
                        }
                        //该线程暂停millis毫秒后再次调用doStage()方法
                        sleep(millis);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try {
                            if (mCanvas != null)
                            {
                                surfaceHolder.unlockCanvasAndPost(mCanvas);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Looper.loop();
            try
            {
                sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    //游戏线程
    private GameThread thread = null;

    //当SurfaceView被加载时，surfaceCreated()方法就会被调用，这样整个游戏线程就会被启动起来，
    //从而控制整个游戏界面上的角色、怪物动起来
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //启动主线程执行部分
        mPaint.setTextSize(15);
        if (thread != null)
        {
            thread.needStop = true;
        }
        thread = new GameThread(mSurfaceHolder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
