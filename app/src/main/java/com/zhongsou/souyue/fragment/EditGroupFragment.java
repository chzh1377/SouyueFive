package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SubGroupEditActivity;
import com.zhongsou.souyue.adapter.GroupEditAdapter;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.CustomProgress;
import com.zhongsou.souyue.circle.util.JSONUtils;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.db.DaoFactory;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.module.AGridDynamic;
import com.zhongsou.souyue.module.GroupSelect;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.sub.GroupGetInfoReq;
import com.zhongsou.souyue.net.sub.SubOrderRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 创建和编辑分组的 fragment
 * @auther: qubian
 * @data: 2016/4/1.
 */
public class EditGroupFragment extends BaseFragment implements View.OnClickListener {
    public static final int CREATE_GROUP = 1;
    public static final int EDIT_GROUP = 2;
    private GridView gridView;
    private GroupEditAdapter groupEditAdapter;
    private boolean isFirst = true;
    private Activity context;
    private AGridDynamic currentOj;
    private SYSharedPreferences sysp = SYSharedPreferences.getInstance();
    private CustomProgress netdialog;
    private List<SuberedItemInfo> suberedItemInfos;
    private List<GroupSelect> selectList;

    private SuberDao dao = DaoFactory.createDao();
    private SuberedItemInfo item;
    private boolean isFinish = false;
    private static final String INTEST = "interest";
    private static final String SRP = "srp";
    private View loadingView;
    protected ProgressBarHelper progress;
    private int C_E_tag; //创建和编辑  1:创建 2 编辑
    private String groupId;
    private String title;
    public boolean isChanged =false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(context, R.layout.fragment_editgroup, null);
        initView(view);
        initListener(view);
        initData();
        return view;
    }

    /**
     * 从网络或者本地数据库 去数据
     */
    private void initData() {
        suberedItemInfos = new ArrayList<SuberedItemInfo>();
        C_E_tag = getArguments().getInt("C_E_tag");
        suberedItemInfos = dao.querySomeNotGroup();
        if (C_E_tag == 1) {
            selectList = GroupSelect.createList(suberedItemInfos.size());
            notifyGridView(suberedItemInfos, true);
        } else {
            groupId = getArguments().getString("groupId");
            getDataFormNet(groupId);
        }
    }

    private void getGroupChildInfo(String groupId) {
        GroupGetInfoReq req = new GroupGetInfoReq(HttpCommon.GROUP_GET_CHILD_REQ, this);
        req.setParams(groupId);
        CMainHttp.getInstance().doRequest(req);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (sysp.getBoolean(SYSharedPreferences.KEY_REGISTERSUCCESS, false)
//                || sysp.getBoolean(SYSharedPreferences.KEY_UPDATE, false)
//                || sysp.getBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, false)) {
//            sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
//            sysp.remove(SYSharedPreferences.KEY_REGISTERSUCCESS);
//            sysp.remove(SYSharedPreferences.KEY_UPDATE);
//            initFromDb();
//        }
    }

    public static EditGroupFragment newInstance(Bundle bundle) {
        EditGroupFragment fragment = new EditGroupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public EditGroupFragment() {
        super();
    }


    private void initFromDb() {
//        suberedItemInfos = dao.queryAll();
        suberedItemInfos = dao.querySomeNotGroup();
        notifyGridView(suberedItemInfos, false);
    }


    /**
     * 初始化view
     */
    private void initView(View view) {
        gridView = (GridView) view.findViewById(R.id.gridView);
        loadingView = view.findViewById(R.id.ll_data_loading);
        progress = new ProgressBarHelper(context, loadingView);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    private void initListener(View view) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                GroupSelect select = selectList.get(position);
                select.setIsSelect(!select.isSelect());
                groupEditAdapter.setmSelectList(selectList);
                groupEditAdapter.notifyDataSetChanged();

                if (!isChanged && (getActivity() instanceof SubGroupEditActivity) &&
                        C_E_tag == CREATE_GROUP) {
                    SuberedItemInfo info = suberedItemInfos.get(position);
                    String title = ((SubGroupEditActivity) getActivity()).getEditText();
                    if (StringUtils.isEmpty(title)) {
                        ((SubGroupEditActivity) getActivity()).setEditText(info.getTitle() + getString(R.string.group));
                    }
                    if(StringUtils.isEmpty(((SubGroupEditActivity) getActivity()).getGroupImageUrl()))
                    {
                        ((SubGroupEditActivity) getActivity()).setGroupImageUrl(info.getImage());
                    }
                }
                isChanged = true;
            }
        });

        if(C_E_tag == EDIT_GROUP)
        {
            progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
                @Override
                public void clickRefresh() {
                    getDataFormNet(groupId);
                }
            });
        }


