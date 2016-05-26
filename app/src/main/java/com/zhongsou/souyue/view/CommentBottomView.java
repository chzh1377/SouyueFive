package com.zhongsou.souyue.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.upyun.api.IUpYunConfig;
import com.upyun.api.Uploader;
import com.upyun.api.utils.UpYunException;
import com.upyun.api.utils.UpYunUtils;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.im.adapter.EmojiAdapter;
import com.zhongsou.souyue.im.adapter.EmojiPagerAdapter;
import com.zhongsou.souyue.im.emoji.Emoji;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.module.ExpressionBean;
import com.zhongsou.souyue.im.module.GifBean;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.net.detail.CommentDetailReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.AnoyomousUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zyw on 2015/11/5.
 * 底部的评论框
 */
public class CommentBottomView extends LinearLayout implements View.OnClickListener {

    public static final int MAX_PICTURE_NUM = 9; // 最大图片数

    /**
     * 公共变量
     */
    private Context mContext;
//    private Http mHttp; // http对象
    private CommentsForCircleAndNews mPublishPosts; //主贴内容
    private String mSign_info;
    private int mOperType; // 操作类型，搜悦客户端 = 3
    private String mSrpid;
    private String mSrpword;


    /** //布局区域，请参看
     * R.layout.circle_follow_layout
     */
    private Button circle_follow_send, circle_follow_add;
    private EditText circle_follow_edit_text;
    private ImageView circle_follow_add_emoj, circle_follow_add_key;
    private LinearLayout circle_follow_add_pic, circle_follow_take_photo;
    private LinearLayout ll_circle_follow_attach;
    private ProgressDialog progressDialog; // 发送进度框

    // 表情列表
    private ArrayList<View> mPageViews; // 表情GridView
    private ViewPager mViewPaper;
    private List<EmojiAdapter> emojiListForAdapter;
    private EmojiAdapter mEmojiAdapter;
    private List<List<Emoji>> mEmojis;
    private ArrayList<ImageView> mPointViews;
    private int mCurrent = 0;//表情当前页
    //表情布局
    private LinearLayout pagenumLayout;
    private LinearLayout faceLayout;

    // 图片
    private List<String> mBlogImageList;
    private List<String> mSmallImageList;
    private RelativeLayout layout_img;
    //改变图片的接口
    private OnChangeListener mListener;
    private OnChangeListener mListenerPhoto;
    private OnChangeListener mListenerAddImg;

    //图片布局
    private TextView tvImgNum;
    private TextView tv_img_count;
    private HorizontalScrollView horizontalScrollView;
    private GestureDetector mGestureDetector;
    private LayoutInflater mLayoutInflater;
    private LinearLayout allPic;
    private LinearLayout mainPic;
    private ImageView iv_add_pic;
    private boolean mHasComment = false;
    private boolean hasMeasured = false; // ScrollView图片是否绘制

    private int mDetailType; //详情类型
    //帖子信息
    private Posts mainPost;
    private String mMainTitle;
    private String mMainImages;
    private String mMainDecsription;
    private String mMainDate = "";
    private String mMainSource;
    private ExpressionView.OnExpressionListener mExpressionListener;

    /**
     * 匿名相关
     */
    public boolean mAnoyomous = false; //是否匿名
    private Bitmap mImageBitmap; // 头像
    private CheckBox circle_follow_anonymous; // 匿名按钮
    private View mDrapView;
    private boolean mIsLogin;

