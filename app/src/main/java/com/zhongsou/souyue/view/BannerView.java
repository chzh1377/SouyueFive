package com.zhongsou.souyue.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.ui.ChildViewPager;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;

import java.util.List;

/**
 * @description: 轮播图
 *  图片比例600*240
 * @auther: qubian
 * @data: 2015/12/25.
 */

public class BannerView extends LinearLayout implements ViewPager.OnPageChangeListener {

    private float wAndh =600/240;
    private ChildViewPager viewPager;
    private TextView tvTitle;
    private LinearLayout indicatorLayout;
    private ImageView[] indicators;
    private List<BaseListData> infos;
    private ViewPagerAdapter adapter;
    private boolean isCycle; //是否循环
    private boolean isWheel; //是否轮播
    private final long time = 500;
    private final int WHEEL = 100; // 转动
    private int currentPosition = 0;
    private String channel;     //统计使用channel
    private Context mContext;
    private LayoutInflater inflater;
    private ImageCycleViewListener listener;
    private final int PageNum = 1000;
    private MyHander hander =new MyHander();
    class MyHander extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHEEL:
                    int position = (currentPosition + 1) % infos.size();
                    viewPager.setCurrentItem(position);
                    break;
            }
        }
    }
    public BannerView(Context context) {
        super(context);
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.banner_item, this, true);
        viewPager = (ChildViewPager) view.findViewById(R.id.viewPager);
        viewPager.setScrollable(false);
        indicatorLayout = (LinearLayout) view.findViewById(R.id.ll_indicator);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        setLayout();
    }
    private void setLayout()
    {
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
        layout.height= (int) (CircleUtils.getDeviceWidth(mContext)/wAndh);
        viewPager.setLayoutParams(layout);
    }



    public void setData(List infos) {
        this.infos = infos;
        if (infos == null || infos.size() == 0) {
            return;
        }
        int ivSize = infos.size();
        //设置指示器
        indicators = new ImageView[ivSize];
        indicatorLayout.removeAllViews();
        //添加dot
        LayoutParams params = null;
        ImageView dot = null;

        if(indicators.length>1) {
            for (int i = 0; i < indicators.length; i++) {

                dot = new ImageView(mContext);
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.leftMargin = 10;
                indicators[i] = dot;
                dot.setLayoutParams(params);
                indicatorLayout.addView(dot);
            }
        }

        adapter = new ViewPagerAdapter();

        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(infos.size()*PageNum);
        if(infos!=null && infos.size()==1) {
            onPageSelected(0);
        }
    }


    @Override
    public void onPageSelected(int i) {
        currentPosition = i % infos.size();
        if(indicators.length>1) {
            setIndicator(currentPosition);
        }
        if (listener != null) {
            listener.onImageShow(i);
        }
        tvTitle.setText(infos.get(currentPosition).getTitle());
        UpEventAgent.onZSYaoWenImageSlide(MainApplication.getInstance(), channel);    //ZSSDK 统计 要闻轮播图滑动响应次数

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    public boolean isCycle() {
        return this.isCycle;
    }

    /**
     * 是否轮播，默认是不轮播，轮播一定要循环
     *
     * @param isWheel
     */
    public void setWheel(boolean isWheel) {
        this.isWheel = isWheel;
        isCycle = true;
        if (isWheel) {
            hander.postDelayed(runnable, time);
        }
    }

    public boolean isWheel() {
        return isWheel;
    }

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mContext != null && isWheel) {
                hander.sendEmptyMessage(WHEEL);
            }
        }
    };

    /**
     * 刷新数据，当外部试图更新后，通知刷新
     */
    public void refreshData() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setIndicator(int selectedPosition) {
        for (int i = 0; i < indicators.length; i++) {
            if (i == selectedPosition) {
                indicators[selectedPosition].setBackgroundResource(R.drawable.icon_point);
            } else {
                indicators[i]
                        .setBackgroundResource(R.drawable.icon_point_pre);
            }
        }
    }


    private class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return  infos.size()==1?1:Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView v = new ImageView(mContext);
            v.setScaleType(ImageView.ScaleType.FIT_XY);
            position %= infos.size();
            if (position < 0) {
                position = infos.size() + position;
            }
            final int pos = position;
            SigleBigImgBean bean=null;
            if(infos.get(position) instanceof SigleBigImgBean)
            {
                bean = (SigleBigImgBean) infos.get(position);
            }
            String imageUrl="";
            if(bean!=null)
            {
                imageUrl= bean.getBigImgUrl();
            }else
            {
                List<String> list =infos.get(position).getImage();
                if(list!=null && list.size()>0)
                {
                    imageUrl=list.get(0);
                }
            }

            PhotoUtils.showCard(PhotoUtils.UriType.HTTP,imageUrl
                    , v, MyDisplayImageOption.getOptions(R.drawable.banner_bg));
            //tvTitle.setText(bean.getTitle());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo 页面跳转
                    BaseListData item =infos.get(pos);
                    //ZSSDK 统计 要闻轮播分页点击次数
                    if(listener!=null)
                    {
                        listener.onItemClick(item,pos);
                    }
                    UpEventAgent.onZSYaoWenImageClick(
                            MainApplication.getInstance(),
                            String.valueOf(viewPager.getCurrentItem() + 1),  //ZSSDK 统计 当前图片索引+1
                            channel);
                }
            });

            container.addView(v,0,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            return v;
        }

    }

    public void setOnImageCycleViewListener(ImageCycleViewListener listener) {
        this.listener = listener;
    }

    /**
     * 轮播控件的监听事件
     */
    public static interface ImageCycleViewListener {
        /**
         *  滑动事件
         *
         * @param position
         */
        public int onImageShow(int position);

        public void onItemClick(BaseListData item,int position);
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
