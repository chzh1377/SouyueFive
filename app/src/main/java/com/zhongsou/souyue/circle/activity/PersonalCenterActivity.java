package com.zhongsou.souyue.circle.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.upyun.api.UploadImageTask;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.adapter.CheeseDynamicAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.PersonalCenterListAdapter;
import com.zhongsou.souyue.circle.model.PCPost;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.model.PersonalCenterInfo;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.view.LinearLayoutForListView;
import com.zhongsou.souyue.circle.view.OverScrollView;
import com.zhongsou.souyue.circle.view.PersonalCenterImAlertMenu;
import com.zhongsou.souyue.im.ac.ImModifyNoteName;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMApi;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.InterestBean;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.SubscribeItem;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserBlogListReq;
import com.zhongsou.souyue.net.personal.UserCenterInfoReq;
import com.zhongsou.souyue.net.personal.UserRepairInfo;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.dynamicgrid.DynamicGridView;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.CollectionUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ThreadPoolUtil;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

//import com.androidquery.AQuery;
//import com.androidquery.callback.AjaxStatus;
//import com.zhongsou.souyue.net.Http;
//import com.zhongsou.souyue.net.volley.CMainHttp;

/**
 * Created by bob zhou on 14-11-3.
 * <p/>
 * 个人中心
 */
public class PersonalCenterActivity extends BaseActivity {

    /**
     * 页面主view start
     */
    private ProgressBarHelper progress;

    private LinearLayoutForListView listView;

    private OverScrollView overScrollView;

    private ImageView refreshImg;       //旋转图片

    private ImageButton refreshBg;      //刷新背景

    private PersonalCenterListAdapter adapter;              //列子列表的adapter

    private RotateAnimation clockwiseAnimation;             //顺时针旋转

    private float mRotationPivotX, mRotationPivotY;

    private Matrix mHeaderImageMatrix;

    private TextView nickNameTv;        //昵称

    private TextView expressionTv;      //心情文字

    private LinearLayout editExpressionLayout;

    private LinearLayout expressionLayout; //心情图片+个人签名

    private ImageView expressionIv;     //表情图片

    /**
     * IM相关View start
     */
    private ImageButton menuBtn;

    private ImageView headBgImg;    //个人背景图

    private ImageView avatarImg;   //头像

    private RelativeLayout chartLayout;

    private ImageView chartImg;

    private LinearLayout remarkNameLayout;

    private TextView remarkNameTv;

    private LinearLayout groupNameLayout;

    private TextView groupNameTv;

    private PersonalCenterImAlertMenu popupMenu;       //IM好友时，弹出的window
    /**
     * IM相关View end
     */

    private LinearLayout interestLayout;

    private TextView interestTv;

    private TextView interestNumTv;

    private LinearLayout srpLayout;

    private TextView srpTv;

    private TextView srpNumTv;

    private TextView postTv;

    private TextView postNumTv;

    private LinearLayout interestMoreLayout;

    private TextView interestMoreTv;

    private LinearLayout srpMoreLayout;

    private TextView srpMoreTv;

    private LinearLayout postMoreLayout;

    private TextView postMoreTv;

    private DynamicGridView interestGridView;

    private DynamicGridView srpGridView;

    private View dividerForSrp;

    private View dividerForInterest;

    private CheeseDynamicAdapter srpItemAdapter;

    private CheeseDynamicAdapter interestItemAdapter;

    private RelativeLayout loadingLayout;

    private View footerProgressBar;

    private TextView footerRefreshTv;

    private RelativeLayout mTitleBarRelativeLayout;

    private TextView mTitleBarName;

    private RelativeLayout mRefreshLayout;

    private ImageButton mGoBack;
    
    private ImageButton mPersonalMenu;
    
    /** 页面主view end*/

    /**
     * 页面主体参数 start
     */
//    private Http http;

    private int picType;                //上传图片时，类型，1：头像  2：背景图

    private PersonPageParam param;      //初始页面参数

    private User user;                  //当前用户

    private int imStatus;               //im状态

    boolean isSelf;

    private String new_srpid;

    private int pno = 1;

    static final int psize = 10;

    private long last_id;

    private boolean isLoading;

    private boolean isLoadAll;

    private boolean canLoadMore = true;

    private boolean isPrivate;

//    private AQuery aq;

    private int from;

    private boolean isHeadViewOk;

    private boolean isPostOk;

    private boolean is_interest_none;

    private boolean is_srp_none;

    private boolean is_post_none;

    private boolean is_first = true;

    private boolean is_showToast;

    private boolean is_forceRefresh;

    private boolean is_needRefresh;
    /** 页面主体参数 end*/


    /**
     * 图片(头像和背景图)上传引入的参数 start
     */
    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";// temp

    private File profileImgFile;        // 通过uid，构建头像的本地存储路径

    private ProgressDialog progdialog;  //传照片的ProgressDialog

    private String bgUrl;               //上传图片用的

    private Drawable drawable;      //

