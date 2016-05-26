package com.zhongsou.souyue.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.net.share.SharePvRequest;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.WXState;
import junit.framework.Assert;

import java.io.File;
import java.io.RandomAccessFile;

public class ShareByWeixin {
	private static final String TAG = "ShareByWeixin";
//	private static final int SHAREPV_REQUESTID = 9874471;
	/*
	 * 测试账号 AppID wx360a9785675a8653 AppKey b97fd1cf3e773428c90d320e5cbc1ea1
	 */
	public static String APP_ID = ShareApi.WEIXIN_APP_ID;
	/*public static String APP_ID = MobclickAgent.getConfigParams(MainApplication.getInstance(), "WEIXIN_APP_ID");// "wx360a9785675a8653";//;"wxd7483d206572082e";
	static {
		if (StringUtils.isEmpty(APP_ID)) {
			APP_ID = ShareAppKeyUtils.WX_APP_ID;
		}
		Log.d("WEIXIN_APP_ID", APP_ID);
	}*/
	private static ShareByWeixin singleton;
	public IWXAPI api;
    private ShareByWeixin() {
		Log.d("WEIXIN_APP_ID", APP_ID);
		api = WXAPIFactory.createWXAPI(MainApplication.getInstance(), APP_ID, false);
		api.registerApp(APP_ID);
	}

	public static ShareByWeixin getInstance() {
		if (singleton == null) {
			singleton = new ShareByWeixin();
		}
		return singleton;
	}

	public IWXAPI getIWXAPI() {
		return api;
	}

	public void share(ShareContent content, boolean isFriends) {
//        new Http(this).pv(isFriends ? "weixinquan" : "weixinfriend", content.getUrl());
		SharePvRequest.send(isFriends ? "weixinquan" : "weixinfriend", content.getUrl(), SharePvRequest.EMPTY_RESPONSE);
		if (api.isWXAppInstalled()) {
			if (isFriends && !isCircleOfFriends(api)) {
				SouYueToast.makeText(MainApplication.getInstance(), R.string.wx_version, 1).show();
				return;
			}
			sendWebpage(content, isFriends);
		} else {
			String weixin_tips = MainApplication.getInstance().getString(R.string.weixin_tips);
			if (StringUtils.isNotEmpty(weixin_tips))
				SouYueToast.makeText(MainApplication.getInstance(), weixin_tips, 1).show();
		}
	}

