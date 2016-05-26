package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.adapter.baselistadapter.ListViewAdapter;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.db.HomePageDBHelper;
import com.zhongsou.souyue.db.homepage.UserHomeList;
import com.zhongsou.souyue.module.Ad;
import com.zhongsou.souyue.module.GroupKeywordItem;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.CrouselItemBean;
import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;
import com.zhongsou.souyue.module.listmodule.SpecialItemData;
import com.zhongsou.souyue.net.news.CommonReq;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.pulltorefresh.CFootView;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.*;
import com.zhongsou.souyue.view.ZSVideoConst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 时政，财经，娱乐，体育，科技，社会列表
 *
 * @author tiansj
 */
@SuppressLint("ValidFragment")
public class SystemRecommendFragmentV2 extends BaseFragment implements  ProgressBarHelper.ProgressBarClickListener, AbsListView.OnScrollListener {

    public static final String SYS_URL = "sysUrl";
    public static final String SYS_CHANNEL = "syschannel";
    public static final String IMGABLE = "imgable";
//    private Http http;
    private View root;
    private PullToRefreshListView pullToRefreshListView;
    private ListViewAdapter adapter;
    private HomeListManager mHomeListManager;
    //    private ImageFetcher mImageFetcher;
    private int psize = 10;//分页大小
    private int visibleLast = 0;
    private int visibleCount = 0;
    private boolean needLoad;
    private boolean isLoadAll = false;

    private String mTitle;
    private String sysUrl;
    private CFootView footerView;
    private ViewFlipper system_ad;
    private ImageButton cancel_longteng_led;
    private LayoutInflater inflater;
    private ProgressBarHelper pbHelper;
//    private TextView getMore;
//    private ProgressBar moreLoading;
    private static final int TYPE_PULL_TO_REFREASH = 1; // 下拉刷新标志
    private static final int TYPE_NORMAL_REFREASH = 0; // 默认加载数据

    HomePageDBHelper mDBHelper;
    private String mUpdateTime;//
    private ImageLoader imgloader;
    private DisplayImageOptions options;
    public static final String ACTION_CHANGER_FONT="update_font";
    private  UpdateBroadCastRecever receiver;
	public static DisplayImageOptions cacheOptions = new DisplayImageOptions.Builder()
			.cacheOnDisk(true).cacheInMemory(false).build();

    public static SystemRecommendFragmentV2 newInstance(GroupKeywordItem  groupkeyworditem) {
        Bundle args = new Bundle();
        SystemRecommendFragmentV2 frag = new SystemRecommendFragmentV2();
        args.putString(SYS_URL, groupkeyworditem.url());
        args.putString(SYS_CHANNEL, groupkeyworditem.title());
        frag.setArguments(args);
        return frag;
    }

    public SystemRecommendFragmentV2() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return root = inflater.inflate(R.layout.fragment_rss_list_v2, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();//更新已读未读的view显示
        }
        mHomeListManager.onResume();
    }
    public void stopPlayVideo()
    {
        if(mHomeListManager!=null)
        {
            mHomeListManager.forceStopPlay();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        try
        {
            mHomeListManager.onPause();
        }catch (Exception e)
        {
            e.printStackTrace();
            mHomeListManager.forceStopPlay();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sysUrl = getArguments().getString(SYS_URL);
            mTitle= getArguments().getString(SYS_CHANNEL);//channel其实是title
        }
//        http = new Http(SystemRecommendFragmentV2.this);
//        http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE_FORCE);
//        adapter = new NewsListAdapterV2(getActivity(), channel);
        mDBHelper = HomePageDBHelper.getInstance();
        IntentFilter inf = new IntentFilter();
        inf.addAction(ACTION_CHANGER_FONT);
        receiver = new UpdateBroadCastRecever();
        getActivity().registerReceiver(receiver, inf);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        mHomeListManager.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListView();
    }

    private void initListView() {

        pbHelper = new ProgressBarHelper(getActivity(), root.findViewById(R.id.ll_data_loading));
        pbHelper.setProgressBarClickListener(this);
        system_ad = (ViewFlipper) root.findViewById(R.id.home_ad_flipper);
        cancel_longteng_led = (ImageButton) root.findViewById(R.id.cancel_longteng_led);
        cancel_longteng_led.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainApplication.sysRecAd = false;
                ((RelativeLayout) system_ad.getParent()).setVisibility(View.GONE);
                system_ad.stopFlipping();
            }
        });
        pullToRefreshListView = (PullToRefreshListView) root.findViewById(R.id.pull_to_refresh_list);
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position > adapter.getCount())
                    return;

                BaseListData item = adapter.getDatas().get(position-1);
                //IntentUtil.skipDetailPage(getActivity(),item,0);
