package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.ui.ProgressBarHelper;

/**
 * 订阅大全Fragment
 * @author Administrator
 */
@SuppressLint("ValidFragment")
public abstract class SubscribeListBaseFragment extends Fragment {
    protected ProgressBarHelper pbHelp;

    public abstract String getIndicatorTitle();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pbHelp = new ProgressBarHelper(getActivity(), view.findViewById(R.id.ll_data_loading));


    }
}
