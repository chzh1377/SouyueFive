package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.LayoutApi;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;

/**
 * 推荐给朋友
 *
 * @author huanglb@zhongsou.com
 */
public class RecommendFriendActivity extends RightSwipeActivity implements IShareContentProvider, PickerMethod {
    private Button btn_rf;
    private String[] appShare;
    private SsoHandler mSsoHandler;
    private ShareMenuDialog mShareMenuDialog;
    // add by trade
    private ImageView mDimension;
    private SYSharedPreferences mSysp = SYSharedPreferences.getInstance();
    private String mDimensional_imgUrl;
    public static final int BLACK = 0xff000000;
    public static final int WHITE = 0xffffffff;
    private ImageView logoIconImg;
    private ImageView logoNameImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //add by trade
        if (ConfigApi.isSouyue()) {
            setContentView(R.layout.recommend_friend);
        }

        ((TextView) findViewById(R.id.activity_bar_title)).setText(getString(R.string.settingActivity_recommend_friend));
        btn_rf = findView(R.id.btn_recomment_friend);

        appShare = getResources().getStringArray(R.array.appShare);

        btn_rf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogShare(v);
            }
        });
        initView();
        initData();
    }

    private void initView() {
        logoIconImg = (ImageView) findViewById(R.id.about_logo_icon_img);
        logoNameImg = (ImageView) findViewById(R.id.about_logo_name_img);
    }

    private void initData() {
        if (SouyueAPIManager.isLogin()) {
            String userId = "";
            User user = SYUserManager.getInstance().getUser();
            if (user != null) {
                userId = String.valueOf(user.userId());
                try {
                    logoIconImg.setImageBitmap(getBitMap(userId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            logoNameImg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取 1/2 分屏幕的 二维码图片大小
     *
     * @param userId
     * @return
     * @throws WriterException
     * @throws FileNotFoundException
     */
    public Bitmap getBitMap(String userId) throws WriterException, FileNotFoundException {
        DisplayMetrics display = getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
        int smallerDimension = width < height ? width : height;
        smallerDimension = (int) ((smallerDimension - 20 * display.density) * 1 / 2);
        String contentString = null;
        contentString = "http://souyue.mobi/?t=userim&uid=" + userId;
        return createQRCode(contentString, smallerDimension);
    }

    /**
     * 生成二维码图片
     *
     * @param str
     * @param widthAndHeight
     * @return
     * @throws WriterException
     * @throws FileNotFoundException
     */
    public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException, FileNotFoundException {
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


    /**
     * 分享对话框
     *
     * @param parent
     */
    private void showDialogShare(View parent) {
        if (mShareMenuDialog == null) {
            mShareMenuDialog = new ShareMenuDialog(this, this, ShareConstantsUtils.RECOMMENDFRIEND);
        }
        mShareMenuDialog.showBottonDialog();
    }

    @Override
    public ShareContent getShareContent() {

        Bitmap image = BitmapFactory.decodeResource(getResources(), LayoutApi.getDrawbleResourceId(R.drawable.about_logo_icon));

        if (SouyueAPIManager.isLogin()) {
            String userId = "";
            User user = SYUserManager.getInstance().getUser();
            if (user != null) {
                userId = String.valueOf(user.userId());
                try {
                    image = getBitMap(userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //add by trade start
        String content = String.format(appShare[0], CommonStringsApi.APP_NAME);
        String title = String.format(appShare[1], CommonStringsApi.APP_NAME);
        String picUrl = "";
        if (StringUtils.isNotEmpty(mDimensional_imgUrl)) {
//            image = mAQuery.getCachedImage(mDimensional_imgUrl);
            File fileImage = PhotoUtils.getImageLoader().getDiskCache().get(mDimensional_imgUrl);
            if(fileImage != null){
                image = BitmapFactory.decodeFile(fileImage.getAbsolutePath());
            }
            picUrl = mDimensional_imgUrl;
        }
        String sharlUrl = CommonStringsApi.SHARE2FRIENDS_URL;
        if (SouyueAPIManager.isLogin()) {
            String userId = "";
            User user = SYUserManager.getInstance().getUser();
            if (user != null) {
                userId = String.valueOf(user.userId());
                sharlUrl = "http://souyue.mobi/?t=userim&uid=" + userId;
            }
        }
        ShareContent shareContent = new ShareContent(title, sharlUrl, image, content, picUrl);
        //add by trade end

        shareContent.setFrom(ShareContent.SHARE_REC);

        return shareContent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    @Override
    public void loadData(int position) {
        ShareContent content = getShareContent();
        if (CMainHttp.getInstance().isNetworkAvailable(this)) {
            switch (position) {
                case ShareMenuDialog.SHARE_TO_SINA:
                    mSsoHandler = ShareByWeibo.getInstance().share(this, content);
                    break;
                case ShareMenuDialog.SHARE_TO_WEIX:
                    ShareByWeixin.getInstance().share(content, false);
                    break;
                case ShareMenuDialog.SHARE_TO_FRIENDS:
                    ShareByWeixin.getInstance().share(content, true);
                    break;
                case ShareMenuDialog.SHARE_TO_QQFRIEND://4.1.1新增分享qq好友
                    ShareByTencentQQ.getInstance().share(this, content);
                    break;
                case ShareMenuDialog.SHARE_TO_QQZONE://4.1.1新增分享qq空间
                    ShareByTencentQQZone.getInstance().share(this, content);
                    break;
                default:
                    break;
            }
        } else {
            SouYueToast.makeText(RecommendFriendActivity.this, getString(R.string.nonetworkerror), Toast.LENGTH_SHORT).show();
            return;
        }

    }
}