package com.zhongsou.souyue.ui.pulltorefresh;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;

/**
 * Created by lvqiang on 15/6/11.
 */
public class CFootView extends RelativeLayout {

    ProgressBar mProgress;
    TextView mLoadingText;
    TextView mMoreText;

    public CFootView(Context context) {
        super(context);
//        setPageOne();
    }

    public CFootView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setPageOne();
    }

    public CFootView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setPageOne();
    }

    public void initView(){
        mProgress = (ProgressBar) findViewById(R.id.pull_to_refresh_progress);
        mLoadingText = (TextView) findViewById(R.id.pull_to_refresh_text);
        mMoreText = (TextView) findViewById(R.id.get_more);
    }

    public void setNetError(){
        mLoadingText.setVisibility(INVISIBLE);
        mProgress.setVisibility(INVISIBLE);
        mMoreText.setVisibility(View.VISIBLE);
        mMoreText.setText(R.string.cricle_manage_pullup_networkerror);
    }

    public void setLoading(){
        mLoadingText.setVisibility(VISIBLE);
        mProgress.setVisibility(VISIBLE);
        mLoadingText.setText(R.string.loading_ing);
        mMoreText.setVisibility(INVISIBLE);
    }

    public void setLoadDone(){
        mLoadingText.setVisibility(INVISIBLE);
        mProgress.setVisibility(INVISIBLE);
        mMoreText.setVisibility(INVISIBLE);
    }

    public void setLoadDoneClick(){
        mLoadingText.setVisibility(INVISIBLE);
        mProgress.setVisibility(INVISIBLE);
        mMoreText.setVisibility(VISIBLE);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getContext().getString(R.string.cricle_no_more_data_and_click));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#4A94D2")), 8, 12, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mMoreText.setText(spannableStringBuilder);
    }
}
