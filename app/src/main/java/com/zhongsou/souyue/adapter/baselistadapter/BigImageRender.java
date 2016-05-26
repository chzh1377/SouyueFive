package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.ZSImageListener;
import com.facebook.drawee.view.ZSImageLoader;
import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;
import com.zhongsou.souyue.net.volley.*;
import com.zhongsou.souyue.utils.*;
import com.zhongsou.souyue.view.HotConfigView;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @description: 单张大图样式渲染器
 * @auther: qubian
 * @data: 2015/12/23.
 */

public class BigImageRender  extends ListTypeRender  {
    private ZSImageView image;
    private ZSImageView image_gif;
    private HotConfigView hotConfigView;
    private ImageView controllerIv;
    private TextView galleryCountTv;
    private int width;
    private int deviceWidth;
    private View controllerView;
    private int defaultID;
    private SigleBigImgBean bean;
    private View loadingBar;
    private boolean isGifPlay;
    public BigImageRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = deviceWidth - DeviceUtil.dip2px(context, 20) ;
    }

    @Override
    public View getConvertView() {
        mConvertView = View.inflate(mContext, R.layout.listitem_bigimage,null);
        image =findView(mConvertView,R.id.image);
        image_gif =findView(mConvertView,R.id.image_gif);
        hotConfigView = findView(mConvertView,R.id.hotconfigView);
        controllerIv = findView(mConvertView,R.id.controller);
        galleryCountTv =findView(mConvertView,R.id.gallery_count);
        controllerView=findView(mConvertView,R.id.controller_layout);
        loadingBar = findView(mConvertView,R.id.loading_progress_bar);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(final int position) {
        super.fitDatas(position);
        bean = (SigleBigImgBean) mAdaper.getItem(position);
        image.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.GONE);
        image_gif.setVisibility(View.GONE);
        ListUtils.setTextViewForImageCount(galleryCountTv,bean.getImgCount());
        hotConfigView.setData(bean.getTitleIcon());
        mTitleTv.setText(ListUtils.calcTitle(mContext,bean.getTitleIcon(),getTitle(bean)));
        defaultID =R.drawable.default_big;
        double aspectRatio =2.00d;
        if((bean.getCategory().equalsIgnoreCase(ConstantsUtils.FR_INFO_GIF)
                ||bean.getCategory().equalsIgnoreCase(ConstantsUtils.FR_INFO_PICTURES))
                &&bean.getImgRatio()>0)
        {
            DecimalFormat df = new DecimalFormat("######0.00");
            aspectRatio=Double.parseDouble(df.format(bean.getImgRatio()/100.00));
        }
        image.setAspectRatio((float) aspectRatio);
        image.setClickable(false);
        image.setTag(bean.getPhoneImageUrl());
        setViewLayout(image,BigImageRender.this.width,aspectRatio);
        setViewLayout(image_gif,BigImageRender.this.width,aspectRatio);
        setViewLayout(controllerView,BigImageRender.this.width,aspectRatio);
        if(bean.getCategory().equalsIgnoreCase(ConstantsUtils.FR_INFO_GIF))
        {
            defaultID=R.drawable.default_gif;
            controllerIv.setVisibility(View.VISIBLE);
            controllerIv.setFocusable(true);
            controllerIv.setTag(position);
            image.setClickable(true);
            image.setOnClickListener(this);
            controllerIv.setOnClickListener(BigImageRender.this);
            showImageForce(image_gif, "", null,null);
            showImageForce(image, bean.getBigImgUrl(),defaultID, null);
        }else
        {
            image.setClickable(false);
            showImage(image, bean.getBigImgUrl(),defaultID,null);
            controllerIv.setVisibility(View.GONE);
        }

    }

    private void setViewLayout(View v , int width, double aspectRatio)
    {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
        params.width = width;
        params.height = (int) (width/aspectRatio);
        v.setLayoutParams(params);
    }

    private void doLoading()
    {
        if(loadingBar!=null)
        {
            loadingBar.setVisibility(View.VISIBLE);
        }

    }

    public void goneLoading()
    {
        if(loadingBar!=null)
        {
            loadingBar.setVisibility(View.GONE);
        }
    }

    public void stopPlayGif()
    {
        if(image!=null)
        {
            image.setVisibility(View.VISIBLE);
            image.controllerGIFStop();
            controllerIv.setVisibility(View.VISIBLE);
//            showImage(image, bean.getBigImgUrl(),defaultID,null);
        }
        goneLoading();
        if(image_gif!=null)
        {
            image_gif.controllerGIFStop();
            image_gif.setVisibility(View.GONE);
        }
        isGifPlay =false;
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(!BaseListManager.isFastDoubleClick())
        {
            if(v.getId()== controllerIv.getId())
            {
                if(mListManager instanceof  IItemInvokeGif)
                {
                    isGifPlay =true;
                    ((IItemInvokeGif)mListManager).stopPlayingGif((Integer) controllerIv.getTag());
                    controllerIv.setVisibility(View.GONE);
                    ((IItemInvokeGif)mListManager).setBigImageRender(BigImageRender.this,(Integer) controllerIv.getTag());
                    image_gif.setVisibility(View.VISIBLE);
                    if(bean.getCategory().equalsIgnoreCase(ConstantsUtils.FR_INFO_GIF))
                    {
//                    downloadByFresco();
                        getGifFormNetWork(bean.getPhoneImageUrl());
                    }
                }else
                {
                    Utils.makeToastTest(mContext,"IItemInvokeGif error can not convert");
                }

            }else if(v.getId()== image.getId())
            {
                IntentUtil.goGifPlay(mContext, StringUtils.isNotEmpty(bean.getPhoneImageUrl())?bean.getPhoneImageUrl():bean.getBigImgUrl());
            }
        }
    }
    private void downloadByFresco(){
//            Drawable d= new BitmapDrawable(ZSImageLoader.getImage(bean.getBigImgUrl()));
//            image.setInitCount(1);
        Log.i("AAAA","onClick");
//        if(ZSImageLoader.isImageDownloaded(bean.getBigImgUrl()))
//        {
//            File file= ZSImageLoader.getCachedImageOnDisk(bean.getBigImgUrl());
//            ZSImageLoader.displyImageFromFile(image_gif,file.getAbsolutePath());
//        }else
        {
            showImageForce(image_gif, bean.getPhoneImageUrl(), null,new ZSImageListener() {
                @Override
                public void onStart(String id, Object callerContext) {
                    controllerIv.setVisibility(View.GONE);
                    doLoading();
                }
                @Override
                public void onSuccess(String id, Object imageInfo, Animatable animatable) {
                    goneLoading();
                    controllerIv.setVisibility(View.GONE);
                    image_gif.setVisibility(View.VISIBLE);
                    image_gif.controllerGIFStart();
//                        image.setVisibility(View.GONE);
                }
                @Override
                public void onFailure(String id, Throwable throwable) {
                    goneLoading();
                    controllerIv.setVisibility(View.VISIBLE);
                }
                @Override
                public void onCancel(String id) {
                }
            });
        }
    }
    private void getGifFormNetWork(String url)
    {
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
                ZSImageLoader.displyGifFromFile(image_gif,downloadFile.getAbsolutePath());
                image_gif.controllerGIFStart();
                controllerIv.setVisibility(View.GONE);
            }else
            {
                if(CMainHttp.getInstance().isRunning(url))
                {
                    return;
                }
                int downloadTag=0;
                try{
                    downloadTag =(Integer) controllerIv.getTag();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                CMainHttp.getInstance().doDownload(HttpCommon.LISTVIEW_DOWNLOAD_GIF+downloadTag, downloadFilePath.getAbsolutePath(), url,null, new IVolleyResponse() {
                    @Override
                    public void onHttpResponse(IRequest request) {
                        goneLoading();
                        int downloadTag=0;
                        try{
                            downloadTag =(Integer) controllerIv.getTag();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        if(isGifPlay&&request.getmId()==HttpCommon.LISTVIEW_DOWNLOAD_GIF+downloadTag){
                            controllerIv.setVisibility(View.GONE);
                            image_gif.setVisibility(View.VISIBLE);
                            File file = new File(request.getResponse().toString());
                            Log.i("AA","file download path:"+file.toString());
                            ZSImageLoader.displyGifFromFile(image_gif,file.getAbsolutePath());
                        }

                    }

                    @Override
                    public void onHttpError(IRequest request) {
                        goneLoading();
                        controllerIv.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onHttpStart(IRequest request) {
                        controllerIv.setVisibility(View.GONE);
                        doLoading();
                    }
                });
            }
        }

    }
}
