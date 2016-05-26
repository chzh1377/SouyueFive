package com.zhongsou.souyue.circle.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.PickerMethod;
import com.zhongsou.souyue.circle.adapter.CircleShareAdapter;

/**
 * 
 * @ClassName: CircleShareMenuDialog 
 * @Description: 圈子详情分享
 * @author gengsong@zhongsou.com
 * @date 2014年4月28日 下午2:41:34 
 * @version 3.5.2
 */
public class CircleShareMenuDialog extends Dialog {
	
	private GridView gridView;
	private CircleShareAdapter popAdapter;
	private Context context;
	private boolean isPrime; //有没有‘精华区分享’功能按钮
	private PickerMethod pickerMethod;
	
	public final static int SHARE_TO_SYFRIENDS = 0; //搜悦好友
	public final static int SHARE_TO_DIGEST = 1; //兴趣圈
	public final static int SHARE_TO_QQFRIEND = 2;//QQ好友
	public final static int SHARE_TO_QQZONE = 3;//QQ空间
//	public final static int SHARE_TO_RENREN = 3;//人人
	public final static int SHARE_TO_WEIX = 4;//微信
	public final static int SHARE_TO_FRIENDS = 5;//微信朋友圈
	public final static int SHARE_TO_SINA = 6;//新浪
	public final static int SHARE_TO_TENCENTWEIBO = 7;//腾讯微博
	public final static int SHARE_TO_EMAIL = 8;//邮箱
	public final static int SHARE_TO_SMS = 9;//短信
	
	
	public CircleShareMenuDialog(Context context, PickerMethod pickerMethod) {
		super(context, R.style.DialogMenu_SNS);
		this.context = context;
		this.pickerMethod = pickerMethod;
	    setOwnerActivity((Activity)context);
	}
	
	public CircleShareMenuDialog(Context context, PickerMethod pickerMethod, boolean isPrime) {
		super(context, R.style.DialogMenu_SNS);
		this.context = context;
		this.pickerMethod = pickerMethod;
		this.isPrime = isPrime;
	    setOwnerActivity((Activity)context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getContentView();
		setListenter();
	}
	
	private void getContentView() {
	     this.setContentView(R.layout.sharemenu);
		 initView();
	}
	
	private void initView() {
		popAdapter = new CircleShareAdapter(context, isPrime);
		gridView = (GridView) this.findViewById(R.id.share_gridview);
		gridView.setAdapter(popAdapter);
		popAdapter.notifyDataSetChanged();
	}
	
	 //dialog显示位置是页面最下方，从下到上弹出
    public void showBottonDialog() {
        Window window = this.getWindow();
        //设置显示动画
        window.setWindowAnimations(R.style.share_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = -1; //设置x坐标
        wl.y = -1;//设置y坐标
        //控制dialog停放位置
        window.setAttributes(wl); 
        window.setGravity(Gravity.BOTTOM);
        //设置显示位置
        this.onWindowAttributesChanged(wl);
        //设置点击外围解散
        this.setCanceledOnTouchOutside(true);
        //最终决定dialog的大小,实际由contentView确定了
        this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        this.show();
    }
    
    //dialog显示位置是下面导航条以上从下到上弹出
    public void showBottonDialog(int mHeight) {
        Window window = this.getWindow();
        //设置显示动画
        window.setWindowAnimations(R.style.share_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = -1; //设置x坐标
        wl.y =  mHeight;//设置y坐标
        //控制dialog停放位置
        window.setAttributes(wl); 
        window.setGravity(Gravity.BOTTOM);
        //设置显示位置
        this.onWindowAttributesChanged(wl);
        //设置点击外围解散
        this.setCanceledOnTouchOutside(true);
        //最终决定dialog的大小,实际由contentView确定了
        this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        this.show();
    }
	
	private void setListenter() {
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Integer selectionId = popAdapter.getItem(arg2);    
				pickerMethod.loadData(arg2);
				dismiss();
			}

		});
	}

}
