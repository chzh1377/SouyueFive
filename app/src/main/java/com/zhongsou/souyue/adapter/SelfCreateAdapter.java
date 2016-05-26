package com.zhongsou.souyue.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoadingDataListener;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.SettingsManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 原创列表数据适配器
 *
 * @author Administrator
 */
public class SelfCreateAdapter extends BaseAdapter implements OnClickListener {
    private static final int VIEW_TYPE_MORE = 0;
    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int MAX_VIEW_TYPE_COUNT = 2;

    private List<SelfCreateItem> listViewData = new ArrayList<SelfCreateItem>();
    //	private List<SelfCreateItem> dbData = new ArrayList<SelfCreateItem>();
    private LoadingDataListener loadingDataListener;
//    private AQuery aq;
    private Activity act;
    public boolean hasMore = false;
    public boolean isLoading = false;
    private View waiting;
    private View getMore;
    private boolean isLoadImage = true;
    private boolean showCheckStatus = true;
    private Context context;

    // 下面用于测量图片宽高
    protected int height, width;
    private int deviceWidth;
    private int height08, width08;

    public SelfCreateAdapter(Activity act) {
        this(act, true);
    }

    public SelfCreateAdapter(Activity act, boolean showCheckStatus) {
        this.context = act;
//        aq = new AQuery(MainApplication.getInstance());
        isLoadImage = SettingsManager.getInstance().isLoadImage();
        this.act = act;
        this.showCheckStatus = showCheckStatus;

        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = (deviceWidth - DeviceUtil.dip2px(context, 48)) / 3;
        height = (int) ((2 * width) / 3);
        width08 = (int) (0.8 * width);
        height08 = (int) (0.8 * height);
    }

    public void clearData() {
        this.listViewData.clear();
    }

    public void clearDraft() {
        List<SelfCreateItem> dels = new ArrayList<SelfCreateItem>();
        for (SelfCreateItem t : listViewData) {
            if (t.status() == ConstantsUtils.STATUS_SEND_ING) {
                dels.add(t);
            }
        }
        for (SelfCreateItem it : dels) {
            listViewData.remove(it);
        }
    }

    public void clearNetData() {
        List<SelfCreateItem> dels = new ArrayList<SelfCreateItem>();
        for (SelfCreateItem t : listViewData) {
            if (t.status() != ConstantsUtils.STATUS_SEND_ING) {
                dels.add(t);
            }
        }
        for (SelfCreateItem it : dels) {
            listViewData.remove(it);
        }
    }

    //	public void clearDBdata() {
//		this.dbData.clear();
//	}
//
//	public void addDBData(List<SelfCreateItem> data) {
//		paixu(data);
//		this.dbData = data;
//	}
//
    private List<SelfCreateItem> paixu(List<SelfCreateItem> data) {
        Collections.reverse(data);
        return data;
    }
//
//	private List<SelfCreateItem> removes(List<SelfCreateItem> data) {
//		try {
//			for (SelfCreateItem s : this.dbData) {
//				for (int i = 0; i < data.size(); i++) {
//					if (s.id().equals(data.get(i).id()))
//						data.remove(i);
//				}
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return data;
//	}

    public void addData(List<SelfCreateItem> _data) {
        List<SelfCreateItem> contain = new ArrayList<SelfCreateItem>();
        for (SelfCreateItem item1 : _data) {
            boolean isContain = false;
            for (SelfCreateItem item2 : listViewData) {
                if (item1.id().equals(item2.id())) {
                    isContain = true;
                }
            }
            if (!isContain) {
                contain.add(item1);
            }
        }
        listViewData.addAll(contain);
    }

//	public void addMore(List<SelfCreateItem> data) {
//		isLoading = false;
////		this.listViewData.addAll(removes(data));
//        addData(data);
//		notifyDataSetChanged();
//	}
//
//	//加入本地数据库数据（草稿数据）
//	public void addRefData(List<SelfCreateItem> data) {
////		this.listViewData.addAll(this.dbData);
////		this.listViewData.addAll(removes(data));
//        addData(data);
//		notifyDataSetChanged();
//	}
//	//不加入本地数据库数据（草稿数据）
//	public void addRefDataFromFriend(List<SelfCreateItem> data) {
////		this.listViewData.addAll(removes(data));
//        addData(data);
//		notifyDataSetChanged();
//	}

