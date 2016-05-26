package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.PushService;
import com.tuita.sdk.im.db.module.Config;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.LoginInputPhoneNumActivity;
import com.zhongsou.souyue.activity.ScaningActivity;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.im.ac.AddContactsListActivity;
import com.zhongsou.souyue.im.ac.ContactsListActivity;
import com.zhongsou.souyue.im.ac.CreateGroupInviteActivity;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.ac.IMSearchActivity;
import com.zhongsou.souyue.im.ac.IMSouYueMessageActivity;
import com.zhongsou.souyue.im.adapter.ChatAdapter;
import com.zhongsou.souyue.im.dialog.ImContactDialog;
import com.zhongsou.souyue.im.dialog.ImLongClickDialog;
import com.zhongsou.souyue.im.dialog.ImProgressDialog;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.ThreadPoolUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gengsong@zhongsou.com
 * @version 4.0
 * @ClassName: MsgTabFragment
 * @Description: 消息
 * @date 2014年8月28日 下午20:56
 */
public class MsgTabFragment extends BaseTabFragment implements View.OnClickListener {

    private View root;
    private SwipeListView swipelistview;
    private ChatAdapter chatAdapter;
    private Activity activity;
    private MessageRecentReceiver messageRecentReceiver;
    private LogoutReceiver getLogoutreceiver = new LogoutReceiver();
    private RelativeLayout network_state;
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;

    private ImageButton msg_tab_contact_imgbtn, msg_tab_more_imgbtn;
    private PopupWindow mTabMsgPopupWindow;
    private RelativeLayout tab_topbar_msg_layout;
    private LinearLayout llLogin;
    private RelativeLayout common_right_parent;
    private TextView news_count;
    private UnreadFriendBroadCastReceiver unreadFriendBroadCastReceiver;
    private ImageView ll_im_nodata;
    private boolean no_data = true;

    private NotifyMainListener mNotifyMainListener;

    private EditText search_edit;
    long lastTime = 0;

    private View mView;//存放临时的view  当view被点击时

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    int sum = 0;//气泡数
                    List<MessageRecent> result = (List<MessageRecent>) msg.obj;
                    sum = msg.arg1;
                    if (result != null) {
                        chatAdapter.setData(result);
                        mNotifyMainListener.showRedNum(sum);
                        exitSwitchPage(IntentUtil.isLogin());
                    }
                    break;
                case 1: //取消点击效果
                    if (mView != null) {
                        mView.setBackgroundColor(getResources().getColor(R.color.discover_bg));
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mNotifyMainListener = (NotifyMainListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.im_chat_frament_tab_msg_layout, container, false);
        initView(inflater);
        return root;
    }

    private void showLCDialog(int position) {
        ImLongClickDialog.Builder longclickbuilder = new ImLongClickDialog.Builder(activity);
        longclickbuilder.setTop(true);
        MessageRecent item = chatAdapter.getItem(position - 1);
        longclickbuilder.setMessageRecentList(item, updateToTopListInterface);
        longclickbuilder.create().show();
    }

