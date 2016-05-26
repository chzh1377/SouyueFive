package com.zhongsou.souyue.circle.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.adapter.InterestAdapter;
import com.zhongsou.souyue.circle.model.Interest;
import com.zhongsou.souyue.circle.model.InterestGroup;
import com.zhongsou.souyue.circle.util.JSONUtils;
import com.zhongsou.souyue.fragment.SubscribeListBaseFragment;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.sub.SubLeftRequest;
import com.zhongsou.souyue.net.sub.SubRightRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YanBin
 * @version V5.2.0
 * @project trunk
 * @Description 订阅大全，搜悦5.2.0版本
 * @date 2016/3/26
 */
public class SubscribeAllListFragment extends SubscribeListBaseFragment implements IVolleyResponse, View.OnClickListener {

    private Activity activity;
    private ListView leftListView, rightListView;
    private View rootView;
    private int currentPos = 0;
    private InterestGroupAdapter groupAdapter;
    private InterestAdapter interestAdapter;
    private long groupId;
    private List<InterestGroup> groups = new ArrayList<InterestGroup>();

    @Override
    public String getIndicatorTitle() {
        return "添加订阅";
    }

    public SubscribeAllListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_subscript_add, null);
        leftListView = (ListView) rootView.findViewById(R.id.lv_category_list);
        //增加一个Header作为占位
        rightListView = (ListView) rootView.findViewById(R.id.lv_newsource_subscribe);
        groupAdapter = new InterestGroupAdapter();
        leftListView.setAdapter(groupAdapter);
        interestAdapter = new InterestAdapter(activity);
        rightListView.setAdapter(interestAdapter);
        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (currentPos == position) {
                    return;
                }
                currentPos = position;
                groupAdapter.setSelectedPosition(position);
                interestAdapter.clearDatas();
                groupId = groups.get(position).getGroup_id();
                sendRightRequest();
                groupAdapter.notifyDataSetChanged();

            }
        });

        rightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                interestAdapter.onClickOther(position);
            }
        });

        View search = rootView.findViewById(R.id.sub_add_search);
        search.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendLeftRequest();

        pbHelp.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {

            @Override
            public void clickRefresh() {
                sendLeftRequest();
            }
        });

    }

    public void sendLeftRequest() {
        SubLeftRequest.send(HttpCommon.SUBER_ALL_INTEREST_GROUP_ACTION, this);
    }

    /**
     * 右侧请求
     */
    public void sendRightRequest() {
        if (groupId <= 0) return;
        SubRightRequest request = new SubRightRequest(HttpCommon.SUBER_ALL_INTEREST_CHILD_ACTION, this);
        request.setParams(String.valueOf(groupId), SYUserManager.getInstance().getUserId());
        CMainHttp.getInstance().doRequest(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        sendRightRequest();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.sub_add_search:
                IntentUtil.gotoSearchAll(getActivity(), this.getClass().getSimpleName());
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {    //不隐藏时
            sendRightRequest();
        }
    }


    class InterestGroupAdapter extends BaseAdapter {
        private int selectedPosition;

        @Override
        public int getCount() {
            return groups.size();
        }

        @Override
        public Object getItem(int position) {
            return groups.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(activity, R.layout.subscribe_category_list, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.tv_left);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(StringUtils.truncate(groups.get(position).getGroup_name().trim(), StringUtils.LENGTH_12));
            if (selectedPosition == position) {
                holder.text.setBackgroundColor(Color.parseColor("#f8f8f8"));
                holder.text.setTextColor(Color.parseColor("#da4644"));
            } else {
                holder.text.setBackgroundColor(Color.parseColor("#f2f2f2"));
                holder.text.setTextColor(Color.parseColor("#282828"));
            }

            return convertView;
        }

        private class ViewHolder {
            TextView text;
        }

        public void setSelectedPosition(int position) {
            if (position == -1 || position == groups.size()) {
                selectedPosition = 0;
            } else {
                selectedPosition = position;
            }
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        pbHelp.showNetError();
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        HttpJsonResponse response = _request.getResponse();
        switch (_request.getmId()) {
            case HttpCommon.SUBER_ALL_INTEREST_GROUP_ACTION:
                groups = JSONUtils.fromJsonArray(response.getBodyArray(), new TypeToken<List<InterestGroup>>() {
                }.getType());
                if (groups.size() > 0) {
                    groupId = groups.get(0).getGroup_id();
                    groupAdapter.notifyDataSetChanged();
                    sendRightRequest();
                    pbHelp.goneLoading();
                } else {
                    pbHelp.showNoData();
                }
                break;

            case HttpCommon.SUBER_ALL_INTEREST_CHILD_ACTION:    //此处有问题，可能出现错位显示问题
                List<Interest> interests = JSONUtils.fromJsonArray(response.getBodyArray(), new TypeToken<List<Interest>>() {
                }.getType());
                if (interests != null && interests.size() > 0)
                    interestAdapter.setDatas(interests);
                break;
            default:
        }
    }

    @Override
    public void onHttpStart(IRequest _request) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CMainHttp.getInstance().cancel(this);
    }
}
