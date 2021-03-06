package com.ben.mymusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

    private static final int START_ACTIVITY = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this,PlayService.class);

        //启动服务  创建就启动 然后再绑定，这样解绑后Service还在后台运行
        startService(intent);

        handler.sendEmptyMessageDelayed(START_ACTIVITY, 3000);
    }

    //只用一次不会内存泄漏
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case START_ACTIVITY:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    break;
            }
        }
    };
}
