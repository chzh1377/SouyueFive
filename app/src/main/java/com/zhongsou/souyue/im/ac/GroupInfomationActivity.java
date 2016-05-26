package com.zhongsou.souyue.im.ac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.activity.CircleIMGroupActivity;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zoulu
 * 新增从兴趣圈跳到此页面，此时invitedid为空
 * on 14-8-28
 * Description:群信息
 */
public class GroupInfomationActivity extends IMBaseActivity implements View.OnClickListener{
    private ImageView image;
    private TextView groupname;
    private TextView groupnumber;
    private Button btn_chat;
    private long groupid;
    private long inviteid;
    private ImserviceHelp service = ImserviceHelp.getInstance();
    private Group mGroup;
    private List<GroupMembers> list = new ArrayList<GroupMembers>();
    private boolean flag;
    private TextView title_name;
    private Group group;
    private int mode;// 1 邀请，2 二维码，3 兴趣圈     --->这个页面默认 是2
    private String source;// 来源   mode>=3  才有来源
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        registerReceiver();
        setContentView(R.layout.groupinfo);
        init();
    }
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BroadcastUtil.ACTION_GROUP_INFO);
        registerReceiver(groupChatReceiver, filter);
        IntentFilter filter1 = new IntentFilter(BroadcastUtil.ACTION_GROUP_CREATE_SUCCESS);
        filter1.addAction(BroadcastUtil.ACTION_GROUP_CREATE_FAIL);
        registerReceiver(chatMsgReceiver, filter1);
    }

    private void init(){
        image = (ImageView) findViewById(R.id.image);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("群信息");
        groupname  = (TextView) findViewById(R.id.groupname);
        groupnumber = (TextView) findViewById(R.id.groupnumber);
        btn_chat = (Button) findViewById(R.id.btn_chat);
        groupid = getIntent().getLongExtra("groupid", 0l);
        inviteid = getIntent().getLongExtra("inviteid", 0l);
        mode = getIntent().getIntExtra("mode", 2);
        source = getIntent().getStringExtra("source");
        btn_chat.setOnClickListener(this);
        if(groupid != 0)
            mGroup = service.db_updateGroup(groupid);
        if(mGroup != null){
            flag = true;
            groupname.setText(mGroup.getGroup_nick_name());
            list.addAll(service.db_findMemberListByGroupid(mGroup.getGroup_id()));
            if(list != null) {
                groupnumber.setText(list.size()+"名成员");
            }
            btn_chat.setText("开始聊天");
            //aQuery.id(image).image(mGroup.getGroup_avatar(), true, true);
            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,mGroup.getGroup_avatar(),image, MyDisplayImageOption.defaultOption);
        }else{
            flag = false;
            btn_chat.setText("加入群聊");
            service.getGroupDetailsOp(11,groupid+"");
        }

    }

    private BroadcastReceiver groupChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("data");
            group = new Gson().fromJson(json, new TypeToken<Group>() { }.getType());
            //aQuery.id(image).image(group.getAvatar(), true, true);
            PhotoUtils.showCard(PhotoUtils.UriType.HTTP,group.getAvatar(),image,MyDisplayImageOption.defaultOption);
            groupname.setText(group.getGroup_nick_name());
            groupnumber.setText(group.getMemberCount()+"名成员");

            //4.1max   新版本新加服务器传得数据用作兴趣圈传的  inviteId
            group.getOwner_id();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(groupChatReceiver);
        unregisterReceiver(chatMsgReceiver);
    }

    @Override
    public void onClick(View view) {
        if (IntentUtil.isLogin()) {
            if(mGroup != null || group != null) {
                if (flag) {
                    IMIntentUtil.gotoGroupChatActivity1(GroupInfomationActivity.this, mGroup, 0);
                } else {
                    showProgress();
                    List<Long> uid = new ArrayList<Long>();
                    uid.add(Long.valueOf(SYUserManager.getInstance().getUserId()));
                    //判断假如inviteid 不存在则从广播中获得
                    if(0l==inviteid){
                        inviteid = group.getOwner_id();
                    }
                    service.addGroupMemberOp(2, groupid + "", inviteid + "", mode, uid,source);
                }
            }
        }else{
            IntentUtil.goLogin(this, true);
        }
    }

    private BroadcastReceiver chatMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissProgress();
            if(intent.getAction().equals(BroadcastUtil.ACTION_GROUP_CREATE_FAIL)) {
                String json = intent.getStringExtra("data");
                //友好提示
                ImUtils.showImError(json,GroupInfomationActivity.this);
                finish();
            }else{
                if(group!=null){
                    IMIntentUtil.gotoGroupChatActivity(GroupInfomationActivity.this, group,0);
                    //发送广播让兴趣圈群聊列表更新信息
                    Intent broadIntent = new Intent();
                    broadIntent.setAction(CircleIMGroupActivity.ACTION_UPDATE_CIRCLE_IMGROUP_LIST);
                    broadIntent.putExtra("group_id",group.getGroup_id());
                    sendBroadcast(broadIntent);
                    GroupInfomationActivity.this.finish();
                }
            }
        }
    };
}
