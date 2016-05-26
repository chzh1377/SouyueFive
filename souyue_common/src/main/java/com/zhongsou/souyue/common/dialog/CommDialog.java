package com.zhongsou.souyue.common.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.zhongsou.souyue.common.R;

/**
 * 搜悦公共弹框类
 * Created by lvqiang on 15/9/10.
 */
public class CommDialog {


    private final static int BUTTON_BOTTOM = 9;
    private final static int BUTTON_TOP    = 9;

    private boolean                   mCancel;
    private Context mContext;
    private AlertDialog               mAlertDialog;
    private Builder    mBuilder;
    private View mView;
    private int                       mTitleResId;
    private int                       mTitleColor;
    private CharSequence              mTitle;
    private int                       mMessageResId;
    private CharSequence              mMessage;
    private CharSequence mPositiveText,mNevigationText;
    private View.OnClickListener mPositiveListener,mNevigationListener;
    private LinearLayout.LayoutParams mLayoutParams;
    private boolean mHasShow = false;
    private Drawable mBackgroundDrawable;
    private int                               mBackgroundResId;
    private View                              mMessageContentView;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private int                       mWindowFlag;
    private int                       mMiddleLineColor;
    protected Button mPositiveButton;
    protected ImageView mMiddleLine;
    protected Button mNevigateButton;

    public CommDialog(Context context) {
        this.mContext = context;
    }

    /**
     * 在屏幕上显示
     */
    public void show() {
        if (mHasShow == false) {
            mBuilder = new Builder();
//            mAlertDialog.show();
        }else
            mAlertDialog.show();
        mHasShow = true;
    }

    /**
     *
     * @param flag 如果是系统弹框需要设置 WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
     */
    public CommDialog setFlag(int flag){
        mWindowFlag = flag;
        if (mBuilder != null) {
            mBuilder.setFlag(flag);
        }
        return this;
    }

    /**
     * 设置整个view的
     * @param view 定制view
     * @return
     */
    public CommDialog setView(View view) {
        mView = view;
        if (mBuilder != null) {
            mBuilder.setView(view);
        }
        return this;
    }

    /**
     * 设置显示提示的view
     * @param view
     * @return
     */
    public CommDialog setContentView(View view) {
        mMessageContentView = view;
        if (mBuilder != null) {
            mBuilder.setContentView(mMessageContentView);
        }
        return this;
    }

    /**
     * 设置背景
     * @param drawable drawble资源
     * @return 返回自己
     */
    public CommDialog setBackground(Drawable drawable) {
        mBackgroundDrawable = drawable;
        if (mBuilder != null) {
            mBuilder.setBackground(mBackgroundDrawable);
        }
        return this;
    }

    /**
     * 设置背景
     * @param resId 资源id
     * @return 返回自己
     */
    public CommDialog setBackgroundResource(int resId) {
        mBackgroundResId = resId;
        if (mBuilder != null) {
            mBuilder.setBackgroundResource(mBackgroundResId);
        }
        return this;
    }


    /**
     * 使对话框消失
     */
    public void dismiss() {
        mAlertDialog.dismiss();
    }


    /**
     * 设置对话框标题
     * @param resId text资源id
     * @return 返回自己
     */
    public CommDialog setTitle(int resId) {
        mTitleResId = resId;
        if (mBuilder != null) {
            mBuilder.setTitle(resId);
        }
        return this;
    }

    /**
     * 设置标题
     * @param title 传入text
     * @return 返回自己
     */
    public CommDialog setTitle(CharSequence title) {
        mTitle = title;
        if (mBuilder != null) {
            mBuilder.setTitle(title);
        }
        return this;
    }

    /**
     * 设置标题颜色
     *
     * @param color 传入text颜色
     * @return 返回自己
     */
    public CommDialog setTitleColor(int color) {
        mTitleColor = color;
        if (mBuilder != null) {
            mBuilder.setTitleColor(color);
        }
        return this;
    }

