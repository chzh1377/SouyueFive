package com.zhongsou.souyue.common;

import android.content.Context;
import com.zhongsou.souyue.common.utils.CommSharePreference;

/**
 * 通用包管理manager
 * Created by lvqiang on 15/9/21.
 */
public class CommManager {
    private static CommManager mInstance;
    public static CommManager getInstance(){
        if (mInstance == null){
            mInstance = new CommManager();
        }
        return mInstance;
    }
    public void init(Context context){
        CommSharePreference.getInstance().initContext(context);
    }
}
