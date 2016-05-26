package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;

public class BaseFragment extends Fragment implements IVolleyResponse {
    protected ImageLoader imageLoader;
    protected ProgressBarHelper pbHelp;
    protected Activity context;
    protected CMainHttp mMainHttp;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.context = activity;
        mMainHttp = CMainHttp.getInstance();
        imageLoader = ImageLoader.getInstance();
    }

    protected String getRes4String(int id) {
        if (context != null)
            return context.getResources().getString(id);
        return null;
    }

    @Override
    public void onResume() {
        ZSSdkUtil.onPageStart(getActivity(), this.getClass().getSimpleName(), null);
        super.onResume();
    }

    @Override
    public void onPause() {
        ZSSdkUtil.onPageEnd(getActivity(), this.getClass().getSimpleName(), null);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMainHttp.cancel(this);
        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View root, int id) {
        return (T) root.findViewById(id);
    }

    protected void showToast(int resId) {
        SouYueToast.makeText(context, getResources().getString(resId), 0)
                .show();
    }

    @Override
    public void onHttpResponse(IRequest request) {

    }

    @Override
    public void onHttpError(IRequest request) {

    }

    @Override
    public void onHttpStart(IRequest request) {

    }
}
