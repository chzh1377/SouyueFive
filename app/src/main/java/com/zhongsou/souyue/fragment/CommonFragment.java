package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import com.zhongsou.souyue.module.NavigationBar;

/**
 * Fragment  除了百科（小档案）和有问必答
 *
 * @author chefb@zhongsou.com
 */
@SuppressLint("ValidFragment")
public class CommonFragment extends SRPFragment {

	public CommonFragment(Context context, NavigationBar nav) {
		this(context, nav, null);
	}

    public CommonFragment(Context context, NavigationBar nav,String type) {
		super(context, nav,type);
	}
    
    public CommonFragment() {
    }

    public void setType(String type) {
        super.type = type;
    }

    public void setKeyWord(String keyWord) {
        super.keyWord = keyWord;
    }

    public void setSrpid(String srpId) {
        super.srpId = srpId;
    }
}
