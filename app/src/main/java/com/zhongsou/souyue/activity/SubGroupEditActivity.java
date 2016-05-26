package com.zhongsou.souyue.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.upyun.api.UploadImageTask;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.util.CustomProgress;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.fragment.EditGroupFragment;
import com.zhongsou.souyue.fragment.SouyueTabFragment;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserRepairInfo;
import com.zhongsou.souyue.net.sub.GroupAddReq;
import com.zhongsou.souyue.net.sub.GroupEditReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ToastUtil;
import com.zhongsou.souyue.utils.Utility;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.regex.Pattern;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 订阅分组编辑页面 ,创建分组
 * @date 2016/3/29
 */
public class SubGroupEditActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 页面内就一个 Fragment
     */
    protected EditGroupFragment currentDisplayFragment;
    private String groupId;
    private String title;
    private int C_E_tag; //创建和编辑  1:创建 2 编辑
    private EditText groupNameTv;
    private ImageView groupIv;
    private String groupImageUrl;
    private Uri imageFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_group_edit);
        initView();
        initDate();
        initFragment();
        initListener();
    }

    private void initListener() {
        groupIv.setOnClickListener(this);
        findView(R.id.sub_group_finish).setOnClickListener(this);

    }

    private void initView() {
        groupNameTv = findView(R.id.groupname_tv);
        groupIv = findView(R.id.sub_group_img);
        ((TextView) findViewById(R.id.sub_group_title)).setText(getResources().getString(R.string.sub_group_title));
        findViewById(R.id.input_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }


    private void hideKeyboard() {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(groupNameTv.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private void initDate() {
        pd = new ProgressDialog(this);
        pd.setMessage(this.getString(R.string.group_image_loading));
        pd.setCanceledOnTouchOutside(false);
        profileImgFile = new File(this.getCacheDir(), "group_photo_");
        user = SYUserManager.getInstance().getUser();

        C_E_tag = getIntent().getIntExtra("C_E_tag", 1);
        if (C_E_tag == EditGroupFragment.EDIT_GROUP)// 编辑
        {
            groupId = getIntent().getStringExtra("groupId");
            title = getIntent().getStringExtra("title");
            groupImageUrl= getIntent().getStringExtra("groupImage");
            groupNameTv.setText(title);
            if(StringUtils.isNotEmpty(groupImageUrl))
            {
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, groupImageUrl, groupIv, MyDisplayImageOption.newoptions);
            }
        }

    }

    private void initFragment() {
        currentDisplayFragment = EditGroupFragment.newInstance(getIntent().getExtras());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, currentDisplayFragment);
        ft.commit();
    }


    private void addGroup(String name, String menber,String groupImage) {
        GroupAddReq req = new GroupAddReq(HttpCommon.GROUP_ADD_REQ, this);
        req.setParams(name, menber, groupImage);
        CMainHttp.getInstance().doRequest(req);
    }

    private void editGroup(String groupId, String name, String menber,String groupImage) {
        GroupEditReq req = new GroupEditReq(HttpCommon.GROUP_EDIT_REQ, this);
        req.setParams(groupId, name, menber, groupImage);
        CMainHttp.getInstance().doRequest(req);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        goneProgress();
        switch (request.getmId()) {
            case HttpCommon.GROUP_ADD_REQ:
                resultSuccess(getResources().getString(R.string.sub_group_add_success));
                break;
            case HttpCommon.GROUP_EDIT_REQ:
                resultSuccess(getResources().getString(R.string.sub_group_edit_success));
                break;
        }
    }

    private void resultSuccess(String str) {
        // 发广播 通知 修改组成员属性
        Intent intentGotoBall = new Intent();
        intentGotoBall.setAction(SouyueTabFragment.REFRESH_HOMEGROUP_DATA);
        intentGotoBall.putExtra(SubGroupActivity.INTENT_EXTRA_TITLE, getEditText());
        intentGotoBall.putExtra(SubGroupActivity.INTENT_EXTRA_IMAGE,groupImageUrl);
        //需要返回groupid
        intentGotoBall.putExtra(SubGroupActivity.INTENT_EXTRA_GROUP_ID,groupId);
        sendBroadcast(intentGotoBall);

        ToastUtil.show(this, str);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        IHttpError error = request.getVolleyError();
        goneProgress();
        switch (request.getmId()) {
            case HttpCommon.GROUP_ADD_REQ:
                if (error.getErrorCode() < 700) {
                    Toast.makeText(this, R.string.sub_group_fail, Toast.LENGTH_SHORT).show();
                }
                break;
            case HttpCommon.GROUP_EDIT_REQ:
                if (error.getErrorCode() < 700) {
                    Toast.makeText(this, R.string.sub_group_fail, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void setEditText(String title) {
        groupNameTv.setText(title);
    }
    public String getEditText()
    {
        return groupNameTv.getText().toString();
    }


    public void setGroupImageUrl(String imgUrl) {
        if(StringUtils.isNotEmpty(imgUrl))
        {
            groupImageUrl = imgUrl;
            PhotoUtils.showCard(PhotoUtils.UriType.HTTP, imgUrl, groupIv, MyDisplayImageOption.newoptions);
        }
    }

    public String getGroupImageUrl()
    {
        return groupImageUrl;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sub_group_img:
                ShowPickDialog();
                break;
            case R.id.sub_group_finish:
                String inputText = groupNameTv.getText().toString();
                if (StringUtils.isEmpty(inputText)) {
                    ToastUtil.show(mContext, R.string.sub_group_name_not_null);
                    return;
                }
                if (!Utility.isChAndEnAndNum(inputText))
                {
                    SouYueToast.makeText(mContext,
                            mContext.getResources().getString(R.string.group_name_error), 0).show();
                    return ;
                }
                String listString = currentDisplayFragment.getListString();
                if (listString.length() < 5) {
                    ToastUtil.show(mContext, R.string.sub_group_item_not_null);
                    return;
                }
                showProgress();
                if (C_E_tag == EditGroupFragment.CREATE_GROUP) {
                    addGroup(inputText, listString,groupImageUrl);
                } else if (C_E_tag == EditGroupFragment.EDIT_GROUP) {
                    String imageUrl = groupImageUrl;
                    if(inputText.equalsIgnoreCase(title))
                    {
                        inputText="";//没有修改 则传空
                    }
                    if(getIntent().getStringExtra("groupImage").equalsIgnoreCase(groupImageUrl))
                    {
                        imageUrl="";//没有修改 则传空
                    }
                    editGroup(groupId, inputText, listString,imageUrl);
                }
                break;
        }
    }

    private CustomProgress netdialog;

    private void showProgress() {
        netdialog = new CustomProgress(this, "", false, null);
        if (netdialog != null && !netdialog.isShowing()) {
            netdialog.show();
        }
    }

    private void goneProgress() {
        if (netdialog != null && netdialog.isShowing()) {
            netdialog.dismiss();
            netdialog = null;
        }
    }
    private User user;
    private ProgressDialog pd;
    private File profileImgFile;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        }
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
                                    imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                                    LogDebugUtil.v("FAN", imageFileUri + "");
                                    if (imageFileUri != null) {
                                        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                        i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
                                        if (Utils.isIntentSafe(mContext, i)) {
                                            startActivityForResult(i, 2);
                                        } else {
                                            SouYueToast.makeText(mContext, getString(R.string.dont_have_camera_app), SouYueToast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        SouYueToast.makeText(mContext, getString(R.string.cant_insert_album), SouYueToast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    SouYueToast.makeText(mContext, getString(R.string.cant_insert_album), SouYueToast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1: // 相册
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent, 1);
                                break;
                            default:
                                break;
                        }
                    }

                });
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
            intent.putExtra("outputX",300);
            intent.putExtra("outputY", 300);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, 3);
        } catch (Exception e) {
            SouYueToast.makeText(this, "图片裁剪异常",
                    SouYueToast.LENGTH_SHORT).show();
        }
    }
    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {

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
                photo = Bitmap.createScaledBitmap(photo, newWidth, newHeight, false);
            }
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
                    pd.show();
                    UploadImageTask.executeTask(this, user.userId(), profileImgFile);
                }else{  //没有网络
                    SouYueToast.makeText(this, R.string.neterror, SouYueToast.LENGTH_SHORT).show();
                }
            } else {
                SouYueToast.makeText(this, R.string.token_error,
                        SouYueToast.LENGTH_SHORT).show();
            }
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
        pd.dismiss();
        if (!TextUtils.isEmpty(url)) {
            setGroupImageUrl(url);
            SouYueToast.makeText(this, R.string.group_image_upload_photo_success,
                    SouYueToast.LENGTH_SHORT).show();
        } else {
            SouYueToast.makeText(this, R.string.group_image_upload_photo_fail,
                    SouYueToast.LENGTH_SHORT).show();
        }
    }

}
