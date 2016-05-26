package com.zhongsou.souyue.circle.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.FirstInActivity;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.CircleListAdapter;
import com.zhongsou.souyue.circle.fragment.CircleBarFragment;
import com.zhongsou.souyue.circle.fragment.EssencePostFragment;
import com.zhongsou.souyue.circle.model.CircleIndexMenuInfo;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.circle.view.CircleIndexPopupMenu;
import com.zhongsou.souyue.circle.view.PagerSlidingTabStrip;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.fragment.BlogFragment;
import com.zhongsou.souyue.fragment.ChatRoomFragment;
import com.zhongsou.souyue.fragment.CommonFragment;
import com.zhongsou.souyue.fragment.ForumFragment;
import com.zhongsou.souyue.fragment.KunlunJueFragment;
import com.zhongsou.souyue.fragment.MySharesFragment;
import com.zhongsou.souyue.fragment.PhotoSearchFragment;
import com.zhongsou.souyue.fragment.QAFragment;
import com.zhongsou.souyue.fragment.RecommendFragment;
import com.zhongsou.souyue.fragment.SRPFragment;
import com.zhongsou.souyue.fragment.SRPSelfCreateFragment;
import com.zhongsou.souyue.fragment.WebpageFragment;
import com.zhongsou.souyue.fragment.WeiboFragment;
import com.zhongsou.souyue.fragment.XiaoDanganFragment;
import com.zhongsou.souyue.module.CWidgetSecondList;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.ShortCutInfo;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CircleExitCircleRequest;
import com.zhongsou.souyue.net.circle.CircleGetCircleMenuRequest;
import com.zhongsou.souyue.net.circle.CircleGetMeberRoleRequest;
import com.zhongsou.souyue.net.circle.CircleIndexNavRequest;
import com.zhongsou.souyue.net.circle.InterestSubscriberReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.ui.CustomViewPager;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.ViewPagerWithTips;
import com.zhongsou.souyue.ui.lib.DialogHelper;
import com.zhongsou.souyue.ui.lib.DialogPlus;
import com.zhongsou.souyue.utils.AnoyomousUtils;
import com.zhongsou.souyue.utils.CollectionUtils;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LocalBroadCastHelper;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob zhou on 14-8-20.
 * <p/>
 * 兴趣圈首页
 */
public class CircleIndexActivity extends BaseActivity implements View.OnClickListener {

    static final String TAG = "CircleIndexActivity";
//    private static final int GETMEBERROLE_REQUESTID = 5454;

    private String genre = "interest";   // 类型判断是SRP还是兴趣圈,固定为兴趣圈

    private ProgressBarHelper progress;

    private TextView circleNameTv;

    private ImageButton searchImgBtn;

    private ImageButton menuImgBtn;

    private ImageView redPointImg;  //菜单按钮右上角红点

    private PagerSlidingTabStrip pagerSlidingTabStrip;

    private CustomViewPager viewPager;

    private List<String> titles = new ArrayList<String>();

//    private Http http;

    // Intent Data
    private String srp_id;
    private String keyword;
    private String interest_name;
    private String from;

    private int role = -1;  //0-非圈子成员 1-圈主 2-圈子普通成员 3-游客, 初始化给一个无意义的值-1

    private int is_bantalk;

    private boolean is_private;

    // 从Menu接口中获取
    private long interest_id;

    private ArrayList<SRPFragment> srpFragments = new ArrayList<SRPFragment>();

    private MyAdapter myAdapter;

    public List<NavigationBar> navs;

    private CircleIndexMenuInfo menuInfo;

    private CircleIndexPopupMenu circleIndexPopupMenu;
    private SuberDao suberDao;

    private List<BlogFragment> blogFragmentList = new ArrayList<BlogFragment>();

    public static final int REQUEST_CODE_MEMBER_SETTING_ACTIVITY = 1001;
    public static final int REQUEST_CODE_POST_DETAIL_ACTIVITY = 1002;
    public static final int REQUEST_CODE_LOGIN_ACTIVITY = 900;

    protected SYSharedPreferences sysp = SYSharedPreferences.getInstance();

    //    private SearchResult searchResult;//搜悦移动统计用到，所以需要挪到外面来
    private CWidgetSecondList mWidgetList; // 二级导航
    private String mTitle;
    private String md5;
    private String nickName;
    private UpdateBroadCastRecever receiver = new UpdateBroadCastRecever();
    private IntentFilter mFilter = new IntentFilter("update_font");
    private ImageButton subButton;

    public static DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(false).build();
    private String mCurrentCircleHead; // 当前圈头像
    private boolean isSubered = false;
    private boolean isClickable = true;
    private LocalBroadcastManager localBroadcastManager;
    private EssencePostFragment essencePostFragment;    //精华区Fragment
    private CircleBarFragment circleBarFragment;    //圈吧Fragment

