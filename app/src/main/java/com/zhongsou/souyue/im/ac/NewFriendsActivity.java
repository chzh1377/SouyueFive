package com.zhongsou.souyue.im.ac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.NewFriend;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.im.adapter.NewFriendsAdapter;
import com.zhongsou.souyue.im.asyntask.MyPhoneContactsTask;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.dialog.ImDialog.Builder.ImDialogInterface;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMApi;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.utils.ThreadPoolUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewFriendsActivity extends IMBaseActivity implements OnClickListener, OnItemClickListener {
    private TextView register, title;
    private SwipeListView swipelistview;
    private NewFriendsAdapter newfriendsadapter;
    private List<NewFriend> newFriendsList;
    private NewFriendsReceiver newFriendsReceiver;
    public static final String IM_ALLOW_ADD_ME_ACTION = "IM_ALLOW_ADD_ME_ACTION";
    public static final String IM_ALLOW_ADD_ME_DATA = "IM_ALLOW_ADD_ME_DATA";
    public static final String IM_ALLOW_ADD_ME_STATUS = "IM_ALLOW_ADD_ME_STATUS";
//    private ImProgressDialog dialog;

    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg != null) {
                List<Contact> contacts = (List<Contact>) msg.obj;
                //比较联系人是否存在手机
                if (newFriendsList != null) {
                    compareContact(contacts, newFriendsList);
                }
//    			if (dialog.isShowing()) {
//    				dialog.dismiss();
//    			}
                dismissProgress();

                newfriendsadapter.setData(newFriendsList);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_newfriendslayout);
        ImserviceHelp.getInstance().cancelNotify(-2);
        ImserviceHelp.getInstance().db_clearFriendBubble();
        initView();
//        loadData();

        //注册接受刷新列表的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(IM_ALLOW_ADD_ME_ACTION);
        registerReceiver(receiver, filter);
    }

    private void loadData() {
        //1、服务器返回新朋友
        newFriendsList = ImserviceHelp.getInstance().db_getNewFriend();
        //2、手机通讯录联系人
        myPhoneContact = new MyPhoneContactsTask(this, handler);
        myPhoneContact.execute();
    }

    private BroadcastReceiver netReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                NetworkInfo netInfo = manager.getActiveNetworkInfo();
                loadData();
