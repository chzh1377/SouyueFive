/*
 Copyright (c) 2013 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zhongsou.souyue.ui.gallery;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.zhongsou.souyue.ui.gallery.touchview.UrlTouchImageView;
import com.zhongsou.souyue.ui.gallery.touchview.UrlTouchImageView.DownLoadLinstener;

import java.util.List;


/**
 * Class wraps URLs to adapter, then it instantiates {@link UrlTouchImageView} objects to paging up through them.
 */
public class UrlPagerAdapter extends PagerAdapter {
    private List<String> mResources;
    private Context mContext;
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    public String getCurrentIv(int pos) {
        return null;
    }

    public UrlPagerAdapter(Context context, List<String> resources) {
        this.mResources = resources;
        this.mContext = context;
        mShortAnimationDuration = mContext.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        UrlTouchImageView iv = new UrlTouchImageView(mContext);
        iv.setDownLoadLinstener((DownLoadLinstener) mContext);
        iv.setBgUrl(mResources.get(position));
//        iv.setUrl(replaceUrl(mResources.get(position)));
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        collection.addView(iv, 0);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).finish();
            }
        });
        return iv;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        ((UrlTouchImageView) object).recyle();
        container.removeView((View) object);
    }

    public String getItem(int pos) {
        return mResources.get(pos);
    }
}
