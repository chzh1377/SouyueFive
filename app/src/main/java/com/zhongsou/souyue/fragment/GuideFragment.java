package com.zhongsou.souyue.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public final class GuideFragment extends Fragment {
    private int ivId;

    public GuideFragment() {

    }

    public GuideFragment(int content, int length, int pos) {
        this.ivId = content;
    }

    public static GuideFragment newInstance(int content, int length, int pos) {
        GuideFragment fragment = new GuideFragment(content, length, pos);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView iv = new ImageView(getActivity());
        iv.setScaleType(ScaleType.FIT_XY);
        iv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        iv.setImageResource(this.ivId);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER);
        layout.addView(iv);
        return layout;
    }
}
