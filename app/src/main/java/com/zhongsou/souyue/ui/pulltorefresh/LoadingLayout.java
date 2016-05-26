/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.zhongsou.souyue.ui.pulltorefresh;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;

public class LoadingLayout extends AbstractLoadingLayout {

//    private final ImageView mHeaderImage;

//    private final TextView mHeaderText;
//    private final TextView mSubHeaderText;

    private String mPullLabel;
    private String mRefreshingLabel;
    private String mReleaseLabel;
    private String mLastTime;

//    private LinearLayout header_text;
//    private LinearLayout imagelayout;

    Context context;

    private ImageView arrowImageView;

    private LinearLayout progressBar, head_arrow_layout;

    private TextView tipsTextview;

    private TextView lastUpdatedTextView;

    private RotateAnimation animation;

    private RotateAnimation reverseAnimation;
    
    public LoadingLayout(Context context){
        super(context);
    }

    public LoadingLayout(Context context, final PullToRefreshBase.Mode mode, TypedArray attrs) {
        super(context);
        this.context = context;
//        ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this);
//        mHeaderText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
//        mSubHeaderText = (TextView) header.findViewById(R.id.pull_to_refresh_sub_text);
//        mHeaderImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
//        header_text = (LinearLayout) header.findViewById(R.id.header_text);
//        imagelayout = (LinearLayout) header.findViewById(R.id.imagelayout);
//        
//        mHeaderImage.setScaleType(ScaleType.MATRIX);

        ViewGroup headerView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.head, this);
        arrowImageView = (ImageView) headerView.findViewById(R.id.head_arrowImageView);
        arrowImageView.setMinimumWidth(50);
        arrowImageView.setMinimumHeight(50);
        progressBar = (LinearLayout) headerView.findViewById(R.id.head_progressBar);
        tipsTextview = (TextView) headerView.findViewById(R.id.head_tipsTextView);
        lastUpdatedTextView = (TextView) headerView.findViewById(R.id.head_lastUpdatedTextView);
        head_arrow_layout = (LinearLayout) headerView.findViewById(R.id.head_arrow_layout);

        animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

//        switch (mode) {
//            case PULL_UP_TO_REFRESH:
//                // Load in labels
//                mPullLabel = context.getString(R.string.pull_to_refresh_from_bottom_pull_label);
//                mRefreshingLabel = context.getString(R.string.pull_to_refresh_from_bottom_refreshing_label);
//                mReleaseLabel = context.getString(R.string.pull_to_refresh_from_bottom_release_label);
//                break;
//
//            case PULL_DOWN_TO_REFRESH:
//            default:
//                // Load in labels
//                mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
//                mRefreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
//                mReleaseLabel = context.getString(R.string.pull_to_refresh_release_label);
//
//
//                break;
//        }
//
//        if (attrs.hasValue(R.styleable.PullToRefreshListView_ptrHeaderTextColor)) {
//            ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefreshListView_ptrHeaderTextColor);
//            setTextColor(null != colors ? colors : ColorStateList.valueOf(0xFF000000));
//        }
//        if (attrs.hasValue(R.styleable.PullToRefreshListView_ptrHeaderSubTextColor)) {
//            ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefreshListView_ptrHeaderSubTextColor);
//            setSubTextColor(null != colors ? colors : ColorStateList.valueOf(0xFF000000));
//        }
//        if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
//            Drawable background = attrs.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
//            if (null != background) {
//                setBackgroundDrawable(background);
//            }
//        }

