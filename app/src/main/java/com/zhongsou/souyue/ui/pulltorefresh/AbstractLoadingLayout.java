package com.zhongsou.souyue.ui.pulltorefresh;

import android.content.Context;
import android.widget.FrameLayout;

public abstract class AbstractLoadingLayout extends FrameLayout {

    public AbstractLoadingLayout(Context context) {
        super(context);
    }
    protected abstract void reset(boolean _isShowText);
    protected abstract void releaseToRefresh() ;
    protected abstract void pullToRefresh();
    protected abstract  void setPullLabel(String pullLabel);
    protected abstract  void refreshing();
    protected abstract void setRefreshTime(String text) ;
    protected abstract void setRefreshingLabel(String refreshingLabel);
    protected abstract void setReleaseLabel(String releaseLabel) ;
    protected abstract void setSubHeaderText(CharSequence label);
    protected abstract void onPullY(float scaleOfHeight) ;
}
