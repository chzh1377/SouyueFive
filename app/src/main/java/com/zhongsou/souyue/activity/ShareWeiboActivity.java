package com.zhongsou.souyue.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.exception.WeiboHttpException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.utils.LogUtil;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.module.UserAction;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.share.SharePvRequest;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.share.ShareSucRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.AccessTokenKeeper;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.File;

/**
 * @author wanglong@zhongsou.com
 * 
 */
public class ShareWeiboActivity extends RightSwipeActivity implements OnClickListener, RequestListener , IWeiboHandler.Response{

	private TextView mTextNum;
	private EditText mEdit;
	private FrameLayout mPiclayout;
    private static final String TAG = "sinaweibo";
	private String mPicPath = "";
	private String mContent = "";
	private String mUrl = "";
	private String srpId="";
	private String keyword="";
	private String sharePointUrl;
	private String mAccessToken = "";
	private static final int EDIT_TEXT_MAX_LENGTH = 120;
	public static final String API_SERVER = "https://api.weibo.com/2";
	public static final String SHARE_SRC = ShareApi.SINA_WEIBO_FROM;
	public static final String SPACE = "  ";
	public static final String HTTPMETHOD_POST = "POST";
	public static final String SERVER_URL_PRIX = API_SERVER + "/statuses";
	public static final String EXTRA_WEIBO_CONTENT = "com.weibo.android.content";
	public static final String EXTRA_PIC_URI = "com.weibo.android.pic.uri";
	public static final String EXTRA_WEBPAGE_URL = "com.weibo.android.webpage.url";
	public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
	public static final String EXTRA_TOKEN_SECRET = "com.weibo.android.token.secret";
	public static final String EXTRA_WEBPAGE_IMAGE_URL = "com.weibo.android.image.url";
	public static final String CALL_BACK = "1";
	public static final int WEIBO_MAX_LENGTH = 120;
	private ProgressDialog progressDialog = null;
	private String text_limit;
//	private Http http;
	private String callback;
	private SsoHandler mSsoHandler;
//	private IWeiboShareAPI mWeiboShareAPI = null;
	private String imageUrl;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		http = new Http(this);
		this.setContentView(R.layout.share);
		TextView text = findView(R.id.text_btn);
		text.setText("发送");
		text.setOnClickListener(this);
        ((TextView)findViewById(R.id.activity_bar_title)).setText(getString(R.string.titlebar_weibo_title));
		Intent in = this.getIntent();
		mPicPath = in.getStringExtra(EXTRA_PIC_URI);
		mUrl = in.getStringExtra(EXTRA_WEBPAGE_URL);
		mContent = in.getStringExtra(EXTRA_WEIBO_CONTENT);
		mAccessToken = in.getStringExtra(EXTRA_ACCESS_TOKEN);
		callback=in.getStringExtra(WebSrcViewActivity.CALLBACK);
		imageUrl =in.getStringExtra(EXTRA_WEBPAGE_IMAGE_URL);
		srpId=in.getStringExtra(ShareContent.SRPID);
		keyword=in.getStringExtra(ShareContent.KEYWORD);
		sharePointUrl=in.getStringExtra(ShareContent.SHAREURL);
		
		text_limit = getString(R.string.word_limit);
		mTextNum = (TextView) this.findViewById(R.id.tv_text_limit);
		mTextNum.setText(String.format(text_limit, WEIBO_MAX_LENGTH)+SPACE);
		mTextNum.setOnClickListener(this);

		ImageView picture = (ImageView) this.findViewById(R.id.ivDelPic);
		picture.setOnClickListener(this);

