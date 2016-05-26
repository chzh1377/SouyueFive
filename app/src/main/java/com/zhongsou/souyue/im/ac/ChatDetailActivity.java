package com.zhongsou.souyue.im.ac;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.IConst;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.interfaceclass.DetailChangeInterface;
import com.zhongsou.souyue.im.search.SearchUtils;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ContactModelUtil;
import com.zhongsou.souyue.im.util.IMApi;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by zoulu
 * on 14-8-27
 * Description:私聊详情页
 */
public class ChatDetailActivity extends IMBaseActivity implements View.OnClickListener{

    public static final String CHAT_DETAIL = "gotoChatDetail";
    private ImageView userhead;
    private ImageView addfriend;
    private TextView username;
    private Contact mContact;
    private long mTargetId;
    private TextView title_name;
    private ImageLoader imageLoader;
    private DisplayImageOptions optHeadImg;
    private RelativeLayout edit_im_layout;
    private RelativeLayout im_download_file;
    private RelativeLayout clear_all_history;
    private ImserviceHelp service = ImserviceHelp.getInstance();
    private boolean isCleanHistory = false;//清空聊天记录
    private ToggleButton tbNewsNotify;//是否开启消息提醒
    private int mIsNewsNotify;
    public static DetailChangeInterface mDetailChangeListener;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.chatdetailactivity);
        getExterIntent();
        imageLoader = ImageLoader.getInstance();
        this.optHeadImg = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .cacheInMemory(true).displayer(new RoundedBitmapDisplayer(10))
                .showImageForEmptyUri(R.drawable.default_head)
                .showImageOnFail(R.drawable.default_head)
                .showImageOnLoading(R.drawable.default_head).build();
        initView();
        setClickListener();
        setData();

    }

    /**
     * 初始化 一些数据
     */
    private void setData() {
        title_name.setText("私聊详情");
        this.imageLoader.displayImage(mContact.getAvatar(), userhead, optHeadImg);
        username.setText(ContactModelUtil.getShowName(mContact));

        //是否开启消息提醒
        mIsNewsNotify = mContact.getIs_news_notify();
        if(mIsNewsNotify == 0) {
            tbNewsNotify.setChecked(false);
            tbNewsNotify.setBackgroundResource(R.drawable.detail_switch_close);
        } else {
            tbNewsNotify.setChecked(true);
            tbNewsNotify.setBackgroundResource(R.drawable.detail_switch_open);
        }
    }

    /**
     * 初始化布局
     *
     */
    private void initView(){
        title_name = (TextView) this.findViewById(R.id.title_name);
        userhead = (ImageView) this.findViewById(R.id.userhead);
        addfriend = (ImageView) this.findViewById(R.id.addfriend);
        username = (TextView) this.findViewById(R.id.username);
        edit_im_layout = (RelativeLayout) this.findViewById(R.id.edit_message);
        im_download_file = (RelativeLayout) this.findViewById(R.id.im_download_file);
        clear_all_history = (RelativeLayout) this.findViewById(R.id.clear_all_history);
        tbNewsNotify = (ToggleButton) this.findViewById(R.id.tb_newsnotify);
    }

    /**
     * 加载事件
     */
    private void setClickListener(){
        userhead.setOnClickListener(this);
        addfriend.setOnClickListener(this);
        edit_im_layout.setOnClickListener(this);
        im_download_file.setOnClickListener(this);
        clear_all_history.setOnClickListener(this);
        tbNewsNotify.setOnClickListener(this);
    }

    /**
     * 获取数据
     * */
    private void  getExterIntent() {
        mTargetId = getIntent().getLongExtra(CHAT_DETAIL,0);
        mContact = ImserviceHelp.getInstance().db_getContactById(
                mTargetId);
    }

    /**
     * 清空信息
     */
    private void clearGroupMsg() {
        ImDialog.Builder build = new ImDialog.Builder(this);
        build.setMessage(getString(R.string.im_clear_msg_sure));
        build.setPositiveButton(R.string.im_dialog_ok, new ImDialog.Builder.ImDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, View v) {
                service.db_clearMessageHistory(mContact.getChat_id(),IConst.CHAT_TYPE_PRIVATE);
                SearchUtils.deleteSession(MainActivity.SEARCH_PATH_MEMORY_DIR,mContact.getMyid(),(short) IConst.CHAT_TYPE_PRIVATE,mContact.getChat_id());
                clearRencentTime();
            }
        }).create().show();
    }

    private void clearRencentTime(){
        ImserviceHelp.getInstance().db_updateRecentTime(mContact.getChat_id(),Long.valueOf(SYUserManager.getInstance().getUserId()),0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.userhead:     //头像
                //4.1
                IMApi.IMGotoShowPersonPage(ChatDetailActivity.this,mContact, PersonPageParam.FROM_IM);
                break;

            case R.id.addfriend:    //加号
                Intent intent = new Intent(ChatDetailActivity.this, CreateGroupInviteActivity.class);
                intent.putExtra("contactId",mContact.getChat_id());
                intent.putExtra("fromChatDetail",true);
                startActivity(intent);
                break;

            case R.id.edit_message://编辑消息
                Intent data = new Intent(ChatDetailActivity.this,IMChatActivity.class);
                setResult(IMIntentUtil.MYCHAT_EDITMSG, data);
                ChatDetailActivity.this.finish();
                break;

            case R.id.im_download_file://打开文件
                Intent i = new Intent();
                i.setClass(ChatDetailActivity.this, FileListActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                ChatDetailActivity.this.finish();

                break;

            case R.id.clear_all_history://清空消息
                clearGroupMsg();
                isCleanHistory = true;
                break;

            case R.id.tb_newsnotify://消息免打扰
                if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    SouYueToast.makeText(getApplicationContext(), getString(R.string.user_login_networkerror), 0).show();
                    return;
                }
                showProgress();
                if(tbNewsNotify.isChecked()) {
                    if(service.updateNewsNotify(5, mContact.getChat_id(), true)){
                        tbNewsNotify.setBackgroundResource(R.drawable.detail_switch_open);
                        mDetailChangeListener.msgNotifyChange(true);
                    }
                }else {
                    if (service.updateNewsNotify(5,mContact.getChat_id(),false)){
                        tbNewsNotify.setBackgroundResource(R.drawable.detail_switch_close);
                        mDetailChangeListener.msgNotifyChange(false);
                    }
                }

                break;

            default:
                break;
        }
    }

    public void onBackPressClick(View view) {
        setBackData();
        this.finish();
    }

    private void setBackData(){
        Intent data = new Intent(this,IMChatActivity.class);
        data.putExtra("isCleanHistory",isCleanHistory);
        setResult(100,data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            setBackData();
            onBackPressed();
            return true;
        }
        return false;
    }

}