//                HomePagerSkipUtils.skip(context,item);
                item.setHasRead(true);
                item.getInvoke().setChan(mTitle);
                mHomeListManager.clickItem(item);
                setHasRead(item);
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                adapter.getDatas().get(position).hasRead_$eq(true);
                adapter.notifyDataSetChanged();

            }
        });

        //添加底部加载
//        footerView = getActivity().getLayoutInflater().inflate(R.layout.cricle_single_list_refresh_footer, null);
//        //加载失败
//        getMore = (TextView) footerView.findViewById(R.id.get_more);
//        moreLoading = (ProgressBar) footerView.findViewById(R.id.pull_to_refresh_progress);
//        getMore.setFocusableInTouchMode(false);
//        getMore.setOnClickListener(null);
        //添加底部加载
        footerView = (CFootView) getActivity().getLayoutInflater().inflate(R.layout.list_refresh_footer, null);
        footerView.initView();

        adapter = new ListViewAdapter(getActivity(),null);
        mHomeListManager = new HomeListManager(getActivity());
        mHomeListManager.setView(adapter,pullToRefreshListView.getRefreshableView());
        mHomeListManager.setChannel(mTitle);
        mHomeListManager.setBallTitle(mTitle);
        mHomeListManager.setPriority(HomeListManager.PRIORITY_MORE);
        adapter.setManager(mHomeListManager);
        ListView view = pullToRefreshListView.getRefreshableView();
        view.addFooterView(footerView);
        if (isCard(mTitle)) {
            view.setDivider(getResources().getDrawable(R.drawable.home_list_devider_joke));
            view.setDividerHeight(DeviceUtil.dip2px(getActivity(), 5));
        }
        pullToRefreshListView.setAdapter(adapter);
        pullToRefreshListView.setOnScrollListener(this);
        // 下拉刷新监听
//        pullToRefreshListView.onUpdateTime(adapter.getChannelTime());

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                visibleLast = 0;
                visibleCount = 0;
                isLoadAll = false;
                if (pullToRefreshListView.getRefreshableView().getFooterViewsCount() == 0) {
                    pullToRefreshListView.getRefreshableView().addFooterView(footerView);
                }
                mHomeListManager.forceStopPlay();
                mHomeListManager.setmRefreshing(true);
