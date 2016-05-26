package com.zhongsou.souyue.share;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.ShareAdapter;
import com.zhongsou.souyue.pop.MyPopupWindow;

public class ShareMenu {
    public MyPopupWindow popupWindow;
    private GridView gridView;
    private View view;
    private ShareAdapter popAdapter;
    public final static int SHARE_TO_DIGEST = 0;
    public final static int SHARE_TO_SINA = 1;
    public final static int SHARE_TO_WEIX = 2;
    public final static int SHARE_TO_FRIENDS = 3;
    public final static int SHARE_TO_EMAIL = 4;
    public final static int SHARE_TO_SMS = 5;
    public final static int SHARE_TO_RENREN = 6;
    public final static int SHARE_TO_TWEIBO = 7;
    public final static int SHARE_TO_SYFRIENDS = 8;
	public final static int SHARE_TO_SYIMFRIEND = 9;//搜悦好友 IM
	public final static int SHARE_TO_QQFRIEND = 11;//QQ好友
	public final static int SHARE_TO_QQZONE = 12;//QQ空间
	
    public void popMenuNotifyDataSetChanged() {
        popAdapter.notifyDataSetChanged();
    }

    public ShareMenu(final Context context, boolean isPullUp) {
        if (popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.sharemenu_twolines, null);
        }
        popAdapter = new ShareAdapter(context);
        gridView = (GridView) view.findViewById(R.id.share_gridview);
        gridView.setAdapter(popAdapter);
        popAdapter.notifyDataSetChanged();
        popupWindow = new MyPopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
        if (isPullUp) popupWindow.isPullUp = true;
    }

    // 设置菜单项点击监听器
    public void setOnItemClickListener(OnItemClickListener listener) {
        gridView.setOnItemClickListener(listener);
    }

    public ShareAdapter getAdapter() {
        return popAdapter;
    }

    public void showAsDropDown(View parent, boolean bottom) {
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] xy = new int[2];
        parent.getLocationOnScreen(xy);
        if (bottom)
            popupWindow.showAsDropDown(parent, 0, 0);
        else
            popupWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, xy[0] / 2, xy[1]);
        popupWindow.update();
    }

    public void showTT(View parent) {
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void dismiss() {
        if (popupWindow != null || popupWindow.isShowing()) popupWindow.dismiss();
    }
    
    /**
	 * 行业资讯用，特殊处理
	 * showDilaogAsDropDown:description. <br/>  
	 *  
	 * @author fm
	 * @date   2014年9月23日 下午2:19:18
	 * @param parent
	 * @param bottom
	 */
	public void showDialogAsDropDown(View parent, boolean bottom) {
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		int[] xy = new int[2];
		parent.getLocationOnScreen(xy);
		if (bottom)
			popupWindow.showAsDropDown(parent, 0, parent.getHeight());
		else
			popupWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, xy[0] / 2, xy[1]);
		popupWindow.update();
	}
}
