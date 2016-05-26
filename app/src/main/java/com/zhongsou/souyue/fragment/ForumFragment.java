package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SendBlogActivity;
import com.zhongsou.souyue.activity.SendWeiboActivity;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.utils.ConstantsUtils;

/**
 * 论坛
 */
@SuppressLint("ValidFragment")
public class ForumFragment extends SRPFragment implements OnClickListener {

    public static final int layoutId = R.layout.srp_forum;

    public ForumFragment(Context context, NavigationBar nav) {
        this(context, nav,null);
    }

    public ForumFragment(Context context, NavigationBar nav,String type) {
        super(context, nav,type);
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

    public ForumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState.getSerializable("nav");

        View view = View.inflate(activity, layoutId, null);
        inits(view);
        return view;
    }

    @Override
    protected void inits(View srpItemView) {
        ImageButton btn_new = (ImageButton) srpItemView
                .findViewById(R.id.btn_new);
        btn_new.setOnClickListener(this);
        btn_new.setVisibility(View.GONE);
        super.inits(srpItemView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_new) {
            SelfCreateItem sci = new SelfCreateItem();
            sci.keyword_$eq(getKeyword());
            sci.column_name_$eq(nav.title());
            sci.srpId_$eq(getSrpId());
            sci.md5_$eq(nav.md5());
            sci.column_type_$eq(ConstantsUtils.TYPE_BBS_SEARCH);
            Intent intent = new Intent();
            intent.putExtra(SendWeiboActivity.TAG, sci);
            intent.setClass(activity, SendBlogActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }


    }
}
