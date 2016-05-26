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
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.SendBlogActivity;
import com.zhongsou.souyue.activity.SendInfoActivity;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SettingsManager;

/**
 * 信息发布微件
 */
@SuppressLint("ValidFragment")
public class BlogFragment extends SRPFragment implements OnClickListener {

	public static final String TAG = "BlogFragment";

	public static final int layoutId = R.layout.srp_blog;

	public static final String ONLY_ADMIN = "2";

	private ImageButton btn_new;

	public BlogFragment(Context context, NavigationBar nav) {
		this(context, nav, null);
	}

	public BlogFragment(Context context, NavigationBar nav, String type) {
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

	public BlogFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState) {
		if (savedInstanceState != null)
			this.nav = (NavigationBar) savedInstanceState
					.getSerializable("nav");

		View view = View.inflate(activity, layoutId, null);
		inits(view);
		return view;
	}

	@Override
	protected void inits(View srpItemView) {
		btn_new = (ImageButton) srpItemView.findViewById(R.id.btn_new);
		btn_new.setOnClickListener(this);
		super.inits(srpItemView);
	}

    @Override
    public void searchResultSuccess(SearchResult searchResult) {
        super.searchResultSuccess(searchResult);
//        if (as.hasExpired) {
//            customListView.startRefresh();
//        }
        if (CMainHttp.getInstance().isWifi(getActivity()))
            adapter.setImgAble(true);
        else
            adapter.setImgAble(SettingsManager.getInstance().isLoadImage());
        if(searchResult.items().size() == 0){
            pbHelper.showNoData();
            return;
        }
        hasDatas = true;
//        if (as != null && as.getTime() != null && as.getTime().getTime() > 0){
//            adapter.setChannelTime(System.currentTimeMillis() + "");
//		  }
		adapter.setChannelTime(System.currentTimeMillis() + "");

        pbHelper.goneLoading();
        adapter.addDatas(searchResult.items());
        adapter.setHasMoreItems(searchResult.hasMore());

    }

    @Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_new) {
			Intent intent = new Intent();
			if (!IntentUtil.isLogin()) {
				intent.setClass(getActivity(), LoginActivity.class);
				intent.putExtra(LoginActivity.Only_Login, true);
				getActivity().startActivityForResult(intent, 900);
				getActivity().overridePendingTransition(R.anim.left_in,
						R.anim.left_out);
				return;
			}

			if (ConstantsUtils.FR_INFO_PUB.equals(nav.category())) {
				intent.setClass(activity, SendInfoActivity.class);
				SelfCreateItem sci = new SelfCreateItem();
				sci.keyword_$eq(ConfigApi.isSouyue() ? keyWord : getKeyword());
				sci.column_name_$eq(nav.title());
				sci.srpId_$eq(ConfigApi.isSouyue() ? srpId : getSrpId());
				sci.md5_$eq(nav.md5());
				intent.putExtra(SendInfoActivity.TAG, sci);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.left_in,
						R.anim.left_out);
			} else {
				intent.setClass(activity, SendBlogActivity.class);
				SelfCreateItem sci = new SelfCreateItem();
				sci.keyword_$eq(ConfigApi.isSouyue() ? keyWord : getKeyword());
				sci.column_name_$eq(nav.title());
				sci.srpId_$eq(ConfigApi.isSouyue() ? srpId : getSrpId());
				sci.md5_$eq(nav.md5());
				sci.column_type_$eq(ConstantsUtils.TYPE_BLOG_SEARCH);
				intent.putExtra(SendBlogActivity.TAG, sci);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.left_in,
						R.anim.left_out);
			}

		}
	}

	public void updatePenView(boolean isAdmin) {
		if (ConstantsUtils.FR_INFO_PUB.equals(this.nav.category())) {
			if (ONLY_ADMIN.equals(this.nav.getRight()) && !isAdmin) {
				if (btn_new != null) {
					btn_new.setVisibility(View.GONE);
				}
			}
		}
	}
}
