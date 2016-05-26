/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 - 2012 J. Devauchelle and contributors.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License version 3 as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */

package com.zhongsou.souyue.ui.webview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Toast;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;

/**
 * Convenient extension of WebViewClient.
 */
public class CBaseWebChromeClient extends WebChromeClient {
    private static final int REQ_CAMERA = 1;
    private static final int REQ_CHOOSE = REQ_CAMERA + 1;

    ValueCallback<Uri> mUploadMessage;
    private Activity mContext;
    private Uri imageFileUri;

    public CBaseWebChromeClient(Activity activity) {
        this.mContext = activity;
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        Log.i("webview", "openFileChooser 3.0 +");
        if (mUploadMessage != null)
            return;
        mUploadMessage = uploadMsg;
        selectImage();
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        Log.i("webview", "openFileChooser < 3.0 ");
        openFileChooser(uploadMsg, "");
    }

    // For Android > 4.1.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        Log.i("webview", "openFileChooser > 4.1.1 ");
        openFileChooser(uploadMsg, acceptType);
    }

    protected final void selectImage() {
        if (!checkSDcard())
            return;

        String[] selectPicTypeStr = {"相机", "相册"};
        new AlertDialog.Builder(mContext).setItems(selectPicTypeStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 相机拍摄
                        openCarcme();
                        break;
                    case 1:// 手机相册
                        openChosePic();
                        break;
                    default:
                        break;
                }
            }
        }).setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (mUploadMessage != null)
                    mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
        }).show();
    }

    /**
     * 检查sdcard
     */
    public final boolean checkSDcard() {
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!flag) {
            Toast.makeText(mContext, "请插入手机存储卡再使用本功能", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    /**
     * 打开相机
     */
    private void openCarcme() {
        try {
            imageFileUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            if (imageFileUri != null) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                if (Utils.isIntentSafe(mContext, i)) {
                    mContext.startActivityForResult(i, REQ_CAMERA);
                } else {
                    Toast.makeText(mContext, "相机有问题", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "相机有问题", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "相机有问题", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开选择照片
     */
    private void openChosePic() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mContext.startActivityForResult(intent, REQ_CHOOSE);
    }

    /**
     * 选择照片后结束
     *
     * @param data
     */
    private Uri afterChosePic(Intent data) {
        if (data != null)
            return data.getData();
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            if (null == mUploadMessage)
                return;
            Uri uri = null;
            if (requestCode == REQ_CAMERA) {
                uri = afterOpenCamera();
            } else if (requestCode == REQ_CHOOSE) {
                uri = afterChosePic(intent);
            }
            mUploadMessage.onReceiveValue(uri);
            mUploadMessage = null;
        } catch (Exception ex) {
            Toast.makeText(mContext, "读取照片错误", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri afterOpenCamera() {
        String picPath = null;
        if (imageFileUri != null) {
            picPath = Utils.getPicPathFromUri(imageFileUri, mContext);
            int degree = 0;
            if (!StringUtils.isEmpty(picPath))
                degree = ImageUtil.readPictureDegree(picPath);
            Matrix matrix = new Matrix();
            if (degree != 0) {// 解决旋转问题
                matrix.preRotate(degree);
            }
            return Uri.fromFile(new File(picPath));
        }
        return null;
    }
}
