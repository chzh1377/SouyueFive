package com.zhongsou.souyue.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.common.utils.Utils;
import com.zhongsou.souyue.module.SubGroupModel;
import com.zhongsou.souyue.presenter.SubGroupActPresenter;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.DeviceUtil;

/**
 * Created by zyw on 2016/3/24.
 */
public class SubGroupActivity extends BaseActivity {
    public static final String TAG = SubGroupActivity.class.getSimpleName();
    private TextView             tvTitle;
    private ImageButton          ibOption;
    private LinearLayout         llIndicatorContainer;
    private FrameLayout          mainContainer;
    private LinearLayout         loading;
    private ProgressBarHelper    mProgressHelper;
    private SubGroupActPresenter mPresenter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static final String INTENT_EXTRA_IMAGE    = "group_image";
    public static final String INTENT_EXTRA_TITLE    = "group_title";
    public static final String INTENT_EXTRA_GROUP_ID = "group_id";
    private float mLastMotionX;
    private boolean isFinish;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.home_group_detail);
        maxHeight = Utils.dip2px(this, 100);
        initView();
        mPresenter = new SubGroupActPresenter(this);
    }

    //初始化视图
    private void initView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout viewById = findView(R.id.title_container);
                tvTitle = findView(R.id.activity_bar_title);
                ibOption = findView(R.id.btn_option);
                ibOption.setImageResource(R.drawable.home_group_options);
                ibOption.setOnClickListener(mPresenter.goGroupEdit);
                llIndicatorContainer = findView(R.id.home_group_idcator_container);
                mainContainer = findView(R.id.home_group_container);
                loading = findView(R.id.home_group_loading);
                mProgressHelper = new ProgressBarHelper(SubGroupActivity.this, loading);
                mProgressHelper.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
                    @Override
                    public void clickRefresh() {
                        mPresenter.loadData(null);
                    }
                });
            }
        });
    }

    public void setTitle(final String title) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                tvTitle.setText(title);
            }
        });
    }

    //增加indicator
    public void addIndicator(final SubGroupModel model) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                TextView                  textView = new TextView(SubGroupActivity.this);
                LinearLayout.LayoutParams params   = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.leftMargin = DeviceUtil.dip2px(SubGroupActivity.this, 10);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_group_indicator_bg));
                textView.setTextColor(Color.parseColor("#282828"));
                textView.setText(model.getTitle());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setTag(model);
                textView.setPadding(DeviceUtil.dip2px(SubGroupActivity.this, 8), textView.getPaddingTop(), DeviceUtil.dip2px(SubGroupActivity.this, 8), textView.getPaddingBottom());
                llIndicatorContainer.addView(textView, params);
                mPresenter.bindListener(textView);
            }
        });
    }

    public void removeAllIndicator() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                llIndicatorContainer.removeAllViews();
            }
        });
    }
    public void setIndicatorState(final boolean enable){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (enable) {
                    View parent = (View) llIndicatorContainer.getParent();
                    parent.setVisibility(View.VISIBLE);
                } else {
                    View parent = (View) llIndicatorContainer.getParent();
                    parent.setVisibility(View.GONE);
                }
            }
        });

    }

    public void addViewToContainer(final View view) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (view.getParent() != null) {
                    ViewGroup viewGroup = (ViewGroup) view.getParent();
                    viewGroup.removeView(view);
                }
                mainContainer.removeAllViews();
                mainContainer.addView(view);
            }
        });
    }


    public void setLoading() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressHelper.showLoading();
            }
        });
    }

    public void removeLoading() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressHelper.goneLoading();
            }
        });
    }


    public void setNoData() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressHelper.showNoData();
            }
        });
    }

    public void setNetError() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressHelper.showNetError();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mPresenter.loadData(null);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mPresenter.onDestroy();
    }


    public SubGroupActPresenter getPresenter() {
        return mPresenter;
    }

    int maxHeight;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final float x = ev.getX();
        float       rawY = ev.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                final float delay = x - mLastMotionX;
                if (delay > getWindow().getDecorView().getMeasuredWidth() / 2) {
                    isFinish = true;
                    if(rawY > maxHeight) {
                        finish();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isFinish) {
                    isFinish = false;
                    return true;
                }
                break;
            default:
                break;
        }
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
