package com.zhongsou.souyue.ui.lib;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.ui.lib.DialogPlus.ScreenType;


/**
 * @author Orhan Obut
 */
public class DialogViewHolder implements Holder {

    private static final int INVALID = -1;

    private int backgroundColor;

    private ViewGroup headerContainer;
    private ViewGroup footerContainer;
    private View.OnKeyListener keyListener;

    private View contentView;
    private int viewResourceId = INVALID;
    
    private ScreenType screenType = ScreenType.HALF;
    private int height ;
    private int halfHeight;
    
	public DialogViewHolder(int viewResourceId,ScreenType screenType) {
        this.viewResourceId = viewResourceId;
        this.screenType = screenType;
        height = DeviceInfo.getScreenHeight();
        halfHeight = DeviceInfo.getScreenHeight() / 2;
    }

    public DialogViewHolder(View contentView) {
        this.contentView = contentView;
    }

    @Override
    public void addHeader(View view) {
        if (view == null) {
            return;
        }
        headerContainer.addView(view);
    }

    @Override
    public void addFooter(View view) {
        if (view == null) {
            return;
        }
        footerContainer.addView(view);
    }

    @Override
    public void setBackgroundColor(int colorResource) {
        this.backgroundColor = colorResource;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_view, parent, false);
        ViewGroup contentContainer = (ViewGroup) view.findViewById(R.id.view_container);
        LayoutParams params = contentContainer.getLayoutParams();
		if (height > 0) {
			if (DialogPlus.ScreenType.HALF.equals(screenType)) {
				params.height = halfHeight;
			} else {
				params.height = height;
			}
		}
        contentContainer.setBackgroundColor(parent.getResources().getColor(backgroundColor));
        contentContainer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyListener == null) {
                    throw new NullPointerException("keyListener should not be null");
                }
                return keyListener.onKey(v, keyCode, event);
            }
        });
        addContent(inflater, parent, contentContainer);
        return view;
    }

    private void addContent(LayoutInflater inflater, ViewGroup parent, ViewGroup container) {
        if (viewResourceId != INVALID) {
            contentView = inflater.inflate(viewResourceId, parent, false);
        } else {
            ViewGroup parentView = (ViewGroup) contentView.getParent();
            if (parentView != null) {
                parentView.removeView(contentView);
            }
        }

        container.addView(contentView);
    }

    @Override
    public void setOnKeyListener(View.OnKeyListener keyListener) {
        this.keyListener = keyListener;
    }

    @Override
    public View getInflatedView() {
        return contentView;
    }
    
    public void setScreenType(ScreenType screenType) {
		this.screenType = screenType;
	}

	public ScreenType getScreenType() {
		return screenType;
	}
}
