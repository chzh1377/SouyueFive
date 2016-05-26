package com.zhongsou.souyue.presenter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facebook.drawee.view.ZSImageOptions;
import com.facebook.drawee.view.ZSImageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.TuitaIMManager;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.FirstInActivity;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.adapter.firstleader.LeaderPageThreeAdapter;
import com.zhongsou.souyue.adapter.firstleader.LeaderPageTwoAdapter;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.fragment.SouyueTabFragment;
import com.zhongsou.souyue.fragment.firstleader.FirstLeaderPage;
import com.zhongsou.souyue.module.AppData;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.module.firstleader.CharacterList;
import com.zhongsou.souyue.module.firstleader.ChildGroupItem;
import com.zhongsou.souyue.module.firstleader.SexList;
import com.zhongsou.souyue.module.firstleader.UserGuideInfo;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.guide.FirstGuideDataReq;
import com.zhongsou.souyue.net.guide.FirstGuideSubReq;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.UnScrollViewPager;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.PushUtils;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyw on 2016/3/24.
 */
public class FirstInActPresenter implements LeaderPageThreeAdapter.OnItemSelectedCountListener {
    public static final String TAG = FirstInActPresenter.class.getSimpleName();
    FirstInActivity mViewController = null;
    private final LayoutInflater mInflater;
    private       Handler        mHanler;
    private Gson mGson = new Gson();
    private int           characterImageHolder;
    private UserGuideInfo mInfo;
    private GenderOnClick savedGender;
    private OnPage3Clicked onPage3Clicked;
    private boolean needRestoreEnterHome; // 恢复前台需要进入首页

    public FirstInActPresenter(FirstInActivity viewController) {
        if (viewController == null)
            throw new IllegalArgumentException("ViewController cant be none");
        this.mViewController = viewController;
        mInflater = LayoutInflater.from(mViewController);
        mHanler = new Handler();
        onPage3Clicked = new OnPage3Clicked();
    }

    public void init() {
        mViewController.setLoading();
        //初始化第一页，第二页数据，加载
        mViewController.setPageOne();
//        makeData();
        //停止推送
        makeData();

    }

    /**
     * 准备本地数据
     */
    private void makeData() {
//        Toast.makeText(mViewController,"构造第一页数据",Toast.LENGTH_SHORT).show();
        new MakeDataThread().start();
    }


