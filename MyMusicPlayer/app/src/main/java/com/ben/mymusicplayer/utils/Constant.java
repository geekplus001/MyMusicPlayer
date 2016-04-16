package com.ben.mymusicplayer.utils;

/**
 * Created by Administrator on 2016/3/31 0031.
 */
public class Constant {
    public static final String SP_NAME = "BenMusic";
    public static final String DB_NAME = "BenPlayer.db";
    public static final int PLAY_RECORD_NUM = 5;//最近播放显示的记录条数

    //百度音乐根目录
    public static final String BAIDU_URL = "http://music.baidu.com/";
    //热歌榜
    public static final String BAIDU_DAYHOT = "top/dayhot/?pst=shouyeTop";
    //搜索
    public static final String BAIDU_SEARCH = "/search/song";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    public static final int SUCCESS = 1;//成功标记
    public static final int FAILED = 2;//失败标记

    public static final String DIR_MUSIC = "/ben_music";
    public static final String DIR_LRC = "/ben_music/lrc";
}
