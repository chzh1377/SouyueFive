package com.zhongsou.souyue.ui;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.CMainHttp;

public class FragmentProgressBarHelper {
    public interface ProgressBarClickListener {
        void clickRefresh();
    }

    public View loading;
    TextView tv;
    ProgressBar progressBar;
    ProgressBarClickListener pcl;
    public boolean isLoading = false;
    public Activity context;

    public void setProgressBarClickListener(ProgressBarClickListener pcl) {
        this.pcl = pcl;
    }

    public FragmentProgressBarHelper(Activity context, View inView) {
        this.context = context;
        if (inView == null || context == null)
            return;
        loading = inView;
        tv = (TextView) loading.findViewById(R.id.fragment_loading_tip_txt);
        progressBar = (ProgressBar) loading.findViewById(R.id.fragment_loading_progress_bar);
    }

    public void showNetError() {
        context.runOnUiThread(new Runnable() {
            public void run() {
                isLoading = false;
                if (tv != null) {
                    tv.setText(R.string.srploaded_nocontent);
                }
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loading.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pcl != null && !progressBar.isShown() && progressBar.getVisibility() == View.GONE) {
                            if (CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance())) {
                                progressBar.setVisibility(View.VISIBLE);
                                pcl.clickRefresh();
                            }
                        }
                    }
                });
            }
        });
    }

    //暂无数据
    public void showNoData() {
        context.runOnUiThread(new Runnable() {
            public void run() {
                isLoading = false;
                if (tv != null) {
                    tv.setText(R.string.nocontent);
                }
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loading.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pcl != null && !progressBar.isShown() && progressBar.getVisibility() == View.GONE) {
                            if (CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance())) {
                                progressBar.setVisibility(View.VISIBLE);
                                pcl.clickRefresh();
                            }
                        }
                    }
                });
            }
        });
    }

    public void goneLoading() {
        context.runOnUiThread(new Runnable() {
            public void run() {
                isLoading = false;
                if (loading != null) {
                    loading.setVisibility(View.GONE);
                }
            }
        });
    }

    public void showLoading() {
        context.runOnUiThread(new Runnable() {
            public void run() {
                isLoading = true;
                if (tv != null) tv.setText(R.string.loading_progress_hint);
                if (loading != null) {
                    loading.setVisibility(View.VISIBLE);
                    if (progressBar != null && !progressBar.isShown())
                        progressBar.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    public boolean isAllGone() {
        if (loading.getVisibility() == View.GONE) return true;
        return false;
    }

    public View getLoadingView() {
        return loading;
    }
}
