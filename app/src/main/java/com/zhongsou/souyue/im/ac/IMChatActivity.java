package com.zhongsou.souyue.im.ac;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.souyue.image.helper.ImageHelper;
import com.speex.encode.AudioLoader;
import com.speex.encode.ChatRecordManager;
import com.speex.encode.ChatRecordManager.OnSendListener;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.PushService;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.AtFriend;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupExtendInfo;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.ImToCricle;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.tuita.sdk.im.db.module.ServiceMessage;
import com.tuita.sdk.im.db.module.ServiceMessageChild;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.tuita.sdk.im.db.module.UserBean;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.circle.activity.CircleSelImgGroupActivity;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.adapter.IMChatAdapter;
import com.zhongsou.souyue.im.adapter.IMFeaturesAdapter;
import com.zhongsou.souyue.im.dialog.ImCircleListDialog;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.dialog.ImDialog.Builder.ImDialogInterface;
import com.zhongsou.souyue.im.download.PackageDao;
import com.zhongsou.souyue.im.emoji.Emoji;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.interfaceclass.DetailChangeInterface;
import com.zhongsou.souyue.im.module.ExpressionBean;
import com.zhongsou.souyue.im.module.ExpressionPackage;
import com.zhongsou.souyue.im.module.GifBean;
import com.zhongsou.souyue.im.module.GroupMember;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.module.SendGifBean;
import com.zhongsou.souyue.im.render.MessageManager;
import com.zhongsou.souyue.im.render.MessageManager.RefreshListener;
import com.zhongsou.souyue.im.render.MsgUtils;
import com.zhongsou.souyue.im.search.SearchUtils;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.*;
import com.zhongsou.souyue.im.view.IMFeaturesGridView;
import com.zhongsou.souyue.im.view.RightSwipeView;
import com.zhongsou.souyue.im.view.RightSwipeView.ActivityFinishListener;
import com.zhongsou.souyue.log.Logger;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.highlight.Highlight;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.*;
import com.zhongsou.souyue.view.ExpressionView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@SuppressLint("NewApi")
public class IMChatActivity extends IMBaseActivity implements OnClickListener,
        TextWatcher, RefreshListener, ImChangeView,
        ExpressionView.OnExpressionListener, DetailChangeInterface, IMFeaturesGridView.IFeaturesClickListener {

    /**
     * intent常量
     */
    private static final String EXTRA_TARGET_TYPE = "TARGET_TYPE"; // 会话类型
    private static final String EXTRA_TARGET_ID = "TARGET_ID"; // 目标id
    private static final String EXTRA_MSG_ID = "MSG_ID"; // msgId
    private static final String FROM_SEARCH_TARGET = "FROM_SEARCH_TARGET"; // 來自搜索定位

    /**
     * 常量
     */
    private static final int SEND_IMAGE = 1; // 发送图片
    private static final int SHOW_LOCATION = 2; // 显示定位按钮
    private static final int DELAY_SHOW_SEND_FAIL = 3; // 延时几秒显示发送失败按钮
    private static final int GONE_LOCATION = 4; // 隐藏定位按钮
    private static final int INIT_EXPRESSION_TAB = 5; // 表情tab初始化
    private static final int SEARCH_PAGE_COUNT = 10; // 每页查询数

    /**
     * 还不知道做了什么的常量
     */
    public static final String KEY_CONTACT = "KEY_CONTACT";
    public static final String KEY_GET_CARD_ID = "KEY_GET_CARD_ID";
    public static final String KEY_GET_GROUPCARD_ID = "KEY_GET_GROUPCARD_ID";
    public static final String KEY_FRIEND_NAME = "KEY_FRIEND_NAME";
    public static final String KEY_FRIEND_ID = "FREND_ID";
    public static final String MSG_UUID = "MSG_UUID";
    public static final String ACTION_FORWARD = "ACTION_FORWARD";
    public static final String ACTION_ASK_SHARE = "ACTION_ASK_SHARE";
    public static final String ACTION_SAY_HELLO = "ACTION_SAY_HELLO";
    public static final String KEY_ACTION = "KEY_ACTION";
    public static final String ACTION_SEND_VCARD = "ACTION_SEND_VCARD";
    public static final String ACTION_SEND_GROUPCARD = "ACTION_SEND_GROUPCARD";
    public static final String ACTION_ASK_COIN = "ACTION_ASK_COIN";
    public static final String ACTION_SHARE_IMFRIEND = "ACTION_SHARE_IMFRIEND";
    public static final String ASK_INTEREST = "com.zhongsou.im.ask.interest";
    private final static int PAGE_COUNT = 8;
    private static final int CODE_ADD_VCARD = 10;
    private static final int CODE_TAKE_PIC = 2;
    private static final int CODE_PICK_PIC = 1;
    public static final int CODE_FORWARD = 5;
    public static final int WHISPERTYPEDELETE = 7;
    private static final long DURITION = 1000 * 60 * 10;
    private boolean isMsgNotify;        //消息免打扰
    /**
     * 目标对象
     */
    private Contact mContact; // 私聊
    private Group mGroup; // 群聊
    private ServiceMessageRecent mServiceMsgRe; // 服务号
    private boolean isFromSearch; // 來自搜索定位的跳轉
    private int msgId; // 消息id 搜索跳轉時使用

    /**
     * 目标类型，用来判断本会话是私聊，群聊，服务号
     */
    private int mTargetType;
    /**
     * 目标id 私聊：好友id 群聊：群id 服务号：服务号id
     */
    public static Long mTargetId = 0l;
    private String mTargetIcon; // 目标头像
    private String mTargetName; // 目标名字
    private String mGroupCount; // title显示的群数量
    private MessageManager mMessageManager; // 消息处理manager

    /**
     * 各种控件
     */
    private EditText mEditTextContent;
    private PullToRefreshListView mPullListView;
    private ListView mListView;
    private ImageButton switchBtn;
    private ImageButton keyBoardSwitch;
    private Button say;
    private View write;
    private IMFeaturesGridView gvFeatures;
    private IMFeaturesAdapter mIMFeaturesAdapter;
    private ExpressionView faceLayout;
    private View disable;
    private TextView titleName;
    private LinearLayout im_chat_bottom_layout;
    private LinearLayout im_bottom_edit;
    private LinearLayout im_button_edit_transpond, im_button_edit_delete,
            im_button_edit_cancel;
    private ImageView editmore;
    private RelativeLayout network_state;
    private RelativeLayout audio_state;// 新增语音播放状态提示
    private TextView dialog_tips;
    private ImageView audio_icon;
    private ImageView silding_contactcount;
    private Button im_add, im_send;
    private TextView tvLocation; // 定位点击按钮
    private LinearLayout llLocation; // 定位点击按钮

    /**
     * 基本
     */
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>(); // 界面数据
    private IMChatAdapter mAdapter; // 基本adapter
    private String mContent; // 发送的文本
    private Long mUserId = 0l; // 当前用户id
    private HashMap<String, ChatMsgEntity> mSendingMap = new HashMap<String, ChatMsgEntity>();
    private ImserviceHelp mImServiceHelp = ImserviceHelp.getInstance(); // 发送消息和查库用到
    private long mCurSessionOrd = 0l; // 以前是记录消息顺序用的，现已废弃
    private boolean isEnd; // 判断是否加载到头
    private long maxShowTimeItem; // 4.0前版本的时间显示规则废弃
    private long minShowTimeItem; // 4.0前版本的时间显示规则废弃
    private Intent mIntent = new Intent(); // 全局intent，接跳转数据
    private int mShareCount; // intent传递过来，分享币用到
    public static boolean isFillListData = true;//原来为内部变量，用于第一次发送，现为全局的，转发时候会用
    private ConnectivityManager mConnectivityManager; // 联网用到
    private NetworkInfo mNetInfo; // 联网用到
    private Handler handler;
    private boolean iscleanHistory;// 历史记录是否被清除
    private Handler mImageHandler;// 用于接收发送图片线程消息的handler
    private long id; // 控制数据库翻页
    private SharedPreferences mBubbleSp; // 获取消息数
    public static int isDetailOpen = ChatMsgEntity.INIT; // 抽取 是否是进入详情标识
    // 三个adapter界面均有用到
    // //因为需要其他界面调用，所以转换成Int
    private boolean isInteractive; // 服务号判断bottom是否显示
    List<ServiceMessageChild> mServiceMsgChild = new ArrayList<ServiceMessageChild>(); // 服务号子类集合
    List<String> mSerMsgImageUrls = new ArrayList<String>(); // 服务号图片集合
    private boolean editGroup = false; // 群聊编辑消息用
    private Vibrator mVibrator; // 震动
    private List<ChatMsgEntity> mLocationDatas; // 定位数据
    List<MessageHistory> mHistorylist; // 搜索历史消息定位

    /**
     * 该会话最近聊天（可以获取草稿和气泡数）
     */
    private MessageRecent mMessageRecent;
    private int mBubbleNum; // 气泡数
    private String mDraftContent, mDraftForAt, atInfo;// 草稿内容,草稿@的内容

    /**
     * 点击事件是否由ListView处理，主要处理的是头像的长按与ListView Touch事件
     */
    private boolean isListViewTouch = true;
    /**
     * 头像长按点击事件
     */
    private boolean isHeadLongClick;

    /**
     * 名片
     */
    private Group mGroupCard;// 群名片
    private boolean mActionSendVCard; // 由朋友信息页发送名片时传true, 默认false;
    private boolean mSendGroupCard;// 群名片，默认false

    /**
     * @用到
     */
    private boolean isContainAt = false;// 是否包括at
    private int mAtLength;
    private AtFriend atFriends = new AtFriend();// 被@者信息
    private ArrayList<GroupMembers> groupMemberses = new ArrayList<GroupMembers>();
    private AtFriend newatFriend;// @实体bean

    /**
     * 语音
     */
    private ChatRecordManager manager;// 录音

    /**
     * 老虎机
     */
    private boolean isFromTiger;
    private String mTigerActionType;

    /**
     * 密信
     */
    private boolean isWhisper = false;
    private int whispercount;
    private boolean isTimerOpen;
    private int deleteWhisper = 0;
    private TextView goCircleTv;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_IMAGE:
                    Bundle bundle = msg.getData();
                    try {
                        addPic(bundle.getString("pic_file_path"),
                                bundle.getString("pic_file_path"),
                                bundle.getBoolean("isVertical"),
                                bundle.getFloat("minWidth"),
                                bundle.getFloat("minHeight"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case SHOW_LOCATION:
                    tvLocation.setText(mBubbleNum + "条未读消息");
                    llLocation.setVisibility(View.VISIBLE);
                    mBubbleNum = 0;
                    break;
                case GONE_LOCATION:
                    llLocation.setVisibility(View.GONE);
                    break;
                case DELAY_SHOW_SEND_FAIL:
                    if (msg.obj instanceof MessageHistory && !isFinishing()) {
                        MessageHistory history = (MessageHistory) msg.obj;
                        msgFaildReceiver(history);
                    }
                    break;
                case INIT_EXPRESSION_TAB:
                    faceLayout.initTab((List<ExpressionPackage>) msg.obj);
                    break;
            }
        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        mAtLength = s.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable arg0) {
        if (arg0.length() > 0) {
            im_add.setVisibility(View.INVISIBLE);
            im_send.setVisibility(View.VISIBLE);
        } else {
            im_send.setVisibility(View.INVISIBLE);
            im_add.setVisibility(View.VISIBLE);

            if (mTargetType == IConst.CHAT_TYPE_GROUP) {
                groupMemberses.clear();
                if (atFriends.getUsers() != null)
                    atFriends.getUsers().clear();
            }
        }

        /**
         * @好友
         */
        if (mTargetType == IConst.CHAT_TYPE_GROUP) {
            String content = arg0.toString();
            if (content != null && mAtLength <= content.length()) {
                if ((content.endsWith(" @") || content.equals("@")
                        || content.endsWith("\n@") || content.endsWith("]@"))
                        && !isContainAt) {
                    IMIntentUtil.gotoInviteGroupFriendActivity(
                            IMChatActivity.this, mTargetId);
                    return;
                }
                if (content.endsWith("@") && !isContainAt) {
                    String flag = content
                            .substring(content.lastIndexOf("@") - 1);
                    if (PingYinUtil.isChinese(flag.charAt(0)))
                        IMIntentUtil.gotoInviteGroupFriendActivity(
                                IMChatActivity.this, mTargetId);
                }
                isContainAt = false;
            }
        }
    }

    @Override
    public void showTitleIcon() {
        if (mTargetType != IConst.CHAT_TYPE_SERVICE_MESSAGE) {
            audio_icon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideTitleIcon() {
        audio_icon.setVisibility(View.GONE);
    }

    @Override
    public void showTipsText(int tipText) {
        dialog_tips.setText("成功切换到" + getResources().getString(tipText) + "!");
        audio_state.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setStartOffset(1000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                audio_state.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        audio_state.startAnimation(alphaAnimation);

    }

    /**
     * emoji表情点击事件
     *
     * @param emoji
     */
    @Override
    public void emojiItemClick(Emoji emoji) {

        if (emoji.getId() == R.drawable.btn_msg_facedelete_selector) {
            int selection = mEditTextContent.getSelectionStart();
            String text = mEditTextContent.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    mEditTextContent.getText().delete(start, end);
                    return;
                }
                mEditTextContent.getText().delete(selection - 1, selection);
            }
        }
        addEmoji(emoji);
    }

    /**
     * gif表情点击事件
     *
     * @param gifBean
     */
    @Override
    public void gifItemClick(GifBean gifBean) {
//        ChatMsgEntity entity = getEntity();
//        entity.setType(MessageHistory.CONTENT_TYPE_GIF);
//        int[] wandh = ImUtils.getDrawableWidthAndHeight(mContext,
//                gifBean.getGifid());
//        SendGifBean bean = new SendGifBean();
//        bean.setGif_name(gifBean.getGifName());
//        bean.setGif_url(gifBean.getGifurl());
//        bean.setGif_h(wandh[1] + "");
//        bean.setGif_w(wandh[0] + "");
//        entity.setText(new Gson().toJson(bean));
//        mMessageManager.sendGif(entity);
        Toast.makeText(this,"IMChatActivity.java line 463",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAll(getIntent());
    }

    /**
     * 进入界面则清理掉相应的通知栏
     */
    private void clearNotify() {
        if (mTargetType == IConst.CHAT_TYPE_PRIVATE || mTargetType == IConst.CHAT_TYPE_GROUP) {
            ImserviceHelp.getInstance().cancelNotify(1);
        } else if (mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
            ImserviceHelp.getInstance().cancelNotify(mTargetId.intValue());
        }
    }

    // *******************exp start***********************************
    @Override
    public void expressionItemClick(ExpressionBean e) {
        ChatMsgEntity entity = getEntity();
        entity.setType(MessageHistory.CONTENT_TYPE_GIF);

        String sendUrl = getFilesDir()+Constants.PACKAGE_DOWNURL + File.separator
                + SYUserManager.getInstance().getUserId() + File.separator
                + e.geteSendUrl();
        int[] wandh = ImUtils
                .getDrawableWidthAndHeightByPath(mContext, sendUrl);
        SendGifBean bean = new SendGifBean();

        bean.setGif_h(wandh[1] + "");
        bean.setGif_w(wandh[0] + "");
        bean.setGif_name(e.geteName());
        bean.setGif_url(e.geteCloundUrl());
        bean.setLocal_url(e.geteSendUrl());
        Slog.d("callback", "send text:-----------" + new Gson().toJson(bean));
        entity.setText(new Gson().toJson(bean));
        mMessageManager.sendGif(entity);
    }

    PackageDao dao = new PackageDao(this);

    // 初始化 tab
    public void initTab() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<ExpressionPackage> expPackage = dao.getAllExpPackage(IMChatActivity.this);
                Message msg = new Message();
                msg.what = INIT_EXPRESSION_TAB;
                msg.obj = expPackage;
                mHandler.sendMessage(msg);
            }
        });

    }

    IntentFilter mFilter = new IntentFilter();

    private BroadcastReceiver mUpdateRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // 更新数据
            String action = intent.getAction();
            PackageBean mPackageBean = (PackageBean) (intent
                    .getSerializableExtra("packagebean"));

            if (Constants.ADD_ACTION.equals(action)) { // 下载
                ExpressionPackage mPackage = dao.getExpPackageById(
                        IMChatActivity.this, mPackageBean.getPackageId());
                faceLayout.addExpressionPackage(mPackage);
            } else if (Constants.DELETE_ACTION.equals(action)) { // 卸载
                initTab();
            }

        }
    };

    public void deleteTab(PackageBean bean) {

    }

    // ********************exp end**********************

    /**
     * 查找群人数
     */
    private void findGroupCount() {
        // 根据groupid 查询成员表
        if (mGroup != null)
            mGroupCount = mImServiceHelp.db_findMemberCountByGroupid(mTargetId)
                    + "";
        else if (mGroupCard != null)
            mGroupCount = mImServiceHelp.db_findMemberCountByGroupid(mGroupCard
                    .getGroup_id()) + "";
        else
            mGroupCount = "";
    }

    /**
     * 启动定时器
     */
    public void startTimer() {
        handler.postDelayed(runnable, 1000);
        isTimerOpen = true;
    }

    /**
     * 停止定时器
     */
    public void stopTimer() {
        whispercount = 0;
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        isTimerOpen = false;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (whispercount > 0) {
                updateWhisperTime();
                handler.postDelayed(this, 1000);
            } else {
                stopTimer();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        PushService.setIsInChat(MainApplication.getInstance(), mTargetId);
        loadBubleData();
        if (SYSharedPreferences.getInstance().getBoolean("showIcon", false)) {// 听筒模式则显示
            showTitleIcon();
        } else {
            hideTitleIcon();
        }
        if (isFillListData && mAdapter != null && !mAdapter.getIsEdit()
                && isDetailOpen != ChatMsgEntity.BACK
                && isDetailOpen != ChatMsgEntity.BIGIMAGE || iscleanHistory) {
            initListData();
        }
        findViewById(R.id.btn_back).setFocusable(false);
        findViewById(R.id.im_chat_more).setFocusable(false);
        clearNotify();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BroadcastUtil.ACTION_MSG);
        registerReceiver(chatMsgReceiver, filter);
        // 网络广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    private void getIntentData(Intent intent) {
        if (intent != null) {
            isInteractive = true;
            this.mIntent = intent;
            mTargetId = intent.getLongExtra(EXTRA_TARGET_ID, 0);
            mTargetType = intent.getIntExtra(EXTRA_TARGET_TYPE, 0);

            isFromSearch = intent.getBooleanExtra(FROM_SEARCH_TARGET, false);
            if (isFromSearch) {
                msgId = intent.getIntExtra(EXTRA_MSG_ID, 0);
            }
            // 获取最近聊天记录
            mMessageRecent = ImserviceHelp.getInstance().db_findMessageRecent(
                    mTargetId);

            if (mMessageRecent != null) {
                mDraftContent = mMessageRecent.getDrafttext();
                mDraftForAt = mMessageRecent.getDraftforat();
                mBubbleNum = mMessageRecent.getBubble_num();
            } else {
                mDraftContent = "";
                mDraftForAt = "";
                mBubbleNum = 0;
            }
            // 通过mTargetType来给头像，名字等赋值
            if (mTargetType == IConst.CHAT_TYPE_PRIVATE) {
                mContact = ImserviceHelp.getInstance().db_getContactById(
                        mTargetId);
                if (mContact == null) {
                    Toast.makeText(getApplicationContext(), "读取用户信息失败", Toast.LENGTH_SHORT).show();
                    ImserviceHelp.getInstance().deleteMessageRecent(mTargetType, mTargetId);
                    IMIntentUtil.gotoMainActivity(IMChatActivity.this);
                } else {
                    mTargetIcon = mContact.getAvatar();
                    mTargetName = ContactModelUtil.getShowName(mContact);
                    isMsgNotify = mContact.getIs_news_notify() == 1 ? false : true;
                }
            } else if (mTargetType == IConst.CHAT_TYPE_GROUP) {
                //update数据库
                if (mMessageRecent != null) {
                    mMessageRecent.setBy1("0");
                    ImserviceHelp.getInstance().db_updateRecent(mMessageRecent);
                }
                mGroup = ImserviceHelp.getInstance().db_findGourp(mTargetId);
                if (mGroup == null) {
                    Toast.makeText(getApplicationContext(), "您不是群成员", Toast.LENGTH_SHORT).show();
                    ImserviceHelp.getInstance().deleteMessageRecent(mTargetType, mTargetId);
                    IMIntentUtil.gotoMainActivity(IMChatActivity.this);
                } else {
                    mTargetName = mGroup.getGroup_nick_name();
                    if (mDraftContent != null && mDraftContent.equals("@")) {
                        isContainAt = true;
                    }
                    isMsgNotify = mGroup.getIs_news_notify() == 1 ? false : true;
                }
            } else if (mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                mServiceMsgRe = ImserviceHelp.getInstance()
                        .db_getTargetServiceMsgRe(mTargetId);
                if (mServiceMsgRe == null) {
                    Toast.makeText(getApplicationContext(), "该服务号不存在", Toast.LENGTH_SHORT).show();
                    ImserviceHelp.getInstance().deleteMessageRecent(mTargetType, mTargetId);
                    IMIntentUtil.gotoMainActivity(IMChatActivity.this);
                } else {
                    mTargetName = mServiceMsgRe.getService_name();
                    mTargetIcon = mServiceMsgRe.getService_avatar();
                    isMsgNotify = mServiceMsgRe.getBy3() == null || mServiceMsgRe.getBy3().equals("0") ? true : false;
                    if (mServiceMsgRe.getBy2() != null) {
                        if (mServiceMsgRe.getBy2().equals("" + "0")) {
                            isInteractive = false;
                        }
                    }
                }

            }

            mGroupCard = (Group) intent
                    .getSerializableExtra(KEY_GET_GROUPCARD_ID);
            mActionSendVCard = ACTION_SEND_VCARD.equals(intent
                    .getStringExtra(KEY_ACTION));
            mSendGroupCard = ACTION_SEND_GROUPCARD.equals(intent
                    .getStringExtra(KEY_ACTION));

            isFromTiger = intent.getBooleanExtra(
                    ContactsListActivity.START_FROM, false);
            mTigerActionType = intent.getStringExtra(IMChatActivity.KEY_ACTION);
            mShareCount = intent.getIntExtra(ConstantsUtils.SHARE_COUNT, 0);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("intent", mIntent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getIntentData((Intent) savedInstanceState.getParcelable("intent"));
    }

    private void setData(Intent intent) {
        atFriends.setUsers(new ArrayList<UserBean>());
        if (mTargetType == IConst.CHAT_TYPE_PRIVATE
                || mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
            titleName.setText(mTargetName);
            findViewById(R.id.group_count_tv).setVisibility(View.GONE);
        } else if (mTargetType == IConst.CHAT_TYPE_GROUP) {// 如果是群聊则显示人数
            findViewById(R.id.group_count_tv).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.group_count_tv)).setText("("
                    + mGroupCount + ")");
            titleName.setText(mTargetName);
        }
        setLayouListener();
        setRecordManager();
        initSend(intent);
    }

    private void addEmoji(Emoji emoji) {
        if (!TextUtils.isEmpty(emoji.getCharacter())) {
            SpannableString spannableString = EmojiPattern.getInstace()
                    .addFace(IMChatActivity.this, emoji.getId(),
                            emoji.getCharacter());
            int index = mEditTextContent.getSelectionStart();
            Editable editable = mEditTextContent.getText();
            editable.insert(index, spannableString);
        }
    }

    /**
     * 由好友信息页调用发送我的名片时调用
     */
    private void initSend(Intent intent) {
        if (mActionSendVCard) {
            Contact card = (Contact) intent.getSerializableExtra(
                    KEY_GET_CARD_ID);
            if (card != null) {
                sendCard(card);
            }
        } else if (isFromTiger) {
            if (mTigerActionType != null
                    && mTigerActionType.equals(ACTION_ASK_SHARE)) {
                sendShareWinCoin(mShareCount, isFromTiger);
            } else if (mTigerActionType != null
                    && mTigerActionType.equals(ACTION_SAY_HELLO)) {
                mEditTextContent.setText(R.string.tg_sayhello);
            } else {
                showAskCoinDlg();
            }
        } else if (mSendGroupCard) {
            if (mGroupCard != null)
                sendGroup_Card(mGroupCard);
        }

    }

    /**
     * 获取所有的数据
     */
    private void initListData() {
        if (mTargetId != null && mTargetId != 0) {
            ThreadPoolUtil.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mBubbleNum > SEARCH_PAGE_COUNT && mTargetType != IConst.CHAT_TYPE_SERVICE_MESSAGE && !isFromSearch) {
                        mLocationDatas = msgConverToEntity(
                                ImserviceHelp.getInstance()
                                        .getMessageByLimitCount(mTargetId,
                                                mBubbleNum), false);
                        if (mLocationDatas.size() > 0) {
                            mLocationDatas.get(0).setShowUnreadLine(true);
                        }
                        Message message = new Message();
                        message.what = SHOW_LOCATION;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = GONE_LOCATION;
                        mHandler.sendMessage(message);
                    }
                    ImserviceHelp.getInstance().db_clearMessageRecentBubble(
                            mTargetId);
                    if (mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                        ImserviceHelp.getInstance()
                                .db_clearSouyueMessageRecentBubble(
                                        mServiceMsgRe.getService_id(),
                                        mServiceMsgRe.getCate_id());
                    }
                }
            });
        }

        if (isFromSearch) {
            // 來自搜索，關鍵詞定位
            try {
                mHistorylist = MessageHistoryDaoHelper.getInstance(
                        IMChatActivity.this)
                        .findSearchTargetMsg(
                                Long.parseLong(SYUserManager.getInstance()
                                        .getUserId()), msgId, mTargetId,
                                mTargetType);
                if (mHistorylist.size() > 0) {
                    id = mHistorylist.get(0).getChat_id();
                    mCurSessionOrd = mHistorylist.get(mHistorylist.size() - 1)
                            .getSession_order();
                    // 设置是否显示时间戳
                    maxShowTimeItem = mHistorylist.get(mHistorylist.size() - 1)
                            .getDate();
                    minShowTimeItem = maxShowTimeItem;
                }
                if (mDataArrays != null)
                    mDataArrays.clear();
                mDataArrays.addAll(msgConverToEntity(mHistorylist, false));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            mHistorylist = mImServiceHelp.db_getMessage(mTargetId, -1,
                    IConst.QUERY_MSG_FIRST);
            if (mHistorylist.size() > 0) {
                id = mHistorylist.get(0).getChat_id();
                mCurSessionOrd = mHistorylist.get(mHistorylist.size() - 1)
                        .getSession_order();
                // 设置是否显示时间戳
                maxShowTimeItem = mHistorylist.get(mHistorylist.size() - 1)
                        .getDate();
                minShowTimeItem = maxShowTimeItem;
            }
            if (mDataArrays != null)
                mDataArrays.clear();
            mDataArrays.addAll(msgConverToEntity(mHistorylist, false));
        }

        if (initWhisperCountAndDelete() != 0) {
            if (!isTimerOpen) {
                startTimer();
            }
        }
        if (mDataArrays.size() < PAGE_COUNT) {
            isEnd = true;
            if (mDataArrays.size() > 0) {
                mDataArrays.get(0).setShowTime(true);
            }
        }
        if (isFromSearch) {
            if (mHistorylist.size() < 8) {
                loadMoreHistory();
            } else {
                mPullListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListView.requestFocusFromTouch();//获取焦点
                        mListView.setSelection(mListView.getTop());
                    }
                });
            }

        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 由于字段变换及扩展考虑加一层转换 通过数据库字段的实例 转化成 Adapter的实例
     *
     * @param find
     * @return
     */
    private List<ChatMsgEntity> msgConverToEntity(List<MessageHistory> find,
                                                  boolean isNews) {
        List<ChatMsgEntity> l = new ArrayList<ChatMsgEntity>();
        if (find != null && find.size() > 0) {
            for (int i = 0; i < find.size(); i++) {
                ChatMsgEntity e;
                MessageHistory m;
                if (!isNews) {
                    m = find.get(find.size() - i - 1);
                    e = new ChatMsgEntity(m);
                    if (Math.abs(minShowTimeItem - e.getDate()) > DURITION) {
                        e.setShowTime(true);
                        minShowTimeItem = e.getDate();
                    }
                } else {
                    m = find.get(i);
                    e = new ChatMsgEntity(m);
                    if (Math.abs(maxShowTimeItem - e.getDate()) > DURITION) {
                        e.setShowTime(true);
                        maxShowTimeItem = e.getDate();
                    }
                }

                if (mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                    changeCT(m, e);
                }
                if (e.isComMsg()) {
                    if (mTargetType == IConst.CHAT_TYPE_PRIVATE
                            || mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                        e.setIconUrl(mTargetIcon);
                    } else if (mTargetType == IConst.CHAT_TYPE_GROUP) {
                        if (!TextUtils.isEmpty(m.getBy2())) {
                            e.setIconUrl(e.getUserImage());
                        }
                    }
                } else {
                    e.setIconUrl(SYUserManager.getInstance().getUser().image());
                }
                if (isNews) {
                    l.add(e);
                } else {
                    l.add(0, e);
                }

                if (m.getStatus() == IConst.STATUS_SENTING) {
                    mSendingMap.put(m.getUuid(), e);
                }
            }
        }
        return l;
    }

    private void setRecordManager() {
        manager = new ChatRecordManager(say, this);
        manager.setOnSendListener(new OnSendListener() {

            @Override
            public void onSend(String fileLocalPath, int voiceLength) {
                if (new File(fileLocalPath).length() > 0) {
                    ChatMsgEntity m = getEntity();
                    m.setType(IMessageConst.CONTENT_TYPE_NEW_VOICE);
                    m.setText(getJson(fileLocalPath, voiceLength));
                    Log.v("log", "msg voice text:" + m.getText());
                    m.setUrl(fileLocalPath);
                    m.setVoiceLength(voiceLength);
                    mMessageManager.saveVoice(m, voiceLength);
                } else {
                    Toast.makeText(mContext, "录音失败", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void initV() {
        say = (Button) findViewById(R.id.im_longClickToSay);
        findViewById(R.id.btn_back).setOnClickListener(this);
        write = findViewById(R.id.im_key_layout);
        titleName = (TextView) findViewById(R.id.title_name);
        mPullListView = (PullToRefreshListView) findViewById(R.id.im_listview);
        mListView = mPullListView.getRefreshableView();
        im_button_edit_transpond = (LinearLayout) findViewById(R.id.im_button_edit_transpond);
        im_button_edit_transpond.setOnClickListener(this);
        im_button_edit_delete = (LinearLayout) findViewById(R.id.im_button_edit_delete);
        im_button_edit_delete.setOnClickListener(this);
        im_button_edit_cancel = (LinearLayout) findViewById(R.id.im_button_edit_cancel);
        im_button_edit_cancel.setOnClickListener(this);
        im_chat_bottom_layout = (LinearLayout) findViewById(R.id.im_chat_bottom_layout);
        im_bottom_edit = (LinearLayout) findViewById(R.id.im_bottom_edit);
        mListView.setSelector(R.color.transparent);
        mPullListView.setOnScrollListener(mOnScrollListener);
        mPullListView.onUpdateTime("");
        mPullListView.setPullLabel("", PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mPullListView.setRefreshingLabel("", PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mPullListView.setReleaseLabel("", PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        mListView.setOnTouchListener(mOnTouchListener);
        mPullListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(mListView.getCount());
            }
        });
        mPullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!isEnd) {
                    loadMoreHistory();
                }
                mPullListView.onRefreshComplete();
            }
        });
        mEditTextContent = (EditText) findViewById(R.id.im_edit_text);
        mEditTextContent.setOnClickListener(this);
        mEditTextContent.addTextChangedListener(this);
        switchBtn = (ImageButton) findViewById(R.id.im_switching_btn);

        silding_contactcount = (ImageView) findViewById(R.id.silding_contactcount_im);
        network_state = (RelativeLayout) findViewById(R.id.network_state);
        audio_state = (RelativeLayout) findViewById(R.id.audio_state);
        dialog_tips = (TextView) findViewById(R.id.dialog_tips);
        audio_icon = (ImageView) findViewById(R.id.audio_icon);
        network_state.setOnClickListener(this);
        network_state.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_SETTINGS));
                    IMChatActivity.this.overridePendingTransition(
                            R.anim.left_in, R.anim.left_out);
                } else {
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    IMChatActivity.this.overridePendingTransition(
                            R.anim.left_in, R.anim.left_out);
                }
            }

        });

        findViewById(R.id.im_add).setOnClickListener(this);
        im_add = (Button) this.findViewById(R.id.im_add);
        im_add.setOnClickListener(this);
        im_send = (Button) this.findViewById(R.id.im_send);
        gvFeatures = (IMFeaturesGridView) findViewById(R.id.gv_features);
        faceLayout = (ExpressionView) findViewById(R.id.facelayout);
        disable = findView(R.id.im_disable);
        disable.setOnClickListener(this);
        keyBoardSwitch = (ImageButton) findViewById(R.id.im_add_emoji);
        keyBoardSwitch.setOnClickListener(this);
        // 新增私聊界面右上角 点击跳转到个人中心的ICON 设置可见和点击监听
        findViewById(R.id.im_chat_pcenter).setOnClickListener(this);

        editmore = (ImageView) findViewById(R.id.im_chat_more);
        editmore.setOnClickListener(this);
        tvLocation = (TextView) findViewById(R.id.tvlocation);
        llLocation = (LinearLayout) findViewById(R.id.lllocation);
        llLocation.setOnClickListener(this);
        goCircleTv= (TextView) findViewById(R.id.im_go_circle);
        goCircleTv.setVisibility(View.GONE);
        if (mTargetType == IConst.CHAT_TYPE_GROUP) {
            changeGroupUI();
        } else if (mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
            changeServiceMsgUI();
        }
        gvFeatures.setChatType(mTargetType);
        gvFeatures.setIFeaturesClickListener(this);
        if (!TextUtils.isEmpty(mDraftContent)) {
            SpannableString spannableString = EmojiPattern.getInstace()
                    .getExpressionString(this, mDraftContent);
            mEditTextContent.setText(spannableString);
        }

        //侧滑关闭
        RightSwipeView rightSwipeView = (RightSwipeView) findViewById(R.id.right_swipe);
        rightSwipeView.setDetector(new GestureDetector(this));
        rightSwipeView.setIsCanRightSwipe(true);
        rightSwipeView.setFinishListener(new ActivityFinishListener() {
            @Override
            public void finishActivity() {
                hideKeyboard();
                onBackPressed();
            }
        });
    }

    /**
     * 服务号界面变化
     */
    private void changeServiceMsgUI() {
        findViewById(R.id.im_chat_pcenter).setVisibility(View.GONE);
        switchBtn.setVisibility(View.GONE);
        findViewById(R.id.biaoqing_line).setVisibility(View.GONE);
        findViewById(R.id.ll_biaoqing).setVisibility(View.GONE);
        if (isInteractive) {
            write.setVisibility(View.VISIBLE);
            im_chat_bottom_layout.setVisibility(View.VISIBLE);
        } else {
            write.setVisibility(View.GONE);
            im_chat_bottom_layout.setVisibility(View.GONE);
        }
    }

    /**
     * 群聊界面变化
     */
    private void changeGroupUI() {
        findViewById(R.id.im_chat_pcenter).setVisibility(View.GONE);
        if(isRelateToCricle())
        {
            goCircleTv.setVisibility(View.VISIBLE);
            goCircleTv.setOnClickListener(this);
            if (isVisitGroupFirst()) {
                showFirstVisitGroup();
            }
        }
    }

    /**
     * 是否关联兴趣圈
     * @return
     */
    private boolean isRelateToCricle()
    {
        boolean isRelate= false;
        if(mGroup!=null)
        {
            GroupExtendInfo extendInfo = mGroup.getExtendInfo();
            if(extendInfo!=null)
            {
                List<ImToCricle> circleList =extendInfo.getCircle_boundCircleList();
                if(circleList!=null &&circleList.size()>0)
                {
                    isRelate= true;
                }
            }

        }
        return isRelate;
    }

    public void onSwitchingClick(View v) {
        if (say == null || write == null)
            return;
        if (say.isShown()) {
            showSayBtn();
        } else {
            showTextBtn();
        }
    }

    /**
     * 监听网络变化广播 做出相应的提示
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                mNetInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetInfo != null && mNetInfo.isAvailable())
                    network_state.setVisibility(View.GONE);
                else
                    network_state.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 发送按钮
     *
     * @param v
     */
    public void onSendButtonClick(View v) {
        if (mTargetType == IConst.CHAT_TYPE_PRIVATE
                || mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
            mContent = mEditTextContent.getText().toString();
            if (!isWhisper) {
                if (mContent.length() > 0
                        && !TextUtils.isEmpty(mContent.replace("\n", ""))
                        && !TextUtils.isEmpty(mContent.replace(" ", ""))) {
                    ChatMsgEntity m = getEntity();
                    m.setText(mContent);
                    if (mMessageManager.sendText(m)) {
                        mEditTextContent.setText("");
                        mEditTextContent.requestFocus();
                    }
                    mListView.setSelection(mListView.getCount() - 1);
                } else {
                    SouYueToast.makeText(this, R.string.im_blank,
                            SouYueToast.LENGTH_SHORT).show();
                }
            } else {
                if (mContent.length() > 0
                        && !TextUtils.isEmpty(mContent.replace("\n", ""))
                        && !TextUtils.isEmpty(mContent.replace(" ", ""))) {
                    ChatMsgEntity entity = getEntity();
                    entity.setType(MessageHistory.CONTENT_TYPE_SECRET_MSG);
                    entity.setText(mContent);
                    if (mMessageManager.sendWhisper(entity)) {
                        mEditTextContent.setText("");
                        mEditTextContent.requestFocus();
                    }
                    mListView.setSelection(mListView.getCount() - 1);
                } else {
                    SouYueToast.makeText(this, R.string.im_blank,
                            SouYueToast.LENGTH_SHORT).show();
                }

            }
        } else if (mTargetType == IConst.CHAT_TYPE_GROUP) {
            sendGroupText();
        }
    }

    private void showSayBtn() {
        write.setVisibility(View.VISIBLE);
        say.setVisibility(View.GONE);
        switchBtn.setImageDrawable(getResources().getDrawable(
                R.drawable.chatting_setmode_voice_btn));
    }

    private void showTextBtn() {
        write.setVisibility(View.GONE);
        say.setVisibility(View.VISIBLE);
        switchBtn.setImageDrawable(getResources().getDrawable(
                R.drawable.chatting_setmode_keyboard_btn));
        faceLayout.setVisibility(View.GONE);
        hideKeyboard();
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (keybordShowing) {
            keybordShowing = false;
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(mEditTextContent.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    private void showKeyboard() {
        InputMethodManager im = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        im.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                View topView = mListView.getChildAt(mListView
                        .getFirstVisiblePosition());
                if ((topView != null) && (topView.getTop() == 0)) {
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
        }
    };

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (isHeadLongClick) {
                return false;
            }

            if (isListViewTouch) {
                if (event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_MOVE) {
                    hideKeyboard();
                    if (faceLayout.isShown()) {
                        faceLayout.setVisibility(View.GONE);
                        keyBoardSwitch
                                .setBackgroundResource(!isWhisper ? R.drawable.btn_msg_face_selector
                                        : R.drawable.btn_im_send_smile_selector);
                    }
                }
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                isListViewTouch = true;
            }

            return false;
        }
    };
    private Uri imageFileUri;
    private Dialog dlgAskForCoin;
    private EditText coinNumEdt;
    private View coinOk;
    protected boolean keybordShowing;
    private Dialog dlgSendCoin;
    private EditText sendCoinNumEdt;
    private View sendCoinOk;
    private TextView coinNumText;
    private String coinNum;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_add:// 打开发送好友名片及求中搜币窗口
                mListView.setSelection(mListView.getCount() - 1);
                addAttach();
                isWhisper = false;
                im_add.setBackgroundResource(R.drawable.btn_im_chat_add);
                keyBoardSwitch
                        .setBackgroundResource(R.drawable.btn_msg_face_selector);
                im_send.setBackgroundResource(R.drawable.btn_im_send_msg_normal_selector);
                im_send.setTextColor(Color.GRAY);
                mEditTextContent
                        .setBackgroundResource(R.drawable.login_edit_normal);
                if (mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                    switchBtn.setVisibility(View.GONE);
                } else {
                    switchBtn.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.im_add_emoji:// 发送表情
                mListView.setSelection(mListView.getCount() - 1);
                hideKeyboard();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        showEmoji();
                    }
                }, 200);
                break;
            case R.id.ask_for_ok: // 求中搜币
                gvFeatures.setVisibility(View.GONE);
                sendAskForCoin();
                break;
            case R.id.ask_for_cancel: // 取消求中搜币
                coinNumEdt.setText("");
                dlgAskForCoin.dismiss();
                break;
            case R.id.lllocation: // 定位按钮
                if (mLocationDatas != null && mLocationDatas.size() > 0) {
                    mDataArrays.clear();
                    mDataArrays.addAll(mLocationDatas);
                    mLocationDatas.clear();
                    mLocationDatas = null;
                    llLocation.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    hideKeyboard();
                    mPullListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListView.smoothScrollToPositionFromTop(0, 0, 500);
                        }
                    }, 500);
                }
                break;
            case R.id.im_send_coin_cancel: // 赠送中搜币弹框 拒绝song中搜币
                sendCoinNumEdt.setText("");
                dlgSendCoin.dismiss();
                break;
            case R.id.im_disable:
                dismissPup();
                break;
            case R.id.im_send_coin_ok: // 赠送中搜币弹框 同意赠送中搜币
                sendCoin();
                break;
            case R.id.im_chat_pcenter: // 跳转到个人中心
                savaDraftEvent();
                IMApi.IMGotoShowPersonPage(this, mContact,
                        PersonPageParam.FROM_SINGLE_CHAT);
                break;
            case R.id.im_chat_more:
                new SYInputMethodManager(this).hideSoftInput();
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);// 重新设置系统输入法
                // 模式
                if (faceLayout.isShown()) {
                    faceLayout.setVisibility(View.GONE);
                }
                if (mTargetType == IConst.CHAT_TYPE_PRIVATE) {
                    ChatDetailActivity.mDetailChangeListener = IMChatActivity.this;
                    IMIntentUtil.ChatDetailActivity(this, mTargetId);
                } else if (mTargetType == IConst.CHAT_TYPE_GROUP) {
                    NewGroupDetailsActivity.mDetailChangeListener = IMChatActivity.this;
                    IMIntentUtil.GroupDetailsActivity(this, mGroup);
                    savaGroupDraftEvent();
                } else if (mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                    ServiceMsgDetailActivity.mDetailChangeListener = IMChatActivity.this;
                    ServiceMsgDetailActivity.invoke(IMChatActivity.this, mTargetId);
                }
                break;
            case R.id.im_send_cion_charge:// 中搜币充值
                IntentUtil.gotoPay(this);
                break;
            case R.id.im_button_edit_transpond:
                if (editListcount() == 0) {
                    SouYueToast.makeText(this, R.string.im_no_select,
                            SouYueToast.LENGTH_SHORT).show();
                } else {
                    List<ChatMsgEntity> editList = editList();
                    boolean flag = false;
                    for (ChatMsgEntity entity : editList) {
                        int type = entity.getType();
                        if (type == IMessageConst.CONTENT_TYPE_TEXT
                                || type == IMessageConst.CONTENT_TYPE_VCARD
                                || type == IMessageConst.CONTENT_TYPE_INTEREST_SHARE
                                || type == IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND
                                || type == IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE
                                || type == IMessageConst.CONTENT_TYPE_INTEREST_CIRCLE_CARD
                                || type == IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE
                                || type == IMessageConst.CONTENT_TYPE_NEW_IMAGE
                                || type == IMessageConst.CONTENT_TYPE_GROUP_CARD
                                || type == IMessageConst.CONTENT_TYPE_FILE
                                || type == IMessageConst.CONTENT_TYPE_WEB)
                            flag = true;
                        else
                            flag = false;
                        if (!flag)
                            break;
                    }
                    if (flag)
                        IMShareActivity.invoke(this, editList);
                    else
                        new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.clear_cache_prompt)
                                .setMessage(R.string.cannotSend)
                                .setNegativeButton(R.string.ikonwn,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                            }
                                        }).show();
                }
                break;
            case R.id.im_button_edit_delete:
                if (editListcount() == 0) {
                    SouYueToast.makeText(this, R.string.im_no_select,
                            SouYueToast.LENGTH_SHORT).show();
                } else {
                    ImDialog.Builder build = new ImDialog.Builder(this);
                    build.setMessage(R.string.im_delete_selected);
                    build.setPositiveButton(R.string.im_dialog_ok,
                            new ImDialogInterface() {
                                @Override
                                public void onClick(DialogInterface dialog, View v) {
                                    deleteSellectedMsg();
                                }
                            }).create().show();
                }
                break;
            case R.id.im_button_edit_cancel:
                uneditMsg();
                break;
            case R.id.im_edit_text:
                mListView.setSelection(mListView.getCount() - 1);
                if (faceLayout.isShown()) {
                    faceLayout.setVisibility(View.GONE);
                }
                if (!isWhisper) {//点击时也需要切换输入框右边图标
                    keyBoardSwitch
                            .setBackgroundResource(R.drawable.btn_msg_face_selector);
                } else {
                    keyBoardSwitch
                            .setBackgroundResource(R.drawable.btn_im_send_smile_selector);
                }
                break;

            case R.id.btn_back: // 返回跳转
                hideKeyboard();
                finish();
                if (mTargetType == IConst.CHAT_TYPE_PRIVATE
                        || mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                    savaDraftEvent();
                } else if (mTargetType == IConst.CHAT_TYPE_GROUP) {
                    savaGroupDraftEvent();
                }
                break;
            case R.id.im_go_circle:
