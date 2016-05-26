package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.SendBlogActivity;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.SYUserManager;

@SuppressLint("ValidFragment")
public class SRPSelfCreateFragment extends SRPFragment implements OnClickListener {

    public static final int layoutId = R.layout.srp_selfcreate;
    private LinearLayout srp_selfcreate_null;
	//add by trade
	private boolean isSrp = true;
    public SRPSelfCreateFragment() {
    }

    public SRPSelfCreateFragment(Context context, NavigationBar nav) {
        super(context, nav, null);
    }

    public SRPSelfCreateFragment(Context context, NavigationBar nav, boolean isSrp) {
    	super(context, nav, null);
    	this.isSrp=isSrp;
    }
    
    public SRPSelfCreateFragment(Context context, NavigationBar nav,String type) {
        super(context, nav, type);
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
        srp_selfcreate_null = (LinearLayout) srpItemView.findViewById(R.id.srp_selfcreate_null);
        super.inits(srpItemView);
    }

    @Override
    public void searchResultSuccess(SearchResult searchResult) {
        super.searchResultSuccess(searchResult);
        if (searchResult.items().size() == 0) {
            srp_selfcreate_null.setVisibility(View.VISIBLE);
        } else {
            srp_selfcreate_null.setVisibility(View.GONE);
        }
    }

    @Override
    public void searchResultToPullDownRefreshSuccess(SearchResult searchResult) {
        super.searchResultToPullDownRefreshSuccess(searchResult);
        if (searchResult.items().size() == 0) {
            srp_selfcreate_null.setVisibility(View.VISIBLE);
        } else {
            srp_selfcreate_null.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btn_new) {
            String utype = SYUserManager.getInstance().getUserType();
            if (null != utype && !utype.equals("1")) {
                // 未登录
                showDialog();
            } else {
                Intent intent = new Intent();
                intent.setClass(activity, SendBlogActivity.class);
                SelfCreateItem sci = new SelfCreateItem();
                sci.keyword_$eq(ConfigApi.isSouyue()?keyWord:getKeyword());
                sci.column_name_$eq(nav.title());
                sci.srpId_$eq(getSrpId());
                sci.md5_$eq(nav.md5());
                sci.column_type_$eq(ConstantsUtils.TYPE_BLOG_SEARCH);
                intent.putExtra(SendBlogActivity.TAG, sci);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("提醒")
                .setMessage("您还未登录，是否登录？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 确定
                        if(isSrp){
                			Intent intent = new Intent();
    						intent.setClass(getActivity(),LoginActivity.class);
    						intent.putExtra(LoginActivity.Only_Login, true);
                            getActivity().startActivityForResult(intent, 900);
    						getActivity().overridePendingTransition(R.anim.left_in,
    						R.anim.left_out);
                		}else{
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), LoginActivity.class);
                            intent.putExtra(LoginActivity.Only_Login, true);
                            intent.putExtra(LoginActivity.CIRCLE_SET_INTEREST_ID, -2l);
                            getActivity().startActivity(intent);
                            (getActivity()).overridePendingTransition(R.anim.left_in,
                                    R.anim.left_out);
                		}

//            	IntentUtil.goLogin(activity, true);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                // 取消

            }
        }).show();
    }
}
