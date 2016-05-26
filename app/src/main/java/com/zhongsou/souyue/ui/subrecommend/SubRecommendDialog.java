package com.zhongsou.souyue.ui.subrecommend;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.adapter.SubRecommendDlgPagerAdapter;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.fragment.SouyueTabFragment;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.RecommendSubModel;
import com.zhongsou.souyue.module.RecommendSubTab;
import com.zhongsou.souyue.module.RecommendTabSubListItem;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.sub.BatchSubAndDelRequest;
import com.zhongsou.souyue.net.sub.SubRecommendDlgListRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.receiver.TaskCenterReceiver;
import com.zhongsou.souyue.ui.highlight.Highlight;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ToastUtil;
import com.zhongsou.souyue.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by zyw on 2015/12/15.
 * 弹窗订阅 add 5.1.0
 */
public class SubRecommendDialog extends Dialog implements View.OnClickListener, CheckBoxWithMask.OnCheckedStateChangeListener, IVolleyResponse {
    private final boolean                     isFromIM; // 是否来自im
    private       Long                        mListId; //请求参数
    private       Long                        mIsPre; //请求参数
    private       Context                     mContext; // 上下文
    private       LinearLayout                topTab; // 顶部的tab
    private       Button                      btnCalcel; // 取消按钮
    private       Button                      btnOk; // ok 按钮
    private       ViewPager                   mainViewPager; // 显示list的viewpager
    private       SubRecommendDlgPagerAdapter mPagerAdapter; // pager的Adapter
    private       TextView                    tvTitle; // 顶部的文本
    private ArrayList<CheckBoxWithMask> mCheckBoxes = new ArrayList<CheckBoxWithMask>();
    private String mCurrentTopBg; // 当前顶部背景图
    private HashSet<RecommendTabSubListItem> requestItem = new HashSet<RecommendTabSubListItem>(); // 请求要用的list

    private static Queue<DialogData> diaLogQueue = new ArrayBlockingQueue<DialogData>(1); // 本地弹窗栈,只保存一个
    private static boolean isShowIng; // 弹窗是否正在上层

    private boolean mLocalDataSuccess; // 准备本地数据成功
    private boolean mRequestSuccess; // 网络请求成功

    public static final String ACTION_ADD                    = SubRecommendDialog.class.getSimpleName().concat("_add"); //添加项目
    public static final String ACTION_REMOVE                 = SubRecommendDialog.class.getSimpleName().concat("_remove"); // 删除项目
    public static final String ACTION_SUBSUCCESS             = SubRecommendDialog.class.getSimpleName().concat("_subsuccess");//订阅成功
    public static final String ACTION_MAKEREQUESTDATASUCCESS = SubRecommendDialog.class.getSimpleName().concat("_datasuccess");//准备本地数据成功
    public static final String ACTION_REMOVE_LIST_MASK       = SubRecommendDialog.class.getSimpleName().concat("_removemask");//移除遮罩
    public static final String ACTION_SHOW_LIST_MASK         = SubRecommendDialog.class.getSimpleName().concat("_showmask");//显示遮罩


    private        LocalBroadcastManager                     mLocalBroadcastManager; // 本地广播，用户数据传递
    private        MyLocalReceiver                           mLocalReceiver; // 本地广播接收器
    private        ImageView                                 ivTopBg; // 顶部背景布局
    private        RelativeLayout                            mainContentLayout; // 主界面
    private        RelativeLayout                            loadingLayout; // loading界界面
    private        boolean                                   mIsFirst;// 是否是第一次进入搜悦
    private static WeakReference<SubRecommendDialog>         subRecommendDialogWeakReference; // 当前运的实例

    private SubRecommendDialog(Context context, boolean first, Long isPre, Long listId, boolean isFromIM) {
        super(context, R.style.dialog_alert);
        Log.e("dialog", "onCreate");
        mContext = context;
        this.mIsFirst = first;
        this.mIsPre = isPre;
        this.mListId = listId;
        this.isFromIM = isFromIM;
    }