//                http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE);
//                //http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_NETWORK);
//                http.searchResultToPullDownRefresh(sysUrl, "0", 10,SYUserManager.getInstance().getToken());
                CommonReq req = new CommonReq(HttpCommon.YAOWEN_LOAD_PULL,sysUrl,SystemRecommendFragmentV2.this);
                req.setParams("0", 10, true);
                mMainHttp.doRequest(req);
            }
        });

        pullToRefreshListView.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
            @Override
            public void onTimeRefresh() {
                if (null != mUpdateTime) {
                    String time = StringUtils
                            .convertDate(mUpdateTime);
                    pullToRefreshListView.onUpdateTime(time);
                }
                mHomeListManager.setmRefreshing(false);
            }
        });

    }
    /**
     * 如果是图集，段子，gif页分割线将会是卡片类型的
     * @param title
     * @return
     */
    public boolean isCard(String title){
        if (title.equals(ConstantsUtils.FR_INFO_PICTURES)||
                title.equals(ConstantsUtils.FR_INFO_JOKE)||
                title.equals(ConstantsUtils.FR_INFO_GIF)||
                title.equals(ConstantsUtils.FR_INFO_VIDEO)){
            return true;
        }
        return false;
    }

    public void loadDataMore() {
        String lastId = "";
        if (adapter == null){
            lastId = "0";
        }else {
            lastId = getLastId();
        }
//        http.searchResultToLoadMore(sysUrl, lastId, AbstractAQuery.CACHE_POLICY_CACHE);
        CommonReq req = new CommonReq(HttpCommon.YAOWEN_LOAD_MORE,sysUrl,this);
        req.setParams(lastId, false);
        mMainHttp.doRequest(req);
    }

    public String getFirstId(){
        String lastId = "";
        List<BaseListData> datas = adapter.getDatas();
        if (datas == null){
            return "0";
        }
        int count = datas.size();
        if (count == 0){
            return "0";
        }
//        BaseListData data = datas.get(0);
        for (BaseListData data:datas){//从前往后遍历,找到非置顶数据去更新
            if (!data.isLocalTop()){
                lastId = data.getId()+"";
                break;
            }
        }
        return lastId;
    }
    public String getLastId(){
        String lastId = "";
        if (adapter == null){
            return "0";
        }
        List<BaseListData> datas = adapter.getDatas();
        if (datas == null){
            return "0";
        }
        int count = datas.size();
        if (count == 0){
            return "0";
        }
        BaseListData data = datas.get(datas.size()-1);
        lastId = data.getId()+"";
        return lastId;
    }

    private void setListHasRead(List<BaseListData> items) {

        String userid = SYUserManager.getInstance().getUserId();
        if (userid == null) {
            Log.e(this.getClass().getName(), "userid is null");
            return;
        }
        HashMap<String, UserHomeList> map = mDBHelper.getUserListDaoReadCache(userid);
        if(items==null)
        {
            return;
        }
        for (int i =0;i<items.size() ;i++) {
            BaseListData temp = items.get(i);
            if (temp != null) {
                String id_type_time = 0 + "_" + mTitle + "_" + temp.getId();
                UserHomeList lis = map.get(id_type_time);
                if (lis != null && lis.getRead() != null && lis.getRead().equals("1")) {
                    temp.setHasRead(true);
                } else {
                    temp.setHasRead(false);
                }
            }
        }
    }

    private void setHasRead(BaseListData item) {
        final String userid = SYUserManager.getInstance().getUserId();
        if (userid == null) {
//            Utils.makeToastTest(getActivity(), "userid 为 null 了，请查证！");
            return;
        }
        final String dataid = item.getId()+"";
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDBHelper.setHasRead(userid, 0+"", mTitle, dataid);//耗时30-50毫秒
            }
        }).start();
    }

    private void insertAd(List<Ad> sr) {
        if (system_ad != null)
            system_ad.removeAllViews();
        imgloader=ImageLoader.getInstance();
        options=new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.default_small)
                .showImageOnFail(R.drawable.default_small)
                .showImageForEmptyUri(R.drawable.default_small)
                .build();
        for (Ad ad : sr) {
            if (ad.category() != null && !("ios".equals(ad.category().toLowerCase()))) {
                addFlipperView(ad);
            }
        }
        if (system_ad.getChildCount() > 0)
            ((RelativeLayout) system_ad.getParent()).setVisibility(View.VISIBLE);
        if (system_ad.getChildCount() > 1)
            initAnimation();
    }

    private void addFlipperView(final Ad ad) {
        if (inflater == null) {
            ((RelativeLayout) system_ad.getParent()).setVisibility(View.GONE);
            return;
        }
        View view = inflater.inflate(R.layout.list_item_ad_pic_bottom, null);
        view.setVisibility(View.GONE);
        ImageView image = (ImageView) view.findViewById(R.id.iv_image);
        if(imgloader!=null&&options!=null)
            imgloader.displayImage(ad.image(), image, options);
        view.setTag(ad);
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ad.isJumtToSrp()) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), SRPActivity.class);
                    intent.putExtra("keyword", ad.keyword());
                    intent.putExtra("srpId", ad.srpId());
                    intent.putExtra("md5", ad.md5());
                    startActivity(intent);
