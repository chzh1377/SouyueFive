package com.zhongsou.souyue.im.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.im.db.helper.ContactDaoHelper;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Config;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.ac.AddContactsListActivity;
import com.zhongsou.souyue.im.ac.ContactsListActivity;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.ac.NewFriendsActivity;
import com.zhongsou.souyue.im.ac.PhoneContactActivity;
import com.zhongsou.souyue.im.adapter.ContactsAdapter;
import com.zhongsou.souyue.im.asyntask.MyPhoneContactsTask;
import com.zhongsou.souyue.im.dialog.ImProgressMsgDialog;
import com.zhongsou.souyue.im.dialog.ImShareDialog;
import com.zhongsou.souyue.im.dialog.ImShareDialog.Builder.ImShareDialogInterface;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ContactModelUtil;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.im.view.AlphaSideBar;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ThreadPoolUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通讯录或者好友查找<br>
 * 依据Intent传递的key=ContactsListFragment.START_TYPE的值判断<br>
 * true表示显示为通讯录<br>
 * false表示为好友查找
 *
 * @author "jianxing.fan@iftek.cn"
 *
 */
public class ContactsListFragment extends Fragment {
    public static final String START_TYPE = "start_type";
    private  List<Contact> convertResult;
    private boolean isContacts;
    private ContactsAdapter adapter;
    // private List<Contact> data;
    private Map<String, Integer> alphaIndex;

    private SwipeListView swipeListView;

    private TextView txtOverlay;
    private EditText search_edit;
    private Button btnSearchClear;
    private TextView news_count;
    private SYInputMethodManager syInputMng;
    private View ll_other;
    private AlphaSideBar alphaBar;

    private LoadContactsTask loadContactsTask;
    private ContactsBroadCastReceiver contactsBroadCastReceiver;
    private UnreadFriendBroadCastReceiver unreadFriendBroadCastReceiver; 
    private ImProgressMsgDialog dialog;

    private Contact contactItem;
    private boolean isFromTiger;
    private String sendType;
    private int tigernum;
    private static int NOTIFY = 1;
    private ImShareNews imsharenews;
    private Object editList;
    private MyPhoneContactsTask myPhoneContact;
    private Contact newcontactItem;
    private boolean showgroup;
    private boolean fromFriendInfo;
    private Group group;
    private boolean showCard;//分享个人、群名片 ,true为分享
    private String SHARE_CALLBACK = "10";
//    private boolean showServiceMessage;]

    private Handler handler = new Handler(){
    	
    	public void handleMessage(android.os.Message msg) {
    		if(msg != null)	{
    			List<Contact> myContacts = (List<Contact>) msg.obj;
    			//比较联系人是否存在手机
    			if(convertResult!=null){
    			    compareContact(myContacts,convertResult);
    			}
    			if(null != adapter) {
    				adapter.setData(convertResult);
    			}
    		}
    	}

    };


