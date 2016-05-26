package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.ui.BannerLinearLayout;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.module.CommonBean;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NewsListAdapterV2 extends BaseAdapter {

    private Context context;
    //private AQuery aquery;
    private List<SearchResultItem> listItems;

    public List<SearchResultItem> getBannerItems() {
        return bannerItems;
    }

    public void setBannerItems(List<SearchResultItem> bannerItems) {
        this.bannerItems = bannerItems;
    }

    private List<SearchResultItem> bannerItems;
    private boolean imgAble;
    private String channelTime;
    private String channel; //统计字段
    private boolean hasMoreItems;
    private LayoutInflater viewInflater;

    protected int height, width;
    private int deviceWidth;
    private int height08, width08;
    private float fontSize;

    public NewsListAdapterV2(Context context) {
        this(context, null);
    }

    public NewsListAdapterV2(Context context, String channel) {
        this.context = context;
        this.listItems = new ArrayList<SearchResultItem>();
        this.bannerItems = new ArrayList<SearchResultItem>();
        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = (deviceWidth - dip2px(context, 48)) / 3;
        height = (int) ((2 * width) / 3);
        width08 = (int) (0.8 * width);
        height08 = (int) (0.8 * height);

        this.channel = channel;
    }

    public void setChannelTime(String str) {
        this.channelTime = str;
    }

    public void setHasMoreItems(boolean has) {
        this.hasMoreItems = has;
    }

    public int getCount() {
        if (bannerItems != null && bannerItems.size() != 0) {
            return listItems == null ? 1 : this.listItems.size() + 1;
        } else {
            return listItems == null ? 0 : this.listItems.size();
        }

    }

    public void setImgAble(boolean imgAble) {
        this.imgAble = imgAble;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null /* || convertView == getCurrentFooter(parent) */
                || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = getCurrentView(position, convertView, holder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setViewData(position, holder);

        return convertView;
    }

    private void updateItemColor(ViewHolder holder) {
        boolean hasRead = holder.searchResultItem.hasRead();
        if (holder.title != null)
            holder.title.setTextColor(hasRead ? 0xff909090 : 0xff303030);
    }

    public String getChannelTime() {
        return channelTime;
    }

    static class ViewHolder {
        SearchResultItem searchResultItem;
        ImageView image, iv1, iv2, iv3;
        TextView title;

        BannerLinearLayout bannerLinearLayout;
//		TextView content;
//		TextView source;
//        TextView time;

        //新加的首页底部属性
        RelativeLayout mBottom;
        ImageView mAdd;
        TextView mComeFrom;
        TextView mFromTime;
        LinearLayout mRightLinear;
        TextView mGreatNums;
        TextView mCommentNums;
        TextView mSRPFrom;
        ViewPager bannerPager;
        TextView mSpecial;


    }

    public String getLastId() {
        if (null != listItems && listItems.size() > 0)
            return listItems.get(listItems.size() - 1).id();
        else
            return "0";
    }

    public void addMore(List<SearchResultItem> listItems) {
        this.listItems.addAll(listItems);
        if (listItems.size() > 0) {
            notifyDataSetChanged();
        }
    }

    public void addBanners(List<SearchResultItem> bannerItems) {
        this.bannerItems = bannerItems;
    }

    public synchronized void addDatas(List<SearchResultItem> datas) {
        this.listItems = datas;
        if (datas != null && datas.size() > 0) {
            notifyDataSetChanged();
        }
        // isRefresh = false;
    }

    public List<SearchResultItem> getDatas() {
        return listItems;
    }


    @Override
    public int getItemViewType(int position) {
        int type = super.getItemViewType(position);
        int len = getCount();

        if (!imgAble) {
            return type = SearchResult.NEWS_TYPE_NORMAL;
        }
        if (bannerItems.size() != 0 && position == 0) {
            return type = SearchResult.NEWS_TYPE_FOCUS;
        } else {
            position = (bannerItems != null && bannerItems.size() > 0) ? (position - 1) : position;
            return type = listItems.get(position).newsLayoutType();
        }

    }

    void setViewData(int position, ViewHolder holder) {
        int id = getItemViewType(position);

        if (id == SearchResult.NEWS_TYPE_FOCUS) {
            //设置要问轮播图
            holder.bannerLinearLayout.setData(bannerItems);

        } else {
            position = (bannerItems != null && bannerItems.size() > 0) ? (position - 1) : position;
            SearchResultItem data = listItems.get(position);
            fontSize = SYSharedPreferences.getInstance().loadResFont(context);
            if (holder.title != null) {
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            }

            if (id == SearchResult.NEWS_TYPE_NORMAL) {
                if (holder.title != null) {
                    holder.title.setText(data.title());
                }
                holder.mComeFrom.setText(data.source());
                holder.mFromTime.setText(StringUtils.convertDate(data.pubTime()));
            } else if (id == SearchResult.NEWS_TYPE_IMAGE_ONE
                    || id == SearchResult.NEWS_TYPE_IMAGE_TWO
                    && holder.image != null) {
                if (holder.title != null)
                    holder.title.setText(data.title());
                holder.mComeFrom.setText(data.source());
                holder.mFromTime.setText(StringUtils.convertDate(data.pubTime()));
                if (listItems.get(position).image().size() > 0)
//				aquery.id(holder.image).image(
//						listItems.get(position).image().get(0), true, true);
                    PhotoUtils.showCard(UriType.HTTP, listItems.get(position).image().get(0), holder.image, MyDisplayImageOption.smalloptions);
            } else if (id == CommonBean.NEWS_TYPE_IMAGE_THREE) {
                if (holder.title != null)
                    holder.title.setText(listItems.get(position).title());
                List<String> urls = listItems.get(position).image();
//			aquery.id(holder.iv1).image(urls.get(0), true, true);
//			aquery.id(holder.iv2).image(urls.get(1), true, true);
//			aquery.id(holder.iv3).image(urls.get(2), true, true);
                PhotoUtils.showCard(UriType.HTTP, urls.get(0), holder.iv1, MyDisplayImageOption.smalloptions);
                PhotoUtils.showCard(UriType.HTTP, urls.get(1), holder.iv2, MyDisplayImageOption.smalloptions);
                PhotoUtils.showCard(UriType.HTTP, urls.get(2), holder.iv3, MyDisplayImageOption.smalloptions);
                holder.mComeFrom.setText(data.source());
                holder.mFromTime.setText(StringUtils.convertDate(data.pubTime()));
            }
        }

        holder.searchResultItem = listItems.get(position);

        if (id != SearchResult.NEWS_TYPE_FOCUS)
            updateItemColor(holder);
        //updateItemColor(holder);
        if(holder.mSpecial!=null && ConstantsUtils.FR_INFO_SPECIAL.equals(listItems.get(position).category())){
               holder.mSpecial.setText("专题");
               holder.mSpecial.setVisibility(View.VISIBLE);
        }else if(holder.mSpecial!=null && ConstantsUtils.FR_INFO_PICTURES.equals(listItems.get(position).category())){
            holder.mSpecial.setText("图集");
            holder.mSpecial.setVisibility(View.VISIBLE);
        }else if(holder.mSpecial!=null){
            holder.mSpecial.setVisibility(View.GONE);
        }

		/*
                     * holder.searchResultItem = listItems.get(position); if (id !=
		 * SearchResult.NEWS_TYPE_FOCUS) updateItemColor(holder);
		 */
    }


    <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    void getBottomView(View convertView, ViewHolder holder) {
        holder.mBottom = findView(convertView, R.id.home_bottom_layout);
        holder.mAdd = findView(convertView, R.id.home_wgd_ib_add);
        holder.mComeFrom = findView(convertView, R.id.home_wgd_tv_news);
        holder.mFromTime = findView(convertView, R.id.home_wgd_tv_time);
        holder.mRightLinear = findView(convertView, R.id.home_wgd_tx_congraduation);
        holder.mGreatNums = findView(convertView, R.id.home_wgd_tv_great);
        holder.mCommentNums = findView(convertView, R.id.home_wgd_tv_comment);
        holder.mSRPFrom = findView(convertView, R.id.home_wgd_tv_come);
        holder.mSpecial = findView(convertView,R.id.tv_home_bottom_spacial);
        if (holder.mBottom != null) {
            holder.mRightLinear.setVisibility(View.GONE);
            holder.mSRPFrom.setVisibility(View.GONE);
            holder.mAdd.setVisibility(View.GONE);
            holder.mBottom.setVisibility(View.VISIBLE);
        }

    }

    protected View inflateView(int id) {
        viewInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return viewInflater.inflate(id, null);
    }

    View getCurrentView(int position, View convertView, ViewHolder holder) {
        int id = getItemViewType(position);
        float fontSize = SYSharedPreferences.getInstance().loadResFont(context);
        // if (!showFocus && id == TYPE_ITEM_FOCUS) // srp也不要焦点图i
        // datas.get(position).newsLayoutType_$eq(TYPE_ITEM_PIC);
        switch (id) {
            case SearchResult.NEWS_TYPE_NORMAL:
                convertView = inflateView(R.layout.fragment_rss_list_item_nopic_v2);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.title.setMaxLines(2);
                break;
            case SearchResult.NEWS_TYPE_FOCUS:
//			convertView = View.inflate(context,
//					R.layout.fragment_rss_focus_item, null);
//			holder.image = (ImageView) convertView.findViewById(R.id.image);
//			holder.title = (TextView) convertView.findViewById(R.id.title);
                // holder.bannerPager = (ViewPager)convertView.findViewById(R.id.banner_focus_item);
                convertView = new BannerLinearLayout(context);
                holder.bannerLinearLayout = (BannerLinearLayout) convertView;
                holder.bannerLinearLayout.setChannel(channel);   //统计

                break;
            case SearchResult.NEWS_TYPE_IMAGE_ONE:
            case SearchResult.NEWS_TYPE_IMAGE_TWO:
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.fragment_rss_list_item_v2, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                RelativeLayout.LayoutParams params01 = (RelativeLayout.LayoutParams) holder.image
                        .getLayoutParams();
                params01.width = width;
                params01.height = height;
                 holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
                params01.setMargins(0, 0, 0, 0);
                holder.image.setLayoutParams(params01);
                holder.title.setMaxLines(3);
                break;
            case SearchResult.NEWS_TYPE_IMAGE_THREE:
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.fragment_rss_list_item_v3, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.iv1
                        .getLayoutParams();
                params1.width = width;
                params1.height = height;
                holder.iv1.setLayoutParams(params1);
                holder.iv1.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.iv2 = (ImageView) convertView.findViewById(R.id.iv2);
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.iv2
                        .getLayoutParams();
                params2.width = width;
                params2.height = height;
                params2.setMargins(dip2px(context, 10), 0, dip2px(context, 10), 0);
                holder.iv2.setLayoutParams(params2);
                holder.iv3 = (ImageView) convertView.findViewById(R.id.iv3);
                LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) holder.iv3
                        .getLayoutParams();
                params3.width = width;
                params3.height = height;
                holder.iv3.setLayoutParams(params3);
                holder.title.setMaxLines(2);
                break;
            default:
                convertView = inflateView(R.layout.fragment_rss_list_item_nopic_v2);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                break;
        }
        if (holder.title != null) {
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
        getBottomView(convertView, holder);
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }


}
