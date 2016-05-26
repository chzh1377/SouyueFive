package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MySubscribeListActivity;
import com.zhongsou.souyue.adapter.GridDynamicAdapter;
import com.zhongsou.souyue.adapter.GridDynamicAdapter.DeleteListener;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.CustomProgress;
import com.zhongsou.souyue.circle.util.JSONUtils;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.db.DaoFactory;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.module.AGridDynamic;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.sub.GroupDeleteReq;
import com.zhongsou.souyue.net.sub.SubListInfoRequest;
import com.zhongsou.souyue.net.sub.SubOrderRequest;
import com.zhongsou.souyue.net.sub.SubTipsRequest;
import com.zhongsou.souyue.net.sub.SubUpdateRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.dynamicgrid.DynamicGridEdit;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的订阅Fragment
 */

public class ManagerGridFragment extends BaseFragment implements
        DeleteListener, OnClickListener {

    private DynamicGridEdit gridView;
    private GridDynamicAdapter adapter;
    private boolean isFirst = true;

    private String token;
    private Activity context;
    private AGridDynamic currentOj;

    private RelativeLayout re_tips;
    private TextView tv_sub_tip;
    private int auditing_count;
    private int refused_count;
    private EditEnable mEditEnable;
    private SYSharedPreferences sysp = SYSharedPreferences.getInstance();
    //    protected CSuberListHttp mVolleyHttp;
    private View rlContainer, ivTipDelete;
    private CustomProgress netdialog;
    private List<SuberedItemInfo> suberedItemInfos = new ArrayList<SuberedItemInfo>();
    private SuberDao dao = DaoFactory.createDao();
    private SuberedItemInfo item;
    private boolean isFinish = false;
    private static final String INTEST = "interest";
    private static final String SRP = "srp";
    private View loadingView;
    protected ProgressBarHelper progress;
    private CMainHttp mainHttp;

    public interface EditEnable {
        void setEditEnable(boolean _isEdit);
    }

    public static ManagerGridFragment newInstance(Bundle bundle) {
        ManagerGridFragment fragment = new ManagerGridFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public ManagerGridFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mVolleyHttp = new CSuberListHttp(getActivity());
        mainHttp = CMainHttp.getInstance();
        token = SYUserManager.getInstance().getToken();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(context, R.layout.manager_fragment_grid, null);
        doInitView(view);
        doInitItemClick(view);
        suberedItemInfos = dao.queryAll();
        doNotify(suberedItemInfos, true);
        doInitData();   //访问网络
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sysp.getBoolean(SYSharedPreferences.KEY_REGISTERSUCCESS, false)
                || sysp.getBoolean(SYSharedPreferences.KEY_UPDATE, false)
                || sysp.getBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, false)) {
            sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
            sysp.remove(SYSharedPreferences.KEY_REGISTERSUCCESS);
            sysp.remove(SYSharedPreferences.KEY_UPDATE);
        }
        initFromDb();

        gridView.setText();
        if (sysp.getReplay()) {
            getApplyTips();
            sysp.setReplay(false);
        }
    }

    private void initFromDb() {
        suberedItemInfos = dao.queryAll();
        doNotify(suberedItemInfos, false);
    }

    public void setText() {
        if (gridView != null) {
            gridView.setText();
        }
    }

    /**
     * 初始化view
     */
    private void doInitView(View view) {

        gridView = (DynamicGridEdit) view.findViewById(R.id.dynamic_edit);
        ivTipDelete = view.findViewById(R.id.iv_tips_delete);   //提示的关闭按钮
        rlContainer = view.findViewById(R.id.rl_container);     //提示条

        re_tips = (RelativeLayout) view.findViewById(R.id.re_tips);
        re_tips.setOnClickListener(this);
        tv_sub_tip = (TextView) view.findViewById(R.id.tv_sub_tip);

        loadingView = view.findViewById(R.id.ll_data_loading);
        progress = new ProgressBarHelper(context, loadingView);

        if (SYSharedPreferences.getInstance().getTipShow(context)) {
            rlContainer.setVisibility(View.GONE);
        }

        gridView.init();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    private void doInitItemClick(View view) {
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                startEditItem();
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = parent.getAdapter().getItem(position);
                SuberedItemInfo info = (SuberedItemInfo) o;

                start2Home(info);
            }
        });

        progress.setProgressBarClickListener(new ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                doSetDataByNet();
            }
        });

        ivTipDelete.setOnClickListener(this);

        gridView.setEditEnable(new DynamicGridEdit.EditEnable() {
            @Override
            public void setEditEnable(boolean _isEdit) {
                if (mEditEnable != null) {
                    mEditEnable.setEditEnable(_isEdit);
                }
            }
        });

        view.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (gridView != null && gridView.isEditMode()) {
                        doSave();
                        return true;
                    }

                }
                return false;
            }
        });

        gridView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case OnScrollListener.SCROLL_STATE_IDLE:
                        if (adapter != null) {
                            adapter.resume();
                        }

                        break;
                    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        if (adapter != null) {
                            adapter.pause();
                        }

                        break;
                    case OnScrollListener.SCROLL_STATE_FLING:
                        if (adapter != null) {
                            adapter.pause();
                        }
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
    }


    @Override
    public void onHttpStart(IRequest _request) {
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        final HttpJsonResponse response = (HttpJsonResponse) _request.getResponse();

        switch (_request.getmId()) {
//            case HttpCommon.SUBER_UPDATE_METHOD: // 删除 TODO ??? 有这个请求吗？
//                sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
//                onStatistics();
//                dao.clearOne(item);
//                break;
            case HttpCommon.SUB_ADDDEL_REQUEST:
                try {
                    response.getBodyInt();//这句代码检测是否返回的是数字，如果是数字就不会抛异常
                    sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
                    onStatistics();
                    dao.clearOne(item);
                } catch (Exception e) {
                    int code = response.getBody().get("result").getAsInt();
                    if (code == 200) {
                        if (gridView.getmAdapter().getCount() == 0) {
                            rlContainer.setVisibility(View.GONE);
                        }
                        sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
                        onStatistics();
                        dao.clearOne(item);
                    } else if (code == 500) {
                        SouYueToast.makeText(getActivity(), R.string.subscibe_delete_fail, Toast.LENGTH_LONG).show();
                    } else if (code == 501) {
                        SouYueToast.makeText(getActivity(), R.string.cricle_admin_no_quit_setting_text, Toast.LENGTH_LONG).show();
                    }
                }

                break;

            case HttpCommon.SUB_LIST_REQUEST: // 我的订阅
                gone();
                suberedItemInfos = JSONUtils.fromJsonArray(response.getBodyArray(), new TypeToken<List<SuberedItemInfo>>() {
                }.getType());
                doNotify(suberedItemInfos, false);
                dao.updateDb(suberedItemInfos);
                //showWindow();

                break;

            case HttpCommon.SUB_MODIFY_REQUEST: // 排序成功
                gone();
                dealBack(true);
                // Toast.makeText(context,"排序成功",Toast.LENGTH_LONG).show();
                sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
                isFinish = false;
                break;

            case HttpCommon.SUB_TIPS_REQUEST:
                auditing_count = response.getBody().get("auditing_count").getAsInt();
                refused_count = response.getBody().get("refused_count").getAsInt();
                if (auditing_count != 0 || refused_count != 0) {
                    re_tips.setVisibility(View.VISIBLE);
                    if (auditing_count != 0 && refused_count == 0)
                        tv_sub_tip.setText(String.format("你有%d个审核中的申请，点击查看！",
                                auditing_count));
                    if (auditing_count == 0 && refused_count != 0)
                        tv_sub_tip.setText(String.format("你有%d个被拒绝的申请，点击查看！",
                                refused_count));
                    if (auditing_count != 0 && refused_count != 0)
                        tv_sub_tip.setText(String.format("你有%d个审核中的申请，%d个被拒绝的申请，点击查看！",
                                auditing_count, refused_count));
                } else {
                    re_tips.setVisibility(View.GONE);
                }
                break;
            case HttpCommon.GROUP_DELETE_REQ:
                dao.clearOne(item);

                List<SuberedItemInfo> deleteGroupList = JSONUtils.fromJsonArray(response.getBodyArray(), new TypeToken<List<SuberedItemInfo>>() {
                }.getType());
                adapter.addData(deleteGroupList);
                gridView.setAdapter(adapter);
                goneLoad();

                sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
                dao.updateDb(suberedItemInfos);
                break;
            default:
                break;
        }
    }

    private void showWindow() {
        if (isFirst && context != null) {
            context.findViewById(R.id.tv_hotrecommed).performClick();
            isFirst = false;
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        switch (_request.getmId()) {
            case HttpCommon.SUB_ADDDEL_REQUEST: // 删除
                Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
                break;

            case HttpCommon.SUB_LIST_REQUEST:
                gone();
//                if (suberedItemInfos.size() == 0) {
//                    showNetError();
//                } else if (suberedItemInfos.size() > 0) {
//                    goneLoad();
//                }
                break;

            case HttpCommon.SUB_MODIFY_REQUEST:
                gone();
                Toast.makeText(context, "排序失败", Toast.LENGTH_LONG).show();
                dealBack(false);
                break;
            case HttpCommon.GROUP_DELETE_REQ: // 删除
                Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    public void dealBack(boolean success) {
        if (success) {
            dao.updateDb(getDb());
        }
        if (isFinish) {
            List<HomeBallBean> balls = getHomeBallBeans();
            Intent intent = new Intent();
            intent.putExtra("balls", (Serializable) balls);
            if (success) {
                context.setResult(context.RESULT_OK, intent);
            } else {
                context.setResult(context.RESULT_CANCELED, intent);
            }
            context.finish();
            context.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    private void show() {
        netdialog = new CustomProgress(context, "", false, null);
        if (netdialog != null && !netdialog.isShowing()) {
            netdialog.show();
        }
    }

    private void gone() {
        if (netdialog != null && netdialog.isShowing()) {
            netdialog.dismiss();
            netdialog = null;
        }
    }

    private void goneLoad() {
        if (progress != null)
            progress.goneLoading();
    }

    private void showLoad() {
        if (progress != null)
            progress.showLoading();
    }

    private void showNetError() {
        if (progress != null)
            progress.showNetError();
    }

    private void showNoData() {
        if (progress != null)
            progress.showNoData();
    }

    @Override
    public void removeItem(int position) {
        if (adapter != null) {
            currentOj = adapter.getItem(position);
            removeAndShow(currentOj);
        }
    }

    public void removeItem(Object object) {
        SuberedItemInfo item = ((SuberedItemInfo) object);// 取消订阅
        adapter.removeData(item);
        gridView.setText();
        adapter.notifyDataSetChanged();
    }

    private void removeAndShow(Object oj) {
        item = ((SuberedItemInfo) oj);// 取消订阅
        if (HomeBallBean.GROUP_NEWS.equalsIgnoreCase(item.getCategory()))// 删除组
        {
            delelteGroup();
        } else {
            SubUpdateRequest request = new SubUpdateRequest(HttpCommon.SUB_ADDDEL_REQUEST, this);
            request.setTag(this);
            request.addParams(SYUserManager.getInstance().getToken(), item.getSrpId(), "del", item.getCategory(), item.getId() + "", item.getKeyword());
            mainHttp.doRequest(request);
        }

        removeItemByLocation();
    }


    private void delelteGroup() {
        GroupDeleteReq req = new GroupDeleteReq(HttpCommon.GROUP_DELETE_REQ, this);
        req.setParams(item.getId() + "");
        mainHttp.doRequest(req);
    }

    private void removeItemByLocation() {
        if (adapter != null) {
            currentOj.setmState(AGridDynamic.STATE_DELETE);
            adapter.removeData(currentOj);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化adapter
     */
    public void doInitData() {
        doSetDataByNet();
    }

    /**
     * 网络获取数据
     */
    private void doSetDataByNet() {
        if (suberedItemInfos != null && suberedItemInfos.size() == 0) {
            showLoad();
        }
        getApplyTips();
        getSuberedInfos();
//        getDb();
    }

    public void dealWithNet() {
        show();
        getSuberedInfos();

        Intent intentGotoBall = new Intent();
        intentGotoBall.setAction(SouyueTabFragment.REFRESH_HOMEBALL_FROMCACHE);
        context.sendBroadcast(intentGotoBall);
    }

    /**
     * 获取已经订阅列表 - 访问网络
     */

    public void getSuberedInfos() {
        SubListInfoRequest listRequest = new SubListInfoRequest(HttpCommon.SUB_LIST_REQUEST, this);
        listRequest.addParams(SYUserManager.getInstance().getToken());
        listRequest.setTag(this);
        mainHttp.doRequest(listRequest);
    }

    /**
     * 获取提示
     */
    public void getApplyTips() {
        SubTipsRequest request = new SubTipsRequest(HttpCommon.SUB_TIPS_REQUEST, this);
        request.setParams(token);
        mainHttp.doRequest(request);
    }

    /**
     * 更新列表数据
     *
     * @param items
     */
    private void doNotify(List<?> items, boolean comeDb) {
        if ((items == null || items.size() <= 0) && !comeDb) {
            rlContainer.setVisibility(View.GONE);
            showNoData();
            return;
        } else if (items.size() > 0) {
            goneLoad();
            if (adapter == null) {
                adapter = new GridDynamicAdapter(context, items);
                adapter.setDeleteListener(this);
            } else {
                adapter.setData(items);
            }
            gridView.setAdapter(adapter);
        }
    }

    public void addNotify(SuberedItemInfo item, boolean isPre) {
        if (adapter == null) {
            goneLoad();
            adapter = new GridDynamicAdapter(context, item);
            adapter.setDeleteListener(this);
            gridView.setAdapter(adapter);
        } else {
            adapter.addOneItem(item, isPre);
        }
        if (gridView != null) {
            gridView.setText();
        }
    }

    /**
     * 编辑状态
     */
    private void startEditItem() {
//        getActivity().findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
        if (adapter != null) {
            adapter.setShowDelView(true);
            adapter.notifyDataSetChanged();
        }
        gridView.startEditMode();
        ((MySubscribeListActivity)getActivity()).setOnEdit(true);
    }

    /**
     * 跳转到首页
     *
     * @param item
     */
    private void start2Home(SuberedItemInfo item) {
        if (item != null) {
            String category = item.getCategory();
            int invokeType = item.getInvokeType();
            if (invokeType == BaseInvoke.INVOKE_TYPE_FOCUSNEWS) {
                IntentUtil.gotoSouYueYaoWen(getActivity(), item.getTitle());
                return;
            }

            if ("interest".equals(category)) {
                UIHelper.showCircleIndex(context, item.getSrpId(), item.getKeyword(), item.getTitle(), item.getImage(), "MySubscribeListActivity");
            } else if ("rss".equals(category)) {
                String url = UrlConfig.getSouyueHost() + "webdata/search.result.groovy?mode=3&rssId=" + item.getId();
                item.setUrl(url);
                IntentUtil.gotoSouYueRss(context, item);
            } else if ("srp".equals(category)) {
                IntentUtil.gotoSouYueSRPAndFinish(context, item.getKeyword(), item.getSrpId(), item.getImage());
            } else if ("special".equals(category)) {
                IntentUtil.gotoSouYueSRPAndFinish(context, item.getKeyword(), item.getSrpId(), item.getImage());
            } else if (HomeBallBean.GROUP_NEWS.equals(category)) {
//                IntentUtil.gotoSubGroupEdit(context, item.getId()+"", item.getTitle()+"", EditGroupFragment.EDIT_GROUP);
                IntentUtil.gotoSubGroupHome(context, item.getId() + "", item.getTitle() + "", item.getImage());
            }
        }
    }

    /**
     * 热门推荐统计功能
     */
    private void onStatistics() {
        if (item != null) {
            if (INTEST.equals(item.getCategory())) {
                UpEventAgent.onGroupQuit(context, item.getId() + "." + "", "");
            } else if (SRP.equals(item.getCategory())) {
                UpEventAgent.onSrpUnsubscribe(context, item.getKeyword(), item.getSrpId());

            }
        }
    }

    public boolean exit2Save(boolean finish) {
        this.isFinish = finish;
        if (gridView != null && gridView.isEditMode()) {
            doSave();
            return true;
        } else {
            if (finish) {
                context.finish();
                context.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
            return false;
        }
    }

    public List<HomeBallBean> getHomeBallBeans() {
        List<HomeBallBean> balls = new ArrayList<HomeBallBean>();
        if (gridView != null && adapter != null && adapter.getItems() != null) {
            for (Object obj : adapter.getItems()) {
                SuberedItemInfo info = (SuberedItemInfo) obj;
                HomeBallBean ball = new HomeBallBean();
                ball.setId(info.getId());
                String category = info.getCategory();
                if ("rss".equals(category)) {
                    continue;
                }
                ball.setCategory(category);
                ball.setImage(info.getImage());
                ball.setKeyword(info.getKeyword());
                ball.setSrpId(info.getSrpId());
                ball.setImage(info.getImage());
                ball.setTitle(info.getTitle());
                ball.setUrl(info.getUrl());
                balls.add(ball);
            }
        }
        return balls;
    }

    private List<SuberedItemInfo> getDb() {
        List<SuberedItemInfo> infos = new ArrayList<SuberedItemInfo>();
        if (gridView != null && adapter != null && adapter.getItems() != null) {
            for (Object obj : adapter.getItems()) {
                SuberedItemInfo info = (SuberedItemInfo) obj;
                infos.add(info);
            }
        }
        return infos;
    }

    /**
     * 保存
     */
    public void doSave() {
        stopEditItem();
        saveMove();
    }

    /**
     * 停止编辑
     */
    public void stopEditItem() {
        if (gridView.isEditMode()) {
            gridView.stopEditMode();
//            getActivity().findViewById(R.id.btn_save).setVisibility(View.GONE);
            if (adapter != null) {
                adapter.setShowDelView(false);
                adapter.notifyDataSetChanged();
            }
            ((MySubscribeListActivity)getActivity()).setOnEdit(false);
        }
    }

    /**
     * 保存排序
     */
    public void saveMove() {
        JSONArray array = new JSONArray();
        if (adapter != null) {
            for (Object obj : adapter.getItems()) {
                SuberedItemInfo item = (SuberedItemInfo) obj;
                String category = item.getCategory();
                String id = item.getId() + "";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", category);
                    jsonObject.put("id", id);
                    array.put(jsonObject);
                } catch (Exception e) {
                }

            }
        }
        onMovePost(array.toString());
    }

    /**
     * 移动后保存
     */
    private void onMovePost(String json) {
        show();
        SubOrderRequest orderRequest = new SubOrderRequest(HttpCommon.SUB_MODIFY_REQUEST, this);
        orderRequest.addParams(json);
        mainHttp.doRequest(orderRequest);
        UpEventAgent.onZSSubscribeManage(getActivity());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){    //不隐藏时
            initFromDb();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            exit2Save(false);
        } else {
            ((MySubscribeListActivity) context).setCurrentDisplayFragment(this);
//            initFromDb();   //访问数据库 刷新数据
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {

            case R.id.re_tips:
                sysp.setReplay(true);
                IntentUtil.gotoCircleCheckRecordActivity(context);
                break;

            case R.id.iv_tips_delete:
                SYSharedPreferences.getInstance().putTipShow(context, true);
                rlContainer.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            context = activity;
            this.mEditEnable = (EditEnable) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement EditEnable");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}