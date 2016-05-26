package com.zhongsou.souyue.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.adapter.ListViewPostsAdapterNew;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.model.Reply;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.module.GalleryCommentDetailItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.detail.NewCommentListRequest;
import com.zhongsou.souyue.net.volley.CDetailHttp;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.AnoyomousUtils;
import com.zhongsou.souyue.utils.CollectionUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.util.ArrayList;
import java.util.List;

//import com.alibaba.fastjson.JSON;

//import com.zhongsou.souyue.ent.http.HttpHelper;

/**
 * Created by zyw on 2015/11/5.
 * 通用的评论区
 * 需要在ActivityResult中调用
 * view.onActivityResult(requestCode,resultCode,data);
 * 需要在Activity.onResume中调用
 * view.onResume();方法
 */
public class CommonCommitView extends RelativeLayout implements View.OnClickListener, IVolleyResponse {

    public static final int DEVICE_COME_FROM = 3;// 来自搜悦客户端
    public static final int DETAIL_TYPE_NEWS = 1;// srp类型
    public static final int CIRCLE_TYPE_NEWS = 0;// 新闻
    public static final long PAGE_SIZE_5 = 5; //每页评论数据
    private final CMainHttp mMainHttp;
    private List<CommentsForCircleAndNews> mPostsList; //评论
    private List<CommentsForCircleAndNews> mPostsListHot; // 精华评论
    private String mKeyword;// 分享的关键字
    private String mSrpId;// 分享的关键字
    private String mUrl;
    private String mTitle;
    private String mContent;
    private String mNickName; // 登录用户在圈成员昵称
    private String mImageUrl; // 登录用户在圈成员头像
    private long mLastSortNum;// 上一页最后一个跟帖的blog_id
    private int mDetailType; // 详情类型
    private int mPno = 1; // 当前页面
    private int mVisibleLast; // 最后一个可见的条目
    private boolean mHeadSuccess; // 头部信息获取成功
    private boolean mCommentListSuccess; // 加载评论成功
    private boolean needLoad; // 是不是需要刷新
    private boolean isLoadAll;// 是否完全加载了
    private boolean isRefreshData; // 是否是新数据
    private boolean mFirstLoaded = true; // 第一次加载
    private GalleryCommentDetailItem mItem; // 帖子详情
    private Uri mImageFileUri; // 本地图片地址
    private CommentBottomView circleFollowDialog; // 评论区域
    private LinearLayout mHaveNoComment; // 没有评论时的view
    private ViewGroup addIMgView; // 添加图片..
    private RelativeLayout mCommentView; //评论的主view对象
    private ListView listView; // 评论显示区
    private ListViewPostsAdapterNew adapter;//评论的adapter
    private View footerView; // 底部view
    //    private long mPubtime;
    private Dialog showDialogAddImg; // 添加图片的弹框

    private final CDetailHttp mVolleyHttp; // http对象
    //    private final Http http; // http对象
    private Activity mContext; // 上下文

    private LoadingListener mListener; // 加载结束调用的接口N
    private int mOptionRoleType; // 成员类型
    private int mCircleType; // 圈子类型
    private long mblog_userId; // uid
    private String mSource; // 来源
    private String mChannel; // 频道，评论

