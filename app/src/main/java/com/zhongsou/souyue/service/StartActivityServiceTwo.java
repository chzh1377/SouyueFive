package com.zhongsou.souyue.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.SplashActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.module.PushInfo;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.ShortCutInfo;
import com.zhongsou.souyue.module.SplashAd;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.ac.SrpWebViewActivity;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ZhongSouActivityMgr;

import java.io.Serializable;

public class StartActivityServiceTwo {

    public static void onStart(Serializable serializable, Context context) {
        if (serializable == null) {
            return;
        }
        /**
         * 消息推送推来的参数(md5,keyword,pushId)
         */
        if (serializable instanceof PushInfo) {
            PushInfo info = (PushInfo) serializable;
            System.out.println("StartActivityServiceTwo info.getJumpType() " + info.getJumpType());
            Intent intent = new Intent();

            if (SplashActivity.JUMP_TYPE_GALLERYNEWS.equals(info.getJumpType())) {
                IntentUtil.StartGalleryNewsActivity(context, info.getGalleryNews());
                return;
            }

            if ("zero".equals(info.getJumpType())) {
                Log.i("Tuita", "goto start activity WebSrcActivity " + info.getJumpType());
            } else if ("im".equals(info.getJumpType())) {
//            ImUIHelpr.startIm(context);

                ZhongSouActivityMgr.getInstance().goHome();
                IntentUtil.openMainActivity(context, new int[]{1});


//            } else if ("slot".equals(info.getJumpType())) {//老虎机没了
//                Log.i("slot", "start TigerGameActivity");
//                intent.putExtra(TigerGameActivity.RECORD_ID_SLOT, info.pushId());
//                intent.setClass(context, TigerGameActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                context.startActivity(intent);
            } else if ("interest".equals(info.getJumpType())) {
                if (info.getInterestBlog().getType() == 1) {
                    IntentUtil.gotoSecretCricleCard(context, info.getInterestBlog().getInterest_id());
                } else {
//                    UIHelper.showPostsDetail(context, info.getInterestBlog().getBlog_id(), info.getInterestBlog().getInterest_id());
                    SearchResultItem item1 = new SearchResultItem();
                    item1.setBlog_id(info.getInterestBlog().getBlog_id());
                    item1.setInterest_id(info.getInterestBlog().getInterest_id());
                    IntentUtil.startskipDetailPushPage(context, item1);
                }
            } else if (SplashActivity.JUMP_TYPE_INTERESTCARD.equals(info.getJumpType())) {
                IntentUtil.gotoSecretCricleCard(context, info.getInterestBlog().getInterest_id());
            } else if (SplashActivity.JUMP_TYPE_LINGPAI.equals(info.getJumpType())) {
                //启动中搜零拍
                Intent appIntent = new Intent(context, WebSrcViewActivity.class);
                appIntent.putExtra(WebSrcViewActivity.PAGE_URL, info.url());
                appIntent.putExtra(WebSrcViewActivity.PAGE_TYPE, "interactWeb");
                context.startActivity(appIntent);
                ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else if ("postsdetail".equals(info.getJumpType())) {
//                intent.setClass(context, PostsActivity.class);
//                intent.putExtra("blog_id", info.getInterestBlog().getBlog_id());
//                intent.putExtra("interest_id", info.getInterestBlog().getInterest_id());
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                context.startActivity(intent);


                SearchResultItem item1 = new SearchResultItem();
                item1.setBlog_id(info.getInterestBlog().getBlog_id());
                item1.setInterest_id(info.getInterestBlog().getInterest_id());
                IntentUtil.startskipDetailPage(context, item1, Intent.FLAG_ACTIVITY_SINGLE_TOP);

            } else if ("atlas".equals(info.getJumpType())) {      //跳转图集
                IntentUtil.StartGalleryNewsActivity(context, info.getGalleryNews());
            } else if (TextUtils.isEmpty(info.pushId()) && TextUtils.isEmpty(info.url())) {
                // 启动SRP页面
                intent.setClass(context, SRPActivity.class);
                intent.putExtra("keyword", info.keyword());
                intent.putExtra("srpId", info.srpId());
                context.startActivity(intent);
            } else {
                // 中间压srp页,跳详情页
                SearchResultItem resultItem = new SearchResultItem();
                Bundle bundleRes = new Bundle();
                resultItem.keyword_$eq(info.keyword());
                resultItem.srpId_$eq(info.srpId());
                if (info.pushId() != null) try {
                    resultItem.pushId_$eq(Long.parseLong(info.pushId()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (info.getStatisticsJumpPosition() != null && !info.getStatisticsJumpPosition().equals(""))
                    resultItem.setStatisticsJumpPosition(info.getStatisticsJumpPosition());

                resultItem.url_$eq(info.url());
                if (info.getPushFrom() != null && !info.getPushFrom().equals("")) {
                    resultItem.setPushFrom(info.getPushFrom());
                }
                if (info.getClickFrom() != null && !info.getClickFrom().equals("")) {
                    resultItem.setClickFrom(info.getClickFrom());
                }
                if (info.getMid() != null && !info.getMid().equals("")) {
                    resultItem.setMsgId(info.getMid());
                }

                bundleRes.putSerializable("searchResultItem", (Serializable) resultItem);
                intent.putExtras(bundleRes);

//                String goStr = isGoNewSrpDetail(info.url());
//                String ifextract = info.getIfextract();

                int value = -1;
                try {
                    value = Integer.parseInt(info.g());
                } catch (NumberFormatException nfe) {
                }
                //新webview微信返回跳转
               /* if (StringUtils.isNotEmpty(goStr) && IMChatMsgViewAdapter.IFEXTRACT_NO.equals(goStr) || 
                        StringUtils.isNotEmpty(ifextract) && IMChatMsgViewAdapter.IFEXTRACT_NO.equals(ifextract)) {
                    intent.setClass(context, SrpWebviewDetailActivity.class);
                    intent.putExtra("keyword", info.keyword());
                    intent.putExtra("srpId", info.srpId());
                    intent.putExtra("url", info.url());
                    intent.putExtra("pushId", resultItem.pushId());
                } else {*/
                if (isEmptyWebView(info.url())) {
                    SearchResultItem searchResultItem = new SearchResultItem();
                    if (!(info.keyword().equals("emptyWeb") && info.srpId().equals("search"))) {//打开无头无尾webview
                        searchResultItem.keyword_$eq(info.keyword());
                        searchResultItem.srpId_$eq(info.srpId());
                    }
                    intent.setClass(context, SrpWebViewActivity.class);
                    intent.putExtra("source_url", info.url());
                    intent.putExtra("searchResultItem", searchResultItem);
                    context.startActivity(intent);
                } else {
                    switch (value) {
                        case 0:
                            intent.setClass(context, WebSrcViewActivity.class);
                            context.startActivity(intent);
                            break;
                        default:
//                            intent.setClass(context, ReadabilityActivity.class);
//                            intent.putExtra("keyword", info.keyword());
//                            intent.putExtra("gotoSRP", true); //点击完推送是玩会SRP页面
//                            intent.putExtra("from", "push");
                            IntentUtil.skipDetailPushPage((Activity) context, resultItem, 0);
                            break;
                    }
//                }
                }

            }
        }

        if (serializable instanceof ShortCutInfo) {
            ShortCutInfo shortCutInfo = (ShortCutInfo) serializable;
            Intent intent = new Intent();
            if (ShortCutInfo.GO_TO_SRP.equals(shortCutInfo.getGoTo())) {
                intent.setClass(context, SRPActivity.class);
                intent.putExtra("keyword", shortCutInfo.getKeyword());
                intent.putExtra("srpId", shortCutInfo.getSrpId());
                context.startActivity(intent);
            }

            if (ShortCutInfo.GO_TO_INTEREST.equals(shortCutInfo.getGoTo())) {
                intent.setClass(context, CircleIndexActivity.class);
                intent.putExtra("srp_id", shortCutInfo.getSrpId());
                intent.putExtra("keyword", shortCutInfo.getKeyword());
                intent.putExtra("interest_name", shortCutInfo.getInterest_name());
                intent.putExtra("interest_logo", shortCutInfo.getInterest_logo());
                context.startActivity(intent);
            }


            if (ShortCutInfo.GO_TO_M_SEARCH.equals(shortCutInfo.getGoTo())) {
                Intent i = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("searchResultItem", "");
                bundle.putString("source_url", shortCutInfo.getUrl());
                i.setClass(context, SrpWebViewActivity.class);
                i.putExtras(bundle);
                context.startActivity(i);
                ((Activity) context).overridePendingTransition(R.anim.left_in,
                        R.anim.left_out);
            }
        }

        if (serializable instanceof SplashAd) {
            SplashAd splashAd = (SplashAd) serializable;
            SearchResultItem item = new SearchResultItem();
            item.url_$eq(splashAd.getJumpUrl());
            Intent intent = new Intent();
            intent.setClass(context, WebSrcViewActivity.class);
            intent.putExtra("source_url", splashAd.getJumpUrl());
            intent.putExtra("searchResultItem", item);
            context.startActivity(intent);
        }

    }

    //没有用  先注掉 //by  zhangwb
//    public static String isGoNewSrpDetail(String shareUrl) {
//        Pattern pm = Pattern.compile("\\?ifextract=(\\d{1})");
//        Matcher m = pm.matcher(shareUrl);
//        if (m.find()) {
//            if (!StringUtils.isEmpty(m.group(1))) {
//                if (IMChatMsgViewAdapter.IFEXTRACT_YES.equals(m.group(1))) {
//                    return IMChatMsgViewAdapter.IFEXTRACT_YES;
//                } else {
//                    return IMChatMsgViewAdapter.IFEXTRACT_NO;
//                }
//            }
//        }
//        return IMChatMsgViewAdapter.IFEXTRACT_NO_MATCH;
//    }
    private static boolean isEmptyWebView(String url) {
        if (StringUtils.isNotEmpty(url) && url.contains("emptyWeb")) {
            return true;
        } else {
            return false;
        }
    }
}
