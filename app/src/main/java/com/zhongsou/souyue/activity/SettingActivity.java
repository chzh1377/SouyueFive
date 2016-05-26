package com.zhongsou.souyue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.facebook.drawee.view.FrescoConfig;
import com.tuita.sdk.PushService;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.module.AccountInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.home.SysRecSpecialRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.service.download.DownloadService;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.CacheUtils;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.SettingTypeHelper;
import com.zhongsou.souyue.utils.ThreadPoolUtil;
import com.zhongsou.souyue.utils.ToastUtil;
import com.zhongsou.souyue.utils.UserInfoUtils;
import com.zhongsou.souyue.utils.ZhongSouActivityMgr;

import java.util.List;

public class SettingActivity extends RightSwipeActivity implements
        OnItemClickListener {
    private static final int CLEAR_CACHE_DIALOG = 0;
    private static final int PROGRESS_DIALOG = 1;
    private int fontSize;
    private User user;
    private boolean old_imgAble;
    private boolean isUmeng = false;
    // add by trade
    public final static String DIMENSIONAL_IMGURL = "dimensional_imgUrl";
    public final static String SWITCH_RECOMMEND = "switch_recommend";

    private ListView settingList;
    private SettingAdapter settingAdapter;

    private final static int SETTING_ACCOUNT_BOUND = 3;//账号绑定
    private final static int SETTING_MSG_LIST = 5;//消息推送列表
    private final static int SETTING_CLEAR = 8;//清除缓存
    private final static int SETTING_GRADE = 9;//评分
    private final static int SETTING_FRIEND = 10;//推荐给好友
    private final static int SETTING_VERSION = 11;//新版本检测
    private final static int SETTING_ABOUT = 12;//关于我们
    private final static int SETTING_LOGOUT = 13;//退出登录
    private final static int SETTING_FEEDBACK = 14;//退出登录
    private final static int SETTING_GUIDE = 15;//新手引导
    public final static String logoutAction = "ACTION_LOGOUT_TO_HOME";
    private boolean pushSwitchState;// 消息推送开关状态
    private boolean pushSoundState;//推送声音设置
    private boolean pushVibrateState;//推送振动设置
    private boolean pushSpecialState;//专题弹窗推荐
    public static final int SPECIAL_SWITCH = 2;
    private CMainHttp mMainHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        settingList = (ListView) findViewById(R.id.setting_list);
        settingAdapter = new SettingAdapter();
        settingList.setAdapter(settingAdapter);
        settingList.setOnItemClickListener(this);
        ((TextView) findViewById(R.id.activity_bar_title))
                .setText(getString(R.string.setting));
        user = SYUserManager.getInstance().getUser();
        mMainHttp = CMainHttp.getInstance();
//		// 初始化2G/3G网络加载图片开关
        old_imgAble = SYSharedPreferences.getInstance().getLoadWifi(this);
//		// 初始化字体大小
        fontSize = (int) SYSharedPreferences.getInstance().loadResFont(this);
//		推送消息设置
        pushSwitchState = sysp.getBoolean(SYSharedPreferences.KEY_PUSHSWITCH, ConfigApi.isSouyue() ? true : ConstantsUtils.PUSH_DEFAULT_OPEN);
        pushSoundState = sysp.getBoolean(SYSharedPreferences.KEY_PUSH_SOUND, true);
//		推送振动设置
        pushVibrateState = sysp.getBoolean(SYSharedPreferences.KEY_PUSH_VIBRATE, true);
        pushSpecialState = UserInfoUtils.getSpecialState(user, pushSpecialState);
        this.setCanRightSwipe(true);
//        if (ConfigApi.isSuperAppProject()) {
//            setRcommendFriendsShow();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 显示最新缓存空间大小
        updateCacheFileSize();
    }

    private void updateCacheFileSize() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (settingAdapter != null) {
                    settingAdapter.setCacheSize(CacheUtils.getSize());
                    settingAdapter.notifyDataSetChanged();
                }
            }
        }, 0);
    }

    public void showToast(int id) {
        SouYueToast
                .makeText(SettingActivity.this, id, SouYueToast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CLEAR_CACHE_DIALOG:
                AlertDialog clearCache = new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.clear_cache_prompt)
                        .setMessage(R.string.clear_cache_warning)
                        .setPositiveButton(R.string.clear_ok,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        showDialog(PROGRESS_DIALOG);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                CacheUtils.clear();
                                                CacheUtils.clearWebViewCache();
                                                CacheUtils.clearImageLoader();
                                                CacheUtils.clearGif();
                                                CMainHttp.getInstance().clearCache();//清除volley缓存
                                                // 清理Fresco 缓存
                                                FrescoConfig.clearDiskCache();
                                                // 清空已下载//
                                                settingAdapter.setCacheSize(getString(R.string.pref_clear_show));
                                                settingAdapter.notifyDataSetChanged();
                                                showToast(R.string.pref_clear_succ);
                                                dismissDialog(PROGRESS_DIALOG);
                                            }
                                        }, 500);
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).create();
                return clearCache;
            case PROGRESS_DIALOG:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(SettingActivity.this.getString(R.string.clearing));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            default:
                return super.onCreateDialog(id);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                sysp.putBoolean(SYSharedPreferences.KEY_UPDATE, true);
                break;
            case 10:
                finish();
                break;
            default:
                break;
        }
    }

