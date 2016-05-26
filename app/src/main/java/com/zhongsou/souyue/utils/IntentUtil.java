package com.zhongsou.souyue.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.im.db.module.IConst;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.AboutActivity;
import com.zhongsou.souyue.activity.ChannelMangerActivity;
import com.zhongsou.souyue.activity.ChargeActivity;
import com.zhongsou.souyue.activity.CommentaryActivity;
import com.zhongsou.souyue.activity.ContactUsActivity;
import com.zhongsou.souyue.activity.DimensionalCodeActivity;
import com.zhongsou.souyue.activity.FragmentMsgPushHistoryActivity;
import com.zhongsou.souyue.activity.GalleryNewsActivity;
import com.zhongsou.souyue.activity.GifPlayActivity;
import com.zhongsou.souyue.activity.ICommentaryActivity;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.LoginInputPhoneNumActivity;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.activity.Mine_PurseActivity;
import com.zhongsou.souyue.activity.Mine_ReadActivity;
import com.zhongsou.souyue.activity.MyFavoriteActivity;
import com.zhongsou.souyue.activity.MySubscribeListActivity;
import com.zhongsou.souyue.activity.RecommendFriendActivity;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.ScaningActivity;
import com.zhongsou.souyue.activity.SearchActivity;
import com.zhongsou.souyue.activity.SearchAllActivity;
import com.zhongsou.souyue.activity.SelfCreateActivity;
import com.zhongsou.souyue.activity.SouYueRssActivity;
import com.zhongsou.souyue.activity.SpecialTopicActivity;
import com.zhongsou.souyue.activity.SubGroupActivity;
import com.zhongsou.souyue.activity.SubGroupEditActivity;
import com.zhongsou.souyue.activity.SubSearchActivity;
import com.zhongsou.souyue.activity.SubscribeListActivity;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.activity.VideoDetailActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.activity.YaoWenActivity;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.circle.activity.CircleCheckRecordActivity;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.circle.activity.CircleQRcodeActivity;
import com.zhongsou.souyue.circle.activity.CircleReplyMeActivity;
import com.zhongsou.souyue.circle.activity.CircleSelImgGroupActivity;
import com.zhongsou.souyue.circle.activity.DetailActivity;
import com.zhongsou.souyue.circle.activity.DetailModuleActivity;
import com.zhongsou.souyue.circle.activity.HistoryActivity;
import com.zhongsou.souyue.circle.activity.SecretCircleCardActivity;
import com.zhongsou.souyue.circle.model.TaskCenterInfo;
import com.zhongsou.souyue.circle.model.VideoAboutResult;
import com.zhongsou.souyue.fragment.MineFragment;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.module.DetailItem;
import com.zhongsou.souyue.module.GalleryNewsHomeBean;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.SpecialDialogData;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.module.VideoDetailItem;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.ac.SrpWebViewActivity;
import com.zhongsou.souyue.receiver.TaskCenterReceiver;
import com.zhongsou.souyue.share.SpecialRecommendDialog;
import com.zhongsou.souyue.ui.lib.DialogHelper;
import com.zhongsou.souyue.ui.lib.DialogPlus;
import com.zhongsou.souyue.ui.subrecommend.SubRecommendDialog;
import com.zhongsou.souyue.view.TaskCenterToast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : zoulu 2014年4月4日 上午10:42:23 类说明 :用于各个Activity之间的跳转
 */
public class IntentUtil {

    public static final int IMSENDCOIN = 0x11;
    public static final int REQUEST_CODE_CHANNEL_MANNGER = 0x600;
    public static final int RESULT_CODE_CHANNEL_MANNGER = 0x601;
    public static final int REQUEST_CODE_EDIT_GROUP = 0x602;
    public static final int RESULT_CODE_EDIT_GROUP = 0x603;
    public static final int QR_M_SEARCH = 1; // 打开二维码，来源类型：搜索端
    private static final int SKIP_FOR_NO_RESULR = 0;
    private static final String SOURCE = "source";  //来源的参数名称

    /**
     * 跳到综合搜索
     *
     * @param act context
     */
    public static void openSearchActivity(Activity act) {
        Intent intent = new Intent();
        intent.setClass(act, SearchActivity.class);
        intent.putExtra(SearchActivity.PAGE_URL, UrlConfig.S_INDEX_PAGE);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到首页指定页
     *
     * @param cotext
     * @param pos    tab 下标
     */
    public static void openMainActivity(Context cotext, int... pos) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(cotext, CommonStringsApi.getHomeClass());
        if (pos != null && pos.length > 0) intent.putExtra(MainActivity.TAB_INDEX_EXTRA, pos[0]);
        cotext.startActivity(intent);
    }


