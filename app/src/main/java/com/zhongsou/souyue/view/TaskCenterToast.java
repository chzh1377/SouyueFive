package com.zhongsou.souyue.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.circle.model.TaskCenterInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;

/**
 * Created by yinguanping on 14-11-10.
 */
public class TaskCenterToast {

    private Activity context;
    private PopupWindow popWindow;
    // private View baseView;// 要依赖于显示位置的view
    private boolean isOpenPop = false;// pop是否弹出
    private int layoutId;// 布局文件
    private TaskCenterInfo taskCenterInfo;
    
	public boolean isOpenPop() {
        return isOpenPop;
    }

    public void setOpenPop(boolean isOpenPop) {
        this.isOpenPop = isOpenPop;
    }

    public TaskCenterToast(Activity context, TaskCenterInfo taskCenterInfo) {
        this.context = context;
        if (!(context instanceof Activity)) {//如果当前没有界面显示，则不做任何显示，因为popupwindow必须附着在activity上显示
            return;
        }
        this.taskCenterInfo = taskCenterInfo;

        if (taskCenterInfo.getCategory() != null
                && taskCenterInfo.getCategory().equals("task")) {
            if (taskCenterInfo.getType() != null
                    && taskCenterInfo.getType().equals("discover")) {// 发现提示消息
                if (!(context instanceof MainActivity)) {//如果不是在MainActivity.不做显示
                    return;
                }
                layoutId = R.layout.taskcenter_from;
            } else if (taskCenterInfo.getType() != null
                    && taskCenterInfo.getType().equals("tip")) {// 全局提示消息
                layoutId = R.layout.taskcenter_all;
            }
        } else if (taskCenterInfo.getCategory() != null
                && taskCenterInfo.getCategory().equals("relogin")) {
            layoutId = R.layout.relogin;
        }
    }

    public void showPopUpWindow() {
        if(layoutId==0){
            return;
        }
        if (popWindow == null) {
            View view = null;
            switch (layoutId) {// 根据layoutId不同区分或得到不同的显示view
                case R.layout.taskcenter_from:
                    view = getShowFromView();
                    newInstancePW(view);
                    break;
                case R.layout.taskcenter_all:
                    view = getShowCenterView();
                    newInstancePW(view);
                    break;
                case R.layout.relogin:
                    view = getShowReloginView();
                    popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    break;
                default:
                    break;
            }


        }
        if(popWindow==null){
            return;
        }
        if(context==null||context.isFinishing()){
            return;
        }
        switch (layoutId) {// 根据layoutId不同区分或得到不同的显示位置
            case R.layout.taskcenter_from:
                WindowManager wm = (WindowManager) context
                        .getSystemService(Context.WINDOW_SERVICE);

                int width = wm.getDefaultDisplay().getWidth();
                int LayoutX = width * 5 / 8 - DeviceUtil.dip2px(context, 240) * 4 / 7 + 19 / 2;

                popWindow.showAtLocation(new View(context), Gravity.BOTTOM | Gravity.LEFT, LayoutX,
                        DeviceUtil.dip2px(context, 47));

                break;
            case R.layout.taskcenter_all:
                popWindow.showAtLocation(new View(context), Gravity.RIGHT
                        | Gravity.CENTER_VERTICAL, 0, 0);
                break;
            case R.layout.relogin:
                popWindow.showAtLocation(new View(context), Gravity.CENTER, 0, 0);
                break;
            default:
                break;
        }
    }

