package com.zhongsou.souyue.im.ac;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.im.adapter.ContactsAdapter;
import com.zhongsou.souyue.im.adapter.IMShareRecentAdapter;
import com.zhongsou.souyue.im.dialog.ImShareDialog;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.im.view.AlphaSideBar;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IM分享转发界面（一级页面）
 */
public class IMShareActivity extends IMBaseActivity implements View.OnClickListener{

    public final static int IM_SHARE_RESULT = 5;

    public final static int IM_SHARE_FROM_WEB = 0;  //来自Web頁面
    public final static int IM_SHARE_FROM_IM = 100;  //来自IM
    public final static int IM_SHARE_FROM_INSTREST = 101;  //来自兴趣圈
    public final static int IM_SHARE_FROM_SRP = 102;  //来自SRP

    private ListView mListView;
    private SwipeListView mSearchListView;
    private EditText mEditText;
    private Button btnSearchClear;
    private LinearLayout llTittleBar;
    private LinearLayout headItemLayout;
    private Object editList;
    private LoadContactsTask loadContactsTask;
    private Map<String, Integer> alphaIndex;
    private AlphaSideBar alphaBar;
    private TextView txtOverlay;
    private ContactsAdapter mSearchAdapter;
    private IMShareRecentAdapter mAdapter;
    private  List<Contact> convertResult;
//    private Http http;

    private boolean isContacts;
    private Group group;
    //个人中心参数
    private Contact contactItem;
    private boolean fromFriendInfo; //是否来自好友名片
    //SRP参数
    private ImShareNews imShareNews;
    //是否來自web頁面（來自為0）
    private int fromType = -1;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_share_select_list_view);
//        http = new Http(this);
        initIntent();
        initView();
        initData();
    }

    private void initIntent() {
        //IM相关参数
        editList = getIntent().getSerializableExtra(ChatMsgEntity.FORWARD);
        isContacts = getIntent().getBooleanExtra("start_type", true);
        group = (Group) getIntent().getSerializableExtra("group_card");

        //SRP参数
        imShareNews=(ImShareNews) getIntent().getSerializableExtra(ImShareNews.NEWSCONTENT);
        //web頁面
        fromType = this.getIntent().getIntExtra("fromType", fromType);
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

        //个人中心发送名片相关参数
        contactItem = (Contact) getIntent().getSerializableExtra("contact");
        fromFriendInfo = getIntent().getBooleanExtra("fromfriendactivity",false);   //是否来自好友名片
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_tittle_bar:
//                finishAnimation(IMShareActivity.this);
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
                break;
            default:
                break;
        }
    }

