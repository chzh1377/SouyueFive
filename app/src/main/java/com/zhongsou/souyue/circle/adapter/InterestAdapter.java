package com.zhongsou.souyue.circle.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.Interest;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.view.InterestDialog;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleExitCircleRequest;
import com.zhongsou.souyue.net.circle.InterestSubscriberReq;
import com.zhongsou.souyue.net.sub.GroupDeleteReq;
import com.zhongsou.souyue.net.sub.SubAddReq;
import com.zhongsou.souyue.net.sub.SubDeleteReq;
import com.zhongsou.souyue.net.sub.SubGroupRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.CSuberAllHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

/**
 * Created by wlong on 14-5-14.
 */
public class InterestAdapter extends BaseAdapter implements IVolleyResponse {

    private List<Interest> interests;
    private Context context;
    private final LayoutInflater mInflater;

    private Interest operaItem;

    private InterestDialog dialog;
    private SuberDao dao;
    private String typeCircle = "interest";
    private String typeSrp = "srp";
    private String typeGroup = "group";
    private boolean isRunning = false;  // 标志网络是否正在请求


    public InterestAdapter(Context context) {
        this.context = context;
        dao = new SuberDaoImp();
        mInflater = LayoutInflater.from(context);
        dialog = new InterestDialog(context);
    }