		mEdit = (EditText) this.findViewById(R.id.etEdit);
		mEdit.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
            private int selectionStart ;
            private int selectionEnd ;
			public void afterTextChanged(Editable s) {
				selectionStart = mEdit.getSelectionStart();
                selectionEnd = mEdit.getSelectionEnd();
                if (getStrLen(temp.toString()) > EDIT_TEXT_MAX_LENGTH) {
                	if (getStrLen(s.toString()) > EDIT_TEXT_MAX_LENGTH)
						s.delete(EDIT_TEXT_MAX_LENGTH + 1, getStrLen(s.toString()));
                	if (selectionStart > 0)
                		s.delete(selectionStart-1, selectionEnd);
                    int tempSelection = selectionStart;
                    mEdit.setText(s);
                    mEdit.setSelection(tempSelection);
                }
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				 temp = s;
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String mText = mEdit.getText().toString();
				int len = getStrLen(mText);
				if (len <= WEIBO_MAX_LENGTH) {
					len = WEIBO_MAX_LENGTH - len;
					mTextNum.setTextColor(Color.BLACK);
				} else {
					len = len - WEIBO_MAX_LENGTH;
					mTextNum.setTextColor(Color.RED);
				}
				mTextNum.setText(String.format(text_limit, len)+SPACE);
			}
		});
		if(mContent.length()>EDIT_TEXT_MAX_LENGTH)
		{
			mContent=mContent.substring(0,EDIT_TEXT_MAX_LENGTH);
		}
		mEdit.setText(mContent);
		mPiclayout = (FrameLayout) ShareWeiboActivity.this.findViewById(R.id.flPic);
		if (TextUtils.isEmpty(this.mPicPath)) {
			mPiclayout.setVisibility(View.GONE);
		} else {
			mPiclayout.setVisibility(View.VISIBLE);
			File file = new File(mPicPath);
			if (file.exists()) {
				Bitmap pic = BitmapFactory.decodeFile(this.mPicPath);
				ImageView image = (ImageView) this.findViewById(R.id.ivImage);
				image.setImageBitmap(pic);
			} else {
				mPiclayout.setVisibility(View.GONE);
			}
		}

