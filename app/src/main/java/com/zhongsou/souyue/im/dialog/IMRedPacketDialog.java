package com.zhongsou.souyue.im.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.view.Rotate3dAnimation;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MyImageLoader;

/**
 * @author zhangwenbin zhangwb@zhongsou.com
 * @version version_code (e.g, V5.1)
 * @Copyright (c) 2016 zhongsou
 * @Description IM拆红包dialog
 * @date 16/1/6
 */
public class IMRedPacketDialog extends Dialog implements View.OnClickListener {
    private Context mContext; // 上下文
    private static boolean isShowIng; // 弹窗是否正在上层
    private static IMRedPacketDialog mInstance; // 当前运的实例

    private String mShowText;
    private String mShowImageUrl;
    private String mJumpUrl;
    private String mRedPacketOwner;

    private RelativeLayout rlContent; // 主界面
    private ImageView ivDismantleRed;//拆红包硬币
    private ImageView ivHead;//头像
    private TextView tvRedOwner;//红包所属
    private TextView tvContent;//红包祝福

    //动画执行顺序监听
    private RedPacketAnimationListener mRedPacketAnimationListener;

    //弹弹动画
    private Animation mScaleAnimationOpen;
    private Animation mScaleAnimation;

    //每个动画翻转90度，切换动画
    private Rotate3dAnimation mRotateAnimation;

    //旋转动画时间
    private final int ROTATE_DURATION = 500;


    private IMRedPacketDialog(Context context, String showText, String showImageUrl, String jumpUrl, String redPacketOwner) {
        super(context, R.style.dialog_alert);
        mContext = context;
        mShowText = showText;
        mShowImageUrl = showImageUrl;
        mJumpUrl = jumpUrl;
        mRedPacketOwner = redPacketOwner;
    }


    /**
     * @param context
     */
    public static void showDialog(Context context, String showText, String showImageUrl, String jumpUrl, String redPacketOwner) {
        mInstance = new IMRedPacketDialog(context, showText, showImageUrl, jumpUrl, redPacketOwner);
        mInstance.show();
    }


    public static IMRedPacketDialog getInstance() {
        return mInstance;
    }

    /**
     * 是否正在显示
     *
     * @return
     */
    public static boolean getIsShowingMe() {
        return isShowIng;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_im_red_packet);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        mRedPacketAnimationListener = new RedPacketAnimationListener();
        initRotateAnimation();
        initView();

    }


    @Override
    public void show() {
        super.show();
        mScaleAnimationOpen = AnimationUtils.loadAnimation(mContext, R.anim.scale_dialog_enter);
        mScaleAnimationOpen.setAnimationListener(mRedPacketAnimationListener);
        rlContent.startAnimation(mScaleAnimationOpen);
        isShowIng = true;
    }

    /**
     * 初始化view
     */
    private void initView() {
        rlContent = (RelativeLayout) findViewById(R.id.rl_content);
        ivDismantleRed = (ImageView) findViewById(R.id.iv_dismantle_red);
        ivDismantleRed.setOnClickListener(this);
        ivHead = (ImageView) findViewById(R.id.iv_head);
        MyImageLoader.imageLoader.displayImage(mShowImageUrl, ivHead,
                MyImageLoader.Circleoptions);//给头像图片赋值
        tvRedOwner = (TextView) findViewById(R.id.tv_red_owner);
        tvRedOwner.setText(mRedPacketOwner + "的红包");
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvContent.setText(mShowText);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dismantle_red:     //拆红包
                ivDismantleRed.startAnimation(mRotateAnimation);
                break;
        }
    }

    @Override
    public void dismiss() {
        isShowIng = false;
        //防止出现一些诡异的异常。
        if (mContext != null && mContext instanceof Activity) {
            if (!((Activity) mContext).isFinishing()) {
                try {
                    super.dismiss();
                } catch (Exception e) {

                }
            }
        }
    }


    /**
     * 动画执行监听
     */
    class RedPacketAnimationListener implements
            android.view.animation.Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == mScaleAnimationOpen) {
                rlContent.startAnimation(mScaleAnimation);
            }
            if (animation == mScaleAnimation) {
                Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.scale_dialog);
                rlContent.startAnimation(animation1);
            }
            if (animation == mRotateAnimation){
                IntentUtil.gotoWeb(mContext,mJumpUrl, "interactWeb");
                dismiss();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

    }

    /**
     * @Title: initAnimation
     * @Description: (初始化翻转动画)
     */
    private void initRotateAnimation() {
        mRotateAnimation = new Rotate3dAnimation(0, 360, DeviceUtil.dip2px(mContext, 45), DeviceUtil.dip2px(
                mContext, 45), 0, true);
        mRotateAnimation.setDuration(ROTATE_DURATION);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setAnimationListener(mRedPacketAnimationListener);

        mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale_dialog_out);
        mScaleAnimation.setAnimationListener(mRedPacketAnimationListener);

    }
}
