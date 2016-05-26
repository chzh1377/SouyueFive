package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.service.SaveImageTask;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class DimensionalCodeActivity extends BaseActivity implements View.OnClickListener, IShareContentProvider, OnItemClickListener, PickerMethod {

    private TextView text, dimen_code_title;
    private ImageView img, code_logo;
    private String id;
    private String keyWord;
    private String url;
    private Bitmap qrCodeBitmap;
    private File cacheFile;
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;
    //    private ShareMenu shareMenu;
    private View view_translucent;
    private LinearLayout suoyulayout;
    private SsoHandler mSsoHandler;
    public static final String INTENT_URL = "url";
    public static final String INTENT_K = "k";
    public static final String INTENT_ID = "id";
    public static final String INTENT_FROM_TYPE = "fromType";
    private ShareMenuDialog mShareMenuDialog;
    private int fromType;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.dimen_code);
        getInentData();
        initView();
        if (url != null) {
            MyImageLoader.imageLoader.displayImage(url, code_logo, MyImageLoader.options);
//            aq.id(code_logo).image(url, true, true, 0, 0, null, AQuery.FADE_IN);
        } else {
            code_logo.setVisibility(View.GONE);
        }
        setData();
    }

    private void getInentData() {
        keyWord = getIntent().getStringExtra(INTENT_K);
        id = getIntent().getStringExtra(INTENT_ID);
        url = getIntent().getStringExtra(INTENT_URL);
        fromType = getIntent().getIntExtra(INTENT_FROM_TYPE, 0);
        System.out.println("DimensionalCodeActivity-url=" + url);
    }

    private void setData() {
        dimen_code_title.setText(keyWord);
        DisplayMetrics display = getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
        int smallerDimension = width < height ? width : height;
        smallerDimension = (int) ((smallerDimension - 40 * display.density) * 7 / 8);
        String contentString = "";
        if(IntentUtil.QR_M_SEARCH != fromType){
        	contentString = "http://souyue.mobi/?k="+ StringUtils.enCodeRUL(keyWord) + "&i=" + id;
        }else {
        	contentString = StringUtils.enCodeRUL(keyWord);
        }
        String dir = ImageUtil.getDir();
        if (dir != null)
            cacheFile = new File(dir, "cache_qr.png");
        FileOutputStream out = null;
        try {
            qrCodeBitmap = createQRCode(contentString, smallerDimension);
            img.setImageBitmap(qrCodeBitmap);
            if (cacheFile != null) {
                if (!cacheFile.exists()) {
                    File file=new File(dir);
                    if (!file.isDirectory()){
                        file.mkdirs();
                    }
                    cacheFile.createNewFile();
                }


                out = new FileOutputStream(cacheFile);
//                Bitmap bm = getBitmapFromView(img);
//                if (bm != null) {
//                    bm.compress(CompressFormat.PNG, 100, out);
//                }
                double p = 1.0;
                double size = qrCodeBitmap.getRowBytes() * qrCodeBitmap.getHeight();
                double limit = 500 * 1024 * 8;
                if (size > limit) {
                    p = limit / size - 0.1;
                }
                qrCodeBitmap.compress(CompressFormat.PNG, (int) (p * 100), out);
            }

        } catch (WriterException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(out);
        }
        text.setText(Html.fromHtml("<font color='#747474' size='24px'>用"+"\"</font><font color='#da1919' size='24px'>"+CommonStringsApi.APP_NAME_SHORT+"</font><font color='#747474' size='24px'>\""+"扫一扫上面的二维码,即可订阅</font>"));
    }

    private void closeStream(Closeable out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        dimen_code_title = (TextView) findViewById(R.id.dimen_code_title);
        text = (TextView) findViewById(R.id.dimen_code_text);
        img = (ImageView) findViewById(R.id.dimen_code_img);
        code_logo = (ImageView) findViewById(R.id.code_logo);
        findViewById(R.id.dimen_code_save).setOnClickListener(this);
        findViewById(R.id.dimen_code_share).setOnClickListener(this);
        view_translucent = findViewById(R.id.view_translucent);
        view_translucent.getBackground().setAlpha(100);
        suoyulayout = (LinearLayout) findViewById(R.id.suoyulayout);
    }

