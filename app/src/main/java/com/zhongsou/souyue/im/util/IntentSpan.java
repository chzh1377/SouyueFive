package com.zhongsou.souyue.im.util;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 *
 * 点击URL时，打开浏览器
 * gengsong
 *
 */
public class IntentSpan extends ClickableSpan implements ParcelableSpan {
    private Intent mIntent;

    public IntentSpan(Intent toActivity) {
        mIntent = toActivity;
    }

    @Override
    public void onClick(View sourceView) {
        Context context = sourceView.getContext();
        context.startActivity( mIntent );
    }

    @Override
    public int getSpanTypeId() {
        return 100;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        // don't write to parcel
    }

    public Intent getIntent() {
        return mIntent;
    }
}

