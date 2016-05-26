package com.zhongsou.souyue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhou on 2016/2/29 0029.
 */
public class NotificationMsgJumpReceiver extends BroadcastReceiver{
    public static final String JUMPACTION = "com.zhongsou.souyue.pushReceiver";//可以发送到这个广播接收者内的action

    public static final int INVOKE_TYPE_SRP = 10;//srp新闻详情页
    public static final int INVOKE_TYPE_SRP_INDEX = 11;//srp新闻列表页
    public static final int INVOKE_TYPE_INTEREST = 20;//圈贴详情页
    public static final int INVOKE_TYPE_INTEREST_INDEX = 21;//圈贴列表页
    public static final int INVOKE_TYPE_PHOTOS = 30;//图集页
    public static final int INVOKE_TYPE_GIF = 40;//gif详情页
    public static final int INVOKE_TYPE_JOKE = 50;//段子详情页
    public static final int INVOKE_TYPE_SPECIA = 60;//专题页
    public static final int INVOKE_TYPE_FOCUSNEWS = 70;//要闻页
    public static final int INVOKE_TYPE_BROWSER = 100;//系统浏览器
    public static final int INVOKE_TYPE_BROWSER_APP = 110;//app浏览器
    public static final int INVOKE_TYPE_BROWSER_APP_NOBOTTOM = 112;//app浏览器没有底部
    public static final int INVOKE_TYPE_DETAIL = 92;//新闻详情页
    public static final int INVOKDE_TYPE_PUSH_PHOTOS = BaseInvoke.INVODE_TYPE_PUSH_PHOTOS; // 图集推送过来的
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("xxxx--" , intent.getStringExtra("data"));
        try {
            JSONObject obj=new JSONObject(intent.getStringExtra("data"));
            int type = obj.getInt("type");
            switch (type){
                case INVOKE_TYPE_SRP:
                    BaseInvoke srpInvoke = new BaseInvoke();
                    srpInvoke.setType(type);
                    srpInvoke.setSrpId(obj.getString("srpId"));
                    srpInvoke.setKeyword(obj.getString("keyword"));
                    srpInvoke.setUrl(obj.getString("url"));
                    srpInvoke.setCategory(obj.getString("category"));
                    srpInvoke.setTitle(obj.getString("title"));
                    srpInvoke.setDesc("desc");
                    HomePagerSkipUtils.skip(context,srpInvoke);
                    break;
                case INVOKE_TYPE_SRP_INDEX:
                    BaseInvoke srpIndexInvoke = new BaseInvoke();
                    srpIndexInvoke.setType(type);
                    srpIndexInvoke.setSrpId(obj.getString("srpId"));
                    srpIndexInvoke.setKeyword(obj.getString("keyword"));
                    srpIndexInvoke.setIconUrl(obj.getString("iconUrl"));
                    srpIndexInvoke.setMd5(obj.getString("md5"));
                    HomePagerSkipUtils.skip(context,srpIndexInvoke);
                    break;
                case INVOKE_TYPE_INTEREST:
                    BaseInvoke interestInvoke = new BaseInvoke();
                    interestInvoke.setType(type);
                    interestInvoke.setInterestId(obj.getLong("interestId"));
                    interestInvoke.setBlogId(obj.getLong("blogId"));
                    interestInvoke.setSrpId(obj.getString("srpId"));
                    interestInvoke.setCategory(obj.getString("category"));
                    interestInvoke.setTitle(obj.getString("title"));
                    interestInvoke.setDesc("desc");
                    HomePagerSkipUtils.skip(context,interestInvoke);
                    break;
                case INVOKE_TYPE_INTEREST_INDEX:
                    BaseInvoke interestIndex = new BaseInvoke();
                    interestIndex.setType(type);
                    interestIndex.setSrpId(obj.getString("srpId"));
                    interestIndex.setKeyword(obj.getString("keyword"));
                    interestIndex.setIconUrl(obj.getString("iconUrl"));
                    interestIndex.setMd5(obj.getString("md5"));
                    interestIndex.setInterestName(obj.getString("interestName"));
                    HomePagerSkipUtils.skip(context,interestIndex);
                    break;
                case INVOKE_TYPE_PHOTOS:
                    BaseInvoke photoInvoke = new BaseInvoke();
                    photoInvoke.setType(type);
                    photoInvoke.setSrpId(obj.getString("srpId"));
                    photoInvoke.setKeyword(obj.getString("keyword"));
                    photoInvoke.setUrl(obj.getString("url"));
                    photoInvoke.setCategory(obj.getString("category"));
                    photoInvoke.setTitle(obj.getString("title"));
                    photoInvoke.setDesc(obj.getString("desc"));
                    Gson gson = new Gson();
                    ArrayList<String> arr = gson.fromJson(obj.getString("images"),new TypeToken<ArrayList<String>>(){}.getType());
                    photoInvoke.setImage(arr);
                    photoInvoke.setBigImgUrl(obj.getString("bigImgUrl"));
                    HomePagerSkipUtils.skip(context,photoInvoke);
                    break;
                case INVOKE_TYPE_GIF:
                    BaseInvoke gifInvoke = new BaseInvoke();
                    gifInvoke.setType(type);
                    gifInvoke.setSrpId(obj.getString("srpId"));
                    gifInvoke.setKeyword(obj.getString("keyword"));
                    gifInvoke.setUrl(obj.getString("url"));
                    gifInvoke.setTitle(obj.getString("title"));
                    gifInvoke.setDesc("desc");
                    HomePagerSkipUtils.skip(context,gifInvoke);
                    break;
                case INVOKE_TYPE_JOKE:
                    BaseInvoke jokeInvoke = new BaseInvoke();
                    jokeInvoke.setType(type);
                    jokeInvoke.setSrpId(obj.getString("srpId"));
                    jokeInvoke.setKeyword(obj.getString("keyword"));
                    jokeInvoke.setUrl(obj.getString("url"));
                    jokeInvoke.setTitle(obj.getString("title"));
                    jokeInvoke.setDesc("desc");
                    HomePagerSkipUtils.skip(context,jokeInvoke);
                    break;
                case INVOKE_TYPE_SPECIA:
                    BaseInvoke speciaInvoke = new BaseInvoke();
                    speciaInvoke.setType(type);
                    speciaInvoke.setSrpId(obj.getString("srpId"));
                    speciaInvoke.setKeyword(obj.getString("keyword"));
                    speciaInvoke.setUrl(obj.getString("url"));
                    speciaInvoke.setDesc("desc");
                    speciaInvoke.setIconUrl(obj.getString("iconUrl"));
                    HomePagerSkipUtils.skip(context,speciaInvoke);
                    break;
                case INVOKE_TYPE_FOCUSNEWS:
                    BaseInvoke focusnewsInvoke = new BaseInvoke();
                    focusnewsInvoke.setType(type);
                    focusnewsInvoke.setChannelId(obj.getString("channelId"));
                    HomePagerSkipUtils.skip(context,focusnewsInvoke);
                    break;
                case INVOKE_TYPE_BROWSER:
                    BaseInvoke browserInvoke = new BaseInvoke();
                    browserInvoke.setType(type);
                    browserInvoke.setUrl(obj.getString("url"));
                    HomePagerSkipUtils.skip(context,browserInvoke);
                    break;
                case INVOKE_TYPE_BROWSER_APP:
                    BaseInvoke browserAppInvoke = new BaseInvoke();
                    browserAppInvoke.setType(type);
                    browserAppInvoke.setUrl(obj.getString("url"));
                    HomePagerSkipUtils.skip(context,browserAppInvoke);
                    break;
                case INVOKE_TYPE_BROWSER_APP_NOBOTTOM:
                    BaseInvoke browserAppNoBottomInvoke = new BaseInvoke();
                    browserAppNoBottomInvoke.setType(type);
                    browserAppNoBottomInvoke.setUrl(obj.getString("url"));
                    HomePagerSkipUtils.skip(context,browserAppNoBottomInvoke);
                    break;
                case INVOKE_TYPE_DETAIL:    //处理type为92的通知
                    BaseInvoke newsInvoke = new BaseInvoke();
                    newsInvoke.setType(type);
                    newsInvoke.setKeyword(obj.getString("keyword"));
                    newsInvoke.setSrpId(obj.getString("pushId"));
                    HomePagerSkipUtils.skip(context,newsInvoke);
                    break;
                case INVOKDE_TYPE_PUSH_PHOTOS:
                    BaseInvoke pushPhotosInvoke = new BaseInvoke();
                    pushPhotosInvoke.setType(type);
                    pushPhotosInvoke.setSrpId(obj.getString("keyId"));
                    HomePagerSkipUtils.skip(context, pushPhotosInvoke);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
