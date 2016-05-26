package com.zhongsou.souyue.im.ac;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.activity.ImFriendActivity;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.im.adapter.GroupListAdapter;
import com.zhongsou.souyue.im.dialog.ImShareDialog;
import com.zhongsou.souyue.im.fragment.ContactsListFragment;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zoulu
 * on 14-10-9
 * Description:分享内容到群
 */
public class ShareGroupActivity extends IMBaseActivity{
    private GroupListAdapter groupListAdapter;
    private TextView title_name;
    private ListView listView;
    private ImserviceHelp service = ImserviceHelp.getInstance();
    private List<Group> savegroupList = new ArrayList<Group>();
    private List<Group> unsavegroupList = new ArrayList<Group>();
    private List<Group> listall = new ArrayList<Group>();

    private boolean isContacts;
    private boolean isFromTiger;
    private Contact contactItem;
    private String sendType;
    private int tigernum;
    private ImShareNews imsharenews;
    private Object editList;

    private boolean isSYFriend = false;
    private Posts mainPosts;
    private String instrest_logo;
    private String shareUrl;
    private String srpId;
    private String instest_name;
    private int type;   //圈子类型
    private boolean isFromBlog;
    private long interest_id;
    private String SHARE_CALLBACK = "10";
//    private Http http;
    private String flag;
    private Group group;
    public static String ISSHOWCARD = "isshowcard";
    private boolean isshowcard;//分享群名片到群聊 私聊中

