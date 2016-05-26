package com.zhongsou.souyue.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.fragment.SouyueTabFragment;

/**
 * 首页需要显示的webview
 * Created by lvqiang on 15/6/13.
 */
public abstract class AFragmentBaseView<T> extends RelativeLayout {
    protected Context mContext;
    protected Activity mActivity;
    protected View mMainView;
    public AFragmentBaseView(Context context) {
        super(context);
        mContext = context;
    }

    public AFragmentBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AFragmentBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void attachActivity(Activity _ac){
        mActivity = _ac;
    }

    public abstract void initView();

    /**
     * 复用销毁时，清除当前view中的一些状态
     */
    public abstract void unInitView();

    /**
     * 复用时，设置每个view的数据
     * @param data
     * @param fragment
     */
    public abstract void setData(T data,SouyueTabFragment fragment);

    /**
     * 进入当前页面会调用这个方法
     * @param force 表示当前这个请求是否是需要强刷的
     */
    public abstract void pullToRefresh(boolean force);

    /**
     * 刷新view
     */
    public abstract void updateViewList();

    /**
     * 设置当前view的销毁状态
     * @param destory
     */
    public abstract void setDestory(boolean destory);

    /**
     * 新浪分享回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 获得当前列表页中的listManager
     * @return
     */
    public HomeListManager getListManager(){
        return null;
    }
}