//                goCircle();
                if(isRelateToCricle())
                {
                    List<ImToCricle> circle_boundCircleList = mGroup.getExtendInfo().getCircle_boundCircleList();
                    if(circle_boundCircleList.size() ==1)
                    {
                        ImToCricle imToCricle = circle_boundCircleList.get(0);
                        goCircle(imToCricle);
                    }else
                    {
                        ImCircleListDialog dialog = new ImCircleListDialog(mContext, circle_boundCircleList, new ImCircleListDialog.ImCircleListDialogListner() {
                            @Override
                            public void itemClick(ImToCricle imToCricle) {
                                goCircle(imToCricle);
                            }
                        });
                        dialog.show();
                    }
                }
                break;
        }
    }

    private void goCircle(ImToCricle imToCricle )
    {
        if(imToCricle.getType()==ImToCricle.TYPE_ORDINARY)
        {
            goCircle(mContext,imToCricle.getSrpId(),imToCricle.getKeyword(),imToCricle.getInterestName(),imToCricle.getInterestLogo());
        }else if(imToCricle.getType()==ImToCricle.TYPE_SECRETERY){
            IntentUtil.gotoSecretCricleCard(mContext, imToCricle.getInterestId(), 0);
        }

    }
    /**
     * 群 进入 兴趣圈
     * @param context
     * @param srp_id
     * @param keyword
     * @param interest_name
     * @param interest_logo
     */
    private void goCircle(Context context,String srp_id, String keyword, String interest_name, String interest_logo) {
        BaseInvoke circleInvoke = new BaseInvoke();
        circleInvoke.setType(BaseInvoke.INVOKE_TYPE_INTEREST_INDEX);
        circleInvoke.setSrpId(srp_id);
        circleInvoke.setKeyword(keyword);
        circleInvoke.setInterestName(interest_name);
        circleInvoke.setIconUrl(interest_logo);
        HomePagerSkipUtils.skip(context,circleInvoke);
    }

    public void sendCoinNew() {
        IntentUtil.gotoWebSendCoin(IMChatActivity.this,
                UrlConfig.getSendCoinUrl() + "&receiveuserid=" + mTargetId,
                "interactWeb");
    }

    private List<ChatMsgEntity> editList() {
        if (mAdapter != null && mDataArrays != null) {
            List<ChatMsgEntity> editList = new ArrayList<ChatMsgEntity>();
            for (ChatMsgEntity editItem : mDataArrays) {
                if (editItem.isEdit()) {
                    editList.add(editItem);
                }
            }
            return editList;
        }
        return null;
    }

    /**
     * 进入页面时发送方发送密信条数
     */
    private int initWhisperCountAndDelete() {
        if (mAdapter != null && mDataArrays != null) {
            ChatMsgEntity entity;
            for (int i = 0; i < mDataArrays.size(); i++) {
                entity = mDataArrays.get(i);
                if (entity.isSendWhisperType()
                        && entity.getWhisperTimestamp() > 0) {
                    if (entity.getTimerLength() > 0
                            && !entity.isWhisperDelete()) {
                        ++whispercount;
                    } else {
                        deleteSingleWhisperMsg(entity);
                    }

                }
            }
        } else {
            stopTimer();
        }
        return whispercount;
    }

    /**
     * 定时器中遍历数据
     */
    private void updateWhisperTime() {
        if (mAdapter != null && mDataArrays != null) {
            ChatMsgEntity entity;
            long whisperLength;
            for (int i = 0; i < mDataArrays.size(); i++) {
                entity = mDataArrays.get(i);
                if (entity != null && entity.isSendWhisperType()
                        && entity.getWhisperTimestamp() > 0
                        && !entity.isWhisperDelete()) {
                    if (entity.getTimerLength() > 0) {
                        whisperLength = entity.getTimerLength();
                        --whisperLength;
                        entity.setTimerLength(whisperLength);
                    } else {
                        deleteSingleWhisperMsg(entity);
                        --whispercount;
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

        } else {
            stopTimer();
        }
    }

    // 编辑状态下，选中消息数量
    private int editListcount() {
        int count = 0;
        if (mAdapter != null && mDataArrays != null) {
            for (int i = 0; i < mDataArrays.size(); i++) {
                if (mDataArrays.get(i).isEdit()) {
                    ++count;
                }
            }
        }
        return count;
    }

    // 编辑状态下，删除选中消息
    private void deleteSellectedMsg() {
        List<ChatMsgEntity> editList = editList();
        if (mTargetType == IConst.CHAT_TYPE_PRIVATE) {
            for (ChatMsgEntity item : editList) {
                mImServiceHelp.db_deleteSelectedMessageHistory(mTargetId,
                        item.getRetry());
            }
        } else if (mTargetType == IConst.CHAT_TYPE_GROUP) {
            if (editGroup) {
                for (ChatMsgEntity item : editList) {
                    mImServiceHelp.db_deleteGroupSelectedMessageHistory(
                            mTargetId, item.getRetry());
                }
            } else {
                for (ChatMsgEntity item : editList) {
                    mImServiceHelp.db_deleteSelectedMessageHistory(mTargetId,
                            item.getRetry());
                }
            }
        }


        for (ChatMsgEntity item : editList) {
            try {
                SearchUtils.delMessage(MainActivity.SEARCH_PATH_MEMORY_DIR, item.userId,
                        (short) mTargetType,
                        mTargetId,
                        new Long(item.getId()).intValue(),
                        item.getText());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        SouYueToast.makeText(this, R.string.favorite_del_success,
                SouYueToast.LENGTH_SHORT).show();
        mDataArrays.removeAll(editList);
        mAdapter.notifyDataSetChanged();
        uneditMsg();
        changeRencentTime();
    }

    private void editMsg() {
        im_chat_bottom_layout.setVisibility(View.GONE);
        im_bottom_edit.setVisibility(View.VISIBLE);
        mAdapter.setIsEdit(true);
        mAdapter.notifyDataSetChanged();
    }
    /**
     * 是否 第一次进入群
     */
    private boolean isVisitGroupFirst()
    {
        boolean isFirst= CommSharePreference.getInstance().getValue(CommSharePreference.DEFAULT_USER, "isVisitGroupFirst", true);
        CommSharePreference.getInstance().putValue(CommSharePreference.DEFAULT_USER, "isVisitGroupFirst", false);    //存储常量
        return isFirst;
    }

    /**
     * 第一次进入群 引导
     */
    private void showFirstVisitGroup()
    {
        new Highlight(this)
            .shadow(false)
            .maskColor(getResources().getColor(R.color.transparent_75))
            .addHighlight(goCircleTv, R.layout.tip_im_group_circle,
                    new Highlight.OnPosCallback() {
                        @Override
                        public void getPos(float rightMargin, float bottomMargin, RectF rectF, Highlight.MarginInfo marginInfo) {
                            marginInfo.topMargin = DeviceUtil.dip2px(mContext, 0);
                            marginInfo.leftMargin = DeviceInfo.getScreenWidth() - DeviceUtil.dip2px(mContext, 260);
                        }
                    })
            .show();
    }

    private void uneditMsg() {
        im_chat_bottom_layout.setVisibility(View.VISIBLE);
        im_bottom_edit.setVisibility(View.GONE);
        mAdapter.setIsEdit(false);
        editmore.setClickable(true);
        if (mAdapter != null && mDataArrays != null) {
            for (ChatMsgEntity editItem : mDataArrays) {
                if (editItem.isEdit()) {
                    editItem.setEdit(false);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    // 修改删除历史聊天记录后修改消息页列表时间，规则以最后一条消息时间为准
    private void changeRencentTime() {
        int len = mDataArrays.size();
        if (len == 0) {
            ImserviceHelp.getInstance().db_updateRecentTime(mTargetId,
                    Long.valueOf(SYUserManager.getInstance().getUserId()), 0);
        } else {
            ChatMsgEntity chatMsgEntity = mDataArrays.get(len - 1);
            ImserviceHelp.getInstance().db_updateRecentTime(mTargetId,
                    Long.valueOf(SYUserManager.getInstance().getUserId()),
                    chatMsgEntity.getDate());
        }
    }

    protected void loadMoreHistory() {
        mPullListView.onRefreshComplete();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int onLoadDataCount = 0;
                if (mDataArrays.size() > 0) {
                    id = mDataArrays.get(0).getId();
                    List<ChatMsgEntity> list = msgConverToEntity(
                            mImServiceHelp.db_getMessage(mTargetId, id,
                                    IConst.QUERY_MSG_UP), false);
                    onLoadDataCount = list.size();
                    if (onLoadDataCount < PAGE_COUNT) {
                        isEnd = true;
                        if (mDataArrays.size() > 0) {
                            mDataArrays.get(0).setShowTime(true);
                        }
                    }
                    mDataArrays.addAll(0, list);
                }
                if (llLocation.isShown()) {
                    if (mLocationDatas != null) {
                        mLocationDatas.clear();
                        mLocationDatas = null;
                    }
                    llLocation.setVisibility(View.GONE);
                }
                if (onLoadDataCount > 0) {
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelectionFromTop(onLoadDataCount,
                            mListView.getHeight());
                } else {
                    mListView.setSelectionFromTop(1, mListView.getHeight());
                }
            }
        });
    }

    /**
     * 清空消息历史记录
     */
    private void clearMsg() {
        ImDialog.Builder build = new ImDialog.Builder(this);
        build.setMessage("确认清空聊天记录？");
        build.setPositiveButton(R.string.im_dialog_ok, new ImDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, View v) {
                mImServiceHelp.db_clearMessageHistory(mTargetId, mTargetType);
                if (mDataArrays != null) {
                    mDataArrays.clear();
                    mAdapter.notifyDataSetChanged();
                }
                isEnd = true;
            }
        }).create().show();
    }

    /**
     * 发送赠送中搜币
     */
    private void sendCoin() {
        coinNum = sendCoinNumEdt.getText().toString().trim();
        if (!validNum(coinNum)) {
            SouYueToast.makeText(getApplicationContext(), "请输入赠送个数",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(coinNum) > myCoinNum) {
            SouYueToast.makeText(getApplicationContext(),
                    "您只有" + myCoinNum + "个中搜币，请充值", Toast.LENGTH_SHORT).show();
            return;
        }
        dlgSendCoin.dismiss();
    }

    /**
     * 赠币
     */
    private void sendZSB(String coinCount) {
        ChatMsgEntity entity = getEntity();
        entity.setType(MessageHistory.CONTENT_TYPE_SENDCOIN);
        entity.setText(getJson(coinCount));
        mMessageManager.sendCoinForNew(entity);
    }

    private int myCoinNum;

    public ChatMsgEntity getEntity() {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.userId = mUserId;
        entity.chatId = mTargetId;
        entity.setSendId(mUserId);
        entity.setChatType(mTargetType);
        entity.setIconUrl(SYUserManager.getInstance().getUser().image());
        return entity;
    }

    private void sendAskForCoin() {
        String str = coinNumEdt.getText().toString().trim();
        if (!validNum(str)) {
            SouYueToast.makeText(this, "请输入数量", Toast.LENGTH_SHORT).show();
        } else {
            try {
                ChatMsgEntity entity = getEntity();
                entity.setText(getJson(str));
                entity.setType(MessageHistory.CONTENT_TYPE_TIGER);
                entity.setFromTiger(isFromTiger);
                mMessageManager.sendAskForCoin(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dlgAskForCoin.dismiss();
        }
    }

    private String getJson(String str) {
        JSONObject j = new JSONObject();
        if (str == null || str.equals("null")) {
            str = "0";
        }
        try {
            j.put("count", str.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return j.toString();
    }


    private String getJson(String url, int length) {
        JSONObject j = new JSONObject();
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (length == 0) {
            return null;
        }
        try {
            j.put("length", length + "");
            j.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return j.toString();
    }

    /**
     * 红包json
     *
     * @param url
     * @param text
     * @param jumpType
     * @return
     */
    private String getJson(String url, String text, String jumpType) {
        JSONObject j = new JSONObject();
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        try {
            j.put("text", text);
            j.put("url", url);
            j.put("jumpType", jumpType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return j.toString();
    }

    private String getJson(String url, String localPath, boolean isVertical,
                           float minWidth, float minHeight) {
        JSONObject j = new JSONObject();
        try {
            j.put("url", url);
            j.put("localPath", localPath);
            j.put("isVertical", isVertical);
            j.put("image-height", minHeight);
            j.put("image-width", minWidth);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return j.toString();
    }

    private void sendShareWinCoin(long num, boolean isFromTiger) {
        ChatMsgEntity entity = getEntity();
        entity.setText(getJson(String.valueOf(num)));
        entity.setType(MessageHistory.CONTENT_TYPE_SHARE_TIGER);
        entity.setFromTiger(isFromTiger);
        mMessageManager.sendAskForCoin(entity);
    }

    private boolean validNum(String str) {
        try {
            if (Integer.parseInt(str) > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendCard() {
        Intent intent = new Intent(this, ContactsListActivity.class);
        intent.putExtra(ContactsListActivity.START_TYPE, false);
        intent.putExtra(ContactsListActivity.SHOWCARD, true);
        startActivityForResult(intent, CODE_ADD_VCARD);
    }

    private void pickFromLocal() {

        Intent intent = new Intent(IMChatActivity.this,
                CircleSelImgGroupActivity.class);
        intent.putExtra("piclen", 0);
        startActivityForResult(intent, 1);
    }

    private void takePicture() {
        try {
            imageFileUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new ContentValues());
            if (imageFileUri != null) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                if (Utils.isIntentSafe(this, i)) {
                    startActivityForResult(i, CODE_TAKE_PIC);
                } else {
                    SouYueToast.makeText(this,
                            getString(R.string.dont_have_camera_app),
                            SouYueToast.LENGTH_SHORT).show();
                }
            } else {
                SouYueToast.makeText(this,
                        getString(R.string.cant_insert_album),
                        SouYueToast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            SouYueToast.makeText(this, getString(R.string.cant_insert_album),
                    SouYueToast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picPath = null;
        Bitmap bm = null;
        SimpleDateFormat format = null;
        boolean isVertical = false;
        float minWidth = 0f;
        float minHeight = 0f;
        if (resultCode == RESULT_OK) {
            gvFeatures.setVisibility(View.GONE);
            faceLayout.setVisibility(View.GONE);
            switch (requestCode) {
                case CODE_PICK_PIC:// 如果是直接从相册获取
                    if (data != null) {
                        try {
                            Uri uri = data.getData();
                            picPath = Utils.getPicPathFromUri(uri, this);
                            String fileName = ImageUtil.getFileName(picPath);
                            if (TextUtils.isEmpty(fileName)) {
                                fileName = System.currentTimeMillis() + "";
                            }
                            // bm = ImageUtil.extractThumbNail(picPath);
                            bm = ImageHelper.resize(new File(picPath));
                            if (bm.getHeight() < bm.getWidth()) {
                                isVertical = false;
                                minHeight = bm.getHeight();
                                minWidth = bm.getWidth();
                            } else {
                                isVertical = true;
                                minHeight = bm.getHeight();
                                minWidth = bm.getWidth();
                            }
                            String pic_file_path = ImageUtil.saveBitmap(mContext,
                                    bm, MsgUtils.getCachePathFile(this), fileName
                                            + ".jpg");
                            File filePath = new File(
                                    MsgUtils.getCachePathFile(this), fileName
                                    + ".jpg");
                            ImageHelper.writeToFile(bm, filePath, 60);
                            imageFileUri = null;
                            addPic(filePath.getAbsolutePath(),
                                    filePath.getAbsolutePath(), isVertical,
                                    minWidth, minHeight);
                        } catch (Exception e) {
                            SouYueToast.makeText(IMChatActivity.this, "无法选择此图片", 0)
                                    .show();
                        }
                    }
                    break;
                case CODE_TAKE_PIC:// 如果是调用相机拍照时 拍照
                    if (imageFileUri != null) {
                        picPath = Utils.getPicPathFromUri(imageFileUri, this);
                        // bm = ImageUtil.extractThumbNail(picPath);
                        bm = ImageHelper.resize(new File(picPath));
                        bm = ImageUtil.rotaingImageView(
                                ImageUtil.readPictureDegree(picPath), bm);
                        String fileName = ImageUtil.getFileName(picPath);// 两种缓存使用同样的文件名
                        if (bm.getHeight() < bm.getWidth()) {
                            isVertical = false;
                            minHeight = bm.getHeight();
                            minWidth = bm.getWidth();
                        } else {
                            isVertical = true;
                            minHeight = bm.getHeight();
                            minWidth = bm.getWidth();
                        }

                        String filePath = MsgUtils.saveImage(this, fileName, bm);
                        addPic(filePath, filePath, isVertical, minWidth, minHeight);
                    } else {
                        SouYueToast.makeText(this, getResources().getString(
                                R.string.self_get_image_error), 0).show();
                    }

                    break;

                case CODE_ADD_VCARD: // 添加名片
                    if (data != null) {
                        Contact card = (Contact) data
                                .getSerializableExtra(KEY_GET_CARD_ID);
                        if (card != null) {
                            isFillListData = false; //查看代码后发现这个是为了避免第一次发送后转圈圈不消失
                            sendCard(card);
                        }
                    }
                    break;
                case CODE_FORWARD: // 添加名片
                    uneditMsg();
                    break;
                case IntentUtil.IMSENDCOIN:// 赠币后发送文本
                    SouYueToast.makeText(IMChatActivity.this, "赠中搜币",
                            Toast.LENGTH_SHORT).show();
                    break;
                case WHISPERTYPEDELETE: // 从密信详情页返回密信删除
                    if (mAdapter != null) {
                        int position = 0;
                        ChatMsgEntity intentData = null;
                        if (data != null) {
                            long timeLength = data.getLongExtra("timeLength", 0);
                            boolean isReceive = data.getBooleanExtra("isReceive",
                                    false);
                            deleteWhisper = data.getIntExtra("delete", 0);
                            intentData = (ChatMsgEntity) data
                                    .getSerializableExtra("chatMsgEntity");
                            if (timeLength > 0) {
                                if (mDataArrays.contains(intentData)) {
                                    position = mDataArrays.indexOf(intentData);
                                    mDataArrays.get(position).setTimerLength(
                                            timeLength);
                                    if (isReceive) {
                                        mDataArrays.get(position)
                                                .setIsReceiveDetailOpen(
                                                        ChatMsgEntity.BACK);
                                    }
                                } else {
                                    stopTimer();
                                }
                            }

                        } else {
                            stopTimer();
                        }
                        if (deleteWhisper == 1) {
                            deleteWhisper(intentData);
                        }
                        isDetailOpen = ChatMsgEntity.BACK;
                        mListView.setSelection(position);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;

                case IMIntentUtil.MYGROUP_NICKNAME: // 群聊用的修改名字
                    if (null != data) {
                        findGroupCount();
                        String mynickname = data.getStringExtra("group_name");
                        iscleanHistory = data.getBooleanExtra("isCleanHistory",
                                false);
                        findViewById(R.id.group_count_tv).setVisibility(
                                View.VISIBLE);
                        ((TextView) findViewById(R.id.group_count_tv)).setText("("
                                + mGroupCount + ")");
                        titleName.setText(mynickname);
                        mGroup.setGroup_nick_name(mynickname);
                    }
                    break;
            }
        } else if (resultCode == IMIntentUtil.MYCHAT_EDITMSG
                || resultCode == IMIntentUtil.MYGROUP_EDITMSG) { // 私聊详情返回编辑消息
            if (mTargetType == IConst.CHAT_TYPE_GROUP)
                editGroup = true;

            List<MessageHistory> list = mImServiceHelp.db_getMessage(mTargetId,
                    -1, IConst.QUERY_MSG_FIRST);
            if (list.size() == 0)
                mDataArrays.clear();
            editMsg();
        } else if (requestCode == IntentUtil.IMSENDCOIN) {// 赠币后发送文本提示
            mMessageManager.setSendCoinBack(true);
            if (resultCode == WebSrcViewActivity.ZSBRESULT) {
                String ZSBcount = "";
                if (data != null) {
                    ZSBcount = data.getStringExtra("ZSBcount");
                }
                sendZSB(ZSBcount);
            }
        } else if (requestCode == IMIntentUtil.SEND_RED_PACKET) {// 发送红包回调
            if (data != null) {
                mMessageManager.sendRedPacket(getRedPacket(data.getStringExtra("url"), data.getStringExtra("content")));
            }
        } else if (resultCode == IMIntentUtil.CIRCLESELECTPIC) { // 4.1新加跳转兴趣圈选择图片
            // 1,从相册选择图片一张或多张
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> list = new ArrayList<String>();
                    list = data.getStringArrayListExtra("imgseldata");
                    for (int i = 0; i < list.size(); i++) {//
                        String picPath = null;
                        Bitmap bm = null;
                        boolean isVertical = false;
                        float minWidth = 0f;
                        float minHeight = 0f;
                        try {
                            picPath = list.get(i);
                            String fileName = ImageUtil.getFileName(picPath);
                            if (TextUtils.isEmpty(fileName)) {
                                fileName = System.currentTimeMillis() + "";
                            }
                            bm = ImageUtil.extractThumbNail(picPath);
                            bm = ImageHelper.resize(new File(picPath));
                            if (bm.getHeight() < bm.getWidth()) {
                                isVertical = false;
                                minHeight = bm.getHeight();
                                minWidth = bm.getWidth();
                            } else {
                                isVertical = true;
                                minHeight = bm.getHeight();
                                minWidth = bm.getWidth();
                            }
                            String filePath = MsgUtils.saveImage(mContext,
                                    fileName, bm);
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("pic_file_path", filePath);
                            bundle.putString("pic_file_path", filePath);
                            bundle.putBoolean("isVertical", isVertical);
                            bundle.putFloat("minWidth", minWidth);
                            bundle.putFloat("minHeight", minHeight);
                            msg.what = 1;
                            msg.setData(bundle);
                            mImageHandler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                            SouYueToast.makeText(IMChatActivity.this,
                                    "无法选择此图片", 0).show();
                        }
                    }
                }
            }).start();

        } else if (resultCode == IMIntentUtil.SCARD) {
            if (data != null) {
                Group groupCard = (Group) data
                        .getSerializableExtra(KEY_GET_CARD_ID);
                if (groupCard != null) {
                    sendGroup_Card(groupCard);
                }
            }
        } else if (resultCode == 100) {
            iscleanHistory = data.getBooleanExtra("isCleanHistory", false);
            mBubbleNum = 0;
            llLocation.setVisibility(View.GONE);
        } else if (resultCode == com.zhongsou.souyue.circle.ui.UIHelper.RESULT_OK) {
            if (data == null) {
                return;
            }
            groupMemberses.clear();
            ArrayList<GroupMembers> list = (ArrayList<GroupMembers>) data
                    .getSerializableExtra("selMembers");
            if (list != null && list.size() > 0) {
                groupMemberses.addAll(list);
                StringBuilder stringBuffer = new StringBuilder();// 所有被修改过的nickname
                String oldnickname = "";// 原始的nickname
                String newc = "";// 修改后的nickname
                for (int i = 0; i < list.size(); i++) {
                    oldnickname = groupMemberses.get(i).getNick_name();

                    long uid = list.get(i).getMember_id();
                    String nickname = list.get(i).getNick_name();
                    Contact contact = ImserviceHelp.getInstance()
                            .db_getContactById(uid);
                    GroupMembers groupMembers = ImserviceHelp.getInstance()
                            .db_findMemberListByGroupidandUid(mGroup.getGroup_id(), uid);
                    String newname = "";
                    if (contact != null
                            && !TextUtils.isEmpty(contact.getComment_name())) {
                        newname = contact.getComment_name();
                    } else if (groupMembers != null
                            && !TextUtils
                            .isEmpty(groupMembers.getMember_name())) {
                        newname = groupMembers.getMember_name();
                    } else {
                        newname = nickname;
                    }
                    newc = oldnickname.replace(nickname, newname);
                    if (i == 0) {
                        stringBuffer.append(newc).append(" ");
                    } else {
                        stringBuffer.append("@").append(newc).append(" ");
                    }
                    UserBean userBean = new UserBean();
                    if (TextUtils.isEmpty(groupMemberses.get(i).getConmmentName())) {
                        userBean.setNick(nickname);
                    } else {
                        userBean.setNick(groupMemberses.get(i).getConmmentName());
                    }
                    userBean.setUid(groupMemberses.get(i).getMember_id());
                    setAtFriend(userBean);
                }
                mEditTextContent.requestFocus();
                mEditTextContent.setFocusable(true);
                mEditTextContent.append(stringBuffer);
            }
            Editable eatable = mEditTextContent.getText();
            if (eatable != null && eatable.length() > 1) {
                Selection.setSelection(eatable, eatable.length());
            }

            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else if (resultCode == IMIntentUtil.SHAREGROUP) {
            uneditMsg();
        }
    }

    public void setAtFriend(UserBean userBean) {
        atFriends.getUsers().add(userBean);
    }

    private void sendCard(Contact card) {
        ChatMsgEntity e = getEntity();
        e.setType(MessageHistory.CONTENT_TYPE_VCARD);
        e.status = MessageHistory.STATUS_SENTING;
        e.setCard(card);
        mMessageManager.sendCard(e);
    }

    private void sendGroup_Card(Group group) {
        ChatMsgEntity e = getEntity();
        e.setType(MessageHistory.CONTENT_TYPE_GROUP_CARD);
        e.status = MessageHistory.STATUS_SENTING;
        e.setGroup(group);
        mMessageManager.sendCard(e);
    }

    private void addPic(String picPath, String localPath, boolean isVertical,
                        float minWidth, float minHeigh) {
        ChatMsgEntity entity = getEntity();
        entity.setText(getJson(picPath, localPath, isVertical, minWidth, minHeigh));
        entity.setType(IMessageConst.CONTENT_TYPE_NEW_IMAGE);
        entity.setUrl(picPath);
        entity.setMinHeight(minHeigh);
        entity.setMinWidth(minWidth);
        isDetailOpen = ChatMsgEntity.BACK;
        mMessageManager.saveImage(entity, localPath, isVertical, minWidth,
                minHeigh);
    }

    private void addAttach() {
        if (gvFeatures.isShown()) {
            hideKeyboard();
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            gvFeatures.setVisibility(View.GONE);
        } else {
            if (isWhisper) {
                gvFeatures.setVisibility(View.GONE);
            } else {
                getWindow()
                        .setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                hideKeyboard();
                gvFeatures.setVisibility(View.VISIBLE);
                faceLayout.setVisibility(View.GONE);
            }
        }
    }

    private void showEmoji() {
        if (!isWhisper) {
            if (faceLayout.isShown()) {
                faceLayout.setVisibility(View.GONE);
                showKeyboard();
                keyBoardSwitch
                        .setBackgroundResource(R.drawable.btn_msg_face_selector);
            } else {
                faceLayout.setVisibility(View.VISIBLE);
                gvFeatures.setVisibility(View.GONE);
                keyBoardSwitch
                        .setBackgroundResource(R.drawable.btn_msg_keyboard_selector);
            }
        } else {
            if (faceLayout.isShown()) {
                faceLayout.setVisibility(View.GONE);
                keyBoardSwitch
                        .setBackgroundResource(R.drawable.btn_im_send_smile_selector);
                showKeyboard();
            } else {
                faceLayout.setVisibility(View.VISIBLE);
                gvFeatures.setVisibility(View.GONE);
                keyBoardSwitch
                        .setBackgroundResource(R.drawable.mixin_btn_keyboard_selector);
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mEditTextContent.setFocusable(true);
        mEditTextContent.setFocusableInTouchMode(true);
        mEditTextContent.requestFocus();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (gvFeatures.isShown() && !disable.isShown()) {
                int[] xy = new int[2];
                gvFeatures.getLocationInWindow(xy);
                if (ev.getY() < xy[1]) {
                    gvFeatures.setVisibility(View.GONE);
                    return true;
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    public void showSendCoinDlg() {
        if (dlgSendCoin == null) {
            createSendCionDlg();
        }
        coinNumText.setText("您账户中有" + myCoinNum + "个中搜币");
        dlgSendCoin.show();
    }

    private void createSendCionDlg() {
        dlgSendCoin = new Dialog(this, R.style.coin_dlg);
        View view = LayoutInflater.from(this).inflate(
                R.layout.im_send_coin_dlg, null);
        sendCoinNumEdt = (EditText) view.findViewById(R.id.im_send_cion_edit);
        sendCoinOk = view.findViewById(R.id.im_send_coin_ok);
        coinNumText = (TextView) view.findViewById(R.id.im_send_my_cointext);
        view.findViewById(R.id.im_send_coin_cancel).setOnClickListener(this);
        sendCoinOk.setOnClickListener(this);
        view.findViewById(R.id.im_send_cion_charge).setOnClickListener(this);
        dlgSendCoin.setContentView(view);
    }

    public void showAskCoinDlg() {
        if (dlgAskForCoin == null) {
            createCoinDlg();
        }
        dlgAskForCoin.show();
    }

    private void createCoinDlg() {
        dlgAskForCoin = new Dialog(this, R.style.coin_dlg);
        View view = LayoutInflater.from(this).inflate(R.layout.ask_for_coin,
                null);
        coinNumEdt = (EditText) view.findViewById(R.id.ask_for_edit);
        coinNumEdt.setText("10");
        coinOk = view.findViewById(R.id.ask_for_ok);
        view.findViewById(R.id.ask_for_cancel).setOnClickListener(this);
        coinOk.setOnClickListener(this);
        dlgAskForCoin.setContentView(view);

    }

    private void dismissPup() {
        disable.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PushService.setIsInChat(MainApplication.getInstance(), 0);
        if (dlgAskForCoin != null && dlgAskForCoin.isShowing()) {
            dlgAskForCoin.dismiss();
        }
        if (dlgSendCoin != null && dlgSendCoin.isShowing()) {
            dlgSendCoin.dismiss();
        }
        AudioLoader.getInstance().stopCurrentPlaying();
    }

    private void setLayouListener() {
        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    keybordShowing = true;
                    if (faceLayout.isShown()) {
                        faceLayout.setVisibility(View.GONE);
                        keyBoardSwitch.setBackgroundResource(!isWhisper ? R.drawable.btn_msg_face_selector : R.drawable.btn_im_send_smile_selector);
                    }
                    mListView.setSelection(mListView.getCount() - 1);
                } else {
                    keybordShowing = false;
                }
            }
        });
    }

//
//    public void onHttpError(String methodName, AjaxStatus as) {
//        SouYueToast.makeText(getApplicationContext(), "获取中搜币失败",
//                Toast.LENGTH_SHORT).show();
//    }

    BroadcastReceiver chatMsgReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("data");
            if (json != null) {
                MessageHistory msg = getMsHistory(json);
                if (msg == null) {
                    return;
                }
                if (BroadcastUtil.ACTION_MSG_SEND_FAIL.equals(msg.getAction())) {
                    Message message = new Message();
                    message.obj = msg;
                    message.what = DELAY_SHOW_SEND_FAIL;
                    MessageHistory historyMessage = MessageHistoryDaoHelper.getInstance(IMChatActivity.this).find(msg.getUuid(), mUserId);
                    if (historyMessage != null && (System.currentTimeMillis() - historyMessage.getDate() > 3000)) {
                        mHandler.sendMessage(message);
                    } else {
                        mHandler.sendMessageDelayed(message, 3000);
                    }
                } else if (BroadcastUtil.ACTION_MSG_ADD.equals(msg.getAction())) {
                    newMsgReceiver();
                } else if (BroadcastUtil.ACTION_MSG_SEND_SUCCESS.equals(msg
                        .getAction())) {
                    msgSuccessReceiver(msg);
                } else if (BroadcastUtil.ACTION_MSG_SEND_ERROR_NOTFRIEND
                        .equals(msg.getAction())) {
                    if (mTargetId == msg.getChat_id()) {
                        dfNotFriendReceiver(msg);
                    }
                } else if (BroadcastUtil.ACTION_MSG_ADD_ONLINE.equals(msg
                        .getAction())) {
                    long sender = msg.getChat_id();
                    if (msg.getChat_type() == 1 || msg.getChat_type() == 0) {
                        if (msg.getContent_type() != 1001
                                && msg.getChat_id() != 0) {
                            if (sender != mTargetId) {
                                silding_contactcount
                                        .setVisibility(View.VISIBLE);
                                SharedPreferences.Editor edit = mBubbleSp
                                        .edit();
                                edit.putInt("bubblenum",
                                        mBubbleSp.getInt("bubblenum", 0) + 1);
                                edit.commit();
                            } else {
                                if (msg.getContent_type() != IMessageConst.CONTENT_TYPE_NEW_SYSTEM_MSG) {
                                    mBubbleNum++;
                                    if (sysp.getBoolean(SYSharedPreferences.KEY_PUSH_VIBRATE, true) && isMsgNotify) {
                                        mVibrator = (Vibrator) context
                                                .getSystemService(Service.VIBRATOR_SERVICE);
                                        mVibrator.vibrate(800);
                                    }
                                }
                            }
                        }
                    }
                    onlineMsgReceiver(msg);
                }
            }

        }
    };

    /**
     * 对方已经将自己删除
     */
    private void dfNotFriendReceiver(MessageHistory msg) {
        mDataArrays.add(new ChatMsgEntity(msg));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 来新消息
     */
    private void newMsgReceiver() {
        List<MessageHistory> tmp = mImServiceHelp.db_getMessage(mTargetId,
                mCurSessionOrd, IConst.QUERY_MSG_DOWN);
        List<ChatMsgEntity> list = msgConverToEntity(tmp, true);
        mDataArrays.addAll(list);
        mAdapter.notifyDataSetChanged();
        refreshSessionOrd();
    }

    private void onlineMsgReceiver(MessageHistory msg) {
        if (mTargetId != null && mTargetId.equals(msg.getChat_id())) {
            ChatMsgEntity cme = new ChatMsgEntity(msg);
            if (mTargetType == IConst.CHAT_TYPE_PRIVATE) {
                if (cme.isComMsg()) {
                    cme.setIconUrl(mTargetIcon);
                } else {
                    cme.setIconUrl(SYUserManager.getInstance().getUser().image());
                }
            } else if (mTargetType == IConst.CHAT_TYPE_GROUP) {
                ImserviceHelp.getInstance().db_updateRecentBy1(
                        msg.getChat_id(), mUserId, "0");
                if (!TextUtils.isEmpty(msg.getBy2())) {
                    GroupMember groupMember = new Gson().fromJson(msg.getBy2(),
                            new TypeToken<GroupMember>() {
                            }.getType());
                    cme.setIconUrl(groupMember.getUserImage());
                }
            } else if (mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                changeCT(msg, cme);
                cme.setIconUrl(mTargetIcon);
            }
            if (cme.getDate() == 0) {
                cme.setDate(System.currentTimeMillis());
            }
            if (Math.abs(cme.getDate() - minShowTimeItem) > DURITION) {
                cme.setShowTime(true);
                minShowTimeItem = cme.getDate();
            }
            mDataArrays.add(cme);
            if (llLocation != null) {
                llLocation.setVisibility(View.GONE);
            }
            mAdapter.notifyDataSetChanged();
            if (cme.getText() == null) {//为空判断，解当群成员自己退群时，群主试图删除这个成员的问题
                Toast.makeText(this, getString(R.string.im_operat_fail), Toast.LENGTH_LONG).show();
            } else if (mTargetType == IConst.CHAT_TYPE_GROUP && cme != null && cme.getText().equals("您已经不在群组了.")) {
                Toast.makeText(this, "抱歉,您被请出该群", Toast.LENGTH_LONG).show();
                IMIntentUtil.gotoMainActivity(this);
                finish();
            }
        }
    }

    /**
     * 消息发送成功
     *
     * @param msg
     */
    private void msgSuccessReceiver(MessageHistory msg) {
        if (msg == null) {
            return;
        }
        ChatMsgEntity e = mSendingMap.remove(msg.getUuid());
        if (e != null) {
            if (e.getDate() == 0) {
                e.setDate(System.currentTimeMillis());
            }
            if (Math.abs(e.getDate() - maxShowTimeItem) > DURITION) {
                e.setShowTime(true);
                maxShowTimeItem = e.getDate();
            }
            e.setSuccess();
            e.setSessionOrd(msg.getSession_order());
            mCurSessionOrd = msg.getSession_order();
        }
        updateWhisper(e, ChatMsgEntity.TOTALLENGTH);
        mAdapter.notifyDataSetChanged();
        if (mTargetId != null && mTargetId != 0l) {
            ThreadPoolUtil.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    ImserviceHelp.getInstance().db_clearMessageRecentBubble(
                            mTargetId);
                }
            });
        }
    }

    /**
     * 密信发送成功，操作密信
     */
    public void updateWhisper(ChatMsgEntity e, long timeLength) {
        if (e != null) {
            if (e.isSendWhisperType()) {
                e.setTimerLength(timeLength);
                long currentTime = Calendar.getInstance().getTimeInMillis();
                e.setWhisperTimestamp(currentTime);
                if (mImServiceHelp != null) {
                    mImServiceHelp.db_updateMessageHistoryTime(e.getRetry(),
                            e.getType(), mTargetId, currentTime);
                }
                ++whispercount;
                if (!isTimerOpen) {
                    startTimer();
                }
            }
        }

    }

    /**
     * 点击密信详情页回来删除密信
     */
    public void deleteWhisper(ChatMsgEntity e) {
        if (e != null) {
            if (e.isSendWhisperType()) {
                if (mImServiceHelp != null) {
                    mDataArrays.remove(e);
                    deleteSingleWhisperMsg(e);
                    ImserviceHelp.getInstance()
                            .db_deleteSelectedMessageHistory(mTargetId,
                                    e.getRetry());
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 删除单条密信
     */
    private void deleteSingleWhisperMsg(ChatMsgEntity entity) {
        entity.setWhisperDelete(true);
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    /**
     * 消息发送失败
     *
     * @param msg
     */
    public void msgFaildReceiver(MessageHistory msg) {
        if (msg == null) {
            return;
        }

        ChatMsgEntity e = mSendingMap.get(msg.getUuid());
        if (e != null) {
            if (e.getDate() == 0) {
                e.setDate(System.currentTimeMillis());
            }
            e.setFailed();
            mAdapter.notifyDataSetChanged();
        }

    }

    protected void onStop() {
        super.onStop();
        isFillListData = true;  //避免发送名片退出后再进页面空白
        isEnd = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initAll(intent);
    }

    protected void initAll(Intent intent) {
        setContentView(R.layout.im_comment_main);
        isWhisper = false;
        isDetailOpen = ChatMsgEntity.INIT;
        handler = new Handler();
        mImageHandler = new MHandler();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        hideKeyboard();
        User u = SYUserManager.getInstance().getUser();
        if (u != null)
            mUserId = u.userId();
        mCurSessionOrd = 0l;
        registerReceiver();
        getIntentData(intent);
        initV();

        // 初始化 导航栏
        initTab();
        mFilter.addAction(Constants.ADD_ACTION);
        mFilter.addAction(Constants.DELETE_ACTION);
        registerReceiver(mUpdateRecevier, mFilter);

        mAdapter = new IMChatAdapter(this, mDataArrays);
        mBubbleSp = getSharedPreferences("BUBBLESP", MODE_PRIVATE);
        mPullListView.setAdapter(mAdapter);
        mMessageManager = new MessageManager(this, mTargetId, mTargetType);
        mMessageManager.setRefreshListener(this);
        mAdapter.setMsgManager(mMessageManager);
        if (mTargetType == IConst.CHAT_TYPE_GROUP)
            findGroupCount();
        setData(intent);

    }

    protected void onDestroy() {
        if (chatMsgReceiver != null)
            unregisterReceiver(chatMsgReceiver);
        mHandler.removeMessages(DELAY_SHOW_SEND_FAIL);
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
        if (mMessageManager != null) {
            mMessageManager.destroy();
        }
        if (mUpdateRecevier != null) {
            unregisterReceiver(mUpdateRecevier);
        }

        super.onDestroy();
    }

    protected MessageHistory getMsHistory(String json) {
        try {
            return new Gson().fromJson(json, new TypeToken<MessageHistory>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void refreshSessionOrd() {
        if (mDataArrays.size() > 0) {
            mCurSessionOrd = mDataArrays.get(mDataArrays.size() - 1)
                    .getSessionOrd();
        }
    }

    @Override
    public void finish() {
        if (mTargetId != null && mTargetId != 0l) {
            ThreadPoolUtil.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    ImserviceHelp.getInstance().db_clearMessageRecentBubble(
                            mTargetId);
                }
            });
        }
        stopTimer();
        super.finish();
    }

    public static void startIMChat(Context activity, Serializable obj,
                                   boolean from, String hello) {
        Intent intent = new Intent(activity, IMChatActivity.class);
        intent.putExtra(IMChatActivity.KEY_CONTACT, obj);
        intent.putExtra(ContactsListActivity.START_FROM, from);
        intent.putExtra(IMChatActivity.KEY_ACTION, hello);
        activity.startActivity(intent);
    }

    private int redCount;

    // 显示未读消息气泡数
    public void loadBubleData() {
        redCount = mBubbleSp.getInt("bubblenum", 0) - mBubbleNum;
        if (redCount > 0 && SouyueAPIManager.isLogin()) {
            silding_contactcount.setVisibility(View.VISIBLE);
        } else {
            silding_contactcount.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (faceLayout.getVisibility() == View.VISIBLE) {
            faceLayout.setVisibility(View.GONE);
        } else {
            stopTimer();
            finish();
        }

        if (mTargetType == IConst.CHAT_TYPE_PRIVATE
                || mTargetType == IConst.CHAT_TYPE_SERVICE_MESSAGE) {
            savaDraftEvent();
        } else if (mTargetType == IConst.CHAT_TYPE_GROUP) {
            savaGroupDraftEvent();
        }

    }

    private void savaDraftEvent() {
        /**
         * 保存草稿
         */
        String draft_content = mEditTextContent.getText().toString();
        if (!TextUtils.isEmpty(draft_content.replace(" ", ""))
                && !TextUtils.isEmpty(draft_content.replace("\n", ""))) {
            ImserviceHelp.getInstance()
                    .db_insertDraft(mTargetId, draft_content);
        } else {
            ImserviceHelp.getInstance().db_insertDraft(mTargetId, "");
        }

    }

    /**
     * 详情页消息免打扰状态改变
     *
     * @param isOpen
     */
    @Override
    public void msgNotifyChange(boolean isOpen) {
        isMsgNotify = !isOpen;
    }

    /**
     * 功能条目点击
     * TODO 以后要抽取到view当中处理
     *
     * @param itemType
     */
    @Override
    public void itemClick(int itemType) {
        switch (itemType) {
            case IMFeaturesHelper.TYPE_SELECT_PHOTO:    //相册
                pickFromLocal();
                break;
            case IMFeaturesHelper.TYPE_TAKE_PHOTO:    //照相
                takePicture();
                break;
            case IMFeaturesHelper.TYPE_CARD:    //名片
                gvFeatures.setVisibility(View.GONE);
                sendCard();
                break;
            case IMFeaturesHelper.TYPE_ASK_FOR_ZSB:    //求中搜币
                showAskCoinDlg();
                break;
            case IMFeaturesHelper.TYPE_GIFT_ZSB:    //赠中搜币
                sendCoinNew();
                break;
            case IMFeaturesHelper.TYPE_WHISPER:    //密信
                showKeyboard();
                addAttach();// 隐藏底部按钮
                isWhisper = true;
                im_add.setBackgroundResource(R.drawable.btn_im_exit_whisper_selector);
                im_send.setBackgroundResource(R.drawable.btn_im_send_msg_selector);
                keyBoardSwitch
                        .setBackgroundResource(R.drawable.btn_im_send_smile_selector);
                mEditTextContent.setBackgroundResource(R.drawable.et_mixin);
                im_send.setTextColor(Color.RED);
                switchBtn.setVisibility(View.GONE);
                break;
            case IMFeaturesHelper.TYPE_RED_PACKET:    //红包
                IMIntentUtil.gotoSendRedPacketWeb(IMChatActivity.this, mTargetType, mTargetId);
                break;
        }
    }

    public class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    try {
                        addPic(bundle.getString("pic_file_path"),
                                bundle.getString("pic_file_path"),
                                bundle.getBoolean("isVertical"),
                                bundle.getFloat("minWidth"),
                                bundle.getFloat("minHeight"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void refresh(ChatMsgEntity msgEntity) {
        if (msgEntity.status == MessageHistory.STATUS_SENTING) {
            mDataArrays.add(msgEntity);
            if (llLocation != null) {
                llLocation.setVisibility(View.GONE);
            }
            mSendingMap.put(msgEntity.UUId, msgEntity);
        } else if (msgEntity.status == MessageHistory.STATUS_SENT_FAIL) {
            msgEntity.status = MessageHistory.STATUS_SENTING;
            mSendingMap.put(msgEntity.UUId, msgEntity);
        } else if (msgEntity.status == MessageHistory.STATUS_HAS_SENT) {
            mDataArrays.add(msgEntity);
            if (llLocation != null) {
                llLocation.setVisibility(View.GONE);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * TestActivity的跳转invoke
     *
     * @param activity
     * @param targetType
     * @param targetId
     */
    public static void invoke(Context activity, int targetType, long targetId) {
        Intent intent = new Intent();
        intent.setClass(activity, IMChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_TARGET_TYPE, targetType);
        intent.putExtra(EXTRA_TARGET_ID, targetId);
        activity.startActivity(intent);
    }

    /**
     * 特有跳轉，定位搜索到的消息位置
     */
    public static void invokeTarget(Activity activity, int targetType,
                                    long targetId, int msgId) {
        Intent intent = new Intent();
        intent.setClass(activity, IMChatActivity.class);
        intent.putExtra(EXTRA_TARGET_TYPE, targetType);
        intent.putExtra(EXTRA_TARGET_ID, targetId);
        intent.putExtra(EXTRA_MSG_ID, msgId);
        intent.putExtra(FROM_SEARCH_TARGET, true);
        activity.startActivity(intent);
    }

    /**
     * 保存草稿
     */
    private void savaGroupDraftEvent() {
        mContent = mEditTextContent.getText().toString().trim();
        if (mContent.length() > 0
                && !TextUtils.isEmpty(mContent.replace(" ", ""))
                && !TextUtils.isEmpty(mContent.replace("\n", ""))) {
            if (mDraftForAt != null && atFriends.getUsers().size() == 0 && !mDraftForAt.equals("")) {
                atFriends.getUsers().addAll(new Gson().fromJson(mDraftForAt, AtFriend.class).getUsers());
            }
            List<UserBean> userBeans = atFriends.getUsers();// 被@者id和搜悦昵称

            newatFriend = new AtFriend();// ct=21的java bean，是否是@靠该bean确定
            newatFriend.setUsers(new ArrayList<UserBean>());

            // 发送前检查是否包含@信息，靠nickname和edit中的内容对比比较。前提是edittext中的名字遵循
            // 【备注名>群昵称>搜悦昵称】
            for (int i = 0; i < userBeans.size(); i++) {
                Contact contact = ImserviceHelp.getInstance()
                        .db_getContactById(userBeans.get(i).getUid());// 获取@用户是否是好友
                GroupMembers groupMembers = ImserviceHelp.getInstance()
                        .db_findMemberListByGroupidandUid(mGroup.getGroup_id(), userBeans.get(i).getUid());
                ;// 获取@用户是否在群里
                String newname = "";// 该用户的名字 遵循【备注名>群昵称>搜悦昵称】
                if (contact != null
                        && !TextUtils.isEmpty(contact.getComment_name())) {
                    newname = contact.getComment_name();// 备注名
                } else if (groupMembers != null) {
                    newname = TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers
                            .getNick_name() : groupMembers.getMember_name();// 群昵称
                } else {
                    newname = userBeans.get(i).getNick();// 搜悦昵称
                }
                if (mContent.contains(newname))// 如果edittext中包含被@的名字，则在users列表中加入该用户信息【id，nickname】
                    newatFriend.getUsers().add(atFriends.getUsers().get(i));
            }

            if (newatFriend.getUsers().size() != 0) {// @消息
                ChatMsgEntity m = getEntity();
                m.setType(MessageHistory.CONTENT_TYPE_AT_FRIEND);
                String c = mEditTextContent.getText().toString();
                List<UserBean> userBean = newatFriend.getUsers();
                for (int i = 0; i < userBean.size(); i++) {
                    long uid = userBean.get(i).getUid();
                    String nickname = userBean.get(i).getNick();
                    GroupMembers groupMembers = ImserviceHelp.getInstance()
                            .db_findMemberListByGroupidandUid(mGroup.getGroup_id(), uid);
                    Contact contact = ImserviceHelp.getInstance()
                            .db_getContactById(userBeans.get(i).getUid());
                    if (contact != null
                            && !TextUtils.isEmpty(contact.getComment_name())) {
                        c = c.replace("@" + nickname + " ", "@" + contact.getComment_name() + " ");
                    } else if (groupMembers != null
                            && !TextUtils
                            .isEmpty(groupMembers.getMember_name())) {
                        c = c.replace("@" + nickname + " ", "@" + groupMembers.getMember_name() + " ");
                    }
                }

                newatFriend.setC(c);
                atInfo = new Gson().toJson(newatFriend);

            }
            /**
             * 如果草稿中包含at，则入库
             */
            if (newatFriend != null && newatFriend.getUsers().size() != 0) {
                ImserviceHelp.getInstance().db_insertDraftForAt(mTargetId,
                        mContent, atInfo);
            } else {
                /**
                 * 保存草稿
                 */
                ImserviceHelp.getInstance().db_insertDraft(mTargetId, mContent);
            }
        } else {// 如果为空，把@草稿字段置空
            ImserviceHelp.getInstance().db_insertDraftForAt(mTargetId, "", "");
        }

    }

    /**
     * 群聊的发送消息
     */
    private void sendGroupText() {
        mContent = mEditTextContent.getText().toString();
        // if (!isWhisper) {
        if (mContent.length() > 0
                && !TextUtils.isEmpty(mContent.replace("\n", ""))
                && !TextUtils.isEmpty(mContent.replace(" ", ""))) {
            List<UserBean> userBeans = atFriends.getUsers();// 被@者id和搜悦昵称

            AtFriend newatFriend = new AtFriend();// ct=21的java
            // bean，是否是@靠该bean确定
            newatFriend.setUsers(new ArrayList<UserBean>());

            // 发送前检查是否包含@信息，靠nickname和edit中的内容对比比较。前提是edittext中的名字遵循
            // 【备注名>群昵称>搜悦昵称】
            for (int i = 0; i < userBeans.size(); i++) {
                Contact contact = ImserviceHelp.getInstance()
                        .db_getContactById(userBeans.get(i).getUid());// 获取@用户是否是好友
                GroupMembers groupMembers = ImserviceHelp.getInstance()
                        .db_findMemberListByGroupidandUid(mGroup.getGroup_id(), userBeans.get(i).getUid());// 获取@用户是否在群里
                String newname = "";// 该用户的名字 遵循【备注名>群昵称>搜悦昵称】
                if (contact != null
                        && !TextUtils.isEmpty(contact.getComment_name())) {
                    newname = contact.getComment_name();// 备注名
                } else if (groupMembers != null) {
                    newname = TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers
                            .getNick_name() : groupMembers.getMember_name();// 群昵称
                } else {
                    newname = userBeans.get(i).getNick();// 搜悦昵称
                }
                if (mContent.contains(newname))// 如果edittext中包含被@的名字，则在users列表中加入该用户信息【id，nickname】
                    newatFriend.getUsers().add(atFriends.getUsers().get(i));
            }

            if (!TextUtils.isEmpty(mDraftForAt)) {
                newatFriend = new Gson().fromJson(mDraftForAt, AtFriend.class);
                for (int i = 0; i < newatFriend.getUsers().size(); i++) {
                    UserBean newUserBean = new UserBean();
                    newUserBean.setUid(newatFriend.getUsers().get(i).getUid());
                    newUserBean
                            .setNick(newatFriend.getUsers().get(i).getNick());
                    setAtFriend(newUserBean);
                }

            }

            if (newatFriend.getUsers().size() == 0) {// 普通文本消息
                ChatMsgEntity m = getEntity();
                m.setText(mContent);
                if (mMessageManager.sendText(m)) {
                    mEditTextContent.getText().clear();
                    mEditTextContent.requestFocus();
                } else {
                    Logger.i("souyue", "IMChatActivity.sendGroupText", "发送失败  mMessageManager.sendText(m) = false");
                }
            } else {// @消息
                ChatMsgEntity m = getEntity();
                m.setType(MessageHistory.CONTENT_TYPE_AT_FRIEND);
                String c = mEditTextContent.getText().toString();
                List<UserBean> userBean = newatFriend.getUsers();
                for (int i = 0; i < userBean.size(); i++) {
                    long uid = userBean.get(i).getUid();
                    String nickname = userBean.get(i).getNick();
                    GroupMembers groupMembers = ImserviceHelp.getInstance()
                            .db_findMemberListByGroupidandUid(
                                    mGroup.getGroup_id(), uid);
                    Contact contact = ImserviceHelp.getInstance()
                            .db_getContactById(userBeans.get(i).getUid());
                    if (contact != null
                            && !TextUtils.isEmpty(contact.getComment_name())) {
                        c = c.replace("@" + nickname + " ", "@" + contact.getComment_name() + " ");
                    } else if (groupMembers != null
                            && !TextUtils
                            .isEmpty(groupMembers.getMember_name())) {
                        c = c.replace("@" + nickname + " ", "@" + groupMembers.getMember_name() + " ");
                    }
                }

                newatFriend.setC(c);
                if (!TextUtils.isEmpty(mDraftForAt) && TextUtils.isEmpty(c)) {
                    atInfo = mDraftForAt;
                    mDraftForAt = "";
                } else {
                    atInfo = new Gson().toJson(newatFriend);
                }

                m.setText(atInfo);
                m.setContentForAt(m.getText());
                if (mMessageManager.sendAtFriend(m,
                        MessageHistory.CONTENT_TYPE_AT_FRIEND, m.getText())) {
                    mEditTextContent.getText().clear();
                    mEditTextContent.requestFocus();
                }
            }
            mListView.setSelection(mListView.getCount() - 1);
        } else {
            SouYueToast.makeText(this, R.string.im_blank,
                    SouYueToast.LENGTH_SHORT).show();
        }
        if (atFriends.getUsers() != null)
            atFriends.getUsers().clear();
    }

    /**
     * 服务号与原有消息整合
     *
     * @param m
     * @param e
     */
    private void changeCT(MessageHistory m, ChatMsgEntity e) {
        // 通过文本类型来查找接受的特定类型的ct1或2的服务号
        if (m.getContent_type() == IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_FIRST
                || m.getContent_type() == IMessageConst.CONTENT_TYPE_SERVICE_MESSAGE_SECOND) {
            // 通过muid查数据库，并set进去
            ServiceMessage serviceMessage = mImServiceHelp
                    .db_getServiceMessageMessage(m.getUuid());
            if (serviceMessage != null) {
                if (serviceMessage.getDetail_type() == 2) {
                    mServiceMsgChild = new Gson().fromJson(
                            serviceMessage.getExra(),
                            new TypeToken<List<ServiceMessageChild>>() {
                            }.getType());
                    serviceMessage.appendmServiceMessageChilds(mServiceMsgChild);

                } else if (serviceMessage.getDetail_type() == 1) {
                    mSerMsgImageUrls = new Gson().fromJson(
                            serviceMessage.getImage_url(),
                            new TypeToken<List<String>>() {
                            }.getType());
                    serviceMessage.appendImageUrls(mSerMsgImageUrls);
                }
                e.setServiceMessage(serviceMessage);
            }
        }
    }

    // 往输入框中追加内容
    public void setEditMsg(String msg) {
        mEditTextContent.append(msg);
        if (!keybordShowing) {
            showKeyboard();
        }
        isListViewTouch = false;
        isHeadLongClick = true;
        if (faceLayout.isShown())
            faceLayout.setVisibility(View.GONE);
        Editable eatable = mEditTextContent.getText();
        if (eatable != null && eatable.length() > 1) {
            Selection.setSelection(eatable, eatable.length());
        }
    }

    public void cancelHeadLongClick() {
        isHeadLongClick = false;
    }

    /**
     * 获得红包bean
     *
     * @return
     */
    private ChatMsgEntity getRedPacket(String url, String text) {
        ChatMsgEntity chatMsgEntity = getEntity();
        chatMsgEntity.setType(MessageHistory.CONTENT_TYPE_RED_PAKETS);
        chatMsgEntity.setText(getJson(url, text, "redPacketUrl"));
        chatMsgEntity.status = IMessageConst.STATUS_HAS_SENT;
        return chatMsgEntity;
    }

}
