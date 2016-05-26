package com.zhongsou.souyue.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MyFavoriteActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.adapter.FavouriteAdapter;
import com.zhongsou.souyue.adapter.FavouriteAdapter.DelHelper;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.circle.activity.DetailActivity;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.module.Favorite;
import com.zhongsou.souyue.module.FavoriteList;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CancleCollectReq;
import com.zhongsou.souyue.net.personal.UserFavoriteList;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.ac.SrpWebViewActivity;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.TitleBarToFragmentHelper;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase.Mode;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 收藏列表
 *
 * @author eye_
 */
public class FavoriteFragment extends BaseFragment implements OnClickListener,
        ProgressBarClickListener ,IVolleyResponse{
    public static final String TAG = "FavoriteFragment";
    // private CustomListView lv;
    private PullToRefreshListView pullToRefreshListView;
    public static FavouriteAdapter favoriteAdapter;
    private View v;
    private ProgressDialog dialog;
    private boolean favoriteEdit = true;
    private boolean hasMore = false;
//    private Http http;
    private String token;
    private long lastId = 0;
    private boolean deling;// 正在删除
    private boolean isEdit;
    private AtomicBoolean isLoading = new AtomicBoolean(false);
    private PullToRefreshBase.OnRefreshListener<ListView> onRefreshListener;
    private ProgressBarHelper pbHelper;
    private TextView right_btn;
    private String time;
    public boolean isHome = false;

    public static String mOpenType = "emptyWeb";//
    public static final String mFrom = "verticalChannel";
    public static final int paramCount = 2;

    public void setHome(boolean isHome) {
        this.isHome = isHome;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 初始化标题栏
        v = inflater.inflate(R.layout.favorite_list, null);
        View includeView = v.findViewById(R.id.title_fragment_bar_included);
        if (!isHome) {
            includeView.setVisibility(View.GONE);
        } else {
            includeView.setVisibility(View.VISIBLE);
        }

        String title = this.getResources().getString(R.string.my_favorite);
        new TitleBarToFragmentHelper(this, includeView, title,
                TitleBarToFragmentHelper.BUTTON_ID_EDIT, false);

        pbHelper = new ProgressBarHelper(getActivity(),
                v.findViewById(R.id.ll_data_loading));
        pbHelper.setProgressBarClickListener(this);

        initView();

        String msg = getResources().getString(R.string.working);
        dialog = ProgressDialog.show(getActivity(), null, msg);
        dialog.setCancelable(true);
        dialog.cancel();

//        http = new Http(this);

        loadData(0);

        return v;
    }

    public void initView() {
        right_btn = (TextView) v.findViewById(R.id.btn_title_fragment_bar_edit);

        pullToRefreshListView = (PullToRefreshListView) v
                .findViewById(R.id.favourite_list);
        View emptyView = v.findViewById(R.id.emptyView);
        pullToRefreshListView.getRefreshableView().setEmptyView(emptyView);

        pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // if (view.getTag() == null) {// 加载更多
                // loadingMore(view);
                // return;
                // }
                if (position == favoriteAdapter.getDatas().size()) {
                    loadingMore(view);
                }

                List<Favorite> datas = favoriteAdapter.getDatas();
                Favorite data = datas.get(position - 1);
                if (data.getDataType() == 2) {
                    CircleResponseResultItem item = new CircleResponseResultItem();
                    item.setBlog_id(data.getBlogId());
                    item.setInterest_id(data.getInterestId());
                    item.setTop_status(data.getTopStatus());
                    item.setIs_prime(data.getIsPrime());
                    item.setUser_id(data.getUserId());
                    item.setCollect(true);
//                    UIHelper.showPostsDetail(getActivity(), data.getBlogId(),
//                            data.getInterestId(), null);
                    SearchResultItem item1 = new SearchResultItem();
                    item1.setBlog_id(data.getBlogId());
                    item1.setInterest_id(data.getInterestId());
                    if (StringUtils.isNotEmpty(item.getBlog_id())) {
                        IntentUtil.skipDetailPage(getActivity(), item1, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
                        getActivity().overridePendingTransition(R.anim.left_in,
                                R.anim.left_out);
                    }
                } else {
                    /*ArrayList<SearchResultItem> items = new ArrayList<SearchResultItem>();
                    for (Favorite fav : datas) {
                        SearchResultItem item = fav2Search(fav);
                        item.srpId_$eq(fav.srpId());
                        item.category_$eq(fav.getCategory());
                        // if (null != fav.url() &&
                        // fav.url().contains("urlContent.groovy"))
                        items.add(item);
                    }*/
//                    Intent i = new Intent();
//                    i.setClass(getActivity(), ReadabilityActivity.class);
                    // Bundle bundle = new Bundle();
                    // bundle.putSerializable("list", items);
//                    IntentCacheHelper.getInstance(List.class).setObject(items);
//                    IntentCacheHelper.getInstance(List.class).setListFlag(true);
//                    i.putExtra("position", position - 1);
                    // i.putExtra("from", "favorite");
                    // i.putExtras(bundle);
//                    startActivity(i);
                    if (data.url().contains(
                            getActivity().getResources().getString(
                                    R.string.trade_zae_domain))) {
                        Intent intent = new Intent(getActivity(),
                                WebSrcViewActivity.class);
                        String tradeUrl = data.url().substring(0,
                                data.url().length() - ("&isfav=1").length());
                        intent.putExtra(WebSrcViewActivity.PAGE_URL, tradeUrl);
                        intent.putExtra(WebSrcViewActivity.PAGE_KEYWORD, data.keyword()); //对于大赛换头需要SRP词
                        startActivity(intent);
                    } else if (ConstantsUtils.FR_INFO_PICTURES.equals(data.getCategory())) {
                        //我的收藏，图集跳转
                        SearchResultItem item = fav2Search(datas.get(position - 1));
                        IntentUtil.getToGalleryNews(getActivity(), item);
                    } else {
                        SearchResultItem item = fav2Search(datas.get(position - 1));
                        if (StringUtils.isNotEmpty(item)) {
                            String url = item.url();
                            if (getUrlParams(url) == paramCount) {
                                Intent i = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("searchResultItem", item);
                                bundle.putString("source_url", url);
                                i.setClass(getActivity(), SrpWebViewActivity.class);
                                i.putExtras(bundle);
                                getActivity().startActivity(i);
                                getActivity().overridePendingTransition(R.anim.left_in,
                                        R.anim.left_out);
                            } else {
                                IntentUtil.startskipDetailPage(getActivity(), item);
                                getActivity().overridePendingTransition(R.anim.left_in,
                                        R.anim.left_out);
                            }
                        }
                    }

                }
            }
        });
        onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!isLoading.get()) {
                    LogDebugUtil.v("Favorite", "isLoading = true");
                    // pullToRefreshListView.loading();
                    lastId = 0;
                    UserFavoriteList favorite = new UserFavoriteList(HttpCommon.USER_FAVORITE_LIST_REQUEST,FavoriteFragment.this);
                    favorite.setParams(lastId);
                    mMainHttp.doRequest(favorite);
//                    http.favoriteList(token, lastId);
                    isLoading.set(true);
                } else {
                    LogDebugUtil.v("Favorite", "isLoading = false");
                    pullToRefreshListView.onRefreshComplete();
                    lastId = 0;
                    isLoading.set(false);
                }
            }
        };
        pullToRefreshListView.setOnRefreshListener(onRefreshListener);
        pullToRefreshListView
                .setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
                    @Override
                    public void onTimeRefresh() {
                        if (null != time)
                            pullToRefreshListView.onUpdateTime(StringUtils
                                    .convertDate(time));
                    }
                });
        favoriteAdapter = new FavouriteAdapter(this);
        favoriteAdapter.setFavouriteEdit(favoriteEdit);
        pullToRefreshListView.setAdapter(favoriteAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogDebugUtil.v("Favorite", "onResume");

        favoriteEdit = true;

        refreshEdit();
    }

    private void loadData(long last) {
        if (!isLoading.get()) {
            isLoading.set(true);
            lastId = last;
            LogDebugUtil.v("Favorite", "onResume loadData lastId=" + lastId);
            UserFavoriteList favorite = new UserFavoriteList(HttpCommon.USER_FAVORITE_LIST_REQUEST,FavoriteFragment.this);
            favorite.setParams(lastId);
            mMainHttp.doRequest(favorite);
//            token = SYUserManager.getInstance().getToken();
//            http.favoriteList(token, lastId);
        }
    }

    public void favoriteListSuccess(FavoriteList list) {
        time = System.currentTimeMillis()+"";
        dialog.dismiss();
        pbHelper.goneLoading();
        pullToRefreshListView.onRefreshComplete();

        hasMore = false;

        List<Favorite> favorites = list.items();
        if (favorites != null && favorites.size() > 0) {
            right_btn.setEnabled(true);
        } else {
            right_btn.setEnabled(false);
        }
        if (favorites != null && favorites.size() >= 0) {
            hasMore = list.hasMore();
            favoriteAdapter.setHasMore(hasMore);

            if (lastId == 0) {// 第一次加载
                favoriteAdapter.setData(favorites);
            } else {
                favoriteAdapter.addData(favorites);
            }

            favoriteAdapter.notifyDataSetChanged();

            if (favorites.size() > 0) {
                pbHelper.goneLoading();
                lastId = favorites.get(favorites.size() - 1).id();
            } else {
                lastId = 0;
            }
        } else {
            favoriteAdapter.setHasMore(hasMore);
        }
        if(favoriteAdapter.getCount()==0){
            pbHelper.showNoData();
        }
        LogDebugUtil.v("Favorite", "favoriteListSuccess lastId=" + lastId);

        isLoading.set(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_for_activity:
                rightButtonClick();
                break;
            case R.id.btn_title_fragment_bar_edit:// "编辑":"保存",false可删除态
                rightButtonClick();
                break;
            default:// 删除被点击
                if (!CMainHttp.getInstance().isNetworkAvailable(getActivity())) {
                    SouYueToast.makeText(getActivity(), R.string.neterror,
                            SouYueToast.LENGTH_SHORT).show();
                    return;
                }

                DelHelper delHelper = (DelHelper) v.getTag();
                // Toast.makeText(getActivity(), "删除被点击"+delHelper.url,
                // SouYueToast.LENGTH_SHORT).show();

                dialog.show();
                deling = true;
                token = SYUserManager.getInstance().getToken();
                //如果是帖子的话需要拼上blog_id  
                if (delHelper.dataType == 2) {
                    delHelper.url = UrlConfig.HOST + "interest/interest.content.groovy?blog_id=" + delHelper.blogId;
                }
//                http.cancelCollect(token, delHelper.url, delHelper.dataType, DetailActivity.DEVICE_COME_FROM);
                CancleCollectReq req = new CancleCollectReq(HttpCommon.CIRLCE_CANCLE_FAVORATE_ID,this);
                req.setParamsForOpenFlag(token, delHelper.url, delHelper.dataType, DetailActivity.DEVICE_COME_FROM);
                CMainHttp.getInstance().doRequest(req);

                favoriteAdapter.deleteData(delHelper.position);
                favoriteAdapter.notifyDataSetChanged();
                if (favoriteAdapter.getDatas() != null && favoriteAdapter.getDatas().size() > 0) {
                    right_btn.setEnabled(true);
                } else {
                    if (!favoriteEdit) {
                        isEdit = false;
                        favoriteEdit = true;
                        refreshEdit();
                        right_btn.setEnabled(false);
                    }
                }
                // if (favoriteAdapter.getCount() == 0 && !favoriteEdit) {// 从保存态还原
                // favoriteEdit = !favoriteEdit;
                // refreshEdit();
                // }
                break;
        }
    }

    private void rightButtonClick() {
        pullToRefreshListView.onRefreshComplete();

        favoriteEdit = !favoriteEdit;
        refreshEdit();
        if (favoriteEdit) {
            pullToRefreshListView.setMode(Mode.PULL_DOWN_TO_REFRESH);
            isEdit = false;
        } else {// 编辑模式
            isEdit = true;
            pullToRefreshListView.setMode(Mode.DISABLED);
        }
    }

    private void refreshEdit() {
        right_btn.setText(favoriteEdit ? "编辑" : "保存");

        favoriteAdapter.setFavouriteEdit(favoriteEdit);
    }

    public void cancelCollectSuccess(HttpJsonResponse res ) {
        SouYueToast.makeText(getActivity(), R.string.favorite_del_success,
                SouYueToast.LENGTH_SHORT).show();
        deling = false;
        dialog.cancel();
    }



    public SearchResultItem fav2Search(Favorite favorite) {
        try {
            LogDebugUtil.v("json1", favorite.toString());

           /* Gson gson = new Gson();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("url", favorite.url());
            jsonObject.addProperty("title", favorite.title());
            jsonObject.addProperty("description", favorite.description());
            jsonObject.addProperty("source", favorite.source());
            jsonObject.addProperty("keyword", favorite.keyword());
            jsonObject.addProperty("srpId", favorite.srpId());
            jsonObject.addProperty("date", "" + favorite.date());

            LogDebugUtil.v("json0", jsonObject.toString());

            SearchResultItem res = gson.fromJson(jsonObject.toString(),
                    SearchResultItem.class);*/

            SearchResultItem res = new SearchResultItem();
            res.url_$eq(favorite.url());
            res.title_$eq(favorite.title());
            res.description_$eq(favorite.description());
            res.source_$eq(favorite.source());
            res.keyword_$eq(favorite.keyword());
            res.srpId_$eq(favorite.srpId());
            res.date_$eq("" + favorite.date());

            if (!TextUtils.isEmpty(favorite.image())) {
                res.image().add(favorite.image());
            }
            LogDebugUtil.v("json2", res.toString());
            return res;
        } catch (Exception e) {
            LogDebugUtil.v("json", e.toString());
            return null;
        }
    }

    @Override
    public void clickRefresh() {
        loadData(0);
    }

    // 加载下一页
    public void loadingMore(View convertView) {
        LogDebugUtil.v("Favorite", "loadingMore" + hasMore);
        TextView tv = ((TextView) convertView.findViewById(R.id.btn_load_more));
        if (tv != null) {
            if (!isLoading.get() && hasMore && !isEdit) {// 加载更多
                tv.setText(R.string.more_loading);
                loadData(lastId);
            } else {
//                tv.setText(getString(R.string.nomore_loading));
                tv.setVisibility(View.GONE);
            }
        }
    }

    public void showBack() {
        View title_activity = v.findViewById(R.id.title_activity);
        title_activity.setVisibility(View.VISIBLE);
        right_btn = (TextView) v.findViewById(R.id.edit_for_activity);
        right_btn.setOnClickListener(this);
        right_btn.setEnabled(false);
        // 返回或菜单按钮
        View goBack = v.findViewById(R.id.goBack_for_activity);
        goBack.setOnClickListener(((MyFavoriteActivity) getActivity()));
    }

    public static int getUrlParams(String url) {
        if (StringUtils.isNotEmpty(url)) {
            Map<String, String> map = new HashMap<String, String>();
            String[] urlParams = url.split("\\?");
            if (urlParams.length >= 2) {
                String[] params = urlParams[1].split("&");
                for (String param : params) {
                    String name = param.split("=")[0];
                    String value = param.split("=")[1];
                    if ("mOpenType".equals(name) && mOpenType.equals(value)) {
                        map.put(name, value);
                    }
                    if ("mFrom".equals(name) && mFrom.equals(value)) {
                        map.put(name, value);
                    }
                }
                return map.size();
            }
        }
        return 0;
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId())
        {
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                cancelCollectSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.USER_FAVORITE_LIST_REQUEST:
                FavoriteList list = new FavoriteList(request.<HttpJsonResponse>getResponse());
                favoriteListSuccess(list);

        }
    }

    @Override
    public void onHttpError(IRequest request) {
        if (favoriteAdapter.getCount() == 0) {
            pbHelper.showNetError();
        } else {
            SouYueToast.makeText(getActivity(), R.string.netError_tips,
                    SouYueToast.LENGTH_SHORT).show();
        }
        pullToRefreshListView.onRefreshComplete();
        switch (request.getmId())
        {
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                if (deling) {
                    deling = false;
                    dialog.cancel();
                    return;
                }
                break;
        }
        if (isLoading.get()) {
            isLoading.set(false);
        }
        dialog.cancel();

    }

//    @Override
//    public void onHttpError(String methodName) {
//        LogDebugUtil.v("Favorite", "onHttpError" + methodName);
//        if (favoriteAdapter.getCount() == 0) {
//            pbHelper.showNetError();
//        } else {
//            SouYueToast.makeText(getActivity(), R.string.netError_tips,
//                    SouYueToast.LENGTH_SHORT).show();
//        }
//        pullToRefreshListView.onRefreshComplete();
//
//        if (deling && "favoriteDelete".equals(methodName)) {
//            deling = false;
//            dialog.cancel();
//            return;
//        }
//
//        if (isLoading.get()) {
//            isLoading.set(false);
//        }
//        dialog.cancel();
//
//    }

    @Override
    public void onHttpStart(IRequest request) {

    }
}
