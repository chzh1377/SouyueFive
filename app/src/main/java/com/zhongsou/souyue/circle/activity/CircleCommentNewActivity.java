package com.zhongsou.souyue.circle.activity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.GalleryCommentActivity;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.ListViewPostsAdapterNew;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.circle.model.Reply;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.util.TextViewUtil;
import com.zhongsou.souyue.circle.view.CircleGridview;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.dialog.SYProgressDialog;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.adapter.EmojiAdapter;
import com.zhongsou.souyue.im.adapter.EmojiPagerAdapter;
import com.zhongsou.souyue.im.emoji.Emoji;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.module.ExpressionBean;
import com.zhongsou.souyue.im.module.GifBean;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.net.EntBaseRequest;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.MBlogCommentsReq;
import com.zhongsou.souyue.net.circle.MblogDelete2Req;
import com.zhongsou.souyue.net.circle.MblogReplyReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.webview.JavascriptInterface;
import com.zhongsou.souyue.utils.AnoyomousUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYMediaplayer;
import com.zhongsou.souyue.utils.SYMediaplayer_Mine;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.view.ExpressionView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CircleCommentNewActivity extends BaseActivity implements OnClickListener, JavascriptInterface.ImagesListener {

    private SYMediaplayer_Mine au;

    //评论文字的最大值
    private static final int TEXT_MAX = 4000;

    //获取主贴和新闻信息的key
    public static final String REPLY_MAIN_TITLE = "mai_title";
    public static final String REPLY_MAIN_NAME = "mai_name";
    public static final String REPLY_MAIN_TIME = "mai_time";
    public static final String REPLY_MAIN_BLOG_ID = "mai_blog_id";
    public static final String REPLY_IS_FROM_PUSH = "is_from_push";


    //提示Dialog
    private SYProgressDialog sydialog;
    //    private String tempFilename;
    private String token;
    //    private Handler handler = new Handler();
//    private Task task;
//    private RelativeLayout playDelLayout;

    //跟贴相关的信息
    private ImageView iv_userPhoto;
    private TextView tv_nickName, tv_time, tv_content;
//    private LinearLayout ll_images;

    private CommentsForCircleAndNews post;


    private SYMediaplayer_Mine audio;

    private Reply replyTemp;

    private ListView mListView;
    private CommentsAdapter adapter;
    private View header;


    //默认每次获取回复的个数
    private static final int COMMENT_PAGE_SIZE = 20;

    private List<Reply> replies = new ArrayList<Reply>();

    //上拉刷新相关
    private View loadMoreView;
    private int visibleLast = 0;
    private int visibleCount = 0;
    private boolean isLoadAll = false;
    private boolean needLoad;
    private int pno = 1;

    private Reply removingReply;
//    private boolean isAdmin;

    private long interest_id;
    private String nickName;
    private String image;
    private long mblog_userId;

    private boolean isLoadSuccess = false;

    public List<String> imageUrls;


    private Button circle_follow_send, circle_follow_add;
    private EditText circle_follow_edit_text;
    private ImageView circle_follow_add_emoj, circle_follow_add_key;
    private LinearLayout ll_circle_follow_attach, circle_follow_add_pic, circle_follow_take_photo;
    private ArrayList<View> pageViews;
    private ViewPager mViewPaper;
    private List<EmojiAdapter> emojiAdapter;
    private EmojiAdapter adapterEmo;
    private List<List<Emoji>> emojis;
    private ArrayList<ImageView> pointViews;
    private LinearLayout pagenumLayout;
    private LinearLayout faceLayout;
    private LinearLayout rl;
    private TextView tvImgNum;

    private int is_bantalk;////0-不禁言 1-禁言
    private LinearLayout layoutBottom;

    private GridViewAdapter gridViewAdapter;
    private CircleGridview layout_image;
    private int operType;
    private String srpword;
    private String srpid;
    private String url;
    private int mDetailType;
    private int mRoleType;
    private int mCircleType;
    private RelativeLayout detail_voice_master;
    private TextView detail_voice_second_m;
    private ImageView detail_voice_animator_m;
    private TextView comment_comefrom;

    private ExpressionView.OnExpressionListener expressionListener;

    //主贴或者新闻信息
    private TextView tvMainTitle;   //帖子标题
    private TextView tvMainName;
    private TextView tvMainTime;
    private TextView tvBackMain;

    private String mMainTitle = "";
    private String mMainName = "";
    private String mMainTime = "";

    private boolean isFromePush;//是否从评论和回复的推送过来的，推送过来的点击查看正文时候启动新的详情页
    private boolean isAdmin;   //代表当前登录的人是不是圈主

    private long mBlogId;

    //副圈主和圈主标识
    private TextView tvHost;
    private TextView tvSubHost;
    private boolean mIsLogin;
    private boolean mAnoyomous;
    private Bitmap imageBitmap;
    private CheckBox circle_follow_anonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_activity_comment);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE | LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();

        initBottomData();
        initBottomView();

        expressionListener = new ExpressionView.OnExpressionListener() {
            @Override
            public void emojiItemClick(Emoji emoji) {
                if (emoji.getId() == R.drawable.btn_msg_facedelete_selector) {
                    int selection = circle_follow_edit_text.getSelectionStart();
                    String text = circle_follow_edit_text.getText().toString();
                    if (selection > 0) {
                        String text2 = text.substring(selection - 1);
                        if ("]".equals(text2)) {
                            int start = text.lastIndexOf("[");
                            int end = selection;
                            circle_follow_edit_text.getText().delete(start, end);
                            return;
                        }
                        circle_follow_edit_text.getText().delete(selection - 1, selection);
                    }
                }
                if (!TextUtils.isEmpty(emoji.getCharacter())) {
                    SpannableString spannableString = EmojiPattern.getInstace().addFace(CircleCommentNewActivity.this, emoji.getId(), emoji.getCharacter());
                    int index = circle_follow_edit_text.getSelectionStart();
                    Editable editable = circle_follow_edit_text.getText();
                    editable.insert(index, spannableString);
                }
            }

            @Override
            public void gifItemClick(GifBean gifBean) {

            }

            @Override
            public void expressionItemClick(ExpressionBean e) {

            }
        };

        setEmoji();

        Intent intent = getIntent();
        post = (CommentsForCircleAndNews) intent.getSerializableExtra("post");
        String comefrom = post.getSrp_word();
        if (comefrom == null || comefrom.equals("") || post.getIs_current_comment() == 1) {
            comment_comefrom.setVisibility(View.GONE);
            tv_time.setPadding(DeviceUtil.dip2px(this, 10), 0, 0, 0);
        } else {
            comment_comefrom.setText(post.getSrp_word());
            comment_comefrom.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(post.getVoice())) {
            detail_voice_master.setVisibility(View.VISIBLE);
            tv_content.setVisibility(View.GONE);
            detail_voice_second_m.setText(post.getVoice_length() + "\"");
            detail_voice_master.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    au.play(detail_voice_animator_m, SYMediaplayer.SOURCE_TYPE_NET, post.getVoice());
                }
            });
        } else {
            detail_voice_master.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
        }
        isAdmin = intent.getBooleanExtra("isAdmin", false);
        interest_id = intent.getLongExtra("interest_id", 0);
        nickName = intent.getStringExtra("nickName");
        operType = intent.getIntExtra("operType", -1);
        mDetailType = intent.getIntExtra("data_type", 0);
        mRoleType = post.getRole();
        image = post.getImage_url();
        mblog_userId = intent.getLongExtra("mblog_userId", 0);
        is_bantalk = getIntent().getIntExtra("isBantank", -1);
        srpid = getIntent().getStringExtra("srpid");
        srpword = getIntent().getStringExtra("srpword");
        url = getIntent().getStringExtra("url");
        mCircleType = getIntent().getIntExtra("mCircleType", 0);
        mMainName = getIntent().getStringExtra(REPLY_MAIN_NAME);
        mMainTime = getIntent().getStringExtra(REPLY_MAIN_TIME);
        mMainTitle = getIntent().getStringExtra(REPLY_MAIN_TITLE);
        mBlogId = getIntent().getLongExtra(REPLY_MAIN_BLOG_ID, 0);
        isFromePush = getIntent().getBooleanExtra(REPLY_IS_FROM_PUSH, false);

        if (is_bantalk == Constant.MEMBER_BAN_TALK_YES) {    //is_bantalk == 1
            Toast.makeText(this, "您已被禁言", Toast.LENGTH_LONG).show();
            layoutBottom.setVisibility(View.GONE);
        } else if (is_bantalk == Constant.MEMBER_BAN_TALK_NO) {
            layoutBottom.setVisibility(View.VISIBLE);
        }
        if (post != null) {
            // 加载数据
            updateBlogInfo(post);

//            http.getBlogCommentsNew(SYUserManager.getInstance().getToken(), post.getComment_id(), pno, COMMENT_PAGE_SIZE, operType, srpword, srpid, 0);
            MBlogCommentsReq req = new MBlogCommentsReq(HttpCommon.DETAIL_BLOG_COMMENTS_ID, CircleCommentNewActivity.this);
            req.setParams(SYUserManager.getInstance().getToken(), post.getComment_id(),
                    pno, COMMENT_PAGE_SIZE, operType, srpword, srpid, 0);

            CMainHttp.getInstance().doRequest(req);

        } else {
            //TODO 没有获取到帖子ID 进行错误处理
        }

        if (sysp == null) sysp = SYSharedPreferences.getInstance();
        au = SYMediaplayer_Mine.getInstance(this);
        audio = SYMediaplayer_Mine.getInstance(this);
        sydialog = new SYProgressDialog(this, 0, "正在发送回复");
        token = SYUserManager.getInstance().getToken();
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmojiPattern.getInstace().getFileText(CircleCommentNewActivity.this);
            }
        }).start();

        if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
            SouYueToast.makeText(CircleCommentNewActivity.this, "网络不可用", Toast.LENGTH_LONG);
            mListView.removeFooterView(loadMoreView);
            return;
        }