    ImLongClickDialog.UpdateToTopListInterface updateToTopListInterface = new ImLongClickDialog.UpdateToTopListInterface() {
        @Override
        public void updateToTopList(MessageRecent messageRecent) {
            //更新列表
            exitSwitchPage(IntentUtil.isLogin());
            chatAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        exitSwitchPage(IntentUtil.isLogin());
    }

    private void initView(LayoutInflater inflater) {
        ((TextView) root.findViewById(R.id.activity_bar_title)).setText(getString(R.string.tab_msg));
        common_right_parent = (RelativeLayout) root.findViewById(R.id.common_right_parent);
        msg_tab_contact_imgbtn = (ImageButton) root.findViewById(R.id.msg_tab_contact_imgbtn);
        msg_tab_contact_imgbtn.setOnClickListener(this);
        msg_tab_more_imgbtn = (ImageButton) root.findViewById(R.id.msg_tab_more_imgbtn);
        msg_tab_more_imgbtn.setOnClickListener(this);
        tab_topbar_msg_layout = (RelativeLayout) root.findViewById(R.id.in_title_bar);
        swipelistview = (SwipeListView) root.findViewById(R.id.delete_lv_list);
        llLogin = (LinearLayout) root.findViewById(R.id.im_top_msg_layout);
        news_count = (TextView) root.findViewById(R.id.news_count);
        ll_im_nodata = (ImageView) root.findViewById(R.id.ll_im_nodata);
        network_state = (RelativeLayout) root.findViewById(R.id.network_state);

        View convertView = inflater.inflate(R.layout.msgtab_swipe_list_header,
                swipelistview, false);
        initListViewHeader(convertView);
        swipelistview.addHeaderView(convertView);

        network_state.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_SETTINGS));
                    activity.overridePendingTransition(R.anim.left_in,
                            R.anim.left_out);
                } else {
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    activity.overridePendingTransition(R.anim.left_in,
                            R.anim.left_out);
                }
            }

        });

        if (getActivity() != null) {
            activity = getActivity();
        }
        chatAdapter = new ChatAdapter(activity, swipelistview, this);
        swipelistview.setAdapter(chatAdapter);
        swipelistview.setRightViewWidth(DeviceUtil.dip2px(activity, 169));
        swipelistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showLCDialog(position);
                return true;
            }
        });

        swipelistview
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        view.setBackgroundColor(getResources().getColor(R.color.im_friends_already_friends_status_color));
                        mView = view;
                        mHandler.sendEmptyMessageDelayed(1,50);
                        if (position == 0) return;
                        MessageRecent item = chatAdapter.getItem(position - 1);

                        if (item.getJumpType() == MessageRecent.ITEM_JUMP_SERVICE_LIST) {
                            IMSouYueMessageActivity.invoke(getActivity(), item.getChat_id());
                        } else {
                            if (item.getChat_type() == IConst.CHAT_TYPE_SERVICE_MESSAGE) {  //服务号
                                UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.IM_LIST_SERVICE_CLICK);
                                UpEventAgent.onZSIMServiceListClick(getActivity(), item.getChatName(), item.getChat_id());  //ZSSDK
                            }
                            IMChatActivity.invoke(getActivity(), item.getChat_type(), item.getChat_id());
                        }
                    }
                });

    }

    private void initListViewHeader(View convertView) {

        search_edit = (EditText) convertView.findViewById(R.id.search_edit);
        search_edit.setHintTextColor(getResources().getColor(R.color.im_edit_text_hint_color));
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_edit.getWindowToken(), 0);
        search_edit.setInputType(0);
        //不用onclickListener 防止第一次點擊不响应
        search_edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    UmengStatisticUtil.onEvent(getActivity(),UmengStatisticEvent.IM_SEARCH_CLICK);
                    startActivityWithAnim(IMSearchActivity.class);
                }
                return false;
            }
        });
    }

    public void exitSwitchPage(boolean isLogin) {
        List<MessageRecent> templist = ImserviceHelp.getInstance().db_getMessageRecent();
        chatAdapter.setData(templist);
        if (templist == null) {
            no_data = true;
        } else {
            if (templist.size() == 0)
                no_data = true;
            else
                no_data = false;
        }

        if (no_data) {
            llLogin.setVisibility(View.GONE);
            common_right_parent.setVisibility(View.VISIBLE);
            ll_im_nodata.setVisibility(View.VISIBLE);
        } else {
            llLogin.setVisibility(View.VISIBLE);
            common_right_parent.setVisibility(View.VISIBLE);
            ll_im_nodata.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_tab_contact_imgbtn:
                if (IntentUtil.isLogin()) {
                    UmengStatisticUtil.onEvent(activity,UmengStatisticEvent.IM_CONTACT_CLICK);
                    news_count.setVisibility(View.INVISIBLE);
                    gotoContactsList();
                } else {
                    IMIntentUtil.gotoShowServiceMessageAc(getActivity());
                }
                break;

            case R.id.msg_tab_more_imgbtn:
                if (IntentUtil.isLogin()) {
                    UmengStatisticUtil.onEvent(activity, UmengStatisticEvent.IM_ADD_MENU_CLICK);
                    createMorePupWindow();
                    mTabMsgPopupWindow.showAtLocation(tab_topbar_msg_layout, Gravity.RIGHT
                            | Gravity.TOP, 9, (int) (tab_topbar_msg_layout.getBottom() * 1.4));
                } else {
                    IntentUtil.goLogin(activity, true);
                }
                break;
            case R.id.im_chat_scanning_layout:
                gotoSinning();
                dismissPupWindows();
                break;
            case R.id.im_chat_create_im_layout:
                gotoCreateIm();
                dismissPupWindows();
                break;
            case R.id.im_chat_add_friend_layout:
                gotoAddFriends();
                dismissPupWindows();
                break;
            case R.id.btn_my_login:
                IntentUtil.goLogin(activity, true);
                break;
            case R.id.btn_my_reg:
                Intent phoneIntent = new Intent(context, LoginInputPhoneNumActivity.class);
                phoneIntent.putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.PHONEREG);
                startActivity(phoneIntent);
                context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            default:
                break;
        }
    }

    private void createMorePupWindow() {
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.tab_msg_im_more_pop, null);
        mTabMsgPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTabMsgPopupWindow.setFocusable(true);
        mTabMsgPopupWindow.setOutsideTouchable(true);
        mTabMsgPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupView.findViewById(R.id.im_chat_scanning_layout).setOnClickListener(this);
        popupView.findViewById(R.id.im_chat_create_im_layout).setOnClickListener(this);
        popupView.findViewById(R.id.im_chat_add_friend_layout).setOnClickListener(this);
    }

    /**
     * 弹出框消失
     */
    private void dismissPupWindows() {
        if (null != mTabMsgPopupWindow) {
            mTabMsgPopupWindow.dismiss();
        }
    }

    /**
     * 二维码扫描页
     */
    private void gotoSinning() {
        Intent intent = new Intent(activity, ScaningActivity.class);
        startActivity(intent);
    }

    /**
     * 创建群聊
     */
    private void gotoCreateIm() {
        Intent intent = new Intent(getActivity(), CreateGroupInviteActivity.class);
        startActivity(intent);
    }

    /**
     * 添加好友
     */
    private void gotoAddFriends() {
        Intent intent = new Intent(activity, AddContactsListActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 通讯录
     */
    private void gotoContactsList() {
        startActivityWithAnim(ContactsListActivity.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        PushService.setMsgNum(MainApplication.getInstance());
        PushService.setIsInChat(MainApplication.getInstance(), 0);
        loadData();
        updataNewsCount();
        setReciever();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void startActivityWithAnim(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    public void loadData() {
        //用线程池获取最近聊天记录
        ThreadPoolUtil.getInstance().execute(mLoadDataRunnable);
    }

    /**
     * 刷新列表页数据  runnable
     */
    private Runnable mLoadDataRunnable = new Runnable() {

        @Override
        public void run() {
            int sum = 0;
            List<MessageRecent> result = ImserviceHelp.getInstance().db_getMessageRecent();
            if (result != null) {
                for (MessageRecent recent : result) {
                    sum += recent.isNotify() && recent.getJumpType() == MessageRecent.ITEM_JUMP_IMCHAT ? recent.getBubble_num() : 0;
                }

                Config c = ImserviceHelp.getInstance().db_getConfig();
                if (c != null) {
                    sum += c.getFriend_bubble();
                }
            }
            Message message = new Message();
            message.obj = result;
            message.what = 0;
            message.arg1 = sum;
            mHandler.sendMessage(message);
        }
    };

    private class MessageRecentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (System.currentTimeMillis() - lastTime < 1000) {
                mHandler.removeCallbacks(mLoadDataRunnable);
            } else {
                mHandler.post(mLoadDataRunnable);
                lastTime = System.currentTimeMillis();
                return;
            }
            mHandler.postDelayed(mLoadDataRunnable, 1000);
        }

    }

    private void setReciever() {
        IntentFilter inf = new IntentFilter();
        inf.addAction(BroadcastUtil.ACTION_MSG_ADD);
        inf.addAction(BroadcastUtil.ACTION_CONTACT_AND_MSG);
        inf.addAction(BroadcastUtil.ACTION_CLRAR_MESSAGE_BUBBLE);
        inf.addAction(BroadcastUtil.ACTION_MSG_SEND_SUCCESS);

        IntentFilter sys = new IntentFilter();
        sys.addAction(BroadcastUtil.ACTION_SYS_MSG);

        IntentFilter logout = new IntentFilter();
        logout.addAction(MineFragment.logoutAction);

        IntentFilter loading_filter = new IntentFilter();
        loading_filter.addAction(BroadcastUtil.ACTION_TAB_LOADING);

        messageRecentReceiver = new MessageRecentReceiver();
        unreadFriendBroadCastReceiver = new UnreadFriendBroadCastReceiver();

        activity.registerReceiver(messageRecentReceiver, inf);

        activity.registerReceiver(getLogoutreceiver, logout);

        activity.registerReceiver(unreadFriendBroadCastReceiver, sys);

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        activity.registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (messageRecentReceiver != null) {
            activity.unregisterReceiver(messageRecentReceiver);
        }
        if (mReceiver != null) {
            activity.unregisterReceiver(mReceiver);
        }
        if (unreadFriendBroadCastReceiver != null)
            activity.unregisterReceiver(unreadFriendBroadCastReceiver);

        if (getLogoutreceiver != null)
            activity.unregisterReceiver(getLogoutreceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 监听网络变化广播 做出相应的提示
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager) getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable())
                    network_state.setVisibility(View.GONE);
                else
                    network_state.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onTabClickListener.setTabViewBageTips(1, 0);
            boolean isLogin = IntentUtil.isLogin();
            exitSwitchPage(isLogin);
        }
    }

    // 更新未读数
    private void updataNewsCount() {
        Config c = ImserviceHelp.getInstance().db_getConfig();
        long unread = 0;
        if (c != null) {
            unread = c.getFriend_bubble();
            news_count.setText(ImUtils.getBubleText(unread + ""));
            news_count.setVisibility(unread > 0 ? View.VISIBLE : View.INVISIBLE);
        }
    }

    // 读取未读的新朋友数量的监听
    public class UnreadFriendBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updataNewsCount();
        }
    }

    public interface NotifyMainListener {
        void showRedNum(int num);
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void refresh() {
        super.refresh();
    }

}
