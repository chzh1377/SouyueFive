package com.zhongsou.souyue.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.ac.IMExpressionListActivity;
import com.zhongsou.souyue.im.ac.IMSettingActivity;
import com.zhongsou.souyue.im.adapter.EmojiAdapter;
import com.zhongsou.souyue.im.adapter.EmojiPagerAdapter;
import com.zhongsou.souyue.im.adapter.ExpressionAdapter;
import com.zhongsou.souyue.im.adapter.GifPagerAdapter;
import com.zhongsou.souyue.im.adapter.HorizonListViewAdapter;
import com.zhongsou.souyue.im.emoji.Emoji;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.emoji.GifPattern;
import com.zhongsou.souyue.im.module.ExpressionBean;
import com.zhongsou.souyue.im.module.ExpressionPackage;
import com.zhongsou.souyue.im.module.ExpressionTab;
import com.zhongsou.souyue.im.module.GifBean;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.util.Constants;
import com.zhongsou.souyue.im.util.FileUtil;
import com.zhongsou.souyue.im.view.CustomerViewPager;
import com.zhongsou.souyue.ui.HorizontalListView;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenbin on 15/4/2.
 */
public class ExpressionView extends LinearLayout {

	private Context mContext;
	private CustomerViewPager mViewPaper;
	private LinearLayout llPageNum;
	private ImageView ivEmoji;// 表情底部栏emoji
	private ImageView ivSouXiaoYue;// 表情底部栏捜小悦
	private ImageView ivCloud;// 表情底部栏云
	private GridView gridView;// 用来显示emoji，GIF表情
	private HorizontalListView mTabsListView;

	private ArrayList<View> mPageViews;
	private List<PagerAdapter> mPageAdapterList = new ArrayList<PagerAdapter>();
	private EmojiAdapter mEmojiAdapter;
	private ArrayList<ImageView> mPointViews;
	private List<List<Emoji>> mEmojis;
	private List<List<GifBean>> mGifbeans;
	private List<EmojiAdapter> mEmojiAdapterList;
	private int mCurrentFace = 0;
	private int mGifFlag = 1;// 用来标识当前表情什么 1--->Emoji,2---->搜小悦,3---->云
	// private OnExpressionListener mExpressionListener;
	private LayoutInflater mLayoutInflater;
	private ImageView ivAdd;

	private List<ExpressionTab> mTabs = new ArrayList<ExpressionTab>();
	private List<List<ExpressionBean>> mExpressinList = new ArrayList<List<ExpressionBean>>();
	private HorizonListViewAdapter mAdapter;

	private ExpressionAdapter mExpressionAdapter;
	private int mCurrentTab = 0; // 显示当前 tab

	private static final int EMOJI_ROWS_NUM = 7; // emoji显示的列数
	private static final int GIF_ROWS_NUM = 5; // gif显示的列数

	private static final int SWITCH_EMOJI = 0; // 切换emoji
	private List<ExpressionAdapter> mExpressionAdapterList = new ArrayList<ExpressionAdapter>(); // 保存当前的表情包中gridview的所有adapter
    private boolean isNew = false;
	/**
	 * 表情点击的接口
	 */
	public interface OnExpressionListener {

		void emojiItemClick(Emoji emoji);

		void gifItemClick(GifBean gifBean);

		void expressionItemClick(ExpressionBean e);
	}

	public ExpressionView(Context context) {
		super(context);

	}