//		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, ShareApi.SINA_CONSUMER_KEY);
//		mWeiboShareAPI.registerApp();
//		if (savedInstanceState != null) {
//			mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
//		}
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		if (viewId == R.id.text_btn) {
			//发送
			if (isFastDoubleClick())
				return;
			this.mContent = mEdit.getText().toString() + SHARE_SRC + mUrl;
//			if (!TextUtils.isEmpty(mPicPath)) {
//				upload(this.mContent + mUrl, this.mPicPath, null, null, this);
//			} else {
//				update(this.mContent + mUrl, null, null, this);
//			}
			if (!TextUtils.isEmpty(mPicPath)) {
				upload(this.mContent, this.mPicPath, null, null, this);
			} else {
				update(this.mContent, null, null, this);
			}
			showProgressDialog();

		} else if (viewId == R.id.tv_text_limit) {
			Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.attention).setMessage(R.string.delete_all).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mEdit.setText("");
				}
			}).setNegativeButton(R.string.cancel, null).create();
			dialog.show();
		} else if (viewId == R.id.ivDelPic) {
			Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.attention).setMessage(R.string.del_pic).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mPiclayout.setVisibility(View.GONE);
					mPicPath = null;
				}
			}).setNegativeButton(R.string.cancel, null).create();
			dialog.show();
		}
	}

	/**
	 * 延长两次点击间隔时间 避免出现重复点击
	 */
	private static long lastClickTime;

	private boolean isFastDoubleClick() {

		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 纯文字的分享
	 * @param content
	 * @param lat
	 * @param lon
	 * @param listener
     */
	public void update(String content, String lat, String lon, RequestListener listener) {
		WeiboParameters params = new WeiboParameters(ShareApi.SINA_CONSUMER_KEY);
		params.put("status", content);
		if (!TextUtils.isEmpty(lon)) {
			params.put("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.put("lat", lat);
		}
		request(SERVER_URL_PRIX + "/update.json", params, HTTPMETHOD_POST, listener);
	}


	/**
	 * 图片 文字， 结合的分享
	 * @param content
	 * @param file
	 * @param lat
	 * @param lon
     * @param listener
     */
	public void upload(String content, String file, String lat, String lon, RequestListener listener) {
		WeiboParameters params = new WeiboParameters(ShareApi.SINA_CONSUMER_KEY);
		params.put("status", content);

		if (!TextUtils.isEmpty(lon)) {
			params.put("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.put("lat", lat);
		}
//		share2Weibo(content, file);
		if(StringUtils.isNotEmpty(file))
		{
			if(file.startsWith("http://"))
			{
				params.put("url", file);
				request(SERVER_URL_PRIX + "/upload_url_text.json", params, HTTPMETHOD_POST, listener);
			}else
			{
				Bitmap bit =null;
				try{
					bit= decodeBitmap(file);
					if(bit !=null)
					{
						params.put("pic", decodeBitmap(file));
						request(SERVER_URL_PRIX + "/upload.json", params, HTTPMETHOD_POST, listener);
					}
					else
					{
						params.put("pic",BitmapFactory.decodeResource(this.getResources(),R.drawable.logo));
						request(SERVER_URL_PRIX + "/upload.json", params, HTTPMETHOD_POST, listener);
					}
				}catch (Exception e)
				{
					e.printStackTrace();
				}finally {
					bit.recycle();
					bit = null;
				}

			}
		}else
		{
			request(SERVER_URL_PRIX + "/update.json", params, HTTPMETHOD_POST, listener);
		}
	}
	public static final float DISPLAY_WIDTH = 300;
	public static final float DISPLAY_HEIGHT = 300;
	/**
	 * 将图片缩小
	 *
	 * @param path
	 * @return
	 */
	private Bitmap decodeBitmap(String path){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		//获取尺寸信息
		Bitmap bmp = BitmapFactory.decodeFile(path, op);
		//获取比例大小
		int wRatio = (int)Math.ceil(op.outWidth/DISPLAY_WIDTH);
		int hRatio = (int)Math.ceil(op.outHeight/DISPLAY_HEIGHT);
		//如果超出指定大小，则缩小相应的比例
		if(wRatio > 1 && hRatio > 1){
			if(wRatio > hRatio){
				op.inSampleSize = wRatio;
			}else{
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(path, op);
		return bmp;
	}

	/**
	 *
	 * 微博分享
	 * @author qubian
	 * @param content
	 * @param imageFile
	 */
	public void share2Weibo(String content ,String imageFile)
	{
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		if(!TextUtils.isEmpty(content))
		{
			weiboMessage.textObject=getTextObj(content);
		}
		if(!TextUtils.isEmpty(imageFile))
		{
			weiboMessage.imageObject=getImageObj(imageFile);
		}
		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		AuthInfo authInfo = new AuthInfo(this, ShareApi.SINA_CONSUMER_KEY,ShareApi.SINA_REDIRECT_URL, null);

//		mWeiboShareAPI.sendRequest(this, request, authInfo, mAccessToken, new WeiboAuthListener() {
//			@Override
//			public void onWeiboException(WeiboException arg0) {
//			}
//
//			@Override
//			public void onComplete(Bundle bundle) {
//				Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
//				AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
//			}
//
//			@Override
//			public void onCancel() {
//			}
//		});
	}
	/**
	 * 创建文本消息对象。
	 *
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj(String text) {
		TextObject textObject = new TextObject();
		textObject.text = text;
		return textObject;
	}

	/**
	 * 创建图片消息对象。
	 *
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj(String image) {
		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(BitmapFactory.decodeFile(image));
		return imageObject;
	}


	protected void request(final String url, final WeiboParameters params, final String httpMethod, RequestListener listener) {
		params.put("access_token", mAccessToken);
		new AsyncWeiboRunner(this).requestAsync(url, params, httpMethod, listener);
	}

	@Override
	public void onComplete(String response) {
//        new Http(this).pv(TAG, mUrl);
		SharePvRequest.send(TAG,mUrl,this);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				SouYueToast.makeText(ShareWeiboActivity.this, R.string.send_sucess, SouYueToast.LENGTH_LONG).show();
				if(UserAction.isLogin())
//					http.userPoint(UserAction.SHARETOSNS, UserAction.getUsername());
				if(!StringUtils.isEmpty(callback)){
//				    http.shareSuc(callback,CALL_BACK,mEdit.getText().toString());
					ShareSucRequest request = new ShareSucRequest(HttpCommon.SHARE_SUC_REQUESTID, callback, null);
					request.setParams(CALL_BACK, mEdit.getText().toString());
					CMainHttp.getInstance().doRequest(request);
				}
				if(!StringUtils.isEmpty(sharePointUrl)){
                       SharePointInfo info=new SharePointInfo();
                       info.setUrl(sharePointUrl);
                       info.setKeyWord(keyword);
                       info.setSrpId(srpId);
                       info.setPlatform(CALL_BACK);
//                       http.userSharePoint(info);
					ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
				}

			}
		});
		if (progressDialog.isShowing())
			progressDialog.dismiss();
		
		this.finish();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		LogUtil.e(TAG, e.getMessage());
		final ErrorInfo info = ErrorInfo.parse(e.getMessage());
		if(UrlConfig.isTest())
		{
			Toast.makeText(this, info.toString(), Toast.LENGTH_LONG).show();
		}
		try {
//			final WeiboHttpException httpe=(WeiboHttpException) e;
			runOnUiThread(new Runnable() {
			 @Override
			 public void run() {
//				 int code = httpe.getStatusCode();
				 int code = Integer.parseInt(info.error_code);
				 switch (code) {
					 case 400:
						 showToast(R.string.result_400);
						 break;
					 case 403:
						 showToast(R.string.result_403);
						 ShareByWeibo.getInstance().unAuth2(ShareWeiboActivity.this);
						 mSsoHandler=ShareByWeibo.getInstance().auth2(ShareWeiboActivity.this, ShareByWeibo.getInstance().new AuthDialogListener(ShareWeiboActivity.this));
						 break;
					 case 40038:
						 showToast(R.string.result_40038);
						 break;
					 case 40012:
						 showToast(R.string.result_40012);
						 break;
					 case 40023:
						 showToast(R.string.result_40023);
						 break;
					 case 40045:
						 showToast(R.string.result_40045);
						 break;
					 case 40008:
						 showToast(R.string.result_40008);
						 break;
					 case 40013:
						 showToast(R.string.result_40013);
						 break;
					 case 40025:
						 showToast(R.string.result_40025);
						 break;
					 case 40072:
						 showToast(R.string.result_40072);
						 break;
					 case 40111:
						 clearCookies(ShareWeiboActivity.this);
						 showToast(R.string.result_40111);
						 break;
					 case 10022:
					 case 10023:
					 case 10024:
						 showToast(R.string.result_10024);
						 break;
					 case 10013:
						 showToast(R.string.result_10013);
						 break;
					 case 10001:
					 case 10002:
					 case 10003:
						 showToast(R.string.result_10001);
						 break;
					 case 10009:
					 case 10010:
					 case 10011:
						 showToast(R.string.result_10009);
						 break;
					 case 10014:
						 showToast(R.string.result_10014);
						 break;
					 case 20003:
						 showToast(R.string.result_20003);
						 ShareByWeibo.getInstance().unAuth2(ShareWeiboActivity.this);
						 mSsoHandler=ShareByWeibo.getInstance().auth2(ShareWeiboActivity.this, ShareByWeibo.getInstance().new AuthDialogListener(ShareWeiboActivity.this));
						 break;
					 case 20012:
						 showToast(R.string.result_20012);
						 break;
					 case 20015:
						 showToast(R.string.result_20015);
						 break;
					 case 20016:
						 showToast(R.string.result_20016);
						 break;
					 case 20017:
					 case 20019:
						 showToast(R.string.result_20019);
						 break;
					 case 20020:
					 case 20018:
					 case 20021:
						 showToast(R.string.result_20021);
						 break;
					 case 20111:
						 showToast(R.string.result_20111);
						 break;
					 default:
						 showToast(R.string.result_default);
						 break;
				 }
			 }
     });
    	} catch (Exception ex){

    	}
     if (null != progressDialog && progressDialog.isShowing()) {
         progressDialog.dismiss();
     }
	}


	/**
	 *  显示信息
	 */
	public void showToast(int msg) {
		SouYueToast.makeText(this, msg, SouYueToast.LENGTH_SHORT).show();
	}
	
	/**
	 * 清楚cookies
	 * @param context
	 */
	private void clearCookies(Context context){
		AccessTokenKeeper.clear(context);
		CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
//        cookieSyncMngr.sync();
	}

//	@Override
//	public void onIOException(IOException e) {
//		showToast(R.string.result_400);
//		if (null != progressDialog && progressDialog.isShowing()) {
//			progressDialog.dismiss();
//		}
//	}

	private void showProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(ShareWeiboActivity.this.getString(R.string.weibo_shareing));
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
	

	/**
	 * 字符串长度
	 *  修改： 原 英文占0.5 个字符 ，中文占 1 个字符
	 *  为 全部占 1 个字符长度
	 *  @by qubian
	 * @param str
	 * @return
     */
	public static int getStrLen(String str) {
		int strLen = 0;
		if (StringUtils.isEmpty(str)) {
			return strLen;
		}
		int i = 0;
		char[] chs = str.toCharArray();
		while (i < chs.length) {
//			strLen  += (chs[i++] > 0xff) ? 2 : 1;
			strLen += 2 ;
			i++;
		}
		return strLen/2;
	}

//    @Override
//    public void onComplete4binary(ByteArrayOutputStream responseOS) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onError(WeiboException e) {
//    	try {
//      final WeiboHttpException httpe=(WeiboHttpException) e;
//      runOnUiThread(new Runnable() {
//
//         @Override
//         public void run() {
//             int code = httpe.getStatusCode();
//
//             switch (code) {
//             case 400:
//                 showToast(R.string.result_400);
//                 break;
//             case 403:
//                 showToast(R.string.result_403);
//                 ShareByWeibo.getInstance().unAuth2(ShareWeiboActivity.this);
//                 mSsoHandler=ShareByWeibo.getInstance().auth2(ShareWeiboActivity.this, ShareByWeibo.getInstance().new AuthDialogListener(ShareWeiboActivity.this));
//                 break;
//             case 40038:
//                 showToast(R.string.result_40038);
//                 break;
//             case 40012:
//                 showToast(R.string.result_40012);
//                 break;
//             case 40023:
//                 showToast(R.string.result_40023);
//                 break;
//             case 40045:
//                 showToast(R.string.result_40045);
//                 break;
//             case 40008:
//                 showToast(R.string.result_40008);
//                 break;
//             case 40013:
//                 showToast(R.string.result_40013);
//                 break;
//             case 20016:
//                 showToast(R.string.result_20016);
//                 break;
//             case 40025:
//                 showToast(R.string.result_40025);
//                 break;
//             case 40072:
//                 showToast(R.string.result_40072);
//                 break;
//             case 40111:
//                 clearCookies(ShareWeiboActivity.this);
//                 showToast(R.string.result_40111);
//                 break;
//             default:
//                 showToast(R.string.result_default);
//                 break;
//             }
//         }
//     });
//    	} catch (Exception ex){
//
//    	}
//     if (null != progressDialog && progressDialog.isShowing()) {
//         progressDialog.dismiss();
//     }
//
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
	/**
	 * @see {@link Activity#onNewIntent}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	@Override
	public void onResponse(BaseResponse baseResponse) {
		progressDialog.dismiss();
		switch (baseResponse.errCode) {
			case WBConstants.ErrorCode.ERR_OK:
				Toast.makeText(this, R.string.share_success, Toast.LENGTH_LONG).show();
				finish();
				break;
			case WBConstants.ErrorCode.ERR_CANCEL:
				Toast.makeText(this, R.string.share_btn_cancel, Toast.LENGTH_LONG).show();
				break;
			case WBConstants.ErrorCode.ERR_FAIL:
				Toast.makeText(this,
						getString(R.string.share_fail),
						Toast.LENGTH_LONG).show();
				break;
		}
	}
}
