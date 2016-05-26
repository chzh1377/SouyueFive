package com.zhongsou.souyue.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.module.listmodule.TitleIconBean;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.List;
import java.util.Map;

/**
 * @description: 热门的描述类
 * @auther: qubian
 * @data: 2015/12/23.
 */

public class HotConfigView extends LinearLayout {

    private Map<String , String > data;
    private Context mContext;
    public void setData(Map<String, String> data) {
        this.data = data;
        if(data!=null&&data.size()>0)
        {
            setVisibility(View.VISIBLE);
            setHotViews(data,mContext);
        }else
        {
            setVisibility(View.GONE);
        }

    }
//    public void setBeanList(List<TitleIconBean>bean)
//    {
//        if(bean!=null && bean.size()>0)
//        {
//            setHotViews(bean,mContext);
//        }
//    }

    public HotConfigView(Context context) {
        super(context);
        init(context);
    }

    public HotConfigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HotConfigView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    private void  init(Context context)
    {
        mContext =context;
    }
    private void setHotViews(Map<String, String> data,Context context)
    {
        BorderTextView textView;
        removeAllViews();
        for(String key :data.keySet())
        {
            textView = new BorderTextView(context);
            textView.setText(key);
            textView.setTextBorderColor(data.get(key));

            float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
            textView.setVisibility(View.VISIBLE);
            textView.setTextSize(fontSize - 4);
            textView.setPadding((int) (fontSize / 5.0), 0, (int) (fontSize / 5.f), 0);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5,0,5,0);
//            textView.setPadding((int)(fontSize/5.0),(int)(fontSize/5.0), (int) (fontSize/5.f),(int)(fontSize/5.0));
            textView.setIncludeFontPadding(false);
            addView(textView,lp);
        }
    }
//    private void setHotViews(List<TitleIconBean>beans,Context context)
//    {
//        BorderTextView textView;
//        removeAllViews();
//        for(TitleIconBean bean :beans)
//        {
//            textView = new BorderTextView(context);
//            textView.setText(bean.getWord());
//            textView.setTextBorderColor(bean.getColor());
//            LinearLayout.LayoutParams layoutParams =new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//            layoutParams.setMargins(5,0,5,0);
//            textView.setLayoutParams(layoutParams);
//
//            float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
//            textView.setVisibility(View.VISIBLE);
//            textView.setTextSize(fontSize - 4);
//            textView.setPadding((int) (fontSize / 6.0), 0, (int) (fontSize / 5.f), 0);
//            addView(textView);
//        }
//    }

}
