package com.zhongsou.souyue.adapter.baselistadapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.circle.view.CircleFollowDialogNew;
import com.zhongsou.souyue.circle.view.InterestDialog;
import com.zhongsou.souyue.countUtils.AppInfoUtils;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.db.HomePageDBHelper;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.fragment.SouyueTabFragment;
import com.zhongsou.souyue.module.DiskLikeBean;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.FootItemBean;
import com.zhongsou.souyue.module.listmodule.SearchItemData;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.home.DisLikeRequest;
import com.zhongsou.souyue.net.other.HistoryClear;
import com.zhongsou.souyue.net.sub.SubUpdateRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.receiver.HomeListener;
import com.zhongsou.souyue.receiver.ScreenListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.view.ZSVideoConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: listview 管理者
 * 1.处理，点赞，踩，评论，分享等事件
 * 2.listview 中的 item 的点击事件
 * 3.gif 播放控制停止等
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class HomeListManager extends BottomInvoke4Manager implements IBottomInvoke3, IBottomInvoke5, IItemInvokeBanner, IItemInvokeGif {
    public static final int POPWINDOWN_COLUMN_COUNT = 2;//列表中热标签
    public static final int SHARE_TO_SSO_REQUEST_CODE = 0x80cd;//新浪认证回调的requestcode
    public static final int VIDEO_VIEW_SEEK_POSITION_REQUEST_CODE = 1301;//视频回调requestcode
    public static final int PRIORITY_MORE = 111111;
    public static final int PRIORITY_PRE = 111110;
    private Activity mActivity;
    private View mPopupView;
    private PopupWindow mPopView;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private boolean mRefreshing;
    private boolean mShowPopupWindow;
    private CMainHttp mMainHttp;
    protected Animation mListViewDelete;
    private String mballTitle;//球球名字
    private CircleFollowDialogNew circleFollowDialog;
    private Posts mPosts;
    private boolean mIsPrivate;
    private int mRole;
    private int mIsBanTalk;
    private Dialog showDialogAddImg;
    private ViewGroup addIMgView;
    private String mInterestId;
    private OnChangeListener onRoleChangeListener;
    private BaseListData mListData;
    private com.zhongsou.souyue.circle.model.ShareContent circleShareContent;
    private SsoHandler mSsoHandler;
    private int gifPlayPosition;
    private BigImageRender bigImageRender;
    private int priority = PRIORITY_PRE;

    private static final String SUBER_ADD = "add";
    private static final String SUBER_DEL = "del";
    private boolean isSubRunning = false;  // 标志网络是否正在请求 - 订阅
    private InterestDialog suberDialg;
    private SearchItemData searchSubItem;     //搜索页面 订阅item
    private View searchSubView; //搜索页面 订阅点击item的View

    public HomeListManager(Activity context) {
        super(context);
        mActivity = context;
        mMainHttp = CMainHttp.getInstance();
        mListViewDelete = AnimationUtils.loadAnimation(mActivity, R.anim.right_out);

    }

    /**
     * 点击底部右侧来源 跳转相关srp页
     *
     * @param data
     */
    public void clickSource(BaseListData data) {
        UmengStatisticUtil.onEvent(mActivity, UmengStatisticEvent.HOME_HEADLINE_ITEM_RIGHT_DOWN_CLICK);  //Umeng
        BaseInvoke ivoke = data.getInvoke().clone();
        if (ivoke != null) {
            FootItemBean foot = data.getFootView();
            int type = 0;
            if (foot != null) {
                type = foot.getChannelInvokeType();
            }
            ivoke.setType(type);
            ivoke.setChan(mChannel);
            HomePagerSkipUtils.skip(mActivity, ivoke);
        }
    }

    /**
     * 点击弹出不感兴趣框
     *
     * @param v
     * @param pos
     * @param data
     */
    public void clickUnLike(View v, int pos, BaseListData data) {
        if (mRefreshing) {
            return;
        }
        FootItemBean bean = data.getFootView();
        List<DiskLikeBean> list = bean.getDisLike();
        boolean isSub = (bean.getIsSubscribe() == 1);
        if (list == null || list.size() == 0) {
            showOlduninterest(v, pos, data);
        } else {
            showNewUninterest(v, pos, data, isSub);
        }
        if (data != null && v != null && data.getCategory() != null) {
            if (data.getCategory().equals(ConstantsUtils.VJ_NEW_SEARCH)) {
                UpEventAgent.onZSNewsDislike(v.getContext());
            } else if (data.getCategory().equals(ConstantsUtils.FR_INTEREST_BAR)) {
                UpEventAgent.onZSGroupDislike(v.getContext());
            } else if (data.getCategory().equals(ConstantsUtils.VJ_DUANZI_SEARCH)) {
                UpEventAgent.onZSDuanziDislike(v.getContext());
            } else if (data.getCategory().equals(ConstantsUtils.VJ_GIF_SEARCH)) {
                UpEventAgent.onZSGIFDislike(v.getContext());
            } else if (data.getCategory().equals(ConstantsUtils.FR_INFO_PICTURES)) {
                UpEventAgent.onZSPicturelDislike(v.getContext());
            } else if (data.getCategory().equals(ConstantsUtils.FR_INFO_SPECIAL)) {
                UpEventAgent.onZSTopicDislike(v.getContext());
            }
        }
    }

    /**
     * 点击订阅按钮 搜索页面 v5.2.0 by YanBin
     *
     * @param view 被点击的view
     * @param item 被点击条目的数据
     */
    public void clickSubscribe(View view, SearchItemData item) {
        searchSubView = view;
        searchSubItem = item;
        if (mRefreshing || isSubRunning || item.getIsPrivate() == 1) {
            return;
        }
        isSubRunning = true;
        suberDialg = new InterestDialog(mActivity);
        String type = SUBER_ADD;
        if (item.getIsSubscribe() == 1) {   // 0:未订阅;1:已订阅
            type = SUBER_DEL;
        }

        if("srp".equals(item.getCategory()) && SUBER_DEL.equals(type)){
            SuberDao dao = new SuberDaoImp();
            SuberedItemInfo suberedItemInfo = dao.queryOne(item.getInvoke().getSrpId());
            if (suberedItemInfo != null) {
                item.setId(suberedItemInfo.getId());
            }
        }

        suberDialg.show();
        suberDialg.progress();
        SubUpdateRequest.send(
                HttpCommon.SUBER_UPDATE_METHOD,
                SYUserManager.getInstance().getToken(),
                DeviceInfo.getDeviceId(),
                item.getInvoke().getSrpId(),    //srpId
                type,
                item.getCategory(),
                item.getId() + "",      //interestid
                item.getInvoke().getKeyword(),
                this);
    }

    /**
     * 删除一条数据
     *
     * @param v
     * @param pos
     * @param data
     */
    public void clickDeleteItem(View v, int pos, BaseListData data) {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)) {
            SouYueToast.makeText(mActivity, R.string.souyue_neterror, Toast.LENGTH_LONG).show();
            return;
        }
        doListDeleteAnimation(data, pos);
        HistoryClear clear = new HistoryClear(0, this);
        clear.addParameters(data.getId());
        mMainHttp.doRequest(clear);
    }


    /**
     * 显示不感兴趣旧的展示方式
     *
     * @param v
     * @param pos
     * @param item
     */
    private void showOlduninterest(View v, int pos, final BaseListData item) {
        if (!mShowPopupWindow) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
                mShowPopupWindow = true;


            mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.popup_window_unintersting_old, null);
            final PopupWindow pop = new PopupWindow(mPopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pop.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.homepage_popup_bg));
            pop.setFocusable(true);
            pop.setOutsideTouchable(true);
            final int positionIndex = pos;
            //设置半透明
            int first = mListView.getFirstVisiblePosition();
            final int index = positionIndex - first + 1;
            final View view = mListView.getChildAt(index);
            setCoverItem(view);
            int[] position = new int[2];
            v.getLocationInWindow(position);
            pop.setAnimationStyle(R.style.home_page_pop_style_old);
            pop.showAtLocation(mListView, Gravity.LEFT | Gravity.TOP, position[0] + v.getWidth(), position[1]);
            pop.update();
            pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mShowPopupWindow = false;
                    clearCoverItem(view);
                }
            });
            mPopupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pop.dismiss();
                    clearCoverItem(view);
                    doListDeleteAnimation(item, positionIndex);
                    BaseInvoke invoke = item.getInvoke();
                    DisLikeRequest request = new DisLikeRequest(HttpCommon.HOME_LIST_DISLIKE, HomeListManager.this);
                    request.setParams(SYUserManager.getInstance().getToken(),
                            invoke.getUrl(), invoke.getBlogId() + "", invoke.getInterestId() + "", "", item, invoke.getChan(), item.getCategory());
                    mMainHttp.doRequest(request);
