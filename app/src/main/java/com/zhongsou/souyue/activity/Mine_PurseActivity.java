package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.Mine_PurseAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.HomePageItem;
import com.zhongsou.souyue.module.JiFen;
import com.zhongsou.souyue.module.MyPoints;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.personal.UserIntegral;
import com.zhongsou.souyue.net.user.UserWalletRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CRequestProcess;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IRequestCache;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.FmtMicrometer;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的钱包界面
 */
public class Mine_PurseActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

//    private static final int USERWALLET_REQUESTID = 12354654;
    private ListView listview;
    private ArrayList<HomePageItem> lists = new ArrayList<HomePageItem>();
    private Mine_PurseAdapter adapter;
    private ImageButton goback;
    private TextView title;
    private TextView ZSB, JF;
    private User user;
    private CMainHttp mCmainHttp; // http 对象

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fragment_myread_tab);
        mCmainHttp = CMainHttp.getInstance();
        user = SYUserManager.getInstance().getUser();
        initUI();
    }

    private View initHeadview() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View headView = inflater.inflate(R.layout.mine_purse_head, null);
        ZSB = (TextView) headView.findViewById(R.id.mine_purse_head_txtZsb);
        JF = (TextView) headView.findViewById(R.id.mine_purse_head_txtJf);
        LinearLayout mine_purse_head_layoutZsb = (LinearLayout) headView.findViewById(R.id.mine_purse_head_layoutZsb);
        mine_purse_head_layoutZsb.setOnClickListener(this);
        LinearLayout mine_purse_head_layoutJf = (LinearLayout) headView.findViewById(R.id.mine_purse_head_layoutJf);
        mine_purse_head_layoutJf.setOnClickListener(this);
        UserIntegral inte = new UserIntegral(HttpCommon.USER_INTERNAL_REQUEST,this);
        inte.setParams(user.userName());
        mMainHttp.doRequest(inte);
