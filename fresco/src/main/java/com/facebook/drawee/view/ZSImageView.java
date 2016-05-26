package com.facebook.drawee.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.SimpleDraweeControllerBuilder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.net.URI;

/**
 * 加载的图片
 *
 *
 * Created by qubian on 2015/12/4.
 */
public class ZSImageView extends SimpleDraweeView {

    private ZSImageListener zsImageListener;
    private ControllerListener<Object> mListener;
    private String mTag;
    private int initCount;
    public ZSImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init();
    }

    public ZSImageView(Context context) {
        super(context);
        init();
    }

    public ZSImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZSImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init()
    {
        initCount = 1;
        mListener = new BaseControllerListener<Object>() {
            @Override
            public void onSubmit(String id, Object callerContext) {
                if(zsImageListener!=null)
                {
                    zsImageListener.onStart(id,callerContext);
                }

            }
            @Override
            public void onFinalImageSet(String id,Object imageInfo, Animatable animatable) {
                if(zsImageListener!=null)
                {
                    zsImageListener.onSuccess(id,imageInfo,animatable);
                }

            }
            @Override
            public void onFailure(String id, Throwable throwable) {
                if(zsImageListener!=null)
                {
                    zsImageListener.onFailure(id,throwable);
                }

            }
            @Override
            public void onRelease(String id) {
                if(zsImageListener!=null)
                {
                    zsImageListener.onCancel(id);
                }

            }
        };
    }

    public void setImageURL(String uri) {
        if(uri==null)
        {
            return;
        }
        setTag(uri);
        super.setImageURI(Uri.parse(uri));
    }

    /**
     * 带监听的 url
     * @param uri
     * @param listener
     */
    public void setImageURL(String uri, ZSImageListener listener) {
        zsImageListener=listener;
        if(uri==null)
        {
            return;
        }
        setTag(uri);
        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                        .setProgressiveRenderingEnabled(false)
                        .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(getController())
                .setControllerListener(mListener)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(false)
                .build();
        setController(draweeController);
    }

    /**
     *
     * 此 坑 已 填
     * 在listview 的适配器中，不要使用 options 因为 在setHierarchy会占用大量内存，尤其是服用的时候，会重置 setHierarchy（）
     * 导致页面卡顿
     * 尽量在 其 xml 中设置
     *
     * @param uri
     * @param options
     */
    public void setImageURL(String uri, ZSImageOptions options) {
        setImageURL(uri);
        if(options!=null&&options.hierarchy!=null&&initCount<=1)
        {
            setHierarchy(options.hierarchy);
        }
        initCount++;
    }

    /**
     * 带配置 与 监听的
     * @param uri
     * @param options
     * @param listener
     */
    public void setImageURL(String uri,ZSImageOptions options,ZSImageListener listener)
    {
        setImageURL(uri,listener);
        if(options!=null&&options.hierarchy!=null&&initCount<=1)
        {
            setHierarchy(options.hierarchy);
        }
        initCount++;
    }

    /**
     * Uri 的数据形式
     * @param uri
     * @param options
     * @param listener
     */
    public void setImageURI(Uri uri,ZSImageOptions options,ZSImageListener listener)
    {
        zsImageListener=listener;
        if(uri==null)
        {
            return;
        }
        setTag(uri);
        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(uri)
                        .setProgressiveRenderingEnabled(false)
                        .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(getController())
                .setControllerListener(mListener)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(false)
                .build();
        setController(draweeController);
        if(options!=null&&options.hierarchy!=null&&initCount<=1)
        {
            setHierarchy(options.hierarchy);
        }
        initCount++;
    }

    public int getInitCount() {
        return initCount;
    }

    /**
     * 用来 强设 setHierarchy
     * @param initCount
     */
    public void setInitCount(int initCount) {
        this.initCount = initCount;
    }

    /**
     * GIF  的控制
     */
    public void controllerGIF()
    {
        DraweeController draweeController =getController();
        if(draweeController!=null)
        {
            Animatable animatable =draweeController.getAnimatable();
            if(animatable!=null)
            {
                if (animatable.isRunning()){
                    animatable.stop();
                }else {
                    animatable.start();
                }
            }
        }
    }
    public void controllerGIFStart()
    {
        DraweeController draweeController =getController();
        if(draweeController!=null)
        {
            Animatable animatable =draweeController.getAnimatable();
            if(animatable!=null&&!animatable.isRunning())
            {
                animatable.start();
            }
        }
    }
    public void controllerGIFStop()
    {
        DraweeController draweeController =getController();
        if(draweeController!=null)
        {
            Animatable animatable =draweeController.getAnimatable();
            if(animatable!=null&&animatable.isRunning())
            {
                animatable.stop();
            }
        }
    }
    /**
     * 设置  圆圈 图片
     * @param uri
     * @param listener
     */
    public void setImageURIAsCircle(String uri, ZSImageListener listener)
    {
        setImageURL(uri,listener);
        RoundingParams roundingParams =getHierarchy().getRoundingParams();
//    roundingParams.setBorder(R.color.red, 1.0);
        roundingParams.setRoundAsCircle(true);
        getHierarchy().setRoundingParams(roundingParams);
    }

    /**
     * 设置  圆角 图片
     * @param uri
     * @param listener
     * @param r
     */
    public void setImageURIAsCircle(String uri, ZSImageListener listener,float r)
    {
        setImageURL(uri,listener);
        RoundingParams roundingParams =getHierarchy().getRoundingParams();
        roundingParams.setCornersRadii(r,r,r,r);
        roundingParams.setRoundAsCircle(true);
        getHierarchy().setRoundingParams(roundingParams);
    }

    /**
     * 设置监听
     * @param listener
     */
    public void setZSImageListener(ZSImageListener listener)
    {
        zsImageListener=listener;
    }

    public void setTag(String tag)
    {
        mTag =tag;
    }
    public String getTag()
    {
        return  mTag;
    }

}
