package com.zhongsou.souyue.circle.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.PCPost;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.utils.CollectionUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob zhou on 14-11-3.
 * <p/>
 * 个人中心，帖子列表展示adapter
 * for PersonalCenterActivity
 */
public class PersonalCenterListAdapter extends BaseAdapter {

    private List<PCPost> list = new ArrayList<PCPost>();
    private Context context;
    private int deviceWidth;
    private int height08, width08;
    protected int height, width;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private ImageLoader imgloader;

    protected static final int TYPE_ITEM_PIC_NO = 0;
    protected static final int TYPE_ITEM_PIC_ONE = 1;
    protected static final int TYPE_ITEM_PIC_TWO = 2;
    protected static final int TYPE_ITEM_PIC_THREE = 3;


    public PersonalCenterListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.imgloader = ImageLoader.getInstance();
        this.options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).showImageOnLoading(R.drawable.default_small).showImageOnFail(R.drawable.default_small).showImageForEmptyUri(R.drawable.default_small).displayer(new SimpleBitmapDisplayer()).build();
        initPicSetting();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        BaseViewHolder baseHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cricle_list_item, null);
            baseHolder = new BaseViewHolder();
            initViewHolder(convertView, baseHolder, type);
            convertView.setTag(baseHolder);
        } else {
            baseHolder = (BaseViewHolder) convertView.getTag();
        }
        setViewData(baseHolder, type, position);
        return convertView;
    }


    private void initPicSetting(){
        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = (deviceWidth - DeviceUtil.dip2px(context, 48)) / 3;
        height = (2 * width) / 3;
        width08 = (int) (0.8 * width);
        height08 = (int) (0.8 * height);
    }


    private void setViewData(BaseViewHolder baseHolder, int layoutType, int position) {
        PCPost item = list.get(position);
        if (StringUtils.isEmpty(item.getTitle())) {
            baseHolder.title.setText(item.getBrief());
        } else {
            baseHolder.title.setText(item.getTitle());
        }
        baseHolder.circle_name.setText(item.getName());
        baseHolder.create_time.setText(StringUtils.convertDate(item.getCreate_time()) + "更新");

        if(layoutType == Constant.TYPE_ITEM_PIC_ONE) {
            if (baseHolder.iv_item_pic1 != null) {
                this.imgloader.displayImage(StringUtils.UpaiYun(item.getImages().get(0)), baseHolder.iv_item_pic1, options);
            }
        }
        if(layoutType == Constant.TYPE_ITEM_PIC_THREE){
            if (baseHolder.iv_item_pic1 != null) {
                this.imgloader.displayImage(StringUtils.UpaiYun(item.getImages().get(0)), baseHolder.iv_item_pic1, options);
            }
            if (baseHolder.iv_item_pic2 != null) {
                this.imgloader.displayImage(StringUtils.UpaiYun(item.getImages().get(1)), baseHolder.iv_item_pic2, options);
            }
            if (baseHolder.iv_item_pic3 != null) {
                this.imgloader.displayImage(StringUtils.UpaiYun(item.getImages().get(2)), baseHolder.iv_item_pic3, options);
            }
        }
    }


    private void initViewHolder(View convertView, BaseViewHolder baseHolder, int layoutType) {
        baseHolder.title = (TextView) convertView.findViewById(R.id.tv_cricle_title);
        baseHolder.create_time = (TextView) convertView.findViewById(R.id.tv_cricle_create_time);
        baseHolder.circle_name = (TextView) convertView.findViewById(R.id.tv_cricle_nickname);
        baseHolder.ll_cricle_pics = (LinearLayout)convertView.findViewById(R.id.ll_cricle_pics);
        convertView.findViewById(R.id.tv_cricle_good).setVisibility(View.GONE);
        convertView.findViewById(R.id.tv_cricle_follow).setVisibility(View.GONE);
        if (layoutType == Constant.TYPE_ITEM_PIC_NO) {
            baseHolder.ll_cricle_pics.setVisibility(View.GONE);
        }
        if(layoutType == Constant.TYPE_ITEM_PIC_ONE) {
            baseHolder.ll_cricle_pics.setVisibility(View.GONE);
            baseHolder.iv_item_pic1 = (ImageView) convertView.findViewById(R.id.iv_cricle_pic);
            baseHolder.iv_item_pic1.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params01 = (RelativeLayout.LayoutParams) baseHolder.iv_item_pic1.getLayoutParams();
            params01.width = width08;
            params01.height = height08;
            params01.setMargins(DeviceUtil.dip2px(context, 20), 0, 0, 0);
            baseHolder.iv_item_pic1.setLayoutParams(params01);
        }
        if(layoutType == Constant.TYPE_ITEM_PIC_THREE){
            baseHolder.iv_item_pic1 = (ImageView) convertView.findViewById(R.id.iv_cricle_pic1);
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) baseHolder.iv_item_pic1.getLayoutParams();
            params1.width = width;
            params1.height = height;
            baseHolder.iv_item_pic1.setLayoutParams(params1);

            baseHolder.iv_item_pic2 = (ImageView) convertView.findViewById(R.id.iv_cricle_pic2);
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) baseHolder.iv_item_pic2.getLayoutParams();
            params2.width = width;
            params2.height = height;
            params2.setMargins(DeviceUtil.dip2px(context, 12), 0, DeviceUtil.dip2px(context, 12), 0);
            baseHolder.iv_item_pic2.setLayoutParams(params2);

            baseHolder.iv_item_pic3 = (ImageView) convertView.findViewById(R.id.iv_cricle_pic3);
            LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) baseHolder.iv_item_pic3.getLayoutParams();
            params3.width = width;
            params3.height = height;
            baseHolder.iv_item_pic3.setLayoutParams(params3);
        }
    }


    @Override
    public int getItemViewType(int position) {
        PCPost item = this.list.get(position);
        List<String> images = item.getImages();
        if (CollectionUtils.isNotEmpty(images)) {
            if (images.size() < 3) {
                return TYPE_ITEM_PIC_ONE;
            } else {
                return TYPE_ITEM_PIC_THREE;
            }
        } else {
            return TYPE_ITEM_PIC_NO;
        }
    }


    public static class BaseViewHolder {
        public TextView title;                  //
        public TextView create_time;
        public TextView circle_name;
        public LinearLayout ll_cricle_pics ;
        public ImageView iv_item_pic1, iv_item_pic2, iv_item_pic3;
    }


    public void setList(List<PCPost> list) {       //第一次加载或者下拉刷新
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    public void addList(List<PCPost> list) {       //加载更多
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public long getLastId() {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return list.get(list.size() - 1).getBlog_id();
    }

}