    @Override
    public void onClick(View v) {

    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        Log.e("http", methodName + "error");
//        if (circleFollowDialog != null)
//            circleFollowDialog.dismissProcessDialog();
//    }

    /**
     * 接口，loading状态的回调
     */
    public interface LoadingListener {

        void onLoadingFinished();

        void onLoadingError();

        void onLoadingAll();
    }

    /**
     * 构造器，需要传入上下文，和评论的view对象
     *
     * @param context
     */
    public CommonCommitView(Activity context, GalleryCommentDetailItem item, LoadingListener listener) {
        super(context);
        this.mContext = context;
        this.mItem = item;
        this.mCommentView = (RelativeLayout) View.inflate(context, R.layout.gallerynews_comment, null);
        this.addView(mCommentView);
        mVolleyHttp = new CDetailHttp(context);
        mMainHttp = CMainHttp.getInstance();//new CGalleryNewsHttp(context);
//        http = new Http(this);
        this.mListener = listener;
        initView();
        initData();
    }

    /**
     * 重载数据
     */
    public void reload() {
        initData();
    }

    /**
     * 这里有一些回调.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode != Activity.RESULT_CANCELED) { // 从相机回来
            String picPath = null;
            if (mImageFileUri != null) {
                picPath = Utils.getPicPathFromUri(mImageFileUri,
                        mContext);
                int degree = 0;
                if (!StringUtils.isEmpty(picPath))
                    degree = ImageUtil.readPictureDegree(picPath);
                Matrix matrix = new Matrix();
                if (degree != 0) {// 解决旋转问题
                    matrix.preRotate(degree);
                }
                ArrayList<String> list = new ArrayList<String>();
                list.add(picPath);
                circleFollowDialog.addImagePath(list);

            } else {
                Toast.makeText(mContext, R.string.self_get_image_error,
                        Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 0x500) { // 详情页登陆后，在关闭时候通知列表做数据的更新
            if (data == null) {
                return;
            }
        }
        if (resultCode == UIHelper.RESULT_OK) {
            Posts published = (Posts) data.getSerializableExtra("publishPosts");
            if (published == null) {
                return;
            }

        } else if (resultCode == UIHelper.RESULT_CODE_REPLY) { // 回复后更新回复列表
            long mBlogId = data.getLongExtra("comment_id", 0);
            ArrayList<Reply> newReplyList = (ArrayList<Reply>) data
                    .getSerializableExtra("newReplyList");
            for (CommentsForCircleAndNews p : mPostsList) {
                if (p.getComment_id() == mBlogId) {
                    p.setReplyList(newReplyList);
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (resultCode == 0x200) { // 从相册回来
            List<String> list = data.getStringArrayListExtra("imgseldata");
            circleFollowDialog.addImagePath(list);
        }
    }

    /**
     * 判断是否需要重新加载品论列表
     */
    public void onResume() {
        if (isRefreshData) {
            mPostsList.clear();
            mLastSortNum = 0;
            mPno = 1;
            mVisibleLast = 0;
            needLoad = false;
            isLoadAll = false;
            getCommentList(false);
        }
        isRefreshData = false;
        if (circleFollowDialog != null) {
            circleFollowDialog.setReview();
        }
    }


    /**
     * 初始化view
     */
    private void initView() {
        mHaveNoComment = findView(R.id.detail_have_no_comment);
        mHaveNoComment.setOnClickListener(this);
        mHaveNoComment.setVisibility(View.INVISIBLE);
        mHaveNoComment.setEnabled(false);
        listView = (ListView) findViewById(R.id.listView);
    }

    /**
     * 初始化data
     */
    private void initData() {
        mChannel = mItem.getChannel();
        mKeyword = mItem.getKeyword();
        mSrpId = mItem.getSrpId();
        mTitle = mItem.getTitle();
        mContent = mItem.getDescription();
        mSource = mItem.getSource();
        mUrl = mItem.getUrl();
        mDetailType = DETAIL_TYPE_NEWS; // mDetailType
        mblog_userId = 0;
        mNickName = SYUserManager.getInstance().getUserName();
        mImageUrl = SYUserManager.getInstance().getImage();
        mCircleType = CIRCLE_TYPE_NEWS;
        mPostsList = new ArrayList<CommentsForCircleAndNews>();
        mPostsListHot = new ArrayList<CommentsForCircleAndNews>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                EmojiPattern.getInstace().getFileText(mContext);
            }
        }).start();
        initAdapter();
        bindListener();

    }


    /**
     * 初始化事件
     */
    private void bindListener() {
        adapter.setChangeListener(new OnChangeListener() {
            @Override
            public void onChange(Object obj) {
                if (adapter.getCount() == 0) {
                    mHaveNoComment.setVisibility(View.VISIBLE);
                    listView.removeFooterView(footerView);
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && mVisibleLast == adapter.getCount() - 1 && needLoad) {
                    needLoad = false;
                    getCommentList(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (listView.getFooterViewsCount() == 0) {
                    mVisibleLast = firstVisibleItem + visibleItemCount - 1;
                } else {
                    mVisibleLast = firstVisibleItem + visibleItemCount - 2;
                }
            }
        });
    }


    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVolleyHttp.cancelAll();
//        mMainHttp.cancelAll();
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        int id = _request.getmId();
//        if (!mMainHttp.setFinished(id)) {
//            return;
//        }
        switch (id) {
            case HttpCommon.DETAIL_COMMENT_NEW_LIST_ID:
                List<Object> comls = (List<Object>) _request.getResponse();
                getCommentListSuccess((List<CommentsForCircleAndNews>) comls.get(CDetailHttp.DETAIL_COMMENT_LIST_COMMENT),
                        (List<CommentsForCircleAndNews>) comls.get(CDetailHttp.DETAIL_COMMENT_LIST_HOT));
                break;
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                commentDetailSuccess(_request.<HttpJsonResponse>getResponse());
                break;
        }
    }


    public void onHttpError(IRequest _request) {
        int id = _request.getmId();
//        if (!mMainHttp.setFinished(id)) {
//            return;
//        }
        switch (id) {
            case HttpCommon.DETAIL_COMMENT_NEW_LIST_ID:
                if (mPno > 1) {
                    SouYueToast.makeText(mContext, "网络异常，请重试！",
                            Toast.LENGTH_SHORT).show();
                    needLoad = true;
                }
                mListener.onLoadingError();
                break;
            case CDetailHttp.HTTP_GET_SECOND_LIST:
                mListener.onLoadingError();
                break;
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                if (circleFollowDialog != null)
                    circleFollowDialog.dismissProcessDialog();
                break;

        }
    }

    @Override
    public void onHttpStart(IRequest _request) {

    }

    /**
     * 初始化adapter
     */
    public void initAdapter() {
        adapter = new ListViewPostsAdapterNew(mContext, mPostsList, mPostsListHot,
                mblog_userId, 0, DEVICE_COME_FROM);// 创建将近耗时10毫秒
        adapter.setMain_title(mTitle);
        adapter.setMain_source(mSource);
        adapter.setMain_decsription(mContent);
        adapter.setMain_name(mSource);
        adapter.setMain_date(Long.toString(mItem.pubTime));
//        adapter.setmDeatilType(mDetailType);
        adapter.setCircleType(CIRCLE_TYPE_NEWS);
//        adapter.setDetailHttp(mVolleyHttp);
        if (adapter != null) {
            adapter.setmUrl(mUrl);
            adapter.notifyDataSetChanged();
        }
        adapter.setKeyWord(mKeyword);
        adapter.setSrp_id(mSrpId);
//        adapter.setmDeatilType(mDetailType);
        adapter.setmUrl(mUrl);
        if (SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST)) {
            adapter.setNickName("游客");
        } else {
            adapter.setNickName(SYUserManager.getInstance().getName());
        }
        adapter.setImage(mImageUrl);
        mOptionRoleType = Constant.ROLE_NONE;
        adapter.setRole(mOptionRoleType);
        mHeadSuccess = true;
        getCommentList(false);
        if (mCommentListSuccess) {
            mHaveNoComment.setEnabled(true);
        }
    }


    /**
     * 底部视图
     *
     * @return
     */
    private View getFootView() {
        if (footerView == null) {
            footerView = View.inflate(mContext,
                    R.layout.ent_refresh_footer, null);
            footerView.setBackgroundColor(0xffffffff);
        }
        return footerView;
    }

    /**
     * 获取评论列表，_ispull = 是否增量
     *
     * @param _ispull
     */
    private void getCommentList(boolean _ispull) {
        if (isLoadAll) {
            UIHelper.ToastMessage(mContext, "已全部加载");
            needLoad = false;
            return;
        }
        if (listView.getFooterViewsCount() == 0 && _ispull) {
            View v = getFootView();
            v.setVisibility(View.VISIBLE);
            listView.addFooterView(footerView);
        }

        NewCommentListRequest request = new NewCommentListRequest(HttpCommon.DETAIL_COMMENT_NEW_LIST_ID, this);
        request.setParams(mUrl,
                DEVICE_COME_FROM, mLastSortNum, mSrpId, mKeyword,
                mDetailType + 1);
        mMainHttp.doRequest(request);
//        mMainHttp.doCommentList(mUrl,
//                DEVICE_COME_FROM, mLastSortNum, mSrpId, mKeyword,
//                mDetailType + 1, this);
    }

    // 初始化跟帖控件添加图片View
    private void initAddImgLayout() {
        if (addIMgView != null) {
            return;
        }
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addIMgView = (ViewGroup) mLayoutInflater.inflate(
                R.layout.circle_follow_add_img_menu, null, false);
        TextView textView_xiangce = (TextView) addIMgView
                .findViewById(R.id.textView_xiangce);
        TextView textView_photo = (TextView) addIMgView
                .findViewById(R.id.textView_photo);
        TextView textView_cancel = (TextView) addIMgView
                .findViewById(R.id.textView_cancel);
        textView_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
            }
        });
        textView_xiangce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
                IntentUtil.jumpImgGroup(mContext,
                        circleFollowDialog.getImgLen());
            }
        });
        textView_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
                jumpTakePhoto();
            }
        });
    }

    /**
     * 增加图片
     */
    private void showAddImgMenu() {
        initAddImgLayout();
        showDialogAddImg = showAlert(mContext, addIMgView, Gravity.BOTTOM);
    }

    /**
     * 警告信息
     *
     * @param context
     * @param content
     * @param gravity
     * @return
     */
    public static Dialog showAlert(final Context context, ViewGroup content,
                                   int gravity) {
        if (content.getParent() != null) {
            ((ViewGroup) content.getParent()).removeView(content);
        }
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = gravity;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(content);
        dlg.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.show();
        return dlg;
    }


    /**
     * 显示评论框
     */
    public void showComment() {
        listView.invalidate();
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showCommentDialog();
            }
        }, 50);
    }

    /**
     * 显示评论框
     */
    private void showCommentDialog() {
        if (circleFollowDialog == null) {
            circleFollowDialog = new CommentBottomView(
                    mContext, this, mUrl,
                    DEVICE_COME_FROM, mSrpId, mKeyword, null);
            this.addView(circleFollowDialog, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }
        circleFollowDialog.setVisibility(View.VISIBLE);
        circleFollowDialog.setMain_title(mTitle);
        circleFollowDialog.setMain_decsription(mContent);
        circleFollowDialog.setDetailType(mDetailType);
        circleFollowDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        circleFollowDialog.saveInfo(mUrl);
                    }
                });
        circleFollowDialog.setListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                if (circleFollowDialog.getImgLen() == 0) {
                    IntentUtil.jumpImgGroup(mContext,
                            circleFollowDialog.getImgLen());
                }
            }
        });
        circleFollowDialog.setPhotoListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                jumpTakePhoto();
            }
        });
        circleFollowDialog.setAddImgListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                showAddImgMenu();
            }
        });
        circleFollowDialog
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                if (adapter.getCount() == 0) {
                                    mHaveNoComment.setVisibility(View.VISIBLE);
                                } else {
                                    mHaveNoComment.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
        circleFollowDialog.showDialog();
        circleFollowDialog.setEditText(SYSharedPreferences.getInstance()
                .getString(mUrl + "_text", ""));
        String strImg = SYSharedPreferences.getInstance().getString(
                mUrl + "_img", "");
        if (strImg != null && !strImg.equals("")) {
//            List<String> list = JSON.parseArray(strImg, String.class);
            List<String> list = new Gson().fromJson(strImg, new TypeToken<List<String>>() {
            }.getType());
            if (list != null && list.size() != 0) {
                circleFollowDialog.addImagePath(list);
            }
        }
    }

    /**
     * 选择图片
     */
    private void jumpTakePhoto() {
        if (circleFollowDialog.getImgLen() > 9) {
            Toast.makeText(mContext, "最多选择9张图片", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            mImageFileUri = mContext.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new ContentValues());
            if (mImageFileUri != null) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);
                if (Utils.isIntentSafe(mContext, i)) {
                    mContext.startActivityForResult(i, 2);
                } else {
                    SouYueToast.makeText(mContext,
                            mContext.getString(R.string.dont_have_camera_app),
                            SouYueToast.LENGTH_SHORT).show();
                }
            } else {
                SouYueToast.makeText(mContext,
                        mContext.getString(R.string.cant_insert_album),
                        SouYueToast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            SouYueToast.makeText(mContext,
                    mContext.getString(R.string.cant_insert_album),
                    SouYueToast.LENGTH_SHORT).show();
        }
    }

    /**
     * 评论列表回来
     *
     * @param listLatest
     * @param listHot
     */
    public void getCommentListSuccess(List<CommentsForCircleAndNews> listLatest, List<CommentsForCircleAndNews> listHot) {
        if (listView.getAdapter() == null) {
            listView.setAdapter(adapter);
        }
        if (!listHot.isEmpty()) {
            mPostsList.addAll(listHot);
            mPostsListHot.addAll(listHot);
        }
        if (!listLatest.isEmpty()) {
            mLastSortNum = listLatest.get(listLatest.size() - 1)
                    .getComment_id();
            mPostsList.addAll(listLatest);
            adapter.notifyDataSetChanged();
            mPno++;
        }
        if (adapter.getCount() == 0) {
            mHaveNoComment.setVisibility(View.VISIBLE);
            listView.removeFooterView(footerView);
            isLoadAll = true;
            needLoad = false;
        } else {
            if (CollectionUtils.isEmpty(listLatest)
                    || listLatest.size() < PAGE_SIZE_5) {
                isLoadAll = true;
                needLoad = false;
                listView.removeFooterView(footerView);
            } else {
                needLoad = true;
            }
            mHaveNoComment.setVisibility(View.GONE);
        }
        mCommentListSuccess = true;
        if (mHeadSuccess) {
            mHaveNoComment.setEnabled(true);
        }
        if (mFirstLoaded) {
            mListener.onLoadingFinished();
            mFirstLoaded = false;
        }
        if (!needLoad && isLoadAll) {
            mListener.onLoadingAll();
        }
    }

    public CommentBottomView getCircleFollowDialog() {
        return circleFollowDialog;
    }

    /**
     * 评论成功
     *
     * @param res
     */
    public void commentDetailSuccess(HttpJsonResponse res) {
        // 成功后清空保存的数据
        SYSharedPreferences.getInstance().putString(mUrl + "_text", "");
        SYSharedPreferences.getInstance().putString(mUrl + "_img", "");
        CommentsForCircleAndNews published = circleFollowDialog
                .getPublishPosts();
        long comment_id = 0;
        try {
            comment_id = res.getBody().get("comment_id").getAsLong();
        } catch (Exception e) {
//            UIHelper.ToastMessage(mContext, "评论失败");
            circleFollowDialog.dismissProcessDialog();
            return;
        }
        UIHelper.ToastMessage(mContext, R.string.comment_detail_success);
        published.setComment_id(comment_id);
        if (mCircleType == CIRCLE_TYPE_NEWS) {
            published.setImage_url(SYUserManager.getInstance().getImage());
            if (AnoyomousUtils.getAnoyomouState(null)) {
                published.setNickname("匿名用户");
                published.setIs_anonymity(1);
                published.setImage_url("");
            } else if (SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST)) {
                published.setNickname(getResources().getString(R.string.user_guest));
            } else {
                published.setNickname(SYUserManager.getInstance().getName());
            }
        } else {
            if (SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST)) { //如果是游客显示默认头像和"游客"
                published.setNickname(StringUtils.isNotEmpty(mNickName) ? mNickName : getResources().getString(R.string.user_guest));
                published.setImage_url("");
            } else if (mOptionRoleType == Constant.ROLE_NONE) {  //登录的非圈成员，显示搜悦昵称和搜悦头像
                published.setImage_url(StringUtils.isNotEmpty(mImageUrl) ? mImageUrl : SYUserManager.getInstance().getImage());
                published.setNickname(StringUtils.isNotEmpty(mNickName) ? mNickName : SYUserManager.getInstance().getName());
            } else {
                published.setImage_url(StringUtils.isNotEmpty(mImageUrl) ? mImageUrl : "");
                published.setNickname(StringUtils.isNotEmpty(mNickName) ? mNickName : "匿名用户");
            }
        }
        published.setGood_num("0");
        published.setSrp_id(mSrpId);
        published.setType(mDetailType + 1);
        published.setRole(mOptionRoleType);
        published.setIs_current_comment(1);
        circleFollowDialog.dismissProcessDialog();
        mPostsList.add(mPostsListHot.size(), published);
        adapter.notifyDataSetChanged();
        Log.e("comment", "add comment...");
        mHaveNoComment.setVisibility(View.INVISIBLE);
        circleFollowDialog.setEditText("");
        circleFollowDialog.dismiss();

        //统计用
        UpEventAgent.onNewsComment(mContext, mChannel, mKeyword, mSrpId,
                mTitle, mUrl);
    }
}