//    private void setRcommendFriendsShow() {
//        try {
//            JiFenJson jsons = CheckIsOpen.getJiFenJsonObj(this);
//            List<Lefttree> lefttree = jsons.lefttree;
//            for (Lefttree lefttree2 : lefttree) {
//                if (lefttree2.category != null) {
//                    if (lefttree2.category.equals("qrcodeimgPath")) {
//                        sysp.putString(DIMENSIONAL_IMGURL, lefttree2.url);
//                    }
//                    if (lefttree2.category.equals("tofriend")) {
//                        sysp.putString(SWITCH_RECOMMEND, lefttree2.state);
//                    }
//                }
//            }
////			String mShow_recommond_friends = sysp.getString(SWITCH_RECOMMEND,
////					"0");
////			String mDimensional_imgUrl = sysp
////					.getString(DIMENSIONAL_IMGURL, "");
//            // 开关状态为开且地址不为空时显示
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//    }

    private class SettingAdapter extends BaseAdapter {

        private static final int VIEW_TYPE_TITLE = 0;
        private static final int VIEW_TYPE_GROUP = 1;
        private static final int VIEW_TYPES_COUNT = 2;
        private String cacheSize;
        private List<String> settingNames = null;
        private List<Integer> settingTypes = null;

        public SettingAdapter() {
            if (SouyueAPIManager.isLogin()) {
                settingNames = SettingTypeHelper.mSettingNames;
                settingTypes = SettingTypeHelper.mSettingTypes;
            } else {
                settingNames = SettingTypeHelper.getNamesWithoutLogout();
                settingTypes = SettingTypeHelper.getTypesWithoutLogout();
            }
        }

        @Override
        public int getCount() {
            return settingTypes.size();
        }

        public void setCacheSize(String cacheSize) {
            this.cacheSize = cacheSize;
        }

        public String getCacheSize() {
            return this.cacheSize;
        }

        @Override
        public Object getItem(int arg0) {
            return settingTypes.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public int getItemViewType(int position) {
            int type = (Integer) getItem(position);
            switch (type) {
                case 2:
                    return VIEW_TYPE_GROUP;
                default:
                    return VIEW_TYPE_TITLE;
            }
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPES_COUNT;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                if (getItemViewType(position) == VIEW_TYPE_TITLE) {
                    convertView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.setting_item, parent, false);
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    holder.setting_right_arrow = (ImageView) convertView.findViewById(R.id.setting_right_arrow);
                    holder.setting_loadImageSwitch = (ToggleButton) convertView.findViewById(R.id.setting_loadImageSwitch);
                    holder.tv_setting_text = (TextView) convertView.findViewById(R.id.tv_setting_text);
                    holder.tv_setting_textsize = (LinearLayout) convertView.findViewById(R.id.tv_setting_textsize);
                    holder.tv_setting_font_big = (TextView) convertView.findViewById(R.id.tv_setting_font_big);
                    holder.tv_setting_font_middle = (TextView) convertView.findViewById(R.id.tv_setting_font_middle);
                    holder.tv_setting_font_small = (TextView) convertView.findViewById(R.id.tv_setting_font_small);
                    holder.setting_logout = (TextView) convertView.findViewById(R.id.setting_logout);
                } else {
                    convertView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.discover_group_item, parent, false);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            setData(holder, position);
            initLisenter(holder, position);
            //判断是否是第十三个,也就是退出登录，改变退出登录的样式
            if (getItemViewType(position) == VIEW_TYPE_TITLE&&settingTypes.get(position) != 13) {
                convertView.setBackgroundResource(R.drawable.discover_listview_item_bg_selector);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }

        private void initLisenter(final ViewHolder holder, final int position) {

            if (holder.tv_setting_font_big != null
                    && holder.tv_setting_font_middle != null
                    && holder.tv_setting_font_small != null
                    &&holder.tv_setting_textsize!=null
                    && holder.tv_setting_textsize.getVisibility()==View.VISIBLE
            ) {
                holder.tv_setting_font_big.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View paramView) {
                        fontSize = bigFontState(holder);
                        SYSharedPreferences.getInstance().putFontString(SYSharedPreferences.getInstance().FONT_VALUE_BIG, SettingActivity.this);
                    }
                });
                holder.tv_setting_font_middle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View paramView) {
                        fontSize = middleFontState(holder);
                        SYSharedPreferences.getInstance().putFontString(SYSharedPreferences.getInstance().FONT_VALUE_MIDDLE, SettingActivity.this);
                    }
                });
                holder.tv_setting_font_small.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View paramView) {
                        fontSize = smallFontState(holder);
                        SYSharedPreferences.getInstance().putFontString(SYSharedPreferences.getInstance().FONT_VALUE_SMALL, SettingActivity.this);
                    }
                });
            }
            if (holder.setting_loadImageSwitch != null
                    &&holder.setting_loadImageSwitch.getVisibility()==View.VISIBLE) {
                holder.setting_loadImageSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (settingTypes.get(position)) {
                            case 0:
                                pushSoundState = !pushSoundState;
                                PushService.setEnableSound(SettingActivity.this, pushSoundState);
                                sysp.putBoolean(SYSharedPreferences.KEY_PUSH_SOUND, pushSoundState);
                                settingAdapter.notifyDataSetChanged();
                                setUserMessageNotify(SYSharedPreferences.KEY_PUSH_SOUND);
                                break;
                            case 1:
                                pushVibrateState = !pushVibrateState;
                                PushService.setEnableVibrate(SettingActivity.this, pushVibrateState);
                                sysp.putBoolean(SYSharedPreferences.KEY_PUSH_VIBRATE, pushVibrateState);
                                settingAdapter.notifyDataSetChanged();
                                setUserMessageNotify(SYSharedPreferences.KEY_PUSH_VIBRATE);
                                break;
                            case 3:
                                pushSpecialState = !pushSpecialState;
                                String state;
                                if (pushSpecialState) {
                                    state = "1";
                                } else {
                                    state = "0";
                                }
                                SysRecSpecialRequest sysRecRequest = new SysRecSpecialRequest(HttpCommon.RECOMMEND_SPECIAL_SWITCH, SettingActivity.this);
                                sysRecRequest.setParams(state);
                                mMainHttp.doRequest(sysRecRequest);
                                break;
                            case 4:
                                pushSwitchState = !pushSwitchState;
                                if (pushSwitchState) {
                                    PushService.startService(SettingActivity.this);
                                    PushService.setImUserIdentity(SettingActivity.this, true);
                                } else {
                                    PushService.stopService(SettingActivity.this);
                                    PushService.setImUserIdentity(SettingActivity.this, false);
                                }
                                sysp.putBoolean(SYSharedPreferences.KEY_PUSHSWITCH, pushSwitchState);
                                settingAdapter.notifyDataSetChanged();
                                setUserMessageNotify(SYSharedPreferences.KEY_PUSHSWITCH);
                                break;
                            case 6:
                                setResult(RESULT_CANCELED);
                                old_imgAble = !old_imgAble;
                                SYSharedPreferences.getInstance().putLoadWifi(SettingActivity.this, old_imgAble);
                                ((MainApplication) getApplication()).initImageLoader();
                                settingAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                });
            }

        }

        private class ViewHolder {
            TextView tv_title;
            ToggleButton setting_loadImageSwitch;
            TextView tv_setting_text;
            ImageView setting_right_arrow;
            LinearLayout tv_setting_textsize;
            TextView tv_setting_font_big;
            TextView tv_setting_font_middle;
            TextView tv_setting_font_small;
            TextView setting_logout;
        }

        private void setData(final ViewHolder holder, final int position) {
            if (getItemViewType(position) == VIEW_TYPE_TITLE) {
                holder.tv_title.setText(settingNames.get(position));
                switch (settingTypes.get(position)) {
                    // 开关按钮
                    case 0:
                        setToggleVisible(holder, pushSoundState);
                        break;
                    case 1:
                        setToggleVisible(holder, pushVibrateState);
                        break;
                    case 3:
                        setToggleVisible(holder, pushSpecialState);
                        break;
                    case 4:
                        // 消息推送，改为消息，免打扰，仅改此处，即：原有的消息推送逻辑不变，仅改显示样式
                        setToggleVisible(holder, !pushSwitchState);
                        break;
                    case 6:
                        setToggleVisible(holder, old_imgAble);
                        break;
                    // 大中小字体
                    case 7:
                        holder.tv_title.setVisibility(View.VISIBLE);
                        holder.tv_setting_textsize.setVisibility(View.VISIBLE);
                        if (fontSize == SYSharedPreferences.FONT_VALUE_BIG_SIZE) {
                            bigFontState(holder);
                        } else if (fontSize == SYSharedPreferences.FONT_VALUE_MIDDLE_SIZE) {
                            middleFontState(holder);
                        } else if (fontSize == SYSharedPreferences.FONT_VALUE_SMALL_SIZE) {
                            smallFontState(holder);
                        }
                        holder.setting_loadImageSwitch.setVisibility(View.GONE);
                        holder.tv_setting_text.setVisibility(View.GONE);
                        holder.setting_right_arrow.setVisibility(View.GONE);
                        holder.setting_logout.setVisibility(View.GONE);
                        break;
                    // 显示数量
                    case 8:
                        setStringText(holder, getCacheSize());
                        break;
                    case 11:
                        setStringText(holder, DeviceInfo.getAppVersion());
                        break;
                    case 13:
                        // 退出登录
                        holder.tv_title.setVisibility(View.GONE);
                        holder.tv_setting_textsize.setVisibility(View.GONE);
                        holder.setting_loadImageSwitch.setVisibility(View.GONE);
                        holder.tv_setting_text.setVisibility(View.GONE);
                        holder.setting_right_arrow.setVisibility(View.GONE);
                        holder.setting_logout.setVisibility(View.VISIBLE);
                        holder.setting_logout.setText(settingNames.get(position));
                        break;
                    // 向右箭头
                    default:
                        holder.tv_title.setVisibility(View.VISIBLE);
                        holder.tv_setting_textsize.setVisibility(View.GONE);
                        holder.setting_loadImageSwitch.setVisibility(View.GONE);
                        holder.tv_setting_text.setVisibility(View.GONE);
                        holder.setting_right_arrow.setVisibility(View.VISIBLE);
                        holder.setting_logout.setVisibility(View.GONE);
                        break;
                }
            }
        }

        /**
         * 三个按钮状态切换需要修改一部分代码。以下实现
         * update by zhangyanwei 5.05 8.19
         */

        private int bigFontState(final ViewHolder holder) {
            holder.tv_setting_font_big.setBackgroundResource(R.drawable.setting_activity_mutilechoice_on);
            holder.tv_setting_font_big.setTextColor(getResources().getColor(R.color.white));
            holder.tv_setting_font_middle.setBackgroundResource(R.drawable.setting_activity_mutilechoice_off);
            holder.tv_setting_font_middle.setTextColor(getResources().getColor(R.color.color_srp_title));
            holder.tv_setting_font_small.setBackgroundResource(R.drawable.setting_activity_mutilechoice_off);
            holder.tv_setting_font_small.setTextColor(getResources().getColor(R.color.color_srp_title));
            return 3;
        }

        private int middleFontState(final ViewHolder holder) {
            holder.tv_setting_font_big.setBackgroundResource(R.drawable.setting_activity_mutilechoice_off);
            holder.tv_setting_font_big.setTextColor(getResources().getColor(R.color.color_srp_title));
            holder.tv_setting_font_middle.setBackgroundResource(R.drawable.setting_activity_mutilechoice_on);
            holder.tv_setting_font_middle.setTextColor(getResources().getColor(R.color.white));
            holder.tv_setting_font_small.setBackgroundResource(R.drawable.setting_activity_mutilechoice_off);
            holder.tv_setting_font_small.setTextColor(getResources().getColor(R.color.color_srp_title));
            return 2;
        }

        private int smallFontState(final ViewHolder holder) {
            holder.tv_setting_font_big.setBackgroundResource(R.drawable.setting_activity_mutilechoice_off);
            holder.tv_setting_font_big.setTextColor(getResources().getColor(R.color.color_srp_title));
            holder.tv_setting_font_middle.setBackgroundResource(R.drawable.setting_activity_mutilechoice_off);
            holder.tv_setting_font_middle.setTextColor(getResources().getColor(R.color.color_srp_title));
            holder.tv_setting_font_small.setBackgroundResource(R.drawable.setting_activity_mutilechoice_on);
            holder.tv_setting_font_small.setTextColor(getResources().getColor(R.color.white));
            return 1;
        }

        private void setToggleVisible(final ViewHolder holder, boolean visible) {
            holder.tv_title.setVisibility(View.VISIBLE);
            holder.setting_loadImageSwitch.setVisibility(View.VISIBLE);
            holder.setting_loadImageSwitch.setChecked(visible);
            holder.tv_setting_text.setVisibility(View.GONE);
            holder.tv_setting_textsize.setVisibility(View.GONE);
            holder.setting_right_arrow.setVisibility(View.GONE);
            holder.setting_logout.setVisibility(View.GONE);
        }

        private void setStringText(final ViewHolder holder, String text) {
            holder.tv_title.setVisibility(View.VISIBLE);
            holder.tv_setting_textsize.setVisibility(View.GONE);
            holder.setting_loadImageSwitch.setVisibility(View.GONE);
            holder.tv_setting_text.setVisibility(View.VISIBLE);
            holder.tv_setting_text.setText(text);
            holder.setting_right_arrow.setVisibility(View.GONE);
            holder.setting_logout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        int type = (Integer) settingAdapter.getItem(position);
        switch (type) {
            //账号绑定
            case SETTING_ACCOUNT_BOUND:
//                IntentUtil.StartAccountBoundActivity(this);
                break;

            //消息推送列表
            case SETTING_MSG_LIST:
                IntentUtil.StartPushHistoryActivity(this);
                break;
            //清除缓存
            case SETTING_CLEAR:
                showDialog(CLEAR_CACHE_DIALOG);
                break;
            //评分
            case SETTING_GRADE:
                GetGrade();
                break;
            //推荐给好友
            case SETTING_FRIEND:
                IntentUtil.StartFriendActivity(this);
                break;
            //新版本检测
            case SETTING_VERSION:
                if(CMainHttp.getInstance().isNetworkAvailable(this)){
                    if (DownloadService.IS_DOWNLOAD_BACKGROUND) {   //apk在后台下载
                        LogDebugUtil.d("DownloadService", "setting : " + DownloadService.IS_DOWNLOAD_BACKGROUND);
                        ToastUtil.show(this, "正在下载");
                    }else{
                        ((MainApplication) this.getApplication()).checkVersion(2);
                    }
                }else{
                    SouYueToast.makeText(this, getResources().getString(R.string.neterror), Toast.LENGTH_SHORT).show();
                }
                break;
            //关于我们
            case SETTING_ABOUT:
                IntentUtil.startAboutActivity(this);
                break;
            //退出登录
            case SETTING_LOGOUT:
                logout();
                break;
            case SETTING_FEEDBACK:
                IntentUtil.StartIMSouYueActivity(this);
                break;
            case SETTING_GUIDE:
                IntentUtil.gotoWeb(this, UrlConfig.NEW_USER_GUIDE, "nopara");
                break;
            default:
                break;
        }
    }

    /**
     * 修改的状态，
     * @param key 三个键值
     */
    private void setUserMessageNotify(String key)
    {
        pushSwitchState=sysp.getBoolean(SYSharedPreferences.KEY_PUSHSWITCH,true);
        pushSoundState =sysp.getBoolean(SYSharedPreferences.KEY_PUSH_SOUND,true);
        pushVibrateState=sysp.getBoolean(SYSharedPreferences.KEY_PUSH_VIBRATE,true);
        boolean result = ImserviceHelp.getInstance().setUserMessageNotify(6,0,!pushSwitchState,pushSoundState,pushVibrateState);
        if(!result) // 失败, 将设置结果 取反
        {
            if(key.equalsIgnoreCase(SYSharedPreferences.KEY_PUSHSWITCH))
            {
                pushSwitchState =!pushSwitchState;
                sysp.putBoolean(key,pushSwitchState);
            }else if(key.equalsIgnoreCase(SYSharedPreferences.KEY_PUSH_SOUND))
            {
                pushSoundState= !pushSoundState;
                sysp.putBoolean(key,!pushSoundState);
            }else if(key.equalsIgnoreCase(SYSharedPreferences.KEY_PUSH_VIBRATE))
            {
                pushVibrateState =!pushVibrateState;
                sysp.putBoolean(key,pushVibrateState);
            }
            SouYueToast.showShort(mContext,"修改失败啦！");
            settingAdapter.notifyDataSetChanged();
        }

    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.systemwarning)
                .setMessage(R.string.userAccountActivity_islogout)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                exitSouYue();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 取消
                            }
                        }).show();
    }

    public void exitSouYue() {
        IntentUtil.chageDiscoverTabRed(this, -2);

        // 找回密码、普通注册跳转主界面
        Intent intent = new Intent();
        intent.setAction(logoutAction);
        sendBroadcast(intent);

        SYSharedPreferences.getInstance().remove(
                SYSharedPreferences.KEY_TASKCENTER_DISCOVERMSG);
        // 确定
        SYUserManager.getInstance().delUser(user);
        String type = AccountInfo.removeLoginToken();
        if (!TextUtils.isEmpty(type)) {
            switch (AccountInfo.THIRDTYPE.valueOf(type)) {
                case SINA_WEIBO:
                    ShareByWeibo.getInstance().unAuth2(this);
                    break;

            }
        }
        ThreadPoolUtil.getInstance().execute(new Runnable() {

            @Override
            public void run() {
//                ImserviceHelp.getInstance().im_logout();
                ImserviceHelp.getInstance().im_connect(
                        DeviceInfo.getAppVersion());
            }
        });

        ZhongSouActivityMgr.getInstance().goHome();
        IntentUtil.openMainActivity(this, new int[]{0});

//        BroadcastUtil.notificationCancel(this,-1);
        ImserviceHelp.getInstance().cancelNotify(-1);
    }

    /**
     * 评分
     */
    private void GetGrade() {
        Intent markIntent = new Intent(Intent.ACTION_VIEW);
        markIntent.setData(Uri.parse("market://details?id="
                + getPackageName()));
        PackageManager pkgMg = getPackageManager();
        List<ResolveInfo> infos = pkgMg.queryIntentActivities(markIntent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (infos.size() > 0) {
            startActivity(markIntent);
        } else {
            showToast(R.string.no_market);
        }
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        UserInfoUtils.setSpecialState(pushSpecialState, user);
        settingAdapter.notifyDataSetChanged();
    }
}
