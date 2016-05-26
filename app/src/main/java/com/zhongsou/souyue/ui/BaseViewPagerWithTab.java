package com.zhongsou.souyue.ui;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;

import java.util.ArrayList;

/**
 * 自定义的带tab的ViewPager
 *
 * @author chefb@zhongsou.com
 */
public class BaseViewPagerWithTab extends LinearLayout implements
        OnClickListener {

    private LinearLayout llLeft;
    private LinearLayout llRight;
    public ViewPager mViewPager;
    private ArrayList<View> views;
    private MyViewPagerListener mViewPagerListener;
    private TextView tv_left;
    private TextView tv_right;

    public BaseViewPagerWithTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        llLeft = (LinearLayout) findViewById(R.id.llLeft);
//		((ImageView) llLeft.getChildAt(1)).setImageResource(R.drawable.iv_hsv);;
        llRight = (LinearLayout) findViewById(R.id.llRight);
        llLeft.setOnClickListener(this);
        llRight.setOnClickListener(this);
        tv_left = (TextView) llLeft.getChildAt(0);
        tv_right = (TextView) llRight.getChildAt(0);
        tv_left.setSelected(true);
        mViewPager = (ViewPager) findViewById(R.id.pager);

    }

    public void initViewPager() {
        mViewPager.setOnPageChangeListener(new MyPagerOnPageChangeListener());
        mViewPager.setAdapter(new MyPagerAdapter());// 设置ViewPager的适配器
    }

    public void setPageViews(ArrayList<View> views) {
        this.views = views;
    }

    /**
     * 设置左右两个textview的文本
     *
     * @param left
     * @param right
     */
    public void setAllText(String left, String right) {
        tv_left.setText(left);
        tv_right.setText(right);
    }

    @Override
    public void onClick(View v) {
        if (views.size() == 0) {
            return;
        }
        switch (v.getId()) {
            case R.id.llLeft:
                if (mViewPager.getCurrentItem() == 0) {
                    return;
                } else {
                    mViewPager.setCurrentItem(0);
                }
                break;
            case R.id.llRight:
                if (mViewPager.getCurrentItem() == 1) {
                    return;
                } else {
                    mViewPager.setCurrentItem(1);
                }
                break;
            default:
                break;
        }

    }

    public interface MyViewPagerListener {
        void onPageSelected(int position);
    }

    public void setMyViewPagerListener(MyViewPagerListener listener) {
        this.mViewPagerListener = listener;
    }

    /**
     * ViewPager的监听器
     *
     * @author chefb@zhongsou.com
     */
    private class MyPagerOnPageChangeListener implements OnPageChangeListener {
        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        /**
         * 滑动ViewPager的时候,让上方的蓝色条自动切换
         */
        @Override
        public void onPageSelected(int position) {
            tv_left.setSelected(position == 0);
            tv_right.setSelected(position == 1);

// 		ImageView iv_left = (ImageView) llLeft.getChildAt(1);
//			ImageView iv_right = (ImageView) llRight.getChildAt(1);
//			if (position == 0) {
//				iv_left.setImageResource(R.drawable.iv_hsv);
//				iv_right.setImageBitmap(null);
//			}
//			if (position == 1) {
//				iv_right.setImageResource(R.drawable.iv_hsv);
//				iv_left.setImageBitmap(null);
//			}
            if (mViewPagerListener != null) {
                mViewPagerListener.onPageSelected(position);
            }

        }

    }

    /**
     * Viewpager的适配器
     *
     * @author chefb@zhongsou.com
     */
    private class MyPagerAdapter extends PagerAdapter {

        public void destroyItem(View v, int position, Object obj) {
            ((ViewPager) v).removeView(views.get(position));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return views.size();
        }

        // 把布局放到viewpager上
        @Override
        public Object instantiateItem(View v, int position) {
            ((ViewPager) v).addView(views.get(position));
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void startUpdate(View arg0) {
        }
    }
}
