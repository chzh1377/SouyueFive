package com.zhongsou.souyue.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.upyun.api.UploadImageTask;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.module.MyPoints;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.personal.UserIntegral;
import com.zhongsou.souyue.net.personal.UserRepairInfo;
import com.zhongsou.souyue.net.volley.*;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ThreadPoolUtil;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by yinguanping on 15/1/12.
 * 个人信息界面
 */
public class Mine_ItemActivity extends BaseActivity implements
        View.OnClickListener {

    private TextView my_username, tv_signatrue, my_info_levelTip, tvUserNameTextView, tvSexTextView;
    private ImageView head_photo;
    private User user;
    private ProgressDialog pd;
    private File profileImgFile;
    private Uri imageFileUri;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.mine_item);

        pd = new ProgressDialog(this);
        pd.setMessage(this.getString(R.string.data_loading));
        pd.setCanceledOnTouchOutside(false);
        profileImgFile = new File(this.getCacheDir(), "headphoto_");

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setName();
    }

    private void initView() {
        findViewById(R.id.goBack).setOnClickListener(this);
        ((TextView) findViewById(R.id.activity_bar_title))
                .setText("个人信息");
        head_photo = (ImageView) findViewById(R.id.im_user_info_head);
        tv_signatrue = (TextView) findViewById(R.id.tv_my_info_signatrue);
        my_username = (TextView) findViewById(R.id.tv_my_info_username);
        my_info_levelTip = (TextView) findViewById(R.id.my_info_levelTip);
        tvSexTextView = (TextView) findViewById(R.id.tv_my_info_sex);//
        tvUserNameTextView = (TextView) findViewById(R.id.tv_my_info_mynamevalue);//

        findViewById(R.id.rv_my_info_update_photo).setOnClickListener(this);
        findViewById(R.id.rv_my_info_update_username).setOnClickListener(this);
        findViewById(R.id.rv_my_info_my_sex).setOnClickListener(this);
        findViewById(R.id.rv_my_info_two_dimen_code).setOnClickListener(this);
        findViewById(R.id.rv_my_info_update_signatrue).setOnClickListener(this);
        findViewById(R.id.my_info_level).setOnClickListener(this);
    }

    private void setName() {
        user = SYUserManager.getInstance().getUser();
        if (user != null) {
            my_username.setText(user.name());// 不经过处理直接按最大的值显示，显示不开末尾补点。。。
            if (tvUserNameTextView != null) {
                tvUserNameTextView.setText(user.userName());
            }
            if (tv_signatrue != null)
                tv_signatrue.setText(user.signature());
            UserIntegral inte = new UserIntegral(HttpCommon.USER_INTERNAL_REQUEST,this);
            inte.setParams(user.userName());
            mMainHttp.doRequest(inte);
//            http.integral(user.userName());
            MyImageLoader.imageLoader.displayImage(user.image(), head_photo,
                    MyImageLoader.options);
            setSexValue();
        }
    }

    public void integralSuccess(MyPoints points) {
        if (points != null) {
            my_info_levelTip.setText("LV" + points.getUserlevel() + " "
                    + points.getUserleveltitle());
        }
    }

    private void setSexValue() {
        int sex = user.getSex();
        switch (sex) {
            case EditSexActivity.USER_MALE:
                tvSexTextView.setText(R.string.my_info_sex_male);
                break;
            case EditSexActivity.USER_FEMALE:
                tvSexTextView.setText(R.string.my_info_sex_female);
                break;
            default:
                tvSexTextView.setText(R.string.my_info_sex_male);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        switch (v.getId()) {
            case R.id.goBack:
                super.onBackPressed();
                break;
            case R.id.rv_my_info_update_photo:
                ShowPickDialog();
                break;
            case R.id.rv_my_info_update_username:
                if (user != null) {
                    i.setClass(this, EditNickNameActivity.class);
                    i.putExtra(EditNickNameActivity.INTENT_USER,
                            user);
                    startAcByAnim(i);
                }
                break;
            case R.id.rv_my_info_my_sex:
                if (user != null) {
                    i.setClass(this, EditSexActivity.class);
                    i.putExtra(EditSexActivity.INTENT_USER, user);
                    startAcByAnim(i);
                }
                break;
            case R.id.rv_my_info_update_signatrue:
                i.setClass(this, SignatureActivity.class);
                startAcByAnim(i);
                break;
            case R.id.rv_my_info_two_dimen_code:
                i.putExtra("name", user.name());
                i.putExtra("image", user.image());
                i.putExtra("signature", user.signature());
                i.putExtra("userid", user.userId());
                i.setClass(this, QRCodeActivity.class);
                startAcByAnim(i);
                break;
            case R.id.my_info_level:// 等级
                if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    SouYueToast.makeText(this,
                            getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                IntentUtil
                        .gotoWeb(this,
                                createAccessUrl(UrlConfig.HOST_ZHONGSOU_JF
                                        + "index"), null);
                break;
            default:
                break;
        }
    }

    private void startAcByAnim(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private String createAccessUrl(String url) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?username=").append(user.userName()).append("&token=")
                .append(user.token()).append("&r=")
                .append(System.currentTimeMillis());
        return sb.toString();
    }

    /**
     * 选择提示对话框
     */
    public void ShowPickDialog() {
        String shareDialogTitle = getString(R.string.pick_dialog_title);
        MMAlert.showAlert(this, shareDialogTitle, getResources()
                        .getStringArray(R.array.picks_item), null,
                new MMAlert.OnAlertSelectId() {

                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case 0: // 拍照
                                try {
                                    imageFileUri = Mine_ItemActivity.this
                                            .getContentResolver()
                                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    new ContentValues());
                                    LogDebugUtil.v("FAN", imageFileUri + "");
                                    if (imageFileUri != null) {
                                        Intent i = new Intent(
                                                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                        i.putExtra(
                                                android.provider.MediaStore.EXTRA_OUTPUT,
                                                imageFileUri);
                                        if (Utils.isIntentSafe(Mine_ItemActivity.this, i)) {
                                            startActivityForResult(i, 2);
                                        } else {
                                            SouYueToast
                                                    .makeText(
                                                            Mine_ItemActivity.this,
                                                            getString(R.string.dont_have_camera_app),
                                                            SouYueToast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    } else {
                                        SouYueToast
                                                .makeText(
                                                        Mine_ItemActivity.this,
                                                        getString(R.string.cant_insert_album),
                                                        SouYueToast.LENGTH_SHORT)
                                                .show();
                                    }
                                } catch (Exception e) {
                                    SouYueToast.makeText(Mine_ItemActivity.this,
                                            getString(R.string.cant_insert_album),
                                            SouYueToast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1: // 相册
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setDataAndType(
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        "image/*");
                                startActivityForResult(intent, 1);
                                break;
                            default:
                                break;
                        }
                    }

                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:// 如果是直接从相册获取
                    if (data != null) {
                        Uri uri = data.getData();
                        startPhotoZoom(uri);
                    }
                    break;
                case 2:// 如果是调用相机拍照时
                    String picPath = null;
                    if (imageFileUri != null) {
                        picPath = Utils.getPicPathFromUri(imageFileUri, this);
                        int degree = 0;
                        if (!StringUtils.isEmpty(picPath))
                            degree = ImageUtil.readPictureDegree(picPath);
                        Matrix matrix = new Matrix();
                        if (degree != 0) {// 解决旋转问题
                            matrix.preRotate(degree);
                        }
                        LogDebugUtil.v("Huang", "imageFileUri != null--picPath="
                                + picPath);
                        Uri uri = Uri.fromFile(new File(picPath));
                        startPhotoZoom(uri);
                    } else {
                        SouYueToast.makeText(this, "图片获取异常",
                                SouYueToast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:// 取得裁剪后的图片
//                    if (data != null) {
//                        setPicToView(data);
//                    }
                    Bitmap bitmap =null;
                    try {
                         bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    setPicToView(bitmap);
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        try {
            LogDebugUtil.v("FAN", "startPhotoZoom URL: " + uri);
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX",640);
            intent.putExtra("outputY", 640);
            intent.putExtra("return-data", false);
            uritempFile= Uri.parse("file://"+getHeadImagePath(mContext)+ "head.jpg");
            Log.i(TAG,"startPhotoZoom:"+uritempFile.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(intent, 3);
        } catch (Exception e) {
            SouYueToast.makeText(this, "图片裁剪异常",
                SouYueToast.LENGTH_SHORT).show();
        }

    }
    Uri uritempFile;
    private static final String HEAD_IMAGE = "souyue/image/";

    public static String getHeadImagePath(Context context)
    {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())    //外部存储是否挂载
                || !Environment.isExternalStorageRemovable())   //外部存储是否移除
                ) {
            cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath+"/"+HEAD_IMAGE;
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        pd.show();
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            int newWidth = 100;
            if (photo.getWidth() >= 100) {
                newWidth = photo.getWidth();
            }
            int newHeight = 100;
            if (photo.getHeight() >= 100) {
                newHeight = photo.getHeight();
            }
            if (photo.getWidth() < 100 || photo.getHeight() < 100) {
//                Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHeight, photo.getConfig());
//                Canvas canvas = new Canvas(newBitmap);
//                canvas.drawBitmap(photo, null, new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight()), null);
//                Bitmap.createBitmap(photo, 0, 0, newWidth, newHeight);
                photo = Bitmap.createScaledBitmap(photo, newWidth, newHeight, false);
            }
//			drawable = new BitmapDrawable(photo);
            try {
                photo.compress(Bitmap.CompressFormat.JPEG, 80,
                        new FileOutputStream(profileImgFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            boolean exit = profileImgFile.exists();
            LogDebugUtil.v("FAN",
                    "setPicToView URL: " + profileImgFile.getAbsolutePath());
            if (!exit) {
                SouYueToast.makeText(this, R.string.upload_photo_fail,
                        SouYueToast.LENGTH_SHORT).show();
                return;
            }
            if (user != null) {
                if(CMainHttp.getInstance().isNetworkAvailable(this)){
                    // 先传到又拍云
                    UploadImageTask.executeTask(this, user.userId(), profileImgFile);
                }else{  //没有网络
                    pd.dismiss();   //取消对话框
                    SouYueToast.makeText(this, R.string.neterror, SouYueToast.LENGTH_SHORT).show();
                }
            } else {
                SouYueToast.makeText(this, R.string.token_error,
                        SouYueToast.LENGTH_SHORT).show();
            }
        }
    }

    private void setPicToView(Bitmap photo) {
        if(photo==null)
        {
            return;
        }
        pd.show();
        int newWidth = 100;
        if (photo.getWidth() >= 100) {
            newWidth = photo.getWidth();
        }
        int newHeight = 100;
        if (photo.getHeight() >= 100) {
            newHeight = photo.getHeight();
        }
        if (photo.getWidth() < 100 || photo.getHeight() < 100) {
            photo = Bitmap.createScaledBitmap(photo, newWidth, newHeight, false);
        }
        try {
            photo.compress(Bitmap.CompressFormat.JPEG, 80,
                    new FileOutputStream(profileImgFile));
        } catch (FileNotFoundException e) {
            Log.i(TAG,e.toString());
            e.printStackTrace();
        }
        boolean exit = profileImgFile.exists();
        LogDebugUtil.v("FAN", "setPicToView URL: " + profileImgFile.getAbsolutePath());
        if (!exit) {
            SouYueToast.makeText(this, R.string.upload_photo_fail,
                    SouYueToast.LENGTH_SHORT).show();
            return;
        }
        if (user != null) {
            if (CMainHttp.getInstance().isNetworkAvailable(this)) {
                // 先传到又拍云
                UploadImageTask.executeTask(this, user.userId(), profileImgFile);
            } else {  //没有网络
                pd.dismiss();   //取消对话框
                SouYueToast.makeText(this, R.string.neterror, SouYueToast.LENGTH_SHORT).show();
            }
        } else {
            SouYueToast.makeText(this, R.string.token_error,
                    SouYueToast.LENGTH_SHORT).show();
        }

    }
    /**
     * 又拍云上传成功
     * @param url
     */
    public void uploadSuccess(String url) {
        if (profileImgFile.exists()) {
            profileImgFile.delete();
        }
        LogDebugUtil.v("FAN", "onFinish URL: " + url);
        if (!TextUtils.isEmpty(url)) {
            if (pd != null) {
                pd.show();
            }
            if (user != null) {
                user.image_$eq(url);
                user.setBigImage(url);
                // 传到服务器
                UserRepairInfo info = new UserRepairInfo(HttpCommon.USER_REPIRE_USER_INFO_REQUEST,this);
                info.setParams(user.token(), url, null, null, null);
                mMainHttp.doRequest(info);
//                http.updateProfile(user.token(), url, null, null, null);
            }
        } else {
            SouYueToast.makeText(this, R.string.upload_photo_fail,
                    SouYueToast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.USER_REPIRE_USER_INFO_REQUEST:
                updateProfileSuccess();
                break;
            case HttpCommon.USER_INTERNAL_REQUEST:
                HttpJsonResponse response = request.getResponse();
                MyPoints points = new Gson().fromJson(response.getBody(),
                        new TypeToken<MyPoints>() {
                        }.getType());
                integralSuccess(points);
                User user = SYUserManager.getInstance().getUser();
                if (user == null) {
                    user = new User();
                }
                user.user_level_$eq(response.getBody().get("userlevel")
                        .getAsString());
                user.user_level_title_$eq(response.getBody().get("userleveltitle")
                        .getAsString());
                user.user_level_time_$eq(String.valueOf(System.currentTimeMillis()));
                SYUserManager.getInstance().setUser(user);
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        IHttpError error = request.getVolleyError();
        switch (request.getmId()){
            case HttpCommon.USER_REPIRE_USER_INFO_REQUEST:
                if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR) {
                    HttpJsonResponse respo = error.getJson();
                    Toast.makeText(this, respo.getBodyString(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, R.string.tg_dialog_noconn, Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * 更新成功
     */
    public void updateProfileSuccess() {
//		LogDebugUtil.v("FAN", "drawable=" + drawable);
        MyImageLoader.imageLoader.displayImage(user.image(), head_photo,
                MyImageLoader.options);
        // head_photo.setImageDrawable(drawable);
        SYUserManager.getInstance().setUser(user);
        ThreadPoolUtil.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ImserviceHelp.getInstance().im_update(4, 0, user.image());
            }
        });
        SouYueToast.makeText(this, R.string.upload_photo_success,
                SouYueToast.LENGTH_SHORT).show();
        if (pd != null) {
            pd.dismiss();
        }
    }
}


