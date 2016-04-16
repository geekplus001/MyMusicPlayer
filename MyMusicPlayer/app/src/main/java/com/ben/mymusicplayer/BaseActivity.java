package com.ben.mymusicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/30 0030.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected PlayService playService;

    private ArrayList<Activity> list = new ArrayList<>();

    private boolean isBinder = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list.add(this);
    }
    //退出功能
    public void exit()
    {
        int c = list.size();
        for(int i=0;i<c;i++)
        {
            list.get(i).finish();
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.PlayBinder playBinder = (PlayService.PlayBinder) service;
            playService = playBinder.getPlayService();

            playService.setMusicUpdateListener(musicUpdateListener);
            //
            musicUpdateListener.onChange(playService.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
        }
    };

    /*
        绑定服务
     */
    public void bindPlayService()
    {
        if(isBinder==false)
        {
            Intent intent = new Intent(this,PlayService.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
            isBinder = true;
        }

    }
    /*
        解除绑定服务
     */
    public void unbindPlayService()
    {
        if(isBinder==true)
        {
            unbindService(conn);
            isBinder = false;
        }

    }

    private PlayService.MusicUpdateListener musicUpdateListener = new PlayService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };

    public abstract void publish(int progress);
    public abstract void change(int position);
}
