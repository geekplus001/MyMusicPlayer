package com.ben.mymusicplayer.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ben.mymusicplayer.BenPlayerApp;

/**
 * Created by Administrator on 2016/4/3 0003.
 */
public class AppUtils {
    //隐藏输入法
    public static void hideInputMethod(View view)
    {
        InputMethodManager imm = (InputMethodManager) BenPlayerApp.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive())
        {
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
