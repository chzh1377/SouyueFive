package com.zhongsou.souyue.im.ac;


import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.im.adapter.ContactsAdapter;
import com.zhongsou.souyue.im.dialog.ImShareDialog;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ContactModelUtil;
import com.zhongsou.souyue.im.view.AlphaSideBar;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分享内容联系人（好友）
 * 分享二级页面 上级页面为IMShareActivity
 */
public class ShareContactActivity extends IMBaseActivity{

    private SwipeListView mListView;
    private EditText mEditText;
    private LinearLayout headItemLayout;
    private ContactsAdapter mAdapter;
    private Map<String, Integer> alphaIndex;
    private List<Contact> convertResult;
    private LoadContactsTask loadContactsTask;
    private AlphaSideBar alphaBar;
    private TextView txtOverlay;
    private TextView tittle;
    private RelativeLayout tittleBar;
    private Object editList;
//    private Http http;

    private boolean isContacts;
    private Group group;
    //SRP参数
    private ImShareNews imShareNews;
    //是否來自web頁面（來自為0）
    private int fromType = -1;
    //个人中心发送名片参数
    private String sendType;
    private Contact keyContact;
    //兴趣圈参数
    private int fromWhere;
    private long interestID;
    private boolean isSYFriend = false; //是否是搜悦好友
    private String interestLogo;
    private String interestName;
    private Posts mainPosts;
    private int type;   //圈子类型
    private boolean isFromBlog;
    private String shareUrl;
    private String srpId;
    private String SHARE_CALLBACK = "10";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.im_swipe_contacts_list_view_fragment);
