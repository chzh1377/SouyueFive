package com.zhongsou.souyue.circle.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoadingDataListener;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.circle.activity.CircleSelImgGroupActivity;
import com.zhongsou.souyue.circle.activity.DetailActivity;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.circle.view.CircleFollowDialogNew;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.InterestSubscriberReq;
import com.zhongsou.souyue.net.detail.AddCommentUpReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CircleListAdapter extends BaseAdapter implements OnClickListener, IVolleyResponse {

	protected LayoutInflater viewInflater;
	private Context context;
	protected View getMore;
	public View waiting;
	protected LoadingDataListener loadingDataListener;
	private Animation animation;
	protected int type_max_count = 0;
	private DisplayImageOptions options;
	private ImageLoader imgloader;
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
    private String keyWord;

	BaseViewHolder baseHolder = null;
	private CircleFollowDialogNew circleFollowDialog;
	private Uri imageFileUri;
	private ViewGroup addIMgView;
	private Dialog showDialogAddImg;
	private OnChangeListener onRoleChangeListener;
	
    private String tag_id; // 圈吧微件要显示的tag_id，如"1,2,3"
    private String onlyjing;// 圈吧微件是否只显示精华数据 “0”，”1“精华
    private boolean isFromEss = true;	//是否是精华区

    private OnChangeListener listener;
    private String mUrl;
    private float fontSize;

    public CircleListAdapter(Context context, long interest_id, String tag_id, String onlyjing) {
    	this(context , interest_id);
        this.tag_id = tag_id;
        this.onlyjing = onlyjing;
        isFromEss = false;
    }
    
    public CircleListAdapter(Context context, long interest_id){
    	this.context = context;
        this.interest_id = interest_id;
        setMaxCount(5);
      //getRole();
        this.imgloader = ImageLoader.getInstance();
        this.options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).
                showImageOnLoading(R.drawable.default_small)
                .showImageOnFail(R.drawable.default_small)
                .showImageForEmptyUri(R.drawable.default_small)
                .displayer(new SimpleBitmapDisplayer()).build();
        initAddImgLayout();
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
	
	private void initAddImgLayout(){
    	LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	addIMgView = (ViewGroup) mLayoutInflater.inflate(R.layout.circle_follow_add_img_menu,null, false);
    	TextView textView_xiangce = (TextView) addIMgView.findViewById(R.id.textView_xiangce);
		TextView textView_photo = (TextView) addIMgView.findViewById(R.id.textView_photo);
		TextView textView_cancle= (TextView) addIMgView.findViewById(R.id.textView_cancel);
		textView_cancle.setOnClickListener(new OnClickListener() {   
            @Override
            public void onClick(View v) {
            	showDialogAddImg.dismiss();
            }
        });
		textView_xiangce.setOnClickListener(new OnClickListener() {   
            @Override
            public void onClick(View v) {
            	showDialogAddImg.dismiss();
            	jumpImgGroup();
            }
        });
		textView_photo.setOnClickListener(new OnClickListener() {  
            @Override
            public void onClick(View v) {
            	showDialogAddImg.dismiss();
            	jumpTakePhoto();
            }
        });
    }

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public CircleResponseResultItem getItem(int position) {
		if(datas.size()==0)
		{
			return null;
		}
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
			convertView = inflateView(R.layout.cricle_list_item);
			initViewHolder(convertView, baseHolder, type);
			convertView.setTag(baseHolder);
			convertView.setBackgroundResource(R.drawable.circle_index_list_item_selector);
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
		fontSize = SYSharedPreferences.getInstance().loadResFont(context);
		baseHolder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
//		if(layoutType != Constant.TYPE_ITEM_PIC_TOP) {
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

            baseHolder.tvPrimeSign= (TextView)convertView.findViewById(R.id.tv_circle_home_prime);
            baseHolder.tvTopSign= (TextView)convertView.findViewById(R.id.tv_circle_home_top);
            baseHolder.tvPrimeSign.setVisibility(View.GONE);
            baseHolder.tvTopSign.setVisibility(View.GONE);

			baseHolder.tv_good.setOnClickListener(this);
			baseHolder.tv_follow.setOnClickListener(this);
			
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
				params01.setMargins(dip2px(context, 20), 8, 0, 0);
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
//		}

	}

	/**
	 * 判断是否显示的视图(判断是否加精)
	 */
	private void isDisplay(BaseViewHolder holder, CircleResponseResultItem item, int layoutType) {
		int prime = item.getIs_prime();
		String title = item.getTitle();
		String brief = item.getBrief();
        int top = item.getTop_status();

//		if(layoutType==Constant.TYPE_ITEM_PIC_TOP){
//			showTitle(holder,title,brief);
//		}else{
			if(isFromEss){
				showTitle(holder,title,brief);
			}else { //来自圈吧，标题图文混排
				try {
					//诡异的空指针，我也只能这样了。
					isDisplayTopPrimeIcon(holder, prime, title, brief,top);
				}catch (Exception e){

				}
			}
//		}
	}

	private void showTitle(BaseViewHolder holder, String title, String brief) {
		String text = StringUtils.isNotEmpty(title) ? title : brief;
		holder.title.setText(text);
	}


	/**
	 * 标题图文混排
	 */
	private void isDisplayTopPrimeIcon(BaseViewHolder holder, int prime,String title, String brief,int top_stauts) {
		String text = StringUtils.isNotEmpty(title) ? title : brief;
		if(top_stauts == Constant.POST_TYPE_TOP_YES && prime == Constant.POST_TYPE_PRIME_YES){
            String textAll = "          "+text;
            holder.tvTopSign.setVisibility(View.VISIBLE);
            holder.tvPrimeSign.setVisibility(View.VISIBLE);
            holder.tvTopSign.setTextSize(fontSize-4);
            holder.tvPrimeSign.setTextSize(fontSize-4);
            holder.tvTopSign.setPadding((int)(fontSize/5.0),(int)(fontSize/5.0), (int) (fontSize/5.f),(int)(fontSize/5.0));
            holder.tvPrimeSign.setPadding((int) (fontSize / 5.0), (int) (fontSize / 5.0), (int) (fontSize / 5.f), (int) (fontSize / 5.0));
            holder.title.setText(textAll);

        }
        if (prime == Constant.POST_TYPE_PRIME_YES && top_stauts != Constant.POST_TYPE_TOP_YES) {
            String textAll = "      "+text;
            holder.tvPrimeSign.setVisibility(View.VISIBLE);
            holder.tvTopSign.setVisibility(View.GONE);
            holder.tvPrimeSign.setTextSize(fontSize - 4);
            holder.tvPrimeSign.setPadding((int) (fontSize / 5.0), (int) (fontSize / 5.0), (int) (fontSize / 5.f), (int) (fontSize / 5.0));
            holder.title.setText(textAll);
		}

        if(top_stauts == Constant.POST_TYPE_TOP_YES && prime != Constant.POST_TYPE_PRIME_YES){
            String textAll = "      "+text;
            holder.tvTopSign.setVisibility(View.VISIBLE);
            holder.tvPrimeSign.setVisibility(View.GONE);
            holder.tvTopSign.setTextSize(fontSize-4);
            holder.tvTopSign.setPadding((int)(fontSize/5.0),(int)(fontSize/5.0), (int) (fontSize/5.f),(int)(fontSize/5.0));
            holder.title.setText(textAll);
        }
        
        if(prime != Constant.POST_TYPE_PRIME_YES && top_stauts != Constant.POST_TYPE_TOP_YES){
			holder.title.setText(text);
            holder.tvPrimeSign.setVisibility(View.GONE);
            holder.tvTopSign.setVisibility(View.GONE);
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
        baseHolder.mData = item;
//		if (layoutType != Constant.TYPE_ITEM_PIC_TOP) {
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
			if (hasPraised) {
				baseHolder.has_praised = true;
				baseHolder.tv_good.setEnabled(false);
				Drawable drawable= context.getResources().getDrawable(R.drawable.cricle_list_item_good_press_icon);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				baseHolder.tv_good.setCompoundDrawables(drawable,null,null,null);
				baseHolder.tv_good.setTextColor(Color.parseColor("#f3585c"));
			} else {
				baseHolder.has_praised = false;
				baseHolder.tv_good.setEnabled(true);
				Drawable drawable= context.getResources().getDrawable(R.drawable.cricle_list_item_good_icon);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				baseHolder.tv_good.setCompoundDrawables(drawable,null,null,null);
				baseHolder.tv_good.setTextColor(Color.parseColor("#959595"));
			}
			if(layoutType == Constant.TYPE_ITEM_PIC_ONE) {
				if (baseHolder.iv_item_pic1 != null) {
					//this.imgloader.displayImage(StringUtils.UpaiYun(item.getImages().get(0)), baseHolder.iv_item_pic1, options);
                                            PhotoUtils.showCard(PhotoUtils.UriType.HTTP,StringUtils.UpaiYun(item.getImages().get(0)),baseHolder.iv_item_pic1, options);
				}
			}
			if(layoutType == Constant.TYPE_ITEM_PIC_THREE){
				if (baseHolder.iv_item_pic1 != null) {
					//this.imgloader.displayImage(StringUtils.UpaiYun(item.getImages().get(0)), baseHolder.iv_item_pic1, options);
                                            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,StringUtils.UpaiYun(item.getImages().get(0)),baseHolder.iv_item_pic1, options);
				}
				if (baseHolder.iv_item_pic2 != null) {
					//this.imgloader.displayImage(StringUtils.UpaiYun(item.getImages().get(1)), baseHolder.iv_item_pic2, options);
                                            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,StringUtils.UpaiYun(item.getImages().get(1)),baseHolder.iv_item_pic2, options);
				}
				if (baseHolder.iv_item_pic3 != null) {
					//this.imgloader.displayImage(StringUtils.UpaiYun(item.getImages().get(2)), baseHolder.iv_item_pic3, options);
                                            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,StringUtils.UpaiYun(item.getImages().get(2)),baseHolder.iv_item_pic3, options);
				}
			}
