package com.zhongsou.souyue.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.im.db.module.Contact;
import com.upyun.api.UploadImageTask;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.SelfCreateAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.model.CircleManageInfo;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.NewsCount;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.SelfCreate;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleMemberListRequest;
import com.zhongsou.souyue.net.personal.UserRepairInfo;
import com.zhongsou.souyue.net.selfCreate.FriendCreateList;
import com.zhongsou.souyue.net.selfCreate.SelfCreateList;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.pop.PopOneself;
import com.zhongsou.souyue.service.SelfCreateTask;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ThreadPoolUtil;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 原创列表主界面
 *
 * @author Administrator
 */
public class SelfCreateActivity extends BaseActivity implements
        OnItemClickListener, LoadingDataListener,
        View.OnClickListener, CircleLoadingDataListener ,IVolleyResponse {
    private PullToRefreshListView selfcreateLV;
    private ListView mListView;
    private TextView groupText, selfcreate_nodata;
    private PopOneself popSelf;
    private SelfCreateAdapter adapter;
    private MySelfCreateCircleAdapter circleAdapter;
    private String token;
    private Integer selectionId = ConstantsUtils.TYPE_ALL;
    private String time;
    private RefreshReceiver refreshRec;
    private ProgressBarHelper pbHelp;
    private View disable;
    private Contact contact;
    private SYUserManager um;
//    private AQuery aq;
    private ProgressDialog progdialog;
    private Drawable drawable;
    private File profileImgFile;// 通过uid，构建头像的本地存储路径
//    private Http http;
    private User user;
    private String imageUrl;
    private LinearLayout ll_picback_01, ll_picback_02;
    //    private String bgUrl;
//    private LinearLayout ll_yonghu_small; // 背景图片下部半透明布局
    private CircleManageInfo cinfo;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private int pno = 1;
    private List<CircleResponseResultItem> postList = new ArrayList<CircleResponseResultItem>();
    private NewsCount newsCount;
    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";// temp

    private List<SelfCreateItem> mDbDreft;
    // file
    private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);// The Uri to store
    // the big bitmap
    private static boolean IS_FROM_CIRCLE;
    private ImageButton tittle_write;
    private LinearLayout loadView;
    private String newNickName = "";
    private RelativeLayout titleLayout;
    private ImageView selfCreate_titlelogo;
    private int state = 0;//传递过来得状态 默认0  0：原接口，不动  1：原创  2：帖子
    private int refreshState = 0;//帖子刷新标记  0：刷新  1：更多
    private int im_statue=1;
    public  String KEY_CONTACT = "contact";
    private FriendCreateList friendcreatelist;
    private SelfCreateList selfcreatelist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.self_create_layout);
        state = getIntent().getIntExtra("state", 0);
        contact = (Contact) getIntent().getSerializableExtra(KEY_CONTACT);
        newNickName = getIntent().getStringExtra("newNickName");
//        IS_FROM_CIRCLE = false;
//        http = new Http(this);
        findView();
        progdialog = new ProgressDialog(this);
        progdialog.setMessage("正在上传 ");
        progdialog.setCanceledOnTouchOutside(false);
        profileImgFile = new File(getCacheDir(), "headphoto_");

        mListView = selfcreateLV.getRefreshableView();

        IntentFilter inf = new IntentFilter();
        inf.addAction(ConstantsUtils.INTENTFILTER_ACTION_REFRESH_SELFCREATE_LV);
        refreshRec = new RefreshReceiver();
        this.registerReceiver(refreshRec, inf);