    private int from_type = -1;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.sharegroupactivity);
        init();
    }

    private void init(){
//        http = new Http(this);
        flag = getIntent().getStringExtra(IMIntentUtil.WHERECOMEFROM);
        //从群名片过来的数据
        group = (Group) getIntent().getSerializableExtra(IMChatActivity.KEY_GET_GROUPCARD_ID);
        //从新闻过来的数据
        isContacts = getIntent()
                .getBooleanExtra(ContactsListFragment.START_TYPE, true);
        isFromTiger = getIntent().getBooleanExtra(
                ContactsListActivity.START_FROM, false);
        contactItem = (Contact) getIntent().getSerializableExtra(
                "contact");
        sendType = getIntent().getStringExtra(
                IMChatActivity.KEY_ACTION);
        tigernum = getIntent().getIntExtra(
                ConstantsUtils.SHARE_COUNT, 0);
        imsharenews=(ImShareNews) getIntent().getSerializableExtra(ImShareNews.NEWSCONTENT);
        editList= getIntent().getSerializableExtra(ChatMsgEntity.FORWARD);
        //从兴趣圈过来的数据
        isSYFriend = this.getIntent().getBooleanExtra("isSYFriend", isSYFriend);
        mainPosts = (Posts) getIntent().getSerializableExtra("Posts");
        instrest_logo = getIntent().getStringExtra("interest_logo");
        shareUrl = getIntent().getStringExtra("shareUrl");
        srpId = getIntent().getStringExtra("srpId");
        from_type = this.getIntent().getIntExtra("fromType", from_type);
        if(null == instrest_logo)
            instrest_logo = "http://souyue-image.b0.upaiyun.com/user/0001/91733511.jpg";
        instest_name = getIntent().getStringExtra("interest_name");
        type = getIntent().getIntExtra("type", -1);
        isFromBlog = getIntent().getBooleanExtra("isFromBlog", false);
        interest_id = getIntent().getLongExtra("interest_id", 1001l);
        //从私聊、群聊进来的
        isshowcard = getIntent().getBooleanExtra(ISSHOWCARD,false);

        listView = (ListView) findViewById(R.id.listView);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("群聊");
        groupListAdapter = new GroupListAdapter(this);
        savegroupList = new Gson().fromJson(service.db_findGroupListByUserid(Long.valueOf(SYUserManager.getInstance().getUserId())),
                new TypeToken<List<Group>>() { }.getType());
//        unsavegroupList = new Gson().fromJson(service.db_findGroupListByGroupidAndIsSaved(Long.valueOf(SYUserManager.getInstance().getUserId()), IMessageConst.STATE_NO_SAVED),
//                new TypeToken<List<Group>>() { }.getType());
        if(savegroupList != null)
            listall.addAll(savegroupList);
//        if(unsavegroupList != null)
//            listall.addAll(unsavegroupList);
        Collections.reverse(listall);
        groupListAdapter.setData(listall);
        listView.setAdapter(groupListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                    final Group itemContact = (Group) groupListAdapter.getItem(position);
                if(flag.equals(IMIntentUtil.NEWS)){
                    //分享新闻
                    if(!StringUtils.isEmpty(sendType)&&sendType.equals(IMChatActivity.ACTION_SHARE_IMFRIEND)){
                        final ImShareDialog.Builder sharebuilder = new ImShareDialog.Builder(ShareGroupActivity.this);
                        if(imsharenews!=null){
                            sharebuilder.setImgHeader(imsharenews.getImgurl());
                            sharebuilder.setContent(imsharenews.getTitle());

                        }
                        sharebuilder.setPositiveButton(R.string.im_dialog_ok, new ImShareDialog.Builder.ImShareDialogInterface() {
                            @Override
                            public void onClick(DialogInterface dialog, View v) {
                                sendShareNews(itemContact,sharebuilder.desc);
                            }

                        }).create().show();
                        return;
                    }
                    //转发信息
                    if(!StringUtils.isEmpty(sendType)&&sendType.equals(IMChatActivity.ACTION_FORWARD)){
                        if(itemContact!=null&&editList!=null){
                            forwardMsg(editList,itemContact);
                        }
                    }
                    //转发用户名片
                    if(!StringUtils.isEmpty(sendType)&&sendType.equals(IMChatActivity.ACTION_SEND_VCARD)){
//                        Intent intent = new Intent(ShareGroupActivity.this, GroupChatActivity.class);
//                        intent.putExtra(GroupChatActivity.KEY_ACTION,
//                                GroupChatActivity.ACTION_SEND_VCARD);
//                        intent.putExtra("group", itemContact);
//                        intent.putExtra(GroupChatActivity.KEY_GET_CARD_ID, contactItem);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.left_in, R.anim.left_out);

                        ChatMsgEntity e =  getEntity(itemContact);
                        e.setType(IMessageConst.CONTENT_TYPE_VCARD);
                        e.status = IMessageConst.STATUS_SENTING;
                        e.setCard(contactItem);
                        ImserviceHelp.getInstance().im_sendMessage(IConst.CHAT_TYPE_GROUP,
                                itemContact.getGroup_id(), e.getType(), e.getText(), e.getRetry());
                        IMChatActivity.invoke(ShareGroupActivity.this, IConst.CHAT_TYPE_GROUP, itemContact.getGroup_id());
                        setResult(IMIntentUtil.SHAREGROUP);
                        finish();
                        return;
                    }

                    if (itemContact == null)
                        return;

                    if (!isContacts) {
                        if (contactItem != null) {
//                            sendCardFromInfo(itemContact);
                            Toast.makeText(ShareGroupActivity.this,"shareGroupAc----->178",Toast.LENGTH_LONG).show();
                        } else if (isFromTiger) {
                            sendCardFromTigerGame(itemContact, sendType);
                        } else if(group != null){
                            sendGroupCard(itemContact);
                        }else if(isshowcard) {
                            //这里用回退的方法做  setresult  把群名片的信息带到群里去  调 群 或者 私聊的 sendcard方法
                            long num = ImserviceHelp.getInstance().db_findMemberCountByGroupid(itemContact == null?itemContact.getGroup_id():itemContact.getGroup_id());
                            itemContact.setMemberCount((int) num);
                            sendCardToChat(itemContact);
                        }else{
                            sendCardFromImChat(itemContact);
                        }
                        return;
                    }
//                    ImFriendInfoActivity.startImFriendInfo(ShareGroupActivity.this, itemContact);
                }else{
                    openShareDialog(itemContact);
                }
            }

        });
    }

    //发送群名片到私聊
    private void sendGroupCard(Group itemContact){
        ChatMsgEntity e =  getEntity(itemContact);
        e.setType(IMessageConst.CONTENT_TYPE_GROUP_CARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setGroup(group);
        ImserviceHelp.getInstance().im_sendMessage(IConst.CHAT_TYPE_GROUP,
                itemContact.getGroup_id(), e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(ShareGroupActivity.this, IConst.CHAT_TYPE_GROUP, itemContact.getGroup_id());
        finish();
    }

    //新闻原创分享
    private void sendShareNews(Group itemContact,EditText desc){
        ImserviceHelp imservice = ImserviceHelp.getInstance();
        String sharenews = null;
        if(imsharenews!=null){
            JSONObject j =new JSONObject();
            try {
                j.put("keyword", imsharenews.getKeyword());
                j.put("sprid", imsharenews.getSrpid());
                j.put("title", imsharenews.getTitle());
                j.put("url", imsharenews.getUrl());
                j.put("imgurl", imsharenews.getImgurl());
                sharenews=j.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(imsharenews.getUrl() != null && !"".equals(imsharenews.getUrl())) {
        	imservice.im_sendMessage(1, itemContact.getGroup_id(), IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE, sharenews, "");
        }else {
        	imservice.im_sendMessage(1, itemContact.getGroup_id(), IMessageConst.CONTENT_TYPE_SRP_SHARE, sharenews, "");
        }
        
        if(!StringUtils.isEmpty(desc.getText())){
            imservice.im_sendMessage(1, itemContact.getGroup_id(), IMessageConst.CONTENT_TYPE_TEXT,desc.getText().toString().trim(),"");
        }
        SouYueToast.makeText(ShareGroupActivity.this, getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        setResult(IMIntentUtil.SHAREGROUP);
        SharePointInfo info = new SharePointInfo();
        info.setPlatform(SHARE_CALLBACK);
        info.setSrpId(imsharenews.getSrpid());
        info.setUrl(imsharenews.getUrl());
        info.setKeyWord(imsharenews.getKeyword());
//        http.userSharePoint(info);
        ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
        finish();
    }

    //转发信息
    private void forwardMsg(Object object,Group itemContact){
        List<ChatMsgEntity> editList=null;
        ChatMsgEntity chatmsgentity=null;
        if(object instanceof ChatMsgEntity){
            chatmsgentity=(ChatMsgEntity) object;
        }else{
            editList=(List<ChatMsgEntity>) object;
        }
        ImserviceHelp service = ImserviceHelp.getInstance();
        if(editList!=null){
            for(ChatMsgEntity item: editList){
                service.im_sendMessage(1, itemContact.getGroup_id(), item.getType(), item.getText(), "");
                setResult(RESULT_OK);
            }
        }
        if(chatmsgentity!=null){
            service.im_sendMessage(1, itemContact.getGroup_id(), chatmsgentity.getType(), chatmsgentity.getText(), "");
        }
        SouYueToast.makeText(ShareGroupActivity.this, R.string.im_chat_forward_success, SouYueToast.LENGTH_SHORT).show();
        setResult(IMIntentUtil.SHAREGROUP);
        IMChatActivity.isDetailOpen = ChatMsgEntity.INIT;
        finish();
    }

    // 从用户信息进入--更多--发送名片
//    private void sendCardFromInfo(Group itemContact) {
//        Intent intent = new Intent(ShareGroupActivity.this, TestChatActivity.class);
//        intent.putExtra(TestChatActivity.KEY_ACTION,
//                TestChatActivity.ACTION_SEND_VCARD);
//        intent.putExtra(TestChatActivity.KEY_CONTACT, itemContact);
//        intent.putExtra(TestChatActivity.KEY_GET_CARD_ID, contactItem);
//        startActivity(intent);
//        overridePendingTransition(R.anim.left_in, R.anim.left_out);
//        finish();
//    }

    /**
     * 从老虎机页面进入
     *
     * @param itemContact
     *            传递的联系人信息
     * @param sendType
     *            1 ACTION_ASK_COIN 索要中搜币 2 ACTION_ASK_SHARE分享
     */
    private void sendCardFromTigerGame(Group itemContact, String sendType) {
        Intent intent = new Intent(ShareGroupActivity.this, IMChatActivity.class);
        intent.putExtra(IMChatActivity.KEY_ACTION, sendType);
        intent.putExtra(IMChatActivity.KEY_CONTACT, itemContact);
        intent.putExtra(ContactsListActivity.START_FROM, isFromTiger);
        intent.putExtra(ConstantsUtils.SHARE_COUNT, tigernum);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    // 从聊天页面进入--发送名片
    private void sendCardFromImChat(Group itemContact) {
        Intent backIntent = new Intent();
        backIntent.putExtra(IMChatActivity.KEY_GET_CARD_ID, itemContact);
        setResult(Activity.RESULT_OK, backIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    //从contactListFragment进入--发送群名片
    private void sendCardToChat(Group group){
        Intent backIntent = new Intent();
        backIntent.putExtra(IMChatActivity.KEY_GET_CARD_ID, group);
        setResult(IMIntentUtil.SCARD, backIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    /**
     * 分享对话框
     */
    private void openShareDialog(final Group group) {

        final ImShareDialog.Builder sharebuilder = new ImShareDialog.Builder(ShareGroupActivity.this);
        sharebuilder.setImgHeader(instrest_logo);
        if (!isFromBlog){
            sharebuilder.setContent(instest_name);
        }else{
            if(!StringUtils.isEmpty(mainPosts.getTitle())){
                sharebuilder.setContent(mainPosts.getTitle());
            }else{
                sharebuilder.setContent(mainPosts.getContent());
            }
        }

        sharebuilder.setPositiveButton(R.string.im_dialog_ok,	new ImShareDialog.Builder.ImShareDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, View v) {
                if(!TextUtils.isEmpty(sharebuilder.getDesc_text())) {
                    //发送文本消息
                    ImserviceHelp.getInstance().im_sendMessage(1, group.getGroup_id(), MessageHistory.CONTENT_TYPE_TEXT, sharebuilder.getDesc_text(),"" );
                }
                if (from_type == ImFriendActivity.FROM_TYPE_WEB){
                    shareGreettingCard(group);
                }else {
                    shareToSYFriends(group);
                }
            }

        }).create().show();
    }

    /**
     * 分享到搜悦好友
     */
    private void shareToSYFriends(Group group) {
        JSONObject json = new JSONObject();
        try {
            json.put("interest_id", interest_id);
        json.put("blog_logo", instrest_logo);
        if(!isFromBlog){
            json.put("blog_title", instest_name);
            ImserviceHelp.getInstance().im_sendMessage(1, group.getGroup_id(),MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD, json.toString(),"" );
        }else{
            if(type == Constant.INTEREST_TYPE_PRIVATE){  //type == 1
                json.put("blog_title", mainPosts.getTitle());
                ImserviceHelp.getInstance().im_sendMessage(1, group.getGroup_id(),MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD, json.toString(),"" );
            }else{
                json.put("blog_title", mainPosts.getTitle());
                json.put("blog_id", mainPosts.getBlog_id());
                json.put("blog_content", mainPosts.getContent());
                json.put("user_id", mainPosts.getUser_id());
                json.put("is_prime", mainPosts.getIs_prime());
                json.put("top_status", mainPosts.getTop_status());
                ImserviceHelp.getInstance().im_sendMessage(1, group.getGroup_id(),MessageHistory.CONTENT_TYPE_INTEREST_SHARE, json.toString(),"" );
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SouYueToast.makeText(this, R.string.share_success, Toast.LENGTH_LONG).show();
        SharePointInfo info = new SharePointInfo();
        info.setPlatform(SHARE_CALLBACK);
        info.setSrpId(srpId);
        info.setUrl(shareUrl);
//        http.userSharePoint(info);
        ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
        setResult(IMIntentUtil.SHAREGROUP);
        this.finish();
    }


    /**
     * 分享贺卡
     * add  by  zhangwb
     */
    private void shareGreettingCard(Group group) {
        JSONObject json = new JSONObject();
        try {
            json.put("srpId", mainPosts.getSrpId());
            json.put("image_url", instrest_logo);
            json.put("title", mainPosts.getTitle());
            json.put("url", mainPosts.getUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImserviceHelp.getInstance().im_sendMessage(1, group.getGroup_id(), MessageHistory.CONTENT_TYPE_WEB, json.toString(), "");
        setResult(IMIntentUtil.SHAREGROUP);

        SouYueToast.makeText(this, R.string.share_success, Toast.LENGTH_LONG).show();
//            SharePointInfo info = new SharePointInfo();
//            info.setPlatform(SHARE_CALLBACK);
//            info.setSrpId(srpId);
//            info.setUrl(shareUrl);
//            http.userSharePoint(info);
        this.finish();
    }


    /**
     * 获取消息对像
     * @param group
     * @return
     */
    private ChatMsgEntity getEntity(Group group) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.userId = Long.parseLong(SYUserManager.getInstance().getUserId());
        entity.chatId = group.getGroup_id();
        entity.setSendId(Long.parseLong(SYUserManager.getInstance().getUserId()));
        entity.setChatType(IConst.CHAT_TYPE_GROUP);
        entity.setIconUrl(SYUserManager.getInstance().getUser().image());
        return entity;
    }
}
