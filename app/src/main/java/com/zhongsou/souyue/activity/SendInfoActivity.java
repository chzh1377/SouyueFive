package com.zhongsou.souyue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.google.gson.Gson;
import com.upyun.api.IUpYunConfig;
import com.upyun.api.Uploader;
import com.upyun.api.utils.UpYunException;
import com.upyun.api.utils.UpYunUtils;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.BolgImageAdapter;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleSaveSendInfoRequest;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.service.SendUtils;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.ui.HorizontalListView;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import com.alibaba.fastjson.JSON;

/**
 * 兴趣圈——信息发布微件
 */
public class SendInfoActivity extends RightSwipeActivity implements
        OnClickListener {

    //	private static final int SAVEINFO_REQUESTID = 6513203;
    private EditText et_content, et_title;
    public static final String TAG = "selfCreateItem";
    private Uri imageFileUri;
    private TextView tv_childcount;
    private SelfCreateItem sci;
    private List<String> bolgImageList;
    private HorizontalListView bolgImageHorList;
    private BolgImageAdapter bolgImageAdapter;
    private String title;
    private String content;
//    private Http http;
    private String uid;
    private ProgressDialog progressDialog;
    public static final int MESSAGE_WHAT_DISMISS_PROGRESS_DIALOG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_blog);
