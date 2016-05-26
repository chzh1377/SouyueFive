package com.zhongsou.souyue.im.ac;

/**
 * 群详情
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.im.adapter.GroupDetailAdapter;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.dialog.ImProgressMsgDialog;
import com.zhongsou.souyue.im.interfaceclass.DetailChangeInterface;
import com.zhongsou.souyue.im.search.SearchUtils;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

public class NewGroupDetailsActivity extends IMBaseActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private ListView lvDetailList;
    private GroupDetailAdapter mAdapter;
    private ArrayList<GroupMembers> mList = new ArrayList<GroupMembers>();
    public ImserviceHelp service = ImserviceHelp.getInstance();
    private Group mGroup;
    private MessageRecent messageRecent = null;
    public static final String GROUP_DETAIL = "gotoGroupDetail";
    private ArrayList<View> mViewList;
    private ImProgressMsgDialog progressDialog;
    private LayoutInflater inflater;
    private RelativeLayout group_qr_code,group_name_layout,clear_all_history,edit_group_im_layout,group_my_nickname,im_download_file;
    private Button im_btn_exitGroup;
    private boolean isCleanHistory = false;//清空聊天记录
    private TextView title_name,group_name_tv,im_group_cout_tv,group_my_nickname_tv;
    private String count = "";//当前群人数
    private GroupMembers mGroupMembers;
    private Group group;//群信息
    private static final int REQUEST_CODE_EXIT_GROIP = 4;//退群
    public static  final int REQUEST_CODE_ADD_USER = 0;//向群聊中添加好友
    private int mGroupMax_number;// 群成员最大数

    private int isSaveContact = 1; //0不保存1，保存
    private int isSaveMessage = 1; //0关闭1，开启;
    private static final int OP = 12;//获取某个群的所有信息的  操作
    private ToggleButton tb_save_to_contact;//是否保存到通讯录
    private ToggleButton tb_save_to_message;//是否开启消息提醒

    private ConnectivityManager connectivityManager;
    private NetworkInfo info;

    private List<Long> vecIds = new ArrayList<Long>();

    public static DetailChangeInterface mDetailChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupdetail_layout);
        registerReceiver();
        getExterIntent();
        initView();
        bindEvent();
        setData();
        // 下面是与服务器同步数据代码，群成员id发给服务器 然后根据服务器返回来同步群数据
        for(GroupMembers members : service.db_findMemberListByGroupid(mGroup.getGroup_id())) {
            vecIds.add(members.getMember_id());
        }
        service.findGroupInfo(OP, mGroup.getGroup_id(), vecIds);
    }
    public int getmGroupMax_number() {
        return mGroupMax_number;
    }

    public Group getmGroup() {
        return mGroup;
    }

    public String getCount() {
        return count;
    }

    public GroupMembers getmGroupMembers() {
        return mGroupMembers;
    }

    /**
     * 初始化视图
     */
    private void initView(){
        title_name = (TextView) this.findViewById(R.id.title_name);
        title_name.setText(R.string.group_details);
        lvDetailList = (ListView) findViewById(R.id.lv_detail_list);

        inflater = LayoutInflater.from(this);
        View conView = inflater.inflate(R.layout.groupdetail_plugs,null);
        group_qr_code=(RelativeLayout)conView.findViewById(R.id.group_qr_code);
        group_name_layout=(RelativeLayout)conView.findViewById(R.id.group_name_layout);
        clear_all_history=(RelativeLayout)conView.findViewById(R.id.clear_all_history);
        edit_group_im_layout=(RelativeLayout)conView.findViewById(R.id.edit_group_im_layout);
        im_download_file=(RelativeLayout)conView.findViewById(R.id.im_download_file);

        group_my_nickname = (RelativeLayout)conView.findViewById(R.id.group_my_nickname);
        im_btn_exitGroup = (Button)conView.findViewById(R.id.im_btn_exitGroup);
        mViewList = new ArrayList<View>();
        mViewList.add(conView);

        group_name_tv = (TextView) conView.findViewById(R.id.group_name_tv);
        group_my_nickname_tv = (TextView)conView.findViewById(R.id.group_my_nickname_tv);
        im_group_cout_tv = (TextView)conView.findViewById(R.id.im_group_cout_tv);
        tb_save_to_contact = (ToggleButton) conView.findViewById(R.id.tb_save_to_contact);
        tb_save_to_message = (ToggleButton) conView.findViewById(R.id.tb_save_to_message);

    }

    /**
     * 绑定事件
     */
    private void bindEvent(){
        group_qr_code.setOnClickListener(this);
        group_name_layout.setOnClickListener(this);
        clear_all_history.setOnClickListener(this);
        im_btn_exitGroup.setOnClickListener(this);
        edit_group_im_layout.setOnClickListener(this);
        im_download_file.setOnClickListener(this);
        group_my_nickname.setOnClickListener(this);
    }

    private void setData(){
        group_name_tv.setText(mGroup.getGroup_nick_name());

        refreshData();
        isSaveContact = group.getIs_group_saved();

        //判断是否保存到通讯录
        if(isSaveContact == 0) {
            tb_save_to_contact.setChecked(false);
            tb_save_to_contact.setBackgroundResource(R.drawable.detail_switch_close);
        } else {
            tb_save_to_contact.setChecked(true);
            tb_save_to_contact.setBackgroundResource(R.drawable.detail_switch_open);
        }
        //是否开启消息提醒
        isSaveMessage = group.getIs_news_notify();
        if(isSaveMessage == 0) {
            tb_save_to_message.setChecked(false);
            tb_save_to_message.setBackgroundResource(R.drawable.detail_switch_close);
        } else {
            tb_save_to_message.setChecked(true);
            tb_save_to_message.setBackgroundResource(R.drawable.detail_switch_open);
        }
        tb_save_to_contact.setOnCheckedChangeListener(this);
        tb_save_to_message.setOnCheckedChangeListener(this);
    }

    private void refreshData(){
        group = service.db_findGourp(mGroup.getGroup_id());
        mList.clear();
        mList.addAll(reGetData(service.db_findMemberListByGroupid(mGroup.getGroup_id())));
        mGroupMembers =  service.db_findMemberListByGroupidandUid(mGroup.getGroup_id(), Long.valueOf(SYUserManager.getInstance().getUserId()));
        updatGroupCount();
        mAdapter = new GroupDetailAdapter(this,mList,mViewList);
        lvDetailList.setAdapter(mAdapter);
    }

    /**
     * 组装数据+、-
     * @return
     */
    private List<GroupMembers> reGetData(List<GroupMembers> list) {
        if(list != null && list.size() > 0) {
            //下面显示加号
            GroupMembers mGroupMembers1 = new GroupMembers();
            mGroupMembers1.setMember_id(1);//1、+号
            mGroupMembers1.setNick_name("");
            mGroupMembers1.setIs_owner(0);
            mGroupMembers1.setBy1("0");
            list.add(mGroupMembers1);

            if(SYUserManager.getInstance().getUserId() != null &&  mGroup != null && SYUserManager.getInstance().getUserId().equals(mGroup.getOwner_id() + "")){//是群主 显示减号
                GroupMembers mGroupMembers = new GroupMembers();
                mGroupMembers.setMember_id(0);//0、-号
                mGroupMembers.setNick_name("");
                mGroupMembers.setIs_owner(0);
                mGroupMembers.setBy1("1");
                list.add(mGroupMembers);
            }

            GroupMembers owner = new GroupMembers();
            for(int i = 0; i<list.size() ; i++){
                if(mGroup.getOwner_id() == list.get(i).getMember_id()){     //群主
                    owner = list.get(i);
                    list.remove(i);
                    list.add(0,owner);
                    break;
                }
            }
        }

        return list;

    }

    /**
     * 群主放首位
     * @return
     */
    private List<GroupMembers> newReGetData(List<GroupMembers> mList) {
        if(mList != null && mList.size() > 0) {
            GroupMembers owner = new GroupMembers();
            for(int i = 0; i<mList.size() ; i++){
                if(mList.get(i).getIs_owner() == 1){
                    owner = mList.get(i);
                    mList.remove(i);
                    break;
                }
            }
            mList.add(0,owner);
        }

        return mList;

    }

