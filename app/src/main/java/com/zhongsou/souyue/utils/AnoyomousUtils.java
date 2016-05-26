package com.zhongsou.souyue.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;

/**
 * Created by zyw on 2015/11/6.
 * 用于匿名评论的工具类。
 */
public abstract class AnoyomousUtils {
    private static final String ANOYOMOUS_KEY = "anoyomous";
    private static final String PRIVATE_ICON = "private_avator";
    private static final String NEWS_ID = "news";

    /**
     * 获取当前兴趣圈的匿名状态
     * @param interestid
     * @return
     */
    public static boolean getAnoyomouState(String interestid) {
        long mUserId = Long.parseLong(SYUserManager.getInstance().getUserId());
        if (TextUtils.isEmpty(interestid) || "0".equals(interestid)) // 如果没有圈子id,就是新闻页面，所以默认就是不匿名
            return CommSharePreference.getInstance().getValue(mUserId, ANOYOMOUS_KEY + "##@##" + NEWS_ID, false);
        return CommSharePreference.getInstance().getValue(mUserId, ANOYOMOUS_KEY + "##@##" + interestid, false);
    }

    /**
     * 设置当前兴趣圈的匿名状态
     * @param state
     * @param interestid
     */
    public static void setAnoyomousState(boolean state, String interestid) {
        long mUserId = Long.parseLong(SYUserManager.getInstance().getUserId());
        if (TextUtils.isEmpty(interestid) || "0".equals(interestid))
            CommSharePreference.getInstance().putValue(mUserId, ANOYOMOUS_KEY + "##@##" + NEWS_ID, state);
        CommSharePreference.getInstance().putValue(mUserId, ANOYOMOUS_KEY + "##@##" + interestid, state);
    }


    /**
     * 保存当前圈子头像的状态
     * @param imageUrl
     * @param interretid
     */
    public static void setCurrentPrivateHeadIcon(String imageUrl, String interretid) {
        long mUserId = Long.parseLong(SYUserManager.getInstance().getUserId());
        CommSharePreference.getInstance().putValue(mUserId, PRIVATE_ICON + "##@##" + interretid, imageUrl);
    }

    /**
     * 获取保存当前圈子头像的状态
     * @param interretid
     * @return
     */
    public static String getCurrentPrivateHeadIcon(String interretid) {
        long mUserId = Long.parseLong(SYUserManager.getInstance().getUserId());
        return CommSharePreference.getInstance().getValue(mUserId, PRIVATE_ICON + "##@##" + interretid, "");
    }

    /**
     * 获取当前user的bitmap
     * @return
     */
    public static Bitmap getBitmapForCurrentUser(Context context, String interest_id) {
        String privateHeader = getCurrentPrivateHeadIcon(interest_id);
        String imageUrl = SYUserManager.getInstance().getImage();
        if (!TextUtils.isEmpty(privateHeader) && !TextUtils.isEmpty(interest_id)) {
            imageUrl = privateHeader;
        }
        ImageLoader imageLoader = PhotoUtils.getImageLoader();
        Bitmap imageBitmap = null;
        if (!TextUtils.isEmpty(imageUrl)) {
            try {
                imageBitmap = ImageUtil.getSmallBitmap(imageLoader.getDiskCache().get(imageUrl).getAbsolutePath());
            } catch (Exception e) {
                imageBitmap = null;
            }
            if (imageBitmap == null) {
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, imageUrl, new ImageView(context), MyDisplayImageOption.smalloptions);
                try {
                    imageBitmap = ImageUtil.getSmallBitmap(imageLoader.getDiskCache().get(imageUrl).getAbsolutePath());
                } catch (Exception e) {
                    imageBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.default_head)).getBitmap();
                }
            }
        } else {
            imageBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.default_head)).getBitmap();
        }
        return com.zhongsou.souyue.common.utils.Utils.getRoundedCornerBitmap(imageBitmap,12);
//        return imageBitmap;
    }
}