//        aq = new AQuery(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void findView() {
        um = SYUserManager.getInstance();
        token = um.getToken();
        user = um.getUser();
        loadView = (LinearLayout) findViewById(R.id.ll_data_loading);
        if (loadView != null) {
            pbHelp = new ProgressBarHelper(this, loadView);
            pbHelp.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
                @Override
                public void clickRefresh() {
                    if (selfcreateLV != null) {
                        selfcreateLV.startRefresh();
                    }
                }
            });
            pbHelp.showLoading();
        }
        selfcreateLV = (PullToRefreshListView) findViewById(R.id.oneself_list);
        // oneselfLV.setOnScrollListener(this);
        selfcreateLV.setOnItemClickListener(onItemClick);
        selfcreateLV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (null != adapter) {
                    adapter.hasMore = false;
                }
                if (state == 2) {
                    refreshState = 0;
                    refreshCircleList();
                } else {
                    SelfCreateTask.getInstance().loadFailData(SelfCreateActivity.this, selectionId + "");
                }
            }
        });
        selfcreateLV.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
            @Override
            public void onTimeRefresh() {
                if (null != selfcreateLV)
                    selfcreateLV.onUpdateTime(StringUtils.convertDate(time));
            }
        });
        groupText = findView(R.id.group_text);
        titleLayout = (RelativeLayout) findViewById(R.id.layout_selfCreateTitle);
        selfCreate_titlelogo = (ImageView) findViewById(R.id.selfCreate_titlelogo);
        selfcreate_nodata = findView(R.id.selfcreate_nodata);

        setTitleRightBtn();
        changeCircleData();
    }

    private void refreshCircleList() {
        if (refreshState == 0) {
            pno=2;
         //   http.getMemberPostList(user.userId(), (long) 0, 1, DEFAULT_PAGE_SIZE);

            CircleMemberListRequest req = new CircleMemberListRequest(HttpCommon.CIRCLE_MEMBERLIST_REQUEST,this);
            req.addParams(user.userId(), (long) 0, 1, DEFAULT_PAGE_SIZE);
            mMainHttp.doRequest(req);

        } else{
            CircleMemberListRequest req = new CircleMemberListRequest(HttpCommon.CIRCLE_MEMBERLIST_REQUEST,this);
            req.addParams(user.userId(), (long) 0, pno, DEFAULT_PAGE_SIZE);
            mMainHttp.doRequest(req);
        }
            //http.getMemberPostList(user.userId(), (long) 0, pno, DEFAULT_PAGE_SIZE);
    }

    private void setTitleRightBtn() {
        tittle_write = (ImageButton) findViewById(R.id.self_create_title_right_btn);
        if (isFromFriend()) {
            tittle_write.setVisibility(View.GONE);
            groupText.setText(R.string.self_create);
        } else {
            tittle_write.setOnClickListener(this);
        }
        disable = findViewById(R.id.self_create_layout_disable);
        disable.setOnClickListener(this);
    }

    public void onGoBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void onPopClick(View view) {
        if (state == 0) {
            popSelf = new PopOneself(this, isFromFriend());
            popSelf.setOnItemClick(this);
            popSelf.showAsDropDown(view);
        }
    }

    private void changeCircleData() {
        switch (state) {
            case 0:
                titleLayout.setVisibility(View.VISIBLE);
                break;
            case 1:
                titleLayout.setVisibility(View.VISIBLE);
                selfCreate_titlelogo.setVisibility(View.GONE);
                if (isFromFriend()) {
                    tittle_write.setVisibility(View.GONE);
                } else {
                    tittle_write.setVisibility(View.VISIBLE);
                }
                IS_FROM_CIRCLE = false;
                adapter = new SelfCreateAdapter(this, !isFromFriend());
                adapter.setLoadingDataListener(this);
                selfcreateLV.setAdapter(adapter);
                groupText.setText("我的原创");
//                popSelf.dismiss();
                selfcreateLV.setVisibility(View.VISIBLE);
                selectionId = 0;
                selfcreateLV.startRefresh();
                break;
            case 2:
                titleLayout.setVisibility(View.VISIBLE);
                selfCreate_titlelogo.setVisibility(View.GONE);
                IS_FROM_CIRCLE = true;
                tittle_write.setVisibility(View.GONE);
                // 老接口
                refreshState = 1;
                refreshCircleList();
//                http.getMemberPostList(user.userId(), (long) 0, pno, DEFAULT_PAGE_SIZE);

                groupText.setText("我的帖子");
//                popSelf.dismiss();
                selfcreateLV.setVisibility(View.VISIBLE);

                circleAdapter = new MySelfCreateCircleAdapter();
//                circleAdapter.setLoadingDataListener(this);
                selfcreateLV.setAdapter(circleAdapter);
//                selfcreateLV.startRefresh();
                break;
            default:
                break;
        }
    }

    /**
     * 顶部导航条中间——分组Item事件处理
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (popSelf.getChangeTitle(position).equals("全部")) {
            IS_FROM_CIRCLE = false;
            if (isFromFriend()) {
                tittle_write.setVisibility(View.GONE);
            } else {
                tittle_write.setVisibility(View.VISIBLE);
            }
            selfcreateLV.setAdapter(adapter);
            // 区分是谁自己还是别人看原创
            if (isFromFriend()) {
                groupText.setText(SelfCreateActivity.this.getResources().getString(R.string.self_create));
            } else {
                groupText.setText(SelfCreateActivity.this.getResources().getString(R.string.my_self_create));
            }

            popSelf.dismiss();
            selfcreateLV.setVisibility(View.VISIBLE);
            selectionId = popSelf.getChangeLink(position);
            selfcreateLV.startRefresh();
        } else if (popSelf.getChangeTitle(position).equals("帖子")) {
            IS_FROM_CIRCLE = true;
            tittle_write.setVisibility(View.GONE);
            // 老接口
            CircleMemberListRequest req = new CircleMemberListRequest(HttpCommon.CIRCLE_MEMBERLIST_REQUEST,this);
            req.addParams(user.userId(), (long) 0, pno, DEFAULT_PAGE_SIZE);
                    mMainHttp.doRequest(req);
                  //  http.getMemberPostList(user.userId(), (long) 0, pno, DEFAULT_PAGE_SIZE);

            groupText.setText(popSelf.getChangeTitle(position));
            popSelf.dismiss();
            selfcreateLV.setVisibility(View.VISIBLE);

            circleAdapter = new MySelfCreateCircleAdapter();
            // circleAdapter.setLoadingDataListener(this);
            selfcreateLV.setAdapter(circleAdapter);
            selfcreateLV.startRefresh();

        } else {
            if (isFromFriend()) {
                tittle_write.setVisibility(View.GONE);
            } else {
                tittle_write.setVisibility(View.VISIBLE);
            }
            IS_FROM_CIRCLE = false;
            selfcreateLV.setAdapter(adapter);
            groupText.setText(popSelf.getChangeTitle(position));
            popSelf.dismiss();
            selfcreateLV.setVisibility(View.VISIBLE);
            selectionId = popSelf.getChangeLink(position);
            selfcreateLV.startRefresh();
        }
    }

    /**
     * 获取圈成员发帖列表的回调
     *
     * @param res
     */
    public void getMemberPostListSuccess(HttpJsonResponse res) {

        if (res != null) {
            if (pbHelp != null && pbHelp.isLoading) {
                pbHelp.goneLoading();
            }

            String jsonString = res.getBodyArray().toString();
            Log.i("tiezilist", "jsonString = " + jsonString);

            List<CircleResponseResultItem> items = null;
            if (jsonString != null) {
                items = new Gson().fromJson(jsonString,new TypeToken<List<CircleResponseResultItem>>(){}.getType());
//                items = JSON.parseArray(jsonString, CircleResponseResultItem.class);
            }

            boolean hasMore = ! (items.size()<10); //res.getHead().get("hasMore").getAsBoolean();

            // Log.i("tiezilist", "items.toString() = " + items.toString() );

            if (items != null && items.size() >=0) {
                selfcreateLV.setVisibility(View.VISIBLE);
                selfcreate_nodata.setVisibility(View.GONE);
                if (refreshState == 0) {
                    postList.clear();
                }
                /*if (items.size() < 10) {
                    // 全部已加载
                    Toast.makeText(this, "全部已加载", Toast.LENGTH_SHORT).show();
                    // mListView.removeFooterView(loadMoreView);
                }
*/
                if (postList == null)
                    postList = items;
                else
                    postList.addAll(items);
                circleAdapter.setHasMore(hasMore);
                circleAdapter.notifyDataSetChanged();
                // mListView.setSelection(visibleLast - visibleCount + 1);
                // needLoad = true;
                if (refreshState != 0)
                    pno++;
            } if(circleAdapter!=null&& postList!=null && postList.size()>0&& items.size()==0){
                Toast.makeText(this,"已经到底了",Toast.LENGTH_LONG).show();
                circleAdapter.setHasMore(hasMore);
                circleAdapter.notifyDataSetChanged();
            }

            else {
//                SouYueToast.makeText(this, R.string.no_sendpost, Toast.LENGTH_SHORT).show();
                if (postList != null && postList.isEmpty()) {
                    selfcreateLV.setVisibility(View.GONE);
                    selfcreate_nodata.setVisibility(View.VISIBLE);
                   // selfcreate_nodata.setText("暂无帖子");
                    pbHelp.showNoData();;
                }

            }
            if (refreshState == 0)
                selfcreateLV.onRefreshComplete();
            else circleAdapter.loadMoreComplete();

        }

    }