        reset(false);
    }

    public void reset(boolean _isShowText) {
//        mHeaderText.setText(wrapHtmlLabel(mPullLabel));
//        header_text.setVisibility(View.VISIBLE);
////        mHeaderImage.setVisibility(View.GONE);
//        imagelayout.setVisibility(View.GONE);
//        mHeaderImage.clearAnimation();
//
//
//        if (TextUtils.isEmpty(mSubHeaderText.getText())) {
//            mSubHeaderText.setVisibility(View.GONE);
//        } else {
//            mSubHeaderText.setVisibility(View.VISIBLE);
//        }
        head_arrow_layout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        arrowImageView.clearAnimation();
        arrowImageView.setImageResource(R.drawable.arrow);
        if (!_isShowText){
            tipsTextview.setText("");
        }else {
            if (mPullLabel != null)
                tipsTextview.setText(mPullLabel);
            else
                tipsTextview.setText(R.string.drop_down);
        }
        tipsTextview.setPadding(0, 0, 0, 0);
    }

    public void releaseToRefresh() {
//        mHeaderText.setText(wrapHtmlLabel(mReleaseLabel));
//        if (SettingUtility.getEnableSound()) {
//            final MediaPlayer mp = MediaPlayer.create(context, R.raw.psst1);
//            mp.start();
//        }
        head_arrow_layout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tipsTextview.setVisibility(View.VISIBLE);
        // lastUpdatedTextView.setVisibility(View.VISIBLE);

        arrowImageView.clearAnimation();
        arrowImageView.startAnimation(animation);

        if (mReleaseLabel != null){
            tipsTextview.setText(mReleaseLabel);
        }else {
            tipsTextview.setText(R.string.release_update);
        }
    }

    public void pullToRefresh() {
//        mHeaderText.setText(wrapHtmlLabel(mPullLabel));
//        if (SettingUtility.getEnableSound()) {
//            final MediaPlayer mp = MediaPlayer.create(context, R.raw.pop);
//            mp.start();
//        }

        progressBar.setVisibility(View.GONE);
        tipsTextview.setVisibility(View.VISIBLE);
        // lastUpdatedTextView.setVisibility(View.VISIBLE);
        arrowImageView.clearAnimation();
        head_arrow_layout.setVisibility(View.VISIBLE);
        // 是由RELEASE_To_REFRESH状态转变来的
        if (mPullLabel != null){
            tipsTextview.setText(mPullLabel);
        }else {
            tipsTextview.setText(R.string.drop_down);
        }
//        tipsTextview.setText(R.string.drop_down);
        arrowImageView.clearAnimation();
        arrowImageView.startAnimation(reverseAnimation);
    }

//    public void setPullLabel(String pullLabel) {
////        mPullLabel = pullLabel;
//    }


    /**
     * 改下拉刷新文本
     * @param pullLabel
     */
    @Override
    protected void setPullLabel(String pullLabel) {
        this.mPullLabel = pullLabel;
    }

    /**
     * 改正在刷新文本
     * @param refreshingLabel
     */
    @Override
    protected void setRefreshingLabel(String refreshingLabel) {
        this.mRefreshingLabel = refreshingLabel;
    }

    /**
     * 改释放立即刷新文本
     * @param releaseLabel
     */
    @Override
    protected void setReleaseLabel(String releaseLabel) {
        this.mReleaseLabel = releaseLabel;
    }


    public void refreshing() {
//        header_text.setVisibility(View.GONE);
//        imagelayout.setVisibility(View.VISIBLE);
////        mHeaderImage.setVisibility(View.VISIBLE);
//        mHeaderText.setText(wrapHtmlLabel(mRefreshingLabel));
//        mHeaderImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pull_refresh));
//
//        mSubHeaderText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        arrowImageView.clearAnimation();
        head_arrow_layout.setVisibility(View.GONE);
        if (mRefreshingLabel != null){
            tipsTextview.setText(mRefreshingLabel);
        }else {
            tipsTextview.setText(R.string.loading);
        }
        if (null != lastUpdatedTextView && mLastTime != null) {
            lastUpdatedTextView.setText(mLastTime);
            lastUpdatedTextView.setVisibility(View.VISIBLE);
        }

//		tipsTextview.setPadding(0, 20, 0, 0);
    }

    @Override
    protected void setRefreshTime(String text) {
        this.mLastTime = text;
        if (null != lastUpdatedTextView) {
            lastUpdatedTextView.setText(text);
            lastUpdatedTextView.setVisibility(View.VISIBLE);
        }
    }

//    public void setRefreshingLabel(String refreshingLabel) {
////        mRefreshingLabel = refreshingLabel;
//    }
//
//    public void setReleaseLabel(String releaseLabel) {
////        mReleaseLabel = releaseLabel;
//    }


    public void setTextColor(ColorStateList color) {
//        mHeaderText.setTextColor(color);
//        mSubHeaderText.setTextColor(color);
    }

    public void setSubTextColor(ColorStateList color) {
//        mSubHeaderText.setTextColor(color);
    }

    public void setTextColor(int color) {
        setTextColor(ColorStateList.valueOf(color));
    }


    public void setSubTextColor(int color) {
        setSubTextColor(ColorStateList.valueOf(color));
    }

    public void setSubHeaderText(CharSequence label) {
//        if (TextUtils.isEmpty(label)) {
//            mSubHeaderText.setVisibility(View.GONE);
//        } else {
//            mSubHeaderText.setText(label);
//            mSubHeaderText.setVisibility(View.VISIBLE);
//        }
    }

    public void onPullY(float scaleOfHeight) {
//        mHeaderImageMatrix.setRotate(scaleOfHeight * 90, mRotationPivotX, mRotationPivotY);
//        mHeaderImage.setImageMatrix(mHeaderImageMatrix);
    }


//    private CharSequence wrapHtmlLabel(String label) {
//        if (!isInEditMode()) {
//            return Html.fromHtml(label);
//        } else {
//            return label;
//        }
//    }
}
