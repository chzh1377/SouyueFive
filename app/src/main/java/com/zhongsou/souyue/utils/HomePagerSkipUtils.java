package com.zhongsou.souyue.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.module.DetailItem;
import com.zhongsou.souyue.module.GalleryNewsHomeBean;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页跳转类
 * Created by lvqiang on 15/10/26.
 */
public class HomePagerSkipUtils {
    public static final int REQUEST_DETAIL_CODE = 10001;

    public static final int SKIP_SRP=0;//跳转srp页
    public static final int SKIP_SEARCH=1;//跳转搜索页
    public static final int SKIP_MASTERNEWS=2;//跳转要闻页
    public static final int SKIP_SWITCHIMAGES=3;//跳转图集页
    public static final int SKIP_CIRCLEHOME=4;//跳转圈子首页
    public static final int SKIP_SPECIL=5;//跳转专题页
    public static final int SKIP_WEBSITE=6;//跳转网址页

    public static final int SKIP_DETAIL=7;//跳转详情页
    public static final int SKIP_SUBSCRIB=8;//跳转订阅页
    public static final int SKIP_SUBSCRIB_SUBALL = 9 ;//跳转订阅页的订阅大全

    public static final int ERROR_EMPTY_SRPID=1;//srpid 为空
    public static final int ERROR_EMPTY_KEYWORD=1<<1;//keyword为空
    public static final int ERROR_EMPTY_URL=1<<2;//url为空
    public static final int ERROR_EMPTY_CATEGORY=1<<3;//category为空
    public static final int ERROR_EMPTY_TITLE_DESC=1<<4;//title或者描述为空了
    public static final int ERROR_EMPTY_INTERESTID=1<<5;//兴趣圈id没传
    public static final int ERROR_EMPTY_BLOGID=1<<6;//blogid没传
    public static final int ERROR_EMPTY_SHARE_IMAGE=1<<7;//分享的image没有

