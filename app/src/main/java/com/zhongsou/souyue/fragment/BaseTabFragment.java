package com.zhongsou.souyue.fragment;

import android.app.Activity;
import com.zhongsou.souyue.fragment.MyFragmentTabHost.OnTabClickListener;

public class BaseTabFragment extends BaseFragment  {
    protected OnTabClickListener onTabClickListener;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.context = activity;

        try {
            onTabClickListener = (OnTabClickListener) activity;
        } catch (Exception e) {
            throw new RuntimeException(activity.getClass().getName() + " must implements OnTabClickListener");
        }

    }

    public void refresh() {
    }


}
