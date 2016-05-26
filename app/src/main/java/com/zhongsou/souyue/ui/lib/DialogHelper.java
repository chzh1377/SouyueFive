package com.zhongsou.souyue.ui.lib;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.ui.lib.DialogPlus.OnBackPressListener;
import com.zhongsou.souyue.ui.lib.DialogPlus.OnCancelListener;
import com.zhongsou.souyue.ui.lib.DialogPlus.OnDismissListener;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.OnJSClickListener;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;

import java.util.ArrayList;

public class DialogHelper {

    private CustomWebView mWebView;
    private SearchResultItem sri;
    private DialogPlus dialog;
    private Holder holder;
    private Activity context;

    private static DialogHelper dialogHelper;

    private DialogHelper() {
    }

    public static DialogHelper getInstance() {
        if (dialogHelper == null) {
            dialogHelper = new DialogHelper();
        }
        return dialogHelper;
    }

    public void showDialog(final Activity context, final DialogPlus.ScreenType screenType, final JSClick jscClick) {
        this.context = context;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder = new DialogViewHolder(R.layout.content, screenType);
                dialog = new DialogPlus.Builder(context)
                        .setContentHolder(holder)
                        .setCancelable(true)
                        .setGravity(com.zhongsou.souyue.ui.lib.DialogPlus.Gravity.TOP)
                        .setOnDismissListener(dismissListener)
                        .setOnCancelListener(cancelListener)
                        .setOnBackPressListener(backPressListener)
                        .create();

                mWebView = (CustomWebView) dialog
                        .findViewById(R.id.searchwebView);
                mWebView.setWebViewClient(new WebViewClient());
                mWebView.setWebChromeClient(new WebChromeClient());
                mWebView.setOnJSClickListener(jsClickListener);
//					mWebView.getSettings().setUseWideViewPort(true);

                dialog.show();
                mWebView.loadUrl(jscClick.url());    //此处链接在浏览器中打开需要添加UA：souyue5.0
            }
        });
    }

    private void toSRIObj(final JSClick jsc) {// 转换成SearchResultItem对象
        if (null == sri)
            sri = new SearchResultItem();
        sri.title_$eq(jsc.title());
        sri.keyword_$eq(jsc.keyword());
        sri.srpId_$eq(jsc.srpId());
        sri.url_$eq(jsc.url());
        sri.md5_$eq(jsc.md5());
        ArrayList<String> t = new ArrayList<String>();
        t.add(jsc.image());
        sri.image_$eq(t);
        sri.description_$eq(jsc.description());
    }

    public DialogPlus getDialog() {
        return dialog;
    }

    public void setDialog(DialogPlus dialog) {
        this.dialog = dialog;
    }

    /**
     * 设置Js监听
     */
    OnJSClickListener jsClickListener = new OnJSClickListener() {
        @Override
        public void onJSClick(final JSClick jsc) {
            try {
                //未抽取
                toSRIObj(jsc);
                if (jsc.isGetfocus()) {
                    return;
                }
                if (!jsc.isShare()) {
                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            if (jsc.isOpenSearchDialog()) {
                                ImJump2SouyueUtil.IMAndWebJump(
                                        context, jsc, sri);
                                return;
                            } else if (jsc.isCloseSearchDialog()) {
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm.isActive()) {
                                    if (mWebView != null) {
                                        imm.hideSoftInputFromWindow(mWebView.getWindowToken(), 0);
                                    }
                                }
                                dismiss(dialog);
                                return;
                            } else {
                                dismiss(dialog);
                                ImJump2SouyueUtil.IMAndWebJump(
                                        context, jsc, sri);
                                return;
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    OnDismissListener dismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogPlus dialog) {
            dismiss(dialog);
        }
    };

    OnCancelListener cancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogPlus dialog) {
            dismiss(dialog);
        }
    };

    OnBackPressListener backPressListener = new OnBackPressListener() {
        @Override
        public void onBackPressed(DialogPlus dialog) {
            dismiss(dialog);
        }
    };

    private void dismiss(DialogPlus dialog) {
        dialog.dismiss();
    }
}
