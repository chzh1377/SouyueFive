package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.KeywordSubscribeActivity;
import com.zhongsou.souyue.dialog.SubscribeDialog;
import com.zhongsou.souyue.module.CateTree;
import com.zhongsou.souyue.module.DELParam;
import com.zhongsou.souyue.module.SRP;
import com.zhongsou.souyue.module.SRPParam;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.sub.SubCateRssRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅大全 - 主题 Fragment
 */
@SuppressLint("ValidFragment")
public class SubscribeListFragment extends SubscribeListBaseFragment implements
        IVolleyResponse {
    private Activity activity;
    private ListView leftListView, rightListView;
    private SubscribeChildCategoryAdapter childAdapter;
    private SubscribeCategoryAdapter categoryAdapter;
    public List<CateTree> cateTrees;
    public List<CateTree> childcateTrees;
    private ArrayList<SRPParam> srpparamdatalist;
    private SRPParam srpparam;
    private DELParam delparam;
    private ArrayList<DELParam> deldataList;
    private int index;
    private int groupId;
    public boolean subscribeFlag;
    private int rightposition;
    // 本次操作是订阅还是取消订阅
    private boolean hasSubscribed;
    private SubscribeDialog subscribedialog;
    private CMainHttp mainHttp;

    @Override
    public String getIndicatorTitle() {
        return "主题";
    }

    public SubscribeListFragment(Activity activity) {
        this.activity = activity;
    }

    public SubscribeListFragment() {
    }

    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //if (categoryAdapter.getCount() == 0)
        mainHttp = CMainHttp.getInstance();
        sendRequest();
        pbHelp.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {

            @Override
            public void clickRefresh() {
                sendRequest();
            }
        });

    }

    public void sendRequest() {
        if (!CMainHttp.getInstance().isNetworkAvailable(getActivity())) {
            pbHelp.showNetError();
            return;
        }
        SubCateRssRequest rssRequest = new SubCateRssRequest(HttpCommon.SUB_CATETREE_REQUEST, this);
        mainHttp.doRequest(rssRequest);
        // http.cateTree30();
    }

    public void sendRightRequest() {
        // http.cateTree30S(groupId + "");
        SubCateRssRequest rssRequest = new SubCateRssRequest(HttpCommon.SUB_CATETREE2_REQUEST, this);
        rssRequest.addParameters(groupId + "");
        mainHttp.doRequest(rssRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View subscribeList = View.inflate(activity, R.layout.subscribe_list,
                null);
        initView(subscribeList);
        leftListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                index = position;
                categoryAdapter.setSelectedPosition(position);
                groupId = (int) cateTrees.get(position).id();
                sendRightRequest();
                childAdapter.notifyDataSetChanged();
                categoryAdapter.notifyDataSetChanged();
            }
        });

        rightListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int keyid = (int) cateTrees.get(index).id();
                String groupName = cateTrees.get(index).title();
                rightposition = position;

                Intent keywordIntent = new Intent(activity,
                        KeywordSubscribeActivity.class);
                keywordIntent.putExtra("groupName",
                        childcateTrees.get(position).title());
                keywordIntent.putExtra("id", childcateTrees.get(position)
                        .id());
                activity.startActivityForResult(keywordIntent, 0);
                activity.overridePendingTransition(R.anim.left_in,
                        R.anim.left_out);

            }
        });

        rightListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                return true;
            }
        });
        return subscribeList;
    }

    private void initView(View subscribeList) {

        leftListView = (ListView) subscribeList
                .findViewById(R.id.lv_category_list);
        rightListView = (ListView) subscribeList
                .findViewById(R.id.lv_child_category_list);
        categoryAdapter = new SubscribeCategoryAdapter();
        leftListView.setAdapter(categoryAdapter);
        childAdapter = new SubscribeChildCategoryAdapter();
        rightListView.setAdapter(childAdapter);
        subscribedialog = new SubscribeDialog(activity);
    }

    // 此接口为左边listview返回数据
    public void cateTree30Success(List<CateTree> cateTreesTemp) {
        cateTrees = cateTreesTemp;
        pbHelp.goneLoading();
        groupId = (int) cateTreesTemp.get(0).id();
        sendRightRequest();
        categoryAdapter.notifyDataSetChanged();
    }

    // 此接口为右边listview返回数据
    public void cateTree30SSuccess(List<CateTree> cateTreesTemp) {
        pbHelp.goneLoading();
        srpparamdatalist = new ArrayList<SRPParam>();
        deldataList = new ArrayList<DELParam>();
        childAdapter.setDatas(cateTreesTemp);
        childAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        HttpJsonResponse response = _request.getResponse();
        switch (_request.getmId()) {
            case HttpCommon.SUB_CATETREE_REQUEST:
                cateTrees = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<CateTree>>() {
                }.getType());
                cateTree30Success(cateTrees);
                break;

            case HttpCommon.SUB_CATETREE2_REQUEST:
                List<CateTree> cateTreesTemp = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<CateTree>>() {
                }.getType());
                cateTree30SSuccess(cateTreesTemp);
                break;

            default:
        }
    }

    @Override
    public void onHttpError(IRequest _request) {

    }

    @Override
    public void onHttpStart(IRequest _request) {

    }

    public class SubscribeCategoryAdapter extends BaseAdapter {

        public int selectedPosition;

        @Override
        public int getCount() {
            return cateTrees != null ? cateTrees.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return cateTrees.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(activity,
                        R.layout.subscribe_category_list, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.tv_left);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(StringUtils.truncate(cateTrees.get(position)
                    .title().trim(), 12));
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
            if (position == -1 || position == cateTrees.size()) {
                selectedPosition = 0;
            } else {
                selectedPosition = position;
            }
        }
    }

    // 保存当前新闻源订阅的和没订阅的
    public boolean issaveNews() {
        if (srpparamdatalist != null && srpparamdatalist.size() > 0) {
            subscribeFlag = true;
            hasSubscribed = true;
        }
        if (deldataList != null && deldataList.size() > 0) {
            subscribeFlag = true;
            hasSubscribed = false;
        }
        return subscribeFlag;
    }

    public class SubscribeChildCategoryAdapter extends BaseAdapter {

        String category;
        LayoutInflater mInflater = LayoutInflater.from(activity);

        public void setDatas(List<CateTree> childcateTreesTemp) {
            childcateTrees = childcateTreesTemp;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (childcateTrees != null) {
                return childcateTrees.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return childcateTrees.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"NewApi", "ResourceAsColor"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.subscribe_child_category_list, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) convertView
                        .findViewById(R.id.tv_right);
                holder.go_right = (ImageView) convertView
                        .findViewById(R.id.go_right);
                holder.iv_newsource_subscribe_add = (ImageView) convertView
                        .findViewById(R.id.iv_newsource_subscribe_add);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(StringUtils.truncate(
                    childcateTrees.get(position).title().trim(), 12));

            category = childcateTrees.get(position).category();
            if (StringUtils.isNotEmpty(category)) {
                holder.go_right.setVisibility(View.GONE);
                holder.iv_newsource_subscribe_add.setVisibility(View.VISIBLE);
                holder.iv_newsource_subscribe_add
                        .setImageDrawable(childcateTrees.get(position)
                                .hasSubscribed() ? getResources().getDrawable(
                                R.drawable.subscribe_cancel01) : getResources()
                                .getDrawable(R.drawable.subscribe_add01));
            } else {
                holder.go_right.setVisibility(View.VISIBLE);
                holder.iv_newsource_subscribe_add.setVisibility(View.GONE);
            }

            return convertView;
        }

        private class ViewHolder {
            TextView text;
            ImageView go_right;
            ImageView iv_newsource_subscribe_add;
        }

        public void changeState(int position, String groupname, int keyid) {
            CateTree catetree = childcateTrees.get(position);
            if (!catetree.hasSubscribed()) {
                // 订阅
                SRP srp = null;
                List<SRP> sublistdata = new ArrayList<SRP>();
                srpparam = new SRPParam(groupname, keyid + "", sublistdata);
                sublistdata.add(addSubkeyword(srp, position));
                if (srpparam != null) {
                    srpparamdatalist.add(srpparam);
                }

            } else {
                // 退订
                delparam = new DELParam(keyid + "", childcateTrees
                        .get(position).sid(), childcateTrees.get(position)
                        .srpId());
                if (delparam != null) {
                    deldataList.add(delparam);
                }
            }

        }

        // 订阅添加子元素
        private SRP addSubkeyword(SRP srp, int position) {
            if (childcateTrees.get(position).category() != null
                    && childcateTrees.get(position).category().equals("system")) {
                srp = new SRP(childcateTrees.get(position).title(),
                        childcateTrees.get(position).id() + "");
            } else {
                srp = new SRP(childcateTrees.get(position).title(),
                        childcateTrees.get(position).srpId());
            }
            return srp;
        }
    }

    public void revertState() {
        childAdapter.setDatas(childcateTrees);
        childAdapter.notifyDataSetChanged();
        categoryAdapter.notifyDataSetChanged();

    }

}