    public String getLastId() {
        if (null != listViewData && listViewData.size() > 0)
            return listViewData.get(listViewData.size() - 1).id();
        return "";
    }

    private View inflateView(int id) {
        LayoutInflater viewInflater = (LayoutInflater) MainApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return viewInflater.inflate(id, null);
    }

    @Override
    public int getViewTypeCount() {
        return MAX_VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        int len = this.listViewData.size();
        if (position == len && hasMore)
            return VIEW_TYPE_MORE;
        else
            return VIEW_TYPE_CONTENT;

    }

    public void setHasMore(boolean hasMore){
        this.hasMore = hasMore;
    }

    @Override
    public int getCount() {
        if (hasMore && listViewData.size() > 5)
            return listViewData.size() + 1;
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
            return getCurrentFooter(parent);// 获得底部的显示更多或者正在加载
        }
        ViewHolder holder = null;
        if (cv == null || cv == getCurrentFooter(parent)) {
            holder = new ViewHolder();
            cv = inflateView(R.layout.self_create_list_item);
            holder.gv = findView(cv, R.id.self_create_photo_layout);
            holder.content = findView(cv, R.id.tv_self_create_content);
            holder.pubtime = findView(cv, R.id.tv_self_create_pubtime);
            holder.status = findView(cv, R.id.tv_self_create_status);
            holder.title = findView(cv, R.id.self_create_title_txt);
            holder.head = findView(cv, R.id.iv_self_create_head);
            holder.d = findView(cv, R.id.self_d);
            holder.p = findView(cv, R.id.self_p);
            holder.publishSite = findView(cv, R.id.self_create_list_item_publish_in_ll);
            holder.publishItemText = findView(cv, R.id.self_create_list_item_publish_in);
            holder.line_1 = findView(cv, R.id.line_1);
            holder.image = findView(cv, R.id.self_create_image);
            holder.ivs.add((ImageView) findView(cv, R.id.photo_1_1));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_1_2));
            holder.ivs.add((ImageView) findView(cv, R.id.photo_1_3));
            cv.setTag(holder);
        }
        setViewData(position, (ViewHolder) cv.getTag());

        return cv;
    }

    private static final int DEFAULT_IMAGE_ID = R.drawable.default_image;

    private void setViewData(int position, final ViewHolder tag) {
        Log.i("", "position : setViewData position --->" + position);
        Log.i("", "position : lv size --->" + getCount());
        if (null == tag)
            return;
        if (position >= listViewData.size())
            return;
        SelfCreateItem sci = (SelfCreateItem) getItem(position);
        if (sci == null)
            return;
        setPublishSite(sci, tag);

        tag.pubtime.setText(StringUtils.convertDate(sci.pubtime()));
        if (sci.title().equals("")) {
            tag.title.setVisibility(View.GONE);
        } else {
            tag.title.setVisibility(View.VISIBLE);
            tag.title.setText(sci.title() == null ? replace(sci.content()) : replace(sci.title()));
        }

        String text = tag.title.getText().toString();
        /*ViewTreeObserver observer = tag.title.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver vto = tag.title.getViewTreeObserver();
                vto.removeOnGlobalLayoutListener(this);
                count = tag.title.getLineCount();
//                    System.out.println("lineCount=" + count);
                if (count == 1) {
                    tag.publishSite.setPadding(0, SingleCricleListAdapter.dip2px(context, 15), 0, 0);
                } else {
                    tag.publishSite.setPadding(0, SingleCricleListAdapter.dip2px(context, 10), 0, 0);
                }
            }
        });*/
        tag.d.setText(sci.upCount() + "");
        tag.p.setText(sci.commentCount() + "");

        if (showCheckStatus) {
            switch (sci.status()) {
                case ConstantsUtils.STATUS_SEND_REVIEW: // 审核中
                    tag.status.setVisibility(View.VISIBLE);
                    tag.status.setBackgroundResource(R.drawable.checking);
                    tag.status.setText("审核中");
                    tag.status.setTextColor(android.graphics.Color.parseColor("#499fc9"));
                    break;
                case ConstantsUtils.STATUS_SEND_PASS: // 审核通过
                    tag.status.setVisibility(View.GONE);
                    // tag.status.setText("审核通过");
                    break;
                case ConstantsUtils.STATUS_SEND_NOPASS: // 审核未通过
                    tag.status.setVisibility(View.VISIBLE);
                    tag.status.setText("未通过");
                    break;
                case ConstantsUtils.STATUS_SEND_FAIL: // 发送失败
                    tag.status.setVisibility(View.VISIBLE);
                    tag.status.setText("发送失败");
                    break;
                case ConstantsUtils.STATUS_SEND_ING: // 发送中(现在视为草稿)
                    tag.status.setVisibility(View.VISIBLE);
                    tag.status.setText("草稿");
                    tag.publishSite.setVisibility(View.VISIBLE);
                    tag.publishItemText.setVisibility(View.GONE);
                    break;
            }
        } else {
            tag.status.setVisibility(View.GONE);
        }
        final List<String> ss = sci.conpics();
        tag.title.post(new Runnable() {
            @Override
            public void run() {
                int count = tag.title.getLineCount();
                if (null == ss || ss.size() <= 0 || !isLoadImage) {
                    if (count == 1) {
                        tag.publishSite.setPadding(0, DeviceUtil.dip2px(context, 5), 0, 0);
                    } else {
                        tag.publishSite.setPadding(0, DeviceUtil.dip2px(context, 10), 0, 0);
                    }
                }
            }
        });
        if (null == ss || ss.size() <= 0 || !isLoadImage) {
            tag.gv.setVisibility(View.GONE);
            tag.image.setVisibility(View.GONE);
        } else if (ss.size() == 1 || ss.size() == 2) {
            tag.image.setVisibility(View.VISIBLE);
            tag.gv.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params01 = (RelativeLayout.LayoutParams) tag.image
                    .getLayoutParams();
            params01.width = width08;
            params01.height = height08;
            params01.setMargins(DeviceUtil.dip2px(context, 20), 0, 0, 0);
            tag.image.setLayoutParams(params01);
            tag.image.setTag(new TagObj(ss, 0));
            //aq.id(tag.image).image(ss.get(0), true, true);
            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,ss.get(0),tag.image, MyDisplayImageOption.defaultOption);
            tag.publishSite.setPadding(0, DeviceUtil.dip2px(context, 10), 0, 0);
        } else {
            tag.image.setVisibility(View.GONE);
            tag.gv.setVisibility(View.VISIBLE);
            tag.line_1.setVisibility(View.VISIBLE);
            tag.publishSite.setPadding(0, DeviceUtil.dip2px(context, 10), 0, 0);
            try {
                int size = ss.size() > 3 ? 3 : ss.size();
                for (int i = 0; i < size; i++) {
                    for (int j = ss.size() - 1; j < 3; j++) {
                        tag.ivs.get(j).setVisibility(View.INVISIBLE);
                    }
                    tag.ivs.get(i).setTag(new TagObj(ss, i));
                    // if (!isScrolling)
                   // aq.id(tag.ivs.get(i)).image(ss.get(i), true, true);
                    PhotoUtils.showCard(PhotoUtils.UriType.HTTP,ss.get(i),tag.ivs.get(i),MyDisplayImageOption.defaultOption);
                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) tag.ivs.get(i)
                            .getLayoutParams();
                    params1.width = width;
                    params1.height = height;
                    tag.ivs.get(i).setLayoutParams(params1);
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
            } catch (Exception ex) {
                Log.i("", "selfcreate image count error ");
            }
        }
    }

    private void setPublishSite(SelfCreateItem sci, ViewHolder tag) {
        if (sci.keyword().equals("")) {
            tag.publishSite.setVisibility(View.GONE);
        } else {
            tag.publishSite.setVisibility(View.VISIBLE);
            tag.publishItemText.setText(" " + sci.keyword());
        }
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
        public TextView publishItemText;
        public TextView pubtime;
        public TextView fa_bu_zai;
        public TextView status;
        public TextView title;
        public TextView content;
        public ImageView head;
        //		public RelativeLayout gv;
        public ImageView image;

        public LinearLayout gv;
        public LinearLayout line_1;
        private List<ImageView> ivs = new ArrayList<ImageView>();

        public RelativeLayout publishSite;
        public TextView d, s, p;
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T findView(View v, int id) {
        return (T) v.findViewById(id);
    }

    protected View getCurrentFooter(ViewGroup parent) {
        if (isLoading) {// 正在加载
            if (waiting == null)
                waiting = inflateView(R.layout.refresh_footer);
            waiting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
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

    @Override
    public void onClick(View v) {
        isLoading = true;
        if (null != loadingDataListener)
            loadingDataListener.loadDataMore(0, "");
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
}
