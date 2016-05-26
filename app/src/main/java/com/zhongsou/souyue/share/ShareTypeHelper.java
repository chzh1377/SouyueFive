package com.zhongsou.souyue.share;

import com.zhongsou.souyue.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过此类获取各种分享的类型
 *
 * @author wanglong
 */
@SuppressWarnings("boxing")
public class ShareTypeHelper {
    private static final List<ShareType> mAllTypes = new ArrayList<ShareType>();
    private static final Map<ShareType, Integer> mIds = new HashMap<ShareType, Integer>();
    private static final Map<ShareType, Integer> mDrawables = new HashMap<ShareType, Integer>();

    static {
        for (ShareType type : ShareType.values()) {
            mAllTypes.add(type);
        }
        mIds.put(ShareType.搜悦好友, 9);
        mDrawables.put(ShareType.搜悦好友, R.drawable.ic_souyuefriends_icon);
        mIds.put(ShareType.兴趣圈, 10);
        mDrawables.put(ShareType.兴趣圈, R.drawable.circle_primeicon);
       /**
        * 5.2 删除的
        * mIds.put(搜悦网友, 8);
        mDrawables.put( R.drawable.ic_sy_friend_icon);
        mIds.put(ShareType.网友推荐区, 0);
        mDrawables.put(ShareType.网友推荐区, R.drawable.ic_sy_digest_icon);
        **/
        mIds.put(ShareType.QQ, 11);
        mDrawables.put(ShareType.QQ, R.drawable.ic_tencent_qq_friend_icon);
        mIds.put(ShareType.QQ空间, 12);
        mDrawables.put(ShareType.QQ空间, R.drawable.ic_tencent_qq_zone_icon);
        mIds.put(ShareType.微信好友, 2);
        mDrawables.put(ShareType.微信好友, R.drawable.ic_weix_icon);
        mIds.put(ShareType.朋友圈, 3);
        mDrawables.put(ShareType.朋友圈, R.drawable.ic_friends_quan_icon);
        mIds.put(ShareType.新浪微博, 1);
        mDrawables.put(ShareType.新浪微博, R.drawable.ic_sina_icon);
        /*mIds.put(ShareType.腾讯微博, 7);
		mDrawables.put(ShareType.腾讯微博, R.drawable.ic_tencent_icon);
		mIds.put(ShareType.邮件, 4);
		mDrawables.put(ShareType.邮件, R.drawable.ic_email_icon);
		mIds.put(ShareType.信息, 5);
		mDrawables.put(ShareType.信息, R.drawable.ic_sms_icon);*/
    }

    /**
     * 从默认分享按钮中拷贝一份，并且删除types中定义的按钮
     *
     * @param types
     * @return
     */
    private static List<ShareType> getAllThenRemove(ShareType... types) {
        List<ShareType> result = new ArrayList<ShareType>(mAllTypes);
        for (ShareType t : types) {
            result.remove(t);
        }
        return result;
    }

    /**
     * 得到分享按钮的Drawable
     *
     * @param types
     * @return
     */
    public static int[] getDrawables(List<ShareType> types) {
        int[] drawables = new int[types.size()];
        for (int i = 0; i < types.size(); i++) {
            drawables[i] = mDrawables.get(types.get(i));
        }
        return drawables;
    }

    /**
     * 得到分享按钮的id
     *
     * @param types
     * @return
     */
    public static int[] getIds(List<ShareType> types) {
        int[] ids = new int[types.size()];
        for (int i = 0; i < types.size(); i++) {
            ids[i] = mIds.get(types.get(i));
        }
        return ids;
    }

    /**
     * 只保留types中定义的分享按钮
     *
     * @param types
     * @return
     */
    private static List<ShareType> getOnlyHave(ShareType... types) {
        List<ShareType> result = new ArrayList<ShareType>();
        for (ShareType t : types) {
            result.add(t);
        }
        return result;
    }