//        http.integral(user.userName());
        return headView;
    }

    public void integralSuccess(MyPoints points) {
        if (points != null) {
            List<JiFen> jifens = points.getScore();
            for (JiFen jf : jifens) {
                if (jf.isZSB()) {
                    String cfj = "<font color='#ffffff'>"
                            + FmtMicrometer.fmtMicrometer(jf.getNum())
                            + "</font>";
                    ZSB.setText(Html.fromHtml(cfj));

                }
                if (jf.isJF()) {
                    String cfj = "<font color='#ffffff'>"
                            + FmtMicrometer.fmtMicrometer(jf.getNum())
                            + "</font>";
                    JF.setText(Html.fromHtml(cfj));
                }
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initUI() {
        goback = (ImageButton) findViewById(R.id.goBack);
        goback.setOnClickListener(this);

        title = (TextView) findViewById(R.id.activity_bar_title);
        if (title != null) {
            title.setText("钱包");
        }

        listview = (ListView) findViewById(R.id.discover_list);
        adapter = new Mine_PurseAdapter(this);

        listview.addHeaderView(initHeadview(), null, false);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(this);

        initMine_PurseList();
    }

    // 第一次取数据取缓存
    private void initMine_PurseList() {
//        http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE_FORCE);
//        http.getMine_PurseList(UrlConfig.MINE_PURSE_LIST, SYUserManager
//                .getInstance().getToken(), false);


//        UserWalletRequest wallet = new UserWalletRequest(HttpCommon.USER_WALLET_REQUEST,this,true);
//        wallet.setParams(SYUserManager
//                .getInstance().getToken());
        UserWalletRequest.send(HttpCommon.USER_WALLET_REQUEST,this,SYUserManager
                .getInstance().getToken(),true);
//        mCmainHttp.doRequest(wallet);
    }

    // 强取
    public void getMine_PurseList() {
        UserWalletRequest.send(HttpCommon.USER_WALLET_REQUEST,this,SYUserManager
                .getInstance().getToken(),false);
//        UserWalletRequest wallet = new UserWalletRequest(HttpCommon.USER_WALLET_REQUEST,this,false);
//        wallet.setParams(SYUserManager
//                .getInstance().getToken());
//        mCmainHttp.doRequest(wallet);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        switch (request.getmId()){
            case HttpCommon.USER_WALLET_REQUEST:
                getMine_PurseListSuccess(request);
                break;
            case HttpCommon.USER_INTERNAL_REQUEST:
                HttpJsonResponse response = request.getResponse();
                MyPoints points = new Gson().fromJson(response.getBody(),
                        new TypeToken<MyPoints>() {
                        }.getType());
                integralSuccess(points);
                User user = SYUserManager.getInstance().getUser();
                if (user == null) {
                    user = new User();
                }
                user.user_level_$eq(response.getBody().get("userlevel")
                        .getAsString());
                user.user_level_title_$eq(response.getBody().get("userleveltitle")
                        .getAsString());
                user.user_level_time_$eq(String.valueOf(System.currentTimeMillis()));
                SYUserManager.getInstance().setUser(user);
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        super.onHttpError(_request);

    }


    public void getMine_PurseListSuccess(IRequest _request) {
        HttpJsonResponse response = _request.<HttpJsonResponse>getResponse();
        lists = new Gson().fromJson(response.getBodyArray(),
                new TypeToken<ArrayList<HomePageItem>>() {
                }.getType());
        if(mCmainHttp.getCacheState(_request.getCacheKey()) == IRequestCache.CACHE_STATE_IS_EXPIRE
                && CMainHttp.getInstance().isNetworkAvailable(Mine_PurseActivity.this) ) {
            getMine_PurseList();
        }

//        if (status.hasExpired == true && mCmainCMainHttp.getInstance().isNetworkAvailable(Mine_PurseActivity.this)) {
//            getMine_PurseList();
//        }

        if (lists.size() > 0) {
            adapter.SetList(lists);
            adapter.notifyDataSetChanged();
        }
    }

    private String createAccessUrl(String url) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?username=").append(user.userName()).append("&token=")
                .append(user.token()).append("&r=")
                .append(System.currentTimeMillis());
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                super.onBackPressed();
                break;
            case R.id.mine_purse_head_layoutZsb://中搜币
                if (!CMainHttp.getInstance().isNetworkAvailable(Mine_PurseActivity.this)) {
                    SouYueToast.makeText(this,
                            getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                IntentUtil
                        .gotoWeb(
                                this,
                                createAccessUrl(UrlConfig.HOST_ZHONGSOU_COINS_BLANCE),
                                null);
                break;
            case R.id.mine_purse_head_layoutJf://积分
                if (!CMainHttp.getInstance().isNetworkAvailable(Mine_PurseActivity.this)) {
                    SouYueToast.makeText(this,
                            getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                IntentUtil.gotoWeb(this,
                        createAccessUrl(UrlConfig.HOST_ZHONGSOU_JF_BLANCE), null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switchContent(adapter.getItem(position - 1), position - 1);
    }

    /**
     * 点击切换contentview显示内容，只处理跳转内容
     *
     * @param homePage
     * @param position 当前homepage的位置
     */
    public void switchContent(HomePageItem homePage, int position) {

        try {
            HomePageItem.CATEGORY category = HomePageItem.CATEGORY.valueOf(homePage.category());
            UpEventAgent.onZSDevMoneyItem(this, homePage.title());
            switch (category) {
//                case scan:
//                    IntentUtil.startActivityWithAnim(this, "", ScaningActivity.class);
//                    break;
//                case srp:
//                    Intent intent = new Intent(this, SRPActivity.class);
//                    intent.putExtra("keyword", homePage.keyword());
//                    intent.putExtra("srpId", homePage.srpId());
//                    this.startActivity(intent);
//                    overridePendingTransition(R.anim.left_in,
//                            R.anim.left_out);
//                    break;
//                case selfCreate:
//                    Intent i = new Intent();
//                    i.setClass(this, SelfCreateActivity.class);
//                    startActivity(i);
//                    overridePendingTransition(R.anim.left_in,
//                            R.anim.left_out);
//                    break;
//                case app:// TODO FAN
//                    boolean isInstall = ActivityUtils.isIntentAvailable(this,
//                            MainActivity.ACTION_APPBIBLE);
//                    if (isInstall) {
//                        // 跳转到应用宝典
//                        Intent appIntent = new Intent(
//                                ConstantsUtils.ACTION_APPBIBLE);
//                        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        this.startActivity(appIntent);
//                        overridePendingTransition(
//                                R.anim.left_in, R.anim.left_out);
//                    } else {
//                        // 打开url
//                        IntentUtil.gotoWeb(this, UrlConfig.bible, "");
//                    }
//                    break;
//                case url:
//                    IntentUtil.gotoWeb(this, homePage.url(), "url");
//                    break;
                case interactWeb:
                    if (!homePage.isOutBrowser()) {
                        if (homePage.title().equals("积分兑换中搜币")) {
                            IntentUtil.gotoWebForResult(this, homePage.url(), "interactWeb");
                        } else {
                            IntentUtil.gotoWeb(this, homePage.url(), "interactWeb");
                        }
                    } else {
                        Intent outIntent = new Intent(Intent.ACTION_VIEW);
                        outIntent.setData(Uri.parse(homePage.url()));
                        startActivity(outIntent);
                    }
                    break;
//                case subscibe:// 我的订阅
//                    IntentUtil.openManagerAcitivity(this,
//                            MySubscribeListActivity.class);
//                    break;
//                case interest:
//                    IntentUtil.openManagerAcitivity(this,   
//                            R.string.manager_grid_ins);
//                    break;
//                case rss:// 我的新闻源
//                    IntentUtil.openManagerAcitivity(this,
//                            R.string.manager_grid_rss);
//                    break;
//                case business:// 我的商家
//                    IntentUtil.openManagerAcitivity(this,
//                            R.string.manager_grid_ent);
//                    break;
//                /*case system:
//                    NewHomeActivity.showNews();
//                    break;*/
//                case search:// 综合搜悦
//                    IntentUtil.openSearchActivity(this);
//                    overridePendingTransition(R.anim.left_in,
//                            R.anim.left_out);
//                    break;
                case rechargeZsb: // 充值
                    IntentUtil.gotoPay(this, 0x3);
                    break;
                /*case exchangeZsb: // 积分兑换中搜币
                    IntentUtil.gotoWeb(this, homePage.url(), "interactWeb");
                    break;*/
                default:
                    break;
            }

        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x3) {
            UserIntegral inte = new UserIntegral(HttpCommon.USER_INTERNAL_REQUEST,this);
            inte.setParams(user.userName());
            mMainHttp.doRequest(inte);
//            http.integral(user.userName());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