//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    if (gridView != null) {
////                        doSave();
//                        return true;
//                    }
//
//                }
//                return false;
//            }
//        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        if (groupEditAdapter != null) {
                            groupEditAdapter.resume();
                        }

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        if (groupEditAdapter != null) {
                            groupEditAdapter.pause();
                        }

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        if (groupEditAdapter != null) {
                            groupEditAdapter.pause();
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
    public void onHttpResponse(IRequest _request) {
        switch (_request.getmId()) {
            case HttpCommon.GROUP_GET_CHILD_REQ:
                goneLoad();
                List<SuberedItemInfo> groups = _request.getResponse();
                selectList = new ArrayList<>();
                if(groups!=null && groups.size()>0)
                {
                    suberedItemInfos.addAll(0,groups);
                }
                int subSize= suberedItemInfos.size();
                int gSize= groups.size();
                for (int i=0 ;i<subSize;i++)
                {
                    GroupSelect s= new GroupSelect();
                    s.setIsSelect(i<gSize?true:false);
                    selectList.add(s);
                }

//                for(SuberedItemInfo info:suberedItemInfos)
//                {
//                    GroupSelect s= new GroupSelect();
//                    s.setIsSelect(false);
//                    for(SuberedItemInfo result:groups)
//                    {
//                        if(result.getId() ==info.getId())
//                        {
//                            s.setIsSelect(true);
//                            continue;
//                        }
//                    }
//                    selectList.add(s);
//                }
                notifyGridView(suberedItemInfos, false);
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
        HttpJsonResponse response = null;
        switch (_request.getmId()) {
            case HttpCommon.GROUP_GET_CHILD_REQ:
                showNetError();
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

    public void goneLoad() {
        if (progress != null)
            progress.goneLoading();
    }

    public void showLoad() {
        if (progress != null)
            progress.showLoading();
    }


    public void showNetError() {
        if (progress != null)
            progress.showNetError();
    }

    private void showNoData() {
        if (progress != null)
            progress.showNoData();
    }


    /**
     * 网络获取数据
     */
    private void getDataFormNet(String groupId) {
        if (suberedItemInfos != null && suberedItemInfos.size() == 0) {
            showLoad();
        }
        getGroupChildInfo(groupId);
    }

    /**
     * 更新列表数据
     *
     * @param items
     */
    private void notifyGridView(List<?> items, boolean comeDb) {
        if (items == null || (items.size() <= 0 && !comeDb)) {
            showNoData();
            return;
        } else if (items.size() > 0) {
            goneLoad();
            if (groupEditAdapter == null) {
                groupEditAdapter = new GroupEditAdapter(context, items);
            } else {
                groupEditAdapter.setData(items);
            }
            groupEditAdapter.setmSelectList(selectList);
            gridView.setAdapter(groupEditAdapter);
        }if(comeDb&&items.size() <= 0)
        {
            showNoData();
        }
    }

    /**
     * 编辑状态
     */
    private void startEditItem() {
//        getActivity().findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
        if (groupEditAdapter != null) {
//            groupEditAdapter.setShowDelView(true);
            groupEditAdapter.notifyDataSetChanged();
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
        if (gridView != null) {
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
        if (gridView != null && groupEditAdapter != null && groupEditAdapter.getItems() != null) {
            for (Object obj : groupEditAdapter.getItems()) {
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
        if (gridView != null && groupEditAdapter != null && groupEditAdapter.getItems() != null) {
            for (Object obj : groupEditAdapter.getItems()) {
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
        saveMove();
    }

    /**
     * 保存排序
     */
    public void saveMove() {
        JSONArray array = new JSONArray();
        if (groupEditAdapter != null) {
            for (Object obj : groupEditAdapter.getItems()) {
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
        CMainHttp.getInstance().doRequest(orderRequest);
        UpEventAgent.onZSSubscribeManage(getActivity());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (!isVisibleToUser) {
//            exit2Save(false);
//        } else {
//            ((MySubscribeListActivity) context).setCurrentDisplayFragment(this);
//        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {

            default:
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            context = activity;
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

    public String getListString()
    {
        if(selectList!=null&&suberedItemInfos!=null)
        {
            List<GroupMember> groupmenbers =new ArrayList<>();
            int listSize= selectList.size();
            for (int i =0 ; i<listSize;i++)
            {
                GroupSelect select = selectList.get(i);
                if(select.isSelect())
                {
                    SuberedItemInfo info =suberedItemInfos.get(i);
                    GroupMember men = new GroupMember();
                    men.setCategory(info.getCategory());
                    men.setId(info.getId());
                    groupmenbers.add(men);
                }
            }
            return new Gson().toJson(groupmenbers);
        }
        return "";
    }
    class GroupMember implements DontObfuscateInterface
    {
        private String category;
        private long id;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}