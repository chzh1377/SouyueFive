package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.activity.Mine_ItemActivity;
import com.zhongsou.souyue.activity.SettingActivity;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.adapter.MineAdapter;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.module.MineListInfo;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.user.MyBgImgRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.LayoutApi;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.view.MineOverScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinguanping on 15/1/7.
 * “我的” 标签
 */
public class MineFragment extends BaseTabFragment implements
        View.OnClickListener, ListView.OnItemClickListener {

    private static final  int HTTP_GET_USER_BG = 800;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

//    public static final String TAB_NAME = "mine";
    public final static String logoutAction = "ACTION_LOGOUT_TO_HOME";
    public static final String TAB_NAME = "me";
    private Context context;
    private ListView mListView = null;
    private final String Title[] = {"个人信息", "我的首页", "", "安全中心", "我的钱袋", "阅读", "","推荐给好友", "设置"};
    private final String Content[] = {"", "向小伙伴展示风采", "", "", "积分兑换", "原创、收藏", "", "",""};
    private final int type[] = {1, 1, 0, 1, 1, 1, 0, 1,1};//类型 1：为正常行显示  0：为隔断行，分隔行
    private final int imgId[] = {R.drawable.mine_icon_personinfo,
            R.drawable.mine_icon_myhomepager,
            0,
            R.drawable.mine_icon_security,
            R.drawable.mine_icon_purse,
            R.drawable.mine_icon_read,
            0,
            R.drawable.mine_icon_friend,
            R.drawable.mine_icon_settings};//左侧icon

//    private ImageView headImgLogo;
    private ImageView headImg;
    private TextView nickName;
    private ImageView img_mine_bg;

    private CMainHttp http;
    private boolean isFromPersonOrLogin = false;
    private boolean isFirst = true;

    private MineOverScrollView overScrollView;
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View root = inflater.inflate(LayoutApi.getLayoutResourceId(R.layout.mine), container, false);
        overScrollView= findView(root, R.id.mineoverscrollview);
        mListView = findView(root, R.id.mine_listview);
        mListView.addHeaderView(getHeadView());
        mListView.setAdapter(new MineAdapter(context, createListData()));
        mListView.setOnItemClickListener(this);
        http = CMainHttp.getInstance();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isFromPersonOrLogin || isFirst){
			MyBgImgRequest myBgImgRequest = new MyBgImgRequest(
					HttpCommon.PERSONCENTER_MY_BG_ID, this)
					.setParams(SYUserManager.getInstance().getToken());
			mMainHttp.doRequest(myBgImgRequest);
        }
        isFirst = false;
        ChangeIsLoginStatue();
        overScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                overScrollView.smoothScrollToTop();
            }
        }, 100);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        overScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                overScrollView.smoothScrollToTop();
            }
        }, 100);
    }

    /**
     * 获得头部用户信息view
     *
     * @return
     */
    private View getHeadView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View HeadView = inflater.inflate(R.layout.mine_head, null);
        RelativeLayout headLayout = findView(HeadView, R.id.mine_head_layout);
        headLayout.setOnClickListener(this);
        headImg = findView(HeadView, R.id.mine_head_img);
        nickName = findView(HeadView, R.id.mine_head_txt);
        img_mine_bg = findView(HeadView,R.id.img_mine_bg);
        ChangeIsLoginStatue();
        img_mine_bg.postDelayed(new Runnable() {
            @Override
            public void run() {
                setDefaultImage();
            }
        },100);
        return HeadView;
    }

    /**
     * 改变登录状态显示信息`
     */
    private void ChangeIsLoginStatue() {
        User user = SYUserManager.getInstance().getUser();
        if (user != null && user.image() != null && headImg != null) {
            MyImageLoader.imageLoader.displayImage(user.image(), headImg,
                    MyImageLoader.Circleoptions);//给头像图片赋值
        } else {
            headImg.setImageResource(R.drawable.mine_head_defaultimg);

        }
        if (IntentUtil.isLogin()) {
            if (nickName != null)
                nickName.setText(user.name());//昵称赋值
        } else {
            if (nickName != null)
                nickName.setText("立即登录");//昵称赋值
            img_mine_bg.setImageResource(R.color.mine_heand_background_red);
        }
    }

    /**
     * 生成列表数据
     */
    private List<MineListInfo> createListData() {
        List<MineListInfo> listInfos = new ArrayList<MineListInfo>();
        for (int i = 0; i < Title.length; i++) {
            MineListInfo mineListInfo = new MineListInfo();
            mineListInfo.setTitle(Title[i]);
            mineListInfo.setContent(Content[i]);
            mineListInfo.setImgId(imgId[i]);
            mineListInfo.setType(type[i]);
            listInfos.add(mineListInfo);
        }

        return listInfos;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_head_layout:
                if (IntentUtil.isLogin()) {//已登录
                    gotoTouchGallery();
                } else {
                    IntentUtil.gotoLogin(context);
                    isFromPersonOrLogin = true;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到头像放大的页面
     *
     */
    private void gotoTouchGallery()
    {
        Intent intent = new Intent();
        intent.setClass(context, TouchGalleryActivity.class);
        TouchGallerySerializable tg = new TouchGallerySerializable();
        List<String> list = new ArrayList<String>();
        String HeadImg=SYUserManager.getInstance().getUser().getBigImage();
        if(StringUtils.isEmpty(HeadImg))
        {
            HeadImg= SYUserManager.getInstance().getUser().image();
        }
        list.add(HeadImg);
        tg.setItems(list);
        tg.setClickIndex(0);
        Bundle extras = new Bundle();
        extras.putSerializable("touchGalleryItems", tg);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 1://個人信息
                if (IntentUtil.isLogin()) {// 已登录
                    UmengStatisticUtil.onEvent(context,UmengStatisticEvent.MY_INFO_CLICK);
                    startActivity(new Intent(context, Mine_ItemActivity.class));
                    ((Activity) context).overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else {
                    IntentUtil.gotoLogin(context);
                    isFromPersonOrLogin = true;
                }
                UpEventAgent.onZSDevMyItem(context,Title[0]);
                break;
            case 2://我的首页
                if (IntentUtil.isLogin()) {
                    UmengStatisticUtil.onEvent(context,UmengStatisticEvent.MY_HOME_CLICK);
                    isFromPersonOrLogin = true;
                    User user = SYUserManager.getInstance().getUser();
                    PersonPageParam param = new PersonPageParam();
                    param.setViewerUid(user.userId());
                    param.setFrom(PersonPageParam.FROM_OTHER);
                    UIHelper.showPersonPage((MainActivity) context, param);
                } else {
                    IntentUtil.gotoLogin(context);
                    isFromPersonOrLogin = true;
                }
                UpEventAgent.onZSDevMyItem(context,Title[1]);
                break;

            case 4://安全中心
                if (IntentUtil.isLogin()) {
                    UmengStatisticUtil.onEvent(context,UmengStatisticEvent.MY_SAFE_CENTER_CLICK);
                    IntentUtil
                            .gotoWeb(context, UrlConfig.SecurityCenter, "interactWeb");
                } else {
                    IntentUtil.gotoLogin(context);
                    isFromPersonOrLogin = true;
                }
                UpEventAgent.onZSDevMyItem(context,Title[3]);
                break;
            case 5://钱包
                if (IntentUtil.isLogin()) {
                    UmengStatisticUtil.onEvent(context,UmengStatisticEvent.MY_MONEY_CLICK);
                    IntentUtil.startPurseActivityWithAnim(context);
                } else {
                    IntentUtil.gotoLogin(context);
                    isFromPersonOrLogin = true;
                }
                UpEventAgent.onZSDevMyItem(context,Title[4]);
                break;
            case 6://阅读
                IntentUtil.startReadActivityWithAnim(context);
                UmengStatisticUtil.onEvent(context,UmengStatisticEvent.MY_READ_CLICK);
                UpEventAgent.onZSDevMyItem(context,Title[5]);
                break;
            case 8://推荐给好友
                UmengStatisticUtil.onEvent(context,"my_recommend_click");
                UpEventAgent.onZSDevMyItem(context,Title[7]);
                IntentUtil.StartFriendActivity(context);
                break;
            case 9://设置
                UmengStatisticUtil.onEvent(context, UmengStatisticEvent.MY_SETTING_CLICK);
                UpEventAgent.onZSDevMyItem(context,Title[8]);
                IntentUtil.startActivityWithAnim(context, "", SettingActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        MyBgImgRequest myBgImgRequest = new MyBgImgRequest(
				HttpCommon.PERSONCENTER_MY_BG_ID, this)
				.setParams(SYUserManager.getInstance().getToken());
		mMainHttp.doRequest(myBgImgRequest);
//        http.getUserBg(HTTP_GET_USER_BG,SYUserManager.getInstance().getToken(),this);
        ChangeIsLoginStatue();
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        super.onHttpResponse(_request);
        HttpJsonResponse res = (HttpJsonResponse) _request.getResponse();
        String bg = res.getBody().get("bg_img").getAsString();
        if(SouyueAPIManager.isLogin())
        {
            if(StringUtils.isNotEmpty(bg))
            {
                MyImageLoader.imageLoader.displayImage(bg, img_mine_bg,MyImageLoader.UserBgoptions);
            }else
            {
                img_mine_bg.setImageResource(R.drawable.circle_vcard_default_top_bg);
            }
        }else
        {
            img_mine_bg.setImageResource(R.color.mine_heand_background_red);
        }
    }

    /**
     * 设置默认情况下 即：无网络状态下的背景图片
     */
    private void setDefaultImage()
    {
        if(SouyueAPIManager.isLogin())
        {
            img_mine_bg.setImageResource(R.drawable.circle_vcard_default_top_bg);
        }else
        {
            img_mine_bg.setImageResource(R.color.mine_heand_background_red);
        }
    }
}