    private PersonalCenterInfo personalCenterInfo;
    private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);// The Uri to store
    /**
     * 图片(头像和背景图)上传引入的参数 end
     */

    public static final int IM_STATUS_SELF = 1;
    public static final int IM_STATUS_FRIEND = 2;
    public static final int IM_STATUS_ADD = 3;

    public static final int SEX_BOY = 0;
    public static final int SEX_GIRL = 1;
    public static final String TAG = "PersonalCenterActivity";
    private Drawable HeadBgDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalcenter);
        findViews();
        bindListener();
        initRotateAnimation();
        initData();
        initUpload();
    }

    private void findViews() {
        progress = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
        overScrollView = (OverScrollView) findViewById(R.id.overscrollview);
        listView = (LinearLayoutForListView) findViewById(R.id.percenter_listView);
        adapter = new PersonalCenterListAdapter(this);
        listView.setAdapter(adapter);
        loadingLayout = (RelativeLayout) findViewById(R.id.ent_footer_loading);
        footerProgressBar = loadingLayout.findViewById(R.id.pull_to_refresh_progress);
        footerRefreshTv = (TextView) loadingLayout.findViewById(R.id.pull_to_refresh_text);

        refreshImg = (ImageView) findViewById(R.id.percenter_refreshImg);
        refreshBg = (ImageButton) findViewById(R.id.percenter_refreshbg);
        menuBtn = (ImageButton) findViewById(R.id.percenter_menuBtn);
        chartLayout = (RelativeLayout) findViewById(R.id.chart_layout);
        chartImg = (ImageView) findViewById(R.id.chart_img);
        headBgImg = (ImageView) findViewById(R.id.center_backImg);
        nickNameTv = (TextView) findViewById(R.id.percenter_nickname);
        remarkNameLayout = (LinearLayout) findViewById(R.id.percenter_remarkname_layout);
        remarkNameTv = (TextView) findViewById(R.id.percenter_remarkname_tv);
        groupNameLayout = (LinearLayout) findViewById(R.id.percenter_groupname_layout);
        groupNameTv = (TextView) findViewById(R.id.percenter_groupname_tv);
        avatarImg = (ImageView) findViewById(R.id.percenter_avatar);
        expressionTv = (TextView) findViewById(R.id.percenter_expression_tv);
        expressionIv = (ImageView) findViewById(R.id.percenter_expression_iv);
        editExpressionLayout = (LinearLayout) findViewById(R.id.percenter_edit_layout);
        expressionLayout = (LinearLayout) findViewById(R.id.expression_layout);

        interestTv = (TextView) findViewById(R.id.percenter_interest_tv);
        interestNumTv = (TextView) findViewById(R.id.percenter_interest_num_tv);
        srpTv = (TextView) findViewById(R.id.percenter_srp_tv);
        srpNumTv = (TextView) findViewById(R.id.percenter_srp_num_tv);
        postTv = (TextView) findViewById(R.id.percenter_post_tv);
        postNumTv = (TextView) findViewById(R.id.percenter_post_num_tv);
        interestMoreLayout = (LinearLayout) findViewById(R.id.percenter_interest_more_layout);
        interestMoreTv = (TextView) findViewById(R.id.percenter_interest_more_tv);
        srpMoreLayout = (LinearLayout) findViewById(R.id.percenter_srp_more_layout);
        srpMoreTv = (TextView) findViewById(R.id.percenter_srp_more_tv);
        postMoreLayout = (LinearLayout) findViewById(R.id.percenter_post_more_layout);
        postMoreTv = (TextView) findViewById(R.id.percenter_post_more_tv);
        interestLayout = (LinearLayout) findViewById(R.id.xqq_layout);
        srpLayout = (LinearLayout) findViewById(R.id.srp_layout);
        interestGridView = (DynamicGridView) findViewById(R.id.interest_gridview);
        srpGridView = (DynamicGridView) findViewById(R.id.srp_gridview);
        dividerForInterest = findViewById(R.id.view1);
        dividerForSrp = findView(R.id.view2);

        mTitleBarRelativeLayout = findView(R.id.layout_titlebar);
        mTitleBarName = findView(R.id.activity_bar_title);
        mTitleBarName.setText(R.string.my_info_home);
        mRefreshLayout = findView(R.id.rl_refresh_layout);
        mGoBack = findView(R.id.percenter_backBtn);
        mPersonalMenu = findView(R.id.ib_personal_menu);
        
        interestItemAdapter = new CheeseDynamicAdapter(this, new ArrayList<Object>(), 3);
        srpItemAdapter = new CheeseDynamicAdapter(this, new ArrayList<Object>(), 3);
        interestGridView.setAdapter(interestItemAdapter);
        srpGridView.setAdapter(srpItemAdapter);
    }


    private void bindListener() {
        overScrollView.setOnScrollChangedListener(new OverScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {
                if (progress != null && progress.isLoading) {//加载数据，不执行滑动事件
                    return;
                }
                if (oldy < 1500 && y < 1500) {     //oldy > y,下拉 oldy < y回弹
                    Log.d(TAG, "deltaY=" + (oldy - 1500));
                    prepareToRotate(true);
                    float scale = Math.abs(oldy - 1500) / (float) 160;
                    onPullImpl(scale * (-1));
                } else {
                    prepareToRotate(false);
                }
                
                //标准导航栏显示控制
                if (y != 1500) {
                    int headImgHeight = headBgImg.getHeight();
                    int scrollInChildTop = overScrollView.getChildAt(0).getTop();
                    int visibleGap = mTitleBarRelativeLayout.getHeight();
                    int currentY = y - 1500 - scrollInChildTop;

                    if (currentY > headImgHeight - visibleGap) {
                        //先获取到再显示，如果获取不到的话就不显示
                        if (!TextUtils.isEmpty(nickNameTv.getText())) {
                            mRefreshLayout.setVisibility(View.GONE);
                            mGoBack.setVisibility(View.GONE);
                            mTitleBarRelativeLayout.setVisibility(View.VISIBLE);
                            if (!isSelf){
                                mTitleBarName.setText(String.format(getString(R.string.others_info_home), nickNameTv.getText()));
                            }
                            if (imStatus == IM_STATUS_FRIEND) {
                            	mPersonalMenu.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        mRefreshLayout.setVisibility(View.VISIBLE);
                        mGoBack.setVisibility(View.VISIBLE);
                        mTitleBarRelativeLayout.setVisibility(View.GONE);
                    }
                }

                if (isHeadViewOk && isPostOk && is_needRefresh && Math.abs(oldy - 1500) < 20) {     //下拉刷新
                    if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                        Toast.makeText(PersonalCenterActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    refreshImg.startAnimation(clockwiseAnimation);
                    reset();
                    menuBtn.setVisibility(View.GONE);
//                    http.getPersonalCenterInfo(param.getSrp_id(), imStatus, param.getViewerUid(), from,param.getComment_id(), user.token());
                    loadPersonalCenterInfo(param.getSrp_id(), imStatus, param.getViewerUid(), from,param.getComment_id(), user.token());
                    loadPostListForPerson(param.getViewerUid(), new_srpid, param.getInterest_id(), imStatus, 1, psize, 0);
                }

                if (overScrollView.isBottom() && !isLoading && !isLoadAll && canLoadMore && !is_first) { //上拉加载更多
                    if (CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                        loadPostListForPerson(param.getViewerUid(), new_srpid, param.getInterest_id(), imStatus, pno++, psize, adapter.getLastId());
                        isLoading = true;
                        is_showToast = false;
                        footerProgressBar.setVisibility(View.VISIBLE);
                        footerRefreshTv.setText("正在加载…");
                    } else if (!is_showToast) {
                        is_showToast = true;
                        Toast.makeText(PersonalCenterActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                        footerProgressBar.setVisibility(View.GONE);
                        footerRefreshTv.setText("网络异常，上拉重试");
                    }
                }
            }

            @Override
            public void refresh() {
                is_needRefresh = true;
            }

            @Override
            public void loadMore() {

            }
        });

        expressionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progress != null && progress.isLoading) {//加载数据，不执行滑动事件
                    return;
                }
                UIHelper.showMoodSignature(PersonalCenterActivity.this, param.getViewerUid(), 1001);
            }
        });


        avatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (isSelf)
