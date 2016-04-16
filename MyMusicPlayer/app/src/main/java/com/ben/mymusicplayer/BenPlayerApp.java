package com.ben.mymusicplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ben.mymusicplayer.utils.Constant;
import com.lidroid.xutils.DbUtils;

/**
 * Created by Administrator on 2016/3/31 0031.
 */
public class BenPlayerApp extends Application {
    public static SharedPreferences sp;//sp用于退出app保存信息
    public static DbUtils dbUtils;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        dbUtils = DbUtils.create(getApplicationContext(),Constant.DB_NAME);
        context = getApplicationContext();
    }
}