    public CommentBottomView(Context context) {
        super(context);
    }
    private IVolleyResponse mresponse;
    public CommentBottomView(Context context, IVolleyResponse response, String sign_info, int operType, String srpid, String srpword, Posts mainPosts) {
        this(context);
        AnoyomousUtils.setAnoyomousState(false,null); // 上来先清空新闻的匿名状态
        this.mresponse = response;
        this.mSign_info = sign_info;
        this.mOperType = operType;
        this.mSrpid = srpid;
        this.mSrpword = srpword;
        this.mainPost = mainPosts;

        this.mContext = context;
        LinearLayout mMainLayout = (LinearLayout) View.inflate(mContext, R.layout.circle_follow_layout, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        this.addView(mMainLayout, params);
        initContext();
        initData();
        initView();
        setEmoji();
        scrollToRight();
    }

    /**
     * 初始化上下文
     */
    private void initContext() {
        mHasComment = false;
        mPublishPosts = new CommentsForCircleAndNews();
        mGestureDetector = new GestureDetector(mContext, new DrapGestureListener());
        mExpressionListener = new ExpressionView.OnExpressionListener() {
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
                    SpannableString spannableString = EmojiPattern.getInstace()
                            .addFace(mContext, emoji.getId(), emoji.getCharacter());
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
    }

    /**
     * 设置匿名状态,东华
     */
    private void setAnymous(){
        ValueAnimator ani = ValueAnimator.ofFloat(0,180);
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private boolean changeRes = false;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                circle_follow_anonymous.setRotationY(fraction * 180);
                if(fraction > 0.5f && !changeRes){
                    if(mAnoyomous){
                        circle_follow_anonymous.setBackgroundResource(R.drawable.common_comment_anonymous);
                    }else{
                        circle_follow_anonymous.setBackgroundDrawable(new BitmapDrawable(mImageBitmap));
                    }
                    changeRes = true;
                }
                if(fraction > 0.9){
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
//            case R.id.circle_follow_switching_btn:
//                break;
            case R.id.circle_follow_anonymous:
                if (!mIsLogin) {
                    SouYueToast.makeText(mContext, mContext.getString(R.string.guest_cant_hide), Toast.LENGTH_SHORT).show();
                    return;
                }
                mAnoyomous = !mAnoyomous;
                setAnymous();
                AnoyomousUtils.setAnoyomousState(mAnoyomous,null);
                break;
            case R.id.circle_follow_add:
//                circle_follow_add.setVisibility(View.GONE);
//                circle_follow_send.setVisibility(View.VISIBLE);
//                circle_follow_send.setBackgroundResource(R.drawable.circle_follow_btn_send_down);
//                circle_follow_send.setTextColor(Color.WHITE);
                ll_circle_follow_attach.setVisibility(View.VISIBLE);
                faceLayout.setVisibility(View.GONE);
                layout_img.setVisibility(View.GONE);
                //隐藏键盘
                hideKeyboard();
                break;
            case R.id.circle_follow_send:
                if (!CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance())) {
                    Toast.makeText(mContext, "请检查网络", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mBlogImageList.size() == 0 && circle_follow_edit_text.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "请输入内容", Toast.LENGTH_LONG).show();
                    return;
                }

                if (mBlogImageList.size() == 0) {
                    savePostsInfo();
                    showProcessDialog();
                } else {
                    mSmallImageList = getSmallImageList();
                    if (mSmallImageList.size() != mBlogImageList.size()) {
                        Toast.makeText(mContext, "图片加载中，请稍后发送", Toast.LENGTH_LONG).show();
                        return;
                    }
                    UploadTask uploadTask = new UploadTask();
                    uploadTask.execute(mSmallImageList);
                    showProcessDialog();
                }
                saveInfo(mSign_info);
                break;
            case R.id.circle_follow_edit_text: // 内容输入框
                ll_circle_follow_attach.setVisibility(View.GONE);
                faceLayout.setVisibility(View.GONE);
                layout_img.setVisibility(View.GONE);
                circle_follow_add_key.setVisibility(View.GONE);
                circle_follow_add_emoj.setVisibility(View.VISIBLE);
                break;
            case R.id.circle_follow_add_pic: // 第一层布局，图片按钮
                if (mListener != null) {
                    mListener.onChange(null);
                }
                ll_circle_follow_attach.setVisibility(View.GONE);
                faceLayout.setVisibility(View.GONE);
                layout_img.setVisibility(View.VISIBLE);
                break;
            case R.id.circle_follow_take_photo:// 第一层布局，拍照按钮
                ll_circle_follow_attach.setVisibility(View.GONE);
                faceLayout.setVisibility(View.GONE);
                layout_img.setVisibility(View.VISIBLE);
                if (mBlogImageList.size() >= MAX_PICTURE_NUM) {
                    Toast.makeText(mContext, "最多选择9张图片", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mListenerPhoto != null) {
                    mListenerPhoto.onChange(null);
                }
                break;
            case R.id.circle_follow_add_emoj: // 第一层布局，表情按钮
//                circle_follow_add.setVisibility(View.GONE);
//                circle_follow_send.setVisibility(View.VISIBLE);
//                circle_follow_send.setBackgroundResource(R.drawable.circle_follow_btn_send_down);
//                circle_follow_send.setTextColor(Color.WHITE);
                ll_circle_follow_attach.setVisibility(View.GONE);
                faceLayout.setVisibility(View.VISIBLE);
                layout_img.setVisibility(View.GONE);
                circle_follow_add_key.setVisibility(View.VISIBLE);
                circle_follow_add_emoj.setVisibility(View.GONE);
                hideKeyboard();
                break;
            case R.id.circle_follow_add_key:  //键盘按钮的点击事件
                circle_follow_edit_text.requestFocus();
                showKeyboard();
                circle_follow_add_emoj.setVisibility(View.VISIBLE);
                circle_follow_add_key.setVisibility(View.GONE);
                ll_circle_follow_attach.setVisibility(View.GONE);
                faceLayout.setVisibility(View.GONE);
                layout_img.setVisibility(View.GONE);
                break;
            case R.id.imgBtn_bolg_image: // 点击图片，查看大图
                for (int i = 0, j = mainPic.getChildCount(); i < j; i++) {
                    if (mainPic.getChildAt(i) == v.getParent()) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, TouchGalleryActivity.class);
                        TouchGallerySerializable tg = new TouchGallerySerializable();
                        tg.setItems(mBlogImageList);
                        tg.setClickIndex(i);
                        Bundle extras = new Bundle();
                        extras.putSerializable("touchGalleryItems", tg);
                        intent.putExtras(extras);
                        mContext.startActivity(intent);
                        break;
                    }
                }
                break;
            case R.id.cicle_follow_delete_img: // 删除图片
                for (int i = 0, j = mainPic.getChildCount(); i < j; i++) {
                    if (mainPic.getChildAt(i) == v.getParent()) {
                        mainPic.removeViewAt(i);
                        mBlogImageList.remove(i);
//                        mSmallImageList.remove(i);

                        toggleAddPicImageView();
                        setSendState();

                        if (mBlogImageList.size() > 0) {
                            tv_img_count.setVisibility(View.VISIBLE);
                            tv_img_count.setText(mBlogImageList.size() + "");
                        } else {
                            tv_img_count.setVisibility(View.GONE);
                        }

                        break;
                    }
                }
                break;
            case R.id.iv_add_pic: //点击“+”添加图片
                if (mListenerAddImg != null) {
                    mListenerAddImg.onChange(null);
                }
                break;
        }
    }

