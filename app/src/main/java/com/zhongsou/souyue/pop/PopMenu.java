package com.zhongsou.souyue.pop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.VerticalShareAdapter;

@SuppressLint({"NewApi", "ViewConstructor"})
public class PopMenu extends PopupWindow {
    private Context context;
    private View view;
    private LayoutInflater inflater;
    private GridView grid;
    private VerticalShareAdapter vsadapter;
    public final static int SHARE_TO_SINA = 1;
    public final static int SHARE_TO_WEIX = 2;
    public final static int SHARE_TO_FRIENDS = 3;
    public final static int SHARE_TO_TWEIBO = 7;
    public final static int SHARE_TO_RENREN = 6;
    public final static int SHARE_TO_EMAIL = 4;
    public final static int SHARE_TO_MESSAGE = 5;

    public PopMenu(View v) {
        super(v, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
        context = v.getContext();
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(false);
        this.setAnimationStyle(R.style.popupAnimation);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        this.update();
        setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    PopMenu.this.dismiss();
                    return true;
                }
                return false;
            }
        });
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(R.layout.vertical_popmenu, null);
        grid = (GridView) view.findViewById(R.id.vertical_gridview);
        vsadapter = new VerticalShareAdapter(context);
        grid.setAdapter(vsadapter);
        if (VERSION.SDK_INT >= 11) this.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setContentView(view);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        view.measure(0, 0);
        super.showAsDropDown(anchor, xoff, -(anchor.getHeight() + view.getMeasuredHeight()));
        return;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        grid.setOnItemClickListener(listener);
    }

    public Integer getItem(int position) {
        return vsadapter.getItem(position);
    }

    public void show(View parent) {
        showAsDropDown(parent, 0, 0);
        update();
    }

    public void dismiss() {
        PopMenu.super.dismiss();
        return;
    }
}