//    private boolean isOwer(List<GroupMembers> mList){
//        boolean flag = false;
//        for(int i = 0; i<mList.size() ; i++){
////            if(mList.get(i).getIs_owner() == 1) {
//                if(mList.get(i).getIs_owner() == 1)
//                    flag = true;
////            }
//        }
//        return flag;
//    }

    /**
     * 获得Intent数据
     */
    private void  getExterIntent() {
        mGroup = (Group) getIntent().getSerializableExtra(GROUP_DETAIL);
        //获取最近聊天记录
        messageRecent = ImserviceHelp.getInstance().db_findMessageRecent(mGroup.getGroup_id());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.group_qr_code://群二维码]
                mGroup.setGroup_nick_name(group_name_tv.getText().toString());
                mGroup.setMemberCount(Integer.valueOf(count));
                IMIntentUtil.gotoGroupQRCode(this, mGroup);
                break;
            case R.id.group_name_layout://群名称
                IMIntentUtil.gotoEditGroupNickName(this, mGroup,group_name_tv.getText().toString());
                break;
            case R.id.clear_all_history://清空聊天记录
                showProgress();
                clearGroupMsg();
                isCleanHistory = true;
                break;
            case R.id.im_btn_exitGroup://退出群聊
                if (!CMainHttp.getInstance().isNetworkAvailable(NewGroupDetailsActivity.this)) {
                    SouYueToast.makeText(getApplicationContext(), getString(R.string.user_login_networkerror), 0).show();
                    return;
                }
                exitGroup();
                break;
            case R.id.edit_group_im_layout://编辑消息
                Intent data = new Intent(this,IMChatActivity.class);
                data.putExtra("editGroup", "editGroup");
                setResult(IMIntentUtil.MYGROUP_EDITMSG, data);
                this.finish();
                break;
            case R.id.im_download_file://打开文件
                Intent i = new Intent();
                i.setClass(NewGroupDetailsActivity.this, FileListActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                NewGroupDetailsActivity.this.finish();
                break;
            case R.id.group_my_nickname://我的群昵称
                IMIntentUtil.gotoMyGroupNickName(this, mGroup,group_my_nickname_tv.getText().toString());
                break;
            default:
                break;
        }
    }

    /**
     * 显示对话框
     */
    public void showProgress() {
        progressDialog = new ImProgressMsgDialog.Builder(this).create();
        if (!isFinishing() || !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 显示对话框
     */
    public void showProgress(String message) {
        progressDialog = new ImProgressMsgDialog.Builder(this).setTextContent(message).create();
        progressDialog.setCancelable(false);// 设置点击屏幕Dialog不消失
        if (!isFinishing()) {
            progressDialog.show();
        }
    }

    public static void invoke(){

    }

    /**
     * 返回键
     */
    public void onBackPressClick(View view) {
        setBackData();
        finish();
    }

    @Override
    public void onBackPressed() {
        setBackData();
        finish();
    }

    /**
     * 设置返回时携带的参数
     */
    private void setBackData(){
        Intent data = new Intent(NewGroupDetailsActivity.this,IMChatActivity.class);
        data.putExtra("group_name", null!=group_name_tv.getText().toString()?group_name_tv.getText().toString():"");
        data.putExtra("isCleanHistory",isCleanHistory);
        setResult(RESULT_OK,data);
    }

    /**
     * 更新群人数
     */
    public void updatGroupCount() {
        if(mGroupMembers!=null && mGroup != null) {
            String mynickname = "";
            if(!TextUtils.isEmpty(mGroupMembers.getMember_name())) {
                mynickname = mGroupMembers.getMember_name();
            } else if(!TextUtils.isEmpty(mGroupMembers.getNick_name())) {
                mynickname = mGroupMembers.getNick_name();
            }
            group_my_nickname_tv.setText(mynickname);
            if(mGroupMembers.getIs_owner() == 1) {
                count = String.valueOf(mList.size()-2);
            } else {
                count = String.valueOf(mList.size()-1);
            }
            if (mGroup != null) {
                mGroupMax_number = mGroup.getMax_numbers();
            }else{
                mGroupMax_number = 40;
            }
            String count_color =  "<font color='#197ee1'>"+ count + "</font>"  + "/" + mGroupMax_number;
            im_group_cout_tv.setText(Html.fromHtml(count_color));
        }
    }

    /**
     * 清空群信息
     */
    private void clearGroupMsg() {
        ImDialog.Builder build = new ImDialog.Builder(this);
        build.setMessage(getString(R.string.im_clear_msg_sure));
        build.setPositiveButton(R.string.im_dialog_ok, new ImDialog.Builder.ImDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog01, View v) {
                service.db_clearMessageHistory(mGroup.getGroup_id(), IConst.CHAT_TYPE_GROUP);
                SearchUtils.deleteSession(MainActivity.SEARCH_PATH_MEMORY_DIR, mGroup.getSelf_id(), (short) IConst.CHAT_TYPE_GROUP, mGroup.getGroup_id());
                dismissProgress();
                clearRencentTime();
            }
        });
        build.setNegativeButton(R.string.im_dialog_cancel, new ImDialog.Builder.ImDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog01, View v) {
                dismissProgress();
            }
        });
        build.create().show();
        dismissProgress();

    }

    /**
     * 取消等待框
     */
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearRencentTime(){
        ImserviceHelp.getInstance().db_updateRecentTime(group.getGroup_id(),Long.valueOf(SYUserManager.getInstance().getUserId()),0);
        ImserviceHelp.getInstance().db_clearMessageRecentBubble(group.getGroup_id());
    }


    /**
     * 退出群聊
     */
    private void exitGroup() {
        ImDialog.Builder build = new ImDialog.Builder(this);
        String exitStr = "";
        if(null != mList && mList.size() == 3) {
            exitStr = "退出并解散该群？";
        } else {
            exitStr = "退群后，将不再接收此群聊信息";
        }
        build.setMessage(exitStr);
        build.setPositiveButton(R.string.im_dialog_ok, new ImDialog.Builder.ImDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog01, View v) {
                if (mGroupMembers != null) {
                    showProgress("正在退群...");
                    if (SYUserManager.getInstance().getUserId().equals(mGroup.getOwner_id()+"")) {
                        service.retreatGroupMembersOp(REQUEST_CODE_EXIT_GROIP, Long.toString(mGroupMembers.getGroup_id()), Long.toString(mList.get(1).getMember_id()));
                    } else {
                        service.retreatGroupMembersOp(REQUEST_CODE_EXIT_GROIP, Long.toString(mGroupMembers.getGroup_id()), "");
                    }
//                    SearchUtils.deleteSession(MainActivity.SEARCH_PATH_MEMORY_DIR, mGroupMembers.getSelf_id(), (short) IConst.CHAT_TYPE_GROUP, mGroupMembers.getGroup_id());
                }
            }
        });
        build.setNegativeButton(R.string.im_dialog_cancel, new ImDialog.Builder.ImDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog01, View v) {
                dismissProgress();
            }
        });
        build.create().show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMIntentUtil.REQUEST_CODE:
                    if (data != null && !isShowError ) {
                        String group_name = data.getStringExtra(EditGroupChatNickName.TAG);
                        group_name_tv.setText(group_name);
                    }
                    break;
                case IMIntentUtil.MYGROUP_NICKNAME:
                    if (data != null &&  !isShowError) {
                        String group_name = data.getStringExtra(EditGroupChatNickName.TAG);
                        group_my_nickname_tv.setText(group_name);
                    }
                    break;
                case REQUEST_CODE_ADD_USER:
                    if (data != null) {
                        ArrayList<GroupMembers> templist =  (ArrayList<GroupMembers>) data.getSerializableExtra("groupMembers");
                        mList.addAll(mList.size()-2,templist);
                        refreshData();

                    }
                    if(null != messageRecent && !TextUtils.isEmpty(messageRecent.getDrafttext())) {
                        //草稿再次入库
                        ImserviceHelp.getInstance().db_insertDraft(mGroup.getGroup_id(),messageRecent.getDrafttext());
                    }

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        switch (compoundButton.getId()) {

            case R.id.tb_save_to_contact:
                if (!CMainHttp.getInstance().isNetworkAvailable(NewGroupDetailsActivity.this)) {
                    SouYueToast.makeText(getApplicationContext(), getString(R.string.user_login_networkerror), 0).show();
                    return;
                }
                showProgress();
                Group group = service.db_findGourp(mGroup.getGroup_id());
                boolean isSetMessaeg = false;
                if(group.getIs_news_notify() == 1)
                    isSetMessaeg = true;
                else
                    isSetMessaeg = false;

                if(isChecked) {
                    if (service.saveGroupConfigOp(5,Long.toString(mGroup.getGroup_id()),true,isSetMessaeg)){
                        tb_save_to_contact.setBackgroundResource(R.drawable.detail_switch_open);
                    }
                }else {
                    if (service.saveGroupConfigOp(5,Long.toString(mGroup.getGroup_id()),false,isSetMessaeg)){
                        tb_save_to_contact.setBackgroundResource(R.drawable.detail_switch_close);
                    }
                }
                break;
            case R.id.tb_save_to_message:
                if (!CMainHttp.getInstance().isNetworkAvailable(NewGroupDetailsActivity.this)) {
                    SouYueToast.makeText(getApplicationContext(), getString(R.string.user_login_networkerror), 0).show();
                    return;
                }
                showProgress();
                Group group2 = service.db_findGourp(mGroup.getGroup_id());
                boolean isSetContact = false;
                if(group2.getIs_group_saved() == 1)
                    isSetContact = true;
                else
                    isSetContact = false;

                if(isChecked) {
                    if(service.saveGroupConfigOp(5,Long.toString(mGroup.getGroup_id()),isSetContact,true)){
                        mDetailChangeListener.msgNotifyChange(true);
                        tb_save_to_message.setBackgroundResource(R.drawable.detail_switch_open);
                    }
                }else {
                    if (service.saveGroupConfigOp(5,Long.toString(mGroup.getGroup_id()),isSetContact,false)){
                        mDetailChangeListener.msgNotifyChange(false);
                        tb_save_to_message.setBackgroundResource(R.drawable.detail_switch_close);
                    }
                }

                break;
            default:
                break;
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BroadcastUtil.ACTION_GROUP_EXIT);
        filter.addAction(BroadcastUtil.ACTION_GROUP_CREATE_FAIL);
        filter.addAction(BroadcastUtil.ACTION_GROUP_EXIT);
        registerReceiver(groupChatReceiver, filter);


        // 网络广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, mFilter);
    }

    BroadcastReceiver groupChatReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BroadcastUtil.ACTION_GROUP_EXIT)) {
                //如果传过来的id等于自己的id  说明是自己退群
                if(SYUserManager.getInstance().getUserId().equals(intent.getStringExtra("data"))) {
                    ImserviceHelp.getInstance().db_clearMessageHistory(mGroup.getGroup_id(),IConst.CHAT_TYPE_GROUP);
                    SearchUtils.deleteSession(MainActivity.SEARCH_PATH_MEMORY_DIR, mGroup.getSelf_id(), (short) IConst.CHAT_TYPE_GROUP, mGroup.getGroup_id());
                    IMIntentUtil.gotoMainActivity(NewGroupDetailsActivity.this);
                    NewGroupDetailsActivity.this.finish();
                }
            } else if(intent.getAction().equals(BroadcastUtil.ACTION_GROUP_CREATE_FAIL)) {
                String json = intent.getStringExtra("data");
                //友好提示
                ImUtils.showImError(json, NewGroupDetailsActivity.this);
            }
        }
    };

    /**
     * 监听网络变化广播 做出相应的提示
     */
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable())
                    dismissProgress();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(groupChatReceiver!=null){
            unregisterReceiver(groupChatReceiver);
        }
        if(networkReceiver!=null){
            unregisterReceiver(networkReceiver);
        }
    }
}