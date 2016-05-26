package com.zhongsou.souyue.circle.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * Created by lvqiang on 15/4/13.
 */
public class CPairFirstListView extends FrameLayout {
    private WebView mWebView;
    public CPairFirstListView(Context context) {
        super(context);
        init(context);
    }

    public CPairFirstListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CPairFirstListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context _context){
        setmListView();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean canScrollVertically(int direction) {
        setmListView();
        return mWebView.canScrollVertically(direction);
    }

    private void setmListView(){
        if(mWebView==null){
            int count = getChildCount();
            for(int i =0;i<count;i++){
                View v = getChildAt(i);
                if(v instanceof WebView){
                    mWebView = (WebView) v;
                }
            }
        }
    }
}
