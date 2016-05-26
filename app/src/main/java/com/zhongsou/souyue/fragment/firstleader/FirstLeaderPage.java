package com.zhongsou.souyue.fragment.firstleader;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zyw on 2016/3/25.
 */
@SuppressLint("ValidFragment")
public class FirstLeaderPage extends Fragment {
    public static final String TAG = FirstLeaderPage.class.getSimpleName();
    private int lId;

    public FirstLeaderPage(int layoutId) {
        this.lId = layoutId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(lId, container, false);
        return view;
    }
}
