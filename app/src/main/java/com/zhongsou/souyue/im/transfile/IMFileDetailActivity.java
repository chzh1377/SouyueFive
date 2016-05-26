package com.zhongsou.souyue.im.transfile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.tuita.sdk.im.db.helper.MessageFileDaoHelper;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.tuita.sdk.im.db.module.MessageFile;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.ac.FileListActivity;
import com.zhongsou.souyue.im.ac.IMBaseActivity;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.render.MsgFileRender;
import com.zhongsou.souyue.im.render.MsgUtils;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.ToastUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by xyh0125 on 15/11/12.
 * 文件下载详情页面
 */
public class IMFileDetailActivity extends IMBaseActivity implements View.OnClickListener {

    public static final String BROADCAST_DOWNLOAD_FILE_TAG = "com.zhongsou.souyue.im.transfile";

    private FileDownloadListener fileDownloadListener;
    private static MsgFileRender.ProgressListener progressListener;     //给render相关显示的回调
    private PopupWindow mTabMsgPopupWindow;
    private ConnectivityManager mConnectivityManager; // 联网用到
    private NetworkInfo mNetInfo; // 联网用到
    private UpdateReceiver receiver;

    private LinearLayout baseView ;
    private RelativeLayout titleView ;
    private TextView tvFileName;
    private TextView tvDownloadPercent;
    private ImageView ivFileType ;
    private ImageView moreBtn;
    private Button baseBtn;
    private ImageButton btn_back;
    private TextView filePercent;
    private TextView fileOpenInfo;
    private ProgressBar my_progressBar;

    private DecimalFormat dFormat;
    private String fileSizeStr;
    public int fileType ;
    private long msgId;
    private long msgFileId;
    private String msgFile;
    private MessageFile messageFile;
    private ChatMsgEntity chatMsgEntity;    //聊天页带来的文件的相关数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.im_file_download_view);
        dFormat = new DecimalFormat("#.##");
        initIntent();
        initView();
        registReceiver();
        loadData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageFile m =MessageFileDaoHelper.getInstance(IMFileDetailActivity.this).select(msgFileId);
        if(m!=null){
            tvFileName.setText(m.getName());
            progressListener.changeFileName(m.getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }

        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
    }

