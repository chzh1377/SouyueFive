package com.zhongsou.souyue.activity;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.facebook.drawee.view.FrescoConfig;
import com.facebook.drawee.view.ZSImageListener;
import com.facebook.drawee.view.ZSImageLoader;
import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.CacheUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.File;

/**
 * @description:  gif 全屏播放
 * @auther: qubian
 * @data: 2015/12/30.
 */

public class GifPlayActivity extends BaseActivity implements View.OnClickListener {
    private ZSImageView gifImageView;
    private String url;
    private ProgressBarHelper loadingHelper;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        setContentView(R.layout.activity_gifplay);
        gifImageView= (ZSImageView) findViewById(R.id.gifView);
        url = getIntent().getStringExtra("gif_url");
        gifImageView.setOnClickListener(this);
        loadImages();
    }
    private static boolean isWifi = CMainHttp.getInstance().isWifi(MainApplication.getInstance());
    private void loadImages()
    {
        getGifFormNetWork(url);
//        loadImage();
//        if(StringUtils.isNotEmpty(url))
//        {
//            if(SYSharedPreferences.getInstance().getLoadWifi(MainApplication.getInstance())){
//                if(isWifi) {
//                    loadImage();
//                }else{
//                    gifImageView.setImageResource(R.drawable.default_big);
//                }
//            }else{
//                loadImage();
//            }
//        }
    }

    private void getGifFormNetWork(String url)
    {
        doLoading();
        if(StringUtils.isNotEmpty(url))
        {
            String[] urls= url.split("/");
            String fileName="";
            if(urls.length>=1)
            {
                fileName=urls[urls.length-1];
            }
            File downloadFilePath = CacheUtils.getGifFile(mContext);
            String downloadFileString =downloadFilePath.getAbsolutePath()+"/"+fileName;
            Log.i("AA","downloadFileString:"+downloadFileString);
            File downloadFile =new File(downloadFileString);
            if(downloadFile!=null&&downloadFile.exists())
            {
                Log.i("AA","downloadFileString:file exist");
                ZSImageLoader.displyGifFromFile(gifImageView,downloadFile.getAbsolutePath());
                loadingHelper.goneLoading();
                loadingHelper.goneLoadingUI();
            }else
            {
                if(CMainHttp.getInstance().isRunning(url))
                {
                    return;
                }
                CMainHttp.getInstance().doDownload(HttpCommon.LISTVIEW_DOWNLOAD_GIF_DETAIL, downloadFilePath.getAbsolutePath(), url,null, new IVolleyResponse() {
                    @Override
                    public void onHttpResponse(IRequest request) {
                        File file = new File(request.getResponse().toString());
                        Log.i("AA","file download path:"+file.toString());
                        ZSImageLoader.displyGifFromFile(gifImageView,file.getAbsolutePath());
                        loadingHelper.goneLoading();
                        loadingHelper.goneLoadingUI();
                    }

                    @Override
                    public void onHttpError(IRequest request) {
                        loadingHelper.showNetError();
                    }

                    @Override
                    public void onHttpStart(IRequest request) {
                        doLoading();
                    }
                });
            }
        }

    }
    private void loadImage()
    {
//
//        Uri uri = Uri.parse("res://" +getPackageName() + "/" + R.drawable.cancle_posts);
//        gifImageView.setImageURI(uri);
//        gifImageView.setVisibility(View.VISIBLE);
//        gifImageView.controllerGIFStart();

        gifImageView.setImageURL(url ,new ZSImageListener() {
            @Override
            public void onStart(String id, Object callerContext) {
                doLoading();
            }

            @Override
            public void onSuccess(String id, Object imageInfo, Animatable animatable) {
                loadingHelper.goneLoading();
                loadingHelper.goneLoadingUI();
                gifImageView.controllerGIFStart();
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                loadingHelper.showNetError();
            }

            @Override
            public void onCancel(String id) {
                loadingHelper.showNetError();
            }
        });
    }
    private void doLoading()
    {
        View loadingBar = findView(R.id.list_loading);
        loadingBar.setBackgroundColor(Color.BLACK);
        loadingHelper = new ProgressBarHelper(this, loadingBar, ProgressBarHelper.MODE_LOADING_PROGRESS);
        loadingHelper.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
                            @Override
                            public void clickRefresh() {
                                loadImages();
                    }
                });
        loadingHelper.setFailureImageRes(R.drawable.net_error_gif_tip);
        loadingHelper.setNoDataImageRes(R.drawable.no_data_gif_tip);
        loadingHelper.showLoading();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    /**
     * 返回键事件
     *
     * @param v
     */
    public void onBackClick(View v) {
        this.finish();
    }
}