//        http = new Http(this);
        if (sysp == null)
            sysp = SYSharedPreferences.getInstance();
        getDataFromIntent();
        uid = SYUserManager.getInstance().getUserId();
        initViews();
    }

    public void getDataFromIntent() {
        Intent intent = getIntent();
        sci = (SelfCreateItem) intent.getSerializableExtra(TAG);
    }

    private void initViews() {
        TextView tv_cancel = findView(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        tv_childcount = findView(R.id.tv_childcount);
        TextView tv_save = findView(R.id.tv_save);
        tv_save.setOnClickListener(this);
        TextView tv_send = findView(R.id.tv_send);
        if (StringUtils.isEmpty(sci.keyword())) {
            tv_send.setText(R.string.next);
        } else {
            tv_send.setText(R.string.send);
        }
        tv_send.setOnClickListener(this);
        et_title = findView(R.id.et_title);
        et_title.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        et_content = findView(R.id.et_content);

        bolgImageHorList = (HorizontalListView) findViewById(R.id.bolg_gallery);
        bolgImageList = new ArrayList<String>();

        bolgImageAdapter = new BolgImageAdapter(this, bolgImageList);
        bolgImageHorList.setAdapter(bolgImageAdapter);

        if (sci != null) {
            et_title.setText(sci.title());
            et_content.setText(sci.content());

            if (sci.conpics() != null && sci.conpics().size() != 0) {
                for (String s : sci.conpics()) {
                    Log.v("Huang", "分解后的sci.conpic():" + s);
                    bolgImageAdapter.addItemPaht(s);
                }
            }

        }

        if (bolgImageAdapter.getCount() < 9) {
            bolgImageAdapter.addItemPaht("add_pic");
            tv_childcount.setText((bolgImageAdapter.getCount() - 1) + "/9");
        } else {
            tv_childcount.setText("9/9");
        }
        bolgImageHorList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String str = ((BolgImageAdapter.ViewHolder) view.getTag()).bolgImagePath;
                if (str.equals("add_pic")) {
                    // 点击+号图片弹出选择列表
                    ShowPickDialog();
                } else {
                    // 点击图片删除
                    showDeleteAlert(str);
                }
            }
        });
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                finishAnimation(this);
                break;
            case R.id.tv_send:
                if (!isFastDoubleClick()) {
                    doNext(v);
                }
                break;
            default:
                break;
        }

    }


    private static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
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
                                    imageFileUri = getContentResolver()
                                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    new ContentValues());
                                    if (imageFileUri != null) {
                                        Intent i = new Intent(
                                                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                        i.putExtra(
                                                android.provider.MediaStore.EXTRA_OUTPUT,
                                                imageFileUri);
                                        if (Utils.isIntentSafe(
                                                SendInfoActivity.this, i)) {
                                            startActivityForResult(i, 2);
                                        } else {
                                            SouYueToast
                                                    .makeText(
                                                            SendInfoActivity.this,
                                                            getString(R.string.dont_have_camera_app),
                                                            SouYueToast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    } else {
                                        SouYueToast
                                                .makeText(
                                                        SendInfoActivity.this,
                                                        getString(R.string.cant_insert_album),
                                                        SouYueToast.LENGTH_SHORT)
                                                .show();
                                    }
                                } catch (Exception e) {
                                    SouYueToast.makeText(SendInfoActivity.this,
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

    private void showDeleteAlert(final String path) {
        Dialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_del_sure))
                .setMessage(getString(R.string.dialog_del_sure_des))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getString(R.string.dialog_del),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                if (!bolgImageAdapter.seletePaht("add_pic")) {
                                    bolgImageAdapter.addItemPaht("add_pic");
                                }
                                bolgImageAdapter.clearBolgImageItem(path);
                                bolgImageAdapter.notifyDataSetChanged();

                                if (bolgImageAdapter.getCount() != 1) {
                                    tv_childcount.setText((bolgImageAdapter
                                            .getCount() - 1) + "/9");
                                } else {
                                    tv_childcount.setText("0/9");
                                }

                            }
                        })
                .setNegativeButton(getString(R.string.dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picPath = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:// 如果是直接从相册获取
                    if (data != null) {
                        Uri uri = data.getData();
                        picPath = Utils.getPicPathFromUri(uri, this);
                        imageFileUri = null;
                        addImagePath(picPath);
                    }
                    break;
                case 2:// 如果是调用相机拍照时
                    if (imageFileUri != null) {
                        picPath = Utils.getPicPathFromUri(imageFileUri, this);
                        int degree = 0;
                        if (!StringUtils.isEmpty(picPath))
                            degree = ImageUtil.readPictureDegree(picPath);
                        Matrix matrix = new Matrix();
                        if (degree != 0) {// 解决旋转问题
                            matrix.preRotate(degree);
                        }
                        Log.v("Huang", "相机拍照imageFileUri != null:" + picPath);
                        addImagePath(picPath);

                    } else {
                        showToast(R.string.self_get_image_error);
                    }
                    break;
            }
        }
    }

    private void addImagePath(String picPath) {
        if (!TextUtils.isEmpty(picPath)) {

            String bolg_image_path = new File(ImageUtil.getSelfDir(),
                    System.currentTimeMillis() + "bolg_image")
                    .getAbsolutePath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 16;
            Bitmap bm = BitmapFactory.decodeFile(picPath, options);
            if (bm == null) {
                picPath = null;
                showToast(R.string.self_get_image_error);
                return;
            } else {
                ImageUtil.saveBitmapToFile(ImageUtil.extractThumbNail(picPath),
                        bolg_image_path);
            }

            bolgImageAdapter.clearBolgImageItem("add_pic");
            bolgImageList.remove("add_pic");
            bolgImageAdapter.addItemPaht(bolg_image_path);
            if (bolgImageAdapter.getCount() < 9) {
                bolgImageAdapter.addItemPaht("add_pic");
                tv_childcount.setText((bolgImageAdapter.getCount() - 1) + "/9");
            } else {
                tv_childcount.setText("9/9");
            }
            bolgImageAdapter.notifyDataSetChanged();
        } else {
            // 图片获取异常
            showToast(R.string.self_get_image_error);
        }
    }

    public void showToast(int resId) {
        SouYueToast.makeText(SendInfoActivity.this,
                getResources().getString(resId), 0).show();
    }

    // 获取资源String
    public String getResString(int id) {
        return this.getResources().getString(id);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        String self_weibo = String.format(getString(R.string.self_weibo_login_no), CommonStringsApi.APP_NAME_SHORT);
        AlertDialog gotoLogin = new AlertDialog.Builder(this)
                .setMessage(self_weibo)
                .setPositiveButton(R.string.go_login,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 登陆
                                Intent intent = new Intent();
                                intent.setClass(SendInfoActivity.this,
                                        LoginActivity.class);
                                intent.putExtra(LoginActivity.Only_Login, true);
                                startActivityForResult(intent, 0);
                                overridePendingTransition(R.anim.left_in,
                                        R.anim.left_out);
                            }
                        })
                .setNegativeButton(R.string.go_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        }).create();
        return gotoLogin;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_WHAT_DISMISS_PROGRESS_DIALOG) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    };

    public void showProcessDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("正在发送...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        } else {
            progressDialog.show();
        }
    }

    public void dismissProcessDialog() {
        hideSoftInput();
        handler.sendEmptyMessage(MESSAGE_WHAT_DISMISS_PROGRESS_DIALOG);
    }

    private void hideSoftInput() {
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View cView = getCurrentFocus();
        if (cView != null) {
            mInputMethodManager.hideSoftInputFromWindow(cView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    class UploadToYun implements IUpYunConfig {

        public String getSaveKey() {
            StringBuffer bucket = new StringBuffer(uid + "");
            while (bucket.length() < 8) {
                bucket.insert(0, '0');
            }
            return bucket.insert(4, '/').insert(0, "/user/").append(randomTo4()).append(".jpg").toString();
        }

        private String randomTo4() {
            String s = "";
            int intCount = 0;
            intCount = (new Random()).nextInt(9999);//
            if (intCount < 1000)
                intCount += 1000;
            s = intCount + "";
            return s;
        }

        public String upload(File file) {
            try {
                String policy = UpYunUtils.makePolicy(getSaveKey(), Uploader.getExpiration(), BUCKET_IMAGE);
                String signature = UpYunUtils.signature(policy + "&" + API_IMAGE_KEY);
                return Uploader.upload(policy, signature, UPDATE_HOST + BUCKET_IMAGE, file);
            } catch (UpYunException e) {
            }
            return null;
        }
    }


    private void doNext(View v) {
        String title = et_title.getText().toString().trim();
        String content = et_content.getText().toString();
        if (bolgImageAdapter.seletePaht("add_pic")) {
            bolgImageAdapter.clearBolgImageItem("add_pic");
        }

        if (!SendUtils.checkUser(this))
            return;
        if (!SendUtils.checkData(sci, title, content))
            return;

        showProcessDialog();
        if (bolgImageList.size() == 0) {
            SaveSendInfo();
        } else {
            UploadTask uploadTask = new UploadTask();
            uploadTask.execute(bolgImageList);
        }
    }

    private void SaveSendInfo() {
        String title = et_title.getText().toString().trim();
        String content = et_content.getText().toString();
        List<String> s_uploadedImgUrls = new ArrayList<String>();
        if (bolgImageList != null) {
            for (String imgUrl : bolgImageList) {
                if (imgUrl.contains("upaiyun.com")) {
                    s_uploadedImgUrls.add(imgUrl);
                } else {
                    s_uploadedImgUrls.add(imgUrl);
                }
            }
        }
//        String s_images = JSON.toJSONString(s_uploadedImgUrls);
        String s_images = new Gson().toJson(s_uploadedImgUrls);
        String username = SYUserManager.getInstance().getUserName();
        String nickname = SYUserManager.getInstance().getName();
        CircleSaveSendInfoRequest.send(HttpCommon.CIRCLE_SAVESENDINFO_REQUESTID, this, title, content, sci.md5(), username, nickname, SYUserManager.getInstance().getUserId(), sci.srpId(), sci.keyword(), s_images);
//		CMainHttp.getInstance().doRequest(circleSaveSendInfo);
//        http.saveSendInfo(title, content, sci.md5(), username,nickname, SYUserManager.getInstance().getUserId(), sci.srpId(), sci.keyword(), s_images);
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        super.onHttpResponse(_request);
        switch (_request.getmId()) {
            case HttpCommon.CIRCLE_SAVESENDINFO_REQUESTID:
                saveSendInfoSuccess();
                break;
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        dismissProcessDialog();
        IHttpError error = _request.getVolleyError();
        int id = _request.getmId();
        HttpJsonResponse res = _request.<HttpJsonResponse>getResponse();
        if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR) {
            switch (id) {
                case HttpCommon.CIRCLE_SAVESENDINFO_REQUESTID:
                    int statusCode = res.getCode();
                    if (statusCode == 700) {
                        String body = res.getBodyString();
                        UIHelper.ToastMessage(SendInfoActivity.this, body);
                        break;
                    }
                default:
                    if (res.getCode() != 200) {
                        UIHelper.ToastMessage(this, "网络异常，请重试！");
                    }
                    break;
            }
        } else {
            UIHelper.ToastMessage(this, "网络异常，请重试！");
        }
    }

    public void saveSendInfoSuccess() {
        dismissProcessDialog();
        UIHelper.ToastMessage(SendInfoActivity.this, "发送成功！");
        finish();
    }

class UploadTask extends ZSAsyncTask<List<String>, Void, Boolean> {
    UploadToYun uty;
    private List<String> images = null;

    @Override
    protected void onPreExecute() {
        uty = new UploadToYun();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(List<String>... params) {
        images = params[0];
        if (images != null) {
            uploadPics(uty);// 如果有图片，先将图片上传up云，并拿到图片地址
            return send();// 一切准备就绪 发送原创到中搜服务器
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            SaveSendInfo();
        } else {
            dismissProcessDialog();
            UIHelper.ToastMessage(SendInfoActivity.this, "图片上传失败，请重试！");
        }
        super.onPostExecute(result);
    }

    /**
     * 上传图片到up云
     *
     * @param uty
     */
    private void uploadPics(UploadToYun uty) {
        for (int i = 0; i < images.size(); i++) {
            String dir = images.get(i);
            File f = new File(dir);
            if (null == f || !f.canRead()) {
                continue;
            }
            String url = null;
            if (!(dir.toLowerCase().contains("http:")))
                url = uty.upload(f);
            if (!StringUtils.isEmpty(url)) {
                url = IUpYunConfig.HOST_IMAGE + url + "!android";
                // url = IUpYunConfig.HOST_IMAGE + url;
                images.set(i, url);
            } else
                break;
        }
    }

    /**
     * 如果图片上传失败1张，就算全部失败
     */
    private boolean send() {
        if (images != null) {
            boolean b = true;
            for (String str : images) {
                if (!str.toLowerCase().contains("http:")) {
                    b = false;
                    break;
                }
            }
            if (b) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}


}
