//package com.zhongsou.souyue.fragment;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.Animation.AnimationListener;
//import android.view.animation.AnimationUtils;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.ViewFlipper;
//import com.androidquery.AbstractAQuery;
//import com.androidquery.callback.AjaxStatus;
//import com.zhongsou.souyue.MainApplication;
//import com.zhongsou.souyue.R;
//import com.zhongsou.souyue.activity.LoadingDataListener;
//import com.zhongsou.souyue.activity.SRPActivity;
//import com.zhongsou.souyue.activity.WebSrcViewActivity;
//import com.zhongsou.souyue.adapter.NewsListAdapter;
//import com.zhongsou.souyue.im.util.MyDisplayImageOption;
//import com.zhongsou.souyue.im.util.PhotoUtils;
//import com.zhongsou.souyue.module.Ad;
//import com.zhongsou.souyue.module.SearchResult;
//import com.zhongsou.souyue.module.SearchResultItem;
//import com.zhongsou.souyue.net.Http;
//import com.zhongsou.souyue.net.IHttpListener;
//import com.zhongsou.souyue.ui.ProgressBarHelper;
//import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
//import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
//import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
//import com.zhongsou.souyue.utils.IntentUtil;
//import com.zhongsou.souyue.utils.SYUserManager;
//import com.zhongsou.souyue.utils.SettingsManager;
//import com.zhongsou.souyue.utils.StringUtils;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * 时政，财经，娱乐，体育，科技，社会列表
// *
// * @author zhangliang01@zhongsou.com
// */
//@SuppressLint("ValidFragment")
//public class SystemRecommendFragment extends Fragment implements LoadingDataListener, IHttpListener, ProgressBarClickListener, Serializable {
//    private static final long serialVersionUID = 6764934175201289274L;
//    public static final String TAG = SystemRecommendFragment.class.getSimpleName();
//    public static final String SYS_URL = "sysUrl";
//    public static final String IMGABLE = "imgable";
//    public static final String POSITION = "pos";
//    public static final String HASREAD = "hasread";
//
//    public PullToRefreshListView pullToRefreshListView;
//    private ViewFlipper system_ad;
//    public NewsListAdapter newsAdapter;
//    private ProgressBarHelper pbHelper;
//    private ImageButton cancel_longteng_led;
//    private boolean imgAble;
//    private SearchResult searchResult;
//    private View wonderfulView;
//    private LayoutInflater inflater;
//    private String sysUrl;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        this.inflater = inflater;
//        wonderfulView = inflater.inflate(R.layout.fragment_wonderful_list, null);
//        pullToRefreshListView = (PullToRefreshListView) wonderfulView.findViewById(R.id.list_data);
//        system_ad = (ViewFlipper) wonderfulView.findViewById(R.id.home_ad_flipper);
//        cancel_longteng_led = (ImageButton) wonderfulView.findViewById(R.id.cancel_longteng_led);
//        cancel_longteng_led.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                MainApplication.sysRecAd = false;
//                ((RelativeLayout) system_ad.getParent()).setVisibility(View.GONE);
//                system_ad.stopFlipping();
//            }
//        });
//        newsAdapter = new NewsListAdapter(getActivity());
//        newsAdapter.setLoadingDataListener(this);
//        pbHelper = new ProgressBarHelper(getActivity(), wonderfulView.findViewById(R.id.ll_data_loading));
//        pbHelper.setProgressBarClickListener(this);
//
//        pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent();
////				Bundle bundle = new Bundle();
////				bundle.putSerializable("list", (ArrayList<SearchResultItem>) newsAdapter.getDatas());
////                IntentCacheHelper.getInstance(List.class).setObject(newsAdapter.getDatas());
////                IntentCacheHelper.getInstance(List.class).setListFlag(true);
//                // newsAdapter.updateView(position - 1, lv);
////                i.putExtra("position", position - 1);
////                i.setClass(getActivity(), ReadabilityActivity.class);
//////				i.putExtras(bundle);
////                startActivityForResult(i, 0);
////                getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                IntentUtil.skipDetailPage(getActivity(),newsAdapter.getDatas().get(position),0);
//            }
//
//        });
//        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//
//            @Override
//            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//                // 重新请求数据
//                newsAdapter.isRefresh = true;
//                htp.searchResultToPullDownRefresh(sysUrl, "0",15, SYUserManager.getInstance().getToken());
//            }
//
//        });
//        pullToRefreshListView.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
//            @Override
//            public void onTimeRefresh() {
//                if (null != newsAdapter.getChannelTime())
//                    pullToRefreshListView.onUpdateTime(StringUtils.convertDate(newsAdapter.getChannelTime()));
//            }
//        });
//        return wonderfulView;
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        sysUrl = getArguments().getString(SYS_URL);
//        getAdapterImageAble();
//        newsAdapter.isRss = false;
//        pullToRefreshListView.setAdapter(newsAdapter);
//
//        if (newsAdapter.dataSize() == 0 && !TextUtils.isEmpty(sysUrl))
////			getNavData(sysUrl);
//            htp.searchResult(sysUrl, "0", SYUserManager.getInstance().getToken());
//        else {
//            pullToRefreshListView.onUpdateTime(newsAdapter.getChannelTime());
//            pbHelper.goneLoading();
//        }
//        if (searchResult != null && MainApplication.sysRecAd && searchResult.adList() != null && searchResult.adList().size() > 0)
//            insertAd(searchResult.adList());
//
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    public void updateHasRead(int[] readPos) {
//        if (newsAdapter != null) {
//            List<SearchResultItem> datas = newsAdapter.getDatas();
//            boolean needNotiy = false;
//            if (datas != null && datas.size() == readPos.length) {
//                for (int i = 0; i < readPos.length; i++) {
//                    if (readPos[i] == 1 && !datas.get(i).hasRead()) {
//                        datas.get(i).hasRead_$eq(true);
//                        needNotiy = true;
//                    }
//                }
//            }
//            if (needNotiy)
//                newsAdapter.notifyDataSetChanged();
//        }
//
//    }
//
//    @Override
//    public void loadDataMore(long start, String type) {// 滑动到底部自动获取更多
//        if (newsAdapter.getHasMoreItems()) {
//            htp.searchResultToLoadMore(sysUrl, newsAdapter == null ? "0" : newsAdapter.getLastId(), AbstractAQuery.CACHE_POLICY_CACHE);
//        }
//    }
//
//    public void searchResultSuccess(final SearchResult sr, AjaxStatus as) {
//        if (Http.isWifi(getActivity()))
//            newsAdapter.setImgAble(true);
//        else
//            setAdapterImageAble();
//        this.searchResult = sr;
//        pbHelper.goneLoading();
//        newsAdapter.setChannelTime(as.getTime().getTime() + "");
//        newsAdapter.setHasMoreItems(sr.hasMore());
//        newsAdapter.addDatas(sr.items());
//        if (sr.hasExpired() && CMainHttp.getInstance().isNetworkAvailable())
//            pullToRefreshListView.startRefresh();
//        if (MainApplication.sysRecAd && sr.adList() != null && sr.adList().size() > 0)
//            insertAd(sr.adList());
//    }
//
//    public void searchResultToLoadMoreSuccess(final SearchResult sr) {
//        newsAdapter.setHasMoreItems(sr.hasMore());
//        newsAdapter.addMore(sr.items());
//    }
//
//    public void searchResultToPullDownRefreshSuccess(final SearchResult sr, AjaxStatus as) {
//        if (Http.isWifi(getActivity()))
//            newsAdapter.setImgAble(true);
//        else
//            setAdapterImageAble();
//        pullToRefreshListView.onRefreshComplete();
//        newsAdapter.setChannelTime(as.getTime().getTime() + "");
//        newsAdapter.setHasMoreItems(sr.hasMore());
//        newsAdapter.addDatas(sr.items());
//        if (MainApplication.sysRecAd && sr.adList() != null && sr.adList().size() > 0)
//            insertAd(sr.adList());
//    }
//
//    private void insertAd(List<Ad> sr) {
//        if (system_ad != null)
//            system_ad.removeAllViews();
//        for (Ad ad : sr) {
//            if (ad.category() != null && !("ios".equals(ad.category().toLowerCase()))) {
//                addFlipperView(ad);
//            }
//        }
//        if (system_ad.getChildCount() > 0)
//            ((RelativeLayout) system_ad.getParent()).setVisibility(View.VISIBLE);
//        if (system_ad.getChildCount() > 1)
//            initAnimation();
//    }
//
//    private void addFlipperView(final Ad ad) {
//        if (inflater == null) {
//            ((RelativeLayout) system_ad.getParent()).setVisibility(View.GONE);
//            return;
//        }
//        View view = inflater.inflate(R.layout.list_item_ad_pic_bottom, null);
//        ImageView image = (ImageView) view.findViewById(R.id.iv_image);
//       // new AQuery(getActivity()).id(image).image(ad.image(), true, true);
//        PhotoUtils.showCard(PhotoUtils.UriType.HTTP,ad.image(),image, MyDisplayImageOption.defaultOption);
//        view.setTag(ad);
//        view.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                if (ad.isJumtToSrp()) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), SRPActivity.class);
//                    intent.putExtra("keyword", ad.keyword());
//                    intent.putExtra("srpId", ad.srpId());
//                    intent.putExtra("md5", ad.md5());
//                    startActivity(intent);
////                } else if (ad.isJumtToSlot()) {
////                    Intent appIntent = new Intent(getActivity(), TigerGameActivity.class);
////                    startActivity(appIntent);
//                } else if (ad.url().endsWith(".apk")) {
//                    Uri uri = Uri.parse(ad.url());
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//                } else {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), WebSrcViewActivity.class);
//                    intent.putExtra(WebSrcViewActivity.PAGE_URL, ad.url());
//                    startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                }
//            }
//        });
//        system_ad.addView(view);
//    }
//
//    private void initAnimation() {
//        Animation in, out;
//        out = AnimationUtils.loadAnimation(getActivity(), R.anim.ad_up_out);
//        in = AnimationUtils.loadAnimation(getActivity(), R.anim.ad_bottom_in);
//        system_ad.setOutAnimation(out);
//        system_ad.setAnimateFirstView(false);
//        system_ad.setInAnimation(in);
//        system_ad.setFlipInterval(9000);// 每隔x秒自动显示ViewFlipper里面的图片
//        system_ad.startFlipping();// 开始显示
//        out.setAnimationListener(new AnimationListener() {
//            public void onAnimationStart(Animation animation) {
//                cancel_longteng_led.setVisibility(View.GONE);
//            }
//
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            public void onAnimationEnd(Animation animation) {
//                cancel_longteng_led.setVisibility(View.VISIBLE);
//            }
//        });
//    }
//
//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        newsAdapter.isNetError = true;
//        pullToRefreshListView.onRefreshComplete();
//        if (newsAdapter.dataSize() == 0)
//            pbHelper.showNetError();
//        else if (newsAdapter != null) {
//            newsAdapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void clickRefresh() {
//        htp.searchResult(sysUrl, "0", SYUserManager.getInstance().getToken());
//    }
//
//    public void hideAd() {
//        if (system_ad != null) {
//            ((RelativeLayout) system_ad.getParent()).setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        cancel_longteng_led = null;
//        inflater = null;
//        pullToRefreshListView.setAdapter(null);
//        pullToRefreshListView = null;
//        newsAdapter = null;
//        pbHelper.context = null;
//        pbHelper = null;
//        htp = null;
//    }
//
//    private void getAdapterImageAble() {
//        imgAble = getArguments().getBoolean(IMGABLE, true);
//        newsAdapter.setImgAble(imgAble);
//    }
//
//    private void setAdapterImageAble() {
//        imgAble = SettingsManager.getInstance().isLoadImage();
//        newsAdapter.setImgAble(imgAble);
//    }
//}