    public static String getErrorMessage(BaseInvoke invoke,String code){
        StringBuilder builder = new StringBuilder();
        builder.append("&token=");
        builder.append(Utils.encode(SYUserManager.getInstance().getToken()));
        builder.append("&appName=");
        builder.append(Utils.encode(com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance())));
        builder.append("&vc=");
        builder.append(Utils.encode(DeviceInfo.getAppVersion()));
        builder.append("&url=");
        builder.append(Utils.encode(invoke.getUrl()));
        builder.append("&srpId=");
        builder.append(Utils.encode(invoke.getSrpId()));
        builder.append("&blogId=");
        builder.append(Utils.encode(invoke.getBlogId()));
        builder.append("&channel=");
        builder.append(Utils.encode(invoke.getChannelId()));
        builder.append("&invokeType=");
        builder.append(Utils.encode(invoke.getType()));
        builder.append("&errorCode=");
        builder.append(Utils.encode(code));
//        builder.append("&data=");
//        BaseListData data = invoke.getData();
//        if (data!=null) {
//            builder.append(Utils.encode(data.getJsonResource()));
//        }
        return builder.toString();
    }

    public static void skip(Context activity,BaseInvoke invoke){
        int errorcode = checkSkip(invoke);
        if (errorcode>0){
            Intent webViewIntent = new Intent();
            webViewIntent.setClass(activity, WebSrcViewActivity.class);
            String code = getErrorCode(invoke.getType(),errorcode);
            webViewIntent.putExtra(WebSrcViewActivity.PAGE_URL, UrlConfig.HOSE_ERROR_URL+"?"+getErrorMessage(invoke,code));
            activity.startActivity(webViewIntent);
            Utils.makeToastTest(activity,"跳转出错，错误码："+invoke.getType()+errorcode);
            return;
        }
        switch (invoke.getType()){
            /** srp新闻详情页 */
            case BaseInvoke.INVOKE_TYPE_SRP:

                DetailItem item1 = new DetailItem();
                item1.setKeyword(invoke.getKeyword());
                item1.setSrpId(invoke.getSrpId());
                item1.setUrl(invoke.getUrl());
                item1.setId(invoke.getId()+"");

                item1.setCategory(invoke.getCategory());
                item1.setChannel(invoke.getChan());
                item1.setDescription(invoke.getDesc());
                item1.setTitle(invoke.getTitle());
                if (invoke.getFlag(BaseInvoke.FLAG_SKIP_COMMENT)){
                    item1.setSkip(DetailItem.SKIP_TO_COMMENT);
                }
                if(invoke.getIsInvokeTemplate()==BaseInvoke.INVOKETEMPLATE)
                {
                    IntentUtil.skipSRPDetailPage(activity,item1,REQUEST_DETAIL_CODE);
                }else
                {
                    IntentUtil.skipOldSRPDetailPage(activity, item1, REQUEST_DETAIL_CODE);
                }
                break;
            /** srp新闻列表页 */
            case BaseInvoke.INVOKE_TYPE_SRP_INDEX:
                IntentUtil.gotoSRP(activity, invoke.getKeyword(), invoke.getSrpId(), invoke.getIconUrl(), invoke.getMd5(), invoke.getChan());
                break;
            /** 圈贴详情页 */
            case BaseInvoke.INVOKE_TYPE_INTEREST:
                DetailItem item2 = new DetailItem();
                item2.setInterestId(invoke.getInterestId());
                item2.setBlogId(invoke.getBlogId());
                item2.setSrpId(invoke.getSrpId());
                item2.setId(invoke.getId()+"");

                item2.setCategory(invoke.getCategory());
                item2.setChannel(invoke.getChan());
                item2.setDescription(invoke.getDesc());
                item2.setTitle(invoke.getTitle());
                if (invoke.getFlag(BaseInvoke.FLAG_SKIP_COMMENT)){
                    item2.setSkip(DetailItem.SKIP_TO_COMMENT);
                }
                if(invoke.getIsInvokeTemplate()==BaseInvoke.INVOKETEMPLATE)
                {
                    IntentUtil.skipSRPDetailPage(activity,item2,REQUEST_DETAIL_CODE);
                }else
                {
                    IntentUtil.skipOldSRPDetailPage(activity,item2,REQUEST_DETAIL_CODE);
                }

                break;
            /** 圈贴列表页 */
            case BaseInvoke.INVOKE_TYPE_INTEREST_INDEX:
                IntentUtil.gotoCircleIndex(activity, invoke.getSrpId(), invoke.getKeyword(), invoke.getInterestName(), invoke.getIconUrl(),invoke.getMd5());
                break;
            /** 图集页 */
            case BaseInvoke.INVOKE_TYPE_PHOTOS:
                GalleryNewsHomeBean bean = new GalleryNewsHomeBean();
                bean.setSrpId(invoke.getSrpId());
                bean.setUrl(invoke.getUrl());
//                bean.setSource(invoke.get);//不是必须，给空
                bean.setKeyword(invoke.getKeyword());
//                bean.setPubTime(invoke.);//不是必须，给空
                List<String> image = invoke.getImage();//跳转图集页面需要将列表中的图片传过去做分享
                if (image == null||image.size()==0){
                    String bigimg = invoke.getBigImgUrl();
                    image = new ArrayList<String>();
                    image.add(bigimg);
                }
                bean.setImage(image);//分享用
                bean.setCategory(invoke.getCategory());
                bean.setTitle(invoke.getTitle());
                bean.setDescription(invoke.getDesc());
                bean.setChannel(invoke.getChan());
                IntentUtil.goToGalleryNews(activity,bean);
                break;
            /** gif详情页 */
            case BaseInvoke.INVOKE_TYPE_GIF:
                DetailItem item3 = new DetailItem();
                item3.setKeyword(invoke.getKeyword());
                item3.setSrpId(invoke.getSrpId());
                item3.setUrl(invoke.getUrl());

                item3.setChannel(invoke.getChan());
                item3.setDescription(invoke.getDesc());
                item3.setTitle(invoke.getTitle());
                item3.setId(invoke.getId()+"");
                if (invoke.getFlag(BaseInvoke.FLAG_SKIP_COMMENT)){
                    item3.setSkip(DetailItem.SKIP_TO_COMMENT);
                }
                IntentUtil.skipOldSRPDetailPage(activity,item3,REQUEST_DETAIL_CODE);
                break;
            /** 段子详情页 */
            case BaseInvoke.INVOKE_TYPE_JOKE:
                DetailItem item4 = new DetailItem();
                item4.setKeyword(invoke.getKeyword());
                item4.setSrpId(invoke.getSrpId());
                item4.setUrl(invoke.getUrl());

                item4.setChannel(invoke.getChan());
                item4.setDescription(invoke.getDesc());
                item4.setTitle(invoke.getTitle());
                item4.setId(invoke.getId()+"");
                if (invoke.getFlag(BaseInvoke.FLAG_SKIP_COMMENT)){
                    item4.setSkip(DetailItem.SKIP_TO_COMMENT);
                }
                IntentUtil.skipOldSRPDetailPage(activity,item4,REQUEST_DETAIL_CODE);
                break;
            /** 专题页 */
            case BaseInvoke.INVOKE_TYPE_SPECIA:
                IntentUtil.gotoSpecilTopic(activity, invoke.getKeyword(), invoke.getUrl(), invoke.getChan(),invoke.getSrpId(),invoke.getDesc(),invoke.getIconUrl());
                break;
            /** 要闻页 */
            case BaseInvoke.INVOKE_TYPE_FOCUSNEWS:
                IntentUtil.gotoSouYueYaoWen(activity,invoke.getChannelId());
                break;
            /** 系统浏览器 */
            case BaseInvoke.INVOKE_TYPE_BROWSER :
                IntentUtil.gotoActionView(activity, invoke.getUrl());
                break;
            /** app浏览器 */
            case BaseInvoke.INVOKE_TYPE_BROWSER_APP :
                IntentUtil.gotoWebSrcView(activity,invoke.getUrl());
                break;
            /** app浏览器没有底部 */
            case BaseInvoke.INVOKE_TYPE_BROWSER_APP_NOBOTTOM :
                IntentUtil.gotoSrpWebView(activity, invoke);
                break;
            /** 只有 keyword 的 新闻详情页*/
            case BaseInvoke.INVOKE_TYPE_NEW_DETAIL: // type = 92
                SearchResultItem resultItem = new SearchResultItem();
                resultItem.keyword_$eq(invoke.getKeyword());
                resultItem.pushId_$eq(Long.parseLong(invoke.getSrpId()));   //推送通知栏传来的pushId
                IntentUtil.startskipDetailPushPage(activity, resultItem);
                break;
            /** 视频详情页 5.2 */
            case BaseInvoke.INVOKE_TYPE_VIDEO:
                IntentUtil.gotoVideoDetail(activity,invoke,0);
                break;
            /** 视频 5.2 web 页面*/
            case BaseInvoke.INVOKE_TYPE_VIDEO_WEB:
                IntentUtil.gotoWebSrcView(activity,invoke.getBigImgUrl());
                break;
            /**推送来的图集页面*/
            case BaseInvoke.INVODE_TYPE_PUSH_PHOTOS:
                IntentUtil.goPushGalleryNews(activity,invoke.getSrpId());
                break;
//            case BaseInvoke.INVOKE_TYPE_SKIP_HOMEBALL:
//                if (activity instanceof MainActivity){
//                    FragmentManager manager = ((MainActivity)activity).getSupportFragmentManager();
//                    SouyueTabFragment fragment = (SouyueTabFragment)manager.findFragmentByTag("SOUYUE");
//                    FragmentTransaction transaction = manager.beginTransaction();
//                    transaction.show(fragment);
//                    transaction.commitAllowingStateLoss();
//                }
        }
    }

    /**
     * 跳转球球
     */