    /**
     * 初始化性别
     */
    public void setPageOneData(final SexList sexList) {
        mHanler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirstLeaderPage currentPageFrag = mViewController.getCurrentPageFrag();
                LinearLayout linearLayout = (LinearLayout) currentPageFrag.getView().findViewById(R.id.home_firstin_bottom_container);
                linearLayout.removeAllViews();
                for (SexList.SexListEntity entity : sexList.getSexList()) {
                    View genderLayout = mInflater.inflate(R.layout.circle_image_with_tag, null);
                    String imageUrl = entity.getImage();
                    ZSImageView icon = (ZSImageView) genderLayout.findViewById(R.id.genger_head_icon);
                    icon.setImageURI(Uri.parse(imageUrl), ZSImageOptions.getLocalImageConfig(mViewController));
                    TextView tv = (TextView) genderLayout.findViewById(R.id.gender_head_text);
                    tv.setText(entity.getName());
                    linearLayout.addView(genderLayout);
                    GenderOnClick onGenderClick = new GenderOnClick(entity);
                    icon.setOnClickListener(onGenderClick);
                }
                mViewController.removeLoading();
            }
        }, 50);
    }

    /**
     * 设置第二页数据
     *
     * @param mSexList
     */
    public void setPageTwoData(SexList.SexListEntity mSexList) {
        mViewController.setLoading();
        mViewController.setPageTwo();
        GridView             mPageTwoGrid = (GridView) mViewController.findViewById(R.id.home_first_leader_pagetwo_button_group);
        LeaderPageTwoAdapter adapter      = new LeaderPageTwoAdapter(mViewController, mSexList.getCharacterList());
        mPageTwoGrid.setAdapter(adapter);
        mPageTwoGrid.setOnItemClickListener(adapter);
        ZSImageView characterImage = (ZSImageView) mViewController.findViewById(R.id.home_leader_pagetwo_character);
        Button btnPage3 = (Button) mViewController.findViewById(R.id.page3);
        adapter.setOnCharacterSelectedListener(new OnCharacterSelected(mSexList.getName(), btnPage3, characterImage));
        adapter.notifyDataSetChanged();
        mViewController.removeLoading();
        RelativeLayout.LayoutParams layoutParams   = (RelativeLayout.LayoutParams) characterImage.getLayoutParams();
        int                         fixedHeight    = getFixedHeight();
        layoutParams.height = (int) (fixedHeight * 0.80);
        layoutParams.bottomMargin = (int) (fixedHeight * 0.10);
        characterImage.requestLayout();
        if (mSexList.getName().equals("男")) {
            characterImageHolder = R.drawable.home_first_leader_unknow_character_boy;
        } else {
            characterImageHolder = R.drawable.home_first_leader_unknow_character_girl;
        }
        characterImage.setImageURI(Uri.parse("res://" + mViewController.getPackageName() + ")/" + characterImageHolder));
        btnPage3.setBackgroundResource(R.drawable.home_guide_nextbtn_backgroup);
        btnPage3.setTextColor(Color.parseColor("#959595"));
        onPage3Clicked.setCharacterList(null);
        btnPage3.setOnClickListener(onPage3Clicked);
        adapter.reshowImage();
        mViewController.removeLoading();

    }

    /**
     * 获取适合居中的宽高
     *
     * @return
     */
    private int getFixedHeight() {
        int sloganHeight       = DeviceUtil.dip2px(mViewController, 105);
        int sloganParentHeight = Utils.getScreenHeight() - DeviceUtil.dip2px(mViewController, 220);
        return sloganParentHeight - sloganHeight;
    }

    /**
     * 第二页事件
     */
    public void onCharacterSelected(CharacterList character, Button btnPage3,ZSImageView characterImage) {
        //TODO 此处添加角色选择事件  guide.people.click
        onPage3Clicked.setCharacterList(character);
        btnPage3.setBackgroundResource(R.drawable.home_first_leader_pagetwo_btn_bg_pressed);
                btnPage3.setTextColor(Color.parseColor("#ffffff"));
        characterImage.setImageURI(Uri.parse(character.getBigImage()));
    }

    /**
     * 第三页
     */
    public void setPageThreeData(CharacterList characterList) {
        mViewController.setPageThree();
        GridView               gridview = (GridView) mViewController.findViewById(R.id.home_first_leader_pagethree_buttongroup);
        LeaderPageThreeAdapter adapter  = new LeaderPageThreeAdapter(mViewController, characterList.getChilds());
        gridview.setAdapter(adapter);
        adapter.setOnItemSelectedCountListener(this);
        gridview.setOnItemClickListener(adapter);
        adapter.notifyDataSetChanged();
        String sex  = characterList.getSex();
        String name = characterList.getName();
        gridview.setHorizontalSpacing(Utils.getScreenWidth() / 20);
        gridview.requestLayout();
        mViewController.removeLoading();
        mViewController.findViewById(R.id.enter_home).setOnClickListener(new OnEnterHomeClick(sex, name, adapter.getDatas()));
    }


    public void goBackPage() {
        mViewController.setCurrentPage(mViewController.getCurrentPage() - 1);
    }


    /**
     * 进入首页
     */
    private void entreHome() {
        mHanler.post(new Runnable() {
            @Override
            public void run() {
                if(mViewController.isFront()){
                    Intent intent = new Intent(mViewController, MainActivity.class);
                    mViewController.overridePendingTransition(R.anim.anim_in_scale, R.anim.anim_out_scale);
                    mViewController.startActivity(intent);
                    mViewController.finish();
                    needRestoreEnterHome = false;
                }else{
                    needRestoreEnterHome = true;
                }
            }
        });
    }

    public void onDestroy() {
        if (mHanler != null) {
            mHanler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onItemSelectedCount(int count) {
        Log.e("onItemSelectedCount", "" + count);
        Button btnPage3 = (Button) mViewController.findViewById(R.id.enter_home);
        if (count > 0) {
            btnPage3.setTextColor(Color.parseColor("#000000"));
            btnPage3.setBackgroundResource(R.drawable.home_first_leader_pagethree_btn_bg_pressed);
            btnPage3.setTag(true);
        } else {
            btnPage3.setTextColor(Color.parseColor("#959595"));
            btnPage3.setBackgroundResource(R.drawable.home_guide_nextbtn_backgroup);
            btnPage3.setTag(false);
        }
    }

    public void onSaveState() {

    }

    public void onRestore() {
        if (needRestoreEnterHome) {
            entreHome();
        }
    }

    class OnPage3Clicked implements View.OnClickListener, IVolleyResponse {
        private CharacterList mC = new CharacterList();

        OnPage3Clicked() {
        }

        public void setCharacterList(CharacterList mC) {
            this.mC = mC;
        }

        @Override
        public void onClick(View v) {
            if (mC == null) {
                return;
            }
            UpEventAgent.onZSGuideCharacterClick(mViewController, mC.getName());
            mViewController.setLoading();
            String parentGroupName = mC.getSex().concat("_").concat(mC.getName());
            FirstGuideDataReq.send(HttpCommon.FIRST_GUIDE_GETDATA, this, parentGroupName);
        }

        private void goToNext() {
            new Thread() {
                @Override
                public void run() {
                    for (int x = 0; x < mC.getChilds().size(); x++) {
                        mC.getChilds().get(x).setDefaultSelected(mC.getChilds().get(x).getIsSelected() == 1);
                    }
                    mViewController.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setPageThreeData(mC);
                        }
                    });
                }
            }.start();

        }

        @Override
        public void onHttpResponse(IRequest request) {
            try {
                List<ChildGroupItem> items = mGson.fromJson(request.getResponse().toString(), new TypeToken<List<ChildGroupItem>>() {
                }.getType());
                if (items != null && items.size() > 0)
                    mC.setChilds(items);
            } catch (Exception e) {

            }
            goToNext();
        }

        @Override
        public void onHttpError(IRequest request) {
            goToNext();
        }

        @Override
        public void onHttpStart(IRequest request) {

        }
    }

    class OnCharacterSelected implements LeaderPageTwoAdapter.OnCharacterSelectedListener {
        private final String      sex;
        private final Button      btnPage3;
        private final ZSImageView characterImage;

        public OnCharacterSelected(String sex, Button btnPage3, ZSImageView characterImage) {
            this.sex = sex;
            this.btnPage3 = btnPage3;
            this.characterImage = characterImage;
        }

        @Override
        public void onCharacterSelected(CharacterList character) {
            if (!mViewController.isLoading()) {
                character.setSex(sex);
                FirstInActPresenter.this.onCharacterSelected(character, btnPage3, characterImage);
            }
        }
    }

    class GenderOnClick implements View.OnClickListener {
        private final SexList.SexListEntity mSexList;

        public GenderOnClick(SexList.SexListEntity sexListEntity) {
            this.mSexList = sexListEntity;
        }

        @Override
        public void onClick(final View v) {
            //TODO 此处添加事件 ：男女点击 guide.gender.click
            if (v != null) {
                UpEventAgent.onZSGuideGenderClick(mViewController, mSexList.getName());
                LeaderPageTwoAdapter.LAST_SELECTED_POS = -1;
            }
            savedGender = GenderOnClick.this;
            setPageTwoData(mSexList);
        }

        public SexList.SexListEntity getSexList() {
            return mSexList;
        }
    }

    class OnEnterHomeClick implements View.OnClickListener, IVolleyResponse {
        private final String               sex;
        private final String               name;
        private final List<ChildGroupItem> subItem;

        public OnEnterHomeClick(String sex, String name, List<ChildGroupItem> childs) {
            this.sex = sex;
            this.name = name;
            this.subItem = childs;
        }

        public void goHome() {
            //恢复
            try{
                mViewController.restoreDefaultPushState();
//                PushUtils.resumePush(mViewController);
            }catch (Exception e){

            }
            entreHome();
        }

        @Override
        public void onClick(View v) {
            boolean canClick = (boolean) v.getTag();
            if (!canClick) {
                return;
            }
            v.setTag(false); // 不允许多次点击
            mViewController.setLoading();
            new Thread() {
                @Override
                public void run() {
                    mInfo = new UserGuideInfo(sex, name, subItem);
                    List<AppData> datas = DeviceInfo.getAppData(mViewController);
                    FirstGuideSubReq.send(HttpCommon.FIRST_GUIDE_SUBSCRIPE, OnEnterHomeClick.this, mInfo, datas);
                    long uid = 0;
                    try {
                        uid = Long.decode(SYUserManager.getInstance().getUserId());
                    } catch (Exception e) {

                    }
                    CommSharePreference.getInstance().putValue(uid, UserGuideInfo.GENDER_KEY, sex);
                    CommSharePreference.getInstance().putValue(uid, UserGuideInfo.AGE_KEY, name);

                }
            }.start();
            try {
                User user = SYUserManager.getInstance().getUser();
                if (user != null) {
                    user.setSex("男".equals(sex) ? 0 : 1);
                    SYUserManager.getInstance().setUser(user);
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onHttpResponse(IRequest request) {
            new Thread() {
                @Override
                public void run() {
                    List<SuberedItemInfo> addList  = new ArrayList<SuberedItemInfo>();// 添加数组
                    List<ChildGroupItem>  subItems = mInfo.getSubItems();
                    for (ChildGroupItem item : subItems) {
                        SuberedItemInfo info = new SuberedItemInfo();
                        info.setCategory(item.getCategory());
                        info.setKeyword(item.getKeyword());
                        info.setSrpId(item.getSrpId());
                        addList.add(info);
                    }
                    SuberDaoImp suberDaoImp = new SuberDaoImp();
                    suberDaoImp.addAll(addList);
                    Intent intentGotoBall = new Intent();
                    intentGotoBall.setAction(SouyueTabFragment.REFRESH_HOMEBALL_FROMCACHE);
                    mViewController.sendBroadcast(intentGotoBall);
                    goHome();
                }
            }.start();

        }

        @Override
        public void onHttpError(IRequest request) {
            mInfo.save();
            goHome();
        }

        @Override
        public void onHttpStart(IRequest request) {

        }
    }

    public View.OnClickListener btnBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mViewController.isLoading()) {
                goBackPage();
            }
        }
    };

    class BtnBackListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            goBackPage();
        }
    }

    class MakeDataThread extends Thread {

        private SexList sexList;

        @Override
        public void run() {
            try {
                InputStream in = mViewController.getAssets().open("leaderpage/data.json");
                InputStreamReader dataReader = new InputStreamReader(in);
                JsonObject jsonObject = mGson.fromJson(dataReader, JsonObject.class);
                sexList = SexList.parseSexList(jsonObject);
                setPageOneData(sexList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public UnScrollViewPager.OnPageChangeListener onPageChangeListener = new UnScrollViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if (position == 1) {
                //第二页需要处理
                if (savedGender != null) {
                    savedGender.onClick(null);
                } else {
                    mViewController.setCurrentPage(0);
                }
            }
            if (position == 0) {
                makeData();
            }
        }
    };

}