    /**
     * 得到分享按钮的标题
     *
     * @param types
     * @return
     */
    public static String[] getTitles(List<ShareType> types) {
        String[] titles = new String[types.size()];
        for (int i = 0; i < types.size(); i++) {
            titles[i] = types.get(i).name();
        }
        return titles;
    }

    /**
     * circle_share_names
     * <item>搜悦好友</item>
     * <item>兴趣圈</item>
     * <item>QQ</item>
     * <item>QQ空间</item>
     * <item>微信好友</item>
     * <item>朋友圈</item>
     * <item>新浪微博</item>
     * <item>腾讯微博</item>
     * <item>邮件</item>
     * <item>信息</item>
     */
    public static List<ShareType> without搜悦网友And网友推荐区() {
        return getAllThenRemove();
    }

    /**
     * circle_share_names_no_digest
     * <item>搜悦好友</item>
     * <item>新浪微博</item>
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     * <item>腾讯微博</item>
     * <item>邮件</item>
     * <item>信息</item>
     */
    public static List<ShareType> circle_share_names_no_digest() {
        return getAllThenRemove(ShareType.兴趣圈, ShareType.QQ, ShareType.QQ空间);
    }

    /**
     * nowx_circle_share_names_no_digest
     * <item>搜悦好友</item>
     * <item>新浪微博</item>
     */
    public static List<ShareType> nowx_circle_share_names_no_digest() {
        return getOnlyHave(ShareType.搜悦好友, ShareType.新浪微博);
    }

    /**
     * nowx_circle_share_names_no_digest_weixin
     * <item>搜悦好友</item>
     * <item>新浪微博</item>
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     */
    public static List<ShareType> nowx_circle_share_names_no_digest_weixin() {
        return getAllThenRemove(ShareType.兴趣圈, ShareType.QQ, ShareType.QQ空间);
    }
    /**re_share_names
     <item>QQ</item>
     <item>QQ空间</item>
     <item>微信好友</item>
     <item>微信朋友圈</item>
     <item>新浪微博</item>
     <item>腾讯微博</item>
     <item>邮件</item>
     <item>信息</item>
     public static List<ShareType> re_share_names() {
     return getAllThenRemove(ShareType.搜悦好友, ShareType.兴趣圈, );
     }
     */
    /**
     * re_share_names_code
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     * <item>新浪微博</item>
     */
    public static List<ShareType> has微信And微博And邮件() {
        return getOnlyHave(ShareType.微信好友, ShareType.朋友圈, ShareType.新浪微博);
    }
    /**re_share_names_code_weixin
     <item>新浪微博</item>
     <item>微信好友</item>
     <item>微信朋友圈</item>
     <item>腾讯微博</item>
     <item>邮件</item>
     public static List<ShareType> re_share_names_code_weixin() {
     return getOnlyHave(ShareType.微信好友, ShareType.朋友圈, ShareType.新浪微博, ShareType.腾讯微博, ShareType.邮件);
     }
     */
    /**
     * re_share_names_code2
     * <item>新浪微博</item>
     */
    public static List<ShareType> re_share_names_code2() {
        return getOnlyHave(ShareType.新浪微博);
    }

    /**
     * re_share_names_weixin
     * <item>新浪微博</item>
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     */
    public static List<ShareType> without搜悦AndQQ() {
        return getAllThenRemove(ShareType.搜悦好友, ShareType.兴趣圈, ShareType.QQ, ShareType.QQ空间);
    }

    /**
     * re_share_names2
     * <item>新浪微博</item>
     */
    public static List<ShareType> re_share_names2() {
        return getOnlyHave(ShareType.新浪微博);
    }

    /**
     * share_names
     * <item>搜悦好友</item>
     * <item>兴趣圈</item>
     * <item>搜悦网友</item>
     * <item>网友推荐区</item>
     * <item>QQ</item>
     * <item>QQ空间</item>
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     * <item>新浪微博</item>
     */
    public static List<ShareType> hasAll() {
        return getAllThenRemove();
    }