    /**
     * 保存帖子信息
     */
    private void savePostsInfo() {
        List<String> s_uploadedImgUrls = new ArrayList<String>();
        if (mSmallImageList != null) {
            for (String imgUrl : mSmallImageList) {
                if (imgUrl.contains("upaiyun.com")) {
                    s_uploadedImgUrls.add(imgUrl);
                } else {
                    s_uploadedImgUrls.add(imgUrl);
                }
            }
        }
//        String s_images = JSON.toJSONString(s_uploadedImgUrls);
        String s_images = new Gson().toJson(s_uploadedImgUrls);

        long blog_id = 0;
        long user_id = Long.valueOf(SYUserManager.getInstance().getUserId());
        mPublishPosts.setUser_id(user_id);
        mPublishPosts.setBlog_id(blog_id);
        mPublishPosts.setCreate_time(System.currentTimeMillis() + "");
        mPublishPosts.setImages(s_uploadedImgUrls);
        mPublishPosts.setContent(getStrWithNoSpaceAtLast(circle_follow_edit_text.getText().toString()));
        if (!mHasComment) {
            mHasComment = true;
            CommentDetailReq req = new CommentDetailReq(HttpCommon.DETAIL_COMMENTDETAIL_ID,mresponse);
            req.setParams(SYUserManager.getInstance().getToken(), mSign_info,
                    circle_follow_edit_text.getText().toString(), "", s_images, 0,
                    mOperType, mSrpid, mSrpword, mDetailType + 1, mMainTitle, mMainImages,
                    mMainDecsription, mMainDate, mMainSource, mAnoyomous);
            CMainHttp.getInstance().doRequest(req);
//            mHttp.commentDetail(SYUserManager.getInstance().getToken(), mSign_info, circle_follow_edit_text.getText().toString(), "", s_images, 0, mOperType, mSrpid, mSrpword, mDetailType + 1, mMainTitle, mMainImages, mMainDecsription, mMainDate, mMainSource, mAnoyomous);
        }
    }