	public ExpressionView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.mContext = context;
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		mLayoutInflater = (LayoutInflater) getContext().getSystemService(
				infService);
		mLayoutInflater.inflate(R.layout.im_emoji_tab, this, true);
		initView();
		setListener();
		mEmojis = EmojiPattern.getInstace().emojiLists;
		mGifbeans = GifPattern.getInstace().gifLists;
		
	}

	/**
	 * 设置tab监听事件
	 */
	private void setListener() {
		mTabsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int size = mAdapter.getCount();
				if (position == size - 1) {
					startActivity();
					return;
				}
				mAdapter.setIndexSelected(position);
				mGifFlag = position;
				mCurrentTab = position;
				switchGif(mCurrentTab);
				mAdapter.notifyDataSetChanged();

			}
		});
		ivAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, IMExpressionListActivity.class);
				mContext.startActivity(intent);
				SYSharedPreferences.getInstance().setNewExpression(false);
				
			}
		});

	}

	private void startActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, IMSettingActivity.class);
		mContext.startActivity(intent);
	}

	/**
	 * 初始化布局
	 */
	private void initView() {
		mViewPaper = (CustomerViewPager) findViewById(R.id.viewpager);
		llPageNum = (LinearLayout) findViewById(R.id.layout_pagenum);
		ivEmoji = (ImageView) findViewById(R.id.iv_emoji);
		ivSouXiaoYue = (ImageView) findViewById(R.id.iv_souxiaoyue);
		ivCloud = (ImageView) findViewById(R.id.iv_cloud);
		this.mTabsListView = (HorizontalListView) this.findViewById(R.id.hlv);
		ivAdd = (ImageView)this.findViewById(R.id.iv_add);
		isNew = SYSharedPreferences.getInstance().hasNewExpression();
		if(isNew){
			ivAdd.setImageResource(R.drawable.new_expression_add);
		}
	}

	/**
	 * 设置emoji表情
	 */
	private void setEmoji() {
		mCurrentTab = 0;
		mAdapter = new HorizonListViewAdapter(mContext, mTabs);
		mTabsListView.setAdapter(mAdapter);
		Init_viewPager();
		Init_Point();
		Init_Data();
	}

	/**
	 * 设置gif
	 * 
	 * @param index
	 */
	private void setGif(int index) {
		String[] packageNamgs;
		String packageName = null;
		File folder = new File(mContext.getFilesDir(),
				Constants.PACKAGE_DOWNURL);
		packageNamgs = FileUtil.list(folder);
		packageName = packageNamgs[0];
	}

	/**
	 * 切换tab
	 * 
	 * @param index
	 */
	private void switchTab(int index) {
		// 清理
		mViewPaper.setAdapter(null);
		clearExpression();

		index -= 1;
		List<ExpressionBean> list = mExpressinList.get(index);
		List<List<ExpressionBean>> beans = GifPattern.getInstace()
				.getExpressionByPage(GifPattern.number, list);
		makeGifViewPager(beans);
		makeGifPoint();
		bindListener();

	}

	/**
	 * 初始化显示表情的viewpager
	 */
	private void Init_viewPager() {
		// viewpager需要的view
		mPageViews = new ArrayList<View>();
		View nullView1 = new View(mContext);
		nullView1.setBackgroundColor(Color.TRANSPARENT);
		mPageViews.add(nullView1);

		mEmojiAdapterList = new ArrayList<EmojiAdapter>();
		for (int i = 0; i < mEmojis.size(); i++) {
			gridView = new GridView(mContext);
			mEmojiAdapter = new EmojiAdapter(mContext, mEmojis.get(i));
			gridView.setAdapter(mEmojiAdapter);
			mEmojiAdapterList.add(mEmojiAdapter);
			// gridView.setOnItemClickListener(this);
			gridView.setNumColumns(EMOJI_ROWS_NUM);
			gridView.setBackgroundColor(Color.TRANSPARENT);
			gridView.setHorizontalSpacing(1);
			gridView.setVerticalSpacing(1);
			gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			gridView.setCacheColorHint(0);
			gridView.setPadding(5, 0, 5, 0);
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			gridView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			gridView.setGravity(Gravity.CENTER);
			mPageViews.add(gridView);
		}

		View nullView2 = new View(mContext);
		nullView2.setBackgroundColor(Color.TRANSPARENT);
		mPageViews.add(nullView2);
	}

	/**
	 * 初始化页点
	 */
	private void Init_Point() {
		mPointViews = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < mPageViews.size(); i++) {
			imageView = new ImageView(mContext);
			imageView.setBackgroundResource(R.drawable.dot_hui);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			llPageNum.addView(imageView, layoutParams);
			if (i == 0 || i == mPageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setBackgroundResource(R.drawable.dot_hei);
			}
			mPointViews.add(imageView);

		}
	}

	/**
	 * 填充数据
	 */
	private void Init_Data() {
		mViewPaper.setAdapter(new EmojiPagerAdapter(mPageViews));

		mViewPaper.setCurrentItem(1);
		mCurrentFace = 0;
		mViewPaper
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						mCurrentFace = arg0 - 1;
						draw_Point(arg0);
						if (arg0 == mPointViews.size() - 1 || arg0 == 0) {
							if (arg0 == 0) {
								mViewPaper.setCurrentItem(arg0 + 1);// 第二屏
								// 会再次实现该回调方法实现跳转.
								mPointViews.get(1).setBackgroundResource(
										R.drawable.dot_hei);
							} else {
								mViewPaper.setCurrentItem(arg0 - 1);// 倒数第二屏
								mPointViews.get(arg0 - 1)
										.setBackgroundResource(
												R.drawable.dot_hei);
							}
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}
				});

	}

	/**
	 * 绘制点
	 */
	public void draw_Point(int index) {
		for (int i = 1; i < mPointViews.size(); i++) {
			if (index == i) {
				mPointViews.get(i).setBackgroundResource(R.drawable.dot_hei);
			} else {
				mPointViews.get(i).setBackgroundResource(R.drawable.dot_hui);
			}
		}
	}

	/**
	 * 设置gifviewPager
	 * 
	 * @param beans
	 */
	private void makeGifViewPager(List<List<ExpressionBean>> beans) {

		mPageViews = new ArrayList<View>();
		View nullView1 = new View(mContext);
		nullView1.setBackgroundColor(Color.TRANSPARENT);
		// 第一页为透明页面，有滑动效果
		mPageViews.add(nullView1);

		mExpressionAdapterList.clear();
		// adapter集合，用户onitemclick时找出对应的item来获取gif
		for (int i = 0; beans != null && i < beans.size(); i++) {
			gridView = new GridView(mContext);
			mExpressionAdapter = new ExpressionAdapter(mContext, beans.get(i));
			gridView.setAdapter(mExpressionAdapter);
			mExpressionAdapterList.add(mExpressionAdapter);
			gridView.setNumColumns(GIF_ROWS_NUM);
			// gridView.setOnItemClickListener(this);
			gridView.setVerticalScrollBarEnabled(false);
			gridView.setBackgroundColor(Color.TRANSPARENT);
			gridView.setHorizontalSpacing(DeviceUtil.dip2px(mContext, 13));
			// gridView.setVerticalSpacing(DeviceUtil.dip2px(mContext, 10));
			gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			gridView.setCacheColorHint(0);
			gridView.setPadding(5, 0, 5, 0);
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			gridView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			gridView.setGravity(Gravity.CENTER);
			mPageViews.add(gridView);
		}

		View nullView2 = new View(mContext);
		nullView2.setBackgroundColor(Color.TRANSPARENT);
		mPageViews.add(nullView2);

		GifPagerAdapter gifPagerAdapter = new GifPagerAdapter(mPageViews);
		mViewPaper.setAdapter(gifPagerAdapter);

		gifPagerAdapter.notifyDataSetChanged();

	}

	private void makeGifPoint() {

		mPointViews = new ArrayList<ImageView>();
		ImageView imageView;

		for (int i = 0; i < mPageViews.size(); i++) {
			imageView = new ImageView(mContext);
			imageView.setBackgroundResource(R.drawable.dot_hei);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			llPageNum.addView(imageView, layoutParams);
			if (i == 0 || i == mPageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setBackgroundResource(R.drawable.dot_hui);
			}
			mPointViews.add(imageView);
		}
	}

	private void bindListener() {

		mViewPaper.setCurrentItem(1);
		mCurrentFace = 0;
		mViewPaper
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						mCurrentFace = arg0 - 1;
						draw_Point(arg0);
						if (arg0 == mPointViews.size() - 1 || arg0 == 0) {
							if (arg0 == 0) {
								mViewPaper.setCurrentItem(arg0 + 1);// 第二屏
								// 会再次实现该回调方法实现跳转.
								mPointViews.get(1).setBackgroundResource(
										R.drawable.dot_hei);
							} else {
								mViewPaper.setCurrentItem(arg0 - 1);// 倒数第二屏
								mPointViews.get(arg0 - 1)
										.setBackgroundResource(
												R.drawable.dot_hei);
							}
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}
				});
	}

	/**
	 * 初始化导航列表
	 * 
	 * @param expPackage
	 */
	public void initTab(List<ExpressionPackage> expPackage) {
		if (expPackage == null)
			new RuntimeException("init expression exception!");

		if (mTabs != null && mExpressinList != null) {
			mTabs.clear();
			mExpressinList.clear();
			clearExpression();
		}
		for (int i = 0; i < expPackage.size(); i++) {
			mTabs.add(expPackage.get(i).getTab());
			mExpressinList.add(expPackage.get(i).getExpressionBeans());
		}

		setEmoji();

	}

	/**
	 * 添加表情包
	 * 
	 * @param mExpressionPackage
	 */
	public void addExpressionPackage(ExpressionPackage mExpressionPackage) {
		if (mTabs == null)
			mTabs = new ArrayList<ExpressionTab>();
		mAdapter.addItem(mExpressionPackage.getTab());
		if (mExpressinList == null)
			mExpressinList = new ArrayList<List<ExpressionBean>>();
		mExpressinList.add(mExpressionPackage.getExpressionBeans());
	}

	// 删除表情包
	public void deleteTabAndPackageBean(PackageBean mPackageBean) {
		ExpressionTab mT2 = getTabByFileName(mPackageBean); // 删除页
		int index = deleteTab(mT2);
		deletePackageBean(index);
		if (mCurrentTab == 0) {
			mAdapter.notifyDataSetChanged();
			return;
		}

		ExpressionTab mT1 = mTabs.get(mCurrentTab - 1); // 当前页

		if (mT2.getPackageName().equals(mT1.getPackageName())) { // 当前页
			setEmoji();
		} else {
			// 啥也不干
			mAdapter.notifyDataSetChanged();
		}
	}

	// 删除tab
	public int deleteTab(ExpressionTab tab) {
		for (int i = 0; i < mTabs.size(); i++) {
			if (tab.equals(mTabs.get(i))) {
				mTabs.remove(i);
				return i;
			}
		}
		return -1;
	}

	public ExpressionTab getTabByFileName(PackageBean mPackageBean) {
		String fileName = mPackageBean.getFileName();
		int index = fileName.indexOf(".");
		fileName = fileName.substring(0, index);
		for (int i = 0; i < mTabs.size(); i++) {
			if (mTabs.get(i).getFileName().equals(fileName)) {
				return mTabs.get(i);
			}
		}
		return null;
	}

	// 删除对应的表情
	public void deletePackageBean(int i) {
		mExpressinList.remove(i);
	}

	/**
	 * 切换下面tab
	 * 
	 * @param index
	 */
	private void switchGif(int index) {
		switch (index) {
		case SWITCH_EMOJI:
			clearExpression();
			setEmoji();
			break;

		default:
			switchTab(index);
			break;
		}
	}

	/**
	 * 清除 点,pageview
	 */
	private void clearExpression() {
		if (mPointViews != null)
			mPointViews.clear();
		if (llPageNum != null)
			llPageNum.removeAllViews();
	}

}