//    public void getSelfcreateInterestListSuccess(HttpJsonResponse res, AjaxStatus status) {
//        try {
//            if (res != null) {
//                String jsonString = res.getBodyArray().toString();
//                Log.i("InterestListSuccess", "jsonString = " + jsonString);
//            }
//        } catch (Exception e) {
//            SouYueToast.makeText(this, "信息获取失败", SouYueToast.LENGTH_SHORT).show();
//        }
//
//    }

    @Override
    protected void onDestroy() {
        if (refreshRec != null)
            this.unregisterReceiver(refreshRec);
        super.onDestroy();
    }

    OnItemClickListener onItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

           /* if (position == 1) {
                if (!isFromFriend()) {
                    ShowPickDialog();
                }
            } else {*/

            if (IS_FROM_CIRCLE) {
                CircleResponseResultItem item = postList.get(position - 1);
                if (item != null) {
//                    Intent intent1 = new Intent(SelfCreateActivity.this, PostsActivity.class);
//                    intent1.putExtra("blog_id", item.getBlog_id());
//                    intent1.putExtra("interest_id", item.getInterest_id());
//                    startActivityForResult(intent1, 0);


                    SearchResultItem item1 = new SearchResultItem();
                    item1.setBlog_id(item.getBlog_id());
                    item1.keyword_$eq(item.getSrp_word());
                    item1.srpId_$eq(item.getSrp_id());
                    item1.setInterest_id(item.getInterest_id());
                    IntentUtil.skipDetailPage(SelfCreateActivity.this, item1, 0);
                }
            } else {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("selfCreateItem", (SelfCreateItem) adapter.getItem(position - 1));
                intent.setClass(SelfCreateActivity.this, SelfCreateDetailActivity.class);
                bundle.putSerializable("contact", contact);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
//        }
    };
    private PopupWindow mPopupWindow;

    public void selfCreateListToPullRefreshSuccess(SelfCreate sc,String originalUrl) {
        pbHelp.goneLoading();
        System.out.println("原URL>>>"+originalUrl);
        getselfCreateListPullRefresh(sc, originalUrl);
    }

    private void getselfCreateListPullRefresh(SelfCreate sc,String originalUrl ) {

        adapter.hasMore = sc.hasMore();

        Log.i("sc.items()", "sc.items()= " + sc.items());
        if (sc.items().size() > 0) {
            selfcreateLV.setVisibility(View.VISIBLE);
            selfcreate_nodata.setVisibility(View.GONE);
            adapter.clearData();//---------add by lv.---------看朋友的原创时不加草稿信息
            if (StringUtils.getUrlParam(originalUrl, "column_type").equals(selectionId + "")) {
                if (isFromFriend()) {
                    //朋友列表不显示本地草稿
//                    adapter.addRefDataFromFriend(sc.items());
                    adapter.addData(sc.items());
                } else {
                    adapter.addData(mDbDreft);
                    adapter.addData(sc.items());
                }
            }
        } else {
            if (!isFromFriend()) {
                adapter.clearData();
                adapter.addData(mDbDreft);
            }
            if (StringUtils.getUrlParam(originalUrl, "column_type").equals(selectionId + "") && adapter.getCount() == 0) {
                selfcreateLV.setVisibility(View.GONE);
//                SouYueToast.makeText(SelfCreateActivity.this, "没有原创数据", 1000).show();
              //  selfcreate_nodata.setVisibility(View.VISIBLE);
                //selfcreate_nodata.setText("暂无原创");
                pbHelp.showNoData();
//                selfcreateLV.setVisibility(View.GONE);
            }
        }

        adapter.notifyDataSetChanged();
        selfcreateLV.onRefreshComplete();
        if(adapter.getCount()==0){
            pbHelp.showNoData();
        }

    }

    public void friendCreateListToPullRefreshSuccess(SelfCreate sc, String originalUrl) {
        pbHelp.goneLoading();
        getselfCreateListPullRefresh(sc, originalUrl);
    }

    public void selfCreateListToLoadMoreSuccess(SelfCreate sc) {
        adapter.hasMore = sc.hasMore();
//        adapter.addMore(sc.items());
        adapter.addData(sc.items());
        adapter.isLoading = false;
        adapter.notifyDataSetChanged();
        if(adapter.getCount()==0){
            pbHelp.showNoData();
        }
    }

    public void friendCreateListToLoadMoreSuccess(SelfCreate sc) {
        adapter.hasMore = sc.hasMore();
//        adapter.addMore(sc.items());
        adapter.isLoading = false;
        adapter.addData(sc.items());
        adapter.notifyDataSetChanged();
        if(adapter.getCount()==0){
            pbHelp.showNoData();
        }
    }

    public void selfCreateListToDBSuccess(List<SelfCreateItem> scis) {

        selfcreateLV.onRefreshComplete();
        if (!CMainHttp.getInstance().isNetworkAvailable(this)) {

            if (scis != null && scis.size() != 0) {
                adapter.clearData();
//                adapter.clearDBdata();
//                adapter.addRefData(scis);
                adapter.addData(scis);
                adapter.notifyDataSetChanged();
            } else {
                selfcreateLV.setVisibility(View.GONE);
                this.pbHelp.showNetError();
            }


        } else {
            selfcreateLV.setVisibility(View.VISIBLE);
            if (null != scis && scis.size() > 0) {
//                adapter.addDBData(scis);
                mDbDreft = scis;
                Collections.reverse(mDbDreft);
                adapter.addData(scis);
//                adapter.notifyDataSetChanged();
            } else {
//                adapter.clearDBdata();
                mDbDreft = new ArrayList<SelfCreateItem>();
//                adapter.clearData();
            }
            if (isFromFriend()) {
                friendcreatelist=new FriendCreateList(HttpCommon.F_SELFCREATELIST_REQUEST_REF_ID,this);
                friendcreatelist.setParams(selectionId + "", "", contact.getChat_id(), im_statue);
                mMainHttp.doRequest(friendcreatelist);
            } else {
                selfcreatelist=new SelfCreateList(HttpCommon.SELFCREATELIST_REF_REQUEST_ID,this);
                selfcreatelist.setParams(selectionId + "", "");
                mMainHttp.doRequest(selfcreatelist);
            }
        }
    }

    private boolean isFromFriend() {
        return contact != null && contact.getChat_id() > 0;
    }

