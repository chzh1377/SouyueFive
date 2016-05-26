package com.zhongsou.souyue.share;

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
import com.zhongsou.souyue.adapter.ShareAdapter;
/**
 * 
 * @ClassName: ShareMenuDialog 
 * @Description: 分享菜单(dialog)
 * @author gengsong@zhongsou.com
 * @date 2014年4月4日 上午11:12:40 
 * @version 3.5.8
 */
public class ShareMenuDialog extends Dialog {
    
    private GridView gridView;
    private ShareAdapter popAdapter;
    private Context context;
    private String shareType;
    private PickerMethod pickerMethod;
    private boolean imageDownloadSucc = false;
    
	public final static int SHARE_TO_SYIMFRIEND = 9;//搜悦好友 IM
    public final static int SHARE_TO_INTEREST = 10;//兴趣圈
    public final static int SHARE_TO_DIGEST = 0;//精华
    public final static int SHARE_TO_SINA = 1;//新浪
    public final static int SHARE_TO_WEIX = 2;//微信
    public final static int SHARE_TO_FRIENDS = 3;//微信朋友圈
    public final static int SHARE_TO_EMAIL = 4;//邮箱
    public final static int SHARE_TO_SMS = 5;//短信
    public final static int SHARE_TO_RENREN = 6;//人人
//    public final static int SHARE_TO_TWEIBO = 7;//腾讯微博
    public final static int SHARE_TO_SYFRIENDS = 8;//搜悦网友
    public final static int SHARE_TO_QQFRIEND = 11;//QQ好友
    public final static int SHARE_TO_QQZONE = 12;//QQ空间
    

    public ShareMenuDialog(Context context, PickerMethod pickerMethod) {
        super(context,R.style.DialogMenu_SNS);
        this.context = context;
        this.pickerMethod = pickerMethod;
        setOwnerActivity((Activity)context);
    }
    
    public ShareMenuDialog(Context context, PickerMethod pickerMethod, String shareType) {
    	super(context,R.style.DialogMenu_SNS);
        this.context = context;
        this.pickerMethod = pickerMethod;
        this.shareType = shareType;
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
        /*if(shareType.equals(ShareConstantsUtils.READABILITY)||shareType.equals(ShareConstantsUtils.SUPERSRP)
                ||shareType.equals(ShareConstantsUtils.SELFCREATEDETAIL)
                ||shareType.equals(ShareConstantsUtils.WEBSRCVIEW)
                ||shareType.equals(ShareConstantsUtils.SRP)
                ||shareType.equals(ShareConstantsUtils.WEBSRCVIEWWEBTYPE)|| shareType.equals(ShareConstantsUtils.NEW_DETAIL)){
            if(!ConfigApi.isSouyue()){
                View view = View.inflate(context, R.layout.sharemenu, null);
                gridView = (GridView) view.findViewById(R.id.share_gridview);
                gridView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            this.setContentView(R.layout.sharemenu);
        }else{
        	if(!ConfigApi.isSouyue()){
                View view = View.inflate(context, R.layout.sharemenu_twolines, null);
                gridView = (GridView) view.findViewById(R.id.share_gridview);
                gridView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            //popuwindow的行数为2行的情况
           this.setContentView(R.layout.sharemenu_twolines);
        }*/
         this.setContentView(R.layout.sharemenu);
         initView();
    }
    
    private void initView() {
        popAdapter = new ShareAdapter(context, shareType);
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
        
        // add by trade: 适配华为mate zhaobo 2014-08-14
        //modify by trade ：导致推荐给朋友高度显示异常 注掉  zg 2014-12-11
//        WindowManager wm = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
//        int wwidth = wm.getDefaultDisplay().getWidth();// 手机屏幕的宽度
//        int hheight = wm.getDefaultDisplay().getHeight();// 手机屏幕的高度
//        if (wwidth == 720 && hheight == 1208) {
//            // 最终决定dialog的大小,实际由contentView确定了
//            this.getWindow().setLayout(LayoutParams.FILL_PARENT,
//                    400);
//        } else {
            // 最终决定dialog的大小,实际由contentView确定了
            this.getWindow().setLayout(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT);
    //    }
        
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
        
        //add by trade: 适配华为mate zhaobo 2014-08-14
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int wwidth = wm.getDefaultDisplay().getWidth();// 手机屏幕的宽度
        int hheight = wm.getDefaultDisplay().getHeight();// 手机屏幕的高度
        if (wwidth == 720 && hheight == 1208) {
            // 最终决定dialog的大小,实际由contentView确定了
            this.getWindow().setLayout(LayoutParams.FILL_PARENT, 390);
        } else {
            // 最终决定dialog的大小,实际由contentView确定了
            this.getWindow().setLayout(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT);
        }
        this.show();
    }
    
//  dialog显示位置是上面导航条以下，从上到下弹出
       public void showTopDialog(int mHeight) {
            Window window = this.getWindow();
            //设置显示动画
            window.setWindowAnimations(R.style.share_menu_up_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0; //设置x坐标
            wl.y =  mHeight+2;//设置y坐标
            //控制dialog停放位置
            window.setAttributes(wl); 
            window.setGravity(Gravity.TOP);
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
            	
            	if(!imageDownloadSucc){
            	    if(popAdapter==null||pickerMethod==null)
            	        return;
	                Integer selectionId = popAdapter.getItem(arg2);
                    if(selectionId != null){
                        pickerMethod.loadData(selectionId);
                    }
	                dismiss();
            	}
            }
        });
    }

	public boolean isImageDownloadSucc() {
		return imageDownloadSucc;
	}

	public void setImageDownloadSucc(boolean imageDownloadSucc) {
		this.imageDownloadSucc = imageDownloadSucc;
	}
}
