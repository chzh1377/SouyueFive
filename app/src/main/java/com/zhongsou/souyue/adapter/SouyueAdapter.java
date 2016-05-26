package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoadingDataListener;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SettingsManager;

import java.util.ArrayList;
import java.util.List;

public abstract class SouyueAdapter extends BaseAdapter implements
		OnClickListener {
	protected Context mContext;
	protected LoadingDataListener loadingDataListener;
	protected List<SearchResultItem> datas = new ArrayList<SearchResultItem>();
	protected LayoutInflater viewInflater;
	protected boolean hasMoreItems = false;
	protected View getMore, waiting;
	public static final int TYPE_ITEM_LONGTENG = 4;
	protected static final int TYPE_ITEM_GET_MORE = 5;
	protected static final int TYPE_ITEM_WAITING = 6;
	protected int type_max_count = 0;
	private Object tag;
	protected ViewHolder mholder;
	private Boolean imgAble;
	public boolean isRefresh = false;
	public boolean isNetError = false;
	private String channelTime;
	public String category;
	// 下面用于测量图片宽高
	public int height, width;
	public int deviceWidth;
	public int height08, width08;

	public void setChannelTime(String str) {
		this.channelTime = str;
	}

	public String getChannelTime() {
		return channelTime;
	}

	public void setHasMoreItems(boolean has) {
		this.hasMoreItems = has;
	}

	public boolean getHasMoreItems() {
		return this.hasMoreItems;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public SouyueAdapter(Context context) {
		this.mContext = context;
//		aq = new AQuery(mContext);
		imgAble = true;

		deviceWidth = CircleUtils.getDeviceWidth(context);
		width = (deviceWidth - DeviceUtil.dip2px(context, 48)) / 3;
		height = (int) ((2 * width) / 3);
		width08 = (int) (0.8 * width);
		height08 = (int) (0.8 * height);
	}


	public synchronized void addDatas(List<SearchResultItem> datas) {
		this.datas.clear();
		this.datas.addAll(datas);
//		this.datas = datas;
		if (datas != null && datas.size() > 0) {
			notifyDataSetChanged();
		}
		isRefresh = false;
	}

	// 上拉加载更多
	public synchronized void addMore(List<SearchResultItem> datas) {
                    if(!this.datas.contains(datas.get(0))) {
                        this.datas.addAll(datas);
                        if (datas.size() > 0) {
                            notifyDataSetChanged();
                        }
                    }
	}

	public List<SearchResultItem> getDatas() {
		return datas;
	}

	public String getLastId() {
		if (null != datas && datas.size() > 0)
			return datas.get(datas.size() - 1).id();
		else
			return "0";
	}

	public int dataSize() {
		return datas.size();
	}

	public void addData(SearchResultItem data) {
		this.datas.add(data);
	}

	public void clearDatas() {
		if (this.datas != null) {
			this.datas.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (hasMoreItems && datas.size() > 0)
			return datas.size() + 1;
		else
			return datas.size();

	}

	@Override
	public Object getItem(int position) {
		if (datas.size() > position)
			return datas.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		if ((datas.size() == 0) || (position >= datas.size()))
			return 0;
		else
			return position;
	}

	public void setImgAble(boolean imgAble) {
		this.imgAble = imgAble;
	}

	public boolean isImgAble() {
		return imgAble;
	}

	@Override
	public int getItemViewType(int position) {
		int type = super.getItemViewType(position);
		int len = this.datas.size();
		if (position < len) {
			if (imgAble != null) {
				if (!imgAble) {
					type = SearchResult.NEWS_TYPE_NORMAL;
				} else
					type = datas.get(position).newsLayoutType();
			} else {// 原来的模式，网络变化后可能导致部分值变化
				if (!SettingsManager.getInstance().isLoadImage()) {
					type = SearchResult.NEWS_TYPE_NORMAL;
				} else
					type = datas.get(position).newsLayoutType();
			}
		} else {
			if ((position == len + 1) && hasMoreItems) {
				if (!CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance()))
					type = TYPE_ITEM_GET_MORE;
				else
					type = TYPE_ITEM_WAITING;
			}
		}
		return type;
	}

	@Override
	public int getViewTypeCount() {
		return type_max_count;
	}

	protected View getCurrentFooter(ViewGroup parent) {
		if (CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance()) && !isNetError) {// 网络错误
			if (waiting == null)
				waiting = inflateView(R.layout.refresh_footer);
			waiting.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			// waiting.setMinimumHeight(dp2px(mContext, 60));
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

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	protected void checkGetMore(int currentPositoin) {
		if ((hasMoreItems)
				&& ((this.datas.size() - currentPositoin) <= 1 && (this.datas
						.size() - currentPositoin) > 0)) {
			// 回调 加载更过数据
			if (loadingDataListener != null) {
				SearchResultItem sci = datas.get(datas.size() - 1);
				if (sci == null)
					return;
				if (sci.category() != null) {
					if (ConstantsUtils.VJ_NEW_SEARCH.equals(sci.category())) {
						if (sci.ding()) {
							loadingDataListener.loadDataMore(datas.size(), "1");
						} else {
							loadingDataListener.loadDataMore(sci.start(), "2");
						}
					} else {
						loadingDataListener.loadDataMore(datas.size(), "");
					}
				} else {
					if (sci.weibo() != null
							&& !ConstantsUtils.FR_INFO_PUB.equals(category)) {
						if (sci.weibo().category() == 2) {
							int i = 0;
							for (SearchResultItem s : datas) {
								if (s.weibo().category() == 2)
									i++;
							}
							loadingDataListener.loadDataMore(i, "2");
						} else {
							loadingDataListener.loadDataMore(datas.size(), "1");
						}
					}

					if (ConstantsUtils.FR_INFO_PUB.equals(category)) {
						loadingDataListener.loadDataMore(
								Integer.parseInt(sci.id()), "");
					}
				}

			}
		}
	}

	protected View inflateView(int id) {
		viewInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return viewInflater.inflate(id, null);
	}

	public static class ViewHolder {
		public String descTxt;
		public SearchResultItem searchResultItem;
		public ImageView iv_marked;
		public ImageView iv_pic;
		public ImageView iv_item, iv_item_1, iv_item_2, iv_item_3;
		public ViewGroup container;

		public TextView title, desc, week_rank_text, month_rank_text,
				score_text, pubish_time;
		public TextView isSelfCreate, date, source, answercount, sameaskcount,
				marked, tv_name, follow, fans, weiboCount, replyContent;

		public TextView pubtime;
		public TextView tvHomeHot;
		public TextView status;
		public TextView content;
		public ImageView head;
		public ImageView iv_on_top, iv_jing;
		public LinearLayout gv;
		public LinearLayout line_1, line_2, line_3;
		public List<ImageView> ivs = new ArrayList<ImageView>();
		public TextView weekRank;
		public TextView monthRank;
		public TextView points;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_more:
			isNetError = false;
			notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	public void setLoadingDataListener(LoadingDataListener loadingDataListener) {
		this.loadingDataListener = loadingDataListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance()) && !isRefresh && !isNetError) {
			if (null == waiting)
				checkGetMore(position);
			else if (!waiting.isShown())
				checkGetMore(position);
		}
		if (position == this.datas.size() && hasMoreItems) {
			return getCurrentFooter(parent);// 获得底部的显示更多或者正在加载
		}
		if (convertView == null || convertView == getCurrentFooter(parent)
				|| convertView.getTag() == null) {
			ViewHolder holder = new ViewHolder();
			convertView = getCurrentView(position, convertView, holder);
			convertView.setTag(holder);
		}
		if (datas != null && datas.size() > position)
			setViewData(position, (ViewHolder) convertView.getTag());
		return convertView;
	}

	// 取得当前的item view
	abstract View getCurrentView(int position, View convertView,
			ViewHolder holder);

	public void setFontSize(TextView tv) {
		float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
	}

	abstract void setViewData(int position, ViewHolder holder);

	protected void updateItemColor(ViewHolder holder) {
		boolean hasRead = holder.searchResultItem.hasRead();
		if (holder.title != null)
			// holder.title.setTextColor(hasRead ? 0xff8d8d8d : 0xff303030);
			holder.title.setTextColor(hasRead ? 0xff909090 : 0xff303030);
		/*
		 * if (holder.desc != null) holder.desc.setTextColor(hasRead ?
		 * 0xff8d8d8d : 0xff5c5c5c);
		 */
	}

//	protected void getImageByAquery(View view, String imgUrl) {
//		aq.id(view).image(imgUrl, true, true, 0, 0, null, AQuery.FADE_IN);
//
//	}

//	protected void getImageByAquery(View view, String imgUrl,
//			BitmapAjaxCallback callBack) {
//		aq.id(view).image(imgUrl, true, true, 0, 0, callBack);
//	}

	/**
	 * 布局种类默认有3种 TYPE_ITEM_LONGTENG = 4; TYPE_ITEM_GET_MORE = 5;
	 * TYPE_ITEM_WAITING = 6;
	 */
	protected void setMaxCount(int max) {
		type_max_count = max + 3;
	}

	protected String replaceAllNR(String str) {
		if (str != null)
			return str.trim().replaceAll("\n|\r", "");
		else
			return "";
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