    /**
     * 新增一个弹出方法，指明是否来自im
     *
     * @param context  -> 上下文
     * @param isFirst  -> 是否第一次启动
     * @param isPre    -> 要给xuchong的参数
     * @param listId   -> 要给xuchong的参数2
     * @param isFromIM ->是否来自im
     */
    public static void showDialog(Context context, boolean isFirst, Long isPre, Long listId, boolean isFromIM) {
        SubRecommendDialog mInstance = null;
        if (!isFromIM) {
            //如果不是来自im,则检测时间
            //检测距离上次弹出时间，如果超过五分钟，就弹出来
            if (Utils.checkOverTime(SYSharedPreferences.KEY_SHOW_TIMESTAMP_SUBRECOMMEND, TaskCenterReceiver.DIALOG_TIME_STEP)) {
                if (isShowIng) {
                    //如果正在显示，就直接干掉
                    return;
                }
                //显示缓存的dlg
                if (!(context instanceof Activity))
                    return;
                if (isPre == null || listId == null) {
                    if (!diaLogQueue.isEmpty()) {
                        DialogData poll = diaLogQueue.poll();
                        mInstance = new SubRecommendDialog(context, poll.isFirst(), poll.getIsPre(), poll.getListId(), isFromIM);
                        mInstance.show();
                        subRecommendDialogWeakReference = new WeakReference<SubRecommendDialog>(mInstance);
                    }
                    return;
                }
                //显示最新数据
                mInstance = new SubRecommendDialog(context, isFirst, isPre, listId, isFromIM);
                mInstance.show();
                subRecommendDialogWeakReference = new WeakReference<SubRecommendDialog>(mInstance);
            }
        } else {
            //如果是来自im，则直接弹出
            mInstance = new SubRecommendDialog(context, isFirst, isPre, listId, isFromIM);
            mInstance.show();
            subRecommendDialogWeakReference = new WeakReference<SubRecommendDialog>(mInstance);
        }
    }

    /**
     * IM 专用的弹出方法
     * ispre -> 是否是预览？
     * listid -> 参数？
     *
     * @param context
     * @param isPre
     * @param listId
     */
    public static void showDialog(Context context, boolean isFirst, Long isPre, Long listId) {
        showDialog(context, isFirst, isPre, listId, true);
    }

    public static void reShowDialog() {
        try {
            Log.e("subrecommenddialog", "引导完毕，显示自身");
            subRecommendDialogWeakReference.get().initView();
            subRecommendDialogWeakReference.get().requestData();
            subRecommendDialogWeakReference.get().getWindow().getDecorView().setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }
    }


    public static SubRecommendDialog getInstance() {
        return subRecommendDialogWeakReference.get();
    }

    /**
     * 添加到队列中(1条)
     *
     * @param isFirst
     * @param isPre
     * @param listId
     */
    public static void addToQueue(boolean isFirst, Long isPre, Long listId) {
        if (!diaLogQueue.isEmpty()) {
            diaLogQueue.clear();
        }
        diaLogQueue.offer(new DialogData(isFirst, isPre, listId));
    }