//                {
//                    showPickDialog();
//                    picType = 1;
//                }
                if (progress != null && progress.isLoading) {//加载数据，不执行事件
                    return;
                }
                gotoTouchGallery();

            }
        });


        headBgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progress != null && progress.isLoading) {//加载数据，不执行事件
                    return;
                }
                if (isSelf) {
                    showPickDialog();
                    picType = 2;
                }
            }
        });

        listView.setOnItemClickListener(new LinearLayoutForListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                if (progress != null && progress.isLoading) {//加载数据，不执行事件
                    return;
                }
                PCPost post = (PCPost) adapter.getItem(index);
//                UIHelper.showPostsDetail(PersonalCenterActivity.this, post.getBlog_id(), post.getInterest_id());
                SearchResultItem item1 = new SearchResultItem();
                item1.setBlog_id(post.getBlog_id());
                item1.setInterest_id(post.getInterest_id());
                IntentUtil.startskipDetailPage(PersonalCenterActivity.this, item1);
            }
        });

        interestMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progress != null && progress.isLoading) {//加载数据，不执行事件
                    return;
                }
                if (!is_interest_none) {
                    UIHelper.showInterestOrSRP(PersonalCenterActivity.this, 1, param.getViewerUid());
                }
            }
        });
        srpMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progress != null && progress.isLoading) {//加载数据，不执行事件
                    return;
                }
                if (!is_srp_none) {
                    UIHelper.showInterestOrSRP(PersonalCenterActivity.this, 0, param.getViewerUid());
                }
            }
        });

        postMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progress != null && progress.isLoading) {//加载数据，不执行事件
                    return;
                }
                if (!is_post_none) {
                    UIHelper.showCirclePostsForMe(PersonalCenterActivity.this, param.getSrp_id(), param.getInterest_id(), imStatus, param.getViewerUid());
                }
            }
        });

        interestGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long applicant_id) {
                if (progress != null && progress.isLoading) {//加载数据，不执行事件
                    return;
                }
                InterestBean interest = (InterestBean) adapterView.getAdapter().getItem(i);
                UIHelper.showCircleIndex(PersonalCenterActivity.this, interest.getSrpId(), interest.getSrp_word(), interest.getName(), interest.getUrl());
            }
        });

        srpGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long applicant_id) {
                if (progress != null && progress.isLoading) {//加载数据，不执行事件
                    return;
                }
                SubscribeItem srp = (SubscribeItem) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(PersonalCenterActivity.this, SRPActivity.class);
                intent.putExtra("keyword", srp.keyword());
                intent.putExtra("srpId", srp.srpId());
                startActivity(intent);
            }
        });

        popupMenu = new PersonalCenterImAlertMenu(this);
        popupMenu.setOnMenuClick(new PersonalCenterImAlertMenu.OnMenuClick() {
            @Override
            public void onClick(View whichButton) {
                if (progress != null && progress.isLoading) {//加载数据，不执行事件
                    return;
                }
                switch (whichButton.getId()) {
                    case R.id.percenter_im_menu_send:
                        IMApi.sendCard(PersonalCenterActivity.this, param.getViewerUid());
                        popupMenu.dismiss();
                        break;
                    case R.id.percenter_im_menu_rename:
                        IMApi.modifyNoteName(PersonalCenterActivity.this, param.getViewerUid());
                        popupMenu.dismiss();
                        break;
                    case R.id.percenter_im_menu_del:
                        IMApi.deleteFriend(PersonalCenterActivity.this, param.getViewerUid(), param.getFrom());
                        popupMenu.dismiss();
                        break;
                }
            }
        });


        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show(view);
            }
        });
        
        mPersonalMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupMenu.show(v);
			}
		});
        
        progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                reset();
                refreshImg.startAnimation(clockwiseAnimation);
