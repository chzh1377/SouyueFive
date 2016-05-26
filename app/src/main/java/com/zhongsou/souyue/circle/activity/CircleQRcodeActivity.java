package com.zhongsou.souyue.circle.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.PickerMethod;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.model.CircleCardInfo;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.BitmapUtil;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CircleGetCircleInfoRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.utils.ChangeSelector;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.view.ShareSNSDialog;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/** 
 * @author : zoulu
 * 2014年7月14日
 * 下午2:31:18 
 * 类说明 :圈子二维码
 */
public class CircleQRcodeActivity extends BaseActivity implements PickerMethod , IShareContentProvider{
//	private static final int CIRCLEGETCIRCLEINFO_REQUESTID = 6587; // 获取圈子信息
	private ImageButton btn_cricle_edit;
	private ImageButton btn_cricle_option;
	private TextView activity_bar_title;
	private ImageView circle_qrcode;
	private Bitmap qrCodeBitmap;
	private int BLACK = 0xff000000;
    private int WHITE = 0xffffffff;
    public static final String NAME = "name";
    public static  final String INTERESTID = "interestid";
    public static final String IMAGEURL = "imageurl";
    public static final String INTEREST_DESC = "interest_desc";
    private TextView circle_name;
    private Button btn_share;
    private ShareSNSDialog dialog ;
    private List<Integer> integers = new ArrayList<Integer>();
    //分享内容
    private ShareContent content;
    private String imageUrl;//圈logo url
//    private AQuery query;
//    private Http http;
    private Bitmap imageBitmap;
    private String title;//全名
    private String interest_desc;  //圈简介
    private long interest;//圈子id
    private String openUrl;//落地页
    private SsoHandler mSsoHandler;
    
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.circleqrcode_activity);
//        http = new Http(this);
		initUI();
	}
	
	@SuppressWarnings("deprecation")
	private void initUI(){
		title = getIntent().getStringExtra(NAME);
        interest_desc = getIntent().getStringExtra(INTEREST_DESC);
		interest = getIntent().getLongExtra(INTERESTID, 0);
//        http.getCircleCardInfomation(interest, 1);
		CircleGetCircleInfoRequest.send(HttpCommon.CIRCLE_GETCIRCLEINFO_REQUESTID,this,interest,Constant.INTEREST_TYPE_PRIVATE);
		imageUrl = getIntent().getStringExtra(IMAGEURL);
		btn_cricle_edit = (ImageButton) findViewById(R.id.btn_cricle_edit);
		btn_cricle_option = (ImageButton) findViewById(R.id.btn_cricle_option);
		activity_bar_title = (TextView) findViewById(R.id.activity_bar_title);
		circle_qrcode = (ImageView) findViewById(R.id.circle_qrcode);
		circle_name = (TextView) findViewById(R.id.circle_name);
		btn_share = (Button) findViewById(R.id.btn_share);
		btn_share.setBackgroundDrawable(ChangeSelector.addStateDrawable(this, R.drawable.circleshow,  R.drawable.circleshowclick,  R.drawable.circleshowclick));
		btn_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showShareWindow(arg0);
			}
		});
		circle_name.setText(title);
		btn_cricle_edit.setVisibility(View.GONE);
		btn_cricle_option.setVisibility(View.INVISIBLE);
		activity_bar_title.setText("圈二维码");
		//add by liudl
		((TextView) findViewById(R.id.tv_join_circle_scan))
		    .setText(String.format(CommonStringsApi.getStringResourceValue(R.string.join_circle_scan),CommonStringsApi.APP_NAME_SHORT));
		
		DisplayMetrics display = getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
		int smallerDimension = width < height ? width : height;
		smallerDimension = (int) ((smallerDimension - 40 * display.density) * 7 / 8);
	    String contentString = "http://souyue.mobi/?t=i&i=" + interest;
		try {
			qrCodeBitmap = createQRCode(contentString, smallerDimension);
			circle_qrcode.setImageBitmap(qrCodeBitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showShareWindow(View parent) {
		integers.clear();
		integers.add(9);
		integers.add(1);
		if(StringUtils.isNotEmpty(ShareApi.WEIXIN_APP_ID)){
		    integers.add(2);
		    integers.add(3);
		}
		integers.add(7);
		integers.add(4);
		integers.add(11);
		integers.add(12);
		dialog = new ShareSNSDialog(this, this, integers);
		dialog.showBottonDialog();
	}
	
	/**
	 * 生成二维码图片
	 * @param str
	 * @param widthAndHeight
	 * @return
	 * @throws WriterException
	 * @throws FileNotFoundException
	 */
	 public Bitmap createQRCode(String str,int widthAndHeight) throws WriterException, FileNotFoundException {  
	        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();    
	        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");   
	        BitMatrix matrix = new MultiFormatWriter().encode(str,  
	                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);  
	        int width = matrix.getWidth();  
	        int height = matrix.getHeight();  
	        int[] pixels = new int[width * height];  
	          
	        for (int y = 0; y < height; y++) {  
	            for (int x = 0; x < width; x++) {  
	                if (matrix.get(x, y)) {  
	                    pixels[y * width + x] = BLACK;  
	                } else {
	                    pixels[y * width + x] = WHITE;
	                }
	            }  
	        }  
	        Bitmap bitmap = Bitmap.createBitmap(width, height,  
	                Bitmap.Config.ARGB_8888); 
	        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
	        return bitmap;  
	    }

	@Override
	public void loadData(int args) {
		if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(), getString(R.string.sdcard_exist), Toast.LENGTH_SHORT).show();
            return;
        }
		 if (CMainHttp.getInstance().isNetworkAvailable(this)) {
	            content = getShareContent();
	            switch (args) {
				case ShareSNSDialog.SHARE_TO_SYIMFRIEND:
					 boolean islogin=( SYUserManager.getInstance().getUser().userType().equals(SYUserManager.USER_ADMIN));
	                    if(islogin){
	                        ImShareNews imsharenews=
	                                new ImShareNews(content.getKeyword(),
	                                    content.getSrpId(),content.getTitle(),
	                                    content.getSharePointUrl(),content.getPicUrl());
//	                                ContactsListActivity.startSYIMFriendAct(this,imsharenews);   
	                                UIHelper.showImFriend(CircleQRcodeActivity.this, interest,true,imageUrl, title, null,4,false,content.getSharePointUrl(),String.valueOf(interest));
	                    }else{
	                        IntentUtil.gotoLogin(this);
	                    }
					break;
				case ShareSNSDialog.SHARE_TO_SINA:
					 mSsoHandler = ShareByWeibo.getInstance().share(this, content);
					break;
				case ShareSNSDialog.SHARE_TO_WEIX:
                    content.setContent(title + "  " + interest_desc);
					ShareByWeixin.getInstance().share(content, false);
					break;
				case ShareSNSDialog.SHARE_TO_FRIENDS:
                    content.setContent(title + "  " + interest_desc);
                    ShareByWeixin.getInstance().share(content, true);
					break;
				case ShareSNSDialog.SHARE_TO_QQFRIEND://4.1.1新增分享qq好友
                	ShareByTencentQQ.getInstance().share(this, content);
                    break;
                case ShareSNSDialog.SHARE_TO_QQZONE://4.1.1新增分享qq空间
                	ShareByTencentQQZone.getInstance().share(this, content);
                    break;	
				default:
					break;
				}
		 }
		
	}

	@Override
	public ShareContent getShareContent() {
		if (!TextUtils.isEmpty(imageUrl)) {
//			query = new AQuery(this);
//            imageBitmap = query.getCachedImage(imageUrl);
			imageBitmap = BitmapUtil.decodeFile(PhotoUtils.getImageLoader().getDiskCache().get(imageUrl).getAbsolutePath());
		}else{
			imageBitmap = null;
		}
		openUrl = UrlConfig.shareInterestCard + interest + CommonStringsApi.getUrlAppendIgId();
		ShareContent result = new ShareContent(title, openUrl, imageBitmap, title, imageUrl);
		result.setSharePointUrl(openUrl);
		result.setKeyword("");
		result.setSrpId("");
		return result;
	}  
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode,resultCode,data);
		// TODO Auto-generated method stub
		if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
	}

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.CIRCLE_GETCIRCLEINFO_REQUESTID: // 圈子信息
				getCircleCardInfomationSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {

	}

	public void getCircleCardInfomationSuccess(HttpJsonResponse resp){
		CircleCardInfo circleCardInfo = new Gson().fromJson(resp.getBody(),
				CircleCardInfo.class);
		//拿到圈子描述
		interest_desc = circleCardInfo.getInterest_desc();
    }
}
