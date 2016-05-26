package com.zhongsou.souyue.im.ac;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.interfaceclass.DetailChangeInterface;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by zhangwb
 * on 15-05-27
 * Description:服务号详情页
 */
public class ServiceMsgDetailActivity extends IMBaseActivity implements View.OnClickListener {

    private static final String EXTRA_TARGET_ID = "TARGET_ID";
    private ServiceMessageRecent mServiceMsgRe;
    private long mTargetId;
    private String mTargetName;
    private TextView tvTitle;
    private RelativeLayout rlWatchHistory;
    private RelativeLayout rlClearHistory;
    private ImserviceHelp service = ImserviceHelp.getInstance();
    private boolean isCleanHistory = false;//清空聊天记录
    private ToggleButton tbNewsNotify;//是否开启消息提醒
    private int mIsNewsNotify;
    private View viewLine;
    public static DetailChangeInterface mDetailChangeListener;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.im_servicemsg_detail_activity);
        getExterIntent();
        initView();
        setClickListener();
        setData();

    }

    /**
     * 初始化 一些数据
     */
    private void setData() {
        tvTitle.setText("服务号详情");

        //是否开启消息提醒
        mIsNewsNotify = mServiceMsgRe.getBy3() == null || mServiceMsgRe.getBy3().equals("0") ? 0 : 1;
        if (mIsNewsNotify == 0) {
            tbNewsNotify.setChecked(false);
            tbNewsNotify.setBackgroundResource(R.drawable.detail_switch_close);
        } else {
            tbNewsNotify.setChecked(true);
            tbNewsNotify.setBackgroundResource(R.drawable.detail_switch_open);
        }

        rlWatchHistory.setVisibility(mServiceMsgRe.getIsShowHistory() == 0 ? View.GONE : View.VISIBLE);
        viewLine.setVisibility(mServiceMsgRe.getIsShowHistory() == 0 ? View.GONE : View.VISIBLE);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        tvTitle = (TextView) this.findViewById(R.id.title_name);
        rlWatchHistory = (RelativeLayout) this.findViewById(R.id.rl_watch_history);
        rlClearHistory = (RelativeLayout) this.findViewById(R.id.rl_clear_history);
        tbNewsNotify = (ToggleButton) this.findViewById(R.id.tb_newsnotify);
        viewLine = this.findViewById(R.id.view_line);
    }

    /**
     * 加载事件
     */
    private void setClickListener() {
        rlWatchHistory.setOnClickListener(this);
        rlClearHistory.setOnClickListener(this);
        tbNewsNotify.setOnClickListener(this);
    }

    /**
     * 获取数据
     */
    private void getExterIntent() {
        mTargetId = getIntent().getLongExtra(EXTRA_TARGET_ID, 0);
        mServiceMsgRe = ImserviceHelp.getInstance()
                .db_getTargetServiceMsgRe(mTargetId);
        mTargetName = mServiceMsgRe.getService_name();
    }

    /**
     * 清空信息
     */
    private void clearHistory() {
        ImDialog.Builder build = new ImDialog.Builder(this);
        build.setMessage(getString(R.string.im_clear_msg_sure));
        build.setPositiveButton(R.string.im_dialog_ok, new ImDialog.Builder.ImDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, View v) {
                service.db_clearMessageHistory(mTargetId,IConst.CHAT_TYPE_SERVICE_MESSAGE);
                clearRencentTime();
            }
        }).create().show();
    }

    private void clearRencentTime() {
        ImserviceHelp.getInstance().db_updateRecentTime(mTargetId, Long.valueOf(SYUserManager.getInstance().getUserId()), 0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_watch_history://查看历史消息
                Intent intent = new Intent(ServiceMsgDetailActivity.this, WebSrcViewActivity.class);
                intent.putExtra(WebSrcViewActivity.PAGE_URL, UrlConfig.IMWatchServiceMsgDetail + "?srvId=" + mTargetId + "&token=" + SYUserManager.getInstance().getToken());
                intent.putExtra(WebSrcViewActivity.PAGE_TYPE, "nopara");
                mContext.startActivity(intent);
                break;

            case R.id.rl_clear_history://清空消息
                clearHistory();
                isCleanHistory = true;
                break;

            case R.id.tb_newsnotify://消息免打扰
                if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    SouYueToast.makeText(getApplicationContext(), getString(R.string.user_login_networkerror), 0).show();
                    return;
                }
                showProgress();
                if (tbNewsNotify.isChecked()) {
                    if (service.saveServiceMsgNotify(mTargetId, true)) {
                        mDetailChangeListener.msgNotifyChange(true);
                        tbNewsNotify.setBackgroundResource(R.drawable.detail_switch_open);
                    }
                } else {
                    if (service.saveServiceMsgNotify(mTargetId, false)) {
                        mDetailChangeListener.msgNotifyChange(false);
                        tbNewsNotify.setBackgroundResource(R.drawable.detail_switch_close);
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

    private void setBackData() {
        Intent data = new Intent(this, IMChatActivity.class);
        data.putExtra("isCleanHistory", isCleanHistory);
        setResult(100, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            setBackData();
            onBackPressed();
            return true;
        }
        return false;
    }

    /**
     * 本页面跳转方法
     */
    public static void invoke(Activity activity, long targetId) {
        Intent intent = new Intent();
        intent.setClass(activity, ServiceMsgDetailActivity.class);
        intent.putExtra(EXTRA_TARGET_ID, targetId);
        activity.startActivity(intent);
    }

}