    /**
     * 创建弹出框实例
     * @param view
     */
	private void newInstancePW(View view) {
		popWindow = new PopupWindow(view, DeviceUtil.dip2px(context, 240),
		        ViewGroup.LayoutParams.WRAP_CONTENT,true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				dissPopWindow();
			}
		});
	}

	/**
     * 隐藏弹出框调用方法
     */
    public void dissPopWindow() {
        if (isOpenPop && popWindow != null) {
            if(popWindow.isShowing()){
                popWindow.dismiss();
                setOpenPop(false);
            }
        }
    }

    /**
     * 返回来自任务中心任务的布局
     *
     * @return
     */
    private View getShowFromView() {
        LayoutInflater lay = LayoutInflater.from(context);
        View v = lay.inflate(R.layout.taskcenter_from, null);
        TextView textView = (TextView) v
                .findViewById(R.id.taskcenter_from_txtMsg);
        String s1 = "";
        String s2 = "";
        if (taskCenterInfo.getMsg().contains(taskCenterInfo.getHighlight())) {
            if (taskCenterInfo.getMsg().startsWith(taskCenterInfo.getHighlight())) {
                s2 = taskCenterInfo.getMsg().substring(taskCenterInfo.getHighlight().length());
            } else if (taskCenterInfo.getMsg().endsWith(taskCenterInfo.getHighlight())) {
                s1 = taskCenterInfo.getMsg().substring(0, taskCenterInfo.getMsg().length() - taskCenterInfo.getHighlight().length());
            } else {
                s1 = taskCenterInfo.getMsg().substring(0, taskCenterInfo.getMsg().indexOf(taskCenterInfo.getHighlight()));
                s2 = taskCenterInfo.getMsg().substring(taskCenterInfo.getMsg().indexOf(taskCenterInfo.getHighlight()) + taskCenterInfo.getHighlight().length(),
                        taskCenterInfo.getMsg().length());
            }
        } else {
            s1 = taskCenterInfo.getMsg();
        }
        textView.setText(Html.fromHtml("<font color='#ffffff' size='32px'>"
                + s1
                + "</font><font color='#fff3c1' size='34px'>"
                + taskCenterInfo.getHighlight() + "</font>" + "<font color='#ffffff' size='32px'>"
                + s2 + "</font>"));
        v.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dissPopWindow();
                //发送广播通知首页tab发现处隐藏红点提醒
                Intent tabRedIntent = new Intent();
                tabRedIntent.setAction(UrlConfig.HIDE_TABRED_ACTION);
                tabRedIntent.putExtra("tag", -2);
                context.sendBroadcast(tabRedIntent);

                Intent appIntent = new Intent(context, WebSrcViewActivity.class);
                appIntent.putExtra(WebSrcViewActivity.PAGE_URL, taskCenterInfo.getUrl());
                appIntent.putExtra(WebSrcViewActivity.PAGE_TYPE, "interactWeb");
                context.startActivity(appIntent);
                context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
        return v;
    }

    /**
     * 返回来自任何界面的屏幕中心提醒布局
     *
     * @return
     */
    private View getShowCenterView() {
        LayoutInflater lay = LayoutInflater.from(context);
        View v = lay.inflate(R.layout.taskcenter_all, null);
        ((TextView) (v.findViewById(R.id.taskcenter_all_txtMsg)))
                .setText(taskCenterInfo.getMsg());
//        && Integer.parseInt(taskCenterInfo.getZsb()) > 0
        if ((taskCenterInfo.getZsb() != null && !"".equals(taskCenterInfo.getZsb()))) {// 奖励中中搜币大于0
            String zsbStr = "";
            if (taskCenterInfo.getZsb().length() > 4) {
                zsbStr = taskCenterInfo.getZsb().substring(0, 3) + "...";
            } else {
                zsbStr = taskCenterInfo.getZsb();
            }
            ((TextView) (v.findViewById(R.id.taskcenter_all_txtCoin)))
                    .setText(" x " + zsbStr);
        } else {
            ((ImageView) (v.findViewById(R.id.taskcenter_all_coin)))
                    .setVisibility(View.GONE);
            ((TextView) (v.findViewById(R.id.taskcenter_all_txtCoin)))
                    .setVisibility(View.GONE);
        }
//        && Integer.parseInt(taskCenterInfo.getScore()) > 0
        if ((taskCenterInfo.getScore() != null && !"".equals(taskCenterInfo.getScore()))) {// 奖励中积分大于0
            String scoreStr = "";
            if (taskCenterInfo.getScore().length() > 4) {
                scoreStr = taskCenterInfo.getScore().substring(0, 3) + "...";
            } else {
                scoreStr = taskCenterInfo.getScore();
            }
            ((TextView) (v.findViewById(R.id.taskcenter_all_txtPoints)))
                    .setText(" x " + scoreStr);
        } else {
            ((ImageView) (v.findViewById(R.id.taskcenter_all_points)))
                    .setVisibility(View.GONE);
            ((TextView) (v.findViewById(R.id.taskcenter_all_txtPoints)))
                    .setVisibility(View.GONE);
        }
        return v;
    }

    /**
     * 返回来自登录互踢的提醒框
     *
     * @return
     */
    private View getShowReloginView() {
        LayoutInflater lay = LayoutInflater.from(context);
        View v = lay.inflate(R.layout.relogin, null);
        ((TextView) v.findViewById(R.id.txtRelogin_msg)).setText(taskCenterInfo.getMsg());
        v.findViewById(R.id.relogin_btnEnsure).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dissPopWindow();
            }
        });
        v.findViewById(R.id.relogin_btnRelogin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dissPopWindow();
                IntentUtil.gotoLogin(context);
            }
        });
        return v;
    }
}