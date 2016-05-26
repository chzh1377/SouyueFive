package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;

public class SrpPopTitleAdapter extends BaseAdapter {

    private Context context;
    private ViewPager pager;
    private int currentClickIndex = 0;

    public SrpPopTitleAdapter(Context context, ViewPager pager) {
        this.context = context;
        this.pager = pager;
    }

    public void setIndex(int currentClickIndex) {
        this.currentClickIndex = currentClickIndex;
    }

    @Override
    public int getCount() {
        if (pager.getAdapter().getCount() % 3 == 1) {
            return pager.getAdapter().getCount() + 2;
        }
        if (pager.getAdapter().getCount() % 3 == 2) {
            return pager.getAdapter().getCount() + 1;
        }
        return pager.getAdapter().getCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if(convertView == null){
            textView = new TextView(context);
            textView.setBackgroundColor(context.getResources().getColor(
                    R.color.srp_poptitle_bg));
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(18, 30, 18, 30);
            textView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.WRAP_CONTENT,
                    AbsListView.LayoutParams.WRAP_CONTENT));
        }else {
            textView = (TextView)convertView;
        }
        if (position == currentClickIndex)
            textView.setTextColor(context.getResources().getColor(
                    R.color.srp_poptitle_current_textColor));
        else
            textView.setTextColor(context.getResources().getColor(
                    R.color.srp_poptitle_normal_textColor));

        textView.setTextSize(14);
        textView.setMaxEms(5);
        textView.setEllipsize(TruncateAt.END);
        textView.setSingleLine(true);
        if (position > pager.getAdapter().getCount() - 1) {
            textView.setText("");
        } else {
            textView.setText(pager.getAdapter().getPageTitle(position)
                    .toString());
        }
        return textView;
    }


}