//        if(isFromePush){ //如果是从im过来的，isAdmin从psots里面去
//            if(mRoleType == Constant.ROLE_ADMIN){
//                isAdmin = true;
//            }else{
//                isAdmin = false;
//            }
//        }
//
//        //判断是否显示相应的标识
//        if (mRoleType == Constant.ROLE_ADMIN && (post.getIs_anonymity() != 1 || post.getIs_private() != 1)) {
//            tvHost.setVisibility(View.VISIBLE);
//            tvSubHost.setVisibility(View.GONE);
//        } else if (mRoleType == Constant.ROLE_SUB_ADMIN && (post.getIs_anonymity() != 1 || post.getIs_private() != 1)) {
//            tvSubHost.setVisibility(View.VISIBLE);
//            tvHost.setVisibility(View.GONE);
//        } else {
//            tvHost.setVisibility(View.GONE);
//            tvSubHost.setVisibility(View.GONE);
//        }

        if (post.getIs_anonymity() == 1) { // 匿名状态
            tvHost.setVisibility(View.GONE);
            tvSubHost.setVisibility(View.GONE);
        } else {
            if (post.getIs_private() == 1) { // 隐私保护状态
                tvHost.setVisibility(View.GONE);
                tvSubHost.setVisibility(View.GONE);
            } else {
                if (post.getRole() == Constant.ROLE_ADMIN) { //圈主
                    tvHost.setVisibility(View.VISIBLE);
                    tvSubHost.setVisibility(View.GONE);
                } else if (post.getRole() == Constant.ROLE_SUB_ADMIN) {// 副圈主
                    tvSubHost.setVisibility(View.VISIBLE);
                    tvHost.setVisibility(View.GONE);
                } else {
                    tvHost.setVisibility(View.GONE);
                    tvSubHost.setVisibility(View.GONE);
                }

            }
        }
    }


    private void initViews() {
        circle_follow_anonymous = (CheckBox) findViewById(R.id.circle_follow_anonymous);
        circle_follow_anonymous.setOnClickListener(this);
        layoutBottom = (LinearLayout) findViewById(R.id.ll_circle_comment_bottom);
        ((RelativeLayout) findView(R.id.ll_comment_list)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        ((LinearLayout) findViewById(R.id.rl_comment_parent)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        //内容相关
        mListView = (ListView) findViewById(R.id.lv_circle_comment_list);
        adapter = new CommentsAdapter();
        tvBackMain = (TextView) findViewById(R.id.tv_reply_main);
        tvBackMain.setOnClickListener(this);

        header = View.inflate(this, R.layout.circle_comment_header_new, null);
        mListView.addHeaderView(header);
        detail_voice_animator_m = (ImageView) header.findViewById(R.id.detail_voice_animator_m);
        detail_voice_master = (RelativeLayout) header.findViewById(R.id.detail_voice_master);
        detail_voice_second_m = (TextView) header.findViewById(R.id.detail_voice_second_m);
        comment_comefrom = (TextView) header.findViewById(R.id.comment_comefrom);
        tvHost = (TextView) findViewById(R.id.tv_reply_tip_host);
        tvSubHost = (TextView) findViewById(R.id.tv_reply_tip_sub_host);
        comment_comefrom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getType() - 1 == DetailActivity.DETAIL_TYPE_NEWS) {
                    IntentUtil.gotoSouYueSRP(CircleCommentNewActivity.this, post.getSrp_word(), post.getSrp_id(), "");
                } else {
                    ListViewPostsAdapterNew.showCircleIndex(CircleCommentNewActivity.this, post.getSrp_id(), post.getSrp_word(), post.getSrp_word(), "", "", "", 0x3);
                }
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard();
            }

        });
        loadMoreView = View.inflate(this, R.layout.ent_refresh_footer, null);
        mListView.addFooterView(loadMoreView);
        mListView.setAdapter(adapter);
        //跟贴相关得内容
        iv_userPhoto = (ImageView) header.findViewById(R.id.iv_circle_comment_blog_photo);
        tv_nickName = (TextView) header.findViewById(R.id.tv_circle_comment_blog_name);
        tv_content = (TextView) header.findViewById(R.id.tv_circle_comment_reply_content);
        tv_time = (TextView) header.findViewById(R.id.tv_circle_comment_blog_time);

        tvMainTitle = (TextView) findViewById(R.id.tv_reply_main_title);
        tvMainName = (TextView) findViewById(R.id.tv_reply_main_name);
        tvMainTime = (TextView) findViewById(R.id.tv_reply_main_time);

        layout_image = (CircleGridview) header.findViewById(R.id.layout_image);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int itemsLastIndex = adapter.getCount();
                if (itemsLastIndex < 0) {
                    return;
                }
                int lastIndex = itemsLastIndex;
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (visibleLast - 1) == lastIndex && needLoad) {
                    needLoad = false;
                    //加载下一页
//                    http.getBlogCommentsNew(SYUserManager.getInstance().getToken(), post.getComment_id(), pno, COMMENT_PAGE_SIZE, operType, srpword, srpid, replies.get(replies.size() - 1).getReply_id());
                    MBlogCommentsReq req = new MBlogCommentsReq(HttpCommon.DETAIL_BLOG_COMMENTS_ID, CircleCommentNewActivity.this);
                    req.setParams(SYUserManager.getInstance().getToken(), post.getComment_id(),
                            pno, COMMENT_PAGE_SIZE, operType, srpword, srpid,
                            replies.get(replies.size() - 1).getReply_id());
                    CMainHttp.getInstance().doRequest(req);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                visibleCount = visibleItemCount;
                visibleLast = firstVisibleItem + visibleItemCount - 1;
                if (isLoadAll) {
                    ++visibleLast;
                }
            }
        });

    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        switch (request.getmId()) {
            case HttpCommon.DETAIL_BLOG_COMMENTS_ID:
                getBlogCommentsNewSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_BLOG_DELETE_ID:
                deleteCommentNewSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_BLOG_REPLY_ID:
                replyNewSuccess(request.<HttpJsonResponse>getResponse());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        circle_follow_edit_text.clearFocus();
        //数据回显
        setReview();
    }

    /**
     * 重置匿名状态
     */
    public void setReview() {
        Log.e("comment", "取出匿名状态");
        mIsLogin = SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_ADMIN);
        mAnoyomous = AnoyomousUtils.getAnoyomouState(interest_id + "");
        if (mIsLogin) {
            imageBitmap = AnoyomousUtils.getBitmapForCurrentUser(mContext, interest_id + "");
            if (!mAnoyomous) {
                circle_follow_anonymous.setBackgroundDrawable(new BitmapDrawable(imageBitmap));
                return;
            }
            circle_follow_anonymous.setBackgroundResource(R.drawable.common_comment_anonymous);
            return;
        }
        circle_follow_anonymous.setBackgroundResource(R.drawable.default_head);
    }

    @Override
    public void setImages(String iags) {
        if (null != iags) {
            imageUrls = Arrays.asList(iags.trim().split(" "));
            com.tencent.mm.sdk.platformtools.Log.i("", "imageUrls size: " + imageUrls.size());
        }
    }

    /**
     * 设置匿名状态动画
     */
    private void setAnymous() {
        ValueAnimator ani = ValueAnimator.ofFloat(0, 180);
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private boolean changeRes = false;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                circle_follow_anonymous.setRotationY(fraction * 180);
                if (fraction > 0.5f && !changeRes) {
                    if (mAnoyomous) {
                        circle_follow_anonymous.setBackgroundResource(R.drawable.common_comment_anonymous);
                    } else {
                        circle_follow_anonymous.setBackgroundDrawable(new BitmapDrawable(imageBitmap));
                    }
                    changeRes = true;
                }
                if (fraction > 0.9) {
                    circle_follow_anonymous.setRotationY(0);
                }
            }
        });
        ani.setDuration(200);
        ani.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_follow_anonymous:
