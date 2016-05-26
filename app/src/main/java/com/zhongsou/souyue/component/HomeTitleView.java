package com.zhongsou.souyue.component;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;
import com.zhongsou.souyue.utils.IntentUtil;

/**
 * Created by Administrator on 2014/8/28.
 * mailto:wzyax@qq.com
 */
public class HomeTitleView extends RelativeLayout implements View.OnClickListener {
    //    private TextView username, userlevel, nologin;
    private Context mContext;
    private Fragment mFragment;
    //    private ImageView userhead;
//    private Http http;
    private User user;

    //CP方式取登录用户的URI：ContentResolver.query
    public static final Uri CONTENT_URI = Uri.parse("content://com.witcool.provider.users/users");
    //字段名：用户id
    public static final String USERID = "userid";
    //字段名：用户昵称
    public static final String NICKNAME = "nickname";
    //字段名：用户头像
    public static final String USERHEAD = "head";

    //该数组中包含了所有要返回的字段
    String columns[] = new String[]{USERID, NICKNAME, USERHEAD};

    public HomeTitleView(final Context _context) {
        super(_context);
        this.mContext = _context;
    }

    public HomeTitleView(final Context _context, AttributeSet attrs) {
        super(_context, attrs);
        this.mContext = _context;
    }

    public HomeTitleView(final Context _context, AttributeSet attrs, int defStyle) {
        super(_context, attrs, defStyle);
        this.mContext = _context;
    }


    public void init(Fragment activity) {
        mFragment = activity;
//        http = new Http(this);
//        http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE_FORCE);
//        username = (TextView) findViewById(R.id.username);
//        userhead = (ImageView) findViewById(R.id.userhead);
//        userlevel = (TextView) findViewById(R.id.userlevel);
//        tv_userzsb = (TextView) findViewById(R.id.tv_userzsb);
//        nologin = (TextView) findViewById(R.id.nologin);
        setAllViewClickListener(this);
//        updataUserName();
    }

    /**
     * 给所有子view加上点击事件
     *
     * @param parent
     */
    private void setAllViewClickListener(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {

//                if (child.getId() == R.id.ll_userlevel) {
//                    child.setOnClickListener(this);
//                    continue;
//                }
                child.setOnClickListener(this);
                setAllViewClickListener((ViewGroup) child);
            }
//            else if (child != null) {
//                child.setOnClickListener(this);
//            }
        }
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
//        if (visibility == VISIBLE) {
//            updataUserName();
//        }
    }