//    public static void skipHomeBall(Activity ac,){
//
//    }

    public static String getErrorCode(int type,int code){
        StringBuilder builder = new StringBuilder();
        int re,index;
        for (int i=0;i<8;i++){
            index = 7-i;
            re = (1<<index)&code;
            if (re!=0){
                builder.append("1");
            }else{
                builder.append("0");
            }
        }

        return builder.toString();
    }
    /**
     * 检测数据错误
     * @param invoke
     * @return
     */
    public static int checkSkip(BaseInvoke invoke){
        int errorcode = 0;
        switch (invoke.getType()) {
            /** srp新闻详情页 */
            case BaseInvoke.INVOKE_TYPE_SRP:
                if (StringUtils.isEmpty(invoke.getKeyword())){
                    errorcode|=ERROR_EMPTY_KEYWORD;
                }
                if(StringUtils.isEmpty(invoke.getSrpId())){
                    errorcode|=ERROR_EMPTY_SRPID;
                }
                if(StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                if (StringUtils.isEmpty(invoke.getCategory())){
                    errorcode|=ERROR_EMPTY_CATEGORY;
                }
                if (StringUtils.isEmpty(invoke.getTitle())&&StringUtils.isEmpty(invoke.getDesc())){
                    errorcode|=ERROR_EMPTY_TITLE_DESC;
                }
                break;
            /** srp新闻列表页 */
            case BaseInvoke.INVOKE_TYPE_SRP_INDEX:
                if (StringUtils.isEmpty(invoke.getKeyword())){
                    errorcode|=ERROR_EMPTY_KEYWORD;
                }
                if(StringUtils.isEmpty(invoke.getSrpId())){
                    errorcode|=ERROR_EMPTY_SRPID;
                }
                break;
            /** 圈贴详情页 */
            case BaseInvoke.INVOKE_TYPE_INTEREST:
                if (invoke.getInterestId()<=0){
                    errorcode|=ERROR_EMPTY_INTERESTID;
                }
                if(invoke.getBlogId()<=0){
                    errorcode|=ERROR_EMPTY_BLOGID;
                }
                if(StringUtils.isEmpty(invoke.getSrpId())){
                    errorcode|=ERROR_EMPTY_SRPID;
                }
                if (StringUtils.isEmpty(invoke.getCategory())){
                    errorcode|=ERROR_EMPTY_CATEGORY;
                }
                if (StringUtils.isEmpty(invoke.getTitle())&&StringUtils.isEmpty(invoke.getDesc())){
                    errorcode|=ERROR_EMPTY_TITLE_DESC;
                }

                break;
            /** 圈贴列表页 */
            case BaseInvoke.INVOKE_TYPE_INTEREST_INDEX:
                if (StringUtils.isEmpty(invoke.getKeyword())){
                    errorcode|=ERROR_EMPTY_KEYWORD;
                }
                if(StringUtils.isEmpty(invoke.getSrpId())){
                    errorcode|=ERROR_EMPTY_SRPID;
                }
                break;
            /** 图集页 */
            case BaseInvoke.INVOKE_TYPE_PHOTOS:
                if (StringUtils.isEmpty(invoke.getKeyword())){
                    errorcode|=ERROR_EMPTY_KEYWORD;
                }
                if(StringUtils.isEmpty(invoke.getSrpId())){
                    errorcode|=ERROR_EMPTY_SRPID;
                }
                if(StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                if (StringUtils.isEmpty(invoke.getCategory())){
                    errorcode|=ERROR_EMPTY_CATEGORY;
                }
                if (StringUtils.isEmpty(invoke.getTitle())&&StringUtils.isEmpty(invoke.getDesc())){
                    errorcode|=ERROR_EMPTY_TITLE_DESC;
                }
                if ((invoke.getImage()==null||invoke.getImage().size()==0)&&StringUtils.isEmpty(invoke.getBigImgUrl())){
                    errorcode|=ERROR_EMPTY_SHARE_IMAGE;
                }
                break;
            /** gif详情页 */
            case BaseInvoke.INVOKE_TYPE_GIF:
                if (StringUtils.isEmpty(invoke.getKeyword())){
                    errorcode|=ERROR_EMPTY_KEYWORD;
                }
                if(StringUtils.isEmpty(invoke.getSrpId())){
                    errorcode|=ERROR_EMPTY_SRPID;
                }
                if(StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                if (StringUtils.isEmpty(invoke.getCategory())){
                    errorcode|=ERROR_EMPTY_CATEGORY;
                }
                if (StringUtils.isEmpty(invoke.getTitle())&&StringUtils.isEmpty(invoke.getDesc())){
                    errorcode|=ERROR_EMPTY_TITLE_DESC;
                }
                break;
            /** 段子详情页 */
            case BaseInvoke.INVOKE_TYPE_JOKE:
                if (StringUtils.isEmpty(invoke.getKeyword())){
                    errorcode|=ERROR_EMPTY_KEYWORD;
                }
                if(StringUtils.isEmpty(invoke.getSrpId())){
                    errorcode|=ERROR_EMPTY_SRPID;
                }
                if(StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                if (StringUtils.isEmpty(invoke.getCategory())){
                    errorcode|=ERROR_EMPTY_CATEGORY;
                }
                if (StringUtils.isEmpty(invoke.getTitle())&&StringUtils.isEmpty(invoke.getDesc())){
                    errorcode|=ERROR_EMPTY_TITLE_DESC;
                }
                break;
            /** 专题页 */
            case BaseInvoke.INVOKE_TYPE_SPECIA:
                if (StringUtils.isEmpty(invoke.getKeyword())){
                    errorcode|=ERROR_EMPTY_KEYWORD;
                }
                if(StringUtils.isEmpty(invoke.getSrpId())){
                    errorcode|=ERROR_EMPTY_SRPID;
                }
                if(StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                if (StringUtils.isEmpty(invoke.getTitle())&&StringUtils.isEmpty(invoke.getDesc())){
                    errorcode|=ERROR_EMPTY_TITLE_DESC;
                }
//                IntentUtil.gotoSpecilTopic(activity, invoke.getTitle(), invoke.getUrl(), invoke.getChan(), invoke.getSrpId(), invoke.getDesc(), invoke.getIconUrl());
                break;
            /** 要闻页 */
            case BaseInvoke.INVOKE_TYPE_FOCUSNEWS:
//                可以不用检查
                break;
            /** 系统浏览器 */
            case BaseInvoke.INVOKE_TYPE_BROWSER:
                if(StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                break;
            /** app浏览器 */
            case BaseInvoke.INVOKE_TYPE_BROWSER_APP:
                if(StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                break;
            /** app浏览器没有底部 */
            case BaseInvoke.INVOKE_TYPE_BROWSER_APP_NOBOTTOM:
                if(StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                break;
            /** 只有keyword 的新闻详情页 */
            case BaseInvoke.INVOKE_TYPE_NEW_DETAIL:
                if(StringUtils.isEmpty(invoke.getKeyword())){
                    errorcode|=ERROR_EMPTY_KEYWORD;
                }
                break;
            /** 视频详情页 5.2 */
            case BaseInvoke.INVOKE_TYPE_VIDEO:
                if (StringUtils.isEmpty(invoke.getKeyword())){
                    errorcode|=ERROR_EMPTY_KEYWORD;
                }
                if(StringUtils.isEmpty(invoke.getSrpId())){
                    errorcode|=ERROR_EMPTY_SRPID;
                }
                if(StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                if (StringUtils.isEmpty(invoke.getCategory())){
                    errorcode|=ERROR_EMPTY_CATEGORY;
                }
                if (StringUtils.isEmpty(invoke.getTitle())&&StringUtils.isEmpty(invoke.getDesc())){
                    errorcode|=ERROR_EMPTY_TITLE_DESC;
                }
                if(StringUtils.isEmpty(invoke.getBigImgUrl())){
                    errorcode|=ERROR_EMPTY_SHARE_IMAGE;
                }
                BaseListData date = invoke.getData();
                if(date instanceof SigleBigImgBean)
                {
                    if(StringUtils.isEmpty(((SigleBigImgBean) date).getPhoneImageUrl()))
                    {
                        errorcode|=ERROR_EMPTY_SHARE_IMAGE;
                    }
                }
                if (StringUtils.isEmpty(invoke.getUrl())){
                    errorcode|=ERROR_EMPTY_URL;
                }
                break;
            case BaseInvoke.INVOKE_TYPE_VIDEO_WEB:
                if(StringUtils.isEmpty(invoke.getBigImgUrl())){
                    errorcode|=ERROR_EMPTY_SHARE_IMAGE;
                }
                break;
            /**
             * 推送过来的图集
             */
            case BaseInvoke.INVODE_TYPE_PUSH_PHOTOS:
                if(TextUtils.isEmpty(invoke.getSrpId())){
                    errorcode |= ERROR_EMPTY_SRPID;
                }
                break;
//            case BaseInvoke.INVOKE_TYPE_SKIP_HOMEBALL:
//                if (activity instanceof MainActivity){
//                    FragmentManager manager = ((MainActivity)activity).getSupportFragmentManager();
//                    SouyueTabFragment fragment = (SouyueTabFragment)manager.findFragmentByTag("SOUYUE");
//                    FragmentTransaction transaction = manager.beginTransaction();
//                    transaction.show(fragment);
//                    transaction.commitAllowingStateLoss();
//                }
        }
        return errorcode;
    }

    public static void skip(Activity activity,SearchResultItem item){

        String category = item.category();
        if (ConstantsUtils.VJ_NEW_SEARCH.equals(category)) {
            boolean isHeadTop = item.isHeadlineTop();

            if (!isHeadTop) {
//                        IntentUtil.skipDetailPage(activity, item, requestDetailCode);//300-400毫秒
                HomePagerSkipUtils.homeSkipToDetail(HomePagerSkipUtils.SKIP_DETAIL,activity,item,REQUEST_DETAIL_CODE);
            } else {
//                        IntentUtil.gotoSouYueYaoWen(mContext);
                HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_MASTERNEWS,activity);
            }
        } else if (ConstantsUtils.FR_INTEREST_BAR.equals(category)) {
//                    IntentUtil.skipHomeDetailPage(activity, item, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
            HomePagerSkipUtils.homeSkipToDetail(HomePagerSkipUtils.SKIP_DETAIL,activity,item, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
        } else if(ConstantsUtils.FR_INFO_PICTURES.equals(category)){
            IntentUtil.getToGalleryNews(activity,item);
        } else if (ConstantsUtils.FR_INFO_SPECIAL.equals(category)) {
//                    IntentUtil.gotoSouYueSRP(activity, item.keyword(), item.srpId(), "");
            HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_SPECIL,activity,item.keyword(),item.url(),item.getChannelId(),item.srpid,item.descreption,item.pic);
            //服务器未返回类型，所以通过srpid判断类型进行跳转到专题
        }
        else if (!TextUtils.isEmpty(item.srpid)) { //专题首页
//                    IntentUtil.gotoSpecilTopic( activity, item.keyword(), item.url(), item.getChannelId());
            HomePagerSkipUtils.homeSkipTo(HomePagerSkipUtils.SKIP_SPECIL,activity,item.keyword(),item.url(),item.getChannelId(),item.srpid,item.descreption,item.pic);
        }
        else {
//                    IntentUtil.skipDetailPage(activity, item, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
            HomePagerSkipUtils.homeSkipToDetail(HomePagerSkipUtils.SKIP_DETAIL,activity,item,CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
        }
    }

    public static void homeSkipTo(int type, Context activity, String ... args){
        if (type == SKIP_DETAIL){
            throw new IllegalStateException("跳转详情页需要调用 homeSkipToDetail() 方法");
        }
        switch (type){
            case SKIP_SRP:
                //跳转srp页，需要
                // args[0] keyword ,
                // args[1] srpid,
                // args[2] image
                IntentUtil.gotoSouYueSRP(activity, args[0],args[1], args[2]);
                break;
            case SKIP_SPECIL:
                //跳转专题页，需要
                // args[0] keyword,
                // args[1] url,
                // args[2] channelName
                IntentUtil.gotoSpecilTopic(activity,args[0],args[1],args[2],args[3],args[4],args[5]);
                break;
            case SKIP_MASTERNEWS:
                //跳转要闻页
                IntentUtil.gotoSouYueYaoWen(activity,"");
                break;
            case SKIP_CIRCLEHOME:
                //跳转圈子首页
                // args[0] srpid,
                // args[1] keyword,
                // args[2] interestName,
                // args[3] interestLogo
                UIHelper.showCircleIndex((Activity)activity, args[0], args[1], args[2], args[3]);
                break;
            case SKIP_SEARCH:
                //跳转搜索页
                IntentUtil.openSearchActivity((Activity) activity);
                break;
            case SKIP_WEBSITE:
                //跳转网址页
                // args[0] url,
                // args[1] type
                IntentUtil.gotoWeb(activity, args[0], args[1]);
                break;
            case SKIP_SUBSCRIB:
                IntentUtil.toMySubscribe(activity, Integer.parseInt(args[0]));
                break;
            case SKIP_SUBSCRIB_SUBALL:
                IntentUtil.toMySubscribeRight(activity, Integer.parseInt(args[0]),true);
                break;
            case SKIP_SWITCHIMAGES:
                //跳转图集页
                IntentUtil.getToGalleryNews(activity,args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8]);
        }
    }

    public static void homeSkipToDetail(int type,Activity ac,SearchResultItem item ,int requestcode){
        switch (type){
            case SKIP_DETAIL:
                IntentUtil.skipDetailPage(ac, item, requestcode);//300-400毫秒
            break;
        }
    }

}
