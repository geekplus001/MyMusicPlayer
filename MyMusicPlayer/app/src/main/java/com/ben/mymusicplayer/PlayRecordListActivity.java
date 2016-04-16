package com.ben.mymusicplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ben.mymusicplayer.adapter.MyMusicListAdapter;
import com.ben.mymusicplayer.utils.Constant;
import com.ben.mymusicplayer.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

public class PlayRecordListActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private BenPlayerApp app;
    private ListView listView_play_record;
    private ArrayList<Mp3Info> mp3Infos;
    private MyMusicListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_record_list);

        app = (BenPlayerApp) getApplication();
        listView_play_record = (ListView) findViewById(R.id.listView_play_record);
        listView_play_record.setOnItemClickListener(this);
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

    //初始化最近播放的数据
    private void initData() {
        try {
            //查询语句
            List<Mp3Info> list = app.dbUtils.findAll(Selector.from(Mp3Info.class).where("playTime","!=",0).orderBy("playTime",true).limit(Constant.PLAY_RECORD_NUM));//true表示desc倒序
            if(list==null || list.size()==0)
            {
                listView_play_record.setVisibility(View.GONE);
            }
            else
            {
                listView_play_record.setVisibility(View.VISIBLE);
                mp3Infos = (ArrayList<Mp3Info>) list;
                adapter = new MyMusicListAdapter(this,mp3Infos);
                listView_play_record.setAdapter(adapter);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(playService.getChangePlayList()!=PlayService.RECORD_MUSIC_LSIT)
        {
            playService.setMp3Infos(mp3Infos);
            playService.setChangePlayList(PlayService.RECORD_MUSIC_LSIT);
        }
        playService.play(position);
    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }
}