    @Override
    public int getCount() {
        return interests != null ? interests.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return interests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_source_subscribe_list_item, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.tv_newsource_subscribe);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.tv_newsource_description);
            holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_newsource_subscribe_icon);
            holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_newsource_subscribe_add);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv_add.setImageDrawable(interests.get(position).getSubscriber() == 1 ?
                context.getResources().getDrawable(R.drawable.subscribe_cancel01) :
                context.getResources().getDrawable(R.drawable.subscribe_add01));
        PhotoUtils.showCard(UriType.HTTP, interests.get(position).getLogo(), holder.iv_pic, MyDisplayImageOption.options);
        holder.text.setText(StringUtils.truncate(interests.get(position).getKeyword().trim(), StringUtils.LENGTH_12));
        if(!("0".equals((interests.get(position)).getSubCount()))){     //内容不为“0”
            holder.txtDescription.setText(StringUtils.truncate((interests.get(position)).getSubCount(), StringUtils.LENGTH_12));
        }
        initClickListener(holder, position);
        return convertView;
    }

    private void initClickListener(ViewHolder holder, int position) {
        if (holder == null) return;
        ViewClickListener listener = new ViewClickListener(position);
        holder.iv_add.setOnClickListener(listener);
    }


    class ViewClickListener implements View.OnClickListener {
        private int position;

        public ViewClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id) {
                case R.id.iv_newsource_subscribe_add:   //点击加号
                    if(!isRunning){
                        onClickBtnAdd(position, dialog);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static class ViewHolder {
        TextView text;
        ImageView iv_pic;
        ImageView iv_add;
        TextView txtDescription;
    }

    public void clearDatas() {
        if (interests != null)
            interests.clear();
    }

    public void setDatas(List<Interest> interests) {
        this.interests = interests;
        notifyDataSetChanged();
    }

    public void onClickOther(int position) {
        Interest current = interests.get(position);

        if (current.getInvokeType() == BaseInvoke.INVOKE_TYPE_FOCUSNEWS) {   //频道跳转
            IntentUtil.gotoSouYueYaoWen(context, current.getKeyword());
            return;
        }

        if (typeCircle.equals(current.getCategory())) {
            UIHelper.showCircleIndex((Activity) context, current.getSrpId(), current.getKeyword(), current.getKeyword(), null, "SubAllFragment");
        } else if (typeSrp.equals(current.getCategory())) {
            IntentUtil.gotoSouYueSRPAndFinish(context, current.getKeyword(), current.getSrpId(), null);
        } else if (typeGroup.equals(current.getCategory())) {
//            IntentUtil.gotoSubGroupHome(context, String.valueOf(current.getId()), current.getKeyword(), null);
        }
    }

    public void onClickBtnAdd(int position, InterestDialog dialog) {
        isRunning = true;
        if (dialog != null) {
            dialog.show();
            dialog.progress();
        }
        operaItem = interests.get(position);

        if (operaItem != null) {
            String category = operaItem.getCategory();
            if (category.equals(typeCircle)) {
                subscribeCircle();
            } else if (category.equals(typeSrp)) {
                subscribeSrp();
            } else if (category.equals(typeGroup)) {
                subscribeGroup();
            }
        }
    }

    private void subscribeCircle() {
//        if (operaItem.getSubscriber() == 0) { // 订阅
//            InterestSubscriberReq.send(
//                    CSuberAllHttp.SUBER_ALL_INTEREST_ADD_ACTION,
//                    SYUserManager.getInstance().getToken() + "",
//                    DeviceInfo.getAppVersion(),
//                    operaItem.getSrpId(),
//                    DeviceInfo.getDeviceId(),
//                    this,
//                    ZSSdkUtil.ALLLIST_SUBSCRIBE_EXTERNAL);
//
//        } else {  //退订
//            CircleExitCircleRequest request = new CircleExitCircleRequest(CSuberAllHttp.SUBER_ALL_INTEREST_DELETE_ACTION,this);
//            request.setParams(operaItem.getSrpId(), ZSSdkUtil.ALLLIST_SUBSCRIBE_EXTERNAL);
//            CMainHttp.getInstance().doRequest(request);
//        }
        if (operaItem.getSubscriber() == 0) { // 订阅
            InterestSubscriberReq.send(
                    CSuberAllHttp.SUBER_ALL_INTEREST_ADD_ACTION,
                    SYUserManager.getInstance().getToken() + "",
                    DeviceInfo.getAppVersion(),
                    operaItem.getId() + "",
                    DeviceInfo.getDeviceId(),
                    this,
                    ZSSdkUtil.ALLLIST_SUBSCRIBE_EXTERNAL);

        } else {  //退订
            CircleExitCircleRequest.send(
                    CSuberAllHttp.SUBER_ALL_INTEREST_DELETE_ACTION,
                    SYUserManager.getInstance().getToken() + "",
                    DeviceInfo.getAppVersion(),
                    operaItem.getId() + "",
                    DeviceInfo.getDeviceId() + "",
                    this,
                    ZSSdkUtil.ALLLIST_SUBSCRIBE_EXTERNAL);
        }
    }

    private void subscribeSrp() {
        if (operaItem.getSubscriber() == 0) { // 订阅

            SubAddReq request = new SubAddReq(HttpCommon.SUB_ADD_REQUEST, this);
            request.addParameters(
                    operaItem.getKeyword(),
                    operaItem.getSrpId(),
                    "-1",
                    "",
                    typeSrp,
                    ZSSdkUtil.ALLLIST_SUBSCRIBE_EXTERNAL);
            CMainHttp.getInstance().doRequest(request);
        } else {  //退订
            SubDeleteReq request = new SubDeleteReq(HttpCommon.SUB_DELETE_REQUEST, this);
            request.setParameters(
                    operaItem.getSubscribe_id(),     //订阅id
                    typeSrp,
                    operaItem.getSrpId(),
                    ZSSdkUtil.ALLLIST_SUBSCRIBE_EXTERNAL);
            CMainHttp.getInstance().doRequest(request);
        }
    }

    private void subscribeGroup() {
        if (operaItem.getSubscriber() == 0) { // 订阅
            SubGroupRequest request = new SubGroupRequest(HttpCommon.GROUP_SUB_REQUEST, this);
            request.setParams(String.valueOf(operaItem.getId()));
            CMainHttp.getInstance().doRequest(request);
        } else {  //退订
            GroupDeleteReq request = new GroupDeleteReq(HttpCommon.GROUP_DELETE_REQ, this);
            request.setParams(String.valueOf(operaItem.getSubscribe_id()));
            CMainHttp.getInstance().doRequest(request);
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        CSouyueHttpError error = (CSouyueHttpError) _request.getVolleyError();
        if (error != null) {
            if (error.getErrorCode() < 700) {
                dialog.subscribefail();
            } else if(error.getErrorCode() == 700){
                operaItem.setSubscriber(0);
                this.notifyDataSetChanged();
                dialog.dimissRightNow();
            }else{
                dialog.dimissRightNow();
            }
        }
        isRunning = false;
    }

    @Override
    public void onHttpResponse(IRequest request) {
        HttpJsonResponse response = request.getResponse();
        String subId = "";
        switch (request.getmId()) {
            case CSuberAllHttp.SUBER_ALL_INTEREST_ADD_ACTION:   //订阅圈子
                operaItem.setSubscriber(1);
                this.notifyDataSetChanged();
                if (dialog != null)
                    dialog.subscribe();

                saveSharedPreferences(operaItem.getSrpId());
                //插入数据库
                insertItem();
                UpEventAgent.onGroupJoin(context, operaItem.getId() + "." + operaItem.getKeyword(), "");
                UmengStatisticUtil.onEvent(context, UmengStatisticEvent.SUBSCRIBE_ADD_ALL_CLICK);
                break;

            case CSuberAllHttp.SUBER_ALL_INTEREST_DELETE_ACTION:    //取消订阅圈子
                operaItem.setSubscriber(0);
                this.notifyDataSetChanged();
                SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
                dialog.unsubscribe();
                deleteItem();
                //统计
                UpEventAgent.onGroupQuit(context, operaItem.getId() + "." + operaItem.getKeyword(), "");
                break;

            case HttpCommon.SUB_ADD_REQUEST:    //sub srp
                subId = response.getBody().getAsJsonArray("id").get(0).toString();
                operaItem.setSubscriber(1);
                operaItem.setSubscribe_id(subId);
                this.notifyDataSetChanged();
                if (dialog != null)
                    dialog.subscribe();

                saveSharedPreferences(operaItem.getSrpId());
                //插入数据库
                insertSrpItem();
                break;
            case HttpCommon.SUB_DELETE_REQUEST: //unsub srp
                operaItem.setSubscriber(0);
                this.notifyDataSetChanged();
                SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
                dialog.unsubscribe();
                deleteItem();
                break;

            case HttpCommon.GROUP_SUB_REQUEST:  //订阅组
                try{
                    subId = response.getBody().get("subId").toString();
                }catch(NullPointerException e){
                    Log.e("GroupSubId", "NullPointerException!");
                }
                operaItem.setSubscriber(1);
                operaItem.setSubscribe_id(subId);
                this.notifyDataSetChanged();
                if (dialog != null)
                    dialog.subscribe();

                saveSharedPreferences(operaItem.getSrpId());
                //插入数据库
                insertGroupItem();
                break;
            case HttpCommon.GROUP_DELETE_REQ:   //取消订阅组
                operaItem.setSubscriber(0);
                this.notifyDataSetChanged();
                SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
                dialog.unsubscribe();
                deleteGroup();
                break;
        }
        isRunning = false;
    }

    private void saveSharedPreferences(String srpId){
        SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
        SYSharedPreferences.getInstance().setSuberSrpId(srpId);//保存id到sysp
    }

    private void insertItem() {
        SuberedItemInfo info = new SuberedItemInfo();
        info.setId(operaItem.getId());
        info.setTitle(operaItem.getKeyword());
        info.setCategory(operaItem.getCategory());
        info.setImage(operaItem.getLogo());
        info.setSrpId(operaItem.getSrpId());
        info.setKeyword(operaItem.getKeyword());
        info.setType("0");  //圈子属性: 1:私密; 0:非私密
        dao.addOne(info);
    }

    private void insertSrpItem() {
        SuberedItemInfo info = new SuberedItemInfo();
        if(!StringUtils.isEmpty(operaItem.getSubscribe_id())){
            info.setId(Long.parseLong(operaItem.getSubscribe_id()));
        }
        info.setTitle(operaItem.getKeyword());
        info.setCategory(operaItem.getCategory());
        info.setImage(operaItem.getLogo());
        info.setSrpId(operaItem.getSrpId());
        info.setKeyword(operaItem.getKeyword());
        info.setType("0");  //圈子属性: 1:私密; 0:非私密
        dao.addOne(info);
    }
    private void insertGroupItem() {
        SuberedItemInfo info = new SuberedItemInfo();
        if(!StringUtils.isEmpty(operaItem.getSubscribe_id())){
            info.setId(Long.parseLong(operaItem.getSubscribe_id()));
        }
        info.setTitle(operaItem.getKeyword());
        info.setCategory(operaItem.getCategory());
        info.setImage(operaItem.getLogo());
        info.setSrpId(String.valueOf(operaItem.getId()));
        info.setKeyword(operaItem.getKeyword());
        dao.addOne(info);
    }

    private void deleteItem() {
        SuberedItemInfo info = new SuberedItemInfo();
        info.setSrpId(operaItem.getSrpId());
        dao.clearOne(info);
    }

    private void deleteGroup() {
        SuberedItemInfo info = new SuberedItemInfo();
        info.setSrpId(String.valueOf(operaItem.getId()));
        dao.clearOne(info);
    }

    @Override
    public void onHttpStart(IRequest _request) {

    }
}
