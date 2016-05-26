package com.zhongsou.souyue.im.util;

import com.tuita.sdk.im.db.module.IConst;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.module.FeaturesBean;

import java.util.ArrayList;

/**
 * @author zhangwenbin zhangwb@zhongsou.com
 * @version version_code (e.g, V5.1)
 * @Copyright (c) 2016 zhongsou
 * @Description class IM功能
 * @date 16/1/8
 */
public class IMFeaturesHelper {

    /**
     * item类型
     */
    public static final int TYPE_SELECT_PHOTO = 1;     //相册
    public static final int TYPE_TAKE_PHOTO = 2;       //照相
    public static final int TYPE_CARD = 3;              //名片
    public static final int TYPE_ASK_FOR_ZSB = 4;       //求中搜币
    public static final int TYPE_GIFT_ZSB = 5;          //赠中搜币
    public static final int TYPE_WHISPER = 6;           //密信
    public static final int TYPE_RED_PACKET = 7;        //红包

    /**
     * item名字
     */
    private static final int NAME_SELECT_PHOTO = R.string.features_photo;
    private static final int NAME_TAKE_PHOTO = R.string.features_take_photo;
    private static final int NAME_CARD = R.string.features_card;
    private static final int NAME_ASK_FOR_ZSB = R.string.features_ask_for_zsb;
    private static final int NAME_GIFT_ZSB = R.string.features_gift_zsb;
    private static final int NAME_WHISPER = R.string.features_whisper;
    private static final int NAME_RED_PACKET = R.string.features_red_packet;


    /**
     * item图标
     */
    private static final int ICON_SELECT_PHOTO = R.drawable.btn_im_select_photo;
    private static final int ICON_TAKE_PHOTO = R.drawable.btn_im_take_photo;
    private static final int ICON_CARD = R.drawable.btn_im_add_card;
    private static final int ICON_ASK_FOR_ZSB = R.drawable.btn_im_ask_for_zscoin;
    private static final int ICON_GIFT_ZSB = R.drawable.btn_im_give_zscoin;
    private static final int ICON_WHISPER = R.drawable.btn_im_click_whisper_selector;
    private static final int ICON_RED_PACKET = R.drawable.btn_im_red_packet_selector;

    /**
     * 要用的 list
     */
    private static ArrayList<FeaturesBean> mFeaturesList = new ArrayList<FeaturesBean>();


    /**
     * 根据类型获取功能list
     *
     * @param chatType
     * @return
     */
    public static ArrayList<FeaturesBean> getFeaturesList(int chatType) {
        mFeaturesList.clear();
        switch (chatType) {
            case IConst.CHAT_TYPE_PRIVATE:
                mFeaturesList.add(new FeaturesBean(TYPE_SELECT_PHOTO, ICON_SELECT_PHOTO, NAME_SELECT_PHOTO));
                mFeaturesList.add(new FeaturesBean(TYPE_TAKE_PHOTO, ICON_TAKE_PHOTO, NAME_TAKE_PHOTO));
                mFeaturesList.add(new FeaturesBean(TYPE_CARD, ICON_CARD, NAME_CARD));
                mFeaturesList.add(new FeaturesBean(TYPE_ASK_FOR_ZSB, ICON_ASK_FOR_ZSB, NAME_ASK_FOR_ZSB));
                mFeaturesList.add(new FeaturesBean(TYPE_GIFT_ZSB, ICON_GIFT_ZSB, NAME_GIFT_ZSB));
                mFeaturesList.add(new FeaturesBean(TYPE_WHISPER, ICON_WHISPER, NAME_WHISPER));
                mFeaturesList.add(new FeaturesBean(TYPE_RED_PACKET, ICON_RED_PACKET, NAME_RED_PACKET,R.color.title_text_color));
                return mFeaturesList;
            case IConst.CHAT_TYPE_GROUP:
                mFeaturesList.add(new FeaturesBean(TYPE_SELECT_PHOTO, ICON_SELECT_PHOTO, NAME_SELECT_PHOTO));
                mFeaturesList.add(new FeaturesBean(TYPE_TAKE_PHOTO, ICON_TAKE_PHOTO, NAME_TAKE_PHOTO));
                mFeaturesList.add(new FeaturesBean(TYPE_CARD, ICON_CARD, NAME_CARD));
                mFeaturesList.add(new FeaturesBean(TYPE_RED_PACKET, ICON_RED_PACKET, NAME_RED_PACKET,R.color.title_text_color));
                return mFeaturesList;
            case IConst.CHAT_TYPE_SERVICE_MESSAGE:
                mFeaturesList.add(new FeaturesBean(TYPE_SELECT_PHOTO, ICON_SELECT_PHOTO, NAME_SELECT_PHOTO));
                mFeaturesList.add(new FeaturesBean(TYPE_TAKE_PHOTO, ICON_TAKE_PHOTO, NAME_TAKE_PHOTO));
                return mFeaturesList;
            default:
                return mFeaturesList;

        }
    }

}
