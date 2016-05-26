package com.zhongsou.souyue.adapter.baselistadapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.circle.activity.CircleSelImgGroupActivity;
import com.zhongsou.souyue.circle.activity.DetailActivity;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.circle.view.CircleFollowDialogNew;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.FootItemBean;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.InterestSubscriberReq;
import com.zhongsou.souyue.net.detail.AddCommentUpReq;
import com.zhongsou.souyue.net.volley.*;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/1/25.
 */

public class CircleListManager extends BottomInvoke4Manager implements  IBottomInvoke2, IVolleyResponse {
    private Activity mActivity;
    private long interest_id ;
    private String keyWord;
    private OnChangeListener listener;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private OnChangeListener onRoleChangeListener;
    private Uri imageFileUri;
    private CircleFollowDialogNew circleFollowDialog;
    private Posts mPosts;
    private Dialog showDialogAddImg;
    private ViewGroup addIMgView;

    public CircleListManager(Activity mActivity, long interest_id)
    {
        super(mActivity);
        this.mActivity = mActivity;
        this.interest_id = interest_id;
    }

    public void setInterest_id(long interest_id) {
        this.interest_id = interest_id;
    }

    public void setKeyWord(String keyWord){
        this.keyWord = keyWord;
    }
    public void setChangeListener(OnChangeListener listener){
        this.listener = listener;
    }
    public void setView(ListViewAdapter adapter,ListView listview){
        mAdapter = adapter;
        mListView = listview;
    }
    public void setOnRoleChangeListener(OnChangeListener onRoleChangeListener) {
        this.onRoleChangeListener = onRoleChangeListener;
    }


    @Override
    public void doComment(BaseListData data) {
        doCircleComment(data);
    }

    public Uri getImageFileUri(){
        return imageFileUri;
    }
    public CircleFollowDialogNew getCircleFollowDialog(){
        return circleFollowDialog;
    }


    /**
     * ListView单条更新
     */
    public void updateSingleRow(CircleResponseResultItem item, int type) {

    }


    @Override
    public void clickItem(BaseListData item) {
        BaseInvoke invoke = item.getInvoke();
        invoke.setChan("");
//        UpEventAgent.onZSDevListItemClick(mActivity, invoke.getTitle(),
//                invoke.getSrpId(),invoke.getKeyword(),invoke.getUrl(),
//                item.getFootView()!=null?item.getFootView().getSource():"",
//                invoke.getCategory(),mballTitle,
//                invoke.getBlogId()+"");
        HomePagerSkipUtils.skip(mActivity, invoke);
    }