	public void sendWebpage(ShareContent content, boolean isFriends) {
		if(isFriends)
			WXState.changeWXState(WXState.TIMELINE);
		else
			WXState.changeWXState(WXState.SESSION);
	    boolean dimen = content.getDimensionalcode()==1;
	    String path = dimen ?content.getDimensionalCodeFile().getAbsolutePath() : content.getTempImageFilePath();
	    WXMediaMessage msg = new WXMediaMessage();
	    if(dimen){
	        if (StringUtils.isEmpty(path)){
	            Toast.makeText(MainApplication.getInstance(), "分享失败", Toast.LENGTH_SHORT).show();
	            return;
	        }
	        WXImageObject imageobject=new WXImageObject();
	        imageobject.setImagePath(path);
	        msg= new WXMediaMessage(imageobject);
	    }else{
	        WXWebpageObject webpage = new WXWebpageObject();
	        webpage.webpageUrl = content.getUrl();
	        msg = new WXMediaMessage(webpage);
	        
	       
	    }
	       if (isFriends){
	            if(content.getDimensionalcode()==1){
	                if(StringUtils.isNotEmpty(content.getCodeContent())){
	                    msg.title=content.getCodeContent();
	                }else{
	                    msg.title = content.getWeiXinTitle() + ":";
	                }
	            }else{
	                if(StringUtils.isNotEmpty(content.getTitle())){
	                    msg.title = content.getTitle();
	                }else{
	                    msg.title = content.getWeiXinTitle()+ ":"; 
	                }
	            }
	        }else{
	            msg.title = content.getTitle();
	        }
	       

        if (!isFriends){
            if(content.getDimensionalcode()==1){
                msg.description = content.getCodeContent();
            }else{
                msg.description = content.getWeiXinContent();
            } 
        }
		if (StringUtils.isNotEmpty(path)) {
            if (content.getDimensionalcode() == 1) {
                msg.setThumbImage(extractThumbNail(path,  260, 260, true));
		    }else{
		        msg.setThumbImage(extractThumbNail(path, 150, 150, true));
		    }
		} else {
			Bitmap bitmap = ImageUtil.drawableToBitmap( 150, 150);
			if (null != bitmap) {
				msg.setThumbImage(bitmap);
			}
			bitmap.recycle();
		}
		if(!StringUtils.isEmpty(content.getCallback())){
		    SYSharedPreferences.getInstance().putString(SYSharedPreferences.SHARECALLBACK, content.getCallback());
		    SYSharedPreferences.getInstance().putString(SYSharedPreferences.CONTENT, msg.description);
		    
		}
		
	      if(content!=null&&!StringUtils.isEmpty(content.getSharePointUrl())){
	          SYSharedPreferences.getInstance().putString(SYSharedPreferences.SHAREURL, content.getSharePointUrl());
	          SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEYWORD, content.getKeyword());
	          SYSharedPreferences.getInstance().putString(SYSharedPreferences.SRPID,content.getSrpId());
	         }
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = getScene(isFriends);
		api.sendReq(req);//send request to wechat app ** true if sent  **
		
	}



    /**
	 * 微信4.2以上支持，如果需要检查微信版本支持API的情况，
	 * 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈
	 * 
	 * @return
	 */
	private int getScene(boolean isFriends) {
		if (isFriends) {
			return SendMessageToWX.Req.WXSceneTimeline;
		} else {
			return SendMessageToWX.Req.WXSceneSession;
		}
	}

	private boolean isCircleOfFriends(IWXAPI api) {
		if (api.getWXAppSupportAPI() >= 0x21020001)
			return true;
		return false;
	}

	public void sendApp(ShareContent content) {
		String path = content.getTempImageFilePath();

		final WXAppExtendObject appdata = new WXAppExtendObject();

		// if(null!=path){
		// appdata.fileData = readFromFile(path, 0, -1);
		// }

		appdata.extInfo = content.getUrl();

		final WXMediaMessage msg = new WXMediaMessage();
		if (null != path) {
			msg.setThumbImage(extractThumbNail(path, 150, 150, true));
		} else {
			Bitmap bitmap = ImageUtil.drawableToBitmap(150, 150);
			if (null != bitmap)
				msg.setThumbImage(bitmap);
			bitmap.recycle();
		}
		msg.title = content.getTitle();
		// msg.description = content.getWeixinContent();
		msg.mediaObject = appdata;

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("appdata");
		req.message = msg;
		// req.scene = 0;
		api.sendReq(req);
	}

	public void sendText(String text) {
		if (text == null || text.length() == 0) {
			return;
		}

		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = text;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		// req.scene = isTimelineCb.isChecked() ?
		// SendMessageToWX.Req.WXSceneTimeline :
		// SendMessageToWX.Req.WXSceneSession;
		// req.scene = getScene(api);
		// 调用api接口发送数据到微信
		api.sendReq(req);
	}

	/**
	 * 对应该请求的事务ID，通常由Req发起，回复Resp时应填入对应事务ID
	 * 
	 * @param type
	 * @return
	 */
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	public static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			Log.i(TAG, "readFromFile: file not found");
			return null;
		}

		if (len == -1) {
			len = (int) file.length();
		}

		Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));

		if (offset < 0) {
			Log.e(TAG, "readFromFile invalid offset:" + offset);
			return null;
		}
		if (len <= 0) {
			Log.e(TAG, "readFromFile invalid len:" + len);
			return null;
		}
		if (offset + len > (int) file.length()) {
			Log.e(TAG, "readFromFile invalid file len:" + file.length());
			return null;
		}

		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // 创建合适文件大小的数组
			in.seek(offset);
			in.readFully(b);
			in.close();

		} catch (Exception e) {
			Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
			e.printStackTrace();
		}
		return b;
	}

	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

	public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}

			Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
			if (scale != null) {
				//bm.recycle();
				bm = scale;
			}

			if (crop && !bm.isRecycled()) {
				final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				//bm.recycle();
				bm = cropped;
				Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
			}
			return bm;

		} catch (final OutOfMemoryError e) {
            e.printStackTrace();
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}
}