    public static void openManagerAcitivity(Activity cx, Class<?> clz, Bundle... bundle) {
        Intent intent = new Intent();
        intent.setClass(cx, clz);
        if (bundle != null && bundle.length > 0) {
            Bundle bundle0 = bundle[0];
            intent.putExtra(MySubscribeListActivity.INTENT_INDEX, bundle0.getInt(MySubscribeListActivity.INTENT_INDEX, 0));
        }
        cx.startActivity(intent);
        cx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    /*
     * public static void openCircleTitorialActivity(Context ctx, String srp_id, String keyword,
     * String title, String imgUrl) { Intent managerIntent = new Intent(ctx,
     * CircleTutorialActivity.class); managerIntent.putExtra("srp_id", srp_id);
     * managerIntent.putExtra("keyword", keyword); managerIntent.putExtra("title", title);
     * managerIntent.putExtra("imgUrl", imgUrl); ctx.startActivity(managerIntent); }
     */
    public static void openSubscribeListActivity(Activity cx, int type) {
        Intent managerIntent = new Intent();
        managerIntent.putExtra(SubscribeListActivity.INTENT_INDEX, type);
        managerIntent.setClass(cx, SubscribeListActivity.class);
        cx.startActivity(managerIntent);
        cx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void gotoWeb(Context cx, String url, String type) {
        Intent appIntent = new Intent(cx, WebSrcViewActivity.class);
        appIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
        appIntent.putExtra(WebSrcViewActivity.PAGE_TYPE, type);
        cx.startActivity(appIntent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void gotoWebForResult(Context cx, String url, String type) {
        Intent appIntent = new Intent(cx, WebSrcViewActivity.class);
        appIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
        appIntent.putExtra(WebSrcViewActivity.PAGE_TYPE, type);
        ((Activity) cx).startActivityForResult(appIntent, 0x3);
        ;
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void gotoContactUS(Context cx) {
        Intent appIntent = new Intent(cx, ContactUsActivity.class);
        appIntent.putExtra(WebSrcViewActivity.PAGE_URL, UrlConfig.contactUsUrl);
        cx.startActivity(appIntent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 关于我们页面中，用户协议页面跳转方法 -- YanBin
     *
     * @param cx Content对象
     */
    public static void gotoUserAgreement(Context cx) {
        Intent appIntent = new Intent(cx, ContactUsActivity.class);
        appIntent.putExtra(WebSrcViewActivity.PAGE_URL, UrlConfig.userAgreementUrl);
        cx.startActivity(appIntent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void gotoWeb(Context cx, String url, String type, boolean flag) {
        Intent appIntent = new Intent(cx, WebSrcViewActivity.class);
        appIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
        appIntent.putExtra(WebSrcViewActivity.PAGE_TYPE, type);
        appIntent.putExtra(WebSrcViewActivity.GOTOIFRAGMENT, flag);
        cx.startActivity(appIntent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void gotoReplyMe(Context cx, String interest_id) {
        Intent appIntent = new Intent(cx, CircleReplyMeActivity.class);
        appIntent.putExtra("interest_id", interest_id);
        cx.startActivity(appIntent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

//    public static void gotoIfragment(Context cx) {
//        Intent intent = new Intent();
//        intent.setClass(cx, MultipleActivity.class);
//        intent.putExtra("fragmentType", MultipleActivity.IFRAGMENT);
//        cx.startActivity(intent);
//        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
//    }

    public static void gotoSecretCricleCard(Context cx, long interest_id) {
        Intent intent = new Intent();
        setSecretCircleData(cx, intent, interest_id, -1);
        cx.startActivity(intent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void gotoSecretCricleCard(Context cx, long interest_id, int type) {
        Intent intent = new Intent();
        setSecretCircleData(cx, intent, interest_id, type);
        cx.startActivity(intent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void gotoSecretCricleCard(Context cx, long interest_id, int type, int requestCode) {
        Intent intent = new Intent();
        setSecretCircleData(cx, intent, interest_id, type);
        ((Activity) cx).startActivityForResult(intent, requestCode);    //带返回结果
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private static void setSecretCircleData(Context cx, Intent intent, long interest_id, int type) {
        intent.setClass(cx, SecretCircleCardActivity.class);
        intent.putExtra(SecretCircleCardActivity.INTEREST_ID, interest_id);
        if (type != -1) {
            intent.putExtra(SecretCircleCardActivity.DIALOGTYPE, type);
        }
    }

    public static void gotoCircleQRcode(Context cx, String name, long interesid, String ImageUrl) {
        Intent intent = new Intent();
        intent.setClass(cx, CircleQRcodeActivity.class);
        intent.putExtra(CircleQRcodeActivity.NAME, name);
        intent.putExtra(CircleQRcodeActivity.INTERESTID, interesid);
        intent.putExtra(CircleQRcodeActivity.IMAGEURL, ImageUrl);
        cx.startActivity(intent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void gotoCircleCheckRecordActivity(Context cx) {
        Intent intent = new Intent();
        intent.setClass(cx, CircleCheckRecordActivity.class);
        cx.startActivity(intent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void gotoLogin(Context cx) {
        Intent intent = new Intent();
        intent.setClass(cx, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, true);
        cx.startActivity(intent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    // 修改密码登陆后跳到个人中心
    public static void gotoLoginIF(Context cx) {
        Intent intent = new Intent();
        intent.setClass(cx, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, false);
        cx.startActivity(intent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static boolean isLogin() {
        User user = SYUserManager.getInstance().getUser();
        return (user != null && user.userType().equals(SYUserManager.USER_ADMIN));
    }

    // /**
    // * 跳到搜悦RSS
    // *
    // * @param cx
    // * @param item
    // */
    // public static void gotoSouYueRss(Context cx, SubscribeItem item) {
    // Intent intent = new Intent(cx, SouYueRssActivity.class);
    // intent.putExtra("item", item);
    // cx.startActivity(intent);
    // }

    /**
     * 跳到搜悦RSS
     *
     * @param cx
     * @param item
     */
    public static void gotoSouYueRss(Context cx, SuberedItemInfo item) {
        Intent intent = new Intent(cx, SouYueRssActivity.class);
        intent.putExtra("item", item);
        cx.startActivity(intent);
    }

    /**
     * 跳转到SRP
     */
    public static void gotoSouYueSRP(Context cx, String keyword, String srpId, String imgUrl) {
        gotoSouYueSRP(cx, keyword, srpId, imgUrl, null);
    }

    /**
     * 跳转到SRP
     */
    public static void gotoSRP(Context cx, String keyword, String srpId, String imgUrl, String md5, String channel) {
        Intent intent = new Intent(cx, SRPActivity.class);
        intent.putExtra("keyword", keyword);
        intent.putExtra("srpId", srpId);
        intent.putExtra("isSearch", false);
        intent.putExtra("imgUrl", imgUrl);
        if (StringUtils.isNotEmpty(channel)) {//ZSSDK需要的统计参数
            intent.putExtra("opSource", channel);
        }
        if (StringUtils.isNotEmpty(md5)) {
            intent.putExtra("md5", md5);
        }
//        cx.startActivity(intent);
//        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
        exActivityAnimation(cx, intent, SKIP_FOR_NO_RESULR);
    }

    /**
     * 跳转到SRP by YanBin
     *
     * @param cx
     * @param keyword
     * @param srpId
     * @param imgUrl
     * @param opSource srp来源
     */
    public static void gotoSouYueSRP(Context cx, String keyword, String srpId, String imgUrl, String opSource) {
        Intent intent = new Intent(cx, SRPActivity.class);
        intent.putExtra("keyword", keyword);
        intent.putExtra("srpId", srpId);
        intent.putExtra("isSearch", false);
        intent.putExtra("imgUrl", imgUrl);
        if (StringUtils.isNotEmpty(opSource)) {
            intent.putExtra("opSource", opSource);
        }
        cx.startActivity(intent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到SRP
     */
    public static void gotoSouYueSRPAndFinish(Context cx, String keyword, String srpId, String imgUrl) {
        Intent intent = new Intent(cx, SRPActivity.class);
        intent.putExtra("keyword", keyword);
        intent.putExtra("srpId", srpId);
        intent.putExtra("isSearch", false);
        intent.putExtra("imgUrl", imgUrl);
        intent.putExtra("isfinish", true);
        cx.startActivity(intent);
    }

    /**
     * 跳转到图集类型
     *
     * @param ctx
     * @param _item
     */
    public static void getToGalleryNews(Context ctx, SearchResultItem _item) {
        Intent intent = new Intent(ctx, GalleryNewsActivity.class);
        GalleryNewsHomeBean galleryHomeBean = GalleryNewsHomeBean.SearchResult2GalleryHomeBean(_item);
        intent.putExtra("item", galleryHomeBean);
        ctx.startActivity(intent);
    }

    /**
     * @param ctx     上下文
     * @param srpid   --
     * @param title   --
     * @param desc    描述
     * @param url     地址
     * @param images  List<String> 图片地址，分享用 image1,iamge2 用,分割
     * @param source  来源 - 评论用
     * @param keyword 关键词
     * @param pubtime 发布时间 可以用date代替（时间戳字符串）
     * @param channel 频道，统计用
     */
    public static void getToGalleryNews(Context ctx, String srpid, String title, String desc, String url, String images, String source, String keyword, String pubtime, String channel) {
        Intent intent = new Intent(ctx, GalleryNewsActivity.class);
        GalleryNewsHomeBean galleryHomeBean = new GalleryNewsHomeBean(srpid, title, desc, url, Arrays.asList(images.split(",")), source, keyword, pubtime, channel);
        intent.putExtra("item", galleryHomeBean);
        ctx.startActivity(intent);
    }

    /**
     * 跳转图集页
     *
     * @param ctx
     * @param bean
     */
    public static void goToGalleryNews(Context ctx, GalleryNewsHomeBean bean) {
        Intent intent = new Intent(ctx, GalleryNewsActivity.class);
        intent.putExtra("item", bean);
//        ctx.startActivity(intent);
        exActivityAnimation(ctx, intent, SKIP_FOR_NO_RESULR);
    }

    /**
     * 跳转到要闻
     */
    public static void gotoSouYueYaoWen(Context cx, String channelid) {
        Intent intent = new Intent(cx, YaoWenActivity.class);
        /*
         * cx.startActivity(intent); ((Activity) cx).overridePendingTransition(R.anim.left_in,
         * R.anim.left_out);
         */
        intent.putExtra("category", channelid);//跳转频道页
        exActivityAnimation(cx, intent, -1);
    }

    public static void gotoActionView(Context activity, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        exActivityAnimation(activity, intent, SKIP_FOR_NO_RESULR);
    }

    public static void gotoWebSrcView(Context activity, String url) {
        Intent webViewIntent = new Intent();
        webViewIntent.setClass(activity, WebSrcViewActivity.class);
        webViewIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
        exActivityAnimation(activity, webViewIntent, SKIP_FOR_NO_RESULR);
    }

    public static void gotoSrpWebView(Context activity, BaseInvoke invoke) {
        Intent intent1 = new Intent(activity, SrpWebViewActivity.class);
        intent1.putExtra("invoke", invoke);
        intent1.putExtra(SrpWebViewActivity.PAGE_URL, invoke.getUrl());
        exActivityAnimation(activity, intent1, SKIP_FOR_NO_RESULR);
    }

    /**
     * 从 HomePagerSkipUtils 调用
     *
     * @param ac
     * @param item
     * @param resultCode
     */
    public static void skipSRPDetailPage(Context ac, DetailItem item, int resultCode) {
        Intent intent = new Intent(ac, DetailModuleActivity.class);
        intent.putExtra("SearchResultItem", item);
        exActivityAnimation(ac, intent, resultCode);
    }

    /**
     * 从 HomePagerSkipUtils 调用 段子、gif
     *
     * @param ac
     * @param item
     * @param resultCode
     */
    public static void skipOldSRPDetailPage(Context ac, DetailItem item, int resultCode) {
        Intent intent = new Intent(ac, DetailActivity.class);
        intent.putExtra("SearchResultItem", item);
        exActivityAnimation(ac, intent, resultCode);
    }

    public static void skipDetailPage(Activity _cx, SearchResultItem _item, int resultCode) {
        Intent intent = new Intent(_cx, DetailModuleActivity.class);
        DetailItem i = DetailItem.SearchResultToDetailItem(_item);
        intent.putExtra("SearchResultItem", i);
        _cx.startActivityForResult(intent, resultCode);
        _cx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    //相关博客 信息发布
    public static void skipOldDetailPage(Activity _cx, SearchResultItem _item, int resultCode) {
        Intent intent = new Intent(_cx, DetailActivity.class);
        DetailItem i = DetailItem.SearchResultToDetailItem(_item);
        intent.putExtra("SearchResultItem", i);
        _cx.startActivityForResult(intent, resultCode);
        _cx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    //收藏走此处
    public static void startskipDetailPage(Context _cx, SearchResultItem _item) {
        Intent intent = new Intent(_cx, DetailModuleActivity.class);
        DetailItem i = DetailItem.SearchResultToDetailItem(_item);
        intent.putExtra("SearchResultItem", i);
        _cx.startActivity(intent);
    }

    public static void skipDetailPushPage(Activity _cx, SearchResultItem _item, int resultCode) {
        Intent intent = new Intent(_cx, DetailModuleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DetailItem i = DetailItem.SearchResultToDetailItem(_item);
        intent.putExtra("SearchResultItem", i);
        _cx.startActivityForResult(intent, resultCode);
        _cx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    //Jpush 跳转详情
    public static void startskipDetailPushPage(Context _cx, SearchResultItem _item) {
        Intent intent = new Intent(_cx, DetailModuleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DetailItem i = DetailItem.SearchResultToDetailItem(_item);
        intent.putExtra("SearchResultItem", i);
        _cx.startActivity(intent);
    }

    /**
     *
     * @param _cx
     * @param _item
     */
    public static void startskipOldDetailPushPage(Context _cx, SearchResultItem _item) {
        Intent intent = new Intent(_cx, DetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DetailItem i = DetailItem.SearchResultToDetailItem(_item);
        intent.putExtra("SearchResultItem", i);
        _cx.startActivity(intent);
    }

    public static void startskipDetailPage(Context _cx, SearchResultItem _item, int _flag) {
        Intent intent = new Intent(_cx, DetailModuleActivity.class);
        DetailItem i = DetailItem.SearchResultToDetailItem(_item);
        intent.putExtra("SearchResultItem", i);
        intent.setFlags(_flag);
        _cx.startActivity(intent);
    }

    /**
     * 仅针对于发帖
     *
     * @param _cx
     * @param _item
     * @param point
     * @param broadcast_tag_id
     * @param broadcast_tag_name
     * @param isNew
     */
    public static void startskipDetailPage(Context _cx, SearchResultItem _item, int point, String broadcast_tag_id, String broadcast_tag_name, Boolean isNew) {
        Intent intent = new Intent(_cx, DetailModuleActivity.class);
        DetailItem i = DetailItem.SearchResultToDetailItem(_item);
        intent.putExtra("SearchResultItem", i);
        intent.putExtra("point", point);
        intent.putExtra("broadcast_tag_id", broadcast_tag_id);
        intent.putExtra("broadcast_tag_name", broadcast_tag_name);
        intent.putExtra("isNew", isNew);
        _cx.startActivity(intent);
    }

    public static void skipDetailPage(Context _cx, JSClick jsClick, int resultCode,
                                      String statisticsJumpPosition, long pushId,
                                      String mid, String clickFrom, String pushFrom) {
        SearchResultItem _item = new SearchResultItem();
        if (StringUtils.isNotEmpty(jsClick.getBlog_id())) {
            _item.setBlog_id(Long.parseLong(jsClick.getBlog_id()));
        }
        _item.srpId_$eq(jsClick.srpId());
        _item.keyword_$eq(jsClick.keyword());
        try {
            _item.url_$eq(URLDecoder.decode(jsClick.url(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        _item.title_$eq(jsClick.title());
        List<String> image = new ArrayList<String>();
        String[] images = null;
        if (jsClick.getImages() != null && jsClick.getImages().length() > 0) {
            images = jsClick.getImages().split(",");
        }
        if (images != null) {
            for (int i = 0; i < images.length; i++) {
                image.add(images[i]);
            }
        }
        _item.image_$eq(image);
        _item.description_$eq(jsClick.description());
        if (statisticsJumpPosition != null) {
            _item.setStatisticsJumpPosition(statisticsJumpPosition);
        }
        if (mid != null) {
            _item.setMsgId(mid);
        }
        if (clickFrom != null) {
            _item.setClickFrom(clickFrom);
        }

        if (pushFrom != null) {
            _item.setPushFrom(pushFrom);
        }
        if (pushId != 0) _item.pushId_$eq(pushId);
        Intent intent = new Intent(_cx, DetailModuleActivity.class);
        DetailItem i = DetailItem.SearchResultToDetailItem(_item);
        intent.putExtra("SearchResultItem", i);
        ((Activity) _cx).startActivityForResult(intent, resultCode);
    }

    /**
     * 赠币
     *
     * @param cx
     * @param url
     * @param type
     */
    public static void gotoWebSendCoin(Activity cx, String url, String type) {
        Intent appIntent = new Intent(cx, WebSrcViewActivity.class);
        appIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
        appIntent.putExtra(WebSrcViewActivity.PAGE_TYPE, type);
        cx.startActivityForResult(appIntent, IMSENDCOIN);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    // add by trade start

    /**
     * 跳转到我的原创
     */
    public static void gotoSelfCreate(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, SelfCreateActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    /**
     * 启动登录页面
     *
     * @param context
     * @param showPager 登录成功跳转的页面
     */
    public static void gotoLogin(Context context, String showPager) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.TAG, showPager);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 启动登录页面
     *
     * @param context
     * @param showPager 登录成功跳转的页面
     */
    public static void gotoLogin(Context context, String showPager, String title) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.TAG, showPager);
        intent.putExtra(ConstantsUtils.TITLE_TAG, title);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    /**
     * 启动移动商城
     *
     * @param context
     * @param action
     */
    public static void gotoShop(Context context, String action) {
        Intent appShopIntent = new Intent(action);
        appShopIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 传递相关参数
        String username = SYSharedPreferences.getInstance().getString(SYSharedPreferences.USERNAME, "");
        String password = SYSharedPreferences.getInstance().getString(SYSharedPreferences.PASSWORD, "");
        long uid = SYSharedPreferences.getInstance().getLong(SYSharedPreferences.UID, 0);
        int type = SYSharedPreferences.getInstance().getInt(SYSharedPreferences.TYPE, 0);
        Bundle bundle = new Bundle();
        bundle.putString("name", username);
        bundle.putString("password", password);
        bundle.putLong("uid", uid);
        bundle.putInt("type", type);
        appShopIntent.putExtras(bundle);
        context.startActivity(appShopIntent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 启动登录页面--移动商城用
     *
     * @param context
     * @param showPager 登录成功跳转的页面
     * @author action 商城启动action
     */
    public static void gotoLoginWithAction(Context context, String showPager, String action) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.TAG, showPager);
        intent.putExtra("shopAction", action);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到我的收藏
     *
     * @param cx
     */
    public static void gotoMyFavorite(Context cx) {
        Intent intent = new Intent();
        intent.setClass(cx, MyFavoriteActivity.class);
        cx.startActivity(intent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到中搜币商城
     *
     * @param cx
     */
    public static void gotoZSBMall(Context cx) {
        Intent intent = new Intent();
        intent.setClass(cx, WebSrcViewActivity.class);
        intent.putExtra(WebSrcViewActivity.PAGE_URL, UrlConfig.gift + "?&version=" + DeviceInfo.getAppVersion());
        intent.putExtra(WebSrcViewActivity.PAGE_TYPE, "youbao");
        cx.startActivity(intent);
        ((Activity) cx).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    public static void gotoSrp(final Context context, JSClick jsClick) {

        final Intent intent = new Intent(context, SRPActivity.class);
        setSrpData(intent, jsClick);
        exActivityAnimation(context, intent, -1);

    }

    public static void gotoSrp(final Context context, JSClick jsClick, int requestCode) {

        final Intent intent = new Intent(context, SRPActivity.class);
        setSrpData(intent, jsClick);
        exActivityAnimation(context, intent, requestCode);

    }

    private static void setSrpData(Intent intent, JSClick jsClick) {
        if (null != jsClick.keyword()) {
            intent.putExtra("keyword", jsClick.keyword());
        }
        if (null != jsClick.srpId()) {
            intent.putExtra("srpId", jsClick.srpId());
        }
        if (null != jsClick.md5()) {
            intent.putExtra("md5", jsClick.md5());
        }
        if (null != jsClick.title()) {
            intent.putExtra("currentTitle", jsClick.title());
        }
        if (null != jsClick.getOpSource()) {    //ZSSDK  统计来源字段
            intent.putExtra("opSource", jsClick.getOpSource());
        }
    }

    private static void exActivityAnimation(final Context context, final Intent intent, final int requestCode) {
        if (context != null && context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (requestCode == SKIP_FOR_NO_RESULR) {
                        ((Activity) context).startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    } else {
                        ((Activity) context).startActivityForResult(intent, requestCode);
                        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }

                }
            });
        } else if (context != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 跳转到SRPACtivity
     *
     * @param context
     * @param _srpid
     * @param _keyword
     * @param _md5
     * @param _title
     */
    public static void gotoSrp(Context context, String _srpid, String _keyword,
                               String _md5, String _title) {
        gotoSrp(context, _srpid, _keyword, _md5, _title, null);
    }

    /**
     * 跳转到SRPACtivity
     *
     * @param context
     * @param _srpid
     * @param _keyword
     * @param _md5
     * @param _title
     * @param opSource SRP 来源
     */
    public static void gotoSrp(Context context, String _srpid, String _keyword,
                               String _md5, String _title, String opSource) {
        Intent intent = new Intent(context, SRPActivity.class);
        intent.putExtra("keyword", _keyword);
        intent.putExtra("srpId", _srpid);
        intent.putExtra("md5", _md5);
        intent.putExtra("currentTitle", _title);
        if (StringUtils.isNotEmpty(opSource)) {
            intent.putExtra("opSource", opSource);
        }
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

//    /**
//     * 显示IM
//     *
//     * @param context
//     */
//    public static void gotoIM(Context context) {
//        Intent intent = new Intent();
//        intent.setClass(context, MultipleActivity.class);
//        intent.putExtra("fragmentType", MultipleActivity.CHATFRAGMENTTYPE);
//        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
//    }

//    /**
//     * 左树跳转IM判断user trade_goToIm:description. <br/>
//     *
//     * @param cx
//     * @param user
//     * @author zhanggang
//     * @date 2014-12-2 上午9:33:48
//     */
//    public static void goToIM_trade(Context cx, User user) {
//        // if (!TextUtils.isEmpty(user != null && "1".equals(user.userType()) ?
//        // user.name() : null)) {
//        gotoIM(cx);
//        // } else {
//        // //直接跳到消息将IM传给TAG
//        // Intent i = new Intent(cx, LoginActivity.class);
//        // i.putExtra(LoginActivity.TAG, SlidingMenuView.IM);
//        // cx.startActivity(i);
//        // ((Activity) cx).overridePendingTransition(R.anim.left_in,
//        // R.anim.left_out);
//        // }
//    }

    public static int toLogin(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, true);
        context.startActivity(intent);
        return ImJump2SouyueUtil.JSLOGINBACK;
    }

    // add by trade end
    // public static void toSlotMachine(Context context) {
    // Intent appIntent = new Intent(context, TigerGameActivity.class);
    // context.startActivity(appIntent);
    // ((Activity) context).overridePendingTransition(R.anim.left_in,
    // R.anim.left_out);
    // }

    public static void toAPPbible(Context context) {
        Intent appIntent = new Intent(MainActivity.ACTION_APPBIBLE);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(appIntent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    public static void toShowImage(Context context, JSClick jsClick) {
        Intent i = new Intent();
        i.setClass(context, TouchGalleryActivity.class);
        Bundle b = new Bundle();
        TouchGallerySerializable tgs = new TouchGallerySerializable();
        tgs.setClickIndex(jsClick.index());
        tgs.setItems(jsClick.imgs());
        b.putSerializable("touchGalleryItems", tgs);
        i.putExtras(b);
        context.startActivity(i);
    }

    public static void ZSBClose(Context context, JSClick jsClick) {
        Intent mIntent = new Intent();
        mIntent.putExtra("ZSBcount", jsClick.ZSBcount());
        ((Activity) context).setResult(WebSrcViewActivity.ZSBRESULT, mIntent);
        ((Activity) context).finish();
    }

    public static void openAPP(Context context, String packageName) {
        if (VersionUtils.checkPackage(context, packageName) == WebSrcViewActivity.INSTALL) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        }
    }

    public static void startExchangeAct(Context context) {
        Intent webViewIntent = new Intent(context, WebSrcViewActivity.class);
        webViewIntent.putExtra(WebSrcViewActivity.PAGE_URL, UrlConfig.gift);
        webViewIntent.putExtra(WebSrcViewActivity.PAGE_TYPE, "interactWeb");
        ((Activity) context).startActivityForResult(webViewIntent, EnvConfig.RESULT_DUIHUAN_BACK_SUCCESS);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void toBrowser(Context context, JSClick jsc) {
        Intent i = new Intent(context, WebSrcViewActivity.class);
        i.putExtra(WebSrcViewActivity.PAGE_URL, jsc.url());
        if (jsc.keyword().equals(ConstantsUtils.MEGAGAME_SEARCH_KEYWORD)) {
            i.putExtra(WebSrcViewActivity.PAGE_KEYWORD, jsc.keyword());
        }
        context.startActivity(i);
    }

    public static void toTel(Context context, JSClick jsc) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + jsc.phoneNumber()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void toStartSrcPage(Context context, JSClick jsc, SearchResultItem sri) {
        Intent intent = new Intent(context, WebSrcViewActivity.class);
        if (sri != null && StringUtils.isNotEmpty(sri.url())) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(WebSrcViewActivity.ITEM_INFO, sri);
            intent.putExtras(bundle);
        } else {
            intent.putExtra(WebSrcViewActivity.PAGE_TYPE, "nopara");
            intent.putExtra(WebSrcViewActivity.PAGE_URL, jsc.url());
        }
        /*
         * ((Activity) context).startActivityForResult(intent, 18); ((Activity)
         * context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
         */
        exActivityAnimation(context, intent, 18);
    }

    // public static void toWebView(Context context, JSClick jsc,
    // SearchResultItem sri) {
    // Intent intent = new Intent(context, ReadabilityActivity.class);
    // Bundle bundle = new Bundle();
    // bundle.putSerializable("searchResultItem", sri);
    // intent.putExtras(bundle);
    // ((Activity) context).startActivityForResult(intent, 0);
    // ((Activity) context).overridePendingTransition(R.anim.left_in,
    // R.anim.left_out);
    // }

    /**
     * 超级app使用，跳转到地图. <br/>
     *
     * @param context
     * @param jsc
     * @author liudl
     * @date 2014-11-12 上午10:32:42
     */
//    public static void toLocation(Context context, JSClick jsc) {
//        UIHelper.goToMapLocation(context, jsc.title(), "", Double.parseDouble(jsc.getLongitude()), Double.parseDouble(jsc.getLatitude()), jsc.title());
//    }

    /**
     * 注册
     *
     * @param context
     */
    public static void toRegister(Context context) {
        context.startActivity(new Intent(context, LoginInputPhoneNumActivity.class).putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.PHONEREG));
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 扫一扫
     *
     * @param context
     */
    public static void toScaning(Context context) {
        context.startActivity(new Intent(context, ScaningActivity.class));
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 我的订阅
     *
     * @param context
     */
    public static void toMySubscribe(Context context, int type) {
        context.startActivity(new Intent(context, MySubscribeListActivity.class).putExtra(MySubscribeListActivity.INTENT_INDEX, type));
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 我的订阅,右边的页面
     *
     * @param context
     */
    public static void toMySubscribeRight(Context context, int type, boolean isRightPage) {
        context.startActivity(new Intent(context, MySubscribeListActivity.class).putExtra(MySubscribeListActivity.INTENT_INDEX, type).putExtra(MySubscribeListActivity.FLAG_SOURCE_HOME, isRightPage));
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 订阅
     *
     * @param context
     */
    public static void toSubscribe(Context context, int type) {
        context.startActivity(new Intent(context, SubscribeListActivity.class).putExtra(SubscribeListActivity.INTENT_INDEX, type));
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转到订阅设置页面
     *
     * @param context
     */
    public static void gotoSubscribeEdit(Context context, String source) {
        Intent intent = new Intent(context, SubGroupEditActivity.class);
        intent.putExtra(SOURCE, source);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 详情评论
     *
     * @param context
     */
    public static void toComment(Context context, SearchResultItem searchResultItem) {
        Intent i = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("searchResultItem", searchResultItem);
        i.setClass(context, CommentaryActivity.class);
        i.putExtras(bundle);
        ((Activity) context).startActivityForResult(i, 1010);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 回首页
     *
     * @param context
     */
    public static void toSyHome(Context context, JSClick jsClick) {
        String typeStr = jsClick.getType();
        if (StringUtils.isNotEmpty(typeStr)) {
            Intent intent = new Intent();
            intent.setClass(context, MainActivity.class);
            if (typeStr.equals("syHome")) {
                intent.putExtra("TAB_TAG_EXTRA", MainActivity.SOUYUE_SPEC);
            } else if (typeStr.equals("discover")) {
                intent.putExtra("TAB_TAG_EXTRA", MainActivity.DISCOVER_SPEC);
            } else if (typeStr.equals("msg")) {
                intent.putExtra("TAB_TAG_EXTRA", MainActivity.MSG_SPEC);
            } else if (typeStr.equals("self")) {
                intent.putExtra("TAB_TAG_EXTRA", MainActivity.ME_SPEC);
            }

            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    /**
     * 打开无头无尾webview
     *
     * @param context
     */
    public static void toOpenNoTitle(Context context, JSClick jsc, SearchResultItem searchResultItem) {
        Intent i = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("searchResultItem", searchResultItem);
        bundle.putString("source_url", jsc.url());
        i.setClass(context, SrpWebViewActivity.class);
        i.putExtras(bundle);
        context.startActivity(i);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void toOpenNoTitleForUrl(Context context, String url, SearchResultItem searchResultItem) {
        Intent i = new Intent();
        i.setClass(context, SrpWebViewActivity.class);
        i.putExtra("source_url", url);
        i.putExtra("searchResultItem", searchResultItem);
        context.startActivity(i);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * add by yinguanping 显示或隐藏发现tab红点
     *
     * @param tag
     */
    public static void chageDiscoverTabRed(Context context, int tag) {
        Intent tabRedIntent = new Intent();
        tabRedIntent.setAction(UrlConfig.HIDE_TABRED_ACTION);
        tabRedIntent.putExtra("tag", tag);
        context.sendBroadcast(tabRedIntent);
    }

    /**
     * add by yinguanping 本地检查是否有缓存踢人消息，如果有，则发送广播通知踢人
     *
     * @param context
     */
    public static void checkKickUserMsg(Context context) {
        String token = SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_TASKCENTER_KICKUSER_TOKEN, "");
        String msg = SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_TASKCENTER_KICKUSER_MSG, "");

        // Toast.makeText(context, "token=" + token + "|||msg=" + msg,
        // Toast.LENGTH_LONG).show();
        if (context == null) return;

        if (token.length() > 0 && msg.length() > 0) {// 有缓存的消息，通知踢人
            TaskCenterInfo taskCenterInfo = new TaskCenterInfo();
            taskCenterInfo.setToken(token);
            taskCenterInfo.setMsg(msg);
            taskCenterInfo.setCategory("relogin");
            SYUserManager.userExitSouYue(context);
            IntentUtil.goHomeSouyue(context);
            TaskCenterToast taskCenterToast = new TaskCenterToast((Activity) context, taskCenterInfo);
            if (taskCenterToast.isOpenPop()) {// 如果当前有正在打开，则关闭当前，显示最新
                taskCenterToast.dissPopWindow();
            }
            taskCenterToast.showPopUpWindow();
            // 事件处理,更改pop状态
            taskCenterToast.setOpenPop(true);
            // 发送广播成功后清除本地缓存消息
            SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_TASKCENTER_KICKUSER_TOKEN);
            SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_TASKCENTER_KICKUSER_MSG);
        }
    }

    /**
     * add by wangchunyan 本地检查是否有缓存热点推荐数据，如果存则显示
     *
     * @param context
     */
    public static void checkSysRecommendMsg(Context context) {

        if (context == null) return;

        String popWindow = SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_SHOW_SYSTEM_SPECIAL, "");

        if (StringUtils.isNotEmpty(popWindow) && popWindow.equals("popWindow")) {// 有缓存的消息，弹出热点推荐
            //检测超时，5min
            if (Utils.checkOverTime(SYSharedPreferences.KEY_SHOW_TIMESTAMP_SPECIAL, TaskCenterReceiver.DIALOG_TIME_STEP)) {
                showSpecialRecommendDialog(context);
            } else {
                checkAndShowSubRecommendDialog(context);
            }
        } else {
            checkAndShowSubRecommendDialog(context);
        }

//            String special_timestamp = SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_SHOW_TIMESTAMP_SPECIAL, "");
//            // 同一用户两条消息之间的间隔超过5分钟的话客户端才处理
//            String[] special_timestamps = special_timestamp.split(",");
//            if (StringUtils.isEmpty(special_timestamp)) {
//                showSpecialRecommendDialog(context);
//            }
//            if (special_timestamps != null && special_timestamps.length > 1) {
//                User u = SYUserManager.getInstance().getUser();
//                if (u != null) {
//                    if (special_timestamps[0].equals(u.userId() + "")) {
//                        long currentTemp = System.currentTimeMillis();
//                        long lastTemp = Long.parseLong(special_timestamps[1]);
//                        // 5*60*1000
//                        if (currentTemp - lastTemp > TaskCenterReceiver.DIALOG_TIME_STEP) {
//                            showSpecialRecommendDialog(context);
//                        }
//                    } else {
//                        showSpecialRecommendDialog(context);
//                    }
//                } else {
//                    showSpecialRecommendDialog(context);
//                }
//            }
//        }else{
//            //检测弹出订阅框
//            checkAndShowSubRecommendDialog(context);
//        }
    }

    /**
     * 显示专题推荐弹框
     *
     * @param context
     */
    private static void showSpecialRecommendDialog(Context context) {
        final SpecialRecommendDialog specialdialog = SpecialRecommendDialog.getInstance();
        // 如果当前弹框正在显示的话，移除要显示的标记
        if (specialdialog.isShowing()) {
            SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SPECIAL);
        } else {
            //否则判断推荐弹框状态
            if (SubRecommendDialog.getIsShowingMe()) {
                //如果推荐弹框正在显示
                SubRecommendDialog instance = SubRecommendDialog.getInstance();
                if (instance != null) {
                    instance.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        //则设置dissmiss的时候显示另一弹框
                        public void onDismiss(DialogInterface dialog) {
                            specialdialog.getData();
                            SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SPECIAL);
                        }
                    });
                }
            } else {
                specialdialog.getData();
                SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SPECIAL);
            }
        }

    }

    /**
     * 检测推荐订阅弹框缓存& 弹出
     *
     * @param context
     */
    public static void checkAndShowSubRecommendDialog(Context context) {
        if (context == null) return;
        String popWindow = SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND, "");
//        if (StringUtils.isNotEmpty(popWindow) && popWindow.equals("subPopWindow")) {// 有缓存的消息，弹出热点推荐
//            if (!SpecialRecommendDialog.getInstance().isShowing()) {
//                //显示缓存数据
//                SubRecommendDialog.showDialog(context, false, null, null);
//            }
//        }
//        SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND);
//
        /**------------------------------------------***/
        if (StringUtils.isNotEmpty(popWindow) && popWindow.equals("subPopWindow")) {// 有缓存的消息，弹出热点推荐
            if (Utils.checkOverTime(SYSharedPreferences.KEY_SHOW_TIMESTAMP_SUBRECOMMEND, TaskCenterReceiver.DIALOG_TIME_STEP)) {
                showSubrecommendDialog(context);
            }
            SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND);
//            String special_timestamp = SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_SHOW_TIMESTAMP_SUBRECOMMEND, "");
//            // 同一用户两条消息之间的间隔超过5分钟的话客户端才处理
//            String[] special_timestamps = special_timestamp.split(",");
//            if (StringUtils.isEmpty(special_timestamp)) {
//                showSpecialRecommendDialog(context);
//            }
//            if (special_timestamps != null && special_timestamps.length > 1) {
//                User u = SYUserManager.getInstance().getUser();
//                if (u != null) {
//                    if (special_timestamps[0].equals(u.userId() + "")) {
//                        long currentTemp = System.currentTimeMillis();
//                        long lastTemp = Long.parseLong(special_timestamps[1]);
//                        // 5*60*1000
//                        if (currentTemp - lastTemp > TaskCenterReceiver.DIALOG_TIME_STEP) {
//                            showSubrecommendDialog(context);
//                        }
//                    } else {
//                        showSubrecommendDialog(context);
//                    }
//                } else {
//                    showSubrecommendDialog(context);
//                }
//            }
//            SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND);
        }
    }

    /**
     * 显示推荐订阅弹框
     *
     * @param context
     */
    private static void showSubrecommendDialog(final Context context) {
        // 如果当前弹框正在显示的话，移除要显示的标记
        if (SubRecommendDialog.getIsShowingMe()) {
            SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND);
        } else {
            //否则判断推荐弹框状态
            if (SpecialRecommendDialog.getInstance().isShowing()) {
                //如果推荐弹框正在显示
                SpecialRecommendDialog.getInstance().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    //则设置dissmiss的时候显示另一弹框
                    public void onDismiss(DialogInterface dialog) {
                        SubRecommendDialog.showDialog(context, false, null, null, false);
                        SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND);
                    }
                });
            } else {
                SubRecommendDialog.showDialog(context, false, null, null, false);
                SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEY_SHOW_SYSTEM_SUBRECOMMEND);
            }
        }
    }


    public static void goHomeSouyue(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, CommonStringsApi.getHomeClass());
        if (ConfigApi.isSouyue()) {
            intent.putExtra("TAB_TAG_EXTRA", MainActivity.SOUYUE_SPEC);
            intent.putExtra("fromkickedout", 1);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void goHomeMine(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, CommonStringsApi.getHomeClass());
        if (ConfigApi.isSouyue()) {
            intent.setClass(context, MainActivity.class);
            intent.putExtra("TAB_TAG_EXTRA", MineFragment.TAB_NAME);
        } 
        /*else {
            intent.setClass(context, MultipleActivity.class);
            intent.putExtra("fragmentType", MultipleActivity.IFRAGMENT);
        }*/
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转页面
     *
     * @param context
     * @param type
     * @param clazz
     */
    public static void startActivityWithAnim(Context context, String type, Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra("fragmentType", type);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转钱包页面
     *
     * @param context
     */
    public static void startPurseActivityWithAnim(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, Mine_PurseActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转阅读页面
     *
     * @param context
     */
    public static void startReadActivityWithAnim(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, Mine_ReadActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

//    /**
//     * 跳转阅读_离线页面
//     *
//     * @param context
//     */
//    public static void startRead_DownLoadActivityWithAnim(Context context) {
//        Intent intent = new Intent();
//        intent.setClass(context, MyDownLoadActivity.class);
//        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
//    }


    public static void startRead_HistoryActivityWithAnim(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, HistoryActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转阅读_收藏页面
     *
     * @param context
     */
    public static void startRead_FavoriteActivityWithAnim(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MyFavoriteActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转阅读_评论页面
     *
     * @param context
     */
    public static void startRead_CommentaryActivityWithAnim(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ICommentaryActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 跳转阅读_原创/帖子页面
     *
     * @param context
     */
    public static void startRead_isCircleActivityWithAnim(Context context, int state) {
        Intent intent = new Intent();
        intent.setClass(context, SelfCreateActivity.class);
        intent.putExtra("state", state);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 打开关于我们页面
     */
    public static void startAboutActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AboutActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 打开推荐给好友页面
     *
     * @param context
     */
    public static void StartFriendActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RecommendFriendActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

//    /**
//     * 打开账号绑定页面
//     *
//     * @param context
//     */
//    public static void StartAccountBoundActivity(Context context) {
//        Intent intent = new Intent();
//        intent.setClass(context, AccountBoundActivity.class);
//        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
//    }

    /**
     * 消息推送列表页面
     */
    public static void StartPushHistoryActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FragmentMsgPushHistoryActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 搜小月页面
     */
    public static void StartIMSouYueActivity(Context context) {
        TuitaIMManager.createSouxiaoyue(context, Long.parseLong(SYUserManager.getInstance().getUserId()));
        IMChatActivity.invoke(context, IConst.CHAT_TYPE_SERVICE_MESSAGE, IConst.SOUXIAOYUE_ID);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);

    }

    /**
     * 从要闻跳转到频道管理界面
     */

    public static void startChannelManngerActivity(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, ChannelMangerActivity.class);
        context.startActivityForResult(intent, REQUEST_CODE_CHANNEL_MANNGER);
    }

    /**
     * 跳转到图片选择
     */
    public static void jumpImgGroup(Activity context, int len) {
        Intent intent = new Intent(context, CircleSelImgGroupActivity.class);
        intent.putExtra("piclen", len);
        context.startActivityForResult(intent, 1);
    }

    public static void openSearchDialog(Context context, JSClick jsc) {
        if (context != null) {
            if (context instanceof SearchActivity) {
                if (StringUtils.isNotEmpty(jsc.getType())) {
                    DialogHelper.getInstance().showDialog((SearchActivity) context, jsc.getType().equals("0") ? DialogPlus.ScreenType.HALF : DialogPlus.ScreenType.FULL, jsc);
                }

            } else if (context instanceof SrpWebViewActivity) {
                DialogHelper.getInstance().showDialog((SrpWebViewActivity) context, DialogPlus.ScreenType.HALF, jsc);
            }
        }
    }

    public static void openQRCode(Context context, JSClick jsc) {
        Intent intent = new Intent(context, DimensionalCodeActivity.class);
        intent.putExtra(DimensionalCodeActivity.INTENT_K, jsc.url());
        intent.putExtra(DimensionalCodeActivity.INTENT_ID, "");
        intent.putExtra(DimensionalCodeActivity.INTENT_URL, jsc.image());
        intent.putExtra(DimensionalCodeActivity.INTENT_FROM_TYPE, QR_M_SEARCH);
        context.startActivity(intent);
    }

    public static void gotoCircleIndex(Context context, String srp_id, String keyword, String interest_name, String interest_logo) {
        Intent intent = new Intent(context, CircleIndexActivity.class);
        intent.putExtra("srp_id", srp_id);
        intent.putExtra("keyword", keyword);
        intent.putExtra("interest_name", keyword);
        intent.putExtra("interest_logo", interest_logo);
        exActivityAnimation(context, intent, -1);
    }

    public static void gotoCircleIndex(Context context, String srp_id, String keyword, String interest_name, String interest_logo, String md5) {
        Intent intent = new Intent(context, CircleIndexActivity.class);
        intent.putExtra("srp_id", srp_id);
        intent.putExtra("keyword", keyword);
        intent.putExtra("interest_name", interest_name);
        intent.putExtra("interest_logo", interest_logo);
        if (StringUtils.isNotEmpty(md5)) {
            intent.putExtra("md5", md5);
        }
        exActivityAnimation(context, intent, -1);
    }

    /**
     * 跳转到 专题页面
     *
     * @param context
     * @param data
     */
    public static void gotoSpecilTopic(Context context, SpecialDialogData data) {
        gotoSpecilTopic(context, data.getKeyword(), data.getUrl(), null, data.getSrpid(), data.getDescreption(), data.getPic());
    }

    /**
     * 跳转到 专题页面 - YanBin
     *
     * @param context
     * @param title
     * @param url
     * @param channel
     */
    public static void gotoSpecilTopic(Context context, String title, String url, String channel, String srpId, String descreption, String imgUrl) {
        Intent intent = new Intent(context, SpecialTopicActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("channel", channel);
        intent.putExtra("Srpid", srpId);
        intent.putExtra("descreption", descreption);
        intent.putExtra("imgUrl", imgUrl);
//        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
        exActivityAnimation(context, intent, SKIP_FOR_NO_RESULR);
    }

    /**
     * 跳转图集
     *
     * @param context
     * @param info
     */
    public static void StartGalleryNewsActivity(Context context, GalleryNewsHomeBean info) {
        Intent intent = new Intent();
        intent.putExtra("item", info);
        intent.setClass(context, GalleryNewsActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 去充值   from com.zhongsou.souyue.ent.ui.UIHelper
     *
     * @param context
     */
    public static void gotoPay(Activity context) {
        Intent chargeIntent = new Intent(context, ChargeActivity.class);
        context.startActivity(chargeIntent);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 去充值  from com.zhongsou.souyue.ent.ui.UIHelper
     *
     * @param context
     */
    public static void gotoPay(Activity context, int request_code) {
        Intent chargeIntent = new Intent(context, ChargeActivity.class);
        context.startActivityForResult(chargeIntent, request_code);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    //-------- delete ent extract method -YanBin-----

    /**
     * 跳转登录页面  from com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
     *
     * @param context
     * @param isCallBack 登录成功后是否回到原页面,true 返回;反之
     */
    public static void goLogin(Context context, boolean isCallBack) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("Only_Login", isCallBack);
        context.startActivity(intent);
    }

    /**
     * 跳转登录页面  from com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
     *
     * @param context
     * @param code    登录成功后是否回到原页面,true 返回;反之
     */
    public static void goLoginForResult(Activity context, int code) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("Only_Login", true);
        context.startActivityForResult(intent, code);
    }

    /**
     * 跳转到gif 全屏播放f
     *
     * @param context
     * @param url
     */
    public static void goGifPlay(Context context, String url) {
        Intent intent = new Intent(context, GifPlayActivity.class);
        intent.putExtra("gif_url", url);
        context.startActivity(intent);
    }

    /**
     * 跳转到视频详情页
     *
     * @param context
     * @param
     */
    public static void gotoVideoDetail(Context context, BaseInvoke invoke, int currentPosition) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        VideoDetailItem item = VideoDetailItem.InVokeToVideoDetailItem(invoke);
        item.setPalyPosition(currentPosition);
        intent.putExtra("VideoDetailItem", item);
        ((Activity) context).startActivityForResult(intent, HomeListManager.VIDEO_VIEW_SEEK_POSITION_REQUEST_CODE);

    }

    public static void gotoVideoDetail(Context context, SearchResultItem searchResultItem) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        VideoDetailItem item = VideoDetailItem.SearchResultItemToVideoDetailItem(searchResultItem);
        item.setPalyPosition(0);
        intent.putExtra("VideoDetailItem", item);
        ((Activity) context).startActivityForResult(intent, HomeListManager.VIDEO_VIEW_SEEK_POSITION_REQUEST_CODE);

    }

    /**
     * 跳转到视频详情页
     *
     * @param context
     * @param
     */
    public static void gotoVideoDetail(Context context, VideoAboutResult result) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        VideoDetailItem item = VideoDetailItem.ResultToVideoDetailItem(result);
        item.setPalyPosition(0);
        intent.putExtra("VideoDetailItem", item);
        context.startActivity(intent);

    }


    /**
     * 到 订阅分组 创建 和 编辑页面
     *
     * @param context
     * @param groupId
     * @param title
     * @param C_E_tag 创建和编辑  1:创建 2 编辑
     */
    public static void gotoSubGroupEdit(Context context, String groupId, String title, int C_E_tag, String groupImage) {
        Intent intent = new Intent(context, SubGroupEditActivity.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("title", title);
        intent.putExtra("C_E_tag", C_E_tag);
        intent.putExtra("groupImage", groupImage);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_EDIT_GROUP);
        } else {
            context.startActivity(intent);
        }
    }

//    /**
//     * 全屏
//     *
//     * @param mContext
//     * @param videoUrl
//     * @param currentPosition
//     */
//    public static void gotoFullScreen(Context mContext, String videoUrl, int currentPosition,String status) {
//        Intent intent = new Intent(new Intent(mContext, VideoFullScreenActivity.class));
//        intent.putExtra("videoUrl", videoUrl);
//        intent.putExtra("position", currentPosition);
//        intent.putExtra("status", status);
//        ((Activity) mContext).startActivityForResult(intent, HomeListManager.VIDEO_VIEW_SEEK_POSITION_REQUEST_CODE);
//    }

    /**
     * 去分组首页 （一个二级导航 + 一个list）
     *
     * @param context
     * @param groupId
     * @param title
     */
    public static void gotoSubGroupHome(Context context, String groupId, String title, String group_image) {
        Intent intent = new Intent(context, SubGroupActivity.class);
        intent.putExtra(SubGroupActivity.INTENT_EXTRA_GROUP_ID, groupId);
        intent.putExtra(SubGroupActivity.INTENT_EXTRA_TITLE, title);
        intent.putExtra(SubGroupActivity.INTENT_EXTRA_IMAGE, group_image);
        context.startActivity(intent);
    }


    public static void gotoSubSearch(Context context, String source) {
        Intent intent = new Intent(context, SubSearchActivity.class);
        intent.putExtra("source", source);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 推送过来的图集页面
     *
     * @param activity
     * @param srpId
     */
    public static void goPushGalleryNews(Context activity, String srpId) {
        Intent intent = new Intent(activity, GalleryNewsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(GalleryNewsActivity.EXTRA_PUSH_ID, srpId);
        intent.putExtra(GalleryNewsActivity.EXTRA_IS_FROM_PUSH, true);
        activity.startActivity(intent);
//        ((Activity) activity).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    /**
     * 跳转到新的搜索页面 v5.2.0
     *
     * @param context
     * @param source  来源
     */
    public static void gotoSearchAll(Context context, String source) {
        Intent intent = new Intent(context, SearchAllActivity.class);
        intent.putExtra("source", source);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    /**
     * 全屏
     *
     * @param mContext
     * @param videoUrl
     * @param currentPosition
     */
    public static void gotoFullScreen(Context mContext, String videoUrl, int currentPosition, String status) {
//        Intent intent = new Intent(new Intent(mContext, VideoFullScreenActivity.class));
//        intent.putExtra("videoUrl", videoUrl);
//        intent.putExtra("position", currentPosition);
//        intent.putExtra("status", status);
//        ((Activity) mContext).startActivityForResult(intent, HomeListManager.VIDEO_VIEW_SEEK_POSITION_REQUEST_CODE);
    }
}

