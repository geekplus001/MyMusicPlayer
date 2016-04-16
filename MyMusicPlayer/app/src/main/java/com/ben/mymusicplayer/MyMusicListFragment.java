package com.ben.mymusicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ben.mymusicplayer.adapter.MyMusicListAdapter;
import com.ben.mymusicplayer.utils.MediaUtils;
import com.ben.mymusicplayer.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;


import java.util.ArrayList;

import src.com.andraskindler.quickscroll.QuickScroll;

/**
 * Created by Administrator on 2016/3/30 0030.
 */
public class MyMusicListFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    private ListView listView_my_music;
    private ArrayList<Mp3Info> mp3Infos;
    private MyMusicListAdapter myMusicListAdapter;

    private  MainActivity mainActivity;

    private ImageView imageView_album;
    private TextView textView_songName,textView_singer;
    private ImageView imageView_play_pause,imageView_next,imageView_play_pre;

    private QuickScroll quickScroll;

//    private boolean isPause = false;

    private int position;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public static MyMusicListFragment newInstance() {
        MyMusicListFragment my = new MyMusicListFragment();
        return my;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout,null);
        listView_my_music = (ListView) view.findViewById(R.id.listView_mymusic);

        imageView_album = (ImageView) view.findViewById(R.id.imageview_album);
        imageView_play_pause = (ImageView) view.findViewById(R.id.imageview2_play_pause);
        imageView_next = (ImageView) view.findViewById(R.id.imageview3_next);
        textView_songName = (TextView) view.findViewById(R.id.textView_songName);
        textView_singer = (TextView) view.findViewById(R.id.textView2_singer);
        imageView_play_pre = (ImageView) view.findViewById(R.id.imageView_play_pre);

        quickScroll = (QuickScroll) view.findViewById(R.id.quickscroll);
        //点击监听
        listView_my_music.setOnItemClickListener(this);
        imageView_play_pause.setOnClickListener(this);
        imageView_next.setOnClickListener(this);
        imageView_play_pre.setOnClickListener(this);
        imageView_album.setOnClickListener(this);
        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        loadData();
        //绑定播放服务
        mainActivity.bindPlayService();
    }


    @Override
    public void onPause() {
        super.onPause();
        //解绑播放服务
        mainActivity.unbindPlayService();
    }

    /*
            加载本地音乐列表
             */
    public void loadData() {
        mp3Infos = MediaUtils.getMp3Infos(mainActivity);
//        mp3Infos = mainActivity.playService.mp3Infos;
        myMusicListAdapter = new MyMusicListAdapter(mainActivity,mp3Infos);
        listView_my_music.setAdapter(myMusicListAdapter);

        initQuickscroll();
    }

    private void initQuickscroll() {
        quickScroll.init(QuickScroll.TYPE_POPUP_WITH_HANDLE,listView_my_music,myMusicListAdapter,QuickScroll.STYLE_HOLO);
        quickScroll.setFixedSize(1);
        quickScroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
        quickScroll.setPopupColor(QuickScroll.BLUE_LIGHT,QuickScroll.BLUE_LIGHT_SEMITRANSPARENT,1, Color.WHITE,1);
    }

    //列表点击事件实现点击播放
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mainActivity.playService.getChangePlayList()!=mainActivity.playService.MY_MUSIC_LIST)
        {
            mainActivity.playService.setMp3Infos(mp3Infos);
            mainActivity.playService.setChangePlayList(PlayService.MY_MUSIC_LIST);
        }
        mainActivity.playService.play(position);

        //保存播放时间
        savePlayRecord();
    }
    //保存手动点击的播放记录
    private void savePlayRecord() {
        Mp3Info mp3Info = mainActivity.playService.getMp3Infos().get(mainActivity.playService.getCurrentPosition());
        try {
            Mp3Info playRecordMp3Info = mainActivity.app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getId()));
            if(playRecordMp3Info==null)
            {
                mp3Info.setMp3InfoId(mp3Info.getId());
                mp3Info.setPlayTime(System.currentTimeMillis());
                mainActivity.app.dbUtils.save(mp3Info);
            }
            else
            {
                playRecordMp3Info.setPlayTime(System.currentTimeMillis());
                mainActivity.app.dbUtils.update(playRecordMp3Info,"playTime");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /*
        回调播放状态下的UI设置
     */
    public void changeUIStatusOnPlay(int position)
    {
        if(position>=0 && position<mainActivity.playService.mp3Infos.size())
        {
            Mp3Info mp3Info = mainActivity.playService.mp3Infos.get(position);
            textView_songName.setText(mp3Info.getTitle());
            textView_singer.setText(mp3Info.getArtist());
            if(mainActivity.playService.isPlaying())
            {
                imageView_play_pause.setImageResource(R.mipmap.player_btn_pause_normal);
            }
            else
            {
                imageView_play_pause.setImageResource(R.mipmap.player_btn_play_normal);
            }

            Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
            imageView_album.setImageBitmap(albumBitmap);
            this.position = position;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imageview2_play_pause:
                if(mainActivity.playService.isPlaying())
                {
                    imageView_play_pause.setImageResource(R.mipmap.player_btn_play_normal);
                    mainActivity.playService.pause();
                }
                else
                {
                    if(mainActivity.playService.isPause())
                    {
                        imageView_play_pause.setImageResource(R.mipmap.player_btn_pause_normal);
                        mainActivity.playService.start();
                    }
                    else
                    {
                        mainActivity.playService.play(mainActivity.playService.getCurrentPosition());
                    }
                }
                break;
            case R.id.imageview3_next:
                mainActivity.playService.next();
                break;
            case R.id.imageView_play_pre:
                mainActivity.playService.prev();
                break;
            case R.id.imageview_album:
                Intent intent = new Intent(mainActivity,MusicPlayActivity.class);
//                intent.putExtra("isPause",isPause);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