//              匿名开关
                if (!mIsLogin) {
                    SouYueToast.makeText(this, this.getString(R.string.guest_cant_hide), Toast.LENGTH_SHORT).show();
                    return;
                }
                mAnoyomous = !mAnoyomous;
                setAnymous();
                AnoyomousUtils.setAnoyomousState(mAnoyomous, interest_id + "");
                break;
            case R.id.circle_follow_add:
                ll_circle_follow_attach.setVisibility(View.VISIBLE);
                faceLayout.setVisibility(View.GONE);
                //隐藏键盘
                hideKeyboard();
                break;
            case R.id.circle_follow_send:
                if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    Toast.makeText(CircleCommentNewActivity.this, "网络不可用", Toast.LENGTH_LONG).show();
                    return;
                }
                String text = circle_follow_edit_text.getText().toString();
                if (!StringUtils.isEmpty(text) && text.length() <= TEXT_MAX) {
                    if (text.replaceAll("(\r\n|\n)+", " ").replaceAll(" ", "").equals("")) {
                        Toast.makeText(CircleCommentNewActivity.this, "回复不能为空", Toast.LENGTH_LONG).show();
                        return;
                    }
//                    http.replyNew(SYUserManager.getInstance().getToken(), url, text.replaceAll("(\r\n|\n)+", " "), "", "[]", 0, operType, srpid, srpword, post.getComment_id(), mDetailType + 1, mAnoyomous);
                    MblogReplyReq req = new MblogReplyReq(HttpCommon.DETAIL_BLOG_REPLY_ID, this);
                    req.setParams(SYUserManager.getInstance().getToken(), url, text.replaceAll("(\r\n|\n)+", " "),
                            "", "[]", 0, operType, srpid, srpword,
                            post.getComment_id(), mDetailType + 1, mAnoyomous);
                    CMainHttp.getInstance().doRequest(req);
                    replyTemp = new Reply();
                    replyTemp.setVoice("");
                    replyTemp.setVoice_length(0);
                    replyTemp.setContent(text);
                    if (sydialog != null) sydialog.show();
                } else if (StringUtils.isEmpty(text)) {
                    SouYueToast.makeText(this, R.string.content_no_null, SouYueToast.LENGTH_SHORT).show();
                } else {
                    SouYueToast.makeText(this, R.string.content_more_than_1000, SouYueToast.LENGTH_SHORT).show();
                }
                new SYInputMethodManager(this).hideSoftInput();
                break;
            case R.id.circle_follow_edit_text:
                faceLayout.setVisibility(View.GONE);
                circle_follow_add_key.setVisibility(View.GONE);
                circle_follow_add_emoj.setVisibility(View.VISIBLE);
                break;
            case R.id.circle_follow_add_emoj:
                faceLayout.setVisibility(View.VISIBLE);
                circle_follow_add_key.setVisibility(View.VISIBLE);
                circle_follow_add_emoj.setVisibility(View.GONE);
                hideKeyboard();
                break;
            case R.id.circle_follow_add_key:
                faceLayout.setVisibility(View.GONE);
                circle_follow_add_key.setVisibility(View.GONE);
                circle_follow_add_emoj.setVisibility(View.VISIBLE);
                showKeyboard();
                circle_follow_edit_text.requestFocus();
                break;
            case R.id.tv_reply_main:
                GalleryCommentActivity.needFinish = true;
                if (isFromePush) {
                    SearchResultItem item1 = new SearchResultItem();
                    item1.setBlog_id(mBlogId);
                    item1.keyword_$eq(srpword);
                    item1.srpId_$eq(srpid);
                    item1.setInterest_id(interest_id);
                    IntentUtil.skipDetailPage(CircleCommentNewActivity.this, item1, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
                } else {
                    finishWithResult();
                }
                break;
            default:
                break;
        }
    }

    //回复成功的回调
    public void replyNewSuccess(HttpJsonResponse res) {
        JsonObject bodyJson = res.getBody();
        if (res != null && res.getCode() == 200) {
            if (sydialog != null && sydialog.isShowing()) sydialog.cancel();
            Reply reply = new Reply();
            if (SouyueAPIManager.isLogin() && (nickName != null && !"".equals(nickName)))
                reply.setNickname(nickName);
            else if (SouyueAPIManager.isLogin()) {
                reply.setNickname(SYUserManager.getInstance().getName());
            } else {
                reply.setNickname(nickName);
            }
            if (AnoyomousUtils.getAnoyomouState(interest_id + "")) {
                //如果是匿名状态
                if (mCircleType == DetailActivity.CIRCLE_TYPE_CIRCLE) {
                    //圈子类型
                    reply.setNickname("匿名用户");
                } else {
                    //新闻类型
                    reply.setNickname("匿名用户");
                }
            }
            reply.setReply_time(new Date().getTime() + "");

            reply.setContent(replyTemp.getContent());
            reply.setVoice_length(replyTemp.getVoice_length());
            reply.setVoice(replyTemp.getVoice());
            reply.setReply_id(bodyJson.get("reply_id").getAsLong());
            reply.setUser_id(Long.parseLong(SYUserManager.getInstance().getUserId()));
            reply.setIs_host(mblog_userId == reply.getUser_id() ? 1 : 0);
            replyTemp = null;
            replies.add(0, reply);
            adapter.notifyDataSetChanged();
            circle_follow_edit_text.setText("");
            //TODO  统计需要主贴id  先注释掉
            //统计
//            UpEventAgent.onGroupReply(this, interest_id + "."+"", "",post.getMblog_id()+"");
            showToast("回复成功");
            switch (mCircleType) {
                case DetailActivity.CIRCLE_TYPE_CIRCLE:
                    UpEventAgent.onGroupReply(this, interest_id + "." + "", "", post.getBlog_id() + "");
                    break;
                default:
            }
        }
    }

    //获取帖子回复列表成功的回调
    public void getBlogCommentsNewSuccess(HttpJsonResponse res) {
        if (res != null) {
            String bodyJson = res.getBodyArray().toString();
//            List<Reply> temp = JSON.parseArray(bodyJson, Reply.class);
            List<Reply> temp = new Gson().fromJson(bodyJson, new TypeToken<List<Reply>>() {
            }.getType());
            if (temp != null && temp.size() > 0) {
                if (replies == null || replies.size() == 0) {
                    replies = temp;
                } else {
                    replies.addAll(temp);
                }
                // 更新UI数据 显示回复
                if (pno == 1 && replies.size() < COMMENT_PAGE_SIZE) {
                    mListView.removeFooterView(loadMoreView);
                    if (adapter.getCount() != 0)
                        Toast.makeText(this, "全部回复已加载", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();

                needLoad = true;
                pno++;
                mListView.setSelection(visibleLast - visibleCount + 1);
            } else {
                mListView.removeFooterView(loadMoreView);
                if (adapter.getCount() != 0)
                    Toast.makeText(this, "全部回复已加载", Toast.LENGTH_SHORT).show();
            }
            isLoadSuccess = true;
        }
    }


    public void deleteCommentNewSuccess(HttpJsonResponse res) {
        showToast("删除回复成功");
        replies.remove(removingReply);
        adapter.notifyDataSetChanged();
        removingReply = null;
        if (audio != null) audio.stopPlayAudio();

    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        if (sydialog.isShowing()) {
            sydialog.dismiss();
        }
        switch (request.getmId()) {
            case HttpCommon.DETAIL_BLOG_COMMENTS_ID:
                mListView.removeFooterView(loadMoreView);
                Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
                break;
        }
    }

//    @Override
//    public void onHttpError(String methodName) {
//        if (sydialog.isShowing()) {
//            sydialog.dismiss();
//        }
//        if ("getBlogCommentsNew".equals(methodName)) {
//            mListView.removeFooterView(loadMoreView);
//            Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
//        }
//    }

    /**
     * 更新帖子信息
     *
     * @param posts
     */
    private void updateBlogInfo(final CommentsForCircleAndNews posts) {
        if (posts != null) {
            //aQuery.id(iv_userPhoto).image(image, true, true, 0, R.drawable.circle_default_head);
            if (posts.getIs_anonymity() == 0) { // 匿名装填
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, image, iv_userPhoto, MyDisplayImageOption.options);
                if (image == null) {
                    iv_userPhoto.setImageResource(R.drawable.circle_default_head);
                }
            } else {
                iv_userPhoto.setImageResource(R.drawable.common_comment_anonymous);
            }
            tv_nickName.setText(posts.getNickname());
            if (posts.getUser_id() != mblog_userId)
                tv_nickName.setCompoundDrawables(null, null, null, null);
            tv_time.setText(StringUtils.convertDate(posts.getCreate_time()));
            tv_content.setText(EmojiPattern.getInstace().getExpressionString(this, post.getContent()));
//            if(!TextUtils.isEmpty(mMainTime)){
//                if (mMainTime.length() < 13 && !TextUtils.isEmpty(mMainTime)) {
//                    mMainTime = Long.toString(Long.decode(mMainTime) * 1000L);
//                }
//            }
            tvMainTime.setText(mMainTime);
            tvMainName.setText(mMainName);
            tvMainTitle.setText(mMainTitle);
            final List<String> images = posts.getImages();
            if (images != null && images.size() > 0) {
                //对跟帖回复
                layout_image.setVisibility(View.VISIBLE);
//                ll_images.setVisibility(View.GONE);
                gridViewAdapter = new GridViewAdapter(images);
                layout_image.setAdapter(gridViewAdapter);
                layout_image.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent();
                        intent.setClass(CircleCommentNewActivity.this, TouchGalleryActivity.class);
                        TouchGallerySerializable tg = new TouchGallerySerializable();
                        tg.setItems(images);
                        tg.setClickIndex(position);
                        Bundle extras = new Bundle();
                        extras.putSerializable("touchGalleryItems", tg);
                        intent.putExtras(extras);
                        CircleCommentNewActivity.this.startActivity(intent);
                    }
                });
            }
        }
    }

    /**
     * @param view
     */
    public void onBackPressClick(View view) {
        finishWithResult();
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }

    private void showToast(String msg) {
        SouYueToast.makeText(this, msg, SouYueToast.LENGTH_SHORT).show();
    }

    public void onDeletePost(View v) {
//        http.deletePosts(post.getBlog_id(), token);
    }

    class CommentsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return replies.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = null;
            ViewHolder holder = null;
            if (convertView == null) {
                view = View.inflate(CircleCommentNewActivity.this, R.layout.detail_comments_item, null);
                holder = new ViewHolder();
                holder.time = (TextView) view.findViewById(R.id.tv_circle_comment_reply_time);
                holder.content = (TextView) view.findViewById(R.id.tv_circle_comment_reply_content);
                holder.detail_voice_second_r = (TextView) view.findViewById(R.id.detail_voice_second_r);
                holder.delete = (ImageView) view.findViewById(R.id.tv_circle_comment_reply_delete);
                holder.voice = (LinearLayout) view.findViewById(R.id.ll_circle_comment_reply_voice);
                holder.detail_voice_2 = (ImageView) view.findViewById(R.id.detail_voice_animator_r);
                holder.line = (View) view.findViewById(R.id.tv_circle_comment_reply_line);
                holder.lineDiveder = view.findViewById(R.id.tv_circle_comment_reply_line_divider);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            }


            if (position == 0) {
                holder.line.setVisibility(View.VISIBLE);
                holder.lineDiveder.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.GONE);
                holder.lineDiveder.setVisibility(View.VISIBLE);
            }

            final Reply reply = replies.get(position);

            //填充数据
            holder.time.setText(StringUtils.convertDate(reply.getReply_time()));

