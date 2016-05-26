package com.zhongsou.souyue.ui.highlight;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.zhongsou.souyue.ui.highlight.util.HighlightViewUtils;
import com.zhongsou.souyue.ui.highlight.view.HighlightView;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description 新功能引导
 * @date 2015/12/25
 */
public class Highlight {
    public static Highlight instance;
    private Highlight nextHighlight;
    public static String SHOW_TIP_NAME = "isShowHighlight";
    private ClickHighlight mClickHighlight;

    public interface ClickHighlight {
        void onClickHighlight();
    }

    public static Highlight getInstance(Context context) {
        if (instance == null) {
            instance = new Highlight(context);
        }
        return instance;
    }

    public static class ViewPosInfo {
        public int layoutId = -1;
        public RectF rectF; //高亮View的值
        public MarginInfo marginInfo;   //提示布局的边距
        public View view;   //高亮View
        public OnPosCallback onPosCallback;
    }

    public static class MarginInfo {
        public float topMargin;
        public float leftMargin;
        public float rightMargin;
        public float bottomMargin;

    }

    public static interface OnPosCallback {
        void getPos(float rightMargin, float bottomMargin, RectF rectF, MarginInfo marginInfo);
    }

    private View mAnchor;
    private List<ViewPosInfo> mViewRects;
    private Context mContext;
    private HighlightView mHighlightView;

    private boolean intercept = true;
    private boolean shadow = true;
    private int maskColor = 0xCC000000;

    public Highlight(Context context) {
        mContext = context;
        mViewRects = new ArrayList<ViewPosInfo>();
        mAnchor = ((Activity) mContext).findViewById(android.R.id.content);
    }

    /**
     * 设置蒙板的view
     *
     * @param anchor
     * @return
     */
    public Highlight anchor(View anchor) {
        mAnchor = anchor;
        return this;
    }

    public Highlight intercept(boolean intercept) {
        this.intercept = intercept;
        return this;
    }

    /**
     * 是否有虚化边框效果
     *
     * @param shadow true 有  ； false 没有
     * @return
     */
    public Highlight shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    /**
     * 蒙板颜色 必须是带透明度的颜色（8位）
     *
     * @param maskColor 默认值0xCC000000
     * @return
     */
    public Highlight maskColor(int maskColor) {
        this.maskColor = maskColor;
        return this;
    }

    public void updateInfo() {
        ViewGroup parent = (ViewGroup) mAnchor;
        for (ViewPosInfo viewPosInfo : mViewRects) {
            RectF rect = new RectF(HighlightViewUtils.getLocationInView(parent, viewPosInfo.view));
            viewPosInfo.rectF = rect;
            viewPosInfo.onPosCallback.getPos(
                    parent.getWidth() - rect.right,
                    parent.getHeight() - rect.bottom,
                    rect,
                    viewPosInfo.marginInfo);
        }
    }

    /**
     * 添加需要提示的View 并设置提示的Layout并设置提示位置
     *
     * @param view          需要提示的view
     * @param decorLayoutId 提示的布局
     * @param onPosCallback 位置设置回调接口
     * @return
     */
    public Highlight addHighlight(View view, int decorLayoutId, OnPosCallback onPosCallback) {
        ViewGroup parent = (ViewGroup) mAnchor;
        RectF rect = new RectF(HighlightViewUtils.getLocationInView(parent, view));
        Log.d("Highlight", "parent : " + parent);  //test
        Log.d("Highlight", "view : " + view);   //test
        ViewPosInfo viewPosInfo = new ViewPosInfo();
        viewPosInfo.layoutId = decorLayoutId;
        viewPosInfo.rectF = rect;
        viewPosInfo.view = view;
        if (onPosCallback == null && decorLayoutId != -1) {
            throw new IllegalArgumentException("onPosCallback can not be null.");
        }
        MarginInfo marginInfo = new MarginInfo();
        onPosCallback.getPos(parent.getWidth() - rect.right, parent.getHeight() - rect.bottom, rect, marginInfo);
        viewPosInfo.marginInfo = marginInfo;
        viewPosInfo.onPosCallback = onPosCallback;
        mViewRects.add(viewPosInfo);

        return this;
    }

    /**
     * 根据View的ID设置提示
     *
     * @param viewId
     * @param decorLayoutId
     * @param onPosCallback
     * @return
     */
    public Highlight addHighlight(int viewId, int decorLayoutId, OnPosCallback onPosCallback) {
        ViewGroup parent = (ViewGroup) mAnchor;
        View view = parent.findViewById(viewId);
        addHighlight(view, decorLayoutId, onPosCallback);
        return this;
    }


    /**
     * 显示提示
     */
    public void show() {

        if (mHighlightView != null) return;    //当前已有提示显示 则返回

        HighlightView highlightView = new HighlightView(mContext, this, maskColor, shadow, mViewRects); //初始化View
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                Gravity.CENTER_HORIZONTAL
//                );
//        highlightView.setLayoutParams(layoutParams);
        if (mAnchor.getClass().getSimpleName().equals("FrameLayout")) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) mAnchor).addView(highlightView, ((ViewGroup) mAnchor).getChildCount(), lp);

        } else {
            FrameLayout frameLayout = new FrameLayout(mContext);
            ViewGroup parent = (ViewGroup) mAnchor.getParent();
            parent.removeView(mAnchor);
            parent.addView(frameLayout, mAnchor.getLayoutParams());

            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayout.addView(mAnchor, lp);

            frameLayout.addView(highlightView);
        }

        if (intercept) {
            //点击调用的接口
            highlightView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(MainApplication.getInstance(), "===",Toast.LENGTH_SHORT).show();
                    if (nextHighlight != null) {
                        remove();
                        nextHighlight.show();
                    } else if (mClickHighlight != null) {
                        remove();   //先移除
                        mClickHighlight.onClickHighlight();
                    } else {
                        remove();
                    }
                }
            });
        }

        mHighlightView = highlightView;
    }

    /**
     * 移除提示
     */
    public void remove() {
        if (mHighlightView == null) return;
        ViewGroup parent = (ViewGroup) mHighlightView.getParent();
        if (parent instanceof RelativeLayout || parent instanceof FrameLayout) {
            parent.removeView(mHighlightView);
        } else {
            parent.removeView(mHighlightView);
            View origin = parent.getChildAt(0);
            ViewGroup graParent = (ViewGroup) parent.getParent();
            graParent.removeView(parent);
            graParent.addView(origin, parent.getLayoutParams());
        }
        mHighlightView = null;
    }

//    public static void invokeRemove() {
//        if (instance != null) {
//            instance.remove();
//        }
//    }

    public void setmClickHighlight(ClickHighlight mClickHighlight) {
        this.mClickHighlight = mClickHighlight;
    }

    public void setNextHighlight(Highlight nextHighlight) {
        this.nextHighlight = nextHighlight;
    }

    /**
     * @return 是否显示过了。
     */
    public static boolean hasShowTip() {
        return SYSharedPreferences.getInstance().getBoolean(SHOW_TIP_NAME, false);
    }
}