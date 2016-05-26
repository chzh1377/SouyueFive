package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
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
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.CommonBean;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.view.CSouyueTabInnerSpecial;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends BaseAdapter {
    public static final int BOTTOM_TYPE_HOME = 0;
    public static final int BOTTOM_TYPE_SRP = 1;
    public static final int BOTTOM_TYPE_CIRCLE = 2;

    public static final int MARGIN_LEFT=10;
    public static final int MARGIN_TOP=5;

    private final ImageLoader imgloader;
    private final DisplayImageOptions options;
    private final DisplayImageOptions mSpecialImageOptions; //新的专题页专用的imageLoader属性
    private Context context;
    //	private AQuery aquery;
    private List<SearchResultItem> listItems;
//    private boolean imgAble;
    private String channelTime;
    private boolean hasMoreItems;
    private LayoutInflater viewInflater;

    // 下面用于测量图片宽高
    protected int height, width;
    private int deviceWidth;

    private int mMarginLeft;
    private int mMarginTop;

    private int mBottomType;
    private onItemAddClick mOnItemAddClick;
    private onItemSourceClick mOnItemSourceClick;

    private float fontSize;

    public void deleteData(SearchResultItem item) {
        listItems.remove(item);
    }

    public interface onItemAddClick {
        public void onItemAddClick(View v,int pos,SearchResultItem _item);
    }

    public interface onItemSourceClick {
        public void onClick(SearchResultItem _item);
    }

    public void deleteData(String _time){
        SearchResultItem result = null;
        for (SearchResultItem item:listItems){
            if (item.date().equals(_time)){
                result = item;
            }
        }
        if (result!=null) {
            listItems.remove(result);
        }
    }



    public HomeAdapter(Context context) {
        this.context = context;
//		aquery = new AQuery(context);
        this.imgloader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
//                .showImageOnLoading(R.drawable.circle_title_default_icon_c)
//                .displayer(new FadeInBitmapDisplayer(500))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        mSpecialImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .showImageForEmptyUri(R.drawable.default_small)
                .showImageOnFail(R.drawable.default_small)
                .showImageOnLoading(R.drawable.default_small)
                .build();
        this.listItems = new ArrayList<SearchResultItem>();

        mMarginLeft = DeviceUtil.dip2px(context, MARGIN_LEFT);
        mMarginTop = DeviceUtil.dip2px(context,MARGIN_TOP);
        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = (deviceWidth - DeviceUtil.dip2px(context, 48)) / 3;
        height = (int) ((2 * width) / 3);
//        width08 = (int) (0.8 * width);
//        height08 = (int) (0.8 * height);
    }

    public <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    public void setBottomType(int _type) {
        mBottomType = _type;
    }

//    public int getmBottomType() {
//        return mBottomType;
//    }

    public void setmOnItemAddClick(onItemAddClick _onadd) {
        mOnItemAddClick = _onadd;
    }

    public void setOnItemSourceClick(onItemSourceClick _click) {
        mOnItemSourceClick = _click;
    }

//    /**
//     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//     */
//    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }

    public void setChannelTime(String str) {
        this.channelTime = str;
    }

    public void setHasMoreItems(boolean has) {
        this.hasMoreItems = has;
    }

    public int getCount() {
        return listItems == null ? 0 : this.listItems.size();
    }

//    public void setImgAble(boolean imgAble) {
//        this.imgAble = imgAble;
//    }

    public Object getItem(int position) {
        return listItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup arg2) {
        if (convertView == null /* || convertView == getCurrentFooter(parent) */
                || convertView.getTag() == null) {
            ViewHolder holder = new ViewHolder(mOnItemSourceClick);
            convertView = getCurrentView(position, convertView, holder);
            setBottomType(holder, mBottomType);
            convertView.setTag(holder);
        }
        if (listItems != null && listItems.size() > 0 && listItems.size() > position) {
            try {
                setViewData(position, (ViewHolder) convertView.getTag());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        TextView tvHomeHot;
        TextView content;
        TextView source;
        TextView time;
        //新加的首页底部属性
        RelativeLayout mBottom;
        ImageView mAdd;
        TextView mComeFrom;
        TextView mFromTime;
        LinearLayout mRightLinear;
        ImageView mGreatIcon;
        TextView mGreatNums;
        ImageView mCommentIcon;
        TextView mCommentNums;
        TextView mSRPFrom;
        TextView tvHomeBottomSpacial;
        onItemSourceClick mClick;
        //新增的专题页面
        ImageView home_special_image;
        TextView home_special_title;
        TextView home_special_date;
        TextView home_special_tag_text;
        TextView home_special_desc;


        public ViewHolder(onItemSourceClick _onclick) {
            mClick = _onclick;
        }

        public void setOnClick() {
            mSRPFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClick.onClick(searchResultItem);
                }
            });
        }
    }

    public String getLastId() {
        if (null != listItems && listItems.size() > 0)
            return listItems.get(listItems.size() - 1).id();
        else
            return "0";
    }

    public String getLastDataId(){
        if (null != listItems && listItems.size() > 0)
            return listItems.get(listItems.size() - 1).date();
        else
            return "0";
    }


    public String getFirstId() {
        if (null != listItems && listItems.size() > 0)
            return listItems.get(0).id();
        else
            return "0";
    }

    public String getFirstDateId(){
        if (null != listItems && listItems.size() > 0)
            return listItems.get(0).date();
        else
            return "0";
    }

    public void addMore(List<SearchResultItem> listItems) {
        this.listItems.addAll(listItems);
        if (listItems.size() > 0) {
            notifyDataSetChanged();
        }
    }

    public synchronized void setDatas(List<SearchResultItem> datas) {
        this.listItems = datas;
        if (datas != null && datas.size() > 0) {
            notifyDataSetChanged();
        }
    }

    public void addDatas(List<SearchResultItem> datas){
        listItems.addAll(0, datas);
    }

    public void clearDatas(){
        listItems.clear();
    }

    public void clearClickRefresh(){
        List<SearchResultItem> items = new ArrayList<SearchResultItem>();
        for (SearchResultItem item:listItems){
            if (item.newsLayoutType() == SearchResult.NEWS_TYPE_CLICK_REFRESH){
                items.add(item);
            }
        }
        for (SearchResultItem item:items){
            listItems.remove(item);
        }
    }

    public List<SearchResultItem> getDatas() {
        return listItems;
    }

    @Override
    public int getItemViewType(int position) {
        int type = super.getItemViewType(position);
        int len = this.listItems.size();
        if (position < len) {
//            if (!imgAble) {
//                type = SearchResult.NEWS_TYPE_NORMAL;
//            } else
            type = listItems.get(position).newsLayoutType();
        } /*
         * else { if ((position == len + 1) && hasMoreItems) { if
		 * (!CMainHttp.getInstance().isNetworkAvailable()) type = TYPE_ITEM_GET_MORE; else type =
		 * TYPE_ITEM_WAITING; } }
		 */
        return type;
    }

    void setViewData(int position, final ViewHolder holder) {

        int id = getItemViewType(position);
        final int posi = position;
        final SearchResultItem data = listItems.get(position);
        if (data.newsLayoutType()==SearchResult.NEWS_TYPE_CLICK_REFRESH){
            return;
        }

        //如果是新的专题页
        if(id == SearchResult.NEWS_TYPE_SPECIAL_TOPIC){
            holder.home_special_date.setText(data.happenTime + " " + data.day);
            if(TextUtils.isEmpty(data.descreption)){
                holder.home_special_desc.setVisibility(View.GONE);
            }else{
                holder.home_special_desc.setVisibility(View.VISIBLE);
                holder.home_special_desc.setText(data.descreption);
            }
            if(TextUtils.isEmpty(data.tag)){
                holder.home_special_tag_text.setVisibility(View.GONE);
            }else{
                holder.home_special_tag_text.setVisibility(View.VISIBLE);
                holder.home_special_tag_text.setText(data.tag);
            }
            holder.home_special_title.setText(data.title());
            imgloader.cancelDisplayTask(holder.home_special_image);
            PhotoUtils.showCard(PhotoUtils.UriType.HTTP, data.pic, holder.home_special_image, mSpecialImageOptions);
            return ;
        }

        fontSize = SYSharedPreferences.getInstance().loadResFont(context);
        String str = data.title();
        if (holder.tvHomeHot!=null) {
            if (data.getIsHot() == CSouyueTabInnerSpecial.HOT_IS) {
                str = "     " + str;
                holder.tvHomeHot.setVisibility(View.VISIBLE);
                holder.tvHomeHot.setTextSize(fontSize - 3);
                holder.tvHomeHot.setPadding((int) (fontSize / 6.0), 0, (int) (fontSize / 5.f), 0);
            } else {
                holder.tvHomeHot.setVisibility(View.GONE);
            }
        }
//        fontSize = SYSharedPreferences.getInstance().loadResFont(context);
//        String str = data.title();
////        int size = DeviceUtil.sp2px(context, fontSize);
////        SpannableStringBuilder spannableString0 = new SpannableStringBuilder();
//        if (data.isHot()){
////            spannableString0.append("icon").append(data.title());
////            mHotBitmap.setBounds(0,0,size,size);
////            ImageSpan span = new ImageSpan(mHotBitmap);
////            spannableString0.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////            spannableString.setSpan(new Backg(Color.parseColor("#4A94D2")), 0, 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            str = "     "+str;
//            holder.tvHomeHot.setVisibility(View.VISIBLE);
//            holder.tvHomeHot.setTextSize(fontSize-3);
//            holder.tvHomeHot.setPadding((int)(fontSize/6.0),0, (int) (fontSize/5.f),0);
//        }else{
//            holder.tvHomeHot.setVisibility(View.GONE);
//        }
//        int length = spannableString0.length();
//        if (data.category().equals(ConstantsUtils.FR_INFO_SPECIAL)) {
//            if (length==0){
//                spannableString0.append(data.title());
//            }
//            spannableString0.append(" ");
//            mSpecial.setBounds(0, 0, size, size);
//            ImageSpan span = new ImageSpan(mSpecial);
//            spannableString0.setSpan(span, length-1, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        if (length==0){
//            spannableString0.append(data.title());
//        }
//        int length = spannableString0.length();
//        if (data.category().equals(ConstantsUtils.FR_INFO_SPECIAL)) {//专题移到5.1
//            if (length==0){
//                spannableString0.append(data.title());
//            }
//            spannableString0.append(" ");
//            mSpecial.setBounds(0, 0, size, size);
//            ImageSpan span = new ImageSpan(mSpecial);
//            spannableString0.setSpan(span, length-1, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        if (length==0){
//            spannableString0.append(data.title());
//        }
        holder.title.setText(str);
        holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);

        if (mBottomType == BOTTOM_TYPE_HOME && holder.mAdd != null) {

            holder.mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemAddClick != null) {
                        mOnItemAddClick.onItemAddClick(v,posi,data);
                    }
                }
            });
        }
        List<String> urls = data.image();
        if (id == CommonBean.NEWS_TYPE_NORMAL) {

        } else if (id == CommonBean.NEWS_TYPE_IMAGE_ONE
                || id == CommonBean.NEWS_TYPE_IMAGE_TWO) {
            imgloader.cancelDisplayTask(holder.image);
            //imgloader.displayImage(urls.get(0), holder.image, options);
            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,urls.get(0),holder.image,options);
        } else if (id == CommonBean.NEWS_TYPE_IMAGE_THREE) {
            imgloader.cancelDisplayTask(holder.iv1);
            imgloader.cancelDisplayTask(holder.iv2);
            imgloader.cancelDisplayTask(holder.iv3);
            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,urls.get(0),holder.iv1,options);
            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,urls.get(1),holder.iv2,options);
            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,urls.get(2),holder.iv3,options);

        } else if (id == CommonBean.NEWS_TYPE_HEADLINETOP) {
            SpannableStringBuilder spannableString = new SpannableStringBuilder(" 要闻 ");
            spannableString.append(" ").append(data.title());
            spannableString.setSpan(new AbsoluteSizeSpan(SouyueAdapter.dp2px(context, 14)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#4A94D2")), 0, 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.title.setText(spannableString);
            String bigurl=data.bigImgUrl();
            if (!StringUtils.isEmpty(bigurl))
               // imgloader.displayImage(bigurl, holder.image, options);
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP,bigurl,holder.image,options);
        }

        holder.searchResultItem = listItems.get(position);
        setBottomData(holder, data);
        if (id != SearchResult.NEWS_TYPE_FOCUS)
            updateItemColor(holder);
		/*
		 * holder.searchResultItem = listItems.get(position); if (id !=
		 * SearchResult.NEWS_TYPE_FOCUS) updateItemColor(holder);
		 */

    }

    protected View inflateView(int id) {
        viewInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return viewInflater.inflate(id, null);
    }

    View getCurrentView(int position, View convertView, ViewHolder holder) {
        int id = getItemViewType(position);

        switch (id) {
            case SearchResult.NEWS_TYPE_NORMAL:
                convertView = inflateView(R.layout.homepage_content_item_nopic);
//                ((TextView)convertView.findViewById(R.id.title)).setTextSize(fontSize);

                break;
            case SearchResult.NEWS_TYPE_FOCUS:
                convertView = View.inflate(context,
                        R.layout.fragment_rss_focus_item, null);
                holder.image = (ImageView) convertView.findViewById(R.id.image);

                break;
            case SearchResult.NEWS_TYPE_IMAGE_ONE:
            case SearchResult.NEWS_TYPE_IMAGE_TWO:
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.homepage_content_item_pic1, null);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                RelativeLayout.LayoutParams params01 = (RelativeLayout.LayoutParams) holder.image
                        .getLayoutParams();
                params01.width = width;
                params01.height = height;
//                params01.setMargins(mMarginLeft, mMarginTop, 0, 0);
                holder.image.setLayoutParams(params01);

                break;
            case SearchResult.NEWS_TYPE_IMAGE_THREE:

                convertView = LayoutInflater.from(context).inflate(
                        R.layout.homepage_content_item_pic3, null);
                holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.iv1
                        .getLayoutParams();
                params1.width = width;
                params1.height = height;
                holder.iv1.setLayoutParams(params1);

                holder.iv2 = (ImageView) convertView.findViewById(R.id.iv2);
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.iv2
                        .getLayoutParams();
                holder.iv2.setLayoutParams(params2);
                params2.width = width;
                params2.height = height;
                params2.setMargins(mMarginLeft, 0, mMarginLeft, 0);

                holder.iv3 = (ImageView) convertView.findViewById(R.id.iv3);
                LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) holder.iv3
                        .getLayoutParams();
                params3.width = width;
                params3.height = height;
                holder.iv3.setLayoutParams(params3);

                break;
            case SearchResult.NEWS_TYPE_HEADLINETOP:
                convertView = View.inflate(context,
                        R.layout.homepage_content_item_headtop, null);
                holder.image = (ImageView) convertView.findViewById(R.id.image);

                break;
            //新增的专题页面
            case SearchResult.NEWS_TYPE_SPECIAL_TOPIC:
                convertView = View.inflate(context,R.layout.home_special_item_layout,null);
                holder.home_special_date = findView(convertView, R.id.home_special_date);
                holder.home_special_desc = findView(convertView, R.id.home_special_desc);
                holder.home_special_image = findView(convertView, R.id.home_special_image);
                holder.home_special_tag_text = findView(convertView,R.id.home_special_tag_text);
                holder.home_special_title = findView(convertView,R.id.home_special_title);
                break;
            case SearchResult.NEWS_TYPE_CLICK_REFRESH:
                convertView = View.inflate(context,
                        R.layout.homepage_content_item_clickrefresh, null);
                return convertView;
            default:
                convertView = inflateView(R.layout.fragment_rss_list_item);
                break;
        }
        if(SearchResult.NEWS_TYPE_SPECIAL_TOPIC != id){
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.tvHomeHot = (TextView) convertView.findViewById(R.id.tv_home_hot);
            getBottomView(convertView, holder);
        }
        return convertView;
    }

    void getBottomView(View convertView, ViewHolder holder) {
        holder.mBottom = findView(convertView, R.id.home_bottom_layout);
        holder.mAdd = findView(convertView, R.id.home_wgd_ib_add);
        holder.mComeFrom = findView(convertView, R.id.home_wgd_tv_news);
        holder.mFromTime = findView(convertView, R.id.home_wgd_tv_time);
        holder.mRightLinear = findView(convertView, R.id.home_wgd_tx_congraduation);
        holder.mGreatIcon = findView(convertView, R.id.imageView2);
        holder.mCommentIcon = findView(convertView, R.id.imageView3);
        holder.mGreatNums = findView(convertView, R.id.home_wgd_tv_great);
        holder.mCommentNums = findView(convertView, R.id.home_wgd_tv_comment);
        holder.mSRPFrom = findView(convertView, R.id.home_wgd_tv_come);
        holder.tvHomeBottomSpacial = findView(convertView, R.id.tv_home_bottom_spacial);
        holder.tvHomeBottomSpacial.setVisibility(View.GONE);
        holder.mBottom.setVisibility(View.VISIBLE);
    }

    /**
     * 这里确实有问题，会导致卡顿现象
     * @param holder
     * @param data
     */
    void setBottomData(ViewHolder holder, SearchResultItem data) {
        if (holder.mAdd == null) {//如果第一个都找不到，还找后面的干嘛？
            return;
        }
        String source = data.source();
        switch (mBottomType) {
            case BOTTOM_TYPE_HOME:
                holder.mAdd.setVisibility(View.VISIBLE);
                holder.mComeFrom.setVisibility(View.VISIBLE);
                holder.mRightLinear.setVisibility(View.GONE);
                holder.mSRPFrom.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(source)){
                    holder.mComeFrom.setVisibility(View.GONE);
                }else {
                    holder.mComeFrom.setText(source);
                }
                String text = data.getChannelName();
                if (text == null || text.equals("")) {
                    holder.mSRPFrom.setVisibility(View.INVISIBLE);
                } else {
                    holder.mSRPFrom.setVisibility(View.VISIBLE);
                    holder.mSRPFrom.setText(text);
                }

                break;
            case BOTTOM_TYPE_SRP:
                holder.mAdd.setVisibility(View.GONE);
                holder.mComeFrom.setVisibility(View.VISIBLE);
                holder.mRightLinear.setVisibility(View.VISIBLE);
                holder.mSRPFrom.setVisibility(View.GONE);

                long upcount = data.upCount();
                if (upcount > 0) {
                    holder.mGreatIcon.setVisibility(View.VISIBLE);
                    holder.mGreatNums.setVisibility(View.VISIBLE);
                    holder.mGreatNums.setText("" + upcount);
                } else {
                    holder.mGreatIcon.setVisibility(View.GONE);
                    holder.mGreatNums.setVisibility(View.GONE);
                }
                long commentcount = data.commentCount();
                if (commentcount > 0) {
                    holder.mCommentIcon.setVisibility(View.VISIBLE);
                    holder.mCommentNums.setVisibility(View.VISIBLE);
                    holder.mCommentNums.setText("" + commentcount);
                } else {
                    holder.mCommentIcon.setVisibility(View.GONE);
                    holder.mCommentNums.setVisibility(View.GONE);
                }


                if (source == null || source.equals("")) {
                    holder.mComeFrom.setVisibility(View.GONE);
                } else {
                    holder.mComeFrom.setText(source);
                }
                break;
            case BOTTOM_TYPE_CIRCLE:
                holder.mAdd.setVisibility(View.GONE);
                holder.mComeFrom.setVisibility(View.GONE);
                holder.mRightLinear.setVisibility(View.VISIBLE);
                holder.mSRPFrom.setVisibility(View.GONE);

                long upcount1 = data.upCount();
                if (upcount1 > 0) {
                    holder.mGreatIcon.setVisibility(View.VISIBLE);
                    holder.mGreatNums.setVisibility(View.VISIBLE);
                    holder.mGreatNums.setText("" + upcount1);
                } else {
                    holder.mGreatIcon.setVisibility(View.GONE);
                    holder.mGreatNums.setVisibility(View.GONE);
                }
                long commentcount1 = data.commentCount();
                if (commentcount1 > 0) {
                    holder.mCommentIcon.setVisibility(View.VISIBLE);
                    holder.mCommentNums.setVisibility(View.VISIBLE);
                    holder.mCommentNums.setText("" + commentcount1);
                } else {
                    holder.mCommentIcon.setVisibility(View.GONE);
                    holder.mCommentNums.setVisibility(View.GONE);
                }
                break;

        }

        holder.mFromTime.setVisibility(View.VISIBLE);
        holder.setOnClick();
        if (mBottomType == BOTTOM_TYPE_HOME || mBottomType == BOTTOM_TYPE_SRP) {
            holder.mFromTime.setText(StringUtils.convertDate(data.date()));
        } else {
            holder.mFromTime.setText(StringUtils.convertDate(data.pubTime()));

        }
        if (data.category().equals(ConstantsUtils.VJ_NEW_SEARCH) && data.isHeadlineTop()) {
            holder.mBottom.setVisibility(View.GONE);
        } else {
            holder.mBottom.setVisibility(View.VISIBLE);

        }

        if (data.category().equals(ConstantsUtils.FR_INFO_SPECIAL)){//专题标识变蓝
            holder.tvHomeBottomSpacial.setVisibility(View.VISIBLE);
            holder.tvHomeBottomSpacial.setText(ConstantsUtils.FR_INFO_SPECIAL);
        }else if (data.category().equals(ConstantsUtils.FR_INFO_PICTURES)){//图集标识变灰
            holder.tvHomeBottomSpacial.setVisibility(View.VISIBLE);
            holder.tvHomeBottomSpacial.setText(ConstantsUtils.FR_INFO_PICTURES);
        }else{
            holder.tvHomeBottomSpacial.setVisibility(View.GONE);
        }

        if (data.isShowMenu()) {
            holder.mAdd.setVisibility(View.VISIBLE);
        } else {
            holder.mAdd.setVisibility(View.GONE);
        }
    }

    void setBottomType(ViewHolder holder, int _type) {
        if (holder.mAdd == null) {//如果第一个都找不到，还找后面的干嘛？
            return;
        }
        switch (mBottomType) {
            case BOTTOM_TYPE_HOME:
                holder.mAdd.setVisibility(View.VISIBLE);
                holder.mComeFrom.setVisibility(View.VISIBLE);
                holder.mRightLinear.setVisibility(View.GONE);
                holder.mSRPFrom.setVisibility(View.VISIBLE);
                break;
            case BOTTOM_TYPE_SRP:
                holder.mAdd.setVisibility(View.GONE);
                holder.mComeFrom.setVisibility(View.VISIBLE);
                holder.mRightLinear.setVisibility(View.VISIBLE);
                holder.mSRPFrom.setVisibility(View.GONE);
                break;
            case BOTTOM_TYPE_CIRCLE:
                holder.mAdd.setVisibility(View.GONE);
                holder.mComeFrom.setVisibility(View.GONE);
                holder.mRightLinear.setVisibility(View.VISIBLE);
                holder.mSRPFrom.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getViewTypeCount() {
//        return 5;
        return 6;
    }
}
