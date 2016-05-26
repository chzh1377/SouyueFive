package com.zhongsou.souyue.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.ShareWeiboActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.module.AccountInfo;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.AccessTokenKeeper;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.File;

public class ShareByWeibo {
	public static String CONSUMER_KEY = ShareApi.SINA_CONSUMER_KEY;
	public static String REDIRECT_URL = ShareApi.SINA_REDIRECT_URL;
	private static ShareByWeibo singleton;
	private AuthInfo authInfo;
	private ShareContent currentContent;

	private ShareByWeibo() {
	}

	public static ShareByWeibo getInstance() {
		if (singleton == null) {
			singleton = new ShareByWeibo();
		}
		return singleton;
	}

	public static boolean isAuthorised(Context context) {
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(context);
		if (!token.isSessionValid()) {
			AccessTokenKeeper.clear(context);
			clearCookies(context);
			return false;
		}
		return true;
	}

    public class AuthDialogListener implements WeiboAuthListener {
        private Activity activity = null;
        private Oauth2AccessToken accessToken = null;

        public AuthDialogListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onComplete(Bundle values) {
             accessToken = Oauth2AccessToken.parseAccessToken(values);
             if (accessToken.isSessionValid()) {
             AccessTokenKeeper.keepAccessToken(activity, accessToken);
             if (currentContent != null) {
                 try {
                     share2weibo(activity, currentContent);
                
                 } catch (WeiboException e) {
                     e.printStackTrace();
                 } finally {
                     currentContent = null;
                 }
             }else{
                 if(activity instanceof AccountInfo.BoundListener){
                     ((AccountInfo.BoundListener)activity).onBoundSuccess();
                 }
             }
             }
        }

        @Override
        public void onWeiboException(WeiboException paramWeiboException) {
            showToast(activity, R.string.bound_fail);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub

        }
    }

	public void showToast(Context contexte, int id) {
		SouYueToast.makeText(contexte, id, SouYueToast.LENGTH_SHORT).show();
	}

	public SsoHandler auth2(Activity activity, WeiboAuthListener listener) {
//	    mWeibo=new WeiboAuth(activity,CONSUMER_KEY,REDIRECT_URL,null);
		authInfo = new AuthInfo(activity,CONSUMER_KEY,REDIRECT_URL,null);
//		sso授权方式
	    SsoHandler mSsoHandler=null;
		if(authInfo!=null){
			mSsoHandler = new SsoHandler(activity, authInfo);
			mSsoHandler.authorize(listener);
		}
		return mSsoHandler;

	}

	public void unAuth2(Context context) {
		AccessTokenKeeper.clear(context);
		clearCookies(context);
	}

	public static void clearCookies(Context context) {
		CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
//		 cookieSyncMngr.sync();
	}

	/**
	 * 如果已经授权认证，直接发送分享内容；如果没有授权认证，跳到认证页面。
	 * 
	 * @param activity
	 * @param content
	 */
	public SsoHandler share(Activity activity, ShareContent content) {
		SsoHandler mSsoHandler=null;
		if (isAuthorised(activity) && content!=null) {
			try {
			    
			  share2weibo(activity,content);
			    
				
			} catch (WeiboException e) {
				e.printStackTrace();
			} finally {
				content = null;
			}

		} else {
			currentContent = content;
			mSsoHandler=auth2(activity, new AuthDialogListener(activity));
		}
		return mSsoHandler;
	}

	private void share2weibo(Activity activity, ShareContent content) throws WeiboException {
	    Intent i = new Intent(activity, ShareWeiboActivity.class);
	    i.putExtra(ShareWeiboActivity.EXTRA_ACCESS_TOKEN, AccessTokenKeeper.readAccessToken(activity).getToken());
	    File file;
	    String picPath = null;
		String title =StringUtils.isNotEmpty(content.getTitle())?content.getTitle():"";
		String contents =StringUtils.isNotEmpty(content.getCodeContent())?content.getCodeContent():"";
		if(StringUtils.isEmpty(contents))
		{
			contents=StringUtils.isNotEmpty(content.getContent())?content.getContent():"";
		}
	    if(content.getDimensionalcode()==1){
	        i.putExtra(ShareWeiboActivity.EXTRA_WEBPAGE_URL, "");
//	        i.putExtra(ShareWeiboActivity.EXTRA_WEIBO_CONTENT, StringUtils.isNotEmpty(content.getCodeContent())?content.getCodeContent():"");
			// 分享内容 改为优先选择标题
	        i.putExtra(ShareWeiboActivity.EXTRA_WEIBO_CONTENT, StringUtils.isNotEmpty(title)?title:contents);
	        file=content.getDimensionalCodeFile();
	        if (null != file) {
	            picPath = file.getAbsolutePath();
	        }
	        if (!TextUtils.isEmpty(picPath)) {
	            i.putExtra(ShareWeiboActivity.EXTRA_PIC_URI, picPath);
	        }
			i.putExtra(ShareWeiboActivity.EXTRA_WEBPAGE_IMAGE_URL,  StringUtils.isNotEmpty(content.getPicUrl())?content.getPicUrl():"");
        }else{
            i.putExtra(ShareWeiboActivity.EXTRA_WEBPAGE_URL,  StringUtils.isNotEmpty(content.getUrl())?content.getUrl():"");
            i.putExtra(ShareWeiboActivity.EXTRA_WEIBO_CONTENT,StringUtils.isNotEmpty(content.getWeiboContent())?content.getWeiboContent():"");
            
            file=content.getTempImageFile();
            if (null != file) {
                picPath = file.getAbsolutePath();
            }
            if (!TextUtils.isEmpty(picPath)) {
                i.putExtra(ShareWeiboActivity.EXTRA_PIC_URI, picPath);
            }
            i.putExtra(WebSrcViewActivity.CALLBACK, StringUtils.isNotEmpty(content.getCallback())?content.getCallback():"");
            i.putExtra(ShareContent.SRPID,  StringUtils.isNotEmpty(content.getSrpId())?content.getSrpId():"");
            i.putExtra(ShareContent.KEYWORD, StringUtils.isNotEmpty(content.getKeyword())?content.getKeyword():"");
            i.putExtra(ShareContent.SHAREURL, StringUtils.isNotEmpty(content.getSharePointUrl())?content.getSharePointUrl():"" );
			i.putExtra(ShareWeiboActivity.EXTRA_WEBPAGE_IMAGE_URL,StringUtils.isNotEmpty(content.getPicUrl())?content.getPicUrl():"");
        }

		activity.startActivity(i);
	}

}
