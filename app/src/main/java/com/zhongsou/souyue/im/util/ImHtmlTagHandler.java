package com.zhongsou.souyue.im.util;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import org.xml.sax.XMLReader;

/**
 * Created by zoulu
 * on 2014/12/9
 * Description:自定义html标签解析器  用于html跳转
 */
public class ImHtmlTagHandler implements Html.TagHandler{
    private int sIndex = 0;
    private  int eIndex=0;
    private final Context mContext;
    public static final String IMTAG = "im_html";

    public ImHtmlTagHandler(Context context){
        mContext=context;
    }

    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        // TODO Auto-generated method stub
        if (tag.toLowerCase().equals("im_html")) {
            if (opening) {
                sIndex = output.length();
            }else {
                eIndex =  output.length();
                output.setSpan(new MxgsaSpan(output.toString().substring(sIndex,eIndex)),sIndex,eIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }


    private class MxgsaSpan extends ClickableSpan implements View.OnClickListener {
        private String content ;
        @Override
        public void onClick(View widget) {
            // TODO Auto-generated method stub
            //具体代码，可以是跳转页面
            Intent intent = new Intent(mContext, WebSrcViewActivity.class);
            if(content.contains("www") && !content.contains("http") && !content.contains("ftp")) {
                intent.putExtra(WebSrcViewActivity.PAGE_URL, "http://"+content);
            } else {
                intent.putExtra(WebSrcViewActivity.PAGE_URL, content);
            }
            intent.putExtra(WebSrcViewActivity.PAGE_TYPE, "nopara");
            mContext.startActivity(intent);
        }
        public MxgsaSpan(String str){
            this.content = str;
        }
    }
}
