package com.zhongsou.souyue.circle.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.GridView;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/3/21.
 */
public class VideoDetailGridView extends FrameLayout {
    private GridView mWebView;
    public VideoDetailGridView(Context context) {
        super(context);
        init(context);
    }

    public VideoDetailGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoDetailGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context _context){
        setGridView();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean canScrollVertically(int direction) {
        setGridView();
        return mWebView.canScrollVertically(direction);
    }

    private void setGridView(){
        if(mWebView==null){
            int count = getChildCount();
            for(int i =0;i<count;i++){
                View v = getChildAt(i);
                if(v instanceof GridView){
                    mWebView = (GridView) v;
                }
            }
        }
    }
}
