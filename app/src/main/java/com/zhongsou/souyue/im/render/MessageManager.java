package com.zhongsou.souyue.im.render;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import com.speex.encode.AudioLoader;
import com.tencent.mm.sdk.platformtools.Log;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.umeng.analytics.MobclickAgent;
import com.upyun.api.IUpYunConfig;
import com.upyun.api.Uploader;
import com.upyun.api.utils.UpYunException;
import com.upyun.api.utils.UpYunUtils;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.UmengDefParams;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ContactModelUtil;
import com.zhongsou.souyue.log.Logger;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.ui.ImRequestDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 消息管理类
 *
 * @author zhaomeng
 */
public class MessageManager {
    private Context mContext;
    private long mUserId;
    private long mTargetId;
    private int mTargetType;
    private String mTargetName;
    private Contact contactInfo;
    private boolean sendCoinBack;
    private Group mGroup;
    private ImserviceHelp mIMService = ImserviceHelp.getInstance();
    private UploadChatVoiceTask uploadChatVoiceTask;
    private UploadImImageTask uploadImImageTask;

    private RefreshListener mRefreshListener;

    public long getFriendId() {
        return mTargetId;
    }

	public String getFriendName() {
		    return mTargetName;
	}

	public interface RefreshListener {
		void refresh(ChatMsgEntity msgEntity);
	}


    public MessageManager(Activity c,long targetId,int targetType) {
        this.mContext = c;
        this.mUserId = Long.parseLong(SYUserManager.getInstance().getUserId());
        this.mTargetId = targetId;
        this.mTargetType = targetType;
        if (targetType == IConst.CHAT_TYPE_PRIVATE) {
            this.contactInfo = ImserviceHelp.getInstance().db_getContactById(targetId);
            if (this.contactInfo != null){
                this.mTargetName = ContactModelUtil.getShowName(contactInfo);
            }
        }else if (targetType == IConst.CHAT_TYPE_GROUP){
           this.mGroup = ImserviceHelp.getInstance().db_findGourp(mTargetId);
            if (this.mGroup != null){
                this.mTargetName = this.mGroup.getGroup_nick_name();
            }
        }


    }

    public void setRefreshListener(RefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }

    public void setSendCoinBack(boolean sendCoinBack) {
        this.sendCoinBack = sendCoinBack;
    }

