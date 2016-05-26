package com.zhongsou.souyue.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoadingDataListener;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.SettingsManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySharesAdapter extends SouyueAdapter implements OnClickListener {
    private static final int VIEW_TYPE_MORE = 0;
    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int MAX_VIEW_TYPE_COUNT = 2;

    private List<SelfCreateItem> listViewData = new ArrayList<SelfCreateItem>();
    public List<SelfCreateItem> dbData = new ArrayList<SelfCreateItem>();
    private LoadingDataListener loadingDataListener;
    private Activity act;
    public boolean hasMore = false;
    public boolean isLoading = false;
    private View waiting;
    private View getMore;
    private boolean isLoadImage = true;

    public MySharesAdapter(Activity act) {
        super(act);
        isLoadImage = SettingsManager.getInstance().isLoadImage();
        this.act = act;
    }

    public String getLastId() {
        if (null != listViewData && listViewData.size() > 0) return listViewData.get(listViewData.size() - 1).id();
        return "";
    }

    @Override
    public int getViewTypeCount() {
        super.getViewTypeCount();
        return MAX_VIEW_TYPE_COUNT;
    }

    public void clearNetData() {
        this.listViewData.clear();
    }

    public void clearDBdata() {
        this.dbData.clear();
    }

    public void addDBData(List<SelfCreateItem> data) {
        this.dbData = data;
    }

    private List<SelfCreateItem> removes(List<SelfCreateItem> data) {
        try {
            for (SelfCreateItem s : this.dbData) {
                for (int i = 0; i < data.size(); i++) {
                    if (s.id().equals(data.get(i).id())) data.remove(i);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public void addMores(List<SelfCreateItem> data) {
        isLoading = false;
        this.listViewData.addAll(removes(data));
        notifyDataSetChanged();
    }

    public void addRefData(List<SelfCreateItem> data) {
        this.listViewData.addAll(this.dbData);
        this.listViewData.addAll(removes(data));
        notifyDataSetChanged();
    }
    
    
    

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        int len = this.listViewData.size();
        if (position == len && hasMore)
            return VIEW_TYPE_MORE;
        else
            return VIEW_TYPE_CONTENT;

    }

    @Override
    public int getCount() {
        super.getCount();
        if (hasMore && listViewData.size() > 5) return listViewData.size() + 1;
        return listViewData.size();
    }

    @Override
    public Object getItem(int position) {
        if (listViewData.size() > position)
            return listViewData.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View cv, ViewGroup parent) {
        if (getItemViewType(position) == VIEW_TYPE_MORE) {
            if (null != loadingDataListener && hasMore) {
                loadingDataListener.loadDataMore(listViewData.size(), "");
            }
            return getCurrentFooter(parent);// 获得底部的显示更多或者正在加载
        }
        ViewHolder holder;
        if (cv == null || cv == getCurrentFooter(parent)) {
            holder = new ViewHolder();
            cv = inflateView(R.layout.my_shares_list_item);
            holder.gv = findView(cv, R.id.self_create_photo_layout);
            holder.content = findView(cv, R.id.tv_self_create_content);
            holder.pubtime = findView(cv, R.id.tv_self_create_pubtime);
            holder.status = findView(cv, R.id.tv_self_create_status);
            holder.title = findView(cv, R.id.self_create_title_txt);
            holder.head = findView(cv, R.id.iv_self_create_head);
            holder.line_1 = findView(cv, R.id.line_1);
            holder.line_2 = findView(cv, R.id.line_2);
            holder.line_3 = findView(cv, R.id.line_3);
            holder.weekRank = findView(cv, R.id.my_shares_list_item_week_rank);
            holder.monthRank = findView(cv, R.id.my_shares_list_item_month_rank);
            holder.points = findView(cv, R.id.my_shares_list_item_points);
            holder.ivs.add((ImageView) findView(cv, R.id.photo_1_1));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_1_2));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_1_3));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_2_1));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_2_2));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_2_3));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_3_1));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_3_2));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_3_3));
            //float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
    		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            cv.setTag(holder);
        }
        setViewData(position, (ViewHolder) cv.getTag());
        
        return cv;
    }

    private void setViewData(int position, ViewHolder tag) {

        if (null == tag) return;
        if (position >= listViewData.size()) return;
        SelfCreateItem sci = (SelfCreateItem) getItem(position);
        if (sci == null) return;
        setPublishSite(sci, tag);
        tag.content.setText(StringUtils.truncate(sci.content().trim(), 180));
        tag.pubtime.setText(StringUtils.convertDate(sci.pubtime()));
        tag.title.setText(sci.title() == null ? "" : replace(sci.title()));
        switch (sci.status()) {
            case ConstantsUtils.STATUS_SEND_REVIEW: // 审核中
                tag.status.setText("审核中");
                break;
            case ConstantsUtils.STATUS_SEND_PASS: // 审核通过
                tag.status.setText("审核通过");
                break;
            case ConstantsUtils.STATUS_SEND_NOPASS: // 审核未通过
                tag.status.setText("审核未通过");
                break;
            case ConstantsUtils.STATUS_SEND_FAIL: // 发送失败
                tag.status.setText("发送失败");
                break;
            case ConstantsUtils.STATUS_SEND_ING: // 发送中
                tag.status.setText("发送中");
                break;
        }
        switch ((int) sci.column_type()) {
            case ConstantsUtils.TYPE_BLOG_SEARCH:
                tag.title.setVisibility(View.VISIBLE);
                tag.head.setImageDrawable(MainApplication.getInstance().getResources().getDrawable(R.drawable.icon_blog));
                break;
            case ConstantsUtils.TYPE_BBS_SEARCH:
                tag.title.setVisibility(View.VISIBLE);
                tag.head.setImageDrawable(MainApplication.getInstance().getResources().getDrawable(R.drawable.icon_bbs));
                break;
            case ConstantsUtils.TYPE_WEIBO_SEARCH:
                tag.title.setVisibility(View.GONE);
                tag.head.setImageDrawable(MainApplication.getInstance().getResources().getDrawable(R.drawable.icon_weibo));
                break;
        }
        List<String> ss = sci.conpics();
        if (null == ss || ss.size() <= 0 || !isLoadImage) {
            tag.gv.setVisibility(View.GONE);
        } else {
            tag.gv.setVisibility(View.VISIBLE);
            if (ss.size() <= 3) {
                tag.line_1.setVisibility(View.VISIBLE);
                tag.line_2.setVisibility(View.GONE);
                tag.line_3.setVisibility(View.GONE);
            } else if (ss.size() <= 6) {
                tag.line_1.setVisibility(View.VISIBLE);
                tag.line_2.setVisibility(View.VISIBLE);
                tag.line_3.setVisibility(View.GONE);
            } else {
                tag.line_1.setVisibility(View.VISIBLE);
                tag.line_2.setVisibility(View.VISIBLE);
                tag.line_3.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < ss.size(); i++) {
                for (int j = ss.size() - 1; j < 9; j++) {
                    tag.ivs.get(j).setVisibility(View.INVISIBLE);
                }
                tag.ivs.get(i).setTag(new TagObj(ss, i));
                // if (!isScrolling)
                //aq.id(tag.ivs.get(i)).image(ss.get(i), true, true);
                PhotoUtils.showCard(UriType.HTTP, ss.get(i), tag.ivs.get(i), MyDisplayImageOption.smalloptions);
                tag.ivs.get(i).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TagObj map = (TagObj) v.getTag();
                        Intent intent = new Intent();
                        intent.setClass(act, TouchGalleryActivity.class);
                        TouchGallerySerializable tg = new TouchGallerySerializable();
                        tg.setItems(map.urls);
                        tg.setClickIndex(map.pos);
                        Bundle extras = new Bundle();
                        extras.putSerializable("touchGalleryItems", tg);
                        intent.putExtras(extras);
                        act.startActivity(intent);
                    }
                });
            }
        }
    }

    private void setPublishSite(SelfCreateItem sci, ViewHolder tag) {
        tag.weekRank.setText(sci.wrank() + "");
        tag.monthRank.setText(sci.mrank() + "");
        tag.points.setText(sci.score() + "");
    }

    class TagObj {
        public List<String> urls;
        public int pos;

        public TagObj(List<String> urls, int i) {
            this.urls = urls;
            this.pos = i;
        }
    }

    public static class ViewHolder {
        public TextView pubtime;
        public TextView status;
        public TextView title;
        public TextView content;
        public ImageView head;
        public LinearLayout gv;
        public LinearLayout line_1, line_2, line_3;
        private List<ImageView> ivs = new ArrayList<ImageView>();
        public TextView weekRank;
        public TextView monthRank;
        public TextView points;
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T findView(View v, int id) {
        return (T) v.findViewById(id);
    }

    protected View getCurrentFooter(ViewGroup parent) {
        if (CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance()) && !isNetError) {// 正在加载
            if (waiting == null) waiting = inflateView(R.layout.refresh_footer);
            waiting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {}
            });
            waiting.setMinimumHeight(70);
            waiting.setBackgroundResource(R.drawable.list_view_item_selector);
            return waiting;
        } else {
            if (getMore == null) {
                getMore = inflateView(R.layout.get_more);
                getMore.setFocusableInTouchMode(false);
                TextView m = (TextView) getMore.findViewById(R.id.get_more);
                m.setOnClickListener(this);
            }
            return getMore;
        }

    }

  /*  // 上拉加载更多
    public synchronized void addMores(List<SelfCreateItem> datas) {
        isLoading = false;
        this.listViewData.addAll(datas);
        notifyDataSetChanged();
    }
*/
    @Override
    public void onClick(View v) {
        if (null != loadingDataListener) {
            isNetError = false;
            loadingDataListener.loadDataMore(listViewData.size(), "");
        }
        notifyDataSetChanged();
    }

    public void setLoadingDataListener(LoadingDataListener loadingDataListener) {
        this.loadingDataListener = loadingDataListener;
    }

    private String replace(String str) {
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        }
        return "";
    }

    @Override
    View getCurrentView(int position, View convertView, com.zhongsou.souyue.adapter.SouyueAdapter.ViewHolder holder) {
        return null;
    }

    @Override
    void setViewData(int position, com.zhongsou.souyue.adapter.SouyueAdapter.ViewHolder holder) {

    }
    
    public void clearDatas() {
        if (this.listViewData != null) {
            this.listViewData.clear();
        }
        notifyDataSetChanged();
    }

}