//                } else if (ad.isJumtToSlot()) {
//                    Intent appIntent = new Intent(getActivity(), TigerGameActivity.class);
//                    startActivity(appIntent);
                } else if (ad.url().endsWith(".apk")) {
                    Uri uri = Uri.parse(ad.url());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WebSrcViewActivity.class);
                    intent.putExtra(WebSrcViewActivity.PAGE_URL, ad.url());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
            }
        });
        system_ad.addView(view);
    }

    private void initAnimation() {
        Animation in, out;
        out = AnimationUtils.loadAnimation(getActivity(), R.anim.ad_up_out);
        in = AnimationUtils.loadAnimation(getActivity(), R.anim.ad_bottom_in);
        system_ad.setOutAnimation(out);
        system_ad.setAnimateFirstView(false);
        system_ad.setInAnimation(in);
        system_ad.setFlipInterval(9000);// 每隔x秒自动显示ViewFlipper里面的图片
        system_ad.startFlipping();// 开始显示
        out.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                cancel_longteng_led.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                cancel_longteng_led.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mHomeListManager !=null){
            mHomeListManager.doSsoHandler(requestCode,resultCode,data);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        int itemsLastIndex = adapter.getCount();
        if (itemsLastIndex < 0) {
            return;
        }
        int lastIndex = itemsLastIndex;
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast >= lastIndex && needLoad) {
            needLoad = false;
            loadDataMore();
            footerView.setLoading();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        visibleCount = visibleItemCount;
        visibleLast = firstVisibleItem + visibleItemCount - 2;
        // 滑动播放的控制
        playVideoController();
    }

    /**
     * 滑动播放的控制
     */
    private void playVideoController()
    {
        ListView listview =pullToRefreshListView.getRefreshableView();
        int PlayPosition =mHomeListManager.getPlayPosition();
        if ((PlayPosition < listview.getFirstVisiblePosition()-1
                || PlayPosition+1 > listview.getLastVisiblePosition())
                && mHomeListManager.getIsPalying()) {
            mHomeListManager.forceStopPlay();
            ZSVideoConst.sendStopBroadcast(context);
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        List datas = request.getResponse();
        boolean hasmore = (Boolean)datas.get(CommonReq.YAOWEN_DATA_INDEX_HASMORE);
        List<BaseListData> topList = (List<BaseListData>) datas.get(CommonReq.YAOWEN_DATA_INDEX_TOPLIST);
        List<BaseListData> focusList = (List<BaseListData>) datas.get(CommonReq.YAOWEN_DATA_INDEX_FOCUS);
        final List<BaseListData> newslist = (List<BaseListData>) datas.get(CommonReq.YAOWEN_DATA_INDEX_NEWSLIST);
        new Thread(new Runnable() {
			@Override
			public void run() {
				 downloadShareImg(newslist);//下载分享图片
			}
		}).start();
        List<Ad> adlist = (List<Ad>) datas.get(CommonReq.YAOWEN_DATA_INDEX_ADLIST);
        updateAdList(adlist);
        switch (request.getmId()){
            //频道都不做增量更新，因此 下拉刷新和第一次更新的逻辑是一样的
            case HttpCommon.YAOWEN_LOAD_LIST:
            case HttpCommon.YAOWEN_LOAD_PULL:

                mUpdateTime = System.currentTimeMillis()+"";
                setDatas(topList, focusList, newslist);
                if (pbHelper!=null) {
                    pbHelper.goneLoading();
                    pbHelper = null;//让gc释放掉
                }
                if (!hasmore){
                    footerView.setLoadDone();
                    needLoad = false;
                }else{
                    needLoad = true;
                }
//                break;
                pullToRefreshListView.onRefreshComplete();
//                //频道都不做增量更新
//                addFirst(topList,focusList,newslist,hasmore);
                break;
            case HttpCommon.YAOWEN_LOAD_MORE:
                adapter.addLast(newslist);
                if (!hasmore){
                    footerView.setLoadDone();
                    needLoad = false;
                }else{
                    needLoad = true;
                }

        }
        setListHasRead(adapter.getDatas());
        adapter.notifyDataSetChanged();
        if (adapter.getCount() == 0) {
            pbHelper.showNoData();
        }
        mHomeListManager.forceStopPlay();
    }

    private void downloadShareImg(List<BaseListData> newslist) {
    	String category = null;
    	String imgUrl = null;
        int newlistLength=newslist.size();
        for (int i =0 ; i < newlistLength;i++) {
            BaseListData data =newslist.get(i);
			category = data.getCategory();
			if(StringUtils.isEmpty(category) || !category.equals(ConstantsUtils.FR_INFO_PICTURES)){
				continue;
			}
			List<String> imgs = data.getImage();
			if (imgs != null && imgs.size() > 0) {
				imgUrl = imgs.get(0);
				if(!TextUtils.isEmpty(imgUrl)){
					imageLoader.loadImage(StringUtils.UpaiYun(imgUrl), cacheOptions, null);
				}else {
					if(data instanceof SigleBigImgBean)
		            {
		                SigleBigImgBean bean = (SigleBigImgBean) data;
		                imgUrl =bean.getBigImgUrl();
		            }else  if(data instanceof SpecialItemData)
		            {
		                SpecialItemData bean = (SpecialItemData) data;
		                imgUrl =bean.getBigImgUrl();
		            }
					imageLoader.loadImage(StringUtils.UpaiYun(imgUrl), cacheOptions, null);
				}
			}
		}
	}

	private void setDatas(List<BaseListData> toplist,List<BaseListData> focusList,List<BaseListData> newslist){
        if (focusList!=null&&focusList.size()>0) {
            CrouselItemBean bean = new CrouselItemBean();
            bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
            bean.setFocus(focusList);
            toplist.add(0, bean);
        }
        adapter.setData(newslist);
        adapter.addFirst(toplist);
    }

    /**
     * 向列表最前面加数据
     * @param toplist
     * @param newslist
     */
    private void addFirst(List<BaseListData> toplist,List<BaseListData> focusList,List<BaseListData> newslist,boolean hasmore){
        if (focusList!=null&&focusList.size()>0) {
            CrouselItemBean bean = new CrouselItemBean();
            bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
            bean.setFocus(focusList);
            bean.setLocalTop(true);
            toplist.add(0, bean);
        }
        if (!hasmore) {
            List<BaseListData> datas = adapter.getDatas();
            List<BaseListData> delete = new ArrayList<BaseListData>();
            for (BaseListData d : datas) {
                if (d.isLocalTop()) {
                    delete.add(d);
                }
            }
            for (BaseListData d : delete) {
                datas.remove(d);
            }
            adapter.setData(datas);
        }else{//下拉如果有更多就将当前页清除
            adapter.clear();
        }
        adapter.addFirst(newslist);
        adapter.addFirst(toplist);
    }

    private void updateAdList(List<Ad> adlist){
        if (MainApplication.sysRecAd && adlist != null && adlist.size() > 0)
            insertAd(adlist);
    }
    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.YAOWEN_LOAD_LIST:
                pbHelper.showNetError();
                break;
            case HttpCommon.YAOWEN_LOAD_PULL:
                pullToRefreshListView.onRefreshComplete();
                break;
            case HttpCommon.YAOWEN_LOAD_MORE:
                UIHelper.ToastMessage(getActivity(),
                        R.string.cricle_manage_networkerror);
                footerView.setNetError();
                needLoad = true;
                break;
        }
    }

    @Override
    public void clickRefresh() {
        needLoad = true;
//        http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE);
//        http.searchResult(sysUrl, "0", SYUserManager.getInstance().getToken());
        CommonReq req = new CommonReq(HttpCommon.YAOWEN_LOAD_LIST,sysUrl,SystemRecommendFragmentV2.this);
        req.setParams("0", false);
        mMainHttp.doRequest(req);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (adapter == null||(adapter != null && adapter.getCount() == 0)) {
                // http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE_FORCE);
//            http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_NETWORK);
//            http.searchResult(sysUrl, "0", SYUserManager.getInstance().getToken());
                CommonReq req = new CommonReq(HttpCommon.YAOWEN_LOAD_LIST, sysUrl, SystemRecommendFragmentV2.this);
                req.setParams("0", 10,false);
                mMainHttp.doRequest(req);
            }

        }
    }

    public class UpdateBroadCastRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (adapter != null && "update_font".equals(action)) {
                //adapter.updateView();
                adapter.notifyDataSetChanged();
//			} else if (action.equals(MyTabFragment.logoutAction)){
//				if (sysp.getBoolean(SYSharedPreferences.KEY_USER_UPDATE, false)) {
//					sysp.remove(SYSharedPreferences.KEY_USER_UPDATE);
//					getBallList(true);
//				}
//			}
            }
        }

    }
}
