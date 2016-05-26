package com.zhongsou.souyue.view;

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

import java.util.ArrayList;
import java.util.List;

/** 
 * @author : zoulu
 * 2014年7月22日
 * 下午3:08:15 
 * 类说明 :分享到社交平台dialog
 */
public class ShareSNSDialog extends Dialog{

	private GridView gridView;
    private ShareSNSDialogAdapter popAdapter;
    private Context context;
    private PickerMethod pickerMethod;
    private List<Integer> integers = new ArrayList<Integer>();
    
    public final static int SHARE_TO_DIGEST = 0;//网友推荐区
    public final static int SHARE_TO_SINA = 1;//新浪微博
    public final static int SHARE_TO_WEIX = 2;//微信好友
    public final static int SHARE_TO_FRIENDS = 3;//微信朋友圈
//    public final static int SHARE_TO_EMAIL = 4;//邮件
//    public final static int SHARE_TO_SMS = 5;//信息
    public final static int SHARE_TO_RENREN = 6;//人人
//    public final static int SHARE_TO_TWEIBO = 7;//腾讯微博
    public final static int SHARE_TO_SYFRIENDS = 8;//搜悦网友
    public final static int SHARE_TO_SYIMFRIEND = 9;//搜悦好友
    public final static int SHARE_TO_INTEREST = 10;//兴趣圈
    
    public final static int SHARE_TO_QQFRIEND = 11;//QQ好友
    public final static int SHARE_TO_QQZONE = 12;//QQ空间
    
    public static final String TO_DIGETST = "网友推荐区";
    public static final String TO_SINA = "新浪微博";
    public static final String TO_WEIX = "微信好友";
    public static final String TO_FRIENDS = "微信朋友圈";
//    public static final String TO_EMAIL = "邮件";
//    public static final String TO_SMS = "信息";
    public static final String TO_RENREN = "人人";
//    public static final String TO_TWEIBO = "腾讯微博";
//    public static final String TO_SYFRIENDS = "搜悦网友";
    public static final String TO_SYIMFRIEND = "搜悦好友";
    public static final String TO_INTEREST = "兴趣圈";
    
    public static final String TO_QQFRIEND = "QQ";
    public static final String TO_QQZONE = "QQ空间";
    /**
     * 构造方法
     * @param context 
     * @param pickerMethod 处理数据接口
     * @param list 要显示的分享第三方平台号 （0-10） 参照上面的静态变量
     */
    public ShareSNSDialog(Context context, PickerMethod pickerMethod , List<Integer> list){
    	 super(context, R.style.Dialog_SNS);
         this.context = context;
         this.pickerMethod = pickerMethod;
         this.integers = list;
         setOwnerActivity((Activity)context);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getContentView();
        setListenter();
    }
    
    private void getContentView() {
         this.setContentView(R.layout.sharesnsdialog);
         initView();
    }
    
    private void initView() {
        popAdapter = new ShareSNSDialogAdapter(context);
        popAdapter.setTitles(integers);
        gridView = (GridView) this.findViewById(R.id.share_gridview);
        gridView.setAdapter(popAdapter);
        popAdapter.notifyDataSetChanged();
    }
    
    private void setListenter() {
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int selectionId = popAdapter.getItem(arg2);    
                pickerMethod.loadData(selectionId);
                dismiss();
            }
        });
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
         this.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
        this.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.show();
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
        this.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.show();
    }

}
