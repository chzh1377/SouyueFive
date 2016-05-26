package com.facebook.drawee.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

/**
 * @description: 加载工具
 * @auther: qubian
 * @data: 2015/12/15.
 */
public class ZSImageLoader
{
    private static final String RES ="res:///";
    private static final String ASSET ="asset:///";
    private static final String FILE ="file://";
    private static final String CONTENT ="content:///";

//    public static ZSImageLoader instance;
//
//    public static ZSImageLoader getInstance() {
//        if (instance == null) {
//            synchronized (ZSImageLoader.class) {
//                if (instance == null) {
//                    instance = new ZSImageLoader();
//                }
//            }
//        }
//        return instance;
//    }

    public static void displyImage(ZSImageView imageView,String uri)
    {

        imageView.setImageURL(uri);

    }

    /**
     * 请注意，在 使用ZSImageOptions ，请勿重复设置，
     * 否则会导致页面卡顿
     * @param imageView
     * @param uri
     * @param options
     */
    public static void displyImage( ZSImageView imageView, String uri,ZSImageOptions options)
    {
        imageView.setImageURL(uri, options);

    }
    public static void displyImage(ZSImageView imageView, String uri,ZSImageOptions options,ZSImageListener listener)
    {
        imageView.setImageURL(uri, options, listener);
    }

    public static void displyImageFromRes(ZSImageView imageView,String uri)
    {
        imageView.setImageURL(RES + uri);
    }

    /**
     *  注意  传递 过来的uri 为： "btnicon.png"
     *
     * @param imageView
     * @param uri
     * @param options
     * @param listener
     */
    public static void displyImageFromAsset(ZSImageView imageView,String uri,ZSImageOptions options,ZSImageListener listener)
    {
        imageView.setImageURL(ASSET + uri,options,listener);
    }
    public static void displyImageFromFile(ZSImageView imageView,String uri)
    {
        imageView.setImageURL(FILE + uri);
    }
    public static void displyImageFromContent(ZSImageView imageView,String uri)
    {
        imageView.setImageURL(CONTENT+uri);
    }

    /**
     *  gif 本地文件
     * @param imageView
     * @param uriString
     */
    public static void displyGifFromFile(ZSImageView imageView,String uriString)
    {
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    ImageInfo imageInfo,
                    Animatable anim) {
                if (anim != null) {
                    anim.start();
                }
            }
        };
        Uri uri=Uri.parse("file://"+uriString);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(controllerListener)
                    .build();
        imageView.setController(controller);
    }
    /**
     * bitmap 记得用完后回收
     * 谢谢
     * @param url
     * @return
     */
    public static Bitmap getImage(String url)
    {
        Bitmap bitmap=null;
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                        .build();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest,null);

//        dataSource.subscribe(new BaseBitmapDataSubscriber() {
//                                 @Override
//                                 public void onNewResultImpl(final @Nullable Bitmap bitmap) {
//                                     // You can use the bitmap in only limited ways
//                                     // No need to do any cleanup.
//                                 }
//                                 @Override
//                                 public void onFailureImpl(DataSource dataSource) {
//                                 }}, CallerThreadExecutor.getInstance());

        CloseableReference<CloseableImage> imageReference =null;
        try {
            imageReference = dataSource.getResult();
            if (imageReference != null) {
                CloseableImage image = imageReference.get();
                if(image instanceof CloseableBitmap)
                {
                    bitmap= ((CloseableBitmap) image).getUnderlyingBitmap();
                }
            }
        } finally {
            dataSource.close();
            CloseableReference.closeSafely(imageReference);
        }
        return  bitmap;
    }

    public static File getFileExistOrNot(String url)
    {
        ImageRequest imageRequest=ImageRequest.fromUri(url);
        CacheKey cacheKey= DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest);
        BinaryResource resource = ImagePipelineFactory.getInstance()
                .getMainDiskStorageCache().getResource(cacheKey);
        if(resource!=null)
        {
            return ((FileBinaryResource)resource).getFile();
        }else
        {
            return null;
        }

    }
    public static boolean isImageDownloaded(String loadUri) {
        if (loadUri == null) {
            return false;
        }
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri));
        return ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey) || ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey);
    }

    //return file or null
    public static File getCachedImageOnDisk(String loadUri) {
        File localFile = null;
        if (loadUri != null) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri));
            if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
                if(resource!=null)
                {
                    localFile = ((FileBinaryResource) resource).getFile();
                }
            } else if (ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().getResource(cacheKey);
                if(resource!=null)
                {
                    localFile = ((FileBinaryResource) resource).getFile();
                }
            }
        }
        return localFile;
    }
}
