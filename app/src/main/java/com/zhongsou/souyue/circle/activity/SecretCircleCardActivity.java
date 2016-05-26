package com.zhongsou.souyue.circle.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.PickerMethod;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.model.CircleCardInfo;
import com.zhongsou.souyue.circle.model.RelationInfo;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.view.CircleAskForDialog;
import com.zhongsou.souyue.circle.view.CircleIndexPopupMenu;
import com.zhongsou.souyue.circle.view.InterestDialog;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.InterestBean;
import com.zhongsou.souyue.module.ResponseObject;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CircleGetCircleInfoRequest;
import com.zhongsou.souyue.net.circle.CircleGetUserRelationShipRequest;
import com.zhongsou.souyue.net.circle.InterestSubscriberReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.ChangeSelector;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.view.ShareSNSDialog;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : zoulu
 *         2014年7月11日
 *         下午2:42:04
 *         类说明 :兴趣圈圈名片
 */
@SuppressLint("ClickableViewAccessibility")
public class SecretCircleCardActivity extends BaseActivity implements OnClickListener, PickerMethod, IShareContentProvider {
//    private static final int CIRCLEGETCIRCLEINFO_REQUESTID = 32156;// 请求全名片信息
//    private static final int CIRCLEGETRELATION_REQUESTID = 9848489; // 获取用户和圈子之间的关系
    private ImageButton btn_circle_edit;
    private ImageButton btn_circle_option;
    private ImageView iv_secret_mark;
    private ImageView iv_secret_logo;
    private TextView tv_sercet_name, tv_sercet_createtime, tv_sercet_owner, member_number, post_number, tv_sercet_content, activity_bar_title;
    private RelativeLayout re_erweima;
    private Button btn1;
    private Button btn2;
    private long interest_id;
    private boolean isPrivate;
    public static final String INTEREST_ID = "interest_id";
    public static final String DIALOGTYPE = "dialog_type";
    private CircleAskForDialog askForDialog;
    private String name;
    private String interest_desc;  //圈简介
    private User user;
    //	public static final String Intent_Broadcast = "com.zhongsou.souyue.ManagerGrid";
    private InterestDialog interestDialog;
    private RelationInfo info;
    private String imageUrl;
    private ScrollView content_scrollview;
    /**
     * 申请者和圈子关系  1:待审核 2：已拒绝 3：已通过 4：从未申请
     */
    private int mstatus;
    private int dialogType;//1 表示分享按钮
    //分享内容
    private SsoHandler mSsoHandler;
    private ShareSNSDialog dialog;
    private List<Integer> integers = new ArrayList<Integer>();
    private Bitmap imageBitmap;
    private String openUrl;//落地页
    private ShareContent content;
    private String new_srpid;
    private String srp_word;

    private int role;  //为了配合后台私密圈表和圈成员表最统一

    private SuberDao suberDao;

    private ProgressBarHelper progress;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.circle_activity_secret_card);
        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        progress = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
        progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