    /**
     * 设置提示字段
     * @param resId 提示字段资源id
     * @return 返回自己
     */
    public CommDialog setMessage(int resId) {
        mMessageResId = resId;
        if (mBuilder != null) {
            mBuilder.setMessage(resId);
        }
        return this;
    }

    /***
     * 设置提示字段
     * @param message 提示字段text
     * @return 返回自己
     */
    public CommDialog setMessage(CharSequence message) {
        mMessage = message;
        if (mBuilder != null) {
            mBuilder.setMessage(message);
        }
        return this;
    }

    /**
     * 设置是否可以取消
     * @param cancelable 可取消否
     * @return 返回自己
     */
    public CommDialog setCancelable(boolean cancelable){
        if (mBuilder != null) {
            mBuilder.setCancelable(cancelable);
        }
        return this;
    }


    /**
     * 设置确定按钮
     * @param resId 确定按键字符串
     * @param listener 确定按钮动作
     * @return 返回自己
     */
    public CommDialog setPositiveButton(int resId, final View.OnClickListener listener) {
        mPositiveText = mContext.getString(resId);
        mPositiveListener = listener;
        mPositiveButton = new Button(mContext);
        mPositiveButton.setBackgroundResource(R.drawable.btn_common_dialog_left);
        mPositiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mPositiveButton.setTextColor(mContext.getResources().getColor(R.color.relogin_highlight_text));
        mPositiveButton.setText(mPositiveText);
        mPositiveButton.setOnClickListener(mPositiveListener);
        return this;
    }

    public CommDialog setMiddleLineView(ImageView view){
        mMiddleLine = view;
        return this;
    }

    public CommDialog setMiddleLineViewColor(int _color){
        mMiddleLineColor = _color;
        mMiddleLine = new ImageView(mContext);
        mMiddleLine.setImageDrawable(new ColorDrawable(_color));
        return this;
    }


    /**
     * 设置确认按钮
     * @param text 确认按钮字符串
     * @param listener 确认按钮动作
     * @return 返回自己
     */
    public CommDialog setPositiveButton(String text, final View.OnClickListener listener) {
        mPositiveText = text;
        mPositiveListener = listener;
        mPositiveButton = new Button(mContext);
        mPositiveButton.setBackgroundResource(R.drawable.btn_common_dialog_left);
        mPositiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mPositiveButton.setTextColor(mContext.getResources().getColor(R.color.relogin_highlight_text));
        mPositiveButton.setText(mPositiveText);
        mPositiveButton.setOnClickListener(mPositiveListener);
        return this;
    }

    /**
     * 设置取消按钮
     * @param resId 取消按钮字符串
     * @param listener 取消按钮动作
     * @return 返回自己
     */
    public CommDialog setNegativeButton(int resId, final View.OnClickListener listener) {
        mNevigationText = mContext.getString(resId);
        mNevigationListener = listener;
        mNevigateButton = new Button(mContext);
        mNevigateButton.setBackgroundResource(R.drawable.btn_common_dialog_right);
        mNevigateButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mNevigateButton.setTextColor(mContext.getResources().getColor(R.color.relogin_highlight_text));
        mNevigateButton.setText(mNevigationText);
        mNevigateButton.setOnClickListener(mNevigationListener);
//        if (mBuilder != null) {
//            mBuilder.setmNevigateButton(mNevigationText, listener);
//        }
        return this;
    }
    /**
     * 设置取消按钮
     * @param text 取消按钮字符串
     * @param listener 取消按钮动作
     * @return 返回自己
     */
    public CommDialog setNegativeButton(String text, final View.OnClickListener listener) {
        mNevigationText = text;
        mNevigationListener = listener;
        mNevigateButton = new Button(mContext);
        mNevigateButton.setBackgroundResource(R.drawable.btn_common_dialog_right);
        mNevigateButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mNevigateButton.setTextColor(mContext.getResources().getColor(R.color.relogin_highlight_text));
        mNevigateButton.setText(mNevigationText);
        mNevigateButton.setOnClickListener(mNevigationListener);
        return this;
    }

