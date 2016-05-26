package com.zhongsou.souyue.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.DimensionalCodeActivity;
import com.zhongsou.souyue.activity.XiaoDanganWebActivity;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.utils.SettingsManager;

/**
 * Fragment 百科（小档案）
 *
 * @author chefb@zhongsou.com
 */
public class XiaoDanganFragment extends SRPFragment implements OnClickListener {

    private ImageView iv;
    private TextView tv_description;
    private TextView btn_more;
    private String url;
    private View btn_qr;
    public static final int layoutId = R.layout.list_item_xiaodangan;

    public XiaoDanganFragment(Context context, NavigationBar nav) {
        super(context, nav);
    }

    public XiaoDanganFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState.getSerializable("nav");

        View view = View.inflate(activity, R.layout.list_item_xiaodangan, null);
        inits(view);
        return view;
    }

    @Override
    public void loadData() {
        if (searchResult != null)
            searchResultSuccess(searchResult);
        else
            super.loadData();
    }

    @Override
    protected void inits(View view) {
        iv = (ImageView) view.findViewById(R.id.iv_logo);
        tv_description = (TextView) view.findViewById(R.id.tv_description);
        btn_more = (TextView) view.findViewById(R.id.btn_more);
        btn_more.setOnClickListener(this);
        btn_qr = view.findViewById(R.id.btn_create_qr);
        btn_qr.setOnClickListener(create);
        createPBHelper(view.findViewById(R.id.ll_data_loading), nav);
    }

    View.OnClickListener create = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), DimensionalCodeActivity.class);
            intent.putExtra(DimensionalCodeActivity.INTENT_K, getKeyword());
            intent.putExtra(DimensionalCodeActivity.INTENT_ID, getSrpId());
            intent.putExtra(DimensionalCodeActivity.INTENT_URL, imgUrl);
            getActivity().startActivity(intent);
        }
    };


    private String imgUrl;

    public void searchResultSuccess(SearchResult searchResult) {
        this.searchResult = searchResult;
        pbHelper.goneLoading();
        if (searchResult.items().size() == 0)
            return;
        SearchResultItem searchResultItem = searchResult.items().get(0);
        url = searchResultItem.url();
        if (SettingsManager.getInstance().isLoadImage()) {
            if (searchResultItem.image().size() > 0)
                this.imgUrl = searchResultItem.image().get(0);
//            aq.id(iv).image(imgUrl, true, true, 0,
//                    0, null, AQuery.FADE_IN);
            PhotoUtils.showCard( PhotoUtils.UriType.HTTP,imgUrl,iv, MyDisplayImageOption.defaultOption);
        } else {
            iv.setVisibility(View.GONE);
        }
        tv_description.setText(searchResultItem.description());
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        pbHelper.showNetError();
//    }

    @Override
    public void onHttpError(IRequest request) {
        pbHelper.showNetError();
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(url))
            return;
        Intent intent = new Intent();
        intent.setClass(activity, XiaoDanganWebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("keyword", getKeyword());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
