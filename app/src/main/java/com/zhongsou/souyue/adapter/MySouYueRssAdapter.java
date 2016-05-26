package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.utils.DeviceUtil;

/**
 * 搜悦报刊列表Adapter
 *
 * @author zcz
 */
public class MySouYueRssAdapter extends SouyueAdapter {
    // private static final int TYPE_ITEM_FOCUS = -1;
    // private static final int TYPE_ITEM_PIC = 1;
    // private static final int TYPE_ITEM_FOCUS_NOPIC = -2;
    // private static final int TYPE_ITEM_PIC_NO = 0;
    private static final int TYPE_ITEM_COUNT = 4;// item的数量 不包括公共的
    // public boolean showFocus = true;
//    private AQuery aquery;
    public boolean isRss = false;

    protected int height, width;
    private int deviceWidth;
    private int height08, width08;
    private Context context;

    public MySouYueRssAdapter(Context context) {
        super(context);
        this.context = context;
        setMaxCount(TYPE_ITEM_COUNT);
//        aquery = new AQuery(context);

        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = (deviceWidth - DeviceUtil.dip2px(context, 48)) / 3;
        height = (int) ((2 * width) / 3);
        width08 = (int) (0.8 * width);
        height08 = (int) (0.8 * height);
    }


    @Override
    View getCurrentView(int position, View convertView, ViewHolder holder) {
        int id = getItemViewType(position);
        // if (!showFocus && id == TYPE_ITEM_FOCUS) // srp也不要焦点图i
        // datas.get(position).newsLayoutType_$eq(TYPE_ITEM_PIC);
        //float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
        //holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        switch (id) {
            case SearchResult.NEWS_TYPE_NORMAL:
                convertView = inflateView(R.layout.homepage_content_item_nopic);
                holder.iv_marked = (ImageView) convertView
                        .findViewById(R.id.focus_noimg);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.date = (TextView) convertView.findViewById(R.id.home_wgd_tv_time);
                holder.source = (TextView) convertView.findViewById(R.id.home_wgd_tv_news);
                break;
            case SearchResult.NEWS_TYPE_FOCUS:
                convertView = inflateView(R.layout.fragment_rss_focus_item);
                holder.marked = (TextView) convertView.findViewById(R.id.title);
                holder.iv_marked = (ImageView) convertView.findViewById(R.id.image);
                break;
            case SearchResult.NEWS_TYPE_IMAGE_ONE:
            case SearchResult.NEWS_TYPE_IMAGE_TWO:
                convertView = inflateView(R.layout.homepage_content_item_pic1);
                holder.iv_pic = (ImageView) convertView.findViewById(R.id.image);
                RelativeLayout.LayoutParams params01 = (RelativeLayout.LayoutParams) holder.iv_pic
                        .getLayoutParams();
                params01.width = width08;
                params01.height = height08;
                params01.setMargins(DeviceUtil.dip2px(context, 20), 0, 0, 0);
                holder.iv_pic.setLayoutParams(params01);
                break;
            case SearchResult.NEWS_TYPE_IMAGE_THREE:
                convertView = inflateView(R.layout.homepage_content_item_pic3);
                holder.iv_item_1 = (ImageView) convertView.findViewById(R.id.iv1);
                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.iv_item_1
                        .getLayoutParams();
                params1.width = width;
                params1.height = height;
                holder.iv_item_1.setLayoutParams(params1);
                holder.iv_item_2 = (ImageView) convertView.findViewById(R.id.iv2);
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.iv_item_2
                        .getLayoutParams();
                params2.width = width;
                params2.height = height;
                params2.setMargins(DeviceUtil.dip2px(context, 12), 0, DeviceUtil.dip2px(context, 12), 0);
                holder.iv_item_2.setLayoutParams(params2);
                holder.iv_item_3 = (ImageView) convertView.findViewById(R.id.iv3);
                LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) holder.iv_item_3
                        .getLayoutParams();
                params3.width = width;
                params3.height = height;
                holder.iv_item_3.setLayoutParams(params3);
                break;
            default:
                convertView = inflateView(R.layout.fragment_rss_list_item);
                break;
        }
        if (SearchResult.NEWS_TYPE_IMAGE_ONE == id
                || SearchResult.NEWS_TYPE_IMAGE_TWO == id
                || SearchResult.NEWS_TYPE_IMAGE_THREE == id) {
            holder.title = (TextView) convertView.findViewById(R.id.title);

            //holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
//			holder.desc = (TextView) convertView
//					.findViewById(R.id.tv_cricle_brief);
//			holder.desc.setMaxLines(2);
//			holder.desc.setEllipsize(TextUtils.TruncateAt.END);
            //holder.date = (TextView) convertView.findViewById(R.id.home_wgd_tv_time);
            //holder.source = (TextView) convertView.findViewById(R.id.home_wgd_tv_news);

        }
        View v = convertView.findViewById(R.id.tv_home_hot);
        if (v != null) {
            v.setVisibility(View.GONE);
        }
        convertView.findViewById(R.id.home_bottom_layout).setVisibility(View.INVISIBLE);
