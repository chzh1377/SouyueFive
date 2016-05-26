package com.zhongsou.souyue.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.SuberRecomendAdapter;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.fragment.SubscribeAllListFragment;
import com.zhongsou.souyue.circle.util.JSONUtils;
import com.zhongsou.souyue.circle.view.InterestDialog;
import com.zhongsou.souyue.circle.view.TipsDialog;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.db.DaoFactory;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.fragment.ManagerGridFragment;
import com.zhongsou.souyue.fragment.MineFragment;
import com.zhongsou.souyue.fragment.SouyueTabFragment;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.sub.SubSwitchGetRequest;
import com.zhongsou.souyue.net.sub.SubSwitchPostRequest;
import com.zhongsou.souyue.net.sub.SubUpdateRequest;
import com.zhongsou.souyue.net.sub.SuberRecomendRequest;
import com.zhongsou.souyue.net.sub.SuberSearchRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.highlight.Highlight;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangzhy@zhongsou.com 我的订阅首界面
 */
public class MySubscribeListActivity extends RightSwipeActivity implements
        OnClickListener, OnTouchListener, ManagerGridFragment.EditEnable, TextWatcher {
    public static final String INTENT_INDEX = "nav_index";
    protected ManagerGridFragment gridInstance;
    protected Fragment currentFragment;

    private TextView tvHotRecommend;
    private PullToRefreshListView mPullGridView;
    private SuberRecomendAdapter mRecommendAdapter;
    private List<SuberedItemInfo> infos;
    private ImageView ivDelete;
    private View shadow;
    private View goBack;
    private SuberedItemInfo item = null;
    private ImageButton btnOption;
    private TipsDialog tipsDialog;
    private int yoffset = 0;
    private boolean isPre;
    private CheckBox cbStatus;
    private static final int INVALID = -1;
    private String token = SYUserManager.getInstance().getToken();
    private boolean isClicked = true;
    private SuberDao dao;
    private View popupWindow;   //热门推荐popupWindow
    private Animation inputAnimation, outAnimation;
    private CharSequence keyWords;
    private EditText etSerach;
    public static final String SUBER_ADD = "add";
    public static final String SUBER_DEL = "del";
    private String type = SUBER_DEL;
    private InterestDialog suberDialg;
    private InputMethodManager im;
    private static final String INTEST = "interest";
    private static final String SRP = "srp";
    private RadioGroup radioGroup;
    private View createGroup;
    private Highlight highlight;
    private View dot;

    public static final String FLAG_SOURCE_HOME = "FLAG_SOURCE_HOME"; //判断是否来自首页球球点击
    private SubscribeAllListFragment subscribeAllListFragment;
    private boolean isOnEdit = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.subscribe_merge_ac);
        initView();
        setLayouListener();
        setData();
        initFragment();

        if (isVisitSubFirst()) {
            showCreateGroupGuide();
        }
        setUpdateReceiver();
    }

    private void initFragment() {
        Intent intent = getIntent();
        boolean flagSourceHome = false;
        addFragment();
        if (intent != null) {
            flagSourceHome = intent.getBooleanExtra(FLAG_SOURCE_HOME, false);
        }

        if (flagSourceHome) {
            radioGroup.check(R.id.sub_add_tab);
        } else {
            radioGroup.check(R.id.sub_list_tab);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {  //不保存fragment状态 默认会保存
        super.onSaveInstanceState(outState);
    }

    public void setCurrentDisplayFragment(ManagerGridFragment currentDisplayFragment) {
        this.gridInstance = currentDisplayFragment;
    }

    private void initView() {
        goBack = this.findViewById(R.id.goBack);
        shadow = this.findViewById(R.id.shadow);
        tvHotRecommend = (TextView) findViewById(R.id.tv_hotrecommed);  //热门推荐
        btnOption = (ImageButton) this.findViewById(R.id.btn_option);
        radioGroup = (RadioGroup) findViewById(R.id.sub_title_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.sub_list_tab: //我的订阅
                        findViewById(R.id.my_sub_foot).setVisibility(View.VISIBLE);
                        showFragment(gridInstance, subscribeAllListFragment);
                        break;
                    case R.id.sub_add_tab:  //添加订阅 - 类似原来订阅大全的逻辑
                        tvHotRecommend.setSelected(false);
                        exitToSave(false);
                        findViewById(R.id.my_sub_foot).setVisibility(View.GONE);
                        showFragment(subscribeAllListFragment, gridInstance);
                        performAnimaitionOut();
                        UpEventAgent.onSuberAll(MySubscribeListActivity.this);
                        UmengStatisticUtil.onEvent(MySubscribeListActivity.this, UmengStatisticEvent.SUBSCRIBE_ALL_CLICK);    //Umeng

                        break;
                }
            }
        });
        dot = this.findViewById(R.id.sub_clicked);
        isClicked = sysp.hasClicked();
        if (isClicked) {
            dot.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.sub_fragment_container,
                subscribeAllListFragment,
                getResources().getString(R.string.add_sub_tab));
        transaction.add(R.id.sub_fragment_container,
                gridInstance,
                getResources().getString(R.string.my_sub_tab));
        transaction.commit();
    }

    private void showFragment(Fragment showFragment, Fragment hideFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(hideFragment);
        transaction.show(showFragment);
        transaction.commit();
        currentFragment = showFragment;
    }


    @SuppressLint("NewApi")
    private void setLayouListener() {
        tvHotRecommend.setOnClickListener(this);
//        rlRight.setOnClickListener(this);
        goBack.setOnClickListener(this);
        shadow.setOnTouchListener(this);
        this.findView(R.id.layout_title).setOnTouchListener(this);
        //indicator.setOnTabReselectedListener(this);
        btnOption.setOnClickListener(this);
//        btnSave.setOnClickListener(this);
//        pager.setOnBeginListener(new ViewPagerWithTips.OnBeginListener() {
//            @Override
//            public void onBeginListener() {
//                onBackPressClick(null);
//            }
//        });
//        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//                switch (i) {
//                    case 0:
//                        radioGroup.check(R.id.sub_list_tab);
//                        findViewById(R.id.my_sub_foot).setVisibility(View.VISIBLE);
//                        break;
//                    case 1:
//                        radioGroup.check(R.id.sub_add_tab);
//                        findViewById(R.id.my_sub_foot).setVisibility(View.GONE);
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });
    }

    private void setData() {
        gridInstance = ManagerGridFragment.newInstance(null);
        subscribeAllListFragment = new SubscribeAllListFragment();

        token = SYUserManager.getInstance().getToken();
        dao = DaoFactory.createDao();
        suberDialg = new InterestDialog(this);
        isPre = sysp.getPosition();

        SubSwitchGetRequest subGetReq = new SubSwitchGetRequest(HttpCommon.SUB_SWITCH_GET_REQUEST, this);
        subGetReq.setParams(token);
        mMainHttp.doRequest(subGetReq);

        im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        List<Fragment> list = new ArrayList<>();
        list.add(ManagerGridFragment.newInstance(null));
        list.add(new SubscribeAllListFragment());
        doConversionIndex();
    }

    private void doConversionIndex() {
        doSetCurrentItem(0);
    }

    private void doSetCurrentItem(int index) {
//        if (pager != null) {
//            pager.setCurrentItem(index);
//            this.index = index;
//        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            yoffset = this.findViewById(R.id.layout_title).getHeight();
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        im.hideSoftInputFromWindow(popupWindow.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (popupWindow != null) {
            popupWindow.clearAnimation();
        }
        if (shadow != null) {
            shadow.setVisibility(View.GONE);
        }
        if (sysp.getBoolean(SYSharedPreferences.KEY_REGISTERSUCCESS, false)
                || sysp.getBoolean(SYSharedPreferences.KEY_UPDATE, false)
                || sysp.getBoolean(SYSharedPreferences.KEY_USER_UPDATE, false)) {

            sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
            sysp.remove(SYSharedPreferences.KEY_REGISTERSUCCESS);
            sysp.remove(SYSharedPreferences.KEY_UPDATE);
            if (gridInstance != null) {
                gridInstance.doInitData();    //访问网络
            }
        }
        if (tipsDialog != null && tipsDialog.isShowing()) {
            tipsDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.rl_right: // 订阅大全
//                tvHotRecommend.setSelected(false);
//                currentDisplayFragment.exit2Save(false);
//                doStartSubActivity(SubscribeListActivity.class);
//                UpEventAgent.onSuberAll(this);
//                UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_ALL_CLICK);    //Umeng
//                break;

            case R.id.tv_hotrecommed: // 热门推荐
                if (popupWindow != null && popupWindow.isShown()) {
                    performAnimaitionOut();
                    tvHotRecommend.setSelected(false);
                } else {
                    tvHotRecommend.setSelected(true);
                    showPopupWindow();
                    exitToSave(false);
                }
                UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_HOT_CLICK);    //Umeng
                break;

            case R.id.goBack:
                exitToSave(true);
                break;

            case R.id.iv_delete:
                etSerach.getText().clear();
                break;
            case R.id.et_seach: //热门推荐部分中搜索框
                IntentUtil.gotoSearchAll(this, "hotrecommed");
                break;

            case R.id.btn_option:   //右上角菜单
                UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_MENU_CLICK);
                sysp.putClicked(true);
                this.findViewById(R.id.sub_clicked).setVisibility(View.GONE);   //红点
                showTips();
                break;
//            case R.id.btn_save:
//                currentDisplayFragment.exit2Save(false);
//                //我的订阅保存按钮统计
//                UpEventAgent.onZSSubscribepageSave(this);
//                break;

            default:
                break;
        }
    }

    private boolean exitToSave(boolean flag) {
        boolean result = false;
        if (currentFragment != null && currentFragment instanceof ManagerGridFragment) {
            result = gridInstance.exit2Save(flag);
        } else if (flag) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
        return result;
    }

    /**
     * 显示右上角菜单
     */
    private void showTips() {
        if (tipsDialog == null) {
            tipsDialog = new TipsDialog(this,
                    getResources().getDimensionPixelOffset(R.dimen.space_15),
                    yoffset - getResources().getDimensionPixelOffset(R.dimen.space_7),
                    INVALID, INVALID,
                    R.layout.dialog_sub_tips,
                    R.style.dialog_style,
                    Gravity.RIGHT, Gravity.TOP);
            cbStatus = (CheckBox) tipsDialog.findViewById(R.id.cb_status);
            cbStatus.setChecked(isPre);
            tipsDialog.findViewById(R.id.sub_root).setOnClickListener(new OnClickListener() {   //新订阅在前条目
                @Override
                public void onClick(View v) {
                    cbStatus.performClick();
                }
            });
            createGroup = tipsDialog.findViewById(R.id.create_group);
            createGroup.setOnClickListener(new OnClickListener() {   //创建分组条目
                @Override
                public void onClick(View v) {
                    UpEventAgent.onZSGroupCreate(mContext);
                    IntentUtil.gotoSubGroupEdit(mContext, "", "", 1, "");
                }
            });
            cbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isPre = isChecked;
                    sysp.putPosition(isPre);
                    String subSwitch = isChecked ? "0" : "1";
                    SubSwitchPostRequest request = new SubSwitchPostRequest(HttpCommon.SUB_SWITCH_POST_REQUEST, MySubscribeListActivity.this);
                    request.setParams(token, subSwitch);
                    mMainHttp.doRequest(request);
                }
            });
        }

        if (tipsDialog.isShowing()) {
            tipsDialog.dismiss();
        } else {
            tipsDialog.show();
        }
    }

    /**
     * 热门推荐弹窗
     */
    public void showPopupWindow() {
        popupWindow = this.findViewById(R.id.popupwindow);
        initGridView(popupWindow);// 初始化gridview
        performAnimationIn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (popupWindow != null) {
            popupWindow.setVisibility(View.GONE);
            tvHotRecommend.setSelected(false);
        }
        if (shadow != null) {
            shadow.setVisibility(View.GONE);
        }
    }

    /**
     * 热门订阅popupWindow的打开动画
     */
    private void performAnimationIn() {
        etSerach.setText("");
        etSerach.removeTextChangedListener(null);
        shadow.setVisibility(View.VISIBLE);

        popupWindow.setVisibility(View.VISIBLE);
        inputAnimation = AnimationUtils.loadAnimation(this, R.anim.suberlist_inputodown);
        popupWindow.startAnimation(inputAnimation);
        inputAnimation.setFillAfter(true);
    }

    /**
     * 热门订阅popupWindow的退出动画
     */
    private void performAnimaitionOut() {
        if (etSerach != null)
            etSerach.removeTextChangedListener(this);

        if (popupWindow != null && popupWindow.isShown()) {
            outAnimation = AnimationUtils.loadAnimation(this, R.anim.suberlist_outdowntoup);
            popupWindow.setVisibility(View.GONE);
            tvHotRecommend.setSelected(false);
            popupWindow.startAnimation(outAnimation);
            shadow.setVisibility(View.GONE);
            hideKeyboard();
        }
    }

    private void initGridView(View view) {
        mPullGridView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_grid);
        etSerach = (EditText) view.findViewById(R.id.et_seach);
        ivDelete = (ImageView) view.findViewById(R.id.iv_delete);

        ivDelete.setOnClickListener(this);
        etSerach.addTextChangedListener(this);
        etSerach.setOnClickListener(this);
        mPullGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        mPullGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                mRecommendAdapter.clickBtnOther(position - 1);
            }
        });
    }

    @Override
    public void onHttpStart(IRequest _request) {
        super.onHttpStart(_request);
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        super.onHttpResponse(_request);
        HttpJsonResponse response = (HttpJsonResponse) _request.getResponse();

        switch (_request.getmId()) {

            case HttpCommon.SUBER_UPDATE_METHOD: // 订阅
                long id = 0;
                try {
                    onStatistics();
                    id = response.getBody().getAsJsonArray("id").get(0).getAsLong();
                } catch (Exception e) {
                    id = response.getBody().get("interest_id").getAsLong();
                } finally {
                    sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
                    if (type.equals(SUBER_ADD)) {
                        suberDialg.subscribe();
                        UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_ADD_HOT_CLICK);
                    } else {
                        suberDialg.unsubscribe();
                    }
                    mRecommendAdapter.updateStatus(item);
                    if (type.equals(SUBER_ADD)) {
                        item.setId(id);
                    }
                    if ("0".equals(item.getStatus())) {
                        if (gridInstance != null) {
                            gridInstance.addNotify(item, isPre);
                            dao.addOne(item);
                            sysp.setSuberSrpId(item.getSrpId());//保存id到sysp
                        }
                    } else {
                        if (gridInstance != null) {
                            gridInstance.removeItem(item);
                            dao.clearOne(item);
                        }
                    }
                }

                break;

            case HttpCommon.SUBER_REMCOMMEND_METHOD: // 热门推荐
                infos = JSONUtils.fromJsonArray(
                        response.getBodyArray(),
                        new TypeToken<List<SuberedItemInfo>>() {}.getType());
                doNotify(infos);
                break;

            case HttpCommon.SUBER_SERACH_METHOD: // 搜索
                infos = JSONUtils.fromJsonArray(response.getBodyArray(), new TypeToken<List<SuberedItemInfo>>() {
                }.getType());
                doNotify(infos);
                break;

            case HttpCommon.SUB_SWITCH_GET_REQUEST: //订阅开关
                String result = response.getBody().get("subSwitch").getAsString();
                isPre = "0".equals(result);
                //保存
                sysp.putPosition(isPre);
                // cbStatus.setChecked(isPre);
                break;

            case HttpCommon.SUB_SWITCH_POST_REQUEST:

                break;

            default:
                break;
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        super.onHttpError(_request);
        IHttpError error = _request.getVolleyError();
        HttpJsonResponse response = null;

        if (error instanceof CSouyueHttpError) {
            response = error.getJson();

            switch (_request.getmId()) {
                case HttpCommon.SUBER_UPDATE_METHOD: // 订阅
                    if (response != null) {
                        int status = response.getHead().get("status").getAsInt();
                        if (status == 600) {
                            suberDialg.dimissRightNow();
                            Toast toast = Toast.makeText(getApplicationContext(), "此栏目已订阅", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else if (status == 700) {
                            suberDialg.dimissRightNow();
                        } else {
                            suberDialg.subscribefail();
                        }
                    } else {
                        suberDialg.subscribefail();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 热门推荐统计功能
     */
    private void onStatistics() {
        if (item != null) {
            if (INTEST.equals(item.getCategory())) {
                if (SUBER_ADD.equals(type)) {
                    UpEventAgent.onGroupJoin(this, item.getId() + "." + "", "");
                } else if (SUBER_DEL.equals(type)) {
                    UpEventAgent.onGroupQuit(this, item.getId() + "." + "", "");
                }
            } else if (SRP.equals(item.getCategory())) {
                if (SUBER_ADD.equals(type)) {
                    UpEventAgent.onSrpSubscribe(MainApplication.getInstance(),
                            item.getKeyword(), item.getSrpId());
                } else if (SUBER_DEL.equals(type)) {
                    UpEventAgent.onSrpUnsubscribe(this, item.getKeyword(), item.getSrpId());
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && popupWindow != null && popupWindow.isShown()) {
            performAnimaitionOut();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取热门推荐
     */
    private void getHotRecommendData() {
        String vc = DeviceInfo.getAppVersion();
        String imei = DeviceInfo.getDeviceId();
//        mVolleyHttp.doSuberRecomend(CSuberListHttp.SUBER_REMCOMMEND_METHOD, token, vc, imei, MySubscribeListActivity.this);
        SuberRecomendRequest.send(HttpCommon.SUBER_REMCOMMEND_METHOD, token, vc, imei, MySubscribeListActivity.this);
    }

    private void doNotify(List<SuberedItemInfo> infoList) {
        mRecommendAdapter = new SuberRecomendAdapter(this, infoList);
        mRecommendAdapter.setmClickBtnAdd(new SuberRecomendAdapter.ClickBtnAdd() {
            @Override
            public void clickBtnAdd(int position) {
//                 item = (SuberedItemInfo) mPullGridView.getList.getItemAtPosition(position);
                item = infos.get(position);
                type = SUBER_ADD;
                if ("0".equals(item.getStatus())) {
                    type = SUBER_DEL;
                }
                if ("1".equals(item.getType())) { // 私密圈
                    IntentUtil.gotoSecretCricleCard(MySubscribeListActivity.this, item.getId(), 2);
                    return;
                }
                suberDialg.show();
                suberDialg.progress();
                SubUpdateRequest.send(HttpCommon.SUBER_UPDATE_METHOD, token, DeviceInfo.getDeviceId(), item.getSrpId(), type, item
                        .getCategory(), item.getId() + "", item.getKeyword(), MySubscribeListActivity.this);
            }
        });
        mPullGridView.setAdapter(mRecommendAdapter);
        mRecommendAdapter.notifyDataSetChanged();
    }

    /**
     * 订阅
     * 订阅大全
     *
     * @param clazz
     */
    private void doStartSubActivity(Class<?> clazz) {
//        int pageIndex = pager.getCurrentItem();
//        Intent intent = new Intent();
//        intent.putExtra(SubscribeListActivity.INTENT_INDEX,
//                getPageTitleByPos(pageIndex));
//        intent.setClass(this, clazz);
//        startActivity(intent);
//        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                sysp.putBoolean(SYSharedPreferences.KEY_UPDATE, true);
                break;
            default:
                break;
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> list;

        private static final int numItems = 1;

        public PagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(getPageTitleByPos(position));
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }


    private int getPageTitleByPos(int position) {
        switch (position) {
            case 0:
                return R.string.manager_grid_insterest;
            case 1:
                return R.string.manager_grid_subject;
            case 2:
                return R.string.manager_grid_rss;
        }
        return R.string.manager_grid_subject;
    }

    @Override
    public void onBackPressed() {
        if (!exitToSave(false)) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        performAnimaitionOut();
        return true;
    }

    @Override
    public void setEditEnable(boolean _isEdit) {    //TODO 不知道用处
//        pager.setmIsInEdit(_isEdit);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {
        keyWords = s;
        UpEventAgent.onZSSubSearch(MySubscribeListActivity.this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (keyWords.length() == 0) {
            ivDelete.setVisibility(View.GONE);
            getHotRecommendData();
        } else {
            ivDelete.setVisibility(View.VISIBLE);
//            mVolleyHttp.doSuberSearch(CSuberListHttp.SUBER_SERACH_METHOD,
//                    token,
//                    keyWords.toString(),
//                    MySubscribeListActivity.this);
            SuberSearchRequest request = new SuberSearchRequest(HttpCommon.SUBER_SERACH_METHOD, this);
            request.setParams(token, keyWords.toString());
            CMainHttp.getInstance().doRequest(request);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mVolleyHttp.cancelAll();
        cancelReceiver();
    }

    /**
     * 设置创建分组新特性引导
     */
    private void showCreateGroupGuide() {

        btnOption.post(new Runnable() {
            @Override
            public void run() {
                highlight = new Highlight(MySubscribeListActivity.this)
                        .shadow(false)
                        .maskColor(MySubscribeListActivity.this.getResources().getColor(R.color.transparent_75))
                        .addHighlight(dot, R.layout.tip_sub_create,
                                new Highlight.OnPosCallback() {
                                    @Override
                                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, Highlight.MarginInfo marginInfo) {
                                        marginInfo.topMargin = DeviceUtil.dip2px(MySubscribeListActivity.this, 43);
                                        marginInfo.leftMargin = DeviceInfo.getScreenWidth() - DeviceUtil.dip2px(MySubscribeListActivity.this, 290);
                                    }
                                });
                highlight.show();
            }
        });
    }

    /**
     * 判断是否第一次进入我的订阅
     *
     * @return true ： 是；false ：否
     */
    private boolean isVisitSubFirst() {
        boolean isFirst;
        isFirst = CommSharePreference.getInstance().getValue(CommSharePreference.DEFAULT_USER, "isVisitSubFirst", true);
        CommSharePreference.getInstance().putValue(CommSharePreference.DEFAULT_USER, "isVisitSubFirst", false);    //存储常量
        return isFirst;
    }

    UpdateBroadCastReceiver receiver;

    /**
     * 注册用于刷新 组数据的广播
     */
    private void setUpdateReceiver() {
        IntentFilter inf = new IntentFilter();
        inf.addAction(MineFragment.logoutAction);
        inf.addAction(SouyueTabFragment.REFRESH_HOMEGROUP_DATA);
        receiver = new UpdateBroadCastReceiver();
        mContext.registerReceiver(receiver, inf);
    }

    private void cancelReceiver() {
        try {
            if (receiver != null) {
                mContext.unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class UpdateBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SouyueTabFragment.REFRESH_HOMEGROUP_DATA)) {
                if (gridInstance != null) {
                    gridInstance.dealWithNet();
                }
            }
        }
    }

    public void setOnEdit(boolean onEdit) {
        isOnEdit = onEdit;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!isOnEdit
                && e2.getX() - e1.getX() > DeviceInfo.getScreenWidth() / 3
                && Math.abs(velocityX) > 0) {
            exitToSave(true);
        }
        return false;
    }
}