//                http.getCircleCardInfomation(interest_id, 1);
                CircleGetCircleInfoRequest.send(HttpCommon.CIRCLE_GETCIRCLEINFO_REQUESTID, SecretCircleCardActivity.this, interest_id, Constant.INTEREST_TYPE_PRIVATE);
            }
        });
        progress.showLoading();
        dialogType = getIntent().getIntExtra(DIALOGTYPE, -1);
        interest_id = getIntent().getLongExtra(INTEREST_ID, 0);
        btn_circle_edit = (ImageButton) findViewById(R.id.btn_cricle_edit);
        btn_circle_edit.setVisibility(View.INVISIBLE);
        btn_circle_option = (ImageButton) findViewById(R.id.btn_cricle_option);
        btn_circle_option.setVisibility(View.GONE);
        btn_circle_option.setOnClickListener(this);
        iv_secret_mark = (ImageView) findViewById(R.id.iv_secret_mark);
        iv_secret_logo = (ImageView) findViewById(R.id.iv_secret_logo);
        tv_sercet_name = (TextView) findViewById(R.id.tv_sercet_name);
        tv_sercet_createtime = (TextView) findViewById(R.id.tv_sercet_createtime);
        tv_sercet_owner = (TextView) findViewById(R.id.tv_sercet_owner);
        member_number = (TextView) findViewById(R.id.member_number);
        post_number = (TextView) findViewById(R.id.post_number);
        tv_sercet_content = (TextView) findViewById(R.id.tv_sercet_content);
        tv_sercet_content.setMovementMethod(new ScrollingMovementMethod());
        re_erweima = (RelativeLayout) findViewById(R.id.re_erweima);
        activity_bar_title = (TextView) findViewById(R.id.activity_bar_title);
        content_scrollview = (ScrollView) findViewById(R.id.content_scrollview);
        content_scrollview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                tv_sercet_content.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        tv_sercet_content.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                arg0.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        activity_bar_title.setText("圈名片");
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        re_erweima.setOnClickListener(this);
    }

    private void initData() {
        user = SYUserManager.getInstance().getUser();
        suberDao = new SuberDaoImp();
//		http.getCircleCardInfomation(interest_id, 1);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        http.getCircleCardInfomation(interest_id, 1);
        CircleGetCircleInfoRequest.send(HttpCommon.CIRCLE_GETCIRCLEINFO_REQUESTID, this, interest_id, Constant.INTEREST_TYPE_PRIVATE);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.re_erweima:
                IntentUtil.gotoCircleQRcode(this, name, interest_id, imageUrl);
                break;
            case R.id.btn1:
                if (dialogType == 1) {
                    showShareWindow(arg0);
                } else {
                    switch (mstatus) {
                        case 1:
                            askForDialog = new CircleAskForDialog(this, this, 1, interest_id, info.getApplicant_nickname(), info.getApply_content(), info.getRefuse_content());
                            askForDialog.showDialog();
                            break;
                        case 2:
                            askForDialog = new CircleAskForDialog(this, this, 3, interest_id, info.getApplicant_nickname(), info.getApply_content(), info.getRefuse_content());
                            askForDialog.showDialog();
                            break;
                        case 3: //是圈成员的时候才显示进入圈 V5.0为了防止私密圈在圈名片和圈首页中进行死循环跳转。
                            //直接进入圈子
                            if (role != 0) {
                                UIHelper.showCircleIndex(SecretCircleCardActivity.this, new_srpid, srp_word, name, imageUrl);
                                return;
                            }
                            askForDialog = new CircleAskForDialog(this, this, 1, interest_id, info.getApplicant_nickname(), info.getApply_content(), info.getRefuse_content());
                            askForDialog.showDialog();
                            //InterestBean bean = new InterestBean();
                            //bean.setId((int)interest_id);
                            //doSendLocalBroadcast(bean);
                            break;
                        case 4:
                            //先判断是否私密圈  (YES--> 判断是否登录   NO--> 登录  YES--> 弹申请框) (NO-->订阅并进首页)
                            if (isPrivate) {
                                if (user != null && SYUserManager.USER_ADMIN.equals(user.userType())) {
                                    askForDialog = new CircleAskForDialog(this, this, 2, interest_id,
                                            info.getApplicant_nickname(), info.getApply_content(), info.getRefuse_content());
                                    askForDialog.showDialog();
                                } else {
                                    Intent intent = new Intent();
                                    intent.setClass(this, LoginActivity.class);
                                    intent.putExtra(LoginActivity.Only_Login, true);
                                    startActivityForResult(intent, 1001);
                                }
                            } else {
                                //订阅普通圈子  并进入首页刷新
                                interestDialog = new InterestDialog(SecretCircleCardActivity.this);
                                interestDialog.show();
                                interestDialog.progress();
//                                Map<String, Object> params = new HashMap<String, Object>();
//                                params.put("token", SYUserManager.getInstance().getToken());
//                                params.put("interest_ids", interest_id);
//                                http.saveRecomentCircles(params);
                                loadSaveRecomentCircles( SYUserManager.getInstance().getToken(),interest_id+"");
                            }
                            break;

                        default:
                            break;
                    }
                    break;
                }
            case R.id.btn2:
                switch (mstatus) {
                    case 2:
                        askForDialog = new CircleAskForDialog(this, this, 2, interest_id, info.getApplicant_nickname(), info.getApply_content(), info.getRefuse_content());
                        askForDialog.showDialog();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 加载数据-----订阅兴趣圈
     *
     * @param token
     * @param interest_id
     */
    private void loadSaveRecomentCircles(String token, String interest_id) {
        InterestSubscriberReq req = new InterestSubscriberReq(HttpCommon.CIRLCE_INTEREST_SUB_ID, this);
        req.setParams(token, interest_id, ZSSdkUtil.CIRCLEINDEX_SUBSCRIBE_GROUP);
        CMainHttp.getInstance().doRequest(req);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                saveRecomentCirclesSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRCLE_GETCIRCLEINFO_REQUESTID:
                getCircleCardInfomationSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRCLE_GETUSERRELATIONSHIP_REQUESTID:
                getRelationWithCircleSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRCLE_APPLY_REQUEST:
                applyForSecretCircleSuccess(request.<HttpJsonResponse>getResponse());
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()) {
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                if(interestDialog!=null)
                interestDialog.dismiss();
                break;
            case HttpCommon.CIRCLE_GETCIRCLEINFO_REQUESTID:
                iv_secret_mark.setVisibility(View.GONE);
                progress.showNetError();
                break;
            case HttpCommon.CIRCLE_APPLY_REQUEST:
                Toast.makeText(this, "申请失败，请重试", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.default_logo) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.default_logo) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.default_logo) // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.default_logo).cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(0))
            .build();

    public void getCircleCardInfomationSuccess(HttpJsonResponse resp) {
        CircleCardInfo cardInfo = new Gson().fromJson(resp.getBody(),
                CircleCardInfo.class);
        if (cardInfo.getInterest_type() == Constant.INTEREST_TYPE_NORMAL) {//0普通 1私密
            iv_secret_mark.setVisibility(View.GONE);
            isPrivate = false;
        } else {
            iv_secret_mark.setVisibility(View.VISIBLE);
            isPrivate = true;
        }
        imageUrl = cardInfo.getInterest_logo();
        if (StringUtils.isNotEmpty(imageUrl)) {
            iv_secret_logo.setScaleType(ImageView.ScaleType.FIT_XY);
            // aQuery.id(iv_secret_logo).image(imageUrl, true, true,0,R.drawable.default_logo);

            PhotoUtils.showCard(PhotoUtils.UriType.HTTP, imageUrl, iv_secret_logo, MyDisplayImageOption.options);
        }
        tv_sercet_name.setText(cardInfo.getInterest_name());
        name = cardInfo.getInterest_name();
        interest_desc = cardInfo.getInterest_desc();
        new_srpid = cardInfo.getNew_srpid();
        srp_word = cardInfo.getSrp_word();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = sdf.parse(cardInfo.getCreate_time());
        } catch (Exception e) {

        }
        tv_sercet_createtime.setText(StringUtils.convertDate(String.valueOf(d.getTime())) + "");
        tv_sercet_owner.setText(cardInfo.getMaster_nickname() + "");
        post_number.setText("帖子数量：" + cardInfo.getMblog_count() + "");
        if (cardInfo.getUsr_count() > CircleIndexPopupMenu.MEMBER_COUNT_W) {
            double memberCount = cardInfo.getUsr_count() / CircleIndexPopupMenu.MEMBER_COUNT_W;
            DecimalFormat df = new DecimalFormat("0.0");
            member_number.setText("成员：" + df.format(memberCount) + "万");
        } else {
            member_number.setText("成员：" + cardInfo.getUsr_count());
        }
        if (TextUtils.isEmpty(cardInfo.getInterest_desc()))
            tv_sercet_content.setVisibility(View.GONE);
        else
            tv_sercet_content.setText(cardInfo.getInterest_desc());
//        http.getRelationWithCircle(interest_id, SYUserManager.getInstance().getToken());
        CircleGetUserRelationShipRequest.send(HttpCommon.CIRCLE_GETUSERRELATIONSHIP_REQUESTID, this, interest_id, SYUserManager.getInstance().getToken());
        progress.goneLoading();
    }

    @SuppressWarnings("deprecation")
    public void getRelationWithCircleSuccess(HttpJsonResponse response) {
        mstatus = response.getBody().get("audit_status").getAsInt();
        role = response.getBody().get("role").getAsInt();
        info = new RelationInfo();
        if (dialogType == 1) {
            btn1.setVisibility(View.VISIBLE);
            btn1.setBackgroundDrawable(ChangeSelector.addStateDrawable(this, R.drawable.circleshow, R.drawable.circleshowclick, R.drawable.circleshowclick));
            btn1.setTextColor(Color.WHITE);
            btn1.setText("分享");
            btn2.setVisibility(View.GONE);
            return;
        }
        switch (mstatus) {
            case 1:
                btn1.setVisibility(View.VISIBLE);
                btn1.setBackgroundDrawable(ChangeSelector.addStateDrawable(this, R.drawable.verify, R.drawable.verify, R.drawable.verify));
                btn1.setTextColor(Color.GRAY);
                btn1.setText("申请审核中");
                info = new Gson().fromJson(response.getBody().get("info"), RelationInfo.class);
                btn2.setVisibility(View.GONE);
                break;
            case 2:
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn1.setBackgroundDrawable(ChangeSelector.addStateDrawable(this, R.drawable.refuse, R.drawable.verify, R.drawable.verify));
                btn2.setBackgroundDrawable(ChangeSelector.addStateDrawable(this, R.drawable.circleshow, R.drawable.verify, R.drawable.verify));
                btn1.setTextColor(Color.WHITE);
                btn2.setTextColor(Color.WHITE);
                btn1.setText("申请被拒绝");
                btn2.setText("重新申请");
                info = new Gson().fromJson(response.getBody().get("info"), RelationInfo.class);
                break;
            case 3:  //是圈成员的时候才显示进入圈 V5.0为了防止私密圈在圈名片和圈首页中进行死循环跳转。
                if (role != 0) {
                    btn1.setVisibility(View.VISIBLE);
                    btn1.setBackgroundDrawable(ChangeSelector.addStateDrawable(this, R.drawable.circleshow, R.drawable.verify, R.drawable.verify));
                    btn1.setTextColor(Color.WHITE);
                    btn1.setText("直接进入圈");
                    btn2.setVisibility(View.GONE);
                    //直接跳到圈子首页
                    UIHelper.showCircleIndex(SecretCircleCardActivity.this, new_srpid, srp_word, name, imageUrl);
                    finish();
                    return;
                }
                btn1.setVisibility(View.VISIBLE);
                btn1.setBackgroundDrawable(ChangeSelector.addStateDrawable(this, R.drawable.verify, R.drawable.verify, R.drawable.verify));
                btn1.setTextColor(Color.GRAY);
                btn1.setText("申请审核中");
                info = new Gson().fromJson(response.getBody().get("info"), RelationInfo.class);
                btn2.setVisibility(View.GONE);
                break;
            case 4:
                btn1.setVisibility(View.VISIBLE);
                btn1.setBackgroundDrawable(ChangeSelector.addStateDrawable(this, R.drawable.circleshow, R.drawable.verify, R.drawable.verify));
                btn1.setTextColor(Color.WHITE);
                btn2.setVisibility(View.GONE);
                User user = SYUserManager.getInstance().getUser();
                if (user != null && SYUserManager.USER_ADMIN.equals(user.userType()))
                    btn1.setText("加入该圈");
                else if (isPrivate) {
                    btn1.setText("进入圈");     //私密圈“进入圈”，公开圈“加入该圈”
                } else {
                    btn1.setText("加入该圈");
                }
                break;
            default:
                break;
        }
    }

    public void applyForSecretCircleSuccess(HttpJsonResponse response) {
        Toast.makeText(this, "申请已提交，请等待圈主审核！", Toast.LENGTH_SHORT).show();
        if (response.getBody().get("result").getAsBoolean()) {
//            http.getRelationWithCircle(interest_id, SYUserManager.getInstance().getToken());
            CircleGetUserRelationShipRequest.send(HttpCommon.CIRCLE_GETUSERRELATIONSHIP_REQUESTID, this, interest_id, SYUserManager.getInstance().getToken());
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
//		if (arg1 == -1) {
//			user = SYUserManager.getInstance().getUser();
//			http.getRelationWithCircle(interest_id, SYUserManager.getInstance().getToken());
//		}
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(arg0, arg1, arg2);
        }
        // 此处，在登录后，返回的user  更新一下user信息
        if (arg0 == 1001) {
            user = SYUserManager.getInstance().getUser();
        }

    }

    private void doSendLocalBroadcast(ResponseObject obj) {
//        Intent imIntent = new Intent(ManagerGridActivity.Intent_Broadcast);
//        imIntent.putExtra("OBJ", obj);
//        //超 A不发送此广播
//        if (ConfigApi.isSouyue()) {
//            LocalBroadcastManager.getInstance(this).sendBroadcast(imIntent);
//        }
//        //ZhongSouActivityMgr.getInstance().goHome();
        UIHelper.showCircleIndex(SecretCircleCardActivity.this, new_srpid, srp_word, name, imageUrl);
        finish();
    }

    public void saveRecomentCirclesSuccess(HttpJsonResponse res) {
        if (res.getBody().get("state").getAsInt() == 1) {
            SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
            if (interestDialog != null)
                interestDialog.subscribeRightNow();
            InterestBean bean = new InterestBean();
            bean.setId((int) interest_id);
            doSendLocalBroadcast(bean);

            // 统计
            UpEventAgent.onGroupJoin(this, interest_id + "." + "", "");

            //数据库操作
            SuberedItemInfo info = new SuberedItemInfo();
            info.setId(interest_id);
            info.setTitle(srp_word);
            info.setCategory("interest");
            info.setImage(imageUrl);
            info.setSrpId(new_srpid);
            info.setKeyword(srp_word);
            info.setType("0");
            suberDao.addOne(info);
        } else {
            if (interestDialog != null)
                interestDialog.subscribefailRightNow();
        }

        setBackData(1, interest_id); //设置返回数据
    }

    private void setBackData(int result, long interestId){
        //设置返回数据
        Intent backIntent = new Intent();
        backIntent.putExtra("result", result);
        backIntent.putExtra("interest_id", interestId);
        setResult(DetailModuleActivity.CIRCLE_REQUEST_CODE,backIntent);
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        if ("saveRecomentCircles".equals(methodName)) {
//            interestDialog.dismiss();
//        }
//
//    }

    private void showShareWindow(View parent) {
        integers.clear();
        integers.add(9);
        integers.add(1);
//		if(ConfigApi.isSouyue()){
//		    integers.add(6);
//		}
        if (StringUtils.isNotEmpty(ShareApi.WEIXIN_APP_ID)) {
            integers.add(2);
            integers.add(3);
        }
        integers.add(7);
        integers.add(4);

        integers.add(11);
        integers.add(12);
        dialog = new ShareSNSDialog(this, this, integers);
        dialog.showBottonDialog();
    }

    @Override
    public ShareContent getShareContent() {
        // TODO Auto-generated method stub
        if (!TextUtils.isEmpty(imageUrl)) {
            File bitmapFile = PhotoUtils.getImageLoader().getDiskCache().get(imageUrl);
            if(bitmapFile != null){
                imageBitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
            }
        } else {
            imageBitmap = null;
        }
        openUrl = UrlConfig.shareInterestCard + interest_id + CommonStringsApi.getUrlAppendIgId();
        ShareContent result = new ShareContent(name, openUrl, imageBitmap, name, imageUrl);
        result.setSharePointUrl(openUrl);
        result.setKeyword("");
        result.setSrpId("");
        return result;
    }

    @Override
    public void loadData(int args) {
        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(), getString(R.string.sdcard_exist), Toast.LENGTH_SHORT).show();
            return;
        }
        if (CMainHttp.getInstance().isNetworkAvailable(this)) {
            content = getShareContent();
            switch (args) {
                case ShareSNSDialog.SHARE_TO_SYIMFRIEND:
                    boolean islogin = (SYUserManager.getInstance().getUser().userType().equals(SYUserManager.USER_ADMIN));
                    if (islogin) {
                        UIHelper.showImFriend(SecretCircleCardActivity.this, interest_id, true, imageUrl, name, null, 4, false, content.getSharePointUrl(), String.valueOf(interest_id));

                    } else {
                        IntentUtil.gotoLogin(this);
                    }
                    break;
                case ShareSNSDialog.SHARE_TO_SINA:
                    mSsoHandler = ShareByWeibo.getInstance().share(this, content);
                    break;
                case ShareSNSDialog.SHARE_TO_WEIX:
                    content.setContent(name + "  " + interest_desc);
                    ShareByWeixin.getInstance().share(content, false);
                    break;
                case ShareSNSDialog.SHARE_TO_FRIENDS:
                    content.setContent(name + "  " + interest_desc);
                    ShareByWeixin.getInstance().share(content, true);
                    break;
                case ShareSNSDialog.SHARE_TO_QQFRIEND://4.1.1新增分享qq好友
                    ShareByTencentQQ.getInstance().share(this, content);
                    break;
                case ShareSNSDialog.SHARE_TO_QQZONE://4.1.1新增分享qq空间
                    ShareByTencentQQZone.getInstance().share(this, content);
                    break;
                default:
                    break;
            }
        }
    }

}
