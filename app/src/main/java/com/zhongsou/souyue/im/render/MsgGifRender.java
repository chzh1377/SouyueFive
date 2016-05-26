package com.zhongsou.souyue.im.render;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.module.SendGifBean;
import com.zhongsou.souyue.im.util.Constants;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.File;

/**
 * Created by Administrator on 2015/3/26.
 */
public class MsgGifRender extends MsgItemRender {
    private SimpleDraweeView gifImageView;
    private GenericDraweeHierarchy hierarchy;
    public MsgGifRender(Context context,
                        BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        hierarchy = builder
                .setFadeDuration(0)
                .setFailureImage(context.getResources().getDrawable(R.drawable.default_head))
//                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.default_head))
                .build();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        if (mChatMsgEntity.getType() == IMessageConst.CONTENT_TYPE_GIF) {
            gifImageView = mViewHolder.obtainView(mContentView, R.id.gif);
            gifImageView.setHierarchy(hierarchy);
            SendGifBean gifbean = new Gson().fromJson(
                    mChatMsgEntity.getText(), SendGifBean.class);
            if (gifbean != null) {
//                Log.e("gifrender", "位置：" + position + "，开始加载图片了，图片地址是："+new Gson().toJson(gifbean));
                final String gifname = gifbean.getGif_name();
                final String local_url = gifbean.getLocal_url();
                File localFile = new File(mContext.getFilesDir()+Constants.PACKAGE_DOWNURL + File.separator
                        + SYUserManager.getInstance().getUserId() + File.separator
                        + local_url);
                if (localFile.exists()) { // 如果下载了
                    try {
                        Log.e(this.getClass().getName(), "gif loadfile exists path:" + localFile.getAbsolutePath());
//                        GifDrawable localGif = new GifDrawable(localFile);
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setAutoPlayAnimations(true)
                                .setUri(Uri.fromFile(localFile))
                                .build();
                        gifImageView.setController(controller);
//                        gifImageView.setImageDrawable(localGif);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Log.e("MsgGifRender", "内存溢出异常OOM了");
                    }
                } else {
                    // sd
                    File sdCardFile = new File(ImageUtil.chooseDir(ImageUtil.GIF_PATH)
                            + gifname + ".gif");
                    if (sdCardFile.exists()) {// 判断文件目录是否存在
                        try {
                            Log.e(this.getClass().getName(),"gif sdcard exists path:"+localFile.getAbsolutePath());
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setAutoPlayAnimations(true)
                                    .setUri(Uri.fromFile(sdCardFile))
                                    .build();
                            gifImageView.setController(controller);
//                            GifDrawable sdCardGif = new GifDrawable(sdCardFile);
//                            gifImageView.setImageDrawable(sdCardGif);
                        } catch (Exception e) {
                            sdCardFile.delete();
//                            getGIfFromInternet(gifbean);
                        }
                    } else {//本地没有，从网络获取
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setAutoPlayAnimations(true)
                                .setUri(Uri.parse(gifbean.getGif_url()))
                                .build();
                        gifImageView.setController(controller);

//                        String[] allowedContentTypes = new String[]{
//                                "image/png", "image/jpeg", "image/gif"};
//                        if (CMainHttp.getInstance().isRunning(gifbean.getGif_url())) {
//                            return;
//                        }


//                        getGIfFromInternet(gifbean);

//                        new AsyncHttpClient().get(gifbean.getGif_url(),
//                                new BinaryHttpResponseHandler(
//                                        allowedContentTypes) {
//                                    @Override
//                                    public void onSuccess(int statusCode,
//                                                          byte[] binaryData) {
//                                        super.onSuccess(statusCode, binaryData);
//                                        FileDownloadUtil.saveByteArrayToFile(
//                                                binaryData,
//                                                ImageUtil
//                                                        .chooseDir(ImageUtil.GIF_PATH),
//                                                gifname);
//                                        try {
//                                            GifDrawable downLoadGif = new GifDrawable(new File("/sdcard/souyue/gif/"
//                                                    + gifname + ".gif"));
//                                            gifImageView.setImageDrawable(downLoadGif);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(Throwable error,
//                                                          byte[] binaryData) {
//                                        super.onFailure(error, binaryData);
//                                        try {
//                                            gifImageView.setImageResource(R.drawable.default_head);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFinish() {
//                                        super.onFinish();
//                                    }
//
//                                    @Override
//                                    public void onStart() {
//                                        super.onStart();
//
//                                        try {
//                                            gifImageView.setImageResource(R.drawable.default_head);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
                    }
                }
            }
        }
    }

//    private void getGIfFromInternet(final SendGifBean bean) {
//        Log.e(this.getClass().getName(),"gif sdcard download path:"+bean.getGif_name() + ".gif");
//        CMainHttp.getInstance().doDownloadRename(new Random().nextInt(), ImageUtil
//                .chooseDir(ImageUtil.GIF_PATH), bean.getGif_name() + ".gif", bean.getGif_url(), null, new IVolleyResponse() {
//            @Override
//            public void onHttpResponse(IRequest request) {
//                try {
//                    String path = request.getResponse().toString();
//                    Log.e(this.getClass().getName(),"im download:"+path);
////                    FileDownloadUtil.saveByteArrayToFile(path, ImageUtil.chooseDir(ImageUtil.GIF_PATH), bean.getGif_name());
////                    GifDrawable downLoadGif = new GifDrawable(new File(path));
////                    gifImageView.setImageDrawable(downLoadGif);
//                    mChatAdapter.notifyDataSetChanged();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onHttpError(IRequest request) {
////                gifImageView.setImageResource(R.drawable.default_head);
//            }
//
//            @Override
//            public void onHttpStart(IRequest request) {
////                gifImageView.setImageResource(R.drawable.default_head);
//            }
//        });
//    }

    @Override
    public void fitEvents() {
        super.fitEvents();
        mViewHolder.obtainView(mContentView, R.id.gif).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChatAdapter.getIsEdit()) {
                            if (cbCheck.isChecked()) {
                                cbCheck.setChecked(false);
                                mChatMsgEntity.setEdit(false);
                                cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox);
                            } else {
                                mChatMsgEntity.setEdit(true);
                                cbCheck.setChecked(true);
                                cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
                            }
                        }
                    }
                });

        mViewHolder.obtainView(mContentView, R.id.gif).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    showLCDialog(false, true);
                }
                return true;
            }
        });
    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_gif_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_gif_right_view;
    }


}
