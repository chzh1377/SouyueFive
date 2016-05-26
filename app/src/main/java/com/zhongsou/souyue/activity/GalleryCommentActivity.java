package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.module.GalleryCommentDetailItem;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.view.CommentBottomView;
import com.zhongsou.souyue.view.CommonCommitView;

/**
 * 评论页面。
 * 主要逻辑详见
 * CommonCommitView
 */
public class GalleryCommentActivity extends RightSwipeActivity implements CommonCommitView.LoadingListener, View.OnClickListener {


    private CommonCommitView view; // 抽取的评论列表页
    private ProgressBarHelper loadingBar; // loading..
    public static boolean needFinish = false; // 是否需要跳过此页面，新开的回复页面需要跳过此页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GalleryCommentDetailItem item = (GalleryCommentDetailItem) getIntent().getSerializableExtra("item");
        view = new CommonCommitView(this, item, this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initView();
    }

    private void initView(){
        setContentView(R.layout.gallerynews_comment_activity);
        findViewById(R.id.layout_option).setVisibility(View.GONE);
        findViewById(R.id.goBack).setOnClickListener(this); // 返回按键
        RelativeLayout mainContent = findView(R.id.main_content);
        mainContent.addView(view,RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        loadingBar = new ProgressBarHelper(this,findView(R.id.gallerynews_comment_loading));
        loadingBar.showLoading();
        loadingBar.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                view.reload();
                loadingBar.showLoading();
            }
        });

    }


    //是否需要直接finish掉当前的页面，在回复页面的回复页面时，可能要用到此s方法
    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
        if(needFinish){
            needFinish = false;
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        view.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLoadingFinished() {
        view.showComment();
        loadingBar.goneLoading();
        loadingBar.goneLoadingUI();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                CommentBottomView circleFollowDialog = view.getCircleFollowDialog();
                if(circleFollowDialog != null){
                    if(SYSharedPreferences.getInstance().getBoolean(SYSharedPreferences.IS_SHOW_KEYBOARD,false)){
                        //如果需要显示键盘
                        circleFollowDialog.showKeyboard();
                        SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.IS_SHOW_KEYBOARD,false);
                    }
                }
            }
        },50);
    }

    @Override
    public void onLoadingError() {
        loadingBar.showNetError();
    }

    @Override
    public void onLoadingAll() {
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
