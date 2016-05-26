package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoadingDataListener;
import com.zhongsou.souyue.activity.QADetailsActivity;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.adapter.BaikeAdapter;
import com.zhongsou.souyue.adapter.BlogAdapter;
import com.zhongsou.souyue.adapter.CommonAdapter;
import com.zhongsou.souyue.adapter.ForumAdapter;
import com.zhongsou.souyue.adapter.HistoryAdapter;
import com.zhongsou.souyue.adapter.MingYanAdapter;
import com.zhongsou.souyue.adapter.MySharesAdapter;
import com.zhongsou.souyue.adapter.NewsListAdapter;
import com.zhongsou.souyue.adapter.PeopleAdapter;
import com.zhongsou.souyue.adapter.PictureAdapter;
import com.zhongsou.souyue.adapter.QaAdapter;
import com.zhongsou.souyue.adapter.RecommendAdapter;
import com.zhongsou.souyue.adapter.SRPSelfCreateAdapter;
import com.zhongsou.souyue.adapter.SouyueAdapter;
import com.zhongsou.souyue.adapter.SouyueAdapter.ViewHolder;
import com.zhongsou.souyue.adapter.VideosAdapter;
import com.zhongsou.souyue.adapter.WebNavAdapter;
import com.zhongsou.souyue.adapter.WeiboAdapter;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.module.ImageUrlInfo;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.srp.SrpListRequest;
import com.zhongsou.souyue.net.srp.SrpMyCreateRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SettingsManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SRP页的Fragment
 *
 * @author chefb@zhongsou.com
 */