    public class UpdateBroadCastRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (myAdapter != null && "update_font".equals(action)) {
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_index);
        registerReceiver(receiver, mFilter);
        initUI();
        bindListener();
        initData();
        UmengStatisticUtil.onEvent(this, UmengStatisticEvent.CIRCLE_ACTIVITY);  //Umeng
    }

    private void initUI() {
        progress = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
        circleNameTv = (TextView) findViewById(R.id.circle_index_circlename_tv);
        searchImgBtn = (ImageButton) findViewById(R.id.circle_index_search_imgBtn);
        menuImgBtn = (ImageButton) findViewById(R.id.circle_index_menu_imgBtn);
        redPointImg = (ImageView) findViewById(R.id.cicle_red_point_img);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.circle_index_indicator);
        subButton = (ImageButton) findViewById(R.id.btn_detail_subscribe);
        initPagerSlidingTabStrip();
        //圈吧首页的条目
        viewPager = (CustomViewPager) findViewById(R.id.circle_index_viewpager);
    }

    /**
     * 初始化pagerSlidingTabStrip
     */
    private void initPagerSlidingTabStrip() {
        pagerSlidingTabStrip
                .setTextColorResource(R.color.pstrip_text__normal_color);
        //设置高亮颜色按钮
        pagerSlidingTabStrip.setIndicatorColorResource(R.color.pstrip_text_selected_color_red);
        //设置选中文本颜色
        pagerSlidingTabStrip.setTextSelectedColor(
                getResources().getColor(R.color.pstrip_text_selected_color_red));
        //设置字体大小
        pagerSlidingTabStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.space_14));
        //设置下划线高度
        pagerSlidingTabStrip.setUnderlineHeight(getResources().getDimensionPixelOffset(R.dimen.space_0_8));
        //设置divider的高度
        pagerSlidingTabStrip.setIndicatorHeight(getResources().getDimensionPixelOffset(R.dimen.space_1));
        //设置线条的颜色
        pagerSlidingTabStrip.setUnderlineColorResource(R.color.bar_line_color);
    }

    private void bindListener() {
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {

                if (srpFragments != null && srpFragments.size() > 0) {
                    if (srpFragments.get(i) instanceof BlogFragment) {
                        BlogFragment blogFragment = (BlogFragment) srpFragments.get(i);
                        if (menuInfo != null) {
                            Boolean isAdmin = menuInfo.isAdmin();
                            blogFragment.updatePenView(isAdmin);
                        }
                        //((BlogFragment) srpFragments.get(i)).updatePenView(menuInfo.isAdmin());
                    }
                }

                isLoadData();
                //添加微件统计
                if (navs.get(i).getTypeId() != -4) {
                    UpEventAgent.onWidgetView(CircleIndexActivity.this, navs.get(i).getTypeId() + "." + navs.get(i).category(), navs.get(i).md5() + "." + navs.get(i).title(), mWidgetList.getSrpId() + "." + mWidgetList.getKeyword());//移动统计系统查看微件统计
                } else {
                    UpEventAgent.onWidgetView(CircleIndexActivity.this, navs.get(i).getTypeSt() + "." + navs.get(i).category(), navs.get(i).md5() + "." + navs.get(i).title(), mWidgetList.getSrpId() + "." + mWidgetList.getKeyword());//移动统计系统查看微件统计
                }
                UmengStatisticUtil.onEvent(CircleIndexActivity.this, UmengStatisticEvent.CIRCLE_CHANNEL_SELECTED);    //Umeng

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        viewPager.setOnBeginListener(new ViewPagerWithTips.OnBeginListener() {
            @Override
            public void onBeginListener() {
                onBackPressClick(null);
            }
        });

        viewPager.setOnEndListener(new ViewPagerWithTips.OnEndListener() {
            @Override
            public void onEndListener() {
                Toast.makeText(CircleIndexActivity.this, "已经到最后一页", Toast.LENGTH_SHORT).show();
            }
        });

        progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                CircleGetCircleMenuRequest.send(CircleIndexActivity.this, SYUserManager.getInstance().getToken(), srp_id);
                CircleIndexNavRequest.send(HttpCommon.CIRLCE_LIST_NAV_REQUEST, CircleIndexActivity.this, keyword, srp_id, null, CircleIndexNavRequest.WIDGET_LIST_CIRCLEINDEX);
            }
        });

        searchImgBtn.setOnClickListener(this);
        menuImgBtn.setOnClickListener(this);
        subButton.setOnClickListener(this);
    }


    private void initData() {
        navs = new ArrayList<NavigationBar>();
        suberDao = new SuberDaoImp();
        myAdapter = new MyAdapter(getSupportFragmentManager());
        srp_id = getIntent().getStringExtra("srp_id");
        keyword = getIntent().getStringExtra("keyword");
        keyword = StringUtils.isNotEmpty(keyword) ? keyword.replaceAll(" ", " ") : "";
        interest_name = getIntent().getStringExtra("interest_name");
        from = getIntent().getStringExtra("from");
        mTitle = getIntent().getStringExtra("title");
        md5 = getIntent().getStringExtra("md5");
        if (isFromAppWidget()) {
            doFromAppWidget();
            return;
        }
        if (ShortCutInfo.FROM_SHORTCUT.equals(from)) {
            if (doFromShortcut()) {
                return;
            }
        }
        circleNameTv.setText(interest_name);
        CircleGetCircleMenuRequest.send(this, SYUserManager.getInstance().getToken(), srp_id);
        CircleIndexNavRequest.send(HttpCommon.CIRLCE_LIST_NAV_REQUEST, this, keyword, srp_id, null, CircleIndexNavRequest.WIDGET_LIST_CIRCLEINDEX);

    }

    public void searchResultSuccess(CWidgetSecondList widgetList) {
        if (!widgetList.isShowMenu()) {
            try {
                findViewById(R.id.circle_index_indicator_father).setVisibility(View.GONE);
            } catch (Exception e) {

            }
        }
        progress.goneLoading();
        this.mWidgetList = widgetList;
//        interest_id = searchResult.getInterest_id();
        interest_id = widgetList.getInterestId();
        getRole();
        LocalBroadCastHelper.sendGoneLoading(CircleIndexActivity.this);
        List<NavigationBar> nav = widgetList.getNav();
        for (NavigationBar n : nav) {
            n.setInterest_id(interest_id);
        }
        initNavbar(nav);
        //搜悦统计系统 -  圈子首页查看事件
        UpEventAgent.onGroupAccess(this, interest_id + "." + interest_name, "");
    }


    private void getRole() {
//        http.getMemberRole(SYUserManager.getInstance().getToken(), interest_id);
//        CircleGetMeberRoleRequest circleGetMeberRole = new CircleGetMeberRoleRequest(GETMEBERROLE_REQUESTID, this);
//        circleGetMeberRole.setParams(SYUserManager.getInstance().getToken(), interest_id);
//        CMainHttp.getInstance().doRequest(circleGetMeberRole);
        CircleGetMeberRoleRequest.send(HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID, this, SYUserManager.getInstance().getToken(), interest_id);

    }

    @Override
    public void onHttpResponse(IRequest _request) {
        super.onHttpResponse(_request);
        switch (_request.getmId()) {
            case HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID:
                getMemberRoleSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID:
                updateQuitCricleSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID_2:
                saveRecomentCirclesSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRCLE_GET_CIRCLE_MENU_REQUESTID:
                getCircleMenuSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_LIST_NAV_REQUEST:
                searchResultSuccess((CWidgetSecondList) _request.getResponse());
                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                saveRecomentCirclesSuccess(_request.<HttpJsonResponse>getResponse());
                break;

        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        switch (_request.getmId()) {
            case HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID:
                subButton.setClickable(true);
                UIHelper.ToastMessage(this,
                        R.string.cricle_manage_upload_quit_failed);
                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID_2:
                isClickable = true;
                IHttpError error2 = _request.getVolleyError();
                if (error2.getErrorType() == IHttpError.TYPE_SERVER_ERROR) {
                    UIHelper.ToastMessage(this,
                            R.string.cricle_manage_save_circle_failed);
                } else {
                    UIHelper.ToastMessage(this,
                            R.string.networkerror);
                }

                break;
            case HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID:
                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                subButton.setClickable(true);
                IHttpError error1 = _request.getVolleyError();
                if (error1.getErrorType() == IHttpError.TYPE_SERVER_ERROR) {
                    UIHelper.ToastMessage(this,
                            R.string.cricle_manage_save_circle_failed);
                } else {
                    UIHelper.ToastMessage(this,
                            R.string.networkerror);
                }
                break;
            default:
                progress.showNetError();
                break;
        }
    }


    public void getMemberRoleSuccess(HttpJsonResponse res) {
//        int statusCode = res.getCode();
//        if (statusCode != 200) {
//            return;
//        }
        nickName = res.getBody().get("nickname").getAsString();
        if (circleIndexPopupMenu != null) {
            circleIndexPopupMenu.setNickName(nickName);
        }
        role = res.getBody().get("role").getAsInt();
        is_bantalk = res.getBody().get("is_bantalk").getAsInt();
        is_private = res.getBody().get("is_private").getAsBoolean();
        //设置匿名头像
        mCurrentCircleHead = res.getBody().get("image").getAsString();
        AnoyomousUtils.setCurrentPrivateHeadIcon(mCurrentCircleHead, interest_id + "");
        updateSubAdapterRole();
        if (role == Constant.ROLE_NONE) {        //role == 0    //非圈成员(未订阅)
            if (is_private) {
                finish();
                IntentUtil.gotoSecretCricleCard(this, interest_id, 0);
                return;
            }
            redPointImg.setVisibility(View.GONE);
            isSubered = false;
            subButton.setImageResource(R.drawable.srp_subscribe_selector);

        } else {
            subButton.setImageResource(R.drawable.srp_no_subscribe_selector);
            isSubered = true;
        }
        subButton.setVisibility(View.VISIBLE);

        setIsBanTalk();
    }


    public void getCircleMenuSuccess(HttpJsonResponse response) {
        JsonObject json = response.getBody();
        menuInfo = new Gson().fromJson(json, new TypeToken<CircleIndexMenuInfo>() {
        }.getType());
        updateBlogFragmentPubPermission();
        menuInfo.setSrpId(srp_id);
        menuInfo.setKeyword(keyword);
        String interestLogo = menuInfo.getInterestLogo();
        if (!StringUtils.isEmpty(interestLogo)) {
            ImageLoader.getInstance().loadImage(interestLogo, options, null);
        }
        circleIndexPopupMenu = new CircleIndexPopupMenu(this, menuInfo, this);
        //增加弹框的背景
        circleIndexPopupMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.common_pop_bg));
        circleIndexPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                menuImgBtn.setImageResource(R.drawable.title_bar_menu_selector);
            }
        });


        if (menuInfo.getAtCount() + menuInfo.getFollowMyCount() > 0
                && !(SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST))) { //判断当不是游客时
            redPointImg.setVisibility(View.VISIBLE);
        } else {
            redPointImg.setVisibility(View.GONE);
        }

    }

    /**
     * <p> 兴趣圈微件列表的第一个是信息发布微件,更新信息发布微件的发布权限</p>
     */
    private void updateBlogFragmentPubPermission() {
        if (CollectionUtils.isNotEmpty(srpFragments)) {
            SRPFragment fragment = srpFragments.get(0);
            if (fragment instanceof BlogFragment) {
                ((BlogFragment) fragment).updatePenView(menuInfo.isAdmin());
            }
        }
    }


    public void saveRecomentCirclesSuccess(HttpJsonResponse res) {
        subButton.setClickable(true);
        if (res.getBody().get("state").getAsInt() == 1) {
            SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
            Toast.makeText(CircleIndexActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
            subButton.setImageResource(R.drawable.srp_no_subscribe_selector);
            isSubered = true;
            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_ADD_CIRCLE_CLICK);
            AnoyomousUtils.setCurrentPrivateHeadIcon(mCurrentCircleHead, interest_id + "");
            menuImgBtn.setImageResource(R.drawable.title_bar_menu_selector);
            //role = 2;
            role = Constant.ROLE_NORMAL;
            updateSubAdapterRole();
//            http.getCircleMenu(SYUserManager.getInstance().getToken(), srp_id);
            CircleGetCircleMenuRequest.send(this, SYUserManager.getInstance().getToken(), srp_id);
            // 统计
            UpEventAgent.onGroupJoin(this, interest_id + "." + "", "");
            SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
            //数据库操作
            SuberedItemInfo info = new SuberedItemInfo();
            info.setId(interest_id);
            info.setTitle(keyword);
            info.setCategory("interest");
            info.setSrpId(srp_id);
            info.setImage(menuInfo.getInterestLogo());
            info.setKeyword(keyword);
            info.setType("0");
            suberDao.addOne(info);

           setBackData(1, interest_id, srp_id); //设置返回数据
        }
    }

    /**
     * 设置返回的数据
     * @param result 1: 订阅；2：退订
     * @param interestId
     * @param srpId 用于发广播
     */
    private void setBackData(int result, long interestId, String srpId){  // result 1: 订阅；2：退订
        //设置返回数据
        Intent backIntent = new Intent();
        backIntent.putExtra("result", result);
        backIntent.putExtra("interest_id", String.valueOf(interestId));
        setResult(DetailModuleActivity.CIRCLE_REQUEST_CODE,backIntent);

        if(result == 2) result = 0;     //订阅标志位映射

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent();
        intent.setAction(ConstantsUtils.SUB_STATUS_ACTION);
        intent.putExtra("result", result);   // result 1: 订阅；0：未订阅
        intent.putExtra("srp_id", srpId);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 圈子管理-退出圈子回调方法
     *
     * @param res
     */
    public void updateQuitCricleSuccess(HttpJsonResponse res) {
//        JsonObject body = res.getBody();
//        JsonElement result = body.get("result");
//        int isSucc = result.getAsInt();
//        if (isSucc == 200) {
        // 统计

        int code = res.getBody().get("result").getAsInt();

        if (code == 500) {
            SouYueToast.makeText(this, R.string.subscibe_delete_fail, Toast.LENGTH_LONG).show();
            return;
        } else if (code == 501) {
            SouYueToast.makeText(this, R.string.cricle_admin_no_quit_setting_text, Toast.LENGTH_LONG).show();
            return;
        }


        subButton.setClickable(true);
        subButton.setImageResource(R.drawable.srp_subscribe_selector);
        isSubered = false;
        UpEventAgent.onGroupQuit(this, interest_id + "." + "", "");
        UIHelper.ToastMessage(this,
                R.string.cricle_manage_upload_quit_success);
        SYSharedPreferences.getInstance().putBoolean(
                SYSharedPreferences.KEY_UPDATE, true);
//            if(!ConfigApi.isSouyue()){
//                //退出兴趣圈，跳转到搜悦新闻首页
//                LocalBroadCastUtil.sendLoginToNewsHome(this);
//            }
        //重置当前的全头像为用户头像
        AnoyomousUtils.setCurrentPrivateHeadIcon(SYUserManager.getInstance().getImage(), interest_id + "");
        role = Constant.ROLE_NONE;
        updateSubAdapterRole();
        redPointImg.setVisibility(View.GONE);
        if (is_private) {
            finish();
            //数据库操作
            SuberedItemInfo info = new SuberedItemInfo();
            info.setTitle(keyword);
            info.setCategory("interest");
            info.setSrpId(srp_id);
            info.setKeyword(keyword);
            info.setType("1");
            suberDao.clearOne(info);
        } else {
            //数据库操作
            SuberedItemInfo info = new SuberedItemInfo();
            info.setTitle(keyword);
            info.setCategory("interest");
            info.setSrpId(srp_id);
            info.setKeyword(keyword);
            info.setType("0");
            suberDao.clearOne(info);
        }

        setBackData(2, interest_id, srp_id);
//        }
//        else {
//            UIHelper.ToastMessage(this,
//                    R.string.cricle_manage_upload_quit_failed);
//            return;
//        }
    }

    public void updateSubAdapterRole() {
        CircleListAdapter.role = role;
        CircleListAdapter.is_bantalk = is_bantalk;
        CircleListAdapter.is_private = is_private;
    }


    private void initNavbar(List<NavigationBar> navs) {
//        this.mWidgetList = searchResult;
        this.navs = navs;
        if (CollectionUtils.isEmpty(navs)) {
            progress.showNetError();
            return;
        }
        srpFragments.clear();
        titles.clear();
        for (int i = 0; i < navs.size(); i++) {
            //=======分享桌面快捷方式:取固定产品微件：logo图,超A专用 update by FM======================
//            if (!ConfigApi.isSouyue() && navs.get(i).category().equals("web") && navs.get(i).title().equalsIgnoreCase("logo图")) {
//                ShortCutUtils.getInstance(this).loadLogoImgUrl(navs.get(i).md5());
//            }
            //=============================
            SRPFragment srpFragment = getFragment(navs.get(i));
            srpFragments.add(srpFragment);
            titles.add(navs.get(i).title());

        }

        if (srpFragments.size() > 0) {
            myAdapter.setList(srpFragments);
            myAdapter.setTitles(titles);
            myAdapter.notifyDataSetChanged();
            viewPager.setAdapter(myAdapter);
            pagerSlidingTabStrip.setViewPager(viewPager);
            viewPager.setCurrentItem(0);
            //添加微件统计
//            if(navs.get(0).getTypeId()!=-4){
//                UpEventAgent.onWidgetView(this, navs.get(0).getTypeId() + "." + navs.get(0).category(), navs.get(0).md5() + "." + navs.get(0).title(), searchResult.srpId() + "." + searchResult.keyword());//移动统计系统查看微件统计
//            }else{
//                UpEventAgent.onWidgetView(this, navs.get(0).getTypeSt() + "." + navs.get(0).category(), navs.get(0).md5() + "." + navs.get(0).title(), searchResult.srpId() + "." + searchResult.keyword());//移动统计系统查看微件统计
//            }
        }

        for (int i = 0; i < navs.size(); i++) {
            if (navs.get(i).md5().equals(md5)) {
                viewPager.setCurrentItem(i);
            }
        }
    }

    private SRPFragment getFragment(NavigationBar nav) {
        if (nav == null) {
            CommonFragment com = new CommonFragment(CircleIndexActivity.this, nav);
            com.setType(genre);
            com.setKeyWord(keyword);
            com.setSrpid(srp_id);
            return com;
        }
        String category = nav.category();
        if (ConstantsUtils.FR_BAIKE.equals(category)) {// 小档案
            return new XiaoDanganFragment(CircleIndexActivity.this, nav);
        }
        if (ConstantsUtils.FR_QA.equals(category)) {
            QAFragment qaf = new QAFragment(CircleIndexActivity.this, nav, genre);
            qaf.setType(genre);
            qaf.setKeyWord(keyword);
            qaf.setSrpid(srp_id);
            return qaf;
        }
        if (ConstantsUtils.FR_WEIBO_SEARCH.equals(category)) {
            WeiboFragment weibo = new WeiboFragment(CircleIndexActivity.this, nav, genre);
            weibo.setType(genre);
            weibo.setKeyWord(keyword);
            weibo.setSrpid(srp_id);
            return weibo;
        }
        if (ConstantsUtils.FR_BLOG_SEARCH.equals(category) || ConstantsUtils.FR_INFO_PUB.equals(category)) {
            BlogFragment blogFragment = new BlogFragment(CircleIndexActivity.this, nav, genre);
            blogFragment.setType(genre);
            blogFragment.setKeyWord(keyword);
            blogFragment.setSrpid(srp_id);
            if (ConstantsUtils.FR_INFO_PUB.equals(category)) {
                blogFragmentList.add(blogFragment);
            }
            return blogFragment;
        }
        if (ConstantsUtils.FR_SRP_SELFCREATE.equals(category)) {    //原创微件
            SRPSelfCreateFragment srpsc = new SRPSelfCreateFragment(CircleIndexActivity.this, nav, genre);
            srpsc.setType(genre);
            srpsc.setKeyWord(keyword);
            srpsc.setSrpid(srp_id);
            return srpsc;
        }
        if (ConstantsUtils.FR_BBS_SEARCH.equals(category)) {
            ForumFragment forun = new ForumFragment(CircleIndexActivity.this, nav, genre);
            forun.setType(genre);
            forun.setKeyWord(keyword);
            forun.setSrpid(srp_id);
            return forun;
        }
        if (ConstantsUtils.FR_JHQ.equals(category)) {
            RecommendFragment recom = new RecommendFragment(CircleIndexActivity.this, nav, genre);
            recom.setType(genre);
            recom.setKeyWord(keyword);
            recom.setSrpid(srp_id);
            return recom;
        }
//        if (ConstantsUtils.FR_ENT.equals(category)) {
//            LongTengFragment longt = new LongTengFragment(CircleIndexActivity.this, nav);
//            longt.setType(genre);
//            longt.setKeyWord(keyword);
//            longt.setSrpid(srp_id);
//            return longt;
//        }
        if (ConstantsUtils.FR_IMG_SEARCH.equals(category)) {
            return new PhotoSearchFragment(CircleIndexActivity.this, nav);
        }
        if (ConstantsUtils.FR_SELF_CREATE.equals(category)) {    //原创分享大赛
            MySharesFragment myshare = new MySharesFragment(CircleIndexActivity.this, nav, genre);
            myshare.setType(genre);
            myshare.setKeyWord(keyword);
            myshare.setSrpid(srp_id);
            return myshare;
        }
        if (ConstantsUtils.FR_WEB.equals(category)) {
            KunlunJueFragment kun = new KunlunJueFragment(CircleIndexActivity.this, nav);
            kun.setType(genre);
            kun.setKeyWord(keyword);
            kun.setSrpid(srp_id);
            return kun;
        }
        if (ConstantsUtils.FR_WEB_SUB.equals(category)) {
            WebpageFragment web = new WebpageFragment(CircleIndexActivity.this, nav);
            web.setType(genre);
            web.setKeyWord(keyword);
            web.setSrpid(srp_id);
            return web;

        }
        if (ConstantsUtils.FR_CHAT_ROOM.equals(category)) {
            SearchResultItem item = new SearchResultItem();
            item.keyword_$eq(keyword);
            item.srpId_$eq(srp_id);
            item.url_$eq(nav.url());
            ChatRoomFragment chat = new ChatRoomFragment(item);
            chat.setType(genre);
            chat.setKeyWord(keyword);
            chat.setSrpid(srp_id);
            return chat;
        }
        if (ConstantsUtils.FR_INTEREST_GROUP.equals(category)) {   //精华区
            essencePostFragment = new EssencePostFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", interest_name);
            bundle.putString("srp_id", srp_id);
            bundle.putString("tag_id", nav.getTag_id());
            bundle.putLong("interest_id", nav.getInterest_id());
            bundle.putString("keyword", keyword);
            essencePostFragment.setArguments(bundle);
            essencePostFragment.setOnRoleChangeListener(new OnChangeListener() {
                @Override
                public void onChange(Object obj) {
                    role = Constant.ROLE_NORMAL;
                    menuImgBtn.setImageResource(R.drawable.title_bar_menu_selector);
                    CircleGetCircleMenuRequest.send(CircleIndexActivity.this, SYUserManager.getInstance().getToken(), srp_id);
//                    http.getCircleMenu(SYUserManager.getInstance().getToken(), srp_id);
                }
            });
            return essencePostFragment;
        }
        if (ConstantsUtils.FR_INTEREST_BAR.equals(category)) {    //圈吧
            circleBarFragment = new CircleBarFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("interest_id", nav.getInterest_id());
            bundle.putString("title", interest_name);
            bundle.putString("srp_id", srp_id);
            bundle.putString("keyword", keyword);
            bundle.putString("tag_id", nav.getTag_id());
            bundle.putString("nickName", nickName);
            bundle.putString("onlyjing", nav.getOnlyjing());
            circleBarFragment.setArguments(bundle);
            circleBarFragment.setOnRoleChangeListener(new OnChangeListener() {
                @Override
                public void onChange(Object obj) {
                    role = Constant.ROLE_NORMAL;
                    menuImgBtn.setImageResource(R.drawable.title_bar_menu_selector);
                    CircleGetCircleMenuRequest.send(CircleIndexActivity.this, SYUserManager.getInstance().getToken(), srp_id);
//                    http.getCircleMenu(SYUserManager.getInstance().getToken(), srp_id);
                }
            });
            return circleBarFragment;
        }
        CommonFragment com = new CommonFragment(CircleIndexActivity.this, nav, genre);
        com.setType(genre);
        com.setKeyWord(keyword);
        com.setSrpid(srp_id);
        return com;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circle_index_search_imgBtn:
//                IntentUtil.openSearchActivity(CircleIndexActivity.this);
//                showDialog(DialogPlus.Gravity.TOP);
//                JSClick jscClick = new JSClick();
//                String sKeyWord = StringUtils.enCodeRUL(keyword);
//                jscClick.setUrl(UrlConfig.S_CURRENT_PAGE + "?k=" + sKeyWord);
//                DialogHelper.getInstance().showDialog(CircleIndexActivity.this, DialogPlus.ScreenType.HALF, jscClick);

                IntentUtil.gotoSearchAll(this, getClass().getSimpleName());
                break;
            case R.id.circle_index_menu_imgBtn:
                if (role == -1) {
                    Toast.makeText(CircleIndexActivity.this, "服务器又贪玩了，您的个人信息还未返回，请稍等...", Toast.LENGTH_SHORT).show();
                }
                if (role > -1) {
                    User user = SYUserManager.getInstance().getUser();
                    if (user != null) {
                        if (circleIndexPopupMenu == null) {
                            Toast.makeText(CircleIndexActivity.this, "服务器又贪玩了，您的个人信息还未返回，请稍等...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        circleIndexPopupMenu.updateMenuItem(role);
                        int height = DeviceUtil.dip2px(CircleIndexActivity.this, 47) + Utils.getTitleBarHeight(CircleIndexActivity.this);
                        circleIndexPopupMenu.showTopDialog(menuImgBtn, height - getResources().getDimensionPixelOffset(R.dimen.space_5));
                        menuImgBtn.setImageResource(R.drawable.imgbtn_menu_selected);
                    } else {
                        IntentUtil.goLoginForResult(CircleIndexActivity.this, 900);
                    }
                }
                UmengStatisticUtil.onEvent(this, UmengStatisticEvent.CIRCLE_MENU_CLICK);    //Umeng
                break;

            case R.id.btn_detail_subscribe:
                subButton.setClickable(false);
                if (isSubered) {
                    CircleExitCircleRequest.send(HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID, CircleIndexActivity.this, interest_id, SYUserManager.getInstance().getToken(), ZSSdkUtil.CIRCLEINDEX_SUBSCRIBE_GROUP);
                } else {
                    if (menuInfo != null) {
                        loadSaveRecomentCircles(SYUserManager.getInstance().getToken(), menuInfo.getInterestId() + "");
                    }
                }
                break;

        }
    }

    /**
     * 加载数据-----订阅兴趣圈
     *
     * @param token
     * @param interest_id
     */
    private void loadSaveRecomentCircles(String token, String interest_id) {
        InterestSubscriberReq req = new InterestSubscriberReq(HttpCommon.CIRLCE_INTEREST_SUB_ID, this);
        req.setParams(token, interest_id, ZSSdkUtil.CIRCLEINDEX_SUBSCRIBE_GROUP);
        CMainHttp.getInstance().doRequest(req);

    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        if (methodName != null && "saveRecomentCircles".equals(methodName)) {
//            if (status.getCode() != 200) {
//                UIHelper.ToastMessage(this,
//                        R.string.cricle_manage_save_circle_failed);
//            }
////        } else if ("getMemberRole".equals(methodName)) {
////            return;
//        } else {
//            progress.showNetError();
//        }
//    }

    class MyAdapter extends FragmentPagerAdapter {

        private List<SRPFragment> list;

        private List<String> titles;


        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return list.get(i);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            if(position > 3){
//                super.destroyItem(container, position, object);
//            }
//        }

        public List<SRPFragment> getList() {
            return list;
        }

        public void setList(List<SRPFragment> list) {
            this.list = list;
        }

        public List<String> getTitles() {
            return titles;
        }

        public void setTitles(List<String> titles) {
            this.titles = titles;
        }
    }

    private void isLoadData() {
        SRPFragment currentFragment = getCurrentFragment();
        if (!currentFragment.hasDatas && !(currentFragment instanceof MySharesFragment) && !(currentFragment instanceof ChatRoomFragment))
            currentFragment.loadData();
    }


    public SRPFragment getCurrentFragment() {
        SRPFragment item = null;
        try {
            item = (SRPFragment) myAdapter.getItem(viewPager.getCurrentItem());
        } catch (Exception ex) {
        }
        return item;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (menuInfo == null) {
        } else if (menuInfo.getAtCount() + menuInfo.getFollowMyCount() > 0 && role > 0  //必须要是圈成员
                && !(SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST))) {  //判断当不是游客时
            redPointImg.setVisibility(View.VISIBLE);
        } else {
            redPointImg.setVisibility(View.GONE);
        }
        if (interest_id != 0) {
            getRole();
            CircleGetCircleMenuRequest.send(this, SYUserManager.getInstance().getToken(), srp_id);
        }


    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        onBackPressClick(null);
    }

    public void onBackPressClick(View view) {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!ConfigApi.isSouyue() && srpFragments.size() == 0) {
            return;
        }

        for (Fragment fragment : srpFragments) {
            if (fragment != null) {
                if (fragment instanceof EssencePostFragment || fragment instanceof CircleBarFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_MEMBER_SETTING_ACTIVITY && data != null) {
                boolean isQuit = data.getBooleanExtra("isQuit", false);
                int interestType = data.getIntExtra("interestType", 0);
                if (isQuit && interestType == Constant.INTEREST_TYPE_PRIVATE) {
                    finish();
                }
                if (isQuit) {
                    //role = 0;
                    role = Constant.ROLE_NONE;
                    updateSubAdapterRole();
                    //menuImgBtn.setImageResource(R.drawable.srp_subcribe);
                    // updateMenu();
                    redPointImg.setVisibility(View.GONE);
                }
            }

            if (requestCode == REQUEST_CODE_POST_DETAIL_ACTIVITY && data != null) {
                //boolean isSubscribeSuccess = data.getBooleanExtra("isSubscribeSuccess", false);
                boolean isUpdateSuccess = data.getBooleanExtra("isUpdateSuccess", false);
                boolean isLogin = data.getBooleanExtra("isLogin", false);
                boolean isQuite = data.getBooleanExtra("isQuite", false);
                if (isQuite) {
                    if (is_private) {
                        finish();
                    } else {
                        menuImgBtn.setImageResource(R.drawable.title_bar_menu_selector);
                        //  getRole();
                        // CircleGetCircleMenuRequest.send(this, SYUserManager.getInstance().getToken(), srp_id);
                    }

                    return;
                }
                if (isUpdateSuccess || isLogin) {
//                    role = 2;
                    menuImgBtn.setImageResource(R.drawable.title_bar_menu_selector);
//                    updateSubAdapterRole();
                    //  getRole();
                    //CircleGetCircleMenuRequest.send(this, SYUserManager.getInstance().getToken(), srp_id);
//                    http.getCircleMenu(SYUserManager.getInstance().getToken(), srp_id);
                }

            }
            if (requestCode == REQUEST_CODE_LOGIN_ACTIVITY) {
                //  CircleGetCircleMenuRequest.send(this, SYUserManager.getInstance().getToken(), srp_id);
//                http.getCircleMenu(SYUserManager.getInstance().getToken(), srp_id);
                //getRole();
                role = -1;      //
            }

            if (requestCode == KunlunJueFragment.FILECHOOSER_RESULTCODE) {
                SRPFragment mFragment = getCurrentFragment();
                if (mFragment instanceof KunlunJueFragment) {
                    if (resultCode == -1 && data != null) {
                        mFragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        }
    }


    /**
     * 创建桌面快捷方式，有些手机会创建桌面小工具，此处正是判断是否来自桌面小工具的
     * 属于非常特殊的情况，不必过于纠结
     *
     * @return
     */
    private boolean isFromAppWidget() {
        return StringUtils.isEmpty(keyword) && StringUtils.isEmpty(srp_id) && StringUtils.isEmpty(interest_name) && StringUtils.isEmpty(from);
    }

    /**
     * 非核心业务，特殊情况，不必过于纠结
     */
    private void doFromAppWidget() {
        if (StringUtils.isNotEmpty(SYUserManager.getInstance().getToken())) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        } else {
            Intent i = new Intent();
            i.setClass(CircleIndexActivity.this, FirstInActivity.class);
            startActivity(i);
        }
        finish();
    }

    /**
     * 来自桌面快捷方式的处理
     * 非核心业务，特殊情况，不必过于纠结
     *
     * @return
     */
    private boolean doFromShortcut() {
        if (StringUtils.isNotEmpty(SYUserManager.getInstance().getToken())) {
            if (!MainApplication.getInstance().isRunning()) {
                Intent mainIntent = new Intent(this, CommonStringsApi.getHomeClass());
                mainIntent.putExtra("from", ShortCutInfo.FROM_SHORTCUT);
                ShortCutInfo info = new ShortCutInfo();
                info.setSrpId(srp_id);
                info.setKeyword(keyword);
                info.setInterest_name(interest_name);
                info.setInterest_logo(""); // 打开兴趣圈需要的参数，目前interest_logo不需要了，@tiansj
                info.setGoTo(ShortCutInfo.GO_TO_INTEREST);
                mainIntent.putExtra(MainActivity.SHORTCUT_EXTRA, info);
                startActivity(mainIntent);
                finish();
                return true;
            }
        } else { //极其特殊的情况，即当用户清空所有搜悦数据，包括用户信息后，点击快捷方式直接走引导页
            Intent i = new Intent();
            i.setClass(CircleIndexActivity.this, FirstInActivity.class);
            startActivity(i);
            return true;
        }
        return false;
    }

    private void setIsBanTalk(){
        if(essencePostFragment != null){
            essencePostFragment.setIsBanTalk(is_bantalk);
        }
        if(circleBarFragment != null){
            circleBarFragment.setIsBanTalk(is_bantalk);
        }
    }
}