//            TextViewUtil.setTextWithHost(CircleCommentActivity.this, holder.content, reply.getNickname(), reply.getContent(), reply.getIs_host() == 1);
            TextViewUtil.setTextWithHostAndTime(CircleCommentNewActivity.this, holder.content, reply.getNickname(), reply.getContent(), StringUtils.convertDate(reply.getReply_time()));

            if (reply.getVoice() != null && !"".equals(reply.getVoice()) && reply.getVoice_length() > 0) {
                TextViewUtil.setTextWithHost(CircleCommentNewActivity.this, holder.content, reply.getNickname(), reply.getContent(), false);
                holder.voice.setVisibility(View.VISIBLE);
                holder.detail_voice_second_r.setText(reply.getVoice_length() + "\"");
            } else {
                holder.voice.setVisibility(View.GONE);
            }
            int visible = View.GONE;
            try {
                long userid = Long.parseLong(SYUserManager.getInstance().getUserId());
                if (reply.getUser_id() == userid) {
                    visible = View.VISIBLE;
                } else if (isAdmin) {
                    if (reply.getIs_current_reply() == 1) {//如果是当前词才能删除
                        visible = View.VISIBLE;
                    }
                }
            } catch (Exception e) {
            }
            holder.delete.setVisibility(visible);
            holder.voice.setTag(holder.detail_voice_2);

            holder.delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //removingCommentView = header.comment;
                    AlertDialog.Builder builder = new AlertDialog.Builder(CircleCommentNewActivity.this);
                    builder.setTitle("删除提示").setMessage("您确定删除该回复吗？").setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removingReply = reply;
