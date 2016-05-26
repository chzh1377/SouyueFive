package com.zhongsou.souyue.circle.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import com.zhongsou.souyue.R;

/**
 * Created by bob zhou on 14-11-11.
 */
public class PersonalCenterImAlertMenu implements View.OnClickListener {

    private final Context mContext;
    private OnMenuClick onMenuClick;
    private PopupWindow popupWindow;

    @Override
    public void onClick(View v) {
        onMenuClick.onClick(v);
    }

    public interface OnMenuClick {
        void onClick(View whichButton);
    }

    public PersonalCenterImAlertMenu(Context context) {
        this.mContext = context;
        View popwindowView = View.inflate(context, R.layout.percenter_im_menu, null);
        popupWindow = new PopupWindow(popwindowView, context.getResources().getDimensionPixelOffset(R.dimen.space_175), RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public PopupWindow getDialog() {
        return popupWindow;
    }

    public OnMenuClick getOnMenuClick() {
        return onMenuClick;
    }

    public void show(View v) {
//        popupWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.RIGHT, 20, 20);
        popupWindow.showAsDropDown(v, 0, -mContext.getResources().getDimensionPixelOffset(R.dimen.space_5));
    }

    public void setOnMenuClick(OnMenuClick onMenuClick) {
        this.onMenuClick = onMenuClick;
        ViewGroup layout = (ViewGroup) popupWindow.getContentView();
        layout.findViewById(R.id.percenter_im_menu_send).setOnClickListener(this);
        layout.findViewById(R.id.percenter_im_menu_rename).setOnClickListener(this);
        layout.findViewById(R.id.percenter_im_menu_del).setOnClickListener(this);
    }
    
    public void dismiss() {
    	popupWindow.dismiss();
    }
    
}
