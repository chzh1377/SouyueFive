package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.ScaningActivity;
import com.zhongsou.souyue.adapter.DiscoverAdapter;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.HomePageItem;
import com.zhongsou.souyue.module.HomePageItem.CATEGORY;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.discover.GetDiscoverListRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IRequestCache;
import com.zhongsou.souyue.utils.ActivityUtils;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/8/20. mailto:wzyax@qq.com
 */
public class DiscoverTabFragment extends BaseTabFragment implements
        OnItemClickListener, OnClickListener {

    private ListView listview;
    private ArrayList<HomePageItem> lists = new ArrayList<HomePageItem>();
    private DiscoverAdapter adapter;
    private ImageButton goback;
    private TextView title;
    private static HomePageItem scanItem = new HomePageItem();
    private boolean isJumpFormMe = false;

    private static long lastClickTime;

    public static DiscoverTabFragment newInstance(String param1, String param2) {
        DiscoverTabFragment fragment = new DiscoverTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DiscoverTabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover_tab, container,
                false);
        initUI(view);
        if (((MainApplication) getActivity().getApplication())
                .isNeedForceRefreshDiscover()) {
            isJumpFormMe = true;
        }
        requestData();
        return view;
    }

    /**
     * add by yinguanping 将加载列表放在onResume中做，如果存有需要强刷发现列表的标记，则强取列表。
     * 放在onResume中做是为了点击列表中某项进去后返回到发现页时再次强刷，以便关掉红点提示
     */
    @Override
    public void onResume() {
        super.onResume();
        resumeData();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * add by yinguanping 显示或隐藏发现tab红点
     *
     * @param tag
     */
    private void chageDiscoverTabRed(int tag) {
        Intent tabRedIntent = new Intent();
        tabRedIntent.setAction(UrlConfig.HIDE_TABRED_ACTION);
        tabRedIntent.putExtra("tag", tag);
        getActivity().sendBroadcast(tabRedIntent);
    }

    /**
     * @param view
     */
    private void initUI(View view) {
        goback = (ImageButton) view.findViewById(R.id.goBack);
        if (goback != null) {
            goback.setVisibility(View.INVISIBLE);
        }

        title = (TextView) view.findViewById(R.id.activity_bar_title);
        title.setTextColor(getResources().getColor(R.color.bar_center_title_color));
        if (title != null) {
            title.setText("发现");
        }

        listview = (ListView) view.findViewById(R.id.discover_list);
        adapter = new DiscoverAdapter(context);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(this);
    }

    // 第一次取数据取缓存
    private void initDiscoverList() {
        keepScan();
        GetDiscoverListRequest request = new GetDiscoverListRequest(HttpCommon.GET_DISCOVER_LIST_REQUEST_ID, this);
        request.setParams(SYUserManager.getInstance().getToken());
        request.setForceCache(true);
        String cachekey = request.getCacheKey();
        int cacheState = mMainHttp.getCacheState(cachekey);
        if (cacheState == IRequestCache.CACHE_STATE_NO_CACHE) {    //cache不存在
            getDiscoverList();
            return;
        } else if (cacheState == IRequestCache.CACHE_STATE_IS_EXPIRE) {
            GetDiscoverListRequest req = new GetDiscoverListRequest(HttpCommon.GET_DISCOVER_LIST_REQUEST_ID, this);
            req.setParams(SYUserManager.getInstance().getToken());
            req.setForceCache(false);
            CMainHttp.getInstance().doRequest(req);
        }
        CMainHttp.getInstance().doRequest(request);
    }

    // 强取
    public void getDiscoverList() {
        keepScan();
        GetDiscoverListRequest request = new GetDiscoverListRequest(HttpCommon.GET_DISCOVER_LIST_REQUEST_ID, this);
        request.setParams(SYUserManager.getInstance().getToken());
        request.setForceRefresh(true);

        CMainHttp.getInstance().doRequest(request);
    }

    public void getDiscoverListSuccess(HttpJsonResponse response) {
        lists = new Gson().fromJson(response.getBodyArray(),
                new TypeToken<ArrayList<HomePageItem>>() {
                }.getType());

        if (lists.size() > 0) {
            adapter.SetList(lists);
            adapter.notifyDataSetChanged();
        }

        // 返回后循环查看当前是否有正在显示红点的item项，如果有，则再次显示红点，如果没有，则通知隐藏红点。
        boolean isNeedShowRed = false;
        for (HomePageItem homePageItem : lists) {
            if (homePageItem.isHasNew()) {
                isNeedShowRed = true;
                break;
            }
        }
        if (isNeedShowRed) {
            chageDiscoverTabRed(-1);
        } else {
            chageDiscoverTabRed(-2);
            ((MainApplication) getActivity().getApplication())
                    .setNeedForceRefreshDiscover(false);
        }
    }

    public void getDiscoverListFail() {
        keepScan();
    }

    public void keepScan() {
        scanItem.category_$eq("scan");
        scanItem.title_$eq("扫一扫");
        scanItem.setImage("local");
        if (lists.size() <= 0) {
            lists.add(new HomePageItem());
            lists.add(scanItem);
            adapter.SetList(lists);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        UmengStatisticUtil.onEvent(context, UmengStatisticEvent.DISCOVER_ITEM_CLICK);
        switchContent(adapter.getItem(position), position);
    }

    /**
     * 点击切换contentview显示内容，只处理跳转内容
     *
     * @param homePage
     * @param position 当前homepage的位置
     */
    public void switchContent(HomePageItem homePage, int position) {

        try {
            CATEGORY category = CATEGORY.valueOf(homePage.category());
            if (homePage.isHasNew()) {
                ((MainApplication) getActivity().getApplication())
                        .setNeedForceRefreshDiscover(true);
                isJumpFormMe = true;
            }
            UpEventAgent.onZSDevDiscoverItem(context, homePage.title());
            switch (category) {
                case scan:
                    if (!isFastDoubleClick())
                        startActivityWithAnim("", ScaningActivity.class);
                    break;
                case srp:
                    Intent intent = new Intent(context, SRPActivity.class);
                    intent.putExtra("keyword", homePage.keyword());
                    intent.putExtra("srpId", homePage.srpId());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.left_in,
                            R.anim.left_out);
                    break;

                case app:
                    boolean isInstall = ActivityUtils.isIntentAvailable(context,
                            MainActivity.ACTION_APPBIBLE);
                    if (isInstall) {
                        // 跳转到应用宝典
                        Intent appIntent = new Intent(
                                ConstantsUtils.ACTION_APPBIBLE);
                        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(appIntent);
                        ((Activity) context).overridePendingTransition(
                                R.anim.left_in, R.anim.left_out);
                    } else {
                        // 打开url
                        IntentUtil.gotoWeb(getActivity(), UrlConfig.bible, "");
                    }
                    break;
                case interactWeb:
                    if (!homePage.isOutBrowser()) {
                        IntentUtil.gotoWeb(getActivity(), homePage.url(), "interactWeb");
                    } else {
                        Intent outIntent = new Intent(Intent.ACTION_VIEW);
                        outIntent.setData(Uri.parse(homePage.url()));
                        startActivity(outIntent);
                    }
                    break;
                case interactNoHeadWeb: //企悦汇
                    IntentUtil.toOpenNoTitleForUrl(context, homePage.url(), null);
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
        }
    }

    private void startActivityWithAnim(String type, Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra("fragmentType", type);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in,
                R.anim.left_out);
    }

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    private void requestData() {
        if (((MainApplication) getActivity().getApplication())
                .isNeedForceRefreshDiscover()) {
            if (isJumpFormMe) {
                getDiscoverList();
                isJumpFormMe = false;
            }
        } else {
            initDiscoverList();
        }
    }

    private void resumeData() {
        if (((MainApplication) getActivity().getApplication())
                .isNeedForceRefreshDiscover()) {
            if (isJumpFormMe) {
                getDiscoverList();
                isJumpFormMe = false;
            }
        }
    }


    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.GET_DISCOVER_LIST_REQUEST_ID:
                getDiscoverListSuccess(request.<HttpJsonResponse>getResponse());
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        IHttpError error = request.getVolleyError();
        switch (request.getmId()) {
            case HttpCommon.GET_DISCOVER_LIST_REQUEST_ID:
                if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR) {
                    getDiscoverListFail();
                    return;
                }
        }
//		keepScan();
    }

    @Override
    public void refresh() {
        super.refresh();
        isJumpFormMe = true;
        requestData();
    }
}