    private void initData() {
        mEmojis = EmojiPattern.getInstace().emojiLists;
        mBlogImageList = new ArrayList<String>();
        mSmallImageList = new ArrayList<String>();

    }

    private void initView() {
        circle_follow_anonymous = (CheckBox) findViewById(R.id.circle_follow_anonymous);
        circle_follow_anonymous.setOnClickListener(this);
        layout_img = (RelativeLayout) findViewById(R.id.layout_img);
        faceLayout = (LinearLayout) findViewById(R.id.layout_face);
        ll_circle_follow_attach = (LinearLayout) findViewById(R.id.ll_circle_follow_attach);
//		circle_follow_switching_btn = (ImageButton) findViewById(R.id.circle_follow_switching_btn);
        circle_follow_add = (Button) findViewById(R.id.circle_follow_add);
        circle_follow_send = (Button) findViewById(R.id.circle_follow_send);
        circle_follow_send.setVisibility(View.GONE);
        circle_follow_edit_text = (EditText) findViewById(R.id.circle_follow_edit_text);
        circle_follow_edit_text.requestFocus();
        circle_follow_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    circle_follow_send.setVisibility(View.VISIBLE);
                    circle_follow_add.setVisibility(View.GONE);
                } else {
                    circle_follow_send.setVisibility(View.GONE);
                    circle_follow_add.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.toString().length();
                if (len > 0 || getImgLen() > 0) {
                    circle_follow_send.setBackgroundResource(R.drawable.circle_follow_btn_send_down);
                    circle_follow_send.setTextColor(Color.WHITE);
                } else {
                    circle_follow_send.setBackgroundResource(R.drawable.circle_follow_btn_send);
                    circle_follow_send.setTextColor(Color.BLACK);
                }
            }
        });
        circle_follow_edit_text.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onClick(v);
                }
            }
        });
        circle_follow_add_emoj = (ImageView) findViewById(R.id.circle_follow_add_emoj);
        circle_follow_add_key = (ImageView) findViewById(R.id.circle_follow_add_key);
        circle_follow_add_pic = (LinearLayout) findViewById(R.id.circle_follow_add_pic);
        circle_follow_take_photo = (LinearLayout) findViewById(R.id.circle_follow_take_photo);
        mViewPaper = (ViewPager) findViewById(R.id.viewpager);
        pagenumLayout = (LinearLayout) findViewById(R.id.layout_pagenum);
        tvImgNum = (TextView) findViewById(R.id.tv_img_num);
        tv_img_count = (TextView) findViewById(R.id.tv_img_count);

