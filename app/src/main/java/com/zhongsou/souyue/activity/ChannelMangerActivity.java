package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.DragAdapter;
import com.zhongsou.souyue.adapter.WaitSelectAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.module.ChannelItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.sub.ChannelListRequest;
import com.zhongsou.souyue.net.sub.SubChannelUpdateRequest;
import com.zhongsou.souyue.net.volley.ChannelMangerHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.view.DragGrid;
import com.zhongsou.souyue.view.WaitSelectGridView;

import java.util.ArrayList;
import java.util.List;

public class ChannelMangerActivity extends BaseActivity implements OnItemClickListener, ProgressBarHelper.ProgressBarClickListener {
    private DragGrid userGridView;
    private WaitSelectGridView waitSelectGridView;
    private TextView activity_bar_title;
    private LinearLayout daixuan_layout;
    private ProgressBarHelper pbHelp;
    private DragAdapter userAdapter;
    private WaitSelectAdapter waitSelectAdapter;
    private List<ChannelItem> tmpUserChannelList;
    private List<ChannelItem> channelListLocal;   //本地保存的待选list
//    private List<ChannelItem> channelListSever;   //服务器的待选list
    private List<ChannelItem> userChannelList;
    boolean isMove = false;
    private boolean isHasAdd = false;

    public static final int HTTP_GET_CHANNAL_MANNGER = 200;
    public static final int HTTP_SAVE_CHANNAL_MANNGER = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_mannger_activity);
        initView();
        initData();
        setData();
        getChannelList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {

        tmpUserChannelList = new ArrayList<ChannelItem>();
        userChannelList = new ArrayList<ChannelItem>();
        channelListLocal = new ArrayList<ChannelItem>();
        userAdapter = new DragAdapter(this, userChannelList);
        userGridView.setAdapter(userAdapter);
        userGridView.setStartChangeListener(new OnChangeListener() {
            @Override
            public void onChange(Object obj) {
                waitSelectGridView.setVisibility(View.GONE);
                daixuan_layout.setVisibility(View.GONE);
            }
        });
        userGridView.setStopChangeListener(new OnChangeListener() {
            @Override
            public void onChange(Object obj) {
                waitSelectGridView.setVisibility(View.VISIBLE);
                daixuan_layout.setVisibility(View.VISIBLE);
            }
        });
        waitSelectAdapter = new WaitSelectAdapter(this, channelListLocal);
        waitSelectGridView.setAdapter(this.waitSelectAdapter);
        waitSelectGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
    }

    private void initView() {
        userGridView = (DragGrid) findViewById(R.id.userGridView);
        waitSelectGridView = (WaitSelectGridView) findViewById(R.id.otherGridView);
        activity_bar_title = (TextView) findViewById(R.id.activity_bar_title);
        activity_bar_title.setText(R.string.yaowen_title);
        daixuan_layout = (LinearLayout)findViewById(R.id.daixuan_layout);
        pbHelp = new ProgressBarHelper(this,findViewById(R.id.ll_data_loading));
        pbHelp.setProgressBarClickListener(this);
    }

    private void setData(){
        String strUser = SYSharedPreferences.getInstance().getString("userChannelList", "");
        String strOther =  SYSharedPreferences.getInstance().getString("channelListLocal", "");
        if(!TextUtils.isEmpty(strUser) && !strUser.equals("[]") && !strUser.equals("null")){
//            userChannelList = JSON.parseArray(strUser, ChannelItem.class);
            userChannelList = new Gson().fromJson(strUser,new TypeToken<List<ChannelItem>>() {}.getType());
            for(int i = 0 ; i< userChannelList.size(); i++){
                tmpUserChannelList.add(userChannelList.get(i));
            }
            isHasAdd = true;
            pbHelp.goneLoading();
        }
        if(!TextUtils.isEmpty(strOther) && !strOther.equals("[]") && !strOther.equals("null")){
//            channelListLocal = JSON.parseArray(strOther, ChannelItem.class);
            channelListLocal = new Gson().fromJson(strOther,new TypeToken<List<ChannelItem>>() {}.getType());
        }
        userAdapter.setListDate(userChannelList);
        waitSelectAdapter.setListDate(channelListLocal);
        userAdapter.notifyDataSetChanged();
        waitSelectAdapter.notifyDataSetChanged();
    }

    private void getChannelList(){
    	ChannelListRequest request = new ChannelListRequest(HttpCommon.SUB_CHANNAL_LIST_REQUEST, this);
    	request.setParams("");
    	mMainHttp.doRequest(request);
//        channelMangerHttp.getChannelList(HTTP_GET_CHANNAL_MANNGER,"",this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        if (isMove) {
            return;
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                if (position != 0) {
                    isMove = true;
                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final ChannelItem channel = ((DragAdapter) parent.getAdapter()).getItem(position);
                        waitSelectAdapter.setVisible(false);
                        waitSelectAdapter.addItem(channel,0);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    waitSelectGridView.getChildAt(waitSelectGridView.getFirstVisiblePosition()).getLocationInWindow(endLocation);
                                    moveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                    userAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                }
                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    isMove = true;
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ChannelItem channel = ((WaitSelectAdapter) parent.getAdapter()).getItem(position);
                    userAdapter.setVisible(false);
                    userAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                moveAnim(moveImageView, startLocation, endLocation, channel, waitSelectGridView);
                                waitSelectAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    private void moveAnim(View moveView, int[] startLocation, int[] endLocation, final ChannelItem moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        moveView.getLocationInWindow(initLocation);
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                if (clickGridView instanceof DragGrid) {
                    waitSelectAdapter.setVisible(true);
                    waitSelectAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    waitSelectAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    @Override
    public void onBackPressed() {
        if(isChange()){
        	SubChannelUpdateRequest request = new SubChannelUpdateRequest(HttpCommon.SUB_SAVE_CHANNAL_REQUEST, this);
        	request.setParams(getHasSelectedId());
        	mMainHttp.doRequest(request);
//            channelMangerHttp.editChannel(HTTP_SAVE_CHANNAL_MANNGER, getHasSelectedId(), this);
        }else{
            finishWithIsChange();
        }
    }

    //判断已选列表一进去的时候和退出的时候是否相同
    private boolean isChange() {
        boolean isChange = false;
        if (tmpUserChannelList.size() != userChannelList.size()) {
            isChange = true;
        } else {
            for (int i = 0; i < tmpUserChannelList.size(); i++) {
                if (!tmpUserChannelList.get(i).getchannelId().equals(userChannelList.get(i).getchannelId())) {
                    isChange = true;
                    break;
                }
            }
        }
        return isChange;
    }

    private String getHasSelectedId(){
        int size = userChannelList.size();
        String str = "";
        for(int i = 0 ; i< size ; i++){
            if(i == size - 1){
                str += userChannelList.get(i).getchannelId();
            }else{
                str += userChannelList.get(i).getchannelId()+",";
            }
        }
        return str;
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        HttpJsonResponse response = (HttpJsonResponse) _request.getResponse();
        int id = _request.getmId();
        switch (id){
            case HttpCommon.SUB_CHANNAL_LIST_REQUEST:
                pbHelp.goneLoading();
                userChannelList = new Gson().fromJson(response.getBody().getAsJsonArray("selectedChannel"),new TypeToken<List<ChannelItem>>() {}.getType());
                if(!isHasAdd){
                    for(int i = 0 ; i< userChannelList.size(); i++){
                        tmpUserChannelList.add(userChannelList.get(i));
                    }
                }
                channelListLocal = new Gson().fromJson(response.getBody().getAsJsonArray("recomChannel"),new TypeToken<List<ChannelItem>>() {}.getType());
                userAdapter.setListDate(userChannelList);
                userAdapter.notifyDataSetChanged();
                waitSelectAdapter.setListDate(channelListLocal);
                waitSelectAdapter.notifyDataSetChanged();
                break;
            case HttpCommon.SUB_SAVE_CHANNAL_REQUEST:
                SouYueToast.makeText(ChannelMangerActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                finishWithIsChange();
                break;
        }

    }

    private void finishWithIsChange() {
        String strUser = new Gson().toJson(userChannelList);
        SYSharedPreferences.getInstance().putString("userChannelList",strUser);
        String str1 = new Gson().toJson(channelListLocal);
        SYSharedPreferences.getInstance().putString("channelListLocal",str1);
        Intent intent = new Intent();
        intent.putExtra("isChange",isChange());
        setResult(IntentUtil.RESULT_CODE_CHANNEL_MANNGER, intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
    }

    @Override
    public void onHttpError(IRequest _request) {
        int id = _request.getmId();
        switch (id) {
            case HttpCommon.SUB_CHANNAL_LIST_REQUEST:
                if (pbHelp.isLoading) {
                    pbHelp.goneLoading();
                }
                if (!isHasAdd) {
                    pbHelp.showNetError();
                }
                break;
            case HttpCommon.SUB_SAVE_CHANNAL_REQUEST:
                SouYueToast.makeText(ChannelMangerActivity.this,"修改失败",Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    @Override
    public void clickRefresh() {
        getChannelList();
    }
}
