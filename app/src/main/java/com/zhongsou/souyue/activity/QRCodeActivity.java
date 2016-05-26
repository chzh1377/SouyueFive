package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.util.FileUtil;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.im.util.ScreenShot;
import com.zhongsou.souyue.service.SaveImageTask;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MyImageLoader;

import java.io.FileNotFoundException;
import java.util.Hashtable;
/**
 * 个人中心-二维码
 * @author Administrator
 */
public class QRCodeActivity extends RightSwipeActivity  implements View.OnClickListener{

	private ImageView ivHeadIcon;
	private TextView tvName;
	private TextView tvSignature;
	private ImageView ivTwoDimenCodePic;
	private TextView tvTitle;
	
	private Intent data;
	private String name ;
	private String image;
	private String signature;
	private Long userid;

	private Bitmap qrCodeBitmap;
	public static final int BLACK = 0xff000000;
    public static final int WHITE = 0xffffffff;
    private RelativeLayout save;
    private RelativeLayout sharetoFriend;
    private RelativeLayout relativeshoot;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.my_two_dimen_code);
		data = getIntent();
		name = data.getStringExtra("name");
		image = data.getStringExtra("image");
		signature = data.getStringExtra("signature");
		userid = data.getLongExtra("userid", -1l);
		
		initView();
		initData();
	}

	private void initView() {
		ivHeadIcon = (ImageView) findViewById(R.id.iv_my_head_icon);
		tvName = (TextView) findViewById(R.id.tv_my_nikename);
		tvSignature = (TextView) findViewById(R.id.tv_my_signature);
		ivTwoDimenCodePic = (ImageView) findViewById(R.id.iv_two_dimen_code_pic);
		tvTitle = (TextView) findViewById(R.id.activity_bar_title);
		relativeshoot = (RelativeLayout) findViewById(R.id.forshoot);
		save = (RelativeLayout) findViewById(R.id.re_savetocard);
		save.setOnClickListener(this);
		sharetoFriend = (RelativeLayout) findViewById(R.id.sharetofriend);
		sharetoFriend.setOnClickListener(this);
		ivHeadIcon.setOnClickListener(this);
	}
	
	private void initData() {
		if(name != null) {
			tvName.setText(name);
		}
		if(image != null) {
            MyImageLoader.imageLoader.displayImage(image, ivHeadIcon, MyImageLoader.options);
//			aq.id(ivHeadIcon).image(image, true, true);
		}
		if(signature != null && !"".equals(signature)) {
			tvSignature.setText(signature);
		}else{
			tvSignature.setText("这家伙很懒，尚未填写签名");
		}
		
		tvTitle.setText("二维码名片");
		
		String userId = "";
		if(userid > 0) {
			userId = String.valueOf(userid);
		}
		DisplayMetrics display = getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
		int smallerDimension = width < height ? width : height;
		smallerDimension = (int) ((smallerDimension - 20 * display.density) * 7 / 8);
		String contentString = null;
		if(userid != null) {
	     contentString = "http://souyue.mobi/?t=userim&uid=" + userId;
		}
		try {
			qrCodeBitmap = createQRCode(contentString, smallerDimension);
			ivTwoDimenCodePic.setImageBitmap(qrCodeBitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 生成二维码图片
	 * @param str
	 * @param widthAndHeight
	 * @return
	 * @throws WriterException
	 * @throws FileNotFoundException
	 */
	 public static Bitmap createQRCode(String str,int widthAndHeight) throws WriterException, FileNotFoundException {  
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
	                Bitmap.Config.RGB_565);
	        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
	        return bitmap;  
	    }

	@Override
	public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_my_head_icon:
                if (IntentUtil.isLogin()) {
                    PersonPageParam param = new PersonPageParam();
                    param.setViewerUid(SouyueAPIManager.getInstance().getUserInfo().userId());
                    param.setFrom(PersonPageParam.FROM_OTHER);
                    UIHelper.showPersonPage(QRCodeActivity.this,param);
                } else
                    IntentUtil.goLogin(QRCodeActivity.this, true);
                break;
            case R.id.re_savetocard:
                try {
                    Bitmap bitmap = ScreenShot.getViewBitmap(relativeshoot);
                    if (bitmap != null)
                        new SaveImageTask(this).execute(FileUtil.saveImg(bitmap, System.currentTimeMillis()+".png"));
                    else
                        Toast.makeText(QRCodeActivity.this,"图片保存失败",Toast.LENGTH_SHORT).show();
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sharetofriend:
                IMIntentUtil.gotoShowPersionalCardToContactList(this);
                break;
        }
	}
	 
	 

}
