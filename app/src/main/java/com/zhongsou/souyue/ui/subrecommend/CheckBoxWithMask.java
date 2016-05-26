package com.zhongsou.souyue.ui.subrecommend;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.DeviceUtil;

/**
 * Created by zyw on 2015/12/16.
 */
public class CheckBoxWithMask extends FrameLayout {

    private Context mContext;
    private int mMaskColor; // 默认遮罩颜色
    private int mTextColor; // 默认字体颜色
    private String mTips; // 描述文本
    //    private int marginTextAndImage = 0;
    private boolean mIschecked = false;
    private ImageView ivBackGround;
    private ImageView ivMask;
    private TextView tv;
    private OnCheckedStateChangeListener mOnCheckedChangeListener;
    private final RelativeLayout mContent;
    //    private int width;
//    private int height;

    public CheckBoxWithMask(Context context, String tips) {
        super(context);
        mContext = context;
        this.mMaskColor = 0x99ffffff;
        this.mTips = tips;
        mContent = (RelativeLayout) View.inflate(context, R.layout.checkbox_withtips_layout, null);
        this.addView(mContent, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        initView();
    }

//    public CheckBoxWithMask(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public CheckBoxWithMask(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxWithMask);
//        mMaskColor = typedArray.getColor(R.styleable.CheckBoxWithMask_maskcolor, 0x99ffffff);
//        mTextColor = typedArray.getColor(R.styleable.CheckBoxWithMask_tipscolor, 0x00000000);
//        mTips = typedArray.getString(R.styleable.CheckBoxWithMask_tipstext);
//        this.mContext = context;
//        setPageOne();
//    }

    private void initView() {
        //check box
        ivBackGround = (ImageView) mContent.findViewById(R.id.checkbox_withtips_ivbg);
        ivBackGround.setBackgroundColor(Color.TRANSPARENT);
        ivMask = (ImageView) mContent.findViewById(R.id.checkbox_withtips_iv);
        //text view
        tv = (TextView) mContent.findViewById(R.id.checkbox_withtips_tv);
        tv.setText(mTips);
        tv.setGravity(Gravity.BOTTOM);
        tv.setTextColor(mTextColor);
        ivMask.setEnabled(mIschecked);
        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{-android.R.attr.state_enabled}, getResources().getDrawable(R.drawable.circle_mask));
        sld.addState(StateSet.WILD_CARD, new ColorDrawable(Color.TRANSPARENT));
        ivMask.setBackgroundDrawable(sld);
//        this.addView(ivBackGround);
//        this.addView(ivMask);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
//        this.addView(tv);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setTextColor(getResources().getColor(R.color.middark_black));
                mIschecked = true;
                ivMask.setEnabled(mIschecked);
                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onClicked(CheckBoxWithMask.this.getTag());
                }
            }
        });
    }

    public void setOnCheckedChangeListener(OnCheckedStateChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    public void setCheckState(boolean state) {
        mIschecked = state;
        ivMask.setEnabled(mIschecked);
        if (mIschecked) {
            tv.setTextColor(getResources().getColor(R.color.middark_black));
        } else {
            tv.setTextColor(getResources().getColor(R.color.checkboxwithmask_uncheckedtext));
        }
        invalidate();
    }

    public void setButtonBackGround(int imageResId) {
        ivBackGround.setBackgroundResource(imageResId);
    }

    public void setButtonBackGround(Drawable backGround) {
        ivBackGround.setImageDrawable(backGround);
    }

    public void setButtonBackGroundColor(int color) {
        ivBackGround.setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        tv.setTextColor(color);
        invalidate();
    }

    public void setTextSize(int textSize) {
        tv.setTextSize(textSize);
        invalidate();
    }

    public void setText(String text) {
        if (tv != null) {
            tv.setText(text);
            invalidate();
        }
    }


    public boolean getChecked() {
        return mIschecked;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        width = MeasureSpec.getSize(widthMeasureSpec);
//        height = MeasureSpec.getSize(heightMeasureSpec);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

    public ImageView getImageViewBackGround() {
        return ivBackGround;
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        LayoutParams layoutParamsCb = (LayoutParams) ivBackGround.getLayoutParams();
//        layoutParamsCb.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        layoutParamsCb.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        layoutParamsCb.bottomMargin = DeviceUtil.dip2px(getContext(),20);
//        layoutParamsCb.gravity = Gravity.CENTER_HORIZONTAL;
//
//        LayoutParams layoutParamsCb1 = (LayoutParams) ivMask.getLayoutParams();
//        layoutParamsCb1.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        layoutParamsCb1.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        layoutParamsCb1.bottomMargin = DeviceUtil.dip2px(getContext(),20);
//        layoutParamsCb1.gravity = Gravity.CENTER_HORIZONTAL;
//
//        LayoutParams layoutParamsTv = (LayoutParams) tv.getLayoutParams();
//        layoutParamsTv.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        layoutParamsTv.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        layoutParamsTv.setMargins(0,height/4*3 + DeviceUtil.dip2px(getContext(),3),0,0);
//        layoutParamsTv.gravity = Gravity.CENTER_HORIZONTAL;
//        invalidate();
//        super.onLayout(changed, left, top, right, bottom);
//    }

    static interface OnCheckedStateChangeListener {
        public void onClicked(Object tag);
    }
}
