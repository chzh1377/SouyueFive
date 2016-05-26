package com.zhongsou.souyue.circle.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.circle.activity.PublishActivity;
import com.zhongsou.souyue.circle.model.CircleIndexMenuInfo;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CircleExitCircleRequest;
import com.zhongsou.souyue.net.circle.InterestSubscriberReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.ActivityUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bob zhou
 * on 15-1-8
 * Description:兴趣圈首页弹出菜单
 */
public class CircleIndexPopupMenu extends PopupWindow implements View.OnClickListener{
//    public static final int CIRCLEEXITCIRCLE_REQUESTID = 4654;
    private final IVolleyResponse mResponse; // 请求回调
    private Context cx;

    private CircleIndexMenuInfo menuInfo;

    private LinearLayout postLayout;

    private LinearLayout myPostLayout;

    private LinearLayout myLayout;

    private LinearLayout atmeLayout;

    private LinearLayout rpme_layout;

    private LinearLayout memberLayout;

    private LinearLayout circleLayout;

    private LinearLayout toolLayout;

    private LinearLayout shortcutLayout;

    private LinearLayout subscribeLayout;

    private LinearLayout exitLayout;

    private TextView memberCountTv;

    private TextView atmeCountTv;

    private TextView rpmeCountTv;
    private String nickName;
    
    private Bitmap mBitmap;

    public static final double MEMBER_COUNT_W = 10000d;
    private View mUnderLineView;


    public CircleIndexPopupMenu(Context context, CircleIndexMenuInfo menuInfo, IVolleyResponse response) {
        super(context);
        this.mResponse = response;
        this.cx = context;
        this.menuInfo = menuInfo;
        init();
    }