//		circle_follow_switching_btn.setOnClickListener(this);
        circle_follow_add.setOnClickListener(this);
        circle_follow_send.setOnClickListener(this);
        circle_follow_add_emoj.setOnClickListener(this);
        circle_follow_add_key.setOnClickListener(this);
        circle_follow_add_pic.setOnClickListener(this);
        circle_follow_take_photo.setOnClickListener(this);
        circle_follow_edit_text.setOnClickListener(this);

        // ScrollView控件相关
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.bolg_gallery);
        allPic = (LinearLayout) findViewById(R.id.ll_all_pic);
        mainPic = (LinearLayout) findViewById(R.id.ll_main_pic);
        mLayoutInflater = LayoutInflater.from(mContext);
        iv_add_pic = (ImageView) findViewById(R.id.iv_add_pic);
        iv_add_pic.setOnClickListener(this);
    }


    /**
     *重设匿名状态
     */
    public void setReview() {
        Log.e("comment", "取出匿名状态");
        mIsLogin = SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_ADMIN);
        mAnoyomous = AnoyomousUtils.getAnoyomouState(null);
        if (mIsLogin) {
            mImageBitmap = AnoyomousUtils.getBitmapForCurrentUser(mContext,null);
            if (!mAnoyomous) {
                circle_follow_anonymous.setBackgroundDrawable(new BitmapDrawable(mImageBitmap));
                return;
            }
            circle_follow_anonymous.setBackgroundResource(R.drawable.common_comment_anonymous);
            return ;
        }
        circle_follow_anonymous.setBackgroundResource(R.drawable.default_head);
    }


    /**
     * 添加图片，外部调用方法
     * @param picPaths
     */
    public void addImagePath(List<String> picPaths) {
        if (picPaths == null || picPaths.size() == 0) {
            Toast.makeText(mContext, R.string.self_get_image_error, Toast.LENGTH_SHORT).show();
            return;
        }

        hasMeasured = false;
        for (String picPath : picPaths) {
            if (StringUtils.isEmpty(picPath)) {
                continue;
            }
            mBlogImageList.add(picPath);
        }

        showPicture();
    }

    @SuppressLint("NewApi")
    private OnDragListener mOnDragListener = new OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    mDrapView.setAlpha(0F);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setAlpha(0.5F);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(1F);
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    int starPos = 0, endPos = 0;
                    for (int i = 0, j = mainPic.getChildCount(); i < j; i++) {

                        if (mainPic.getChildAt(i) == v.getParent()) {
                            endPos = i;
                            for (int x = 0, y = mainPic.getChildCount(); x < y; x++) {
                                if (mainPic.getChildAt(x) == view) {
                                    starPos = x;
                                    Log.e("ACTION_DROP", "starPos开始位置是:" + starPos);
                                }
                            }
                            Log.e("ACTION_DROP", "endPos目标位置是:" + endPos);
                            mainPic.removeView(view);
                            mainPic.addView(view, endPos);

                            String imageUri = mBlogImageList.get(starPos);
                            mBlogImageList.remove(starPos);
                            mBlogImageList.add(endPos, imageUri);

//                        String oriImageUri = mSmallImageList.get(starPos);
//                        mSmallImageList.remove(starPos);
//                        mSmallImageList.add(endPos,oriImageUri);

                            break;
                        }
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1F);
                    mDrapView.setAlpha(1F);
                default:
                    break;
            }
            return true;
        }
    };

    /**
     * 显示评论图片
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showPicture() {
        if (mBlogImageList != null && mBlogImageList.size() > 0) {
            //有图，显示发送按钮
            circle_follow_add.setVisibility(View.GONE);
            circle_follow_send.setVisibility(View.VISIBLE);
            circle_follow_send.setBackgroundResource(R.drawable.circle_follow_btn_send_down);
            circle_follow_send.setTextColor(Color.WHITE);
        }

        mainPic.removeAllViews();
        for (int i = 0; i < mBlogImageList.size(); i++) {
            View itemView = mLayoutInflater.inflate(
                    R.layout.circle_follow_image_item, null);
            ImageView imageView = (ImageView) itemView
                    .findViewById(R.id.imgBtn_bolg_image);
            imageView.setOnTouchListener(mOnTouchListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // 高版本 api的方法,低版本不注册拖动事件
                imageView.setOnDragListener(mOnDragListener);
            }
            imageView.setOnClickListener(this);
            ImageView delView = (ImageView) itemView
                    .findViewById(R.id.cicle_follow_delete_img);
            delView.setOnClickListener(this);
            new BitmapWorkerTask(imageView).execute(mBlogImageList.get(i));
            mainPic.addView(itemView);
        }

        toggleAddPicImageView();
        setSendState();
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mDrapView = (View) v.getParent();
            if (mGestureDetector.onTouchEvent(event))
                return true;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }

            return false;
        }
    };

    /**
     * 滑动逻辑
     */
    private void scrollToRight() {
        ViewTreeObserver vto = horizontalScrollView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (!hasMeasured) {
                    hasMeasured = true;
                    int width = allPic.getMeasuredWidth();
                    DisplayMetrics dm = new DisplayMetrics();
                    ((Activity) mContext).getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int windowWidth = dm.widthPixels;
                    int addPicWidth = iv_add_pic.getMeasuredWidth();
                    horizontalScrollView.scrollTo(width - windowWidth + addPicWidth, 0);
                }
                return true;
            }
        });
    }

    private DialogInterface.OnCancelListener mOnCancelListener;

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mOnCancelListener.onCancel(null);
    }

    private DialogInterface.OnDismissListener mOnDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class DrapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ClipData data = ClipData.newPlainText("", "");
                MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(mDrapView);
                mDrapView.startDrag(data, shadowBuilder, mDrapView, 0);
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class MyDragShadowBuilder extends View.DragShadowBuilder {

        private final WeakReference<View> mView;

        public MyDragShadowBuilder(View view) {
            super(view);
            mView = new WeakReference<View>(view);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            canvas.scale(1.2F, 1.2F);
            super.onDrawShadow(canvas);
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {

            final View view = mView.get();
            if (view != null) {
                shadowSize.set((int) (view.getWidth() * 1.2F), (int) (view.getHeight() * 1.2F));
                shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
            } else {
            }
        }
    }

    /**
     * 进度菊花
     */
    public void showProcessDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("正在发送...");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dismiss();
                }
            });
            progressDialog.show();
        } else {
            progressDialog.show();
        }
    }

    public void dismissProcessDialog() {
        progressDialog.dismiss();
    }

    /**
     * 控制显示与否
     */
    public void dismiss() {
        initContext();
        initData();
        hideKeyboard();
        mOnDismissListener.onDismiss(null);
        mOnCancelListener.onCancel(null);
        layout_img.setVisibility(View.GONE);
        faceLayout.setVisibility(View.GONE);
        showPicture();
    }

    /**
     * 设置显示
     */
    public void showDialog() {
        setReview();
    }


    /**
     * 生成缩略图
     * @param filePath
     * @return
     */
    private Bitmap extractThumbNail(String filePath) {
        int width = MainApplication.getInstance().getResources().getDisplayMetrics().widthPixels;
        Bitmap bm = ImageUtil.getSmallBitmap(filePath, width * width);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bm != null) {
            bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] b = baos.toByteArray();
            ByteArrayInputStream bas = new ByteArrayInputStream(b);
            bm.recycle();
            return BitmapFactory.decodeStream(bas);
        } else {
            return null;
        }
    }

    public CommentsForCircleAndNews getPublishPosts() {
        return mPublishPosts;
    }

    public void setListener(OnChangeListener listener) {
        this.mListener = listener;
    }

    public void setPhotoListener(OnChangeListener listener) {
        this.mListenerPhoto = listener;
    }

    public void setAddImgListener(OnChangeListener listener) {
        this.mListenerAddImg = listener;
    }

    public int getImgLen() {
        return mBlogImageList.size();
    }

    /**
     * 生成缩略图的任务
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";
        private String smallImagePath = "";

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            smallImagePath = new File(ImageUtil.getSelfDir(), System.currentTimeMillis() + "blog_image").getAbsolutePath();
            Bitmap bitmap = extractThumbNail(data);
            // 错误图片处理
            if (bitmap != null) {
                ImageUtil.saveBitmapToFile(bitmap, smallImagePath);
            } else {
                smallImagePath = data; //小图地址等于原图地址
            }
            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                    imageView.setTag(smallImagePath);
                }
            }
        }
    }

    public void setEditText(String str) {
        circle_follow_edit_text.setText(EmojiPattern.getInstace().getExpressionString(mContext, str));
    }

    public Posts getMainPost() {
        return mainPost;
    }

    //对话框消失时保留文字表情和图片
    public void saveInfo(String key) {
        SYSharedPreferences.getInstance().putString(key + "_text", circle_follow_edit_text.getText().toString());
//        String str = JSON.toJSONString(mBlogImageList);
        String str = new Gson().toJson(mBlogImageList);
        SYSharedPreferences.getInstance().putString(key + "_img", str);
    }

    //过滤跟帖最后的空格和换行
    String mStrSpace = " ";
    String mStrEnter = "\n";

    private String getStrWithNoSpaceAtLast(String strSrc) {
        if (strSrc.endsWith(mStrSpace)) {
            strSrc = strSrc.substring(0, strSrc.lastIndexOf(mStrSpace));
        }

        if (strSrc.endsWith(mStrEnter)) {
            strSrc = strSrc.substring(0, strSrc.lastIndexOf(mStrEnter));
        }

        if (!strSrc.endsWith(mStrSpace) && !strSrc.endsWith(mStrEnter)) {
            return strSrc;
        }
        return getStrWithNoSpaceAtLast(strSrc);
    }

    /**
     * 选图的页面逻辑控制
     */
    private void toggleAddPicImageView() {
        int size = mBlogImageList.size();

        iv_add_pic.setVisibility(size == MAX_PICTURE_NUM ? View.GONE : View.VISIBLE);
        tvImgNum.setText("已选" + size + "张，还可以选" + (MAX_PICTURE_NUM - size) + "张");

        tv_img_count.setText(size + "");
        tv_img_count.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置评论状态
     */
    private void setSendState() {
        int len = circle_follow_edit_text.getText().toString().length();
        if (len > 0 || mBlogImageList.size() > 0) {
            circle_follow_send.setBackgroundResource(R.drawable.circle_follow_btn_send_down);
            circle_follow_send.setTextColor(Color.WHITE);
        } else {
            circle_follow_send.setBackgroundResource(R.drawable.circle_follow_btn_send);
            circle_follow_send.setTextColor(Color.BLACK);
        }
    }

    /**
     * 获取小图
     * @return
     */
    private List getSmallImageList() {
        List<String> smallImageList = new ArrayList<String>();
        int count = mainPic.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView imageView = (ImageView) mainPic.getChildAt(i).findViewById(R.id.imgBtn_bolg_image);
            if (imageView.getTag() != null) {
                smallImageList.add((String) imageView.getTag());
            }
        }
        return smallImageList;
    }


    private void setEmoji() {
        initEmojiViewPager();
        initEmojiPoint();
        initEmojiData();
    }

    /**
     * 初始化显示表情的viewpager
     */
    private void initEmojiViewPager() {
        mPageViews = new ArrayList<View>();
        View nullView1 = new View(mContext);

        nullView1.setBackgroundColor(Color.TRANSPARENT);
        mPageViews.add(nullView1);

        emojiListForAdapter = new ArrayList<EmojiAdapter>();
        for (int i = 0; i < mEmojis.size(); i++) {
            GridView view = new GridView(mContext);
            mEmojiAdapter = new EmojiAdapter(mContext, mExpressionListener, mEmojis.get(i));
            view.setAdapter(mEmojiAdapter);
            emojiListForAdapter.add(mEmojiAdapter);
//			view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            mPageViews.add(view);
        }

        View nullView2 = new View(mContext);
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        mPageViews.add(nullView2);
    }

    /**
     * 初始化页点
     */
    private void initEmojiPoint() {
        mPointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < mPageViews.size(); i++) {
            imageView = new ImageView(mContext);
            imageView.setBackgroundResource(R.drawable.dot_hui);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            pagenumLayout.addView(imageView, layoutParams);
            if (i == 0 || i == mPageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.dot_hei);
            }
            mPointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void initEmojiData() {
        mViewPaper.setAdapter(new EmojiPagerAdapter(mPageViews));

        mViewPaper.setCurrentItem(1);
        mCurrent = 0;
        mViewPaper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                mCurrent = arg0 - 1;
                draw_Point(arg0);
                if (arg0 == mPointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        mViewPaper.setCurrentItem(arg0 + 1);// 第二屏，会再次实现该回调方法实现跳转.
                        mPointViews.get(1).setBackgroundResource(R.drawable.dot_hei);
                    } else {
                        mViewPaper.setCurrentItem(arg0 - 1);// 倒数第二屏
                        mPointViews.get(arg0 - 1).setBackgroundResource(R.drawable.dot_hui);
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
        for (int i = 1; i < mPointViews.size(); i++) {
            if (index == i) {
                mPointViews.get(i).setBackgroundResource(R.drawable.dot_hei);
            } else {
                mPointViews.get(i).setBackgroundResource(R.drawable.dot_hui);
            }
        }
    }


    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(circle_follow_edit_text.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示软键盘
     */
    public void showKeyboard() {
        InputMethodManager im = ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE));
        im.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        layout_img.setVisibility(View.GONE);
        faceLayout.setVisibility(View.GONE);
    }

    /**
     * 上传任务
     */
    class UploadTask extends ZSAsyncTask<List<String>, Void, Boolean> {
        UploadToYun mUty;
        private List<String> mImages = null;

        @Override
        protected void onPreExecute() {
            mUty = new UploadToYun();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(List<String>... params) {
            mImages = params[0];
            if (mImages != null) {
                uploadPics(mUty);// 如果有图片，先将图片上传up云，并拿到图片地址
                return send();// 一切准备就绪 发送原创到中搜服务器
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                savePostsInfo();
            } else {
                progressDialog.dismiss();
                UIHelper.ToastMessage(mContext, "图片上传失败，请重试！");
            }
            super.onPostExecute(result);
        }

        /**
         * 上传图片到up云
         *
         * @param uty
         */
        private void uploadPics(UploadToYun uty) {
            for (int i = 0; i < mImages.size(); i++) {
                String dir = mImages.get(i);
                File f = new File(dir);
                if (null == f || !f.canRead()) {
                    continue;
                }
                String url = null;
                if (!(dir.toLowerCase().contains("http:")))
                    url = uty.upload(f);
                if (!StringUtils.isEmpty(url)) {
                    url = IUpYunConfig.HOST_IMAGE + url + "!android";
                    mImages.set(i, url);
                } else
                    break;
            }
        }

        /**
         * 如果图片上传失败1张，就算全部失败
         */
        private boolean send() {
            if (mImages != null) {
                boolean b = true;
                for (String str : mImages) {
                    if (!str.toLowerCase().contains("http:")) {
                        b = false;
                        break;
                    }
                }
                return b;
            }
            return false;
        }
    }

    /**
     * upyun设置
     */
    class UploadToYun implements IUpYunConfig {
        @Override
        public String getSaveKey() {
            StringBuffer bucket = new StringBuffer(SYUserManager.getInstance().getUserId() + "");
            while (bucket.length() < 8) {
                bucket.insert(0, '0');
            }
            return bucket.insert(4, '/').insert(0, "/user/").append(randomTo4()).append(".jpg").toString();
        }

        public String upload(File file) {
            try {
                String policy = UpYunUtils.makePolicy(getSaveKey(), Uploader.getExpiration(), BUCKET_IMAGE);
                String signature = UpYunUtils.signature(policy + "&" + API_IMAGE_KEY);
                return Uploader.upload(policy, signature, UPDATE_HOST + BUCKET_IMAGE, file);
            } catch (UpYunException e) {
            }
            return null;
        }
    }

    /**
     * 生成随机四位数
     * @return
     */
    private String randomTo4() {
        String s = "";
        int intCount = 0;
        intCount = (new Random()).nextInt(9999);//
        if (intCount < 1000)
            intCount += 1000;
        s = intCount + "";
        return s;
    }

    public void setDetailType(int detailType) {
        this.mDetailType = detailType;
    }

    public void setMain_date(String main_date) {
        this.mMainDate = main_date;
    }

    public void setMain_decsription(String main_decsription) {
        this.mMainDecsription = main_decsription;
    }

    public void setMain_images(String main_images) {
        this.mMainImages = main_images;
    }

    public void setMain_source(String main_source) {
        this.mMainSource = main_source;
    }

    public void setMain_title(String main_title) {
        this.mMainTitle = main_title;
    }

}