//                http.getPersonalCenterInfo(param.getSrp_id(), imStatus, param.getViewerUid(), from,param.getComment_id(), user.token());
                loadPersonalCenterInfo(param.getSrp_id(), imStatus, param.getViewerUid(), from,param.getComment_id(), user.token());
                loadPostListForPerson(param.getViewerUid(), new_srpid, param.getInterest_id(), imStatus, 1, psize, 0);
            }
        });

        refreshImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPostOk && isHeadViewOk && CMainHttp.getInstance().isNetworkAvailable(mContext) && !is_forceRefresh) {
                    reset();
                    forceRefresh();
                    refreshImg.startAnimation(clockwiseAnimation);
//                    http.getPersonalCenterInfo(param.getSrp_id(), imStatus, param.getViewerUid(), from,param.getComment_id(), user.token());
                    loadPersonalCenterInfo(param.getSrp_id(), imStatus, param.getViewerUid(), from,param.getComment_id(), user.token());
                    loadPostListForPerson(param.getViewerUid(), new_srpid, param.getInterest_id(), imStatus, 1, psize, 0);
                }
            }
        });
        
        mTitleBarRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
        mGoBack.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (progress != null && progress.isLoading) {
                    return true;
                }else {
                	return false;
                }
			}
		});
    }


    private void forceRefresh() {
        overScrollView.forceRefresh();
        is_forceRefresh = true;
    }

    private boolean isAnimationPlaying;

    private void initRotateAnimation() {
        refreshImg.setScaleType(ImageView.ScaleType.MATRIX);
        mHeaderImageMatrix = new Matrix();
        refreshImg.setImageMatrix(mHeaderImageMatrix);
        onLoadingDrawableSet(refreshImg.getDrawable());
        clockwiseAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        clockwiseAnimation.setInterpolator(new LinearInterpolator());
        clockwiseAnimation.setDuration(800);
        clockwiseAnimation.setRepeatCount(Animation.INFINITE);
        clockwiseAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationPlaying = true;
                refreshBg.setVisibility(View.VISIBLE);
                refreshImg.setVisibility(View.VISIBLE);
                menuBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("animation", "end,,,,");
                if (imStatus == IM_STATUS_FRIEND && !isPrivate) {
                    menuBtn.setVisibility(View.VISIBLE);
                    refreshBg.setVisibility(View.GONE);
                    refreshImg.setVisibility(View.GONE);
                }
                isAnimationPlaying = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        refreshImg.startAnimation(clockwiseAnimation);
    }

    private void initData() {
//        http = new Http(this);
//        aq = new AQuery(this);
//        mCMainHttp= CMainHttp.getInstance();
        param = (PersonPageParam) getIntent().getSerializableExtra("param");
        user = SYUserManager.getInstance().getUser();
        if (param != null && user != null) {
            if (param.getFrom() == PersonPageParam.FROM_INTEREST) {
                from = PersonPageParam.FROM_INTEREST;
            }
            isSelf = param.getViewerUid() == user.userId();
            try {
                imStatus = IMApi.getPersonStatus(param.getViewerUid());
                updateImView();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            http.getPersonalCenterInfo(param.getSrp_id(), imStatus, param.getViewerUid(), from,param.getComment_id(), user.token());
            loadPersonalCenterInfo(param.getSrp_id(), imStatus, param.getViewerUid(), from,param.getComment_id(), user.token());
            //获取帖子列表
            loadPostListForPerson(param.getViewerUid(), new_srpid, param.getInterest_id(), imStatus, 1, psize, 0);
        }
    }

    /**
     * 加载网络数据---个人中心点击更多获取帖子列表
     * @param user_id
     * @param new_srpid
     * @param interest_id
     * @param is_friend
     * @param pno
     * @param psize
     * @param last_sort_num
     */
    private void loadPostListForPerson(long user_id, String new_srpid,
                                       long interest_id, int is_friend, int pno, int psize,
                                       long last_sort_num)
    {

        UserBlogListReq req = new UserBlogListReq(HttpCommon.PERSONCENTER_INTEREST_BLOG_ID,this);
        req.setParams(user_id,new_srpid,interest_id,is_friend,pno,psize,last_sort_num);
        mMainHttp.doRequest(req);
    }

    /**
     * 加载网络数据-----个人中心头部数据接口
     * @param srp_id
     * @param status
     * @param user_id
     * @param from
     * @param comment_id
     * @param token
     */
    private void loadPersonalCenterInfo(String srp_id, int status,
                                        long user_id, int from,long comment_id, String token)
    {
        UserCenterInfoReq req = new UserCenterInfoReq(HttpCommon.PERSONCENTER_INFO_ID,this);
        req.setParams(srp_id,status, user_id, from,comment_id, token);
        mMainHttp.doRequest(req);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        switch (request.getmId())
        {
            case HttpCommon.PERSONCENTER_INFO_ID:
                getPersonalCenterInfoSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.PERSONCENTER_INTEREST_BLOG_ID:
                getPostListForPersonSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.USER_REPIRE_USER_INFO_REQUEST:
                updateProfileSuccess();
                break;
        }

    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        if (progdialog != null) {
            progdialog.dismiss();
        }
        switch (request.getmId()) {
            case HttpCommon.PERSONCENTER_INFO_ID:
            case HttpCommon.PERSONCENTER_INTEREST_BLOG_ID:
                refreshImg.clearAnimation();
                menuBtn.setVisibility(View.GONE);
                progress.showNetError();
                break;
        }

    }

    public void getPersonalCenterInfoSuccess(HttpJsonResponse response) {
        try {
            JsonObject json = response.getBody();
            personalCenterInfo = new Gson().fromJson(json, new TypeToken<PersonalCenterInfo>() {
            }.getType());
            if (personalCenterInfo != null) {             //更新头部
                isPrivate = personalCenterInfo.getIs_private() == PersonalCenterInfo.IS_PRIVATE_YES;
                fillingHeaderView(personalCenterInfo);
                updateHeaderView(personalCenterInfo);
            }
            isHeadViewOk = true;
            if (isPostOk) {
                refreshImg.clearAnimation();
                if (is_forceRefresh) {
                    is_forceRefresh = false;
                    overScrollView.completeForceRefresh();
                }
                if (imStatus == IM_STATUS_FRIEND && !isPrivate) {
                    //TODO set title for others
                    menuBtn.setVisibility(View.VISIBLE);
                    refreshBg.setVisibility(View.GONE);
                    refreshImg.setVisibility(View.GONE);
                } else {
                    menuBtn.setVisibility(View.GONE);
                    refreshBg.setVisibility(View.VISIBLE);
                    refreshImg.setVisibility(View.VISIBLE);
                }
                if (!isSelf) {
                    mTitleBarName.setText(String.format(getString(R.string.others_info_home), nickNameTv.getText()));
                }
                progress.goneLoading();
                is_first = false;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取帖子列表
     * @param response
     */
    public void getPostListForPersonSuccess(HttpJsonResponse response) {
        JsonArray array = response.getBody().getAsJsonArray("personalMblogList");
        List<PCPost> postList = new Gson().fromJson(array, new TypeToken<List<PCPost>>() {
        }.getType());
        if (isLoading) {    //加载更多
            isLoading = false;
            if (CollectionUtils.isEmpty(postList)) {
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(PersonalCenterActivity.this, "已加载全部", Toast.LENGTH_SHORT).show();
                isLoadAll = true;
            } else {
                adapter.addList(postList);
            }
        } else {            //刷新或者第一次加载
            adapter.setList(postList);
            String num = response.getBody().get("totalNum").getAsString();
            postNumTv.setText(num);
            if (CollectionUtils.isEmpty(postList)) {
                postMoreTv.setCompoundDrawables(null, null, null, null);
                postMoreTv.setText("没有发布任何帖子");
                is_post_none = true;
                listView.removeAllViews();
                postMoreLayout.setVisibility(View.VISIBLE);
                postNumTv.setVisibility(View.GONE);
            } else {
                postMoreTv.setText("仅查看" + param.getCircleName() + "帖子");
            }
            if (CollectionUtils.isEmpty(postList) || postList.size() < psize) {
                loadingLayout.setVisibility(View.GONE);
            }
        }
        isPostOk = true;
        if (isHeadViewOk) {
            refreshImg.clearAnimation();
            if (is_forceRefresh) {
                is_forceRefresh = false;
                overScrollView.completeForceRefresh();
            }
            if (imStatus == IM_STATUS_FRIEND && !isPrivate) {
                menuBtn.setVisibility(View.VISIBLE);
                refreshBg.setVisibility(View.GONE);
                refreshImg.setVisibility(View.GONE);
            }
            is_first = false;
            progress.goneLoading();
        }
    }

    private void updateImView() {
        if (isSelf || SYUserManager.USER_GUEST.equals(user.userType())) {    //自己或者游客的时候，隐藏
            chartLayout.setVisibility(View.GONE);
        }
        if (imStatus == IM_STATUS_FRIEND) {    //已经是好友，直接聊天
//            menuBtn.setVisibility(View.VISIBLE);
            chartImg.setImageResource(R.drawable.btn_percenter_chart_selector);
            chartLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (progress != null && progress.isLoading) {//加载数据，不执行事件
                        return;
                    }
                    try {
                        if (param.getFrom() == PersonPageParam.FROM_SINGLE_CHAT) {
                            PersonalCenterActivity.this.finish();
                        } else {
                            IMApi.personGotoIMChat(PersonalCenterActivity.this, param.getViewerUid());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            String remarkName = IMApi.getIMCommentName(param.getViewerUid());
            if (StringUtils.isNotEmpty(remarkName)) {
                remarkNameTv.setText(remarkName);
                remarkNameLayout.setVisibility(View.VISIBLE);
            }
        }

        if (param.getFrom() == PersonPageParam.FROM_IM) {
            if (StringUtils.isNotEmpty(param.getShowName())) {
                nickNameTv.setText(param.getShowName());
            }
            if (StringUtils.isNotEmpty(param.getSubName1())) {
                remarkNameTv.setText(param.getSubName1());
                remarkNameLayout.setVisibility(View.VISIBLE);
            }
            if (StringUtils.isNotEmpty(param.getSubName2())) {
                groupNameTv.setText(param.getSubName2());
                groupNameLayout.setVisibility(View.VISIBLE);
            }
        }

        if (imStatus == IM_STATUS_ADD) {    //非好友,且非游客,加好友
            chartImg.setImageResource(R.drawable.btn_percenter_addbg_selector);
            chartLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (progress != null && progress.isLoading) {//加载数据，不执行事件
                        return;
                    }
                    try {
                        IMApi.addFriend(PersonalCenterActivity.this, param.getViewerUid());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    //下拉刷新的时候reset
    public void reset() {
        last_id = 0;
        pno = 1;
        isLoadAll = false;
        isHeadViewOk = false;
        isPostOk = false;
        isLoading = false;
        loadingLayout.setVisibility(View.VISIBLE);
        canLoadMore = true;
        is_interest_none = false;
        is_post_none = false;
        is_srp_none = false;
        is_first = true;
        is_needRefresh = false;
    }


    /**
     * 往headerView 填充各项数据
     *
     * @param info PersonalCenterInfo
     */
    private void fillingHeaderView(PersonalCenterInfo info) {
        nickNameTv.setText(info.getPerson().getNickname());

//            aq.id(avatarImg).image(info.getPerson().getHead_img(), true, true, 0, R.drawable.circle_default_head);
//        如果url为null的话加载默认图片
        PhotoUtils.showCard( PhotoUtils.UriType.HTTP,info.getPerson().getHead_img(),avatarImg, MyDisplayImageOption.persalOption);

        if (StringUtils.isEmpty(info.getPerson().getBg_img())) {
            headBgImg.setImageResource(R.drawable.circle_vcard_default_top_bg);
        } else {
//             aq.id(headBgImg).image(info.getPerson().getBg_img(), true, true, 0, R.drawable.circle_vcard_default_top_bg);
            if(HeadBgDrawable!=null)
            {
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP,
                        info.getPerson().getBg_img(), headBgImg,
                        MyDisplayImageOption.getOptionsForDrawable(HeadBgDrawable),new ImageLoadingListener()
                        {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                headBgImg.setImageDrawable(HeadBgDrawable);
                            }
                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                headBgImg.setImageDrawable(HeadBgDrawable);
                            }
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            }
                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                                headBgImg.setImageDrawable(HeadBgDrawable);
                            }
                        });
            }else
            {
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, info.getPerson().getBg_img(), headBgImg, MyDisplayImageOption.getOptions(R.drawable.circle_vcard_default_top_bg));
            }
        }
//        aq.id(R.id.percenter_sex).image(info.getPerson().getSex() == SEX_GIRL ? R.drawable.sex_girl : R.drawable.sex_boy);
        ImageView percenter_sex = findView(R.id.percenter_sex);
        if(percenter_sex != null){
            percenter_sex.setImageResource(info.getPerson().getSex() == SEX_GIRL ? R.drawable.sex_girl : R.drawable.sex_boy);
        }
        expressionTv.setText(StringUtils.isNotEmpty(info.getPerson().getSignature()) ? info.getPerson().getSignature() : "没有留下任何文字");
        expressionIv.setImageResource(getExpressionImage(info.getPerson().getMood_id()));
        if (isSelf) {   //自己
            interestTv.setText("我的兴趣圈");
            srpTv.setText("我的主题");
            postTv.setText("我的帖子");
            editExpressionLayout.setVisibility(View.VISIBLE);
        }
        interestNumTv.setText(info.getInterest_num() + "");
        srpNumTv.setText(info.getSrp_num() + "");

        List<PersonalCenterInfo.SRP> srpList = info.getSrp();
        List<PersonalCenterInfo.Interest> interestList = info.getInterest();

        if (CollectionUtils.isEmpty(interestList)) {
            interestGridView.setVisibility(View.GONE);
            interestMoreTv.setCompoundDrawables(null, null, null, null);
            if (isSelf) {
                interestMoreTv.setText("没有加入任何兴趣圈");
            } else {
                interestMoreTv.setText("对方设置了隐私保护");
            }
            interestNumTv.setVisibility(View.GONE);
            is_interest_none = true;
        } else {
            interestItemAdapter.set(convertToInterestBean(interestList));
        }
        if (CollectionUtils.isEmpty(srpList)) {
            srpGridView.setVisibility(View.GONE);
            srpMoreTv.setCompoundDrawables(null, null, null, null);
            srpMoreTv.setText("没有订阅任何主题");
            srpNumTv.setVisibility(View.GONE);
            is_srp_none = true;
        } else {
            srpItemAdapter.set(convertToSubscribeItem(srpList));
        }
    }

    /**
     * 根据业务需求，隐藏或者显示部分headerView 中的子view
     *
     * @param info PersonalCenterInfo
     */
    public void updateHeaderView(PersonalCenterInfo info) {
        if (!isSelf && from == PersonPageParam.FROM_INTEREST) {               //不是自己且来源为圈子
            if (info.getIs_private() == PersonalCenterInfo.IS_PRIVATE_YES) {  //开启隐私保护
                expressionLayout.setVisibility(View.GONE);
                interestLayout.setVisibility(View.GONE);
                srpLayout.setVisibility(View.GONE);
                dividerForInterest.setVisibility(View.GONE);
                dividerForSrp.setVisibility(View.GONE);
                if (!is_post_none) {
                    postMoreLayout.setVisibility(View.GONE);
                }
                remarkNameLayout.setVisibility(View.GONE);              //备注名也不能显示
                menuBtn.setVisibility(View.GONE);
                chartLayout.setVisibility(View.GONE);
                canLoadMore = true;
                loadingLayout.setVisibility(View.GONE);
            } else {                                                    //未开启隐私保护,则默认都显示,各种查看更多都存在
                expressionLayout.setVisibility(View.VISIBLE);           //此else是有必要的，因为刷新的时候，有可能从开启隐私保护状态改为关闭隐私保护状态
                interestLayout.setVisibility(View.VISIBLE);
                if (!is_interest_none) {
                    interestGridView.setVisibility(View.VISIBLE);
                }
                if (!is_srp_none) {
                    srpGridView.setVisibility(View.VISIBLE);
                }
                srpLayout.setVisibility(View.VISIBLE);
                dividerForInterest.setVisibility(View.VISIBLE);
                dividerForSrp.setVisibility(View.VISIBLE);
                postMoreLayout.setVisibility(View.VISIBLE);
                canLoadMore = true;
                loadingLayout.setVisibility(View.VISIBLE);
            }

            if (imStatus == PersonalCenterActivity.IM_STATUS_FRIEND && info.getIs_private() == PersonalCenterInfo.IS_PRIVATE_NO) {     //是好友,各种都开启,各种功能都可以用
                expressionLayout.setVisibility(View.VISIBLE);
                interestLayout.setVisibility(View.VISIBLE);
                srpLayout.setVisibility(View.VISIBLE);
                dividerForInterest.setVisibility(View.VISIBLE);
                dividerForSrp.setVisibility(View.VISIBLE);
                postMoreLayout.setVisibility(View.VISIBLE);
                canLoadMore = true;
                loadingLayout.setVisibility(View.VISIBLE);

            }

            if (imStatus == PersonalCenterActivity.IM_STATUS_ADD) {           //不是好友,只显示三条帖子，不能上拉加载更多
                if (!is_interest_none) {
                    interestMoreLayout.setVisibility(View.GONE);
                }
                if (!is_srp_none) {
                    srpMoreLayout.setVisibility(View.GONE);
                }
                if (!is_post_none) {
                    postMoreLayout.setVisibility(View.GONE);
                }
                loadingLayout.setVisibility(View.GONE);
                canLoadMore = false;
            }

            if (info.getIs_private() == PersonalCenterInfo.IS_PRIVATE_YES) {
                canLoadMore = true;
                loadingLayout.setVisibility(View.VISIBLE);
            }
        }


        if (!isSelf && from != PersonPageParam.FROM_INTEREST) {               //不是自己且来源为非圈子
            if (imStatus == PersonalCenterActivity.IM_STATUS_FRIEND && !is_post_none) {        //是好友,无筛选来源圈帖子的功能
                postMoreLayout.setVisibility(View.GONE);
            }

            if (imStatus == PersonalCenterActivity.IM_STATUS_ADD) {           //不是好友
                if (!is_interest_none) {
                    interestMoreLayout.setVisibility(View.GONE);
                }
                if (!is_srp_none) {
                    srpMoreLayout.setVisibility(View.GONE);
                }
                if (!is_post_none) {
                    postMoreLayout.setVisibility(View.GONE);
                }
                loadingLayout.setVisibility(View.GONE);
                canLoadMore = false;
            }
        }

        if (from != PersonPageParam.FROM_INTEREST) {      //来源不是圈子，没有筛选功能
            postMoreLayout.setVisibility(View.GONE);
        }


        if (remarkNameLayout.getVisibility() == View.GONE && groupNameLayout.getVisibility() == View.GONE) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) expressionLayout.getLayoutParams();
            layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.space_25), getResources().getDimensionPixelOffset(R.dimen.space_10), 0, 0);
            expressionLayout.setLayoutParams(layoutParams);
        }

    }


    public List<InterestBean> convertToInterestBean(List<PersonalCenterInfo.Interest> list) {
        List<InterestBean> interestBeanList = new ArrayList<InterestBean>();
        for (PersonalCenterInfo.Interest interest : list) {
            InterestBean bean = new InterestBean();
            bean.setSrpId(interest.getId());
            bean.setId((int) interest.getInterestId());
            bean.setImage(interest.getImage());
            bean.setName(interest.getName());
            bean.setSrp_word(interest.getKeyword());
            interestBeanList.add(bean);
        }
        return interestBeanList;
    }

    public List<SubscribeItem> convertToSubscribeItem(List<PersonalCenterInfo.SRP> list) {
        List<SubscribeItem> subscribeItemList = new ArrayList<SubscribeItem>();
        for (PersonalCenterInfo.SRP srp : list) {
            SubscribeItem item = new SubscribeItem(srp.getKeyword(), srp.getSrpId());
            item.image_$eq(srp.getImage());
            subscribeItemList.add(item);
        }
        return subscribeItemList;
    }


    public int getExpressionImage(int id) {
        switch (id) {
            case 0:
                return R.drawable.expression_happy_small;
            case 1:
                return R.drawable.expression_laugh_small;
            case 2:
                return R.drawable.expression_meng_small;
            case 3:
                return R.drawable.expression_daze_samll;
            case 4:
                return R.drawable.expression_anger_samll;
            case 5:
                return R.drawable.expression_sad_small;
            case 6:
                return R.drawable.expression_depressed_small;
            case 7:
                return R.drawable.expression_cry_small;
            default:
                return R.drawable.expression_happy_small;
        }
    }

    public void onLoadingDrawableSet(Drawable imageDrawable) {
        if (null != imageDrawable) {
            mRotationPivotX = imageDrawable.getIntrinsicWidth() / 2f;
            mRotationPivotY = imageDrawable.getIntrinsicHeight() / 2f;
        }
    }

    protected void onPullImpl(float scaleOfLayout) {
        float angle = scaleOfLayout * 90f;
        // Log.d(TAG, "angle=" + angle);
        mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
        refreshImg.setImageMatrix(mHeaderImageMatrix);
    }

    private void prepareToRotate(boolean isRotate) {
        if (isRotate) {
            if (menuBtn.getVisibility() == View.VISIBLE) {
                menuBtn.setVisibility(View.GONE);
            }
            if (refreshBg.getVisibility() == View.GONE) {
                refreshBg.setVisibility(View.VISIBLE);
                refreshImg.setVisibility(View.VISIBLE);
            }
            if (refreshImg.getVisibility() == View.GONE) {
                refreshImg.setVisibility(View.VISIBLE);
                refreshBg.setVisibility(View.VISIBLE);
            }
        } else {
            if (imStatus == IM_STATUS_FRIEND && !isPrivate) {
                if (!isAnimationPlaying) {
                    menuBtn.setVisibility(View.VISIBLE);
                    if (refreshBg.getVisibility() == View.VISIBLE) {
                        refreshBg.setVisibility(View.GONE);
                    }
                    if (refreshImg.getVisibility() == View.VISIBLE) {
                        refreshImg.setVisibility(View.GONE);
                    }
                }
            }
        }
    }


    private void initUpload() {
        progdialog = new ProgressDialog(this);
        progdialog.setMessage("正在上传 ");
        progdialog.setCanceledOnTouchOutside(false);
        profileImgFile = new File(getCacheDir(), "headphoto_");
    }

    private void gotoTouchGallery() {
        String HeadImg = "";
        if (isSelf) {
            HeadImg = SYUserManager.getInstance().getUser().getBigImage();
            if (StringUtils.isEmpty(HeadImg)) {
                HeadImg = SYUserManager.getInstance().getUser().image();
            }
        } else if (!StringUtils.isEmpty(personalCenterInfo.getPerson().getHead_img())) {
            HeadImg = personalCenterInfo.getPerson().getBigImage();
            if (StringUtils.isEmpty(HeadImg)) {
                HeadImg = personalCenterInfo.getPerson().getHead_img();
            }
        }
        if (!StringUtils.isEmpty(HeadImg)) {
            Intent intent = new Intent();
            intent.setClass(this, TouchGalleryActivity.class);
            TouchGallerySerializable tg = new TouchGallerySerializable();
            List<String> list = new ArrayList<String>();
            list.add(HeadImg);
            tg.setItems(list);
            tg.setClickIndex(0);
            Bundle extras = new Bundle();
            extras.putSerializable("touchGalleryItems", tg);
            intent.putExtras(extras);
            startActivity(intent);
        }

    }

    /**
     * 选择提示对话框
     */
    public void showPickDialog() {
        String shareDialogTitle = getString(R.string.pick_dialog_title);
        MMAlert.showAlert(this, shareDialogTitle, getResources().getStringArray(R.array.picks_item), null, new MMAlert.OnAlertSelectId() {

            @Override
            public void onClick(int whichButton) {
                switch (whichButton) {
                    case 0: // 拍照
                        try {
                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            if (Utils.isIntentSafe(PersonalCenterActivity.this, i)) {
                                startActivityForResult(i, 2);
                            } else {
                                SouYueToast.makeText(PersonalCenterActivity.this, getString(R.string.dont_have_camera_app), SouYueToast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            SouYueToast.makeText(PersonalCenterActivity.this, getString(R.string.cant_insert_album), SouYueToast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1: // 相册
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, 4);
                        break;
                    default:
                        break;
                }
            }

        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 4:// 如果是直接从相册获取
                    if (data != null) {
                        Uri uri = data.getData();
                        startPhotoZoom(uri, imageUri);
                    }
                    break;
                case 2:// 如果是调用相机拍照时
                    String picPath = null;
                    if (imageUri != null) {
                        picPath = Utils.getPicPathFromUri(imageUri, this);
                        int degree = 0;
                        if (!StringUtils.isEmpty(picPath))
                            degree = ImageUtil.readPictureDegree(picPath);
                        Matrix matrix = new Matrix();
                        if (degree != 0) {// 解决旋转问题
                            matrix.preRotate(degree);
                        }
                        Uri uri = Uri.fromFile(new File(picPath));
                        startPhotoZoom(uri, imageUri);
                    } else {
                        SouYueToast.makeText(this, "图片获取异常", SouYueToast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:// 取得裁剪后的图片
                    if (data != null) {
                        setPicToView(imageUri);
                    }
                    break;
                case 1:
                    if (data != null) {
                        String remarkName = data.getStringExtra(ImModifyNoteName.TAG);
                        if (StringUtils.isNotEmpty(remarkName)) {
                            remarkNameTv.setText(remarkName);
                            remarkNameLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 1001:
                    if (data != null) {
                        String signature = data.getStringExtra("signature");
                        int mood_id = data.getIntExtra("mood_id", 0);
                        if (isSelf && StringUtils.isNotEmpty(signature)) {
                            expressionTv.setText(signature);
                        }
                        expressionIv.setImageResource(getExpressionImage(mood_id));
                    }
                    break;
            }
        }
    }


    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView(Uri imageUri) {
        if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
            SouYueToast.makeText(PersonalCenterActivity.this,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        progdialog.show();
        try {
            Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            int newWidth = 100;
            if (photo.getWidth() >= 100) {
                newWidth = photo.getWidth();
            }
            int newHeight = 100;
            if (photo.getHeight() >= 100) {
                newHeight = photo.getHeight();
            }
            if (photo.getWidth() < 100 || photo.getHeight() < 100) {
                photo = Bitmap.createScaledBitmap(photo, newWidth, newHeight, false);
            }
            drawable = new BitmapDrawable(photo);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(profileImgFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        boolean exit = profileImgFile.exists();
        LogDebugUtil.v("FAN", "setPicToView URL: " + profileImgFile.getAbsolutePath());
        if (!exit) {
            showToast(R.string.upload_photo_fail);
            return;
        }

        if (user != null) {
//            http.uploadUserHead(this, user.userId(), profileImgFile);
            UploadImageTask.executeTask(this, user.userId(), profileImgFile);

        } else {
            showToast(R.string.token_error);
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri, Uri imageUri) {
        // LogDebugUtil.v("FAN", "startPhotoZoom URL: " + uri);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        if (picType == 1) {
            intent.putExtra("outputX", 100);
            intent.putExtra("outputY", 100);
        } else if (picType == 2) {
            intent.putExtra("outputX", 250);
            intent.putExtra("outputY", 250);
        }
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 3);
    }

    public void showToast(int resId) {
        SouYueToast.makeText(PersonalCenterActivity.this, getResources().getString(resId), 0).show();
    }

    public void uploadSuccess(String url) {
        if (profileImgFile.exists()) {
            profileImgFile.delete();
        }
        File file = new File(IMAGE_FILE_LOCATION);
        if (file.exists())
            file.delete();
        LogDebugUtil.v("FAN", "onFinish URL: " + url);
        Log.i("uploadSuccess", "uploadSuccess:" + url);
        bgUrl = url;
        if (!TextUtils.isEmpty(url)) {
            if (user != null)

                if (picType == 1) {
                    UserRepairInfo info = new UserRepairInfo(HttpCommon.USER_REPIRE_USER_INFO_REQUEST,this);
                    info.setParams(user.token(), url, user.name(), null, null);
                    mMainHttp.doRequest(info);
//                    http.updateProfile(user.token(), url, user.name(), null, null);
                }

            if (picType == 2) {
                UserRepairInfo info = new UserRepairInfo(HttpCommon.USER_REPIRE_USER_INFO_REQUEST,this);
                info.setParams(user.token(), null, user.name(), url, null);
                mMainHttp.doRequest(info);
//                http.updateProfile(user.token(), null, user.name(), url, null);
            }

        } else {
            SouYueToast.makeText(PersonalCenterActivity.this, "上传图片失败", 0).show();
        }
    }

    public void updateProfileSuccess() {
        LogDebugUtil.v("FAN", "drawable=" + drawable);
        if (picType == 1) {
            avatarImg.setImageDrawable(drawable);
            user.image_$eq(bgUrl);
        }

        if (picType == 2) {
            HeadBgDrawable =drawable;
            headBgImg.setImageDrawable(drawable);
            user.bgUrl_$eq(bgUrl);
        }

        Log.i("bgUrl", "bgUrl:" + bgUrl);
        SYUserManager.getInstance().setUser(user);
        ThreadPoolUtil.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                ImserviceHelp.getInstance().im_update(4, 0, SYUserManager.getInstance().getImage());
            }
        });
        SouYueToast.makeText(PersonalCenterActivity.this, "上传图片成功", 0).show();
        if (progdialog != null) {
            progdialog.dismiss();
        }
    }

//    @Override
//    public void onHttpError(String methodName) {
//        if ("getPersonalCenterInfo".equals(methodName)) {
//            //Toast.makeText(PersonalCenterActivity.this,"get personal info error",Toast.LENGTH_SHORT).show();
//            refreshImg.clearAnimation();
//            menuBtn.setVisibility(View.GONE);
//            progress.showNetError();
//
//        }
//
//        if ("getPostListForPerson".equals(methodName)) {
//            //Toast.makeText(PersonalCenterActivity.this,"get personal list error",Toast.LENGTH_SHORT).show();
//            refreshImg.clearAnimation();
//            menuBtn.setVisibility(View.GONE);
//            progress.showNetError();
//
//        }
//
//        if (progdialog != null) {
//            progdialog.dismiss();
//        }
//
//    }
    
    /*private void fadeInOutAnimation(View view,float fromAlpha,float toAlpha) {
        AnimationSet animationSet = new AnimationSet(true);//创建一个AnimationSet对象
    	AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);//创建一个AlphaAnimation对象             
    	alphaAnimation.setDuration(50);//设置动画执行的时间（单位：毫秒）         
    	animationSet.addAnimation(alphaAnimation);//将AlphaAnimation对象添加到AnimationSet当中          
    	view.startAnimation(animationSet);//使用view的startAnimation方法开始执行动画      
    }*/
}