//
//    public void updataUserName() {
//        String imageUrl = null;
//        if (isLogin()) {
//            nologin.setVisibility(GONE);
//
////            http.integral(user.userName());
//            initIntegral();
//            username.setText(user.name());
//            imageUrl = user.image();
//            MyImageLoader.imageLoader.displayImage(imageUrl, userhead, MyImageLoader.options);
//        } else {
//            if (http.isNetworkAvailable()&&"一品白客".equals((DeviceInfo.getUmengChannel(MainApplication.getInstance())))) {//一品白客
//                getYiPinBaiKeData();
//            } else {
//                username.setText(mContext.getResources().getString(R.string.home_user_state));
//                userhead.setImageResource(R.drawable.default_head);
//                viewInvisible();
//            }
//        }
//    }
//
//    private void initIntegral(){
//        if(http.isNetworkAvailable()){
//            if(user==null||user.getUser_level()==null||user.isExpires()){
//                http.integral(user.userName());
//            }else{
//                if(user!=null)
//                updateUserLevel("Lv" + user.getUser_level() + "-" + user.getUser_level_title());
//            }
//        }else{
//            if(user!=null)
//                updateUserLevel("Lv" + user.getUser_level() + "-" + user.getUser_level_title());
//        }
//
//
//    }
//
//    private void setUserGuest(){
//        username.setText(mContext.getResources().getString(R.string.home_user_state));
//        userhead.setImageResource(R.drawable.default_head);
//        viewInvisible();
//    }
//
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    new LoginUtils(mContext, HomeTitleView.this,HomeTitleView.this).loninYiPinBaiKe((String[]) msg.obj);
//                    break;
//                case 1:
//                    setUserGuest();
//                    break;
//            }
//        }
//    };
//    private void getYiPinBaiKeData(){
//         new Thread() {
//            @Override
//            public void run() {
//                String[] strings = QueryContentReslover();
//                if (null != strings) {
//                    Message msg = handler.obtainMessage(0, strings);
//                    handler.sendMessage(msg);
//                }else{
//                    Message msg = handler.obtainMessage(1);
//                    handler.sendMessage(msg);
//                }
//            }
//        }.start();
//    }
//
//    /**
//     * 尝试获取一品白客的contentProvider提供得用户信息
//     *
//     * @return id，昵称，头像地址三个参数
//     */
//    private String[] QueryContentReslover() {
//        Cursor cursor = null;
//        String userid = "";
//        String nickname = "";
//        String head = "";
//        try {
////            cursor= mContext.getContentResolver().query(CONTENT_URI, columns, null, null, null);
//            cursor= mContext.getContentResolver().query(CONTENT_URI, null, null, null, null);
//            while (cursor.moveToNext()) {
//                userid = cursor.getString(cursor.getColumnIndex(USERID));
//                nickname = cursor.getString(cursor.getColumnIndex(NICKNAME));
//                head = cursor.getString(cursor.getColumnIndex(USERHEAD));
//                System.out.println(" cursor "+userid +" "+nickname+" "+head);
//                return new String[]{userid, nickname, head};
//            }
//        } catch (Exception e) {
//        }finally{
//            if(cursor!=null&&!cursor.isClosed()){
//                cursor.close();
//            }
//        }
//        return null;
//
//    }
//
//    private void viewInvisible() {
//        userlevel.setVisibility(GONE);
////        tv_userzsb.setVisibility(GONE);
//        nologin.setVisibility(VISIBLE);
//    }
//
//    private void viewVisible() {
//        userlevel.setVisibility(VISIBLE);
////        tv_userzsb.setVisibility(VISIBLE);
//        nologin.setVisibility(GONE);
//    }
//
//    public void integralSuccess(MyPoints points,AjaxStatus status) {
//        viewVisible();
//        if (points != null) {
//            String cfj = "0";
//            List<JiFen> jifens = points.getScore();
//            for (JiFen jf : jifens) {
//                if (jf.isZSB()) {
//                    cfj = jf.getNum();
//                }
//            }
//            updateUserLevel("Lv" + points.getUserlevel() + "-" + points.getUserleveltitle());
//        }
//    }
//
//    public void updateUserLevel(String level) {
//        userlevel.setVisibility(View.VISIBLE);
//        userlevel.setText(level);
////        tv_userzsb.setText(zsb);
//    }
//
//    private boolean isLogin() {
//        user = SYUserManager.getInstance().getUser();
//        if (!TextUtils.isEmpty(user != null && "1".equals(user.userType()) ? user.name() : null)) {
//            return true;
//        }
//        return false;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_home_subcribe:
//            	openManagerAcitivity(mFragment, MySubscribeListActivity.class, SouyueTabFragment.REQUEST_CODE_TO_SUBCRIBLE);

                HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_SUBSCRIB, mFragment.getActivity(), R.string.manager_grid_subject + "");
                UpEventAgent.onHomeSubscribe(MainApplication.getInstance());
                UmengStatisticUtil.onEvent(MainApplication.getInstance(), UmengStatisticEvent.HOME_SUBSCRIBE_CLICK);   //Umeng
                break;
            case R.id.rl_home_website:
//                IntentUtil.gotoWeb(mContext, UrlConfig.web_nav, "url");
                HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_WEBSITE, mContext, UrlConfig.web_nav, "url");
                UpEventAgent.onHomeWebsite(MainApplication.getInstance());
                UmengStatisticUtil.onEvent(MainApplication.getInstance(), UmengStatisticEvent.HOME_WEB_ADDRESS);   //Umeng
                break;
            case R.id.rl_home_search:
//                IntentUtil.openSearchActivity((Activity) mContext);
//                HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_SEARCH,mContext);
                IntentUtil.gotoSearchAll(mContext, this.getClass().getSimpleName());
                UpEventAgent.onHomeSearch(MainApplication.getInstance());
                UmengStatisticUtil.onEvent(MainApplication.getInstance(), UmengStatisticEvent.HOME_SEARCH);   //Umeng
                break;
//            case R.id.ll_userlevel:
//                if (isLogin()) {
//                IntentUtil.openMainActivity(mContext, new int[]{3});
////                    user = SYUserManager.getInstance().getUser();
////                    PersonPageParam param = new PersonPageParam();
////	            	param.setViewerUid(user.userId());
////	            	param.setFrom(PersonPageParam.FROM_OTHER);
////	            	UIHelper.showPersonPage((MainActivity)context,param);
//                } else {
//                    IntentUtil.gotoLogin(mContext);
//                }
//                break;
//            case R.id.ico_home_title_inews:
//                IntentUtil.gotoSouYueYaoWen(mContext);
//                break;
        }
    }

    public static void openManagerAcitivity(Fragment cx, Class<?> clz, int requestcode) {
        Intent intent = new Intent();
        intent.setClass(cx.getActivity(), clz);
        cx.startActivityForResult(intent, requestcode);
        cx.getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

//    @Override
//    public void loginCallBack() {
////        updataUserName();//登录成功后回调，再次尝试调用给顶部用户栏赋值操作
//    }
//
//    @Override
//    public void loginGuestCallBack() {
////        setUserGuest();
//    }
}