    /**
     * share_names_rankdetail
     * <item>搜悦好友</item>
     * <item>兴趣圈</item>
     * <item>搜悦网友</item>
     * <item>QQ</item>
     * <item>QQ空间</item>
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     * <item>新浪微博</item>
     */
    public static List<ShareType> without网友推荐区() {
        return getAllThenRemove();
    }

    /**
     * share_names_rankdetail_weixin
     * <item>搜悦好友</item>
     * <item>兴趣圈</item>
     * <item>搜悦网友</item>
     * <item>新浪微博</item>
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     * <item>腾讯微博</item>
     * <item>邮件</item>
     * <item>信息</item>
     */
    public static List<ShareType> without网友推荐区AndQQ() {
        return getAllThenRemove( ShareType.QQ, ShareType.QQ空间);
    }

    /**
     * share_names_rankdetail2
     * <item>搜悦好友</item>
     * <item>兴趣圈</item>
     * <item>搜悦网友</item>
     * <item>新浪微博</item>
     */
    public static List<ShareType> without网友推荐区And腾讯() {
        return getAllThenRemove( ShareType.QQ, ShareType.QQ空间, ShareType.微信好友, ShareType.朋友圈);
    }

    /**
     * share_names_rss
     * <item>QQ</item>
     * <item>QQ空间</item>
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     * <item>新浪微博</item>
     */
    public static List<ShareType> without搜悦() {
        return getAllThenRemove(ShareType.搜悦好友, ShareType.兴趣圈);
    }
    /**share_names_rss_weixin
     <item>搜悦好友</item>
     <item>兴趣圈</item>
     <item>QQ</item>
     <item>QQ空间</item>
     <item>新浪微博</item>
     <item>微信好友</item>
     <item>微信朋友圈</item>
     <item>腾讯微博</item>
     <item>邮件</item>
     <item>信息</item>
     public static List<ShareType> share_names_rss_weixin() {
     return getAllThenRemove();
     }
     */
    /**
     * share_names_rss2
     * <item>搜悦好友</item>
     * <item>兴趣圈</item>
     * <item>新浪微博</item>
     */
    public static List<ShareType> without搜悦网友And网友推荐区And腾讯() {
        return getAllThenRemove( ShareType.QQ, ShareType.QQ空间, ShareType.微信好友, ShareType.朋友圈);
    }

    /**
     * share_names_srp
     * <item>搜悦好友</item>
     * <item>QQ</item>
     * <item>QQ空间</item>
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     * <item>新浪微博</item>
     */
    public static List<ShareType> without搜悦好友And第三方() {
        return getAllThenRemove(ShareType.兴趣圈 );
    }

    /**
     * share_names_weixin
     * <item>搜悦好友</item>
     * <item>兴趣圈</item>
     * <item>搜悦网友</item>
     * <item>网友推荐区</item>
     * <item>新浪微博</item>
     * <item>微信好友</item>
     * <item>微信朋友圈</item>
     */
    public static List<ShareType> withoutQQ() {
        return getAllThenRemove(ShareType.QQ, ShareType.QQ空间);
    }

    /**
     * share_names2
     * <item>搜悦好友</item>
     * <item>兴趣圈</item>
     * <item>搜悦网友</item>
     * <item>网友推荐区</item>
     * <item>新浪微博</item>
     */
    public static List<ShareType> without腾讯() {
        return getAllThenRemove(ShareType.QQ, ShareType.QQ空间, ShareType.微信好友, ShareType.朋友圈);
    }

    /**
     * 图集新闻的分享类型，第三方+ 圈子
     *
     * @return
     */
    public static List<ShareType> withThirdpartAndCircle() {
        return getAllThenRemove(ShareType.搜悦好友);
    }
}