//                                    http.deleteComment(reply.getReply_id() + "", token);
                            //2代表是删除回复,1代表是删除评论
//                            http.deleteCommentNew(SYUserManager.getInstance().getToken(), reply.getReply_id(), operType, srpid, srpword, 2);
                            MblogDelete2Req req = new MblogDelete2Req(HttpCommon.DETAIL_BLOG_DELETE_ID, CircleCommentNewActivity.this);
                            req.setParams(SYUserManager.getInstance().getToken(), reply.getReply_id(), operType, srpid, srpword, 2);
                            CMainHttp.getInstance().doRequest(req);

                        }
                    }).setNegativeButton("取消", null);
                    builder.create().show();
                }
            });

            holder.voice.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    audio.play((ImageView) view.getTag(), SYMediaplayer.SOURCE_TYPE_NET, reply.getVoice());
                }
            });

            return view;
        }
    }

    static class ViewHolder {
        TextView time, content, detail_voice_second_r;
        ImageView delete;
        ImageView detail_voice_2;
        LinearLayout voice;
        View line;
        View lineDiveder;

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (audio != null) audio.stopPlayAudio();
    }

    private void finishWithResult() {

        if (!isLoadSuccess) {
            finish();
            return;
        }
        ArrayList<Reply> newReplyList = new ArrayList<Reply>();
        if (replies != null) {
            int size = replies.size();
            if (size > 0) {
                newReplyList.add(replies.get(0));
            }
            if (size > 1) {
                newReplyList.add(replies.get(1));
            }
            if (size > 2) {
                newReplyList.add(replies.get(2));
            }
        }

        Intent data = getIntent();
        data.putExtra("posts", post);
        data.putExtra("comment_id", post.getComment_id());
        data.putExtra("newReplyList", newReplyList);
        setResult(UIHelper.RESULT_CODE_REPLY, data);
        finish();
    }

    private void initBottomView() {
        faceLayout = (LinearLayout) findViewById(R.id.layout_face);
        ll_circle_follow_attach = (LinearLayout) findViewById(R.id.ll_circle_follow_attach);
        ll_circle_follow_attach.setVisibility(View.GONE);
        circle_follow_add = (Button) findViewById(R.id.circle_follow_add);
        circle_follow_add.setVisibility(View.GONE);
        circle_follow_send = (Button) findViewById(R.id.circle_follow_send);
        circle_follow_send.setVisibility(View.VISIBLE);
        circle_follow_edit_text = (EditText) findViewById(R.id.circle_follow_edit_text);
        circle_follow_edit_text.setHint("写回复");
        circle_follow_add_pic = (LinearLayout) findViewById(R.id.circle_follow_add_pic);
        circle_follow_take_photo = (LinearLayout) findViewById(R.id.circle_follow_take_photo);
        circle_follow_take_photo.setVisibility(View.GONE);
        circle_follow_add_pic.setVisibility(View.GONE);
        tvImgNum = (TextView) findViewById(R.id.tv_img_count);
        tvImgNum.setVisibility(View.GONE);
        circle_follow_edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    faceLayout.setVisibility(View.GONE);
                }
            }
        });
        circle_follow_edit_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.toString().length();
                if (len > 0) {
                    circle_follow_send.setBackgroundResource(R.drawable.circle_follow_btn_send_down);
                    circle_follow_send.setTextColor(Color.WHITE);
                } else {
                    circle_follow_send.setBackgroundResource(R.drawable.circle_follow_btn_send);
                    circle_follow_send.setTextColor(Color.BLACK);
                }
            }
        });
        circle_follow_add_emoj = (ImageView) findViewById(R.id.circle_follow_add_emoj);
        circle_follow_add_key = (ImageView) findViewById(R.id.circle_follow_add_key);
        mViewPaper = (ViewPager) findViewById(R.id.viewpager);
        pagenumLayout = (LinearLayout) findViewById(R.id.layout_pagenum);

        circle_follow_add.setOnClickListener(this);
        circle_follow_send.setOnClickListener(this);
        circle_follow_add_emoj.setOnClickListener(this);
        circle_follow_add_key.setOnClickListener(this);
        circle_follow_edit_text.setOnClickListener(this);
    }

    private void initBottomData() {
        emojis = EmojiPattern.getInstace().emojiLists;
    }

    /**
     * 初始化显示表情的viewpager
     */
    private void Init_viewPager() {
        pageViews = new ArrayList<View>();
        View nullView1 = new View(this);

        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        emojiAdapter = new ArrayList<EmojiAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(this);
            adapterEmo = new EmojiAdapter(this, expressionListener, emojis.get(i));
            view.setAdapter(adapterEmo);
            emojiAdapter.add(adapterEmo);
//            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        View nullView2 = new View(this);
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }

    /**
     * 初始化页点
     */
    private void Init_Point() {

        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.dot_hui);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            pagenumLayout.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.dot_hei);
            }
            pointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void Init_Data() {
        mViewPaper.setAdapter(new EmojiPagerAdapter(pageViews));

        mViewPaper.setCurrentItem(1);
        mViewPaper.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                draw_Point(arg0);
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        mViewPaper.setCurrentItem(arg0 + 1);// 第二屏
                        // 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.dot_hei);
                    } else {
                        mViewPaper.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(R.drawable.dot_hui);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制点
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.dot_hei);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.dot_hui);
            }
        }
    }

    private void setEmoji() {
        Init_viewPager();
        Init_Point();
        Init_Data();
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(circle_follow_edit_text.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示软键盘
     */
    private void showKeyboard() {
        InputMethodManager im = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        im.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    private class GridViewAdapter extends BaseAdapter {

        List<String> images;

        public GridViewAdapter(List<String> images) {
            this.images = images;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemViewholder holder1 = null;
            if (convertView == null) {
                holder1 = new ItemViewholder();
                convertView = LayoutInflater.from(CircleCommentNewActivity.this).inflate(R.layout.circle_follow_grid_img_item, null);
                holder1.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder1);
            } else {
                holder1 = (ItemViewholder) convertView.getTag();
            }
            String imgUrl = images.get(position);
            if (!TextUtils.isEmpty(imgUrl)) {
                // aQuery.id(holder1.img).image(AppRestClient.getImageUrl(imgUrl), true, true, 0, 0, null, AQuery.FADE_IN);
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, EntBaseRequest.getImageUrl(imgUrl), holder1.img, MyDisplayImageOption.getOptions(R.drawable.default_small));
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

    static class ItemViewholder {
        ImageView img;
    }
}