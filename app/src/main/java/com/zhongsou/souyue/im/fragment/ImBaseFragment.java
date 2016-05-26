package com.zhongsou.souyue.im.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.zhongsou.souyue.ui.SouYueToast;

public class ImBaseFragment extends Fragment {
    public ImBaseFragment() {}
    protected Activity mContext;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity(); 
    }
    
    protected String getRes4String(int id){
        if (mContext != null)
            return mContext.getResources().getString(id);
        return null;
    }
    
    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View root, int id){
        return (T) root.findViewById(id);
    }
    protected void showToast(int resId) {
        SouYueToast.makeText(mContext, getResources().getString(resId), 0).show();
    }
}