    /**
     * 发送文本
     *
     * @param e
     */
    public boolean sendText(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_TEXT, e.getText(),
                e.getRetry());
        if (sendCoinBack) {// 从赠送中搜币网页过来需要延时操作
            try {
                Thread.sleep(1500);
            } catch (InterruptedException a) {
                a.printStackTrace();
            } finally {
                sendCoinBack = false;
            }
        }
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }else{
            Logger.i("souyue", "MessageManager.sendText", "sendText  return s = " + null);
        }
        return false;
    }

    private void refresh(ChatMsgEntity msgEntity) {
        if (mRefreshListener != null) {
            mRefreshListener.refresh(msgEntity);
        }
    }

    /**
     * 赠送中搜币——新协议
     */
    public boolean sendCoinForNew(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService
                .im_sendMessage(mTargetType, mTargetId,
                        MessageHistory.CONTENT_TYPE_GIFT_COIN, e.getText(),
                        e.getRetry());
        if (sendCoinBack) {// 从赠送中搜币网页过来需要延时操作
            try {
                Thread.sleep(1500);
            } catch (InterruptedException a) {
                a.printStackTrace();
            } finally {
                sendCoinBack = false;
            }
        }
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;
    }

    /**
     * 发送分享新闻
     *
     * @param e
     */
    public boolean sendShareNews(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_SOUYUE_NEWS_SHARE,
                e.getText(), e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;
    }

    /**
     * 发送分享SRP
     *
     * @param e
     */
    public boolean sendShareSRP(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_SRP_SHARE, e.getText(),
                e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;
    }

    /**
     * 发送分享兴趣圈
     *
     * @param e
     */
    public boolean sendShareInterest(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_INTEREST_SHARE,
                e.getText(), e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;
    }

    /**
     * 发送@好友 ct=21
     * @param e
     * @param type
     * @return
     */
    public boolean sendAtFriend(ChatMsgEntity e,int type,String content){
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, type,content,
                e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;
    }
    /**
     * 发送web类型消息
     *
     * @param e
     */
    public boolean sendWebMessage(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_WEB, e.getText(),
                e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;
    }

    /**
     * 邀请加入公开兴趣圈
     *
     * @param e
     */
    public boolean sendAskInterest(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_INTEREST_ADD_FRIEND,
                e.getText(), e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;
    }

    /**
     * 邀请加入私密兴趣圈
     *
     * @param e
     */
    public boolean sendAskPrivateInterest(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId,
                MessageHistory.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE,
                e.getText(), e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;
    }

    /**
     * 兴趣圈圈名片
     *
     * @param e
     */
    public boolean sendInterestCard(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD,
                e.getText(), e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;
    }

    /**
     * 发送图片
     *
     * @param e
     */
    public void sendImage(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_NEW_IMAGE, e.getText(),
                e.getRetry());
    }

    /**
     * 存储图片
     *
     * @param msgEntity
     */
    public void saveImage(ChatMsgEntity msgEntity, String localPath,
                          boolean isVertical, float minWidth, float minHeight) {
        msgEntity.setSendId(mUserId);
        File file = new File(msgEntity.getUrl());
        String s = mIMService.im_saveMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_NEW_IMAGE,
                msgEntity.getText(), msgEntity.getRetry());
        if (s != null) {
            msgEntity.UUId = s;
            refresh(msgEntity);
        }
        uploadImImageTask = new UploadImImageTask(mContext, mUserId, file,
                msgEntity, this, localPath, isVertical, minWidth, minHeight);
        uploadImImageTask.execute();

    }

    /**
     * 发送声音
     *
     * @param e
     */
    public void sendVoice(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_NEW_VOICE, e.getText(),
                e.getRetry());
        // if (s != null) {
        // e.UUId = s;
        // refresh(e);
        // }
    }

    /**
     * 存储声音
     *
     * @param e
     */
    public void saveVoice(ChatMsgEntity e, int voiceLength) {
        File file = new File(AudioLoader.getFilePath(e.getUrl())
                .getAbsolutePath());
        if (file == null || !file.canRead() || file.isDirectory()) {
            SouYueToast.makeText(mContext, "录音失败", 0).show();
        } else {
            e.setSendId(mUserId);
            String s = mIMService.im_saveMessage(mTargetType,
                    mTargetId, MessageHistory.CONTENT_TYPE_NEW_VOICE,
                    e.getText(), e.getRetry());
            if (s != null) {
                e.UUId = s;
                refresh(e);
            }
            uploadChatVoiceTask = new UploadChatVoiceTask(this, file, e,
                    voiceLength);
            uploadChatVoiceTask.execute();
        }
    }

    /**
     * 发送名片
     *
     * @param e
     */
    public void sendCard(ChatMsgEntity e) {
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, e.getType(), e.getText(), e.getRetry());
//        try {  因为添上会造成没网时多次点击重试会出现多个发送失败的 item  所以去掉  zcz
//            Thread.sleep(1000);
//        } catch (InterruptedException a) {
//            a.printStackTrace();
//        }
        if (s != null) {
            e.UUId = s;
            refresh(e);
        }
    }

    /**
     * 求中搜币 发送文本的格式：{count:"100"}
     *
     * @param e
     */
    public void sendAskForCoin(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, e.getType(), e.getText(), e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
        }
    }

    /**
     * 发送悄悄话
     */
    public boolean sendWhisper(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, MessageHistory.CONTENT_TYPE_SECRET_MSG, e.getText(),
                e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
            return true;
        }
        return false;

    }

    /**
     * 赠送中搜币
     */
    // public void sendCoin() {
    // if (progressDlg == null) {
    // progressDlg = new ImProgressDialog.Builder(
    // TestChatActivity.this).create();
    // }
    // progressDlg.show();
    // http.integral(SYUserManager.getInstance().getUser().userName());
    // }

    /**
     * 拒绝赠送中搜币
     */
    public void rejestSendCoin() {
        ChatMsgEntity entity = getEntity();
        String content = MobclickAgent.getConfigParams(
                mContext, UmengDefParams.LAOHUJI_REPLY_NO);
        if (StringUtils.isNotEmpty(content)) {
            entity.setText(content);
        } else {
            entity.setText(mContext.getString(R.string.laohuji_reply_no_value));
        }
        this.sendText(entity);
    }

    public void sendAddFriend(final String content) {
        ImRequestDialog mDialog = new ImRequestDialog(mContext);
        mDialog.show();
        if(CMainHttp.getInstance().isNetworkAvailable(mContext)){
            if(ImserviceHelp.getInstance().im_userOp(1, mTargetId,contactInfo.getNick_name(), contactInfo.getAvatar(),content)){
                mDialog.mDismissDialog();
                SouYueToast.makeText(mContext, mContext.getString(R.string.im_send_success), Toast.LENGTH_SHORT).show(); //发送成功不提示
            }else{
                mDialog.mDismissDialog();
                SouYueToast.makeText(mContext, mContext.getString(R.string.im_server_busy), Toast.LENGTH_SHORT).show();
            }
        }else{
            mDialog.mDismissDialog();
            SouYueToast.makeText(mContext, mContext.getString(R.string.im_net_unvisiable), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendGif(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_sendMessage(mTargetType,
                mTargetId, e.getType(), e.getText(), e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
        }
    }

    /**
     * 发送红包
     * @param e
     */
    public void sendRedPacket(ChatMsgEntity e) {
        e.setSendId(mUserId);
        String s = mIMService.im_saveMessage(mTargetType,
                mTargetId, e.getType(), e.getText(), e.getRetry());
        if (s != null) {
            e.UUId = s;
            refresh(e);
        }
    }

    /**
     * 取消声音上传任务
     */
    public void destroy() {
        if (uploadChatVoiceTask != null) {
            uploadChatVoiceTask.cancel(true);
            uploadChatVoiceTask = null;
        }
    }

    private String getJson(String url, int length) {
        JSONObject j = new JSONObject();
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (length == 0) {
            return null;
        }
        try {
            j.put("length", length + "");
            j.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return j.toString();
    }

    class UploadChatVoiceTask extends ZSAsyncTask<Void, Void, String> implements
            IUpYunConfig {
        private SimpleDateFormat formatDate = new SimpleDateFormat(
                "/yy/MM/dd/hh");
        private Random r = new Random();
        private File file;
        private Object mCallbackHandler;
        private ChatMsgEntity m;
        private int voiceLength;

        public UploadChatVoiceTask(Object callbackHandler, File file,
                                   ChatMsgEntity m, int voiceLength) {
            this.mCallbackHandler = callbackHandler;
            this.file = file;
            this.m = m;
            this.voiceLength = voiceLength;
        }

        public String getSaveKey() {
            return "/imandroid" + formatDate.format(new Date())
                    + SYUserManager.getInstance().getToken()
                    + (r.nextInt(8999) + 1000) + ".amr";
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String policy = UpYunUtils.makePolicy(getSaveKey(),
                        Uploader.getExpiration(), BUCKET_VOICE);
                String signature = UpYunUtils.signature(policy + "&"
                        + API_VOICE_KEY);
                return Uploader.upload(policy, signature, UPDATE_HOST
                        + BUCKET_VOICE, file);
            } catch (UpYunException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (StringUtils.isNotEmpty(url)) {
                m.setType(12);
                String voiceUrl = IUpYunConfig.HOST_VOICE + url;
                m.setText(getJson(voiceUrl, voiceLength));
                m.setVoiceUrl(voiceUrl);
                sendVoice(m);
                // Uploader.invokeMethod(callbackHandler,
                // "uploadSuccess",IUpYunConfig.HOST_VOICE + url);
            } else {
                // m.setText(getJson(file.getPath(), voiceLength));
                // m.setFailed();
                // Uploader.invokeMethod(callbackHandler,
                // "uploadFaild","--uploadFaild--");
            }
        }
    }

    class UploadImImageTask extends ZSAsyncTask<Void, Void, String> implements
            IUpYunConfig {
        private long uid;
        private File file;
        private Object callbackHandler;
        private ChatMsgEntity m;
        private MessageManager messageManager;
        private String localPath;
        private boolean isVertical;
        private float mWidth;
        private float mHeight;

        public UploadImImageTask(Object callbackHandler, long uid, File file,
                                 ChatMsgEntity m, MessageManager messageManager,
                                 String localPath, boolean isVertical, float mWidth,
                                 float mHeight) {
            this.callbackHandler = callbackHandler;
            this.m = m;
            this.uid = uid;
            this.file = file;
            this.messageManager = messageManager;
            this.localPath = localPath;
            this.isVertical = isVertical;
            this.mWidth = mWidth;
            this.mHeight = mHeight;
        }

        public String getSaveKey() {
            StringBuffer bucket = new StringBuffer(uid + "");
            while (bucket.length() < 8) {
                bucket.insert(0, '0');
            }
            return bucket.insert(4, '/').insert(0, "/user/")
                    .append(randomTo4()).append(".jpg").toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String policy = UpYunUtils.makePolicy(getSaveKey(),
                        Uploader.getExpiration(), BUCKET_IMAGE);
                String signature = UpYunUtils.signature(policy + "&"
                        + API_IMAGE_KEY);
                return Uploader.upload(policy, signature, UPDATE_HOST
                        + BUCKET_IMAGE, file);
            } catch (UpYunException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (StringUtils.isNotEmpty(url)) {
                m.setType(15);
                String imageUrl = IUpYunConfig.HOST_IMAGE + url + "!android";
                m.setText(getJson(imageUrl, localPath, isVertical, mWidth,
                        mHeight));
                m.setUrl(imageUrl);
                messageManager.sendImage(m);
                Uploader.invokeMethod(callbackHandler, IUpYunConfig.HOST_IMAGE
                        + url + "?r=" + System.currentTimeMillis());
            }
        }

        private String randomTo4() {
            String s = "";
            int intCount = 0;
            intCount = (new Random()).nextInt(9999);//
            if (intCount < 1000)
                intCount += 1000;
            s = intCount + "";
            return s;
        }

        public String getJson(String url, String localPath, boolean isVertical,
                              float mWidth, float mHeight) {
            JSONObject j = new JSONObject();
            if (TextUtils.isEmpty(url)) {
                return null;
            }
            try {
                j.put("url", url);
                j.put("localPath", localPath);
                j.put("isVertical", isVertical);
                j.put("image-width", mWidth);
                j.put("image-height", mHeight);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return j.toString();
        }
    }


    private ChatMsgEntity getEntity() {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.userId = mUserId;
        entity.chatId = mTargetId;
        entity.setSendId(mUserId);
        entity.setChatType(mTargetType);
        entity.setIconUrl(SYUserManager.getInstance().getUser().image());
        return entity;
    }
}