//    public void finishAnimation(Activity activity) {
//        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        activity.finish();
//        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
//    }

    private void initView() {
        llTittleBar = findView(R.id.ll_tittle_bar);
        llTittleBar.setOnClickListener(this);
        btnSearchClear = (Button) findViewById(R.id.btn_search_clear);
        btnSearchClear.setVisibility(View.GONE);
        btnSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
                btnSearchClear.setVisibility(View.GONE);
            }
        });
        mEditText = findView(R.id.search_edit);
        mEditText.setHintTextColor(getResources().getColor(R.color.im_edit_text_hint_color));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                    mListView.setVisibility(View.GONE);
                    headItemLayout.setVisibility(View.GONE);
                    mSearchListView.setVisibility(View.VISIBLE);
                    btnSearchClear.setVisibility(View.VISIBLE);
                    mSearchListView.setSwipeAble(false);
                    mSearchAdapter.setShowNoValue(true);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    headItemLayout.setVisibility(View.VISIBLE);
                    mSearchListView.setVisibility(View.GONE);
                    btnSearchClear.setVisibility(View.GONE);
                    mSearchListView.setSwipeAble(true);
                    mSearchAdapter.setShowNoValue(false);
                    mSearchAdapter.setKeyWord(null);
                }
                loadContactsTask.cancel(true);
                loadContactsTask = new LoadContactsTask();
                loadContactsTask.execute(after);

            }
        });

        mListView = findView(R.id.lv_list);
        //滑动列表时隐藏软键盘
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
        mAdapter = new IMShareRecentAdapter(IMShareActivity.this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int mPos = position - mListView.getHeaderViewsCount();
                if(mPos<0) return;
                final MessageRecent mItem = mAdapter.getItem(mPos);
                if (mItem != null && editList != null) {
                    shareMsg(editList, mItem);
                }
                if(!isContacts && group != null){
                    if(mItem.getChat_type() == IConst.CHAT_TYPE_PRIVATE){
                        sendGroupCard(mItem.getChat_type(), mItem.getChat_id());
                    }else{
                        sendGroupCardToGroup(mItem.getChat_type(), mItem.getChat_id());
                    }

                }
                if(fromWhere==IM_SHARE_FROM_INSTREST||fromWhere==IM_SHARE_FROM_WEB){
                    openShareDialog(mItem.getChat_type(), mItem.getChat_id());
                }
                if(fromWhere==IM_SHARE_FROM_SRP){
                    openSrpShareDialog(mItem.getChat_type(),mItem.getChat_id());
                }
                if (contactItem != null) {//个人中心发送名片
                    sendCardFromInfo(mItem.getChat_type(),mItem.getChat_id());
                }
            }
        });

        setSlideBar();
        mSearchListView = findView(R.id.example_lv_list);
        mSearchListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        alphaIndex = new HashMap<String, Integer>();
        mSearchAdapter = new ContactsAdapter(mSearchListView, IMShareActivity.this, alphaIndex,0);
        mSearchListView.setAdapter(mSearchAdapter);
        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Contact mItem = mSearchAdapter.getItem(position);
                if (mItem != null && editList != null) {
                    shareSearchMsg(editList, mItem);
                }
                if(!isContacts && (group != null)){
                    if(mItem.getChat_type() == IConst.CHAT_TYPE_PRIVATE){
                        sendGroupCard(mItem.getChat_type(), mItem.getChat_id());
                    }else{
                        sendGroupCardToGroup(mItem.getChat_type(), mItem.getChat_id());
                    }

                }
                if(fromWhere==IM_SHARE_FROM_INSTREST||fromWhere==IM_SHARE_FROM_WEB){
                    openShareDialog(mItem.getChat_type(),mItem.getChat_id());
                }
                if(fromWhere==IM_SHARE_FROM_SRP){
                    openSrpShareDialog(mItem.getChat_type(),mItem.getChat_id());
                }
                if (contactItem != null) {//个人中心发送名片
                    sendCardFromInfo(mItem.getChat_type(),mItem.getChat_id());
                }
            }
        });
        mSearchListView.setVisibility(View.GONE);
    }

    private void initListViewHeader(View headView) {
        headItemLayout = (LinearLayout) headView.findViewById(R.id.ll_other);
//        mEditText = (EditText) headView.findViewById(R.id.search_edit);

        //手机联系人界面点击
        View myContacts = headItemLayout.findViewById(R.id.phone_friends);
        myContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAnim(ShareContactActivity.class);
            }
        });

        //群聊界面点击
        View group_chat = headItemLayout.findViewById(R.id.group_chat);
        group_chat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isContacts && group != null){
                    IMIntentUtil.gotoShowGroupCardToShareGroup(IMShareActivity.this , group);
                }else if(fromWhere==IM_SHARE_FROM_INSTREST||fromWhere==IM_SHARE_FROM_WEB){
                    IMIntentUtil.showImFriend(IMShareActivity.this, interestID, isSYFriend, interestLogo, interestName, mainPosts, type
                            , isFromBlog, shareUrl, srpId, IMIntentUtil.CIRCLE, fromType);
                }else if(fromWhere==IM_SHARE_FROM_SRP){
                    IMIntentUtil.gotoShareGroupActivity(IMShareActivity.this , imShareNews , IMIntentUtil.NEWS);
                }else if(fromFriendInfo){
                    IMIntentUtil.shareIMFriendInfoToGroup(IMShareActivity.this,contactItem,IMIntentUtil.NEWS);
                }else {
                    IMIntentUtil.startForwardAct(IMShareActivity.this, editList, IMIntentUtil.NEWS);
                }
            }
        });

    }

    private void initData() {
        //正常展示数据
        List<MessageRecent> templist = ImserviceHelp.getInstance().db_getMsgRecent();
        mAdapter.setData(templist);

        //搜索结果数据
        loadContactsTask = new LoadContactsTask();
    }

    private void setSlideBar() {
        alphaBar = (AlphaSideBar)findViewById(R.id.alphaView);
        alphaBar.setVisibility(View.GONE);
        alphaBar.setListView(mSearchListView);
        txtOverlay = (TextView)findViewById(R.id.tv_window);
        alphaBar.setTextView(txtOverlay);
    }

    // 个人中心进入——发送名片
    private void sendCardFromInfo(int chatType, long chatId) {
        ChatMsgEntity e =  getEntity(chatType,chatId);
        e.setType(IMessageConst.CONTENT_TYPE_VCARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setCard(contactItem);
        ImserviceHelp.getInstance().im_sendMessage(
                chatType,chatId, e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(IMShareActivity.this, chatType, chatId);
        finish();
    }

    /**
     * 彈出SRP分享对话框
     */
    private void openSrpShareDialog(final int chatType,final long chatId){
        final ImShareDialog.Builder mShareBuilder = new ImShareDialog.Builder(IMShareActivity.this);
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

    /**
     * 彈出兴趣圈分享对话框
     */
    private void openShareDialog(final int chatType,final long chatId) {
        final ImShareDialog.Builder sharebuilder = new ImShareDialog.Builder(IMShareActivity.this);
        sharebuilder.setImgHeader(interestLogo);
        if (!isFromBlog){
            sharebuilder.setContent(interestName);
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
                if (fromWhere == IM_SHARE_FROM_WEB){//賀卡的？
                    shareGreettingCard(chatType,chatId,sharebuilder.getDesc_text());
                }else {
                    shareToSYFriends(chatType,chatId,sharebuilder.getDesc_text());
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
//            http.userSharePoint(info);
            ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
            this.finish();
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
            imservice.im_sendMessage(chatType, chatId,IMessageConst.CONTENT_TYPE_TEXT,desc.getText().toString().trim(),"");
        }

        SouYueToast.makeText(IMShareActivity.this, getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        SharePointInfo info = new SharePointInfo();
        info.setPlatform(SHARE_CALLBACK);
        info.setSrpId(imShareNews.getSrpid());
        info.setKeyWord(imShareNews.getKeyword());
        info.setUrl(imShareNews.getUrl());
//        new Http(this).userSharePoint(info);
        ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
        finish();
    }

    //正常界面IM转发信息
    private void shareMsg(Object object, MessageRecent itemContact) {
        List<ChatMsgEntity> editList = null;
        ChatMsgEntity chatmsgentity = null;
        if (object instanceof ChatMsgEntity) {
            chatmsgentity = (ChatMsgEntity) object;
        } else {
            editList = (List<ChatMsgEntity>) object;
        }
        ImserviceHelp service = ImserviceHelp.getInstance();
        if (editList != null) {
            for (ChatMsgEntity item : editList) {
                service.im_sendMessage(itemContact.getChat_type(), itemContact.getChat_id(), item.getType(), item.getText(), "");
                setResult(RESULT_OK);
            }
        }
        if (chatmsgentity != null) {
            service.im_sendMessage(itemContact.getChat_type(), itemContact.getChat_id(), chatmsgentity.getType(), chatmsgentity.getText(), "");
        }
        IMChatActivity.isDetailOpen = ChatMsgEntity.INIT;
        IMChatActivity.isFillListData = true;
        SouYueToast.makeText(IMShareActivity.this, R.string.im_chat_forward_success, SouYueToast.LENGTH_SHORT).show();
        finish();
    }

    //搜索点击后转发信息
    private void shareSearchMsg(Object object, Contact itemContact) {
        List<ChatMsgEntity> editList = null;
        ChatMsgEntity chatmsgentity = null;
        if (object instanceof ChatMsgEntity) {
            chatmsgentity = (ChatMsgEntity) object;
        } else {
            editList = (List<ChatMsgEntity>) object;
        }
        ImserviceHelp service = ImserviceHelp.getInstance();
        if (editList != null) {
            for (ChatMsgEntity item : editList) {
                service.im_sendShareMessage(itemContact.getChat_type(), itemContact.getChat_id(), item.getType(), item.getText(), "", item.getId(), item.getFileMsgId());
                setResult(RESULT_OK);
            }
        }
        if (chatmsgentity != null) {
            service.im_sendShareMessage(itemContact.getChat_type(), itemContact.getChat_id(), chatmsgentity.getType(), chatmsgentity.getText(), "", chatmsgentity.getId(),chatmsgentity.getFileMsgId());
        }
        IMChatActivity.isDetailOpen = ChatMsgEntity.INIT;
        IMChatActivity.isFillListData = true;
        SouYueToast.makeText(IMShareActivity.this, R.string.im_chat_forward_success, SouYueToast.LENGTH_SHORT).show();
        finish();
    }

    public class LoadContactsTask extends AsyncTask<String, Void, List<Contact>> {
        private boolean needLoad;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 参数为空表示取全部，
         * 参数个数为1表示本地搜索
         * 参数个数为2表示初次取通讯录，需要通知获取网络通讯录
         * 参数个数为3表示收到更新的广播，从新获取本地通讯录
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
                long mTestTime = System.currentTimeMillis();
                data = ImserviceHelp.getInstance().db_getContact();
                Log.i("shareTest","搜索" + data.size() + "条数据，一共用了" + (System.currentTimeMillis() - mTestTime) + "毫秒");
                if (args.length == 2) {
                    needLoad = true;
                }
            } else {
                data = ImserviceHelp.getInstance().db_findLike(keyword_F);
                mSearchAdapter.setKeyWord(sourceKey.toUpperCase().replace(" ",""));
            }

            return convertResult((List<Contact>) (data == null ? Collections.emptyList() : data));
        }

        protected void onPostExecute(final List<Contact> result) {
            if (needLoad) {
                ImserviceHelp.getInstance().im_info(4, null);
            }
            if(null!=mSearchAdapter) {
                mSearchAdapter.addAll(convertResult);
                mSearchAdapter.notifyDataSetChanged();
            }
        }

        private List<Contact> convertResult(List<Contact> result) {
            convertResult = new ArrayList<Contact>();
            convertResult.addAll(result);
            return convertResult;
        }
    }

    /**
     * 进入联系人列表
     * @param clazz
     */
    private void startActivityWithAnim(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(IMShareActivity.this, clazz);
        Bundle bundle=new Bundle();
        bundle.putSerializable(ChatMsgEntity.FORWARD, (Serializable) editList);

        if(fromFriendInfo){
            bundle.putString(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_SEND_VCARD);
            bundle.putSerializable("contact", contactItem);
        }

        if(!isContacts && group != null){
            intent.putExtra("start_type", false);
            bundle.putSerializable("group_card", group);
        }
        if(fromWhere==IM_SHARE_FROM_INSTREST||fromWhere==IM_SHARE_FROM_WEB){
            intent.putExtra("interest_id", interestID);
            intent.putExtra("isSYFriend", isSYFriend);
            intent.putExtra("interest_logo", interestLogo);
            intent.putExtra("interest_name", interestName);
            intent.putExtra("Posts", mainPosts);
            intent.putExtra("type", type);
            intent.putExtra("isFromBlog", isFromBlog);
            intent.putExtra("shareUrl", shareUrl);
            intent.putExtra("srpId", srpId);
            if(fromWhere==IM_SHARE_FROM_WEB){
                intent.putExtra("fromWhere", IMShareActivity.IM_SHARE_FROM_WEB);
            }else {
                intent.putExtra("fromWhere", IMShareActivity.IM_SHARE_FROM_INSTREST);
            }
        }

        if(fromWhere==IM_SHARE_FROM_SRP){
            bundle.putSerializable(ImShareNews.NEWSCONTENT, imShareNews);
            intent.putExtra("fromWhere", IMShareActivity.IM_SHARE_FROM_SRP);
        }

        intent.putExtras(bundle);
        startActivityForResult(intent,IM_SHARE_RESULT);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    //发送群名片到私聊
    private void sendGroupCard(int chatType, long chatId){
        ChatMsgEntity e =  getEntity(chatType, chatId);
        e.setType(IMessageConst.CONTENT_TYPE_GROUP_CARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setGroup(group);
        ImserviceHelp.getInstance().im_sendMessage(chatType,chatId, e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(IMShareActivity.this, IConst.CHAT_TYPE_PRIVATE, chatId);
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
        ImserviceHelp.getInstance().im_sendMessage(IConst.CHAT_TYPE_GROUP,
                chatId, e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(IMShareActivity.this, IConst.CHAT_TYPE_GROUP, chatId);
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


    //分享搜悦新闻，原创到Im好友
    public static void startSYIMFriendAct(Activity activity,ImShareNews newsContent) {
        Intent intent = new Intent(activity, IMShareActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable(ImShareNews.NEWSCONTENT, newsContent);
        bundle.putBoolean(ContactsListActivity.START_TYPE, false);
        bundle.putString(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_SHARE_IMFRIEND);
        bundle.putBoolean(ContactsListActivity.START_FROM, false);
        intent.putExtra("fromWhere", IMShareActivity.IM_SHARE_FROM_SRP);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 其他类打开本类进行转发分享
     * @param act
     * @param editList
     */
    public static void invoke(Activity act, Object editList) {
        Intent intent = new Intent(act, IMShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatMsgEntity.FORWARD, (Serializable) editList);
        bundle.putBoolean(ContactsListActivity.START_TYPE, false);
        bundle.putString(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_FORWARD);
        bundle.putBoolean(ContactsListActivity.START_FROM, false);
        intent.putExtras(bundle);
        if (editList instanceof ChatMsgEntity) {
            act.startActivity(intent);
        } else {
            act.startActivityForResult(intent, IMChatActivity.CODE_FORWARD);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == IM_SHARE_RESULT){
            finish();
        }
        if(resultCode == IMIntentUtil.SHAREGROUP){
            finish();
        }
    }

}


