package com.zhongsou.souyue.im.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.personal.UserInviteFriend;
import com.zhongsou.souyue.net.share.ShareSucRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import org.apache.http.HttpConnection;

/**
 * 短信监听工具类
 * <br>首先获取SMSUtils 对象，调用sendSms 发送短信</br>
 * <br>当activity 调用onDestroy方法是，需调用unListening(Context context)</br>
 * @author iamzl
 */
public class SMSUtils {
    public static final Uri CONTENT_URI_OUTBOX = Uri.parse("content://sms/outbox");
    public static final Uri CONTENT_URI = Uri.parse("content://sms");
    public static final String def_body_link = "http://souyue.mobi";
    private SMSContentObserver mObserver;
    private Context context;
    
    private String callback;
    private String CALL_BACK="7";
    
    private ShareContent sharecontent;

    public SMSUtils(Context context, Handler handler) {
        this.context = context;
        listening(handler);
    }
   
    public SMSUtils(Context context) {
        this.context = context;
        listening(new Handler());
    }
    
    public void sendSms(String body) {
        sendSms(body, null);
    }

    public void sendSms(String body,String phoneNo) {
        StringBuilder builder = new StringBuilder("smsto:");
        if (phoneNo != null && phoneNo.length() > 0)
            builder.append(phoneNo);
        Uri uri = Uri.parse(builder.toString());
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        if(StringUtils.isNotEmpty(body)){
           if(ConfigApi.isSouyue()){
                it.putExtra("sms_body", body + def_body_link);
            }else{
                it.putExtra("sms_body", body);
            }
        }else{
            if(ConfigApi.isSouyue()){
                it.putExtra("sms_body", context.getString(R.string.message_reply_no_value) + def_body_link);
            }else{
                it.putExtra("sms_body", context.getString(R.string.message_reply_no_value));
            }
        }
        
        if (context != null) context.startActivity(it);
    }

    public void sendShareMessage(ShareContent content){
        sharecontent=content;
        this.callback=content.getCallback();
        StringBuilder builder = new StringBuilder("smsto:");
        Uri uri = Uri.parse(builder.toString());
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        if(StringUtils.isNotEmpty(content.getSmsContent())){
            it.putExtra("sms_body", content.getSmsContent() );
        }
        
        if (context != null) context.startActivity(it);
    }
    
    public void unListening(Context context) {
        if (mObserver != null && context != null) {
            ContentResolver CR = context.getContentResolver();
            CR.unregisterContentObserver(mObserver);
        }
    }

    private void listening(Handler handler) {
        if (context != null){
            ContentResolver resolver = context.getContentResolver();
            mObserver = new SMSContentObserver(resolver, handler);
            resolver.registerContentObserver(CONTENT_URI, true, mObserver);
        }
    }

    public class SMSContentObserver extends ContentObserver {
        public static final String TAG = "SMSContentObserver";
        public final String[] PROJECTION = new String[] {"body","address"};
        private Handler handler;
        private ContentResolver mResolver;

        public SMSContentObserver(ContentResolver contentResolver, Handler handler) {
            super(handler);
            this.handler = handler;
            this.mResolver = contentResolver;
        }
        
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Cursor cursor = mResolver.query(CONTENT_URI_OUTBOX, PROJECTION, null, null, "_id desc limit 1");
            String body = null;
            String sendTo = null;
            try {
                if (cursor.moveToFirst()) {
                    body = cursor.getString(0);
                    sendTo = cursor.getString(1);
                }
            } catch (Exception e) {
                Log.e("", "query sms throws exception " + e.getMessage());
            } finally {
                if (cursor != null && !cursor.isClosed()) cursor.close();
            }
            if (body != null) Log.e("", "send sms : " + body);
            if ((ConfigApi.isSouyue() && isYQcontent(body)) || (!ConfigApi.isSouyue() && !isSharecontent(body))) {
                if (handler != null) {
                    Message msg = handler.obtainMessage();
                    msg.obj = sendTo;
                    UserInviteFriend inviteFriend = new UserInviteFriend(HttpCommon.USER_INVITE_FRIEND_REQUEST,null);
                    inviteFriend.setParams(sendTo);
                    CMainHttp.getInstance().doRequest(inviteFriend);//只用发送不用管是否发送成功
//                    new Http(SMSUtils.this).giveCoinAfterSendSmsToInviteUser(SYUserManager.getInstance().getUser().token(), sendTo);
                    handler.sendMessage(msg);
                }

            }
            if(callback!=null&&isSharecontent(body)){
//                Http http=new Http(context);
//                http.shareSuc(callback,CALL_BACK,body);
                ShareSucRequest request = new ShareSucRequest(HttpCommon.SHARE_SUC_REQUESTID, callback, null);
                request.setParams(CALL_BACK,body);
                CMainHttp.getInstance().doRequest(request);
            }
            if(isSharecontent(body)){
                if(sharecontent!=null&&!StringUtils.isEmpty(sharecontent.getSharePointUrl())){
                    SharePointInfo info=new SharePointInfo();
                    info.setUrl(sharecontent.getSharePointUrl());
                    info.setKeyWord(sharecontent.getKeyword());
                    info.setSrpId(sharecontent.getSrpId());
                    info.setPlatform(CALL_BACK);
//                    Http http=new Http(context);
//                    http.userSharePoint(info);
                    ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
                    sharecontent.setSharePointUrl(null);
                }
            }
        }

        private boolean isYQcontent(String body) {
            if (body != null && body.contains(def_body_link)) return true;
            return false;
        }
        
        private boolean isSharecontent(String body) {
            if (body != null && body.contains(context.getString(R.string.share_sms))) return true;
            return false;
        }

    }
    
}