//                    mMainHttp.doDisLike(CMainHttp.HTTP_REQUEST_HOMELIST_DISLIKE, SYUserManager.getInstance().getToken(),
//                            invoke.getUrl(), invoke.getBlogId() + "", invoke.getInterestId() + "","", HomeListManager.this, item,invoke.getChan(),item.getCategory());
                }
            });
        }
    }


    public void setBigImageRender(BigImageRender bigImageRender, int gifPlayPosition) {
        this.bigImageRender = bigImageRender;
        this.gifPlayPosition = gifPlayPosition;
    }

    public void stopPlayingGif(int position) {
        if (position != this.gifPlayPosition && bigImageRender != null) {
            bigImageRender.stopPlayGif();
        }
    }

    /**
     * 显示不感兴趣新的展示方式
     *
     * @param v
     * @param pos
     * @param _item
     */
    private void showNewUninterest(View v, int pos, BaseListData _item, boolean isSubed) {
        if (!mShowPopupWindow) {
            if (mActivity instanceof MainActivity) {
                ((MainActivity) mActivity).setEnable(false);
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
                mShowPopupWindow = true;
            mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.popup_window_unintersting, null);
            final BaseListData item = _item;
            final int positionIndex = pos;
            //设置半透明
            int first = mListView.getFirstVisiblePosition();
            final int index = positionIndex - first + 1;
            final View view = mListView.getChildAt(index);
            setCoverItem(view);
            int[] position = new int[2];
            v.getLocationInWindow(position);
            boolean istop = true;
            if (position[1] > Utils.getScreenHeight() / 2) {
                istop = false;
            }
            int height = genernateMiddleLayout(mPopupView, item, istop, view, positionIndex, isSubed);//添加中间的view
            mPopView = new PopupWindow(mPopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopView.setWidth(mListView.getWidth());
            //必须设置背景，才能相应返回键，和点击外面消失---不知道为什么
            mPopView.setBackgroundDrawable(mActivity.getResources().getDrawable(R.color.transparent));
            mPopView.setFocusable(true);
            mPopView.setOutsideTouchable(true);

            int y = position[1];
            if (istop) {
                y = position[1] + DeviceUtil.dip2px(mActivity, 25);
                mPopView.setAnimationStyle(R.style.home_page_pop_style);
            } else {
                y = position[1] - height;
                mPopView.setAnimationStyle(R.style.home_page_pop_style_bottom);
            }
            mPopView.showAtLocation(mListView, Gravity.LEFT | Gravity.TOP, position[0] + v.getWidth(), y);
            mPopView.update();
            changeBackground();
            mPopView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mShowPopupWindow = false;
                    clearCoverItem(view);
                    closePopWindow();
                    if (mActivity instanceof MainActivity) {
                        ((MainActivity) mActivity).setEnable(true);
                    }
                }
            });
            UmengStatisticUtil.onEvent(mActivity, UmengStatisticEvent.DISLIKE_CLICK);
        }
    }

    private void changeBackground() {
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = 0.5f;
        mActivity.getWindow().setAttributes(params);
    }

    private void closePopWindow() {
        mPopView = null;
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = 1.f;
        mActivity.getWindow().setAttributes(params);
    }

    private int genernateMiddleLayout(View view, final BaseListData item, boolean istop, final View itemview, final int positionIndex, final boolean isSubed) {
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.rl_homepage_delete_item);
        RelativeLayout top = (RelativeLayout) view.findViewById(R.id.rl_homepage_delete_top);
        Button confirm = (Button) view.findViewById(R.id.bt_homepage_delete_confirm); // 不感兴趣
        final Button disLike = (Button) view.findViewById(R.id.bt_homepage_ubsubed_confirm); // 取消订阅
        LinearLayout bottomContainer = (LinearLayout) view.findViewById(R.id.homepage_delete_bottom_popup_container);
        TextView topTitle = (TextView) view.findViewById(R.id.tv_homepage_delete_title);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topTitle.getLayoutParams();
        LinearLayout.LayoutParams topparams = (LinearLayout.LayoutParams) top.getLayoutParams();
        RelativeLayout.LayoutParams btparams = (RelativeLayout.LayoutParams) bottomContainer.getLayoutParams();
        //如果订阅显示disLike
        disLike.setVisibility(View.GONE);
        int height = 0;
        if (istop) {
            bottomContainer.setBackgroundResource(R.drawable.homepage_delete_bottom_popup_button);
            top.setBackgroundResource(R.drawable.home_delete_item_top);
            topparams.height = DeviceUtil.dip2px(mActivity, 40);
            params.topMargin = DeviceUtil.dip2px(mActivity, 3);
            height += topparams.height;
            height += DeviceUtil.dip2px(mActivity, 30);//底下按钮的高度
        } else {
            bottomContainer.setBackgroundResource(R.drawable.homepage_delete_bottom_popup_button_anthor);
            topparams.height = DeviceUtil.dip2px(mActivity, 30);
            params.topMargin = DeviceUtil.dip2px(mActivity, 4);
            btparams.height = DeviceUtil.dip2px(mActivity, 50);
            height += btparams.height;
            height += DeviceUtil.dip2px(mActivity, 30);//顶上title的高度
        }
        height += DeviceUtil.dip2px(mActivity, 30);//中间布局的bottompadding
        final List<DiskLikeBean> datas = item.getFootView().getDisLike();
        if (datas == null) {
            return height;
        }
        final HashMap<String, String> obj = new HashMap<String, String>();
        View.OnClickListener click = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_homepage_delete_confirm:
                        clearCoverItem(itemview);
                        doListDeleteAnimation(item, positionIndex);
                        JSONObject o = new JSONObject();
                        for (Map.Entry<String, String> data : obj.entrySet()) {
                            try {
                                o.put(data.getKey(), data.getValue());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        UmengStatisticUtil.onEvent(mActivity, UmengStatisticEvent.DISLIKE_CONFIRM_CLICK);
                        mPopView.dismiss();
                        BaseInvoke invoke = item.getInvoke();
                        DisLikeRequest request = new DisLikeRequest(HttpCommon.HOME_LIST_DISLIKE, HomeListManager.this);
                        request.setParams(SYUserManager.getInstance().getToken(),
                                invoke.getUrl(), invoke.getBlogId() + "", invoke.getInterestId() + "",
                                o.toString(), item, invoke.getChan(), item.getCategory());

                        mMainHttp.doRequest(request);
                        Toast.makeText(v.getContext(), "不感兴趣", Toast.LENGTH_SHORT).show();
//                        mMainHttp.doDisLike(CMainHttp.HTTP_REQUEST_HOMELIST_DISLIKE, SYUserManager.getInstance().getToken(),
//                                invoke.getUrl(), invoke.getBlogId() + "", invoke.getInterestId() + "", o.toString(), HomeListManager.this, item,invoke.getChan(),item.getCategory());
                        break;
                    case R.id.bt_homepage_ubsubed_confirm:
                        clearCoverItem(itemview);
                        doListDeleteAnimation(item, positionIndex);
                        JSONObject o1 = new JSONObject();
                        for (Map.Entry<String, String> data : obj.entrySet()) {
                            try {
                                o1.put(data.getKey(), data.getValue());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        UmengStatisticUtil.onEvent(mActivity, UmengStatisticEvent.DISLIKE_CONFIRM_CLICK);
                        mPopView.dismiss();
                        BaseInvoke invoke1 = item.getInvoke();
                        try {
                            o1.put("srpId", invoke1.getSrpId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(SouyueTabFragment.ACTION_UNSUB_BALL);
                        intent.putExtra("srpId", invoke1.getSrpId());
                        v.getContext().sendBroadcast(intent);
                        Toast.makeText(v.getContext(), "取消订阅", Toast.LENGTH_SHORT).show();
                        DisLikeRequest request1 = new DisLikeRequest(HttpCommon.HOME_LIST_DISLIKE, HomeListManager.this);
                        request1.setParams(SYUserManager.getInstance().getToken(),
                                invoke1.getUrl(), invoke1.getBlogId() + "", invoke1.getInterestId() + "",
                                o1.toString(), item, invoke1.getChan(), item.getCategory());

                        mMainHttp.doRequest(request1);
                        break;
                    default:
//                        Object ox = v.getTag();
//                        if (ox instanceof DiskLikeBean) {
//                            DiskLikeBean bean = (DiskLikeBean) ox;
//                            if (obj.containsKey(bean.getLog())) {
//                                obj.remove(bean.getLog());
//                                checked((TextView) v, false);
//                            } else {
//                                obj.put(bean.getLog(), bean.getTag());
//                                checked((TextView) v, true);
//                            }
//                        }
                }
            }
        };
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Object ox = v.getTag();
                    if (ox instanceof DiskLikeBean) {
//                      此处点击不感兴趣弹窗中内容的回调 // TODO 更改页面
                        DiskLikeBean bean = (DiskLikeBean) ox;
                        if (obj.containsKey(bean.getLog())) {
                            obj.remove(bean.getLog());
                            checked((TextView) v, false);
                            if (bean.getIsSubscribe() == 1) {
                                disLike.setVisibility(View.GONE);
                            }
                        } else {
                            obj.put(bean.getLog(), bean.getTag());
                            checked((TextView) v, true);
                            if (bean.getIsSubscribe() == 1) {
                                disLike.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                return false;
            }
        };
        confirm.setOnClickListener(click);
        top.setOnClickListener(click);
        layout.setOnClickListener(click);
        disLike.setOnClickListener(click);
        int textHeight = DeviceUtil.dip2px(mActivity, 22);//每个textview高度
        int topdp = DeviceUtil.dip2px(mActivity, 12);

//       新的gridlayout布局
        int l = datas.size();
        int column = (l + POPWINDOWN_COLUMN_COUNT / 2) / POPWINDOWN_COLUMN_COUNT;//向上取整算出行数
        height += column * topdp;
        height += column * textHeight;
        int index = 0;
        LinearLayout pre = null;
        for (int i = 0; i < column; i++) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(mActivity).inflate(R.layout.popup_window_unintersting_text, null);
//            andnroid4.2才有的方法，不能用View.grenerateId方法
            linearLayout.setId(R.layout.popup_window_unintersting_text + 1001 + i);
            TextView tv1 = (TextView) linearLayout.findViewById(R.id.tv_uninterest_item1);
            DiskLikeBean bean1 = datas.get(index);
            tv1.setTag(bean1);
            tv1.setText(bean1.getTag());
            tv1.setOnTouchListener(listener);
            index++;
            TextView tv2 = (TextView) linearLayout.findViewById(R.id.tv_uninterest_item2);
            if (index >= l) {
                tv2.setVisibility(View.INVISIBLE);
            } else {
                DiskLikeBean bean2 = datas.get(index);
                tv2.setTag(bean2);
                tv2.setText(bean2.getTag());
                tv2.setOnTouchListener(listener);
                index++;
            }
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.topMargin = topdp;
            if (pre != null) {
                params1.addRule(RelativeLayout.BELOW, pre.getId());
            }
            layout.addView(linearLayout, params1);
            pre = linearLayout;
        }
        return height;
    }


    protected void doListDeleteAnimation(BaseListData item, int positionIndex) {
        int first = mListView.getFirstVisiblePosition();
        final int index = positionIndex - first + 1;
        View view = mListView.getChildAt(index);
        if (view == null) {
            return;
        }
        final ViewGroup vvv = (ViewGroup) view;

        final BaseListData posi = item;
        vvv.setVisibility(View.INVISIBLE);
        mListViewDelete.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doListPushUpAnimation(vvv, posi);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        vvv.clearAnimation();
        vvv.startAnimation(mListViewDelete);
    }

    protected void doListPushUpAnimation(final ViewGroup vvv, final BaseListData posi) {
        final int initialHeight = vvv.getMeasuredHeight();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                if (interpolatedTime == 1) {
//                    vvv.setVisibility(View.GONE);
//                }
//                else {
                vvv.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                vvv.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vvv.setVisibility(View.VISIBLE);
                for (int i = 0; i < vvv.getChildCount(); i++) {//显示所有子view
                    vvv.getChildAt(i).setVisibility(View.VISIBLE);
                }
                mAdapter.deleteData(posi);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(300);
        vvv.clearAnimation();
        vvv.startAnimation(anim);
        for (int i = 0; i < vvv.getChildCount(); i++) {//让所有子view不可见来播放动画
            vvv.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }

    private void checked(TextView v, boolean ischecked) {
        if (ischecked) {
            v.setBackgroundResource(R.drawable.homepage_delete_item_checked);
            v.setTextColor(mActivity.getResources().getColor(R.color.homepage_delete_red));
        } else {
            v.setBackgroundResource(R.drawable.homepage_delete_item_normal);
            v.setTextColor(mActivity.getResources().getColor(R.color.ball_text_color));
        }
    }

    private void setCoverItem(View view) {
        if (view != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                view.setAlpha(0.5f);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void clearCoverItem(View view) {
        if (view != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                view.setAlpha(1.f);
            }
        }
    }

    /**
     * 设置滑动的时候不感兴趣不能点击
     *
     * @param mRefreshing
     */
    public void setmRefreshing(boolean mRefreshing) {
        this.mRefreshing = mRefreshing;
    }

    public boolean ismRefreshing() {
        return mRefreshing;
    }

    public void setView(ListViewAdapter adapter, ListView listview) {
        mAdapter = adapter;
        mListView = listview;
    }

    public void setBallTitle(String ballTitle) {
        mballTitle = ballTitle;
    }

    /**
     * 跳转方法
     *
     * @param item
     */
    public void clickItem(BaseListData item) {
        BaseInvoke invoke = item.getInvoke();
        invoke.setChan(mChannel);
        UpEventAgent.onZSDevListItemClick(mActivity, invoke.getTitle(),
                invoke.getSrpId(), invoke.getKeyword(), invoke.getUrl(), item.getFootView() != null ? item.getFootView().getSource() : "", invoke.getCategory(), mballTitle,
                invoke.getBlogId() + "");
        forceStopPlay();
        HomePagerSkipUtils.skip(mActivity, invoke);
    }

    private void getNewsShortUrl(BaseListData data) {
        ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID, this);
        req.setParams(appendStringUrl(data));
        CMainHttp.getInstance().doRequest(req);
    }

    private String appendStringUrl(BaseListData data) {
        StringBuilder mUrl = new StringBuilder();
        mUrl.append(getNewsUrl(data.getInvoke().getUrl(), data.getInvoke().getKeyword(), data.getInvoke().getSrpId(), data.getTitle(), "", "", data.getInvoke().getCategory()));
        MakeCookie.synCookies(mActivity, mUrl.toString());// 耗时33毫秒
//        if(mUrl.toString().contains("?")){
//            mUrl.append("&");
//        }else{
//            mUrl.append("?");
//        }
//        mUrl.append("pushfrom=").append(mItem.getPushFrom()).append("&mid=").append(mItem.getMsgId()).append("&clickfrom=").append(mItem.getClickFrom());
        return mUrl.toString();
    }

    private String getNewsUrl(String mSourceUrl, String mKeyword, String mSrpId, String mTitle, String sources, String pubTime, String category) {
        String url = "";
        if (mSourceUrl == null || mSourceUrl.contains("ugc.groovy")
                || mSourceUrl.contains("urlContent.groovy")
                || mSourceUrl.contains("interest.content.groovy")
                || mSourceUrl.contains("isextract=1")
                || mSourceUrl.contains("or_id")
                || mSourceUrl.contains("jokesDetail.groovy")
                || mSourceUrl.contains("gifDetail.groovy")) {
            url = mSourceUrl;
        } else {
            if (mKeyword == null) {  //从微信过来的报刊是没有mkeyword的，为了防止编码报错
                mKeyword = "";
            }
            try {
                //图集页面跳转
                if (mSourceUrl.contains("PicNews?")) {
                    url = UrlConfig.HOST_SHARE + "newsdetail/index?category=picnews&keyword="
                            + URLEncoder.encode(mKeyword, "utf-8") + "&srpId=" + mSrpId + "&url="
                            + URLEncoder.encode(mSourceUrl, "utf-8") + "&title=" + URLEncoder.encode(mTitle, "utf-8")
                            + "&source=" + URLEncoder.encode(sources, "utf-8") + "&pubTime=" + pubTime;
                } else {
                    url = UrlConfig.HOST_SHARE + "newsdetail/index?keyword="
                            + URLEncoder.encode(mKeyword, "utf-8") + "&srpId=" + (TextUtils.isEmpty(mSrpId) ? "" : mSrpId) + "&url="
                            + URLEncoder.encode(mSourceUrl, "utf-8");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotEmpty(mSourceUrl)) {
            if (mSourceUrl.contains("jokesDetail.groovy")) {
                UpEventAgent.onZSNewsView(mActivity, mChannel, mSrpId,
                        mTitle, mSourceUrl, StringUtils.isNotEmpty(category) ? category : ConstantsUtils.VJ_DUANZI_SEARCH, "0");
            } else if (mSourceUrl.contains("gifDetail.groovy")) {
                UpEventAgent.onZSNewsView(mActivity, mChannel, mSrpId,
                        mTitle, mSourceUrl, StringUtils.isNotEmpty(category)
                                ? category : ConstantsUtils.VJ_GIF_SEARCH, "0");
            }
        }
        url += "&token=" + SYUserManager.getInstance().getToken();
        url += ("&appName=") + AppInfoUtils.getAppName(mActivity);
        return url;
    }


    /**
     * 非圈成员 订阅成功
     *
     * @param res
     */
    public void saveRecomentCirclesSuccess(HttpJsonResponse res) {
        if (res.getBody().get("state").getAsInt() == 1) {
            Toast.makeText(mActivity, "订阅成功", Toast.LENGTH_SHORT).show();
            UpEventAgent.onGroupJoin(mActivity, mInterestId + "." + "", "");// 统计
            SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);

            /**
             * 需要通知页面的改变
             */
            if (onRoleChangeListener != null) {
                onRoleChangeListener.onChange(null);
            }
        } else {
            Toast.makeText(mActivity, "订阅失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        switch (request.getmId()) {
            case HttpCommon.HOME_LIST_DISLIKE:
                BaseListData item = (BaseListData) request.getKeyValueTag(DisLikeRequest.HOME_PAGE_DISLIKE);
                HomePageDBHelper.getInstance().deleteData(SYUserManager.getInstance().getUserId(), item.getId() + "");//这里只用dataid,因为只有我的头条有删除
                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                saveRecomentCirclesSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                BaseListData data = (BaseListData) request.getKeyValueTag("item");
                if (data != null) {
                    FootItemBean foot = data.getFootView();
                    foot.setCommentCount(foot.getCommentCount() + 1);
                    mAdapter.notifyDataSetChanged();
                }
                circleFollowDialog.dismissProcessDialog();
                SouYueToast.makeText(mActivity, R.string.comment_detail_success, Toast.LENGTH_LONG).show();
                break;
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                shortURLSuccess(request.<HttpJsonResponse>getResponse().getBodyString());
                break;
            case HttpCommon.SUBER_UPDATE_METHOD: // 订阅
                HttpJsonResponse response = (HttpJsonResponse) request.getResponse();

                long id = 0;
                try {
                    id = response.getBody().getAsJsonArray("id").get(0).getAsLong();
                } catch (Exception e) {
                    id = response.getBody().get("interest_id").getAsLong();
                } finally {
                    SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);

                    if (searchSubItem.getIsSubscribe() == 0) {   //未订阅
                        suberDialg.subscribe();
                        searchSubItem.setId(id);
                        subItem(id);
                        searchSubItem.setIsSubscribe(1);
                        SYSharedPreferences.getInstance().setSuberSrpId(searchSubItem.getInvoke().getSrpId());//保存id到sysp
                    } else {  //已订阅
                        suberDialg.unsubscribe();
                        unsubItem();
                        searchSubItem.setIsSubscribe(0);
                    }
                    mAdapter.notifyDataSetChanged();
                    isSubRunning = false;
                }
                break;
        }
    }

    /**
     * 订阅
     *
     * @param id
     */
    private void subItem(long id) {
        SuberDao suberDao = new SuberDaoImp();
        SuberedItemInfo info = new SuberedItemInfo();

        info.setId(id);
        info.setTitle(searchSubItem.getInvoke().getKeyword());
        info.setCategory(searchSubItem.getCategory());
        info.setImage(searchSubItem.getImage().get(0));
        info.setSrpId(searchSubItem.getInvoke().getSrpId());
        info.setKeyword(searchSubItem.getInvoke().getKeyword());
        info.setType("0");  //圈子属性: 1:私密; 0:非私密
        suberDao.addOne(info);
    }

    /**
     * 退订
     */
    private void unsubItem() {
        SuberDao suberDao = new SuberDaoImp();
        SuberedItemInfo info = new SuberedItemInfo();
        info.setSrpId(searchSubItem.getInvoke().getSrpId());
        suberDao.clearOne(info);
    }

    private void shortURLSuccess(String url) {
        mShortUrl = url;
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        IHttpError error = request.getVolleyError();
        int errorType = error.getErrorType();
        int code = error.getErrorCode();
        switch (request.getmId()) {
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                if (circleFollowDialog != null) {
                    circleFollowDialog.dismissProcessDialog();
                }
                if (errorType == CSouyueHttpError.TYPE_SERVER_ERROR) {
                    if (code < 700) {
                        SouYueToast.makeText(mActivity, "评论失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SouYueToast.makeText(mActivity, R.string.networkerror, Toast.LENGTH_SHORT).show();
                }

                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                IHttpError error1 = request.getVolleyError();
                if (error1.getErrorType() == CSouyueHttpError.TYPE_SERVER_ERROR) {
                    UIHelper.ToastMessage(mActivity,
                            R.string.cricle_manage_save_circle_failed);
                } else {
                    UIHelper.ToastMessage(mActivity, R.string.networkerror);
                }
                break;
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                break;
            case HttpCommon.SUBER_UPDATE_METHOD: // 订阅
//                if (suberDialg != null && suberDialg.isShowing()) {
//                    suberDialg.dismiss();
//                }
//                SouYueToast.makeText(mActivity, R.string.fail, 0).show();

                HttpJsonResponse response = null;
                if (error instanceof CSouyueHttpError) {
                    response = error.getJson();
                }
                if (response != null) {
                    int status = response.getHead().get("status").getAsInt();
                    if (status == 700) {
                        suberDialg.dimissRightNow();
                    } else {
                        suberDialg.subscribefail();
                    }
                } else {
                    suberDialg.subscribefail();
                }
                isSubRunning = false;
                break;
        }
    }

    @Override
    public void onHttpStart(IRequest request) {

    }

    @Override
    public void bannerClickItem(BaseListData item) {
        clickItem(item);
    }


    VideoUpdateBroadCastRecever receiver;

    /**
     * 注册用于刷新 视频数据的广播
     */
    public void setUpdateReciever() {
        if (receiver == null) {
            IntentFilter inf = new IntentFilter();
            inf.addAction(ZSVideoConst.REFRESH_VIDEO);
            inf.addAction(ZSVideoConst.VIDEO_NET_ACTION);
            inf.setPriority(priority);
            receiver = new VideoUpdateBroadCastRecever();
            mActivity.registerReceiver(receiver, inf);
        }

    }

    public class VideoUpdateBroadCastRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ZSVideoConst.REFRESH_VIDEO)) {
                dealWithBroaCast(intent);
            }
            if (action.equals(ZSVideoConst.VIDEO_NET_ACTION)) {
                String status = intent.getStringExtra(ZSVideoConst.VIDEO_NET_STATUS);
                if (status.equalsIgnoreCase(ZSVideoConst.VIDEO_NET_STATUS_PHONE)) {
                    showNetChangeDialog();
                    abortBroadcast();
                } else if (status.equalsIgnoreCase(ZSVideoConst.VIDEO_NET_STATUS_NO)) {
                    dealWithNoNet();
                }

            }
        }
    }

    private void cancelReciever() {
        try {
            if (receiver != null) {
                mActivity.unregisterReceiver(receiver);
                receiver = null;
            }
        } catch (Exception e) {

        }

    }

    public void onDestroy() {
        mediaPlayRelease();
        cancelReciever();
        cancelHomeListener();
        cancelScreenListener();
        cancle();
    }

    private HomeListener mHomeWatcher;

    public void setHomeListener() {
        if (mHomeWatcher == null) {
            mHomeWatcher = new HomeListener(mActivity);
            mHomeWatcher.setOnHomePressedListener(new HomeListener.OnHomePressedListener() {
                @Override
                public void onHomePressed() {
                    forceStopPlay();
                }

                @Override
                public void onHomeLongPressed() {
                    forceStopPlay();
                }

                @Override
                public void onPowerOffPressed() {
                    forceStopPlay();
                }
            });
            mHomeWatcher.startWatch();
        }

    }

    public void cancelHomeListener() {
        if (mHomeWatcher != null) {
            mHomeWatcher.stopWatch();
            mHomeWatcher = null;
        }
    }

    private ScreenListener screenListener;

    public void setScreenListener() {
        if (screenListener == null) {
            screenListener = new ScreenListener(mActivity);
            screenListener.setScreenStateListener(new ScreenListener.ScreenStateListener() {
                @Override
                public void onScreenOn() {

                }

                @Override
                public void onScreenOff() {
                    forceStopPlay();
                }

                @Override
                public void onUserPresent() {

                }
            });
            screenListener.startWatch();
        }

    }

    public void cancelScreenListener() {
        if (screenListener != null) {
            screenListener.stopWatch();
            screenListener = null;
        }
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static String CANCLE_ALL_ACTION = "cancle_all_action";

    /**
     * 用于取消网络同步的监听
     */
    private class CancelAllBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CANCLE_ALL_ACTION)) {
                cancleAll();
            }
        }
    }

    CancelAllBroadcastReceiver cancleAllReceiver;

    public void sendCancleAll() {
        if (cancleAllReceiver == null) {
            IntentFilter inf = new IntentFilter();
            inf.addAction(CANCLE_ALL_ACTION);
            cancleAllReceiver = new CancelAllBroadcastReceiver();
            mActivity.registerReceiver(cancleAllReceiver, inf);
        }

    }

    private void cancle() {
        if (cancleAllReceiver != null) {
            mActivity.unregisterReceiver(cancleAllReceiver);
            cancleAllReceiver = null;
        }
    }

    private void cancleAll() {
        cancelReciever();
        cancelHomeListener();
        cancelScreenListener();
    }

    public void startPlayForPosition(int position, String url) {
        Intent intent = new Intent();
        intent.setAction(ZSVideoConst.VIDEO_START_PLAY);
        intent.putExtra(ZSVideoConst.VIDEO_START_PLAY_POSITION, position);
        intent.putExtra(ZSVideoConst.VIDEO_START_PLAY_URL, url);
        mActivity.sendOrderedBroadcast(intent, null);
    }
//    public void setScreenLandscape(VideoRender render, final int currentPosition)
//    {
//        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        Rect rect = new Rect();
//        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//        render.getConvertView().setLayoutParams(new AbsListView.
//                LayoutParams(ListView.LayoutParams.MATCH_PARENT,
//                mActivity.getWindowManager().getDefaultDisplay().getHeight() - rect.top));
//        mListView.post(new Runnable() {
//            @Override
//            public void run() {
//                mListView.requestFocusFromTouch();
//                mListView.setSelection(currentPosition);
//            }
//        });
//
//    }
//    public void setScreenPortrait(VideoRender render, final int currentPosition) {
//        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        render.getConvertView().setLayoutParams(new AbsListView.LayoutParams(
//                ListView.LayoutParams.MATCH_PARENT, mActivity.getResources()
//                .getDimensionPixelOffset(R.dimen.space_230)));
//        mListView.post(new Runnable() {
//            @Override
//            public void run() {
//                mListView.requestFocusFromTouch();
//                mListView.setSelection(currentPosition);
//            }
//        });
//    }
}