public abstract class SRPFragment extends BaseFragment implements
        OnItemClickListener {

    public static final int layoutId = R.layout.srp_item;
    protected Activity activity;
    protected NavigationBar nav;
    public PullToRefreshListView customListView;
    public SouyueAdapter adapter;
    public ProgressBarHelper pbHelper;
    private int fragmentType;
    public SearchResult searchResult;
    public boolean hasDatas;// 有没有数据
    public String type;//竟然没有地方给他赋值。。。
    public String keyWord;
    public String srpId;
    private String isInterest;
    private boolean isLastRow;

    protected CMainHttp mainHttp;

    public SRPFragment(Context context, NavigationBar nav) {
        this(context, nav, "");
    }

    public SRPFragment(Context context, NavigationBar nav, String type) {
        this.activity = (Activity) context;
        this.nav = nav;
        mainHttp = CMainHttp.getInstance();
        this.isInterest = type;
    }

    public SRPFragment() {
        mainHttp = CMainHttp.getInstance();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("nav", nav);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = (Activity) activity;
        super.onAttach(activity);
    }

    public String getKeyword() {
        if (activity instanceof SRPActivity) {
            return ((SRPActivity) activity).getKeyword();
        } else if (activity instanceof CircleIndexActivity) {
            return keyWord;
        }
        return "";
    }

    public String getSrpId() {
        if (activity instanceof SRPActivity) {
            return ((SRPActivity) activity).srpId;
        }
        return "";
    }

    public List<NavigationBar> getNavs() {
        if (activity instanceof SRPActivity) {
            return ((SRPActivity) activity).navs;
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState
                    .getSerializable("nav");
        View srpItemView = getSRPItemView();
        if (srpItemView != null)
            inits(srpItemView);
        return srpItemView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (searchResult != null) {// srpactivity第一次请求返回的数据显示出来
            searchResultSuccess(searchResult);
            searchResult = null;
        }
        if (hasDatas)
            pbHelper.goneLoading();

        if (savedInstanceState == null && !hasDatas)
            loadData();
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        hasDatas = false;
        super.onDestroyView();
    }

    public void loadData() {
        if (nav != null) {
            Log.v("url_load:", "loaddata:" + nav.url());
            SrpListRequest srpListRequest = new SrpListRequest(HttpCommon.SRP_LIST_REQUEST, this);
            srpListRequest.addParams(nav.url(), 0, 15, false);
            mainHttp.doRequest(srpListRequest);
        }
        // loadData(http, 0, nav.url());
    }

    protected void inits(View srpItemView) {
        // 这是父类的方法，用到customListView和adapter的都要调用此方法 除了小档案
        if (adapter == null)
            setAdapter(nav);
        customListView = getclvFromParent(srpItemView);
        adapter.setLoadingDataListener(new LoadingDataListener() {
            @Override
            public void loadDataMore(long start, String type) {
                if (adapter.getHasMoreItems()) {

                    if (!ConstantsUtils.VJ_SELF_CREATE.equals(nav.category())) {
                        SrpListRequest srpMoreRequest = new SrpListRequest(HttpCommon.SRP_LIST_MORE_REQUEST, SRPFragment.this);
                        srpMoreRequest.addParams(nav.url(), (Long) start, 15, type, true);
                        mainHttp.doRequest(srpMoreRequest);
                    } else {
                        checkToken();
                        SrpMyCreateRequest myCreateMoreRequest = new SrpMyCreateRequest(HttpCommon.SRP_LIST_MY_CREATE_MORE_REQUEST, SRPFragment.this);
                        myCreateMoreRequest.addParams(nav.url(), (Long) start, 0, type, true);
                        mainHttp.doRequest(myCreateMoreRequest);
                    }
                }
            }
        });
        customListView.setAdapter(adapter);
        customListView.onUpdateTime(adapter.getChannelTime());
        if (!(/* adapter instanceof WeiboAdapter || */adapter instanceof MingYanAdapter || adapter instanceof HistoryAdapter))
            customListView.setOnItemClickListener(this);
        customListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // loadData(http, 0, nav.url());
                        adapter.isRefresh = true;
                        if (!ConstantsUtils.VJ_SELF_CREATE.equals(nav
                                .category())) {
                            SrpListRequest srpRefreshRequest = new SrpListRequest(HttpCommon.SRP_LIST_REFRESH_REQUEST, SRPFragment.this);
                            srpRefreshRequest.addParams(nav.url(), new Long(0), true);
                            mainHttp.doRequest(srpRefreshRequest);
                        } else {
                            SrpMyCreateRequest myCreateRefreshRequest = new SrpMyCreateRequest(HttpCommon.SRP_LIST_MY_CREATE_REFRESH_REQUEST, SRPFragment.this);
                            myCreateRefreshRequest.addParams(nav.url(), new Long(0), true);
                            mainHttp.doRequest(myCreateRefreshRequest);
                        }
                    }
                });
        customListView
                .setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
                    @Override
                    public void onTimeRefresh() {
                        if (null != adapter.getChannelTime())
                            customListView.onUpdateTime(StringUtils
                                    .convertDate(adapter.getChannelTime()));
                    }
                });
        createPBHelper(srpItemView.findViewById(R.id.ll_data_loading), nav);
    }

    protected boolean checkToken() {
        return false;
    }

    public void searchResultToLoadMoreSuccess(final SearchResult sr) {
        adapter.setHasMoreItems(sr.hasMore());
        adapter.addMore(sr.items());
    }

    public void searchResultToPullDownRefreshSuccess(final SearchResult sr) {
        if (CMainHttp.getInstance().isWifi(getActivity()))
            adapter.setImgAble(true);
        else
            adapter.setImgAble(SettingsManager.getInstance().isLoadImage());
        customListView.onRefreshComplete();
        adapter.setChannelTime(System.currentTimeMillis() + "");
        adapter.setHasMoreItems(sr.hasMore());
        adapter.addDatas(sr.items());
    }

    /**
     * SRP默认加载列表
     *
     * @param searchResult
     */
    public void searchResultSuccess(SearchResult searchResult) {
        if (CMainHttp.getInstance().isWifi(getActivity()))
            adapter.setImgAble(true);
        else
            adapter.setImgAble(SettingsManager.getInstance().isLoadImage());
        hasDatas = true;
        adapter.setChannelTime(System.currentTimeMillis() + "");
        pbHelper.goneLoading();
        adapter.setHasMoreItems(searchResult.hasMore());
        adapter.addDatas(searchResult.items());
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.SRP_LIST_REQUEST:
                searchResultSuccess((SearchResult) request.getResponse());
                break;
            case HttpCommon.SRP_LIST_REFRESH_REQUEST:
                SearchResult sr = request.getResponse();
                searchResultToPullDownRefreshSuccess(sr);
                break;
            case HttpCommon.SRP_LIST_MORE_REQUEST:
                SearchResult srMore = request.getResponse();
                searchResultToLoadMoreSuccess(srMore);
                break;
        }
    }

    protected PullToRefreshListView getclvFromParent(View view) {
        return (PullToRefreshListView) view.findViewById(R.id.customlv);
    }

    private View getSRPItemView() {
        View view = View.inflate(activity, layoutId, null);
        return view;
    }

    @Override
    public void onHttpError(IRequest request) {
        if (pbHelper.isLoading) {
            pbHelper.goneLoading();
        }
        if (!hasDatas)
            pbHelper.showNetError();
        if (customListView != null)
            customListView.onRefreshComplete();
    }

    protected void setAdapter(NavigationBar nav) {
        if (nav == null) {
            adapter = new CommonAdapter(activity);
            fragmentType = 5;
        }
        String category = nav.category();
        if (ConstantsUtils.VJ_NEW_SEARCH.equals(category) || ConstantsUtils.FR_INFO_PUB.equals(category)) {
            NewsListAdapter newsListAdapter = new NewsListAdapter(activity);
            newsListAdapter.setImgAble(SettingsManager.getInstance()
                    .isLoadImage());
            adapter = newsListAdapter;
            adapter.setCategory(category);
            fragmentType = 5;
        } else if (ConstantsUtils.VJ_VIDEO_SEARCH.equals(category)) {
            adapter = new VideosAdapter(activity);
            fragmentType = 3;
        } else if (ConstantsUtils.VJ_BAIKE_LORE.equals(category)) {
            adapter = new BaikeAdapter(activity);
            fragmentType = 3;
        } else if (ConstantsUtils.VJ_BBS_SEARCH.equals(category)) {
            adapter = new ForumAdapter(activity);
            adapter.setImgAble(SettingsManager.getInstance().isLoadImage());
            fragmentType = 3;
        } else if (ConstantsUtils.VJ_QA.equals(category)) {
            adapter = new QaAdapter(activity);
            fragmentType = 1;
        } else if (ConstantsUtils.VJ_PEOPLE.equals(category)) {
            adapter = new PeopleAdapter(activity);
            fragmentType = 2;
        } else if (ConstantsUtils.VJ_HISTORY.equals(category)) {
            adapter = new HistoryAdapter(activity);
            fragmentType = 0;
        } else if (ConstantsUtils.VJ_WEIBO_SEARCH.equals(category)) {
            adapter = new WeiboAdapter(activity);
            adapter.setImgAble(SettingsManager.getInstance().isLoadImage());
            fragmentType = 6;
        } else if (ConstantsUtils.VJ_BLOG_SEARCH.equals(category)) {
            adapter = new BlogAdapter(activity);
            adapter.setImgAble(SettingsManager.getInstance().isLoadImage());
            fragmentType = 5;
        } else if (ConstantsUtils.VJ_SRP_SELFCREATE.equals(category)) { // 原创微件
            adapter = new SRPSelfCreateAdapter(activity);
            adapter.setImgAble(SettingsManager.getInstance().isLoadImage());
            fragmentType = 5; // 走通用的就行
        } else if (ConstantsUtils.VJ_IMG_NAV.equals(category)) {
            adapter = new PictureAdapter(activity);
            fragmentType = 8;
        } else if (ConstantsUtils.VJ_STAR_SAY.equals(category)) {
            adapter = new MingYanAdapter(activity);
            fragmentType = 0;
        } else if (ConstantsUtils.VJ_JHQ.equals(category)
                || ConstantsUtils.VJ_PHB.equals(category)) {
            adapter = new RecommendAdapter(activity);
            adapter.setCategory(category);
            adapter.setImgAble(SettingsManager.getInstance().isLoadImage());
            fragmentType = 5;
        } else if (ConstantsUtils.VJ_SELF_CREATE.equals(category)) {
            adapter = new MySharesAdapter(activity);
            adapter.setImgAble(SettingsManager.getInstance().isLoadImage());
            fragmentType = 7;
        } else if (ConstantsUtils.VJ_WEB_NAV.equals(category)) {
            adapter = new WebNavAdapter(activity);
            fragmentType = 8;
        } else {
            adapter = new CommonAdapter(activity);
            fragmentType = 5;
        }
    }

    public void createPBHelper(View view, final NavigationBar nav) {
        pbHelper = new ProgressBarHelper(activity, view);
        pbHelper.setProgressBarClickListener(new ProgressBarClickListener() {
            public void clickRefresh() {
                SrpListRequest srpListRequest = new SrpListRequest(HttpCommon.SRP_LIST_REQUEST, SRPFragment.this);
                srpListRequest.addParams(nav.url(), 0, 15, false);
                mainHttp.doRequest(srpListRequest);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // updateItemColor( ((ViewHolder) view.getTag()),true,position);
        dispatchOnItemClick(view, position);
    }

    private void updateItemColor(ViewHolder viewHolder, boolean hasRead,
                                 int position) {
        viewHolder.searchResultItem.hasRead_$eq(hasRead);
        if (viewHolder.title != null)
            viewHolder.title.setTextColor(hasRead ? 0xff8d8d8d : 0xff303030);
        if (viewHolder.desc != null)
            viewHolder.desc.setTextColor(hasRead ? 0xff8d8d8d : 0xff5c5c5c);
        if (adapter instanceof HistoryAdapter) {
            if (viewHolder.title != null)
                viewHolder.title
                        .setTextColor(hasRead ? 0xff8d8d8d : 0xff303030);
        }
    }

    public void dispatchOnItemClick(View view, int position) {
        Intent intent = new Intent();
        ViewHolder holder = (ViewHolder) view.getTag();
        SearchResultItem item = null;
        if (holder != null)
            item = holder.searchResultItem;
        switch (fragmentType) {

            case 0:// MingYan History
                break;
            case 1:// 有问必答
                intent.setClass(activity, QADetailsActivity.class);
                if (item != null) {
                    User user = item.user();
                    intent.putExtra("md5", item.md5());
                    intent.putExtra("id", item.id());
                    intent.putExtra("kid", item.kid());
                    intent.putExtra("sameaskcount", item.sameAskCount());
                    intent.putExtra("answercount", item.answerCount());
                    intent.putExtra("description", item.description());
                    intent.putExtra("name", user.name());
                    intent.putExtra("face", user.image());
                    intent.putExtra("date", item.date());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.left_in,
                            R.anim.left_out);
                }
                break;
            case 2:// 相关人物
                start2Spr(item);
                break;
            case 3:// Video Forum Baike Picture
                intent = new Intent(activity, WebSrcViewActivity.class);
                if (item != null) {
                    item.keyword_$eq(getKeyword());
                    item.srpId_$eq(getSrpId());
                    if (item.isOriginal() == 1) {
                        IntentUtil.skipDetailPage(activity, item, 0);

                    } else {
                        // intent.putExtra(WebSrcViewActivity.PAGE_URL,
                        // StringUtils.enCodeKeyword(item.url()));
                        // 追加分享内容
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(WebSrcViewActivity.ITEM_INFO, item);
                        if (keyWord.equals(ConstantsUtils.MEGAGAME_SEARCH_KEYWORD)) {
                            bundle.putString(WebSrcViewActivity.PAGE_KEYWORD, keyWord);
                        }
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    }
                    activity.overridePendingTransition(R.anim.left_in,
                            R.anim.left_out);
                    updateItemColor(holder, true, position);
                }
                break;
            case 4:// Web
            case 5:// 其他
                item.keyword_$eq(getKeyword());
                item.srpId_$eq(getSrpId());
                System.out.println("**" + item.keyword() + " " + item.srpId());
                IntentUtil.skipOldDetailPage(getActivity(), item, 0);
                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case 6:// Weibo
                SearchResultItem sri = (SearchResultItem) adapter
                        .getItem(position - 1);
                Bundle bundle = new Bundle();
                SearchResultItem searchResultItem = new SearchResultItem();
                searchResultItem.keyword_$eq(getKeyword());
                searchResultItem.srpId_$eq(getSrpId());
                String content = sri.weibo().content();
                searchResultItem.title_$eq(content.substring(0,
                        content.length() > 50 ? 50 : content.length()));
                searchResultItem.description_$eq(content);
                searchResultItem.date_$eq(sri.weibo().date());
                searchResultItem.source_$eq(sri.weibo().source());
                searchResultItem.url_$eq(sri.weibo().url());
                List<String> imgurl = new ArrayList<String>();
                if (sri.weibo().image() != null) {
                    for (ImageUrlInfo iui : sri.weibo().image()) {
                        imgurl.add(iui.small());
                    }
                }
                searchResultItem.image_$eq(imgurl);
                if (sri.weibo().category() == 1) {

                    IntentUtil.skipDetailPage(activity, searchResultItem, 0);
                    activity.overridePendingTransition(R.anim.left_in,
                            R.anim.left_out);
                } else if (sri.weibo().category() != 0) {
                    intent = new Intent(activity, WebSrcViewActivity.class);
                    bundle.putSerializable(WebSrcViewActivity.ITEM_INFO,
                            searchResultItem);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.left_in,
                            R.anim.left_out);
                }
                break;
            case 7:
                break;
            case 8:
                if (item != null) {
                    if (StringUtils.isEmpty(item.keyword())) {
                        start2WebView(item.url());
                    } else {
                        start2Spr(item);
                    }
                }
                break;
            case 9:
                if (item != null)
                    start2WebView(item.url());
                break;
            default:
                break;
        }
    }

    private void start2Spr(SearchResultItem item) {
        if (item != null) {
            Intent intent = new Intent(getActivity(), SRPActivity.class);
            intent.putExtra("keyword", item.keyword());
            intent.putExtra("srpId", item.srpId());
            start(intent);
        }
    }

    private void start2WebView(String url) {
        Intent webViewIntent = new Intent();
        webViewIntent.setClass(getActivity(), WebSrcViewActivity.class);
        webViewIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
        start(webViewIntent);
    }

    private void start(Intent intent) {
        startActivity(intent);
        getActivity()
                .overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void updateHasRead(int[] readPos) {
        if (adapter != null) {
            List<SearchResultItem> datas = adapter.getDatas();
            boolean needNotiy = false;
            if (datas != null && datas.size() == readPos.length) {
                for (int i = 0; i < readPos.length; i++) {
                    if (readPos[i] == 1 && !datas.get(i).hasRead()) {
                        datas.get(i).hasRead_$eq(true);
                        needNotiy = true;
                    }
                }
            }
            if (needNotiy)
                adapter.notifyDataSetChanged();
        }

    }

    public void toInterest(JSClick jsc) {
        IntentUtil.gotoSecretCricleCard(getActivity(),
                Long.parseLong(jsc.getInterest_id()));
    }
    //加载全部提示暂时去掉
   /* 
    * class MyOnScrollListener implements android.widget.AbsListView.OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			boolean hasMore = adapter.getHasMoreItems();
			if (isLastRow && !hasMore && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				ToastHelper.getInstance().show(R.string.footer_hint_no_mroe);
				isLastRow = false;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// 判断是否滚到最后一行
			if (firstVisibleItem + visibleItemCount == totalItemCount
					&& totalItemCount > 0) {
				isLastRow = true;
			}
		}
    	
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //qq分享,SharedByTencentQq
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE) {
            if (resultCode == com.tencent.connect.common.Constants.ACTIVITY_OK) {
                ShareByTencentQQ.getInstance().handleResultData(data);
            }
        }
        //qzone 分享
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_QZONE_SHARE) {
            if (resultCode == com.tencent.connect.common.Constants.ACTIVITY_OK) {
                ShareByTencentQQZone.getInstance().handleResultData(data);
            }
        }
    }
}