//		}

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
//              aq.id(view).image(imgUrl, true, true, 0, 0, null, AQuery.FADE_IN);
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
		if (type == Constant.CIRCLE_BROADCAST_TYPE_UPDATE || type == Constant.CIRCLE_BROADCAST_TYPE_EDIT) {
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
                if(type == Constant.CIRCLE_BROADCAST_TYPE_EDIT){
                    item_old.setTitle(item.getTitle());
                }
				item_old.setImages(item.getImages());
				item_old.setIs_prime(item.getIs_prime());
				item_old.setPostLayoutType(item.getPostLayoutType());
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

        baseHolder = m_valueToKey.get(blogId);
        CircleResponseResultItem item = baseHolder.mData;
        mainPosts = new Posts();
        mainPosts.setMblog_id(blogId);
        mainPosts.setGood_num(baseHolder.tv_good.getText().toString());
        mainPosts.setHas_praised(baseHolder.has_praised);
        mainPosts.setSign_id(item.getSign_id());
        mainPosts.setSrpId(item.getSrp_id());
        mainPosts.setNew_srpid(item.getNew_srpid());
        mainPosts.setKeyword(item.getSrp_word());
        mainPosts.setUser_id(item.getUser_id());
        mUrl = UrlConfig.HOST+"interest/interest.content.groovy?blog_id="+blogId;
        String mParamUrl = "";
        try{
            mParamUrl = URLEncoder.encode("http://interest.zhongsou.com?sign_id=" + mainPosts.getSign_id() + "&blog_id=" + blogId+ "&sign_info=" + blogId + "&srpid=" + mainPosts.getNew_srpid() + "&srpword =" + keyWord, "utf-8");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

		switch (v.getId()) {
		case R.id.get_more:
			notifyDataSetChanged();
			break;
			
		case R.id.tv_cricle_good:
            if(!isFastDoubleClick()){   //防止过快点赞
                token = SYUserManager.getInstance().getToken();
                if (!CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance())) {
                    UIHelper.ToastMessage(context, R.string.cricle_manage_networkerror);
                    return;
                }
//                http.getPraisePostCount(token, blogId);
//                http.commentUp(keyWord,mainPosts.getNew_srpid(),mParamUrl,SYUserManager.getInstance().getToken(), DetailActivity.DEVICE_COME_FROM,DetailActivity.UP_TYPE_MAIN,0,"","","","","",mainPosts.getUser_id());
				AddCommentUpReq req = new AddCommentUpReq(HttpCommon.DETAIL_ADDUP_ID,this);
				req.setParams(keyWord,mainPosts.getNew_srpid(),mParamUrl,SYUserManager.getInstance().getToken(),
						DetailActivity.DEVICE_COME_FROM,DetailActivity.UP_TYPE_MAIN,
						0,"","","","","",mainPosts.getUser_id());
				CMainHttp.getInstance().doRequest(req);
				baseHolder = m_valueToKey.get(getBlogId());
                baseHolder.tv_good.setEnabled(false);
            }

			break;
			
		case R.id.tv_cricle_follow:
			// 判断用户是否登陆
			if (!SouyueAPIManager.isLogin() && is_private) {   //5.0.7游客可以评论公开圈
				SouyueAPIManager.goLoginForResult(((FragmentActivity) context), CircleIndexActivity.REQUEST_CODE_LOGIN_ACTIVITY);
				return;
			} else if(role == Constant.ROLE_NONE && is_private) { // 非圈成员  //5.0.7非圈成员可以评论公开圈
//            	SouYueToast.makeText(context, R.string.please_subscribe_interest, Toast.LENGTH_SHORT).show();
				showJoinInterest();
            	return;
            } else if(is_bantalk == Constant.MEMBER_BAN_TALK_YES){
            	SouYueToast.makeText(context, "您已被禁言", Toast.LENGTH_SHORT).show();
            	return;
            }else {
				//当前页跟帖
                circleFollowDialog = new CircleFollowDialogNew(context,this,mParamUrl,DetailActivity.DEVICE_COME_FROM,mainPosts.getNew_srpid(),keyWord,mainPosts);
                circleFollowDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        circleFollowDialog.saveInfo(mUrl);
                    }
                });
                circleFollowDialog.setListener(new OnChangeListener() {

                    @Override
                    public void onChange(Object obj) {
                        if(circleFollowDialog.getImgLen()== 0){
                            jumpImgGroup();
                        }

                    }
                });
				circleFollowDialog.setmInterestId(interest_id+"");
                circleFollowDialog.setPhotoListener(new OnChangeListener() {

                    @Override
                    public void onChange(Object obj) {
                        jumpTakePhoto();
                    }
                });
                circleFollowDialog.setAddImgListener(new OnChangeListener() {

                    @Override
                    public void onChange(Object obj) {
                        showAddImgMenu();
                    }
                });
                circleFollowDialog.showDialog();
                circleFollowDialog.setEditText(SYSharedPreferences.getInstance().getString(mUrl + "_text",""));
                String strImg = SYSharedPreferences.getInstance().getString(mUrl + "_img", "");
                if(strImg != null && !strImg.equals("")){
//                    List<String> list = JSON.parseArray(strImg, String.class);
					List<String> list = new Gson().fromJson(strImg,new TypeToken<List<String>>(){}.getType());
                    if(list != null &&list.size() != 0){
                        circleFollowDialog.addImagePath(list);
                    }
                }
            }
			break;
		default:
			break;
		}
	}

	public static class BaseViewHolder {
		public Context context;
//		public ImageSpan imgSpanPrime;
//        public ImageSpan imgSpanTop;
        public CircleResponseResultItem mData;

		BaseViewHolder(Context context) {
			this.context = context;
			// 加精置顶图片
//			Drawable drawable_prime = context.getResources().getDrawable(R.drawable.cricle_list_prime_icon_new);
//			drawable_prime.setBounds(0, 0, drawable_prime.getIntrinsicWidth(), drawable_prime.getIntrinsicHeight());
//            Drawable drawable_top = context.getResources().getDrawable(R.drawable.cricle_list_item_top_icon);
//            drawable_prime.setBounds(0, 0, drawable_prime.getIntrinsicWidth(), drawable_prime.getIntrinsicHeight());
//			this.imgSpanPrime = new ImageSpan(drawable_prime, ImageSpan.ALIGN_BOTTOM);
//            this.imgSpanTop = new ImageSpan(drawable_top,ImageSpan.ALIGN_BOTTOM);
		}

		public TextView title;
		public TextView tvPrimeSign;
		public TextView tvTopSign;
		public TextView nickname;
		public TextView create_time;
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
        public String sign_id;
        public String srp_id;
        private String srp_word;
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

	/**
	 * 点赞成功回调
	 */
	public void commentUpSuccess(HttpJsonResponse res) {
//		int statusCode = res.getCode();
//		if (statusCode != 200) {
//			return;
//		}
//		String responseResult = res.getBody().toString();
//		if (!TextUtils.isEmpty(responseResult)) {
//			String state = JSON.parseObject(responseResult).getString("state");
//			switch (Integer.parseInt(state)) {
//			case 0: // //0-失败 1-成功 3-帖子不存在 4-已经点过赞
//				baseHolder = m_valueToKey.get(getBlogId());
//				baseHolder.tv_good.setEnabled(true);
//				Drawable drawable= context.getResources().getDrawable(R.drawable.cricle_list_item_good_icon);
//				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//				baseHolder.tv_good.setCompoundDrawables(drawable,null,null,null);
//				updateItemAtPosition(baseHolder);
//				break;
//			case 1:
                updateGoodIcon();
//				break;
//            case 4:
//                updateGoodIcon();
//                break;
//			default:
//				break;
//			}
//		}
	}

    private void updateGoodIcon() {
        baseHolder = m_valueToKey.get(getBlogId());
        baseHolder.tv_good.setEnabled(false);
		Drawable drawable= context.getResources().getDrawable(R.drawable.cricle_list_item_good_press_icon);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		baseHolder.tv_good.setCompoundDrawables(drawable,null,null,null);
        baseHolder.has_praised = true;
        animation = AnimationUtils.loadAnimation(context, R.anim.addone);
        baseHolder.tv_add_one.setVisibility(View.VISIBLE);
        baseHolder.tv_add_one.startAnimation(animation);
        baseHolder.tv_add_one.setVisibility(View.INVISIBLE);
        baseHolder.tv_good.setTextColor(Color.parseColor("#f3585c"));

        for (int i = 0; i < this.datas.size(); i++) {
            CircleResponseResultItem item_old = this.datas.get(i);
            long blog_id_old = item_old.getBlog_id();
            long blog_id_temp = getBlogId();
            if (blog_id_old == blog_id_temp) {
                item_old.setHas_praised(true);
                String text = baseHolder.tv_good.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    if (!text.equals("顶")) {
                        baseHolder.tv_good.setText(Integer.parseInt(text) + 1 + "");
                        item_old.setGood_num(Integer.parseInt(text) + 1 + "");
                    } else {
                        baseHolder.tv_good.setText(1 + "");
                        item_old.setGood_num(1 + "");
                    }
                }
                updateItemAtPosition(item_old);
                break;
            }
        }
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
	
	private void jumpImgGroup() {
		Intent intent = new Intent(context,CircleSelImgGroupActivity.class);
		intent.putExtra("piclen", circleFollowDialog.getImgLen());
		if(isFromEss){
			((FragmentActivity) context).startActivityForResult(intent, 100);
		}else{
			((FragmentActivity) context).startActivityForResult(intent, 1);
		}
		
	}

	private void jumpTakePhoto() {
		if(circleFollowDialog.getImgLen() >= 9){
			Toast.makeText(context, "最多选择9张图片", Toast.LENGTH_LONG).show();
			return;
		}
		try {
		    imageFileUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
		    if (imageFileUri != null) {
		        Intent i = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
		        i.putExtra( MediaStore.EXTRA_OUTPUT,imageFileUri);
		        if (Utils.isIntentSafe(((FragmentActivity) context), i)) {
		        	if(isFromEss){
		        		((FragmentActivity) context).startActivityForResult(i, 200);
		        	}else{
		        		((FragmentActivity) context).startActivityForResult(i, 2);
		        	}
		        	
		        } else {
		            SouYueToast.makeText(context,context.getString(R.string.dont_have_camera_app),SouYueToast.LENGTH_SHORT).show();
		        }
		    } else {
		        SouYueToast.makeText(context, context.getString(R.string.cant_insert_album),SouYueToast.LENGTH_SHORT).show();
		    }
		} catch (Exception e) {
		    SouYueToast.makeText(context,context.getString(R.string.cant_insert_album),SouYueToast.LENGTH_SHORT).show();
		}
	}
	
	private void showAddImgMenu(){
		showDialogAddImg = DetailActivity.showAlert(context, addIMgView, Gravity.BOTTOM);
	}
	
	public CircleFollowDialogNew getCircleFollowDialog(){
		return circleFollowDialog;
	}
	
	public Uri getImageFileUri(){
		return imageFileUri;
	}

	/**
	 * 回帖成功回调
	 */
	public void commentDetailSuccess(HttpJsonResponse res) {
			UIHelper.ToastMessage(context,R.string.comment_detail_success);
            //成功后清空保存的数据
            SYSharedPreferences.getInstance().putString(mUrl + "_text","");
            SYSharedPreferences.getInstance().putString(mUrl+"_img","");

			// 统计 圈贴跟帖  ToDO
//			UpEventAgent.onGroupComment(context, interest_id + "." + "", "",
//					circleFollowDialog.getMainPosts().getBlog_id() + "");
			Posts published = circleFollowDialog.getMainPost();
			CircleResponseResultItem item = new CircleResponseResultItem();
			item.setBlog_id(published.getMblog_id());
			item.setGood_num(published.getGood_num());
			item.setHas_praised(published.isHas_praised());
			
			for (int i = 0; i < this.datas.size(); i++) {
	            CircleResponseResultItem item_old = this.datas.get(i);
	            long blog_id_old = item_old.getBlog_id();
	            long blog_id_temp = getBlogId();
	            if (blog_id_old == blog_id_temp) {
	                String text = baseHolder.tv_follow.getText().toString();
	                if (!TextUtils.isEmpty(text)) {
	                    if (!text.equals("跟帖")) {
	                        baseHolder.tv_follow.setText(Integer.parseInt(text) + 1 + "");
	                        item_old.setFollow_num(Integer.parseInt(text) + 1 + "");
	                    } else {
	                        baseHolder.tv_follow.setText(1 + "");
	                        item_old.setFollow_num(1 + "");
	                    }
	                }
	                updateItemAtPosition(item_old);
	                break;
	            }
	        }
		circleFollowDialog.dismissProcessDialog();
	}

	private void showJoinInterest() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("您还不是该圈的成员，是否立即加入？")
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						if (is_private) {// 判断是私密圈还是公开圈，进行跳转,私密圈，进行申请
							IntentUtil.gotoSecretCricleCard(context, id);
						} else {// 公开圈，直接加入圈子
//							Map<String, Object> params = new HashMap<String, Object>();
//							params.put("token", SYUserManager.getInstance()
//									.getToken() + "");
//							params.put("interest_ids", interest_id);
//							http.saveRecomentCircles(params);
							loadSaveRecomentCircles(SYUserManager.getInstance().getToken(),interest_id+"");
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				}).create().show();
	}

	/**
	 *  加载数据-----订阅兴趣圈
	 * @param token
	 * @param interest_id
	 */
	private void loadSaveRecomentCircles(String token,String interest_id)
	{
		InterestSubscriberReq req =  new InterestSubscriberReq(HttpCommon.CIRLCE_INTEREST_SUB_ID,this);
		req.setParams(token,interest_id, ZSSdkUtil.CIRCLEINDEX_SUBSCRIBE_GROUP);
		CMainHttp.getInstance().doRequest(req);

	}
	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId())
		{
			case HttpCommon.CIRLCE_INTEREST_SUB_ID:
				saveRecomentCirclesSuccess((HttpJsonResponse)request.getResponse());
				break;
			case HttpCommon.DETAIL_ADDUP_ID:
				commentUpSuccess(request.<HttpJsonResponse>getResponse());
				break;
			case HttpCommon.DETAIL_COMMENTDETAIL_ID:
				commentDetailSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		switch (request.getmId())
		{
			case HttpCommon.CIRLCE_INTEREST_SUB_ID:
				IHttpError error1 = request.getVolleyError();
				if (error1.getErrorType() == IHttpError.TYPE_SERVER_ERROR) {
					UIHelper.ToastMessage(context,
							R.string.cricle_manage_save_circle_failed);
				} else {
					UIHelper.ToastMessage(context,R.string.networkerror);
				}
				break;
			case HttpCommon.DETAIL_COMMENTDETAIL_ID:
				if(circleFollowDialog!=null)
				{
					circleFollowDialog.dismissProcessDialog();
				}
				Toast.makeText(context, "评论失败", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public void onHttpStart(IRequest request) {

	}

	public void saveRecomentCirclesSuccess(HttpJsonResponse res ) {
		if (res.getBody().get("state").getAsInt() == 1) {
			Toast.makeText(context, "订阅成功", Toast.LENGTH_SHORT).show();
			UpEventAgent.onGroupJoin(context, interest_id + "." + "", "");// 统计
			// role = 2;
			role = Constant.ROLE_NORMAL;
			SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
			if (onRoleChangeListener != null) {
				onRoleChangeListener.onChange(null);
			}
		} else {
			Toast.makeText(context, "订阅失败", Toast.LENGTH_SHORT).show();
		}
	}

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        if("commentDetail".equals(methodName)){
//            circleFollowDialog.dismissProcessDialog();
//            if(status.getCode() != 200) {
//                Toast.makeText(context, "评论失败", Toast.LENGTH_SHORT).show();
//            }
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

    public void setKeyWord(String keyWord){
        this.keyWord = keyWord;
    }
}
