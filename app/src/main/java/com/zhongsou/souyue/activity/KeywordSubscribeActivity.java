package com.zhongsou.souyue.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.view.InterestDialog;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.module.CateTree;
import com.zhongsou.souyue.module.DELParam;
import com.zhongsou.souyue.module.SRP;
import com.zhongsou.souyue.module.SRPParam;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.SubscribeKeywordBack;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.sub.SubCateRssRequest;
import com.zhongsou.souyue.net.sub.SubSrpRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class KeywordSubscribeActivity extends RightSwipeActivity implements
        OnClickListener, OnItemClickListener,IVolleyResponse {
    private List<CateTree> keycateTrees;
    private KeywordsSubscribeAdapter keywordsAdapter;
    private ListView listView;
    //private boolean subAll = false;
    private String groupName;
//    private Http http;
    private boolean settingFlag = false;
    private List<SRPParam> srpparamdatalist;
    private List<DELParam> deldataList;
    //private TextView title;
    private long groupId;
    private ImageButton img_search;
    // 本次操作是订阅还是取消订阅
    private boolean hasSubscribed;
    private int rightPosition;
    private SRPParam srpparam;
    private DELParam delparam;
    public boolean subscribeFlag;
    private InterestDialog subscribedialog;
    private TextView tvTitle;
    private CateTree operateCateTree;
    private SuberDao dao;
    private CMainHttp mainHttp;

    public void cateTree30SSuccess(List<CateTree> cateTreesTemp) {
        pbHelp.goneLoading();
        keycateTrees = cateTreesTemp;
        CateTree tree;
        for(int i=0;keycateTrees!=null&&i<keycateTrees.size();i++){
            tree = keycateTrees.get(i);
            if(!"".equals(tree.sid())){
                keycateTrees.get(i).hassubscribed_$eq(true);
            }else{
                keycateTrees.get(i).hassubscribed_$eq(false);
            }
        }
        srpparamdatalist = new ArrayList<SRPParam>();
        deldataList = new ArrayList<DELParam>();
        keywordsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        HttpJsonResponse response = request.getResponse();
        switch (request.getmId()){
            case HttpCommon.SUB_SRP_REQUEST:
                List<SubscribeKeywordBack> list = new Gson().fromJson(response.getBodyArray(),new TypeToken<List<SubscribeKeywordBack>>(){}.getType());
                srpSubscribe30Success(list);
                break;

            case HttpCommon.SUB_CATETREE2_REQUEST:
                List<CateTree> cateTreesTemp = new Gson().fromJson(response.getBodyArray(),new TypeToken<List<CateTree>>(){}.getType());
                cateTree30SSuccess(cateTreesTemp);
                break;

            default:
                ;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()){
            case HttpCommon.SUB_SRP_REQUEST:
                subscribedialog.subscribefail();
                subscribedialog.dismiss();
                break;

            case HttpCommon.SUB_CATETREE2_REQUEST:
                if (settingFlag && keycateTrees.size() > 0) {
                    SouYueToast.makeText(getApplicationContext(),
                            R.string.subscribe_fail, SouYueToast.LENGTH_SHORT)
                            .show();
                }
                pbHelp.showNetError();
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keywords_subscribe);
        setCanRightSwipe(true);
        pbHelp = new ProgressBarHelper(this, null);
        pbHelp.setProgressBarClickListener(new ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                setData();
            }
        });
        initView();
        setData();
    }

    public void setData() {
//        http = new Http(this);
        mainHttp = CMainHttp.getInstance();
        dao = new SuberDaoImp();
        groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getLongExtra("id", -1);
       // http.cateTree30S(groupId + "");

        SubCateRssRequest request = new SubCateRssRequest(HttpCommon.SUB_CATETREE2_REQUEST,this);
        request.addParameters(groupId+"");
        mainHttp.doRequest(request);

    }

    public void initView() {
        img_search = findView(R.id.img_btn_title_activity_bar_search);
        img_search.setVisibility(View.VISIBLE);
        img_search.setOnClickListener(this);
        tvTitle = (TextView) this.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.suberlist_all);
        this.findViewById(R.id.rl_option).setVisibility(View.GONE);

        listView = (ListView) findViewById(R.id.lv_keywords_subscribe);
        keywordsAdapter = new KeywordsSubscribeAdapter(this);
        listView.setAdapter(keywordsAdapter);
        listView.setOnItemClickListener(this);
        subscribedialog = new InterestDialog(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_title_activity_bar_search:// "搜索"
                IntentUtil.openSearchActivity(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        SYSharedPreferences spys = SYSharedPreferences.getInstance();
//        if(spys.getBoolean(SYSharedPreferences.KEY_UPDATE, false)){
//            String token = SYUserManager.getInstance().getToken();
//           // http.cateTree30S(groupId + "");d
//            spys.remove(SYSharedPreferences.KEY_UPDATE);
//            spys.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
//        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position,
                            long id) {
        keywordsAdapter.changeState(position, groupName, groupId);
        rightPosition = position;
        if (issaveNews()) {
            saveNews();
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

    public void saveNews() {
        if (subscribeFlag) {
            SYSharedPreferences.getInstance().putBoolean(
                    SYSharedPreferences.KEY_UPDATE, true);

            SubSrpRequest request = new SubSrpRequest(HttpCommon.SUB_SRP_REQUEST,this);
            request.addParams(srpparamdatalist,deldataList);
            mainHttp.doRequest(request);

          //  http.srpSubscribe30(srpparamdatalist, deldataList, "");

            if (srpparamdatalist.isEmpty()) {
                if (deldataList != null) {
                    String tempDel = ((DELParam) deldataList.get(0)).srpId;
                    UpEventAgent.onSrpUnsubscribe(MainApplication.getInstance()
                            .getApplicationContext(), "", tempDel);
                }
            } else {
                if (srpparamdatalist != null) {

                    StringBuilder tempAdd = new StringBuilder();
                    List<SRP> srp = ((SRPParam) srpparamdatalist.get(0)).srp;
                    for (int i = 0; i < srp.size(); i++) {
                        tempAdd.append(srp.get(i).srpId + ",");
                    }
                    tempAdd.deleteCharAt(tempAdd.length() - 1);
                    UpEventAgent.onSrpSubscribe(MainApplication.getInstance(),
                            "", tempAdd.toString());
                }
            }
            subscribedialog.show();
            subscribedialog.progress();
            srpparamdatalist.clear();
            deldataList.clear();
            subscribeFlag = false;
        }
    }

    public void srpSubscribe30Success(List<SubscribeKeywordBack> list) {
        if (hasSubscribed) {
            subscribedialog.subscribe();
                if (list != null
                        && list.size() > 0
                        && keycateTrees.get(rightPosition).title()
                        .equals(list.get(0).getKeyword())) {
                    keycateTrees.get(rightPosition).sid_$eq(
                            list.get(0).getSid());
                }
                keycateTrees.get(rightPosition).hassubscribed_$eq(true);
                insertOne(); //插入数据库
            SYSharedPreferences.getInstance().setSuberSrpId(keycateTrees.get(rightPosition).srpId());
            UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_ADD_ALL_CLICK);
        } else {
            subscribedialog.unsubscribe();
            keycateTrees.get(rightPosition).hassubscribed_$eq(false);
            //删除
            deleteOne();

        }
        keywordsAdapter.notifyDataSetChanged();
        SYSharedPreferences.getInstance().putBoolean(
                SYSharedPreferences.KEY_UPDATE, true);
    }


    private void insertOne(){
        SuberedItemInfo info = new SuberedItemInfo();
        //获取srp的icon
        String imageIcon = CommonStringsApi.getSrpIconUrl(this, operateCateTree.srpId());
        //info.setId(operateCateTree.id());
        info.setId(Long.parseLong(keycateTrees.get(rightPosition).sid()));
        info.setTitle(operateCateTree.title());
        info.setCategory("srp");
        info.setImage(imageIcon);
        info.setSrpId(operateCateTree.srpId());
        info.setKeyword(operateCateTree.title());
        info.setType("0");
        dao.addOne(info);
    }

    private void deleteOne(){
        SuberedItemInfo info = new SuberedItemInfo();
        info.setSrpId(operateCateTree.srpId());
        dao.clearOne(info);
    }

    public class KeywordsSubscribeAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;

        public KeywordsSubscribeAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return keycateTrees != null ? keycateTrees.size() : 0;

        }

        @Override
        public Object getItem(int position) {
            return keycateTrees.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.keywords_subscribe_list_item, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) convertView
                        .findViewById(R.id.tv_keywords_subscribe);
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.iv_keywords_subscribe_add);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageView.setImageDrawable(keycateTrees.get(position)
                    .hasSubscribed() ? getResources().getDrawable(
                    R.drawable.subscribe_cancel01) : getResources()
                    .getDrawable(R.drawable.subscribe_add01));
            holder.text.setText(keycateTrees.get(position).title().trim());
            return convertView;
        }

        public void changeState(int position, String groupname, long keyid) {

            CateTree catetree = keycateTrees.get(position);
            operateCateTree = catetree;
            if (!catetree.hasSubscribed()) {
                // 订阅
                List<SRP> sublistdata = new ArrayList<SRP>();
                srpparam = new SRPParam(groupname, keyid + "", sublistdata);
                sublistdata.add(new SRP(keycateTrees.get(position).title(),
                        keycateTrees.get(position).srpId()));

                if (srpparam != null) {
                    srpparamdatalist.add(srpparam);
                }

            } else {
                // 退订
                delparam = new DELParam(keyid + "", keycateTrees.get(position)
                        .sid(), keycateTrees.get(position).srpId());
                if (delparam != null) {
                    deldataList.add(delparam);
                }

            }
            notifyDataSetChanged();

        }

        private class ViewHolder {
            TextView text;
            ImageView imageView;
        }

    }
}