//        http = new Http(this);
        initIntent();
        initView();
        initData();
    }

    private void initIntent(){
        //IM相关参数
        editList= getIntent().getSerializableExtra(ChatMsgEntity.FORWARD);
        isContacts = getIntent().getBooleanExtra("start_type", true);
        group = (Group) getIntent().getSerializableExtra("group_card");
        //SRP参数
        imShareNews=(ImShareNews) getIntent().getSerializableExtra(ImShareNews.NEWSCONTENT);
        //web頁面
        fromType = this.getIntent().getIntExtra("fromType", fromType);
        //个人中心发送名片
        sendType = getIntent().getStringExtra(IMChatActivity.KEY_ACTION);
        keyContact = (Contact) getIntent().getSerializableExtra("contact");
        //兴趣圈相关参数
        fromWhere = getIntent().getIntExtra("fromWhere", -1);
        interestID = getIntent().getLongExtra("interest_id", 1001l);
        isSYFriend = this.getIntent().getBooleanExtra("isSYFriend", isSYFriend);
        interestLogo = getIntent().getStringExtra("interest_logo");
        if(null == interestLogo){
            interestLogo = "http://souyue-image.b0.upaiyun.com/user/0001/91733511.jpg";
        }
        interestName = getIntent().getStringExtra("interest_name");
        if(null == interestName){
            interestName = "圈子名称";
        }
        mainPosts = (Posts) getIntent().getSerializableExtra("Posts");
        type = getIntent().getIntExtra("type", -1);
        isFromBlog = getIntent().getBooleanExtra("isFromBlog", false);
        shareUrl = getIntent().getStringExtra("shareUrl");
        srpId = getIntent().getStringExtra("srpId");
    }

    private void initView() {
        mListView = findView(R.id.example_lv_list);
        mListView.setSwipeAble(false);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        View headView = getLayoutInflater().inflate(R.layout.im_share_list_header_view, null);
        initListViewHeader(headView);
        mListView.addHeaderView(headView);
        setSlideBar();
        alphaIndex = new HashMap<String, Integer>();
        mAdapter = new ContactsAdapter(mListView, ShareContactActivity.this, alphaIndex,mListView.getRightViewWidth());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SwipeListView sl = ((SwipeListView) parent);
                final Contact itemContact = mAdapter.getItem(position - sl.getHeaderViewsCount());
                if (!isContacts && group != null) {
                    if (itemContact.getChat_type() == IConst.CHAT_TYPE_PRIVATE) {
                        sendGroupCard(itemContact.getChat_type(), itemContact.getChat_id());
                    } else {
                        sendGroupCardToGroup(itemContact.getChat_type(), itemContact.getChat_id());
                    }
                } else if (fromWhere == IMShareActivity.IM_SHARE_FROM_INSTREST || fromWhere == IMShareActivity.IM_SHARE_FROM_WEB) {
                    openShareDialog(itemContact.getChat_type(), itemContact.getChat_id());
                } else if (fromWhere == IMShareActivity.IM_SHARE_FROM_SRP) {
                    openSrpShareDialog(itemContact.getChat_type(), itemContact.getChat_id());
                } else if (!StringUtils.isEmpty(sendType) && sendType.equals(IMChatActivity.ACTION_SEND_VCARD)) {
                    sendVCard(itemContact);
                } else {
                    forwardMsg(editList, itemContact);
                }
            }
        });

        tittleBar = findView(R.id.rl_tittle_bar);
        tittleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setSelection(0);
            }
        });
        tittle = findView(R.id.activity_bar_title);
        tittle.setText("好友");

    }

    private void initListViewHeader(View headView) {
        headItemLayout = (LinearLayout) headView.findViewById(R.id.ll_other);
        headItemLayout.setVisibility(View.GONE);
        mEditText = (EditText) headView.findViewById(R.id.search_edit);
        mEditText.setHint(R.string.search_no_group);
        mEditText.setHintTextColor(getResources().getColor(R.color.im_edit_text_hint_color));

        mEditText.setVisibility(View.VISIBLE);
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager im = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                im.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            String before = null;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String after = s.toString();
                if (before != null && before.equals(after)) {// 没有改变
                    return;
                }
                if (!TextUtils.isEmpty(after)) {// 有输入
                    alphaBar.setVisibility(View.GONE);
                    mListView.setSwipeAble(false);
                    mAdapter.setShowNoValue(true);
                } else {
                    alphaBar.setVisibility(View.VISIBLE);
                    mListView.setSwipeAble(true);
                    mAdapter.setShowNoValue(false);
                    mAdapter.setKeyWord(null);
                }
                loadContactsTask.cancel(true);
                loadContactsTask = new LoadContactsTask();
                loadContactsTask.execute(after);
            }
        });

    }

    private void initData() {
        loadContactsTask = new LoadContactsTask();
        loadContactsTask.execute("", "");// 首次获取
    }

    public class LoadContactsTask extends
            AsyncTask<String, Void, List<Contact>> {
        private boolean needLoad;

        @Override
        protected void onPreExecute() {
//            dialog.show();
            super.onPreExecute();
        }

        /**
         * 参数为空表示取全部，
         * 参数个数为1表示本地搜索
         * 参数个数为2表示初次取通讯录，需要通知获取网络通讯录
         *
         */
        protected List<Contact> doInBackground(String... args) {
            List<Contact> data = null;
            boolean showAll = true;
            needLoad = false;
            String keyword_F = null;
            String sourceKey = null;
            if (args != null && args.length > 0) {
                String searchKey = args[0];// 输入框输入
                if (!TextUtils.isEmpty(searchKey)) {
                    keyword_F = PingYinUtil.conver2SqlReg(searchKey);
                    sourceKey = searchKey;
                    if (!TextUtils.isEmpty(keyword_F)) {// 显示全部
                        showAll = false;
                    }
                }
            }
            long muid = SYUserManager.getInstance().getUser().userId();
            if (showAll) {// 显示全部
                data = ImserviceHelp.getInstance().db_getContact();
                if (args.length == 2) {
                    needLoad = true;
                }
            } else {
                data = ImserviceHelp.getInstance().db_findLikeOnlyContact(keyword_F);
                mAdapter.setKeyWord(sourceKey.toUpperCase().replace(" ",""));
            }

            return convertResult((List<Contact>) (data == null ? Collections.emptyList() : data));
        }

        protected void onPostExecute(final List<Contact> result) {

            if (needLoad) {
                ImserviceHelp.getInstance().im_info(4, null);
            }
            if(null!=mAdapter) {
                mAdapter.addAll(convertResult);
                mAdapter.notifyDataSetChanged();
            }

        }

        private List<Contact> convertResult(List<Contact> result) {
            convertResult = new ArrayList<Contact>();
            boolean isSearch = false;
            if (!TextUtils.isEmpty(mEditText.getText().toString())) {
                isSearch = true;
            }

            LogDebugUtil.v("fan", result.size() + "总共");
            Map<String, List<Contact>> sectionItems = new HashMap<String, List<Contact>>();
            List<Contact> finalRes = null;
            for (int i = 0; i < result.size(); i++) {
                Contact item = result.get(i);
                String lhs_p = PingYinUtil.getPingYin(ContactModelUtil
                        .getShowName(item));
                if (TextUtils.isEmpty(lhs_p))
                    continue;

                String key = lhs_p.substring(0, 1).toUpperCase();
                if ("A".compareTo(key) > 0 || "Z".compareTo(key) < 0) {
                    key = "#";
                }

                if (!sectionItems.containsKey(key)) {
                    finalRes = new ArrayList<Contact>();
                    sectionItems.put(key, finalRes);
                } else {
                    finalRes = sectionItems.get(key);
                }
                finalRes.add(item);
            }

            List<String> keys = new ArrayList<String>(sectionItems
                    .keySet());
            Collections.sort(keys, new Comparator<String>() {

                @Override
                public int compare(String lhs, String rhs) {
                    if (lhs.equals(rhs)) {
                        return 0;
                    } else if ("#".equals(lhs) && !"#".equals(rhs)) {
                        return 1;
                    } else if (!"#".equals(lhs) && "#".equals(rhs)) {
                        return -1;
                    }
                    return lhs.compareTo(rhs);
                }
            });

            int sectionIndex = 0;
            if (!isSearch){
                for (String key : keys) {
                    List<Contact> items = sectionItems.get(key);
                    Collections.sort(items, new Comparator<Contact>() {

                        @Override
                        public int compare(Contact lhs, Contact rhs) {
                            String lhs_p = PingYinUtil
                                    .getPingYin(ContactModelUtil
                                            .getShowName(lhs));
                            String rhs_p = PingYinUtil
                                    .getPingYin(ContactModelUtil
                                            .getShowName(rhs));
                            return lhs_p.compareTo(rhs_p);
                        }
                    });

                    if (!isSearch) {// 追加索引字母
                        Contact indexItem = new Contact();
                        indexItem.setComment_name(key);
                        items.add(0, indexItem);
                    }

                    alphaIndex.put(key, sectionIndex);
                    convertResult.addAll(items);
                    sectionIndex += items.size();
                }
            }else{
                convertResult.addAll(result);
            }
            return convertResult;
        }
    }

    private void setSlideBar() {
        alphaBar = (AlphaSideBar)findViewById(R.id.alphaView);
        alphaBar.setListView(mListView);
        txtOverlay = (TextView)findViewById(R.id.tv_window);
        alphaBar.setTextView(txtOverlay);
    }

    //发送群名片到私聊
    private void sendGroupCard(int chatType, long chatId){
        ChatMsgEntity e =  getEntity(chatType, chatId);
        e.setType(IMessageConst.CONTENT_TYPE_GROUP_CARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setGroup(group);
        ImserviceHelp.getInstance().im_sendMessage(chatType, chatId, e.getType(), e.getText(), e.getRetry());
        setResult(IMShareActivity.IM_SHARE_RESULT);

        IMChatActivity.invoke(ShareContactActivity.this, IConst.CHAT_TYPE_PRIVATE, chatId);
        finish();
    }

    //发送群名片到群聊
    public void sendGroupCardToGroup(int chatType, long chatId){
        ChatMsgEntity e = new ChatMsgEntity();
        e.userId = Long.parseLong(SYUserManager.getInstance().getUserId());
        e.chatId = group.getGroup_id();
        e.setSendId(Long.parseLong(SYUserManager.getInstance().getUserId()));
        e.setChatType(IConst.CHAT_TYPE_GROUP);
        e.setIconUrl(SYUserManager.getInstance().getUser().image());
        e.setType(IMessageConst.CONTENT_TYPE_GROUP_CARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setGroup(group);
        setResult(IMShareActivity.IM_SHARE_RESULT);
        ImserviceHelp.getInstance().im_sendMessage(IConst.CHAT_TYPE_GROUP,
                chatId, e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(ShareContactActivity.this, IConst.CHAT_TYPE_GROUP, chatId);
        finish();
    }

    /**
     * 获取消息对像
     */
    private ChatMsgEntity getEntity(int chatType, long chatId) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.userId = Long.parseLong(SYUserManager.getInstance().getUserId());
        entity.chatId = chatId;
        entity.setSendId(Long.parseLong(SYUserManager.getInstance().getUserId()));
        entity.setChatType(chatType);
        entity.setIconUrl(SYUserManager.getInstance().getUser().image());
        return entity;
    }

    //个人中心发送名片
    private void sendVCard(Contact itemContact){
        ChatMsgEntity e =  getEntity(itemContact.getChat_type(),itemContact.getChat_id());
        e.setType(IMessageConst.CONTENT_TYPE_VCARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setCard(keyContact);
        ImserviceHelp.getInstance().im_sendMessage(IConst.CHAT_TYPE_PRIVATE,
                itemContact.getChat_id(), e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(ShareContactActivity.this, IConst.CHAT_TYPE_PRIVATE, itemContact.getChat_id());
        setResult(IMShareActivity.IM_SHARE_RESULT);
        finish();
        return;
    }

    //转发信息
    private void forwardMsg(Object object,Contact itemContact){
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
                service.im_sendMessage(itemContact.getChat_type(), itemContact.getChat_id(), item.getType(), item.getText(), "");
                setResult(RESULT_OK);
            }
        }
        if(chatmsgentity!=null){
            service.im_sendMessage(itemContact.getChat_type(), itemContact.getChat_id(), chatmsgentity.getType(), chatmsgentity.getText(), "");
        }
        setResult(IMShareActivity.IM_SHARE_RESULT);
        IMChatActivity.isDetailOpen = ChatMsgEntity.INIT;
        SouYueToast.makeText(this, R.string.im_chat_forward_success, SouYueToast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 彈出兴趣圈分享对话框
     */
    private void openShareDialog(final int chatType, final long chatId) {
        final ImShareDialog.Builder sharebuilder = new ImShareDialog.Builder(ShareContactActivity.this);
        sharebuilder.setImgHeader(interestLogo);
        if (!isFromBlog) {
            sharebuilder.setContent(interestName);
        } else {
            if (!StringUtils.isEmpty(mainPosts.getTitle())) {
                sharebuilder.setContent(mainPosts.getTitle());
            } else {
                sharebuilder.setContent(mainPosts.getContent());
            }
        }

        sharebuilder.setPositiveButton(R.string.im_dialog_ok, new ImShareDialog.Builder.ImShareDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, View v) {
                if (fromWhere == IMShareActivity.IM_SHARE_FROM_WEB) {//賀卡的？
                    shareGreettingCard(chatType,chatId,sharebuilder.getDesc_text());
                } else {
                    shareToSYFriends(chatType, chatId,sharebuilder.getDesc_text());
                }
            }
        }).create().show();

    }

    /**
     * 分享贺卡
     */
    private void shareGreettingCard(int chatType,long chatId,String desc) {
        JSONObject json = new JSONObject();
        try {
            json.put("image_url", interestLogo);
            json.put("title", mainPosts.getTitle());
            json.put("url", mainPosts.getUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImserviceHelp.getInstance().im_sendMessage(chatType, chatId, MessageHistory.CONTENT_TYPE_WEB, json.toString(), "");

        if(!StringUtils.isEmpty(desc)){
            ImserviceHelp.getInstance().im_sendMessage(chatType, chatId,MessageHistory.CONTENT_TYPE_TEXT, desc,"");
        }
        setResult(IMShareActivity.IM_SHARE_RESULT);

        SouYueToast.makeText(this, R.string.share_success, Toast.LENGTH_LONG).show();
        this.finish();
    }

    /**
     * 兴趣圈分享到搜悦好友
     */
    private void shareToSYFriends(int chatType,long chatId,String desc) {
        JSONObject json = new JSONObject();
        try {
            json.put("interest_id", interestID);
        json.put("blog_logo", interestLogo);
        if(!isFromBlog){
            json.put("blog_title", interestName);
            ImserviceHelp.getInstance().im_sendMessage(chatType, chatId, MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD, json.toString(),"" );
        }else{
            if(type == Constant.INTEREST_TYPE_PRIVATE){  //type == 1
                json.put("blog_title", mainPosts.getTitle());
                ImserviceHelp.getInstance().im_sendMessage(chatType, chatId,MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD, json.toString(),"" );
            }else{
                json.put("blog_title", mainPosts.getTitle());
                json.put("blog_id", mainPosts.getBlog_id());
                json.put("blog_content", mainPosts.getContent());
                json.put("user_id", mainPosts.getUser_id());
                json.put("is_prime", mainPosts.getIs_prime());
                json.put("top_status", mainPosts.getTop_status());
                ImserviceHelp.getInstance().im_sendMessage(chatType, chatId,MessageHistory.CONTENT_TYPE_INTEREST_SHARE, json.toString(),"" );
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!StringUtils.isEmpty(desc)){
            ImserviceHelp.getInstance().im_sendMessage(chatType, chatId,MessageHistory.CONTENT_TYPE_TEXT, desc,"");
        }
        SouYueToast.makeText(this, R.string.share_success, Toast.LENGTH_LONG).show();
        SharePointInfo info = new SharePointInfo();
        info.setPlatform(SHARE_CALLBACK);
        info.setSrpId(srpId);
        info.setUrl(shareUrl);
//        http.userSharePoint(info);
        ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
        setResult(IMShareActivity.IM_SHARE_RESULT);
        finish();
    }

    private void openSrpShareDialog(final int chatType,final long chatId){
        final ImShareDialog.Builder mShareBuilder = new ImShareDialog.Builder(ShareContactActivity.this);
        if (imShareNews != null) {
            mShareBuilder.setImgHeader(imShareNews.getImgurl());
            mShareBuilder.setContent(imShareNews.getTitle());

        }
        mShareBuilder.setPositiveButton(R.string.im_dialog_ok, new ImShareDialog.Builder.ImShareDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, View v) {
                sendShareNews(chatType, chatId, mShareBuilder.desc);
            }

        }).create().show();
        return;
    }

    //新闻原创分享
    private void sendShareNews(final int chatType,final long chatId,EditText desc){
        ImserviceHelp imservice = ImserviceHelp.getInstance();
        String sharenews = null;
        if(imShareNews!=null){
            org.json.JSONObject j =new org.json.JSONObject();
            try {
                j.put("keyword", imShareNews.getKeyword());
                j.put("srpid", imShareNews.getSrpid());
                j.put("title", imShareNews.getTitle());
                j.put("url", imShareNews.getUrl());
                j.put("imgurl", imShareNews.getImgurl());
                sharenews=j.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(imShareNews.getUrl() != null && !"".equals(imShareNews.getUrl())) {
            imservice.im_sendMessage(chatType, chatId, IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE, sharenews, "");
        }else {
            imservice.im_sendMessage(chatType, chatId, IMessageConst.CONTENT_TYPE_SRP_SHARE, sharenews, "");
        }
        if(!StringUtils.isEmpty(desc.getText())){
            imservice.im_sendMessage(chatType, chatId, IMessageConst.CONTENT_TYPE_TEXT,desc.getText().toString().trim(),"");
        }

        SouYueToast.makeText(ShareContactActivity.this, getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        SharePointInfo info = new SharePointInfo();
        info.setPlatform(SHARE_CALLBACK);
        info.setSrpId(imShareNews.getSrpid());
        info.setKeyWord(imShareNews.getKeyword());
        info.setUrl(imShareNews.getUrl());
//        new Http(this).userSharePoint(info);
        ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
        setResult(IMShareActivity.IM_SHARE_RESULT);
        finish();
    }

}
