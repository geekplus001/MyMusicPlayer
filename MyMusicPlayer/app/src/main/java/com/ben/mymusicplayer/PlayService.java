package com.ben.mymusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.ben.mymusicplayer.utils.MediaUtils;
import com.ben.mymusicplayer.vo.Mp3Info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    音乐播放的服务组件
    实现功能：
    1、播放
    2、暂停
    3、上一首
    4、下一首
    5、获取当前的播放进度

 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{

    private MediaPlayer mPlayer;
    private int currentPosition;//表示当前播放的歌曲位置
    ArrayList<Mp3Info> mp3Infos;

    private MusicUpdateListener musicUpdateListener;
    //创建线程池
    private ExecutorService es = Executors.newSingleThreadExecutor();

    private boolean isPause = false;
    //切换播放列表
    public static final int MY_MUSIC_LIST = 1;//我的音乐列表
    public static final int LOVE_MUSIC_LIST = 2;//我喜欢的列表
    public static final int RECORD_MUSIC_LSIT = 3;//最近播放列表
    private int changePlayList = MY_MUSIC_LIST;

    public int getChangePlayList() {
        return changePlayList;
    }

    public void setChangePlayList(int changePlayList) {
        this.changePlayList = changePlayList;
    }

    public static final int ORDER_PLAY = 1;
    public static final int RANDOM_PLAY = 2;
    public static final int SINGLE_PLAY = 3;
    public int play_mode = ORDER_PLAY;//播放模式
    public int getPlay_mode() {
        return play_mode;
    }
    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    public boolean isPause()
    {
        return isPause;
    }

    public PlayService() {
    }

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    public ArrayList<Mp3Info> getMp3Infos() {
        return mp3Infos;
    }

    public int getCurrentPosition()
    {
        return currentPosition;
    }

    private Random random = new Random();
    //播放完成调用的方法
    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (play_mode)
        {
            case ORDER_PLAY:
                next();
                break;
            case RANDOM_PLAY:
                play(random.nextInt(mp3Infos.size()));
                break;
            case SINGLE_PLAY:
                play(currentPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }


    //Binder类实现IBinder接口
    class PlayBinder extends Binder{
        public PlayService getPlayService()
        {
            return PlayService.this;
        }

    }



    @Override
    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //拿上次退出存储的信息
        BenPlayerApp app = (BenPlayerApp) getApplication();
        currentPosition = app.sp.getInt("currentPosition",0);
        play_mode = app.sp.getInt("play_mode",PlayService.ORDER_PLAY);

        mPlayer = new MediaPlayer();
        mp3Infos = MediaUtils.getMp3Infos(this);

        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);

        es.execute(updateStatusRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(es!=null && !es.isShutdown())
        {
            es.shutdown();
            es = null;
        }
        mPlayer = null;
        mp3Infos = null;
        musicUpdateListener = null;
    }

    /*
        更新进度
         */
    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
           while(true)
           {
               if(musicUpdateListener!=null && mPlayer!=null && mPlayer.isPlaying())
               {
                   musicUpdateListener.onPublish(getCurrentProgress());
               }
               try {
                   Thread.sleep(500);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        }
    };

    /*
        播放
     */
    public void play(int position)
    {
        Mp3Info mp3Info = null;
        if(position<0 || position>=mp3Infos.size())
        {
            position = 0;
        }
        mp3Info = mp3Infos.get(position);
        try {
            mPlayer.reset();
            mPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
            mPlayer.prepare();
            mPlayer.start();
            currentPosition = position;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(musicUpdateListener!=null)
        {
            musicUpdateListener.onChange(currentPosition);
        }
    }
    /*
        暂停
     */
    public void pause()
    {
        if(mPlayer.isPlaying())
        {
            mPlayer.pause();
            isPause = true;
        }
    }
    /*
        下一首
     */
    public void next()
    {
        if(currentPosition+1>=mp3Infos.size())
        {
            currentPosition = 0;
        }
        else
        {
            currentPosition++;
        }
        play(currentPosition);
    }
    /*
        上一首
     */
    public void prev()
    {
        if(currentPosition-1<0)
        {
            currentPosition = mp3Infos.size()-1;
        }
        else
        {
            currentPosition--;
        }
        play(currentPosition);
    }
    /*

     */
    public void start()
    {
        if(mPlayer!=null && !mPlayer.isPlaying())
        {
            mPlayer.start();
        }
    }
    public boolean isPlaying()
    {
        if(mPlayer!=null)
        {
            return mPlayer.isPlaying();
        }
        else
        {
            return false;
        }
    }

    /*
        获取进度值
     */
    public int getCurrentProgress()
    {
        if(mPlayer!=null && mPlayer.isPlaying())
        {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }
    /*

     */
    public int getDuration()
    {
        return mPlayer.getDuration();
    }
    /*
        进度条 跳
     */
    public void seekTo(int msec)
    {
        mPlayer.seekTo(msec);
    }

    /*
        更新状态的接口（UI 回调）
     */
    public interface MusicUpdateListener{
        public void onPublish(int progress);
        public void onChange(int position);
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }
}