    @Override
    public void doCircleUp(BottomViewRender bottomViewRender, View v, BaseListData data) {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)){
            SouYueToast.makeText(mActivity, R.string.souyue_neterror, Toast.LENGTH_LONG).show();
            return;
        }
        if(!isFastDoubleClick()){   //防止过快点赞
            AddCommentUpReq req = new AddCommentUpReq(HttpCommon.DETAIL_ADDUP_ID,this);
            req.setParams(data.getInvoke().getKeyword(),data.getInvoke().getSrpId(),
                    getCircleUrl(data), SYUserManager.getInstance().getToken(),
                    DetailActivity.DEVICE_COME_FROM,DetailActivity.UP_TYPE_MAIN,
                    0,"","","","","",data.getInvoke().getBlogId());
            req.addKeyValueTag("render",bottomViewRender);
            req.addKeyValueTag("data",data);
            CMainHttp.getInstance().doRequest(req);
//            v.setEnabled(false);
        }
    }
    private boolean mIsPrivate;
    private int mRole;
    private int mIsBanTalk;     //是否禁言
    private String mInterestId;

    public int getmIsBanTalk() {
        return mIsBanTalk;
    }

    public void setmIsBanTalk(int mIsBanTalk) {
        this.mIsBanTalk = mIsBanTalk;
    }

    public int getmRole() {
        return mRole;
    }

    public void setmRole(int mRole) {
        this.mRole = mRole;
    }

    public boolean ismIsPrivate() {
        return mIsPrivate;
    }

    public void setmIsPrivate(boolean mIsPrivate) {
        this.mIsPrivate = mIsPrivate;
    }

    public Posts getmPosts() {
        return mPosts;
    }

    public void setmPosts(Posts mPosts) {
        this.mPosts = mPosts;
    }

    public String getmInterestId() {
        return mInterestId;
    }

    public void setmInterestId(String mInterestId) {
        this.mInterestId = mInterestId;
    }

    /**
     * 圈子评论
     */
    public void doCircleComment(final BaseListData data )
    {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)){
            SouYueToast.makeText(mActivity,R.string.souyue_neterror,Toast.LENGTH_LONG).show();
            return;
        }

        // 判断用户是否登陆
        if (!SouyueAPIManager.isLogin() && mIsPrivate) {   //5.0.7游客可以评论公开圈
            SouyueAPIManager.goLoginForResult(mActivity , CircleIndexActivity.REQUEST_CODE_LOGIN_ACTIVITY);
            return;
        } else if(mRole == Constant.ROLE_NONE && mIsPrivate) { // 非圈成员  //5.0.7非圈成员可以评论公开圈
            showJoinInterest(mIsPrivate,data.getInvoke().getInterestId()+"");
            return;
        } else if(mIsBanTalk == Constant.MEMBER_BAN_TALK_YES){
            SouYueToast.makeText(mActivity, "您已被禁言", Toast.LENGTH_SHORT).show();
            return;
        }else {
            //当前页跟帖

            circleFollowDialog = new CircleFollowDialogNew(mActivity,this,getCircleUrl(data)
                    , DetailActivity.DEVICE_COME_FROM,data.getInvoke().getSrpId(),data.getInvoke().getKeyword(),mPosts);
            circleFollowDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    circleFollowDialog.saveInfo(data.getInvoke().getUrl());
                }
            });
            circleFollowDialog.setListener(new OnChangeListener() {

                @Override
                public void onChange(Object obj) {
                    if(circleFollowDialog.getImgLen()== 0){
                        jumpImgGroup();
                    }
                }
            });
            circleFollowDialog.setListData(data);
            circleFollowDialog.setmInterestId(data.getInvoke().getSrpId());
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
            circleFollowDialog.showDialog();
            circleFollowDialog.setEditText(SYSharedPreferences.getInstance().getString(data.getInvoke().getUrl() + "_text",""));
            String strImg = SYSharedPreferences.getInstance().getString(data.getInvoke().getUrl() + "_img", "");
            if(strImg != null && !strImg.equals("")){
//                List<String> list = JSON.parseArray(strImg, String.class);
                List<String> list =  new Gson().fromJson(strImg,new TypeToken<List<String>>(){}.getType());
                if(list != null &&list.size() != 0){
                    circleFollowDialog.addImagePath(list);
                }
            }
        }
    }

    /**
     * 拼接 url
     * @param data
     * @return
     */
    private String getCircleUrl(BaseListData data)
    {
        String mParamUrl = "";
        try{
            mParamUrl = URLEncoder.encode("http://interest.zhongsou.com?sign_id=" + data.getInvoke().getSignId() +
                    "&blog_id=" + data.getInvoke().getBlogId()+
                    "&sign_info=" + data.getInvoke().getBlogId() +
                    "&srpid=" + data.getInvoke().getSrpId() +
                    "&srpword =" + data.getInvoke().getKeyword(), "utf-8");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mParamUrl;
    }
    /**
     * 添加照片的布局
     */
    private void initAddImgLayout(){
        LayoutInflater mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addIMgView = (ViewGroup) mLayoutInflater.inflate(R.layout.circle_follow_add_img_menu,null, false);
        TextView textView_xiangce = (TextView) addIMgView.findViewById(R.id.textView_xiangce);
        TextView textView_photo = (TextView) addIMgView.findViewById(R.id.textView_photo);
        TextView textView_cancle= (TextView) addIMgView.findViewById(R.id.textView_cancel);
        textView_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
            }
        });
        textView_xiangce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
                jumpImgGroup();
            }
        });
        textView_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
                jumpTakePhoto();
            }
        });
    }
    private void showAddImgMenu(){
        initAddImgLayout();
        showDialogAddImg = DetailActivity.showAlert(mActivity, addIMgView, Gravity.BOTTOM);
    }
    /**
     * 跳转到
     */
    private void jumpImgGroup() {
        Intent intent = new Intent(mActivity,CircleSelImgGroupActivity.class);
        intent.putExtra("piclen", circleFollowDialog.getImgLen());
        mActivity.startActivityForResult(intent, 100);
    }
    /**
     * 选择照片
     */
    private void jumpTakePhoto() {
        if(circleFollowDialog.getImgLen() >= 9){
            Toast.makeText(mActivity, "最多选择9张图片", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Uri imageFileUri = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            if (imageFileUri != null) {
                Intent i = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra( MediaStore.EXTRA_OUTPUT,imageFileUri);
                if (Utils.isIntentSafe(((FragmentActivity) mActivity), i)) {
                    ((FragmentActivity) mActivity).startActivityForResult(i, 200);
                } else {
                    SouYueToast.makeText(mActivity,mActivity.getString(R.string.dont_have_camera_app),SouYueToast.LENGTH_SHORT).show();
                }
            } else {
                SouYueToast.makeText(mActivity, mActivity.getString(R.string.cant_insert_album),SouYueToast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            SouYueToast.makeText(mActivity,mActivity.getString(R.string.cant_insert_album),SouYueToast.LENGTH_SHORT).show();
        }
    }
    /**
     * 显示加入圈，非圈成员加入圈
     * @param is_private
     * @param interest_id
     */
    private void showJoinInterest(final boolean is_private, final String interest_id ) {
        mInterestId= interest_id;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("您还不是该圈的成员，是否立即加入？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (is_private) {// 判断是私密圈还是公开圈，进行跳转,私密圈，进行申请
                            IntentUtil.gotoSecretCricleCard(mActivity, id);
                        } else {// 公开圈，直接加入圈子
                            InterestSubscriberReq req =  new InterestSubscriberReq(HttpCommon.CIRLCE_INTEREST_SUB_ID,CircleListManager.this);
                            req.setParams(SYUserManager.getInstance().getToken(),interest_id,"");
                            CMainHttp.getInstance().doRequest(req);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        switch (request.getmId())
        {
            case HttpCommon.DETAIL_ADDUP_ID:
                commentUpSuccess((BaseBottomViewRender) request.getKeyValueTag("render"),
                        (BaseListData) request.getKeyValueTag("data"),request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                saveRecomentCirclesSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                BaseListData data = (BaseListData) request.getKeyValueTag("item");
                if (data!=null) {
                    FootItemBean foot = data.getFootView();
                    foot.setCommentCount(foot.getCommentCount() + 1);
                    mAdapter.notifyDataSetChanged();
                }
                circleFollowDialog.dismissProcessDialog();
                SouYueToast.makeText(mActivity,R.string.comment_detail_success,Toast.LENGTH_LONG).show();
                break;
        }
    }
    /**
     *   非圈成员 订阅成功
     * @param res
     */
    public void saveRecomentCirclesSuccess(HttpJsonResponse res) {
        if (res.getBody().get("state").getAsInt() == 1) {
            Toast.makeText(mActivity, "订阅成功", Toast.LENGTH_SHORT).show();
            UpEventAgent.onGroupJoin(mActivity, mInterestId + "." + "", "");// 统计
            SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);

            /**
             * 需要通知页面的改变
             */
            if (onRoleChangeListener != null) {
                onRoleChangeListener.onChange(null);
            }
        } else {
            Toast.makeText(mActivity, "订阅失败", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 点赞成功回调
     */
    public void commentUpSuccess(BaseBottomViewRender render,BaseListData listData, HttpJsonResponse res) {
        int count =listData.getFootView().getUpCount();
        count ++;
        if(count <=0)
        {
            count=1;
        }
        FootItemBean foot = listData.getFootView();
        foot.setUpCount(count);
        foot.setIsUp(1);
        if(render instanceof  BottomViewRender2)
        {
            ((BottomViewRender2)render).setUp(true);
            ((BottomViewRender2)render).upAnimation();
            ((BottomViewRender2)render).doUpCallBack();
        }else  if(render instanceof  BottomViewRender4)
        {
            ((BottomViewRender4)render).setUp(true);
            ((BottomViewRender4)render).doUpCallBack();

        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        IHttpError error = request.getVolleyError();
        int errorType = error.getErrorType();
        int code = error.getErrorCode();
        switch (request.getmId()) {
            case HttpCommon.DETAIL_ADDUP_ID:
                if(errorType ==CSouyueHttpError.TYPE_SERVER_ERROR )
                {
                    if (code<700) {
                        SouYueToast.makeText(mActivity, "点赞失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    SouYueToast.makeText(mActivity,R.string.networkerror, Toast.LENGTH_SHORT).show();
                }
                break;
            case HttpCommon.CIRLCE_INTEREST_SUB_ID:
                IHttpError error1 = request.getVolleyError();
                if (error1.getErrorType() == CSouyueHttpError.TYPE_SERVER_ERROR) {
                    UIHelper.ToastMessage(mActivity,
                            R.string.cricle_manage_save_circle_failed);
                } else {
                    UIHelper.ToastMessage(mActivity,R.string.networkerror);
                }
                break;
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                if(circleFollowDialog!=null)
                {
                    circleFollowDialog.dismissProcessDialog();
                }
                if(errorType ==CSouyueHttpError.TYPE_SERVER_ERROR )
                {
                    if (code<700)
                    {
                        SouYueToast.makeText(mActivity, "评论失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    SouYueToast.makeText(mActivity,R.string.networkerror, Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    public void onHttpStart(IRequest request) {

    }
}