//                if(netInfo != null && netInfo.isConnected() && netInfo.isAvailable()){
//                }else{
//                    SouYueToast.makeText(NewFriendsActivity.this, "无网络链接", Toast.LENGTH_LONG).show();
//                    dismissProgress();
//                }
            }
        }
    };
    private MyPhoneContactsTask myPhoneContact;

    private void initView() {
        register = findView(R.id.text_btn);
        register.setText(getResources().getString(R.string.im_clearfriends));
        register.setOnClickListener(this);
        title = findView(R.id.activity_bar_title);
        title.setText(getResources().getString(R.string.im_newfriends));
        swipelistview = (SwipeListView) findViewById(R.id.delete_lv_list);
        swipelistview.setOnItemClickListener(this);
        setReciever();
        newfriendsadapter = new NewFriendsAdapter(this, swipelistview);
        swipelistview.setAdapter(newfriendsadapter);
        swipelistview.setSwipeAble(false);

//        dialog = new ImProgressDialog.Builder(this).create();
//		if (!dialog.isShowing()) {
//			dialog.show();
//		}
        if (CMainHttp.getInstance().isNetworkAvailable(NewFriendsActivity.this)) {
            showProgress();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_btn:
                ImDialog.Builder builder = new ImDialog.Builder(v.getContext());
                builder.setMessage(getString(R.string.sure_clear_friends));
                builder.setPositiveButton("确定", new OKClickListener());
                builder.create().show();
                break;

            default:
                break;
        }
    }

    public class OKClickListener implements ImDialogInterface {

        @Override
        public void onClick(DialogInterface dialog, View v) {
            dialog.dismiss();
            if (newFriendsList != null) {
                newFriendsList.clear();
                newfriendsadapter.setData(newFriendsList);
            }
            ThreadPoolUtil.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    ImserviceHelp.getInstance().db_clearNewFriend();
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        if (newFriendsReceiver != null) {
            unregisterReceiver(newFriendsReceiver);
        }
        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
//        if (dialog.isShowing()) {
//			dialog.dismiss();
//		}
        dismissProgress();
        super.onDestroy();
    }

    private void setReciever() {
        IntentFilter inf = new IntentFilter();
        inf.addAction(BroadcastUtil.ACTION_SYS_MSG);
        newFriendsReceiver = new NewFriendsReceiver();
        registerReceiver(newFriendsReceiver, inf);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
    }

    private class NewFriendsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                loadData();
            }

        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pisotion, long arg3) {
        NewFriend newFriend = newfriendsadapter.getItem(pisotion);
        IMApi.IMGotoShowPersonPage(NewFriendsActivity.this, newFriend, PersonPageParam.FROM_IM);
    }

    @Override
    public void finish() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ImserviceHelp.getInstance().db_clearFriendBubble();
            }
        });
        super.finish();
    }

    private void compareContact(List<Contact> contacts, List<NewFriend> newFriends) {
        if (contacts != null) {
            Map<String, String> myPhoneContact = new HashMap<String, String>();
            for (int i = 0; i < contacts.size(); i++) {
                Contact contact = contacts.get(i);
                String myPhoneNum = contact.getPhone();
                String myPhoneName = contact.getNick_name();

                if (myPhoneNum != null && !"".equals(myPhoneNum)) {
                    myPhoneContact.put(myPhoneNum, myPhoneName);
                }
            }

            for (int j = 0; j < newFriends.size(); j++) {    //循环新朋友列表
                NewFriend newFriend = newFriends.get(j);
                String newFriendNum = newFriend.getBy1();
                String autoMatch = newFriend.getBy2();
                String allow_text = newFriend.getAllow_text();
                String origin = newFriend.getOrigin();
                if (newFriendNum != null && !"".equals(newFriendNum.trim())) {
                    if (!myPhoneContact.containsKey(newFriendNum)) {                //如果手机联系人里没有这个新朋友的手机号
                        //优先显示验证信息，其次显示来源
                        if (allow_text != null && !"".equals(allow_text)) {
                            newFriend.setAllow_text(allow_text);
                        } else if (origin != null && !"".equals(origin)) {
                            newFriend.setAllow_text(origin);
                        } else {
                            newFriend.setAllow_text("");
                        }
                    } else {
                        if (autoMatch != null && !"".equals(autoMatch)) {                //自动匹配
                            if (autoMatch.equals("2")) {
                                newFriend.setAllow_text(getString(R.string.im_new_friend_msgs_match_phone));
                            } else if (autoMatch.equals("1")) {
                                newFriend.setAllow_text(getString(R.string.im_new_friend_msgs_phone));
                            }
                        } else {                //添加好友
                            if (allow_text != null && !"".equals(allow_text)) {
                                newFriend.setAllow_text(allow_text);
                            } else if (origin != null && !"".equals(origin)) {
                                newFriend.setAllow_text(origin);
                            } else {
                                newFriend.setAllow_text(getString(R.string.im_new_friend_msgs_phone) + myPhoneContact.get(newFriendNum));
                            }
                        }

                    }
                }

            }

        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && IM_ALLOW_ADD_ME_ACTION.equals(action)) {
                Contact contact = (Contact) intent.
                        getSerializableExtra(IM_ALLOW_ADD_ME_DATA);
                int status = intent.getIntExtra(IM_ALLOW_ADD_ME_STATUS, 0);
                long chatId = contact.getChat_id();
                for (NewFriend newFriend : newFriendsList) {
                    long chatIdNew = newFriend.getChat_id();

                    if (chatId == chatIdNew) {
                        switch (status) {
                            case 2:
                                newFriend.setStatus(3);
                                break;
                            case 1:
                                newFriend.setStatus(1);
                                break;
                        }

                    }
                }
                newfriendsadapter.setData(newFriendsList);
            }
        }
    };

    /**
     * 跳转本界面方法
     * @param context
     */
    public static void invoke(Context context) {
        Intent intent = new Intent(context, NewFriendsActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转本界面方法
     * @param context
     */
    public static void invokeNewTask(Context context) {
        Intent intent = new Intent(context, NewFriendsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