    private void initIntent() {

        chatMsgEntity = (ChatMsgEntity) getIntent().getSerializableExtra("mChatEntity");

        //Message id:
        msgId = chatMsgEntity.getId();
        msgFile = chatMsgEntity.getText();
        //MessageFile实体类 （ 里面的ID是fileID ）
        Gson gson = new Gson();
        messageFile = gson.fromJson(msgFile, MessageFile.class);
        messageFile.setOnlyId(msgId);

        //判断文件列表 数据表id
        if (chatMsgEntity.getFileMsgId()==-1){
            //没有
            messageFile.setCursize((long) 0);
            messageFile.setLocalpath(FileDownloadService.getSavePath() + messageFile.getName());
            messageFile.setState(MessageFile.DOWNLOAD_STATE_INIT);
            fileType = MsgUtils.getFileType(MsgUtils.getFileName(messageFile.getUrl()));
            Log.i("ImDownload", "IMFileDetailActivity==> initIntent——if————fileType:" + fileType);
            messageFile.setType(fileType);
            //将文件bean插入文件列表数据表
            msgFileId = MessageFileDaoHelper.getInstance(IMFileDetailActivity.this).insert(messageFile);
            Log.i("ImDownload","IMFileDetailActivity==> initIntent——if————msgFileId:"+msgFileId);
            //将id插入msghistory表
            long id = chatMsgEntity.getId();
            Log.i("ImDownload","IMFileDetailActivity==> initIntent——if————chatMsgEntity.getId():"+chatMsgEntity.getId());

            MessageHistoryDaoHelper.getInstance(IMFileDetailActivity.this).updateMsgFileId(id,msgFileId);

        }else {
            //有  初始化  相关数据  如 ”已下载“
            updateMessageFile();
        }

        try {
            if(messageFile!=null){
                JSONObject o = new JSONObject(msgFile);
                //判断文件是否过期
                messageFile.setInvalidTime(Long.valueOf(o.getString("expiry")));
                messageFile.setName(o.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateMessageFile() {
        Log.i("ImDownload","IMFileDetailActivity==> initIntent——else————chatMsgEntity.getFileMsgId():"+chatMsgEntity.getFileMsgId());
        Log.i("ImDownload", "IMFileDetailActivity==> initIntent——else————chatMsgEntity.getId():" + chatMsgEntity.getId());
        long id = MessageHistoryDaoHelper.getInstance(IMFileDetailActivity.this).findMsgFileId(chatMsgEntity.getId());
        Log.i("ImDownload", "IMFileDetailActivity==> initIntent——else————id:" + id);
        messageFile =  MessageFileDaoHelper.getInstance(IMFileDetailActivity.this).select(id);
    }


    private void initView() {
        baseView = (LinearLayout) findViewById(R.id.im_file_download_base_view);
        titleView = (RelativeLayout) findViewById(R.id.rl_layout);
        tvFileName = (TextView) findViewById(R.id.im_download_file_name);
        tvDownloadPercent = (TextView) findViewById(R.id.im_download_file_percent);
        ivFileType = (ImageView) findViewById(R.id.im_download_file_type);
        moreBtn = (ImageView) findViewById(R.id.im_chat_more);
        moreBtn.setOnClickListener(this);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        baseBtn = (Button) findViewById(R.id.im_download_file_btn);
        baseBtn.setOnClickListener(this);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        filePercent = (TextView)findViewById(R.id.im_download_file_percent);
        fileOpenInfo = (TextView)findViewById(R.id.im_download_file_info);
        my_progressBar = (ProgressBar)findViewById(R.id.my_progressBar);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.im_download_file_btn:

                if(messageFile==null) return;
                if(messageFile.getState()==null){
                    messageFile.setState(MessageFile.DOWNLOAD_STATE_INIT);
                }

                switch (messageFile.getState()){

                    //下载未开始等待中，点击按钮进行——下载操作
                    case MessageFile.DOWNLOAD_STATE_INIT:
                        startDownload();
                        baseBtn.setText("停止");
                        messageFile.setState(MessageFile.DOWNLOAD_STATE_LOADING);
                        Log.i("ImDownload","IMFileDetailActivity==> baseBtnClick————下载未开始等待中，点击按钮进行——下载操作");
                        break;

                    //下载完成，点击按钮进行——打开文件操作
                    case MessageFile.DOWNLOAD_STATE_COMPLETE:
                        if(messageFile.getLocalpath()!=null){
                            IMFieUtil.openFile(IMFileDetailActivity.this,new File(messageFile.getLocalpath()));
                        }
                        Log.i("ImDownload","IMFileDetailActivity==> baseBtnClick————下载完成，点击按钮进行——打开文件操作");
                        break;

                    //下载失败，点击按钮进行——重新下载操作
                    case MessageFile.DOWNLOAD_STATE_FAILED:
                        startDownload();
                        baseBtn.setText("停止");
                        messageFile.setState(MessageFile.DOWNLOAD_STATE_LOADING);
                        Log.i("ImDownload", "IMFileDetailActivity==> baseBtnClick————下载失败，点击按钮进行——重新下载操作");
                        break;

                    //下载中，点击按钮进行——暂停操作
                    case MessageFile.DOWNLOAD_STATE_LOADING:
                        messageFile.setState(MessageFile.DOWNLOAD_STATE_PAUSE);
                        stopDownload();
                        tvDownloadPercent.setText(fileSizeStr + " | 未下载");
                        baseBtn.setText("下载文件");
                        Log.i("ImDownload", "IMFileDetailActivity==> baseBtnClick————下载中，点击按钮进行——暂停操作");
                        break;

                    //暂停中，点击按钮进行——继续下载操作
                    case MessageFile.DOWNLOAD_STATE_PAUSE:
                        //查询数据库
                        updateMessageFile();
                        startDownload();
                        baseBtn.setText("停止");
                        messageFile.setState(MessageFile.DOWNLOAD_STATE_LOADING);
                        Log.i("ImDownload", "IMFileDetailActivity==> baseBtnClick————暂停中，点击按钮进行——下载操作");
                        break;
                }

                break;

            case R.id.im_chat_more:
                createMorePupWindow();
                mTabMsgPopupWindow.showAtLocation(titleView, Gravity.RIGHT | Gravity.TOP, 9, (int) (titleView.getBottom() * 1.4));
                break;

            case R.id.im_share_to_friends_layout:
                IMShareActivity.invoke(IMFileDetailActivity.this, chatMsgEntity);
                break;

            case R.id.im_check_file_layout:
                startActivityWithAnim(FileListActivity.class);
                break;

            case R.id.btn_back:
                finish();
                break;

            default:
                break;

        }
    }


    private void stopDownload() {
          FileDownloadService.stopThread(IMFileDetailActivity.this,messageFile);
//        FileDownloadService.pauseQueueItem(IMFileDetailActivity.this, messageFile);
    }

    private void startDownload() {
        FileDownloadService.addItemToQueue(IMFileDetailActivity.this, messageFile);
    }

    private void registReceiver() {
        receiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_DOWNLOAD_FILE_TAG);
        registerReceiver(receiver, filter);
        IntentFilter mfilter = new IntentFilter();
        mfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mfilter);
    }

    /**
     * 监听网络变化广播 做出相应的提示
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                mNetInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetInfo != null && mNetInfo.isAvailable()){
                    //有网状态
                }else {
                    //无网状态
                    ToastUtil.show(IMFileDetailActivity.this, "网络断了，无法下载");
                }
            }
        }
    };

    private void loadData() {
        if(messageFile==null) {
            tvDownloadPercent.setText("该文件已被删除！！！");
            return;
        }

        if(messageFile.getState()!=null && messageFile.getState()!=null){

            fileSizeStr = MsgUtils.getFileSize(messageFile.getSize(),dFormat);
            int state = messageFile.getState();
            CharSequence ch ;
            switch (state){
                case MessageFile.DOWNLOAD_STATE_COMPLETE:
                    ch = fileSizeStr+" | 已下载 " ;
                    baseBtn.setText("打开文件");
                    my_progressBar.setProgress(100);
                    break;
                case MessageFile.DOWNLOAD_STATE_INIT:
                    ch = fileSizeStr+" | 未下载" ;
                    break;
                case MessageFile.DOWNLOAD_STATE_FAILED:
                case MessageFile.DOWNLOAD_STATE_PAUSE:
                case MessageFile.DOWNLOAD_STATE_LOADING:
                    ch = fileSizeStr + " | 未下载 " ;
                    baseBtn.setText("下载文件");
                    messageFile.setState(MessageFile.DOWNLOAD_STATE_PAUSE);
//                    baseBtn.setText("该显示什么呢？");
                    break;
                default:
                    ch = "";
            }
            tvDownloadPercent.setText(ch);

        }

        if(System.currentTimeMillis()>messageFile.getInvalidTime()&&messageFile.getState()!=MessageFile.DOWNLOAD_STATE_COMPLETE){
            baseBtn.setText("文件已失效，无法提供下载");
            baseBtn.setBackgroundResource(R.color.gray);
            baseBtn.setClickable(false);
        }else {
            baseBtn.setClickable(true);
        }

        if(messageFile.getUrl()!=null){
            setImagePic(ivFileType, messageFile.getUrl());
        }

        if(messageFile.getName() !=null){
            tvFileName.setText(messageFile.getName());
        }

    }

    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int what = bundle.getInt("what");
            Object obj = bundle.get("obj");

            Message message = Message.obtain();
            message.what = what;
            message.obj = obj;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 利用消息处理机制适时更新进度条,非UI线程执行操作
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                MessageFile item = (MessageFile) msg.obj;
                updateProgress(item);
            }else if (msg.what == 2) {
                MessageFile item = (MessageFile) msg.obj;
                updateStateChange(item);
            }
        }
    };

    /**
     * 更新状态
     * @param item
     */
    private void updateStateChange(MessageFile item) {
        if(item.getOnlyId() == chatMsgEntity.getId()) {

        switch (item.getState()){
            case MessageFile.DOWNLOAD_STATE_COMPLETE:
                progressListener.setProgress("下载完成",msgId);
                my_progressBar.setProgress(100);
                //下载完成，按钮显示“打开文件”
                baseBtn.setText("打开文件");
                tvDownloadPercent.setText(fileSizeStr+" | 已下载 ");
                fileOpenInfo.setVisibility(View.VISIBLE);
                messageFile.setState(MessageFile.DOWNLOAD_STATE_COMPLETE);

                Log.i("ImDownload","IMFileDetailActivity==> updateStateChange————下载完成");
                break;

            case MessageFile.DOWNLOAD_STATE_FAILED:
                filePercent.setText("下载失败");
                progressListener.setProgress("下载失败",msgId);
                baseBtn.setText("下载失败");
                fileOpenInfo.setVisibility(View.GONE);

                Log.i("ImDownload", "IMFileDetailActivity==> updateStateChange————下载失败");
                break;
            case MessageFile.DOWNLOAD_STATE_LOADING:
                progressListener.setProgress("",msgId);
                fileOpenInfo.setVisibility(View.GONE);
//                //下载中，按钮显示 “停止”
//                baseBtn.setText("停止");

                Log.i("ImDownload", "IMFileDetailActivity==> updateStateChange————下载中");
                break;
            case MessageFile.DOWNLOAD_STATE_PAUSE:
                progressListener.setProgress("",msgId);
                baseBtn.setText("下载文件");
                fileOpenInfo.setVisibility(View.GONE);

                Log.i("ImDownload", "IMFileDetailActivity==> updateStateChange————暂停了");
                break;

            case MessageFile.DOWNLOAD_STATE_INIT:
                progressListener.setProgress("等待中",msgId);
                baseBtn.setText("下载文件");
                messageFile.setState(MessageFile.DOWNLOAD_STATE_INIT);
                fileOpenInfo.setVisibility(View.GONE);

                Log.i("ImDownload", "IMFileDetailActivity==> updateStateChange————等待中");
                break;
            }
        }

    }

    /**
     * 更新进度 ： 下载界面 & 聊天界面
     *
     * @param item
     */
    private void updateProgress(final MessageFile item) {
        if (my_progressBar == null) {
            return;
        }
        if(item.getOnlyId() == chatMsgEntity.getId()) {
            int downloadPercent = 0;
            if (item.getCursize() == null) {
                item.setCursize((long) 0);
            }
            downloadPercent = (int) (item.getCursize() * 100 / item.getSize());
            if(downloadPercent<100){
                filePercent.setText(fileSizeStr + " | " + downloadPercent + "%");
            }
            progressListener.setProgress(downloadPercent + "%",chatMsgEntity.getId());
            my_progressBar.setProgress(downloadPercent);
            Log.i("ImDownload", "IMFileDetailActivity==》updateProgress——downloadPercent：" + downloadPercent);
        }
    }

    private void startActivityWithAnim(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(IMFileDetailActivity.this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 给render回调监听进度变化
     *
     * @param listener
     */
    public static void setListener(MsgFileRender.ProgressListener listener){
        progressListener =listener;
    }

    private void createMorePupWindow() {
        View popupView = getLayoutInflater().inflate(R.layout.im_file_detail_more_pop, null);
        mTabMsgPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTabMsgPopupWindow.setFocusable(true);
        mTabMsgPopupWindow.setOutsideTouchable(true);
        mTabMsgPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupView.findViewById(R.id.im_share_to_friends_layout).setOnClickListener(this);
        popupView.findViewById(R.id.im_check_file_layout).setOnClickListener(this);
    }

    /**
     * 弹出框消失
     */
    private void dismissPupWindows() {
        if (null != mTabMsgPopupWindow) {
            mTabMsgPopupWindow.dismiss();
        }
    }

    public void setImagePic(ImageView image,String url){
        int fileType = MsgUtils.getFileType(getFileName(url));
        if(MsgUtils.FILE_TYPE_PDF == fileType){
            image.setImageResource(R.drawable.im_download_type_pdf);
        }else if(MsgUtils.FILE_TYPE_IMG == fileType){
            image.setImageResource(R.drawable.im_download_type_pic);
        }else if(MsgUtils.FILE_TYPE_MP3 ==fileType){
            image.setImageResource(R.drawable.im_download_type_mp3);
        }else{
            image.setImageResource(R.drawable.im_download_type_file);
        }
    }

    public String getFileName(String url){
        return url.substring(url.lastIndexOf('/')+1);
    }

    public static interface FileDownloadListener {
        public void setProgress(MessageFile messageFile,long msgId);
        public void setState(MessageFile messageFile,long msgId);
    }

}