    /**
     * 设置点击对话框外区域是否取消对话框
     * @param cancel 可取消否
     * @return 返回自己
     */
    public CommDialog setCanceledOnTouchOutside(boolean cancel) {
        this.mCancel = cancel;
        if (mBuilder != null) {
            mBuilder.setCanceledOnTouchOutside(mCancel);
        }
        return this;
    }

    /**
     * 对话框消失后执行的逻辑
     * @param onDismissListener 消失后回调
     * @return 返回自己
     */
    public CommDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
        return this;
    }


    private class Builder {

        private TextView mTitleView;
        private TextView     mMessageView;
        private ImageView     mMiddleLayoutLine;
        private Window mAlertDialogWindow;
        private LinearLayout mButtonLayout;

//        CharSequence mPositiveText,mNevigationText;
//        View.OnClickListener mPositiveListener,mNevigationListener;
        private boolean mCancele;
        private int mFlag;


        private Builder(){
            mAlertDialog = new AlertDialog.Builder(mContext).create();
            mAlertDialog.setCancelable(mCancele);
            mAlertDialog.requestWindowFeature(mFlag);
            mAlertDialog.show();

            mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            mAlertDialogWindow = mAlertDialog.getWindow();
            View contv = LayoutInflater.from(mContext).inflate(R.layout.comm_dialog, null);
            contv.setFocusable(true);
            contv.setFocusableInTouchMode(true);
            mAlertDialogWindow.setBackgroundDrawableResource(R.drawable.bg_common_dialog_window);

            mAlertDialogWindow.setContentView(contv);
            // mAlertDialogWindow.setContentView(R.layout.layout_CSouyueDialog);

//7
//            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.TYPE_PHONE,
//                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
//                    PixelFormat.TRANSLUCENT
//            );

            mTitleView = (TextView) mAlertDialogWindow.findViewById(R.id.tv_com_dialog_title);
            mMessageView = (TextView) mAlertDialogWindow.findViewById(R.id.tv_com_dialog_msg);
            mButtonLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.ll_tv_com_dialog_bottomLayout);
            mMiddleLayoutLine = (ImageView) mAlertDialogWindow.findViewById(R.id.iv_comm_dialog_middle_line);
//            mPositiveButton = (Button) mAlertDialogWindow.findViewById(R.id.bt_com_dialog_ensure);
//            mNevigateButton = (Button) mAlertDialogWindow.findViewById(R.id.bt_com_dialog_cancel);
            if (mView != null) {
                LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.ll_com_dialog_contentview);
                linearLayout.removeAllViews();
                linearLayout.addView(mView);
            }
            if (mTitleResId != 0) {
                setTitle(mTitleResId);
            }
            if (mTitle != null) {
                setTitle(mTitle);
            }

            if (mTitleColor != 0) {
                setTitleColor(mTitleColor);
            }
            if (mTitle == null && mTitleResId == 0) {
                mTitleView.setVisibility(View.GONE);
            }

            if (mMessageResId != 0) {
                setMessage(mMessageResId);
            }
            if (mMessage != null) {
                setMessage(mMessage);
            }

            if (mPositiveButton!=null){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.weight = 1;
                mButtonLayout.addView(mPositiveButton,params);
                if (mNevigationText==null){
                    mPositiveButton.setBackgroundResource(R.drawable.btn_common_dialog_all);
//                    mNevigateButton.setVisibility(View.GONE);
//                    mButtonLayout.removeView(mNevigateButton);
                }
            }

            if ( mMiddleLine != null){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
                if (mMiddleLineColor != 0){
                    mMiddleLine.setBackgroundColor(mMiddleLineColor);
                }
                mButtonLayout.addView(mMiddleLine,params);
            }
            if (mMiddleLineColor!=0){
                mMiddleLayoutLine.setImageDrawable(new ColorDrawable(mMiddleLineColor));
            }
            if (mNevigateButton!=null){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.weight = 1;
                mButtonLayout.addView(mNevigateButton,params);
                if (mPositiveText==null){
                    mNevigateButton.setBackgroundResource(R.drawable.btn_common_dialog_all);
//                    mPositiveButton.setVisibility(View.GONE);
                }
            }


            if (mBackgroundResId != 0) {
                LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.ll_com_dialog_bg);
                linearLayout.setBackgroundResource(mBackgroundResId);
            }
            if (mBackgroundDrawable != null) {
                LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.ll_com_dialog_bg);
                if (Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN) {
                    linearLayout.setBackground(mBackgroundDrawable);
                }else{
                    linearLayout.setBackgroundDrawable(mBackgroundDrawable);
                }
            }

            if (mMessageContentView != null) {
                this.setContentView(mMessageContentView);
            }
            mAlertDialog.setCanceledOnTouchOutside(mCancel);
            if (mOnDismissListener != null) {
                mAlertDialog.setOnDismissListener(mOnDismissListener);
            }
        }

        public void setCancelable(boolean cancelable){
            mCancele = cancelable;
        }

        public void setTitle(int resId) {
            mTitleView.setText(resId);
        }

        public void setTitle(CharSequence title) {
            mTitleView.setText(title);
        }

        public void setTitleColor(int titleColor) {
            mTitleView.setTextColor(titleColor);
        }
        public void setMessage(int resId) {
            mMessageView.setText(resId);
        }

        public void setMessage(CharSequence message) {
            mMessageView.setText(message);
        }


        public void setView(View view) {
            LinearLayout l = (LinearLayout) mAlertDialogWindow.findViewById(R.id.ll_com_dialog_contentview);
            l.removeAllViews();
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);

            view.setOnFocusChangeListener(
                    new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            System.out.println("-->" + hasFocus);
                            mAlertDialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            // show imm
                            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(
                                    InputMethodManager.SHOW_FORCED,
                                    InputMethodManager.HIDE_IMPLICIT_ONLY
                            );

                        }
                    }
            );

            l.addView(view);

            if (view instanceof ViewGroup) {

                ViewGroup viewGroup = (ViewGroup) view;

                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (viewGroup.getChildAt(i) instanceof EditText) {
                        EditText editText = (EditText) viewGroup.getChildAt(i);
                        editText.setFocusable(true);
                        editText.requestFocus();
                        editText.setFocusableInTouchMode(true);
                    }
                }
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (viewGroup.getChildAt(i) instanceof AutoCompleteTextView) {
                        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) viewGroup.getChildAt(i);
                        autoCompleteTextView.setFocusable(true);
                        autoCompleteTextView.requestFocus();
                        autoCompleteTextView.setFocusableInTouchMode(true);
                    }
                }
            }
        }

        public void setContentView(View contentView) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            contentView.setLayoutParams(layoutParams);
            if (contentView instanceof ListView) {
                setListViewHeightBasedOnChildren((ListView) contentView);
            }
            LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.ll_com_dialog_bg);
            if (linearLayout != null) {
                linearLayout.removeAllViews();
                linearLayout.addView(contentView);
            }
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                if (linearLayout.getChildAt(i) instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) linearLayout.getChildAt(i);
                    autoCompleteTextView.setFocusable(true);
                    autoCompleteTextView.requestFocus();
                    autoCompleteTextView.setFocusableInTouchMode(true);
                }
            }
        }

        public void setBackground(Drawable drawable) {
            LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.ll_com_dialog_bg);
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN) {
                linearLayout.setBackground(drawable);
            }else{
                linearLayout.setBackgroundDrawable(drawable);
            }
        }

        public void setBackgroundResource(int resId) {
            LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.ll_com_dialog_bg);
            linearLayout.setBackgroundResource(resId);
        }


        public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            mAlertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        }

        public void setFlag(int flag) {

        }

    }

    /**
     * 动态测量listview-Item的高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
