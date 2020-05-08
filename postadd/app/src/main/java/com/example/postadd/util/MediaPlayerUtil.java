package com.example.postadd.util;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;




import com.example.postadd.base.BaseApp;

import java.io.IOException;

public class MediaPlayerUtil implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static volatile MediaPlayerUtil sMediaPlayerUtil = null;
    private final MediaPlayer mMediaPlayer;

    private MediaPlayerUtil(){
        //初始化全局唯一的MediaPlayer对象
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public static MediaPlayerUtil getInstance(){
        if (sMediaPlayerUtil == null){
            synchronized (MediaPlayerUtil.class){
                if (sMediaPlayerUtil == null){
                    sMediaPlayerUtil = new MediaPlayerUtil();
                }
            }
        }

        return sMediaPlayerUtil;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.print("mediaplayer error:"+extra);
        return false;
    }

    /**
     * 播放网络/本地sd卡的音频
     * @param url
     */
    public void setData(String url){
        try {
            //重新设置资源的时候需要重置
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            //网络资源,必须异步加载
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放本地raw下的资源
     * @param resId
     */
    public void setData(int resId){
        mMediaPlayer.reset();
        AssetFileDescriptor afd = BaseApp.getRes().openRawResourceFd(resId);
        if (afd == null) return;

        try {
           /* AudioAttributes aa = new AudioAttributes.Builder().build();
            mMediaPlayer.setAudioAttributes(aa);

            mMediaPlayer.setAudioSessionId(0);*/
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