    public class ContactsBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context mContext, Intent intent) {
            LogDebugUtil.v("fan", "ContactsListFragment:通讯录变化");
        }
    }

    // 读取未读的新朋友数量的监听
    public class UnreadFriendBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updataNewsCount();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        swipeListView.setAdapter(null);
        swipeListView = null;
        adapter.clear();
        adapter = null;
        alphaBar = null;
        alphaBar = null;
        if (txtOverlay != null) {
            WindowManager mWindowManager = (WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.removeView(txtOverlay);
        }
        txtOverlay = null;
        syInputMng.hideSoftInput();
        syInputMng = null;
        getActivity().unregisterReceiver(contactsBroadCastReceiver);
        getActivity().unregisterReceiver(unreadFriendBroadCastReceiver);
        contactsBroadCastReceiver = null;
        unreadFriendBroadCastReceiver = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater
                .inflate(R.layout.im_swipe_contacts_list_view_fragment,
                        container, false);

        isContacts = getActivity().getIntent()
                .getBooleanExtra(START_TYPE, true);
        isFromTiger = getActivity().getIntent().getBooleanExtra(ContactsListActivity.START_FROM, false);
        contactItem = (Contact) getActivity().getIntent().getSerializableExtra(
                "contact");
        sendType = getActivity().getIntent().getStringExtra(
        		IMChatActivity.KEY_ACTION);
        tigernum = getActivity().getIntent().getIntExtra(ConstantsUtils.SHARE_COUNT, 0);
        group = (Group) getActivity().getIntent().getSerializableExtra("group_card");
        imsharenews=(ImShareNews) getActivity().getIntent().getSerializableExtra(ImShareNews.NEWSCONTENT);
        editList= getActivity().getIntent().getSerializableExtra(ChatMsgEntity.FORWARD);
        showgroup = getActivity().getIntent().getBooleanExtra(ContactsListActivity.SHOWGROUPCHAT,false);
        fromFriendInfo = getActivity().getIntent().getBooleanExtra(ContactsListActivity.FROMFRIENDINFOACTIVITY,false);
        showCard = getActivity().getIntent().getBooleanExtra(ContactsListActivity.SHOWCARD , false);
//        showServiceMessage = getActivity().getIntent().getBooleanExtra(ContactsListActivity.SHOWSERVICEMESSAGE,false);
               
        // listView
        swipeListView = (SwipeListView) root.findViewById(R.id.example_lv_list);
        View convertView = inflater
                .inflate(R.layout.add_titlebar_contacts_swipe_list,
                        swipeListView, false);
        initListViewHeader(convertView);
        swipeListView.addHeaderView(convertView);
        alphaIndex = new HashMap<String, Integer>();
        adapter = new ContactsAdapter(swipeListView, getActivity(), alphaIndex,
                swipeListView.getRightViewWidth());
        swipeListView.setAdapter(adapter);

        swipeListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        syInputMng.hideSoftInput();
                        if (parent instanceof SwipeListView) {
                            SwipeListView sl = ((SwipeListView) parent);
                            final Contact itemContact = adapter.getItem(position
                                    - sl.getHeaderViewsCount());
                            //分享新闻
                            if (!StringUtils.isEmpty(sendType) && sendType.equals(IMChatActivity.ACTION_SHARE_IMFRIEND)) {
                                search_edit.clearFocus();
                                final ImShareDialog.Builder sharebuilder = new ImShareDialog.Builder(getActivity());
                                if (imsharenews != null) {
                                    sharebuilder.setImgHeader(imsharenews.getImgurl());
                                    sharebuilder.setContent(imsharenews.getTitle());

                                }
                                sharebuilder.setPositiveButton(R.string.im_dialog_ok, new ImShareDialogInterface() {
                                    @Override
                                    public void onClick(DialogInterface dialog, View v) {
                                        sendShareNews(itemContact, sharebuilder.desc);
                                    }

                                }).create().show();
                                return;
                            }
                            //转发信息
                            if (!StringUtils.isEmpty(sendType) && sendType.equals(IMChatActivity.ACTION_FORWARD)) {
                                if (itemContact != null && editList != null) {
                                    forwardMsg(editList, itemContact);
                                }
                            }

                            if (itemContact == null)
                                return;
                            /**
                             * 重新拼装，在兼容老版本的基础之上，支持显示通讯录中的名称
                             */
                    /*        if(null != itemContact) {
                            	 newcontactItem = new Contact();
                                 newcontactItem.setAvatar(itemContact.getAvatar());
                                 newcontactItem.setChat_id(itemContact.getChat_id());
                                 newcontactItem.setMyid(itemContact.getMyid());
                                 newcontactItem.setChat_type(itemContact.getChat_type());
                                 newcontactItem.setComment_name(itemContact.getComment_name());
                                 newcontactItem.setNick_name(itemContact.getComment_name());
                                 newcontactItem.setLocal_order(itemContact.getLocal_order());
                                 newcontactItem.setPhone(itemContact.getPhone());
                                 newcontactItem.setStatus(itemContact.getStatus());
                            }*/

                            if (!isContacts) {
                                if (itemContact.getChat_type() == IConst.CHAT_TYPE_PRIVATE){
                                    if (contactItem != null) {
                                        sendCardFromInfo(itemContact);
                                    } else if (isFromTiger) {
                                        sendCardFromTigerGame(itemContact, sendType);
                                    } else if (group != null) {
                                        sendGroupCard(itemContact);
                                    } else {
                                        sendCardFromImChat(itemContact);
                                    }
                                }else if (itemContact.getChat_type() == IConst.CHAT_TYPE_GROUP){
                                    Group group1 = new Group();
                                    group1.setGroup_id(itemContact.getChat_id());
                                    group1.setGroup_nick_name(itemContact.getNick_name());
                                    group1.setGroup_avatar(itemContact.getAvatar());
                                    if (contactItem != null) {
                                        ImUtils.sendCardFromInfo(getActivity(),group1,contactItem);
                                    } else if (isFromTiger) {
                                        ImUtils.sendCardFromTigerGame(getActivity(),group1, sendType,isFromTiger,tigernum);
                                    } else if (group != null) {
                                       ImUtils.sendGroupCard(getActivity(),group1,group);
                                    }else if (showCard){
                                        //这里用回退的方法做  setresult  把群名片的信息带到群里去  调 群 或者 私聊的 sendcard方法
                                        long num = ImserviceHelp.getInstance().db_findMemberCountByGroupid(itemContact == null?group1.getGroup_id():group1.getGroup_id());
                                        group1.setMemberCount((int) num);
                                        ImUtils.sendCardToChat(getActivity(),group1);
                                    }else {
                                        ImUtils.sendCardFromImChat(getActivity(),group1);
                                    }

                                }
                                return;
                            }
                            if (sl.getIsShown()) {
                                sl.onChanged();
                                return;
                            }
                            //跳转到聊天界面  新修改
                            if(itemContact.getChat_type() == 0){
                                IMChatActivity.invoke(getActivity(), IConst.CHAT_TYPE_PRIVATE, itemContact.getChat_id());
                            }else if (itemContact.getChat_type() == 1){
                                Group group1 = new Group();
                                group1.setGroup_id(itemContact.getChat_id());
                                group1.setGroup_nick_name(itemContact.getNick_name());
                                group1.setGroup_avatar(itemContact.getAvatar());
                                IMIntentUtil.gotoGroupChatActivity(getActivity(),group1,0);
                            }
                        }
                    }
                });
        swipeListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                syInputMng.hideSoftInput();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        // listview-end

        setUpContactsSearch(root);
        if (dialog == null)
            dialog = new ImProgressMsgDialog.Builder(getActivity()).create();
        dialog.show();
        loadContactsTask = new LoadContactsTask();
        loadContactsTask.execute("", "");// 首次获取

        contactsBroadCastReceiver = new ContactsBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastUtil.ACTION_CONTACT);
        filter.addAction(BroadcastUtil.ACTION_CONTACT_AND_MSG);
        getActivity().registerReceiver(contactsBroadCastReceiver, filter);

        IntentFilter unfilter = new IntentFilter(BroadcastUtil.ACTION_SYS_MSG);
        unreadFriendBroadCastReceiver = new UnreadFriendBroadCastReceiver();
        getActivity().registerReceiver(unreadFriendBroadCastReceiver, unfilter);

        syInputMng = new SYInputMethodManager(getActivity());
        return root;
    }

    // 从用户信息进入--更多--发送名片
    private void sendCardFromInfo(Contact itemContact) {

        ChatMsgEntity e =  getEntity(itemContact);
        e.setType(IMessageConst.CONTENT_TYPE_VCARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setCard(contactItem);
        ImserviceHelp.getInstance().im_sendMessage(itemContact.getChat_type(),
                itemContact.getChat_id(), e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(getActivity(), IConst.CHAT_TYPE_PRIVATE, itemContact.getChat_id());
        getActivity().finish();
    }

    //发送群名片到私聊
    private void sendGroupCard(Contact itemContact){

        ChatMsgEntity e =  getEntity(itemContact);
        e.setType(IMessageConst.CONTENT_TYPE_GROUP_CARD);
        e.status = IMessageConst.STATUS_SENTING;
        e.setGroup(group);
        ImserviceHelp.getInstance().im_sendMessage(itemContact.getChat_type(),
                itemContact.getChat_id(), e.getType(), e.getText(), e.getRetry());
        IMChatActivity.invoke(getActivity(), IConst.CHAT_TYPE_PRIVATE, itemContact.getChat_id());
        getActivity().finish();
    }

    // 从聊天页面进入--发送名片
    private void sendCardFromImChat(Contact itemContact) {
        Intent backIntent = new Intent();
        backIntent.putExtra(IMChatActivity.KEY_GET_CARD_ID, itemContact);
        getActivity().setResult(Activity.RESULT_OK, backIntent);
        getActivity()
                .overridePendingTransition(R.anim.left_in, R.anim.left_out);
        getActivity().finish();
    }
    //新闻原创分享
    private void sendShareNews(Contact itemContact,EditText desc){
        ImserviceHelp imservice = ImserviceHelp.getInstance();
        String sharenews = null;
        if(imsharenews!=null){
            JSONObject j =new JSONObject();
            try {
                j.put("keyword", imsharenews.getKeyword());
                j.put("srpid", imsharenews.getSrpid());
                j.put("title", imsharenews.getTitle());
                j.put("url", imsharenews.getUrl());
                j.put("imgurl", imsharenews.getImgurl());
                sharenews=j.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(imsharenews.getUrl() != null && !"".equals(imsharenews.getUrl())) {
        	imservice.im_sendMessage(itemContact.getChat_type(), itemContact.getChat_id(), IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE, sharenews, "");
        }else {
        	imservice.im_sendMessage(itemContact.getChat_type(), itemContact.getChat_id(), IMessageConst.CONTENT_TYPE_SRP_SHARE, sharenews, "");
        }
        if(!StringUtils.isEmpty(desc.getText())){
            imservice.im_sendMessage(itemContact.getChat_type(), itemContact.getChat_id(), IMessageConst.CONTENT_TYPE_TEXT,desc.getText().toString().trim(),"");
        }
        
        SouYueToast.makeText(getActivity(), getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        SharePointInfo info = new SharePointInfo();
        info.setPlatform(SHARE_CALLBACK);
        info.setSrpId(imsharenews.getSrpid());
        info.setKeyWord(imsharenews.getKeyword());
        info.setUrl(imsharenews.getUrl());
//        new Http(this).userSharePoint(info);
        ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID, null, info);
        getActivity().finish();
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
                getActivity().setResult(getActivity().RESULT_OK);
            }
        }
        if(chatmsgentity!=null){
            service.im_sendMessage(itemContact.getChat_type(), itemContact.getChat_id(), chatmsgentity.getType(), chatmsgentity.getText(), "");
        }
        SouYueToast.makeText(getActivity(), R.string.im_chat_forward_success, SouYueToast.LENGTH_SHORT).show();
        getActivity().finish();
    }
    /**
     * 从老虎机页面进入
     *
     * @param itemContact
     *            传递的联系人信息
     * @param sendType
     *            1 ACTION_ASK_COIN 索要中搜币 2 ACTION_ASK_SHARE分享
     */
    private void sendCardFromTigerGame(Contact itemContact, String sendType) {
        Intent intent = new Intent(getActivity(), IMChatActivity.class);
        intent.putExtra(IMChatActivity.KEY_ACTION, sendType);
        intent.putExtra(IMChatActivity.KEY_CONTACT, itemContact);
        intent.putExtra(ContactsListActivity.START_FROM, isFromTiger);
        intent.putExtra(ConstantsUtils.SHARE_COUNT, tigernum);
        startActivity(intent);
        getActivity()
                .overridePendingTransition(R.anim.left_in, R.anim.left_out);
        getActivity().finish();

    }

    private void initListViewHeader(View convertView) {
        ll_other = convertView.findViewById(R.id.ll_other);
        if(!isContacts){
            convertView.findViewById(R.id.new_friends).setVisibility(View.GONE);
            convertView.findViewById(R.id.phone_friends).setVisibility(View.GONE);
            convertView.findViewById(R.id.rl_service_message).setVisibility(View.GONE);
            if(convertView.findViewById(R.id.im_newfriend_under_line)!=null){
                convertView.findViewById(R.id.im_newfriend_under_line).setVisibility(View.GONE);
            }
            if(convertView.findViewById(R.id.im_phonefriend_under_line)!=null){
                convertView.findViewById(R.id.im_phonefriend_under_line).setVisibility(View.GONE);
            }

            if(convertView.findViewById(R.id.im_groupchat_under_line)!=null){
                convertView.findViewById(R.id.im_groupchat_under_line).setVisibility(View.GONE);
            }
        }
        if(showgroup || isFromTiger){
            convertView.findViewById(R.id.group_chat).setVisibility(View.GONE);
        }

        search_edit = (EditText) convertView.findViewById(R.id.search_edit);
        search_edit.setHintTextColor(getResources().getColor(R.color.im_edit_text_hint_color));
        search_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager im = ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
//                im.showSoftInputFromInputMethod(getActivity().getCurrentFocus().getApplicationWindowToken(),InputMethodManager.SHOW_IMPLICIT);
                    im.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        btnSearchClear = (Button) convertView.findViewById(R.id.btn_search_clear);
        btnSearchClear.setVisibility(View.GONE);
        btnSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit.setText("");
                btnSearchClear.setVisibility(View.GONE);
            }
        });
        search_edit.addTextChangedListener(new TextWatcher() {
            String before = null;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                before = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String after = s.toString();        //这里区分通讯录和其他搜索情况
                if (!isFromTiger) {
                    if (!TextUtils.isEmpty(after))
                        ll_other.findViewById(R.id.group_chat).setVisibility(View.GONE);
                    else
                        ll_other.findViewById(R.id.group_chat).setVisibility(View.VISIBLE);
                }
                if (before != null && before.equals(after)) {// 没有改变
                    return;
                }
//                if (isContacts) {
                    if (!TextUtils.isEmpty(after)) {// 有输入
                        swipeListView.setSwipeAble(false);
                        adapter.setShowNoValue(true);
                        loadContactsTask.cancel(true);
                        loadContactsTask = new LoadContactsTask();
                        loadContactsTask.execute(after);

                        ll_other.setVisibility(View.GONE);
                        alphaBar.setVisibility(View.GONE);
                        btnSearchClear.setVisibility(View.VISIBLE);

                    } else {
                        ll_other.setVisibility(View.VISIBLE);
                        alphaBar.setVisibility(View.VISIBLE);
                        btnSearchClear.setVisibility(View.GONE);
                        swipeListView.setSwipeAble(true);
                        adapter.setShowNoValue(false);
                        adapter.setKeyWord(null);

                        loadContactsTask.cancel(true);
                        loadContactsTask = new LoadContactsTask();
                        loadContactsTask.execute("", "");
                    }
//                }


            }
        });
        //新朋友界面点击
        View new_friends = ll_other.findViewById(R.id.new_friends);
        new_friends.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityWithAnim(NewFriendsActivity.class);
                ThreadPoolUtil.getInstance().execute(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        ImserviceHelp.getInstance().db_clearFriendBubble();
                    }
                });
            }
        });
        news_count = (TextView) new_friends.findViewById(R.id.news_count);
        //手机联系人界面点击
        View phone_friends = ll_other.findViewById(R.id.phone_friends);
        phone_friends.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityWithAnim(PhoneContactActivity.class);
            }
        });
        //群聊界面点击
        View group_chat = ll_other.findViewById(R.id.group_chat);
        if(isContacts){
            group_chat.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    IMIntentUtil.gotoGroupList(getActivity());
                }
            });
        }else{
            if(!StringUtils.isEmpty(sendType)&&sendType.equals(IMChatActivity.ACTION_FORWARD)){
                group_chat.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        IMIntentUtil.startForwardAct(getActivity(),editList,IMIntentUtil.NEWS);
                    }
                });
            }else if(fromFriendInfo){
                group_chat.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        IMIntentUtil.shareIMFriendInfoToGroup(getActivity(),contactItem,IMIntentUtil.NEWS);
                    }
                });
            }else if(group != null){
                group_chat.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        IMIntentUtil.gotoShowGroupCardToShareGroup(getActivity() , contactItem , group);
                    }
                });
            }else if(showCard){
                group_chat.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IMIntentUtil.gotoShowCardShareGroup(getActivity());
                    }
                });
            }else{
                group_chat.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        IMIntentUtil.gotoShareGroupActivity(getActivity() , imsharenews , IMIntentUtil.NEWS);
                    }
                });
            }
        }
        //服务号界面点击
        View service_message = ll_other.findViewById(R.id.rl_service_message);
        service_message.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IMIntentUtil.gotoShowServiceMessageAc(getActivity());
            }
        });

    }

    private void startActivityWithAnim(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        startActivity(intent);
        getActivity()
                .overridePendingTransition(R.anim.left_in, R.anim.left_out);
        swipeListView.onChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        updataNewsCount();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();

    }
		
    // 更新未读数
    private void updataNewsCount() {
        Config c = ImserviceHelp.getInstance().db_getConfig();
        long unread = 0;
        if (c != null)
            unread = c.getFriend_bubble();
        news_count.setText(ImUtils.getBubleText(unread + ""));
        news_count.setVisibility(unread > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private void setUpContactsSearch(View root) {
        ((TextView) root.findViewById(R.id.activity_bar_title))
                .setText(isContacts ? R.string.contacts_list
                        : R.string.contacts_search);
        search_edit.setHint((showgroup || isFromTiger) ? R.string.search_no_group : R.string.search_has_group);
        ImageView addFriend = (ImageView) root.findViewById(R.id.text_btn);
        if (isContacts) {
            root.findViewById(R.id.im_contact_friend).setVisibility(View.VISIBLE);
            root.findViewById(R.id.im_find_friend).setVisibility(View.GONE);
            addFriend.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ContactsListFragment.this.startAddIntent();
                }
            });

        } else {
            addFriend.setVisibility(View.GONE);
//			root.findViewById(R.id.alphaView).setVisibility(View.GONE);
            root.findViewById(R.id.im_contact_friend).setVisibility(View.GONE);
            root.findViewById(R.id.im_find_friend).setVisibility(View.VISIBLE);
//            ll_other.setVisibility(View.GONE);
            swipeListView.setSwipeAble(false);
        }
        setSlideBar(root);
    }

    protected void startAddIntent() {
        Intent intent = new Intent(getActivity(), AddContactsListActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private void setSlideBar(View root) {
        alphaBar = (AlphaSideBar) root.findViewById(R.id.alphaView);
        alphaBar.setListView(swipeListView);
        txtOverlay = (TextView) LayoutInflater.from(getActivity()).inflate(
                R.layout.alpha_window, null);
        txtOverlay.setVisibility(View.INVISIBLE);
        alphaBar.setTextView(txtOverlay);
        WindowManager mWindowManager = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(txtOverlay, lp);
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
         * 参数为空表示取全部， 参数个数为1表示本地搜索 参数个数为2表示初次取通讯录，需要通知获取网络通讯录
         * 参数个数为3表示收到更新的广播，从新获取本地通讯录
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
                if (isFromTiger) {
                    data = ContactDaoHelper.getInstance(
                            MainApplication.getInstance())
                            .findLike(muid, keyword_F);
                }else {
                    data = ImserviceHelp.getInstance().db_findLike(keyword_F);
                }
                adapter.setKeyWord(sourceKey.toUpperCase().replace(" ",""));
            }

            if (args.length == 1){
                return convertResultSearch((List<Contact>) (data == null ? Collections.emptyList() : data));
            }else {
                return convertResult((List<Contact>) (data == null ? Collections.emptyList() : data));
            }
        }

        protected void onPostExecute(final List<Contact> result) {

            if (needLoad) {
                ImserviceHelp.getInstance().im_info(4, null);
            }
            if(null!=adapter) {
            	adapter.addAll(convertResult);
                adapter.notifyDataSetChanged();

//                swipeListView.setVisibility(View.VISIBLE);
//                search_edit.requestFocus();
//                InputMethodManager imm = ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
//                imm.showSoftInputFromInputMethod(search_edit.getWindowToken(), 0);

                dialog.dismiss();
//                if (isContacts) {
//                	loadData();
//                }
            }
            
          
        }

        private List<Contact> convertResultSearch(List<Contact> result) {
            convertResult = new ArrayList<Contact>();
            convertResult.addAll(result);
            return convertResult;
        }

        private List<Contact> convertResult(List<Contact> result) {
            convertResult = new ArrayList<Contact>();
            boolean isSearch = false;
            if (!TextUtils.isEmpty(search_edit.getText().toString())) {
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
//            if (isSearch && convertResult.size() > 0) {
//                Contact friendItem = new Contact();
//                friendItem.setComment_name("好友");
//                convertResult.add(0, friendItem);
//            }
            return convertResult;
        }
    }
    
    private void loadData() {
		//1、手机通讯录联系人
		myPhoneContact = new MyPhoneContactsTask(getActivity(), handler);
		myPhoneContact.execute();
    }
    
    
    /**
     * 
     * @param contacts 手机联系人
     * @param serverContacts 服务器数据
     */
    private void compareContact(List<Contact> contacts,List<Contact> serverContacts) {
    	if(contacts != null ) {
    		for(int i = 0 ;i < serverContacts.size();i++) {
    			Contact newContact = serverContacts.get(i);
    			String newFriendNum;
					if(StringUtils.isNotEmpty(newContact.getPhone())
							&&StringUtils.isEmpty(newContact.getBy1())
							){
						newFriendNum= newContact.getPhone();
						for(int j=0;j<contacts.size();j++){
							if(StringUtils.isNotEmpty(newFriendNum)&&
									StringUtils.isNotEmpty(contacts.get(j).getPhone())
									&&contacts.get(j).getPhone().equals(newFriendNum)){

                                if(newContact.getComment_name() == null && "".equals(newContact.getComment_name())) {
                                    newContact.setComment_name(contacts.get(j).getNick_name());
                                    ImserviceHelp.getInstance().db_updateCommentName(serverContacts.get(i).getChat_id(),contacts.get(j).getNick_name());
                                }

							}
						}
						
					}
				} 
					
    	}
	}


    /**
     * 获取消息对象
     * @param contact
     * @return
     */
    private ChatMsgEntity getEntity(Contact contact) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.userId = Long.parseLong(SYUserManager.getInstance().getUserId());
        entity.chatId = contact.getChat_id();
        entity.setSendId(Long.parseLong(SYUserManager.getInstance().getUserId()));
        entity.setChatType(contact.getChat_type());
        entity.setIconUrl(SYUserManager.getInstance().getUser().image());
        return entity;
    }

}