    private void init() {
        LayoutInflater inflater = (LayoutInflater) cx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.circle_index_popup_menu, null);
        setContentView(contentView);
        initView(contentView);
        bindListener();
        //5.05 更改布局.
        this.setWidth(cx.getResources().getDimensionPixelSize(R.dimen.space_210));
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        setOutsideTouchable(true);
    }


    private void initView(View view) {
        mUnderLineView = view.findViewById(R.id.circle_popwindow_lastunderline);
        postLayout = (LinearLayout) view.findViewById(R.id.ntool_post_layout);
        myPostLayout = (LinearLayout) view.findViewById(R.id.ntool_mypost_layout);
        myLayout = (LinearLayout) view.findViewById(R.id.ntool_my_layout);
        atmeLayout = (LinearLayout) view.findViewById(R.id.ntool_atme_layout);
        rpme_layout = (LinearLayout) view.findViewById(R.id.ntool_rpme_layout);
        memberLayout = (LinearLayout) view.findViewById(R.id.ntool_member_layout);
        circleLayout = (LinearLayout) view.findViewById(R.id.ntool_circle_layout);
        toolLayout = (LinearLayout) view.findViewById(R.id.ntool_tool_layout);
        shortcutLayout = (LinearLayout) view.findViewById(R.id.ntool_shortcut_layout);
        subscribeLayout = (LinearLayout) view.findViewById(R.id.ntool_subscribe_layout);
        exitLayout = (LinearLayout) view.findViewById(R.id.ntool_exit_layout);
        memberCountTv = (TextView) view.findViewById(R.id.ntool_member_count_tv);
        atmeCountTv = (TextView) view.findViewById(R.id.ntool_atme_count_tv);
        rpmeCountTv = (TextView) view.findViewById(R.id.ntool_rpme_count_tv);
        if (menuInfo.isAdmin()) {
            toolLayout.setVisibility(View.VISIBLE);
        }

        if (menuInfo.getMemberCount() > MEMBER_COUNT_W) {
            double memberCount = menuInfo.getMemberCount() / MEMBER_COUNT_W;
            DecimalFormat df = new DecimalFormat("0.0");
            memberCountTv.setText(df.format(memberCount) + "万");
        } else {
            memberCountTv.setText(menuInfo.getMemberCount() + "");
        }

        if (menuInfo.getAtCount() > 0) {
            atmeCountTv.setText(menuInfo.getAtCount() + "");
        } else {
            atmeCountTv.setVisibility(View.GONE);
        }

        //回复我的右侧红色textView  如果大于0则显示数字否则不显示
        if (menuInfo.getFollowMyCount() > 0) {
            rpmeCountTv.setText(menuInfo.getFollowMyCount() + "");
        } else {
            rpmeCountTv.setVisibility(View.GONE);
        }


    }

    public void showTopDialog(View parent, int height) {
        if (!this.isShowing()) {
            //update at souyue 5.05
            //by zhangyanwei
//            this.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 20, height);
            this.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 15, height);
        } else {
            this.dismiss();
        }

    }


    private void bindListener() {
        postLayout.setOnClickListener(this);
        myPostLayout.setOnClickListener(this);
        myLayout.setOnClickListener(this);
        atmeLayout.setOnClickListener(this);
        rpme_layout.setOnClickListener(this);
        memberLayout.setOnClickListener(this);
        circleLayout.setOnClickListener(this);
        toolLayout.setOnClickListener(this);
        shortcutLayout.setOnClickListener(this);
        subscribeLayout.setOnClickListener(this);
        exitLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ntool_post_layout:
                UIHelper.showPublish((Activity) cx, null, menuInfo.getInterestId(), menuInfo.getSrpId(),menuInfo.getKeyword(), PublishActivity.PUBLISH_TYPE_M_NEW,nickName);
                break;
            case R.id.ntool_mypost_layout:  //我的圈内发帖
                UIHelper.showMyPost((Activity) cx,menuInfo.getInterestId());
                break;
            case R.id.ntool_my_layout:
                UIHelper.showCircleMemberSetting((Activity) cx, menuInfo.getInterestId(), menuInfo.getInterestType(), CircleIndexActivity.REQUEST_CODE_MEMBER_SETTING_ACTIVITY);
                break;
            case R.id.ntool_atme_layout: //@我的
                menuInfo.setAtCount(0);
                atmeCountTv.setVisibility(View.GONE);
                UIHelper.showAtMeSetting((Activity) cx, menuInfo.getInterestId());
                break;
            case R.id.ntool_member_layout:
                UIHelper.showCircleMemberList((Activity) cx, menuInfo.getInterestId(), menuInfo.getInterestLogo(), menuInfo.getInterestName(), menuInfo.getInterestType(), menuInfo.getSrpId());
                break;

            case R.id.ntool_circle_layout:
                IntentUtil.gotoSecretCricleCard(cx, menuInfo.getInterestId(), 1);
                break;
            case R.id.ntool_tool_layout:
                IntentUtil.gotoWeb(cx, UrlConfig.CIRCLE_TOOL + "?uid=" + SYUserManager.getInstance().getUser().userId()
                        + "&cid=" + menuInfo.getInterestId() + "&souyue_version=" + DeviceInfo.getAppVersion()
                        + "&token=" + SYUserManager.getInstance().getToken() + "&srpid=" + menuInfo.getSrpId(), "nopara");
                break;
            case R.id.ntool_rpme_layout://回复我的  返回会自动更新数据
                menuInfo.setFollowMyCount(0);
                rpmeCountTv.setVisibility(View.GONE);
                IntentUtil.gotoReplyMe(cx, menuInfo.getInterestId() + "");
                break;
            case R.id.ntool_shortcut_layout:
                Map<String, Object> extras = new HashMap<String, Object>();
                extras.put("srp_id", menuInfo.getSrpId());
                extras.put("keyword", menuInfo.getKeyword());
                extras.put("interest_name", menuInfo.getInterestName());
                extras.put("interest_logo", menuInfo.getInterestLogo());
                extras.put("from", "shortcut");
//                Bitmap bitmap = new AQuery(cx).getCachedImage(menuInfo.getInterestLogo());
            	File cacheFile = ImageLoader.getInstance().getDiskCache().get(menuInfo.getInterestLogo());
            	if(cacheFile != null) {
            		mBitmap =  BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            	}
                if (mBitmap != null) {
                	mBitmap = ImageUtil.getRoundCornerRect(mBitmap, 18, true); //魔数18，圆角半径，采用18比较合适
                	mBitmap = ImageUtil.zoomImg(mBitmap, CircleUtils.dip2px(cx, 45), CircleUtils.dip2px(cx, 45));     //魔数45,经过测试，比较合适，且对各个大小的屏幕兼容性较好
                }
                ActivityUtils.addShortCut(cx, "com.zhongsou.souyue.circle.activity.CircleIndexActivity", mBitmap, menuInfo.getInterestName(), extras);
                break;
            case R.id.ntool_subscribe_layout:
//                Http http = new Http(cx);
//                Map<String, Object> params = new HashMap<String, Object>();
//                params.put("token", SYUserManager.getInstance().getToken());
//                params.put("interest_ids", menuInfo.getInterestId());
//                http.saveRecomentCircles(params);
                loadSaveRecomentCircles(SYUserManager.getInstance().getToken(), menuInfo.getInterestId()+"");
                break;
            case  R.id.ntool_exit_layout:
//                AlertDialog.Builder builder = new AlertDialog.Builder(cx);
//                builder.setTitle(R.string.cricle_manage_upload_quit_dialog_title);
//                builder.setMessage(R.string.cricle_manage_upload_quit_dialog_content);
//                builder.setPositiveButton(
//                        R.string.cricle_manage_edit_quit_dialog_confirm,
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
////                                CircleExitCircleRequest circleExitCircle = new CircleExitCircleRequest(CIRCLEEXITCIRCLE_REQUESTID, mResponse);
////                                circleExitCircle.setParams(menuInfo.getInterestId(), SYUserManager.getInstance().getToken());
////                                CMainHttp.getInstance().doRequest(circleExitCircle);
//                                CircleExitCircleRequest.send(HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID,mResponse,menuInfo.getInterestId(), SYUserManager.getInstance().getToken(),ZSSdkUtil.CIRCLEINDEX_SUBSCRIBE_GROUP);
////                                Http http1 = new Http(cx);
////                                http1.updateQuitCricle(menuInfo.getInterestId(), SYUserManager.getInstance().getToken());
//                            }
//                        });
//                builder.setNegativeButton(
//                        R.string.cricle_manage_edit_quit_dialog_cancel,
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                builder.create().show();
                CircleExitCircleRequest.send(HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID,mResponse,menuInfo.getInterestId(), SYUserManager.getInstance().getToken(),ZSSdkUtil.CIRCLEINDEX_SUBSCRIBE_GROUP);

                break;
            default:
                break;

        }
        dismiss();
    }

    /**
     *  加载数据-----订阅兴趣圈
     * @param token
     * @param interest_id
     */
    private void loadSaveRecomentCircles(String token,String interest_id)
    {
        InterestSubscriberReq req =  new InterestSubscriberReq(HttpCommon.CIRLCE_INTEREST_SUB_ID_2,mResponse);
        req.setParams(token,interest_id, ZSSdkUtil.OTHER_SUBSCRIBE_MENU);
        CMainHttp.getInstance().doRequest(req);

    }

    /**
     *
     * 控制菜单子项的隐藏和显示
     * 其中：圈成员，圈名片/分享，创建桌面快捷方式为常驻项，且不论任何情况都显示
     *
     * @param role  role代表圈角色  0-非圈子成员 1-圈主 2-圈子普通成员 3-游客, 初始化给一个无意义的值-1
     */
    public void updateMenuItem(int role) {
        String type = SYUserManager.getInstance().getUser().userType();
        mUnderLineView.setVisibility(View.GONE);
        if(role > 0){           //圈成员
            if(SYUserManager.USER_GUEST.equals(type)){      //游客
                postLayout.setVisibility(View.GONE);
                myPostLayout.setVisibility(View.GONE);
                myLayout.setVisibility(View.GONE);
                atmeLayout.setVisibility(View.GONE);
                rpme_layout.setVisibility(View.GONE);
                toolLayout.setVisibility(View.GONE);
                exitLayout.setVisibility(View.VISIBLE);
            }else{
                postLayout.setVisibility(View.VISIBLE);
                myPostLayout.setVisibility(View.VISIBLE);
                myLayout.setVisibility(View.VISIBLE);
                atmeLayout.setVisibility(View.VISIBLE);
                rpme_layout.setVisibility(View.VISIBLE);
                exitLayout.setVisibility(View.VISIBLE);
                if(menuInfo.isAdmin() ){
                    toolLayout.setVisibility(View.VISIBLE);
                    mUnderLineView.setVisibility(View.GONE);
                    exitLayout.setVisibility(View.GONE);
                }
                if( role == Constant.ROLE_SUB_ADMIN){
                    toolLayout.setVisibility(View.VISIBLE);
                    mUnderLineView.setVisibility(View.VISIBLE);
                    exitLayout.setVisibility(View.VISIBLE);
                }
            }
            subscribeLayout.setVisibility(View.GONE);
        }else {         //非圈成员
            postLayout.setVisibility(View.GONE);
            myPostLayout.setVisibility(View.GONE);
            myLayout.setVisibility(View.GONE);
            atmeLayout.setVisibility(View.GONE);
            rpme_layout.setVisibility(View.GONE);
            toolLayout.setVisibility(View.GONE);
            subscribeLayout.setVisibility(View.VISIBLE);
            exitLayout.setVisibility(View.GONE);
        }
    }

    public void setNickName(String nickName){
        this.nickName = nickName;
    }
}
