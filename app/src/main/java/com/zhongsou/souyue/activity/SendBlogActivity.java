package com.zhongsou.souyue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.BolgImageAdapter;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.activity.CircleSelImgGroupActivity;
import com.zhongsou.souyue.db.SelfCreateHelper;
import com.zhongsou.souyue.dialog.SYProgressDialog;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.net.selfCreate.SelfCreateDel;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.service.SelfCreateTask;
import com.zhongsou.souyue.service.SendUtils;
import com.zhongsou.souyue.ui.HorizontalListView;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
/**
 * 我的原创——发表博客（长文章）
 * @author Administrator
 *
 */
public class SendBlogActivity extends RightSwipeActivity implements
        OnClickListener, OnCancelListener {

    private EditText et_content, et_title;
    public static final String TAG = "selfCreateItem";
    private Uri imageFileUri;
    private TextView tv_childcount;
    private SelfCreateItem sci;
    // 改进后的属性
    private List<String> bolgImageList;
    private HorizontalListView bolgImageHorList;
    private BolgImageAdapter bolgImageAdapter;

    // data
    private String title;
    private String content;
//    private Http http;
    private SYProgressDialog sydialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_blog);
//        http = new Http(this);
        sydialog = new SYProgressDialog(this, 0, "保存中");
        sydialog.setOnCancelListener(this);
        if (sysp == null)
            sysp = SYSharedPreferences.getInstance();
        getDataFromIntent();
        initViews();
    }

//	@Override
//	protected void onStop() {
//		super.onStop();
//		for(int i = 0 ;i< bolgImageList.size() ; i++){
//			bolgImageList.remove(i);
//		}
//		bolgImageList.size();
//	}

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

//		if (sci != null && !StringUtils.isEmpty(sci.content())) {
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

    public void onBackPressed() {
        checkData();
        return;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                checkData();
                break;
            case R.id.tv_send:
                doNext(v);
                break;
            case R.id.tv_save:
                //草稿修改后再次保存，删除上次保存的草稿
                if (StringUtils.isEmpty(sci.id())
                        && sci.status() == ConstantsUtils.STATUS_SEND_ING) {
                    SelfCreateHelper.getInstance().delSelfCreateItem(sci);
                } else {
                    SelfCreateDel selfcreatedel=new SelfCreateDel(HttpCommon.SELFCREATEDEL_REQUEST_ID,this);
                    selfcreatedel.setParams(sci.id());
                    mMainHttp.doRequest(selfcreatedel);
                }
                //保存
                doSave();
                break;
            default:
                break;
        }

    }

    private void checkData() {
        title = et_title.getText().toString().trim();
        content = et_content.getText().toString();
//		if (!SendUtils.checkUser(this))
//			return;
//		if (!SendUtils.checkData(sci, title, content))
//			return;
        if (isEmpty(content) && isEmpty(title) && bolgImageList.size() > 0 && bolgImageList.get(0).equals("add_pic") && (bolgImageList.size() < 2)) {
            finishAnimation(this);
        } else {
            if (isEmpty(content) && isEmpty(title) && bolgImageList.size() <= 0) {
                finishAnimation(this);
            } else {
                askSave();
            }
        }

    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    private void askSave() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("提醒")
                .setMessage("是否将保存为草稿？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 确定
                        //草稿修改后再次保存，删除上次保存的草稿
                        if (StringUtils.isEmpty(sci.id())
                                && sci.status() == ConstantsUtils.STATUS_SEND_ING) {
                            SelfCreateHelper.getInstance().delSelfCreateItem(sci);
                        }
                        doSave();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                // 取消
                finish(); //
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        }).show();
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
                                                SendBlogActivity.this, i)) {
                                            startActivityForResult(i, 2);
                                        } else {
                                            SouYueToast
                                                    .makeText(
                                                            SendBlogActivity.this,
                                                            getString(R.string.dont_have_camera_app),
                                                            SouYueToast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    } else {
                                        SouYueToast
                                                .makeText(
                                                        SendBlogActivity.this,
                                                        getString(R.string.cant_insert_album),
                                                        SouYueToast.LENGTH_SHORT)
                                                .show();
                                    }
                                } catch (Exception e) {
                                    SouYueToast.makeText(SendBlogActivity.this,
                                            getString(R.string.cant_insert_album),
                                            SouYueToast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1: // 相册
                                Intent intent = new Intent(SendBlogActivity.this, CircleSelImgGroupActivity.class);
                                intent.putExtra("piclen", 8);
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
        if (resultCode == 0x200) {
            if (data != null) {
                List<String> list = data.getStringArrayListExtra("imgseldata");
                if (list != null && list.size() > 0) {
                    picPath = list.get(0);
                    addImagePath(picPath);
                }
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
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
        SouYueToast.makeText(SendBlogActivity.this,
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
                                intent.setClass(SendBlogActivity.this,
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


    /**
     * 验证数据，异步写入数据
     */
    private void doSave() {
        try {
            title = et_title.getText().toString().trim();
            content = et_content.getText().toString();
            if (bolgImageAdapter.seletePaht("add_pic")) {
                bolgImageAdapter.clearBolgImageItem("add_pic");
            }
            if (!SendUtils.checkUser(this))
                return;
//			if (!SendUtils.checkData(sci, title, content))
//				return;
            if (sci == null)
                sci = new SelfCreateItem();
            sci.title_$eq(title);
            sci.content_$eq(content);
//			sci.status_$eq(ConstantsUtils.STATUS_SEND_FAIL);
            sci.status_$eq(ConstantsUtils.STATUS_SEND_ING);
            sci.conpics_$eq(bolgImageAdapter.getBolgImageList());
            sci.pubtime_$eq(System.currentTimeMillis() + "");
            SelfCreateTask.getInstance().save2draftBox(SendBlogActivity.this, sci);

        } catch (Exception ex) {

        }
        sydialog.show();

    }

    // 数据库保存后 回调方法
    public void save2BoxSuccess(String result) {
        Log.i("save2BoxSuccess", "result = " + result);
        // gone dialog
        sydialog.cancel();
        if (result == null) {
            // 保存失败, 失败继续留在原页面
        } else {
            // 保存成功， 跳转到列表中
//			Intent intent = new Intent();
//			intent.setClass(SendBlogActivity.this, SelfCreateActivity.class);
//
//			startActivity(intent);
            finish(); //
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            Intent tofresh = new Intent();
            tofresh.putExtra("ismodify", true);
            tofresh.setAction(ConstantsUtils.INTENTFILTER_ACTION_REFRESH_SELFCREATE_LV);
            sendBroadcast(tofresh);
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
        sci.title_$eq(title);
        sci.content_$eq(content);
        sci.conpics_$eq(bolgImageAdapter.getBolgImageList());
        v.setEnabled(SendUtils.sendOrNext(sci, SendBlogActivity.this, false));
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // TODO Auto-generated method stub

    }

}
