package com.zhongsou.souyue.circle.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoadingDataListener;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPostListAdapter extends BaseAdapter implements OnClickListener {

	protected LayoutInflater viewInflater;
	private Context context;
//	private AQuery aq;
	protected View getMore;
	public View waiting;
	protected LoadingDataListener loadingDataListener;
	private Animation animation;
	protected int type_max_count = 0;
	private DisplayImageOptions options;
	private ImageLoader imgloader;
//	private Http http;
	private ListView mListView;
	private long interest_id ;

	protected List<CircleResponseResultItem> datas = new ArrayList<CircleResponseResultItem>();
	private final HashMap<Long, BaseViewHolder> m_valueToKey = new HashMap<Long, BaseViewHolder>();

	protected int height, width;
	private int deviceWidth;
	private int height08, width08;
	private String channelTime;
	public static int role = -1; //0-非圈子成员 1-圈主 2-圈子普通成员 3-游客, 初始化给一个无意义的值-1
	public static int is_bantalk;  //是不是被禁言
    public static boolean is_private ; //是否私密圈
//	private int topCount = 0;
	private boolean isRowUpdate = false;
	private String token;
	private long blogId;
	private Posts mainPosts;

	BaseViewHolder baseHolder = null;
//	private CircleFollowDialog circleFollowDialog;
	private Uri imageFileUri;
	private ViewGroup addIMgView;
	private Dialog showDialogAddImg;
	private OnChangeListener onRoleChangeListener;

    private String tag_id; // 圈吧微件要显示的tag_id，如"1,2,3"
    private String onlyjing;// 圈吧微件是否只显示精华数据 “0”，”1“精华
    private boolean isFromEss = true;	//是否是精华区

    private OnChangeListener listener;

    public MyPostListAdapter(Context context, long interest_id, String tag_id, String onlyjing) {
    	this(context  , interest_id);
        this.tag_id = tag_id;
        this.onlyjing = onlyjing;
        isFromEss = false;
    }

    public MyPostListAdapter(Context context, long interest_id){
    	this.context = context;
//        this.aq = aq;
        this.interest_id = interest_id;
        setMaxCount(5);
//        http = new Http(this);
      //getRole();
        this.imgloader = ImageLoader.getInstance();
        this.options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).showImageOnLoading(R.drawable.default_small).displayer(new SimpleBitmapDisplayer()).build();
        initPicSetting();
    }

    public List<CircleResponseResultItem> getDatas(){
        return datas;
    }

    public void setChangeListener(OnChangeListener listener){
        this.listener = listener;
    }
    
    private void initPicSetting(){
    	deviceWidth = CircleUtils.getDeviceWidth(context);
		width = (deviceWidth - dip2px(context, 48)) / 3;
		height = (int) ((2 * width) / 3);
		width08 = (int) (0.8 * width);
		height08 = (int) (0.8 * height);

    }
    

	public long getLastId() {
		if (null != datas && datas.size() > 0)
			if(isFromEss){
				//为了解决精华区列表下拉多页后数据重复的问题
				return Long.parseLong(datas.get(datas.size() - 1).getCreate_time());
			}else{
				return datas.get(datas.size() - 1).getSort_num();
			}
		else
			return 0l;
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public CircleResponseResultItem getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return type_max_count;
	}

	@Override
	public int getItemViewType(int position) {
		int type = super.getItemViewType(position);
		// 数据图片数据
		CircleResponseResultItem item = this.datas.get(position);
		if (item != null) {
			if(item.getPostLayoutType()==2){
				type = 1 ;
			}else{
				type = item.getPostLayoutType();
			}
		}
		return type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BaseViewHolder baseHolder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			baseHolder = new BaseViewHolder(context);
			switch (type) {
				case Constant.TYPE_ITEM_PIC_TOP:
					convertView = inflateView(R.layout.cricle_list_item_top);
					break;
				default:
					convertView = inflateView(R.layout.mypost_list_item);
					break;
			}
			initViewHolder(convertView, baseHolder, type);
			convertView.setTag(baseHolder);
			convertView.setBackgroundResource(R.drawable.circle_list_item_selector);
		} else {
			baseHolder = (BaseViewHolder) convertView.getTag();
		}

		final CircleResponseResultItem item = datas.get(position);
		isDisplay(baseHolder, item, type);
		setViewData(baseHolder, item, type, position);

		return convertView;
	}

	/**
	 * 获取view
	 */
	private void initViewHolder(View convertView, BaseViewHolder baseHolder, int layoutType) {
		baseHolder.title = (TextView) convertView.findViewById(R.id.tv_cricle_title);
		if(layoutType != Constant.TYPE_ITEM_PIC_TOP) {
			// 设置item里按钮点击标志
			baseHolder.tv_add_one = (TextView) convertView.findViewById(R.id.tv_add_one);
			baseHolder.nickname = (TextView) convertView.findViewById(R.id.tv_cricle_nickname);
			if(isFromEss){
				baseHolder.nickname.setVisibility(View.GONE);
			}
			baseHolder.create_time = (TextView) convertView.findViewById(R.id.tv_cricle_create_time);
			baseHolder.ll_cricle_pics = (LinearLayout)convertView.findViewById(R.id.ll_cricle_pics);
			baseHolder.layout_title_icon = (RelativeLayout) convertView.findViewById(R.id.rl_cricle_title_icon);
			baseHolder.tv_good = (TextView)convertView.findViewById(R.id.tv_cricle_good);
			baseHolder.tv_follow= (TextView)convertView.findViewById(R.id.tv_cricle_follow);

			if (layoutType == Constant.TYPE_ITEM_PIC_NO) {
				baseHolder.ll_cricle_pics.setVisibility(View.GONE);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(dip2px(context,15),dip2px(context,10),dip2px(context,15),0);
                baseHolder.layout_title_icon.setLayoutParams(lp);
			}
			if(layoutType == Constant.TYPE_ITEM_PIC_ONE) {
				baseHolder.ll_cricle_pics.setVisibility(View.GONE);
				baseHolder.iv_item_pic1 = (ImageView) convertView.findViewById(R.id.iv_cricle_pic);
				baseHolder.iv_item_pic1.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams params01 = (RelativeLayout.LayoutParams) baseHolder.iv_item_pic1.getLayoutParams();
				params01.width = width08;
				params01.height = height08;
				params01.setMargins(dip2px(context, 20), 0, 0, 0);
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
				params2.setMargins(dip2px(context, 10), 0, dip2px(context, 10), 0);
				baseHolder.iv_item_pic2.setLayoutParams(params2);

				baseHolder.iv_item_pic3 = (ImageView) convertView.findViewById(R.id.iv_cricle_pic3);
				LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) baseHolder.iv_item_pic3.getLayoutParams();
				params3.width = width;
				params3.height = height;
				baseHolder.iv_item_pic3.setLayoutParams(params3);
			}
		}

	}

	/**
	 * 判断是否显示的视图(判断是否加精)
	 */
	private void isDisplay(BaseViewHolder holder, CircleResponseResultItem item, int layoutType) {
		int prime = item.getIs_prime();
		String title = item.getTitle();
		String brief = item.getBrief();

		if(layoutType==Constant.TYPE_ITEM_PIC_TOP){
			showTitle(holder,title,brief);
		}else{
			if(isFromEss){
				showTitle(holder,title,brief);
			}else { //来自圈吧，标题图文混排
				isDisplayTopPrimeIcon(holder, prime, title, brief);
			}
		}
	}

	private void showTitle(BaseViewHolder holder, String title, String brief) {
		String text = StringUtils.isNotEmpty(title) ? title : brief;
		holder.title.setText(text);
	}


	/**
	 * 标题图文混排
	 */
	private void isDisplayTopPrimeIcon(BaseViewHolder holder, int prime,String title, String brief) {
		String text = StringUtils.isNotEmpty(title) ? title : brief;
		SpannableStringBuilder spannableString = new SpannableStringBuilder(text);;
		if (prime == Constant.POST_TYPE_PRIME_YES) {
			spannableString.insert(0, "   ");
			Drawable imgSpanPrime = holder.imgSpanPrime.getDrawable();
			imgSpanPrime.setBounds(0, -(int) (0.25 * imgSpanPrime.getIntrinsicHeight()),imgSpanPrime.getIntrinsicWidth(), imgSpanPrime.getIntrinsicHeight());
			spannableString.setSpan(holder.imgSpanPrime, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.title.setText(spannableString);
		} else {
			holder.title.setText(text);
		}

	}
	
	/**
	 * view绑定数据
	 */
	private void setViewData(BaseViewHolder baseHolder, CircleResponseResultItem item, int layoutType, int position) {
		final long blogId = item.getBlog_id();
		final boolean hasPraised = item.isHas_praised();
		final String goodNum = item.getGood_num();
		final String followNum = item.getFollow_num();

		if (layoutType != Constant.TYPE_ITEM_PIC_TOP) {
			baseHolder.nickname.setText(item.getNickname());
			baseHolder.create_time.setText(StringUtils.convertDate(item.getCreate_time())+"更新");

			if (!TextUtils.isEmpty(goodNum)) {
				if (!"0".equals(goodNum)) {
					baseHolder.tv_good.setText(goodNum);
				} else {
					baseHolder.tv_good.setText("顶");
				}
			}
			if (!TextUtils.isEmpty(followNum)) {
				if (!"0".equals(followNum)) {
					baseHolder.tv_follow.setText(followNum);
				} else {
					baseHolder.tv_follow.setText("跟帖");
				}
			}
			
			baseHolder.tv_good.setTag(blogId);
			baseHolder.tv_follow.setTag(blogId);
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

		m_valueToKey.put(blogId, baseHolder);

		if (isRowUpdate())
			setRowUpdate(false);
	}

	// 上拉加载更多
	public synchronized void addMore(List<CircleResponseResultItem> datas) {
		this.datas.addAll(datas);
		if (datas.size() > 0) {
			notifyDataSetChanged();
		}
	}

	public void clearDatas() {
		if (this.datas != null) {
			this.datas.clear();
		}
		notifyDataSetChanged();
	}

	public void setLoadingDataListener(LoadingDataListener loadingDataListener) {
		this.loadingDataListener = loadingDataListener;
	}

	protected View inflateView(int id) {
		LayoutInflater viewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return viewInflater.inflate(id, null);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	protected void setMaxCount(int max) {
		type_max_count = max + 1;
	}

//	protected void getImageByAquery(View view, String imgUrl) {
//		aq.id(view).image(imgUrl, true, true, 0, 0, null, AQuery.FADE_IN);
//
//	}

    // 判断广播过来的数据是否需要显示，根据tag_id和圈吧显示的tag_id进行匹配
    private boolean isNeedShow(CircleResponseResultItem item) {
        String broadcast_tag_id = item.getBroadcast_tag_id();

        // 默认圈吧，什么帖子都显示
        if(StringUtils.isEmpty(tag_id)) {
            // 选择了分类需要拼装前缀
            if(StringUtils.isEmpty(broadcast_tag_id)) {
                return true;
            }
            String broadcast_tag_name = item.getBroadcast_tag_name();
            String title = item.getTitle();
            if(StringUtils.isNotEmpty(title)) {
                item.setTitle("【" + broadcast_tag_name + "】" + title);
            } else {
                item.setBrief("【" + broadcast_tag_name + "】" + item.getBrief());
            }
            return true;
        }

        // 默认分类只显示在默认圈吧下
        if(StringUtils.isEmpty(broadcast_tag_id) && tag_id.length() > 0) {
            return false;
        }

        String[] ids = tag_id.split(",");
        // 匹配上返回
        for(String tag_id : ids) {
            if(tag_id.equals(broadcast_tag_id)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * ListView单条更新
	 */
	public void updateSingleRow(CircleResponseResultItem item, int type) {
		if (item == null)	
			return ;
		
		long blog_id_new = item.getBlog_id();
		int oldTopCount = 0;
		for (int i = 0; i < this.datas.size(); i++) {
			if (i >= 3) {
				break;
			}
			CircleResponseResultItem item_old = this.datas.get(i);
			int top = item_old.getTop_status();
			if (top == Constant.POST_TYPE_TOP_YES) {
				oldTopCount ++ ;
			}
		}

		// 如果没有包含，则为新增
		if (type == Constant.CIRCLE_BROADCAST_TYPE_ADD ) {
			// 只显示精华数据的圈吧
            if(item.getIs_prime() == 0) {
                if("1".equals(onlyjing) || isFromEss) {
                    return;
                }
            }
            // 发帖选择的分类和圈吧显示的分类不对应，则不需要notifyDataSetChanged
			if (!isNeedShow(item)) {
				return;
			}

			int newTopStatus = item.getTop_status();
			if (newTopStatus == 1) {
				this.datas.add(0, item);
			} else {
                if(this.datas.size() == 0){
                    if(listener != null){
                        listener.onChange(null);
                    }
                }
				this.datas.add(oldTopCount, item);
			}
			notifyDataSetChanged();
			return;
		}
		

		// 删除圈吧列表item
		if (type == Constant.CIRCLE_BROADCAST_TYPE_DEL) {
			for (int i = 0; i < this.datas.size(); i++) {
				CircleResponseResultItem item_old = this.datas.get(i);
				long blog_id_old = item_old.getBlog_id();
				if (blog_id_new == blog_id_old) {
					this.datas.remove(i);
					notifyDataSetChanged();
					return;
				}
			}
		}
		
		// 有跟新（加精、取消加精、跟帖、点赞）后更新列表
		if (type == Constant.CIRCLE_BROADCAST_TYPE_UPDATE) {
			setRowData(item, blog_id_new, type);
		}
		if (isRowUpdate())
			setRowUpdate(false);
	}

	public void setRowData(CircleResponseResultItem item, long blog_id_new, int type) {
		for (int i = 0; i < this.datas.size(); i++) {

			CircleResponseResultItem item_old = this.datas.get(i);
			long blog_id_old = item_old.getBlog_id();
			if (blog_id_new == blog_id_old) {
				setRowUpdate(true);
				item_old.setImages(item.getImages());
				item_old.setIs_prime(item.getIs_prime());
				item_old.setTop_day(item.getTop_day());
				item_old.setTop_status(item.getTop_status());
				item_old.setGood_num(item.getGood_num());
				String follow_num = item.getFollow_num();
				String good_num = item.getGood_num();
				boolean has_praised = item.isHas_praised();
				if (!TextUtils.isEmpty(follow_num)) {
					item_old.setFollow_num(String.valueOf(follow_num));
				}
				if (!TextUtils.isEmpty(good_num)) {
					item_old.setGood_num(good_num);
				}
				item_old.setHas_praised(has_praised);
				
				updateItemAtPosition(item_old);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {

		Long blogId = (Long) v.getTag();
		setBlogId(blogId);

		switch (v.getId()) {
		case R.id.get_more:
			notifyDataSetChanged();
			break;
			
		default:
			break;
		}
	}

	public static class BaseViewHolder {
		public Context context;
		public ImageSpan imgSpanPrime;

		BaseViewHolder(Context context) {
			this.context = context;
			// 加精图片
			Drawable drawable_prime = context.getResources().getDrawable(R.drawable.cricle_list_prime_icon_new);
			drawable_prime.setBounds(0, 0, drawable_prime.getIntrinsicWidth(), drawable_prime.getIntrinsicHeight());
			this.imgSpanPrime = new ImageSpan(drawable_prime, ImageSpan.ALIGN_BASELINE);
		}

		public TextView title;
		public TextView prime;
		public TextView top;
		public TextView nickname;
		public TextView create_time;
		public TextView srp_word;
		public ViewGroup container;
		public String images;
		public RelativeLayout layout_title_icon;
		public LinearLayout ll_cricle_pics ;
		public RelativeLayout rl_bottombar;
		public TextView tv_good;
		public TextView tv_follow;
		public boolean has_praised;
		public TextView tv_add_one;
		public ImageView iv_item_pic1, iv_item_pic2, iv_item_pic3;
	}

	public void setChannelTime(String str) {
		this.channelTime = str;
	}
	public String getChannelTime() {
		return channelTime;
	}
	public long getBlogId() {
		return blogId;
	}
	public void setBlogId(long blogId) {
		this.blogId = blogId;
	}
	public boolean isRowUpdate() {
		return isRowUpdate;
	}
	public void setRowUpdate(boolean isRowUpdate) {
		this.isRowUpdate = isRowUpdate;
	}

	private void updateItemAtPosition(Object target) {
		if(mListView !=null){
			int start = mListView.getFirstVisiblePosition();
			for (int i = start, j = mListView.getLastVisiblePosition(); i <= j; i++)
				if (target == mListView.getItemAtPosition(i)) {
					View view = mListView.getChildAt(i - start);
					mListView.getAdapter().getView(i, view, mListView);
					break;
				}
		}
	}
	
	public Uri getImageFileUri(){
		return imageFileUri;
	}

//	public void saveRecomentCirclesSuccess(HttpJsonResponse res, AjaxStatus status) {
//		if (res.getBody().get("state").getAsInt() == 1) {
//			Toast.makeText(context, "订阅成功", Toast.LENGTH_SHORT).show();
//			UpEventAgent.onGroupJoin(context, interest_id + "." + "", "");// 统计
//			// role = 2;
//			role = Constant.ROLE_NORMAL;
//			SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
//			if (onRoleChangeListener != null) {
//				onRoleChangeListener.onChange(null);
//			}
//		} else {
//			Toast.makeText(context, "订阅失败", Toast.LENGTH_SHORT).show();
//		}
//	}

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        if("savePostsInfo".equals(methodName)){
////            circleFollowDialog.dismissProcessDialog();
//        }
//    }

    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public void setOnRoleChangeListener(OnChangeListener onRoleChangeListener) {
        this.onRoleChangeListener = onRoleChangeListener;
    }

	public void setListView(ListView listView) {
		this.mListView = listView;
	}
}
