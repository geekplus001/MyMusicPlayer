package com.ben.mymusicplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ben.mymusicplayer.adapter.MyMusicListAdapter;
import com.ben.mymusicplayer.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

public class MyLoveMusicListActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private ListView listView_love;
    private BenPlayerApp app;
    private ArrayList<Mp3Info> loveMp3Infos;
    private MyMusicListAdapter adapter;
//    private boolean isChange = false;//用来表示当前播放列表是否为收藏列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (BenPlayerApp) getApplication();
        setContentView(R.layout.activity_my_love_music_list);
        listView_love = (ListView) findViewById(R.id.listView_love);
        listView_love.setOnItemClickListener(this);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }


    private void initData() {
        try {
            List<Mp3Info> list = app.dbUtils.findAll(Selector.from(Mp3Info.class).where("isLike","=","1"));
            if(list==null || list.size()==0)
            {
                return;
            }
            loveMp3Infos = (ArrayList<Mp3Info>) list;
            adapter = new MyMusicListAdapter(this,loveMp3Infos);
            listView_love.setAdapter(adapter);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(playService.getChangePlayList()!=PlayService.LOVE_MUSIC_LIST)
        {
            playService.setMp3Infos(loveMp3Infos);
            playService.setChangePlayList(PlayService.LOVE_MUSIC_LIST);
        }
        playService.play(position);
        //我喜欢的同样保存播放时间
        savePlayRecord();
    }
    private void savePlayRecord() {
        Mp3Info mp3Info = playService.getMp3Infos().get(playService.getCurrentPosition());
        try {
            Mp3Info playRecordMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=", mp3Info.getMp3InfoId()));
            if(playRecordMp3Info==null)
            {
                mp3Info.setPlayTime(System.currentTimeMillis());
                app.dbUtils.save(mp3Info);
            }
            else
            {
                playRecordMp3Info.setPlayTime(System.currentTimeMillis());
                app.dbUtils.update(playRecordMp3Info,"playTime");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }
}
