package com.mediatek.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5; //可以同时播放的音频的个数

    private AssetManager mAssetManager;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        mAssetManager = context.getAssets();
        //This old constructor is deprecated, but we need it for compatibility.
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSounds();
    }

    private void loadSounds() {
        String[] soundNames;
        try {
            soundNames = mAssetManager.list(SOUNDS_FOLDER);
            Log.i(TAG, "Found " + soundNames.length + " sounds");
        }
        catch (IOException ioe) {
            Log.e(TAG, "Could not list assets", ioe);
            return;
        }

        for (String filename: soundNames) {
            try {
                String assetPath = SOUNDS_FOLDER + "/" + filename;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            }
            catch (IOException ioe) {
                Log.e(TAG, "Could not load sound: " + filename, ioe);
            }
        }
    }

    //load: 加载音频
    private void load(Sound sound) throws IOException {
        AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(sound.getAssetPath());
        //调用mSoundPool.load(AssetFileDescriptor, int)方法可以把文件载入SoundPool待播。
        int soundId = mSoundPool.load(assetFileDescriptor, 1);
        sound.setSoundId(soundId);
    }

    //播放音频
    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) { //使用Integer而不是Int作为Sound ID，就是因为Integer可以为null值
            return;
        }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release() {
        mSoundPool.release();
    }

    public List<Sound> getSounds() {
        return mSounds;
    }
}
