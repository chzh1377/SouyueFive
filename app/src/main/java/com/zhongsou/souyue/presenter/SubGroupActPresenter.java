package com.zhongsou.souyue.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;

import com.zhongsou.souyue.activity.SubGroupActivity;
import com.zhongsou.souyue.fragment.EditGroupFragment;
import com.zhongsou.souyue.fragment.SouyueTabFragment;
import com.zhongsou.souyue.module.SubGroupModel;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.net.group.GroupTitleReq;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.SubGroupListView;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;
import com.zhongsou.souyue.utils.IntentUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by zyw on 2016/3/26.
 */
public class SubGroupActPresenter {
    public static final String TAG = SubGroupActPresenter.class.getSimpleName();
    private SubGroupActivity mView;
    private String           title;
    private String           groupId;
    private String           image;
    private boolean hasInitList = false; //列表是否已经初始化
    private boolean isEmptyGroup = false; // 是否空分组
    private boolean hasToRefresh = false; // 是否需要刷新
    private SubGroupListView groupListView;

    public SubGroupActPresenter(SubGroupActivity act) {
        mView = act;
        init(mView.getIntent());
        IntentFilter intentFilter = new IntentFilter(SouyueTabFragment.REFRESH_HOMEGROUP_DATA);
        mView.registerReceiver(refreshReciever, intentFilter);
    }

    private void init(Intent intent) {
        if (!hasInitList) {
            mView.setLoading();
        }
        if (intent != null) {
            title = intent.getStringExtra(SubGroupActivity.INTENT_EXTRA_TITLE);
            groupId = intent.getStringExtra(SubGroupActivity.INTENT_EXTRA_GROUP_ID);
            image = intent.getStringExtra(SubGroupActivity.INTENT_EXTRA_IMAGE);
        }
        if (TextUtils.isEmpty(title)) {
            title = "订阅分组";
        }
        mView.setTitle(title);
        GroupTitleReq.send(HttpCommon.GROUP_TITLE_REQ, new GroupTitleResp(), groupId, "");
        initPagerItem(groupId);
    }

    private void loadTitle(List<SubGroupModel> titles) {
        mView.removeAllIndicator();
        if(titles.size() == 0){
            isEmptyGroup = true;
            mView.setIndicatorState(false);

        }else{
            isEmptyGroup = false;
            for (int x = 0; x < titles.size(); x++) {
                mView.addIndicator(titles.get(x));
            }
            mView.setIndicatorState(true);
        }
        if(groupListView != null){
            groupListView.setListNoData(titles.size() == 0);
        }
        mView.removeLoading();
    }

    private void initPagerItem(String groupId) {
        if (!hasInitList) {
            groupListView = new SubGroupListView(mView, groupId, title, image);
            mView.addViewToContainer(groupListView);
            hasInitList = true;
        }
    }

    public boolean isHasToRefresh() {
        return hasToRefresh;
    }

    public void setHasToRefresh(boolean hasToRefresh) {
        this.hasToRefresh = hasToRefresh;
    }

    public void loadData(Intent intent) {
        try {
            if(hasToRefresh){
                init(intent);
                groupListView.startFresh();
                hasToRefresh = false;
            }
        }catch (Exception e){

        }
    }

    public void bindListener(View v) {
        v.setOnClickListener(new TabClickListener());
    }


    class TabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            hasToRefresh = true;
            SubGroupModel model = (SubGroupModel) v.getTag();
            int           type  = 0;
            BaseInvoke    dest  = new BaseInvoke();
            if ("srp".equals(model.getCategory())) {
                type = model.getInvokeType() > 0 ? model.getInvokeType() : BaseInvoke.INVOKE_TYPE_SRP_INDEX;
            } else if ("interest".equals(model.getCategory())) {
                type = BaseInvoke.INVOKE_TYPE_INTEREST_INDEX;
                dest.setInterestName(model.getTitle());
            }
            dest.setType(type);
            dest.setSrpId(model.getSrpId());
            dest.setKeyword(model.getKeyword());
            dest.setIconUrl(model.getImage());
            if (type == BaseInvoke.INVOKE_TYPE_FOCUSNEWS) {
                IntentUtil.gotoSouYueYaoWen(mView, model.getTitle());
            } else {
                HomePagerSkipUtils.skip(mView, dest);
            }
        }
    }

    class GroupTitleResp implements IVolleyResponse {

        @Override
        public void onHttpResponse(IRequest request) {
            List<SubGroupModel> titles = request.getResponse();
            if (titles != null) {
                loadTitle(titles);
            }
        }

        @Override
        public void onHttpError(IRequest request) {

        }

        @Override
        public void onHttpStart(IRequest request) {

        }
    }

    private BroadcastReceiver refreshReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SouyueTabFragment.REFRESH_HOMEGROUP_DATA)) {
                //刷新分组
                init(intent);
            }
        }
    };

    public View.OnClickListener goGroupEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hasToRefresh = true;
            IntentUtil.gotoSubGroupEdit(mView, groupId, title, EditGroupFragment.EDIT_GROUP, image);
        }
    };

    public void onDestroy() {
        try {
            mView.unregisterReceiver(refreshReciever);
        } catch (Exception e) {

        }
    }

    public boolean isEmptyGroup(){
        return isEmptyGroup;
    }
}
