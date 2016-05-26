package com.zhongsou.souyue.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by yinguanping on 14-9-23.
 */
public class SelectImg {

    private static final int REQ_CAMERA = 100;
    private static final int REQ_CHOOSE = REQ_CAMERA + 1;

    private static Activity mContext = null;
    private Uri imageFileUri = null;
    private static SelectImg selectImg;

    public synchronized static SelectImg getInstance(Activity mContext) {
        SelectImg.mContext = mContext;
        if (selectImg == null) {
            selectImg = new SelectImg();
        }
        return selectImg;
    }


//    public void selectImage() {
//        if (!checkSDcard())
//            return;
//
//        String[] selectPicTypeStr = {"相机", "相册"};
//        new AlertDialog.Builder(mContext).setItems(selectPicTypeStr, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:// 相机拍摄
//                        openCarcme();
//                        break;
//                    case 1:// 手机相册
//                        openChosePic();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//            @Override
//            public void onCancel(DialogInterface dialog) {
//            }
//        }).show();
//    }
//
//    /**
//     * 检查sdcard
//     */
//    public final boolean checkSDcard() {
//        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//        if (!flag) {
//            Toast.makeText(mContext, "请插入手机存储卡再使用本功能", Toast.LENGTH_SHORT).show();
//        }
//        return flag;
//    }
//
//    /**
//     * 打开相机
//     */
//    private void openCarcme() {
//        try {
//            imageFileUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
//            if (imageFileUri != null) {
//                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
//                if (Utils.isIntentSafe(mContext, i)) {
//                    mContext.startActivityForResult(i, REQ_CAMERA);
//                } else {
//                    Toast.makeText(mContext, "相机有问题", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(mContext, "相机有问题", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(mContext, "相机有问题", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 打开选择照片
//     */
//    private void openChosePic() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        mContext.startActivityForResult(intent, REQ_CHOOSE);
//    }

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

    public String onActivityResult(int requestCode, int resultCode, Intent intent) {
        String picPath = "";
        try {
            if (requestCode == REQ_CAMERA) {
                picPath = afterOpenCamera();
            } else if (requestCode == REQ_CHOOSE) {
                Uri uri = afterChosePic(intent);
                picPath = Utils.getPicPathFromUri(uri, mContext);
                if (!picPath.endsWith(".png") && !picPath.endsWith(".jpg")) {
                    Toast.makeText(mContext, "请选取格式PNG或JPG格式的图片", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }
        } catch (Exception ex) {
            Toast.makeText(mContext, "读取照片错误", Toast.LENGTH_SHORT).show();
        }
        return picPath;
    }

    private String afterOpenCamera() {
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
            return picPath;
        }
        return null;
    }
}
