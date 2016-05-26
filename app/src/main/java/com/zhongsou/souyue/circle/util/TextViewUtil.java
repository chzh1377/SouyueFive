package com.zhongsou.souyue.circle.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.emoji.EmojiPattern;

/**
 * Created by wlong on 14-5-19.
 */
public class TextViewUtil {

    public static void setTextWithHost(Context context, TextView tv, String nickName, String content, boolean ishost ){
        if (!ishost) {
            tv.setText(Html.fromHtml("<font color=\"#0xFFA9A9A9\">" + nickName + "：" + "</font>" + content));
        } else {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Drawable louzhu = context.getResources().getDrawable(R.drawable.circle_louzhu1212);
            louzhu.setBounds(0, 0, louzhu.getIntrinsicWidth(), louzhu.getIntrinsicHeight());
            ImageSpan louzhuSpan = new ImageSpan(louzhu);

            Spanned spanned = Html.fromHtml("<font color=\"#0xFFA9A9A9\">" + nickName + " _@*@_ ：" + "</font>" + content);
            builder.insert(0, spanned);

            builder.setSpan(louzhuSpan, spanned.toString().indexOf("_@*@_"), spanned.toString().indexOf("_@*@_")+5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv.setText(builder);
        }
    }
    
    public static void setTextWithHostAndTime(Context context, TextView tv, String nickName, String content,String time){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString msp = new SpannableString(time);
        int timeSize = time.length();
        int gaycolor = 0xFFA9A9A9;
        msp.setSpan(new RelativeSizeSpan(0.85f), 0, timeSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //0.5f表示默认字体大小的一半
        msp.setSpan(new ForegroundColorSpan(gaycolor), 0, timeSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Spanned spanned = Html.fromHtml("<font color=\"#0xFFA9A9A9\">" + nickName + "：" + "</font>");
        builder.insert(0, spanned);
        SpannableString span = EmojiPattern.getInstace().getExpressionString(context, content.replaceAll("(\r\n|\n)+", " "));
        builder.append(span);
        builder.append("  ");
        builder.append(msp);
        tv.setText(builder, BufferType.SPANNABLE);
    }

    public static void setTextPostsReward(TextView tv, String rewards ,String str1,String str2){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        Spanned spanned1 = Html.fromHtml("<font color=\"#4d4d4d\">" + rewards  + "</font>");
        Spanned spanned0 = Html.fromHtml("<font color=\"#7e7e7e\">" + str1  + "</font>");
        Spanned spanned2 = Html.fromHtml("<font color=\"#7e7e7e\">" + str2  + "</font>");
        builder.insert(0, spanned0);
        builder.append(spanned1);
        builder.append(spanned2);
        tv.setText(builder, BufferType.SPANNABLE);
    }
}