    /**
     * 是否正在显示
     *
     * @return
     */
    public static boolean getIsShowingMe() {
        return isShowIng;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            setContentView(R.layout.subrecommenddialog_layout);
        } catch (Exception e) {
            try {
                dismiss();
            } catch (Exception ex) {

            }
        }
        if (!isFromIM) {
            getWindow().getDecorView().setVisibility(View.GONE);
        }
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ADD);
        intentFilter.addAction(ACTION_REMOVE);
        intentFilter.addAction(ACTION_SUBSUCCESS);
        intentFilter.addAction(ACTION_MAKEREQUESTDATASUCCESS);
        intentFilter.addAction(ACTION_REMOVE_LIST_MASK);
        intentFilter.addAction(ACTION_SHOW_LIST_MASK);
        mLocalReceiver = new MyLocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, intentFilter);
        if(getIndexHeightIsShowing()){
            Log.e("subrecommenddialog","引导显示？" + getIndexHeightIsShowing());
            getWindow().getDecorView().setVisibility(View.GONE);
        }else{
            initView();
            requestData();
        }
    }

    private boolean getIndexHeightIsShowing() {
        return  !Highlight.hasShowTip();
    }

    /**
     * 请求列表项
     */
    private void requestData() {
        SubRecommendDlgListRequest request = new SubRecommendDlgListRequest(HttpCommon.SUB_RECOMMENDDLGLIST_REQUEST, this);
        if (mIsFirst) {
            request.setParams(SubRecommendDlgListRequest.FIRST_TIME, 0, 0);
        } else {
            request.setParams(SubRecommendDlgListRequest.NORMAL, mIsPre, mListId);
        }
        request.setTag(this);
        CMainHttp.getInstance().doRequest(request);
    }

    //增加订阅项
    private void addSubItem(RecommendTabSubListItem item) {
//        Log.e("dlg", "添加项目" + item.getTitle());
        item.setAction(0);
        requestItem.add(item);
    }

    //移除订阅项
    private void removeSubItem(RecommendTabSubListItem item) {
//        Log.e("dlg", "删除项目" + item.getTitle());
        item.setAction(1);
        requestItem.add(item);
    }

    @Override
    public void show() {
        super.show();
        isShowIng = true;
    }

    private void initView() {
        //contentview
        mainContentLayout = findView(R.id.subrecommanddlg_content);
        //loading view
        loadingLayout = findView(R.id.subrecommanddlg_loadinglayout);
        topTab = findView(R.id.subrecommanddlg_toptab);
        btnCalcel = findView(R.id.subrecommanddlg_btncancle);
        btnOk = findView(R.id.subrecommanddlg_btnok);
        btnOk.setOnClickListener(this);
        mainViewPager = findView(R.id.subrecommanddlg_viewpager);
        ivTopBg = findView(R.id.subrecommanddlg_top_background);
        btnCalcel.setOnClickListener(this);
        //顶部布局
        tvTitle = findView(R.id.subrecommanddlg_title);
        //显示loading
        showLoading();
    }

    /**
     * 显示loading界面
     */
    public void showLoading() {
        mainContentLayout.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 错误界面
     */
    public void showError() {
        if (isFromIM) {
            ToastUtil.show(mContext, "网络错误");
        }
        if (isShowing()) {
            dismiss();
        }
    }

    /**
     * loading结束
     */
    public void loadingFinished() {
        if (mLocalDataSuccess && mRequestSuccess) {
            mainContentLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.INVISIBLE);
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            getWindow().getDecorView().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 正常显示
     */
    public void showData(HttpJsonResponse response) {
        RecommendSubModel bean = new Gson().fromJson(response.getBody(),
                new TypeToken<RecommendSubModel>() {
                }.getType());
        if (bean == null) {
            showError();
            return;
        }
        if (bean.getRecommends() == null) {
            showError();
            return;
        }
        if (bean.getRecommends().isEmpty()) {
            showError();
            return;
        }
        initData(bean);
        initRequestData(bean);
    }

    //初始化请求需要用到的数据
    private void initRequestData(final RecommendSubModel bean) {
        new Thread() {
            @Override
            public void run() {
                List<RecommendSubTab> recommends = bean.getRecommends();
                for (int x = 0; x < recommends.size(); x++) {
                    //默认订阅的先加到请求数组内
                    List<RecommendTabSubListItem> tablist = recommends.get(x).getTablist();
                    for (int y = 0; y < tablist.size(); y++) {
                        if (tablist.get(y).isDefault()) {
                            //添加所有默认订阅的项目
                            addSubItem(tablist.get(y));
                        }
                    }
                }
                //搞定之后发送一个本地广播
                Intent intent = new Intent();
                intent.setAction(ACTION_MAKEREQUESTDATASUCCESS);
                mLocalBroadcastManager.sendBroadcast(intent);
            }
        }.start();
    }

    /**
     * 初始化数据
     */
    private void initData(RecommendSubModel bean) {
//        SystemClock.sleep(2000);
        tvTitle.setText(bean.getTitle());
        tvTitle.setTextColor(Color.parseColor(bean.getTitlecolor()));
        //初始化pager
        initPager(bean);
        //初始化顶部视图
        initTitleTab(bean.getRecommends());
        mRequestSuccess = true;
        loadingFinished();
    }

    /**
     * 初始化viewpager
     *
     * @param bean
     */
    private void initPager(final RecommendSubModel bean) {
        mPagerAdapter = new SubRecommendDlgPagerAdapter(getContext(), bean.getRecommends());
        mainViewPager.setOffscreenPageLimit(5);//缓存每一页，这样就不需要考虑数据回显了。
        mainViewPager.setAdapter(mPagerAdapter);
        mainViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mCurrentTopBg = bean.getRecommends().get(i).getBackgroundimage();
                onClicked(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 显示顶部背景
     */
    private void loadTopBackGroundImage() {
        PhotoUtils.getImageLoader().displayImage(mCurrentTopBg, ivTopBg, MyDisplayImageOption.getRoundOption(mContext, 5));
    }

    /**
     * 初始化头部的tab控件
     *
     * @param recommends
     */
    private void initTitleTab(List<RecommendSubTab> recommends) {
        int width  = getContext().getResources().getDimensionPixelOffset(R.dimen.space_50);
        int height = getContext().getResources().getDimensionPixelOffset(R.dimen.space_67);
        topTab.removeAllViews();
        int recommendsLength = recommends.size();
        int margin           = cacluCateMargin(recommendsLength);
        for (int x = 0; x < recommendsLength; x++) {
            RecommendSubTab subTab = recommends.get(x);
            CheckBoxWithMask cbw = new CheckBoxWithMask(mContext, subTab.getTitle());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.leftMargin = margin;
            params.rightMargin = margin;
            PhotoUtils.getImageLoader().displayImage(subTab.getTitleimageurl(), cbw.getImageViewBackGround(), MyImageLoader.subrecommendOptions);
            cbw.setOnCheckedChangeListener(this);
            cbw.setTag(x);
            mCheckBoxes.add(cbw);
            topTab.addView(cbw, params);
        }
        if (recommends.size() > 0) {
            mCurrentTopBg = recommends.get(0).getBackgroundimage();
            onClicked(0);// 切换到第0页.
        }
    }

    /**
     * 计算每个tab之间的距离，没找到规律...先switch吧。
     *
     * @param count
     * @return
     */
    private int cacluCateMargin(int count) {
        int margin = DeviceUtil.dip2px(mContext, (float) (17 - (count * 1.5)));
        switch (count) {
            case 2:
                return DeviceUtil.dip2px(mContext, 18);
            case 3:
                return DeviceUtil.dip2px(mContext, 14);
            case 4:
                return DeviceUtil.dip2px(mContext, 8);
        }
        return margin;
    }

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subrecommanddlg_btncancle:
                //取消订阅统计
                UpEventAgent.onZSJxCancel(getContext());
                dismiss(); // 取消按钮
                break;
            case R.id.subrecommanddlg_btnok:
                submit(); // 提交按钮
                UpEventAgent.onZSJxSubscribe(getContext());
                break;
        }
    }

    /**
     * 提交订阅数据
     */
    private void submit() {
        showLoading();
        /**
         * 排重
         */
        Iterator<RecommendTabSubListItem> iterator = requestItem.iterator();
        while (iterator.hasNext()) {
            RecommendTabSubListItem item = iterator.next();
            //如果已经订阅，并且还要订阅，就取消此订阅项
            if (item.issubed() && item.getAction() == 0)
                iterator.remove();
            //如果还未订阅，并且要取消此订阅项，还是删除
            if (!item.issubed() && item.getAction() == 1)
                iterator.remove();
        }
        /**
         * 批量订阅请求
         */
        BatchSubAndDelRequest request = new BatchSubAndDelRequest(HttpCommon.SUB_BATCHSUBANDDEL_REQUEST, this);
        if (requestItem.size() > 0) {
            request.setParams(requestItem);
            request.setTag(this);
            CMainHttp.getInstance().doRequest(request);
        } else {
//            ToastUtil.show(mContext, "操作成功");
            dismiss();
        }

    }

    /**
     * 只有主动点击按钮的情况下，才会调用此方法。
     *
     * @param tag ->  view.getTag();
     */
    @Override
    public void onClicked(Object tag) {
        for (CheckBoxWithMask cbw : mCheckBoxes) {
            if (tag != null) {
                if (!tag.equals(cbw.getTag())) {
                    //不是当前按钮，置灰
                    cbw.setCheckState(false);
                } else {
                    //是当前按钮。切换pager
                    mainViewPager.setCurrentItem((Integer) tag, true);
                    cbw.setCheckState(true);
                    loadTopBackGroundImage();
                    boolean currentMaskState = mPagerAdapter.getCurrentMaskState((Integer) tag);
                    setListMastState(currentMaskState);
                }
            }
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        int              id   = request.getmId();
        HttpJsonResponse resp = request.getResponse();
        switch (id) {
            case HttpCommon.SUB_RECOMMENDDLGLIST_REQUEST: //进来请求的接口
                showData(resp);
                break;
            case HttpCommon.SUB_BATCHSUBANDDEL_REQUEST: // 批量订阅
                subSuccess(resp, requestItem);
                break;
        }
    }

    /**
     * 订阅成功
     *
     * @param resp
     * @param requestItem
     */
    private void subSuccess(HttpJsonResponse resp, final HashSet<RecommendTabSubListItem> requestItem) {
        final SuberDaoImp suberDaoImp = new SuberDaoImp();
        if (resp.getCode() == 200) {
            //成功请求
            new Thread() {
                @Override
                public void run() {
                    //更新本地订阅
                    List<SuberedItemInfo> addList = new ArrayList<SuberedItemInfo>();// 添加数组
                    List<SuberedItemInfo> delList = new ArrayList<SuberedItemInfo>();//删除数组
                    // 批量插入 && 删除
                    for (RecommendTabSubListItem item : requestItem) {
                        if (item.getAction() == 0) {
                            //添加
                            addList.add(item.toSuberedItemInfo());
                        } else if (item.getAction() == 1) {
                            //删除
                            delList.add(item.toSuberedItemInfo());
                        }
                    }
                    //批量添加，删除
                    try {
                        if (!addList.isEmpty()) {
                            suberDaoImp.addAll(addList);
                        }
                        if (!delList.isEmpty()) {
                            suberDaoImp.clearList(delList);
                        }
                        //删除完毕，成功...
                        Intent intent = new Intent();
                        intent.setAction(ACTION_SUBSUCCESS);
                        mLocalBroadcastManager.sendBroadcast(intent);
                    } catch (Exception e) {
//                        ToastUtil.show(mContext, "操作失败");
                        dismiss();
                    }

                }
            }.start();
            //取消订阅统计

        } else {
//            ToastUtil.show(mContext, "操作失败");
            dismiss();
        }

    }

    @Override
    public void onHttpError(IRequest request) {
        int id = request.getmId();
        switch (id) {
            case HttpCommon.SUB_RECOMMENDDLGLIST_REQUEST: //数据都没出来，必须干掉
                showError();
                break;
            case HttpCommon.SUB_BATCHSUBANDDEL_REQUEST: // 批量订阅
//                ToastUtil.show(mContext, "操作失败");
                dismiss();
                break;
        }
    }

    @Override
    public void onHttpStart(IRequest request) {

    }

    /**
     * 广播处理器
     */
    private class MyLocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.e("timetest", SystemClock.currentThreadTimeMillis()+"");
            RecommendTabSubListItem item = (RecommendTabSubListItem) intent.getSerializableExtra("data");
            if (intent.getAction().equals(ACTION_ADD)) {
                //增加订阅项
                addSubItem(item);
            }
            if (intent.getAction().equals(ACTION_REMOVE)) {
                //移除订阅项
                removeSubItem(item);
            }
            if (intent.getAction().equals(ACTION_SUBSUCCESS)) {
                //订阅成功，发送一个广播，通知首页刷新
                try {
                    returnToBall();
                } catch (Exception e) {
                    //此处可能会发生异常。。。
                }
                Intent intentGotoBall = new Intent();
                intentGotoBall.setAction(SouyueTabFragment.REFRESH_HOMEBALL_FROMCACHE);
                mContext.sendBroadcast(intentGotoBall);
                Log.e(this.getClass().getName(), "suber send broadcast!!!");
                dismiss();
            }
            if (intent.getAction().equals(ACTION_MAKEREQUESTDATASUCCESS)) {
                //准备本地数据成功
                mLocalDataSuccess = true;
                loadingFinished();
            }
            if (intent.getAction().equals(ACTION_SHOW_LIST_MASK)) {
                //显示遮罩
                setListMastState(true);
            }
            if (intent.getAction().equals(ACTION_REMOVE_LIST_MASK)) {
                //移除遮罩
                setListMastState(false);
            }
        }
    }

    /**
     * 设置list的遮罩状态
     *
     * @param isShow
     */
    private void setListMastState(boolean isShow) {
        findViewById(R.id.subercommend_dlg_mask).setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 记录首页的球球srpid,回来的时候跳过去
     */
    private void returnToBall() {
        if (mContext instanceof MainActivity) {
            //主页跳转
            MainActivity mainActivity = (MainActivity) mContext;
            FragmentManager supportFragmentManager = mainActivity.getSupportFragmentManager();
            Fragment souyueTabFragment = null;
            if (supportFragmentManager != null) {
                souyueTabFragment = supportFragmentManager.findFragmentByTag(MainActivity.SOUYUE_SPEC);
            }
            if (souyueTabFragment != null) {
                String currentMiddleSrpId = ((SouyueTabFragment) souyueTabFragment).getCurrentMiddleSrpId();
                SYSharedPreferences sySharedPreferences = SYSharedPreferences.getInstance();
                if (currentMiddleSrpId != null && sySharedPreferences != null)
                    sySharedPreferences.putString(SYSharedPreferences.SUBER_SRPID, currentMiddleSrpId);
            }
        }
    }

    @Override
    public void dismiss() {
//        CMainHttp.getInstance().cancel(this);
        //退出之前，消失弹框
        try {
            mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
            if (!isFromIM) {
                //只记录不是来自im的弹框
                SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_TIMESTAMP_SUBRECOMMEND, SYUserManager.getInstance().getUser().userId() + "," + System.currentTimeMillis());
            }
        } catch (Exception e) {

        }
        isShowIng = false;
//        if(diaLogQueue.isEmpty()){
//            SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND);
//        }else{
//            SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND, "subPopWindow");
//        }
        //防止出现一些诡异的异常。
        if (mContext != null && mContext instanceof Activity) {
            if (!((Activity) mContext).isFinishing()) {
                try {
                    super.dismiss();
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * 弹框用到的数据
     */
    static class DialogData {
        private boolean isFirst;
        private Long    isPre;
        private Long    listId;

        public DialogData(boolean isFirst, Long isPre, Long listId) {
            this.isFirst = isFirst;
            this.isPre = isPre;
            this.listId = listId;
        }

        public boolean isFirst() {
            return isFirst;
        }

        public void setFirst(boolean first) {
            isFirst = first;
        }

        public Long getIsPre() {
            return isPre;
        }

        public void setIsPre(Long isPre) {
            this.isPre = isPre;
        }

        public Long getListId() {
            return listId;
        }

        public void setListId(Long listId) {
            this.listId = listId;
        }
    }
}