//    public void onHttpError(String methodName, AjaxStatus as) {
//        pbHelp.showNetError();
//        selfcreateLV.onRefreshComplete();
//        adapter.isLoading = false;
//        adapter.notifyDataSetChanged();
//
//        if (progdialog != null) {
//            progdialog.dismiss();
//        }
//        Log.v("Huang", "methodName error：" + methodName);
//
//    }

    public void loadDataMore(long start, String type) {
        if (isFromFriend()) {
            friendcreatelist=new FriendCreateList(HttpCommon.F_SELFCREATELIST_REQUEST_ID,this);
            friendcreatelist.setParams(selectionId + "", adapter.getLastId(), contact.getChat_id(), im_statue);
            mMainHttp.doRequest(friendcreatelist);
        } else {
            selfcreatelist=new SelfCreateList(HttpCommon.SELFCREATELIST_REQUEST_ID,this);
            selfcreatelist.setParams(selectionId + "", adapter.getLastId());
            mMainHttp.doRequest(selfcreatelist);
        }
    }

    @Override
    public void circleLoadDataMore() {
        refreshState = 1;
        refreshCircleList();
    }

    class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getBooleanExtra("ismodify", false)) {
                if (null != selfcreateLV) {
                    selfcreateLV.startRefresh();
                    if(selfcreate_nodata!=null && selfcreateLV.getRefreshableView().getCount()!=0){
                        selfcreate_nodata.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // 顶栏右侧按钮
            case R.id.self_create_title_right_btn:
//			showCreatePup();
                showBlog();
                break;
            default:
                break;
        }
    }

    private void showBlog() {
        Intent intent = new Intent(this, SendBlogActivity.class);
        SelfCreateItem sci = new SelfCreateItem();
        sci.column_name_$eq(getString(R.string.create_org_blog));
        sci.column_type_$eq(ConstantsUtils.TYPE_BLOG_SEARCH);
        intent.putExtra("selfCreateItem", sci);
        goNext(intent);
    }

    private void showWeiBo() {
        Intent intentweibo = new Intent(this, SendWeiboActivity.class);
        SelfCreateItem wsci = new SelfCreateItem();
        wsci.column_name_$eq(getString(R.string.create_org_weibo));
        wsci.column_type_$eq(ConstantsUtils.TYPE_WEIBO_SEARCH);
        intentweibo.putExtra("selfCreateItem", wsci);
        goNext(intentweibo);
    }

    private void goNext(Intent intent) {
        startActivity(intent);
//		mPopupWindow.dismiss();
//		disable.setVisibility(View.GONE);
    }

    /**
     * 显示选择原创类型窗口
     */
//	private void showCreatePup() {
//		if (mPopupWindow == null) {
//			createPupWindow();
//		}
//		mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.TOP, 0, getPopupLocation());
//		disable.setVisibility(View.VISIBLE);
//	}
    private int getPopupLocation() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return (int) (outMetrics.heightPixels - 200 * outMetrics.density);
    }

    /**
     * 弹出按钮框（短文章、长文章、取消）
     */
