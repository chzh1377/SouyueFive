package com.zhongsou.souyue.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SubscribeListActivity;
import com.zhongsou.souyue.utils.IntentUtil;

/**
 * 标题栏辅助工具类。
 *
 * @author zhangliang01@zhongsou.com
 */
public class TitleBarToFragmentHelper implements OnClickListener {
    public static final int BUTTON_ID_RIGHT_NONE = -1;// 右侧无按钮
    public static final int BUTTON_ID_EDIT = R.string.title_bar_edit;// 编辑
    public static final int BUTTON_ID_SEARCH = R.id.img_btn_title_fragment_bar_search;// 搜索按钮
    public static final int BUTTON_ID_ADD_SUBSCRIBE = R.id.bt_add_subscribe;// 订阅按钮 不包含分享按钮
    public static final int BUTTON_ID_RIGHT_WITH_SHARE = R.id.bar_btn_share;// 包含分享按钮
    private Fragment fragment;
    private Activity context;
    private View includedView;// 包含工具栏的view
    private View view; // 标题栏
    private String title;// 标题文本
    private TextView textTitle, right_text;
    private ImageButton imgbtn_search;
    private View imgbtn_add_subscribe;
    private int rightButtonIdToShow;// 右侧要显示的按钮的id
    private int showAddView = View.GONE;

    /**
     * @param includedView        包含标题栏的view 参数为null时从activity里找标题栏
     * @param title               标题
     * @param rightButtonIdToShow 右侧显示的按钮
     */
    public TitleBarToFragmentHelper(Fragment fragment, View includedView, String title, int rightButtonIdToShow, boolean showAddBtn) {
        this.fragment = fragment;
        this.includedView = includedView;
        this.title = title;
        this.rightButtonIdToShow = rightButtonIdToShow;
        this.showAddView = showAddBtn ? View.VISIBLE : View.GONE;
        this.init();
    }

    public TitleBarToFragmentHelper(Activity context, View includedView, String title, int rightButtonIdToShow, boolean showAddBtn) {
        this.context = context;
        this.includedView = includedView;
        this.title = title;
        this.rightButtonIdToShow = rightButtonIdToShow;
        this.showAddView = showAddBtn ? View.VISIBLE : View.GONE;
        this.init();

    }

    public void setTxt(String zh) {
        if (textTitle != null) {
            textTitle.setText(zh == null ? "" : zh);
        }
    }

    public void showOrHideTitle(boolean show) {
        if (textTitle != null)
            if (show)
                textTitle.setVisibility(View.VISIBLE);
            else
                textTitle.setVisibility(View.INVISIBLE);
    }

    View shareView = null;

    /**
     * @param show 分享按钮
     *             点击事件交由activity处理，在xml文件中定义
     * @author fan
     */
    public void showShare(boolean show) {
        if (view != null) {
            if (shareView == null) {
                shareView = view.findViewById(R.id.bar_btn_share);
            }
            shareView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置右侧按钮的显示风格和设置其点击事件
     */
    private void init() {
        if (includedView != null) {
            this.view = this.includedView.findViewById(R.id.title_fragment_bar_included);
        } else if (this.context != null) {
            this.view = this.context.findViewById(R.id.title_fragment_bar_included);
        } else if (this.fragment != null) {
            this.view = this.fragment.getActivity().findViewById(R.id.title_fragment_bar_included);
        }
        if (this.view == null) {
            return;
        }

        right_text = (TextView) view.findViewById(R.id.btn_title_fragment_bar_edit);
        right_text.setOnClickListener(this);
        textTitle = (TextView) view.findViewById(R.id.current_channel);
        imgbtn_search = (ImageButton) view.findViewById(BUTTON_ID_SEARCH);
        imgbtn_add_subscribe = view.findViewById(R.id.bt_add_subscribe);
        imgbtn_add_subscribe.setVisibility(showAddView);
        imgbtn_add_subscribe.setOnClickListener(this);
        if (textTitle != null) {
            textTitle.setText(this.title);
        }
        switch (this.rightButtonIdToShow) {// 选择显示右侧哪个按钮（左侧为唯一返回按钮，默认显示）
            case BUTTON_ID_EDIT://我的收藏左侧显示编辑按钮
                right_text.setText("编辑");
                right_text.setVisibility(View.VISIBLE);
                right_text.setOnClickListener(this);
                view.findViewById(R.id.common_right_parent).setVisibility(View.GONE);
                // textTitle_en.setVisibility(View.GONE);
//			imgbtn_search.setVisibility(View.GONE);
                break;
            case BUTTON_ID_RIGHT_WITH_SHARE://显示通用右侧按钮 包括分享
                view.findViewById(R.id.common_right_parent).setVisibility(View.VISIBLE);
                view.findViewById(R.id.bar_btn_share).setVisibility(View.VISIBLE);
                break;
            case BUTTON_ID_ADD_SUBSCRIBE://显示通用右侧按钮 不包括分享
            case BUTTON_ID_SEARCH://显示通用右侧按钮 不包括分享
                view.findViewById(R.id.common_right_parent).setVisibility(View.VISIBLE);
                break;
            case BUTTON_ID_RIGHT_NONE:
                view.findViewById(R.id.common_right_parent).setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.goBack) {
            this.fragment.getActivity().finish();
            fragment.getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }
        if (v.getId() == R.id.bt_add_subscribe) {
            Intent addIntent = new Intent();
            addIntent.setClass(fragment.getActivity(), SubscribeListActivity.class);
            fragment.getActivity().startActivity(addIntent);
            fragment.getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }

        switch (rightButtonIdToShow) {
            case BUTTON_ID_EDIT:
                if (fragment instanceof OnClickListener) {
                    ((OnClickListener) fragment).onClick(v);
                }
                break;
            default:
                break;
        }
    }

    public void onSearchClick(View view) {
        IntentUtil.openSearchActivity(fragment.getActivity());
        fragment.getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
