package com.zhongsou.souyue.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SendNextActivity;
import com.zhongsou.souyue.activity.ShareWeiboActivity;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.ui.MySendingAlertDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.AccessTokenKeeper;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.VersionUtils;

import java.util.List;


/**
 * 发送前，进行判断
 * 1、判断用户是否登录，调用checkUser
 * 2、判断数据是否合法，调用checkData
 * 3、如果是编辑，要进行判断是否修改数据，调用checkModify
 *
 * @author wangchunyan@zhongsou.com
 */
@SuppressWarnings("deprecation")
public class SendUtils {
    private static final int TEXT_MAX = 10000;
    private static final int WB_TEXT_MAX = 140;
    private static Activity mAc;


    public static boolean sendOrNext(SelfCreateItem sci, Activity ac, boolean weibochecked) {
        sci.conpic_$eq("");
        sci.pubtime_$eq(System.currentTimeMillis() + "");
        if (isEmpty(sci.keyword())) {
            next(sci, ac, weibochecked);
        } else {
            send(sci, ac, weibochecked);
        }
        return true;
    }

    public static void next(SelfCreateItem sci, Activity ac, boolean weibochecked) {
        Intent i = new Intent(ac, SendNextActivity.class);
        i.putExtra("selfCreateItem", sci);

        i.putExtra("weibochecked", weibochecked);
        ac.startActivity(i);
        ac.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public static void send(SelfCreateItem sci, Activity ac, boolean weibochecked) {
        // 全部通过，发送...
        ConnectivityManager mConnectivityManager = (ConnectivityManager) ac
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        mAc = ac;
        if (mNetworkInfo != null) {
//            new Http(ac).uploadImages(ac, , sci);
		    SelfCreateTask t = SelfCreateTask.getInstance();
		    t.setUid(SYUserManager.getInstance().getUserId());
		    t.sendRequest(sci);

//            Intent i = new Intent();
//            i.putExtra("ismodify", true);
//            i.setAction(ConstantsUtils.INTENTFILTER_ACTION_REFRESH_SELFCREATE_LV);
//            ac.sendBroadcast(i);
            if (weibochecked) {
                send2Weibo(ac, sci);
            }

//            else {
//                Intent intent = new Intent(ac, SelfCreateActivity.class);
//                ac.startActivity(intent);
//                ac.finish();
//
//            }
//            ac.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } else {
            showToast(R.string.self_msg_neterror);
        }
    }

    public static void sendFail(boolean _isSuccess) {
        if (mAc != null) {
            if (_isSuccess) {
                if (!(mAc instanceof SendNextActivity)) {
                    int oldVersion = SYSharedPreferences.getInstance().getInt(SYSharedPreferences.KEY_SENDALERTHIDE, 0);
                    if (VersionUtils.getVersionCode() > oldVersion) {
                        new MySendingAlertDialog(mAc).show();
                    } else {
                        mAc.finish();
                    }
                } else {
                    IntentUtil.startRead_isCircleActivityWithAnim(mAc, 1);
                    mAc.finish();
                    ((SendNextActivity) mAc).deleteSave();
                }
                mAc = null;
            }
        }
    }

    /**
     * 验证用户是否登录
     *
     * @param ac
     * @return 游客 false
     */
    public static boolean checkUser(Activity ac) {
        String utype = SYUserManager.getInstance().getUserType();
        if (null != utype && !utype.equals("1")) {
            // 未登录
            ac.showDialog(0);
            return false;
        }
        return true;
    }

    /**
     * 验证数据是否合法
     *
     * @param sci
     * @param title
     * @param content
     * @return
     */
    public static boolean checkData(SelfCreateItem sci, String title, String content) {
        if (sci.column_type() != ConstantsUtils.TYPE_WEIBO_SEARCH) {

//            if (isEmpty(title)) {
//                showToast(R.string.self_bolg_title_empty);
//                return false;
//            }  
            if (title.length() > 30) {
                showToast(R.string.self_bolg_title_count_long);
                return false;
            }
        }
        if (isEmpty(content)) {
            showToast(R.string.self_msg_empty);
            return false;
        } else {
            if (sci.column_type() != ConstantsUtils.TYPE_WEIBO_SEARCH) {
                if (content.length() > TEXT_MAX) {
                    showToast(R.string.self_bolg_content_count_long);
                    return false;
                }
            } else if (ShareWeiboActivity.getStrLen(content) > WB_TEXT_MAX) {
                showToast(R.string.self_weibo_content_count_long);
                return false;
            }
        }
        return true;
    }

    /**
     * 拼接keyword
     *
     * @param kws
     * @return
     */
    public static String preK(List<String> kws) {
        return pre(kws, false);
    }

    /**
     * 拼接srpid 如果null -1占位
     *
     * @param sid
     * @return
     */
    public static String preI(List<String> sid) {
        return pre(sid, true);
    }


    private static void showToast(int resId) {
        SouYueToast.makeText(MainApplication.getInstance(), MainApplication.getInstance().getResources().getString(resId), 0).show();
    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    private static String pre(List<String> list, boolean flg) {
        if (list == null) return null;
        StringBuffer sb = new StringBuffer();
        int max = list.size();
        for (int i = 0; i < max; i++) {
            sb.append(flg == true && isEmpty(list.get(i)) ? "-1" : list.get(i));
            if (max - 1 != i) sb.append(",");
        }
        return sb.toString();
    }

    private static void send2Weibo(final Activity ac, SelfCreateItem sci) {
//        StatusesAPI api = new StatusesAPI(AccessTokenKeeper.readAccessToken(ac));
//        RequestListener listener = new RequestListener() {
//
//            @Override
//            public void onComplete(String response) {
//                ac.runOnUiThread(new Runnable() {
//                    public void run() {
//                        SouYueToast.makeText(ac, ac.getString(R.string.self_send_weibo_success), 0).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onComplete4binary(ByteArrayOutputStream responseOS) {
//
//            }
//
//            @Override
//            public void onIOException(IOException e) {
//                ac.runOnUiThread(new Runnable() {
//                    public void run() {
//                        SouYueToast.makeText(ac, ac.getString(R.string.self_sending_weibo_error), 0).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onError(WeiboException e) {
//                ac.runOnUiThread(new Runnable() {
//                    public void run() {
//                        SouYueToast.makeText(ac, ac.getString(R.string.self_sending_weibo_error), 0).show();
//                    }
//                });
//            }
//
//        };
        StatusesAPI api = new StatusesAPI(ac, ShareApi.SINA_CONSUMER_KEY,AccessTokenKeeper.readAccessToken(ac));
        RequestListener listener = new RequestListener() {


            @Override
            public void onComplete(String s) {
                ac.runOnUiThread(new Runnable() {
                    public void run() {
                        SouYueToast.makeText(ac, ac.getString(R.string.self_send_weibo_success), 0).show();
                    }
                });
            }

            @Override
            public void onWeiboException(WeiboException e) {
                ac.runOnUiThread(new Runnable() {
                    public void run() {
                        SouYueToast.makeText(ac, ac.getString(R.string.self_sending_weibo_error), 0).show();
                    }
                });
            }
        };
        if (TextUtils.isEmpty(sci.conpic())) {
            api.update(sci.content(), null, null, listener);
        } else {
            // 发微博带图片
            if (sci.conpic().startsWith("http")) {
                // 上传网络图片到微博，需微博开通此接口权限
                api.uploadUrlText(sci.content(), sci.conpic().trim(), "", null, null, listener);
            } else
            {
                // 上传本地图片到微博
//                api.upload(sci.content(), sci.conpic().trim(), null, null, listener);
                api.upload(sci.content(), BitmapFactory.decodeFile(sci.conpic().trim()), null, null, listener);
            }

        }

    }

}