//	private void createPupWindow() {
//		View popupView = getLayoutInflater().inflate(R.layout.self_create_menu, null);
//		mPopupWindow = new PopupWindow(popupView, LayoutParams.FILL_PARENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
//		mPopupWindow.setAnimationStyle(R.style.menu_anim_style);
//		mPopupWindow.setFocusable(false);
//		mPopupWindow.setOutsideTouchable(false);
//
//		popupView.findViewById(R.id.self_create_munu_blog).setOnClickListener(this);
//		popupView.findViewById(R.id.self_create_munu_weibo).setOnClickListener(this);
//		popupView.findViewById(R.id.self_create_munu_cancel).setOnClickListener(this);
//	}

    /**
     * 选择提示对话框
     */
    public void ShowPickDialog() {
        String shareDialogTitle = getString(R.string.pick_dialog_title);
        MMAlert.showAlert(this, shareDialogTitle, getResources().getStringArray(R.array.picks_item), null, new MMAlert.OnAlertSelectId() {

            @Override
            public void onClick(int whichButton) {
                switch (whichButton) {
                    case 0: // 拍照
                        try {
                            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                            if (Utils.isIntentSafe(SelfCreateActivity.this, i)) {
                                startActivityForResult(i, 2);
                            } else {
                                SouYueToast.makeText(SelfCreateActivity.this, getString(R.string.dont_have_camera_app), SouYueToast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            SouYueToast.makeText(SelfCreateActivity.this, getString(R.string.cant_insert_album), SouYueToast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1: // 相册
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, 1);
                        break;
                    default:
                        break;
                }
            }

        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:// 如果是直接从相册获取
                    if (data != null) {
                        Uri uri = data.getData();
                        startPhotoZoom(uri, imageUri);
                    }
                    break;
                case 2:// 如果是调用相机拍照时
                    String picPath = null;
                    if (imageUri != null) {
                        picPath = Utils.getPicPathFromUri(imageUri, this);
                        int degree = 0;
                        if (!StringUtils.isEmpty(picPath))
                            degree = ImageUtil.readPictureDegree(picPath);
                        Matrix matrix = new Matrix();
                        if (degree != 0) {// 解决旋转问题
                            matrix.preRotate(degree);
                        }
                        Uri uri = Uri.fromFile(new File(picPath));
                        startPhotoZoom(uri, imageUri);
                    } else {
                        SouYueToast.makeText(this, "图片获取异常", SouYueToast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:// 取得裁剪后的图片
                    if (data != null) {
                        setPicToView(imageUri);
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri, Uri imageUri) {
        // LogDebugUtil.v("FAN", "startPhotoZoom URL: " + uri);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView(Uri imageUri) {

        progdialog.show();
        try {
            Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            drawable = new BitmapDrawable(photo);
            photo.compress(CompressFormat.JPEG, 100, new FileOutputStream(profileImgFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        boolean exit = profileImgFile.exists();
        LogDebugUtil.v("FAN", "setPicToView URL: " + profileImgFile.getAbsolutePath());
        if (!exit) {
            showToast(R.string.upload_photo_fail);
            return;
        }

        if (user != null) {
//            http.uploadUserHead(this, user.userId(), profileImgFile);
            UploadImageTask t = new UploadImageTask(this, user.userId(), profileImgFile);
            t.execute();
        } else {
            showToast(R.string.token_error);
        }
    }

    public void showToast(int resId) {
        SouYueToast.makeText(SelfCreateActivity.this, getResources().getString(resId), 0).show();
    }

    public void uploadSuccess(String url) {
        if (profileImgFile.exists()) {
            profileImgFile.delete();
        }
        File file = new File(IMAGE_FILE_LOCATION);
        if (file.exists())
            file.delete();
        LogDebugUtil.v("FAN", "onFinish URL: " + url);
        Log.i("uploadSuccess", "uploadSuccess:" + url);
//        bgUrl = url;
        if (!TextUtils.isEmpty(url)) {
            if (user != null) {
                UserRepairInfo info = new UserRepairInfo(HttpCommon.USER_REPIRE_USER_INFO_REQUEST,this);
                info.setParams(user.token(), null, user.name(), url, null);
                mMainHttp.doRequest(info);
//                http.updateProfile(user.token(), null, user.name(), url, null);
            }
        } else {
            showToast(R.string.upload_photo_fail);
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        Object obj;
        HttpJsonResponse response = request.getResponse();
        switch (request.getmId()){
            case HttpCommon.USER_REPIRE_USER_INFO_REQUEST:
                updateProfileSuccess();
                break;
            case HttpCommon.SELFCREATELIST_REQUEST_ID:
                obj= request.getResponse();
                request.getCacheKey();
                selfCreateListToLoadMoreSuccess(new SelfCreate((HttpJsonResponse) obj));
                break;
            case HttpCommon.SELFCREATELIST_REF_REQUEST_ID:
                obj = request.getResponse();
                selfCreateListToPullRefreshSuccess(new SelfCreate((HttpJsonResponse) obj),request.getCacheKey());
                break;
            case HttpCommon.F_SELFCREATELIST_REQUEST_ID:
                obj = request.getResponse();
                friendCreateListToLoadMoreSuccess(new SelfCreate((HttpJsonResponse) obj));
                break;
            case HttpCommon.F_SELFCREATELIST_REQUEST_REF_ID:
                obj = request.getResponse();
                friendCreateListToPullRefreshSuccess(new SelfCreate((HttpJsonResponse) obj),request.getCacheKey());
                break;
            case HttpCommon.CIRCLE_MEMBERLIST_REQUEST:
                getMemberPostListSuccess(response);
                break;

        }
    }

    @Override
    public void onHttpError(IRequest request) {
        pbHelp.showNetError();
        selfcreateLV.onRefreshComplete();
        if (adapter!=null) {
            adapter.isLoading = false;
            adapter.notifyDataSetChanged();
        }

        if (progdialog != null) {
            progdialog.dismiss();
        }
    }

    public void updateProfileSuccess() {
        LogDebugUtil.v("FAN", "drawable=" + drawable);
        /*head_background.setImageDrawable(drawable);
        Log.i("bgUrl", "bgUrl:" + bgUrl);
        if (user != null)
            user.bgUrl_$eq(bgUrl);*/
        SYUserManager.getInstance().setUser(user);
        ThreadPoolUtil.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ImserviceHelp.getInstance().im_update(4, 0, um.getImage());
            }
        });
        showToast(R.string.upload_background_success);
        if (progdialog != null) {
            progdialog.dismiss();
        }
    }

    class MySelfCreateCircleAdapter extends BaseAdapter {

        private TextView txtRefresh;
        private ProgressBar progressBar;
        private static final int VIEW_TYPE_MORE = 0;
        private static final int VIEW_TYPE_CONTENT = 1;
        private static final int MAX_VIEW_TYPE_COUNT = 2;
        //        private static final int VIEW_TYPE_EMPTY = 3;
        private int count;
        // 下面用于测量图片宽高
        protected int height, width;
        private int deviceWidth;
        private int height08, width08;
        private boolean hasMore;

        public MySelfCreateCircleAdapter() {
            deviceWidth = CircleUtils.getDeviceWidth(SelfCreateActivity.this);
            width = (deviceWidth - DeviceUtil.dip2px(SelfCreateActivity.this, 48)) / 3;
            height = (int) ((2 * width) / 3);
            width08 = (int) (0.8 * width);
            height08 = (int) (0.8 * height);
        }

        public void setHasMore(boolean hasMore){
            this.hasMore = hasMore;
        }

        @Override
        public int getViewTypeCount() {
            return MAX_VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            int len = postList.size();
            if (position == len && hasMore) {
                /*if (len == 0) {
                    return VIEW_TYPE_EMPTY;
                } else {
                }*/
                return VIEW_TYPE_MORE;
            } else {
                return VIEW_TYPE_CONTENT;
            }

        }

        @Override
        public int getCount() {

            if (hasMore)
                return postList.size() + 1;
            return postList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (postList.size() == 0) {
                return convertView;
            }
            if (getItemViewType(position) == VIEW_TYPE_MORE) {
                View view = View.inflate(SelfCreateActivity.this, R.layout.refresh_footer, null);
                txtRefresh = (TextView) view.findViewById(R.id.pull_to_refresh_text);
                progressBar = (ProgressBar) view.findViewById(R.id.pull_to_refresh_progress);
                progressBar.setVisibility(View.GONE);
                txtRefresh.setText("更多");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        txtRefresh.setText("加载中...");
                        circleLoadDataMore();
                    }
                });
                return view;
            }
            /*else if (getItemViewType(position) == VIEW_TYPE_EMPTY) {
                View emptyView = View.inflate(SelfCreateActivity.this, R.layout.emptylist, null);
                TextView textView = (TextView) emptyView.findViewById(R.id.emptylist_txt);
                textView.setText("您还为发表过任何帖子");
                return emptyView;
            }*/
            if (convertView == null) {
                convertView = View.inflate(SelfCreateActivity.this, R.layout.circle_vcard_post_item, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.tv_circle_vcard_item_title);
                holder.brief = (TextView) convertView.findViewById(R.id.tv_circle_vcard_item_brief);
                holder.time = (TextView) convertView.findViewById(R.id.tv_circle_vcard_time);
                holder.up_num = (TextView) convertView.findViewById(R.id.tv_circle_vcard_up);
                holder.follow_num = (TextView) convertView.findViewById(R.id.tv_circle_vcard_comment);
                holder.source = (TextView) convertView.findViewById(R.id.tv_circle_vcard_source);

                holder.rlBottom = (RelativeLayout) convertView.findViewById(R.id.rl_circle_vcard_total);
                holder.rlImages = (RelativeLayout) convertView.findViewById(R.id.rl_circle_vcard_item_content_imgs);
                holder.llImages = (LinearLayout) convertView.findViewById(R.id.ll_circle_vcard_item_content_imgs);
                holder.img1 = (ImageView) convertView.findViewById(R.id.iv_circle_vcard_item_content_img1);
                holder.img2 = (ImageView) convertView.findViewById(R.id.iv_circle_vcard_item_content_img2);
                holder.img3 = (ImageView) convertView.findViewById(R.id.iv_circle_vcard_item_content_img3);
                holder.img4 = (ImageView) convertView.findViewById(R.id.iv_circle_vcard_item_content_img4);
                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.img2
                        .getLayoutParams();
                params1.width = width;
                params1.height = height;
                holder.img2.setLayoutParams(params1);
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.img3
                        .getLayoutParams();
                params2.width = width;
                params2.height = height;
                holder.img3.setLayoutParams(params2);
                LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) holder.img4
                        .getLayoutParams();
                params3.width = width;
                params3.height = height;
                holder.img4.setLayoutParams(params3);
                holder.pic = (ImageView) convertView.findViewById(R.id.iv_circle_vcard_item_image);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            CircleResponseResultItem postItem = postList.get(position);
            if ("".equals(postItem.getTitle()) || postItem.getTitle() == null) {
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(postItem.getBrief());
            } else {
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(postItem.getTitle());
            }
            /*ViewTreeObserver observer = holder.title.getViewTreeObserver();
            final ViewHolder finalHolder = holder;
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver vto = finalHolder.title.getViewTreeObserver();
                    vto.removeOnGlobalLayoutListener(this);
                    count = finalHolder.title.getLineCount();
//                System.out.println("lineCount=" + count);
                    if (count == 1) {
                        finalHolder.rlBottom.setPadding(0, SingleCricleListAdapter.dip2px(mContext, 15), 0, 0);
                    } else {
                        finalHolder.rlBottom.setPadding(0, SingleCricleListAdapter.dip2px(mContext, 10), 0, 0);
                    }
                }
            });*/
            holder.time.setText(StringUtils.convertDate(postItem.getCreate_time() + ""));
            holder.source.setText(postItem.getSrp_word());
            holder.up_num.setText(postItem.getGood_num() + "");
            holder.follow_num.setText(postItem.getFollow_num() + "");

            loadImages(holder, postItem.getImages(), postItem);

            return convertView;
        }

        public void loadMoreComplete() {
            if (progressBar != null && txtRefresh != null) {
                progressBar.setVisibility(View.GONE);
                txtRefresh.setText("更多");
            }
        }

        // 加载内容中的图片
        private void loadImages(final ViewHolder holder, final List<String> imgs, final CircleResponseResultItem item) {
            holder.title.post(new Runnable() {
                @Override
                public void run() {
                    int count = holder.title.getLineCount();
                    if (imgs == null || imgs.size() == 0) {
                        if (count == 1) {
                            holder.rlBottom.setPadding(0, DeviceUtil.dip2px(mContext, 5), 0, 0);
                        } else {
                            holder.rlBottom.setPadding(0, DeviceUtil.dip2px(mContext, 10), 0, 0);
                        }
                    }
                }
            });
            if (imgs == null || imgs.size() == 0) {
                holder.rlImages.setVisibility(View.GONE);
                holder.pic.setVisibility(View.GONE);
                return;
            } else if (imgs.size() == 1 || imgs.size() == 2) {
                holder.rlImages.setVisibility(View.GONE);
                holder.pic.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params01 = (RelativeLayout.LayoutParams) holder.pic
                        .getLayoutParams();
                params01.width = width08;
                params01.height = height08;
                params01.setMargins(DeviceUtil.dip2px(SelfCreateActivity.this, 20), 0, 0, 0);
                holder.pic.setLayoutParams(params01);
                loadImage(holder.pic, imgs.get(0));
                holder.rlBottom.setPadding(0, DeviceUtil.dip2px(mContext, 10), 0, 0);
            } else {
                holder.pic.setVisibility(View.GONE);
                int imgsSize = imgs.size();
                if (imgsSize > 3)
                    imgsSize = 3;
                holder.rlImages.setVisibility(View.VISIBLE);
                holder.rlBottom.setPadding(0, DeviceUtil.dip2px(mContext, 10), 0, 0);
                switch (imgsSize) {
                    case 1:
                        // holder.img1.setVisibility(View.VISIBLE);
                        // holder.llImages.setVisibility(View.GONE);
                        // loadImage(holder.img1, imgs.get(0));
                        holder.img1.setVisibility(View.GONE);
                        holder.llImages.setVisibility(View.VISIBLE);
                        loadImage(holder.img2, imgs.get(0));
                        holder.img3.setVisibility(View.INVISIBLE);
                        holder.img4.setVisibility(View.INVISIBLE);
                        holder.img2.setOnClickListener(new BrowserPicListener(item, 0));
                        break;
                    case 2:
                        holder.img1.setVisibility(View.GONE);
                        holder.llImages.setVisibility(View.VISIBLE);
                        loadImage(holder.img2, imgs.get(0));
                        loadImage(holder.img3, imgs.get(1));
                        holder.img2.setOnClickListener(new BrowserPicListener(item, 0));
                        holder.img3.setOnClickListener(new BrowserPicListener(item, 1));
                        holder.img4.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        holder.img1.setVisibility(View.GONE);
                        holder.llImages.setVisibility(View.VISIBLE);
                        loadImage(holder.img2, imgs.get(0));
                        loadImage(holder.img3, imgs.get(1));
                        loadImage(holder.img4, imgs.get(2));
                        holder.img2.setOnClickListener(new BrowserPicListener(item, 0));
                        holder.img3.setOnClickListener(new BrowserPicListener(item, 1));
                        holder.img4.setOnClickListener(new BrowserPicListener(item, 2));
                        break;
                    case 4:
                        holder.img1.setVisibility(View.VISIBLE);
                        holder.llImages.setVisibility(View.VISIBLE);
                        loadImage(holder.img1, imgs.get(0));
                        loadImage(holder.img2, imgs.get(1));
                        loadImage(holder.img3, imgs.get(2));
                        loadImage(holder.img4, imgs.get(3));
                        holder.img1.setOnClickListener(new BrowserPicListener(item, 0));
                        holder.img2.setOnClickListener(new BrowserPicListener(item, 1));
                        holder.img3.setOnClickListener(new BrowserPicListener(item, 2));
                        holder.img4.setOnClickListener(new BrowserPicListener(item, 3));
                        break;

                }
            }
        }
    }

    static class ViewHolder {
        TextView title, brief, follow_num, time, up_num, source;
        ImageView img1, img2, img3, img4, pic;
        LinearLayout llImages;
        RelativeLayout rlImages, rlBottom;
    }


    /**
     * 图片浏览监听
     */
    private class BrowserPicListener implements OnClickListener {
        private CircleResponseResultItem item;
        private int pos;

        public BrowserPicListener(CircleResponseResultItem item, int pos) {
            this.item = item;
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            // 设置图片浏览
            if (StringUtils.isNotEmpty(item.getImages())) {
                List<String> images = item.getImages();
                Intent intent = new Intent();
                intent.setClass(SelfCreateActivity.this, TouchGalleryActivity.class);
                TouchGallerySerializable tg = new TouchGallerySerializable();
                tg.setItems(images);
                tg.setClickIndex(pos);
                Bundle extras = new Bundle();
                extras.putSerializable("touchGalleryItems", tg);
                intent.putExtras(extras);
                SelfCreateActivity.this.startActivity(intent);
            }
        }

    }

    private static final int DEFAULT_IMAGE_ID = R.drawable.default_image;

    private void loadImage(ImageView imageView, String url) {
        //aq.id(imageView).image(url, true, true, 0, DEFAULT_IMAGE_ID);
        PhotoUtils.showCard(PhotoUtils.UriType.HTTP,url,imageView, MyDisplayImageOption.smalloptions);
    }

}