//    public Bitmap getBitmapFromView(View view) {
//        view.destroyDrawingCache();
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        view.setDrawingCacheEnabled(true);
//        Bitmap bitmap = view.getDrawingCache(true);
//        return bitmap;
//    }

    public Bitmap createQRCode(String str, int widthAndHeight) throws WriterException, FileNotFoundException {
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

    public void onGoBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dimen_code_save:
                System.out.println("cacheFile.getAbsolutePath()=" + cacheFile.getAbsolutePath());
                new SaveImageTask(this).execute(cacheFile.getAbsolutePath());
                break;
            case R.id.dimen_code_share:
//                shareMenu = new ShareMenu(this, true);
//                shareMenu.setOnItemClickListener(this);
//                showTranslucentView(true, false);
//                shareMenu.showAsDropDown(suoyulayout, true);
//                shareMenu.popupWindow.setOnDismissListener(new OnDismissListener() {
//                    public void onDismiss() {
//                        showTranslucentView(false, false);
//                    }
//                });
                showShareWindow(suoyulayout);
                break;
            default:
                break;
        }
    }

    @Override
    public ShareContent getShareContent() {
        ShareContent result = new ShareContent();
        result.setKeyword(keyWord);
        result.setDimensionalcode(1);
        result.setDimenImg(cacheFile);
        return result;
    }

    public void showTranslucentView(boolean show, boolean showTitle) {
        if (show) {
            view_translucent.bringToFront();
            view_translucent.setVisibility(View.VISIBLE);
        } else {
            view_translucent.setVisibility(View.GONE);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(), getString(R.string.sdcard_exist), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
            SouYueToast.makeText(DimensionalCodeActivity.this, getString(R.string.nonetworkerror), Toast.LENGTH_SHORT).show();
            return;
        }
//        shareMenu.dismiss();

    }

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
    //分享到第三方
//    private void dimensionalCodeShare(Integer selectionId){
//        
//        final ShareContent content = getShareContent();
//             switch (selectionId) {
//                case ShareMenu.SHARE_TO_SINA:
//                    mSsoHandler=ShareByWeibo.getInstance().share(DimensionalCodeActivity.this, content);
//                    break;
//                case ShareMenu.SHARE_TO_WEIX:
//                    ShareByWeixin.getInstance().share(content, false);
//                    break;
//                case ShareMenu.SHARE_TO_FRIENDS:
//                    String wxFriendUrl = content.getUrl();
//                    content.setUrl(wxFriendUrl);
//                    ShareByWeixin.getInstance().share(content, true);
//                    break;
//                case ShareMenu.SHARE_TO_EMAIL:
//                    ShareByEmailOrOther.shareByEmail(DimensionalCodeActivity.this, content);
//                    break;
//                case ShareMenu.SHARE_TO_RENREN:
//                    ShareByRenren.getInstance().share(DimensionalCodeActivity.this, content);
//                    break;
//                case ShareMenu.SHARE_TO_TWEIBO:
//                    ShareByTencentWeiboSSO.getInstance().share(DimensionalCodeActivity.this, content);
//                    break;
//                } 
//    }

    private void showShareWindow(View parent) {
        if (mShareMenuDialog == null) {
            mShareMenuDialog = new ShareMenuDialog(this, this, ShareConstantsUtils.DIMENSIONALCODE);
        }
        mShareMenuDialog.showBottonDialog();

    }

    @Override
    public void loadData(int position) {
        final ShareContent content = getShareContent();
        switch (position) {
            case ShareMenuDialog.SHARE_TO_SINA:
                mSsoHandler = ShareByWeibo.getInstance().share(DimensionalCodeActivity.this, content);
                break;
            case ShareMenuDialog.SHARE_TO_WEIX:
                ShareByWeixin.getInstance().share(content, false);
                break;
            case ShareMenuDialog.SHARE_TO_FRIENDS:
                String wxFriendUrl = content.getUrl();
                content.setUrl(wxFriendUrl);
                ShareByWeixin.getInstance().share(content, true);
                break;
            case ShareMenuDialog.SHARE_TO_QQFRIEND://4.1.1新增分享qq好友
            	ShareByTencentQQ.getInstance().share(DimensionalCodeActivity.this, content);
                break;
            case ShareMenuDialog.SHARE_TO_QQZONE://4.1.1新增分享qq空间
            	ShareByTencentQQZone.getInstance().share(DimensionalCodeActivity.this, content);
                break;
        }

    }
}