//		findView(convertView, R.id.home_wgd_ib_add).setVisibility(View.GONE);
//		findView(convertView, R.id.home_wgd_tx_congraduation).setVisibility(View.GONE);
//		findView(convertView, R.id.home_wgd_tv_come).setVisibility(View.GONE);
        return convertView;
    }
//
//	void getBottomView(View convertView, ViewHolder holder) {
//		holder.mBottom = findView(convertView, R.id.home_bottom_layout);
//		holder.mAdd = findView(convertView, R.id.home_wgd_ib_add);
//		holder.mComeFrom = findView(convertView, R.id.home_wgd_tv_news);
//		holder.mFromTime = findView(convertView, R.id.home_wgd_tv_time);
//		holder.mRightLinear = findView(convertView, R.id.home_wgd_tx_congraduation);
//		holder.mGreatIcon = findView(convertView, R.id.imageView2);
//		holder.mCommentIcon = findView(convertView, R.id.imageView3);
//		holder.mGreatNums = findView(convertView, R.id.home_wgd_tv_great);
//		holder.mCommentNums = findView(convertView, R.id.home_wgd_tv_comment);
//		holder.mSRPFrom = findView(convertView, R.id.home_wgd_tv_come);
//		holder.mBottom.setVisibility(View.VISIBLE);
//	}

    public <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    void setViewData(int position, ViewHolder holder) {
        int id = getItemViewType(position);
        if (id == SearchResult.NEWS_TYPE_NORMAL) {
            // int color = ImageUtil.getColorByKey(datas.get(position).title());
            // holder.iv_marked.setBackgroundColor(MainApplication.getInstance().getResources().getColor(color));
        }
        if (id == SearchResult.NEWS_TYPE_FOCUS) {
            holder.iv_marked.setBackgroundResource(R.drawable.default_big);
//			aquery.id(holder.iv_marked).image(
//					datas.get(position).image().get(0), true, true);
            PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_marked);
            //PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_marked, MyDisplayImageOption.smalloptions, 0);

            holder.marked.setText(datas.get(position).title());
        }

        if (id == SearchResult.NEWS_TYPE_IMAGE_THREE
                && holder.iv_item_1 != null && holder.iv_item_2 != null
                && holder.iv_item_3 != null) {
            holder.iv_item_1.setImageResource(R.drawable.default_small);
            holder.iv_item_2.setImageResource(R.drawable.default_small);
            holder.iv_item_3.setImageResource(R.drawable.default_small);
            if (datas.get(position).image().get(0) != null) {

//				aquery.id(holder.iv_item_1).image(
//						datas.get(position).image().get(0), true, true);
                PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item_1, MyDisplayImageOption.smalloptions);
            }

            if (datas.get(position).image().get(1) != null) {

//				aquery.id(holder.iv_item_2).image(
//						datas.get(position).image().get(1), true, true);
                PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(1), holder.iv_item_2, MyDisplayImageOption.smalloptions);
            }
            if (datas.get(position).image().get(2) != null) {

//				aquery.id(holder.iv_item_3).image(
//						datas.get(position).image().get(2), true, true);
                PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(2), holder.iv_item_3, MyDisplayImageOption.smalloptions);
            }
//			holder.descTxt = datas.get(position).description();
        }
        if ((id == SearchResult.NEWS_TYPE_IMAGE_ONE || id == SearchResult.NEWS_TYPE_IMAGE_TWO)
                && holder.iv_pic != null) {
            holder.iv_pic.setImageResource(R.drawable.default_small);
//			aquery.id(holder.iv_pic).image(datas.get(position).image().get(0),
//					true, true);
            PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_pic, MyDisplayImageOption.smalloptions);
//			holder.descTxt = datas.get(position).description();// StringUtils.truncate(replaceAllNR(datas.get(position).description()),
            // StringUtils.LENGTH_46);
        } else {
//			holder.descTxt = datas.get(position).description();// StringUtils.truncate(replaceAllNR(datas.get(position).description()),
        }
//		if (holder.date != null) {
//			holder.date.setVisibility(View.VISIBLE);
//			holder.date.setText(StringUtils.convertDate(datas.get(position)
//					.date()));
//		}
//		if (holder.source != null) {
//			holder.source.setVisibility(View.VISIBLE);
//			holder.source.setText(datas.get(position).source());
//		}
//		if (holder.desc != null) {
//			holder.desc.setText(holder.descTxt);
//		}
        if (holder.title != null) {
            holder.title.setMaxLines(2);
            holder.title.setText(datas.get(position).title());
        }
        holder.searchResultItem = datas.get(position);
        if (id != SearchResult.NEWS_TYPE_FOCUS)
            updateItemColor(holder);
    }
}
