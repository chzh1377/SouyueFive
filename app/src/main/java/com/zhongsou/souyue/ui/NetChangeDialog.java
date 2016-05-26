package com.zhongsou.souyue.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.zhongsou.souyue.R;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/4/12.
 */
public class NetChangeDialog extends Dialog implements View.OnClickListener{

    private static NetChangeDialog instance = null;
    public static NetChangeDialog getInstance(Context context, NetClickListener cickListener)
    {
        if(instance == null)
        {
            synchronized (NetChangeDialog.class)
            {
                if(instance == null)
                {
                    instance = new NetChangeDialog(context,cickListener);
                }
            }
        }
        return instance;
    }

    @Override
    public void show() {
        if(instance!=null&& !instance.isShowing())
        {
            try{
                super.show();
            }catch (Exception e)
            {
                e.printStackTrace();
            }finally {
                instance =null;
            }

        }
    }

    private Context context;
    private NetClickListener mClickListener;
    private static int default_width = ViewGroup.LayoutParams.WRAP_CONTENT;
    private static int default_height = ViewGroup.LayoutParams.WRAP_CONTENT;

    public NetChangeDialog(Context context, NetClickListener cickListener) {
        super(context, R.style.im_dialog_style);
        this.context =context;
        mClickListener =cickListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_netchange);
        if (mClickListener != null)
        {
            findViewById(R.id.btn_continue).setOnClickListener(this);
            findViewById(R.id.btn_cancel).setOnClickListener(this);
        }
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        float density = getDensity(context);
        // 代码修改，FILL_PARENT也会留出一个边
        int[] widthAndHeight = getSrceenPixels(context);
        params.width = (int) (widthAndHeight[0] - 40 * density);
        params.height = default_height;

//        if (isFullScreen) {
//            params.width = (int) widthAndHeight[0];
//            params.height = default_height;
//        }
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);
    }
    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }
    private int[] getSrceenPixels(Context context) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) (context
                .getSystemService(Context.WINDOW_SERVICE));
        windowManager.getDefaultDisplay().getMetrics(displaysMetrics);
        int[] widthAndHeight = new int[2];
        widthAndHeight[0] = displaysMetrics.widthPixels;
        widthAndHeight[1] = displaysMetrics.heightPixels;
        return widthAndHeight;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_continue:
                if(mClickListener!=null)
                {
                    mClickListener.continuePlay();
                }
                dismiss();
                break;
            case R.id.btn_cancel:
                if(mClickListener!=null)
                {
                    mClickListener.canclePlay();
                }
                dismiss();
                break;
        }
    }
    public interface NetClickListener
    {
        void continuePlay();
        void canclePlay();
    }
}
